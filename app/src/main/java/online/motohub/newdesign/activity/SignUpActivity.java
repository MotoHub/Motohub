package online.motohub.newdesign.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.dialog.DialogManager;
import online.motohub.model.ErrorMessage;
import online.motohub.model.SignUpResModel;
import online.motohub.retrofit.RetrofitClient;

public class SignUpActivity extends BaseActivity {

    @BindView(R.id.sign_up_co_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.first_name_et)
    EditText mFirstNameEt;

    @BindView(R.id.last_name_et)
    EditText mLastNameEt;

    @BindView(R.id.email_et)
    EditText mEmailEt;

    @BindView(R.id.pwd_et)
    EditText mPwdEt;

    @BindView(R.id.conf_pwd_et)
    EditText mConfPwdEt;

    @BindString(R.string.internet_failure)
    String mInternetFailed;

    String mFirstName = "", mLastName = "", mEmail = "", mPwd = "", mConfPwd = "", mPhoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setupUI(mCoordinatorLayout);
        setToolbar(mToolbar, getString(R.string.register));
        setToolbarLeftBtn(mToolbar);
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    @OnClick({R.id.submit_btn, R.id.toolbar_back_img_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.submit_btn:
                validateFields();
                break;
        }
    }

    private void validateFields() {
        mFirstName = mFirstNameEt.getText().toString().trim();
        mLastName = mLastNameEt.getText().toString().trim();
        mEmail = mEmailEt.getText().toString().trim();
        mPwd = mPwdEt.getText().toString().trim();
        mConfPwd = mConfPwdEt.getText().toString().trim();
        //mPhoneNumber = mPhoneNumberEt.getText().toString().trim();
        if (mFirstName.isEmpty() && mLastName.isEmpty() && mEmail.isEmpty() &&
                mPwd.isEmpty() && mConfPwd.isEmpty()) {
            showSnackBar(mCoordinatorLayout, getString(R.string.enter_all_fields));
            return;
        }
        if (mFirstName.isEmpty()) {
            showSnackBar(mCoordinatorLayout, getString(R.string.enter_first_name));
            return;
        }
        if (mLastName.isEmpty()) {
            showSnackBar(mCoordinatorLayout, getString(R.string.enter_last_name));
            return;
        }
        /*if (mPhoneNumber.isEmpty()) {
            showSnackBar(mCoordinatorLayout, getString(R.string.enter_phone_number));
            return;
        }*/
        if (mEmail.isEmpty()) {
            showSnackBar(mCoordinatorLayout, getString(R.string.enter_email));
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            showSnackBar(mCoordinatorLayout, getString(R.string.enter_valid_email));
            return;
        }
        if (mPwd.isEmpty()) {
            showSnackBar(mCoordinatorLayout, getString(R.string.enter_pwd));
            return;
        }
        if (mPwd.length() < 6) {
            showSnackBar(mCoordinatorLayout, getString(R.string.enter_min_pwd));
            return;
        }
        if (mConfPwd.isEmpty()) {
            showSnackBar(mCoordinatorLayout, getString(R.string.enter_conf_pwd));
            return;
        }
        if (!mPwd.equals(mConfPwd)) {
            showSnackBar(mCoordinatorLayout, getString(R.string.mismatch_pwd));
            return;
        }
        callRegister();
    }

    private void callRegister() {
        try {
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(SignUpResModel.first_name, mFirstName);
            mJsonObject.addProperty(SignUpResModel.last_name, mLastName);
            mJsonObject.addProperty(SignUpResModel.new_password, mPwd);
            mJsonObject.addProperty(SignUpResModel.email, mEmail);
            //mJsonObject.addProperty(SignUpResModel.phone_number, mPhoneNumber);
            RetrofitClient.getRetrofitInstance().callSignUp(this, mJsonObject, RetrofitClient
                    .EMAIL_SIGN_UP_RESPONSE);
        } catch (Exception e) {
            sysOut("" + e.getMessage());
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof SignUpResModel) {
            SignUpResModel mSignUpResModel = (SignUpResModel) responseObj;
            if (mSignUpResModel.isSuccess()) {
                Toast.makeText(this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public void onRequestError(ErrorMessage mErrObj) {
        super.onRequestError(mErrObj);
        if (mErrObj.getError().getContext() != null && mErrObj.getError().getContext().getEmail() != null) {
            showSnackBar(mCoordinatorLayout, mErrObj.getError().getContext().getEmail()[0]);
        } else {
            showSnackBar(mCoordinatorLayout, mErrObj.getError().getMessage());
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
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        showSnackBar(mCoordinatorLayout, mInternetFailed);
    }

}
