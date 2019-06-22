package online.motohub.activity;


import android.annotation.TargetApi;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.constants.AppConstants;
import online.motohub.dialog.DialogManager;


public class VideoPreviewScreen extends BaseActivity {


    public final static String TAG = "ViewLiveVideoViewScreen";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindString(R.string.app_name)
    String mToolbarTitle;

    @BindView(R.id.live_stream_co_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.video_view)
    VideoView mVideoView;

    @BindView(R.id.left_arrow)
    ImageView mLeftArrow;
    @BindView(R.id.right_arrow)
    ImageView mRightArrow;
    private MediaController mMediaController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_live_video_view_screen1);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Uri uri = Uri.parse(getIntent().getStringExtra(AppConstants.VIDEO_PATH));
        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        initVideoView();
        if (uri != null) {
            playVideo(uri);
        } else {
            showToast(this, "Oops!!! Something went wrong! Please try again.");
        }
    }


    private void initVideoView() {
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                DialogManager.hideProgress();
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        /*
                         * add media controller
                         */
                        mMediaController = new MediaController(VideoPreviewScreen.this);
                        mVideoView.setMediaController(mMediaController);
                        /*
                         * and set its position on screen
                         */
                        mMediaController.setAnchorView(mVideoView);
                    }
                });
                mVideoView.start();
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START)
                            DialogManager.showProgress(VideoPreviewScreen.this);
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END)
                            DialogManager.hideProgress();
                        return false;
                    }
                });
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(VideoPreviewScreen.this, "Video Stopped!", Toast
                        .LENGTH_LONG).show();
                releasePlayer();
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                DialogManager.hideProgress();
                Toast.makeText(VideoPreviewScreen.this, "Oops!!! Something went wrong! Please try again.", Toast
                        .LENGTH_LONG).show();
                return true;
            }
        });
    }

    private void playVideo(Uri url) {
        releasePlayer();
        try {
            DialogManager.showProgress(VideoPreviewScreen.this);
            mVideoView.setVideoURI(url);
            mVideoView.requestFocus();
        } catch (Exception e) {
            DialogManager.hideProgress();
            Toast.makeText(this, "Error in creating player!  " + e.getMessage(), Toast
                    .LENGTH_LONG).show();
        }
    }

    private void releasePlayer() {
        if (mVideoView != null && mVideoView.isPlaying())
            mVideoView.stopPlayback();
    }


    @OnClick({R.id.toolbar_back_img_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;

        }
    }

    private void pauseVideo() {
        if (mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }

    private void resumeVideo() {
        if (mVideoView != null && !mVideoView.isPlaying()) {
            mVideoView.resume();
            mVideoView.requestFocus();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
        releasePlayer();
    }

}
