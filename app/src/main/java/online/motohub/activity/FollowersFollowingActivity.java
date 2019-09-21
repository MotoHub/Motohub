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
import online.motohub.adapter.FollowersFollowingsAdapter;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.dialog.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.Utility;

public class FollowersFollowingActivity extends BaseActivity {

    @BindView(R.id.follow_user_parent)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.follow_user_list_view)
    RecyclerView mFollowersFollowingListView;
    @BindView(R.id.search_edt)
    EditText mSearchFollowingEt;

    @BindString(R.string.followers)
    String mToolbarFollowersTitle;
    @BindString(R.string.following)
    String mToolbarFollowingTitle;
    @BindString(R.string.no_followers_found_err)
    String mNoFollowersErr;
    @BindString(R.string.no_following_found_err)
    String mNoFollowingErr;
    @BindString(R.string.follow_success)
    String mFollowSuccess;
    @BindString(R.string.un_follow_success)
    String mUnFollowSuccess;

    private ArrayList<ProfileResModel> mFollowersFollowingList = new ArrayList<>();
    private FollowersFollowingsAdapter mFollowersFollowingAdapter;
    private ProfileResModel mMyProfileEntity;
    private boolean isMyFollowers;

    private int mMyProfileID;

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
        assert getIntent().getExtras() != null;

        /*mMyProfileEntity = (ProfileResModel) getIntent().getExtras().get(AppConstants.MY_PROFILE_OBJ);*/
        //mMyProfileEntity = MotoHub.getApplicationInstance().getmProfileResModel();
        mMyProfileEntity = EventBus.getDefault().getStickyEvent(ProfileResModel.class);

        assert mMyProfileEntity != null;
        mMyProfileID = mMyProfileEntity.getID();

        isMyFollowers = getIntent().getBooleanExtra(AppConstants.IS_FOLLOWERS, false);

        if (isMyFollowers) {
            setToolbar(mToolbar, mToolbarFollowersTitle);
        } else {
            setToolbar(mToolbar, mToolbarFollowingTitle);
        }
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        mFollowersFollowingListView.setLayoutManager(new LinearLayoutManager(this));

        getFollowersOrFollowingsList();

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

    private void searchProfile(String searchStr) {
        if (mFollowersFollowingList.size() == 0) {
            if (isMyFollowers) {
                showToast(this, getString(R.string.no_followers_found_err));
            } else {
                showToast(this, getString(R.string.no_following_found_err));
            }

            return;

        }
        ArrayList<ProfileResModel> filteredNames = new ArrayList<>();

        for (ProfileResModel mProfileEntity : mFollowersFollowingList) {
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
        if (mFollowersFollowingAdapter != null)
            mFollowersFollowingAdapter.filterList(filteredNames);
    }

    private void setAdapter() {
        if (mFollowersFollowingAdapter == null) {
            mFollowersFollowingAdapter = new FollowersFollowingsAdapter(this, mFollowersFollowingList, isMyFollowers, mMyProfileEntity);
            mFollowersFollowingListView.setAdapter(mFollowersFollowingAdapter);
        } else {
            mFollowersFollowingAdapter.notifyDataSetChanged();
        }
    }

    private void getFollowersOrFollowingsList() {
        String mFilter;
        String mIDs;
        String mErrMsg;
        if (isMyFollowers) {
            mIDs = Utility.getInstance().getMyFollowersFollowingsID(mMyProfileEntity.getFollowprofile_by_FollowProfileID(), isMyFollowers);
            mFilter = APIConstants.ID + " in (" + mIDs + ")";
            mErrMsg = mNoFollowersErr;
        } else {
            mIDs = Utility.getInstance().getMyFollowersFollowingsID(mMyProfileEntity.getFollowprofile_by_ProfileID(), isMyFollowers);
            mFilter = APIConstants.ID + " in (" + mIDs + ")";
            mErrMsg = mNoFollowingErr;
        }
        if (mIDs.isEmpty()) {
            showSnackBar(mCoordinatorLayout, mErrMsg);
            mFollowersFollowingList.clear();
            setAdapter();
            return;
        }

        if (isNetworkConnected(this))
            RetrofitClient.getRetrofitInstance().callGetProfilesWithFollowBlock(this, mFilter, RetrofitClient.GET_FOLLOWERS_FOLLOWING_PROFILE_RESPONSE);
        else
            showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
    }

    @OnClick(R.id.toolbar_back_img_btn)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.FOLLOWERS_FOLLOWING_RESULT:
                    assert data.getExtras() != null;
                    mMyProfileEntity = (ProfileResModel) data.getExtras().get(AppConstants.MY_PROFILE_OBJ);
                    getFollowersOrFollowingsList();
                    setResult(RESULT_OK, new Intent()
                            .putExtra(AppConstants.IS_FOLLOW_RESULT, true));
                    break;
            }
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof ProfileModel) {
            ProfileModel mResponse = (ProfileModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_FOLLOWERS_FOLLOWING_PROFILE_RESPONSE:
                    if (mResponse.getResource().size() > 0) {
                        mFollowersFollowingList.clear();
                        mFollowersFollowingList.addAll(mResponse.getResource());
                    } else {
                        if (isMyFollowers) {
                            showSnackBar(mCoordinatorLayout, mNoFollowersErr);
                        } else {
                            showSnackBar(mCoordinatorLayout, mNoFollowingErr);
                        }
                    }
                    setAdapter();
                    break;
            }

        } else if (responseObj instanceof SessionModel) {
            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            getFollowersOrFollowingsList();

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

    public FollowersFollowingActivity getInstance() {
        return this;
    }

}
