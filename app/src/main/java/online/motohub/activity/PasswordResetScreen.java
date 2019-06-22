package online.motohub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.model.CommonResponse;
import online.motohub.model.ErrorMessage;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.dialog.DialogManager;
import online.motohub.util.Utility;

public class PasswordResetScreen extends BaseActivity {


    @BindView(R.id.password_reset_parent)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.email_edt)
    EditText mEmailEdt;
    @BindView(R.id.reset_code_edt)
    EditText mResetCodeEdt;
    @BindView(R.id.pwd_edt)
    EditText mPwdEdt;
    @BindView(R.id.conf_pwd_edt)
    EditText mConfPwdEdt;
    @BindView(R.id.change_pwd_btn)
    Button mChangePwdBtn;
    @BindView(R.id.scroll_view)
    ScrollView mScrollView;
    private String mResetCode = "", mEmail = "", mPwd = "", mConfPwd = "";
    private boolean isFromDeepLink = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset_screen);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null) {
            Uri data = intent.getData();
            sysOut("" + data);
            isFromDeepLink = true;
            mEmail = data.getQueryParameter("email");
            mEmailEdt.setText(mEmail);
            mEmailEdt.setEnabled(false);
            mResetCode = data.getQueryParameter("resetcode");
            mResetCodeEdt.setText(mResetCode);
            mResetCodeEdt.setEnabled(false);
            sysOut("resetcode = " + mResetCode);
            sysOut("email = " + mEmail);
        }
        initView();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initView() {
        setupUI(mCoordinatorLayout);
        setToolbar(mToolbar, getString(R.string.enter_code));
        if (!isFromDeepLink) {
            showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        }

    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.change_pwd_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                break;
            case R.id.change_pwd_btn:
                validateFields();
                break;
        }
    }

    private void validateFields() {
        mEmail = mEmailEdt.getText().toString().trim();
        mResetCode = mResetCodeEdt.getText().toString().trim();
        mPwd = mPwdEdt.getText().toString().trim();
        mConfPwd = mConfPwdEdt.getText().toString().trim();
        if (mEmail.isEmpty() && mResetCode.isEmpty() &&
                mPwd.isEmpty() && mConfPwd.isEmpty()) {
            showSnackBar(mCoordinatorLayout, getString(R.string.empty_all));
        } else if (mEmail.isEmpty()) {
            showSnackBar(mCoordinatorLayout, getString(R.string.empty_email));
        } else if (!Utility.isEmailValid(mEmail)) {
            showSnackBar(mCoordinatorLayout, getString(R.string.valid_email));
        } else if (mResetCode.isEmpty()) {
            showSnackBar(mCoordinatorLayout, getString(R.string.empty_reset_code));
        } else if (mPwd.isEmpty()) {
            showSnackBar(mCoordinatorLayout, getString(R.string.empty_pwd));
        } else if (mPwd.length() < 6) {
            showSnackBar(mCoordinatorLayout, getString(R.string.length_pwd));
        } else if (mConfPwd.isEmpty()) {
            showSnackBar(mCoordinatorLayout, getString(R.string.empty_conf_pwd));
        } else if (!mPwd.equals(mConfPwd)) {
            showSnackBar(mCoordinatorLayout, getString(R.string.mismatch_pwd));
        } else {
            callResetPassword();
        }
    }

    private void callResetPassword() {
        try {
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(APIConstants.email, mEmail);
            mJsonObject.addProperty(APIConstants.code, mResetCode);
            mJsonObject.addProperty(APIConstants.new_password, mPwd);
            RetrofitClient.getRetrofitInstance().callResetPassword(this, mJsonObject);
        } catch (Exception e) {
            sysOut("" + e.getMessage());
        }
    }


    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        if (responseObj instanceof CommonResponse) {
            CommonResponse mResponse = (CommonResponse) responseObj;
            if (mResponse.isSuccess()) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                showSnackBar(mCoordinatorLayout, getString(R.string.no_blocked_users_found_err));
            }
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
    public void onRequestError(ErrorMessage mErrObj) {
        super.onRequestError(mErrObj);
        showSnackBar(mCoordinatorLayout, mErrObj.getError().getMessage());
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
    public void onBackPressed() {
        if (isFromDeepLink) {
            finish();
        } else {
            startActivity(new Intent(this, ForgotPasswordScreen.class));
            finish();
        }

    }

}
