package online.motohub.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import online.motohub.R;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsResModel;
import online.motohub.model.ImageModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SpectatorLiveModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;


public class VideoStoryPreviewActivity extends BaseActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

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

    private Uri videoUri;

    private EventsResModel mEventResModel;
    private ProfileResModel mMyProfileResModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        ButterKnife.bind(this);
        initialise();
        setUpVideoView();
        setImageWithGlide(mImageView, videoUri);
    }

    private void initialise() {
        mBackBtn.setVisibility(View.VISIBLE);
        videoUri = getIntent().getParcelableExtra("file_uri");
        mEventResModel = (EventsResModel) getIntent().getBundleExtra("bundle_data").getSerializable(EventsModel.EVENTS_RES_MODEL);
        mMyProfileResModel = (ProfileResModel) getIntent().getBundleExtra("bundle_data").getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
    }

    private void setUpVideoView() {
        MediaController controller = new MediaController(this);
        controller.setAnchorView(mVideoView);
        controller.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(controller);
        mVideoView.setVideoURI(videoUri);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnCompletionListener(this);
    }

    @OnClick({R.id.iv_video, R.id.iv_video_play, R.id.btn_cancel, R.id.btn_next, R.id.toolbar_back_img_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_next:
                /*nextScreen();*/
                uploadSpecLiveVideo();
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

    private void uploadSpecLiveVideo() {
        File mFile = new File(videoUri.getPath());
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mFile.getPath(),
                MediaStore.Images.Thumbnails.MINI_KIND);
        File mThumb = getThumbPath(bitmap);
        RequestBody videoBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        RequestBody imageBody = RequestBody.create(MediaType.parse("*/*"), mThumb);
        MultipartBody.Part videoPart =
                MultipartBody.Part.createFormData("files[]", mFile.getName(), videoBody);
        MultipartBody.Part imagePart =
                MultipartBody.Part.createFormData("files[]", mThumb.getName(), imageBody);
        RetrofitClient.getRetrofitInstance().callUploadSpectatorLive(
                this,
                videoPart, imagePart,
                RetrofitClient.UPLOAD_PROFILE_IMAGE_FILE_RESPONSE);
    }

    private void apiCallToUploadSpecLiveStreamVideo(String mVideoPath, String mThumb) {

        PromotersResModel mPromoter = mEventResModel.getPromoterByUserID();

        JsonObject mObject = new JsonObject();
        mObject.addProperty(SpectatorLiveModel.PROFILE_ID, mPromoter.getUserId());
        mObject.addProperty(SpectatorLiveModel.USERID, mPromoter.getUserId());
        mObject.addProperty(SpectatorLiveModel.USERTYPE, mPromoter.getUserType());
        mObject.addProperty(SpectatorLiveModel.CAPTION, mEditStory.getText().toString());
        mObject.addProperty(SpectatorLiveModel.FILEURL, mVideoPath);
        mObject.addProperty(SpectatorLiveModel.THUMBNAIL, mThumb);
        mObject.addProperty(SpectatorLiveModel.EVENTID, mEventResModel.getID());
        mObject.addProperty(SpectatorLiveModel.EVENT_FINISH_DATE, mEventResModel.getFinish());
        mObject.addProperty(SpectatorLiveModel.LIVE_POST_PROFILE_ID, mMyProfileResModel.getID());

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mObject);

        RetrofitClient.getRetrofitInstance().callToPostSpectatorData(VideoStoryPreviewActivity.this, mJsonArray, RetrofitClient.SPECTATOR_LIVE_RESPONSE);
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
}
