package online.motohub.activity;


import android.app.Dialog;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.LiveCamerasAdapter;
import online.motohub.dialog.DialogManager;
import online.motohub.model.LiveStreamEntity;
import online.motohub.model.LiveStreamResponse;
import online.motohub.retrofit.RetrofitClient;


public class LiveStreamingActivity extends BaseActivity implements IVLCVout.Callback, LiveCamerasAdapter.CameraListener {


    public final static String TAG = "LiveStreamingActivity";
    @BindView(R.id.no_broadcast_txt)
    TextView mNoBroadcast;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindString(R.string.live_stream)
    String mToolbarTitle;
    @BindView(R.id.live_stream_co_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.btnRetry)
    Button mRetry;
    private BaseActivity mBaseActivity;
    private SurfaceView mSurface;
    private SurfaceHolder holder;
    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;
    private Dialog customdialog;

    private ArrayList<LiveStreamEntity> mLiveStreamList;
    /**
     * Registering callbacks
     */
    private MediaPlayer.EventListener mPlayerListener = new MyPlayerListener(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_live_stream);
        ButterKnife.bind(this);
        mBaseActivity = this;
        mSurface = findViewById(R.id.surface);
        holder = mSurface.getHolder();

        initView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setSize(mVideoWidth, mVideoHeight);
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

    private void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        if (holder == null || mSurface == null)
            return;

        int w = getWindow().getDecorView().getWidth();
        int h = getWindow().getDecorView().getHeight();
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        holder.setFixedSize(mVideoWidth, mVideoHeight);
        ViewGroup.LayoutParams lp = mSurface.getLayoutParams();
        lp.width = w;
        lp.height = h;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();
    }

    private void playVideo(String media) {
        releasePlayer();
        try {
            holder.setKeepScreenOn(true);

            // Creating media player
            mMediaPlayer = new MediaPlayer(libvlc);

            mMediaPlayer.setEventListener(mPlayerListener);
            // Seting up video output
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(mSurface);
            //vout.setSubtitlesView(mSurfaceSubtitles);
            vout.addCallback(this);
            vout.attachViews();
            mRetry.setVisibility(View.GONE);
            mSurface.setVisibility(View.VISIBLE);

            Media m = new Media(libvlc, Uri.parse(media));
            mMediaPlayer.setMedia(m);

            mMediaPlayer.play();

        } catch (Exception e) {

            Toast.makeText(this, "Error in creating player!", Toast
                    .LENGTH_LONG).show();
        }


    }

    private void releasePlayer() {
        if (libvlc == null)
            return;
        if (mMediaPlayer == null)
            return;

        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.removeCallback(this);
        vout.detachViews();
        mMediaPlayer.stop();

        libvlc.release();
        mVideoWidth = 0;
        mVideoHeight = 0;
    }
//
//    @Override
//    public void onNewLayout(IVLCVout vout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
//        if (width * height == 0)
//            return;
//
//        // store video size
//        mVideoWidth = width;
//        mVideoHeight = height;
//        setSize(mVideoWidth, mVideoHeight);
//    }

    @Override
    public void onSurfacesCreated(IVLCVout vout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vout) {

    }

//    @Override
//    public void onHardwareAccelerationError(IVLCVout vlcVout) {
//        this.releasePlayer();
//        Toast.makeText(this, "Error with hardware acceleration", Toast.LENGTH_LONG).show();
//    }

    @Override
    public void cameraClicked(int pos) {
        customdialog.dismiss();
        String mStreamName = mLiveStreamList.get(pos).getStreamName();
        String url = "rtsp://208.109.95.214:1935/live/" + mStreamName;
        playVideo(url);

        // ApiClient.getRetrofitInstance().getDeviceInfo(this,mDeviceID, ApiClient.GET_DEVICE_INFO_RESPONSE);
    }

    private void initView() {
        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        mLiveStreamList = new ArrayList<>();
        RetrofitClient.getRetrofitInstance().callGetLiveStream(this, "");

        // Create LibVLC
        ArrayList<String> options = new ArrayList<>();
        //options.add("--subsdec-encoding <encoding>");
        options.add("--aout=opensles");
        options.add("--audio-time-stretch"); // time stretching
        options.add("-vvv"); // verbosity
        libvlc = new LibVLC(this, options);
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.camera_floating_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                releasePlayer();
                finish();
                break;

            case R.id.camera_floating_btn:
                // Create custom dialog object
                customdialog = new Dialog(this, R.style.MyDialogBottomSheet);
                // Include dialog.xml file
                customdialog.setContentView(R.layout.dialog_live_stream_cameras);
                // Set dialog title
                customdialog.setTitle("Custom Dialog");

                ImageView ivCloseDialog = customdialog.findViewById(R.id.ivCancelDialog);
                ivCloseDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customdialog.dismiss();
                    }
                });

                final RecyclerView mCamerasRecyclerView = customdialog.findViewById(R.id.live_cameras_recycler_view);
                mCamerasRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                mCamerasRecyclerView.setItemAnimator(new DefaultItemAnimator());
                LiveCamerasAdapter mAdapter = new LiveCamerasAdapter(this, LiveStreamingActivity.this, mLiveStreamList);
                mCamerasRecyclerView.setAdapter(mAdapter);
                customdialog.show();
                break;
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
                String mStreamName = mLiveStreamList.get(0).getStreamName();
                String url = "rtsp://208.109.95.214:1935/live/" + mStreamName;
                playVideo(url);
            } else {
                //TODO no streams available
            }

        }
