package online.motohub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.BlockedProfilesAdapter;
import online.motohub.application.MotoHub;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.BlockedUserResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.Utility;

public class BlockedUsersActivity extends BaseActivity {

    @BindView(R.id.follow_user_parent)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.follow_user_list_view)
    RecyclerView mBlockedUserListView;

    @BindView(R.id.search_edt)
    EditText mSearchFollowingEt;

    @BindString(R.string.blocked_users)
    String mToolbarTitle;

    @BindString(R.string.no_blocked_users_found_err)
    String mNoBlockedUserErr;

    @BindString(R.string.un_block_success)
    String mUnBlockSuccess;

    private ArrayList<ProfileResModel> mBlockedUserList = new ArrayList<>();
    private BlockedProfilesAdapter mBlockedProfilesAdapter;
    private ProfileResModel mMyProfileResModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_user_screen);

        ButterKnife.bind(this);

        initView();

    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initView() {
        setupUI(mCoordinatorLayout);
        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        //mMyProfileResModel = (ProfileResModel) getIntent().getSerializableExtra(ProfileModel.MY_PROFILE_RES_MODEL);
        //mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);

        mBlockedUserListView.setLayoutManager(new LinearLayoutManager(this));

        getBlockedProfileList();

        mSearchFollowingEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchProfile(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void getBlockedProfileList() {
        if (mMyProfileResModel.getBlockedUserProfilesByProfileID().size() > 0) {
            String mBlockedUsersID = Utility.getInstance().getMyBlockedUsersID(mMyProfileResModel.getBlockedUserProfilesByProfileID(), new ArrayList<BlockedUserResModel>());
            String mFilter = "(ID  IN (" + mBlockedUsersID + "))";
            if (isNetworkConnected(this))
                RetrofitClient.getRetrofitInstance().callGetProfilesWithFollowBlock(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);
            else
                showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
        } else {
            showToast(this, getString(R.string.no_blocked_users_found_err));
//            onBackPressed();
        }
    }

    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String dialogType, StringBuilder profileTypesStr, ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);
        switch (dialogType) {
            case AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG:
                getBlockedProfileList();
                break;
        }
    }

    private void searchProfile(String searchStr) {
        if (mBlockedUserList.size() > 0) {
            ArrayList<ProfileResModel> filteredNames = new ArrayList<>();
            for (ProfileResModel mProfileEntity : mBlockedUserList) {
                String mProfileName = "", mMotoName = "";
                mProfileName = Utility.getInstance().getUserName(mProfileEntity);
                if (Utility.getInstance().isSpectator(mProfileEntity)) {
                    if (mProfileName.toLowerCase().startsWith(searchStr.toLowerCase())) {
                        filteredNames.add(mProfileEntity);
                    }
                } else {
                    mMotoName = mProfileEntity.getMotoName();
                    if ((mProfileName.toLowerCase().startsWith(searchStr.toLowerCase()) ||
                            (mMotoName.toLowerCase().startsWith(searchStr.toLowerCase())))) {
                        filteredNames.add(mProfileEntity);
                    }
                }
            }
            if (mBlockedProfilesAdapter != null)
                mBlockedProfilesAdapter.filterList(filteredNames);
        } else {
            showToast(this, getString(R.string.no_blocked_users_found_err));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.FOLLOWERS_FOLLOWING_RESULT:
                    mMyProfileResModel = (ProfileResModel) data.getExtras()
                            .getSerializable(AppConstants.MY_PROFILE_OBJ);
                    getBlockedProfileList();
                    setResult(RESULT_OK, new Intent().putExtra(AppConstants.IS_FOLLOW_RESULT, true));
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @OnClick(R.id.toolbar_back_img_btn)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                break;
        }
    }

    private void setAdapter() {
        if (mBlockedProfilesAdapter == null) {
            mBlockedProfilesAdapter = new BlockedProfilesAdapter(this, mBlockedUserList, mMyProfileResModel);
            mBlockedUserListView.setAdapter(mBlockedProfilesAdapter);
        } else {
            mBlockedProfilesAdapter.updateProfile(mMyProfileResModel);
            mBlockedProfilesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        if (responseObj instanceof ProfileModel) {
            ProfileModel mResponse = (ProfileModel) responseObj;
            if (mResponse.getResource().size() > 0) {
                switch (responseType) {
                    case RetrofitClient.GET_PROFILE_RESPONSE:
                        mBlockedUserList.clear();
                        mBlockedUserList.addAll(mResponse.getResource());
                        setAdapter();
                        break;
                }
            } else {
                showSnackBar(mCoordinatorLayout, getString(R.string.no_blocked_users_found_err));
            }


        } else if (responseObj instanceof SessionModel) {
            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            getBlockedProfileList();

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

}

