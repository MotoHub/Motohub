package online.motohub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import butterknife.BindView;
import butterknife.ButterKnife;
import online.motohub.R;
import online.motohub.dialog.DialogManager;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.util.ExoPlayerUtils;

public class LoadVideoFromServerActivity extends BaseActivity {

    /* @BindView(R.id.toolbar)
     Toolbar mToolbar;*/
    @BindView(R.id.video_player)
    PlayerView mExoPlayerView;
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
        mExoPlayer = ExoPlayerUtils.Companion.getInstance().getExoPlayer();
        mExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
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
                String err = error.getMessage();
                if (TextUtils.isEmpty(err)) {
                    err = "Can't play this video";
                }
                showToast(LoadVideoFromServerActivity.this, err);
                mExoPlayer.stop();
                mExoPlayer.setPlayWhenReady(false);
                mProgressBar.setVisibility(View.GONE);
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
