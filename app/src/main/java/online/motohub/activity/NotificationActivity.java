package online.motohub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.NotificationAdapter;
import online.motohub.model.NotificationModel;
import online.motohub.model.NotificationResModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.dialog.DialogManager;
import online.motohub.util.PreferenceUtils;

public class NotificationActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, NotificationAdapter.TotalNotificationResultCount {

    private static final int mDataLimit = 15;

    @BindView(R.id.list_view_co_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindString(R.string.notifications)
    String mToolbarTitle;

    @BindView(R.id.notification_recycler_view)
    RecyclerView mNotificationRecyclerView;

    @BindString(R.string.no_notification_found)
    String mNoNotificationErr;

    @BindView(R.id.notification_refresh_layout)
    SwipeRefreshLayout mNotificationRefreshLayout;

    private ArrayList<NotificationResModel> mNotificationListData;
    private NotificationAdapter mNotificationAdapter;
    private ProfileResModel mProfileResModel;
    private LinearLayoutManager mLinearLayoutManager;
    private int mMsgRvOffset = 0, mMsgRvTotalCount = -1;
    private boolean mIsMsgRvLoading = true;
    private boolean mRefresh = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initView() {
        try {
            setToolbar(mToolbar, mToolbarTitle);
            showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

            mLinearLayoutManager = new LinearLayoutManager(this);
            mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            mNotificationRecyclerView.setLayoutManager(mLinearLayoutManager);

            mNotificationRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int mVisibleItemCount = mLinearLayoutManager.getChildCount();
                    int mTotalItemCount = mLinearLayoutManager.getItemCount();
                    int mFirstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();

                    if (!mIsMsgRvLoading && !(mMsgRvOffset >= mMsgRvTotalCount)) {
                        if ((mVisibleItemCount + mFirstVisibleItemPosition) >= mTotalItemCount
                                && mFirstVisibleItemPosition >= 0) {
                            mIsMsgRvLoading = true;
                            getNotifications();
                        }
                    }
                }
            });
            mNotificationListData = new ArrayList<>();
            mNotificationRefreshLayout.setOnRefreshListener(this);
            setNotificationAdapter();
            /*assert getIntent().getExtras() != null;
            mProfileResModel = (ProfileResModel) getIntent().getExtras().get(ProfileModel.MY_PROFILE_RES_MODEL);*/
            //mProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
            mProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
            getNotifications();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getNotifications() {

        if (mNotificationListData.size() == 0) {
            mNotificationRefreshLayout.setRefreshing(false);
        }

        if (mRefresh) {
            mMsgRvTotalCount = -1;
            mMsgRvOffset = 0;
            mNotificationListData.clear();
        }

        String mFilter = "((ReceiverID LIKE '%," + mProfileResModel.getID() + ",%') AND (SenderID " +
                "!= " + mProfileResModel.getID() + "))";
        RetrofitClient.getRetrofitInstance().callGetNotifications(this, mFilter, mDataLimit, mMsgRvOffset, RetrofitClient.CALL_GET_NOTIFICATIONS);
    }

    @OnClick(R.id.toolbar_back_img_btn)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
        }
    }

    private void setNotificationAdapter() {
        if (mNotificationAdapter == null) {
            mNotificationAdapter = new NotificationAdapter(this, mNotificationListData);
            mNotificationRecyclerView.setAdapter(mNotificationAdapter);
        } else {
            mNotificationAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        if (responseObj instanceof NotificationModel) {

            NotificationModel mNotificationModel = (NotificationModel) responseObj;

            switch (responseType) {

                case RetrofitClient.CALL_GET_NOTIFICATIONS:
                    if (mNotificationModel.getResource() != null && mNotificationModel.getResource().size() > 0) {

                        mMsgRvTotalCount = mNotificationModel.getMeta().getCount();
                        mIsMsgRvLoading = false;
                        mRefresh = false;
                        mNotificationRefreshLayout.setRefreshing(false);
                        if (mMsgRvOffset == 0) {
                            mNotificationListData.clear();
                        }
                        mNotificationListData.addAll(mNotificationModel.getResource());
                        mMsgRvOffset = mMsgRvOffset + mDataLimit;
                    } else {
                        if (mMsgRvOffset == 0) {
                            mMsgRvTotalCount = 0;
                        }
                        showSnackBar(mCoordinatorLayout, mNoNotificationErr);
                    }
                    setNotificationAdapter();
                    break;
            }

        } else if (responseObj instanceof SessionModel) {

            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            getNotifications();
        }
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mCoordinatorLayout, mErrorMsg);
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
    public void onRefresh() {
        mMsgRvOffset = 0;
        mIsMsgRvLoading = true;
        mMsgRvTotalCount = -1;
        mNotificationListData.clear();
        mNotificationRecyclerView.getRecycledViewPool().clear();
        mNotificationAdapter.notifyDataSetChanged();
        getNotifications();
    }

    @Override
    public int getTotalNotificationResultCount() {
        return mMsgRvTotalCount;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.FOLLOWERS_FOLLOWING_RESULT:
                    ProfileResModel mMyProfileResModel = (ProfileResModel) data.getExtras()
                            .getSerializable(AppConstants.MY_PROFILE_OBJ);
                    setResult(RESULT_OK, new Intent().putExtra(AppConstants.IS_FOLLOW_RESULT, true));
                    break;
            }
        }
    }
}
