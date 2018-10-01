package online.motohub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.PostViewAdapter;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.CommonAPI;
import online.motohub.util.PreferenceUtils;

public class PostViewActivity extends BaseActivity implements PostViewAdapter.TotalRetrofitPostsResultCount {

    @BindView(R.id.postViewCoLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler_view)
    RecyclerView mPostRecyclerView;

    @BindString(R.string.post)
    String mToolbarTitle;

    private ArrayList<PostsResModel> mPostList;
    private PostViewAdapter mPostAdapter;

    private static final int mDataLimit = 1;

    private int mPostsRvOffset = 0, mPostsRvTotalCount = 0;

    private int mCurrentPostPosition;

    private FeedShareModel mSharedFeed;
    private String mFilter;
    private ProfileResModel mCurrentProfileObj;
    private int mPostPos;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);
        ButterKnife.bind(this);
        initView();

    }

    private void setAdapter() {
        if (mPostAdapter == null) {
            mPostAdapter = new PostViewAdapter(mPostList, mCurrentProfileObj, this);
            mPostRecyclerView.setAdapter(mPostAdapter);
        } else {
            mPostAdapter.notifyDataSetChanged();
        }
    }


    private void initView() {

        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        try {
            assert getIntent().getExtras() != null;
            JSONObject mJsonObject = new JSONObject(getIntent().getExtras().getString(MyFireBaseMessagingService.ENTRY_JSON_OBJ));

            LinearLayoutManager mPostsLayoutManager = new LinearLayoutManager(this);
            mPostsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            mPostRecyclerView.setLayoutManager(mPostsLayoutManager);

            mPostList = new ArrayList<>();

            if (mJsonObject.getString("Type").equals("TAGGED") || mJsonObject.getString("Type").equals("FOLLOWER_POST")) {
                int mPostID = Integer.parseInt(mJsonObject.getJSONObject("Details").get("ID").toString());
                mFilter = "id=" + mPostID;
            } else if (mJsonObject.getString("Type").equals("POST_COMMENTS") || mJsonObject.getString("Type").equals("POST_LIKES")
                    || mJsonObject.getString("Type").equals("COMMENT_REPLY")) {
                int mPostID = Integer.parseInt(mJsonObject.getJSONObject("Details").get("PostID").toString());
                mFilter = "id=" + mPostID;
            } else if (mJsonObject.getString("Type").equals("VIDEO_SHARE")) {
                mFilter = "id=" + mJsonObject.getJSONObject("Details").get("ID");
            } else if (mJsonObject.getString("Type").equals("POST_SHARE")) {
                mFilter = "id=" + mJsonObject.getJSONObject("Details").get("ID");
            }

            getMotoProfiles();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getMotoProfiles() {

        int mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        String mFilter = "UserID = " + mUserID;
        RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);

    }

    private void getPost(String mFilter) {
        if (mCurrentProfileObj != null) {
            mPostsRvTotalCount = -1;
            RetrofitClient.getRetrofitInstance().callGetProfilePosts(this, mFilter, RetrofitClient.GET_FEED_POSTS_RESPONSE, mDataLimit, mPostsRvOffset);
        }

    }


    @OnClick({R.id.toolbar_back_img_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
        }
    }

    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String dialogType, StringBuilder profileTypesStr, ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);

        switch (dialogType) {
            case AppDialogFragment.BOTTOM_DELETE_DIALOG:
                mPostPos = position;
                RetrofitClient.getRetrofitInstance().callDeleteProfilePosts(this, mPostList.get(position).getID(), RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE);
                RetrofitClient.getRetrofitInstance().callDeleteSharedPost(this, mPostList.get(position).getID(), RetrofitClient.DELETE_SHARED_POST_RESPONSE);

                break;
            case AppDialogFragment.BOTTOM_EDIT_DIALOG:
                mPostPos = position;
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(PostsModel.POST_MODEL, mPostList.get(position));
                mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mCurrentProfileObj);
                startActivityForResult(
                        new Intent(this, PostEditActivity.class).putExtras(mBundle),
                        AppConstants.POST_UPDATE_SUCCESS);
                break;

            case AppDialogFragment.BOTTOM_SHARE_DIALOG:
                mCurrentPostPosition = position;
                CommonAPI.getInstance().callPostShare(this, mPostList.get(mCurrentPostPosition), mCurrentProfileObj.getID());
                break;

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.POST_COMMENT_REQUEST:
                    assert data.getExtras() != null;
                    ArrayList<FeedCommentModel> mFeedCommentModel = (ArrayList<FeedCommentModel>) data.getExtras().getSerializable(PostsModel.COMMENTS_BY_POSTID);
                    mPostAdapter.refreshCommentList(mFeedCommentModel);
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

                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        mCurrentProfileObj = mProfileModel.getResource().get(getProfileCurrentPos());
                        getPost(mFilter);
                    } else {
                        showSnackBar(mCoordinatorLayout, mNoProfileErr);
                    }

                    break;

            }

        } else if (responseObj instanceof PostsModel) {

            PostsModel mPostsModel = (PostsModel) responseObj;

            switch (responseType) {

                case RetrofitClient.GET_FEED_POSTS_RESPONSE:

                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {

                        mPostsRvTotalCount = mPostsModel.getMeta().getCount();

                        mPostList.clear();

                        mPostList.addAll(mPostsModel.getResource());

                        mPostsRvOffset = mPostsRvOffset + mDataLimit;

                    } else {
                        if (mPostsRvOffset == 0) {
                            mPostsRvTotalCount = 0;
                        }
                        showToast(this, getString(R.string.post_deleted_err));
                    }

                    setAdapter();

                    break;

                case RetrofitClient.SHARED_POST_RESPONSE:

                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {

                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            PostsResModel mPostsResModel = mPostsModel.getResource().get(0);
                            mPostList.add(0, mPostsResModel);
                            mPostsRvTotalCount += 1;
                            setAdapter();
                            mPostAdapter.resetShareAdapter(mSharedFeed);
                        }
                        showSnackBar(mCoordinatorLayout, getResources().getString(R.string.post_shared));
                    }

                    break;
            }

        } else if (responseObj instanceof FeedCommentModel) {

            FeedCommentModel mFeedCommentList = (FeedCommentModel) responseObj;

            switch (responseType) {

                case RetrofitClient.POST_COMMENTS:
                    ArrayList<FeedCommentModel> mNewFeedComment = mFeedCommentList.getResource();
                    mPostAdapter.resetCommentAdapter(mNewFeedComment.get(0));
                    break;
            }
        } else if (responseObj instanceof FeedLikesModel) {

            FeedLikesModel mFeedLikesList = (FeedLikesModel) responseObj;
            ArrayList<FeedLikesModel> mNewFeedLike = mFeedLikesList.getResource();
            switch (responseType) {
                case RetrofitClient.POST_LIKES:
                    mPostAdapter.resetLikeAdapter(mNewFeedLike.get(0));
                    break;
                case RetrofitClient.POST_UNLIKE:
                    mPostAdapter.resetDisLike(mNewFeedLike.get(0));
                    break;
            }

        } else if (responseObj instanceof SessionModel) {

            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }

            getMotoProfiles();

        } else if (responseObj instanceof FeedShareModel) {

            FeedShareModel mFeedShareList = (FeedShareModel) responseObj;
            ArrayList<FeedShareModel> mNewFeedShare = mFeedShareList.getResource();
            switch (responseType) {
                case RetrofitClient.POST_SHARES:
                    mSharedFeed = mNewFeedShare.get(0);
                    CommonAPI.getInstance().callAddSharedPost(this, mPostList.get(mCurrentPostPosition), mCurrentProfileObj);
                    break;

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
        super.onDestroy();
    }

    @Override
    public int getTotalPostsResultCount() {
        return mPostsRvTotalCount;
    }


}
