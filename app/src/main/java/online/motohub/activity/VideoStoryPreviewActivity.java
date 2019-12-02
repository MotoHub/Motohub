package online.motohub.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.database.DatabaseHandler;
import online.motohub.dialog.DialogManager;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsResModel;
import online.motohub.model.ImageModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SpectatorLiveEntity;
import online.motohub.model.SpectatorLiveModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.services.SpectatorFileUploadService;
import online.motohub.services.UploadJobScheduler;
import online.motohub.util.UrlUtils;


public class VideoStoryPreviewActivity extends BaseActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private static final String TAG = "SpectorLive";
    @BindView(R.id.video)
    VideoView mVideoView;
    @BindView(R.id.iv_video)
    ImageView mImageView;
    @BindView(R.id.edit_caption)
    EditText mEditStory;
    @BindView(R.id.iv_video_play)
    ImageView mIvPlay;
    @BindView(R.id.toolbar_back_img_btn)
    ImageButton mBackBtn;
    TransferObserver observerVideo, observerImage;
    private Uri videoUri;
    private EventsResModel mEventResModel;
    private ProfileResModel mMyProfileResModel;
    private ProgressDialog pDialog;
    private String COMPRESSED_VIDEO_FOLDER = "MotoHUB";
    private String mCompressedVideoPath;
    private DatabaseHandler databaseHandler = new DatabaseHandler(this);
    private String data = "Do you want upload this video later ?";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        ButterKnife.bind(this);
        AWSMobileClient.getInstance().initialize(this).execute();
        initialise();
        setUpVideoView();
        setImageWithGlide(mImageView, videoUri);
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        hideDialog();
        super.onDestroy();
    }

    private void initialise() {
        mBackBtn.setVisibility(View.VISIBLE);
        videoUri = getIntent().getParcelableExtra("file_uri");
        mEventResModel = (EventsResModel) getIntent().getBundleExtra("bundle_data").getSerializable(EventsModel.EVENTS_RES_MODEL);
        //mMyProfileResModel = (ProfileResModel) getIntent().getBundleExtra("bundle_data").getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        //mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
    }

    private void setUpVideoView() {
        MediaController controller = new MediaController(this);
        controller.setAnchorView(mVideoView);
        controller.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(controller);
        mVideoView.setVideoURI(videoUri);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnCompletionListener(this);

        pDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        pDialog.setCancelable(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.iv_video, R.id.iv_video_play, R.id.btn_cancel, R.id.btn_next, R.id.toolbar_back_img_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_next:
                if (mEventResModel != null) {
                    //if (isNetworkConnected(this)) {
                    uploadSpecLiveVideo();
                    /*} else {
                        jobScheduler();
                    }*/
                }
                break;
            case R.id.toolbar_back_img_btn:
                super.onBackPressed();
                break;
            default:
                mImageView.setVisibility(View.GONE);
                mIvPlay.setVisibility(View.GONE);
                playVideo();
                break;
        }
    }

    /*private void showAlertDialog(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(VideoStoryPreviewActivity.this, R.style.MyAlertDialogStyle);
                builder.setTitle(data);
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        jobScheduler();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadSpecLiveVideo();
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }*/

    /*@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void jobScheduler() {
        File mFile = new File(videoUri.getPath());
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mFile.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        File mThumb = getThumbPath(bitmap);

        PromotersResModel mPromoter = mEventResModel.getPromoterByUserID();
        String text = null;
        try {
            text = URLEncoder.encode(mEditStory.getText().toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        SpectatorLiveEntity entity = new SpectatorLiveEntity();
        entity.setProfileID(String.valueOf(mMyProfileResModel.getID()));
        entity.setUserID(String.valueOf(mPromoter.getUserId()));
        entity.setUserType(AppConstants.USER_EVENT_VIDEOS);
        entity.setCaption(text);
        entity.setVideoUrl(videoUri.getPath());
        assert mThumb != null;
        entity.setThumbnail(mThumb.getAbsolutePath());
        entity.setEventID(String.valueOf(mEventResModel.getID()));
        entity.setEventFinishDate(mEventResModel.getFinish());
        entity.setLivePostProfileID(String.valueOf(mMyProfileResModel.getID()));
        databaseHandler.insertSpectatorLiveVideo(entity);
        boolean mIsJobSchedule = PreferenceUtils.getInstance(this).getBooleanData(PreferenceUtils.IS_JOB_SCHEDULER);
        if (!mIsJobSchedule) {
            scheduleJob();
            PreferenceUtils.getInstance(this).saveBooleanData(PreferenceUtils.IS_JOB_SCHEDULER, true);
        }
        finish();
    }*/

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void uploadSpecLiveVideo() {
        File mFile = new File(videoUri.getPath());
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mFile.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        File mThumb = getThumbPath(bitmap);
        PromotersResModel mPromoter = mEventResModel.getPromoterByUserID();
        String text = null;
        try {
            text = URLEncoder.encode(mEditStory.getText().toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (isNetworkConnected(this)) {
            SpectatorLiveEntity entity = new SpectatorLiveEntity();
            entity.setProfileID(String.valueOf(mMyProfileResModel.getID()));
            entity.setUserID(String.valueOf(mPromoter.getUserId()));
            entity.setUserType(AppConstants.USER_EVENT_VIDEOS);
            entity.setCaption(text);
            entity.setVideoUrl(videoUri.getPath());
            assert mThumb != null;
            entity.setThumbnail(mThumb.getAbsolutePath());
            entity.setEventID(String.valueOf(mEventResModel.getID()));
            entity.setEventFinishDate(mEventResModel.getFinish());
            entity.setLivePostProfileID(String.valueOf(mMyProfileResModel.getID()));
            Gson g = new Gson();
            String json = g.toJson(entity);
            databaseHandler.insertSpectatorLiveVideo(entity);
            //scheduleJob1(json);
            Intent service_intent = new Intent(this, SpectatorFileUploadService.class);
            service_intent.putExtra("data", json);
            startService(service_intent);
            finish();
        } else {
            SpectatorLiveEntity entity = new SpectatorLiveEntity();
            entity.setProfileID(String.valueOf(mMyProfileResModel.getID()));
            entity.setUserID(String.valueOf(mPromoter.getUserId()));
            entity.setUserType(AppConstants.USER_EVENT_VIDEOS);
            entity.setCaption(text);
            entity.setVideoUrl(videoUri.getPath());
            assert mThumb != null;
            entity.setThumbnail(mThumb.getAbsolutePath());
            entity.setEventID(String.valueOf(mEventResModel.getID()));
            entity.setEventFinishDate(mEventResModel.getFinish());
            entity.setLivePostProfileID(String.valueOf(mMyProfileResModel.getID()));
            databaseHandler.insertSpectatorLiveVideo(entity);
            finish();
        }
    }

    /*@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scheduleJob() {
        ComponentName componentName = new ComponentName(this, UploadJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 10000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        assert scheduler != null;
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }*/

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scheduleJob1(String data) {
        Random ran = new Random();
        int x = ran.nextInt(50) + 1;
        ComponentName componentName = new ComponentName(this, UploadJobScheduler.class);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("data", data);
        JobInfo info = new JobInfo.Builder(x, componentName)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setExtras(bundle)
                .setPeriodic(50000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        assert scheduler != null;
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void cancelJob(View v) {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        assert scheduler != null;
        scheduler.cancel(123);
        Log.d(TAG, "Job cancelled");
    }

    private void apiCallToUploadSpecLiveStreamVideo(String mVideoPath, String mThumb) {
        try {
            PromotersResModel mPromoter = mEventResModel.getPromoterByUserID();
            String text = URLEncoder.encode(mEditStory.getText().toString(), "UTF-8");

            JsonObject mObject = new JsonObject();
            mObject.addProperty(SpectatorLiveModel.PROFILE_ID, mMyProfileResModel.getID());
            mObject.addProperty(SpectatorLiveModel.USERID, mPromoter.getUserId());
            mObject.addProperty(SpectatorLiveModel.USERTYPE, AppConstants.USER_EVENT_VIDEOS);
            mObject.addProperty(SpectatorLiveModel.CAPTION, text);
            mObject.addProperty(SpectatorLiveModel.FILEURL, mVideoPath);
            mObject.addProperty(SpectatorLiveModel.THUMBNAIL, mThumb);
            mObject.addProperty(SpectatorLiveModel.EVENTID, mEventResModel.getID());
            mObject.addProperty(SpectatorLiveModel.EVENT_FINISH_DATE, mEventResModel.getFinish());
            mObject.addProperty(SpectatorLiveModel.LIVE_POST_PROFILE_ID, mMyProfileResModel.getID());

            JsonArray mJsonArray = new JsonArray();
            mJsonArray.add(mObject);

            RetrofitClient.getRetrofitInstance().callToPostSpectatorData(VideoStoryPreviewActivity.this, mJsonArray, RetrofitClient.SPECTATOR_LIVE_RESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getThumbPath(Bitmap bitmap) {
        File imageFile = null;
        try {
            imageFile = compressedImgFromBitmap(bitmap);
            return imageFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void nextScreen() {
        Intent intent = new Intent(this, StorySettingActivity.class);
        intent.putExtra("file_uri", videoUri);
        intent.putExtra("file_type", "video");
        Bundle mBundle = getIntent().getExtras().getBundle("bundle_data");
        if (mBundle != null)
            intent.putExtra("bundle_data", mBundle);
        startActivity(intent);
        finish();
    }

    void playVideo() {
        if (mVideoView.isPlaying()) return;
        mVideoView.start();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();
        float videoWidth = mediaPlayer.getVideoWidth();
        float videoHeight = mediaPlayer.getVideoHeight();
        float viewWidth = mVideoView.getWidth();
        lp.height = (int) (viewWidth * (videoHeight / videoWidth));
        mVideoView.setLayoutParams(lp);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mImageView.setVisibility(View.VISIBLE);
        mIvPlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        switch (responseType) {
            case RetrofitClient.UPLOAD_PROFILE_IMAGE_FILE_RESPONSE:
                ImageModel mImageModel = (ImageModel) responseObj;
                apiCallToUploadSpecLiveStreamVideo(mImageModel.getmModels().get(0).getPath(), mImageModel.getmModels().get(1).getPath());
                break;
            case RetrofitClient.SPECTATOR_LIVE_RESPONSE:
                SpectatorLiveModel mSpectatorLiveStory = (SpectatorLiveModel) responseObj;
                showToast(getApplicationContext(), "Successfully added");
                finish();
                break;
        }
    }


    private void amazoneUpload(final File video, final File image) {

        pDialog.setMessage("Please wait video is uploading...");
        showDialog();

        /*ClientConfiguration configuration = new ClientConfiguration();
        configuration.setMaxErrorRetry(3);
        configuration.setConnectionTimeout(501000);
        configuration.setSocketTimeout(501000);
        configuration.setProtocol(Protocol.HTTP);

        credentials = new BasicAWSCredentials(AppConstants.KEY, AppConstants.SECRET);
        s3 = new AmazonS3Client(credentials, configuration);
        s3.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));
        transferUtility = new TransferUtility(s3, VideoStoryPreviewActivity.this);*/

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                        .build();

        if (!video.exists()) {
            Toast.makeText(VideoStoryPreviewActivity.this, "File Not Found!", Toast.LENGTH_SHORT).show();
            return;
        }

        final String videoFileName = video.getName();
        final String imageFileName = image.getName();

        observerVideo = transferUtility.upload(AppConstants.BUCKET_NAME, UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + videoFileName, video);

        observerImage = transferUtility.upload(AppConstants.BUCKET_NAME, UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + imageFileName, image);

        observerVideo.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (TransferState.COMPLETED.equals(observerVideo.getState())) {
                    hideDialog();
                    apiCallToUploadSpecLiveStreamVideo(UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + videoFileName, UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + imageFileName);
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                long _bytesCurrent = bytesCurrent;
                long _bytesTotal = bytesTotal;

                float percentage = ((float) _bytesCurrent / (float) _bytesTotal * 100);
                Log.d("percentage", "" + percentage);
                //pDialog.setProgress((int) percentage);

            }

            @Override
            public void onError(int id, Exception ex) {
                hideDialog();
                //Toast.makeText(VideoStoryPreviewActivity.this, "" + ex.getMainObj(), Toast.LENGTH_SHORT).show();
            }
        });


        observerImage.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (TransferState.COMPLETED.equals(observerImage.getState())) {
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                long _bytesCurrent = bytesCurrent;
                long _bytesTotal = bytesTotal;

                float percentage = ((float) _bytesCurrent / (float) _bytesTotal * 100);
                Log.d("percentage", "" + percentage);
                /*pb.setProgress((int) percentage);*/
            }

            @Override
            public void onError(int id, Exception ex) {
                //Toast.makeText(VideoStoryPreviewActivity.this, "" + ex.getMainObj(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public String getCompressedVideoPath() {
        CreateCompressedVideoPath();
        mCompressedVideoPath = Environment.getExternalStorageDirectory()
                + File.separator
                + COMPRESSED_VIDEO_FOLDER + System.currentTimeMillis() + "COMPRESSED_VIDEO.mp4";
        return mCompressedVideoPath;

    }


}
