package online.motohub.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.club.ClubsListActivity;
import online.motohub.activity.news_and_media.NewsAndMediaListActivity;
import online.motohub.activity.ondemand.OnDemandActivity;
import online.motohub.activity.performance_shop.PerformanceShopListActivity;
import online.motohub.activity.promoter.PromotersListActivity;
import online.motohub.activity.track.TrackListActivity;
import online.motohub.adapter.EventsFindAdapter;
import online.motohub.adapter.FollowPhoneEmailProfileAdapter;
import online.motohub.adapter.FollowProfileAdapter;
import online.motohub.adapter.PostsAdapter;
import online.motohub.adapter.TaggedProfilesAdapter;
import online.motohub.application.MotoHub;
import online.motohub.database.DatabaseHandler;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.CommonInterface;
import online.motohub.interfaces.SharePostInterface;
import online.motohub.model.EventAddOnModel;
import online.motohub.model.EventAnswersModel;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsResModel;
import online.motohub.model.EventsWhoIsGoingModel;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.FollowProfileEntity;
import online.motohub.model.FollowProfileModel;
import online.motohub.model.ImageModel;
import online.motohub.model.LiveStreamResponse;
import online.motohub.model.NotificationBlockedUsersModel;
import online.motohub.model.PaymentModel;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PurchasedAddOnModel;
import online.motohub.model.PushTokenModel;
import online.motohub.model.RacingModel;
import online.motohub.model.SessionModel;
import online.motohub.model.SpectatorLiveEntity;
import online.motohub.model.VideoShareModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.CommonAPI;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.StringUtils;
import online.motohub.util.UploadFileService;
import online.motohub.util.UploadOfflineVideos;
import online.motohub.util.Utility;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static online.motohub.application.MotoHub.mIsFirstLaunch;
import static online.motohub.application.MotoHub.setmIsFirstLaunch;
import static online.motohub.fcm.MyFireBaseMessagingService.ENTRY_JSON_OBJ;
import static online.motohub.fcm.MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY;

