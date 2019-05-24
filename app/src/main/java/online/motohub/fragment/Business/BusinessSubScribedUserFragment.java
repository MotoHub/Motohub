package online.motohub.fragment.Business;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.adapter.club.ClubSubscribedUsersAdapter;
import online.motohub.fragment.BaseFragment;
import online.motohub.fragment.club.ClubSubscribedUsersFragment;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PromoterSubsResModel;
import online.motohub.model.promoter_club_news_media.PromoterSubs;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;

public class BusinessSubScribedUserFragment extends BaseFragment {

    private static final int mDataLimit = 15;
    private static final String TAG = ClubSubscribedUsersFragment.class.getName();
    public boolean mRefresh = true;
    @BindView(R.id.club_users)
    RecyclerView mClubUsersRV;
    @BindString(R.string.no_users_err)
    String mNoUsersSubscribed;
    LinearLayoutManager mClubLayout;
    ClubSubscribedUsersAdapter mClubSubsUserAdapter;
    PromotersResModel mPromotersResModel;
    ProfileResModel mMyProfileResModel;
    ArrayList<PromoterSubs> mClubsubusersList = new ArrayList<>();
    private Activity mActivity;
    private Unbinder mUnBinder;
    private boolean mIsPostsRvLoading = true;
    private int mPostsRvOffset = 0, mPostsRvTotalCount = 0;

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
        return inflater.inflate(R.layout.fragment_clubusers, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnBinder = ButterKnife.bind(this, view);
        initView();
    }

    public void initView() {
        assert getArguments() != null;
        /*mPromotersResModel = (PromotersResModel) getArguments().getSerializable(PromotersModel.PROMOTERS_RES_MODEL);
        mMyProfileResModel = (ProfileResModel) getArguments().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);*/

        /*mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        mPromotersResModel = MotoHub.getApplicationInstance().getmPromoterResModel();*/
        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
        mPromotersResModel = EventBus.getDefault().getStickyEvent(PromotersResModel.class);
        mClubLayout = new LinearLayoutManager(mActivity);
        mClubLayout.setOrientation(LinearLayoutManager.VERTICAL);
        mClubUsersRV.setLayoutManager(mClubLayout);
        mClubSubsUserAdapter = new ClubSubscribedUsersAdapter(mClubsubusersList, mActivity, TAG);
        mClubUsersRV.setAdapter(mClubSubsUserAdapter);
        mClubUsersRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int mVisibleItemCount = mClubLayout.getChildCount();
                int mTotalItemCount = mClubLayout.getItemCount();
                int mFirstVisibleItemPosition = mClubLayout.findFirstVisibleItemPosition();
                if (!mIsPostsRvLoading && !(mPostsRvOffset >= mPostsRvTotalCount)) {
                    if ((mVisibleItemCount + mFirstVisibleItemPosition) >= mTotalItemCount
                            && mFirstVisibleItemPosition >= 0) {
                        mIsPostsRvLoading = true;
                        callGetSubscribedusers();
                    }
                }
            }
        });
    }

    @Override
    public void setRefresh(boolean refresh) {
        super.setRefresh(refresh);
        this.mRefresh = refresh;
    }

    public void onRefresh() {
        setRefresh(true);
        mPostsRvOffset = 0;
        callGetSubscribedusers();
    }

    private void callGetSubscribedusers() {
        String mFilter = "PromoterID=" + mPromotersResModel.getUserId();
        RetrofitClient.getRetrofitInstance()
                .getPromotersSubs((BaseActivity) getActivity(), mFilter, RetrofitClient.GET_CLUB_USERS, mDataLimit, mPostsRvOffset);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof PromoterSubsResModel) {
            PromoterSubsResModel mClubSubscribedUsers = (PromoterSubsResModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_CLUB_USERS:
                    mRefresh = false;
                    if (mClubSubscribedUsers.getResource() != null && mClubSubscribedUsers.getResource().size() > 0) {
                        mPostsRvTotalCount = mClubSubscribedUsers.getMeta().getCount();
                        mIsPostsRvLoading = false;
                        if (mPostsRvOffset == 0) {
                            mClubsubusersList.clear();
                        }
                        mClubsubusersList.addAll(mClubSubscribedUsers.getResource());
                        mPostsRvOffset = mPostsRvOffset + mDataLimit;
                    } else {
                        if (mPostsRvOffset == 0) {
                            mPostsRvTotalCount = 0;
                        }
                        ((BaseActivity) mActivity).showToast(mActivity, mNoUsersSubscribed);
                    }
                    mClubSubsUserAdapter.notifyDataSetChanged();
                    break;
                case RetrofitClient.UPDATE_SUBSCRIPTION:
                    if (mClubSubscribedUsers.getResource().size() > 0) {
                        mClubsubusersList.add(mClubSubscribedUsers.getResource().get(0));
                        mClubSubsUserAdapter.notifyDataSetChanged();
                    }
                    break;
                case RetrofitClient.CALL_REMOVE_PROMOTER_SUBS:
                    if (mClubSubscribedUsers.getResource().size() > 0) {
                        for (int i = 0; i < mClubsubusersList.size(); i++) {
                            if (mClubsubusersList.contains(mClubSubscribedUsers.getResource().get(0))) {
                                mClubsubusersList.remove(i);
                                mClubSubsUserAdapter.notifyDataSetChanged();
                                break;

                            }
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (code == RetrofitClient.GET_CLUB_USERS) {
            mPostsRvTotalCount = 0;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
    }

    @Override
    public void retrofitOnFailure(int responseType) {
        super.retrofitOnFailure(responseType);
        ((BaseActivity) mActivity).showAppDialog(AppDialogFragment.ALERT_API_FAILURE_DIALOG, null);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mClubsubusersList.size() == 0) {
            callGetSubscribedusers();
        }
    }

    public void updateMembers() {
        mPostsRvOffset = 0;
        callGetSubscribedusers();
    }
}