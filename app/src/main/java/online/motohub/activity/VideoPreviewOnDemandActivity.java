package online.motohub.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.dialog.DialogManager;
import online.motohub.model.EventsResModel;
import online.motohub.model.ProfileResModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.services.ProfileUploadService;

public class VideoPreviewOnDemandActivity extends BaseActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    @BindView(R.id.toolbar_back_img_btn)
    ImageButton toolbarBackImgBtn;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar_settings_img_btn)
    ImageView toolbarSettingsImgBtn;
    @BindView(R.id.toolbar_messages_img_btn)
    ImageView toolbarMessagesImgBtn;
    @BindView(R.id.toolbar_notification_img_btn)
    ImageView toolbarNotificationImgBtn;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.video)
    VideoView video;
    @BindView(R.id.iv_video)
    ImageView ivVideo;
    @BindView(R.id.iv_video_play)
    ImageView ivVideoPlay;
    @BindView(R.id.video_frame)
    FrameLayout videoFrame;
    @BindView(R.id.edit_caption)
    EditText editCaption;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.btns_lay)
    LinearLayout btnsLay;
    @BindView(R.id.parent)
    RelativeLayout parent;

    private Uri videoUri;

    private EventsResModel mEventResModel;
    private ProfileResModel mMyProfileResModel;
    private String mVideoPathUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview_ondemand);
        ButterKnife.bind(this);
        initialise();
        setUpVideoView();
        setImageWithGlide(ivVideo, videoUri);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
    }

    @Override
    public void retrofitOnFailure(int code, String message) {
        super.retrofitOnFailure(code, message);
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(
                    this,
                    RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showToast(this, mErrorMsg);
        }
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    @OnClick({R.id.iv_video_play, R.id.edit_caption, R.id.btn_cancel, R.id.btn_next, R.id.toolbar_back_img_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_video_play:
                ivVideo.setVisibility(View.GONE);
                ivVideoPlay.setVisibility(View.GONE);
                playVideo();
                break;
            case R.id.edit_caption:
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_next:
                uploadVideoFile();
                showToast(VideoPreviewOnDemandActivity.this, getString(R.string.uploading_video));
                break;
            case R.id.toolbar_back_img_btn:
                super.onBackPressed();
                break;

        }
    }

    private void initialise() {
        toolbarBackImgBtn.setVisibility(View.VISIBLE);
        videoUri = getIntent().getParcelableExtra("file_uri");
        mVideoPathUri = getIntent().getStringExtra("mVideoPathUri");
        mMyProfileResModel = (ProfileResModel) getIntent().getSerializableExtra("bundle_data");

    }

    private void setUpVideoView() {
        MediaController controller = new MediaController(this);
        controller.setAnchorView(video);
        controller.setMediaPlayer(video);
        video.setMediaController(null);
        video.setVideoURI(videoUri);
        video.setOnPreparedListener(this);
        video.setOnCompletionListener(this);
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

    void playVideo() {
        if (video.isPlaying()) return;
        video.start();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        ViewGroup.LayoutParams lp = video.getLayoutParams();
        float videoWidth = mediaPlayer.getVideoWidth();
        float videoHeight = mediaPlayer.getVideoHeight();
        float viewWidth = video.getWidth();
        lp.height = (int) (viewWidth * (videoHeight / videoWidth));
        video.setLayoutParams(lp);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        ivVideo.setVisibility(View.VISIBLE);
        ivVideoPlay.setVisibility(View.VISIBLE);
    }


    private void uploadVideoFile() {
        try {
            String text = editCaption.getText().toString();
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mVideoPathUri, MediaStore.Images.Thumbnails.MINI_KIND);
            File imageFile = compressedImgFromBitmap(thumb);
            Intent service_intent = new Intent(this, ProfileUploadService.class);
            service_intent.putExtra(AppConstants.VIDEO_PATH, mVideoPathUri);
            service_intent.putExtra(AppConstants.IMAGE_PATH, String.valueOf(imageFile));
            String destFilePath = Environment.getExternalStorageDirectory().getPath() + getString(R.string.util_app_folder_root_path);
            service_intent.putExtra(AppConstants.PROFILE_ID, mMyProfileResModel.getID());
            service_intent.putExtra(AppConstants.CAPTION, text);
            service_intent.putExtra(AppConstants.DEST_PATH, destFilePath);
            service_intent.putExtra(AppConstants.USER_TYPE, AppConstants.ONDEMAND);
            service_intent.setAction("ProfileUploadService");
            startService(service_intent);
            mVideoPathUri = "";
            parent.setVisibility(View.GONE);
            finish();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
