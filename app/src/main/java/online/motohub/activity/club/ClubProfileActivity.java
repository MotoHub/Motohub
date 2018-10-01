package online.motohub.activity.club;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
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
import online.motohub.activity.BaseActivity;
import online.motohub.activity.PickerImageActivity;
import online.motohub.activity.UpgradeProfileActivity;
import online.motohub.adapter.EventsFindAdapter;
import online.motohub.adapter.TaggedProfilesAdapter;
import online.motohub.adapter.club.ClubPostsAdapter;
import online.motohub.adapter.club.ClubViewPagerAdapter;
import online.motohub.database.DatabaseHandler;
import online.motohub.fragment.BaseFragment;
import online.motohub.fragment.club.ClubHomeFragment;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.ClubGroupModel;
import online.motohub.model.EventAnswersModel;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsWhoIsGoingModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.GalleryImgModel;
import online.motohub.model.GalleryVideoModel;
import online.motohub.model.ImageModel;
import online.motohub.model.PaymentModel;
import online.motohub.model.PostsModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PromoterSubsResModel;
import online.motohub.model.PromotersFollowers1;
import online.motohub.model.PurchasedAddOnModel;
import online.motohub.model.RacingModel;
import online.motohub.model.SessionModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerResModel;
import online.motohub.model.promoter_club_news_media.PromoterSubs;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.CommonAPI;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.UploadFileService;
import online.motohub.util.UrlUtils;
import online.motohub.util.Utility;

public class ClubProfileActivity extends BaseActivity implements TabLayout.OnTabSelectedListener, ClubPostsAdapter.TotalRetrofitPostsResultCount, TaggedProfilesAdapter.TaggedProfilesSizeInterface {

