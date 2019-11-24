package online.motohub.newdesign.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.CreateProfileActivity;
import online.motohub.activity.ForgotPasswordScreen;
import online.motohub.activity.TermsAndConActivity;
import online.motohub.adapter.RecentUsersAdapter;
import online.motohub.database.DatabaseHandler;
import online.motohub.dialog.DialogManager;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.interfaces.CommonReturnInterface;
import online.motohub.model.LoginModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;
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

    @BindView(R.id.fb_login_btn)
    Button btnFB;

    @BindView(R.id.terms_conditions_check_box)
    CheckBox mTermsAndConChkBox;

    @BindString(R.string.terms_and_conditions)
    String mTermsAndConStr;

    @BindString(R.string.accept_terms_and_con)
    String mAcceptTermsAndConStr;

    @BindString(R.string.storage_permission_denied)
    String mNoStoragePer;

    @BindView(R.id.recent_users_list)
    RecyclerView mRecentUsersList;

    @BindView(R.id.recent_users_lay)
    RelativeLayout mRecentUsersLay;

    @BindString(R.string.internet_failure)
    String mInternetFailed;

    private String mEmail = "", mPwd = "";

    private String mLoginType = "0";

    private DatabaseHandler mDatabaseHandler;
    private ProfileResModel localDBModel;
    private ArrayList<ProfileResModel> mRecentList = new ArrayList<>();
    CommonReturnInterface mCommonReturnInterface = new CommonReturnInterface() {
        @Override
        public void onSuccess(int pos) {

            if (mRecentList.get(pos).getLoginType().equals("1")) {
                callFbLogin();
            } else {
                mEmail = mRecentList.get(pos).getEmail();
                mPwd = mRecentList.get(pos).getPassword();
                callEmailLogin();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        EventBus.getDefault().removeAllStickyEvents();
        mDatabaseHandler = new DatabaseHandler(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecentUsersList.setLayoutManager(manager);
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
            mLoginType = "1";
            RetrofitClient.getRetrofitInstance().callFacebookLogin(this, data.getQueryParameter("service"),
                    data.getQueryParameter("code"), data.getQueryParameter("state"), RetrofitClient.FACEBOOK_LOGIN_RESPONSE);
        }

        setRecentUserAdapter();

    }

    private void setRecentUserAdapter() {
        mRecentList.clear();
        mRecentList.addAll(mDatabaseHandler.getLocalUserList());
        if (mRecentList.size() > 0) {
            mRecentUsersLay.setVisibility(View.VISIBLE);
            RecentUsersAdapter mAdapter = new RecentUsersAdapter(this, mRecentList, mCommonReturnInterface);
            mRecentUsersList.setAdapter(mAdapter);
        }

    }

    @OnClick({R.id.fb_login_btn, R.id.register_btn, R.id.email_login_btn, R.id.forgot_pwd_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fb_login_btn:
                if (mTermsAndConChkBox.isChecked()) {
                    callFbLogin();
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

    private void callFbLogin() {
        Uri uri = Uri.parse(UrlUtils.BASE_URL + UrlUtils.FACEBOOK_LOGIN);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        finish();

        //startActivity(new Intent(this, BrowserActivity.class));
        //finish();
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
            mJsonObject.addProperty("remember_me", true);
            mLoginType = "0";
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
            switch (responseType) {
                case RetrofitClient.CALL_GET_PROFILE_USER_TYPE:
                    MyFireBaseMessagingService mMyFireBaseMessagingService = new MyFireBaseMessagingService();
                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                    mMyFireBaseMessagingService.sendRegistrationToken(refreshedToken, MyFireBaseMessagingService.UPDATE_PUSH_TOKEN);
                    int mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
                    String mUserType = mLoginModel.getPhone();
                    if (mUserType.equals(AppConstants.PROMOTER) || mUserType.equals(AppConstants.TRACK) || mUserType.equals(AppConstants.NEWS_MEDIA) || mUserType.equals(AppConstants.SHOP) || mUserType.equals(AppConstants.CLUB)) {
                        String mFilter = "user_id=" + mUserID;
                        RetrofitClient.getRetrofitInstance().callGetPromoterWithPushToken(this, mFilter, RetrofitClient.GET_PROMOTER_RESPONSE);
                    } else {
                        String mFilter = "UserID=" + mUserID;
                        RetrofitClient.getRetrofitInstance().callGetProfilesWithPushToken(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);
                    }

                    break;
                default:
                    saveUserDataLocally(mLoginModel);
                    RetrofitClient.getRetrofitInstance().callGetProfileUserType(this, RetrofitClient.CALL_GET_PROFILE_USER_TYPE);
                    break;
            }
        }

        if (responseObj instanceof ProfileModel) {

            ProfileModel mProfileModel = (ProfileModel) responseObj;

            if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                PreferenceUtils.getInstance(this).saveBooleanData(PreferenceUtils.USER_PROFILE_COMPLETED, true);
                localDBModel.setProfilePicture(mProfileModel.getResource().get(0).getProfilePicture());
                addUserToLocalDB();
                PERMISSION_ACTION_TYPE = PERMISSION_SHARING_WRITE_ACCESS;
                // if (isPermissionAdded()) {
                startActivity(new Intent(this, HomeActivity.class));
                // }
                finish();
            } else {
                addUserToLocalDB();
                startActivity(new Intent(this, CreateProfileActivity.class).putExtra(AppConstants.TAG, TAG));
                finish();
            }

        } else if (responseObj instanceof PromotersModel) {
            PromotersModel mPromoterModel = (PromotersModel) responseObj;
            if (mPromoterModel.getResource() != null && mPromoterModel.getResource().size() > 0) {
                localDBModel.setProfilePicture(mPromoterModel.getResource().get(0).getProfileImage());
                addUserToLocalDB();
                PreferenceUtils.getInstance(this).saveBooleanData(PreferenceUtils.BUSINESS_PROFILE_COMPLETED, true);
                // if (isPermissionAdded()) {
                startActivity(new Intent(this, BusinessProfileActivity.class));
                // }
                finish();
            }
        }

    }


    private void saveUserDataLocally(LoginModel loginModel) {
        localDBModel = new ProfileResModel();
        localDBModel.setEmail(loginModel.getEmail());
        localDBModel.setUserName(loginModel.getName());

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

        PreferenceUtils.getInstance(getApplicationContext()).saveBooleanData(PreferenceUtils.IS_PERMANATLY_DENIED_CONTACT_PERMISSION, false);

    }

    @Override
    public void retrofitOnError(int code, String message, int responsetype) {
        // super.retrofitOnError(code, message);
        if (code == 401) {
            showSnackBar(mCoordinatorLayout, getString(R.string.invalid_credentials));
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

    private void addUserToLocalDB() {
        localDBModel.setLoginType(mLoginType);
        localDBModel.setPassword(mPwd);

        mDatabaseHandler.addLocalUser(localDBModel);
    }


}
