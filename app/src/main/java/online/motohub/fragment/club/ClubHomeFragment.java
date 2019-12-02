package online.motohub.fragment.club;

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
import android.widget.RelativeLayout;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.PostEditActivity;
import online.motohub.activity.ReportActivity;
import online.motohub.adapter.club.ClubPostsAdapter;
import online.motohub.dialog.DialogManager;
import online.motohub.fragment.BaseFragment;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.SharePostInterface;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.NotificationBlockedUsersModel;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.services.UploadFileService;
import online.motohub.util.CommonAPI;
import online.motohub.util.Utility;

import static android.app.Activity.RESULT_OK;

public class ClubHomeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final int mDataLimit = 15;
    public boolean mRefresh = true;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mNewsFeedRecyclerView;
    @BindView(R.id.clubfeedlay)
    RelativeLayout mClubFeedLay;
    @BindView(R.id.shimmer_feeds)
    ShimmerFrameLayout mShimmer_feeds;
    private Activity mActivity;
    private Unbinder mUnBinder;
    private LinearLayoutManager mNewsFeedLayoutManager;
    private boolean mIsPostsRvLoading = true;
    private int mPostsRvOffset = 0, mPostsRvTotalCount = 0;
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
            Bundle b = intent.getExtras();
            String mUsertype = b.getString(AppConstants.USER_TYPE);
            if (mUsertype.trim().equals("club_user"))
                updatePost(intent);
        }
    };
    private String mShareTxt = "";
    SharePostInterface mShareTextWithPostInterface = new SharePostInterface() {
        @Override
        public void onSuccess(String shareMessage) {
            mShareTxt = shareMessage;
            CommonAPI.getInstance().callPostShare(getActivity(), mNewsFeedList.get(mCurrentPostPosition), mMyProfileResModel.getID());
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

        /*mMyProfileResModel = (ProfileResModel) getArguments().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        mPromotersResModel = (PromotersResModel) getArguments().getSerializable(PromotersModel.PROMOTERS_RES_MODEL);*/
        /*mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        mPromotersResModel = MotoHub.getApplicationInstance().getmPromoterResModel();*/
        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
        mPromotersResModel = EventBus.getDefault().getStickyEvent(PromotersResModel.class);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        try {
            mClubPostsAdapter = new ClubPostsAdapter(mNewsFeedList, mPromotersResModel, mMyProfileResModel, mActivity);
            mNewsFeedRecyclerView.setAdapter(mClubPostsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        mIsPostsRvLoading = true;
        mPostsRvTotalCount = -1;
        mNewsFeedList.clear();
        getNewsFeedPosts();
    }

    @Override
    public void callGetEvents() {
        super.callGetEvents();
        if (mRefresh) {
            mPostsRvTotalCount = -1;
            mPostsRvOffset = 0;
            mIsPostsRvLoading = true;
            mNewsFeedList.clear();
            mClubPostsAdapter.notifyDataSetChanged();
            getNewsFeedPosts();
        }
    }

    private void getNewsFeedPosts() {
        if (mNewsFeedList.size() == 0) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        String mFilter = "((ProfileID=" + mPromotersResModel.getUserId() + ") AND (user_type=club)) OR ((" + PostsModel.TO_SUBSCRIBED_USER_ID + "=" + mPromotersResModel.getUserId() + ") AND (user_type = club_user)) AND (ReportStatus == 0)";
        RetrofitClient.getRetrofitInstance().callGetProfilePosts((BaseActivity) mActivity, mFilter, RetrofitClient.GET_FEED_POSTS_RESPONSE, mDataLimit, mPostsRvOffset);
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
                        mClubPostsAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case RetrofitClient.SHARED_POST_RESPONSE:
                    mClubPostsAdapter.resetShareCount(mSharedFeed);
                    break;
                case RetrofitClient.FEED_VIDEO_COUNT:
                    try {
                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            mClubPostsAdapter.addViewCount(mPostsModel.getResource().get(0).getViewCount());
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    break;
                case RetrofitClient.ADD_FEED_COUNT:
                    try {
                        if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                            mClubPostsAdapter.ViewCount(mPostsModel.getResource().get(0).getViewCount());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                    CommonAPI.getInstance().callAddSharedPost(getContext(), mNewsFeedList.get(mCurrentPostPosition), mMyProfileResModel, mShareTxt);
                    break;
                case RetrofitClient.DELETE_SHARED_POST_RESPONSE:
                    RetrofitClient.getRetrofitInstance()
                            .callDeleteProfilePosts((BaseActivity) mActivity, mNewsFeedList.get(mCurrentPostPosition).getID(), RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE);
                    break;
            }
        } else if (responseObj instanceof NotificationBlockedUsersModel) {
            NotificationBlockedUsersModel mNotify = (NotificationBlockedUsersModel) responseObj;
            ArrayList<NotificationBlockedUsersModel> mPostNotification = mNotify.getResource();
            switch (responseType) {
                case RetrofitClient.BLOCK_NOTIFY:
                    if (mPostNotification.size() > 0)
                        mClubPostsAdapter.resetBlock(mPostNotification.get(0));
                    break;
                case RetrofitClient.UNBLOCK_NOTIFY:
                    if (mPostNotification.size() > 0)
                        mClubPostsAdapter.resetUnBlock(mPostNotification.get(0));
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
            RetrofitClient.getRetrofitInstance().callUpdateSession((BaseActivity) mActivity, RetrofitClient.UPDATE_SESSION_RESPONSE);
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
                DialogManager.showShareDialogWithCallback(getActivity(), mShareTextWithPostInterface);
                // CommonAPI.getInstance().callPostShare(getContext(), mNewsFeedList.get(mCurrentPostPosition), mMyProfileResModel.getID());
                break;
            case AppDialogFragment.BOTTOM_DELETE_DIALOG:
                mPostPos = position;
                RetrofitClient.getRetrofitInstance().callDeleteSharedPost((BaseActivity) mActivity, mNewsFeedList.get(position).getNewSharedPostID(), RetrofitClient.DELETE_SHARED_POST_RESPONSE);
                break;
            case AppDialogFragment.BOTTOM_EDIT_DIALOG:
                mPostPos = position;
                //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                EventBus.getDefault().postSticky(mMyProfileResModel);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(PostsModel.POST_MODEL, mNewsFeedList.get(position));
                /*mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL,
                        mMyProfileResModel);*/
                getActivity().startActivityForResult(
                        new Intent(getActivity(), PostEditActivity.class).putExtras(mBundle),
                        AppConstants.POST_UPDATE_SUCCESS);
                /*MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                MotoHub.getApplicationInstance().setmPostResModel(mNewsFeedList.get(position));
                getActivity().startActivityForResult(
                        new Intent(getActivity(), PostEditActivity.class),
                        AppConstants.POST_UPDATE_SUCCESS);*/
                break;
            case AppDialogFragment.BOTTOM_REPORT_ACTION_DIALOG:
                startActivityForResult(
                        new Intent(getActivity(), ReportActivity.class)
                                .putExtra(PostsModel.POST_ID, mNewsFeedList.get(position).getID())
                                .putExtra(ProfileModel.PROFILE_ID, mMyProfileResModel.getID())
                                .putExtra(ProfileModel.USER_ID, mMyProfileResModel.getUserID())
                                .putExtra(AppConstants.REPORT, AppConstants.REPORT_POST), AppConstants.REPORT_POST_SUCCESS);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RetrofitClient.UPDATE_FEED_COUNT:
                    assert data.getExtras() != null;
                    mClubPostsAdapter.updateView(data.getIntExtra(AppConstants.POSITION, 0));
                    break;
                case AppConstants.POST_COMMENT_REQUEST:
                    assert data.getExtras() != null;
                    ArrayList<FeedCommentModel> mFeedCommentModel = (ArrayList<FeedCommentModel>) data.getExtras().getSerializable(PostsModel.COMMENTS_BY_POSTID);
                    mClubPostsAdapter.refreshCommentList(mFeedCommentModel);
                    break;
                case AppConstants.WRITE_POST_REQUEST:
                    mIsPostsRvLoading = true;
                    mPostsRvTotalCount = -1;
                    mPostsRvOffset = 0;
                    mNewsFeedList.clear();
                    getNewsFeedPosts();
                    break;
                case AppConstants.REPORT_POST_SUCCESS:
                    //TODO remove the reported post
                    mIsPostsRvLoading = true;
                    mPostsRvTotalCount = -1;
                    mPostsRvOffset = 0;
                    mNewsFeedList.clear();
                    getNewsFeedPosts();
                    break;
                case AppConstants.POST_UPDATE_SUCCESS:
                    mIsPostsRvLoading = true;
                    mPostsRvTotalCount = -1;
                    mPostsRvOffset = 0;
                    mNewsFeedList.clear();
                    getNewsFeedPosts();
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