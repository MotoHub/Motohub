package online.motohub.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.PostEditActivity;
import online.motohub.adapter.PostsAdapter;
import online.motohub.application.MotoHub;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.CommonAPI;
import online.motohub.util.UploadFileService;
import online.motohub.util.Utility;

import static android.app.Activity.RESULT_OK;

public class NewsFeedFragment extends BaseFragment {

    private static final int mDataLimit = 15;

    @BindView(R.id.recycler_view)
    RecyclerView mNewsFeedRecyclerView;

    private Unbinder mUnBinder;
    private ArrayList<PostsResModel> mNewsFeedList;
    private PostsAdapter mNewsFeedAdapter;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updatePost(intent);
        }
    };
    //    private ArrayList<ProfileResModel> mFullMPList;
    private Activity mActivity;
    private boolean mRefresh = true;
    private int mPostPos;
    private int mPostsRvOffset = 0, mPostsRvTotalCount = 0;
    private boolean mIsPostsRvLoading = true;
    private LinearLayoutManager mNewsFeedLayoutManager;
    private int mCurrentPostPosition;
    private FeedShareModel mSharedFeed;

    private ProfileResModel mCurrentProfileObj;

    public static Fragment newInstance() {
        return new NewsFeedFragment();
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
            ((BaseActivity) getActivity()).showToast(getActivity(), getString(R.string.post_update_success));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnBinder = ButterKnife.bind(this, view);
        initView();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentProfileObj = (ProfileResModel) savedInstanceState.getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
            mRefresh = savedInstanceState.getBoolean("REFRESH");
            mPostsRvOffset = savedInstanceState.getInt("POST_RV_OFFSET");
            mPostsRvTotalCount = savedInstanceState.getInt("POST_RV_TOTAL_COUNT");
            mIsPostsRvLoading = savedInstanceState.getBoolean("IS_POST_RV_LOADING");
            mNewsFeedList.clear();
            mNewsFeedList.addAll((ArrayList<PostsResModel>)
                    savedInstanceState.getSerializable(PostsModel.POST_MODEL));
            setPostAdapter();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(UploadFileService.NOTIFY_POST_VIDEO_UPDATED));
        MotoHub.getApplicationInstance().newsFeedFragmentOnResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mBroadcastReceiver);
        MotoHub.getApplicationInstance().newsFeedFragmentOnPause();
    }
	
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
    }

    @SuppressWarnings("unchecked")
    private void initView() {

        mNewsFeedLayoutManager = new LinearLayoutManager(mActivity);
        mNewsFeedLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mNewsFeedRecyclerView.setLayoutManager(mNewsFeedLayoutManager);
        mNewsFeedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
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
        });

        mNewsFeedList = new ArrayList<>();

        getNewsFeedPosts();

    }

    private void setPostAdapter() {
        if (mNewsFeedAdapter == null) {
            mNewsFeedAdapter = new PostsAdapter(mNewsFeedList, mCurrentProfileObj, mActivity);
            mNewsFeedRecyclerView.setAdapter(mNewsFeedAdapter);
        } else {
            mNewsFeedAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void setRefresh(boolean refresh) {
        super.setRefresh(refresh);
        this.mRefresh = refresh;
    }

    @Override
    public void callFeedPost(ProfileResModel mProfileResModel) {
        super.callFeedPost(mProfileResModel);
        if (mRefresh) {
            mCurrentProfileObj = mProfileResModel;
            mPostsRvTotalCount = -1;
            mPostsRvOffset = 0;
            mNewsFeedList.clear();
            setPostAdapter();
            getNewsFeedPosts();
        }
    }

    private void getNewsFeedPosts() {
        if (mCurrentProfileObj != null) {
            String mFilter;
            String mMyFollowingsID = Utility.getInstance().getMyFollowersFollowingsID(mCurrentProfileObj.getFollowprofile_by_ProfileID(), false);
            String mBlockedUsers = Utility.getInstance().getMyBlockedUsersID(mCurrentProfileObj.getBlockedUserProfilesByProfileID(),
                    mCurrentProfileObj.getBlockeduserprofiles_by_BlockedProfileID());

            String mPromoterFollowers = Utility.getInstance().getMyPromoterFollowingsID(mCurrentProfileObj.getPromoterFollowerByProfileID());
            if (mMyFollowingsID.isEmpty()) {
                mFilter = "((ProfileID=" + mCurrentProfileObj.getID()
                        + ") OR (TaggedProfileID LIKE '%," + mCurrentProfileObj.getID()
                        + ",%') OR (SharedProfileID LIKE '%," + mCurrentProfileObj.getID()
                        + ",%')) AND ((user_type!='promoter') AND (user_type!='club') AND (user_type!='newsmedia') AND (user_type!='track'))";
            } else {
                mFilter = "((ProfileID IN (" + mMyFollowingsID + ","
                        + mCurrentProfileObj.getID()
                        + ") OR (TaggedProfileID LIKE '%," + mCurrentProfileObj.getID()
                        + ",%')  OR (SharedProfileID LIKE '%," + mCurrentProfileObj.getID()
                        + ",%')) AND ((user_type!='promoter') AND (user_type!='club') AND " + "(user_type!='newsmedia') AND (user_type!='track'))";
            }
            if (!mBlockedUsers.trim().isEmpty()) {
                mFilter = "(" + mFilter + ") AND (ProfileID NOT IN (" + mBlockedUsers + "))";
            }
            if (!mPromoterFollowers.trim().isEmpty()) {
                mFilter = "(" + mFilter + ") OR ((ProfileID IN (" + mPromoterFollowers + ")) AND ((user_type!='user') AND (user_type!='sharedPost')))";
            }
            if (this.isVisible()) {
                if (((BaseActivity) mActivity).isNetworkConnected()) {
                    RetrofitClient.getRetrofitInstance().callGetProfilePosts(
                            (BaseActivity) mActivity,
                            mFilter,
                            RetrofitClient.GET_FEED_POSTS_RESPONSE,
                            mDataLimit,
                            mPostsRvOffset);
                } else {
                    ((BaseActivity) mActivity).showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
            }
        }
    }

    @Override
    public void alertDialogPositiveBtnClick(String dialogType, int position) {
        super.alertDialogPositiveBtnClick(dialogType, position);
        switch (dialogType) {
            case AppDialogFragment.BOTTOM_DELETE_DIALOG:
                mPostPos = position;
                if (((BaseActivity) mActivity).isNetworkConnected()) {
                    RetrofitClient.getRetrofitInstance().callDeleteProfilePosts((BaseActivity) mActivity, mNewsFeedList.get(position).getID(), RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE);
                    RetrofitClient.getRetrofitInstance().callDeleteSharedPost((BaseActivity) mActivity, mNewsFeedList.get(position).getID(), RetrofitClient.DELETE_SHARED_POST_RESPONSE);
                } else {
                    ((BaseActivity) mActivity).showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
                }
                break;
            case AppDialogFragment.BOTTOM_EDIT_DIALOG:
                mPostPos = position;
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(PostsModel.POST_MODEL, mNewsFeedList.get(position));
                mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mCurrentProfileObj);
                getActivity().startActivityForResult(
                        new Intent(getActivity(), PostEditActivity.class).putExtras(mBundle),
                        AppConstants.POST_UPDATE_SUCCESS);
                break;
            case AppDialogFragment.BOTTOM_SHARE_DIALOG:
                mCurrentPostPosition = position;
                CommonAPI.getInstance().callPostShare(getContext(), mNewsFeedList.get(mCurrentPostPosition), mCurrentProfileObj.getID());
                break;
        }
    }

    @Override
    public int getTotalPostsResultCount() {
        return mPostsRvTotalCount;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.POST_UPDATE_SUCCESS:
                    if (data == null) {
                        ((BaseActivity) getActivity()).showToast(getActivity(), getString(R.string.post_video_uploading_update));
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
                case AppConstants.POST_COMMENT_REQUEST:
                    assert data.getExtras() != null;
                    ArrayList<FeedCommentModel> mFeedCommentModel = (ArrayList<FeedCommentModel>) data.getExtras().getSerializable(PostsModel.COMMENTS_BY_POSTID);
                    mNewsFeedAdapter.refreshCommentList(mFeedCommentModel);
                    break;
            }
        }
    }


    @Override
    public void retrofitOnError(int responseType, String errorMessage) {
        super.retrofitOnError(responseType, errorMessage);
        if (responseType == RetrofitClient.GET_FEED_POSTS_RESPONSE)
            mPostsRvTotalCount = 0;
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        if (responseObj != null) {

            if (responseObj instanceof PostsModel) {

                PostsModel mPostsModel = (PostsModel) responseObj;

                switch (responseType) {

                    case RetrofitClient.GET_FEED_POSTS_RESPONSE:
                        mRefresh = false;
                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            mPostsRvTotalCount = mPostsModel.getMeta().getCount();
                            mIsPostsRvLoading = false;
                            if (mPostsRvOffset == 0) {
                                mNewsFeedList.clear();
                            }
                            mNewsFeedList.addAll(mPostsModel.getResource());
                            mPostsRvOffset = mPostsRvOffset + mDataLimit;
                        } else {
                            if (mPostsRvOffset == 0) {
                                mPostsRvTotalCount = 0;
                            }
                        }
                        setPostAdapter();
                        mNewsFeedAdapter.refreshProfilePos();
                        break;
                    case RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE:
                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            mNewsFeedList.remove(mPostPos);
                            mPostsRvTotalCount -= 1;
                            setPostAdapter();
                            mNewsFeedAdapter.refreshProfilePos();
                        }
                        break;
                    case RetrofitClient.SHARED_POST_RESPONSE:
                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            PostsResModel mPostsResModel = mPostsModel.getResource().get(0);
                            mNewsFeedList.add(0, mPostsResModel);
                            mPostsRvTotalCount += 1;
                            setPostAdapter();
                            mNewsFeedAdapter.refreshProfilePos();
                        }
                        mNewsFeedAdapter.resetShareAdapter(mSharedFeed);
                        mNewsFeedAdapter.refreshProfilePos();
                        break;
                    case RetrofitClient.CREATE_PROFILE_POSTS_RESPONSE:
                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            PostsResModel mPostsResModel = mPostsModel.getResource().get(0);
                            ProfileResModel mProfileResModel = new ProfileResModel();
                            mProfileResModel.setProfilePicture(mCurrentProfileObj.getProfilePicture());
                            mProfileResModel.setProfileType(mCurrentProfileObj.getProfileType());
                            if (Utility.getInstance().isSpectator(mCurrentProfileObj)) {
                                mProfileResModel.setSpectatorName(mCurrentProfileObj.getSpectatorName());
                            } else {
                                mProfileResModel.setDriver(mCurrentProfileObj.getDriver());
                            }
                            mPostsResModel.setProfilesByWhoPostedProfileID(mProfileResModel);
                            mPostsResModel.setProfileID(mCurrentProfileObj.getID());
                            mPostsRvTotalCount = mPostsRvTotalCount + 1;
                            mNewsFeedList.add(0, mPostsResModel);
                            setPostAdapter();
                            mNewsFeedAdapter.refreshProfilePos();
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
            } else if (responseObj instanceof FeedLikesModel) {
                FeedLikesModel mFeedLikesList = (FeedLikesModel) responseObj;
                ArrayList<FeedLikesModel> mNewFeedLike = mFeedLikesList.getResource();
                switch (responseType) {
                    case RetrofitClient.POST_LIKES:
                        mNewsFeedAdapter.resetLikeAdapter(mNewFeedLike.get(0));
                        break;
                    case RetrofitClient.POST_UNLIKE:
                        mNewsFeedAdapter.resetDisLike(mNewFeedLike.get(0));
                        break;
                }

            } else if (responseObj instanceof FeedShareModel) {
                FeedShareModel mFeedShareList = (FeedShareModel) responseObj;
                ArrayList<FeedShareModel> mNewFeedShare = mFeedShareList.getResource();
                switch (responseType) {
                    case RetrofitClient.POST_SHARES:
                        mSharedFeed = mNewFeedShare.get(0);
                        CommonAPI.getInstance().callAddSharedPost(getContext(), mNewsFeedList.get(mCurrentPostPosition), mCurrentProfileObj);
                        break;
                    case RetrofitClient.DELETE_SHARED_POST_RESPONSE:
                        break;
                }
            }

        }
    }

}
