package online.motohub.util;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import online.motohub.database.DatabaseHandler;
import online.motohub.model.SpectatorLiveEntity;
import online.motohub.model.SpectatorLiveModel;
import online.motohub.model.VideoUploadModel;
import online.motohub.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("AccessStaticViaInstance")
public class SpectatorLiveOfflineUpload extends IntentService implements ProgressRequestBody.UploadCallbacks {


    /*private AmazonS3Client s3;
    private BasicAWSCredentials credentials;
    private TransferUtility transferUtility;*/
    private TransferObserver observerVideo;
    private TransferObserver observerImage;
    private int count = 111;
    private NotificationManager mNotificationManager;
    private Notification.Builder mNotificationBuilder;
    private NotificationCompat.Builder mNotificationCompatBuilder;
    private Notification mNotification;
    private int mProfileID, mUserId;
    private String mUserType = AppConstants.USER_EVENT_VIDEOS, mCaption = "";
    private VideoUploadModel post, videoUploadModel;
    private String mVideoPath;
    private File mThumbImgFile;
    private int mEventId;
    private String mEventFinishDate;
    private int mLivePostProfileID;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SpectatorLiveOfflineUpload(String name) {
        super(SpectatorLiveOfflineUpload.class.getName());
    }

    public SpectatorLiveOfflineUpload() {
        super(SpectatorLiveOfflineUpload.class.getName());
    }

    protected String getHttpFilePath(String filePath) {
        return filePath;
    }

    protected String getUserId() {
        return String.valueOf(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AWSMobileClient.getInstance().initialize(this).execute();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onProgressUpdate(int percentage, int notificationid) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationBuilder.setProgress(100, percentage, false);
            mNotification = mNotificationBuilder.build();
        } else {
            mNotificationCompatBuilder.setProgress(100, percentage, false);
            mNotification = mNotificationCompatBuilder.build();
        }
        mNotificationManager.notify(notificationid, mNotification);
    }

