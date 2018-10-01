package online.motohub.activity;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.yovenny.videocompress.MediaController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import online.motohub.R;
import online.motohub.activity.club.ClubsListActivity;
import online.motohub.activity.news_and_media.NewsAndMediaListActivity;
import online.motohub.activity.ondemand.OnDemandActivity;
import online.motohub.activity.performance_shop.PerformanceShopListActivity;
import online.motohub.activity.promoter.PromotersListActivity;
import online.motohub.activity.track.TrackListActivity;
import online.motohub.adapter.EventsFindAdapter;
import online.motohub.adapter.FollowProfileAdapter;
import online.motohub.adapter.PostsAdapter;
import online.motohub.adapter.TaggedProfilesAdapter;
import online.motohub.application.MotoHub;
import online.motohub.database.DatabaseHandler;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.CommonInterface;
import online.motohub.interfaces.CommonReturnInterface;
import online.motohub.model.EventAddOnModel;
import online.motohub.model.EventAnswersModel;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsResModel;
import online.motohub.model.EventsWhoIsGoingModel;
import online.motohub.model.FeedCommentLikeModel;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedCommentReplyModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.ImageModel;
import online.motohub.model.LiveStreamResponse;
import online.motohub.model.PaymentModel;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PurchasedAddOnModel;
import online.motohub.model.PushTokenModel;
import online.motohub.model.RacingModel;
import online.motohub.model.ReplyLikeModel;
import online.motohub.model.SessionModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerResModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.CommonAPI;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.StringUtils;
import online.motohub.util.UploadFileService;
import online.motohub.util.Utility;

import static online.motohub.fcm.MyFireBaseMessagingService.ENTRY_JSON_OBJ;
import static online.motohub.fcm.MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY;

