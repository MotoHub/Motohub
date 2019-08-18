package online.motohub.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import org.greenrobot.eventbus.EventBus;

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
import online.motohub.adapter.PostsAdapter;
import online.motohub.adapter.TaggedProfilesAdapter;
import online.motohub.application.MotoHub;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.CommonInterface;
import online.motohub.interfaces.CommonReturnInterface;
import online.motohub.interfaces.SharePostInterface;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.FollowProfileModel1;
import online.motohub.model.ImageModel;
import online.motohub.model.LiveStreamResponse;
import online.motohub.model.NotificationBlockedUsersModel;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PushTokenModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.util.CommonAPI;
import online.motohub.dialog.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.StringUtils;
import online.motohub.services.UploadFileService;
import online.motohub.util.UrlUtils;
import online.motohub.util.Utility;

public class MyMotoFileActivity extends BaseActivity implements PostsAdapter.TotalRetrofitPostsResultCount,
        TaggedProfilesAdapter.TaggedProfilesSizeInterface, CommonInterface, OnMenuItemClickListener {

    public static final String EXTRA_RESULT_DATA = "activity_video_picker_uri";
    private static final String TAG = MyMotoFileActivity.class.getName();
    private static final int mDataLimit = 15;
    private final int START_LIVE_STREAM = 1;
    @BindView(R.id.swipe_refresh_view)
    SwipeRefreshLayout mSwipeRefreshLay;
    @BindView(R.id.nestedScrollView)
    NestedScrollView mNestedScrollView;
    @BindView(R.id.my_moto_file_co_layout)
    RelativeLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.profile_img)
    CircleImageView mMPImg;
    @BindView(R.id.cover_photo_img_view)
    ImageView mMPCoverImg;
    @BindView(R.id.name_of_moto_tv)
    TextView mNameOfMotoTv;
    @BindView(R.id.name_of_driver_tv)
    TextView mNameOfDriverTv;
    @BindView(R.id.posts_recycler_view)
    RecyclerView mPostsRecyclerView;
    @BindView(R.id.write_post_et)
    EditText mWritePostEt;
    @BindView(R.id.close_layout)
    FrameLayout mPostImgVideoCloseBtnLayout;
    @BindView(R.id.post_picture_img_view)
    ImageView mPostPicImgView;
    @BindView(R.id.imageframe)
    RelativeLayout mPostImgVideoLayout;
    @BindView(R.id.name_of_following_count_tv)
    TextView mFollowingCount;
    @BindView(R.id.name_of_followers_count_tv)
    TextView mFollowersCount;
    @BindView(R.id.tag_profiles_recycler_view)
    RecyclerView mTagProfilesRecyclerView;
    @BindString(R.string.my_moto_file)
    String mToolbarTitle;
    @BindString(R.string.post_success)
    String mPostSuccess;
    @BindString(R.string.post_delete)
    String mPostDeleteSuccess;
    @BindString(R.string.tag_err)
    String mTagErr;
    @BindString(R.string.camera_permission_denied)
    String mNoCameraPer;
    @BindView(R.id.play)
    ImageView mPlayIconImgView;
    @BindView(R.id.live_box)
    RelativeLayout mLiveBoxLay;
    @BindString(R.string.storage_permission_denied)
    String mNoStoragePer;
    @BindView(R.id.user_details)
    LinearLayout mUserdetails;
    @BindView(R.id.shimmer_myfeeds)
    ShimmerFrameLayout mShimmer_myfeeds;
    @BindView(R.id.shimmer_myprofile)
    ShimmerFrameLayout mShimmer_myprofiledet;
    @BindString(R.string.update_profile_success)
    String mUpdateProfileSuccessStr;
    int DELETE_LIVE_STREAM = 2;
    String mCoverImgUri, mUploadedServerCoverImgFileUrl;
    private ArrayList<ProfileResModel> mFullMPList = new ArrayList<>();
    private ArrayList<PostsResModel> mPostsList = new ArrayList<>();
    private PostsAdapter mPostsAdapter;
    private ArrayList<String> mMPSpinnerList = new ArrayList<>();
    private int mPostPos = 0;
    private ArrayList<ProfileResModel> mFollowingListData = new ArrayList<>();
    private ArrayList<ProfileResModel> mTaggedProfilesList = new ArrayList<>();
    private ProfileResModel mCurrentProfileObj;
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
    private TaggedProfilesAdapter mTaggedProfilesAdapter;
    private LinearLayoutManager mPostsLayoutManager;
    private int mPostsRvOffset = 0, mPostsRvTotalCount = 0;
    private FeedShareModel mSharedFeed;
    private int mCurrentPostPosition;
    private boolean mIsPostsRvLoading = true;
    private int mCurrentProfileID = 0;
    private String mMyFollowings;
    private int LIVE_STREAM_RES_TYPE = 0;
    CommonReturnInterface mCommonReturnInterface = new CommonReturnInterface() {
        @Override
        public void onSuccess(int type) {
            if (type == 1) {
                startSingleStream();
            } else {
                startActivity(new Intent(MyMotoFileActivity.this, ViewStreamUsersActivity.class).putExtra(AppConstants.PROFILE_ID, mCurrentProfileID)
                        .putExtra(AppConstants.MY_FOLLOWINGS, mMyFollowings));
            }
        }
    };
    private String mShareTxt = "";
    SharePostInterface mShareTextWithPostInterface = new SharePostInterface() {
        @Override
        public void onSuccess(String shareMessage) {
            mShareTxt = shareMessage;
            CommonAPI.getInstance().callPostShare(MyMotoFileActivity.this, mPostsList.get(mCurrentPostPosition), mCurrentProfileObj.getID());
        }
    };
    private int liveStreamID = 0;
    private String mLiveStreamName = "";
    private boolean isExtraProfile = false;
    private boolean isCoverPicture;
    private int selProfileID = 0;
    private int prevProfilePos = 0;
    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;

    private void updatePost(Intent intent) {
        PostsResModel mPostsModel =
                (PostsResModel) intent.getSerializableExtra(PostsModel.POST_MODEL);
        if (mPostsModel.getUserType().trim().equals("user")) {
            if (intent.hasExtra(PostsModel.IS_UPDATE)) {
                mPostsList.set(mPostPos, mPostsModel);
            } else {
                mPostsRvTotalCount = mPostsRvTotalCount + 1;
                mPostsList.add(0, mPostsModel);
            }
            setPostAdapter();
            showSnackBar(mCoordinatorLayout, getString(R.string.post_update_success));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_moto_file);
        ButterKnife.bind(this);
        initView();
        getMyProfiles();
    }


    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, new IntentFilter(UploadFileService.NOTIFY_POST_VIDEO_UPDATED));
        MotoHub.getApplicationInstance().myMotoFileOnResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
        MotoHub.getApplicationInstance().myMotoFileOnPause();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        if (selProfileID != 0)
            PreferenceUtils.getInstance(this).saveIntData(PreferenceUtils.CURRENT_PROFILE_POS, prevProfilePos);
        super.onDestroy();
    }

    private void initView() {
        setupUI(mCoordinatorLayout);
        AppConstants.LIVE_STREAM_CALL_BACK = this;
        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        mShimmer_myfeeds.startShimmerAnimation();
        mShimmer_myprofiledet.startShimmerAnimation();

        selProfileID = getIntent().getIntExtra(AppConstants.MY_PROFILE_ID, 0);
        if (selProfileID != 0)
            prevProfilePos = getProfileCurrentPos();

        mPostsLayoutManager = new LinearLayoutManager(this);
        mPostsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mPostsRecyclerView.setLayoutManager(mPostsLayoutManager);

        mNestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = mNestedScrollView.getChildAt(mNestedScrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (mNestedScrollView.getHeight() + mNestedScrollView
                        .getScrollY()));

                if (diff == 0) {
                    // your pagination code

                    int mVisibleItemCount = mPostsLayoutManager.getChildCount();
                    int mTotalItemCount = mPostsLayoutManager.getItemCount();
                    int mFirstVisibleItemPosition = mPostsLayoutManager.findFirstVisibleItemPosition();
                    if (!mIsPostsRvLoading && !(mPostsRvOffset >= mPostsRvTotalCount)) {
                        if ((mVisibleItemCount + mFirstVisibleItemPosition) >= mTotalItemCount
                                && mFirstVisibleItemPosition >= 0) {
                            mIsPostsRvLoading = true;
                            getProfilePosts();
                        }
                    }
                }
            }
        });

        FlexboxLayoutManager mFlexBoxLayoutManager = new FlexboxLayoutManager(this);
        mFlexBoxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        mFlexBoxLayoutManager.setFlexDirection(FlexDirection.ROW);
        mFlexBoxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        mTagProfilesRecyclerView.setLayoutManager(mFlexBoxLayoutManager);
        mTaggedProfilesAdapter = new TaggedProfilesAdapter(mTaggedProfilesList, this);
        mTagProfilesRecyclerView.setAdapter(mTaggedProfilesAdapter);

        fragmentManager = getSupportFragmentManager();
        initMenuFragment();

    }

    private void setPostAdapter() {
        if (mPostsAdapter == null) {
            mPostsAdapter = new PostsAdapter(mPostsList, getCurrentProfile(), this, true);
            mPostsRecyclerView.setAdapter(mPostsAdapter);
        } else {
            mPostsAdapter.notifyDataSetChanged();
        }
    }

    private void getMyProfiles() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                Collections.addAll(mMPSpinnerList, getResources().getStringArray(R.array.empty_array));
                int mUserID = PreferenceUtils.getInstance(MyMotoFileActivity.this).getIntData(PreferenceUtils.USER_ID);
                String mFilter = "UserID = " + mUserID;
                RetrofitClient.getRetrofitInstance().callGetProfiles(MyMotoFileActivity.this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);
            }
        });
    }

    private void getProfilePosts() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code.
                String mFilter;
                ProfileResModel mProfileResModel = getCurrentProfile();
                String mBlockedUsers = Utility.getInstance().getMyBlockedUsersID(mProfileResModel.getBlockedUserProfilesByProfileID(),
                        mProfileResModel.getBlockeduserprofiles_by_BlockedProfileID());
                if (mBlockedUsers.trim().isEmpty()) {
                    mFilter = "((ProfileID=" + mProfileResModel.getID() + ") AND ((user_type!='promoter') AND (user_type!='club') AND (user_type!='newsmedia') AND (user_type!='track') AND (user_type!='shop'))) AND (ReportStatus == 0)";
                } else {
                    mFilter = "((ProfileID=" + mProfileResModel.getID() + ") AND ((user_type!='promoter') AND (user_type!='club') AND (user_type!='newsmedia') AND (user_type!='track') AND (user_type!='shop'))) AND (ProfileID NOT IN (" + mBlockedUsers + ")) AND (ReportStatus == 0)";
                }
                RetrofitClient.getRetrofitInstance().callGetProfilePosts(MyMotoFileActivity.this, mFilter, RetrofitClient.GET_PROFILE_POSTS_RESPONSE, mDataLimit, mPostsRvOffset);
            }
        });
    }

    private void profilePostContent(final String postImgFilePath) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                try {
                    String mWritePostStr = mWritePostEt.getText().toString().trim();
                    if (TextUtils.isEmpty(postImgFilePath) && TextUtils.isEmpty(mWritePostStr)) {
                        return;
                    }

                    int mUserId = PreferenceUtils.getInstance(MyMotoFileActivity.this).getIntData(PreferenceUtils.USER_ID);

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
                    if (isNetworkConnected(MyMotoFileActivity.this))
                        RetrofitClient.getRetrofitInstance().callCreateProfilePosts(MyMotoFileActivity.this, mJsonArray, RetrofitClient.CREATE_PROFILE_POSTS_RESPONSE);
                    else
                        showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @OnClick({R.id.refresh_btn, R.id.toolbar_back_img_btn, R.id.toolbar_settings_img_btn,
            R.id.toolbar_messages_img_btn, R.id.toolbar_notification_img_btn, R.id.profile_img,
            R.id.name_of_moto_tv, R.id.name_of_driver_tv, R.id.vehicle_info_box, R.id.post_btn,
            R.id.add_post_img, R.id.followers_box, R.id.following_box, R.id.imageframe,
            R.id.add_post_video, R.id.tag_profile_img, R.id.ib_add_cover_photo,
            R.id.cover_photo_img_view, R.id.your_build_box, R.id.photo_box, R.id.video_box,
            R.id.remove_post_img_btn, R.id.live_box, R.id.btn_write_post})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refresh_btn:
                getMyProfiles();
                break;
            case R.id.add_post_video:
                showAppDialog(AppDialogFragment.BOTTOM_ADD_VIDEO_DIALOG, null);
                break;
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.toolbar_settings_img_btn:
                //showPopupMenu(v);
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
            case R.id.toolbar_messages_img_btn:
                if (mFullMPList.size() > 0) {
                    //MotoHub.getApplicationInstance().setmProfileResModel(getCurrentProfile());
                    EventBus.getDefault().postSticky(getCurrentProfile());
                    startActivity(new Intent(MyMotoFileActivity.this, ChatHomeActivity.class));
                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;

            case R.id.toolbar_notification_img_btn:
                if (mFullMPList.size() > 0) {
                    //MotoHub.getApplicationInstance().setmProfileResModel(getCurrentProfile());
                    EventBus.getDefault().postSticky(getCurrentProfile());
                    startActivityForResult(new Intent(this, NotificationActivity.class), AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.cover_photo_img_view:
                if (mFullMPList.size() > 0) {
                    ProfileResModel mMyProfileResModel = mFullMPList.get(PreferenceUtils.getInstance(this)
                            .getIntData(PreferenceUtils.CURRENT_PROFILE_POS));
                    if (!mMyProfileResModel.getCoverPicture().trim().isEmpty()) {
                        moveLoadImageScreen(this, UrlUtils.FILE_URL + mMyProfileResModel.getCoverPicture());
                    }
                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.ib_add_cover_photo:
                isCoverPicture = true;
                showAppDialog(AppDialogFragment.BOTTOM_ADD_IMG_DIALOG, null);
                break;
            case R.id.profile_img:
            case R.id.name_of_moto_tv:
            case R.id.name_of_driver_tv:
                if (mFullMPList.size() > 0) {
                    Bundle mBundle = new Bundle();
                    /*mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mFullMPList.get(PreferenceUtils
                            .getInstance
                                    (this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));*/
                    /*MotoHub.getApplicationInstance().setmProfileResModel(mFullMPList.get(PreferenceUtils
                            .getInstance
                                    (this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));*/
                    EventBus.getDefault().postSticky(mFullMPList.get(PreferenceUtils
                            .getInstance
                                    (this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));
                    startActivityForResult(new Intent(this, UpdateProfileActivity.class), AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.vehicle_info_box:
                if (mFullMPList.size() > 0) {
                    Bundle mBundle = new Bundle();
                    /*mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mFullMPList.get(PreferenceUtils
                            .getInstance
                                    (this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));*/
                    /*MotoHub.getApplicationInstance().setmProfileResModel(mFullMPList.get(PreferenceUtils
                            .getInstance
                                    (this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));*/
                    EventBus.getDefault().postSticky(mFullMPList.get(PreferenceUtils
                            .getInstance
                                    (this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));
                    mBundle.putBoolean(AppConstants.IS_FROM_VEHICLE_INFO, true);
                    startActivityForResult(new Intent(this, UpdateProfileActivity.class).putExtras(mBundle), AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.followers_box:
                if (mFullMPList.size() > 0) {
                    if (getCurrentProfile().getFollowprofile_by_FollowProfileID().size() > 0) {
                        //MotoHub.getApplicationInstance().setmProfileResModel(getCurrentProfile());
                        EventBus.getDefault().postSticky(getCurrentProfile());
                        startActivityForResult(new Intent(this, FollowersFollowingActivity.class)
                                /*.putExtra(AppConstants.MY_PROFILE_OBJ, getCurrentProfile())*/
                                .putExtra(AppConstants.IS_FOLLOWERS, true), AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                    } else {
                        showToast(MyMotoFileActivity.this, getString(R.string.no_followers_found_err));
                    }
                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.following_box:
                if (mFullMPList.size() > 0) {
                    if (getCurrentProfile().getFollowprofile_by_ProfileID().size() > 0) {
                        //MotoHub.getApplicationInstance().setmProfileResModel(getCurrentProfile());
                        EventBus.getDefault().postSticky(getCurrentProfile());
                        startActivityForResult(new Intent(this, FollowersFollowingActivity.class)
                                /*.putExtra(AppConstants.MY_PROFILE_OBJ, getCurrentProfile())*/
                                .putExtra(AppConstants.IS_FOLLOWERS, false), AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                    } else {
                        showToast(MyMotoFileActivity.this, getString(R.string.no_following_found_err));
                    }
                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.your_build_box:
                startActivity(new Intent(this, ComingSoonActivity.class));
                break;
            case R.id.photo_box:
                if (mFullMPList.size() > 0) {
                    //MotoHub.getApplicationInstance().setmProfileResModel(getCurrentProfile());
                    EventBus.getDefault().postSticky(getCurrentProfile());
                    Intent photoIntent = new Intent(MyMotoFileActivity.this, ProfileImgGalleryActivity.class);
                    ProfileResModel model = getCurrentProfile();
                    photoIntent.putExtra(ProfileImgGalleryActivity.EXTRA_PROFILE, model.getID());
                    startActivity(photoIntent);

                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.video_box:
                if (mFullMPList.size() > 0) {
                    //MotoHub.getApplicationInstance().setmProfileResModel(getCurrentProfile());
                    EventBus.getDefault().postSticky(getCurrentProfile());
                    Intent videoIntent = new Intent(MyMotoFileActivity.this, ProfileVideoGalleryActivity.class);
                    ProfileResModel model1 = getCurrentProfile();
                    videoIntent.putExtra(ProfileVideoGalleryActivity.EXTRA_PROFILE, model1.getID());
                    startActivity(videoIntent);
                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.live_box:
                DialogManager.showMultiLiveOptionPopup(this, mCommonReturnInterface, getString(R.string.single_stream), getString(R.string.live_stream));
                break;
            case R.id.btn_write_post:
                if (mCurrentProfileObj != null) {
                    /*Gson mGson = new Gson();
                    String mProfile = mGson.toJson(mCurrentProfileObj);*/
                    //MotoHub.getApplicationInstance().setmProfileResModel(mCurrentProfileObj);
                    EventBus.getDefault().postSticky(mCurrentProfileObj);
                    startActivityForResult(new Intent(MyMotoFileActivity.this, WritePostActivity.class)
                            /*.putExtra(AppConstants.MY_PROFILE_OBJ, mProfile)*/
                            .putExtra(AppConstants.USER_TYPE, AppConstants.USER)
                            .putExtra(AppConstants.IS_NEWSFEED_POST, true), AppConstants.WRITE_POST_REQUEST);
                }
                break;
        }
    }

    private void startSingleStream() {
        try {
            String mStreamName = StringUtils.genRandomStreamName(this);
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(APIConstants.StreamName, mStreamName);
            mJsonObject.addProperty(APIConstants.CreatedProfileID, mCurrentProfileID);
            mJsonObject.addProperty(APIConstants.StreamProfileID, mCurrentProfileID);
            JsonArray mJsonArray = new JsonArray();
            mJsonArray.add(mJsonObject);
            LIVE_STREAM_RES_TYPE = START_LIVE_STREAM;
            if (isNetworkConnected(this))
                RetrofitClient.getRetrofitInstance().callPostLiveStream(this, mJsonArray);
            else
                showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
        } catch (Exception e) {
            sysOut("" + e.getMessage());
        }
    }

    @Override
    public void onSuccess() {
        String mFilter = "ID=" + liveStreamID;
        LIVE_STREAM_RES_TYPE = DELETE_LIVE_STREAM;
        if (isNetworkConnected(this))
            RetrofitClient.getRetrofitInstance().callDeleteLiveStream(this, mFilter);
        else
            showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
    }

    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String mDialogType, StringBuilder profileTypesStr,
                                            ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, mDialogType, profileTypesStr, profileTypes, position);
        switch (mDialogType) {
            case AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG:
                getMyProfiles();
                break;
            case AppDialogFragment.ALERT_OTHER_PROFILE_DIALOG:
                prevProfilePos = position;
                PreferenceUtils.getInstance(this).saveIntData(PreferenceUtils.CURRENT_PROFILE_POS, position);
                setResult(RESULT_OK, new Intent()
                        .putExtra(AppConstants.IS_FOLLOW_RESULT, true));
                changeAndSetProfile(position);
                getMyProfiles();
                //refreshFeeds(getCurrentProfile());
                break;
            case AppDialogFragment.BOTTOM_REPORT_ACTION_DIALOG:
                startActivityForResult(
                        new Intent(this, ReportActivity.class).putExtra(PostsModel.POST_ID, mPostsList.get(position).getID()).putExtra(ProfileModel.PROFILE_ID, mFullMPList.get(getProfileCurrentPos()).getID()).putExtra(ProfileModel.USER_ID, mFullMPList.get(getProfileCurrentPos()).getUserID()),
                        AppConstants.REPORT_POST_SUCCESS);
                break;
            case AppDialogFragment.BOTTOM_DELETE_DIALOG:
                mPostPos = position;
                if (isNetworkConnected(this))
                    RetrofitClient.getRetrofitInstance().callDeleteProfilePosts(this, mPostsList.get(position).getID(), RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE);
                else
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                break;
            case AppDialogFragment.BOTTOM_EDIT_DIALOG:
                mPostPos = position;
                Bundle mBundle = new Bundle();
                /*MotoHub.getApplicationInstance().setmProfileResModel(mFullMPList.get(PreferenceUtils.getInstance(this)
                        .getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));*/
                EventBus.getDefault().postSticky(mFullMPList.get(PreferenceUtils.getInstance(this)
                        .getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));
                mBundle.putSerializable(PostsModel.POST_MODEL, mPostsList.get(position));
                //mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mFullMPList.get(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));
                startActivityForResult(new Intent(this, PostEditActivity.class).putExtras(mBundle), AppConstants.POST_UPDATE_SUCCESS);
                /*MotoHub.getApplicationInstance().setmPostResModel(mPostsList.get(position));
                MotoHub.getApplicationInstance().setmProfileResModel(mFullMPList.get(PreferenceUtils.getInstance(this)
                        .getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));
                startActivityForResult(new Intent(this, PostEditActivity.class), AppConstants.POST_UPDATE_SUCCESS);*/
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
            case AppDialogFragment.BOTTOM_SHARE_DIALOG:
                mCurrentPostPosition = position;
                CommonAPI.getInstance().callPostShare(this, mPostsList.get(mCurrentPostPosition), mFullMPList.get(getProfileCurrentPos()).getID());
                break;
            case AppDialogFragment.LOG_OUT_DIALOG:
                logout();
                break;
        }
    }

    private void logout() {
        int mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        String mFilter = "UserID=" + mUserID;
        if (isNetworkConnected(this))
            RetrofitClient.getRetrofitInstance().callDeletePushToken(this, mFilter, RetrofitClient.FACEBOOK_LOGOUT);
        else
            showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
    }

    private void changeAndSetProfile(final int position) {
        runOnUiThread(new Runnable() {
            public void run() {
                //TODO updating values in UI thread
                try {
                    if (mFullMPList.size() > position) {
                        ProfileResModel mProfileResModel = mFullMPList.get(position);
                        mCurrentProfileID = mProfileResModel.getID();
                        mMyFollowings = Utility.getInstance().getMyFollowersFollowingsID(mProfileResModel.getFollowprofile_by_FollowProfileID(), true);
                        if (!mProfileResModel.getProfilePicture().isEmpty()) {
                            setImageWithGlide(mMPImg, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);
                        } else {
                            mMPImg.setImageResource(R.drawable.default_profile_icon);
                        }
                        if (!mProfileResModel.getCoverPicture().isEmpty()) {
                            setImageWithGlide(mMPCoverImg, mProfileResModel.getCoverPicture(), R.drawable.default_cover_img);
                        } else {
                            mMPCoverImg.setImageResource(R.drawable.default_cover_img);
                        }
                        if (Utility.getInstance().isSpectator(mProfileResModel)) {
                            mNameOfMotoTv.setText(mProfileResModel.getSpectatorName());
                            mNameOfDriverTv.setVisibility(View.GONE);
                        } else {
                            mNameOfMotoTv.setText(mProfileResModel.getMotoName());
                            mNameOfDriverTv.setVisibility(View.VISIBLE);
                            mNameOfDriverTv.setText(mProfileResModel.getDriver());
                        }
                        mShimmer_myprofiledet.stopShimmerAnimation();
                        mShimmer_myprofiledet.setVisibility(View.GONE);
                        mUserdetails.setVisibility(View.VISIBLE);
                        showToolbarBtn(mToolbar, R.id.toolbar_settings_img_btn);
                        showToolbarBtn(mToolbar, R.id.toolbar_messages_img_btn);
                        showToolbarBtn(mToolbar, R.id.toolbar_notification_img_btn);
                        setFollowUnFollowContent(mProfileResModel);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(true);
        menuParams.setAnimationDuration(100);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        //mMenuDialogFragment.setItemLongClickListener(this);
    }

    private void setFollowUnFollowContent(ProfileResModel mProfileResModel) {
        getFollowerscount(mProfileResModel);
        getFollowingcount(mProfileResModel);
    }

    private void getFollowerscount(ProfileResModel mProfileResModel) {
        String FFilter = "FollowProfileID=" + mProfileResModel.getID();
        RetrofitClient.getRetrofitInstance().callfollowfollowerscount(this, FFilter, RetrofitClient.GET_follower_count);
    }

    private void getFollowingcount(ProfileResModel mProfileResModel) {
        String FFilter = "ProfileID=" + mProfileResModel.getID();
        RetrofitClient.getRetrofitInstance().callfollowfollowerscount(this, FFilter, RetrofitClient.GET_following_count);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RetrofitClient.UPDATE_FEED_COUNT:
                    try {
                        assert data.getExtras() != null;
                        mPostsAdapter.updateView(data.getIntExtra(AppConstants.POSITION, 0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case AppConstants.FOLLOWERS_FOLLOWING_RESULT:
                    getMyProfiles();
                    setResult(RESULT_OK, new Intent()
                            .putExtra(AppConstants.IS_FOLLOW_RESULT, true));
                    break;
                case AppConstants.POST_UPDATE_SUCCESS:
                    if (data == null) {
                        showToast(this, getString(R.string.post_video_uploading_update));
                    } else {
                        assert data.getExtras() != null;
                        PostsResModel mPostsResModel = (PostsResModel) data.getExtras().get(PostsModel.POST_MODEL);
                        assert mPostsResModel != null;
                        mPostsList.get(mPostPos).setPostPicture(mPostsResModel.getPostPicture());
                        mPostsList.get(mPostPos).setPostText(mPostsResModel.getPostText());
                        mPostsList.get(mPostPos).setTaggedProfileID(mPostsResModel.getTaggedProfileID());
                        mPostsList.get(mPostPos).setPostVideoThumbnailURL(mPostsResModel.getPostVideoThumbnailURL());
                        mPostsList.get(mPostPos).setPostVideoURL(mPostsResModel.getPostVideoURL());
                        setPostAdapter();
                    }
                    break;
                case AppConstants.CREATE_PROFILE_RES:
                    isExtraProfile = true;
                    setResult(RESULT_OK, new Intent()
                            .putExtra(AppConstants.IS_FOLLOW_RESULT, true));
                    getMyProfiles();
                    break;
                case AppConstants.POST_COMMENT_REQUEST:
                    assert data.getExtras() != null;
                    ArrayList<FeedCommentModel> mFeedCommentModel = (ArrayList<FeedCommentModel>) data.getExtras().getSerializable(PostsModel.COMMENTS_BY_POSTID);
                    mPostsAdapter.refreshCommentList(mFeedCommentModel);
                    break;
                case AppConstants.WRITE_POST_REQUEST:
                    /*getMyProfiles();*/
                    refreshFeeds(mCurrentProfileObj);
                    break;
                case CAMERA_CAPTURE_REQ:
                    Uri mCameraPicUri = getImgResultUri(data);
                    DialogManager.showProgress(this);
                    if (mCameraPicUri != null) {
                        try {
                            if (isCoverPicture) {
                                File mCoverImgFile = compressedImgFile(mCameraPicUri, POST_IMAGE_NAME_TYPE, "");
                                mCoverImgUri = Uri.fromFile(mCoverImgFile).toString();
                                setImageWithGlide(mMPCoverImg, Uri.parse(mCoverImgUri), R.drawable.default_cover_img);
                                if (mCoverImgUri != null)
                                    uploadCoverPicture(mCoverImgUri);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mCoverImgUri = null;
                            showSnackBar(mCoordinatorLayout, e.getMessage());
                        }
                    } else {
                        DialogManager.hideProgress();
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
                case GALLERY_PIC_REQ:
                    assert data.getExtras() != null;
                    Uri mSelectedImgFileUri = (Uri) data.getExtras().get(PickerImageActivity.EXTRA_RESULT_DATA);
                    DialogManager.showProgress(this);
                    if (mSelectedImgFileUri != null) {
                        try {
                            if (isCoverPicture) {
                                File mCoverImgFile = compressedImgFile(mSelectedImgFileUri, POST_IMAGE_NAME_TYPE, "");
                                mCoverImgUri = Uri.fromFile(mCoverImgFile).toString();
                                setImageWithGlide(mMPCoverImg, Uri.parse(mCoverImgUri), R.drawable.default_cover_img);
                                if (mCoverImgUri != null)
                                    uploadCoverPicture(mCoverImgUri);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mCoverImgUri = null;
                            showSnackBar(mCoordinatorLayout, e.getMessage());
                        }
                    } else {
                        DialogManager.hideProgress();
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
            }
        }

    }

    private void uploadCoverPicture(String imgUri) {
        File mFile = new File(Uri.parse(imgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", mFile.getName(), requestBody);
        RetrofitClient.getRetrofitInstance()
                .callUploadProfileCoverImg(this, filePart, RetrofitClient.UPLOAD_COVER_IMAGE_FILE_RESPONSE);
    }

    private void submitCoverpic(String coverimguri) {
        String mTempCoverImgUrl;
        mTempCoverImgUrl = coverimguri;
        if (coverimguri == null || TextUtils.isEmpty(coverimguri)) {
            mTempCoverImgUrl = mCurrentProfileObj.getCoverPicture();
        }
        int mUserId = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        JsonObject mJsonObject = new JsonObject();
        mJsonObject.addProperty(ProfileModel.ID, mCurrentProfileObj.getID());
        mJsonObject.addProperty(ProfileModel.USER_ID, mUserId);
        mJsonObject.addProperty(ProfileModel.COVER_PICTURE, mTempCoverImgUrl);
        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mJsonObject);

        RetrofitClient.getRetrofitInstance().callUpdateProfile(this, mJsonArray, RetrofitClient.UPDATE_PROFILE_RESPONSE);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        if (responseObj instanceof ProfileModel) {
            ProfileModel mProfileModel = (ProfileModel) responseObj;
            if (mProfileModel.getResource().size() > 0) {
                mCurrentProfileObj = mProfileModel.getResource().get(0);
            }
            switch (responseType) {
                case RetrofitClient.GET_PROFILE_RESPONSE:
                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        mFullMPList.clear();
                        mFullMPList.addAll(mProfileModel.getResource());
                        for (int i = 0; i < mFullMPList.size(); i++) {
                            if (selProfileID != 0 && mFullMPList.get(i).getID() == selProfileID) {
                                PreferenceUtils.getInstance(this).saveIntData(PreferenceUtils.CURRENT_PROFILE_POS, i);
                                break;
                            }
                        }
                        if (isExtraProfile) {
                            isExtraProfile = false;
                            PreferenceUtils.getInstance(this).saveIntData(PreferenceUtils.CURRENT_PROFILE_POS, (mFullMPList.size() - 1));
                        }
                        mCurrentProfileObj = mFullMPList.get(getProfileCurrentPos());
                        ArrayList<String> mTempMPSpinnerList = new ArrayList<>();
                        for (ProfileResModel mProfileResModel : mFullMPList) {
                            if (Utility.getInstance().isSpectator(mProfileResModel)) {
                                mTempMPSpinnerList.add(mProfileResModel.getSpectatorName());
                            } else {
                                mTempMPSpinnerList.add(mProfileResModel.getMotoName());
                            }
                        }
                        mPostsRvOffset = 0;
                        mIsPostsRvLoading = true;
                        mMPSpinnerList.clear();
                        mMPSpinnerList.addAll(mTempMPSpinnerList);
                        changeAndSetProfile(getProfileCurrentPos());
                        //MotoHub.getApplicationInstance().setmProfileResModel(getCurrentProfile());
                        EventBus.getDefault().postSticky(getCurrentProfile());
                        mPostsRvTotalCount = -1;
                        setPostAdapter();
                        refreshFeeds(getCurrentProfile());
                    } else {
                        showSnackBar(mCoordinatorLayout, mNoProfileErr);
                    }
                    break;
                case RetrofitClient.UPDATE_PROFILE_RESPONSE:
                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        showToast(this, mUpdateProfileSuccessStr);
                        saveInAppPurchasesLocally(Integer.parseInt(PROFILE_PURCHASED), String.valueOf(getCurrentProfile().getProfileType()));
                        getMyProfiles();
                        DialogManager.hideProgress();
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
            }
        } else if (responseObj instanceof PostsModel) {
            PostsModel mPostsModel = (PostsModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_PROFILE_POSTS_RESPONSE:
                    try {
                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            mPostsRvTotalCount = mPostsModel.getMeta().getCount();
                            mPostsList.clear();
                            mIsPostsRvLoading = false;
                            if (mPostsRvOffset == 0) {
                                mPostsList.clear();
                            }
                            mPostsList.addAll(mPostsModel.getResource());
                            mPostsRvOffset = mPostsRvOffset + mDataLimit;
                        } else {
                            if (mPostsRvOffset == 0) {
                                mPostsRvTotalCount = 0;
                            }
                        }
                        setPostAdapter();
                        mShimmer_myfeeds.stopShimmerAnimation();
                        mShimmer_myfeeds.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case RetrofitClient.CREATE_PROFILE_POSTS_RESPONSE:
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        mWritePostEt.setText("");
                        mPostPicImgView.setImageResource(R.drawable.photos_icon);
                        mPostPicImgView.setVisibility(View.GONE);
                        mPostImgVideoLayout.setVisibility(View.GONE);
                        mPostImgVideoCloseBtnLayout.setVisibility(View.GONE);
                        int prefPos = getProfileCurrentPos();
                        PostsResModel mPostsResModel = mPostsModel.getResource().get(0);
                        ProfileResModel mProfileResModel = new ProfileResModel();
                        mProfileResModel.setProfilePicture(mFullMPList.get(prefPos).getProfilePicture());
                        mProfileResModel.setProfileType(mFullMPList.get(prefPos).getProfileType());
                        if (Utility.getInstance().isSpectator(mFullMPList.get(prefPos))) {
                            mProfileResModel.setSpectatorName(mFullMPList.get(prefPos).getSpectatorName());
                        } else {
                            mProfileResModel.setDriver(mFullMPList.get(prefPos).getDriver());
                        }
                        mPostsResModel.setProfilesByWhoPostedProfileID(mProfileResModel);
                        mPostsRvTotalCount = mPostsRvTotalCount + 1;
                        mPostsList.add(0, mPostsResModel);
                        setPostAdapter();
                        mTaggedProfilesList.clear();
                        mTaggedProfilesAdapter.notifyDataSetChanged();
                        if (mTaggedProfilesList.size() > 0) {
                            mTagProfilesRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            mTagProfilesRecyclerView.setVisibility(View.GONE);
                        }
                        showSnackBar(mCoordinatorLayout, mPostSuccess);
                    }
                    break;
                case RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE:
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        mPostsList.remove(mPostPos);
                        mPostsRvTotalCount -= 1;
                        setPostAdapter();
                        showSnackBar(mCoordinatorLayout, mPostDeleteSuccess);
                    }
                    break;
                case RetrofitClient.SHARED_POST_RESPONSE:
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        PostsResModel mPostsResModel = mPostsModel.getResource().get(0);
                        mPostsList.add(0, mPostsResModel);
                        mPostsRvTotalCount += 1;
                        setPostAdapter();
                    }
                    mPostsAdapter.resetShareAdapter(mSharedFeed);
                    break;
                case RetrofitClient.FEED_VIDEO_COUNT:
                    try {
                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            mPostsAdapter.addViewCount(mPostsModel.getResource().get(0).getViewCount());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case RetrofitClient.ADD_FEED_COUNT:
                    try {
                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            mPostsAdapter.ViewCount(mPostsModel.getResource().get(0).getViewCount());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                case RetrofitClient.UPLOAD_COVER_IMAGE_FILE_RESPONSE:
                    mUploadedServerCoverImgFileUrl = getHttpFilePath(mImageModel.getmModels().get(0).getPath());
                    mCoverImgUri = null;
                    submitCoverpic(mUploadedServerCoverImgFileUrl);
                    break;
            }
        } else if (responseObj instanceof NotificationBlockedUsersModel) {
            NotificationBlockedUsersModel mNotify = (NotificationBlockedUsersModel) responseObj;
            ArrayList<NotificationBlockedUsersModel> mPostNotification = mNotify.getResource();
            switch (responseType) {
                case RetrofitClient.BLOCK_NOTIFY:
                    if (mPostNotification != null && mPostNotification.size() > 0) {
                        mPostsAdapter.resetBlock(mPostNotification.get(0));
                    }
                    break;
                case RetrofitClient.UNBLOCK_NOTIFY:
                    if (mPostNotification != null && mPostNotification.size() > 0) {
                        mPostsAdapter.resetUnBlock(mPostNotification.get(0));
                    }
                    break;
            }
        } else if (responseObj instanceof FeedLikesModel) {
            FeedLikesModel mFeedLikesList = (FeedLikesModel) responseObj;
            ArrayList<FeedLikesModel> mNewFeedLike = mFeedLikesList.getResource();
            switch (responseType) {
                case RetrofitClient.POST_LIKES:
                    if (mNewFeedLike != null && mNewFeedLike.size() > 0) {
                        mPostsAdapter.resetLikeAdapter(mNewFeedLike.get(0));
                    }
                    break;
                case RetrofitClient.POST_UNLIKE:
                    if (mNewFeedLike != null && mNewFeedLike.size() > 0) {
                        mPostsAdapter.resetDisLike(mNewFeedLike.get(0));
                    }
                    break;
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
                    CommonAPI.getInstance().callAddSharedPost(this, mPostsList.get(mCurrentPostPosition), getCurrentProfile(), mShareTxt);
                    break;
            }
        } else if (responseObj instanceof PushTokenModel) {
            clearBeforeLogout();
            Intent loginActivity = new Intent(this, LoginActivity.class);
            loginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginActivity);
            finish();
        } else if (responseObj instanceof LiveStreamResponse) {
            LiveStreamResponse mLiveStreamResponse = (LiveStreamResponse) responseObj;
            if (LIVE_STREAM_RES_TYPE == START_LIVE_STREAM) {
                if (mLiveStreamResponse.getResource().size() > 0) {
                    liveStreamID = mLiveStreamResponse.getResource().get(0).getID();
                    mLiveStreamName = mLiveStreamResponse.getResource().get(0).getStreamName();
                }
                MotoHub.getApplicationInstance().setLiveStreamName(mLiveStreamName);
                Intent mCameraActivity = new Intent(this, CameraActivity.class);
                startActivity(mCameraActivity);
            }
        } else if (responseObj instanceof FollowProfileModel1) {
            FollowProfileModel1 mResponse = (FollowProfileModel1) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_follower_count:
                    FollowProfileModel1.Meta meta1 = mResponse.getMeta();
                    if (meta1 != null)
                        mFollowersCount.setText(String.valueOf(meta1.getCount()));
                    else
                        mFollowersCount.setText(0);
                    break;
                case RetrofitClient.GET_following_count:
                    FollowProfileModel1.Meta meta2 = mResponse.getMeta();
                    if (meta2 != null)
                        mFollowingCount.setText(String.valueOf(meta2.getCount()));
                    else
                        mFollowingCount.setText(0);
                    break;
            }
        }
    }

    private void launchTagFollowersProfileDialog() {
        DialogFragment mDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(AppDialogFragment.TAG);
        if (mDialogFragment != null && mDialogFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(mDialogFragment).commit();
        }
        AppDialogFragment.newInstance(AppDialogFragment.TAG_FOLLOWING_PROFILE_DIALOG, mFollowingListData, null)
                .show(getSupportFragmentManager(), AppDialogFragment.TAG);
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (message.equals("Unauthorized") || code == 401) {
            if (isNetworkConnected(this))
                RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
            else
                showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mCoordinatorLayout, mErrorMsg);
        }
    }

    @Override
    public void retrofitOnError(int code, String message, int responseType) {
        super.retrofitOnError(code, message, responseType);
        if (message.equals("Unauthorized") || code == 401) {
            if (isNetworkConnected(this))
                RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
            else
                showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
        } else {
            switch (responseType) {
                case RetrofitClient.POST_SHARES:
                    showSnackBar(mCoordinatorLayout, "Already shared this post");
                    break;
            }
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
        showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
    }

    @Override
    public int getTotalPostsResultCount() {
        return mPostsRvTotalCount;
    }

    private ProfileResModel getCurrentProfile() {
        return mCurrentProfileObj;
    }

    public void refreshFeeds(ProfileResModel mProfileObj) {
        mCurrentProfileObj = mProfileObj;
        mPostsRvTotalCount = -1;
        mPostsRvOffset = 0;
        mPostsList.clear();
        setPostAdapter();
        getProfilePosts();
    }

    @Override
    public void notifyEmptyTaggedProfilesList(ArrayList<ProfileResModel> taggedProfilesList) {
        mTaggedProfilesList.addAll(taggedProfilesList);
        if (mTaggedProfilesList.size() > 0) {
            mTagProfilesRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mTagProfilesRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMenuItemClick(View view, int position) {
        switch (position) {
            case 1:
                if (mFullMPList.size() > 0) {
                    Intent intent = new Intent(this, CreateProfileActivity.class);
                    intent.putExtra(CREATE_PROF_AFTER_REG, true);
                    intent.putExtra(AppConstants.TAG, TAG);
                    startActivityForResult(intent, AppConstants.CREATE_PROFILE_RES);
                }
                break;
            case 2:
                if (mFullMPList.size() > 0) {
                    showAppDialog(AppDialogFragment.ALERT_OTHER_PROFILE_DIALOG, mMPSpinnerList);
                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case 3:
                startActivity(new Intent(MyMotoFileActivity.this, NotificationSettingsActivity.class));
                break;
            case 4:
                if (mFullMPList.size() > 0) {
                    if (getCurrentProfile().getBlockedUserProfilesByProfileID().size() > 0) {
                        //MotoHub.getApplicationInstance().setmProfileResModel(getCurrentProfile());
                        EventBus.getDefault().postSticky(getCurrentProfile());
                        /*startActivityForResult(new Intent(this, BlockedUsersActivity.class).putExtra(ProfileModel.MY_PROFILE_RES_MODEL,
                                getCurrentProfile()),
                                AppConstants.FOLLOWERS_FOLLOWING_RESULT);*/
                        startActivityForResult(new Intent(this, BlockedUsersActivity.class), AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                    } else {
                        showToast(MyMotoFileActivity.this, getString(R.string.no_blocked_users_found_err));
                    }
                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case 5:
                startActivity(new Intent(this, PaymentActivity.class).putExtra(AppConstants.CARD_SETTINGS, true));
                break;
            case 6:
                showAppDialog(AppDialogFragment.LOG_OUT_DIALOG, null);
                break;
        }
    }

    public List<MenuObject> getMenuObjects() {

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setBgColor(ContextCompat.getColor(this, R.color.colorBlack));
        close.setResource(R.drawable.orange_close_icon);

        MenuObject newprofile = new MenuObject(getString(R.string.create_new_profile));
        newprofile.setBgColor(ContextCompat.getColor(this, R.color.colorBlack));
        newprofile.setResource(R.drawable.ic_new_profile);

        MenuObject otherprofile = new MenuObject(getString(R.string.other_profiles));
        otherprofile.setBgColor(ContextCompat.getColor(this, R.color.colorBlack));
        otherprofile.setResource(R.drawable.ic_other_profile);

        MenuObject notification = new MenuObject(getString(R.string.notifications));
        notification.setBgColor(ContextCompat.getColor(this, R.color.colorBlack));
        notification.setResource(R.drawable.ic_notification);

        MenuObject blockeduser = new MenuObject(getString(R.string.blocked_users));
        blockeduser.setBgColor(ContextCompat.getColor(this, R.color.colorBlack));
        blockeduser.setResource(R.drawable.ic_blocked_user);

        MenuObject cardmanagement = new MenuObject(getString(R.string.card_management));
        cardmanagement.setBgColor(ContextCompat.getColor(this, R.color.colorBlack));
        cardmanagement.setResource(R.drawable.ic_card_management);

        MenuObject logout = new MenuObject(getString(R.string.logout));
        logout.setBgColor(ContextCompat.getColor(this, R.color.colorBlack));
        logout.setResource(R.drawable.ic_logout);

        menuObjects.add(close);
        menuObjects.add(newprofile);
        menuObjects.add(otherprofile);
        menuObjects.add(notification);
        menuObjects.add(blockeduser);
        menuObjects.add(cardmanagement);
        menuObjects.add(logout);
        return menuObjects;
    }
}