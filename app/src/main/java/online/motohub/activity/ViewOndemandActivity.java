package online.motohub.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.OnDemandEventsAdapter;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.OndemandNewResponse;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.dialog.DialogManager;

/**
 * Created by Prithiv on 5/14/2018.
 */

public class ViewOndemandActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    public OnDemandEventsAdapter adapter;
    @BindView(R.id.list_view_co_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindString(R.string.notifications)
    String mToolbarTitle;
    @BindView(R.id.notification_recycler_view)
    RecyclerView mNotificationRecyclerView;
    @BindView(R.id.notification_refresh_layout)
    SwipeRefreshLayout mNotificationRefreshLayout;
    private ArrayList<OndemandNewResponse> mListOndemand;
    private int mCurrentProfileID = 0;
    private ProfileResModel mMyProfileResModel;
    private String api_key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ondemandnewdesign);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initView() {
        setToolbar(mToolbar, "On Demand");
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        mListOndemand = new ArrayList<>();
        // SwipeRefreshLayout
        mNotificationRefreshLayout.setOnRefreshListener(this);
        mNotificationRefreshLayout.setColorSchemeResources(R.color.colorOrange,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mNotificationRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mNotificationRefreshLayout.setRefreshing(true);
                // Fetching data from server
                callApi();
            }
        });

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mNotificationRecyclerView.setLayoutManager(mLinearLayoutManager);
        api_key = getResources().getString(R.string.dream_factory_api_key);
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mCurrentProfileID = mBundle.getInt(AppConstants.PROFILE_ID, 0);
        }
        getMyProfiles();
    }

    @OnClick(R.id.toolbar_back_img_btn)
    public void onClick(View v) {
        finish();
    }

    private void callApi() {
        mNotificationRefreshLayout.setRefreshing(false);
        //RetrofitClient.getRetrofitInstance().callGetOndemandList(this, api_key, RetrofitClient.CALL_GET_NOTIFICATIONS);
    }

    private void getMyProfiles() {
        String mFilter = "ID=" + mCurrentProfileID;
        if (isNetworkConnected(this))
            RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);
        else
            showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
    }

    private void setAdapter() {
        if (adapter == null) {
            adapter = new OnDemandEventsAdapter(this, mListOndemand, mCurrentProfileID, mMyProfileResModel);
            mNotificationRecyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void retrofitOnResponse(ArrayList<OndemandNewResponse> responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (RetrofitClient.CALL_GET_NOTIFICATIONS == responseType) {
            mListOndemand.addAll(responseObj);
            setAdapter();
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof ProfileModel) {
            ProfileModel mProfileModel = (ProfileModel) responseObj;
            String data = new Gson().toJson(responseObj);
            if (mProfileModel.getResource().size() > 0) {
                mMyProfileResModel = mProfileModel.getResource().get(0);
                setAdapter();
            } else {
                showSnackBar(mCoordinatorLayout, mNoProfileErr);
            }
        }
    }

    @Override
    public void retrofitOnFailure(int code, String message) {
        super.retrofitOnFailure(code, message);
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
    }

    @Override
    public void onRefresh() {
        // Fetching data from server
        //callApi();
        getMyProfiles();
    }

}
