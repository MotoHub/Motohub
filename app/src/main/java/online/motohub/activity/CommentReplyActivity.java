package online.motohub.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import online.motohub.R;
import online.motohub.adapter.FeedCommentsReplyAdapter;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedCommentReplyModel;
import online.motohub.model.ImageModel;
import online.motohub.model.PostsModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.ReplyLikeModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.Utility;

public class CommentReplyActivity extends BaseActivity {
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

    private String mPostImgUri = null;

    private String imgUrl = "";

    private ArrayList<ProfileResModel> mFullMPList = new ArrayList<>();

    private String mReplyFilter, mCommentFilter;
    private FeedCommentModel mFeedCommentModel;
    private FeedCommentsReplyAdapter mFeedCommentsReplyAdapter;
    private ArrayList<FeedCommentReplyModel> mFeedCommentReplyList = new ArrayList<>();
    private ProfileResModel mMyProfileResModel;
    private int mCommentID;


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
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mMyProfileResModel = (ProfileResModel) savedInstanceState
                .getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        mCommentID = savedInstanceState.getInt(PostsModel.POST_ID, 0);
        mPostImgUri = savedInstanceState.getString(PostsModel.POST_PICTURE, null);
        mFeedCommentReplyList.clear();
        assert savedInstanceState.getSerializable(ProfileModel.FOLLOWING) != null;
        ArrayList<FeedCommentReplyModel> mTempReplyList = (ArrayList<FeedCommentReplyModel>)
                savedInstanceState
                        .getSerializable(FeedCommentReplyModel
                                .REPLY_LIST);
        assert mTempReplyList != null;
        mFeedCommentReplyList.addAll(mTempReplyList);
        if (mFeedCommentsReplyAdapter != null)
            mFeedCommentsReplyAdapter.notifyDataSetChanged();
        mFeedCommentModel = (FeedCommentModel) savedInstanceState.getSerializable(FeedCommentModel.COMMENT_LIST);
        if (mPostImgUri != null) {
            mImageConstraintLay.setVisibility(View.VISIBLE);
            setImageWithGlide(
                    mIvPostImage,
                    Uri.parse(mPostImgUri),
                    R.drawable.default_cover_img);
        }
        mFullMPList = (ArrayList<ProfileResModel>) savedInstanceState.getSerializable(ProfileModel.FULL_PROFILE_LIST);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
        outState.putInt(PostsModel.POST_ID, mCommentID);
        outState.putString(PostsModel.POST_PICTURE, mPostImgUri);
        outState.putSerializable(FeedCommentReplyModel.REPLY_LIST, mFeedCommentReplyList);
        outState.putSerializable(FeedCommentModel.COMMENT_LIST, mFeedCommentModel);
        outState.putSerializable(ProfileModel.FULL_PROFILE_LIST, mFullMPList);