    @Override
    public void onDestroy() {
        count = 111;
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        /*try {
            if (intent != null) {
                mProfileID = intent.getIntExtra(SpectatorLiveModel.PROFILE_ID, 0);
                mUserId = intent.getIntExtra(SpectatorLiveModel.USERID, 0);
                mCaption = intent.getStringExtra(SpectatorLiveModel.CAPTION);
                mVideoPath = intent.getStringExtra(SpectatorLiveModel.FILEURL);
                mThumbImgFile = new File(intent.getStringExtra(SpectatorLiveModel.THUMBNAIL));
                mEventId = intent.getIntExtra(SpectatorLiveModel.EVENTID, 0);
                mEventFinishDate = intent.getStringExtra(SpectatorLiveModel.EVENT_FINISH_DATE);
                mLivePostProfileID = intent.getIntExtra(SpectatorLiveModel.LIVE_POST_PROFILE_ID, 0);

                UploadVideoFile(this, mVideoPath, mThumbImgFile, mProfileID);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true)
    public void OnEvent(ArrayList<SpectatorLiveEntity> list) {
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                SpectatorLiveEntity entity = list.get(i);
                mProfileID = Integer.parseInt(entity.getProfileID());
                mUserId = Integer.parseInt(entity.getUserID());
                mCaption = entity.getCaption();
                mVideoPath = entity.getVideoUrl();
                mThumbImgFile = new File(entity.getThumbnail());
                mEventId = Integer.parseInt(entity.getEventID());
                mEventFinishDate = entity.getEventFinishDate();
                mLivePostProfileID = Integer.parseInt(entity.getLivePostProfileID());
                UploadVideoFile(this, mVideoPath, mThumbImgFile, mProfileID);
            }
            EventBus.getDefault().removeStickyEvent(list);
        }
    }

    @Override
    public void onError() {
    }

    @Override
    public void onFinish() {
    }

    private void UploadVideoFile(Context mContext, String compressedFilePath, File mImageFile, int mProfileID) {
        File videoFile = new File(compressedFilePath);
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        videoUploadModel = new VideoUploadModel();
        int count = databaseHandler.getPendingCount();
        int notificationid = count + 1;
        String s = videoFile.toString();
        videoUploadModel.setVideoURL(s.substring(s.lastIndexOf("/") + 1, s.length()));
        videoUploadModel.setFlag(1);
        videoUploadModel.setThumbnailURl(mImageFile.toString());
        videoUploadModel.setProfileID(mProfileID);
        videoUploadModel.setNotificationflag(notificationid);
        videoUploadModel.setUserType(mUserType);
        databaseHandler.addVideoDetails(videoUploadModel);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(NotificationUtils.getInstance().getNotificationChannel(notificationid));
            }
            mNotificationBuilder = NotificationUtils.getInstance().getNotificationBuilder(this, false, "Uploading Video",
                    notificationid, 100, 0);
            mNotification = mNotificationBuilder.build();
            mNotificationManager.notify(notificationid, mNotification);
        } else {
            mNotificationCompatBuilder = NotificationUtils.getInstance().getNotificationCompatBuilder(this, false, "Uploading Video",
                    notificationid, 100, 0);
            mNotification = mNotificationCompatBuilder.build();
            mNotificationManager.notify(notificationid, mNotification);
        }
        startForeground(notificationid, mNotification);

        UploadAsync uploadAsync = new UploadAsync(notificationid, videoFile, mImageFile);
        uploadAsync.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class UploadAsync extends AsyncTask<String, String, Void> {

        private Integer notificationid;
        private File imagfile;
        private File videofile;

        UploadAsync(Integer notificationid, File videofile, File ImageFile) {
            this.notificationid = notificationid;
            imagfile = ImageFile;
            this.videofile = videofile;
        }

        @Override
        protected Void doInBackground(String... paths) {
            amazoneUpload(videofile, imagfile, notificationid);
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    private void amazoneUpload(final File video, final File image, final int notificationid) {

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(NotificationUtils.getInstance().getNotificationChannel(notificationid));
            }
            mNotificationBuilder = NotificationUtils.getInstance().getNotificationBuilder(this, false, "Uploading Video",
                    notificationid, 100, 0);
            mNotification = mNotificationBuilder.build();
            mNotificationManager.notify(notificationid, mNotification);
        } else {
            mNotificationCompatBuilder = NotificationUtils.getInstance().getNotificationCompatBuilder(this, false, "Uploading Video",
                    notificationid, 100, 0);
            mNotification = mNotificationCompatBuilder.build();
            mNotificationManager.notify(notificationid, mNotification);
        }
        startForeground(notificationid, mNotification);

        /*ClientConfiguration configuration = new ClientConfiguration();
        configuration.setMaxErrorRetry(3);
        configuration.setConnectionTimeout(501000);
        configuration.setSocketTimeout(501000);
        configuration.setProtocol(Protocol.HTTP);

        credentials = new BasicAWSCredentials(AppConstants.KEY, AppConstants.SECRET);
        s3 = new AmazonS3Client(credentials, configuration);
        s3.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));
        transferUtility = new TransferUtility(s3, SpectatorLiveOfflineUpload.this);*/

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                        .build();

        if (!video.exists()) {
            // Toast.makeText(ProfileUploadService.this, "File Not Found!", Toast.LENGTH_SHORT).show();
            return;
        }

        observerVideo = transferUtility.upload(AppConstants.BUCKET_NAME, UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + video.getName(), video);

        observerImage = transferUtility.upload(AppConstants.BUCKET_NAME, UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + image.getName(), image);

        observerVideo.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (state.COMPLETED.equals(observerVideo.getState())) {

                    try {

                        //String text = URLEncoder.encode(mCaption, "UTF-8");

                        JsonObject mObject = new JsonObject();
                        mObject.addProperty(SpectatorLiveModel.PROFILE_ID, mProfileID);
                        mObject.addProperty(SpectatorLiveModel.USERID, mUserId);
                        mObject.addProperty(SpectatorLiveModel.USERTYPE, AppConstants.USER_EVENT_VIDEOS);
                        mObject.addProperty(SpectatorLiveModel.CAPTION, mCaption);
                        mObject.addProperty(SpectatorLiveModel.FILEURL, UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + video.getName());
                        mObject.addProperty(SpectatorLiveModel.THUMBNAIL, UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + image.getName());
                        mObject.addProperty(SpectatorLiveModel.EVENTID, mEventId);
                        mObject.addProperty(SpectatorLiveModel.EVENT_FINISH_DATE, mEventFinishDate);
                        mObject.addProperty(SpectatorLiveModel.LIVE_POST_PROFILE_ID, mLivePostProfileID);

                        JsonArray mJsonArray = new JsonArray();
                        mJsonArray.add(mObject);
                        UploadData(mJsonArray);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                float percentage = ((float) bytesCurrent / (float) bytesTotal * 100);
                //Log.d("percentage", "" + percentage);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    mNotificationBuilder.setProgress(100, (int) percentage, false);
                    mNotification = mNotificationBuilder.build();
                } else {
                    mNotificationCompatBuilder.setProgress(100, (int) percentage, false);
                    mNotification = mNotificationCompatBuilder.build();
                }
                mNotificationManager.notify(notificationid, mNotification);
            }

            @Override
            public void onError(int id, Exception ex) {

                Toast.makeText(SpectatorLiveOfflineUpload.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        observerImage.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (state.COMPLETED.equals(observerImage.getState())) {
                    //Toast.makeText(ProfileUploadService.this, "File Upload Complete", Toast.LENGTH_SHORT).show();
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                float percentage = ((float) bytesCurrent / (float) bytesTotal * 100);
            }

            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(SpectatorLiveOfflineUpload.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UploadData(JsonArray jsonArray) {

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callPostSpectatorLiveStory("*", jsonArray).enqueue(new Callback<SpectatorLiveModel>() {
            @Override
            public void onResponse(Call<SpectatorLiveModel> call, Response<SpectatorLiveModel> response) {
                onDownloadComplete("File Uploaded", videoUploadModel.getNotificationflag());
            }

            @Override
            public void onFailure(Call<SpectatorLiveModel> call, Throwable t) {

            }
        });
    }

    @SuppressWarnings("SameParameterValue")
    private void onDownloadComplete(String value, int mNotificationID) {
        try {
            DatabaseHandler databaseHandler = new DatabaseHandler(this);
            int checkCount = databaseHandler.getPendingCount();
            stopForeground(true);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                if (mNotificationManager != null) {
                    mNotificationManager.createNotificationChannel(NotificationUtils.getInstance().getNotificationChannel(mNotificationID));
                }
                mNotificationBuilder = NotificationUtils.getInstance().getNotificationBuilder(this, false, value, mNotificationID, 0, 0);
                mNotification = mNotificationBuilder.build();
            } else {
                mNotificationCompatBuilder = NotificationUtils.getInstance().getNotificationCompatBuilder(this, false, value, mNotificationID, 0, 0);
                mNotification = mNotificationCompatBuilder.build();
            }
            if (checkCount == 0 || mNotificationID == 0) {
                count = count + 1;
                mNotificationManager.cancelAll();
                mNotificationManager.notify(count, mNotification);
            } else if (mNotificationID > 0) {
                databaseHandler.deletepost(mNotificationID);
                mNotificationManager.notify(mNotificationID, mNotification);
            }
            sendBroadcast(new Intent().setAction("UPLOAD_STATUS").putExtra("status", value));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}