public class ViewProfileActivity extends BaseActivity implements
        PopupMenu.OnMenuItemClickListener,
        PostsAdapter.TotalRetrofitPostsResultCount,
        CommonInterface,
        FollowProfileAdapter.TotalRetrofitResultCount,
        TaggedProfilesAdapter.TaggedProfilesSizeInterface, AppBarLayout.OnOffsetChangedListener {

    public static final String EXTRA_RESULT_DATA = "activity_video_picker_uri";
    private static final int mDataLimit = 15;

    private static final String TAG = ViewProfileActivity.class.getName();

    private static String mSearchStr = "";
    private static ArrayList<ProfileResModel> mSearchProfilesList = new ArrayList<>();
    @BindView(R.id.postNextedScrollView)
    NestedScrollView postNestedScrollView;
    @BindView(R.id.news_feed_recycler_view)
    RecyclerView newsFeedRecyclerView;
    @BindView(R.id.live_events_recycler_view)
    RecyclerView mEventsRecyclerView;
    @BindView(R.id.appBarLayout)
    AppBarLayout mAppBarLay;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLay;
    @BindView(R.id.swipe_refresh_view)
    SwipeRefreshLayout mSwipeRefreshLay;
    @BindView(R.id.view_profile_co_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.profile_img)
    CircleImageView mMPImg;
    @BindView(R.id.name_of_moto_tv)
    TextView mNameOfMotoTv;
    @BindView(R.id.name_of_driver_tv)
    TextView mNameOfDriverTv;
    @BindView(R.id.imageframe)
    RelativeLayout mPostImgVideoLayout;
    @BindString(R.string.home)
    String mToolbarTitle;
    @BindView(R.id.write_post_et)
    EditText mWritePostEt;
    @BindView(R.id.close_layout)
    FrameLayout mPostImgVideoCloseBtnLayout;
    @BindView(R.id.post_picture_img_view)
    ImageView mPostPicImgView;
    @BindView(R.id.play)
    ImageView mPlayIconImgView;
    @BindView(R.id.tag_profiles_recycler_view)
    RecyclerView mTagProfilesRecyclerView;
    @BindView(R.id.win_car_btn)
    TextView mWinCarBtn;
    @BindView(R.id.horRvSeparator)
    View mUnfollowersView;
    @BindView(R.id.searchBoxSeparator)
    View mSearchView;
    @BindView(R.id.searchBox)
    RelativeLayout mSearchLay;
    @BindView(R.id.hor_recycler_view)
    RecyclerView mHorRecyclerView;
    @BindView(R.id.search_et)
    EditText mSearchEt;
    @BindString(R.string.post_success)
    String mPostSuccess;
    @BindString(R.string.tag_err)
    String mTagErr;
    @BindString(R.string.camera_permission_denied)
    String mNoCameraPer;
    @BindString(R.string.storage_permission_denied)
    String mNoStoragePer;
    private ArrayList<ProfileResModel> mFullMPList = new ArrayList<>();
    CommonReturnInterface mUpgradeProfileCallback = new CommonReturnInterface() {
        @Override
        public void onSuccess(int type) {
            if (type == 1) {
                ProfileResModel mMyProfileResModel = mFullMPList.get(PreferenceUtils.getInstance(ViewProfileActivity.this).
                        getIntData(PreferenceUtils.CURRENT_PROFILE_POS));
                String myProfileObj = new Gson().toJson(mMyProfileResModel);
                startActivity(new Intent(ViewProfileActivity.this, UpgradeProfileActivity.class).
                        putExtra(AppConstants.MY_PROFILE_OBJ, myProfileObj));
                dismissAppDialog();
            } else {
                showToast(ViewProfileActivity.this, getString(R.string.under_dev));
            }
        }
    };
    private ArrayList<String> mMPSpinnerList = new ArrayList<>();
    private List<EventsResModel> mEventsFindListData = new ArrayList<>();
    private boolean isProfilePurchased;
    private ArrayList<ProfileResModel> mFollowingListData = new ArrayList<>();
    private ArrayList<ProfileResModel> mTaggedProfilesList = new ArrayList<>();
    private TaggedProfilesAdapter mTaggedProfilesAdapter;
    private boolean isDelete = false;
    private int liveStreamID = 0;
    private String mLiveStreamName = "";
    private String mVideoPathUri, mPostImgUri;
    private LinearLayoutManager mHorLayoutManager;
    private int mHorRvOffset = 0, mHorRvTotalCount = 0, mHorFindFriendsRvOffset = 0,
            mHorFindFriendsRvTotalCount = 0;
    private boolean mIsHorRvLoading = true,
            mIsHorFindFriendsRvLoading = true;
    private ArrayList<ProfileResModel> mOtherProfilesList = new ArrayList<>();
    private FollowProfileAdapter mOtherProfileAdapter;
    private FollowProfileAdapter mSearchProfileAdapter;
    private boolean isExtraProfile = false;
    //New chages
    private LinearLayoutManager mNewsFeedLayoutManager;
    private ArrayList<PostsResModel> mNewsFeedList = new ArrayList<>();
    private PostsAdapter mNewsFeedAdapter;
    //private ArrayList<ProfileResModel> mFullMPList;
    private Activity mActivity;
    private boolean mRefresh = true;
    private int mPostPos;
    private int mPostsRvOffset = 0, mPostsRvTotalCount = 0;
    private boolean mIsPostsRvLoading = true;
    private int mCurrentPostPosition;
    private FeedShareModel mSharedFeed;
    private ProfileResModel mCurrentProfileObj;
    //Ended
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updatePost(intent);
        }
    };
    private EventsFindAdapter mEventsFindAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppBarLay.addOnOffsetChangedListener(this);
        registerReceiver(mBroadcastReceiver, new IntentFilter(UploadFileService.NOTIFY_POST_VIDEO_UPDATED));
        MotoHub.getApplicationInstance().newsFeedFragmentOnResume();
        getMyProfiles();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAppBarLay.removeOnOffsetChangedListener(this);
        unregisterReceiver(mBroadcastReceiver);
        MotoHub.getApplicationInstance().newsFeedFragmentOnPause();
    }

    private void getUpcomingEvents() {
        int status = 2;
        String mDateFilter = "(( Date <= " + getCurrentDate() + " ) AND ( Finish >= " + getCurrentDate() + " )) AND ( EventStatus=" + status + ")";
        RetrofitClient.getRetrofitInstance().callGetEvents(this, mDateFilter, RetrofitClient.GET_EVENTS_RESPONSE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {

        assert getIntent().getExtras() != null;
        if (getIntent().getExtras().getBoolean(IS_FROM_NOTIFICATION_TRAY)) {
            try {
                JSONObject mJsonObject = new JSONObject(getIntent().getExtras().getString(MyFireBaseMessagingService.ENTRY_JSON_OBJ));
                navigateNotification(mJsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mSearchStr = "";
        setupUI(mCoordinatorLayout);
        AppConstants.LIVE_STREAM_CALL_BACK = this;

        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_settings_img_btn);
        showToolbarBtn(mToolbar, R.id.toolbar_messages_img_btn);
        showToolbarBtn(mToolbar, R.id.toolbar_notification_img_btn);

        FlexboxLayoutManager mFlexBoxLayoutManager = new FlexboxLayoutManager(this);
        mFlexBoxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        mFlexBoxLayoutManager.setFlexDirection(FlexDirection.ROW);
        mFlexBoxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        mTagProfilesRecyclerView.setLayoutManager(mFlexBoxLayoutManager);

        mTaggedProfilesAdapter = new TaggedProfilesAdapter(mTaggedProfilesList, this);
        mTagProfilesRecyclerView.setAdapter(mTaggedProfilesAdapter);

        mHorLayoutManager = new LinearLayoutManager(this);
        mHorLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHorRecyclerView.setLayoutManager(mHorLayoutManager);

        mHorRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int mVisibleItemCount = mHorLayoutManager.getChildCount();
                int mTotalItemCount = mHorLayoutManager.getItemCount();
                int mFirstVisibleItemPosition = mHorLayoutManager.findFirstVisibleItemPosition();
                if (mSearchStr.isEmpty()) {
                    if (!mIsHorRvLoading && !(mHorRvOffset >= mHorRvTotalCount)) {
                        if ((mVisibleItemCount + mFirstVisibleItemPosition) >= mTotalItemCount
                                && mFirstVisibleItemPosition >= 0) {
                            mIsHorRvLoading = true;
                            getOtherProfileList();
                        }
                    }
                } else if (!mIsHorFindFriendsRvLoading && !(mHorFindFriendsRvOffset >= mHorFindFriendsRvTotalCount)) {
                    if ((mVisibleItemCount + mFirstVisibleItemPosition) >= mTotalItemCount
                            && mFirstVisibleItemPosition >= 0) {
                        mIsHorFindFriendsRvLoading = true;
                        getSearchProfileList(
                                RetrofitClient.PROFILE_FIND_FRIENDS_LOAD_MORE_RESPONSE);
                    }
                }
            }
        });


        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String mSearchStrTemp = editable.toString();
                if (mSearchStrTemp.length() > 0 && mSearchStrTemp.charAt(0) == '@') {
                    if (mSearchStrTemp.length() == 1) {
                        mSearchStrTemp = "";
                    } else {
                        mSearchStrTemp = mSearchStrTemp.substring(1, mSearchStrTemp.length() - 1);
                    }
                }
                findFriendsOrVehicles(mSearchStrTemp);
            }

        });
        mSwipeRefreshLay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLay.setRefreshing(false);
                getMyProfiles();
            }
        });

        //new
        mNewsFeedLayoutManager = new LinearLayoutManager(getApplicationContext());
        mNewsFeedLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        newsFeedRecyclerView.setLayoutManager(mNewsFeedLayoutManager);

        postNestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) postNestedScrollView.getChildAt(postNestedScrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (postNestedScrollView.getHeight() + postNestedScrollView
                        .getScrollY()));

                if (diff == 0) {
                    // your pagination code

                    int mVisibleItemCount = mNewsFeedLayoutManager.getChildCount();
                    int mTotalItemCount = mNewsFeedLayoutManager.getItemCount();
                    int mFirstVisibleItemPosition = mNewsFeedLayoutManager.findFirstVisibleItemPosition();
                    if (!mIsPostsRvLoading && !(mPostsRvOffset >= mPostsRvTotalCount)) {
                        if ((mVisibleItemCount + mFirstVisibleItemPosition) >= mTotalItemCount
                                && mFirstVisibleItemPosition >= 0) {
                            mIsPostsRvLoading = true;
                            getNewsFeedPosts();
                        }
                    }
                }
            }
        });

       /* newsFeedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int mVisibleItemCount = mNewsFeedLayoutManager.getChildCount();
                int mTotalItemCount = mNewsFeedLayoutManager.getItemCount();
                int mFirstVisibleItemPosition = mNewsFeedLayoutManager.findFirstVisibleItemPosition();

                if (!mIsPostsRvLoading && !(mPostsRvOffset >= mPostsRvTotalCount)) {
                    if ((mVisibleItemCount + mFirstVisibleItemPosition) >= mTotalItemCount
                            && mFirstVisibleItemPosition >= 0) {
                        mIsPostsRvLoading = true;
                        getNewsFeedPosts();
                    }
                }
            }
        });*/
        getMyProfiles();
    }

    private void setOtherProfilesAdapter() {
        mSearchStr = "";
        mSearchProfileAdapter = null;
        if (mOtherProfileAdapter == null) {
            mOtherProfileAdapter = new FollowProfileAdapter(mOtherProfilesList, getCurrentProfile(), this);
            mHorRecyclerView.setAdapter(mOtherProfileAdapter);
        } else {
            mOtherProfileAdapter.notifyDataSetChanged();
        }
    }

    private void setEventsAdapter() {

        if (mEventsFindAdapter == null) {
            mEventsFindAdapter = new EventsFindAdapter(this, mEventsFindListData, mCurrentProfileObj, true);
            mEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mEventsRecyclerView.setAdapter(mEventsFindAdapter);
        } else {
            mEventsFindAdapter.notifyDataSetChanged();
        }
    }


    private void setSearchProfilesAdapter() {
        mOtherProfileAdapter = null;
        if (mSearchProfileAdapter == null) {
            mSearchProfileAdapter = new FollowProfileAdapter(mSearchProfilesList, getCurrentProfile(), this);
            mHorRecyclerView.setAdapter(mSearchProfileAdapter);
        } else {
            mSearchProfileAdapter.notifyDataSetChanged();
        }
    }

    private void getMyProfiles() {
        mMPSpinnerList.clear();
        Collections.addAll(mMPSpinnerList, getResources().getStringArray(R.array.empty_array));
        int mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        String mFilter = "UserID=" + mUserID;
        RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);

    }

    private void getOtherProfileList() {
        int mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        ProfileResModel mMyProfileResModel = mFullMPList.get(PreferenceUtils.getInstance(this)
                .getIntData(PreferenceUtils.CURRENT_PROFILE_POS));
        String mBlockedUsersID = Utility.getInstance().getMyBlockedUsersID(mMyProfileResModel.getBlockedUserProfilesByProfileID(),
                mMyProfileResModel.getBlockeduserprofiles_by_BlockedProfileID());
        String mFilter;
        if (!mBlockedUsersID.trim().isEmpty())
            mFilter = "(UserID != " + mUserID + ") AND (ID NOT IN (" + mBlockedUsersID + "))";
        else
            mFilter = "(UserID != " + mUserID + ")";

        RetrofitClient.getRetrofitInstance().callAllOtherProfiles(this, mFilter, RetrofitClient.GET_OTHER_PROFILES_RESPONSE, mDataLimit, mHorRvOffset, mIsHorRvLoading);

    }

    private void findFriendsOrVehicles(final String searchStr) {
        if (mFullMPList.size() > 0) {
            mSearchStr = searchStr;
            mSearchProfilesList.clear();
            mHorFindFriendsRvOffset = 0;
            mIsHorFindFriendsRvLoading = true;
            //mHorFindFriendsRvTotalCount = -1;
            setSearchProfilesAdapter();
            getSearchProfileList(RetrofitClient.PROFILE_FIND_FRIENDS_RESPONSE);
        }
    }

    private void getSearchProfileList(int requestCode) {
        if (!mSearchStr.isEmpty()) {
            String mFilter;
            int mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
            ProfileResModel mMyProfileResModel = mFullMPList.get(PreferenceUtils.getInstance(this)
                    .getIntData(PreferenceUtils.CURRENT_PROFILE_POS));
            String mBlockedUsersID = Utility.getInstance().getMyBlockedUsersID(mMyProfileResModel.getBlockedUserProfilesByProfileID(),
                    mMyProfileResModel.getBlockeduserprofiles_by_BlockedProfileID());
            if (!mBlockedUsersID.trim().isEmpty()) {
                mFilter = "(UserID != " + mUserID + ") AND (id NOT IN ("
                        + mBlockedUsersID + ")) AND ((Driver LIKE '%" + mSearchStr
                        + "%') OR (SpectatorName LIKE '%" + mSearchStr + "%') OR (MotoName LIKE '%" +
                        mSearchStr + "%') OR (Make LIKE '%" +
                        mSearchStr + "%') OR (Model LIKE '%" +
                        mSearchStr + "%') OR (PhoneNumber LIKE '%" +
                        mSearchStr + "%'))";
            } else {
                mFilter = "(UserID != " + mUserID + ") AND ((Driver LIKE '%" + mSearchStr
                        + "%') OR (SpectatorName LIKE '%" + mSearchStr + "%') OR (MotoName LIKE '%" +
                        mSearchStr + "%') OR (Make LIKE '%" +
                        mSearchStr + "%') OR (Model LIKE '%" +
                        mSearchStr + "%') OR (PhoneNumber LIKE '%" +
                        mSearchStr + "%'))";
            }
            setSearchProfilesAdapter();

            RetrofitClient.getRetrofitInstance().callGetSearchProfiles(this, mFilter, requestCode, mDataLimit, mHorFindFriendsRvOffset);

        } else {
            mHorRvOffset = 0;
            mIsHorRvLoading = true;
            mOtherProfilesList.clear();
            setOtherProfilesAdapter();
            getOtherProfileList();
        }
    }

    private void getTagProfileList() {
        ProfileResModel mMyProfileResModel = mFullMPList.get(PreferenceUtils.getInstance(this)
                .getIntData(PreferenceUtils.CURRENT_PROFILE_POS));
        String mBlockedUsersID = Utility.getInstance().getMyBlockedUsersID(mMyProfileResModel.getBlockedUserProfilesByProfileID(),
                mMyProfileResModel.getBlockeduserprofiles_by_BlockedProfileID());
        String mFollowingsID = Utility.getInstance().getMyFollowersFollowingsID(mMyProfileResModel.getFollowprofile_by_ProfileID(), false);
        if (mFollowingsID.isEmpty()) {
            showSnackBar(mCoordinatorLayout, mTagErr);
            return;
        }
        String mFilter;
        if (mBlockedUsersID.trim().isEmpty()) {
            mFilter = "id IN (" + mFollowingsID + ")";
        } else {
            mFilter = "id IN (" + mFollowingsID + ") AND ( id NOT IN (" + mBlockedUsersID + "))";
        }
        RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_FOLLOWING_PROFILE_RESPONSE);

    }

    @OnClick({R.id.refresh_btn, R.id.my_moto_file_box, R.id.events_box, R.id.toolbar_back_img_btn, R.id.toolbar_settings_img_btn, R.id.toolbar_messages_img_btn, R.id.toolbar_notification_img_btn, R.id.profile_img, R.id.name_of_moto_tv,
            R.id.name_of_driver_tv, R.id.add_post_video, R.id.imageframe, R.id.news_and_media_box, R.id.bottom_live_box, R.id.remove_post_img_btn,
            R.id.post_btn, R.id.add_post_img, R.id.tag_profile_img, R.id.clubs_box, R.id.promoter_box, R.id.tracks_container,
            R.id.win_car_btn, R.id.performance_box, R.id.btn_write_post})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refresh_btn:
                getMyProfiles();
                break;
            case R.id.news_and_media_box:
                if (mFullMPList.size() > 0) {
                    startActivityForResult(new Intent(this, NewsAndMediaListActivity.class)
                            .putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mFullMPList.get(PreferenceUtils.getInstance(this)
                                    .getIntData(PreferenceUtils.CURRENT_PROFILE_POS))), AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.clubs_box:
                if (mFullMPList.size() > 0) {
                    startActivityForResult(new Intent(this, ClubsListActivity.class)
                            .putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mFullMPList.get(PreferenceUtils.getInstance(this)
                                    .getIntData(PreferenceUtils.CURRENT_PROFILE_POS))), AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.promoter_box:
                if (mFullMPList.size() > 0) {
                    startActivityForResult(new Intent(this, PromotersListActivity.class)
                            .putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mFullMPList.get(PreferenceUtils.getInstance(this)
                                    .getIntData(PreferenceUtils.CURRENT_PROFILE_POS))), AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.toolbar_settings_img_btn:
                showPopupMenu(v);
                break;
            case R.id.toolbar_messages_img_btn:
                if (mFullMPList.size() > 0) {
                    startActivity(new Intent(ViewProfileActivity.this, ChatHomeActivity.class)
                            .putExtra(ProfileModel.MY_PROFILE_RES_MODEL, getCurrentProfile()));

                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.toolbar_notification_img_btn:
                if (mFullMPList.size() > 0) {
                    startActivityForResult(new Intent(this, NotificationActivity.class).putExtra(ProfileModel.MY_PROFILE_RES_MODEL,
                            getCurrentProfile()),
                            AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.my_moto_file_box:
                if (mFullMPList.size() > 0) {
                    moveMyProfileScreenWithResult(this, 0, AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.bottom_live_box:
                if (mFullMPList.size() > 0) {
//                        startActivity(new Intent(this, ComingSoonActivity.class));
                    startMultiStream();
                    //TODO have to change it in future
                    //TODO showAppDialog(AppDialogFragment.BOTTOM_LIVE_STREAM_DIALOG, null);

                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.events_box:
                if (mFullMPList.size() > 0) {
                    startActivity(new Intent(this, EventsHomeActivity.class)
                            .putExtra(AppConstants.PROFILE_ID, getCurrentProfile().getID()));

                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.performance_box:
                if (mFullMPList.size() > 0) {
                    startActivityForResult(new Intent(this, PerformanceShopListActivity.class)
                            .putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mFullMPList.get(PreferenceUtils.getInstance(this)
                                    .getIntData(PreferenceUtils.CURRENT_PROFILE_POS))), AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.profile_img:
            case R.id.name_of_moto_tv:
            case R.id.name_of_driver_tv:
                if (mFullMPList.size() > 0) {
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mFullMPList.get(PreferenceUtils
                            .getInstance
                                    (this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));
                    startActivityForResult(new Intent(this, UpdateProfileActivity.class).putExtras(mBundle),
                            AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.tracks_container:
                if (mFullMPList.size() > 0) {
                    Intent trackIntent = new Intent(ViewProfileActivity.this, TrackListActivity.class);
                    trackIntent.putExtra(TrackListActivity.EXTRA_PROFILE_DATA, getCurrentProfile());
                    startActivityForResult(trackIntent, AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.post_btn:
                try {
                    if (mFullMPList.size() > 0) {
                        if (mVideoPathUri != null) {
                            File videoFile = copiedVideoFile(Uri.fromFile(new File(mVideoPathUri)),
                                    GALLERY_VIDEO_NAME_TYPE);
                            String mPath = String.valueOf(videoFile);
                            new VideoCompressor().execute(mPath, getCompressedVideoPath());

                        } else if (mPostImgUri != null) {
                            uploadPicture(mPostImgUri);
                        } else {
                            profilePostContent("");
                        }

                    } else {
                        showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.add_post_img:
                showAppDialog(AppDialogFragment.BOTTOM_ADD_IMG_DIALOG, null);
                break;
            case R.id.add_post_video:
                showAppDialog(AppDialogFragment.BOTTOM_ADD_VIDEO_DIALOG, null);
                break;
            case R.id.tag_profile_img:
                if (mFullMPList.size() > 0) {
                    getTagProfileList();

                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.remove_post_img_btn:
                mPostImgUri = null;
                mVideoPathUri = null;
                mPostPicImgView.setImageDrawable(null);
                mPostImgVideoCloseBtnLayout.setVisibility(View.GONE);
                mPostImgVideoLayout.setVisibility(View.GONE);
                mPostPicImgView.setVisibility(View.GONE);
                break;
            case R.id.imageframe:
                if (mVideoPathUri != null) {
                    moveLoadVideoPreviewScreen(this, mVideoPathUri);
                }
                break;
            case R.id.win_car_btn:
                ProfileResModel mMyProfileResModel = mFullMPList.get(PreferenceUtils.getInstance(this).
                        getIntData(PreferenceUtils.CURRENT_PROFILE_POS));
                if (Utility.getInstance().isSpectator(mMyProfileResModel)) {
                    DialogManager.showUpgradeRacerOptionPopup(this, mUpgradeProfileCallback);
                } else {
                    Intent intent = new Intent(this, TermsAndConActivity.class);
                    intent.putExtra(AppConstants.IS_FROM_TERMS, true);
                    startActivity(intent);
                }
                break;
            case R.id.btn_write_post:
                Gson mGson = new Gson();
                String mProfile = mGson.toJson(mFullMPList);
                startActivityForResult(new Intent(ViewProfileActivity.this, WritePostActivity.class).putExtra(AppConstants.MY_PROFILE_OBJ, mProfile).putExtra(AppConstants.IS_NEWSFEED_POST, false), AppConstants.WRITE_POST_REQUEST);
                break;
//            case R.id.view_stream_btn:
//                Intent mGoWatchActivity = new Intent(this, ViewLiveVideoViewScreen2.class);
//                mGoWatchActivity.putExtra(AppConstants.PROFILE_ID, mCurrentProfileID);
//                startActivity(mGoWatchActivity);
//                break;
//            case R.id.view_request_btn:
//                int reqProfileID = getCurrentProfile().getID();
//                startActivity(new Intent(this, ViewRequestUsersActivity.class).putExtra(AppConstants.PROFILE_ID, reqProfileID));
//                break;
        }
    }

    private void startUploadVideoService() {
        try {
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mVideoPathUri,
                    MediaStore.Images.Thumbnails.MINI_KIND);
            File imageFile = compressedImgFromBitmap(thumb);
            String postText;
            if (mWritePostEt.getText().toString().isEmpty() || mWritePostEt.getText().toString().equals("")) {
                postText = "";
            } else {
                postText = mWritePostEt.getText().toString();
            }
            DatabaseHandler databaseHandler = new DatabaseHandler(this);
            int count = databaseHandler.getPendingCount();
            String destFilePath = Environment.getExternalStorageDirectory().getPath()
                    + getString(R.string.util_app_folder_root_path);
            Intent service_intent = new Intent(this, UploadFileService.class);
            service_intent.putExtra("videofile", mCompressedVideoPath);
            service_intent.putExtra("imagefile", String.valueOf(imageFile));
            service_intent.putExtra("posttext", postText);
            service_intent.putExtra("profileid", getCurrentProfile().getID());
            service_intent.putExtra("dest_file", destFilePath);
            service_intent.putExtra("running", count + 1);
            service_intent.putExtra("flag", 1);
            service_intent.setAction("UploadService");
            if (mTaggedProfilesList.size() == 0) {
                service_intent.putExtra(PostsModel.TAGGED_PROFILE_ID, "");
            } else {
                StringBuilder mTaggedProfileID = new StringBuilder();
                for (ProfileResModel mProfileResModel : mTaggedProfilesList) {
                    mTaggedProfileID.append(mProfileResModel.getID()).append(",");
                }
                mTaggedProfileID.deleteCharAt(mTaggedProfileID.length() - 1);
                service_intent.putExtra(PostsModel.TAGGED_PROFILE_ID, mTaggedProfileID.toString());
            }
            startService(service_intent);
            mVideoPathUri = null;
            mPostImgUri = null;
            mCompressedVideoPath = "";
            mWritePostEt.setText("");
            mPostPicImgView.setImageDrawable(null);
            mPostPicImgView.setVisibility(View.GONE);
            mPostImgVideoLayout.setVisibility(View.GONE);
            mPostImgVideoCloseBtnLayout.setVisibility(View.GONE);
            mTaggedProfilesList.clear();
            mTaggedProfilesAdapter.notifyDataSetChanged();
            if (mTaggedProfilesList.size() > 0) {
                mTagProfilesRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mTagProfilesRecyclerView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String dialogType, StringBuilder profileTypesStr,
                                            ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);
        switch (dialogType) {
            case AppDialogFragment.ALERT_OTHER_PROFILE_DIALOG:
                PreferenceUtils.getInstance(this).saveIntData(PreferenceUtils.CURRENT_PROFILE_POS, position);
                isProfilePurchased = true;
                changeAndSetProfile(position);
                if (mSearchStr.isEmpty()) {
                    mOtherProfileAdapter = null;
                    setOtherProfilesAdapter();
                } else {
                    mSearchProfileAdapter = null;
                    setSearchProfilesAdapter();
                }
                //((BaseFragment) mNewsFeedFragment).setRefresh(true);
                //((BaseFragment) mNewsFeedFragment).callFeedPost(getCurrentProfile());
                refreshFeeds(getCurrentProfile());
                break;

            case AppDialogFragment.BOTTOM_DELETE_DIALOG:
                mPostPos = position;

                RetrofitClient.getRetrofitInstance().callDeleteProfilePosts(this, mNewsFeedList.get(position).getID(), RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE);
                RetrofitClient.getRetrofitInstance().callDeleteSharedPost(this, mNewsFeedList.get(position).getID(), RetrofitClient.DELETE_SHARED_POST_RESPONSE);

                break;
            case AppDialogFragment.BOTTOM_REPORT_ACTION_DIALOG:
                startActivityForResult(
                        new Intent(ViewProfileActivity.this, ReportActivity.class).putExtra(PostsModel.POST_ID, mNewsFeedList.get(position).getID()).putExtra(ProfileModel.PROFILE_ID, mFullMPList.get(getProfileCurrentPos()).getID()).putExtra(ProfileModel.USER_ID, mFullMPList.get(getProfileCurrentPos()).getUserID()),
                        AppConstants.REPORT_POST_SUCCESS);
                break;
            case AppDialogFragment.BOTTOM_EDIT_DIALOG:
                //((BaseFragment) mNewsFeedFragment).alertDialogPositiveBtnClick(dialogType, position);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(PostsModel.POST_MODEL, mNewsFeedList.get(position));
                mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mCurrentProfileObj);
                startActivityForResult(
                        new Intent(ViewProfileActivity.this, PostEditActivity.class).putExtras(mBundle),
                        AppConstants.POST_UPDATE_SUCCESS);
                break;

            case AppDialogFragment.ALERT_EXIT_DIALOG:
                finishAffinity();
                break;

            case AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG:

                getMyProfiles();

                break;

            case AppDialogFragment.BOTTOM_SHARE_DIALOG:

                //((BaseFragment) mNewsFeedFragment).alertDialogPositiveBtnClick(dialogType, position);
                mCurrentPostPosition = position;
                CommonAPI.getInstance().callPostShare(ViewProfileActivity.this, mNewsFeedList.get(mCurrentPostPosition), mCurrentProfileObj.getID());
                break;
            case AppDialogFragment.TAG_FOLLOWING_PROFILE_DIALOG:

                mTaggedProfilesList.clear();
                mTaggedProfilesAdapter.notifyDataSetChanged();

                for (ProfileResModel mFollowerModel : mFollowingListData) {
                    if (mFollowerModel.getIsProfileTagged()) {
                        mTaggedProfilesList.add(mFollowerModel);
                        mTaggedProfilesAdapter.notifyDataSetChanged();
                    }
                }

                if (mTaggedProfilesList.size() > 0) {
                    mTagProfilesRecyclerView.setVisibility(View.VISIBLE);
                    AppDialogFragment.getInstance().dismiss();
                } else {
                    mTagProfilesRecyclerView.setVisibility(View.GONE);
                    showToast(getApplicationContext(), "Please select at least one profile");
                }
                break;
            case AppDialogFragment.BOTTOM_LIVE_STREAM_DIALOG:
                showAppDialog(AppDialogFragment.BOTTOM_LIVE_STREAM_OPTION_DIALOG, null);
                break;
            case AppDialogFragment.BOTTOM_LIVE_STREAM_OPTION_DIALOG:
                startSingleStream();
                break;
            case AppDialogFragment.BOTTOM_LIVE_STREAM_OPTION_MULTI:
                startMultiStream();
                break;
            case AppDialogFragment.LOG_OUT_DIALOG:
                logout();
                break;
            case AppDialogFragment.EVENT_CATEGORY_DIALOG:
                mEventsFindAdapter.alertDialogPositiveBtnClick(dialogType);
                dismissAppDialog();
        }

    }

    private void logout() {
        int mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        String mFilter = "UserID=" + mUserID;
        RetrofitClient.getRetrofitInstance().callDeletePushToken(this, mFilter, RetrofitClient.FACEBOOK_LOGOUT);

    }

    private void startSingleStream() {
        String mStreamName = StringUtils.genRandomStreamName(this);
        JsonObject mJsonObject = new JsonObject();
        JsonObject mItem = new JsonObject();
        mItem.addProperty(APIConstants.StreamName, mStreamName);
        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mItem);
        mJsonObject.add("resource", mJsonArray);
        RetrofitClient.getRetrofitInstance().callPostLiveStream(this, mJsonObject);

    }

    private void startMultiStream() {
        String followersID = Utility.getInstance().getMyFollowersFollowingsID(getCurrentProfile().getFollowprofile_by_ProfileID(),
                false);
        String promoterFollowersID = getPromoterIDs(getCurrentProfile().getPromoterFollowerByProfileID());
        int reqProfileID = getCurrentProfile().getID();
        Bundle mBundle = new Bundle();
        mBundle.putInt(AppConstants.PROFILE_ID, reqProfileID);
        mBundle.putString(AppConstants.MY_FOLLOWINGS, followersID);
        mBundle.putString(AppConstants.MY_PROMOTERS_FOLLOWINGS, promoterFollowersID);
//        mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, getCurrentProfile());
//        startActivityForResult(new Intent(this, LiveStreamActivity.class)
//                .putExtras(mBundle), AppConstants.FOLLOWERS_FOLLOWING_RESULT);

        /*startActivityForResult(new Intent(this, PromoterVideoGalleryActivity.class)
                .putExtras(mBundle), AppConstants.FOLLOWERS_FOLLOWING_RESULT);*/


        //startActivityForResult(new Intent(this, ViewOndemandActivity.class).putExtras(mBundle),AppConstants.FOLLOWERS_FOLLOWING_RESULT);
        MotoHub.getApplicationInstance().setProfileId(reqProfileID);
        Intent intent = new Intent(this, OnDemandActivity.class);
        startActivity(intent);
    }

    private String getPromoterIDs(List<PromoterFollowerResModel> promoterFollowerByProfileID) {
        String mStr = "";
        StringBuilder mStringBuilder = new StringBuilder();
        for (PromoterFollowerResModel model : promoterFollowerByProfileID) {
            mStringBuilder.append(model.getPromoterUserID()).append(",");
        }
        mStr = mStringBuilder.toString();
        if (promoterFollowerByProfileID.size() > 0)
            mStr = mStr.substring(0, mStr.length() - 1);
        return mStr;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.create_new_profile:
                if (mFullMPList.size() > 0) {
                    Intent intent = new Intent(this, CreateProfileActivity.class);
                    intent.putExtra(CREATE_PROF_AFTER_REG, true);
                    intent.putExtra(AppConstants.TAG, TAG);
                    startActivityForResult(intent, AppConstants.CREATE_PROFILE_RES);
                }
                return true;
            case R.id.other_profiles:
                if (mFullMPList.size() > 0) {
                    showAppDialog(AppDialogFragment.ALERT_OTHER_PROFILE_DIALOG, mMPSpinnerList);
                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                return true;
            case R.id.notification_settings:
                startActivity(new Intent(ViewProfileActivity.this, NotificationSettingsActivity.class));
                return true;
            case R.id.blocked_users:
                if (mFullMPList.size() > 0) {
                    if (getCurrentProfile().getBlockedUserProfilesByProfileID().size() > 0) {
                        startActivityForResult(new Intent(this, BlockedUsersActivity.class).putExtra(ProfileModel.MY_PROFILE_RES_MODEL,
                                getCurrentProfile()),
                                AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                    } else {
                        showToast(ViewProfileActivity.this, getString(R.string.no_blocked_users_found_err));
                    }
                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                return true;
            case R.id.logout:
                showAppDialog(AppDialogFragment.LOG_OUT_DIALOG, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeAndSetProfile(int position) {
        if (mFullMPList.size() > position) {
            ProfileResModel mProfileEntity = mFullMPList.get(position);
            if (!mProfileEntity.getProfilePicture().isEmpty()) {
                setImageWithGlide(mMPImg, mProfileEntity.getProfilePicture(), R.drawable.default_profile_icon);
            } else {
                mMPImg.setImageResource(R.drawable.default_profile_icon);
            }

            if (Utility.getInstance().isSpectator(mProfileEntity)) {
                mNameOfMotoTv.setText(mProfileEntity.getSpectatorName());
                mNameOfDriverTv.setVisibility(View.GONE);
            } else {
                mNameOfMotoTv.setText(mProfileEntity.getMotoName());
                mNameOfDriverTv.setText(mProfileEntity.getDriver());
                mNameOfDriverTv.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case EventsFindAdapter.EVENT_PAYMENT_REQ_CODE:
                    mEventsFindAdapter.onActivityResult(requestCode, resultCode, data);
                    break;
                case EventsFindAdapter.EVENT_QUESTIONS_REQ_CODE:
                    mEventsFindAdapter.onActivityResult(requestCode, resultCode, data);
                    break;
                case EventsFindAdapter.EVENT_LIVE_PAYMENT_REQ_CODE:
                    mEventsFindAdapter.onActivityResult(requestCode, resultCode, data);
                    break;
                case CAMERA_CAPTURE_REQ:
                    Uri mCameraPicUri = getImgResultUri(data);
                    mVideoPathUri = null;
                    if (mCameraPicUri != null) {
                        try {
                            File mPostImgFile = compressedImgFile(mCameraPicUri,
                                    POST_IMAGE_NAME_TYPE, "");
                            mPostImgUri = Uri.fromFile(mPostImgFile).toString();
                            setImageWithGlide(mPostPicImgView, Uri.parse(mPostImgUri));

                            mPostImgVideoCloseBtnLayout.setVisibility(View.VISIBLE);
                            mPlayIconImgView.setVisibility(View.GONE);
                            mPostImgVideoLayout.setVisibility(View.VISIBLE);
                            mPostPicImgView.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showSnackBar(mCoordinatorLayout, e.getMessage());
                        }
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
                case GALLERY_PIC_REQ:
                    assert data.getExtras() != null;
                    mVideoPathUri = null;
                    Uri mSelectedImgFileUri = (Uri) data.getExtras().get(PickerImageActivity.EXTRA_RESULT_DATA);
                    if (mSelectedImgFileUri != null) {
                        try {
                            String mProfileID = String.valueOf(getCurrentProfile().getID());
                            File mPostImgFile = compressedImgFile(mSelectedImgFileUri,
                                    POST_IMAGE_NAME_TYPE, mProfileID);
                            mPostImgUri = Uri.fromFile(mPostImgFile).toString();
                            setImageWithGlide(mPostPicImgView, Uri.parse(mPostImgUri));
                            mPostImgVideoCloseBtnLayout.setVisibility(View.VISIBLE);
                            mPlayIconImgView.setVisibility(View.GONE);
                            mPostPicImgView.setVisibility(View.VISIBLE);
                            mPostImgVideoLayout.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showSnackBar(mCoordinatorLayout, e.getMessage());
                        }
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
                case ACTION_TAKE_VIDEO:
                    Uri videoUri = data.getData();
                    mPostImgUri = null;
                    if (videoUri != null) {
                        mVideoPathUri = getRealPathFromURI(videoUri);
                        setVideoPost();
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
                case GALLERY_VIDEO_REQ:
                    mVideoPathUri = data.getStringExtra(EXTRA_RESULT_DATA);
                    mPostImgUri = null;
                    if (mVideoPathUri != null) {
                        setVideoPost();
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
                case AppConstants.POST_UPDATE_SUCCESS:
                    //mNewsFeedFragment.onActivityResult(requestCode, resultCode, data);
                    if (data == null) {
                        showToast(getApplicationContext(), getString(R.string.post_video_uploading_update));
                    } else {
                        assert data.getExtras() != null;
                        PostsResModel mPostsResModel = (PostsResModel) data.getExtras().get(PostsModel.POST_MODEL);
                        assert mPostsResModel != null;
                        mNewsFeedList.get(mPostPos).setPostPicture(mPostsResModel.getPostPicture());
                        mNewsFeedList.get(mPostPos).setPostText(mPostsResModel.getPostText());
                        mNewsFeedList.get(mPostPos).setTaggedProfileID(mPostsResModel.getTaggedProfileID());
                        mNewsFeedList.get(mPostPos).setPostVideoThumbnailURL(mPostsResModel.getPostVideoThumbnailURL());
                        mNewsFeedList.get(mPostPos).setPostVideoURL(mPostsResModel.getPostVideoURL());
                        setPostAdapter();
                    }
                    break;
                case AppConstants.CREATE_PROFILE_RES:
                    isExtraProfile = true;
                    getMyProfiles();
                    break;
                case AppConstants.FOLLOWERS_FOLLOWING_RESULT:
                    mHorRvOffset = 0;
                    getMyProfiles();
                    break;
                default:
                    mHorRvOffset = 0;
                    getMyProfiles();
                    break;
                case AppConstants.POST_COMMENT_REQUEST:
                    //mNewsFeedFragment.onActivityResult(requestCode, resultCode, data);
                    assert data.getExtras() != null;
                    ArrayList<FeedCommentModel> mFeedCommentModel = (ArrayList<FeedCommentModel>) data.getExtras().getSerializable(PostsModel.COMMENTS_BY_POSTID);
                    mNewsFeedAdapter.refreshCommentList(mFeedCommentModel);
                    break;
                case AppConstants.REPORT_POST_SUCCESS:
                    //TODO remove the reported post
                    getMyProfiles();
                    break;
                case AppConstants.WRITE_POST_REQUEST:
                    getMyProfiles();
                    break;
            }
        }

    }

    private void setVideoPost() {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(this.mVideoPathUri,
                MediaStore.Images.Thumbnails.MINI_KIND);
        Drawable drawable = new BitmapDrawable(getResources(), thumb);
        mPostImgVideoCloseBtnLayout.setVisibility(View.VISIBLE);
        mPostPicImgView.setVisibility(View.VISIBLE);
        mPlayIconImgView.setVisibility(View.VISIBLE);
        mPostImgVideoLayout.setVisibility(View.VISIBLE);
        mPostPicImgView.setImageDrawable(drawable);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        String data = new Gson().toJson(responseObj);

        if (responseObj instanceof ProfileModel) {
            ProfileModel mProfileModel = (ProfileModel) responseObj;
            if (mProfileModel.getResource().size() > 0) {
                mCurrentProfileObj = mProfileModel.getResource().get(0);
            }
            switch (responseType) {

                case RetrofitClient.GET_PROFILE_RESPONSE:

                    if (mProfileModel.getResource().size() > 0) {

                        mFullMPList.clear();
                        mFullMPList.addAll(mProfileModel.getResource());
                        if (isExtraProfile) {
                            isExtraProfile = false;
                            PreferenceUtils.getInstance(this).saveIntData(PreferenceUtils.CURRENT_PROFILE_POS, (mFullMPList.size() - 1));
                            if (mSearchStr.isEmpty()) {
                                mOtherProfileAdapter = null;
                                setOtherProfilesAdapter();
                            } else {
                                mSearchProfileAdapter = null;
                                setSearchProfilesAdapter();
                            }
                        }

                        MotoHub.getApplicationInstance().setFullProfileList(mFullMPList);
                        ArrayList<String> mTempMPSpinnerList = new ArrayList<>();
                        for (ProfileResModel mProfileResModel : mFullMPList) {
                            if (Utility.getInstance().isSpectator(mProfileResModel)) {
                                mTempMPSpinnerList.add(mProfileResModel.getSpectatorName());
                            } else {
                                mTempMPSpinnerList.add(mProfileResModel.getMotoName());
                            }
                        }
                        mMPSpinnerList.clear();
                        mMPSpinnerList.addAll(mTempMPSpinnerList);

                        changeAndSetProfile(getProfileCurrentPos());
                        isProfilePurchased = true;
                        //mSearchEt.setText("");
                        getUpcomingEvents();


//                        mIabHelper.flagEndAsync();
//                        checkInAppBillingSupport(mCoordinatorLayout);

                    } else {
                        showSnackBar(mCoordinatorLayout, mNoProfileErr);
                    }

                    break;

                case RetrofitClient.GET_FOLLOWING_PROFILE_RESPONSE:

                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {

                        mFollowingListData.clear();
                        mFollowingListData.addAll(mProfileModel.getResource());

                        launchTagFollowersProfileDialog();

                    } else {
                        showSnackBar(mCoordinatorLayout, mTagErr);
                    }
                    break;

                case RetrofitClient.UPDATE_PROFILE_RESPONSE:

                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        saveInAppPurchasesLocally(Integer.parseInt(PROFILE_PURCHASED), String.valueOf(getCurrentProfile().getProfileType()));
                        getMyProfiles();
                    }

                    break;
                case RetrofitClient.GET_OTHER_PROFILES_RESPONSE:
                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        mHorRvTotalCount = mProfileModel.getMeta().getCount();
                        mIsHorRvLoading = false;
                        if (mHorRvOffset == 0) {
                            mOtherProfilesList.clear();
                        }
                        mOtherProfilesList.addAll(mProfileModel.getResource());
                        mHorRvOffset = mHorRvOffset + mDataLimit;
                    } else {
                        if (mHorRvOffset == 0) {
                            mHorRvTotalCount = 0;
                        }
                    }
                    if (mOtherProfilesList.size() == 0) {
                        mSearchLay.setVisibility(View.GONE);
                        mSearchView.setVisibility(View.GONE);
                        mUnfollowersView.setVisibility(View.GONE);
                        mHorRecyclerView.setVisibility(View.GONE);
                    }
                    setOtherProfilesAdapter();
                    refreshFeeds(getCurrentProfile());

                    break;

                case RetrofitClient.PROFILE_FIND_FRIENDS_LOAD_MORE_RESPONSE:
                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        mHorFindFriendsRvTotalCount = mProfileModel.getMeta().getCount();
                        mIsHorFindFriendsRvLoading = false;
                        if (mHorFindFriendsRvOffset == 0) {
                            mSearchProfilesList.clear();
                        }
                        mSearchProfilesList.addAll(mProfileModel.getResource());
                        mHorFindFriendsRvOffset = mHorFindFriendsRvOffset + mDataLimit;
                    } else {
                        if (mHorFindFriendsRvOffset == 0) {
                            mHorFindFriendsRvTotalCount = 0;
                            showToast(this, getString(R.string.no_profile_found_to_follow_err));
                        }
                    }
                    setSearchProfilesAdapter();
                    break;
                case RetrofitClient.PROFILE_FIND_FRIENDS_RESPONSE:
                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        mHorFindFriendsRvTotalCount = mProfileModel.getMeta().getCount();
                        mIsHorFindFriendsRvLoading = false;
                        mSearchProfilesList.clear();
                        mSearchProfilesList.addAll(mProfileModel.getResource());
                        mHorFindFriendsRvOffset = mDataLimit;
                    } else {
                        if (mHorFindFriendsRvOffset == 0) {
                            mHorFindFriendsRvTotalCount = 0;
                            showToast(this, getString(R.string.no_profile_found_to_follow_err));
                        }
                    }
                    setSearchProfilesAdapter();
                    break;
            }

        } else if (responseObj instanceof PostsModel) {

            PostsModel mPostsModel = (PostsModel) responseObj;

            switch (responseType) {

                case RetrofitClient.CREATE_PROFILE_POSTS_RESPONSE:

                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {

                        mWritePostEt.setText("");
                        mPostImgUri = null;
                        mPostPicImgView.setImageResource(R.drawable.photos_icon);
                        mPostPicImgView.setVisibility(View.GONE);
                        mPostImgVideoCloseBtnLayout.setVisibility(View.GONE);
                        mPostImgVideoLayout.setVisibility(View.GONE);
                        mVideoPathUri = null;

                        mTaggedProfilesList.clear();
                        mTaggedProfilesAdapter.notifyDataSetChanged();

                        mFollowingListData.clear();

                        if (mTaggedProfilesList.size() > 0) {
                            mTagProfilesRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            mTagProfilesRecyclerView.setVisibility(View.GONE);
                        }
                        showSnackBar(mCoordinatorLayout, mPostSuccess);
                        //((BaseFragment) mNewsFeedFragment).retrofitOnResponse(responseObj, responseType);

                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            PostsResModel mPostsResModel = mPostsModel.getResource().get(0);
                            ProfileResModel mProfileResModel = new ProfileResModel();
                            mProfileResModel.setProfilePicture(mCurrentProfileObj.getProfilePicture());
                            mProfileResModel.setProfileType(mCurrentProfileObj.getProfileType());
                            if (Utility.getInstance().isSpectator(mCurrentProfileObj)) {
                                mProfileResModel.setSpectatorName(mCurrentProfileObj.getSpectatorName());
                            } else {
                                mProfileResModel.setDriver(mCurrentProfileObj.getDriver());
                            }
                            mPostsResModel.setProfilesByWhoPostedProfileID(mProfileResModel);
                            mPostsResModel.setProfileID(mCurrentProfileObj.getID());
                            mPostsRvTotalCount = mPostsRvTotalCount + 1;
                            mNewsFeedList.add(0, mPostsResModel);
                            setPostAdapter();
                            mNewsFeedAdapter.refreshProfilePos();
                        }

                    }
                    break;

                case RetrofitClient.GET_FEED_POSTS_RESPONSE:

                    /*if (mNewsFeedFragment.isVisible()) {
                        ((BaseFragment) mNewsFeedFragment).retrofitOnResponse(responseObj, responseType);
                    }*/

                    String data1 = new Gson().toJson(mPostsModel.getResource());

                    mRefresh = false;
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        mPostsRvTotalCount = mPostsModel.getMeta().getCount();
                        mIsPostsRvLoading = false;
                        if (mPostsRvOffset == 0) {
                            mNewsFeedList.clear();
                        }
                        mNewsFeedList.addAll(mPostsModel.getResource());
                        mPostsRvOffset = mPostsRvOffset + mDataLimit;
                    } else {
                        if (mPostsRvOffset == 0) {
                            mPostsRvTotalCount = 0;
                        }
                    }
                    setPostAdapter();
                    mNewsFeedAdapter.refreshProfilePos();
                    break;

                case RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE:

                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {

                        //((BaseFragment) mNewsFeedFragment).retrofitOnResponse(responseObj, responseType);

                        mNewsFeedList.remove(mPostPos);
                        mPostsRvTotalCount -= 1;
                        setPostAdapter();
                        mNewsFeedAdapter.refreshProfilePos();

                        showSnackBar(mCoordinatorLayout, getResources().getString(R.string.post_delete));
                    }

                    break;

                case RetrofitClient.SHARED_POST_RESPONSE:

                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {

                        //((BaseFragment) mNewsFeedFragment).retrofitOnResponse(responseObj, responseType);
                        PostsResModel mPostsResModel = mPostsModel.getResource().get(0);
                        mNewsFeedList.add(0, mPostsResModel);
                        mPostsRvTotalCount += 1;
                        setPostAdapter();
                        mNewsFeedAdapter.refreshProfilePos();
                        mNewsFeedAdapter.resetShareAdapter(mSharedFeed);
                        mNewsFeedAdapter.refreshProfilePos();

                        showSnackBar(mCoordinatorLayout, getResources().getString(R.string.post_shared));


                    }

                    break;
            }

        } else if (responseObj instanceof FeedCommentModel) {

            //((BaseFragment) mNewsFeedFragment).retrofitOnResponse(responseObj, responseType);
            FeedCommentModel mFeedCommentList = (FeedCommentModel) responseObj;
            switch (responseType) {
                case RetrofitClient.POST_COMMENTS:
                    ArrayList<FeedCommentModel> mNewFeedComment = mFeedCommentList.getResource();
                    mNewsFeedAdapter.resetCommentAdapter(mNewFeedComment.get(0));
                    break;
            }

        } else if (responseObj instanceof FeedLikesModel) {

            //((BaseFragment) mNewsFeedFragment).retrofitOnResponse(responseObj, responseType);
            FeedLikesModel mFeedLikesList = (FeedLikesModel) responseObj;
            ArrayList<FeedLikesModel> mNewFeedLike = mFeedLikesList.getResource();
            switch (responseType) {
                case RetrofitClient.POST_LIKES:
                    mNewsFeedAdapter.resetLikeAdapter(mNewFeedLike.get(0));
                    break;
                case RetrofitClient.POST_UNLIKE:
                    mNewsFeedAdapter.resetDisLike(mNewFeedLike.get(0));
                    break;
            }

        } else if (responseObj instanceof EventsModel) {
            EventsModel mEventsModel = (EventsModel) responseObj;
            if (mEventsModel.getResource() != null && mEventsModel.getResource().size() > 0) {
                mEventsRecyclerView.setVisibility(View.VISIBLE);
                mEventsFindListData.clear();
                mEventsFindListData.addAll(mEventsModel.getResource());
                setEventsAdapter();
            } else {
                mEventsRecyclerView.setVisibility(View.GONE);
            }
            getOtherProfileList();
        } else if (responseObj instanceof EventsWhoIsGoingModel) {
            EventsWhoIsGoingModel mEventsWhoIsGoingModel = (EventsWhoIsGoingModel) responseObj;
            if (mEventsWhoIsGoingModel.getResource() != null && mEventsWhoIsGoingModel.getResource().size() > 0) {
                mEventsFindAdapter.bookAnEventSuccess(mEventsWhoIsGoingModel.getResource().get(0));
                mEventsFindAdapter.callPostRacingData();
            }
        } else if (responseObj instanceof PaymentModel) {
            PaymentModel mResponse = (PaymentModel) responseObj;
            if (mResponse.getStatus() != null && mResponse.getStatus().equals("succeeded")) {
                //  mTempPaymentModel = mResponse;
                mEventsFindAdapter.bookAnEventRequest(mResponse);
            } else {
                String mErrorMsg = "Your card was declined.";
                if (mResponse.getMessage() != null) {
                    mErrorMsg = mResponse.getMessage();
                }
                mErrorMsg = mErrorMsg + " " + getString(R.string.try_again);
                showToast(this, mErrorMsg);
            }
        } else if (responseObj instanceof RacingModel) {
            ArrayList<EventAddOnModel> mSelectedEventAddOn = MotoHub.getApplicationInstance().getPurchasedAddOn();
            if (mSelectedEventAddOn.size() > 0)
                mEventsFindAdapter.callPostSelectedAddOns(mSelectedEventAddOn);
            else
                showSnackBar(mCoordinatorLayout, "Successfully booked an event.");
        } else if (responseObj instanceof EventAnswersModel) {
            EventAnswersModel mEventAnswerList = (EventAnswersModel) responseObj;
            ArrayList<EventAnswersModel> mResEventAnswerModel = mEventAnswerList.getResource();
            mEventsFindAdapter.callUpdateEventAnswerScreen(mResEventAnswerModel);
        } else if (responseObj instanceof PurchasedAddOnModel) {
            showSnackBar(mCoordinatorLayout, "Successfully booked an event.");
        } else if (responseObj instanceof SessionModel) {

            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            getMyProfiles();

        } else if (responseObj instanceof FeedShareModel) {

            //((BaseFragment) mNewsFeedFragment).retrofitOnResponse(responseObj, responseType);
            FeedShareModel mFeedShareList = (FeedShareModel) responseObj;
            ArrayList<FeedShareModel> mNewFeedShare = mFeedShareList.getResource();
            switch (responseType) {
                case RetrofitClient.POST_SHARES:
                    mSharedFeed = mNewFeedShare.get(0);
                    CommonAPI.getInstance().callAddSharedPost(ViewProfileActivity.this, mNewsFeedList.get(mCurrentPostPosition), mCurrentProfileObj);
                    break;
                case RetrofitClient.DELETE_SHARED_POST_RESPONSE:
                    break;
            }

        } else if (responseObj instanceof ImageModel) {

            ImageModel mImageModel = (ImageModel) responseObj;

            switch (responseType) {

                case RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE:
                    //update the record in database/tablet
                    String imgUrl = getHttpFilePath(mImageModel.getmModels().get(0).getPath());
                    profilePostContent(imgUrl);
                    break;
            }

        } else if (responseObj instanceof FeedCommentReplyModel) {
            //((BaseFragment) mNewsFeedFragment).retrofitOnResponse(responseObj, responseType);
            FeedCommentModel mFeedCommentList = (FeedCommentModel) responseObj;
            switch (responseType) {
                case RetrofitClient.POST_COMMENTS:
                    ArrayList<FeedCommentModel> mNewFeedComment = mFeedCommentList.getResource();
                    mNewsFeedAdapter.resetCommentAdapter(mNewFeedComment.get(0));
                    break;
            }
        } else if (responseObj instanceof LiveStreamResponse) {
            if (isDelete) {
                isDelete = false;
            } else {
                LiveStreamResponse mLiveStreamResponse = (LiveStreamResponse) responseObj;
                if (mLiveStreamResponse.getResource().size() > 0) {
                    liveStreamID = mLiveStreamResponse.getResource().get(0).getID();
                    mLiveStreamName = mLiveStreamResponse.getResource().get(0).getStreamName();
                }
                MotoHub.getApplicationInstance().setLiveStreamName(mLiveStreamName);
                Intent mCameraActivity = new Intent(this, CameraActivity.class);
                startActivity(mCameraActivity);
            }

        } else if (responseObj instanceof PushTokenModel) {
            clearBeforeLogout();
            Intent loginActivity = new Intent(this, LoginActivity.class);
            loginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginActivity);
            finish();
        } else if (responseObj instanceof FeedCommentLikeModel) {
            //((BaseFragment) mNewsFeedFragment).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof ReplyLikeModel) {
            //((BaseFragment) mNewsFeedFragment).retrofitOnResponse(responseObj, responseType);
        }
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);

        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else if (code == RetrofitClient.GET_FEED_POSTS_RESPONSE) {
            //((BaseFragment) mNewsFeedFragment).retrofitOnError(code, message);
            if (code == RetrofitClient.GET_FEED_POSTS_RESPONSE)
                mPostsRvTotalCount = 0;
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mCoordinatorLayout, mErrorMsg);
        }

    }

    @Override
    public void retrofitOnError(int code, String message, int responseType) {
        super.retrofitOnError(code, message, responseType);

        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            switch (responseType) {
                case RetrofitClient.POST_SHARES:
                    showSnackBar(mCoordinatorLayout, "Already shared this post");
                    break;
            }
        }
    }

    @Override
    public int getTotalUnFollowedResultCount() {
        return mSearchStr.isEmpty() ? mHorRvTotalCount : mHorFindFriendsRvTotalCount;
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
        //showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);

    }

    @Override
    public void onBackPressed() {
        showAppDialog(AppDialogFragment.ALERT_EXIT_DIALOG, null);
    }

    @Override
    public int getTotalPostsResultCount() {
        //return ((BaseFragment) mNewsFeedFragment).getTotalPostsResultCount();
        return mPostsRvTotalCount;
    }

    private void uploadPicture(String imgUri) {
        File mFile = new File(Uri.parse(imgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", mFile.getName(), requestBody);
        RetrofitClient.getRetrofitInstance().callUploadProfilePostImg(this, filePart, RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE);

    }

    private void profilePostContent(String postImgFilePath) {

        try {

            String mWritePostStr = mWritePostEt.getText().toString().trim();

            if (TextUtils.isEmpty(postImgFilePath) && TextUtils.isEmpty(mWritePostStr)) {
                return;
            }

            int mUserId = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);

            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(PostsModel.POST_TEXT, URLEncoder.encode(mWritePostStr, "UTF-8"));

            String mPostPic = "[\"" + postImgFilePath + "\"]";

            mJsonObject.addProperty(PostsModel.POST_PICTURE, mPostPic);
            mJsonObject.addProperty(PostsModel.PROFILE_ID, getCurrentProfile().getID());
            mJsonObject.addProperty(PostsModel.WHO_POSTED_PROFILE_ID, getCurrentProfile().getID());
            mJsonObject.addProperty(PostsModel.WHO_POSTED_USER_ID, mUserId);
            mJsonObject.addProperty(PostsModel.IS_NEWS_FEED_POST, true);

            if (mTaggedProfilesList.size() == 0) {
                mJsonObject.addProperty(PostsModel.TAGGED_PROFILE_ID, "");
            } else {
                StringBuilder mTaggedProfileID = new StringBuilder();
                for (ProfileResModel mProfileResModel : mTaggedProfilesList) {
                    mTaggedProfileID.append(mProfileResModel.getID()).append(",");
                }
                mTaggedProfileID.deleteCharAt(mTaggedProfileID.length() - 1);
                mJsonObject.addProperty(PostsModel.TAGGED_PROFILE_ID, mTaggedProfileID.toString());
            }

            JsonArray mJsonArray = new JsonArray();
            mJsonArray.add(mJsonObject);

            RetrofitClient.getRetrofitInstance().callCreateProfilePosts(this, mJsonArray, RetrofitClient.CREATE_PROFILE_POSTS_RESPONSE);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void launchTagFollowersProfileDialog() {
        DialogFragment mDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(AppDialogFragment.TAG);
        if (mDialogFragment != null && mDialogFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(mDialogFragment).commit();
        }
        AppDialogFragment.newInstance(AppDialogFragment.TAG_FOLLOWING_PROFILE_DIALOG, mFollowingListData, null)
                .show(getSupportFragmentManager(), AppDialogFragment.TAG);
        mTagProfilesRecyclerView.setVisibility(View.GONE);
        mTaggedProfilesList.clear();
    }

    @Override
    public void onSuccess() {
        isDelete = true;
        String mLiveStreamName = "ID=" + liveStreamID;
        RetrofitClient.getRetrofitInstance().callDeleteLiveStream(this, mLiveStreamName);

    }

    @Override
    public void notifyEmptyTaggedProfilesList(ArrayList<ProfileResModel> mTaggedProfilesList1) {
        mTaggedProfilesList = mTaggedProfilesList1;
        if (mTaggedProfilesList.size() > 0) {
            mTagProfilesRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mTagProfilesRecyclerView.setVisibility(View.GONE);
        }
    }

    private ProfileResModel getCurrentProfile() {
        return mFullMPList.get(getProfileCurrentPos());
    }

    public void refreshFeeds(ProfileResModel mProfileObj) {
        //((BaseFragment) mNewsFeedFragment).setRefresh(true);
        //((BaseFragment) mNewsFeedFragment).callFeedPost(mProfileObj);
        mRefresh = true;
        mCurrentProfileObj = mProfileObj;
        mPostsRvTotalCount = -1;
        mPostsRvOffset = 0;
        mNewsFeedList.clear();
        setPostAdapter();
        getNewsFeedPosts();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mCollapsingToolbarLay.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(mCollapsingToolbarLay)) {
            mSwipeRefreshLay.setEnabled(false);
        } else {
            mSwipeRefreshLay.setEnabled(true);
        }
    }

    //New
    private void getNewsFeedPosts() {
        if (mCurrentProfileObj != null) {
            String mFilter;
            String mMyFollowingsID = Utility.getInstance().getMyFollowersFollowingsID(mCurrentProfileObj.getFollowprofile_by_ProfileID(), false);
            String mBlockedUsers = Utility.getInstance().getMyBlockedUsersID(mCurrentProfileObj.getBlockedUserProfilesByProfileID(),
                    mCurrentProfileObj.getBlockeduserprofiles_by_BlockedProfileID());

            String mPromoterFollowers = Utility.getInstance().getMyPromoterFollowingsID(mCurrentProfileObj.getPromoterFollowerByProfileID());
            if (mMyFollowingsID.isEmpty()) {

                mFilter = "((ProfileID=" + mCurrentProfileObj.getID() + ") OR (TaggedProfileID LIKE '%," + mCurrentProfileObj.getID() + ",%') OR (SharedProfileID LIKE '%," + mCurrentProfileObj.getID() +
                        ",%') AND ((user_type!='promoter') AND (user_type!='club') AND (user_type!='newsmedia') AND (user_type!='track')  AND (user_type!='shop')))";
            } else {
                mFilter = "((ProfileID IN (" + mMyFollowingsID + ","
                        + mCurrentProfileObj.getID()
                        + ")) OR (TaggedProfileID LIKE '%," + mCurrentProfileObj.getID()
                        + ",%')  OR (SharedProfileID LIKE '%," + mCurrentProfileObj.getID()
                        + ",%') AND ((user_type!='promoter') AND (user_type!='club') AND " + "(user_type!='newsmedia') AND (user_type!='track')  AND (user_type!='shop')))";
            }
            if (!mBlockedUsers.trim().isEmpty()) {
                mFilter = "(" + mFilter + " AND (ProfileID NOT IN (" + mBlockedUsers + "))) AND (ReportStatus == false)";
            }
            if (!mPromoterFollowers.trim().isEmpty()) {

                mFilter = "(" + mFilter + " OR ((ProfileID IN (" + mPromoterFollowers + ")) AND ((user_type!='user') AND (user_type!='sharedPost')))) AND (ReportStatus == false)";
            }
            RetrofitClient.getRetrofitInstance().callGetProfilePosts(
                    this,
                    mFilter,
                    RetrofitClient.GET_FEED_POSTS_RESPONSE,
                    mDataLimit,
                    mPostsRvOffset);

        }
    }

    private void setPostAdapter() {
        if (mNewsFeedAdapter == null) {
            mNewsFeedAdapter = new PostsAdapter(mNewsFeedList, mCurrentProfileObj, this);
            newsFeedRecyclerView.setAdapter(mNewsFeedAdapter);
        } else {
            mNewsFeedAdapter.notifyDataSetChanged();
        }
    }

    private void updatePost(Intent intent) {
        PostsResModel mPostsModel = (PostsResModel) intent.getSerializableExtra(PostsModel.POST_MODEL);
        if (mPostsModel.getUserType().trim().equals("user")) {
            if (intent.hasExtra(PostsModel.IS_UPDATE)) {
                mNewsFeedList.set(mPostPos, mPostsModel);
            } else {
                mPostsRvTotalCount = mPostsRvTotalCount + 1;
                mNewsFeedList.add(0, mPostsModel);
            }
            setPostAdapter();
            showToast(getApplicationContext(), getString(R.string.post_update_success));
        }
    }

    private void navigateNotification(String json) {
        try {
            JSONObject mEntryObj = new JSONObject(json);

            int mNotificationPostID, mNotificationEventID;

            String mContentTitle;

            Intent mResultIntent;

            JSONObject mDetailsObj = mEntryObj.getJSONObject("Details");
            switch (mEntryObj.getString("Type")) {

                case "LIVE_STREAM":
                    /*mNotificationPostID = Integer.parseInt(mDetailsObj.get(APIConstants.ID).toString());
                    composeStreamNotification(mEntryObj, this, mNotificationPostID, ViewLiveVideoViewScreen2.class);*/
                    mResultIntent = new Intent(this, ViewLiveVideoViewScreen2.class);
                    mResultIntent.putExtra(AppConstants.PROFILE_ID, mDetailsObj.getInt(APIConstants.StreamProfileID));
                    startActivity(mResultIntent);
                    break;
                case "LIVE_REQUEST":
                    /*mNotificationPostID = Integer.parseInt(mDetailsObj.get(APIConstants.ID).toString());
                    composeStreamNotification(mEntryObj, this, mNotificationPostID, ViewRequestUsersActivity.class);*/
                    mResultIntent = new Intent(this, ViewRequestUsersActivity.class);
                    mResultIntent.putExtra(AppConstants.PROFILE_ID, mDetailsObj.getInt(APIConstants.StreamProfileID));
                    startActivity(mResultIntent);
                    break;
                case "LIVE_ACCEPT":
                    /*mNotificationPostID = Integer.parseInt(mDetailsObj.get(APIConstants.ID).toString());
                    composeStreamNotification(mEntryObj, this, mNotificationPostID, ViewStreamUsersActivity.class);*/
                    mResultIntent = new Intent(this, ViewStreamUsersActivity.class);
                    mResultIntent.putExtra(AppConstants.PROFILE_ID, mDetailsObj.getInt(APIConstants.StreamProfileID));
                    startActivity(mResultIntent);
                    break;
                case "FOLLOW":
                    /*mNotificationPostID = Integer.parseInt(mDetailsObj.get(PostsModel.PROFILE_ID).toString());
                    composeNotification(mEntryObj, this, mNotificationPostID, OthersMotoFileActivity.class);*/
                    mResultIntent = new Intent(this, OthersMotoFileActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    break;
                case "FOLLOWER_POST":
                case "TAGGED":
                    /*mNotificationPostID = Integer.parseInt(mDetailsObj.get("ID").toString());
                    composeNotification(mEntryObj, this, mNotificationPostID, PostViewActivity.class);*/
                    mResultIntent = new Intent(this, PostViewActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    break;
                case "COMMENT_LIKE":
                    /*mNotificationPostID = Integer.parseInt(mDetailsObj.get("CommentID").toString());
                    composeNotification(mEntryObj, this, mNotificationPostID, PostCommentLikeViewActivity.class);*/
                    mResultIntent = new Intent(this, PostCommentLikeViewActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    //mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    break;
                case "COMMENT_REPLY_LIKE":
                    /*mNotificationPostID = Integer.parseInt(mDetailsObj.get("PostcommentID").toString());
                    composeNotification(mEntryObj, this, mNotificationPostID, CommentReplyActivity.class);*/
                    mResultIntent = new Intent(this, CommentReplyActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    break;
                case "COMMENT_REPLY":
                    /*mNotificationPostID = Integer.parseInt(mDetailsObj.get("CommentID").toString());
                    composeNotification(mEntryObj, this, mNotificationPostID, CommentReplyActivity.class);*/
                    mResultIntent = new Intent(this, CommentReplyActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    break;
                case "POST_COMMENTS":
                case "POST_LIKES":
                    /*mNotificationPostID = Integer.parseInt(mDetailsObj.get("PostID").toString());
                    composeNotification(mEntryObj, this, mNotificationPostID, PostViewActivity.class);*/
                    mResultIntent = new Intent(this, PostViewActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    //mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    break;
                case "VIDEO_SHARE":
                    /*mNotificationPostID = Integer.parseInt(mDetailsObj.get("OriginalVideoID").toString());
                    composeNotification(mEntryObj, this, mNotificationPostID, PostViewActivity.class);*/
                    mResultIntent = new Intent(this, PostViewActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    //mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    break;
                case "POST":
                case "POST_SHARE":
                    /*mNotificationPostID = Integer.parseInt(mDetailsObj.get("ID").toString());
                    composeNotification(mEntryObj, this, mNotificationPostID, PostViewActivity.class);*/
                    mResultIntent = new Intent(this, PostViewActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    //mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    break;
                case "VEHICLE_LIKE":
                    /*mNotificationPostID = Integer.parseInt(mDetailsObj.get(VehicleInfoLikeModel.LIKED_PROFILE_ID).toString());
                    composeNotification(mEntryObj, this, mNotificationPostID, UpdateProfileActivity.class);*/
                    mResultIntent = new Intent(this, UpdateProfileActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    break;
                case "EVENT_CREATION":
                    /*int mEventCreationID = Integer.parseInt((mDetailsObj.get("PromoterID").toString()) + (mDetailsObj.get("EventID").toString()));
                    composeNotification(mEntryObj, this, mEventCreationID, NotificationEventCreatedActivity.class);*/
                    mResultIntent = new Intent(this, NotificationEventCreatedActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    //mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    break;
                case "VIDEO_COMMENT_LIKE":
                    /*mNotificationPostID = mDetailsObj.getInt("VideoCommentID");
                    composeNotification(mEntryObj, this, mNotificationPostID, VideoCommentsActivity.class);*/
                    mResultIntent = new Intent(this, VideoCommentsActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    break;
                case "VIDEO_COMMENT_REPLY":
                    /*mNotificationPostID = mDetailsObj.getInt("CommentID");
                    composeNotification(mEntryObj, this, mNotificationPostID, VideoCommentReplyActivity.class);*/
                    mResultIntent = new Intent(this, VideoCommentReplyActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    break;
                case "VIDEO_COMMENT_REPLY_LIKE":
                    /*mNotificationPostID = mDetailsObj.getInt("VideoCommentID");
                    composeNotification(mEntryObj, this, mNotificationPostID, VideoCommentReplyActivity.class);*/
                    mResultIntent = new Intent(this, VideoCommentReplyActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    break;
                case "VIDEO_COMMENTS":
                case "VIDEO_LIKES":
                    /*mNotificationPostID = mDetailsObj.getInt("VideoID");
                    composeNotification(mEntryObj, this, mNotificationPostID, PromoterVideoGalleryActivity.class);*/
                    mResultIntent = new Intent(this, PromoterVideoGalleryActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    break;
                case "EVENT_CHAT":
                    /*try {
                        //JSONObject mJsonEntryObject = new JSONObject(mEntryObj.opt(ENTRY_JSON_OBJ).toString());
                        mResultIntent = new Intent(this, ChatBoxEventGrpActivity.class);
                        mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                        mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                        startActivity(mResultIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    mResultIntent = new Intent(this, ChatBoxEventGrpActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);

                    break;
                case "EVENT_LIVE_CHAT":

                    /*try {
                        //JSONObject mJsonEntryObject = new JSONObject(mEntryObj.opt(ENTRY_JSON_OBJ).toString());
                        mResultIntent = new Intent(this, EventLiveActivity.class);
                        mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                        mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                        startActivity(mResultIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    mResultIntent = new Intent(this, EventLiveActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);

                    break;
                case "GROUP_CHAT_MSG":
                    /*try {
                     *//*JSONObject mNotificationJsonObject = new JSONObject();
                        mNotificationJsonObject.put(ENTRY_JSON_OBJ, mEntryObj);
                        JSONObject mGroupChatObj = mDetailsObj.getJSONObject("GroupChat");
                        String mGroupChatRoomID = (mDetailsObj.get("GroupChatRoomID").toString());
                        mNotificationEventID = Integer.parseInt(mDetailsObj.get("GroupChatRoomID").toString());
                        mNotificationJsonObject.put((GroupChatRoomModel.GRP_CHAT_ROOM_ID), mGroupChatRoomID);
                        mContentTitle = "Group : " + ((mGroupChatObj.get("GroupName").toString()));
                        composeChatNotification(mNotificationJsonObject, this, mNotificationEventID, mGroupChatRoomID, mContentTitle, ChatBoxGroupActivity.class);*//*
                        //JSONObject mJsonEntryObject = new JSONObject(mEntryObj.opt(ENTRY_JSON_OBJ).toString());
                        mResultIntent = new Intent(this, ChatBoxGroupActivity.class);
                        mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                        mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                        startActivity(mResultIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    mResultIntent = new Intent(this, ChatBoxGroupActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);

                    break;
                case "SINGLE_CHAT":

                    /*try {
                     *//*JSONObject mNotificationJsonObject = new JSONObject();
                        mNotificationJsonObject.put(ENTRY_JSON_OBJ, mEntryObj);
                        String mSingleChatID = mDetailsObj.get("FromProfileID").toString();
                        int mNotificationID = Integer.parseInt(mDetailsObj.get("FromProfileID").toString());
                        mContentTitle = "From : " + ((mDetailsObj.get(SENDER_NAME).toString()));
                        composeChatNotification(mNotificationJsonObject, this, mNotificationID, mSingleChatID, mContentTitle, ChatBoxSingleActivity.class);*//*
                        JSONObject mJsonEntryObject = new JSONObject(mEntryObj.opt(ENTRY_JSON_OBJ).toString());
                        mResultIntent = new Intent(this, ChatBoxGroupActivity.class);
                        mResultIntent.putExtra(ENTRY_JSON_OBJ, mJsonEntryObject.toString());
                        mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                        startActivity(mResultIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    mResultIntent = new Intent(this, ChatBoxSingleActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);

                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class VideoCompressor extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogManager.showProgress(ViewProfileActivity.this);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return MediaController.getInstance().convertVideo(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            DialogManager.hideProgress();
            if (compressed) {
                showToast(ViewProfileActivity.this, getString(R.string.uploading_video));
                startUploadVideoService();
            }
        }
    }
}
