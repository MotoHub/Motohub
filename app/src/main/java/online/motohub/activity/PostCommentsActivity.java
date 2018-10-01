package online.motohub.activity;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.StringTokenizer;

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
import online.motohub.adapter.FeedCommentsAdapter;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.FeedCommentLikeModel;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedCommentReplyModel;
import online.motohub.model.ImageModel;
import online.motohub.model.PostsModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.Utility;
import online.motohub.adapter.TaggedProfilesAdapter;

public class PostCommentsActivity extends BaseActivity implements TaggedProfilesAdapter.TaggedProfilesSizeInterface {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindString(R.string.comments)
    String mToolbarTitle;

    @BindView(R.id.feeds_comments_list_view)
    RecyclerView mFeedCommentsListView;

    @BindView(R.id.user_img)
    CircleImageView mUserImg;

    @BindView(R.id.name_txt)
    TextView mUsernameTxt;

    @BindView(R.id.comment_edt)
    EditText mCommentEdt;

    @BindView(R.id.imageConstraintLay)
    ConstraintLayout mImageConstraintLay;

    @BindView(R.id.iv_post_image)
    ImageView mIvPostImage;

    @BindView(R.id.ivPost)
    ImageView mIvPost;

    @BindView(R.id.main_lay)
    RelativeLayout mParentLay;

    @BindView(R.id.rvTagList)
    RecyclerView mRvTagList;

    private String imgUrl = "";
    private String mFilter;
    private FeedCommentsAdapter mFeedCommentsAdapter;
    private ArrayList<FeedCommentModel> mFeedCommentList = new ArrayList<>();
    private ProfileResModel mMyProfileResModel;
    private int mPostID;
    private String mPostImgUri = null;
    private ArrayList<ProfileResModel> mTaggedProfilesList = new ArrayList<>();
    private CommentTagAdapter mSearchProfileAdapter;
    private static String mSearchStr = "";
    private LinearLayoutManager mHorLayoutManager;
    private static ArrayList<ProfileResModel> mSearchProfilesList = new ArrayList<>();
    private int mTagRvOffset = 0, mTagRvTotalCount = 0;
    private static final int mDataLimit = 15;
    private boolean mIsTagRvLoading;
    private int mCurrentIndexOfCommentTag;
    private boolean mSearchFriendList = false;

