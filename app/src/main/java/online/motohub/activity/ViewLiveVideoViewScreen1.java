package online.motohub.activity;


import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import online.motohub.constants.AppConstants;
import online.motohub.dialog.DialogManager;
import online.motohub.util.PreferenceUtils;


public class ViewLiveVideoViewScreen1 extends BaseActivity {


    public final static String TAG = "ViewLiveVideoViewScreen";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindString(R.string.live)
    String mToolbarTitle;

    @BindView(R.id.live_stream_co_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.pager)
    ViewPager mViewPager;

    @BindView(R.id.left_arrow)
    ImageView mLeftArrow;
    @BindView(R.id.right_arrow)
    ImageView mRightArrow;

    private VideoView mVideoView;
    private ArrayList<LiveStreamEntity> mLiveStreamList = new ArrayList<>();

    private int mCurrentProfileID = 0;
    private ViewPagerAdapter mViewPagerAdapter;

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
        callGetLiveStream();

        mViewPager
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position,
                                               float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (mViewPager.getCurrentItem() == 0) {
                            mLeftArrow.setVisibility(View.GONE);
                        } else {
                            mLeftArrow.setVisibility(View.VISIBLE);
                        }
                        if ((mViewPager.getCurrentItem() == mLiveStreamList.size() - 1)) {
                            mRightArrow.setVisibility(View.GONE);
                        } else {
                            mRightArrow.setVisibility(View.VISIBLE);
                        }
                        String mStreamName = mLiveStreamList.get(position).getStreamName();
                        String url = AppConstants.LIVE_BASE_URL + mStreamName;
//                        String url = "http://temp1.pickzy.com/corners/uploads/message/183/video/1505910757248.mp4";
                        playVideo(url);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
    }

    private void initVideoView(final VideoView videoView) {
        releasePlayer();
        mVideoView = videoView;
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
                Toast.makeText(ViewLiveVideoViewScreen1.this, "Current Stream Stopped! ", Toast
                        .LENGTH_LONG).show();
                releasePlayer();
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                dismissAppDialog();
                Toast.makeText(ViewLiveVideoViewScreen1.this, "Live stream might be ended!!! Kindly choose another camera! ", Toast
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
//        url = "http://temp1.pickzy.com/corners/uploads/message/183/video/1505910757248.mp4";
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
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                break;
            case R.id.right_arrow:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
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
                setAdapter();
            } else {
                showToast(ViewLiveVideoViewScreen1.this, "No streams are available");
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

    private void setAdapter() {
        if (mLiveStreamList.size() > 1) {
            mRightArrow.setVisibility(View.VISIBLE);
        }
        mViewPagerAdapter = new ViewPagerAdapter(this, mLiveStreamList);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(mLiveStreamList.size());
        mViewPager.setCurrentItem(0);

        String mStreamName = mLiveStreamList.get(0).getStreamName();
        String url = AppConstants.LIVE_BASE_URL + mStreamName;
//                        String url = "http://temp1.pickzy.com/corners/uploads/message/183/video/1505910757248.mp4";
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
        DialogManager.hideProgress();
        super.onDestroy();
        releasePlayer();
    }

    private class ViewPagerAdapter extends PagerAdapter {
        private Context mContext;
        private ArrayList<LiveStreamEntity> mLiveStreamList;
        private LayoutInflater mInflater;

        public ViewPagerAdapter(Context context, ArrayList<LiveStreamEntity> liveStreamList) {
            mContext = context;
            mLiveStreamList = liveStreamList;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mLiveStreamList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View convertView = mInflater.inflate(R.layout.adap_video_view, container,
                    false);
            VideoView mVideoView = convertView.findViewById(R.id.video_view);

            initVideoView(mVideoView);

            container.addView(convertView);

            return convertView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((CoordinatorLayout) object);
        }
    }
}
