package online.motohub.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.dialog.DialogManager;
import online.motohub.model.CommonResponse;
import online.motohub.model.ErrorMessage;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.Utility;


public class ForgotPasswordScreen extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.email_edt)
    EditText mEmailEdt;
    @BindView(R.id.forgot_pwd_parent)
    CoordinatorLayout mCoordinatorLayout;
    private String mEmail = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_forgot_pwd_screen);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setToolbar(mToolbar, getString(R.string.forgot_pwd));
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        setupUI(mCoordinatorLayout);
//        mEmail= PreferenceHelper.getStringValue(this, PreferenceConstants.EMAIL);
//        mEmailEdt.setText(mEmail);
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.send_code_btn, R.id.already_have_code_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                break;
            case R.id.send_code_btn:
                validateFields();
                break;
            case R.id.already_have_code_btn:
                startActivity(new Intent(this, PasswordResetScreen.class));
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void validateFields() {
        mEmail = mEmailEdt.getText().toString().trim();
        if (mEmail.isEmpty()) {
            showSnackBar(mCoordinatorLayout, getString(R.string.empty_email));
        } else if (!Utility.isEmailValid(mEmail)) {
            showSnackBar(mCoordinatorLayout, getString(R.string.valid_email));
        } else {
            callForgotPassword();

        }
    }

    private void callForgotPassword() {
        try {
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(APIConstants.email, mEmail);
            RetrofitClient.getRetrofitInstance().callForgotPassword(this, mJsonObject);
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
                startActivity(new Intent(this, PasswordResetScreen.class));
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
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
        String mErrorMsg = code + " - " + message;
        showSnackBar(mCoordinatorLayout, mErrorMsg);

    }

    @Override
    public void onRequestError(ErrorMessage mErrObj) {
        super.onRequestError(mErrObj);
        showSnackBar(mCoordinatorLayout, mErrObj.getError().getMessage());
    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        showSnackBar(mCoordinatorLayout, mInternetFailed);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