        super.onSaveInstanceState(outState);
    }


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
                    mReplyFilter = "CommentID = " + mCommentID;
                } else if (mJsonObject.getJSONObject("Details").has("PostcommentID")) {
                    mCommentID = Integer.parseInt(mJsonObject.getJSONObject("Details").get("PostcommentID").toString());
                    mCommentFilter = "ID = " + mCommentID;
                    mReplyFilter = "CommentID = " + mCommentID;
                }

                getMotoProfiles();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mMyProfileResModel = (ProfileResModel) getIntent().getSerializableExtra(ProfileModel.MY_PROFILE_RES_MODEL);
            mFeedCommentModel = (FeedCommentModel) getIntent().getSerializableExtra(FeedCommentModel.COMMENT_LIST);
            mCommentID = mFeedCommentModel.getId();
            mReplyFilter = "CommentID=" + mCommentID;
            getCommentsReply(mReplyFilter);
        }


    }

    void clearFields() {
        mCommentEdt.setText("");
        mImageConstraintLay.setVisibility(View.GONE);
        mIvPost.setVisibility(View.VISIBLE);
        mPostImgUri = null;
        imgUrl = "";

    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.post_btn, R.id.ivPost, R.id.iv_remove_image})
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
                        callPostFeedCommentsReply(imgUrl, mFeedCommentModel.getId(), mMyProfileResModel.getID(), mFeedCommentModel.getmPostId());
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
        RetrofitClient.getRetrofitInstance().callGetCommentsReply(this, mFilter, RetrofitClient.CALL_GET_COMMENTS_REPLY);
    }

    private void setReplyList() {
        String commentImgStr = mFeedCommentModel.getProfiles_by_ProfileID().getProfilePicture();
        if (!commentImgStr.isEmpty()) {
            setImageWithGlide(mCommentUserImg, commentImgStr, R.drawable.default_profile_icon);
        } else {
            mCommentUserImg.setImageResource(R.drawable.default_profile_icon);
        }
        mCommentUserNameTxt.setText(Utility.getInstance().getUserName(mFeedCommentModel.getProfiles_by_ProfileID()));

        if (!mFeedCommentModel.getmComment().trim().isEmpty()) {
            try {
                mCommentTxt.setVisibility(View.VISIBLE);
                mCommentTxt.setText(URLDecoder.decode(mFeedCommentModel.getmComment(), "UTF-8"));
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
                moveLoadImageScreen(CommentReplyActivity.this, mFeedCommentModel.getCommentImages());
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

    private void callPostFeedCommentsReply(String imgUrl, int mCommentId, int mProfileId, int postID) {
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
        mItem.addProperty("PostID", postID);
        mItem.addProperty("ReplyImages", imgUrl);

        clearFields();

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mItem);
        mJsonObject.add("resource", mJsonArray);
        RetrofitClient.getRetrofitInstance().callPostFeedCommentReply(this, mJsonObject, RetrofitClient.POST_COMMENTS_REPLY);
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
            }

        } else if (responseObj instanceof FeedCommentModel) {
            if (responseType == RetrofitClient.CALL_GET_COMMENTS) {
                FeedCommentModel mResFeedCommentModel = (FeedCommentModel) responseObj;
                ArrayList<FeedCommentModel> mResFeedCommentList = mResFeedCommentModel.getResource();
                mFeedCommentModel = mResFeedCommentList.get(0);
                getCommentsReply(mReplyFilter);
            }

        } else if (responseObj instanceof FeedCommentReplyModel) {
            FeedCommentReplyModel mResFeedCommentReplyModel = (FeedCommentReplyModel) responseObj;
            switch (responseType) {
                case RetrofitClient.POST_COMMENTS_REPLY:
                    mFeedCommentReplyList.add(0, mResFeedCommentReplyModel.getResource().get(0));
                    mFeedCommentsReplyAdapter.notifyDataSetChanged();
                    setResult(RESULT_OK, new Intent().putExtra(AppConstants.COMMENT_REPLY_FOR_POST_VIDEOS, mFeedCommentReplyList));
                    break;
                case RetrofitClient.CALL_GET_COMMENTS_REPLY:
                    mFeedCommentReplyList = mResFeedCommentReplyModel.getResource();
                    setFeedCommentReplyAdapter();
                    break;
            }
        } else if (responseObj instanceof ReplyLikeModel) {
            ReplyLikeModel mReplyLikeList = (ReplyLikeModel) responseObj;
            ArrayList<ReplyLikeModel> mReplyLike = mReplyLikeList.getResource();
            switch (responseType) {
                case RetrofitClient.REPLY_LIKE:
                    if (mReplyLike.size() > 0) {
                        mFeedCommentsReplyAdapter.resetReplyLikeList(mReplyLike.get(0));
                    }
                    break;
                case RetrofitClient.REPLY_UNLIKE: {
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
                        callPostFeedCommentsReply(imgUrl, mFeedCommentModel.getId(), mMyProfileResModel.getID(), mFeedCommentModel.getmPostId());
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

            getCommentsReply(mReplyFilter);

        }
    }

    private void getComments(String mCommentFilter) {
        RetrofitClient.getRetrofitInstance().callGetComments(this, mCommentFilter, RetrofitClient.CALL_GET_COMMENTS);
    }

    private void setFeedCommentReplyAdapter() {
        setReplyList();
        mFeedCommentsReplyAdapter = new FeedCommentsReplyAdapter(this, mFeedCommentReplyList, mMyProfileResModel, mFeedCommentModel);
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

}
