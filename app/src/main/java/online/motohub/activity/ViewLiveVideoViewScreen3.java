package online.motohub.activity;


import android.annotation.TargetApi;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.LiveStreamEntity;
import online.motohub.model.LiveStreamResponse;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;


public class ViewLiveVideoViewScreen3 extends BaseActivity {


    public final static String TAG = "ViewLiveVideoViewScreen";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindString(R.string.live)
    String mToolbarTitle;

    @BindView(R.id.live_stream_co_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.video_view)
    VideoView mVideoView;

    @BindView(R.id.left_arrow)
    ImageView mLeftArrow;
    @BindView(R.id.right_arrow)
    ImageView mRightArrow;
    private ArrayList<LiveStreamEntity> mLiveStreamList = new ArrayList<>();
    private int mCurrentProfileID = 0;
    private int mCurrentPos = 0;
    private android.widget.MediaController mMediaController;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_live_video_view_screen1);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mCurrentProfileID = mBundle.getInt(AppConstants.PROFILE_ID, 0);
        }
        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        initVideoView();
        callGetLiveStream();

    }

    private void initVideoView() {
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                /*
                 * add media controller
                 */
                        mMediaController = new android.widget.MediaController(ViewLiveVideoViewScreen3.this);
                        mVideoView.setMediaController(mMediaController);
                /*
                 * and set its position on screen
                 */
                        mMediaController.setAnchorView(mVideoView);
                    }
                });
                dismissAppDialog();
                mVideoView.start();
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START)
                            showAppDialog(AppDialogFragment.BUFFERING_DIALOG, null);
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END)
                            dismissAppDialog();
                        return false;
                    }
                });
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(ViewLiveVideoViewScreen3.this, "Current Stream Stopped! ", Toast
                        .LENGTH_LONG).show();
                releasePlayer();
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                dismissAppDialog();
                Toast.makeText(ViewLiveVideoViewScreen3.this, "Live stream might be ended!!! Kindly choose another camera! ", Toast
                        .LENGTH_LONG).show();
                return true;
            }
        });
    }


    private void callGetLiveStream() {
        String mFilter = APIConstants.StreamProfileID + "=" + mCurrentProfileID;
        RetrofitClient.getRetrofitInstance().callGetLiveStream(this, mFilter);
    }


    private void playVideo(String url) {
        releasePlayer();
        try {
            DialogManager.showProgress(this);
            mVideoView.setVideoPath(url);
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


    @OnClick({R.id.toolbar_back_img_btn, R.id.left_arrow, R.id.right_arrow})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.left_arrow:
                mCurrentPos = mCurrentPos - 1;
                loadVideo(mCurrentPos);
                break;
            case R.id.right_arrow:
                mCurrentPos = mCurrentPos + 1;
                loadVideo(mCurrentPos);
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
    public void retrofitOnResponse(Object responseObj, final int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof LiveStreamResponse) {
            LiveStreamResponse mResponse = (LiveStreamResponse) responseObj;
            mLiveStreamList.clear();
            mLiveStreamList = mResponse.getResource();
            if (mLiveStreamList.size() > 0) {
                loadVideo(0);
            } else {
                showToast(ViewLiveVideoViewScreen3.this, "No streams are available");
                finish();
            }

        } else if (responseObj instanceof SessionModel) {
            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            callGetLiveStream();

        }
    }

    private void loadVideo(int currentPos) {
        if (mLiveStreamList.size() > 1) {
            mRightArrow.setVisibility(View.VISIBLE);
        } else {
            mRightArrow.setVisibility(View.GONE);
            mLeftArrow.setVisibility(View.GONE);
        }
        mCurrentPos = currentPos;
        if (mCurrentPos == 0) {
            mLeftArrow.setVisibility(View.GONE);
        } else if (mCurrentPos > 0) {
            mLeftArrow.setVisibility(View.VISIBLE);
            if (mCurrentPos == mLiveStreamList.size() - 1) {
                mRightArrow.setVisibility(View.GONE);
            }
        }
        String url = mLiveStreamList.get(mCurrentPos).getStreamName();
        playVideo(url);
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mCoordinatorLayout, mErrorMsg);
        }
    }

    @Override
    public void retrofitOnError(int code, String message, final int responseType) {
        super.retrofitOnError(code, message, responseType);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mCoordinatorLayout, mErrorMsg);
        }
    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
        String mErrorMsg = code + " - " + message;
        showSnackBar(mCoordinatorLayout, mErrorMsg);
    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        Toast.makeText(getApplicationContext(), mInternetFailed, Toast.LENGTH_SHORT).show();
        showSnackBar(mCoordinatorLayout, mInternetFailed);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //createPlayer(mFilePath);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

}