/*

        mRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retryAPI(responseType);
            }
        });
*/


    /*    if(responseObj instanceof DeviceInfoModel){

            DeviceInfoModel mDeviceInfoModel = (DeviceInfoModel) responseObj;

            if(mDeviceInfoModel.mEasyDarwin.mHeader.ErrorNum.equals("200")) {

                String mSnapURL = mDeviceInfoModel.mEasyDarwin.mBody.SnapURL;

                if (!mSnapURL.trim().isEmpty()) {

                    mSnapImage.setVisibility(View.VISIBLE);

                    mBaseActivity.setImageWithGlide(mSnapImage, mSnapURL, R.drawable.default_profile_icon);
                }

                ApiClient.getRetrofitInstance().startDeviceStream(this, mDeviceID,ApiClient.START_DEVICE_STREAM);
            } else{


                mRetry.setVisibility(View.VISIBLE);
                Toast.makeText(this,mDeviceInfoModel.mEasyDarwin.mHeader.ErrorString,Toast.LENGTH_LONG).show();
            }

        } else if(responseObj instanceof DeviceStreamModel){

            DeviceStreamModel mDeviceStreamModel = (DeviceStreamModel) responseObj;

            if(mDeviceStreamModel.mEasyDarwin.mHeader.ErrorNum.equals("200")){

                ApiClient.getRetrofitInstance().getDeviceStream(this,mDeviceID,ApiClient.GET_DEVICE_STREAM);
            } else{

                mRetry.setVisibility(View.VISIBLE);
                Toast.makeText(this,mDeviceStreamModel.mEasyDarwin.mHeader.ErrorString,Toast.LENGTH_LONG).show();
            }

        } else if(responseObj instanceof GetDeviceStreamModel){

            GetDeviceStreamModel mGetDeviceStreamModel = (GetDeviceStreamModel) responseObj;

            if(mGetDeviceStreamModel.mEasyDarwin.mHeader.ErrorNum.equals("200")) {
                mSnapImage.setVisibility(View.GONE);

                String mPlayBackURL = mGetDeviceStreamModel.mEasyDarwin.mBody.getURL().trim();

                playVideo(mPlayBackURL);


            } else{

                mRetry.setVisibility(View.VISIBLE);
                Toast.makeText(this,mGetDeviceStreamModel.mEasyDarwin.mHeader.ErrorString,Toast.LENGTH_LONG).show();
            }

        }
*/
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

      /*  mRetry.setVisibility(View.VISIBLE);
        String mErrorMsg = code + " - " + message;
        showSnackBar(mCoordinatorLayout, mErrorMsg);
        mRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retryAPI(responseType);
            }
        });*/
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

    private static class MyPlayerListener implements MediaPlayer.EventListener {
        private WeakReference<LiveStreamingActivity> mOwner;

        MyPlayerListener(LiveStreamingActivity owner) {
            mOwner = new WeakReference<>(owner);
        }

        @Override
        public void onEvent(MediaPlayer.Event event) {
            LiveStreamingActivity player = mOwner.get();

            switch (event.type) {
                case MediaPlayer.Event.EndReached:
                    player.releasePlayer();
                    break;
                case MediaPlayer.Event.Buffering:
                    break;
                case MediaPlayer.Event.Playing:
                case MediaPlayer.Event.Paused:
                case MediaPlayer.Event.Stopped:


                default:
                    break;
            }
        }
    }
/*
    public void retryAPI(int responseType){

        //playVideo("rtmp://184.72.239.149/vod/BigBuckBunny_115k.mov");


        switch (responseType){
            case ApiClient.GET_DEVICE_INFO_RESPONSE:
                ApiClient.getRetrofitInstance().getDeviceInfo(this,mDeviceID, ApiClient.GET_DEVICE_INFO_RESPONSE);
                break;
            case ApiClient.START_DEVICE_STREAM:
                ApiClient.getRetrofitInstance().startDeviceStream(this,mDeviceID,ApiClient.START_DEVICE_STREAM);
                break;
            case ApiClient.GET_DEVICE_STREAM:
                ApiClient.getRetrofitInstance().getDeviceStream(this,mDeviceID,ApiClient.GET_DEVICE_STREAM);
                break;
        }

    }*/
}