    public static final String EXTRA_RESULT_DATA = "activity_video_picker_uri";
    private final int SUBSCRIPTION_EXPIRED = 0;
    private final int SUBSCRIPTION_PURCHASED = 1;
    @BindView(R.id.promoterCoLayout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.cover_photo_img_view)
    ImageView mCoverImg;
    @BindView(R.id.profile_img)
    CircleImageView mProfileImg;
    @BindView(R.id.promoterName)
    TextView mClubNameTv;
    @BindView(R.id.followBtn)
    TextView mFollowBtn;
    @BindView(R.id.followers_count_tv)
    TextView mFollowersCountTv;
    @BindView(R.id.view_pager_tab_layout)
    TabLayout mViewPagerTabLayout;
    @BindView(R.id.write_post_et)
    EditText mWritePostEt;
    @BindView(R.id.close_layout)
    FrameLayout mPostImgVideoCloseBtnLayout;
    @BindView(R.id.post_picture_img_view)
    ImageView mPostPicImgView;
    @BindView(R.id.play)
    ImageView mPlayIconImgView;
    @BindView(R.id.imageframe)
    RelativeLayout mPostImgVideoLayout;
    @BindView(R.id.tag_profiles_recycler_view)
    RecyclerView mTagProfilesRecyclerView;
    @BindString(R.string.internet_failure)
    String mInternetFailed;
    @BindString(R.string.no_clubs_err)
    String mNoClubsFoundErr;
    @BindView(R.id.write_post_card)
    RelativeLayout mWritePostCard;
    @BindView(R.id.writePostSeparator)
    View mWritePostSeparator;
    @BindView(R.id.tag_profile_img)
    ImageButton mTagFollowersImgBtn;
    @BindView(R.id.subscribeBtn)
    TextView mSubscribeBtn;
    @BindString(R.string.post_success)
    String mPostSuccess;
    @BindString(R.string.tag_err)
    String mTagErr;
    private ClubViewPagerAdapter mViewPagerAdapter;
    private PromotersResModel mPromotersResModel;
    private ProfileResModel mMyProfileResModel;
    private String mVideoPathUri, mPostImgUri;
    private int mClubGroupID;
    private ArrayList<ProfileResModel> mFollowingListData = new ArrayList<>();
    private ArrayList<ProfileResModel> mTaggedProfilesList = new ArrayList<>();
    private TaggedProfilesAdapter mTaggedProfilesAdapter;
    private boolean isSubscribed;
    private String mSubscriptionId = "";
    private PromotersFollowers1.Resource mPromoterFollowers1;
    private PromotersFollowers1.Meta meta;
    private int followCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_profile);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //callGetClubSubscription();
    }

    private void initView() {
        Bundle mBundle = getIntent().getExtras();
        FlexboxLayoutManager mFlexBoxLayoutManager = new FlexboxLayoutManager(this);
        mFlexBoxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        mFlexBoxLayoutManager.setFlexDirection(FlexDirection.ROW);
        mFlexBoxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        mTagProfilesRecyclerView.setLayoutManager(mFlexBoxLayoutManager);

        mTaggedProfilesAdapter = new TaggedProfilesAdapter(mTaggedProfilesList, this);
        mTagProfilesRecyclerView.setAdapter(mTaggedProfilesAdapter);

        if (mBundle != null) {
            mMyProfileResModel = (ProfileResModel) getIntent().getSerializableExtra(ProfileModel.MY_PROFILE_RES_MODEL);
            mPromotersResModel = (PromotersResModel) mBundle.get(PromotersModel.PROMOTERS_RES_MODEL);
            callGetClubsPromoters();
        }
        setUpPurchseUI(mPromotersResModel.getSubscription_fee());
    }

    private void callCheckFollow() {
        String mFilter = "FollowRelation=" + mMyProfileResModel.getID() + "_" + mPromotersResModel.getUserId();
        RetrofitClient.getRetrofitInstance().callCheckFollowers(this, mFilter, RetrofitClient.CHECK_FOLLOWER_STATUS);
    }

    private void callGetClubSubscription() {
        String mFilter = "(PromoterID=" + mPromotersResModel.getUserId() + ") AND (" + "ProfileID=" + mMyProfileResModel.getID() + ")";
        RetrofitClient.getRetrofitInstance().callGetPromoterSubs(this, mFilter, RetrofitClient.CALL_GET_PROMOTER_SUBS);
    }

    private void callGetClubsPromoters() {
        /*String mFilter = "id=" + mPromotersResModel.getID();
        RetrofitClient.getRetrofitInstance().callGetPromoters(this, mFilter, RetrofitClient.GET_PROMOTERS_RESPONSE);*/
        String mFilter = "PromoterUserID=" + mPromotersResModel.getUserId();
        RetrofitClient.getRetrofitInstance().callGetPromotersFollowers(this, mFilter, RetrofitClient.GET_PROMOTERS_RESPONSE);
    }

    private void setProfile() {
        try {
            mViewPagerAdapter = new ClubViewPagerAdapter(getSupportFragmentManager(), this, mPromotersResModel, mMyProfileResModel);
            mViewPager.setAdapter(mViewPagerAdapter);
            mViewPagerTabLayout.setupWithViewPager(mViewPager);
            mViewPager.setOffscreenPageLimit(4);
            mViewPagerTabLayout.addOnTabSelectedListener(this);
            if (mPromotersResModel != null) {
                setToolbar(mToolbar, mPromotersResModel.getName());
                showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
                if (mPromotersResModel.getProfileImage() != null && !mPromotersResModel.getProfileImage().isEmpty()) {
                    setImageWithGlide(mProfileImg, mPromotersResModel.getProfileImage(), R.drawable.default_profile_icon);
                }
                if (mPromotersResModel.getCoverImage() != null && !mPromotersResModel.getCoverImage().isEmpty()) {
                    GlideUrl mGlideUrl = new GlideUrl(UrlUtils.FILE_URL + mPromotersResModel.getCoverImage().trim(), new LazyHeaders.Builder()
                            .addHeader("X-DreamFactory-Api-Key", getString(R.string.dream_factory_api_key))
                            .build());
                    Glide.with(getApplicationContext())
                            .load(mGlideUrl)
                            .apply(new RequestOptions()
                                    .error(R.drawable.default_cover_img)
                                    .centerCrop()
                                    .dontAnimate())
                            .into(mCoverImg);
                }
                setToolbar(mToolbar, mPromotersResModel.getName());
                mClubNameTv.setText(mPromotersResModel.getName());
                List<PromoterFollowerResModel> mPromoterFollowerResModelList =
                        mPromotersResModel.getPromoterFollowerByPromoterUserID();
                PromoterFollowerResModel mPromoterFollowerResModel = new PromoterFollowerResModel();
                mPromoterFollowerResModel.setProfileID(mMyProfileResModel.getID());

               /* if (isAlreadyFollowed()) {
                    mPromotersResModel.setIsFollowing(true);
                }
                if (mPromotersResModel.getIsFollowing()) {
                    mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                    mFollowBtn.setText(R.string.following);
                }
                mFollowersCountTv.setText(String.valueOf(mPromoterFollowerResModelList.size()));*/

                if (meta != null) {
                    followCount = meta.getCount();
                    mFollowersCountTv.setText(String.valueOf(followCount));
                }

                ArrayList<ClubGroupModel> mClubGroupList = mPromotersResModel.getClubGroupByClubUserID();

                for (int i = 0; i < mClubGroupList.size(); i++) {
                    if (String.valueOf(mClubGroupList.get(i).getMemberProfileID()).trim().equals(String.valueOf(mMyProfileResModel.getID()).trim())) {
                        mClubGroupID = mClubGroupList.get(i).getID();
                        if (mClubGroupList.get(i).getStatus() == 1) {
                            isSubscribed = false;
                            mSubscribeBtn.setText(getString(R.string.pending));
                            mSubscribeBtn.setEnabled(false);
                            mWritePostCard.setVisibility(View.GONE);
                        } else if (mClubGroupList.get(i).getStatus() == 2) {
                            isSubscribed = true;
                            mSubscribeBtn.setText(getString(R.string.un_subscribe));
                            mWritePostCard.setVisibility(View.VISIBLE);
                        } else if (mClubGroupList.get(i).getStatus() == 3) {
                            isSubscribed = false;
                            mSubscribeBtn.setText(getString(R.string.Subscribe));
                            mWritePostCard.setVisibility(View.GONE);
                        }
                        break;
                    } else {
                        isSubscribed = false;
                        mSubscribeBtn.setText(getString(R.string.Subscribe));
                        mWritePostCard.setVisibility(View.GONE);
                    }
                }
                // QueryItemDetails();
            }
            callCheckFollow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isAlreadyFollowed() {
        String mFollowRelation = mMyProfileResModel.getID() + "_" + mPromotersResModel.getUserId();
        ArrayList<PromoterFollowerResModel> mPromoterFollowerResModelList = mPromotersResModel.getPromoterFollowerByPromoterUserID();
        for (int i = 0; i < mPromoterFollowerResModelList.size(); i++) {
            if (mPromoterFollowerResModelList.get(i).getFollowRelation().trim().equals(mFollowRelation)) {
                return true;
            }
        }
        return false;
    }


    @OnClick({R.id.toolbar_back_img_btn, R.id.followBtn, R.id.subscribeBtn, R.id.post_btn, R.id.add_post_img, R.id.add_post_video, R.id.remove_post_img_btn, R.id.imageframe, R.id.tag_profile_img, R.id.cover_photo_img_view})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.subscribeBtn:
                String mSubsStr = mSubscribeBtn.getText().toString();
                if (mPurchaseDialog != null && mSubsStr.equals(getString(R.string.Subscribe)))
                    mPurchaseDialog.show();
                else
                    apiCallForUnSubscription();
                break;
            case R.id.followBtn:
                if (!mPromotersResModel.getIsFollowing()) {
                    CommonAPI.getInstance().callFollowPromoter(this, mPromotersResModel.getUserId(), mMyProfileResModel.getID());
                } else {
                    showProfileViewDialog();
                }
                break;
            case R.id.post_btn:
                /*try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                break;
            case R.id.add_post_img:
                // showAppDialog(AppDialogFragment.BOTTOM_ADD_IMG_DIALOG, null);
                break;
            case R.id.add_post_video:
                //showAppDialog(AppDialogFragment.BOTTOM_ADD_VIDEO_DIALOG, null);
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
            case R.id.tag_profile_img:
               /* if (mFollowingListData.size() > 0) {
                    launchTagFollowersProfileDialog();
                } else {
                    getFollowingProfileList();
                }*/
                break;

            case R.id.cover_photo_img_view:
                if (!mPromotersResModel.getCoverImage().trim().isEmpty()) {
                    moveLoadImageScreen(this, mPromotersResModel.getCoverImage().trim());
                }
                break;
        }
    }

    private void showUnSubsAlert() {
        showToast(this, "You have to go playstore for unsubscribtion");
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
            service_intent.putExtra("profileid", mMyProfileResModel.getID());
            service_intent.putExtra("dest_file", destFilePath);
            service_intent.putExtra("running", count + 1);
            service_intent.putExtra("flag", 1);
            service_intent.putExtra("usertype", "club_user");
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

        } catch (Exception e) {
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

    }

    private void getFollowingProfileList() {
        String mMyFollowingsID = Utility.getInstance().getMyFollowersFollowingsID(mMyProfileResModel.getFollowprofile_by_ProfileID(), false);
        if (mMyFollowingsID.isEmpty()) {
            showSnackBar(mCoordinatorLayout, mTagErr);
            return;
        }
        ArrayList<ClubGroupModel> mClubGroupList = mPromotersResModel.getClubGroupByClubUserID();
        StringBuilder mClubMembersID = new StringBuilder();
        String mClubGroupMembers = "";
        if (mClubGroupList != null && !mClubGroupList.isEmpty()) {
            for (int i = 0; i < mClubGroupList.size(); i++) {
                if (mClubGroupList.get(i).getMemberProfileID() == mMyProfileResModel.getID())
                    continue;
                mClubMembersID.append(mClubGroupList.get(i).getMemberProfileID()).append(",");
            }
            mClubMembersID.deleteCharAt(mClubMembersID.length() - 1);
            mClubGroupMembers = String.valueOf(mClubMembersID);
        }

        String mBlockedUsers = Utility.getInstance().getMyBlockedUsersID(mMyProfileResModel.getBlockedUserProfilesByProfileID(),
                mMyProfileResModel.getBlockeduserprofiles_by_BlockedProfileID());

        String mFilter = "id IN (" + mMyFollowingsID + ")";
        if (!mBlockedUsers.trim().isEmpty()) {
            mFilter = "(" + mFilter + ") AND ( id NOT IN (" + mBlockedUsers + "))";
        }
        if (!mClubGroupMembers.trim().isEmpty()) {
            mFilter = "(" + mFilter + ") OR ( id IN (" + mClubGroupMembers + "))";
        }
        RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_FOLLOWING_PROFILE_RESPONSE);
    }


   /* private void postUnSubscribeRequestToClub() {
        String mFilter = "(" + ClubGroupModel.MEMBER_PROFILE_ID + "=" + mMyProfileResModel.getID() + ") AND (" + ClubGroupModel.CLUB_USER_ID + "=" + mPromotersResModel.getUserId() + ")";
        RetrofitClient.getRetrofitInstance().callUnSubScribeClub(this, mFilter, RetrofitClient.DELETE_UNSUBSCRIBE_CLUB);
    }*/

    private void callUnFollowPromoterRequest() {
        String mFilter =
                "FollowRelation=" + mMyProfileResModel.getID() + "_" + mPromotersResModel.getUserId();
        RetrofitClient.getRetrofitInstance().callUnFollowPromoter(this,
                mFilter, RetrofitClient.GET_PROMOTER_UN_FOLLOW_RESPONSE);
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
            mJsonObject.addProperty(PostsModel.PROFILE_ID, mMyProfileResModel.getID());
            // mJsonObject.addProperty(PostsModel.WHO_POSTED_PROFILE_ID, mMyProfileResModel.getID());
            mJsonObject.addProperty(PostsModel.WHO_POSTED_PROFILE_ID, mUserId);
            mJsonObject.addProperty(PostsModel.WHO_POSTED_USER_ID, mUserId);
            mJsonObject.addProperty(PostsModel.IS_NEWS_FEED_POST, false);
            mJsonObject.addProperty(PostsModel.USER_TYPE, mPromotersResModel.getUserType());
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

    private void showProfileViewDialog() {
        ArrayList<String> mArrayList = new ArrayList<>();
        mArrayList.add(ProfileModel.FOLLOWING);
        mArrayList.add(String.valueOf(mPromotersResModel.getID()));
        mArrayList.add(mPromotersResModel.getName());
        mArrayList.add(mPromotersResModel.getProfileImage());
        showAppDialog(AppDialogFragment.DIALOG_PROMOTER_PROFILE_VIEW, mArrayList);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void alertDialogNegativeBtnClick() {
        super.alertDialogNegativeBtnClick();
        dismissAppDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        (mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition()))
                .onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.FOLLOWERS_FOLLOWING_RESULT:
                    ProfileResModel mMyProfileResModel = (ProfileResModel) data.getExtras()
                            .get(ProfileModel.MY_PROFILE_RES_MODEL);
                    assert mMyProfileResModel != null;
                    this.mMyProfileResModel = mMyProfileResModel;
                    setResult(RESULT_OK, new Intent()
                            .putExtra(ProfileModel.MY_PROFILE_RES_MODEL, this.mMyProfileResModel)
                            .putExtra(PromotersModel.PROMOTERS_RES_MODEL, mPromotersResModel));
                    break;
                case CAMERA_CAPTURE_REQ:
                    Uri mCameraPicUri = getImgResultUri(data);
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
                    Uri videoUri = data.getData();
                    if (videoUri != null) {
                        mVideoPathUri = getRealPathFromURI(videoUri);
                        setVideoPost();
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
                case GALLERY_VIDEO_REQ:
                    mVideoPathUri = data.getStringExtra(EXTRA_RESULT_DATA);
                    if (mVideoPathUri != null) {
                        setVideoPost();
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
                case AppConstants.POST_COMMENT_REQUEST:
                    mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition()).onActivityResult(requestCode, resultCode, data);
                    break;
                case EventsFindAdapter.EVENT_PAYMENT_REQ_CODE:
                    String mToken = data.getStringExtra("TOKEN");
                    apiCallForSubscription(mToken);
                    break;
            }
        }
    }

    private void apiCallForSubscription(String token) {
        RetrofitClient.getRetrofitInstance().postSubScribeRequestToClub(this, mPromotersResModel.getSubscription_planID(),
                PreferenceUtils.getInstance(this).getStrData(PreferenceUtils.USER_EMAIL), token,
                "SUBSCRIBE", mPromotersResModel.getStripeUserId(), RetrofitClient.POST_DATA_FOR_PAYMENT);
    }

    private void apiCallForUnSubscription() {
        RetrofitClient.getRetrofitInstance().postUnSubScribeRequestToClub(this, mSubscriptionId, "UNSUBSCRIBE", mPromotersResModel.getAccessToken(), RetrofitClient.PROMOTER_PAYMENT_UNSUBSCRIPTION);
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
        if (responseObj instanceof PromotersModel) {
            PromotersModel mPromotersModel = (PromotersModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_PROMOTERS_RESPONSE:
                    if (mPromotersModel.getResource() != null && mPromotersModel.getResource().size() > 0) {
                        mPromotersResModel = mPromotersModel.getResource().get(0);
                        setProfile();
                    } else {
                        showSnackBar(mCoordinatorLayout, mNoClubsFoundErr);
                    }
                    break;
            }
        } else if (responseObj instanceof PromoterFollowerModel) {
            PromoterFollowerModel mPromoterFollowerModel = (PromoterFollowerModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_PROMOTER_FOLLOW_RESPONSE:
                    if (mPromoterFollowerModel.getResource() != null
                            && mPromoterFollowerModel.getResource().size() > 0) {
                        mPromotersResModel.setIsFollowing(true);
                        ArrayList<PromoterFollowerResModel> mPromoterFollowerResModelList =
                                mPromotersResModel.getPromoterFollowerByPromoterUserID();
                        mPromoterFollowerResModelList
                                .add(mPromoterFollowerModel.getResource().get(0));
                        mPromotersResModel
                                .setPromoterFollowerByPromoterUserID(mPromoterFollowerResModelList);
                        mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                        mFollowBtn.setText(R.string.following);
                        followCount = followCount + 1;
                        mFollowersCountTv.setText(String.valueOf(followCount));
                        showSnackBar(mCoordinatorLayout, getString(R.string.follow_success));
                        mPromotersResModel.setIsFollowing(true);
                    }
                    break;
                case RetrofitClient.GET_PROMOTER_UN_FOLLOW_RESPONSE:
                    if (mPromoterFollowerModel.getResource() != null
                            && mPromoterFollowerModel.getResource().size() > 0) {
                        mPromotersResModel.setIsFollowing(false);
                        ArrayList<PromoterFollowerResModel> mPromoterFollowerResModelList =
                                mPromotersResModel.getPromoterFollowerByPromoterUserID();

                        for (int i = 0; i < mPromoterFollowerResModelList.size(); i++) {
                            if (mPromoterFollowerResModelList.get(i).getFollowRelation().trim().equals(mPromoterFollowerModel.getResource().get(0).getFollowRelation().trim())) {
                                mPromoterFollowerResModelList.remove(i);
                            }
                        }
                        mPromotersResModel
                                .setPromoterFollowerByPromoterUserID(mPromoterFollowerResModelList);
                        mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                        mFollowBtn.setText(R.string.follow);
                        followCount = followCount - 1;
                        mFollowersCountTv.setText(String.valueOf(followCount));
                        showSnackBar(mCoordinatorLayout, getString(R.string.un_follow_success));
                        mPromotersResModel.setIsFollowing(false);
                    }
                    break;
            }
            Intent mIntent = new Intent();
            mIntent.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
            mIntent.putExtra(PromotersModel.PROMOTERS_RES_MODEL, mPromotersResModel);
            setResult(RESULT_OK, mIntent);
        } else if (responseObj instanceof PostsModel) {
            PostsModel mPostsModel = (PostsModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_FEED_POSTS_RESPONSE:
                    if ((mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).isVisible()) {
                        ((BaseFragment) mViewPagerAdapter.getItem(0)).retrofitOnResponse(responseObj, responseType);
                    }
                    break;
                case RetrofitClient.SHARED_POST_RESPONSE:
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                        showSnackBar(mCoordinatorLayout, getResources().getString(R.string.post_shared));
                    }
                    break;
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

                        if (mTaggedProfilesList.size() > 0) {
                            mTagProfilesRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            mTagProfilesRecyclerView.setVisibility(View.GONE);
                        }

                        showSnackBar(mCoordinatorLayout, mPostSuccess);
                        ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);

                    }

                    break;

                case RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE:

                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {

                        ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);

                        showSnackBar(mCoordinatorLayout, getResources().getString(R.string.post_delete));
                    }

                    break;
            }
        } else if (responseObj instanceof EventsModel) {
            if ((mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mViewPagerAdapter.getItem(1)).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof EventsWhoIsGoingModel) {
            if ((mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof PaymentModel) {
            if (responseType == RetrofitClient.PROMOTER_PAYMENT_SUBSCRIPTION)
                subscriptionCall(responseObj);
            else
                unSubscribeCall(responseObj);
        } else if (responseObj instanceof RacingModel) {
            if ((mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof PurchasedAddOnModel) {
            if ((mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof EventAnswersModel) {
            if ((mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
            }

        } else if (responseObj instanceof SessionModel) {
            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            if ((mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof FeedShareModel) {

            ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);

        } else if (responseObj instanceof FeedLikesModel) {

            ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);

        } else if (responseObj instanceof ClubGroupModel) {
            ClubGroupModel mClubGroupModel = (ClubGroupModel) responseObj;
            switch (responseType) {
                case RetrofitClient.POST_SUBSCRIBE_CLUB:
                   /* isSubscribed = true;
                    mSubscribeBtn.setText(getString(R.string.pending));
                    mSubscribeBtn.setEnabled(false);
                    ArrayList<ClubGroupModel> mClubGroupList = mPromotersResModel.getClubGroupByClubUserID();
                    mClubGroupList.add(mClubGroupModel);
                    mPromotersResModel.setClubGroupByClubUserID(mClubGroupList);
                    if ((mViewPagerAdapter.getItem(0)).isVisible()) {
                        ((ClubHomeFragment) mViewPagerAdapter.getItem(0)).mRefresh = true;
                        ((BaseFragment) mViewPagerAdapter.getItem(0)).callGetEvents();
                    }*/
                    showSnackBar(mCoordinatorLayout, getString(R.string.subscribe_success));
                    break;
                case RetrofitClient.POST_UNSUBSCRIBE_CLUB:
                    mSubscribeBtn.setText(getString(R.string.Subscribe));
                    mWritePostCard.setVisibility(View.GONE);

                    showSnackBar(mCoordinatorLayout, getString(R.string.un_subscribe_success));
                    break;
                case RetrofitClient.DELETE_UNSUBSCRIBE_CLUB:
                    isSubscribed = false;
                    mSubscribeBtn.setText(getString(R.string.Subscribe));
                    ArrayList<ClubGroupModel> mNewClubGroupList = mPromotersResModel.getClubGroupByClubUserID();
                    for (int i = 0; i < mNewClubGroupList.size(); i++) {
                        if (mNewClubGroupList.get(i).getID() == mClubGroupID) {
                            mNewClubGroupList.remove(i);
                        }
                    }
                    mPromotersResModel.setClubGroupByClubUserID(mNewClubGroupList);
                    if ((mViewPagerAdapter.getItem(0)).isVisible()) {
                        ((ClubHomeFragment) mViewPagerAdapter.getItem(0)).mRefresh = true;
                        ((ClubHomeFragment) mViewPagerAdapter.getItem(0)).callGetUnSubscribeFeed();
                    }
                    showSnackBar(mCoordinatorLayout, getString(R.string.un_subscribe_success));
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
        } else if (responseObj instanceof GalleryImgModel) {
            ((BaseFragment) mViewPagerAdapter.getItem(2)).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof GalleryVideoModel) {
            ((BaseFragment) mViewPagerAdapter.getItem(3)).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof ProfileModel) {

            ProfileModel mProfileModel = (ProfileModel) responseObj;
            switch (responseType) {
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
        } else if (responseObj instanceof PromoterSubsResModel) {
            PromoterSubsResModel mPromoterSub = (PromoterSubsResModel) responseObj;
            if (mPromoterSub.getResource().size() != 0) {
                mSubscriptionId = mPromoterSub.getResource().get(0).getSubscriptionID();
                manageSubscription(true);
            } else {
                manageSubscription(false);
            }

        } else if (responseObj instanceof PromotersFollowers1) {
            PromotersFollowers1 promotersFollowers1 = (PromotersFollowers1) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_PROMOTERS_RESPONSE:
                    if (promotersFollowers1.getResource() != null && promotersFollowers1.getResource().size() > 0) {
                        mPromoterFollowers1 = promotersFollowers1.getResource().get(0);
                        meta = promotersFollowers1.getMeta();
                        setProfile();
                    } else {
                        showSnackBar(mCoordinatorLayout, getResources().getString(R.string.no_news_media_err));
                    }
                    break;
                case RetrofitClient.CHECK_FOLLOWER_STATUS:
                    if (promotersFollowers1.getResource() != null && promotersFollowers1.getResource().size() > 0) {
                        mPromotersResModel.setIsFollowing(true);
                        mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                        mFollowBtn.setText(R.string.following);
                    } else {
                        mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                        mFollowBtn.setText(R.string.follow);
                        mPromotersResModel.setIsFollowing(false);
                    }
                    break;
            }
        }
    }

    private void manageSubscription(boolean isSubscribed) {
        if (isSubscribed) {
            mSubscribeBtn.setText(getString(R.string.un_subscribe));
            mWritePostCard.setVisibility(View.VISIBLE);
            mWritePostSeparator.setVisibility(View.VISIBLE);
        } else {
            mSubscribeBtn.setText(getString(R.string.Subscribe));
            mWritePostCard.setVisibility(View.GONE);
            mWritePostSeparator.setVisibility(View.GONE);
        }
    }

    private void apiCallToUpdatePromoterSubs(String subs_id) {
        JsonObject mInputObj = new JsonObject();
        mInputObj.addProperty(PromoterSubs.PROMOTER_ID, mPromotersResModel.getUserId());
        mInputObj.addProperty(PromoterSubs.SUBS_STATUS, SUBSCRIPTION_PURCHASED);
        mInputObj.addProperty(PromoterSubs.SUBS_ID, subs_id);
        mInputObj.addProperty(PromoterSubs.PROFILE_ID, mMyProfileResModel.getID());
        mInputObj.addProperty(PromoterSubs.PLAN_ID, mPromotersResModel.getSubscription_planID());
        mInputObj.addProperty(PromoterSubs.PROMOTER_TYPE, mPromotersResModel.getUserType());
        JsonArray mArray = new JsonArray();
        mArray.add(mInputObj);
        RetrofitClient.getRetrofitInstance().callUpdatePromoterSubs(this, mArray, RetrofitClient.UPDATE_SUBSCRIPTION);
    }


    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else if (code == RetrofitClient.GET_FEED_POSTS_RESPONSE) {
            ((ClubHomeFragment) mViewPagerAdapter.getItem(0)).retrofitOnError(code, message);
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mCoordinatorLayout, mErrorMsg);
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
    public void retrofitOnError(int code, String message, int responseType) {
        super.retrofitOnError(code, message, responseType);
        switch (responseType) {
            case RetrofitClient.POST_SHARES:
                showSnackBar(mCoordinatorLayout, "Already shared this post");
                break;
        }
    }

    @Override
    public void retrofitOnFailure(final int responseType) {
        super.retrofitOnFailure(responseType);
        if ((mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).isVisible()) {
            ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnFailure(responseType);
        }
        showSnackBar(mCoordinatorLayout, mInternetFailed);
    }

    @Override
    public int getTotalPostsResultCount() {
        return ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).getTotalPostsResultCount();
    }

    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String dialogType, StringBuilder profileTypesStr, ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);
        switch (dialogType) {
            case AppDialogFragment.BOTTOM_SHARE_DIALOG:
                ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).alertDialogPositiveBtnClick(dialogType, position);
                break;
            case AppDialogFragment.DIALOG_PROMOTER_PROFILE_VIEW:
                callUnFollowPromoterRequest();
                dismissAppDialog();
                break;
            case AppDialogFragment.ALERT_SPECTATOR_UPDATE_DIALOG:
                String myProfileObj = new Gson().toJson(mMyProfileResModel);
                startActivity(new Intent(this, UpgradeProfileActivity.class).putExtra(AppConstants.MY_PROFILE_OBJ, myProfileObj));
                dismissAppDialog();
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
            default:
                ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).alertDialogPositiveBtnClick(dialogType, position);
                break;
        }
    }


    @Override
    public void retrofitOnFailure(int responseType, String message) {
        super.retrofitOnFailure(responseType, message);
        if (responseType == RetrofitClient.POST_DATA_FOR_PAYMENT) {
            showSnackBar(mCoordinatorLayout, message);
        }
    }


    private void subscriptionCall(Object responseObj) {
        PaymentModel mResponse = (PaymentModel) responseObj;
        if (mResponse.getStatus() != null && mResponse.getStatus().equals("active")) {
            apiCallToUpdatePromoterSubs(mResponse.getID());
        } else {
            String mErrorMsg = "Your card was declined.";
            if (mResponse.getMessage() != null) {
                mErrorMsg = mResponse.getMessage();
            }
            mErrorMsg = mErrorMsg + " " + getString(R.string.try_again);
            showToast(this, mErrorMsg);
        }
    }

    private void unSubscribeCall(Object responseObj) {
        PaymentModel mResponse = (PaymentModel) responseObj;
        if (mResponse.getStatus() != null) {
            deleteSubsDataInTable();
        } else {
            String mErrorMsg = "Your card was declined.";
            if (mResponse.getMessage() != null) {
                mErrorMsg = mResponse.getMessage();
            }
            mErrorMsg = mErrorMsg + " " + getString(R.string.try_again);
            showToast(this, mErrorMsg);
        }
    }

    private void deleteSubsDataInTable() {
        String mFilter = "(PromoterID=" + mPromotersResModel.getUserId() + ") AND (" + "ProfileID=" + mMyProfileResModel.getID() + ")";
        RetrofitClient.getRetrofitInstance().callRemovePromoterSubs(this, mFilter, RetrofitClient.CALL_REMOVE_PROMOTER_SUBS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void notifyEmptyTaggedProfilesList(ArrayList<ProfileResModel> mTaggedProfilesList) {
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
            DialogManager.showProgress(ClubProfileActivity.this);
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
                showToast(ClubProfileActivity.this, getString(R.string.uploading_video));
                startUploadVideoService();
            }
        }
    }
}
