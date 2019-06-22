package online.motohub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import online.motohub.adapter.FeedCommentsAdapter;
import online.motohub.adapter.promoter.PostCommentLikeViewAdapter;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.SharePostInterface;
import online.motohub.model.FeedCommentLikeModel;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedCommentReplyModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.ImageModel;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.util.CommonAPI;
import online.motohub.dialog.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.Utility;

public class PostCommentLikeViewActivity extends BaseActivity implements PostCommentLikeViewAdapter.TotalRetrofitPostsResultCount {

    private static final int mDataLimit = 1;
    @BindView(R.id.postViewCoLayout)
    RelativeLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mPostRecyclerView;
    @BindString(R.string.post)
    String mToolbarTitle;
    @BindView(R.id.user_img)
    CircleImageView mUserImg;
    @BindView(R.id.name_txt)
    TextView mUsernameTxt;
    @BindView(R.id.feeds_comments_list_view)
    RecyclerView mFeedCommentRecyclerView;
    @BindView(R.id.comment_edt)
    EditText mCommentEdt;
    @BindView(R.id.imageConstraintLay)
    ConstraintLayout mImageConstraintLay;
    @BindView(R.id.iv_post_image)
    ImageView mIvPostImage;
    @BindView(R.id.ivPost)
    ImageView mIvPost;
    private ArrayList<PostsResModel> mPostList = new ArrayList<>();
    private PostCommentLikeViewAdapter mPostCommentLikeViewAdapter;
    private int mPostsRvOffset = 0, mPostsRvTotalCount = 0;

    private int mCurrentPostPosition;

    private String mShareTxt = "";

