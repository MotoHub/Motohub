package online.motohub.activity;

import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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
import online.motohub.adapter.StreamUserAdapter;
import online.motohub.dialog.DialogManager;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;

public class ViewStreamUsersActivity extends BaseActivity {


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
    @BindString(R.string.send_stream_request)
    String mToolbarTitle;
    boolean isFromNotification;
    private ArrayList<ProfileResModel> mUsersList = new ArrayList<>();
    private StreamUserAdapter mAdapter;
    private int mCurrentProfileID = 0;
    private String mMyFollowings = "";
    private LinearLayoutManager mLayoutManager;
    private int MY_PROFILE_TYPE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_users);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            isFromNotification = mBundle.getBoolean(AppConstants.IS_FROM_NOTIFICATION_TRAY, false);
            mCurrentProfileID = mBundle.getInt(AppConstants.PROFILE_ID, 0);
            mMyFollowings = mBundle.getString(AppConstants.MY_FOLLOWINGS, "");
        }

        setupUI(mParentLay);
        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        mLayoutManager = new LinearLayoutManager(this);
        mUserListView.setLayoutManager(mLayoutManager);
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
        if (isFromNotification) {
            callGetMyProfile(mCurrentProfileID, MY_PROFILE_TYPE);
        } else {
            callGetUsersList();
        }

    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void searchUser(String searchStr) {
        ArrayList<ProfileResModel> filteredNames = new ArrayList<>();
        for (ProfileResModel entity : mUsersList) {
            if (entity != null) {
                String mName = "";
                if (entity.getProfileType() == 5) {
                    mName = entity.getSpectatorName();
                } else {
                    mName = entity.getDriver();
                }
                if (mName.toLowerCase().contains(searchStr.toLowerCase())) {
                    filteredNames.add(entity);
                }
            }
        }
        if (mAdapter != null)
            mAdapter.filterList(filteredNames);
    }

    private void callGetUsersList() {
        //TODO get profile object from previous screen
//        mMyFollowings = Utility.getInstance().getMyFollowersFollowingsID(.getFollowprofile_by_ProfileID(), false));
        if (mMyFollowings.isEmpty()) {
            showToast(this, "No friends are found!!!. Please follow your friends first!!!");
            finish();
        } else {
            int userID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
            String mFilter = "(UserID!=" + userID + ") AND (ID in (" + mMyFollowings + "))";
            RetrofitClient.getRetrofitInstance().callGetAllStreamUserProfile(this, mFilter);
        }
    }

    private void callGetMyProfile(int mProfileID, int responseType) {
        String mFilter = "id=" + mProfileID;
        RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, responseType);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof ProfileModel) {
            ProfileModel mProfileResponse = (ProfileModel) responseObj;
            if (mProfileResponse.getResource().size() > 0) {
                if (MY_PROFILE_TYPE == responseType) {
                    callGetUsersList();
                } else {
                    mUsersList.addAll(mProfileResponse.getResource());
                    setAdapter();
                    mUserListView.getLayoutManager().scrollToPosition(0);
                }

            } else {
                showToast(ViewStreamUsersActivity.this, "No Friends are available!!!");
                finish();
            }

        } else if (responseObj instanceof SessionModel) {
            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            callGetUsersList();
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
            mAdapter = new StreamUserAdapter(this, mCurrentProfileID, mUsersList);
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
