package online.motohub.fragment.Performance_Shop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import online.motohub.adapter.news_and_media.NewsAndMediaPostsAdapter;
import online.motohub.fragment.BaseFragment;
import online.motohub.fragment.dialog.AppDialogFragment;
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

import static android.app.Activity.RESULT_OK;

public class PerfNewsFeedFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView mNewsFeedRecyclerView;

    private static final int mDataLimit = 15;
    private Activity mActivity;
    private Unbinder mUnBinder;
    private LinearLayoutManager mNewsFeedLayoutManager;
    private boolean mIsPostsRvLoading = true;
    private int mPostsRvOffset = 0, mPostsRvTotalCount = 0;
    private boolean mRefresh = true;
    private ArrayList<PostsResModel> mNewsFeedList;
    private NewsAndMediaPostsAdapter mPromoterPostsAdapter;
    private PromotersResModel mPromotersResModel;
    private ProfileResModel mMyProfileResModel;
    private int mCurrentPostPosition;
    private String mNewSharedID;
    private FeedShareModel mSharedFeed;

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

    @Override
    public void onResume() {
        super.onResume();
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

        assert getArguments() != null;
        mMyProfileResModel = (ProfileResModel) getArguments().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        mPromotersResModel = (PromotersResModel) getArguments().getSerializable(PromotersModel.PROMOTERS_RES_MODEL);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mNewsFeedList = new ArrayList<>();
        mPromoterPostsAdapter = new NewsAndMediaPostsAdapter(mNewsFeedList, mMyProfileResModel, mActivity);
        mNewsFeedRecyclerView.setAdapter(mPromoterPostsAdapter);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.POST_COMMENT_REQUEST:
                    assert data.getExtras() != null;
                    ArrayList<FeedCommentModel> mFeedCommentModel = (ArrayList<FeedCommentModel>)data.getExtras().getSerializable(PostsModel.COMMENTS_BY_POSTID);
                    mPromoterPostsAdapter.refreshCommentList(mFeedCommentModel);
                    break;
            }
        }
    }

    @Override
    public void callGetEvents() {
        super.callGetEvents();
        if (mRefresh) {
            mPostsRvTotalCount = -1;
            mPostsRvOffset = 0;
            mNewsFeedList.clear();
            mPromoterPostsAdapter.notifyDataSetChanged();
            getNewsFeedPosts();
        }
    }

    private void getNewsFeedPosts() {
        if (mNewsFeedList.size() == 0) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        String mFilter = "(ProfileID=" + mPromotersResModel.getUserId() + ") AND (user_type=shop)";
        RetrofitClient.getRetrofitInstance().callGetProfilePosts((BaseActivity) mActivity, mFilter, RetrofitClient.GET_FEED_POSTS_RESPONSE, mDataLimit, mPostsRvOffset);
    }

    @Override
    public void retrofitOnError(int code, String message) {
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
                    mPromoterPostsAdapter.notifyDataSetChanged();
                    break;
                case RetrofitClient.SHARED_POST_RESPONSE:
                    mPromoterPostsAdapter.resetShareCount(mSharedFeed);
                    break;

            }
        } else if (responseObj instanceof SessionModel) {
            getNewsFeedPosts();
        } else if (responseObj instanceof FeedLikesModel) {

            FeedLikesModel mFeedLikesList = (FeedLikesModel) responseObj;
            ArrayList<FeedLikesModel> mNewFeedLike = mFeedLikesList.getResource();
            switch (responseType) {
                case RetrofitClient.POST_LIKES:
                    mPromoterPostsAdapter.resetLikeAdapter(mNewFeedLike.get(0));
                    break;
                case RetrofitClient.POST_UNLIKE:
                    mPromoterPostsAdapter.resetDisLike(mNewFeedLike.get(0));
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
        }
    }
}