    private String mTagSearchString = "";
    private int mSearchTextIndex;
    private String mCommentTaggedUserNames = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);
        ButterKnife.bind(this);
        initView();

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mMyProfileResModel = (ProfileResModel) savedInstanceState
                .getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        mPostID = savedInstanceState.getInt(PostsModel.POST_ID, 0);
        mPostImgUri = savedInstanceState.getString(PostsModel.POST_PICTURE, null);
        mFeedCommentList.clear();
        assert savedInstanceState.getSerializable(ProfileModel.FOLLOWING) != null;
        ArrayList<FeedCommentModel> mTempCommentList = (ArrayList<FeedCommentModel>)
                savedInstanceState
                        .getSerializable(FeedCommentModel
                                .COMMENT_LIST);
        assert mTempCommentList != null;
        mFeedCommentList.addAll(mTempCommentList);
        if (mFeedCommentsAdapter != null)
            mFeedCommentsAdapter.notifyDataSetChanged();
        if (mPostImgUri != null) {
            mImageConstraintLay.setVisibility(View.VISIBLE);
            setImageWithGlide(
                    mIvPostImage,
                    Uri.parse(mPostImgUri),
                    R.drawable.default_cover_img);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
        outState.putInt(PostsModel.POST_ID, mPostID);
        outState.putString(PostsModel.POST_PICTURE, mPostImgUri);
        outState.putSerializable(FeedCommentModel.COMMENT_LIST, mFeedCommentList);
        super.onSaveInstanceState(outState);
    }

    private void initView() {
        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        mMyProfileResModel = (ProfileResModel) getIntent().getSerializableExtra(ProfileModel.MY_PROFILE_RES_MODEL);
        mPostID = getIntent().getIntExtra(PostsModel.POST_ID, 0);
        mFilter = "PostID=" + mPostID;
        getComments(mFilter);

        /*FlexboxLayoutManager mFlexBoxLayoutManager = new FlexboxLayoutManager(this);
        mFlexBoxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        mFlexBoxLayoutManager.setFlexDirection(FlexDirection.ROW);
        mFlexBoxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        mRvTagList.setLayoutManager(mFlexBoxLayoutManager);
*/
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
      /*  mCommentEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if(count == 0){
                        mSearchTextIndex = start - 1;
                        if(mSearchTextIndex > 0 && mTagSearchString.length() > 0)
                        mTagSearchString = mTagSearchString.substring(0,mTagSearchString.length()-1);
                    } else{
                        mSearchTextIndex = start;
                    }
                    if(mSearchTextIndex >= 0 ) {
                        if ((s.toString().charAt(mSearchTextIndex)) == '@') {
                            mSearchFriendList = true;
                            mCurrentIndexOfCommentTag = mSearchTextIndex;
                            mTagSearchString = "";
                        }
                      *//*  if (mSearchFriendList && (s.toString().charAt(mSearchTextIndex)) == ' ') {
                            mSearchFriendList = false;
                            mTagSearchString = "";
                            String mReplaceTxt = mCommentEdt.getText().toString();
                            mCommentTaggedUserNames = mCommentTaggedUserNames + ",@" + mTagSearchString;
                            if(mReplaceTxt.contains(mTagSearchString)){
                                mReplaceTxt.replace(mTagSearchString, "<b>"+"@"+mTagSearchString+"</b>");
                            }
                            mCommentEdt.setText(Html.fromHtml(mReplaceTxt));
                            mRvTagList.setVisibility(View.GONE);
                            mCommentEdt.setSelection(mSearchTextIndex);
                        }*//*
                        if (mSearchFriendList && (s.toString().charAt(mSearchTextIndex)) != '@') {
                            mTagSearchString = mTagSearchString + (s.toString().charAt(mSearchTextIndex));
                        }
                    } else{
                        mSearchFriendList = false;
                        mTagSearchString = "";
                        mRvTagList.setVisibility(View.GONE);
                    }
            }


            @Override
            public void afterTextChanged(Editable editable) {

                if (mSearchFriendList) {
                    findFriendsOrVehicles(mTagSearchString);


                }
            }

        });*/
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

    @OnClick({R.id.toolbar_back_img_btn, R.id.post_btn, R.id.ivPost, R.id.iv_remove_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                break;
            case R.id.post_btn:
                String mComment = mCommentEdt.getText().toString().trim();
                if (mPostImgUri != null) {
                    uploadPicture(mPostImgUri);
                } else if (mComment.isEmpty()) {
                    showToast(getApplicationContext(), getString(R.string.empty_comments));
                } else {
                    try {
                        callPostFeedComments(imgUrl, mPostID, mMyProfileResModel.getID());
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

    private void uploadPicture(String mCommentImgUri) {
        File mFile = new File(Uri.parse(mCommentImgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", "PostComments_" + mFile.getName(), requestBody);
        RetrofitClient.getRetrofitInstance().callUploadCommentReplyPostImg(this, filePart, RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE);

    }

    void clearFields() {
        mImageConstraintLay.setVisibility(View.GONE);
        mCommentEdt.setText("");
        mIvPost.setVisibility(View.VISIBLE);
        mPostImgUri = null;
        imgUrl = "";
    }

    private void callPostFeedComments(String imgUrl, int mPostId, int mProfileId) throws Exception {

        String mComment = mCommentEdt.getText().toString().trim();

        JsonObject mItem = new JsonObject();
        JsonObject mJsonObject = new JsonObject();


        mItem.addProperty("PostID", mPostId);
        mItem.addProperty("ProfileID", mProfileId);
        mItem.addProperty("Comment", URLEncoder.encode(mComment, "UTF-8"));
        mItem.addProperty("CommentImages", imgUrl);
     //   mItem.addProperty("CommentTaggedUserNames", mCommentTaggedUserNames);

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mItem);
        mJsonObject.add("resource", mJsonArray);

        clearFields();

        RetrofitClient.getRetrofitInstance().callPostFeedComments(this, mJsonObject, RetrofitClient.POST_COMMENTS);

    }


    private void getComments(String mFilter) {
        RetrofitClient.getRetrofitInstance().callGetComments(this, mFilter, RetrofitClient.CALL_GET_COMMENTS);
    }

    private void setCommentList() {

        mUsernameTxt.setText(Utility.getInstance().getUserName(mMyProfileResModel));
        String imgStr = mMyProfileResModel.getProfilePicture();
        if (!imgStr.isEmpty()) {
            setImageWithGlide(mUserImg, imgStr, R.drawable.default_profile_icon);
        } else {
            mUserImg.setImageResource(R.drawable.default_profile_icon);
        }

        if (mFeedCommentList.size() == 0) {
            showToast(getApplicationContext(), getString(R.string.no_comments));

        }

    }


    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof FeedCommentModel) {
            FeedCommentModel mResFeedCommentModel = (FeedCommentModel) responseObj;
            switch (responseType) {
                case RetrofitClient.POST_COMMENTS:
                    mFeedCommentList.add(0, mResFeedCommentModel.getResource().get(0));
                    mFeedCommentsAdapter.notifyDataSetChanged();
                    setResult(RESULT_OK, new Intent().putExtra(PostsModel.COMMENTS_BY_POSTID, mFeedCommentList));
                    break;
                case RetrofitClient.CALL_GET_COMMENTS:
                    mFeedCommentList = mResFeedCommentModel.getResource();
                    setFeedCommentAdapter();
                    break;
            }
        } else if (responseObj instanceof FeedCommentLikeModel) {
            FeedCommentLikeModel mFeedCommentLikeList = (FeedCommentLikeModel) responseObj;
            ArrayList<FeedCommentLikeModel> mCommentLike = mFeedCommentLikeList.getResource();
            switch (responseType) {
                case RetrofitClient.COMMENT_LIKES:
                    if (mCommentLike != null && mCommentLike.size() > 0)
                        mFeedCommentsAdapter.resetCommentsLikeList(mCommentLike.get(0));
                    break;
                case RetrofitClient.COMMENT_UNLIKE:
                    if (mCommentLike != null && mCommentLike.size() > 0)
                        mFeedCommentsAdapter.resetCommentUnLikeList(mCommentLike.get(0));
                    break;
            }
        } else if (responseObj instanceof ImageModel) {

            ImageModel mImageModel = (ImageModel) responseObj;

            switch (responseType) {

                case RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE:
                    //update the record in database/tablet
                    imgUrl = getHttpFilePath(mImageModel.getmModels().get(0).getPath());
                    try {
                        callPostFeedComments(imgUrl, mPostID, mMyProfileResModel.getID());
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

            getComments(mFilter);

        } else if (responseObj instanceof ProfileModel) {

            ProfileModel mProfileModel = (ProfileModel) responseObj;
            switch (responseType) {

                case RetrofitClient.PROFILE_FIND_FRIENDS_LOAD_MORE_RESPONSE:

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

        }

    }

    private void setFeedCommentAdapter() {
        setCommentList();
        mFeedCommentsAdapter = new FeedCommentsAdapter(this, mFeedCommentList, mMyProfileResModel);
        mFeedCommentsListView.setAdapter(mFeedCommentsAdapter);
        mFeedCommentsListView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        showSnackBar(mParentLay, mInternetFailed);
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
        super.retrofitOnFailure(responseType);
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
                case AppConstants.POST_COMMENT_REPLY_REQUEST:
                    assert data.getExtras() != null;
                    ArrayList<FeedCommentReplyModel> mFeedCommentModel = (ArrayList<FeedCommentReplyModel>) data.getExtras().getSerializable(AppConstants.COMMENT_REPLY_FOR_POST_VIDEOS);
                    mFeedCommentsAdapter.resetCommentReplyList(mFeedCommentModel);
                    break;
            }
        }
    }

    public void addTaggedFriends(ProfileResModel profileResModel) {
        mRvTagList.setVisibility(View.GONE);
        mTagSearchString = "";
        mSearchFriendList = false;

        String mCommentTxt, mTempCommentTxt2,mTempCommentTxt1;
        mCommentTxt = mCommentEdt.getText().toString();
        mTempCommentTxt1 = mCommentTxt.substring(0,mCurrentIndexOfCommentTag);
        if(mSearchTextIndex < mCommentTxt.length()) {
            mTempCommentTxt2 = mCommentTxt.substring(mSearchTextIndex+1, mCommentTxt.length());
        } else{
            mTempCommentTxt2 = "";
        }
        String mResCommentTxt,mResCommentTxt1;
        mCommentTaggedUserNames = mCommentTaggedUserNames + ",@"+ Utility.getInstance().getUserName(profileResModel);
        String mCursorIndexString = mTempCommentTxt1 +  Utility.getInstance().getUserName(profileResModel) ;
        mResCommentTxt = mTempCommentTxt1 + "@" + Utility.getInstance().getUserName(profileResModel);
        mResCommentTxt1 = mResCommentTxt + mTempCommentTxt2;
        int mCursorPosition = mCursorIndexString.length();
        setTextEdt(mResCommentTxt1);
        mCommentEdt.setSelection(mCursorPosition);
    }

    private void setTextEdt(String mResCommentTxt1) {
        String mCommentTagString = mResCommentTxt1;
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String [] mCommentTagArray = mCommentTaggedUserNames.split(",@");
        for(int i = 1; i < mCommentTagArray.length; i++) {
            if (mCommentTagString.contains(mCommentTagArray[i]))

            {
                SpannableString redSpannable = new SpannableString("@"+mCommentTagArray[i]);
                redSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, mCommentTagArray[i].length()+1, 0);
                builder.append(redSpannable);
            } else{
                SpannableString redSpannable = new SpannableString("@"+mCommentTagArray[i]);
                redSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, mCommentTagArray[i].length()+1, 0);
                builder.append(redSpannable);
            }
        }
        mCommentEdt.setText(builder, TextView.BufferType.SPANNABLE);
    }
}
