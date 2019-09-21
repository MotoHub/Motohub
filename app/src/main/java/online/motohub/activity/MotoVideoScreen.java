package online.motohub.activity;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.VideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import online.motohub.R;
import online.motohub.dialog.DialogManager;
import online.motohub.newdesign.activity.LoginActivity;
import online.motohub.util.PreferenceUtils;


public class MotoVideoScreen extends BaseActivity {

    @BindView(R.id.video_view)
    VideoView mVideoView;
    private String mVideoFilePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_moto_video_view_screen);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        PreferenceUtils mPreferenceUtils = PreferenceUtils.getInstance(this);
        mPreferenceUtils.saveBooleanData(PreferenceUtils.IS_NOT_FIRST_LAUNCH, true);

        initVideoView();
        playVideo();
    }


    private void initVideoView() {
        mVideoFilePath = "android.resource://" + getPackageName() + "/" + R.raw.motohub_video;
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                DialogManager.hideProgress();
                mVideoView.start();
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START)
                            DialogManager.showProgress(MotoVideoScreen.this);
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
                releasePlayer();
                doAction();
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                DialogManager.hideProgress();
                Toast.makeText(MotoVideoScreen.this, "Live stream might be ended!!! Kindly choose another camera!", Toast
                        .LENGTH_LONG).show();
                return true;
            }
        });
    }

    private void playVideo() {
        releasePlayer();
        try {
            DialogManager.showProgress(this);
            mVideoView.setVideoURI(Uri.parse(mVideoFilePath));
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

    private void doAction() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    /**
     * FuturePurpose
     */

//    boolean mKeepMeLoggedIn = PreferenceUtils.getInstance(this).getBooleanData(PreferenceUtils.USER_KEEP_LOGGED_IN);
//    boolean mUserProfileCompleted = PreferenceUtils.getInstance(this).getBooleanData(PreferenceUtils.USER_PROFILE_COMPLETED);
//    boolean mIsTutorialFirst = PreferenceUtils.getInstance(this).getBooleanData(PreferenceUtils.IS_TUTORIAL_FIRST);
//        if (mKeepMeLoggedIn) {
//        if (!mIsTutorialFirst) {
//            startActivity(new Intent(this, TutorialScreen.class));
//        } else if (mUserProfileCompleted) {
//            startActivity(new Intent(this, ViewProfileActivity.class));
//        } else {
//            startActivity(new Intent(this, CreateProfileActivity.class));
//        }
//
//    } else {
//        startActivity(new Intent(this, LoginActivity.class));
//    }
//    finish();
}
