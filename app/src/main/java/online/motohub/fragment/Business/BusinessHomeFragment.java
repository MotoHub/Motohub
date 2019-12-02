package online.motohub.fragment.Business;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.adapter.business.BusinessPostAdapter;
import online.motohub.fragment.BaseFragment;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;

import static android.app.Activity.RESULT_OK;

public class BusinessHomeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final int mDataLimit = 15;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mNewsFeedRecyclerView;
    @BindView(R.id.shimmer_feeds)
    ShimmerFrameLayout mShimmer_feeds;
    private Activity mActivity;
    private Unbinder mUnBinder;
    private LinearLayoutManager mNewsFeedLayoutManager;
    private boolean mIsPostsRvLoading = true;
    private int mPostsRvOffset = 0, mPostsRvTotalCount = 0;
    private boolean mRefresh = true;
    private ArrayList<PostsResModel> mNewsFeedList;
    private BusinessPostAdapter mPromoterPostsAdapter;
    private PromotersResModel mPromotersResModel;
    BroadcastReceiver broadCastUploadStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String status = intent.getStringExtra("status");
                if (status.equalsIgnoreCase("File Uploaded")) {
                    if (mRefresh) {
                        mPostsRvTotalCount = -1;
                        mPostsRvOffset = 0;
                        mNewsFeedList.clear();
                        mPromoterPostsAdapter.notifyDataSetChanged();
                        getNewsFeedPosts();
                    }
                    showToast(getActivity(), "Video uploaded successfully");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(this.broadCastUploadStatus, new IntentFilter("UPLOAD_STATUS"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.broadCastUploadStatus);
    }

    private void initView() {

        mShimmer_feeds.startShimmerAnimation();
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
        /*ProfileResModel mMyProfileResModel = (ProfileResModel) getArguments().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        mPromotersResModel = (PromotersResModel) getArguments().getSerializable(PromotersModel.PROMOTERS_RES_MODEL);*/

        /*ProfileResModel mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        mPromotersResModel = MotoHub.getApplicationInstance().getmPromoterResModel();*/
        ProfileResModel mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
        mPromotersResModel = EventBus.getDefault().getStickyEvent(PromotersResModel.class);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mNewsFeedList = new ArrayList<>();
        //if (mMyProfileResModel != null && mMyProfileResModel.getID() != 0) {
        mPromoterPostsAdapter = new BusinessPostAdapter(mNewsFeedList, mMyProfileResModel, mActivity);
        mNewsFeedRecyclerView.setAdapter(mPromoterPostsAdapter);
        //}
        if (mNewsFeedList.size() == 0 && mPromotersResModel != null && mPromotersResModel.getID() != 0)
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
                    ArrayList<FeedCommentModel> mFeedCommentModel = (ArrayList<FeedCommentModel>) data.getExtras().getSerializable(PostsModel.COMMENTS_BY_POSTID);
                    mPromoterPostsAdapter.refreshCommentList(mFeedCommentModel);
                    break;
                case AppConstants.WRITE_POST_REQUEST:
                    mIsPostsRvLoading = true;
                    mPostsRvTotalCount = -1;
                    mPostsRvOffset = 0;
                    mNewsFeedList.clear();
                    getNewsFeedPosts();
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
            //mPromoterPostsAdapter.notifyDataSetChanged();
            getNewsFeedPosts();
        }
    }

    private void getNewsFeedPosts() {
        if (mNewsFeedList.size() == 0) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        String mFilter = "(ProfileID=" + mPromotersResModel.getUserId() + ") AND (user_type=" + mPromotersResModel.getUserType() + ")";
        RetrofitClient.getRetrofitInstance().callGetProfilePosts((BaseActivity) mActivity, mFilter, RetrofitClient.GET_FEED_POSTS_RESPONSE, mDataLimit, mPostsRvOffset);
    }

    @Override
    public void retrofitOnError(int code, String message) {
        if (code == RetrofitClient.GET_FEED_POSTS_RESPONSE) {
            mPostsRvTotalCount = 0;
        } else if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession((BaseActivity) mActivity, RetrofitClient.UPDATE_SESSION_RESPONSE);
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
                    try {
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
                        mShimmer_feeds.stopShimmerAnimation();
                        mShimmer_feeds.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        mPromoterPostsAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }
        } else if (responseObj instanceof SessionModel) {
            getNewsFeedPosts();
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

}
