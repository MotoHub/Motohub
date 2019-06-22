package online.motohub.activity;


import android.app.Dialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.LiveCamerasAdapter;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.LiveStreamEntity;
import online.motohub.model.LiveStreamResponse;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.dialog.DialogManager;
import online.motohub.util.PreferenceUtils;


public class ViewLiveVideoViewScreen extends BaseActivity implements LiveCamerasAdapter.CameraListener {


    public final static String TAG = "ViewLiveVideoViewScreen";
    @BindView(R.id.no_broadcast_txt)
    TextView mNoBroadcast;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindString(R.string.live)
    String mToolbarTitle;

    @BindView(R.id.live_stream_co_layout)
    CoordinatorLayout mCoordinatorLayout;


    @BindView(R.id.btnRetry)
    Button mRetry;

    @BindView(R.id.video_view)
    VideoView mVideoView;

    private Dialog customdialog;

    private ArrayList<LiveStreamEntity> mLiveStreamList = new ArrayList<>();

    private long mStartTime, mCurrentTime;
    private boolean isCameraAPI = false;
    private int mCurrentProfileID = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_live_video_view_screen);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mCurrentProfileID = mBundle.getInt(AppConstants.PROFILE_ID, 0);
        }
        mStartTime = System.currentTimeMillis();
        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
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
                Toast.makeText(ViewLiveVideoViewScreen.this, "Current Stream Stopped! ", Toast
                        .LENGTH_LONG).show();
                releasePlayer();
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                dismissAppDialog();
                Toast.makeText(ViewLiveVideoViewScreen.this, "No live stream is available! Kindly choose another camera! ", Toast
                        .LENGTH_LONG).show();
                return true;
            }
        });
        isCameraAPI = false;
        callGetLiveStream();
    }

    private void callGetLiveStream() {
        String mFilter = APIConstants.StreamProfileID + "=" + mCurrentProfileID;
        RetrofitClient.getRetrofitInstance().callGetLiveStream(this, mFilter);
    }


    private void playVideo(String url) {
        url = "http://temp1.pickzy.com/corners/uploads/message/183/video/1505910757248.mp4";
        releasePlayer();
        try {
            DialogManager.showProgress(this);
            mVideoView.setVideoURI(Uri.parse(url));
            mVideoView.requestFocus();
        } catch (Exception e) {
            DialogManager.hideProgress();
            Toast.makeText(this, "Error in creating player!  " + e.getMessage(), Toast
                    .LENGTH_LONG).show();
        }
    }

    private void releasePlayer() {
        if (mVideoView == null)
            return;
        mVideoView.stopPlayback();
    }

    @Override
    public void cameraClicked(int pos) {
        customdialog.dismiss();
        String mStreamName = mLiveStreamList.get(pos).getStreamName();
        String url = AppConstants.LIVE_BASE_URL + mStreamName;
        playVideo(url);
    }


    @OnClick({R.id.toolbar_back_img_btn, R.id.camera_floating_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                releasePlayer();
                finish();
                break;

            case R.id.camera_floating_btn:
                pauseVideo();
                mCurrentTime = System.currentTimeMillis();
                if ((mCurrentTime - mStartTime) >= 120000) {
                    mStartTime = System.currentTimeMillis();
                    isCameraAPI = true;
                    callGetLiveStream();
                } else {
                    showCameraList();
                }
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
//            Toast.makeText(getApplicationContext(), "Please wait..! Stream is Resuming", Toast.LENGTH_LONG).show();
//            Toast.makeText(getApplicationContext(), "Please wait..! Stream is Resuming", Toast.LENGTH_LONG).show();
//            Toast.makeText(getApplicationContext(), "Please wait..! Stream is Resuming", Toast.LENGTH_LONG).show();
//            Toast.makeText(getApplicationContext(), "Please wait..! Stream is Resuming", Toast.LENGTH_LONG).show();
//            Toast.makeText(getApplicationContext(), "Please wait..! Stream is Resuming", Toast.LENGTH_LONG).show();
        }
    }

    private void showCameraList() {
        // Create custom dialog object
        if (mLiveStreamList.size() > 0) {
            customdialog = new Dialog(this, R.style.MyDialogBottomSheet);
            // Include dialog.xml file
            customdialog.setContentView(R.layout.dialog_live_stream_cameras);
            // Set dialog title
            customdialog.setTitle("Custom Dialog");

            ImageView ivCloseDialog = customdialog.findViewById(R.id.ivCancelDialog);
            ivCloseDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resumeVideo();
                    customdialog.dismiss();
                }
            });

            final RecyclerView mCamerasRecyclerView = customdialog.findViewById(R.id.live_cameras_recycler_view);
            mCamerasRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            mCamerasRecyclerView.setItemAnimator(new DefaultItemAnimator());
            LiveCamerasAdapter mAdapter = new LiveCamerasAdapter(this, ViewLiveVideoViewScreen.this, mLiveStreamList);
            mCamerasRecyclerView.setAdapter(mAdapter);
            customdialog.show();
        } else {
            showSnackBar(mCoordinatorLayout, "No streams are available");
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
                if (isCameraAPI) {
                    isCameraAPI = false;
                    showCameraList();
                } else {
                    String mStreamName = mLiveStreamList.get(mLiveStreamList.size() - 1).getStreamName();
                    String url = AppConstants.LIVE_BASE_URL + mStreamName;
                    playVideo(url);
                }
            } else {
                showToast(ViewLiveVideoViewScreen.this, "No streams are available");
                finish();
            }

        } else if (responseObj instanceof SessionModel) {

            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            // showSnackBar(mCoordinatorLayout, mSessionUpdated);
            callGetLiveStream();

        }
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
        DialogManager.hideProgress();
        super.onDestroy();
        releasePlayer();
    }
}
