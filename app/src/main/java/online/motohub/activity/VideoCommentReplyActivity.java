package online.motohub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import online.motohub.R;
import online.motohub.adapter.CommentTagAdapter;
import online.motohub.adapter.TaggedProfilesAdapter;
import online.motohub.adapter.VideoCommentsReplyAdapter;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.ImageModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.model.VideoCommentReplyModel;
import online.motohub.model.VideoCommentsModel;
import online.motohub.model.VideoReplyLikeModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.UrlUtils;
import online.motohub.util.Utility;


public class VideoCommentReplyActivity extends BaseActivity implements TaggedProfilesAdapter.TaggedProfilesSizeInterface {
    private static final int mDataLimit = 15;
    private static String mSearchStr = "";
    private static ArrayList<ProfileResModel> mSearchProfilesList = new ArrayList<>();
    private final long DELAY = 800;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindString(R.string.replies)
    String mToolbarTitle;
    @BindView(R.id.feeds_comments_list_view)
    RecyclerView mFeedCommentsListView;
    @BindView(R.id.user_img)
    CircleImageView mUserImg;
    @BindView(R.id.name_txt)
    TextView mUsernameTxt;
    @BindView(R.id.comment_edt)
    EditText mCommentEdt;
    @BindView(R.id.comment_user_img)
    CircleImageView mCommentUserImg;
    @BindView(R.id.comment_user_name_txt)
    TextView mCommentUserNameTxt;
    @BindView(R.id.comment_txt)
    TextView mCommentTxt;
    @BindView(R.id.main_lay)
    RelativeLayout mParentLay;
    @BindView(R.id.imageConstraintLay)
    ConstraintLayout mImageConstraintLay;
    @BindView(R.id.iv_post_image)
    ImageView mIvPostImage;
    @BindView(R.id.ivPost)
    ImageView mIvPost;
    @BindView(R.id.ivCommentImg)
    ImageView mIvCommentImg;
    @BindView(R.id.rvTagList)
    RecyclerView mRvTagList;
    private String mPostImgUri = null;
    private String imgUrl = "";
    private ArrayList<ProfileResModel> mFullMPList = new ArrayList<>();
    private String mFilter;
    private VideoCommentsModel mFeedCommentModel;
    private VideoCommentsReplyAdapter mFeedCommentsReplyAdapter;
    private ArrayList<VideoCommentReplyModel> mFeedCommentReplyList = new ArrayList<>();
    private ProfileResModel mMyProfileResModel;
    private int mCommentID;
    private String mCommentFilter;
    private ArrayList<String> mCommentTaggedUserList = new ArrayList<>();
    private ArrayList<Integer> mCommentTaggedUserIDs = new ArrayList<>();
    private HashMap<Integer, String> mCommentTaggedUserDetails = new HashMap<>();
    private ArrayList<ProfileResModel> mTaggedProfilesList = new ArrayList<>();
    private CommentTagAdapter mSearchProfileAdapter;
    private LinearLayoutManager mHorLayoutManager;
    private int mTagRvOffset = 0, mTagRvTotalCount = 0;
    private boolean mIsTagRvLoading;
    private String mTagSearchString = "";
    private int mSearchTextIndex;
    private String mCommentTaggedUserNames = "", mCommentUserIDs = "";
    private boolean isBackspaceClicked = false;
    private boolean isSelected = false;
    private int mCurrentIndexOfCommentTag;
    private boolean mSearchFriendList = false;
    private Timer timer = new Timer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_reply_list);
        ButterKnife.bind(this);
        initView();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

   /* @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mMyProfileResModel = (ProfileResModel) savedInstanceState
                .getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        mCommentID = savedInstanceState.getInt(PostsModel.POST_ID, 0);
        mPostImgUri = savedInstanceState.getString(PostsModel.POST_PICTURE, null);
        mFeedCommentReplyList.clear();
        assert savedInstanceState.getSerializable(ProfileModel.FOLLOWING) != null;
        ArrayList<VideoCommentReplyModel> mTempReplyList = (ArrayList<VideoCommentReplyModel>)
                savedInstanceState
                        .getSerializable(FeedCommentReplyModel
                                .REPLY_LIST);
        assert mTempReplyList != null;
        mFeedCommentReplyList.addAll(mTempReplyList);
        if (mFeedCommentsReplyAdapter != null)
            mFeedCommentsReplyAdapter.notifyDataSetChanged();
        mFeedCommentModel = (VideoCommentsModel) savedInstanceState.getSerializable(FeedCommentModel.COMMENT_LIST);
        if (mPostImgUri != null) {
            mImageConstraintLay.setVisibility(View.VISIBLE);
            setImageWithGlide(
                    mIvPostImage,
                    Uri.parse(mPostImgUri),
                    R.drawable.default_cover_img);
        }
        mFullMPList = (ArrayList<ProfileResModel>) savedInstanceState.getSerializable(ProfileModel.FULL_PROFILE_LIST);
    }*/

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
        outState.putInt(PostsModel.POST_ID, mCommentID);
        outState.putString(PostsModel.POST_PICTURE, mPostImgUri);
        outState.putSerializable(FeedCommentReplyModel.REPLY_LIST, mFeedCommentReplyList);
        outState.putSerializable(FeedCommentModel.COMMENT_LIST, mFeedCommentModel);
        outState.putSerializable(ProfileModel.FULL_PROFILE_LIST, mFullMPList);

        super.onSaveInstanceState(outState);
    }*/

    private void initView() {
        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY)) {
            try {
                assert getIntent().getExtras() != null;
                JSONObject mJsonObject = new JSONObject(getIntent().getExtras().getString(MyFireBaseMessagingService.ENTRY_JSON_OBJ));

                if (mJsonObject.getJSONObject("Details").has("CommentID")) {
                    mCommentID = Integer.parseInt(mJsonObject.getJSONObject("Details").get("CommentID").toString());
                    mCommentFilter = "ID = " + mCommentID;
                    mFilter = "CommentID = " + mCommentID;
                } else if (mJsonObject.getJSONObject("Details").has("VideoCommentID")) {
                    mCommentID = Integer.parseInt(mJsonObject.getJSONObject("Details").get("VideoCommentID").toString());
                    mCommentFilter = "ID = " + mCommentID;
                    mFilter = "CommentID = " + mCommentID;
                }
                getMotoProfiles();
                return;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //mMyProfileResModel = (ProfileResModel) getIntent().getSerializableExtra(ProfileModel.MY_PROFILE_RES_MODEL);
        //mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
        mFeedCommentModel = (VideoCommentsModel) getIntent().getSerializableExtra(FeedCommentModel.COMMENT_LIST);
        mCommentID = mFeedCommentModel.getId();
        mFilter = "CommentID=" + mCommentID;

        CommentTagAdapter mTaggedProfilesAdapter = new CommentTagAdapter(mTaggedProfilesList, this);
        mRvTagList.setAdapter(mTaggedProfilesAdapter);

        mHorLayoutManager = new LinearLayoutManager(this);
        mHorLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvTagList.setLayoutManager(mHorLayoutManager);

        mRvTagList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int mVisibleItemCount = mHorLayoutManager.getChildCount();
                int mTotalItemCount = mHorLayoutManager.getItemCount();
                int mFirstVisibleItemPosition = mHorLayoutManager.findFirstVisibleItemPosition();

                if (!mIsTagRvLoading && !(mTagRvOffset >= mTagRvTotalCount)) {
                    if ((mVisibleItemCount + mFirstVisibleItemPosition) >= mTotalItemCount
                            && mFirstVisibleItemPosition >= 0) {
                        mIsTagRvLoading = true;
                        getSearchProfileList(
                                RetrofitClient.PROFILE_FIND_FRIENDS_LOAD_MORE_RESPONSE);

                    }
                }
            }
        });

        mCommentEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                mSearchTextIndex = mCommentEdt.getSelectionStart();

                isBackspaceClicked = after < count;

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (isBackspaceClicked) {
                        if (s.toString().length() > 0) {
                            if ((s.toString().charAt(mSearchTextIndex - 1)) == '@') {
                                mSearchFriendList = true;
                                mCurrentIndexOfCommentTag = mSearchTextIndex;
                                mTagSearchString = "";
                            }
                        } else {
                            mSearchFriendList = false;
                            mRvTagList.setVisibility(View.GONE);
                        }
                    } else {
                        if (s.toString().length() > 0) {
                            if ((s.toString().charAt(mSearchTextIndex)) == '@' && !isSelected) {
                                mSearchFriendList = true;
                                mCurrentIndexOfCommentTag = mSearchTextIndex;
                                mTagSearchString = "";
                            } else if (isSelected) {
                                isSelected = false;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (mSearchFriendList) {

                    int length = mCommentEdt.getSelectionStart();
                    String tagSearch = "";

                    if (mCommentEdt.getText().toString().length() > 0 && length > mCurrentIndexOfCommentTag) {
                        tagSearch = mCommentEdt.getText().toString().substring(mCurrentIndexOfCommentTag + 1, length);
                    }

                    mTagSearchString = tagSearch;
                }

//avoid triggering event when text is too short
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mSearchFriendList) {
                                    findFriendsOrVehicles(mTagSearchString);
                                }
                            }
                        });
