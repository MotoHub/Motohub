package online.motohub.fragment.club;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.PostCommentsActivity;
import online.motohub.activity.PostEditActivity;
import online.motohub.adapter.club.ClubPostsAdapter;
import online.motohub.fragment.BaseFragment;
import online.motohub.fragment.dialog.AppDialogFragment;

import online.motohub.model.ClubGroupModel;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.CommonAPI;
import online.motohub.util.UploadFileService;
import online.motohub.util.Utility;

import static android.app.Activity.RESULT_OK;

public class ClubHomeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView mNewsFeedRecyclerView;

    @BindView(R.id.clubfeedlay)
    RelativeLayout mClubFeedLay;

    private static final int mDataLimit = 15;
    private Activity mActivity;
    private Unbinder mUnBinder;
    private LinearLayoutManager mNewsFeedLayoutManager;
    private boolean mIsPostsRvLoading = true;
    private int mPostsRvOffset = 0, mPostsRvTotalCount = 0;
    public boolean mRefresh = true;
    private ArrayList<PostsResModel> mNewsFeedList = new ArrayList<>();
    private ClubPostsAdapter mClubPostsAdapter;
    private PromotersResModel mPromotersResModel;
    private ProfileResModel mMyProfileResModel;
    private int mCurrentPostPosition;
    private String mNewSharedID;
    private FeedShareModel mSharedFeed;
    private int mPostPos;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updatePost(intent);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mRefresh = true;
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

        mMyProfileResModel = (ProfileResModel) getArguments().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        mPromotersResModel = (PromotersResModel) getArguments().getSerializable(PromotersModel.PROMOTERS_RES_MODEL);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mClubPostsAdapter = new ClubPostsAdapter(mNewsFeedList, mPromotersResModel, mMyProfileResModel, mActivity);
        mNewsFeedRecyclerView.setAdapter(mClubPostsAdapter);
        if (mNewsFeedList.size() == 0)
            callGetEvents();
    }

    @Override
    public void setRefresh(boolean refresh) {
        super.setRefresh(refresh);
        this.mRefresh = refresh;
    }

    @Override
    public void onRefresh() {
        setRefresh(true);
        mPostsRvOffset = 0;
        getNewsFeedPosts();
    }

    @Override
    public void callGetEvents() {
        super.callGetEvents();
        if (mRefresh) {
            mPostsRvTotalCount = -1;
            mPostsRvOffset = 0;
            mNewsFeedList.clear();
            mClubPostsAdapter.notifyDataSetChanged();
            getNewsFeedPosts();
        }
    }

    private void getNewsFeedPosts() {
        if (mNewsFeedList.size() == 0) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        ArrayList<ClubGroupModel> mClubGroupList = mPromotersResModel.getClubGroupByClubUserID();

        String mFilter = "(ProfileID=" + mPromotersResModel.getUserId() + ") AND (user_type=club)";

        if (mClubGroupList != null) {

            StringBuilder mClubMembersID = new StringBuilder();
            String mClubGroupMembers = "";

            if (!mClubGroupList.isEmpty()) {
                for (int i = 0; i < mClubGroupList.size(); i++) {
                    mClubMembersID.append(mClubGroupList.get(i).getMemberProfileID()).append(",");
                }
                mClubMembersID.deleteCharAt(mClubMembersID.length() - 1);
                mClubGroupMembers = String.valueOf(mClubMembersID);
            }

            for (int i = 0; i < mClubGroupList.size(); i++) {
                if (String.valueOf(mClubGroupList.get(i).getMemberProfileID()).trim().equals(String.valueOf(mMyProfileResModel.getID()).trim()) && mClubGroupList.get(i).getStatus() == 2) {
                    mFilter = "(" + mFilter + ") OR ((ProfileID IN (" + mClubGroupMembers + ")) AND (user_type=club_user))";
                    break;
                }
            }
        }

        RetrofitClient.getRetrofitInstance().callGetProfilePosts((BaseActivity) mActivity, mFilter, RetrofitClient.GET_FEED_POSTS_RESPONSE, mDataLimit, mPostsRvOffset);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof PostsModel) {
            PostsModel mPostsModel = (PostsModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_FEED_POSTS_RESPONSE:
                    mRefresh = false;
                    mSwipeRefreshLayout.setRefreshing(false);
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
                    mClubPostsAdapter.notifyDataSetChanged();
                    break;

                case RetrofitClient.SHARED_POST_RESPONSE:
                    mClubPostsAdapter.resetShareCount(mSharedFeed);
                    break;

                case RetrofitClient.CREATE_PROFILE_POSTS_RESPONSE:

                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {

                        PostsResModel mPostsResModel = mPostsModel.getResource().get(0);

                        ProfileResModel mProfileResModel = new ProfileResModel();
                        mProfileResModel.setProfilePicture(mMyProfileResModel.getProfilePicture());
                        mProfileResModel.setProfileType(mMyProfileResModel.getProfileType());
                        if (Utility.getInstance().isSpectator(mMyProfileResModel)) {
                            mProfileResModel.setSpectatorName(mMyProfileResModel.getSpectatorName());
                        } else {
                            mProfileResModel.setDriver(mMyProfileResModel.getDriver());
                        }

                        mPostsResModel.setProfilesByWhoPostedProfileID(mProfileResModel);

                        mPostsResModel.setProfileID(mMyProfileResModel.getID());

                        mPostsRvTotalCount = mPostsRvTotalCount + 1;

                        mNewsFeedList.add(0, mPostsResModel);
                        mClubPostsAdapter.notifyDataSetChanged();
                    }
                    break;
                case RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE:

                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {

                        mNewsFeedList.remove(mPostPos);
                        mPostsRvTotalCount -= 1;
                        mClubPostsAdapter.notifyDataSetChanged();
                    }
                    break;

            }
        } else if (responseObj instanceof SessionModel) {
            getNewsFeedPosts();
        } else if (responseObj instanceof FeedLikesModel) {

            FeedLikesModel mFeedLikesList = (FeedLikesModel) responseObj;
            ArrayList<FeedLikesModel> mNewFeedLike = mFeedLikesList.getResource();
            switch (responseType) {
                case RetrofitClient.POST_LIKES:
                    mClubPostsAdapter.resetLikeAdapter(mNewFeedLike.get(0));
                    break;
                case RetrofitClient.POST_UNLIKE:
                    mClubPostsAdapter.resetDisLike(mNewFeedLike.get(0));
                    break;
            }

        } else if (responseObj instanceof FeedShareModel) {
            FeedShareModel mFeedShareList = (FeedShareModel) responseObj;
            ArrayList<FeedShareModel> mNewFeedShare = mFeedShareList.getResource();
            switch (responseType) {
                case RetrofitClient.POST_SHARES:
                    mSharedFeed = mNewFeedShare.get(0);
                    CommonAPI.getInstance().callAddSharedPost(getContext(), mNewsFeedList.get(mCurrentPostPosition), mMyProfileResModel);
                    break;
            }
        }
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (code == RetrofitClient.GET_FEED_POSTS_RESPONSE) {
            mPostsRvTotalCount = 0;
        } else if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession((BaseActivity)mActivity,  RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            ((BaseActivity) mActivity).showToast(getActivity(), mErrorMsg);
        }
    }

    @Override
    public int getTotalPostsResultCount() {
        return mPostsRvTotalCount;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
    }

    @Override
    public void alertDialogPositiveBtnClick(String dialogType, int position) {
        super.alertDialogPositiveBtnClick(dialogType, position);
        switch (dialogType) {
            case AppDialogFragment.BOTTOM_SHARE_DIALOG:
                mCurrentPostPosition = position;
                CommonAPI.getInstance().callPostShare(getContext(),mNewsFeedList.get(mCurrentPostPosition),mMyProfileResModel.getID());
                break;
            case AppDialogFragment.BOTTOM_DELETE_DIALOG:
                mPostPos = position;
                RetrofitClient.getRetrofitInstance().callDeleteProfilePosts((BaseActivity) mActivity, mNewsFeedList.get(position).getID(), RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE);
                RetrofitClient.getRetrofitInstance().callDeleteSharedPost((BaseActivity) mActivity, mNewsFeedList.get(position).getID(), RetrofitClient.DELETE_SHARED_POST_RESPONSE);
                break;
            case AppDialogFragment.BOTTOM_EDIT_DIALOG:
                mPostPos = position;
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(PostsModel.POST_MODEL, mNewsFeedList.get(position));
                mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL,
                        mMyProfileResModel);
                getActivity().startActivityForResult(
                        new Intent(getActivity(), PostEditActivity.class).putExtras(mBundle),
                        AppConstants.POST_UPDATE_SUCCESS);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.POST_COMMENT_REQUEST:
                    assert data.getExtras() != null;
                    ArrayList<FeedCommentModel> mFeedCommentModel = (ArrayList<FeedCommentModel>)data.getExtras().getSerializable(PostsModel.COMMENTS_BY_POSTID);
                    mClubPostsAdapter.refreshCommentList(mFeedCommentModel);
                    break;
            }
        }
    }

    private void updatePost(Intent intent) {
        PostsResModel mPostsModel =
                (PostsResModel) intent.getSerializableExtra(PostsModel.POST_MODEL);
        if (intent.hasExtra(PostsModel.IS_UPDATE)) {
            mNewsFeedList.set(mPostPos, mPostsModel);
        } else {
            mPostsRvTotalCount = mPostsRvTotalCount + 1;
            mNewsFeedList.add(0, mPostsModel);
        }
        mClubPostsAdapter.notifyDataSetChanged();
        showSnackBar(mClubFeedLay, getString(R.string.post_update_success));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(UploadFileService.NOTIFY_POST_VIDEO_UPDATED));
        //MotoHub.getApplicationInstance().newsFeedFragmentOnResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mBroadcastReceiver);
        //MotoHub.getApplicationInstance().newsFeedFragmentOnPause();
    }

    public void callGetUnSubscribeFeed() {
        if (mNewsFeedList.size() == 0) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        mPostsRvTotalCount = -1;
        mPostsRvOffset = 0;
        mNewsFeedList.clear();
        mClubPostsAdapter.notifyDataSetChanged();

        String mFilter = "(ProfileID=" + mPromotersResModel.getUserId() + ") AND (user_type=club)";
        RetrofitClient.getRetrofitInstance().callGetProfilePosts((BaseActivity) mActivity, mFilter, RetrofitClient.GET_FEED_POSTS_RESPONSE, mDataLimit, mPostsRvOffset);
    }

}
