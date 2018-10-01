package online.motohub.activity;

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
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

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
import online.motohub.database.DatabaseHandler;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.CommonInterface;
import online.motohub.interfaces.CommonReturnInterface;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.ImageModel;
import online.motohub.model.LiveStreamResponse;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PushTokenModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.CommonAPI;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.StringUtils;
import online.motohub.util.UploadFileService;
import online.motohub.util.Utility;

public class MyMotoFileActivity extends BaseActivity implements
        PopupMenu.OnMenuItemClickListener,
        PostsAdapter.TotalRetrofitPostsResultCount,
        TaggedProfilesAdapter.TaggedProfilesSizeInterface,
        CommonInterface {

    public static final String EXTRA_RESULT_DATA = "activity_video_picker_uri";
    private static final String TAG = MyMotoFileActivity.class.getName();
    private static final int mDataLimit = 15;
    private final int START_LIVE_STREAM = 1;
    private final int DELETE_LIVE_STREAM = 2;
    @BindView(R.id.swipe_refresh_view)
    SwipeRefreshLayout mSwipeRefreshLay;
    /* @BindView(R.id.appBarLayout)
     AppBarLayout mAppBarLay;*/
   /* @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLay;*/
    /*@BindView(R.id.my_moto_file_co_layout)
    CoordinatorLayout mCoordinatorLayout;*/
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
    private String mBlockedUsers = "";
    private ArrayList<ProfileResModel> mFullMPList = new ArrayList<>();
    private ArrayList<PostsResModel> mPostsList = new ArrayList<>();
    private PostsAdapter mPostsAdapter;
    private ArrayList<String> mMPSpinnerList = new ArrayList<>();
    private int mPostPos = 0;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updatePost(intent);
        }
    };
    private ArrayList<ProfileResModel> mFollowingListData = new ArrayList<>();
    private ArrayList<ProfileResModel> mTaggedProfilesList = new ArrayList<>();
    private TaggedProfilesAdapter mTaggedProfilesAdapter;
    private LinearLayoutManager mPostsLayoutManager;
    private int mPostsRvOffset = 0, mPostsRvTotalCount = 0;
    private FeedShareModel mSharedFeed;
    private int mCurrentPostPosition;
    private String mNewSharedID;
    private boolean mIsPostsRvLoading = true;
    private int mCurrentProfileID = 0;
    private String mVideoPathUri, mPostImgUri, mMyFollowings;
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
    private int liveStreamID = 0;
    private String mLiveStreamName = "";
    private boolean isExtraProfile = false;
    private int selProfileID = 0;
    private int prevProfilePos = 0;

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

    /*   @Override
       @SuppressWarnings("unchecked")
       protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
           mFullMPList.clear();
           mFullMPList.addAll((ArrayList<ProfileResModel>)
                   savedInstanceState.getSerializable(ProfileModel.MY_PROFILE_RES_MODEL));
           mMPSpinnerList.clear();
           mMPSpinnerList.addAll((ArrayList<String>)
                   savedInstanceState.getSerializable(ProfileModel.SPINNER_LIST));
           mPostImgUri = savedInstanceState.getString(PostsModel.POST_PICTURE);
           mPostsList.clear();
           mPostsList.addAll((ArrayList<PostsResModel>)
                   savedInstanceState.getSerializable(PostsModel.POST_MODEL));
           setPostAdapter();
           if (mPostImgUri != null) {
               setImageWithGlide(mPostPicImgView, Uri.parse(mPostImgUri));
               mPostImgVideoCloseBtnLayout.setVisibility(View.VISIBLE);
               mPostPicImgView.setVisibility(View.VISIBLE);
               mPostImgVideoLayout.setVisibility(View.VISIBLE);
           }
           mVideoPathUri = savedInstanceState.getString(PostsModel.PostVideoURL);
           if (mVideoPathUri != null) {
               setVideoPost();
           }
           mTaggedProfilesList.clear();
           mTaggedProfilesList.addAll((ArrayList<ProfileResModel>)
                   savedInstanceState.getSerializable(ProfileModel.FOLLOWING));
           if (mTaggedProfilesAdapter != null)
               mTaggedProfilesAdapter.notifyDataSetChanged();
           if (mTaggedProfilesList.size() > 0) {
               mTagProfilesRecyclerView.setVisibility(View.VISIBLE);
           } else {
               mTagProfilesRecyclerView.setVisibility(View.GONE);
           }
           changeAndSetProfile(getProfileCurrentPos());
           super.onRestoreInstanceState(savedInstanceState);
       }*/
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, new IntentFilter(UploadFileService.NOTIFY_POST_VIDEO_UPDATED));
        MotoHub.getApplicationInstance().myMotoFileOnResume();
    }

    /*   @Override
       protected void onSaveInstanceState(Bundle outState) {
           outState.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mFullMPList);
           outState.putSerializable(ProfileModel.SPINNER_LIST, mMPSpinnerList);
           outState.putSerializable(ProfileModel.FOLLOWING, mTaggedProfilesList);
           outState.putSerializable(PostsModel.POST_MODEL, mPostsList);
           outState.putString(PostsModel.POST_PICTURE, mPostImgUri);
           outState.putString(PostsModel.PostVideoURL, mVideoPathUri);
           outState.putString(PostsModel.PostVideoURL, mVideoPathUri);
           super.onSaveInstanceState(outState);
       }
   */
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
        MotoHub.getApplicationInstance().myMotoFileOnPause();
    }

    @Override
    protected void onDestroy() {
        if (selProfileID != 0)
            PreferenceUtils.getInstance(this).saveIntData(PreferenceUtils.CURRENT_PROFILE_POS, prevProfilePos);
        super.onDestroy();
    }

    private void initView() {
        setupUI(mCoordinatorLayout);
        AppConstants.LIVE_STREAM_CALL_BACK = this;
        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        showToolbarBtn(mToolbar, R.id.toolbar_settings_img_btn);
        showToolbarBtn(mToolbar, R.id.toolbar_messages_img_btn);
        showToolbarBtn(mToolbar, R.id.toolbar_notification_img_btn);
        selProfileID = getIntent().getIntExtra(AppConstants.MY_PROFILE_ID, 0);
        if (selProfileID != 0)
            prevProfilePos = getProfileCurrentPos();

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


        FlexboxLayoutManager mFlexBoxLayoutManager = new FlexboxLayoutManager(this);
        mFlexBoxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        mFlexBoxLayoutManager.setFlexDirection(FlexDirection.ROW);
        mFlexBoxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        mTagProfilesRecyclerView.setLayoutManager(mFlexBoxLayoutManager);

        mTaggedProfilesAdapter = new TaggedProfilesAdapter(mTaggedProfilesList, this);
        mTagProfilesRecyclerView.setAdapter(mTaggedProfilesAdapter);

    }

    private void setPostAdapter() {
        if (mPostsAdapter == null) {
            mPostsAdapter = new PostsAdapter(mPostsList, getCurrentProfile(), this);
            mPostsRecyclerView.setAdapter(mPostsAdapter);
        } else {
            mPostsAdapter.notifyDataSetChanged();
        }
    }

    private void getMyProfiles() {
        Collections.addAll(mMPSpinnerList, getResources().getStringArray(R.array.empty_array));
        int mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        String mFilter = "UserID = " + mUserID;
        if (isNetworkConnected())
            RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);
        else
            showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);

    }

    private void getProfilePosts() {
        String mFilter;
        ProfileResModel mProfileResModel = getCurrentProfile();
        mBlockedUsers = Utility.getInstance().getMyBlockedUsersID(mProfileResModel.getBlockedUserProfilesByProfileID(),
                mProfileResModel.getBlockeduserprofiles_by_BlockedProfileID());



       if (mBlockedUsers.trim().isEmpty()) {
           mFilter = "((ProfileID=" + mProfileResModel.getID() + ") OR (TaggedProfileID LIKE '%," +mProfileResModel.getID() + ",%') OR (SharedProfileID LIKE '%," + mProfileResModel.getID() +
                   ",%') AND ((user_type!='promoter') AND (user_type!='club') AND (user_type!='newsmedia') AND (user_type!='track')  AND (user_type!='shop')))";

       } else {

           mFilter = "((ProfileID=" + mProfileResModel.getID() + ") OR (TaggedProfileID LIKE '%," +mProfileResModel.getID() + ",%') OR (SharedProfileID LIKE '%," + mProfileResModel.getID() +
                   ",%') AND ((user_type!='promoter') AND (user_type!='club') AND (user_type!='newsmedia') AND (user_type!='track')  AND (user_type!='shop'))) AND (ProfileID NOT IN (" + mBlockedUsers + "))";
        }

        if (isNetworkConnected())
            RetrofitClient.getRetrofitInstance().callGetProfilePosts(this, mFilter, RetrofitClient.GET_PROFILE_POSTS_RESPONSE, mDataLimit, mPostsRvOffset);
        else
            showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
    }

    private void getTagProfileList() {
        ProfileResModel mMyProfileResModel = mFullMPList.get(PreferenceUtils.getInstance(this)
                .getIntData(PreferenceUtils.CURRENT_PROFILE_POS));
        String mMyFollowingsID = Utility.getInstance().getMyFollowersFollowingsID(mMyProfileResModel.getFollowprofile_by_ProfileID(), false);
        if (mMyFollowingsID.isEmpty()) {
            showSnackBar(mCoordinatorLayout, mTagErr);
            return;
        }
        mBlockedUsers = Utility.getInstance().getMyBlockedUsersID(mMyProfileResModel.getBlockedUserProfilesByProfileID(),
                mMyProfileResModel.getBlockeduserprofiles_by_BlockedProfileID());
        String mFilter;
        if (mBlockedUsers.trim().isEmpty()) {
            mFilter = "id IN (" + mMyFollowingsID + ")";
        } else {
            mFilter = "id IN (" + mMyFollowingsID + ") AND ( id NOT IN (" + mBlockedUsers + "))";
        }
        if (isNetworkConnected())
            RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_FOLLOWING_PROFILE_RESPONSE);
        else
            showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
    }

    private void uploadPicture(String imgUri) {
        File mFile = new File(Uri.parse(imgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", mFile.getName(), requestBody);
        if (isNetworkConnected())
            RetrofitClient.getRetrofitInstance().callUploadProfilePostImg(
                    this,
                    filePart,
                    RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE);
        else
            showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
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
            if (isNetworkConnected())
                RetrofitClient.getRetrofitInstance().callCreateProfilePosts(this, mJsonArray, RetrofitClient.CREATE_PROFILE_POSTS_RESPONSE);
            else
                showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

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
                showPopupMenu(v);
                break;
            case R.id.toolbar_messages_img_btn:
                if (mFullMPList.size() > 0) {
                    startActivity(new Intent(MyMotoFileActivity.this, ChatHomeActivity.class)
                            .putExtra(ProfileModel.MY_PROFILE_RES_MODEL, getCurrentProfile()));
                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.imageframe:
                if (mVideoPathUri != null) {
                    moveLoadVideoPreviewScreen(this, mVideoPathUri);
                }
                break;
            case R.id.toolbar_notification_img_btn:
                if (mFullMPList.size() > 0) {
                    startActivityForResult(new Intent(this, NotificationActivity.class)
                            .putExtra(ProfileModel.MY_PROFILE_RES_MODEL, getCurrentProfile()), AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.cover_photo_img_view:
                ProfileResModel mMyProfileResModel = mFullMPList.get(PreferenceUtils.getInstance(this)
                        .getIntData(PreferenceUtils.CURRENT_PROFILE_POS));
                if (!mMyProfileResModel.getCoverPicture().trim().isEmpty()) {
                    moveLoadImageScreen(this, mMyProfileResModel.getCoverPicture());
                }
                break;
            case R.id.ib_add_cover_photo:
            case R.id.profile_img:
            case R.id.name_of_moto_tv:
            case R.id.name_of_driver_tv:
                if (mFullMPList.size() > 0) {
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mFullMPList.get(PreferenceUtils
                            .getInstance
                                    (this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));
                    startActivityForResult(new Intent(this, UpdateProfileActivity.class).putExtras(mBundle), AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.vehicle_info_box:
                if (mFullMPList.size() > 0) {
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mFullMPList.get(PreferenceUtils
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
                        startActivityForResult(new Intent(this, FollowersFollowingActivity.class)
                                .putExtra(AppConstants.MY_PROFILE_OBJ, getCurrentProfile())
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
                        startActivityForResult(new Intent(this, FollowersFollowingActivity.class)
                                .putExtra(AppConstants.MY_PROFILE_OBJ, getCurrentProfile())
                                .putExtra(AppConstants.IS_FOLLOWERS, false), AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                    } else {
                        showToast(MyMotoFileActivity.this, getString(R.string.no_following_found_err));
                    }

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
            case R.id.remove_post_img_btn:
                mPostImgUri = null;
                mVideoPathUri = null;
                mPostPicImgView.setImageDrawable(null);
                mPostImgVideoCloseBtnLayout.setVisibility(View.GONE);
                mPostImgVideoLayout.setVisibility(View.GONE);
                mPostPicImgView.setVisibility(View.GONE);
                break;
            case R.id.tag_profile_img:
                if (mFullMPList.size() > 0) {
                    getTagProfileList();

                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.your_build_box:
                startActivity(new Intent(this, ComingSoonActivity.class));
                break;
            case R.id.photo_box:
                if (mFullMPList.size() > 0) {
                    Intent photoIntent = new Intent(MyMotoFileActivity.this, ProfileImgGalleryActivity.class);
                    ProfileResModel model = getCurrentProfile();
                    photoIntent.putExtra(ProfileImgGalleryActivity.EXTRA_PROFILE, model);
                    startActivity(photoIntent);

                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.video_box:
                if (mFullMPList.size() > 0) {
                    Intent videoIntent = new Intent(MyMotoFileActivity.this, ProfileVideoGalleryActivity.class);
                    ProfileResModel model1 = getCurrentProfile();
                    videoIntent.putExtra(ProfileVideoGalleryActivity.EXTRA_PROFILE, model1);
                    startActivity(videoIntent);
                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case R.id.live_box:
                DialogManager.showMultiLiveOptionPopup(this, mCommonReturnInterface, getString(R.string.single_stream), getString(R.string.live_stream));
                break;
            case R.id.btn_write_post:
                Gson mGson = new Gson();
                String mProfile = mGson.toJson(mFullMPList);
                startActivityForResult(new Intent(MyMotoFileActivity.this, WritePostActivity.class).putExtra(AppConstants.MY_PROFILE_OBJ, mProfile).putExtra(AppConstants.IS_NEWSFEED_POST, true), AppConstants.WRITE_POST_REQUEST);
                break;
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
            service_intent.putExtra("flag", 2);
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
            mCompressedVideoPath = "";
            mVideoPathUri = null;
            mPostImgUri = null;
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
            if (isNetworkConnected())
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
        if (isNetworkConnected())
            RetrofitClient.getRetrofitInstance().callDeleteLiveStream(this, mFilter);
        else
            showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
    }

    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String mDialogType, StringBuilder profileTypesStr, ArrayList<String> profileTypes, int position) {
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
                getProfilePosts();
                break;
            case AppDialogFragment.BOTTOM_DELETE_DIALOG:
                mPostPos = position;
                if (isNetworkConnected())
                    RetrofitClient.getRetrofitInstance().callDeleteProfilePosts(this, mPostsList.get(position).getID(), RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE);
                else
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                break;
            case AppDialogFragment.BOTTOM_EDIT_DIALOG:
                mPostPos = position;
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(PostsModel.POST_MODEL, mPostsList.get(position));
                mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mFullMPList.get(PreferenceUtils.getInstance(this)
                        .getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));
                startActivityForResult(new Intent(this, PostEditActivity.class).putExtras(mBundle), AppConstants.POST_UPDATE_SUCCESS);
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
        if (isNetworkConnected())
            RetrofitClient.getRetrofitInstance().callDeletePushToken(this, mFilter, RetrofitClient.FACEBOOK_LOGOUT);
        else
            showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
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
                } else {
                    showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
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
                startActivity(new Intent(MyMotoFileActivity.this, NotificationSettingsActivity.class));
                return true;
            case R.id.blocked_users:
                if (mFullMPList.size() > 0) {
                    if (getCurrentProfile().getBlockedUserProfilesByProfileID().size() > 0) {
                        startActivityForResult(new Intent(this, BlockedUsersActivity.class).putExtra(ProfileModel.MY_PROFILE_RES_MODEL,
                                getCurrentProfile()),
                                AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                    } else {
                        showToast(MyMotoFileActivity.this, getString(R.string.no_blocked_users_found_err));
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
            setFollowUnFollowContent(mProfileResModel);
        }
    }

    private void setFollowUnFollowContent(ProfileResModel mProfileResModel) {
        int followingCount = mProfileResModel.getFollowprofile_by_ProfileID().size();
        mFollowingCount.setText(String.valueOf(followingCount));
        int followersCount = mProfileResModel.getFollowprofile_by_FollowProfileID().size();
        mFollowersCount.setText(String.valueOf(followersCount));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
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
                case GALLERY_PIC_REQ:
                    assert data.getExtras() != null;
                    mVideoPathUri = null;
                    Uri mSelectedImgFileUri = (Uri) data.getExtras()
                            .get(PickerImageActivity.EXTRA_RESULT_DATA);
                    if (mSelectedImgFileUri != null) {
                        try {
                            File mPostImgFile = compressedImgFile(mSelectedImgFileUri,
                                    POST_IMAGE_NAME_TYPE, "");
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
                    mPostImgUri = null;
                    Uri videoUri = data.getData();
                    if (videoUri != null) {
                        mVideoPathUri = getRealPathFromURI(videoUri);
                        setVideoPost();
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
                case GALLERY_VIDEO_REQ:
                    mPostImgUri = null;
                    mVideoPathUri = data.getStringExtra(EXTRA_RESULT_DATA);
                    if (mVideoPathUri != null) {
                        setVideoPost();
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
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

        if (responseObj instanceof ProfileModel) {

            ProfileModel mProfileModel = (ProfileModel) responseObj;

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
                        mPostsRvTotalCount = -1;
                        setPostAdapter();
                        getProfilePosts();

                    } else {
                        showSnackBar(mCoordinatorLayout, mNoProfileErr);
                    }

                    break;

                case RetrofitClient.UPDATE_PROFILE_RESPONSE:
                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        saveInAppPurchasesLocally(Integer.parseInt(PROFILE_PURCHASED), String.valueOf(getCurrentProfile().getProfileType()));
                        getMyProfiles();
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

                    break;

                case RetrofitClient.CREATE_PROFILE_POSTS_RESPONSE:

                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {

                        mWritePostEt.setText("");
                        mVideoPathUri = null;
                        mPostImgUri = null;
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
                    CommonAPI.getInstance().callAddSharedPost(this, mPostsList.get(mCurrentPostPosition), getCurrentProfile());
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
            if (isNetworkConnected())
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
            if (isNetworkConnected())
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
        return mFullMPList.get(getProfileCurrentPos());
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

    class VideoCompressor extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogManager.showProgress(MyMotoFileActivity.this);
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
                showToast(MyMotoFileActivity.this, getString(R.string.uploading_video));
                startUploadVideoService();
            }
        }
    }

}
