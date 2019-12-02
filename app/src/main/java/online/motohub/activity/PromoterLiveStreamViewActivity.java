package online.motohub.activity;

import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.dialog.DialogManager;
import online.motohub.model.LiveStreamEntity;
import online.motohub.model.LiveStreamResponse;
import online.motohub.model.SessionModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;
import online.motohub.vlc.VlcListener;
import online.motohub.vlc.VlcVideoLibrary;

public class PromoterLiveStreamViewActivity extends BaseActivity implements VlcListener {


    public final static String TAG = "PromoterLiveStreamViewActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindString(R.string.live)
    String mToolbarTitle;

    @BindView(R.id.live_stream_co_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.surface_view)
    SurfaceView mSurfaceView;

    @BindView(R.id.left_arrow)
    ImageView mLeftArrow;
    @BindView(R.id.right_arrow)
    ImageView mRightArrow;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.main_lay)
    RelativeLayout mMainLay;

    @BindView(R.id.progress_lay)
    RelativeLayout mProgressLay;
    private ArrayList<LiveStreamEntity> mLiveStreamList = new ArrayList<>();
    private int mEventID = 0;
    private int mCurrentPos = 0;
    private VlcVideoLibrary mVLCLib;
    private int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_video_layout);
        ButterKnife.bind(this);

        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mEventID = mBundle.getInt(AppConstants.EVENT_ID, 0);
        }
    }

    private void initView() {
        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        mVLCLib = new VlcVideoLibrary(this, this, mSurfaceView);
        setProgress(false);
        mLiveStreamList.clear();
        loadVideo(0);
        callGetLiveStream();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void callGetLiveStream() {
        String mFilter = APIConstants.EventID + "=" + mEventID;
        RetrofitClient.getRetrofitInstance().callGetLiveStream(this, mFilter);
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.left_arrow, R.id.right_arrow})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
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

    @Override
    public void onBackPressed() {
        releasePlayer();
        finish();
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
                showToast(PromoterLiveStreamViewActivity.this, "No streams are available");
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
        if (mCurrentPos < mLiveStreamList.size()) {
            String url = mLiveStreamList.get(mCurrentPos).getStreamName();
            playVideo(url);
        }
    }

    private void playVideo(String url) {
        releasePlayer();
        try {
            setProgress(true);
//          setProgressState(true, "PROGRESS START ");
//          setProgressState(true, url.trim() + " ");
            mVLCLib.play(url.trim());
        } catch (Exception e) {
            setProgress(false);
//          setProgressState(false, "PROGRESS EXCEPTION ");
            Toast.makeText(this, "Error in creating player!  " + e.getMessage(), Toast
                    .LENGTH_LONG).show();
        }
    }

    private void releasePlayer() {
        if (mVLCLib != null) {
            setProgress(false);
            if (mVLCLib.isPlaying())
                mVLCLib.stop();
        }
//        setProgressState(false, "PROGRESS STOP ");
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    @Override
    public void onOpening() {
//        setProgress(true);
    }

    @Override
    public void onPlaying() {

    }

    @Override
    public void onStopped() {
        releasePlayer();
        Toast.makeText(PromoterLiveStreamViewActivity.this, "Live stream might be ended!!! Kindly choose another camera! ", Toast
                .LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        Toast.makeText(PromoterLiveStreamViewActivity.this, "Live stream might be ended!!! Kindly choose another camera! ", Toast
                .LENGTH_SHORT).show();
    }

    @Override
    public void onBuffer() {
        if (mVLCLib != null && mVLCLib.isPlaying()) {
            setProgress(false);
//            setProgressState(false, "PROGRESS BUFFER ");
        } else {
            setProgress(true);
//            setProgressState(true, "PROGRESS BUFFER ");
        }
    }

    private void setProgress(boolean isView) {
        mProgressLay.setVisibility(isView ? View.VISIBLE : View.GONE);
    }

//    private void setProgressState(boolean isView, String s) {
////        sysOut(s + isView);
////        sysOut(s + i);
////        i = i + 1;
//    }
}
