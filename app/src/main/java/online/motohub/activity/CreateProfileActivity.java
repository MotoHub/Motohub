package online.motohub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.dialog.DialogManager;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.PushTokenModel;
import online.motohub.newdesign.activity.LoginActivity;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;

/**
 * Create Profile Activity.
 *
 * @version 1.0, 27/04/2017
 * @since 1.0
 */
public class CreateProfileActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener {

    @BindView(R.id.create_profile_co_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.bike_toggle_btn)
    ToggleButton mBikeToggleBtn;
    @BindView(R.id.boat_toggle_btn)
    ToggleButton mBoatToggleBtn;
    @BindView(R.id.car_toggle_btn)
    ToggleButton mCarToggleBtn;
    @BindView(R.id.kart_toggle_btn)
    ToggleButton mKartToggleBtn;
    @BindView(R.id.spectator_toggle_btn)
    ToggleButton mSpectatorToggleBtn;
    @BindString(R.string.create_your_profile)
    String mToolbarTitle;
    @BindString(R.string.memo)
    String mMemoStr;
    @BindString(R.string.select_profile_err)
    String mSelectProfileErr;
    ArrayList<String> mProfileTypes = new ArrayList<>();
    private boolean mCreateProfAfterReg = false;

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof PushTokenModel) {
            clearBeforeLogout();
            Intent loginActivity = new Intent(this, LoginActivity.class);
            loginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginActivity);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_settings_img_btn);
        mCreateProfAfterReg = getIntent().getBooleanExtra(CREATE_PROF_AFTER_REG, false);
        if (mCreateProfAfterReg) {
            clearProfileTypePreferences();
            showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        }
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.enter_btn, R.id.toolbar_settings_img_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                super.onBackPressed();
                break;
            case R.id.enter_btn:
                if (mProfileTypes.size() > 0) {
                    Intent mIntent = new Intent(this, CompleteProfileActivity.class);
                    mIntent.putStringArrayListExtra(PROFILE_TYPE, mProfileTypes);
                    mIntent.putExtra(AppConstants.TAG, getIntent().getStringExtra(AppConstants.TAG));
                    if (mCreateProfAfterReg) {
                        mIntent.putExtra(CREATE_PROF_AFTER_REG, true);
                        startActivityForResult(mIntent, AppConstants.CREATE_PROFILE_RES);
                    } else {
                        startActivity(mIntent);
                    }
                } else {
                    showSnackBar(mCoordinatorLayout, mSelectProfileErr);
                }
                break;
            case R.id.toolbar_settings_img_btn:
                showLogoutMenu(v);
                break;
        }
    }

    @OnCheckedChanged({R.id.bike_toggle_btn, R.id.boat_toggle_btn, R.id.car_toggle_btn,
            R.id.kart_toggle_btn, R.id.spectator_toggle_btn})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.bike_toggle_btn:
                if (isChecked) {
                    mBoatToggleBtn.setChecked(false);
                    mCarToggleBtn.setChecked(false);
                    mKartToggleBtn.setChecked(false);
                    mSpectatorToggleBtn.setChecked(false);
                }
                addOrRemoveProfileTypes(BIKE, isChecked);
                break;
            case R.id.boat_toggle_btn:
                if (isChecked) {
                    mBikeToggleBtn.setChecked(false);
                    mCarToggleBtn.setChecked(false);
                    mKartToggleBtn.setChecked(false);
                    mSpectatorToggleBtn.setChecked(false);
                }
                addOrRemoveProfileTypes(BOAT, isChecked);
                break;
            case R.id.car_toggle_btn:
                if (isChecked) {
                    mBikeToggleBtn.setChecked(false);
                    mBoatToggleBtn.setChecked(false);
                    mKartToggleBtn.setChecked(false);
                    mSpectatorToggleBtn.setChecked(false);
                }
                addOrRemoveProfileTypes(CAR, isChecked);
                break;
            case R.id.kart_toggle_btn:
                if (isChecked) {
                    mBikeToggleBtn.setChecked(false);
                    mBoatToggleBtn.setChecked(false);
                    mCarToggleBtn.setChecked(false);
                    mSpectatorToggleBtn.setChecked(false);
                }
                addOrRemoveProfileTypes(KART, isChecked);
                break;
            case R.id.spectator_toggle_btn:
                if (isChecked) {
                    mBikeToggleBtn.setChecked(false);
                    mBoatToggleBtn.setChecked(false);
                    mCarToggleBtn.setChecked(false);
                    mKartToggleBtn.setChecked(false);
                }
                addOrRemoveProfileTypes(SPECTATOR, isChecked);
                break;
        }
    }

    private void changeButtonBg(int id){

    }

    private void addOrRemoveProfileTypes(String profile_type, boolean isChecked) {
        if (isChecked) {
            mProfileTypes.add(profile_type);
        } else {
            mProfileTypes.remove(profile_type);
        }
    }

    @Override
    public void onBackPressed() {
        if (mCreateProfAfterReg) {
            super.onBackPressed();
        } else {
            showAppDialog(AppDialogFragment.ALERT_EXIT_DIALOG, null);
        }
    }

    @Override
    public void alertDialogPositiveBtnClick(
            BaseActivity activity, String mDialogType,
            StringBuilder profileTypesStr,
            ArrayList<String> profileTypes,
            int position) {
        super.alertDialogPositiveBtnClick(
                activity,
                mDialogType,
                profileTypesStr,
                profileTypes,
                position);
        switch (mDialogType) {
            case AppDialogFragment.ALERT_EXIT_DIALOG:
                finishAffinity();
                break;
        }
    }

    private void logout() {
        int mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        String mFilter = "UserID=" + mUserID;
        RetrofitClient.getRetrofitInstance().callDeletePushToken(this, mFilter, RetrofitClient.FACEBOOK_LOGOUT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.CREATE_PROFILE_RES:
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        logout();
        return true;
    }

}
