package online.motohub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.fcm.MyFireBaseInstanceIdService;
import online.motohub.model.LoginModel;
import online.motohub.model.ProfileModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.UrlUtils;

/**
 * Login Activity.
 *
 * @version 1.0, 27/04/2017
 * @since 1.0
 */
public class LoginActivity extends BaseActivity {

    public static final String TAG = LoginActivity.class.getName();

    @BindView(R.id.login_co_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.email_et)
    EditText mEmailEt;

    @BindView(R.id.pwd_et)
    EditText mPwdEt;

    @BindView(R.id.terms_conditions_check_box)
    CheckBox mTermsAndConChkBox;

    @BindString(R.string.terms_and_conditions)
    String mTermsAndConStr;

    @BindString(R.string.accept_terms_and_con)
    String mAcceptTermsAndConStr;

    @BindString(R.string.storage_permission_denied)
    String mNoStoragePer;

    private String mEmail = "", mPwd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        initView();

    }

    private void initView() {
        setupUI(mCoordinatorLayout);
        SpannableString mSpannableString = new SpannableString(mTermsAndConStr);
        PreferenceUtils.getInstance(this).saveBooleanData(PreferenceUtils.ALLOW_NOTIFICATION, true);
        PreferenceUtils.getInstance(this).saveBooleanData(PreferenceUtils.ALLOW_NOTIFICATION_Sound, true);
        PreferenceUtils.getInstance(this).saveBooleanData(PreferenceUtils.ALLOW_NOTIFICATION_VIB, true);
        ClickableSpan mClickableSpan = new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(LoginActivity.this, R.color.colorOrange));
                ds.setUnderlineText(true);
            }

            @Override
            public void onClick(View widget) {
                startActivity(new Intent(LoginActivity.this, TermsAndConActivity.class));
            }

        };

        mSpannableString.setSpan(mClickableSpan, 27, 47, 0);
        mTermsAndConChkBox.setText(mSpannableString);
        mTermsAndConChkBox.setMovementMethod(LinkMovementMethod.getInstance());

        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null) {
            Uri data = intent.getData();
            RetrofitClient.getRetrofitInstance().callFacebookLogin(this, data.getQueryParameter("service"),
                    data.getQueryParameter("code"), data.getQueryParameter("state"), RetrofitClient.FACEBOOK_LOGIN_RESPONSE);
        }
    }

    @OnClick({R.id.fb_login_btn, R.id.register_btn, R.id.email_login_btn, R.id.forgot_pwd_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fb_login_btn:
                if (mTermsAndConChkBox.isChecked()) {
                    Uri uri = Uri.parse(UrlUtils.BASE_URL + UrlUtils.FACEBOOK_LOGIN);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    finish();
//                    startActivity(new Intent(this, BrowserActivity.class));
//                    finish();
                } else {
                    showSnackBar(mCoordinatorLayout, mAcceptTermsAndConStr);
                }
                break;
            case R.id.register_btn:
                clearFields();
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.email_login_btn:
                validateFields();
                break;
            case R.id.forgot_pwd_btn:
                startActivity(new Intent(this, ForgotPasswordScreen.class));
                break;
        }
    }

    private void validateFields() {
        mEmail = mEmailEt.getText().toString().trim();
        mPwd = mPwdEt.getText().toString().trim();
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
        if (!mTermsAndConChkBox.isChecked()) {
            showSnackBar(mCoordinatorLayout, mAcceptTermsAndConStr);
            return;
        }
        callEmailLogin();
    }

    private void clearFields() {
        mEmailEt.setText("");
        mPwdEt.setText("");
    }

    private void callEmailLogin() {
        try {
            final JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(LoginModel.email, mEmail);
            mJsonObject.addProperty(LoginModel.password, mPwd);
            mJsonObject.addProperty("remember_me",true);
            RetrofitClient.getRetrofitInstance().callEmailLogin(this, mJsonObject,
                    RetrofitClient.EMAIL_LOGIN_RESPONSE);
        } catch (Exception e) {
            sysOut("" + e.getMessage());
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        if (responseObj instanceof LoginModel) {
            LoginModel mLoginModel = (LoginModel) responseObj;
            saveUserDataLocally(mLoginModel);
            MyFireBaseInstanceIdService mMyFireBaseInstanceIdService = new MyFireBaseInstanceIdService();
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            mMyFireBaseInstanceIdService.sendRegistrationToken(refreshedToken, MyFireBaseInstanceIdService.UPDATE_PUSH_TOKEN);
            int mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
            String mFilter = "UserID=" + mUserID;

            RetrofitClient.getRetrofitInstance().callGetProfilesWithPushToken(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);

        }

        if (responseObj instanceof ProfileModel) {

            ProfileModel mProfileModel = (ProfileModel) responseObj;

            if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                PreferenceUtils.getInstance(this).saveBooleanData(PreferenceUtils.USER_PROFILE_COMPLETED, true);
                startActivity(new Intent(this, ViewProfileActivity.class));
                finish();
            } else {
                startActivity(new Intent(this, CreateProfileActivity.class).putExtra(AppConstants.TAG, TAG));
                finish();
            }

        }

    }

    private void saveUserDataLocally(LoginModel loginModel) {

        PreferenceUtils mPreferenceUtils = PreferenceUtils.getInstance(this);

        mPreferenceUtils.saveBooleanData(PreferenceUtils.USER_KEEP_LOGGED_IN, true);

        mPreferenceUtils.saveStrData(PreferenceUtils.SESSION_TOKEN, loginModel.getSessionToken());

        if (loginModel.getSessionToken() == null || loginModel.getSessionToken().isEmpty()) {
            mPreferenceUtils.saveStrData(PreferenceUtils.SESSION_TOKEN, loginModel.getSessionId());
        }

        mPreferenceUtils.saveIntData(PreferenceUtils.USER_ID, loginModel.getId());

        mPreferenceUtils.saveStrData(PreferenceUtils.USER_EMAIL, loginModel.getEmail());

        mPreferenceUtils.saveStrData(PreferenceUtils.USER_NAME, loginModel.getName());

        mPreferenceUtils.saveStrData(PreferenceUtils.FIRST_NAME, loginModel.getFirstName());

    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (code == 401) {
            showSnackBar(mCoordinatorLayout, getString(R.string.invalid_credentials));
        } else {
            String mErrorMsg = code + " - " + message;
            //showSnackBar(mCoordinatorLayout, mErrorMsg);
        }

    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        showSnackBar(mCoordinatorLayout, mInternetFailed);
    }

}
