package online.motohub.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.model.EventsResModel;
import online.motohub.model.OnDemandLoadVideosWhilePlaying;
import online.motohub.model.PromoterVideoModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.DialogManager;
import online.motohub.util.OnSwipeTouchListener;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.UrlUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnDemanAudoVideoView extends BaseActivity {

    @BindView(R.id.tv_name)
    TextView mUserName;
    @BindView(R.id.tv_time)
    TextView mTime;
    @BindView(R.id.tv_caption)
    TextView mCaption;
    @BindView(R.id.video_view)
    SimpleExoPlayerView mVideoView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    int remainder;
    private EventsResModel mEventResModel;
    private SimpleExoPlayer mExoPlayer;
    private int pos = 0;
    private CacheDataSourceFactory mCacheDataSrcFactory;
    private  ArrayList<PromoterVideoModel.Resource> mPostsList = new ArrayList<>();


    private ArrayList<HashMap<String, String>> mVideoList;
    private int ProfileID;
    private String mFilter = "(EventFinishDate<" + getCurrentDate() + ") AND (UserType != user) AND (UserID =" + ProfileID + ")";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); //Remove title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //Remove notification bar
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_on_deman_audo_video_view);
        ButterKnife.bind(this);
        setSwipeListenerForVideoView();
        //mPostsList = (ArrayList<PromoterVideoModel.Resource>) getIntent().getSerializableExtra(AppConstants.ONDEMAND_DATA);
        mPostsList = (ArrayList<PromoterVideoModel.Resource>) getIntent().getSerializableExtra(AppConstants.ONDEMAND_DATA);
       // mVideoList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra(AppConstants.ONDEMAND_DATA);
        ProfileID = getIntent().getIntExtra("ID", 0);
        pos = getIntent().getIntExtra(AppConstants.POSITION, 0);
        mVideoList = convertModelToList(mPostsList);
    }

    private ArrayList<HashMap<String, String>> convertModelToList(List<PromoterVideoModel.Resource> mPostsList) {
        ArrayList<HashMap<String, String>> mVideoList = new ArrayList<>();
        for (PromoterVideoModel.Resource mResource : mPostsList) {
            HashMap<String, String> mMapData = new HashMap<>();
            mMapData.put(AppConstants.VIDEO_PATH, mResource.getVideoUrl());
            mMapData.put(AppConstants.CAPTION, mResource.getCaption());
            mVideoList.add(mMapData);
        }
        return mVideoList;
    }


    @Override
    protected void onResume() {
        super.onResume();
        initializePlayer();
        if (mVideoList != null && mVideoList.size() > 0)
            playVideo();
    }

    private void setSwipeListenerForVideoView() {
        mVideoView.setOnTouchListener(new OnSwipeTouchListener(OnDemanAudoVideoView.this) {

            public void onSwipeRight() {
                managePrevVideoAndPlayer();
            }

            public void onSwipeLeft() {
                manageNextVideoAndPlayer();
            }
        });
    }

    private void initializePlayer() {
        mVideoView.setUseController(false);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector mTrackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl mLoadControl = new DefaultLoadControl(new DefaultAllocator(true, 16 * 1024),
                25000, 30000, 2500, 5000);

        mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                mTrackSelector, mLoadControl);

        mExoPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
                sysOut("Listener-onTimelineChanged...");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                sysOut("Listener-onTracksChanged...");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                sysOut("Listener-onLoadingChanged...isLoading:" + isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                sysOut("Listener-onPlayerStateChanged..." + playbackState);
                if (playbackState == ExoPlayer.STATE_BUFFERING) {
                    mProgressBar.setVisibility(View.VISIBLE);
                } else if (playbackState == ExoPlayer.STATE_READY) {
                    mProgressBar.setVisibility(View.GONE);
                } else if (playbackState == ExoPlayer.STATE_ENDED) {
                    if (mExoPlayer != null)
                        manageNextVideoAndPlayer();
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                sysOut("Listener-onPlayerError...");
                showToast(OnDemanAudoVideoView.this, "Can't play this video");
                manageNextVideoAndPlayer();
            }

            @Override
            public void onPositionDiscontinuity() {
                sysOut("Listener-onPositionDiscontinuity...");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                sysOut("Listener-onPlaybackParametersChanged...");
            }

        });
        mVideoView.requestFocus();
        mVideoView.setPlayer(mExoPlayer);
        mVideoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        mExoPlayer.setPlayWhenReady(mExoPlayer.getPlayWhenReady());
        mExoPlayer.seekTo(0, 0);

        Cache mCache = new SimpleCache(getCacheDir(),
                new LeastRecentlyUsedCacheEvictor(getCacheDir().getUsableSpace()));
        mCacheDataSrcFactory = new CacheDataSourceFactory(mCache,
                new DefaultHttpDataSourceFactory("ua"),
                CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
    }

    @OnClick({R.id.btn_skip})
    public void onClick(View view) {
        manageNextVideoAndPlayer();
    }

    private void manageNextVideoAndPlayer() {
        if (pos <= mVideoList.size()) {
            pos = pos + 1;
            if (pos <= mVideoList.size() && pos >= 0) {
                remainder = mVideoList.size() % 10;
                if (remainder == 0 && pos == mVideoList.size()) {
                    callGetPromoterVideosWhilePlaying(OnDemanAudoVideoView.this, mFilter, mVideoList.size(), true);
                }
                playVideo();
            }
        } else {
            mExoPlayer.seekToDefaultPosition();
            mExoPlayer.setPlayWhenReady(false);
            finish();
        }
    }

    private void managePrevVideoAndPlayer() {
        if (pos > 0) {
            pos = pos - 1;
            if (pos <= mVideoList.size() && pos >= 0) {
                playVideo();
            }
        } else {
            mExoPlayer.seekToDefaultPosition();
            mExoPlayer.setPlayWhenReady(false);
            finish();
        }
    }

    private void playVideo() {
        try {
            mCaption.setText(mVideoList.get(pos).get(AppConstants.CAPTION));
            Uri uri = Uri.parse(UrlUtils.FILE_URL + mVideoList.get(pos).get(AppConstants.VIDEO_PATH) + "?api_key="
                    + getResources().getString(R.string.dream_factory_api_key)
                    + "&session_token="
                    + PreferenceUtils.getInstance(this).getStrData(PreferenceUtils
                    .SESSION_TOKEN));

            MediaSource mediaSource = new ExtractorMediaSource(uri,
                    mCacheDataSrcFactory,
                    new DefaultExtractorsFactory(),
                    null,
                    null);
            mExoPlayer.setPlayWhenReady(true); //run file/link when ready to play.
            mExoPlayer.prepare(mediaSource, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    public void callGetPromoterVideosWhilePlaying(final BaseActivity activity, String filter, int mOffset, boolean mIsLoadMore) {
        int mLimit = mVideoList.size();
        String fields = "Caption,VideoUrl";
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.GALLERY_VIDEO + "?fields=" + fields + "&filter=" + filter +
                "&limit=" + mLimit + "&offset=" + mOffset + "&include_count=true");
        if (!mIsLoadMore)
            DialogManager.showProgress(activity);

        String mOrderBy = "CreatedAt DESC";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetPromotersVideosWhilePlaying(fields, filter,
                mOrderBy, mLimit, mOffset, true)
                .enqueue(new Callback<OnDemandLoadVideosWhilePlaying>() {
                    @Override
                    public void onResponse(Call<OnDemandLoadVideosWhilePlaying> call, Response<OnDemandLoadVideosWhilePlaying> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            OnDemandLoadVideosWhilePlaying obj = response.body();
                            for (OnDemandLoadVideosWhilePlaying.Resource mResource : obj.getResource()) {
                                HashMap<String, String> mMapData = new HashMap<>();
                                mMapData.put(AppConstants.VIDEO_PATH, mResource.getVideoUrl());
                                mMapData.put(AppConstants.CAPTION, mResource.getCaption());
                                mVideoList.add(mMapData);
                            }
                        } else {
                            showToast(OnDemanAudoVideoView.this, "" + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<OnDemandLoadVideosWhilePlaying> call, Throwable t) {
                        DialogManager.hideProgress();
                        showToast(OnDemanAudoVideoView.this, getResources().getString(R.string.internet_err));
                    }
                });
    }


}