    private FeedShareModel mSharedFeed;
    private String mFilter;
    private FeedCommentsAdapter mFeedCommentsAdapter;
    private int mCommentID;
    private ArrayList<ProfileResModel> mCurrentProfileResModel = new ArrayList<>();
    private ArrayList<FeedCommentModel> mFeedCommentsList = new ArrayList<>();
    private int mCurrentCommentPosition;
    private ProfileResModel mMyProfileResModel;
    SharePostInterface mShareTextWithPostInterface = new SharePostInterface() {
        @Override
        public void onSuccess(String shareMessage) {
            mShareTxt = shareMessage;
            CommonAPI.getInstance().callPostShare(PostCommentLikeViewActivity.this, mPostList.get(mCurrentPostPosition), mMyProfileResModel.getID());
        }
    };
    private String mPostImgUri;
    private int mPostID;
    private String imgUrl = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_comment_like);
        ButterKnife.bind(this);

        initView();

    }
    /*@Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mMyProfileResModel = (ProfileResModel) savedInstanceState
                .getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        mPostID = savedInstanceState.getInt(PostsModel.POST_ID, 0);
        mPostImgUri = savedInstanceState.getString(PostsModel.POST_PICTURE, null);
        mFeedCommentsList.clear();
        assert savedInstanceState.getSerializable(ProfileModel.FOLLOWING) != null;
        ArrayList<FeedCommentModel> mTempCommentList = (ArrayList<FeedCommentModel>)
                savedInstanceState
                        .getSerializable(FeedCommentModel
                                .COMMENT_LIST);
        assert mTempCommentList != null;
        mFeedCommentsList.addAll(mTempCommentList);
        if (mFeedCommentsAdapter != null)
            mFeedCommentsAdapter.notifyDataSetChanged();

        mPostList.clear();
        assert savedInstanceState.getSerializable(PostsModel.POST_LIST) != null;
        ArrayList<PostsResModel> mTempPostList = (ArrayList<PostsResModel>)
                savedInstanceState
                        .getSerializable(PostsModel
                                .POST_LIST);
        assert mTempPostList != null;
        mPostList.addAll(mTempPostList);
        if (mPostCommentLikeViewAdapter != null)
            mPostCommentLikeViewAdapter.notifyDataSetChanged();

        if (mPostImgUri != null) {
            mImageConstraintLay.setVisibility(View.VISIBLE);
            setImageWithGlide(
                    mIvPostImage,
                    Uri.parse(mPostImgUri),
                    R.drawable.default_cover_img);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
        outState.putInt(PostsModel.POST_ID, mPostID);
        outState.putString(PostsModel.POST_PICTURE, mPostImgUri);
        outState.putSerializable(FeedCommentModel.COMMENT_LIST, mFeedCommentsList);
        outState.putSerializable(PostsModel.POST_LIST, mPostList);
        super.onSaveInstanceState(outState);
    }*/

    private void initView() {

        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        try {

            assert getIntent().getExtras() != null;
            JSONObject mJsonObject = new JSONObject(getIntent().getExtras().getString(MyFireBaseMessagingService.ENTRY_JSON_OBJ));
            LinearLayoutManager mPostsLayoutManager = new LinearLayoutManager(this);
            mPostsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mPostRecyclerView.setLayoutManager(mPostsLayoutManager);
            mPostCommentLikeViewAdapter = new PostCommentLikeViewAdapter(mPostList, null, mCurrentProfileResModel, this, true);
            mPostRecyclerView.setAdapter(mPostCommentLikeViewAdapter);

            mPostID = Integer.parseInt(mJsonObject.getJSONObject("Details").get("PostID").toString());
            mFilter = "id=" + mPostID;

            mCommentID = Integer.parseInt(mJsonObject.getJSONObject("Details").get("CommentID").toString());

            getPost(mFilter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setCommentList() {
        mUsernameTxt.setText(Utility.getInstance().getUserName(mMyProfileResModel));
        String imgStr = mMyProfileResModel.getProfilePicture();
        if (!imgStr.isEmpty()) {
            setImageWithGlide(mUserImg, imgStr, R.drawable.default_profile_icon);
        } else {
            mUserImg.setImageResource(R.drawable.default_profile_icon);
        }

        if (mFeedCommentsList == null) {
            showToast(getApplicationContext(), getString(R.string.no_comments));

        }

    }

    private void getPost(String mFilter) {

        mPostsRvTotalCount = -1;

        RetrofitClient.getRetrofitInstance().callGetProfilePost(this, mFilter, RetrofitClient.GET_FEED_POSTS_RESPONSE, mDataLimit, mPostsRvOffset);

    }


    @OnClick({R.id.toolbar_back_img_btn, R.id.post_btn, R.id.ivPost, R.id.iv_remove_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.post_btn:
                String mComment = mCommentEdt.getText().toString().trim();
                if (mPostImgUri != null) {
                    uploadPicture(mPostImgUri);
                } else if (mComment.isEmpty()) {
                    showToast(getApplicationContext(), getString(R.string.empty_comments));
                } else {
                    try {
                        callPostFeedComments(imgUrl, mComment, mPostID, mMyProfileResModel.getID());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mCommentEdt.setText("");
                }
                break;
            case R.id.ivPost:
                showAppDialog(AppDialogFragment.BOTTOM_ADD_IMG_DIALOG, null);
                break;

            case R.id.iv_remove_image:
                mImageConstraintLay.setVisibility(View.GONE);
                mIvPost.setVisibility(View.VISIBLE);
                mPostImgUri = null;
                break;
        }
    }

    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String dialogType, StringBuilder profileTypesStr, ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);

        switch (dialogType) {

            case AppDialogFragment.BOTTOM_SHARE_DIALOG:
                mCurrentPostPosition = position;
                DialogManager.showShareDialogWithCallback(this, mShareTextWithPostInterface);
                // CommonAPI.getInstance().callPostShare(this, mPostList.get(getProfileCurrentPos()), mMyProfileResModel.getID());
                break;

        }

    }

    private void uploadPicture(String mCommentImgUri) {
        File mFile = new File(Uri.parse(mCommentImgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", mFile.getName(), requestBody);
        RetrofitClient.getRetrofitInstance().callUploadCommentReplyPostImg(this, filePart, RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE);

    }

    private void callPostFeedComments(String imgUrl, String mComment, int mPostId, int mProfileId) throws Exception {

        JsonObject mItem = new JsonObject();
        JsonObject mJsonObject = new JsonObject();


        mItem.addProperty("PostID", mPostId);
        mItem.addProperty("ProfileID", mProfileId);
        mItem.addProperty("Comment", URLEncoder.encode(mComment, "UTF-8"));
        mItem.addProperty("CommentImages", imgUrl);

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mItem);
        mJsonObject.add("resource", mJsonArray);

        mImageConstraintLay.setVisibility(View.GONE);
        mIvPost.setVisibility(View.VISIBLE);
        mPostImgUri = null;

        RetrofitClient.getRetrofitInstance().callPostFeedComments(this, mJsonObject, RetrofitClient.POST_COMMENTS);

    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        if (responseObj instanceof PostsModel) {

            PostsModel mPostsModel = (PostsModel) responseObj;

            switch (responseType) {

                case RetrofitClient.GET_FEED_POSTS_RESPONSE:

                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {

                        mPostsRvTotalCount = mPostsModel.getMeta().getCount();

                        mPostList.clear();

                        mPostList.addAll(mPostsModel.getResource());

                        ArrayList<FeedCommentModel> mFeedCommentArrayModel = mPostList.get(0).getPostComments();

                        mCurrentProfileResModel.add(getCurrentProfileResModel(mFeedCommentArrayModel));

                        callGetPostComments();

                        mPostsRvOffset = mPostsRvOffset + mDataLimit;

                    } else {
                        if (mPostsRvOffset == 0) {
                            mPostsRvTotalCount = 0;
                        }
                        showToast(this, getString(R.string.post_deleted_err));
                    }

                    mPostCommentLikeViewAdapter.notifyDataSetChanged();
                    break;

                case RetrofitClient.SHARED_POST_RESPONSE:

                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {

                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            PostsResModel mPostsResModel = mPostsModel.getResource().get(0);
                            mPostList.add(0, mPostsResModel);
                            mPostsRvTotalCount += 1;
                            mPostCommentLikeViewAdapter.notifyDataSetChanged();
                        }

                        mPostCommentLikeViewAdapter.resetShareAdapter(mSharedFeed);

                        showSnackBar(mCoordinatorLayout, getResources().getString(R.string.post_shared));

                    }

                    break;
            }

        } else if (responseObj instanceof FeedCommentModel) {

            FeedCommentModel mFeedCommentList = (FeedCommentModel) responseObj;

            switch (responseType) {

                case RetrofitClient.CALL_GET_COMMENTS:
                    setFeedCommentAdapter(mFeedCommentList.getResource());
                    break;

                case RetrofitClient.POST_COMMENTS:
                    ArrayList<FeedCommentModel> mNewFeedComment = mFeedCommentList.getResource();
                    mCommentEdt.setText("");
                    mIvPost.setVisibility(View.VISIBLE);
                    mFeedCommentsList.add(0, mNewFeedComment.get(0));
                    mFeedCommentsAdapter.notifyDataSetChanged();
                    break;
            }

        } else if (responseObj instanceof FeedLikesModel) {

            FeedLikesModel mFeedLikesList = (FeedLikesModel) responseObj;
            ArrayList<FeedLikesModel> mNewFeedLike = mFeedLikesList.getResource();
            switch (responseType) {
                case RetrofitClient.POST_LIKES:
                    mPostCommentLikeViewAdapter.resetLikeAdapter(mNewFeedLike.get(0));
                    break;
                case RetrofitClient.POST_UNLIKE:
                    mPostCommentLikeViewAdapter.resetDisLike(mNewFeedLike.get(0));
                    break;
            }

        } else if (responseObj instanceof SessionModel) {

            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }

            getPost(mFilter);

        } else if (responseObj instanceof FeedShareModel) {

            FeedShareModel mFeedShareList = (FeedShareModel) responseObj;
            ArrayList<FeedShareModel> mNewFeedShare = mFeedShareList.getResource();
            switch (responseType) {
                case RetrofitClient.POST_SHARES:
                    mSharedFeed = mNewFeedShare.get(0);
                    CommonAPI.getInstance().callAddSharedPost(this, mPostList.get(mCurrentPostPosition), mMyProfileResModel, mShareTxt);
                    break;

            }

        } else if (responseObj instanceof FeedCommentLikeModel) {
            FeedCommentLikeModel mFeedCommentLikeList = (FeedCommentLikeModel) responseObj;
            ArrayList<FeedCommentLikeModel> mCommentLike = mFeedCommentLikeList.getResource();
            switch (responseType) {
                case RetrofitClient.COMMENT_LIKES:
                    mFeedCommentsAdapter.resetCommentsLikeList(mCommentLike.get(0));
                    break;
                case RetrofitClient.COMMENT_UNLIKE:
                    mFeedCommentsAdapter.resetCommentUnLikeList(mCommentLike.get(0));
                    break;
            }
        } else if (responseObj instanceof ImageModel) {

            ImageModel mImageModel = (ImageModel) responseObj;

            switch (responseType) {

                case RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE:
                    //update the record in database/tablet

                    imgUrl = getHttpFilePath(mImageModel.getmModels().get(0).getPath());
                    String mComment = mCommentEdt.getText().toString().trim();
                    try {
                        callPostFeedComments(imgUrl, mComment, mPostID, mMyProfileResModel.getID());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }

        }

    }

    private void callGetPostComments() {
        String mFilter = "PostID = " + mPostID;
        RetrofitClient.getRetrofitInstance().callGetComments(this, mFilter, RetrofitClient.CALL_GET_COMMENTS);
    }

    private void setFeedCommentAdapter(ArrayList<FeedCommentModel> mFeedCommentList) {
        setCommentList();
        mFeedCommentsList = mFeedCommentList;
        mFeedCommentsAdapter = new FeedCommentsAdapter(this, mFeedCommentsList, mMyProfileResModel);
        mFeedCommentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFeedCommentRecyclerView.setAdapter(mFeedCommentsAdapter);
        mFeedCommentRecyclerView.scrollToPosition(mCurrentCommentPosition);

    }

    private ProfileResModel getCurrentProfileResModel(ArrayList<FeedCommentModel> mFeedCommentList) {

        mFeedCommentRecyclerView.setVisibility(View.VISIBLE);
        for (int i = 0; i < mFeedCommentList.size(); i++) {
            if (mFeedCommentList.get(i).getId() == mCommentID) {
                mCurrentCommentPosition = i;
                mMyProfileResModel = mFeedCommentList.get(i).getProfiles_by_ProfileID();
                break;
            }
        }
        return mMyProfileResModel;

    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);

        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else if (code == RetrofitClient.GET_FEED_POSTS_RESPONSE) {
            mPostsRvTotalCount = 0;
            showToast(this, getString(R.string.post_deleted_err));
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
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    @Override
    public int getTotalPostsResultCount() {
        return mPostsRvTotalCount;
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
                            showSnackBar(mCoordinatorLayout, e.getMessage());
                        }
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
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
                            showSnackBar(mCoordinatorLayout, e.getMessage());
                        }
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
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
}
