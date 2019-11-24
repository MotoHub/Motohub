package online.motohub.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.StreamRequestedUserAdapter;
import online.motohub.dialog.DialogManager;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.LiveStreamRequestEntity;
import online.motohub.model.LiveStreamRequestResponse;
import online.motohub.model.SessionModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;

public class ViewRequestUsersActivity extends BaseActivity {


    @BindView(R.id.parent_lay)
    CoordinatorLayout mParentLay;
    @BindView(R.id.users_list)
    RecyclerView mUserListView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.search_lay)
    LinearLayout mSearchLay;
    @BindView(R.id.search_edt)
    EditText mSearchEdt;
    @BindString(R.string.requested_users)
    String mToolbarTitle;
    private ArrayList<LiveStreamRequestEntity> mUsersList = new ArrayList<>();
    private StreamRequestedUserAdapter mAdapter;
    private int mCurrentProfileID = 0;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_users);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initView() {

        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mCurrentProfileID = mBundle.getInt(AppConstants.PROFILE_ID, 0);
        }
        setupUI(mParentLay);
        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        mLayoutManager = new LinearLayoutManager(this);
        mUserListView.setLayoutManager(mLayoutManager);
        callGetRequestedUsersList();

        mSearchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void searchUser(String searchStr) {
        ArrayList<LiveStreamRequestEntity> filteredNames = new ArrayList<>();
        for (LiveStreamRequestEntity entity : mUsersList) {
            if (entity != null) {
                String mName = "";
                if (entity.getProfiles_by_RequestedProfileID().getProfileType() == 5) {
                    mName = entity.getProfiles_by_RequestedProfileID().getSpectatorName();
                } else {
                    mName = entity.getProfiles_by_RequestedProfileID().getDriver();
                }
                if (mName.toLowerCase().contains(searchStr.toLowerCase())) {
                    filteredNames.add(entity);
                }
            }
        }
        if (mAdapter != null)
            mAdapter.filterList(filteredNames);
    }

    private void callGetRequestedUsersList() {
        String mFilter = "ReceiverProfileID=" + mCurrentProfileID;
        RetrofitClient.getRetrofitInstance().callGetRequestedUsersList(this, mFilter);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof LiveStreamRequestResponse) {
            LiveStreamRequestResponse mRequestedUserResponse = (LiveStreamRequestResponse) responseObj;
            if (mRequestedUserResponse.getResource().size() > 0) {
                mUsersList.addAll(mRequestedUserResponse.getResource());
                setAdapter();
                mUserListView.getLayoutManager().scrollToPosition(0);
            } else {
                showToast(this, "No requested user's are available!!!");
                finish();
            }


        } else if (responseObj instanceof SessionModel) {
            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            callGetRequestedUsersList();
        }
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showToast(this, mErrorMsg);
        }
    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
    }

    private void setAdapter() {
        if (mUsersList.size() == 0) {
            mSearchLay.setVisibility(View.GONE);
        } else {
            mSearchLay.setVisibility(View.VISIBLE);
        }
        if (mAdapter != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        } else {
            mAdapter = new StreamRequestedUserAdapter(this, mCurrentProfileID, mUsersList);
            mUserListView.setAdapter(mAdapter);
        }
    }

    @OnClick({R.id.toolbar_back_img_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
