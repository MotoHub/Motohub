package online.motohub.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.plattysoft.leonids.ParticleSystem;

import org.greenrobot.eventbus.EventBus;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.application.MotoHub;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsResModel;
import online.motohub.model.FollowProfileModel;
import online.motohub.model.PostsModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PromoterVideoModel;
import online.motohub.model.VideoLikesModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.CommonAPI;
import online.motohub.util.DialogManager;
import online.motohub.util.OnSwipeTouchListener;
import online.motohub.util.UrlUtils;
import online.motohub.util.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnDemanAudoVideoView extends BaseActivity {

    @BindView(R.id.tv_name)
    TextView mUserName;
    @BindView(R.id.tv_time)
    TextView mTime;
    @BindView(R.id.follow_count_txt)
    Button follow_unfollow_txt;
    @BindView(R.id.tv_caption)
    TextView mCaption;
    @BindView(R.id.video_view)
    SimpleExoPlayerView mVideoView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.likeBtn)
    Button mLikeBtn;
    @BindView(R.id.right_btn)
    Button mRightBtn;
    @BindView(R.id.left_btn)
    Button mLeftBtn;
    @BindView(R.id.follow_unfollow_btn)
    Button mFollowBtn;
    @BindView(R.id.like_count_txt)
    Button mLikeCountTxt;
    @BindView(R.id.img_logo)
    ImageView imgLogo;

    private SimpleExoPlayer mExoPlayer;
    private ProfileResModel mMyProfileResModel, mOtherProfileResModel;
    private int pos = 0, checkPosition = 0;
    private CacheDataSourceFactory mCacheDataSrcFactory;
    private ArrayList<PromoterVideoModel.Resource> mPostsList = new ArrayList<>();
    private boolean mIsAlreadyBlocked, isBlockedme, mIsAlreadyFollowing, isNextVideo, isLoadedData;
    private String usertype, replaceString, mFilter;
    private int mOtherProfileID, mMyProfileID, mDeleteLikeID;
    private int mQuotient, mTotalCount;
    private ArrayList<EventsResModel.EventadByEventID> imageList;
    private EventsResModel mEventResModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); //Remove title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //Remove notification bar
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_on_deman_audo_video_view);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        try {
            imageList = new ArrayList<>();
            setSwipeListenerForVideoView();
            checkPosition = getIntent().getIntExtra(AppConstants.POSITION, 0);
            mFilter = getIntent().getStringExtra("Filter");
            String profile = getIntent().getStringExtra("profile");
            mMyProfileResModel = new Gson().fromJson(profile, ProfileResModel.class);
            mPostsList = MotoHub.getApplicationInstance().getmPostsList();
            mOtherProfileID = Integer.parseInt(mPostsList.get(pos).getProfileID());
            mEventResModel = mPostsList.get(pos).getEvent_by_EventID();
            if (mMyProfileResModel != null) {
                mMyProfileID = mMyProfileResModel.getID();
            }
            if (mEventResModel != null)
                getEvent();
            pos = checkPosition;

            if (isAlreadyLikedPost()) {
                mLikeBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.liked_icon));
                mLikeCountTxt.setText("unlike");
                mLikeBtn.setTag("unlike");
            } else {
                mLikeBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.like_icon));
                mLikeBtn.setTag("like");
                mLikeCountTxt.setText("like");
            }
            isAlreadyFollowed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getEvent() {
        String mFilter = "ID=" + mEventResModel.getID();
        RetrofitClient.getRetrofitInstance().callGetEvents(this, mFilter, RetrofitClient.GET_EVENTS_RESPONSE);
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        if (mPostsList != null)
            EventBus.getDefault().removeStickyEvent(mPostsList);
        super.onDestroy();
    }

    private void viewHideAfterload() {
        mRightBtn.postDelayed(new Runnable() {
            public void run() {
                mRightBtn.setVisibility(View.GONE);
            }
        }, 3000);
        mLeftBtn.postDelayed(new Runnable() {
            public void run() {
                mLeftBtn.setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void isAlreadyFollowed() {
        //TODO your background code
        try {
            if (pos >= 0) {
                usertype = mPostsList.get(pos).getUserType().trim();
                mOtherProfileID = Integer.parseInt(mPostsList.get(pos).getProfileID());
                mFollowBtn.setVisibility(View.GONE);
                follow_unfollow_txt.setVisibility(View.GONE);
                if (mOtherProfileID != mMyProfileID) {
                    if (usertype.equals(AppConstants.USER)
                            || usertype.equals(AppConstants.ONDEMAND)
                            || usertype.equals(AppConstants.USER_EVENT_VIDEOS)) {
                        String mFollowRelation = mOtherProfileID + "_" + mMyProfileID;
                        RetrofitClient.getRetrofitInstance().callGetIsAlreadyFollowedUserWithoutBuffering
                                (OnDemanAudoVideoView.this, mFollowRelation, RetrofitClient.PROFILE_IS_ALREADY_FOLLOWED);
                    } else {
                        String mFollowRelation = mMyProfileID + "_" + mOtherProfileID;
                        RetrofitClient.getRetrofitInstance().callGetIsAlreadyFollowedPromoterWithoutBuffering
                                (OnDemanAudoVideoView.this, mFollowRelation, RetrofitClient.PROMOTER_IS_ALREADY_FOLLOWED);
                    }
                    mFollowBtn.setVisibility(View.VISIBLE);
                    follow_unfollow_txt.setVisibility(View.VISIBLE);
                } else if (mOtherProfileID == mMyProfileID) {
                    mFollowBtn.setVisibility(View.GONE);
                    follow_unfollow_txt.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFollowUnfollow() {
        if (mIsAlreadyFollowing) {
            follow_unfollow_txt.setText("UnFollow");
            mFollowBtn.setBackground(ContextCompat.getDrawable(OnDemanAudoVideoView.this, R.drawable.ic_follow));
        } else if (!mIsAlreadyFollowing) {
            follow_unfollow_txt.setText("Follow");
            mFollowBtn.setBackground(ContextCompat.getDrawable(OnDemanAudoVideoView.this, R.drawable.ic_unfollow));
        }
    }

    private void callGetProfile(final int mProfileID, final int responseType) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                String mFilter = "id=" + mProfileID;
                RetrofitClient.getRetrofitInstance().callGetProfilesWithoutBuffering(OnDemanAudoVideoView.this, mFilter, responseType);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //setResult(RESULT_OK, new Intent().putExtra(AppConstants.VIDEO_LIST, mPostsList));
        mPostsList.clear();
        manageNextVideoAndPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializePlayer();
        mPostsList.removeAll(Collections.singleton(null));
        isNextVideo = true;
        mQuotient = mPostsList.size() / 10;
        if (mPostsList != null && mPostsList.size() > 0) {
            playVideo();
        }
        viewHideAfterload();
        updateVideoList();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setSwipeListenerForVideoView() {
        mVideoView.setOnTouchListener(new OnSwipeTouchListener(OnDemanAudoVideoView.this) {

            public void onSwipeRight() {
                isNextVideo = false;
                if (mPostsList.size() - 1 > pos)
                    managePrevVideoAndPlayer();
                else
                    mVideoView.setFocusable(false);
            }

            public void onSwipeLeft() {
                isNextVideo = true;
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
                    if (mExoPlayer != null) {
                        if (isNextVideo)
                            manageNextVideoAndPlayer();
                        else
                            managePrevVideoAndPlayer();
                    }
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                sysOut("Listener-onPlayerError...");
                showToast(OnDemanAudoVideoView.this, "Can't play this video");
                if (isNextVideo)
                    manageNextVideoAndPlayer();
                else
                    managePrevVideoAndPlayer();
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

        Cache mCache = new SimpleCache(getCacheDir(), new LeastRecentlyUsedCacheEvictor(getCacheDir().getUsableSpace()));
        mCacheDataSrcFactory = new CacheDataSourceFactory(mCache, new DefaultHttpDataSourceFactory("ua"),
                CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
    }

    private void updateVideoList() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                if (mTotalCount != mPostsList.size() && isLoadedData == false && pos >=
                        mPostsList.size() - 5) {
                    //Log.d("calling api", pos + ", " + mPostsList.size());
                    isLoadedData = true;
                    callGetPromoterVideosWhilePlaying(OnDemanAudoVideoView.this, mFilter, mPostsList.size(), true);
                }
            }
        });
    }

    @OnClick({R.id.btn_skip, R.id.likeBtn, R.id.follow_unfollow_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_skip:
                if (pos < mPostsList.size()) {
                    isNextVideo = true;
                    manageNextVideoAndPlayer();
                }
                break;
            case R.id.follow_unfollow_btn:
                if (!isMultiClicked()) {
                    follow_unfollow();
                }
                break;
            case R.id.likeBtn:
                if (!isMultiClicked() && pos >= 0) {
                    if (view.getTag().toString().equals("like")) {
                        new ParticleSystem(this, 50, R.drawable.liked_icon, 1000)
                                .setSpeedRange(0.2f, 0.5f)
                                .oneShot(findViewById(R.id.likeBtn), 25);
                        try {
                            callLikePost(mPostsList.get(pos).getID(), mMyProfileResModel.getID());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        mLikeBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.like_icon));
                        mLikeBtn.setTag("like");
                        callUnLikePost(mDeleteLikeID);
                    }
                }
                break;
        }
    }

    private void follow_unfollow() {
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {*/
        // Do whatever you want
        try {
            if (pos >= 0) {
                isBlockedme = Utility.getInstance().isAlreadyBlockedMe(mMyProfileResModel.getBlockeduserprofiles_by_BlockedProfileID(),
                        Integer.parseInt(mPostsList.get(pos).getUserID()));
                mIsAlreadyBlocked = Utility.getInstance().isAlreadyBlocked(mMyProfileResModel.getBlockedUserProfilesByProfileID(),
                        Integer.parseInt(mPostsList.get(pos).getUserID()));
                if (mIsAlreadyBlocked) {
                    showToast(OnDemanAudoVideoView.this, getString(R.string.follow_err));
                } else if (isBlockedme) {
                    showToast(OnDemanAudoVideoView.this, getString(R.string.block_follow_err));
                } else {
                    if (mIsAlreadyFollowing) {
                        if (usertype.equals(AppConstants.USER)
                                || usertype.equals(AppConstants.ONDEMAND)
                                || usertype.equals(AppConstants.USER_EVENT_VIDEOS)) {
                            String mFilter = "FollowRelation=" + mOtherProfileID + "_" + mMyProfileID;
                            RetrofitClient.getRetrofitInstance()
                                    .callUnFollowProfileWithoutBuffering(OnDemanAudoVideoView.this, mFilter, RetrofitClient.UN_FOLLOW_PROFILE_RESPONSE);
                        } else {
                            String mFilter = "FollowRelation=" + mMyProfileID + "_" + mOtherProfileID;
                            RetrofitClient.getRetrofitInstance()
                                    .callUnFollowPromoterWithoutBuffering(OnDemanAudoVideoView.this, mFilter, RetrofitClient.GET_PROMOTER_UN_FOLLOW_RESPONSE);
                        }
                    } else {
                        if (usertype.equals(AppConstants.USER)
                                || usertype.equals(AppConstants.ONDEMAND)
                                || usertype.equals(AppConstants.USER_EVENT_VIDEOS)) {
                            CommonAPI.getInstance()
                                    .callFollowProfile(OnDemanAudoVideoView.this, mMyProfileID, mOtherProfileID);
                        } else {
                            CommonAPI.getInstance()
                                    .callFollowPromoter(OnDemanAudoVideoView.this, mOtherProfileID, mMyProfileID);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
            /*}
        });*/

    }

    private boolean isAlreadyLikedPost() {
        try {
            if (pos >= 0 && mPostsList.size() > 0) {
                ArrayList<VideoLikesModel> mFeedLikeList = mPostsList.get(pos).getVideolikes_by_VideoID();
                if (mFeedLikeList.size() > 0) {
                    for (VideoLikesModel likesEntity : mFeedLikeList) {
                        if (likesEntity.getOwnerID() == mMyProfileResModel.getID() && likesEntity.getFeedID() == mPostsList.get(pos).getID()) {
                            mDeleteLikeID = likesEntity.getId();
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void callUnLikePost(Integer profileId) {
        RetrofitClient.getRetrofitInstance().callUnLikeForVideoswithoutBuffering(this, profileId, RetrofitClient.VIDEO_UNLIKE);
    }

    private void callLikePost(int postId, int profileId) {
        JsonObject mJsonObject = new JsonObject();
        JsonObject mItem = new JsonObject();
        mItem.addProperty("VideoID", postId);
        mItem.addProperty("ProfileID", profileId);
        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mItem);
        mJsonObject.add("resource", mJsonArray);
        RetrofitClient.getRetrofitInstance().postLikesForVideosWithoutBuffering(this, mJsonObject, RetrofitClient.VIDEO_LIKES);
    }

    private void manageNextVideoAndPlayer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Stuff that updates the UI
                if (pos < mPostsList.size() && pos > 0) {
                    pos = pos - 1;
                    if (pos < mPostsList.size()) {
                        if (isAlreadyLikedPost()) {
                            mLikeBtn.setBackground(ContextCompat.getDrawable(OnDemanAudoVideoView.this, R.drawable.liked_icon));
                            mLikeBtn.setTag("unlike");
                        } else {
                            mLikeBtn.setBackground(ContextCompat.getDrawable(OnDemanAudoVideoView.this, R.drawable.like_icon));
                            mLikeBtn.setTag("like");
                        }
                        isAlreadyFollowed();
                        playVideo();
                    } else {
                        mExoPlayer.seekToDefaultPosition();
                        mExoPlayer.setPlayWhenReady(false);
                        finish();
                    }
                } else {
                    if (mExoPlayer != null) {
                        mExoPlayer.seekToDefaultPosition();
                        mExoPlayer.setPlayWhenReady(false);
                        finish();
                    }
                }
            }
        });

    }

    private void managePrevVideoAndPlayer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                if (pos >= 0) {
                    if (mQuotient != 0 && mPostsList.size() / 10 == mQuotient) {
                        if (pos < mPostsList.size()) {
                            updateVideoList();
                        }
                    }
                    pos = pos + 1;
                    if (pos < mPostsList.size() && pos >= 0) {

                        if (isAlreadyLikedPost()) {
                            mLikeBtn.setBackground(ContextCompat.getDrawable(OnDemanAudoVideoView.this, R.drawable.liked_icon));
                            mLikeBtn.setTag("unlike");
                        } else {
                            mLikeBtn.setBackground(ContextCompat.getDrawable(OnDemanAudoVideoView.this, R.drawable.like_icon));
                            mLikeBtn.setTag("like");
                        }
                        isAlreadyFollowed();
                        playVideo();
                    } else {
                        mExoPlayer.seekToDefaultPosition();
                        mExoPlayer.setPlayWhenReady(false);
                        finish();
                    }
                } else {
                    if (mExoPlayer != null) {
                        mExoPlayer.seekToDefaultPosition();
                        mExoPlayer.setPlayWhenReady(false);
                        finish();
                    }
                }
            }
        });

    }

    private void playVideo() {
        try {
            if (mPostsList.get(pos).getCaption() != null || !mPostsList.get(pos).getCaption().equals("")) {
                if (mPostsList.get(pos).getCaption().contains(" ")) {
                    mCaption.setText(mPostsList.get(pos).getCaption());
                } else {
                    mCaption.setText(URLDecoder.decode(mPostsList.get(pos).getCaption(), "UTF-8"));
                }
            }
          /*  Uri uri = Uri.parse(UrlUtils.AWS_FILE_URL + mPostsList.get(pos).getVideoUrl() + "?api_key="
                    + getResources().getString(R.string.dream_factory_api_key)
                    + "&session_token="
                    + PreferenceUtils.getInstance(this).getStrData(PreferenceUtils
                    .SESSION_TOKEN)); */
            getViewCount(pos);
            mPostsList.get(pos).setViewCount(mPostsList.get(pos).getViewCount() + 1);
            replaceString = mPostsList.get(pos).getVideoUrl().replaceAll(" ", "%20");
            Uri uri = Uri.parse(UrlUtils.AWS_S3_BASE_URL + replaceString);
            MediaSource mediaSource = new ExtractorMediaSource(uri, mCacheDataSrcFactory, new DefaultExtractorsFactory(), null, null);
            mExoPlayer.setPlayWhenReady(true); //run file/link when ready to play.
            mExoPlayer.prepare(mediaSource, true, false);
            //Log.d("watching", pos + ", " + mPostsList.size());
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

    public void getViewCount(int pos) {
        String mFilter = "ID = " + String.valueOf(mPostsList.get(pos).getID());
        RetrofitClient.getRetrofitInstance().getViewCountOnDemand(this, mFilter, RetrofitClient.FEED_VIDEO_COUNT);
    }

    public void addViewCount(int count) {
        try {
            int videocount = count + 1;

            JsonArray mJsonArray = new JsonArray();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(PostsModel.POST_ID, mPostsList.get(pos).getID());
            jsonObject.addProperty(PostsModel.viewCount, videocount);
            mJsonArray.add(jsonObject);

            JsonObject mJsonObj = new JsonObject();
            mJsonObj.add("resource", mJsonArray);

            RetrofitClient.getRetrofitInstance().setViewCountOnDemand(this, mJsonObj, RetrofitClient.ADD_FEED_COUNT);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void callGetPromoterVideosWhilePlaying(final BaseActivity activity, String filter, int mOffset, boolean mIsLoadMore) {
        int mLimit = 50;
        String fields = "*";
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.GALLERY_VIDEO + "?fields=" + fields + "&filter=" + filter +
                "&limit=" + mLimit + "&offset=" + mOffset + "&include_count=true");
        if (!mIsLoadMore)
            DialogManager.showProgress(activity);
        String mOrderBy = "CreatedAt DESC";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetPromotersVideos(fields, filter, APIConstants.mPromoterGalleryRelated,
                mOrderBy, mLimit, mOffset, true)
                .enqueue(new Callback<PromoterVideoModel>() {
                    @Override
                    public void onResponse(Call<PromoterVideoModel> call, Response<PromoterVideoModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            PromoterVideoModel mModel = response.body();
                            assert mModel != null;
                            if (mModel.getResource().size() > 0) {
                                isLoadedData = false;
                                mTotalCount = mModel.getMeta().getCount();
                                if (mQuotient != 0 && mPostsList.size() / 10 == mQuotient) {
                                    mPostsList.addAll(mModel.getResource());
                                    mVideoView.setFocusable(true);
                                    mQuotient = mPostsList.size() / 10;
                                    // Log.d("responsesuccess", pos + ", " + mPostsList.size());
                                    // Log.d("count", mTotalCount + ", " + mPostsList.size());
                                }
                            }
                        } else {
                            if (mExoPlayer != null) {
                                mExoPlayer.seekToDefaultPosition();
                                mExoPlayer.setPlayWhenReady(false);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PromoterVideoModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        mVideoView.setFocusable(true);
                        if (mExoPlayer != null) {
                            mExoPlayer.seekToDefaultPosition();
                            mExoPlayer.setPlayWhenReady(false);
                            finish();
                        }
                        showToast(OnDemanAudoVideoView.this, getResources().getString(R.string.internet_err));
                    }
                });
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof VideoLikesModel) {
            ArrayList<VideoLikesModel> mLikeList = mPostsList.get(pos).getVideolikes_by_VideoID();
            //((BaseFragment) mNewsFeedFragment).retrofitOnResponse(responseObj, responseType);
            VideoLikesModel mFeedLikesList = (VideoLikesModel) responseObj;
            ArrayList<VideoLikesModel> mNewFeedLike = mFeedLikesList.getResource();
            switch (responseType) {
                case RetrofitClient.VIDEO_LIKES:
                    mLikeList.add(mNewFeedLike.get(0));
                    mPostsList.get(pos).setVideolikes_by_VideoID(mLikeList);
                    mLikeBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.liked_icon));
                    mLikeBtn.setTag("unlike");
                    mLikeCountTxt.setText("unlike");
                    setResult(RESULT_OK, new Intent().putExtra(AppConstants.VIDEO_LIST, mPostsList));
                    break;
                case RetrofitClient.VIDEO_UNLIKE:
                    for (int i = 0; i < mLikeList.size(); i++) {
                        if (mLikeList.get(i).getId() == mDeleteLikeID) {
                            mLikeList.remove(i);
                            break;
                        }
                    }
                    mLikeBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.like_icon));
                    mLikeBtn.setTag("like");
                    mLikeCountTxt.setText("like");
                    mPostsList.get(pos).setVideolikes_by_VideoID(mLikeList);
                    setResult(RESULT_OK, new Intent().putExtra(AppConstants.VIDEO_LIST, mPostsList));
                    break;
            }
        } else if (responseObj instanceof FollowProfileModel) {
            FollowProfileModel mFollowProfileModel = (FollowProfileModel) responseObj;
            switch (responseType) {
                case RetrofitClient.PROFILE_IS_ALREADY_FOLLOWED:
                    if (mFollowProfileModel.getResource().size() > 0) {
                        mIsAlreadyFollowing = true;
                    } else {
                        mIsAlreadyFollowing = false;
                    }
                    setFollowUnfollow();
                    //callGetProfile(mOtherProfileID, RetrofitClient.GET_OTHER_PROFILE_RESPONSE);
                    break;
                case RetrofitClient.FOLLOW_PROFILE_RESPONSE:
                    mIsAlreadyFollowing = true;
                    setFollowUnfollow();
                    break;
                case RetrofitClient.UN_FOLLOW_PROFILE_RESPONSE:
                    mIsAlreadyFollowing = false;
                    setFollowUnfollow();
                    break;
            }
        } else if (responseObj instanceof PromoterFollowerModel) {
            PromoterFollowerModel mPromoterFollowerModel = (PromoterFollowerModel) responseObj;
            switch (responseType) {
                case RetrofitClient.PROMOTER_IS_ALREADY_FOLLOWED:
                    if (mPromoterFollowerModel.getResource() != null && mPromoterFollowerModel.getResource().size() > 0) {
                        mIsAlreadyFollowing = true;
                    } else {
                        mIsAlreadyFollowing = false;
                    }
                    setFollowUnfollow();
                    callGetProfile(mOtherProfileID, RetrofitClient.GET_OTHER_PROFILE_RESPONSE);
                    break;
                case RetrofitClient.GET_PROMOTER_FOLLOW_RESPONSE:
                    mIsAlreadyFollowing = true;
                    setFollowUnfollow();
                    break;
                case RetrofitClient.GET_PROMOTER_UN_FOLLOW_RESPONSE:
                    mIsAlreadyFollowing = false;
                    setFollowUnfollow();
                    break;
            }
        } else if (responseObj instanceof ProfileModel) {
            ProfileModel mProfileModel = (ProfileModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_OTHER_PROFILE_RESPONSE:
                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        mOtherProfileResModel = mProfileModel.getResource().get(0);
                    }
                    break;
            }
        } else if (responseObj instanceof PromoterVideoModel) {
            PromoterVideoModel mPostsModel = (PromoterVideoModel) responseObj;
            switch (responseType) {
                case RetrofitClient.FEED_VIDEO_COUNT:
                    try {
                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            addViewCount(mPostsModel.getResource().get(0).getViewCount());
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    break;
                case RetrofitClient.ADD_FEED_COUNT:
                    try {
                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            mPostsList.get(pos).setViewCount(mPostsModel.getResource().get(0).getViewCount());
                            setResult(RESULT_OK, new Intent().putExtra(AppConstants.VIDEO_LIST, mPostsList));
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else if (responseObj instanceof EventsModel) {
            EventsModel mEventModel = (EventsModel) responseObj;
            if (mEventModel.getResource().size() > 0) {
                mEventResModel = mEventModel.getResource().get(0);
                imageList.addAll(mEventResModel.getEventadByEventID());
                if (imageList.size() > 0) {
                    showLogoOnVideo(R.drawable.motohub_logo);
                }
            }
        }
    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        releasePlayer();
    }

    @Override
    public void retrofitOnFailure(int code, String message) {
        super.retrofitOnFailure(code, message);
        releasePlayer();
    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
        releasePlayer();
    }

    @Override
    public void retrofitOnError(int code, String message, int responseType) {
        super.retrofitOnError(code, message, responseType);
        releasePlayer();
    }

    private void showLogoOnVideo(int drawable) {

        int arraySize = imageList.size();

        new Runnable() {
            int currentIndex = 0;
            int updateInterval = 7000; //=one second

            @Override
            public void run() {

                currentIndex += 1;
                if (currentIndex == arraySize) {
                    currentIndex = 0;
                }

                GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + imageList.get(currentIndex).getEventAd(), new LazyHeaders.Builder()
                        .addHeader("X-DreamFactory-Api-Key", getString(R.string.dream_factory_api_key))
                        .build());
                Glide.with(getApplicationContext())
                        .load(glideUrl)
                        .apply(new RequestOptions()
                                .override(120, 100)
                                .dontAnimate()
                                .error(drawable)
                        )
                        .into(imgLogo);

                imgLogo.postDelayed(this, updateInterval);
            }
        }.run();
    }

}