public class ViewProfileActivity extends BaseActivity implements
        PostsAdapter.TotalRetrofitPostsResultCount, CommonInterface, FollowProfileAdapter.TotalRetrofitResultCount,
        TaggedProfilesAdapter.TaggedProfilesSizeInterface, AppBarLayout.OnOffsetChangedListener, OnMenuItemClickListener {

    public static final String EXTRA_RESULT_DATA = "activity_video_picker_uri";
    private static final int mDataLimit = 15;
    private static final String TAG = ViewProfileActivity.class.getName();
    private static String mSearchStr = "", mPhoneEmailSearchStr = "";
    private static ArrayList<ProfileResModel> mSearchProfilesList = new ArrayList<>();
    @BindView(R.id.postNextedScrollView)
    NestedScrollView postNestedScrollView;
    @BindView(R.id.news_feed_recycler_view)
    RecyclerView newsFeedRecyclerView;
    @BindView(R.id.live_events_recycler_view)
    RecyclerView mEventsRecyclerView;
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
    @BindView(R.id.hor_phone_email_friends_recycler_view)
    RecyclerView mHorPhoneEmailFriendsRecyclerView;
    @BindView(R.id.search_phone_email_et)
    EditText mSearchPhoneEmailEt;
    @BindView(R.id.relative_layout_phone_email_friends)
    RelativeLayout mRelativeLayoutPhoneEmailFriends;
    @BindView(R.id.btn_connect_with_friends)
    Button mBtnConnectWithFriends;
    @BindView(R.id.shimmer_findfriends)
    ShimmerFrameLayout mShimmerView_otherprofiles;
    @BindView(R.id.shimmer_posts)
    ShimmerFrameLayout mShimmerView_posts;
    @BindView(R.id.shimmer_phonecontacts)
    ShimmerFrameLayout mShimmerView_phonecontacts;
    @BindView(R.id.shimmer_profile)
    ShimmerFrameLayout mShimmerView_profiles;

    private ArrayList<ProfileResModel> mFullMPList = new ArrayList<>();
    private ArrayList<String> mMPSpinnerList = new ArrayList<>();
    private List<EventsResModel> mEventsFindListData = new ArrayList<>();
    private ArrayList<ProfileResModel> mFollowingListData = new ArrayList<>();
    private ArrayList<ProfileResModel> mTaggedProfilesList = new ArrayList<>();
    private boolean isDelete = false;
    private int liveStreamID = 0;
    private String mLiveStreamName = "";
    private LinearLayoutManager mHorLayoutManager;
    private int mHorRvUnfollowOffset = 0, mHorRvUnfollowTotalCount = 0, mHorFindFriendsRvOffset = 0, mHorRvfollowTotalCount = 0,
            mHorFindFriendsRvTotalCount = 0, mHorRvOtherUsersCount = 0;

    private boolean mIsHorRvLoading = true, mIsHorFindFriendsRvLoading = true, isSearched = false;
    private ArrayList<ProfileResModel> mOtherProfilesList = new ArrayList<>();
    private ArrayList<ProfileResModel> mPhoneEmailProfileList = new ArrayList<>();
    private ArrayList<ProfileResModel> mAllPhoneEmailProfileList = new ArrayList<>();
    private FollowProfileAdapter mOtherProfileAdapter = null;
    private FollowProfileAdapter mSearchProfileAdapter = null;

    private FollowPhoneEmailProfileAdapter mPhoneEmailProfileAdapter = null;
    private boolean isExtraProfile = false;
    //New changes
    private LinearLayoutManager mNewsFeedLayoutManager;
    private ArrayList<PostsResModel> mNewsFeedList = new ArrayList<>();
    private PostsAdapter mNewsFeedAdapter;
    private int mPostPos;
    private int mPostsRvOffset = 0, mPostsRvTotalCount = 0;
    private boolean mIsPostsRvLoading = true;
    private int mCurrentPostPosition;
    private FeedShareModel mSharedFeed;
    private ProfileResModel mCurrentProfileObj;
    //Ended
    String data = "This build version is only for testing purpose.";
    String contactDescription = "We will connect you with your existing friends already on Motohub.";
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            assert b != null;
            String mUsertype = b.getString(AppConstants.USER_TYPE);
            assert mUsertype != null;
            if (mUsertype.equals("user"))
                updatePost(intent);
        }
    };
    private String mShareTxt = "";
    SharePostInterface mShareTextWithPostInterface = new SharePostInterface() {
        @Override
        public void onSuccess(String shareMessage) {
            mShareTxt = shareMessage;
            CommonAPI.getInstance().callPostShare(ViewProfileActivity.this, mNewsFeedList.get(mCurrentPostPosition), mCurrentProfileObj.getID());
        }
    };
    private EventsFindAdapter mEventsFindAdapter;
    private String mPhoneNumbers = "", mEmailIDs = "";
    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;

    public ViewProfileActivity getInstance() {
        return this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        ButterKnife.bind(this);
        initView();
        postNestedScrollView.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, new IntentFilter(UploadFileService.NOTIFY_POST_VIDEO_UPDATED));
        MotoHub.getApplicationInstance().newsFeedFragmentOnResume();
        assert getIntent().getExtras() != null;
        if (getIntent().getExtras().getBoolean(IS_FROM_NOTIFICATION_TRAY)) {
            try {
                JSONObject mJsonObject = new JSONObject(getIntent().getExtras().getString(MyFireBaseMessagingService.ENTRY_JSON_OBJ));
                navigateNotification(mJsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (mNewsFeedList.size() == 0 && mOtherProfilesList.size() == 0) {
                if (isNetworkConnected(this)) {
                    getMyProfiles();
                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
            }
        }
        uploadOffline();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
        MotoHub.getApplicationInstance().newsFeedFragmentOnPause();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initView() {
        setupUI(mCoordinatorLayout);
        setUpPurchseSuccessUI();
        AppConstants.LIVE_STREAM_CALL_BACK = this;
        setToolbar(mToolbar, mToolbarTitle);

        //Shimmer Animation
        mShimmerView_otherprofiles.startShimmerAnimation();
        mShimmerView_posts.startShimmerAnimation();
        mShimmerView_phonecontacts.startShimmerAnimation();
        mShimmerView_profiles.startShimmerAnimation();

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
                if (!mIsHorRvLoading && !(mHorRvUnfollowOffset >= mHorRvUnfollowTotalCount)) {
                    if ((mVisibleItemCount + mFirstVisibleItemPosition) >= mTotalItemCount && mFirstVisibleItemPosition >= 0) {
                        mIsHorRvLoading = true;
                        if (mSearchEt.getText().toString().length() == 0)
                            getOtherProfileList();
                    }
                }
            }
        });

        LinearLayoutManager mHorPhoneEmailLayoutManager = new LinearLayoutManager(this);
        mHorPhoneEmailLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHorPhoneEmailFriendsRecyclerView.setLayoutManager(mHorPhoneEmailLayoutManager);
        //new
        mNewsFeedLayoutManager = new LinearLayoutManager(getApplicationContext());
        mNewsFeedLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        newsFeedRecyclerView.setLayoutManager(mNewsFeedLayoutManager);

        postNestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = postNestedScrollView.getChildAt(postNestedScrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (postNestedScrollView.getHeight() + postNestedScrollView
                        .getScrollY()));

                if (diff == 0 && mNewsFeedList.size() > 0) {
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
        fragmentManager = getSupportFragmentManager();
        initMenuFragment();
        //TODO friends search
        RxTextView.textChanges(mSearchEt)
                .skip(1)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.computation())
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        SystemClock.sleep(1000); // Simulate the heavy stuff.
                        return true;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CharSequence>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "Error.", e);
                    }

                    @Override
                    public void onNext(CharSequence charSequence) {
                        if (charSequence.length() > 0) {
                            isSearched = true;
                            mShimmerView_otherprofiles.startShimmerAnimation();
                            findFriendsOrVehicles(charSequence.toString());
                        } else {
                            mHorRvUnfollowOffset = 0;
                            mIsHorRvLoading = true;
                            mOtherProfilesList.clear();
                            mShimmerView_otherprofiles.startShimmerAnimation();
                            getOtherProfileList();
                        }
                    }
                });

        mSearchPhoneEmailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    filterPhoneEmailProfiles(s.toString());
                } else {
                    mPhoneEmailProfileList.clear();
                    mPhoneEmailProfileList.addAll(mAllPhoneEmailProfileList);
                    if (mPhoneEmailProfileAdapter != null)
                        mPhoneEmailProfileAdapter.notifyDataSetChanged();
                }
            }
        });
        // showAlertDialog(data);
    }


    private void showAlertDialog(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ViewProfileActivity.this, R.style.MyAlertDialogStyle);
                builder.setTitle(data);
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //finish();
                        dialog.dismiss();
                    }
                });
                //builder.setNegativeButton("No", null);
                builder.show();
            }
        });
    }

    private void filterPhoneEmailProfiles(String text) {
        try {
            ArrayList<ProfileResModel> temp = new ArrayList<>();
            for (ProfileResModel d : mAllPhoneEmailProfileList) {
                if (d.getPhone().contains(text) || d.getEmail().trim().contains(text) || d.getDriver().toLowerCase().contains(text.toLowerCase()) || d.getSpectatorName().toLowerCase().contains(text.toLowerCase()) || d.getMotoName().toLowerCase().contains(text.toLowerCase()) || d.getModel().toLowerCase().contains(text.toLowerCase())) {
                    temp.add(d);
                }
            }
            if (temp.size() > 0) {
                mPhoneEmailProfileList.clear();
                mPhoneEmailProfileList.addAll(temp);
                mPhoneEmailProfileAdapter.notifyDataSetChanged();
            } else {
                mPhoneEmailProfileList.clear();
                mPhoneEmailProfileAdapter.notifyDataSetChanged();
                showToast(this, getString(R.string.no_profile_found_err));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOtherProfilesAdapter() {
        try {
            ViewProfileActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mSearchStr = "";
                    mSearchProfileAdapter = null;
                    mSearchEt.setVisibility(View.VISIBLE);
                    mSearchEt.setClickable(true);
                    if (mOtherProfileAdapter == null) {
                        mOtherProfileAdapter = new FollowProfileAdapter(mOtherProfilesList, mCurrentProfileObj, ViewProfileActivity.this);
                        mHorRecyclerView.setAdapter(mOtherProfileAdapter);
                    } else {
                        mOtherProfileAdapter.notifyDataSetChanged();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setPhoneEmailProfilesAdapter() {
        ViewProfileActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPhoneEmailSearchStr = "";
                mSearchPhoneEmailEt.setVisibility(View.VISIBLE);
                mSearchPhoneEmailEt.setClickable(true);
                Set<ProfileResModel> hs = new HashSet<>(mPhoneEmailProfileList);
                mPhoneEmailProfileList.clear();
                mPhoneEmailProfileList.addAll(hs);
                if (mPhoneEmailProfileAdapter == null) {
                    mPhoneEmailProfileAdapter = new FollowPhoneEmailProfileAdapter(mPhoneEmailProfileList, mCurrentProfileObj, ViewProfileActivity.this);
                    mHorPhoneEmailFriendsRecyclerView.setAdapter(mPhoneEmailProfileAdapter);
                } else {
                    mPhoneEmailProfileAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setEventsAdapter() {
        ViewProfileActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                if (mEventsFindAdapter == null && mCurrentProfileObj != null && mCurrentProfileObj.getID() != 0) {
                    mEventsFindAdapter = new EventsFindAdapter(ViewProfileActivity.this, mEventsFindListData, mCurrentProfileObj, null, true);
                    mEventsRecyclerView.setLayoutManager(new LinearLayoutManager(ViewProfileActivity.this));
                    mEventsRecyclerView.setAdapter(mEventsFindAdapter);
                } else {
                    mEventsFindAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initMenuFragment() {
        try {
            MenuParams menuParams = new MenuParams();
            menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
            menuParams.setMenuObjects(getMenuObjects());
            menuParams.setClosableOutside(true);
            menuParams.setAnimationDuration(100);
            mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
            mMenuDialogFragment.setItemClickListener(ViewProfileActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSearchProfilesAdapter() {
        ViewProfileActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                mOtherProfileAdapter = null;
                if (mSearchProfileAdapter == null) {
                    mSearchProfileAdapter = new FollowProfileAdapter(mSearchProfilesList, getCurrentProfile(), ViewProfileActivity.this);
                    mHorRecyclerView.setAdapter(mSearchProfileAdapter);
                } else {
                    mSearchProfileAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void getMyProfiles() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mMPSpinnerList.clear();
                Collections.addAll(mMPSpinnerList, getResources().getStringArray(R.array.empty_array));
                int mUserID = PreferenceUtils.getInstance(ViewProfileActivity.this).getIntData(PreferenceUtils.USER_ID);
                final String mFilter = "UserID = " + mUserID;
                RetrofitClient.getRetrofitInstance().callGetProfiles(ViewProfileActivity.this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);
            }
        });
    }

    private void getUpcomingEvents() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int status = AppConstants.EVENT_STATUS;
                final String mDateFilter = "(( Date <= " + getCurrentDate() + " ) AND ( Finish >= " + getCurrentDate() + " )) AND ( EventStatus=" + status + ")";
                RetrofitClient.getRetrofitInstance().callGetEvents(ViewProfileActivity.this, mDateFilter, RetrofitClient.GET_EVENTS_RESPONSE);
            }
        });
    }

    private void getOtherProfileList() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int mUserID = PreferenceUtils.getInstance(ViewProfileActivity.this).getIntData(PreferenceUtils.USER_ID);
                String mBlockedUsersID = Utility.getInstance().getMyBlockedUsersID(mCurrentProfileObj.getBlockedUserProfilesByProfileID(),
                        mCurrentProfileObj.getBlockeduserprofiles_by_BlockedProfileID());
                String mFilter = "(UserID != " + mUserID + ")";
                if (!mBlockedUsersID.trim().isEmpty())
                    mFilter = mFilter + " AND (ID NOT IN (" + mBlockedUsersID + "))";
                RetrofitClient.getRetrofitInstance().callAllOtherProfiles(ViewProfileActivity.this, mFilter,
                        RetrofitClient.GET_OTHER_PROFILES_RESPONSE, mDataLimit, mHorRvUnfollowOffset);
            }
        });
    }

    private void getPhoneEmailProfileList() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                /*if (mPhoneNumbers.trim().isEmpty() && mEmailIDs.trim().isEmpty())
                    return;*/
                JsonObject mJsonObject = new JsonObject();
                mJsonObject.addProperty("phonenumbers", mPhoneNumbers);
                mJsonObject.addProperty("emails", mEmailIDs);
                JsonObject mParameteres = new JsonObject();
                mParameteres.add("parameters", mJsonObject);
                RetrofitClient.getRetrofitInstance().callGetPhoneEmailProfiles(ViewProfileActivity.this, mParameteres, RetrofitClient.GET_PHONE_EMAIL_PROFILES_RESPONSE);
            }
        });
    }

    private void findFriendsOrVehicles(final String searchStr) {
        if (mFullMPList.size() > 0) {
            mSearchStr = searchStr;
            mSearchProfilesList.clear();
            mHorFindFriendsRvOffset = 0;
            mIsHorFindFriendsRvLoading = true;
            getSearchProfileList(RetrofitClient.PROFILE_FIND_FRIENDS_RESPONSE);
        }
    }

    private void getSearchProfileList(final int requestCode) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                final String mFilter;
                int mUserID = PreferenceUtils.getInstance(ViewProfileActivity.this).getIntData(PreferenceUtils.USER_ID);
                ProfileResModel mMyProfileResModel = mFullMPList.get(PreferenceUtils.getInstance(ViewProfileActivity.this)
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
                //setSearchProfilesAdapter();
                RetrofitClient.getRetrofitInstance().callGetSearchProfiles(ViewProfileActivity.this, mFilter, requestCode, mDataLimit, mHorFindFriendsRvOffset);

            }
        });
    }

    @OnClick({R.id.refresh_btn, R.id.my_moto_file_box, R.id.events_box, R.id.toolbar_back_img_btn, R.id.toolbar_settings_img_btn, R.id.toolbar_messages_img_btn, R.id.toolbar_notification_img_btn, R.id.profile_img, R.id.name_of_moto_tv,
            R.id.name_of_driver_tv, R.id.add_post_video, R.id.imageframe, R.id.news_and_media_box, R.id.bottom_live_box, R.id.remove_post_img_btn,
            R.id.post_btn, R.id.add_post_img, R.id.tag_profile_img, R.id.clubs_box, R.id.promoter_box, R.id.tracks_container,
            R.id.win_car_btn, R.id.performance_box, R.id.btn_write_post, R.id.btn_connect_with_friends})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refresh_btn:

                //Other profile
                mHorRvUnfollowOffset = 0;
                mIsHorRvLoading = true;
                mOtherProfilesList.clear();

                //Search other profile
                mSearchProfilesList.clear();
                mHorFindFriendsRvOffset = 0;
                mIsHorFindFriendsRvLoading = true;
                mPhoneEmailProfileList.clear();
                mIsPostsRvLoading = true;
                mPostsRvOffset = 0;

                if (isNetworkConnected(this))
                    getMyProfiles();
                else
                    Toast.makeText(ViewProfileActivity.this, getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
                break;
            case R.id.news_and_media_box:
                if (isNetworkConnected(this)) {
                    if (mFullMPList.size() > 0) {
                        /*startActivityForResult(new Intent(this, NewsAndMediaListActivity.class)
                                .putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mFullMPList.get(PreferenceUtils.getInstance(this)
                                        .getIntData(PreferenceUtils.CURRENT_PROFILE_POS))), AppConstants.FOLLOWERS_FOLLOWING_RESULT);*/
                        //MotoHub.getApplicationInstance().setmProfileResModel(mFullMPList.get(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));
                        EventBus.getDefault().postSticky(mFullMPList.get(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));
                        startActivityForResult(new Intent(ViewProfileActivity.this, NewsAndMediaListActivity.class), AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                    } else {
                        Toast.makeText(ViewProfileActivity.this, getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ViewProfileActivity.this, getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.clubs_box:
                if (isNetworkConnected(this)) {
                    if (mFullMPList.size() > 0) {
                        /*startActivityForResult(new Intent(this, ClubsListActivity.class)
                                .putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mFullMPList.get(PreferenceUtils.getInstance(this)
                                        .getIntData(PreferenceUtils.CURRENT_PROFILE_POS))), AppConstants.FOLLOWERS_FOLLOWING_RESULT);*/
                        /*MotoHub.getApplicationInstance().setmProfileResModel(mFullMPList.get(PreferenceUtils.getInstance(this)
                                .getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));*/
                        EventBus.getDefault().postSticky(mFullMPList.get(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));
                        startActivityForResult(new Intent(ViewProfileActivity.this, ClubsListActivity.class), AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                    } else {
                        //showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                        Toast.makeText(ViewProfileActivity.this, getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ViewProfileActivity.this, getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.promoter_box:
                if (isNetworkConnected(this)) {
                    if (mFullMPList.size() > 0) {
                        /*startActivityForResult(new Intent(this, PromotersListActivity.class)
                                .putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mFullMPList.get(PreferenceUtils.getInstance(this)
                                        .getIntData(PreferenceUtils.CURRENT_PROFILE_POS))), AppConstants.FOLLOWERS_FOLLOWING_RESULT);*/
                        /*MotoHub.getApplicationInstance().setmProfileResModel(mFullMPList.get(PreferenceUtils.getInstance(this)
                                .getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));*/
                        EventBus.getDefault().postSticky(mFullMPList.get(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));
                        startActivityForResult(new Intent(ViewProfileActivity.this, PromotersListActivity.class), AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                    } else {
                        //showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                        Toast.makeText(ViewProfileActivity.this, getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ViewProfileActivity.this, getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.toolbar_settings_img_btn:

                try {
                    if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                        if (!mMenuDialogFragment.isVisible())
                            mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.toolbar_messages_img_btn:
                if (mFullMPList.size() > 0) {
                    /*startActivity(new Intent(ViewProfileActivity.this, ChatHomeActivity.class)
                            .putExtra(ProfileModel.MY_PROFILE_RES_MODEL, getCurrentProfile()));*/
                    //MotoHub.getApplicationInstance().setmProfileResModel(getCurrentProfile());
                    EventBus.getDefault().postSticky(getCurrentProfile());
                    startActivity(new Intent(ViewProfileActivity.this, ChatHomeActivity.class));

                } else {
                    //showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                    Toast.makeText(ViewProfileActivity.this, getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.toolbar_notification_img_btn:
                if (!isNetworkConnected(this)) {
                    Toast.makeText(ViewProfileActivity.this, getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
                    //showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                    return;
                }
                if (mFullMPList.size() > 0) {
                    /*startActivityForResult(new Intent(this, NotificationActivity.class).putExtra(ProfileModel.MY_PROFILE_RES_MODEL,
                            getCurrentProfile()),
                            AppConstants.FOLLOWERS_FOLLOWING_RESULT);*/
                    //MotoHub.getApplicationInstance().setmProfileResModel(getCurrentProfile());
                    EventBus.getDefault().postSticky(getCurrentProfile());
                    startActivityForResult(new Intent(this, NotificationActivity.class),
                            AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                } else {
                    //showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                    Toast.makeText(ViewProfileActivity.this, getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.my_moto_file_box:
                if (isNetworkConnected(this)) {
                    if (mFullMPList.size() > 0) {
                        moveMyProfileScreenWithResult(this, 0, AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                    } /*else {
                        showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                    }*/
                } else {
                    Toast.makeText(ViewProfileActivity.this, getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bottom_live_box:
                if (isNetworkConnected(this)) {
                    if (mFullMPList.size() > 0) {
//                        startActivity(new Intent(this, ComingSoonActivity.class));
                        startMultiStream();
                        //TODO have to change it in future
                        //TODO showAppDialog(AppDialogFragment.BOTTOM_LIVE_STREAM_DIALOG, null);

                    }/* else {
                        showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                    }*/
                } else {
                    Toast.makeText(ViewProfileActivity.this, getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.events_box:
                if (isNetworkConnected(this)) {
                    if (mFullMPList.size() > 0) {
                        if (getCurrentProfile() != null) {
                            startActivity(new Intent(this, EventsHomeActivity.class)
                                    .putExtra(AppConstants.PROFILE_ID, getCurrentProfile().getID()));
                        }
                    }
                } else {
                    //showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                    Toast.makeText(ViewProfileActivity.this, getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.performance_box:
                if (isNetworkConnected(this)) {
                    if (mFullMPList.size() > 0) {
                        /*startActivityForResult(new Intent(this, PerformanceShopListActivity.class)
                                .putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mFullMPList.get(PreferenceUtils.getInstance(this)
                                        .getIntData(PreferenceUtils.CURRENT_PROFILE_POS))), AppConstants.FOLLOWERS_FOLLOWING_RESULT);*/
                        //MotoHub.getApplicationInstance().setmProfileResModel(mFullMPList.get(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));
                        EventBus.getDefault().postSticky(mFullMPList.get(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));
                        startActivityForResult(new Intent(this, PerformanceShopListActivity.class), AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                    }
                } else {
                    //showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                    Toast.makeText(ViewProfileActivity.this, getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.profile_img:
            case R.id.name_of_moto_tv:
            case R.id.name_of_driver_tv:
                if (isNetworkConnected(this)) {
                    if (mFullMPList.size() > 0) {
                        Bundle mBundle = new Bundle();
                        /*mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mFullMPList.get(PreferenceUtils
                                .getInstance
                                        (this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));*/
                        /*MotoHub.getApplicationInstance().setmProfileResModel(mFullMPList.get(PreferenceUtils
                                .getInstance
                                        (this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));*/
                        EventBus.getDefault().postSticky(mFullMPList.get(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));
                        startActivityForResult(new Intent(this, UpdateProfileActivity.class).putExtras(mBundle),
                                AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                    }
                } else {
                    //showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                    Toast.makeText(ViewProfileActivity.this, getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tracks_container:
                if (isNetworkConnected(this)) {
                    if (mFullMPList.size() > 0) {
                        //MotoHub.getApplicationInstance().setmProfileResModel(getCurrentProfile());
                        EventBus.getDefault().postSticky(getCurrentProfile());
                        Intent trackIntent = new Intent(ViewProfileActivity.this, TrackListActivity.class);
                        //trackIntent.putExtra(TrackListActivity.EXTRA_PROFILE_DATA, getCurrentProfile());
                        startActivityForResult(trackIntent, AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                    }
                } else {
                    Toast.makeText(ViewProfileActivity.this, getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_write_post:
                if (!mIsHorRvLoading)
                    if (isNetworkConnected(this)) {
                        Gson mGson = new Gson();
                        if (mFullMPList.size() > 0) {
                            //String mProfile = mGson.toJson(mCurrentProfileObj);
                            //MotoHub.getApplicationInstance().setmProfileResModel(mCurrentProfileObj);
                            EventBus.getDefault().postSticky(mCurrentProfileObj);
                            startActivityForResult(new Intent(ViewProfileActivity.this, WritePostActivity.class)
                                    /*.putExtra(AppConstants.MY_PROFILE_OBJ, mProfile)*/
                                    .putExtra(AppConstants.IS_NEWSFEED_POST, false)
                                    .putExtra(AppConstants.USER_TYPE, "user"), AppConstants.WRITE_POST_REQUEST);
                        } else {
                            showAppDialog(AppDialogFragment.ACCESS_CONTACT_ALERT_DIALOG, null);
                        }
                    } else
                        showToast(this, getString(R.string.internet_err));
                break;
            case R.id.btn_connect_with_friends:
                if (isNetworkConnected(this))
                    showAppDialog(AppDialogFragment.ACCESS_CONTACT_ALERT_DIALOG, null);
                else
                    showToast(this, getString(R.string.internet_err));
                break;

        }
    }


    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String dialogType, StringBuilder profileTypesStr,
                                            ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);
        switch (dialogType) {
            case AppDialogFragment.ALERT_OTHER_PROFILE_DIALOG:
                PreferenceUtils.getInstance(this).saveIntData(PreferenceUtils.CURRENT_PROFILE_POS, position);
                changeAndSetProfile(position);
                if (mSearchStr.isEmpty()) {
                    mOtherProfileAdapter = null;
                    setOtherProfilesAdapter();
                } else {
                    mSearchProfileAdapter = null;
                    setSearchProfilesAdapter();
                }
                if (mPhoneEmailSearchStr.isEmpty()) {
                    mPhoneEmailProfileAdapter = null;
                    setPhoneEmailProfilesAdapter();
                }
                getMyProfiles();
                break;
            case AppDialogFragment.BOTTOM_DELETE_DIALOG:
                mPostPos = position;
                if (mNewsFeedList.get(position).getUserType().equals(AppConstants.VIDEO_SHARED_POST) || mNewsFeedList.get(position).getUserType().equals(AppConstants.USER_VIDEO_SHARED_POST))
                    RetrofitClient.getRetrofitInstance().callDeleteVideoSharedPost(this, mNewsFeedList.get(position).getNewSharedPostID(), RetrofitClient.DELETE_VIDEO_SHARED_POST_RESPONSE);
                else
                    RetrofitClient.getRetrofitInstance().callDeleteSharedPost(this, mNewsFeedList.get(position).getNewSharedPostID(), RetrofitClient.DELETE_SHARED_POST_RESPONSE);
                break;
            case AppDialogFragment.BOTTOM_REPORT_ACTION_DIALOG:
                startActivityForResult(
                        new Intent(ViewProfileActivity.this, ReportActivity.class).putExtra(PostsModel.POST_ID, mNewsFeedList.get(position).getID()).putExtra(ProfileModel.PROFILE_ID, mFullMPList.get(getProfileCurrentPos()).getID()).putExtra(ProfileModel.USER_ID, mFullMPList.get(getProfileCurrentPos()).getUserID()).putExtra(AppConstants.REPORT, AppConstants.REPORT_POST),
                        AppConstants.REPORT_POST_SUCCESS);
                break;
            case AppDialogFragment.BOTTOM_EDIT_DIALOG:
                Bundle mBundle = new Bundle();
                //MotoHub.getApplicationInstance().setmProfileResModel(mCurrentProfileObj);
                EventBus.getDefault().postSticky(mCurrentProfileObj);
                mBundle.putSerializable(PostsModel.POST_MODEL, mNewsFeedList.get(position));
                /*mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mCurrentProfileObj);*/
                startActivityForResult(
                        new Intent(ViewProfileActivity.this, PostEditActivity.class).putExtras(mBundle),
                        AppConstants.POST_UPDATE_SUCCESS);
                /*MotoHub.getApplicationInstance().setmProfileResModel(mCurrentProfileObj);
                MotoHub.getApplicationInstance().setmPostResModel(mNewsFeedList.get(position));
                startActivityForResult(
                        new Intent(ViewProfileActivity.this, PostEditActivity.class),
                        AppConstants.POST_UPDATE_SUCCESS);*/
                break;
            case AppDialogFragment.ALERT_EXIT_DIALOG:
                setResult(Activity.RESULT_CANCELED);
                finishAffinity();
                break;
            case AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG:
                getMyProfiles();
                break;
            case AppDialogFragment.BOTTOM_SHARE_DIALOG:
                mCurrentPostPosition = position;
                DialogManager.showShareDialogWithCallback(this, mShareTextWithPostInterface);
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
                break;
            case AppDialogFragment.ACCESS_CONTACT_ALERT_DIALOG:
                connectWithFriends();
                break;
            case AppDialogFragment.SET_CONTACT_PERMISSION:
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                finish();
                break;
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
        if (isNetworkConnected(this)) {
            int reqProfileID = getCurrentProfile().getID();
            MotoHub.getApplicationInstance().setProfileId(reqProfileID);
            Intent intent = new Intent(this, OnDemandActivity.class);
            startActivity(intent);
        } else {
            showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
        }
    }


    public void connectWithFriends() {
        if (PreferenceUtils.getInstance(this).getBooleanData(PreferenceUtils.IS_PERMANATLY_DENIED_CONTACT_PERMISSION)) {
            showAppDialog(AppDialogFragment.SET_CONTACT_PERMISSION, null);
        } else {
            readContacts();
        }
    }


    private boolean isContactPermissoinAdded() {
        boolean addPermission = true;
        int readContactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (readContactPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_CONTACTS);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            addPermission = isPermission(listPermissionsNeeded);
        }
        return addPermission;
    }

    private boolean isPermission(List<String> camera) {
        listPermissionsNeeded = new ArrayList<>();
        listPermissionsNeeded.addAll(camera);

        if (!listPermissionsNeeded.isEmpty()) {
            int permsRequestCode = 200;
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), permsRequestCode);
            return false;
        }

        return true;
    }

    public void readContacts() {
        if (isContactPermissoinAdded()) {
            PreferenceUtils.getInstance(getApplicationContext()).saveBooleanData(PreferenceUtils.IS_CONTACT_PERMISSION_ADDED, true);
            mBtnConnectWithFriends.setVisibility(View.GONE);
            new MyTask().execute();
        } else {
            try {
                mShimmerView_phonecontacts.stopShimmerAnimation();
                mShimmerView_phonecontacts.setVisibility(View.GONE);
                mRelativeLayoutPhoneEmailFriends.setVisibility(View.GONE);
                showToolbarBtn(mToolbar, R.id.toolbar_settings_img_btn);
                showToolbarBtn(mToolbar, R.id.toolbar_messages_img_btn);
                showToolbarBtn(mToolbar, R.id.toolbar_notification_img_btn);
                mBtnConnectWithFriends.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void changeAndSetProfile(int position) {
        try {
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
            mShimmerView_profiles.stopShimmerAnimation();
            mShimmerView_profiles.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RetrofitClient.UPDATE_FEED_COUNT:
                    assert data.getExtras() != null;
                    mNewsFeedAdapter.updateView(data.getIntExtra(AppConstants.POSITION, 0));
                    break;
                case EventsFindAdapter.EVENT_PAYMENT_REQ_CODE:
                    mEventsFindAdapter.onActivityResult(requestCode, resultCode, data);
                    break;
                case EventsFindAdapter.EVENT_QUESTIONS_REQ_CODE:
                    mEventsFindAdapter.onActivityResult(requestCode, resultCode, data);
                    break;
                case EventsFindAdapter.EVENT_LIVE_PAYMENT_REQ_CODE:
                    mEventsFindAdapter.onActivityResult(requestCode, resultCode, data);
                    break;

                case AppConstants.POST_UPDATE_SUCCESS:
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
                    mHorRvUnfollowOffset = 0;
                    mPhoneEmailProfileList.clear();
                    mShimmerView_phonecontacts.setVisibility(View.VISIBLE);
                    mShimmerView_phonecontacts.startShimmerAnimation();
                    getMyProfiles();
                    break;
                case AppConstants.POST_COMMENT_REQUEST:
                    assert data.getExtras() != null;
                    ArrayList<FeedCommentModel> mFeedCommentModel = (ArrayList<FeedCommentModel>) data.getExtras().getSerializable(PostsModel.COMMENTS_BY_POSTID);
                    mNewsFeedAdapter.refreshCommentList(mFeedCommentModel);
                    break;
                case AppConstants.REPORT_POST_SUCCESS:
                    //TODO remove the reported post
                    mHorRvUnfollowOffset = 0;
                    mPhoneEmailProfileList.clear();
                    getMyProfiles();
                    break;
                case AppConstants.WRITE_POST_REQUEST:
                    mHorRvUnfollowOffset = 0;
                    mPhoneEmailProfileList.clear();
                    getMyProfiles();
                    break;
                default:
                    mHorRvUnfollowOffset = 0;
                    mPhoneEmailProfileList.clear();
                    getMyProfiles();
                    break;
            }
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof ProfileModel) {
            ProfileModel mProfileModel = (ProfileModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_PROFILE_RESPONSE:
                    if (mProfileModel.getResource().size() > 0) {
                        mFullMPList.clear();
                        mFullMPList.addAll(mProfileModel.getResource());
                        if (isExtraProfile) {
                            isExtraProfile = false;
                            PreferenceUtils.getInstance(this).saveIntData(PreferenceUtils.CURRENT_PROFILE_POS, (mFullMPList.size() - 1));
                            mCurrentProfileObj = getCurrentProfile();
                            if (mSearchStr.isEmpty()) {
                                mOtherProfileAdapter = null;
                                setOtherProfilesAdapter();
                            } else {
                                mSearchProfileAdapter = null;
                                setSearchProfilesAdapter();
                            }
                            if (mPhoneEmailSearchStr.isEmpty()) {
                                mPhoneEmailProfileAdapter = null;
                                setPhoneEmailProfilesAdapter();
                            }
                        }
                        mCurrentProfileObj = getCurrentProfile();
                        MotoHub.getApplicationInstance().setFullProfileList(mFullMPList);
                        //MotoHub.getApplicationInstance().setmProfileResModel(getCurrentProfile());
                        EventBus.getDefault().postSticky(getCurrentProfile());
                        ArrayList<String> mTempMPSpinnerList = new ArrayList<>();
                        for (ProfileResModel mProfileResModel : mFullMPList) {
                            if (Utility.getInstance().isSpectator(mProfileResModel)) {
                                mTempMPSpinnerList.add(mProfileResModel.getSpectatorName());
                            } else {
                                mTempMPSpinnerList.add(mProfileResModel.getMotoName());
                            }
                        }
                        refreshFeeds(mCurrentProfileObj);
                        getOtherProfileList();
                        mMPSpinnerList.clear();
                        mMPSpinnerList.addAll(mTempMPSpinnerList);
                        changeAndSetProfile(getProfileCurrentPos());
                        getUpcomingEvents();
                        if (mIsFirstLaunch) {
                            setActiveCount(mFullMPList.get(0).getUserID(), mFullMPList.get(0).getmActiveUserCount());
                        }
                    } else {
                        showSnackBar(mCoordinatorLayout, mNoProfileErr);
                    }
                    break;
                case RetrofitClient.USER_COUNT:
                    setmIsFirstLaunch(false);
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
                case RetrofitClient.GET_PHONE_EMAIL_PROFILES_RESPONSE:
                    try {
                        showToolbarBtn(mToolbar, R.id.toolbar_settings_img_btn);
                        showToolbarBtn(mToolbar, R.id.toolbar_messages_img_btn);
                        showToolbarBtn(mToolbar, R.id.toolbar_notification_img_btn);
                        mShimmerView_phonecontacts.stopShimmerAnimation();
                        mShimmerView_phonecontacts.setVisibility(View.GONE);
                        mRelativeLayoutPhoneEmailFriends.setVisibility(View.GONE);
                        if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                            mRelativeLayoutPhoneEmailFriends.setVisibility(View.VISIBLE);
                            mPhoneEmailProfileList.clear();
                            mAllPhoneEmailProfileList.clear();
                            ArrayList<ProfileResModel> mResList = removeFollowBlockedProfiles(mProfileModel.getResource());
                            if (mResList.isEmpty()) {
                                mRelativeLayoutPhoneEmailFriends.setVisibility(View.GONE);
                            } else {
                                mPhoneEmailProfileList.clear();
                                mPhoneEmailProfileList.addAll(mResList);
                                mAllPhoneEmailProfileList.addAll(mPhoneEmailProfileList);
                                setPhoneEmailProfilesAdapter();
                            }
                        } else {
                            mRelativeLayoutPhoneEmailFriends.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case RetrofitClient.PROFILE_FIND_FRIENDS_RESPONSE:
                    try {
                        if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                            mHorFindFriendsRvTotalCount = mProfileModel.getMeta().getCount();
                            mIsHorFindFriendsRvLoading = false;
                            //mSearchProfilesList.clear();
                            if (mHorFindFriendsRvOffset == 0) {
                                mSearchProfilesList.clear();
                            }
                            if (isSearched) {
                                isSearched = false;
                                mSearchProfilesList.addAll(mProfileModel.getResource());
                            }
                            mHorFindFriendsRvOffset = mHorFindFriendsRvOffset + mDataLimit;
                        } else {
                            if (mHorFindFriendsRvOffset == 0) {
                                mHorFindFriendsRvTotalCount = 0;
                                showToast(this, getString(R.string.no_profile_found_to_follow_err));
                            }
                        }
                        mShimmerView_otherprofiles.stopShimmerAnimation();
                        mShimmerView_otherprofiles.setVisibility(View.GONE);
                        setSearchProfilesAdapter();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case RetrofitClient.PROFILE_FIND_FRIENDS_LOAD_MORE_RESPONSE:
                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        mHorFindFriendsRvTotalCount = mProfileModel.getMeta().getCount();
                        mIsHorFindFriendsRvLoading = false;
                        mSearchProfilesList.clear();
                        /*if (mHorFindFriendsRvOffset == 0) {
                            mSearchProfilesList.clear();
                        }*/
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
                case RetrofitClient.GET_OTHER_PROFILES_RESPONSE:
                    try {
                        if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                            mHorRvUnfollowTotalCount = mProfileModel.getMeta().getCount();
                            mHorRvOtherUsersCount = mHorRvUnfollowTotalCount;
                            mIsHorRvLoading = false;
                            if (mHorRvUnfollowOffset == 0) {
                                mOtherProfilesList.clear();
                                //mHorRvUnfollowOffset = mOtherProfilesList.size();
                            }

                            mOtherProfilesList.addAll(mProfileModel.getResource());
                            mHorRvUnfollowOffset = mOtherProfilesList.size();
                            setOtherProfilesAdapter();
                        } else {
                            if (mHorRvUnfollowOffset == 0) {
                                mHorRvUnfollowTotalCount = 0;
                            }
                        }
                        mToolbar.setVisibility(View.VISIBLE);
                        mShimmerView_otherprofiles.stopShimmerAnimation();
                        mShimmerView_otherprofiles.setVisibility(View.GONE);
                        if (mOtherProfilesList.size() == 0) {
                            mSearchLay.setVisibility(View.GONE);
                            mSearchView.setVisibility(View.GONE);
                            mUnfollowersView.setVisibility(View.GONE);
                            mHorRecyclerView.setVisibility(View.GONE);
                        }
                        if (mPhoneNumbers.trim().isEmpty() && mEmailIDs.trim().isEmpty())
                            readContacts();
                        else
                            getPhoneEmailProfileList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case RetrofitClient.GET_OTHER_PROFILES_FOLLOW_RESPONSE:
                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        if (mHorRvfollowTotalCount == 0) {
                            mHorRvfollowTotalCount = mProfileModel.getMeta().getCount();
                            mHorRvOtherUsersCount = mHorRvOtherUsersCount + mHorRvfollowTotalCount;
                        }
                        mIsHorRvLoading = false;
                        mOtherProfilesList.addAll(mProfileModel.getResource());
                    }
                    break;
            }
        }
        if (responseObj instanceof FollowProfileModel) {
            FollowProfileModel mResponse = (FollowProfileModel) responseObj;
            if (responseType == RetrofitClient.FOLLOW_PROFILE_RESPONSE) {
                if (mResponse.getResource().size() > 0) {
                    ArrayList<FollowProfileEntity> mFollowList = mCurrentProfileObj.getFollowprofile_by_ProfileID();
                    mFollowList.add(mResponse.getResource().get(0));
                    mCurrentProfileObj.setFollowprofile_by_ProfileID(mFollowList);
                    if (mOtherProfileAdapter != null)
                        mOtherProfileAdapter.updateFollowProfile(mResponse.getResource().get(0));
                    else if (mSearchProfileAdapter != null)
                        mSearchProfileAdapter.updateFollowProfile(mResponse.getResource().get(0));
                    if (mPhoneEmailProfileAdapter != null) {
                        mPhoneEmailProfileAdapter.updateUnFollowProfile(mResponse.getResource().get(0));
                    }
                }
            } else if (responseType == RetrofitClient.UN_FOLLOW_PROFILE_RESPONSE) {
                if (mResponse.getResource().size() > 0) {
                    ArrayList<FollowProfileEntity> mFollowList = mCurrentProfileObj.getFollowprofile_by_ProfileID();
                    if (mFollowList.contains(mResponse.getResource().get(0))) {
                        mFollowList.remove(mResponse.getResource().get(0));
                    }
                    mCurrentProfileObj.setFollowprofile_by_ProfileID(mFollowList);
                    if (mHorPhoneEmailFriendsRecyclerView.getVisibility() == View.GONE)
                        mHorPhoneEmailFriendsRecyclerView.setVisibility(View.VISIBLE);
                    if (mOtherProfileAdapter != null) {
                        mOtherProfileAdapter.updateUnFollowProfile(mResponse.getResource().get(0));
                    } else if (mSearchProfileAdapter != null)
                        mSearchProfileAdapter.updateUnFollowProfile(mResponse.getResource().get(0));
                    if (mPhoneEmailProfileAdapter != null) {
                        getPhoneEmailProfileList();
                    }
                }
            }
        } else if (responseObj instanceof PostsModel) {
            PostsModel mPostsModel = (PostsModel) responseObj;
            String data = new Gson().toJson(mPostsModel);
            switch (responseType) {
                case RetrofitClient.GET_FEED_POSTS_RESPONSE:
                    try {
                        mShimmerView_posts.stopShimmerAnimation();
                        mShimmerView_posts.setVisibility(View.GONE);
                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            mPostsRvTotalCount = mPostsModel.getMeta().getCount();
                            mIsPostsRvLoading = false;
                            if (mPostsRvOffset == 0) {
                                mNewsFeedList.clear();
                                newsFeedRecyclerView.setVisibility(View.VISIBLE);
                            }
                            mNewsFeedList.addAll(mPostsModel.getResource());
                            mPostsRvOffset = mPostsRvOffset + mDataLimit;
                            setPostAdapter();
                            mNewsFeedAdapter.refreshProfilePos();
                            postNestedScrollView.setNestedScrollingEnabled(true);
                        } else {
                            if (mPostsRvOffset == 0) {
                                mPostsRvTotalCount = 0;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE:
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        mNewsFeedList.remove(mPostPos);
                        mPostsRvTotalCount -= 1;
                        setPostAdapter();
                        mNewsFeedAdapter.refreshProfilePos();
                        showSnackBar(mCoordinatorLayout, getResources().getString(R.string.post_delete));
                    }
                    break;
                case RetrofitClient.SHARED_POST_RESPONSE:
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
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
                case RetrofitClient.FEED_VIDEO_COUNT:
                    try {
                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            mNewsFeedAdapter.addViewCount(mPostsModel.getResource().get(0).getmViewCount());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case RetrofitClient.ADD_FEED_COUNT:
                    try {
                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            mNewsFeedAdapter.ViewCount(mPostsModel.getResource().get(0).getmViewCount());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else if (responseObj instanceof FeedCommentModel) {
            FeedCommentModel mFeedCommentList = (FeedCommentModel) responseObj;
            switch (responseType) {
                case RetrofitClient.POST_COMMENTS:
                    ArrayList<FeedCommentModel> mNewFeedComment = mFeedCommentList.getResource();
                    mNewsFeedAdapter.resetCommentAdapter(mNewFeedComment.get(0));
                    break;
            }
        } else if (responseObj instanceof NotificationBlockedUsersModel) {
            NotificationBlockedUsersModel mNotify = (NotificationBlockedUsersModel) responseObj;
            ArrayList<NotificationBlockedUsersModel> mPostNotification = mNotify.getResource();
            switch (responseType) {
                case RetrofitClient.BLOCK_NOTIFY:
                    if (mPostNotification.size() > 0)
                        mNewsFeedAdapter.resetBlock(mPostNotification.get(0));
                    break;
                case RetrofitClient.UNBLOCK_NOTIFY:
                    if (mPostNotification.size() > 0)
                        mNewsFeedAdapter.resetUnBlock(mPostNotification.get(0));
                    break;
            }
        } else if (responseObj instanceof FeedLikesModel) {
            FeedLikesModel mFeedLikesList = (FeedLikesModel) responseObj;
            ArrayList<FeedLikesModel> mNewFeedLike = mFeedLikesList.getResource();
            switch (responseType) {
                case RetrofitClient.POST_LIKES:
                    if (mNewFeedLike.size() > 0)
                        mNewsFeedAdapter.resetLikeAdapter(mNewFeedLike.get(0));
                    break;
                case RetrofitClient.POST_UNLIKE:
                    if (mNewFeedLike.size() > 0)
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
        } else if (responseObj instanceof EventsWhoIsGoingModel) {
            EventsWhoIsGoingModel mEventsWhoIsGoingModel = (EventsWhoIsGoingModel) responseObj;
            if (mEventsWhoIsGoingModel.getResource() != null && mEventsWhoIsGoingModel.getResource().size() > 0) {
                mEventsFindAdapter.bookAnEventSuccess(mEventsWhoIsGoingModel.getResource().get(0));
                mEventsFindAdapter.callPostRacingData();
            }
        } else if (responseObj instanceof PaymentModel) {
            PaymentModel mResponse = (PaymentModel) responseObj;
            if (mResponse.getStatus() != null && mResponse.getStatus().equals("succeeded")) {
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
            else {
                /*if (this.mPurchaseSuccessDialog != null)
                    this.mPurchaseSuccessDialog.show();*/
                if (mEventsFindAdapter.mEventType.equals(AppConstants.FREE_EVENT)) {
                    showSnackBar(mCoordinatorLayout, "Successfully booked an event.");
                } else {
                    if (mPurchaseSuccessDialog != null)
                        mPurchaseSuccessDialog.show();
                }
            }
        } else if (responseObj instanceof EventAnswersModel) {
            EventAnswersModel mEventAnswerList = (EventAnswersModel) responseObj;
            ArrayList<EventAnswersModel> mResEventAnswerModel = mEventAnswerList.getResource();
            mEventsFindAdapter.callUpdateEventAnswerScreen(mResEventAnswerModel);
        } else if (responseObj instanceof PurchasedAddOnModel) {
            /*if (this.mPurchaseSuccessDialog != null)
                this.mPurchaseSuccessDialog.show();*/
            if (mEventsFindAdapter.mEventType.equals(AppConstants.FREE_EVENT)) {
                showSnackBar(mCoordinatorLayout, "Successfully booked an event.");
            } else {
                if (mPurchaseSuccessDialog != null)
                    mPurchaseSuccessDialog.show();
            }
        } else if (responseObj instanceof SessionModel) {

            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            getMyProfiles();
        } else if (responseObj instanceof FeedShareModel) {
            FeedShareModel mFeedShareList = (FeedShareModel) responseObj;
            ArrayList<FeedShareModel> mNewFeedShare = mFeedShareList.getResource();
            switch (responseType) {
                case RetrofitClient.POST_SHARES:
                    mSharedFeed = mNewFeedShare.get(0);
                    CommonAPI.getInstance().callAddSharedPost(ViewProfileActivity.this, mNewsFeedList.get(mCurrentPostPosition), mCurrentProfileObj, mShareTxt);
                    break;
                case RetrofitClient.DELETE_SHARED_POST_RESPONSE:
                    RetrofitClient.getRetrofitInstance().callDeleteProfilePosts(this, mNewsFeedList.get(mCurrentPostPosition).getID(), RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE);
                    break;
            }
        } else if (responseObj instanceof ImageModel) {
            ImageModel mImageModel = (ImageModel) responseObj;
            switch (responseType) {
                case RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE:
                    String imgUrl = getHttpFilePath(mImageModel.getmModels().get(0).getPath());
                    profilePostContent(imgUrl);
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
        } else if (responseObj instanceof VideoShareModel) {
            RetrofitClient.getRetrofitInstance().callDeleteProfilePosts(this, mNewsFeedList.get(mCurrentPostPosition).getID(), RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE);
        }
    }

    private ArrayList<ProfileResModel> removeFollowBlockedProfiles(ArrayList<ProfileResModel> phoneEmailProfileList) {
        ArrayList<ProfileResModel> final_phoneEmailProfileList = new ArrayList<ProfileResModel>();
        for (int i = 0; i < phoneEmailProfileList.size(); i++) {
            if (Utility.getInstance()
                    .isAlreadyFollowed(phoneEmailProfileList.get(i).getFollowprofile_by_FollowProfileID(), mCurrentProfileObj.getID())) {
//                phoneEmailProfileList.remove(i);
            } else if (Utility.getInstance().
                    isAlreadyBlocked(mCurrentProfileObj.getBlockedUserProfilesByProfileID(), phoneEmailProfileList.get(i).getID())) {
//                phoneEmailProfileList.remove(i);
            } else {
                final_phoneEmailProfileList.add(phoneEmailProfileList.get(i));
            }
        }
        return final_phoneEmailProfileList;
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);

        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else if (code == RetrofitClient.GET_FEED_POSTS_RESPONSE) {
            mPostsRvTotalCount = 0;
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mCoordinatorLayout, mErrorMsg);
        }
    }

    @Override
    public void retrofitOnError(int code, String message, int responseType) {
        if (message.equals("Unauthorized") || code == 401) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    RetrofitClient.getRetrofitInstance().callUpdateSession(ViewProfileActivity.this, RetrofitClient.UPDATE_SESSION_RESPONSE);
                }
            });
            thread.start();
        }
        if (code == RetrofitClient.GET_PHONE_EMAIL_PROFILES_RESPONSE) {
            try {
                mRelativeLayoutPhoneEmailFriends.setVisibility(View.GONE);
                mShimmerView_phonecontacts.stopShimmerAnimation();
                mShimmerView_phonecontacts.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        return mSearchStr.isEmpty() ? mHorRvUnfollowTotalCount : mHorFindFriendsRvTotalCount;
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
    }

    @Override
    public void onBackPressed() {
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else {
            showAppDialog(AppDialogFragment.ALERT_EXIT_DIALOG, null);
        }

    }

    @Override
    public int getTotalPostsResultCount() {
        return mPostsRvTotalCount;
    }

    private void setActiveCount(int ID, int ActiveUserCount) {
        if (mCurrentProfileObj != null) {
            int new_count = ActiveUserCount + 1;
            JsonArray jsonArray = new JsonArray();
            JsonObject jsonobj = new JsonObject();
            jsonobj.addProperty(ProfileModel.ID, mCurrentProfileObj.getID());
            jsonobj.addProperty(ProfileModel.USER_ID, ID);
            jsonobj.addProperty(ProfileModel.ActiveUserCount, new_count);
            jsonArray.add(jsonobj);
            RetrofitClient.getRetrofitInstance().setActiveUserCount(this, jsonArray, RetrofitClient.USER_COUNT);
        }
    }

    private void profilePostContent(String postImgFilePath) {

        try {
            if (getCurrentProfile() != null) {
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
            } else {
                Toast.makeText(ViewProfileActivity.this, getResources().getString(R.string.try_again), Toast.LENGTH_LONG).show();
            }

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
        mCurrentProfileObj = mProfileObj;
        mPostsRvTotalCount = -1;
        mPostsRvOffset = 0;
        mNewsFeedList.clear();
        setPostAdapter();
        getNewsFeedPosts();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        /*if (mCollapsingToolbarLay.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(mCollapsingToolbarLay)) {
            mSwipeRefreshLay.setEnabled(false);
        } else {
            mSwipeRefreshLay.setEnabled(true);
        }*/
    }

    private void getNewsFeedPosts() {
        /*mCurrentProfileObj.getID()*/
        RetrofitClient.getRetrofitInstance().callGetAllPosts(
                ViewProfileActivity.this,
                mCurrentProfileObj.getID(),
                RetrofitClient.GET_FEED_POSTS_RESPONSE,
                mDataLimit,
                mPostsRvOffset);

    }

    private void setPostAdapter() {

        ViewProfileActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mNewsFeedAdapter == null && mCurrentProfileObj.getID() != 0) {
                    mNewsFeedAdapter = new PostsAdapter(mNewsFeedList, mCurrentProfileObj, ViewProfileActivity.this, false);
                    newsFeedRecyclerView.setAdapter(mNewsFeedAdapter);
                } else {
                    mNewsFeedAdapter.notifyDataSetChanged();
                }
            }
        });
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

            Intent mResultIntent;

            JSONObject mDetailsObj = mEntryObj.getJSONObject("Details");
            switch (mEntryObj.getString("Type")) {

                case "LIVE_STREAM":
                    mResultIntent = new Intent(this, ViewLiveVideoViewScreen2.class);
                    mResultIntent.putExtra(AppConstants.PROFILE_ID, mDetailsObj.getInt(APIConstants.StreamProfileID));
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "LIVE_REQUEST":
                    mResultIntent = new Intent(this, ViewRequestUsersActivity.class);
                    mResultIntent.putExtra(AppConstants.PROFILE_ID, mDetailsObj.getInt(APIConstants.StreamProfileID));
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "LIVE_ACCEPT":
                    mResultIntent = new Intent(this, ViewStreamUsersActivity.class);
                    mResultIntent.putExtra(AppConstants.PROFILE_ID, mDetailsObj.getInt(APIConstants.StreamProfileID));
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "FOLLOW":
                    mResultIntent = new Intent(this, OthersMotoFileActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "FOLLOWER_POST":
                case "TAGGED":
                    mResultIntent = new Intent(this, PostViewActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "COMMENT_LIKE":
                    mResultIntent = new Intent(this, PostCommentLikeViewActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "COMMENT_REPLY_LIKE":
                    mResultIntent = new Intent(this, CommentReplyActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "TAGGED_COMMENT_REPLY":
                case "COMMENT_REPLY":
                    mResultIntent = new Intent(this, CommentReplyActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "TAGGED_POST_COMMENTS":
                case "POST_COMMENTS":
                case "POST_LIKES":
                    mResultIntent = new Intent(this, PostViewActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "VIDEO_SHARE":
                    mResultIntent = new Intent(this, PostViewActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "POST":
                case "POST_SHARE":
                    mResultIntent = new Intent(this, PostViewActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "VEHICLE_LIKE":
                    mResultIntent = new Intent(this, UpdateProfileActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "EVENT_CREATION":
                    mResultIntent = new Intent(this, NotificationEventCreatedActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "VIDEO_COMMENT_LIKE":
                    mResultIntent = new Intent(this, VideoCommentsActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "TAGGED_VIDEO_COMMENT_REPLY":
                case "VIDEO_COMMENT_REPLY":
                    mResultIntent = new Intent(this, VideoCommentReplyActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "VIDEO_COMMENT_REPLY_LIKE":
                    mResultIntent = new Intent(this, VideoCommentReplyActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "TAGGED_POST_VIDEO_COMMENTS":
                case "VIDEO_COMMENTS":
                case "VIDEO_LIKES":
                    mResultIntent = new Intent(this, PromoterVideoGalleryActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "EVENT_CHAT":
                    mResultIntent = new Intent(this, ChatBoxEventGrpActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "EVENT_LIVE_CHAT":
                    mResultIntent = new Intent(this, EventLiveActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "GROUP_CHAT_MSG":
                    mResultIntent = new Intent(this, ChatBoxGroupActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
                case "SINGLE_CHAT":
                    mResultIntent = new Intent(this, ChatBoxSingleActivity.class);
                    mResultIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
                    mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
                    startActivity(mResultIntent);
                    getIntent().removeExtra(IS_FROM_NOTIFICATION_TRAY);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMenuItemClick(View view, int position) {
        try {
            switch (position) {
                case 1:
                    if (mOtherProfilesList.size() > 0) {
                        Intent intent = new Intent(this, CreateProfileActivity.class);
                        intent.putExtra(CREATE_PROF_AFTER_REG, true);
                        intent.putExtra(AppConstants.TAG, TAG);
                        startActivityForResult(intent, AppConstants.CREATE_PROFILE_RES);
                    }
                    break;
                case 2:
                    if (mOtherProfilesList.size() > 0) {
                        showAppDialog(AppDialogFragment.ALERT_OTHER_PROFILE_DIALOG, mMPSpinnerList);
                    } else {
                        showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                    }
                    break;
                case 3:
                    if (mOtherProfilesList.size() > 0) {
                        startActivity(new Intent(ViewProfileActivity.this, NotificationSettingsActivity.class));
                    }
                    break;
                case 4:
                    if (mOtherProfilesList.size() > 0) {
                        if (getCurrentProfile().getBlockedUserProfilesByProfileID().size() > 0) {
                            /*startActivityForResult(new Intent(this, BlockedUsersActivity.class).putExtra(ProfileModel.MY_PROFILE_RES_MODEL,
                                    getCurrentProfile()),
                                    AppConstants.FOLLOWERS_FOLLOWING_RESULT);*/
                            //MotoHub.getApplicationInstance().setmProfileResModel(getCurrentProfile());
                            EventBus.getDefault().postSticky(getCurrentProfile());
                            startActivityForResult(new Intent(this, BlockedUsersActivity.class),
                                    AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                        } else {
                            showToast(ViewProfileActivity.this, getString(R.string.no_blocked_users_found_err));
                        }
                    } else {
                        showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                    }
                    break;
                case 5:
                    if (mOtherProfilesList.size() > 0) {
                        startActivity(new Intent(this, PaymentActivity.class).putExtra(AppConstants.CARD_SETTINGS, true));
                    }
                    break;
                case 6:
                    if (mOtherProfilesList.size() > 0) {
                        showAppDialog(AppDialogFragment.LOG_OUT_DIALOG, null);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200: {
                Map<String, Integer> perms = new HashMap<>();
                if (listPermissionsNeeded != null && listPermissionsNeeded.size() > 0) {
                    for (int i = 0; i < listPermissionsNeeded.size(); i++) {
                        perms.put(listPermissionsNeeded.get(i), PackageManager.PERMISSION_GRANTED);
                    }
                    if (grantResults.length > 0) {
                        for (int i = 0; i < permissions.length; i++)
                            perms.put(permissions[i], grantResults[i]);
                        for (int j = 0; j < listPermissionsNeeded.size(); j++) {
                            if (perms.get(listPermissionsNeeded.get(j)) == PackageManager.PERMISSION_GRANTED) {
                                PreferenceUtils.getInstance(getApplicationContext()).saveBooleanData(PreferenceUtils.IS_PERMANATLY_DENIED_CONTACT_PERMISSION, false);
                                PreferenceUtils.getInstance(getApplicationContext()).saveBooleanData(PreferenceUtils.IS_CONTACT_PERMISSION_ADDED, true);
                                readContacts();
                            } else {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(this, listPermissionsNeeded.get(j))) {
                                    PreferenceUtils.getInstance(getApplicationContext()).saveBooleanData(PreferenceUtils.IS_PERMANATLY_DENIED_CONTACT_PERMISSION, true);
                                    PreferenceUtils.getInstance(getApplicationContext()).saveBooleanData(PreferenceUtils.IS_CONTACT_PERMISSION_ADDED, false);
                                    readContacts();
                                    break;
                                } else {
                                    //permission is denied (and never ask again is  checked)
                                    //shouldShowRequestPermissionRationale will return false
                                    if (perms.get(listPermissionsNeeded.get(j)) == PackageManager.PERMISSION_DENIED) {

                                        PreferenceUtils.getInstance(getApplicationContext()).saveBooleanData(PreferenceUtils.IS_CONTACT_PERMISSION_ADDED, false);
                                        PreferenceUtils.getInstance(getApplicationContext()).saveBooleanData(PreferenceUtils.IS_PERMANATLY_DENIED_CONTACT_PERMISSION, true);

                                    } else {

                                        PreferenceUtils.getInstance(getApplicationContext()).saveBooleanData(PreferenceUtils.IS_PERMANATLY_DENIED_CONTACT_PERMISSION, false);
                                        PreferenceUtils.getInstance(getApplicationContext()).saveBooleanData(PreferenceUtils.IS_CONTACT_PERMISSION_ADDED, true);

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void hidePhoneEmailLayout() {
        mRelativeLayoutPhoneEmailFriends.setVisibility(View.GONE);
    }

    @SuppressLint("StaticFieldLeak")
    private class MyTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                ContentResolver cr = getContentResolver();
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

                assert cur != null;
                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            System.out.println("name : " + name + ", ID : " + id);

                            // get the phone number
                            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{id}, null);
                            assert pCur != null;
                            while (pCur.moveToNext()) {
                                String trimStr = (pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                                trimStr = trimStr.replaceAll("[^0-9]", "");
                                if (mCurrentProfileObj != null && mCurrentProfileObj.getPhone() != null) {
                                    if (!trimStr.isEmpty() && !trimStr.equalsIgnoreCase(mCurrentProfileObj.getPhone())) {
                                        if (mPhoneNumbers.trim().isEmpty()) {
                                            mPhoneNumbers = trimStr;
                                        } else {
                                            mPhoneNumbers = mPhoneNumbers + "," + trimStr;
                                        }
                                    }
                                }
                            }
                            pCur.close();
                            // get email and type
                            Cursor emailCur = cr.query(
                                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                    new String[]{id}, null);
                            assert emailCur != null;
                            while (emailCur.moveToNext()) {
                                // This would allow you get several email addresses
                                // if the email addresses were stored in an array
                                String mEmailIds = (emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))).trim();
                                mEmailIds = mEmailIds.replaceAll(" ", "");
                                if (mCurrentProfileObj != null && mCurrentProfileObj.getEmail() != null) {
                                    if (!mEmailIds.isEmpty() && !mEmailIds.equalsIgnoreCase(mCurrentProfileObj.getEmail())) {
                                        if (mEmailIDs.trim().isEmpty()) {
                                            mEmailIDs = mEmailIds;
                                        } else {
                                            mEmailIDs = mEmailIDs + "," + mEmailIds;
                                        }
                                    }
                                }
                            }
                            emailCur.close();
                        }
                    }
                }
                cur.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mPhoneNumbers;
        }

        @Override
        protected void onPostExecute(String result) {
            // Call activity method with results
            if (result.trim().isEmpty()) {
                mRelativeLayoutPhoneEmailFriends.setVisibility(View.GONE);
                showToolbarBtn(mToolbar, R.id.toolbar_settings_img_btn);
                showToolbarBtn(mToolbar, R.id.toolbar_messages_img_btn);
                showToolbarBtn(mToolbar, R.id.toolbar_notification_img_btn);
            } else {
                mRelativeLayoutPhoneEmailFriends.setVisibility(View.VISIBLE);
                getPhoneEmailProfileList();
            }
        }
    }

    private void uploadOffline(){
        DatabaseHandler handler = new DatabaseHandler(this);
        ArrayList<SpectatorLiveEntity> mList = handler.getSpectatorLiveVideos();
        if (mList.size() > 0) {
            if (isNetworkConnected(this)) {
                for (int i = 0; i < mList.size(); i++) {
                    Intent service_intent = new Intent(this, UploadOfflineVideos.class);
                    String data = new Gson().toJson(mList.get(i));
                    service_intent.putExtra("data", data);
                    startService(service_intent);
                }

                /*Intent service_intent = new Intent(this, UploadOfflineVideos.class);
                startService(service_intent);*/
            }
        }
    }

}