//
                    }

                }, DELAY);
            }

        });
        getCommentsReply(mFilter);
    }

    private void findFriendsOrVehicles(final String searchStr) {

        mSearchStr = searchStr;
        mSearchProfilesList.clear();
        mTagRvOffset = 0;
        mIsTagRvLoading = true;
        getSearchProfileList(RetrofitClient.PROFILE_FIND_FRIENDS_LOAD_MORE_RESPONSE);

    }

    private void setSearchProfilesAdapter() {

        if (mSearchProfileAdapter == null) {
            mSearchProfileAdapter = new CommentTagAdapter(mSearchProfilesList, this);
            mRvTagList.setAdapter(mSearchProfileAdapter);
        } else {
            mSearchProfileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyEmptyTaggedProfilesList(ArrayList<ProfileResModel> mTaggedProfilesList1) {
        mTaggedProfilesList = mTaggedProfilesList1;
        if (mTaggedProfilesList.size() > 0) {
            mRvTagList.setVisibility(View.VISIBLE);
        } else {
            mRvTagList.setVisibility(View.GONE);
        }
    }

    private void getSearchProfileList(int requestCode) {

        String mFilter;
        int mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
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

        RetrofitClient.getRetrofitInstance().callGetSearchProfiles(this, mFilter, requestCode, mDataLimit, mTagRvOffset);


    }


    private void getTaggedUserList(HashMap<Integer, String> mCommentTaggedUserDetails) {
        mCommentTaggedUserIDs.clear();
        mCommentTaggedUserList.clear();
        for (HashMap.Entry<Integer, String> entry : mCommentTaggedUserDetails.entrySet()) {
            mCommentTaggedUserIDs.add(entry.getKey());
            mCommentTaggedUserList.add(entry.getValue());
        }
        for (int i = 0; i < mCommentTaggedUserIDs.size(); i++) {
            if (i == 0) {
                mCommentUserIDs = String.valueOf(mCommentTaggedUserIDs.get(i));
                mCommentTaggedUserNames = mCommentTaggedUserList.get(i);
            } else {
                mCommentUserIDs = mCommentUserIDs + "," + mCommentTaggedUserIDs.get(i);
                mCommentTaggedUserNames = mCommentTaggedUserNames + "," + mCommentTaggedUserList.get(i);
            }
        }
    }

    void clearFields() {
        mCommentEdt.setText("");
        mImageConstraintLay.setVisibility(View.GONE);
        mIvPost.setVisibility(View.VISIBLE);
        mPostImgUri = null;
        imgUrl = "";
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.post_btn, R.id.ivPost, R.id.iv_remove_image, R.id.comment_user_img_lay})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                break;
            case R.id.post_btn:

                String mCommentReply = mCommentEdt.getText().toString().trim();
                if (mPostImgUri != null) {
                    uploadPicture(mPostImgUri);
                } else if (mCommentReply.isEmpty()) {
                    showToast(getApplicationContext(), getString(R.string.empty_comments));
                } else {
                    try {
                        callPostVideoCommentsReply(imgUrl, mFeedCommentModel.getId(), mMyProfileResModel.getID(), mFeedCommentModel.getmPostId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
            case R.id.ivPost:
                showAppDialog(AppDialogFragment.BOTTOM_ADD_IMG_DIALOG, null);
                break;

            case R.id.iv_remove_image:
                clearFields();
                break;
            case R.id.comment_user_img_lay:
                profileClick();
                break;
        }
    }

    private void profileClick() {
        if (mMyProfileResModel != null) {
            if (mFeedCommentModel.getmProfileId() == mMyProfileResModel.getID()) {
                moveMyProfileScreenWithResult(this, 0, AppConstants.FOLLOWERS_FOLLOWING_RESULT);
            } else {
                moveOtherProfileScreen(this, mMyProfileResModel.getID(),
                        mFeedCommentModel.getmProfileId());
            }
        }

    }

    private void uploadPicture(String mCommentReplyImgUri) {
        File mFile = new File(Uri.parse(mCommentReplyImgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", mFile.getName(), requestBody);
        RetrofitClient.getRetrofitInstance().callUploadCommentReplyPostImg(this, filePart, RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE);
    }

    private void getMotoProfiles() {
        mFullMPList.clear();
        int mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        String mFilter = "UserID = " + mUserID;
        RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);
    }

    private void getCommentsReply(String mFilter) {
        RetrofitClient.getRetrofitInstance().callGetVideoCommentsReply(this, mFilter, RetrofitClient.CALL_GET_VIDEO_REPLIES);
    }

    private void setReplyList() {
        String commentImgStr = mFeedCommentModel.getProfiles_by_ProfileID().getProfilePicture();
        if (!commentImgStr.isEmpty()) {
            setImageWithGlide(mCommentUserImg, commentImgStr, R.drawable.default_profile_icon);
        } else {
            mCommentUserImg.setImageResource(R.drawable.default_profile_icon);
        }
        if (mFeedCommentModel.getProfiles_by_ProfileID().getProfileType() == Integer.parseInt(BaseActivity.SPECTATOR)) {
            mCommentUserNameTxt.setText(mFeedCommentModel.getProfiles_by_ProfileID().getSpectatorName());
        } else {
            mCommentUserNameTxt.setText(mFeedCommentModel.getProfiles_by_ProfileID().getDriver());
        }
        if (!mFeedCommentModel.getmComment().trim().isEmpty()) {
            try {
                mCommentTxt.setVisibility(View.VISIBLE);
                mCommentTxt.setText(setTextEdt(this, URLDecoder.decode(mFeedCommentModel.getmComment(), "UTF-8"), mFeedCommentModel.getCommentTaggedUserNames(), mFeedCommentModel.getCommentTaggedUserID(), mMyProfileResModel.getID()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            mCommentTxt.setVisibility(View.GONE);
        }
        if (!mFeedCommentModel.getCommentImages().trim().isEmpty()) {
            mIvCommentImg.setVisibility(View.VISIBLE);
            setImageWithGlide(mIvCommentImg, mFeedCommentModel.getCommentImages(), R.drawable.default_cover_img);
        } else {
            mIvCommentImg.setVisibility(View.GONE);
        }
        mIvCommentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveLoadImageScreen(VideoCommentReplyActivity.this, UrlUtils.FILE_URL + mFeedCommentModel.getCommentImages());
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mFeedCommentsListView.setLayoutManager(mLayoutManager);
        mFeedCommentsListView.setItemAnimator(new DefaultItemAnimator());
        if (mMyProfileResModel.getProfileType() == Integer.parseInt(BaseActivity.SPECTATOR)) {
            mUsernameTxt.setText(mMyProfileResModel.getSpectatorName());
        } else {
            mUsernameTxt.setText(mMyProfileResModel.getDriver());
        }
        String imgStr = mMyProfileResModel.getProfilePicture();
        if (!imgStr.isEmpty()) {
            setImageWithGlide(mUserImg, imgStr, R.drawable.default_profile_icon);
        } else {
            mUserImg.setImageResource(R.drawable.default_profile_icon);
        }
        if (mFeedCommentReplyList == null) {
            showToast(getApplicationContext(), getString(R.string.no_comments));
        }
    }

    private void callPostVideoCommentsReply(String imgUrl, int mCommentId, int mProfileId, int postID) {

        getTaggedUserList(mCommentTaggedUserDetails);
        String mCommentReply = mCommentEdt.getText().toString().trim();
        JsonObject mJsonObject = new JsonObject();
        JsonObject mItem = new JsonObject();

        mItem.addProperty("CommentID", mCommentId);
        mItem.addProperty("ProfileID", mProfileId);
        try {
            mItem.addProperty("ReplyText", URLEncoder.encode(mCommentReply, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mItem.addProperty("VideoID", postID);
        mItem.addProperty("ReplyImages", imgUrl);
        mItem.addProperty("ReplyTaggedUserNames", mCommentTaggedUserNames);
        mItem.addProperty("ReplyTaggedUserID", mCommentUserIDs);

        clearFields();

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mItem);
        mJsonObject.add("resource", mJsonArray);
        RetrofitClient.getRetrofitInstance().callPostVideoCommentReply(this, mJsonObject, RetrofitClient.POST_VIDEO_COMMENTS_REPLY);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof ProfileModel) {

            ProfileModel mProfileModel = (ProfileModel) responseObj;

            switch (responseType) {

                case RetrofitClient.GET_PROFILE_RESPONSE:

                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {

                        mFullMPList.addAll(mProfileModel.getResource());

                        mMyProfileResModel = mFullMPList.get(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS));

                        getComments(mCommentFilter);

                    } else {
                        showSnackBar(mParentLay, mNoProfileErr);
                    }

                    break;
                case RetrofitClient.PROFILE_FIND_FRIENDS_LOAD_MORE_RESPONSE:
                    mSearchProfilesList.clear();
                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        mRvTagList.setVisibility(View.VISIBLE);
                        mTagRvTotalCount = mProfileModel.getMeta().getCount();
                        mIsTagRvLoading = false;
                        mSearchProfilesList.addAll(mProfileModel.getResource());
                        mTagRvOffset = mDataLimit;
                    } else {
                        mRvTagList.setVisibility(View.GONE);
                        mSearchProfilesList.clear();
                        if (mTagRvOffset == 0) {
                            mTagRvTotalCount = 0;
                            showToast(this, getString(R.string.no_profile_found_to_follow_err));
                        }
                    }
                    setSearchProfilesAdapter();
                    break;

            }

        } else if (responseObj instanceof VideoCommentReplyModel) {
            VideoCommentReplyModel mResFeedCommentReplyModel = (VideoCommentReplyModel) responseObj;
            switch (responseType) {
                case RetrofitClient.POST_VIDEO_COMMENTS_REPLY:
                    mFeedCommentReplyList.add(0, mResFeedCommentReplyModel.getResource().get(0));
                    mFeedCommentsReplyAdapter.notifyDataSetChanged();
                    setResult(RESULT_OK, new Intent().putExtra(AppConstants.COMMENT_REPLY_FOR_POST_VIDEOS, mFeedCommentReplyList));
                    break;
                case RetrofitClient.CALL_GET_VIDEO_REPLIES:
                    mFeedCommentReplyList = mResFeedCommentReplyModel.getResource();
                    setFeedCommentReplyAdapter();

            }

        } else if (responseObj instanceof VideoCommentsModel) {
            if (responseType == RetrofitClient.CALL_GET_VIDEO_COMMENTS) {
                VideoCommentsModel mVideoCommentModel = (VideoCommentsModel) responseObj;
                mFeedCommentModel = mVideoCommentModel.getResource().get(0);
                getCommentsReply(mFilter);
            }
        } else if (responseObj instanceof VideoReplyLikeModel) {
            VideoReplyLikeModel mReplyLikeList = (VideoReplyLikeModel) responseObj;
            ArrayList<VideoReplyLikeModel> mReplyLike = mReplyLikeList.getResource();
            switch (responseType) {
                case RetrofitClient.VIDEO_REPLY_LIKE:
                    if (mReplyLike.size() > 0) {
                        mFeedCommentsReplyAdapter.resetReplyLikeList(mReplyLike.get(0));
                    }
                    break;
                case RetrofitClient.VIDEO_REPLY_UNLIKE: {
                    mFeedCommentsReplyAdapter.resetReplyUnLikeList(mReplyLike.get(0));
                }
                break;
            }

        } else if (responseObj instanceof ImageModel) {

            ImageModel mImageModel = (ImageModel) responseObj;

            switch (responseType) {

                case RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE:
                    //update the record in database/tablet
                    mImageConstraintLay.setVisibility(View.GONE);
                    imgUrl = getHttpFilePath(mImageModel.getmModels().get(0).getPath());

                    try {
                        callPostVideoCommentsReply(imgUrl, mFeedCommentModel.getId(), mMyProfileResModel.getID(), mFeedCommentModel.getmPostId());
                    } catch (Exception e) {
                        e.printStackTrace();
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

            getCommentsReply(mFilter);

        }
    }

    private void getComments(String mCommentFilter) {
        RetrofitClient.getRetrofitInstance().callGetVideoComments(this, mCommentFilter, RetrofitClient.CALL_GET_VIDEO_COMMENTS);
    }

    private void setFeedCommentReplyAdapter() {
        setReplyList();
        mFeedCommentsReplyAdapter = new VideoCommentsReplyAdapter(this, mFeedCommentReplyList, mMyProfileResModel, mFeedCommentModel);
        mFeedCommentsListView.setAdapter(mFeedCommentsReplyAdapter);
    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(
                    this,
                    RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mParentLay, mErrorMsg);
        }
    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
        String mErrorMsg = code + " - " + message;
        showSnackBar(mParentLay, mErrorMsg);
    }

    @Override
    public void retrofitOnError(int code, String message, int responseType) {
        super.retrofitOnError(code, message, responseType);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(
                    this,
                    RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mParentLay, mErrorMsg);
        }
    }

    @Override
    public void retrofitOnFailure(int responseType) {
        super.retrofitOnFailure();
        showSnackBar(mParentLay, mInternetFailed);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_CAPTURE_REQ:
                    Uri mCameraPicUri = getImgResultUri(data);
                    if (mCameraPicUri != null) {
                        try {
                            File mPostImgFile = compressedImgFile(mCameraPicUri,
                                    POST_IMAGE_NAME_TYPE, "");
                            mPostImgUri = Uri.fromFile(mPostImgFile).toString();
                            mImageConstraintLay.setVisibility(View.VISIBLE);
                            setImageWithGlide(mIvPostImage, Uri.parse(mPostImgUri), R.drawable.default_cover_img);
                            mIvPost.setVisibility(View.GONE);

                        } catch (Exception e) {
                            e.printStackTrace();
                            showSnackBar(mParentLay, e.getMessage());
                        }
                    } else {
                        showSnackBar(mParentLay, getString(R.string.file_not_found));
                    }
                    break;
                case GALLERY_PIC_REQ:
                    assert data.getExtras() != null;
                    Uri mSelectedImgFileUri = (Uri) data.getExtras().get(PickerImageActivity.EXTRA_RESULT_DATA);
                    if (mSelectedImgFileUri != null) {
                        try {
                            String mProfileID = String.valueOf(mMyProfileResModel.getID());
                            File mPostImgFile = compressedImgFile(mSelectedImgFileUri,
                                    POST_IMAGE_NAME_TYPE, mProfileID);
                            mPostImgUri = Uri.fromFile(mPostImgFile).toString();
                            mImageConstraintLay.setVisibility(View.VISIBLE);
                            setImageWithGlide(mIvPostImage, Uri.parse(mPostImgUri), R.drawable.default_cover_img);
                            mIvPost.setVisibility(View.GONE);

                        } catch (Exception e) {
                            e.printStackTrace();
                            showSnackBar(mParentLay, e.getMessage());
                        }
                    } else {
                        showSnackBar(mParentLay, getString(R.string.file_not_found));
                    }
                    break;
            }
        }
    }

    public void addTaggedFriends(ProfileResModel profileResModel) {
        mRvTagList.setVisibility(View.GONE);
        mTagSearchString = "";
        mSearchFriendList = false;
        isSelected = true;

        String mCommentTxt, mTempCommentTxt2, mTempCommentTxt1;
        mCommentTxt = mCommentEdt.getText().toString();
        mTempCommentTxt1 = mCommentTxt.substring(0, mCurrentIndexOfCommentTag);
        if (mSearchTextIndex < mCommentTxt.length()) {
            mTempCommentTxt2 = mCommentTxt.substring(mSearchTextIndex + 1);
        } else {
            mTempCommentTxt2 = "";
        }
        String mResCommentTxt, mResCommentTxt1;
        mCommentTaggedUserDetails.put(profileResModel.getUserID(), "@" + Utility.getInstance().getUserName(profileResModel));
        getTaggedUserList(mCommentTaggedUserDetails);

        mResCommentTxt = mTempCommentTxt1 + " @" + Utility.getInstance().getUserName(profileResModel);
        mResCommentTxt1 = mResCommentTxt + " " + mTempCommentTxt2;
        int mCursorPosition = mResCommentTxt.length();
        mCommentEdt.setText(setTextEdt(this, mResCommentTxt1, mCommentTaggedUserNames, mCommentUserIDs, mMyProfileResModel.getID()));
        mCommentEdt.setSelection(mCursorPosition + 1);
        mSearchTextIndex = mCursorPosition + 1;
    }

}
