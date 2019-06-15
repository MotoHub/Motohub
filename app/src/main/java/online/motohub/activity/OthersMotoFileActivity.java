package online.motohub.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.adapter.PostsAdapter;
import online.motohub.application.MotoHub;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.CommonInterface;
import online.motohub.interfaces.CommonReturnInterface;
import online.motohub.interfaces.RetrofitResInterface;
import online.motohub.interfaces.SharePostInterface;
import online.motohub.model.BlockedUserModel;
import online.motohub.model.BlockedUserResModel;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.FollowProfileEntity;
import online.motohub.model.FollowProfileModel;
import online.motohub.model.FollowProfileModel1;
import online.motohub.model.LiveStreamRequestEntity;
import online.motohub.model.LiveStreamRequestResponse;
import online.motohub.model.LiveStreamResponse;
import online.motohub.model.NotificationBlockedUsersModel;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.model.SingleChatRoomModel;
import online.motohub.model.SingleChatRoomResModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.util.CommonAPI;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.StringUtils;
import online.motohub.util.Utility;

public class OthersMotoFileActivity extends BaseActivity
        implements PostsAdapter.TotalRetrofitPostsResultCount, CommonInterface {

    private static final int mDataLimit = 15;
    @BindView(R.id.my_moto_file_co_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.cover_photo_img_view)
    ImageView mMPCoverImg;
    @BindView(R.id.profile_img)
    CircleImageView mMPImg;
    @BindView(R.id.name_of_moto_tv)
    TextView mNameOfMotoTv;
    @BindView(R.id.name_of_driver_tv)
    TextView mNameOfDriverTv;
    @BindView(R.id.name_of_following_count_tv)
    TextView mFollowingCount;
    @BindView(R.id.name_of_followers_count_tv)
    TextView mFollowersCount;
    @BindView(R.id.posts_recycler_view)
    RecyclerView mPostsRecyclerView;
    @BindView(R.id.follow_un_follow_img_btn)
    ImageButton mFollowUnFollowImgBtn;
    @BindView(R.id.follow_un_follow_tv)
    TextView mFollowUnFollowTv;
    @BindView(R.id.block_box)
    RelativeLayout mBlockBox;
    @BindView(R.id.block_tv)
    TextView mBlockTxt;
    @BindString(R.string.follow_success)
    String mFollowSuccess;
    @BindString(R.string.un_follow_success)
    String mUnFollowSuccess;
    @BindString(R.string.un_follow)
    String mUnFollowStr;
    @BindString(R.string.follow)
    String mFollowStr;
    @BindString(R.string.un_block)
    String mUnBlock;
    @BindView(R.id.other_profiles_details)
    LinearLayout mlinear_other_profiles;
    @BindView(R.id.shimmer_otherprofiles)
    ShimmerFrameLayout mShimmer_other_profiles;
    /* CommonReturnInterface mCommonReturnInterface = new CommonReturnInterface() {
         @Override
         public void onSuccess(int type) {
             if (type == 1) {
                 startSingleStream();
             } else {
                 viewLiveStream();
             }
         }
     };*/
    SingleChatRoomResModel mSingleChatRoomResModel;
    int mOtherProfileID = 0, mMyProfileID = 0;
    CommonReturnInterface mCommonReturnInterface = new CommonReturnInterface() {
        @Override
        public void onSuccess(int type) {
            if (type == 1) {
                startSingleStream();
            } else {
                viewLiveStream();
            }
        }
    };
    private LinearLayoutManager mPostsLayoutManager;
    private ArrayList<PostsResModel> mPostsList = new ArrayList<>();
    private PostsAdapter mPostsAdapter;
    private ProfileResModel mMyProfileResModel, mOtherProfileResModel;
    private int mPostsRvOffset = 0, mPostsRvTotalCount = -1;
    private boolean mIsPostsRvLoading = true;
    private FeedShareModel mSharedFeed;
    private int mCurrentPostPosition;
    private ArrayList<LiveStreamRequestEntity> mRequestedUsersList = new ArrayList<>();
    private ArrayList<BlockedUserResModel> mBlockedUsersList = new ArrayList<>();
    private boolean isDelete = false;
    private int liveStreamID = 0;
    private String mLiveStreamName = "";
    private boolean mIsAlreadyBlocked;
    private boolean mIsAlreadyFollowing;
    private boolean isBlockAPI;
    RetrofitResInterface mRetrofitResInterface = new RetrofitResInterface() {
        @Override
        public void retrofitOnResponse(Object responseObj, int responseType) {
            if (responseObj instanceof FollowProfileModel) {
                sysOut(responseObj.toString());
                FollowProfileModel mResponse = (FollowProfileModel) responseObj;
                if (mResponse.getResource().size() > 0) {
                    switch (responseType) {
                        case RetrofitClient.PROFILE_IS_ALREADY_FOLLOWED:
                            mIsAlreadyFollowing = mResponse.getResource().size() > 0;
                            setFollowUnFollowContent();
                            break;
                        case RetrofitClient.FOLLOW_PROFILE_RESPONSE:
                            updateFollowProfile(mResponse.getResource().get(0));
                            break;
                        case RetrofitClient.UN_FOLLOW_PROFILE_RESPONSE:
                            updateUnFollowProfile(mResponse.getResource().get(0));
                            break;
                        case RetrofitClient.UN_FOLLOW_MY_PROFILE_RESPONSE:
                            updateUnFollowMyProfile(mResponse.getResource().get(0));
                            break;
                        case RetrofitClient.UN_FOLLOW_BOTH_PROFILE_RESPONSE:
                            mIsAlreadyFollowing = false;
                            if (mResponse.getResource().size() > 1) {
                                FollowProfileEntity mMyFollowingEntity, mMyFollowerEntity;
                                if (mResponse.getResource().get(0).getProfileID() == mMyProfileID) {
                                    mMyFollowingEntity = mResponse.getResource().get(0);
                                    mMyFollowerEntity = mResponse.getResource().get(1);
                                } else {
                                    mMyFollowerEntity = mResponse.getResource().get(0);
                                    mMyFollowingEntity = mResponse.getResource().get(1);
                                }
                                updateUnFollowBothProfile(mMyFollowingEntity, mMyFollowerEntity);
                            } else {
                                updateUnFollowMyProfile(mResponse.getResource().get(0));
                            }
                            break;
                        default:
                            break;
                    }
                    setResult();
                } else {
                    showToast(OthersMotoFileActivity.this, getString(R.string.something_wrong));
                }
            } else if (responseObj instanceof BlockedUserModel) {
                sysOut(responseObj.toString());
                BlockedUserModel mResponse = (BlockedUserModel) responseObj;
                if (mResponse.getResource().size() > 0) {
                    switch (responseType) {
                        case RetrofitClient.CALL_BLOCK_USER_PROFILE:
                            updateBlockProfile(mResponse.getResource().get(0));
                            break;
                        case RetrofitClient.CALL_UN_BLOCK_USER_PROFILE:
                            updateUnBlockProfile(mResponse.getResource().get(0));
                            break;
                    }
                    setResult();
                } else {
                    showToast(OthersMotoFileActivity.this, getString(R.string.something_wrong));
                }
            } else if (responseObj instanceof SessionModel) {
                SessionModel mSessionModel = (SessionModel) responseObj;
                if (mSessionModel.getSessionToken() == null) {
                    PreferenceUtils.getInstance(OthersMotoFileActivity.this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
                } else {
                    PreferenceUtils.getInstance(OthersMotoFileActivity.this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
                }
                showToast(OthersMotoFileActivity.this, getString(R.string.session_updated));
            }
        }

        @Override
        public void retrofitOnError(int code, String message) {
            if (message.equals("Unauthorized") || code == 401) {
                RetrofitClient.getRetrofitInstance().callUpdateSession(OthersMotoFileActivity.this, mRetrofitResInterface, RetrofitClient.UPDATE_SESSION_RESPONSE);
            } else {
                showToast(OthersMotoFileActivity.this, getString(R.string.internet_err));
            }
        }


        @Override
        public void retrofitOnSessionError(int code, String message) {
            retrofitOnSessionError(code, message);
        }

        @Override
        public void retrofitOnFailure() {
            showToast(OthersMotoFileActivity.this, getString(R.string.internet_err));
        }
    };
    private String mShareTxt = "";
    SharePostInterface mShareTextWithPostInterface = new SharePostInterface() {
        @Override
        public void onSuccess(String shareMessage) {
            mShareTxt = shareMessage;
            CommonAPI.getInstance().callPostShare(OthersMotoFileActivity.this, mPostsList.get(mCurrentPostPosition), mMyProfileResModel.getID());
        }
    };

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_moto_file);
        ButterKnife.bind(this);
        if (!isNetworkConnected(this)) {
            showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
            return;
        }
        initView();
    }

    private void initView() {
        mlinear_other_profiles.setVisibility(View.GONE);
        mShimmer_other_profiles.startShimmerAnimation();
        AppConstants.LIVE_STREAM_CALL_BACK = this;
        setToolbar(mToolbar, "MotoHUB");
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        assert getIntent().getExtras() != null;
        if (getIntent().getExtras().getBoolean(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY)) {
            try {
                JSONObject mJsonObject = new JSONObject(getIntent().getExtras().getString(MyFireBaseMessagingService.ENTRY_JSON_OBJ));
                mOtherProfileID = Integer.parseInt(mJsonObject.getJSONObject("Details").get("ProfileID").toString());
                mMyProfileID = Integer.parseInt(mJsonObject.getJSONObject("Details").get("FollowsProfileID").toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mOtherProfileID = getIntent().getExtras().getInt(AppConstants.OTHER_PROFILE_ID);
            mMyProfileID = getIntent().getExtras().getInt(AppConstants.MY_PROFILE_ID);
        }
        mPostsLayoutManager = new LinearLayoutManager(this);
        mPostsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPostsRecyclerView.setLayoutManager(mPostsLayoutManager);
        mPostsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
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
        });
        callGetProfile(mMyProfileID, RetrofitClient.GET_PROFILE_RESPONSE);
    }

    private void setToolbarTitle() {
        String mToolbarTitle;
        assert mOtherProfileResModel != null;
        mToolbarTitle = Utility.getInstance().getUserName(mOtherProfileResModel);
        setToolbar(mToolbar, mToolbarTitle);
    }

    private void callGetProfile(int mProfileID, int responseType) {
        String mFilter = "id=" + mProfileID;
        RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, responseType);
    }

    private boolean isStartLiveEnable() {
        boolean isTrue = false;
        for (int i = 0; i < mRequestedUsersList.size(); i++) {
            if (mMyProfileID == mRequestedUsersList.get(i).getReceiverProfileID() && mRequestedUsersList.get(i).getStatus() == 1) {
                isTrue = true;
                break;
            }
        }
        return isTrue;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setPostAdapter() {
        if (mPostsAdapter == null) {
            mPostsAdapter = new PostsAdapter(mPostsList, mMyProfileResModel, this, true);
            mPostsRecyclerView.setAdapter(mPostsAdapter);
        } else {
            mPostsAdapter.notifyDataSetChanged();
        }
    }

    private void isAlreadyFollowed() {
        try {
            String mFollowRelation = mOtherProfileResModel.getID() + "_" + mMyProfileResModel.getID();
            RetrofitClient.getRetrofitInstance().callGetIsAlreadyFollowedUser(this, mFollowRelation, RetrofitClient.PROFILE_IS_ALREADY_FOLLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setProfile() {
        try {
            isAlreadyFollowed();
            getFollowerscount();
            getFollowingcount();
            mIsAlreadyBlocked = Utility.getInstance()
                    .isAlreadyBlocked(mMyProfileResModel.getBlockedUserProfilesByProfileID(), mOtherProfileResModel.getID());
            if (mIsAlreadyBlocked) {
                mBlockTxt.setText(getString(R.string.un_block));
            } else {
                mBlockTxt.setText(getString(R.string.block));
            }
            if (!mOtherProfileResModel.getProfilePicture().isEmpty()) {
                setImageWithGlide(mMPImg, mOtherProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);
            } else {
                mMPImg.setImageResource(R.drawable.default_profile_icon);
            }
            if (!mOtherProfileResModel.getCoverPicture().isEmpty()) {
                setImageWithGlide(mMPCoverImg, mOtherProfileResModel.getCoverPicture(), R.drawable.default_cover_img);
            } else {
                mMPCoverImg.setImageResource(R.drawable.default_cover_img);
            }
            mNameOfMotoTv.setText(Utility.getInstance().getUserName(mOtherProfileResModel));
            if (Utility.getInstance().isSpectator(mOtherProfileResModel)) {
                mNameOfDriverTv.setVisibility(View.GONE);
            } else {
                mNameOfDriverTv.setVisibility(View.VISIBLE);
                mNameOfDriverTv.setText(mOtherProfileResModel.getMotoName());
            }
            mShimmer_other_profiles.stopShimmerAnimation();
            mShimmer_other_profiles.setVisibility(View.GONE);
            mlinear_other_profiles.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProfilePosts() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                String mFilter;
                StringBuilder mBlockedProfiledId = new StringBuilder();
                ArrayList<BlockedUserResModel> mResBlockedModel = mOtherProfileResModel.getBlockedUserProfilesByProfileID();
                String mBlockedUsers = "";
                if (mResBlockedModel != null && !mResBlockedModel.isEmpty()) {
                    for (int i = 0; i < mResBlockedModel.size(); i++) {
                        mBlockedProfiledId.append(mResBlockedModel.get(i).getBlockedProfileID()).append(",");
                    }
                    mBlockedProfiledId.deleteCharAt(mBlockedProfiledId.length() - 1);
                    mBlockedUsers = String.valueOf(mBlockedProfiledId);
                }
                if (mBlockedUsers.trim().isEmpty()) {
                    mFilter = "((ProfileID=" + mOtherProfileResModel.getID()
                            + ")) AND ((user_type!='promoter') AND " +
                            "(user_type!='club') AND (user_type!='newsmedia') AND (user_type!='track') AND (user_type!='shop')) AND (ReportStatus == 0)";
                } else {
                    mFilter = "((ProfileID=" + mOtherProfileResModel.getID()
                            + ")) AND (ProfileID NOT IN (" + mBlockedUsers + ")) AND ((user_type!='promoter') AND (user_type!='club') AND (user_type!='newsmedia') AND (user_type!='track')AND (user_type!='shop')) AND (ReportStatus == 0)";
                }
                RetrofitClient.getRetrofitInstance().callGetProfilePosts(OthersMotoFileActivity.this, mFilter, RetrofitClient.GET_PROFILE_POSTS_RESPONSE, mDataLimit, mPostsRvOffset);
            }
        });
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.followers_box, R.id.following_box,
            R.id.follow_un_follow_box, R.id.block_box, R.id.chat_box, R.id.photos_videos_box,
            R.id.vehicle_info_box, R.id.live_box, R.id.your_build_box})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.followers_box:
                break;
            case R.id.following_box:
                break;
            case R.id.follow_un_follow_box:
                if (mMyProfileResModel != null) {
                    try {
                        boolean isBlockedMe = Utility.getInstance().isAlreadyBlockedMe(mMyProfileResModel.getBlockeduserprofiles_by_BlockedProfileID(), mOtherProfileID);
                        if (mOtherProfileResModel != null)
                            if (mIsAlreadyBlocked) {
                                showToast(OthersMotoFileActivity.this, getString(R.string.follow_err));
                            } else if (isBlockedMe) {
                                showToast(OthersMotoFileActivity.this, getString(R.string.block_follow_err));
                            } else {
                                if (mIsAlreadyFollowing) {
                                    CommonAPI.getInstance().callUnFollowProfile(OthersMotoFileActivity.this, mRetrofitResInterface,
                                            Utility.getInstance().getUnFollowRowID(mOtherProfileResModel.getFollowprofile_by_FollowProfileID(),
                                                    mMyProfileID, mOtherProfileID));
                                } else {
                                    CommonAPI.getInstance().callFollowProfile(OthersMotoFileActivity.this, mRetrofitResInterface, mMyProfileID, mOtherProfileID);
                                }
                            }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.block_box:
                if (mOtherProfileResModel != null)
                    if (mIsAlreadyBlocked) {
                        showAppDialog(AppDialogFragment.ALERT_UN_BLOCK_DIALOG, null);
                    } else {
                        showAppDialog(AppDialogFragment.ALERT_BLOCK_DIALOG, null);
                    }
                break;
            case R.id.chat_box:
                if (mSingleChatRoomResModel == null) {
                    if (mMyProfileResModel != null && mOtherProfileResModel != null) {
                        String mFilter = "(FromProfileID = " + mMyProfileResModel.getID() + ") AND (ToProfileID = " + mOtherProfileResModel.getID() + ")";
                        RetrofitClient.getRetrofitInstance().callGetSingleChatRoom(OthersMotoFileActivity.this, mFilter, RetrofitClient.GET_SINGLE_CHAT_ROOM);
                    }
                } else {
                    showSingleChatBox();
                }
                break;
            case R.id.your_build_box:
                startActivity(new Intent(OthersMotoFileActivity.this, ComingSoonActivity.class));
                break;
            case R.id.photos_videos_box:
                if (mOtherProfileResModel != null)
                    showAppDialog(AppDialogFragment.BOTTOM_VIEW_PHOTOS_VIDEOS_DIALOG, null);
                break;
            case R.id.vehicle_info_box:
                showVehicleInfoOtherProfile();
                break;
            case R.id.live_box:
                if (isStartLiveEnable()) {
                    DialogManager.showLiveOptionPopup(OthersMotoFileActivity.this, mCommonReturnInterface);
                } else {
                    viewLiveStream();
                }
                break;
        }
    }

    private void updateBlockProfile(BlockedUserResModel mEntity) {
        mIsAlreadyFollowing = false;
        mIsAlreadyBlocked = true;
        mBlockTxt.setText(mUnBlock);
        setFollowUnFollowContent();
        mBlockedUsersList = mMyProfileResModel.getBlockedUserProfilesByProfileID();
        mBlockedUsersList.add(mEntity);
        mMyProfileResModel.setBlockedUserProfilesByProfileID(mBlockedUsersList);
//        showToast(this, getString(R.string.block_success));
    }

    private void updateUnBlockProfile(BlockedUserResModel mEntity) {
        mIsAlreadyBlocked = false;
        mBlockTxt.setText(getString(R.string.block));
        mFollowUnFollowTv.setText(mFollowStr);
        mBlockedUsersList = mMyProfileResModel.getBlockedUserProfilesByProfileID();
        for (int i = 0; i < mBlockedUsersList.size(); i++) {
            if (mBlockedUsersList.get(i).getID() == mEntity.getID()) {
                mBlockedUsersList.remove(i);
            }
        }
        mMyProfileResModel.setBlockedUserProfilesByProfileID(mBlockedUsersList);
//        showToast(this, getString(R.string.un_block_success));
    }

    private void updateFollowProfile(FollowProfileEntity mEntity) {
        mIsAlreadyFollowing = true;
        ArrayList<FollowProfileEntity> mOtherFollowersList = mOtherProfileResModel.getFollowprofile_by_FollowProfileID();
        ArrayList<FollowProfileEntity> mMyFollowingsList = mMyProfileResModel.getFollowprofile_by_ProfileID();
        mOtherFollowersList.add(mEntity);
        mMyFollowingsList.add(mEntity);
        mOtherProfileResModel.setFollowprofile_by_FollowProfileID(mOtherFollowersList);
        mMyProfileResModel.setFollowprofile_by_ProfileID(mMyFollowingsList);
    }

    private void updateUnFollowProfile(FollowProfileEntity mEntity) {
        mIsAlreadyFollowing = false;
        ArrayList<FollowProfileEntity> mOtherFollowersList = mOtherProfileResModel.getFollowprofile_by_FollowProfileID();
        ArrayList<FollowProfileEntity> mMyFollowingsList = mMyProfileResModel.getFollowprofile_by_ProfileID();
        for (int i = 0; i < mOtherFollowersList.size(); i++) {
            if (mOtherFollowersList.get(i).getID() == mEntity.getID()) {
                mOtherFollowersList.remove(i);
                break;
            }
        }
        for (int i = 0; i < mMyFollowingsList.size(); i++) {
            if (mMyFollowingsList.get(i).getID() == mEntity.getID()) {
                mMyFollowingsList.remove(i);
                break;
            }
        }
        mOtherProfileResModel.setFollowprofile_by_FollowProfileID(mOtherFollowersList);
        mMyProfileResModel.setFollowprofile_by_ProfileID(mMyFollowingsList);
        if (isBlockAPI) {
            isBlockAPI = false;
            CommonAPI.getInstance().callBlockProfile(OthersMotoFileActivity.this, mRetrofitResInterface,
                    mMyProfileID, mOtherProfileID);
        }
    }

    private void updateUnFollowMyProfile(FollowProfileEntity mEntity) {
        ArrayList<FollowProfileEntity> mOtherFollowingsList = mOtherProfileResModel.getFollowprofile_by_ProfileID();
        ArrayList<FollowProfileEntity> mMyFollowersList = mMyProfileResModel.getFollowprofile_by_FollowProfileID();
        for (int i = 0; i < mOtherFollowingsList.size(); i++) {
            if (mOtherFollowingsList.get(i).getID() == mEntity.getID()) {
                mOtherFollowingsList.remove(i);
                break;
            }
        }
        for (int i = 0; i < mMyFollowersList.size(); i++) {
            if (mMyFollowersList.get(i).getID() == mEntity.getID()) {
                mMyFollowersList.remove(i);
                break;
            }
        }
        mOtherProfileResModel.setFollowprofile_by_ProfileID(mOtherFollowingsList);
        mMyProfileResModel.setFollowprofile_by_FollowProfileID(mMyFollowersList);
        if (isBlockAPI) {
            isBlockAPI = false;
            CommonAPI.getInstance().callBlockProfile(OthersMotoFileActivity.this, mRetrofitResInterface,
                    mMyProfileID, mOtherProfileID);
        }
    }

    private void updateUnFollowBothProfile(FollowProfileEntity mMyFollowingEntity, FollowProfileEntity mMyFollowerEntity) {
        mIsAlreadyFollowing = false;
        ArrayList<FollowProfileEntity> mOtherFollowersList = mOtherProfileResModel.getFollowprofile_by_FollowProfileID();
        ArrayList<FollowProfileEntity> mMyFollowingsList = mMyProfileResModel.getFollowprofile_by_ProfileID();
        for (int i = 0; i < mOtherFollowersList.size(); i++) {
            if (mOtherFollowersList.get(i).getID() == mMyFollowingEntity.getID()) {
                mOtherFollowersList.remove(i);
                break;
            }
        }
        for (int i = 0; i < mMyFollowingsList.size(); i++) {
            if (mMyFollowingsList.get(i).getID() == mMyFollowingEntity.getID()) {
                mMyFollowingsList.remove(i);
                break;
            }
        }
        mOtherProfileResModel.setFollowprofile_by_FollowProfileID(mOtherFollowersList);
        mMyProfileResModel.setFollowprofile_by_ProfileID(mMyFollowingsList);
        updateUnFollowMyProfile(mMyFollowerEntity);
    }

    private void setFollowUnFollowContent() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (mIsAlreadyFollowing) {
                    mFollowUnFollowImgBtn.setImageResource(R.drawable.unfollow_icon);
                    mFollowUnFollowTv.setText(mUnFollowStr);
//            showSnackBar(mCoordinatorLayout, mFollowSuccess);
                } else {
                    mFollowUnFollowImgBtn.setImageResource(R.drawable.follow_icon);
                    mFollowUnFollowTv.setText(mFollowStr);
//            showSnackBar(mCoordinatorLayout, mUnFollowSuccess);
                }
            }
        });
    }

    private void getFollowerscount() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                String FFilter = "FollowProfileID=" + mOtherProfileResModel.getID();
                RetrofitClient.getRetrofitInstance().callfollowfollowerscount(OthersMotoFileActivity.this, FFilter, RetrofitClient.GET_follower_count);
            }
        });
    }

    private void getFollowingcount() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                String FFilter = "ProfileID=" + mOtherProfileResModel.getID();
                RetrofitClient.getRetrofitInstance().callfollowfollowerscount(OthersMotoFileActivity.this, FFilter, RetrofitClient.GET_following_count);
            }
        });
    }

    private void setResult() {
        setFollowUnFollowContent();
        setResult(RESULT_OK, new Intent().putExtra(AppConstants.MY_PROFILE_OBJ, mMyProfileResModel).putExtra(AppConstants.IS_FOLLOW_RESULT, true));
    }

    private void startSingleStream() {
        try {
            String mStreamName = StringUtils.genRandomStreamName(OthersMotoFileActivity.this);
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(APIConstants.StreamName, mStreamName);
            mJsonObject.addProperty(APIConstants.CreatedProfileID, mMyProfileID);
            mJsonObject.addProperty(APIConstants.StreamProfileID, mOtherProfileID);
            JsonArray mJsonArray = new JsonArray();
            mJsonArray.add(mJsonObject);
            RetrofitClient.getRetrofitInstance().callPostLiveStream(OthersMotoFileActivity.this, mJsonArray);
        } catch (Exception e) {
            sysOut("" + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RetrofitClient.UPDATE_FEED_COUNT:
                    assert data.getExtras() != null;
                    mPostsAdapter.updateView(data.getIntExtra(AppConstants.POSITION, 0));
                    break;
                case AppConstants.POST_COMMENT_REQUEST:
                    assert data.getExtras() != null;
                    ArrayList<FeedCommentModel> mFeedCommentModel = (ArrayList<FeedCommentModel>) data.getExtras().getSerializable(PostsModel.COMMENTS_BY_POSTID);
                    mPostsAdapter.refreshCommentList(mFeedCommentModel);
                    break;
                case AppConstants.REPORT_POST_SUCCESS:
                    //TODO remove the reported post
                    mPostsRvOffset = 0;
                    //callGetProfile(mMyProfileID, RetrofitClient.GET_PROFILE_RESPONSE);
                    getProfilePosts();
                    break;
            }
        }
    }

    private void viewLiveStream() {
        Intent mGoWatchActivity = new Intent(OthersMotoFileActivity.this, ViewLiveVideoViewScreen2.class);
        mGoWatchActivity.putExtra(AppConstants.PROFILE_ID, mOtherProfileID);
        startActivity(mGoWatchActivity);
    }

    private void showVehicleInfoOtherProfile() {
        if (mOtherProfileResModel != null && mMyProfileResModel != null) {
            Bundle mBundle = new Bundle();
            //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
            EventBus.getDefault().postSticky(mMyProfileResModel);
            MotoHub.getApplicationInstance().setmOthersProfileResModel(mOtherProfileResModel);
            //mBundle.putSerializable(ProfileModel.OTHERS_PROFILE_RES_MODEL, mOtherProfileResModel);
            //mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
            startActivity(new Intent(this, VehicleInfoOtherProfiles.class)/*.putExtras(mBundle)*/);


            //MotoHub.getApplicationInstance().setmOtherProfileResModel(mOtherProfileResModel);
            //startActivity(new Intent(this, VehicleInfoOtherProfiles.class));

        }
    }

    private void showSingleChatBox() {
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(SingleChatRoomModel.SINGLE_CHAT_ROOM_RES_MODEL, mSingleChatRoomResModel);
        mBundle.putBoolean(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY, false);
        //mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
        //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
        EventBus.getDefault().postSticky(mMyProfileResModel);
        startActivity(new Intent(OthersMotoFileActivity.this, ChatBoxSingleActivity.class).putExtras(mBundle));
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof ProfileModel) {
            ProfileModel mProfileModel = (ProfileModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_PROFILE_RESPONSE:
                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        mMyProfileResModel = mProfileModel.getResource().get(0);
                        callGetProfile(mOtherProfileID, RetrofitClient.GET_OTHER_PROFILE_RESPONSE);
                    }
                    break;
                case RetrofitClient.GET_OTHER_PROFILE_RESPONSE:
                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        mOtherProfileResModel = mProfileModel.getResource().get(0);
                        setToolbarTitle();
                    }
                    setProfile();
                    getProfilePosts();
                    setFollowUnFollowContent();
                    break;
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
        } else if (responseObj instanceof LiveStreamRequestResponse) {
            LiveStreamRequestResponse mRequestedUserResponse = (LiveStreamRequestResponse) responseObj;
            if (mRequestedUserResponse.getResource().size() > 0) {
                mRequestedUsersList.addAll(mRequestedUserResponse.getResource());
            }
            getProfilePosts();
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
                Intent mCameraActivity = new Intent(OthersMotoFileActivity.this, CameraActivity.class);
                startActivity(mCameraActivity);
            }
        } else if (responseObj instanceof PostsModel) {
            PostsModel mPostsModel = (PostsModel) responseObj;
            if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                switch (responseType) {
                    case RetrofitClient.GET_PROFILE_POSTS_RESPONSE:
                        mPostsRvTotalCount = mPostsModel.getMeta().getCount();
                        mIsPostsRvLoading = false;
                        if (mPostsRvOffset == 0) {
                            mPostsList.clear();
                        }
                        mPostsList.addAll(mPostsModel.getResource());
                        mPostsRvOffset = mPostsRvOffset + mDataLimit;
                        break;
                    case RetrofitClient.SHARED_POST_RESPONSE:
                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            mPostsAdapter.resetShareCount(mSharedFeed);
                            showSnackBar(mCoordinatorLayout, getResources().getString(R.string.post_shared));
                        }
                        break;
                    case RetrofitClient.FEED_VIDEO_COUNT:
                        try {
                            if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                                mPostsAdapter.addViewCount(mPostsModel.getResource().get(0).getmViewCount());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case RetrofitClient.ADD_FEED_COUNT:
                        try {
                            if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                                mPostsAdapter.ViewCount(mPostsModel.getResource().get(0).getmViewCount());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            } else {
                if (mPostsRvOffset == 0) {
                    mPostsRvTotalCount = 0;
                }
            }
            setPostAdapter();
        } else if (responseObj instanceof NotificationBlockedUsersModel) {
            NotificationBlockedUsersModel mNotify = (NotificationBlockedUsersModel) responseObj;
            ArrayList<NotificationBlockedUsersModel> mPostNotification = mNotify.getResource();
            switch (responseType) {
                case RetrofitClient.BLOCK_NOTIFY:
                    mPostsAdapter.resetBlock(mPostNotification.get(0));
                    break;
                case RetrofitClient.UNBLOCK_NOTIFY:
                    mPostsAdapter.resetUnBlock(mPostNotification.get(0));
                    break;
            }
        } else if (responseObj instanceof SingleChatRoomModel) {
            SingleChatRoomModel mSingleChatRoomModel = (SingleChatRoomModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_SINGLE_CHAT_ROOM:
                    if (mSingleChatRoomModel.getResource() != null && mSingleChatRoomModel.getResource().size() > 0) {
                        mSingleChatRoomResModel = mSingleChatRoomModel.getResource().get(0);
                        showSingleChatBox();
                    } else {
                        List<SingleChatRoomResModel> mSingleChatRoomResModelList = new ArrayList<>();
                        SingleChatRoomResModel mSingleChatRoomResModel = new SingleChatRoomResModel();
                        mSingleChatRoomResModel.setFromProfileID(mMyProfileResModel.getID());
                        mSingleChatRoomResModel.setToProfileID(mOtherProfileResModel.getID());
                        String mFromToRelation = mMyProfileResModel.getID() + "_" + mOtherProfileResModel.getID();
                        mSingleChatRoomResModel.setFromToRelation(mFromToRelation);
                        mSingleChatRoomResModelList.add(mSingleChatRoomResModel);
                        mSingleChatRoomResModel = new SingleChatRoomResModel();
                        mSingleChatRoomResModel.setFromProfileID(mOtherProfileResModel.getID());
                        mSingleChatRoomResModel.setToProfileID(mMyProfileResModel.getID());
                        mFromToRelation = mOtherProfileResModel.getID() + "_" + mMyProfileResModel.getID();
                        mSingleChatRoomResModel.setFromToRelation(mFromToRelation);
                        mSingleChatRoomResModelList.add(mSingleChatRoomResModel);
                        mSingleChatRoomModel.setResource(mSingleChatRoomResModelList);
                        RetrofitClient.getRetrofitInstance().callCreateSingleChatRoom(this, mSingleChatRoomModel, RetrofitClient.CREATE_SINGLE_CHAT_ROOM);
                    }
                    break;
                case RetrofitClient.CREATE_SINGLE_CHAT_ROOM:
                    if (mSingleChatRoomModel.getResource() != null && mSingleChatRoomModel.getResource().size() > 0) {
                        mSingleChatRoomResModel = mSingleChatRoomModel.getResource().get(0);
                        showSingleChatBox();
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
            callGetProfile(mMyProfileID, RetrofitClient.GET_PROFILE_RESPONSE);
        } else if (responseObj instanceof FeedCommentModel) {
            FeedCommentModel mFeedCommentList = (FeedCommentModel) responseObj;
            switch (responseType) {
                case RetrofitClient.POST_COMMENTS:
                    ArrayList<FeedCommentModel> mNewFeedComment = mFeedCommentList.getResource();
                    mPostsAdapter.resetCommentAdapter(mNewFeedComment.get(0));
                    break;
            }
        } else if (responseObj instanceof FeedLikesModel) {
            FeedLikesModel mFeedLikesList = (FeedLikesModel) responseObj;
            ArrayList<FeedLikesModel> mNewFeedLike = mFeedLikesList.getResource();
            switch (responseType) {
                case RetrofitClient.POST_LIKES:
                    mPostsAdapter.resetLikeAdapter(mNewFeedLike.get(0));
                    break;
                case RetrofitClient.POST_UNLIKE:
                    mPostsAdapter.resetDisLike(mNewFeedLike.get(0));
                    break;
            }
        } else if (responseObj instanceof FeedShareModel) {
            FeedShareModel mFeedShareList = (FeedShareModel) responseObj;
            ArrayList<FeedShareModel> mNewFeedShare = mFeedShareList.getResource();
            switch (responseType) {
                case RetrofitClient.POST_SHARES:
                    mSharedFeed = mNewFeedShare.get(0);
                    CommonAPI.getInstance().callAddSharedPost(this, mPostsList.get(mCurrentPostPosition), mMyProfileResModel, mShareTxt);
                    break;
            }
        } else if (responseObj instanceof FollowProfileModel) {
            FollowProfileModel mResponse = (FollowProfileModel) responseObj;
            if (mResponse.getResource().size() > 0) {
                switch (responseType) {
                    case RetrofitClient.PROFILE_IS_ALREADY_FOLLOWED:
                        mIsAlreadyFollowing = mResponse.getResource().size() > 0;
                        setFollowUnFollowContent();
                        break;
                }
            }
        }
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
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
        String mErrorMsg = code + " - " + message;
        showSnackBar(mCoordinatorLayout, mErrorMsg);
    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        showSnackBar(mCoordinatorLayout, mInternetFailed);
    }

    @Override
    public int getTotalPostsResultCount() {
        return mPostsRvTotalCount;
    }

    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String dialogType,
                                            StringBuilder profileTypesStr, ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);
        switch (dialogType) {
            case AppDialogFragment.BOTTOM_SHARE_DIALOG:
                mCurrentPostPosition = position;
                DialogManager.showShareDialogWithCallback(this, mShareTextWithPostInterface);
                // CommonAPI.getInstance().callPostShare(this, mPostsList.get(mCurrentPostPosition), mMyProfileResModel.getID());
                break;
            case AppDialogFragment.ALERT_BLOCK_DIALOG:
                try {
                    isBlockAPI = true;
                    int rowID = Utility.getInstance().getUnFollowRowID(mOtherProfileResModel.getFollowprofile_by_FollowProfileID(),
                            mMyProfileID, mOtherProfileID);
                    boolean isFollowedMe = Utility.getInstance().isAlreadyFollowedMe(mMyProfileResModel.getFollowprofile_by_FollowProfileID(), mOtherProfileID);
                    if (mIsAlreadyFollowing && isFollowedMe) {
                        CommonAPI.getInstance().callUnFollowBothProfile(this, mRetrofitResInterface, rowID, mMyProfileID, mOtherProfileID);
                    } else if (mIsAlreadyFollowing) {
                        CommonAPI.getInstance().callUnFollowProfile(this, mRetrofitResInterface,
                                Utility.getInstance().getUnFollowRowID(mOtherProfileResModel.getFollowprofile_by_FollowProfileID(),
                                        mMyProfileID, mOtherProfileID));
                    } else if (isFollowedMe) {
                        CommonAPI.getInstance().callUnFollowMyProfile(this, mRetrofitResInterface, mMyProfileID, mOtherProfileID);
                    } else {
                        CommonAPI.getInstance().callBlockProfile(OthersMotoFileActivity.this, mRetrofitResInterface,
                                mMyProfileID, mOtherProfileID);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case AppDialogFragment.ALERT_UN_BLOCK_DIALOG:
                CommonAPI.getInstance().callUnBlockProfile(OthersMotoFileActivity.this, mRetrofitResInterface,
                        Utility.getInstance().getUnBlockRowID(mMyProfileResModel.getBlockedUserProfilesByProfileID(),
                                mMyProfileID, mOtherProfileID));
                break;
            case AppDialogFragment.BOTTOM_REPORT_ACTION_DIALOG:
                startActivityForResult(
                        new Intent(OthersMotoFileActivity.this, ReportActivity.class).putExtra(PostsModel.POST_ID, mPostsList.get(position).getID()).putExtra(ProfileModel.PROFILE_ID, mMyProfileID).putExtra(ProfileModel.USER_ID, mMyProfileResModel.getUserID()).putExtra(AppConstants.REPORT, AppConstants.REPORT_POST),
                        AppConstants.REPORT_POST_SUCCESS);
                break;
            case AppDialogFragment.BOTTOM_VIEW_PHOTOS_DIALOG:
                Intent photoIntent = new Intent(this, ProfileImgGalleryActivity.class);
                photoIntent.putExtra(ProfileImgGalleryActivity.EXTRA_PROFILE, mOtherProfileResModel.getID());
                photoIntent.putExtra(AppConstants.FROM_OTHER_PROFILE, true);
                startActivity(photoIntent);
                break;
            case AppDialogFragment.BOTTOM_VIEW_VIDEOS_DIALOG:
                Intent videoIntent = new Intent(this, ProfileVideoGalleryActivity.class);
                videoIntent.putExtra(ProfileVideoGalleryActivity.EXTRA_PROFILE, mOtherProfileResModel.getID());
                videoIntent.putExtra(AppConstants.FROM_OTHER_PROFILE, true);
                startActivity(videoIntent);
                break;
        }
    }

    @Override
    public void onSuccess() {
        isDelete = true;
        String mFilter = "ID=" + liveStreamID;
        RetrofitClient.getRetrofitInstance().callDeleteLiveStream(this, mFilter);
    }

}