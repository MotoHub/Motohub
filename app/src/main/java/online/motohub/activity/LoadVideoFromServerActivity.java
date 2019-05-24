package online.motohub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

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
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import butterknife.BindView;
import butterknife.ButterKnife;
import online.motohub.R;
import online.motohub.util.AppConstants;
import online.motohub.util.DialogManager;

public class LoadVideoFromServerActivity extends BaseActivity {

    /* @BindView(R.id.toolbar)
     Toolbar mToolbar;*/
    @BindView(R.id.video_player)
    SimpleExoPlayerView mExoPlayerView;
    @BindView(R.id.videoProgress)
    ProgressBar mProgressBar;

    private SimpleExoPlayer mExoPlayer;
    private String mVideoUrl = "", replaceString;
    private int post_pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); //Remove title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //Remove notification bar
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_load_video_from_server);
        ButterKnife.bind(this);
        mVideoUrl = getIntent().getStringExtra(AppConstants.VIDEO_PATH);
        post_pos = getIntent().getIntExtra(AppConstants.POSITION, 0);
        setResult(RESULT_OK, new Intent().putExtra(AppConstants.POSITION, post_pos));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initializePlayer() {

        mExoPlayerView.setUseController(true);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector mTrackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl mLoadControl = new DefaultLoadControl(new DefaultAllocator(true, 16 * 1024),
                25000, 30000, 2500, 5000);
//        LoadControl mLoadControl = new DefaultLoadControl();
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
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                sysOut("Listener-onPlayerStateChanged..." + playbackState);
                if (playbackState == ExoPlayer.STATE_BUFFERING) {
                    mProgressBar.setVisibility(View.VISIBLE);
                } else if (playbackState == ExoPlayer.STATE_READY) {
                    mProgressBar.setVisibility(View.GONE);
                } else if (playbackState == ExoPlayer.STATE_ENDED) {
                    mExoPlayer.setPlayWhenReady(false);
                    mProgressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                sysOut("Listener-onPlayerError...");
                showToast(LoadVideoFromServerActivity.this, "Can't play this video");
                mExoPlayer.stop();
                mExoPlayer.setPlayWhenReady(false);
                mProgressBar.setVisibility(View.GONE);
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
        mExoPlayerView.requestFocus();
        mExoPlayerView.setPlayer(mExoPlayer);
        mExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        mExoPlayer.seekTo(0, 0);
        /*Uri uri = Uri.parse(mVideoUrl + "?api_key="
                + getResources().getString(R.string.dream_factory_api_key)
                + "&session_token="
                + PreferenceUtils.getInstance(this).getStrData(PreferenceUtils
                .SESSION_TOKEN));*/


        replaceString = mVideoUrl.replaceAll(" ", "%20");

        Uri uri = Uri.parse(replaceString);

        Cache mCache = new SimpleCache(getCacheDir(), new LeastRecentlyUsedCacheEvictor(getCacheDir().getUsableSpace()));
        DataSource.Factory mCacheDataSrcFactory = new CacheDataSourceFactory(mCache, new DefaultHttpDataSourceFactory("ua"), CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
        MediaSource mediaSource = new ExtractorMediaSource(uri, mCacheDataSrcFactory, new DefaultExtractorsFactory(), null, null);
        mExoPlayer.setPlayWhenReady(true); //run file/link when ready to play.
        mExoPlayer.prepare(mediaSource, true, false);
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
//            initializePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }
}
