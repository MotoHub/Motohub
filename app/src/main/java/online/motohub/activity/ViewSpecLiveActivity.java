package online.motohub.activity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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

import java.net.URLDecoder;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsResModel;
import online.motohub.model.GalleryVideoModel;
import online.motohub.model.GalleryVideoResModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.dialog.DialogManager;
import online.motohub.util.OnSwipeTouchListener;
import online.motohub.util.UrlUtils;

public class ViewSpecLiveActivity extends BaseActivity {

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
    ArrayList<GalleryVideoResModel> mListLiveVideos;

    private EventsResModel mEventResModel;
    private SimpleExoPlayer mExoPlayer;

    private int pos = 0;
    private CacheDataSourceFactory mCacheDataSrcFactory;
    private String replaceString;

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_spec_live);
        ButterKnife.bind(this);
        setSwipeListenerForVideoView();
        String mTAG = getIntent().getStringExtra(AppConstants.TAG);
        if (mTAG.equals(EventLiveActivity.TAG))
            getSpecLiveVideos();
        else
            getVideoFrmIntent();
    }

    private void getVideoFrmIntent() {
        mListLiveVideos = new ArrayList<>();
        mListLiveVideos = getIntent().getParcelableArrayListExtra(AppConstants.VIDEOLIST);
        pos = getIntent().getIntExtra(AppConstants.POSITION, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mListLiveVideos != null) {
            mListLiveVideos.clear();
            manageNextVideoAndPlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializePlayer();
        if (mListLiveVideos != null && mListLiveVideos.size() > 0)
            playVideo();
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
                showToast(ViewSpecLiveActivity.this, "Can't play this video");
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

        mExoPlayer.setPlayWhenReady(mExoPlayer.getPlayWhenReady());
        mExoPlayer.seekTo(0, 0);

        Cache mCache = new SimpleCache(getCacheDir(),
                new LeastRecentlyUsedCacheEvictor(getCacheDir().getUsableSpace()));
        mCacheDataSrcFactory = new CacheDataSourceFactory(mCache,
                new DefaultHttpDataSourceFactory("ua"),
                CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setSwipeListenerForVideoView() {

        mVideoView.setOnTouchListener(new OnSwipeTouchListener(ViewSpecLiveActivity.this) {

            public void onSwipeRight() {
                managePrevVideoAndPlayer();
            }

            public void onSwipeLeft() {
                manageNextVideoAndPlayer();
            }
        });
    }

    @OnClick({R.id.btn_skip})
    public void onClick(View view) {
        manageNextVideoAndPlayer();
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

    private void playVideo() {
        try {
            if (mListLiveVideos.get(pos).getCaption().contains(" ")) {
                mCaption.setText(mListLiveVideos.get(pos).getCaption());
            } else {
                mCaption.setText(URLDecoder.decode(mListLiveVideos.get(pos).getCaption(), "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        replaceString = mListLiveVideos.get(pos).getVideoUrl().replaceAll(" ", "%20");

        Uri uri = Uri.parse(UrlUtils.AWS_S3_BASE_URL + replaceString);

        MediaSource mediaSource = new ExtractorMediaSource(uri,
                mCacheDataSrcFactory,
                new DefaultExtractorsFactory(),
                null,
                null);
        mExoPlayer.setPlayWhenReady(true); //run file/link when ready to play.
        mExoPlayer.prepare(mediaSource, true, false);
    }

    private void getSpecLiveVideos() {
        mEventResModel = (EventsResModel) getIntent().getSerializableExtra(EventsModel.EVENTS_RES_MODEL);
        if (mEventResModel != null && mEventResModel.getID() != 0) {
            String mFilter = "(EventID=" + mEventResModel.getID() + ") AND (EventFinishDate>=" + getCurrentDate() + ") AND (" + APIConstants.UserType + "!=user)";
            RetrofitClient.getRetrofitInstance()
                    .getPromoterVideoGalleryForSpectator(this,
                            mFilter, RetrofitClient.GET_VIDEO_FILE_RESPONSE);
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        GalleryVideoModel videoModel = (GalleryVideoModel) responseObj;
        if (videoModel != null && videoModel.getResModelList().size() > 0) {
            mListLiveVideos = new ArrayList<>();
            mListLiveVideos.addAll(videoModel.getResModelList());
            if (mListLiveVideos.size() > 0)
                playVideo();
        } else {
            showToast(ViewSpecLiveActivity.this, "No videos found!");
            finish();
        }
    }

    private void manageNextVideoAndPlayer() {
        if (pos < mListLiveVideos.size() - 1) {
            pos = pos + 1;
            if (pos <= mListLiveVideos.size() && pos >= 0)
                playVideo();
        } else {
            mExoPlayer.seekToDefaultPosition();
            mExoPlayer.setPlayWhenReady(false);
            finish();
        }
    }

    private void managePrevVideoAndPlayer() {
        if (pos > 0) {
            pos = pos - 1;
            if (pos <= mListLiveVideos.size() && pos >= 0)
                playVideo();
        } else {
            mExoPlayer.seekToDefaultPosition();
            mExoPlayer.setPlayWhenReady(false);
            finish();
        }
    }
}