package online.motohub.fragment.Performance_Shop;

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
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PromoterSubsResModel;
import online.motohub.model.promoter_club_news_media.PromoterSubs;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;

public class PerfVehiclesFragment extends BaseFragment {

    private static final int mDataLimit = 15;
    private static final String TAG = PerfVehiclesFragment.class.getName();
    public boolean mRefresh = true;
    @BindView(R.id.shop_vehicles)
    RecyclerView mShopVehiclesRV;
    @BindString(R.string.no_vehicles_err)
    String mNoVehiclesSubscribed;
    LinearLayoutManager mShopLayout;
    ClubSubscribedUsersAdapter mShopVehiclesAdapter;
    PromotersResModel mPromotersResModel;
    ProfileResModel mMyProfileResModel;
    ArrayList<PromoterSubs> mShopVehiclesList = new ArrayList<>();
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
        return inflater.inflate(R.layout.fragment_shopvehicles, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnBinder = ButterKnife.bind(this, view);
        initView();
    }

    public void initView() {
        /*mPromotersResModel = (PromotersResModel) getArguments().getSerializable(PromotersModel.PROMOTERS_RES_MODEL);
        mMyProfileResModel = (ProfileResModel) getArguments().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);*/
        /*mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        mPromotersResModel = MotoHub.getApplicationInstance().getmPromoterResModel();*/
        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
        mPromotersResModel = EventBus.getDefault().getStickyEvent(PromotersResModel.class);
        mShopLayout = new LinearLayoutManager(mActivity);
        mShopLayout.setOrientation(LinearLayoutManager.VERTICAL);
        mShopVehiclesRV.setLayoutManager(mShopLayout);
        mShopVehiclesAdapter = new ClubSubscribedUsersAdapter(mShopVehiclesList, mActivity, TAG);
        mShopVehiclesRV.setAdapter(mShopVehiclesAdapter);
        mShopVehiclesRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int mVisibleItemCount = mShopLayout.getChildCount();
                int mTotalItemCount = mShopLayout.getItemCount();
                int mFirstVisibleItemPosition = mShopLayout.findFirstVisibleItemPosition();
                if (!mIsPostsRvLoading && !(mPostsRvOffset >= mPostsRvTotalCount)) {
                    if ((mVisibleItemCount + mFirstVisibleItemPosition) >= mTotalItemCount
                            && mFirstVisibleItemPosition >= 0) {
                        mIsPostsRvLoading = true;
                        callGetVehicles();
                    }
                }
            }
        });
    }

    public void updateVehicles() {
        mPostsRvOffset = 0;
        callGetVehicles();
    }

    @Override
    public void setRefresh(boolean refresh) {
        super.setRefresh(refresh);
        this.mRefresh = refresh;
    }

    public void onRefresh() {
        setRefresh(true);
        mPostsRvOffset = 0;
        callGetVehicles();
    }

    private void callGetVehicles() {
        String mFilter = "(PromoterID=" + mPromotersResModel.getUserId() + ") AND (" + PromoterSubs.SUBS_STATUS + " =1)";
        RetrofitClient.getRetrofitInstance()
                .getPromotersSubs((BaseActivity) getActivity(), mFilter, RetrofitClient.GET_SHOP_VEHICLES, mDataLimit, mPostsRvOffset);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof PromoterSubsResModel) {
            PromoterSubsResModel mShopSubscribedVehicles = (PromoterSubsResModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_SHOP_VEHICLES:
                    mRefresh = false;
                    if (mShopSubscribedVehicles.getResource() != null && mShopSubscribedVehicles.getResource().size() > 0) {
                        mPostsRvTotalCount = mShopSubscribedVehicles.getMeta().getCount();
                        mIsPostsRvLoading = false;
                        if (mPostsRvOffset == 0) {
                            mShopVehiclesList.clear();
                        }
                        mShopVehiclesList.addAll(mShopSubscribedVehicles.getResource());
                        mPostsRvOffset = mPostsRvOffset + mDataLimit;
                    } else {
                        if (mPostsRvOffset == 0) {
                            mPostsRvTotalCount = 0;
                        }
                        ((BaseActivity) mActivity).showToast(mActivity, mNoVehiclesSubscribed);
                    }
                    mShopVehiclesAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (code == RetrofitClient.GET_SHOP_VEHICLES) {
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
        ((BaseActivity) getActivity()).showAppDialog(AppDialogFragment.ALERT_API_FAILURE_DIALOG, null);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mShopVehiclesList.size() == 0) {
            callGetVehicles();
        }
    }

}