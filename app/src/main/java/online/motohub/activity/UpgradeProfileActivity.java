package online.motohub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import online.motohub.R;
import online.motohub.dialog.DialogManager;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.ImageModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;

public class UpgradeProfileActivity extends BaseActivity {

    @BindView(R.id.complete_profile_co_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.profile_title_tv)
    TextView mProfileTitleTv;
    @BindView(R.id.coverImage)
    ImageView mCoverImgView;
    @BindView(R.id.add_profile_img)
    CircleImageView mCirProfileImgView;
    @BindView(R.id.upload_profile_img_tv)
    TextView mUploadProfileImgTv;
    @BindView(R.id.driver_tv)
    TextView mDriverOrNameTv;
    @BindView(R.id.driver_et)
    EditText mDriverOrNameEt;
    @BindView(R.id.driver_line)
    View mDriverOrNameLine;
    @BindView(R.id.name_of_moto_box)
    LinearLayout mNameOfMotoBox;
    @BindView(R.id.name_of_moto_tv)
    TextView mNameOfMotoTv;
    @BindView(R.id.name_of_moto_et)
    EditText mNameOfMotoEt;
    @BindView(R.id.name_of_moto_line)
    View mNameOfMotoLine;
    @BindView(R.id.make_box)
    LinearLayout mMakeBox;
    @BindView(R.id.make_et)
    EditText mMakeEt;
    @BindView(R.id.make_line)
    View mMakeLine;
    @BindView(R.id.model_box)
    LinearLayout mModelBox;
    @BindView(R.id.model_et)
    EditText mModelEt;
    @BindView(R.id.phone_et)
    EditText mPhoneEt;
    @BindView(R.id.upgrade_btn)
    Button mUpgradeBtn;
    @BindView(R.id.profile_type_spin)
    Spinner mProfileTypeSpin;

    @BindString(R.string.complete_your_profile)
    String mToolbarTitle;
    @BindString(R.string.driver)
    String mDriverStr;
    @BindString(R.string.name)
    String mNameStr;
    @BindString(R.string.upload_moto_photo)
    String mUploadMotoPhotoStr;
    @BindString(R.string.upload_photo)
    String mUploadPhotoStr;
    @BindString(R.string.camera_permission_denied)
    String mNoCameraPer;
    @BindString(R.string.storage_permission_denied)
    String mNoStoragePer;
    @BindString(R.string.done)
    String mDoneStr;
    @BindString(R.string.get_started)
    String mGetStartedStr;
    @BindString(R.string.driver_name_err)
    String mDriverNameErr;
    @BindString(R.string.make_name_err)
    String mMakeErr;
    @BindString(R.string.model_err)
    String mModelErr;
    @BindString(R.string.enter_phone_number)
    String mPhoneErr;
    @BindString(R.string.enter_all_fields)
    String mEnterAllFieldsErr;
    @BindString(R.string.enter_name)
    String mEnterNameErr;
    @BindString(R.string.failed)
    String mFailed;
    @BindString(R.string.profile_created)
    String mProfileCreated;
    @BindString(R.string.profile_completed)
    String mProfileCompleted;

    private boolean mCheckInAppPurchasesLocally = false;
    private boolean mSubmitProfileData = false;
    private boolean isCoverPicture;
    private String mCoverImgUri, mProfilePicImgUri, mUploadedServerCoverImgFileUrl, mProfileType, mProfileTypeStr;
    private ArrayList<String> mTypeList;
    private ProfileResModel mMyProfileResModel;
    private String myProfileObj, mPurchaseType;
    private boolean isPurchased = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_profile);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        setupUI(mCoordinatorLayout);
        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        myProfileObj = getIntent().getStringExtra(AppConstants.MY_PROFILE_OBJ);
        mMyProfileResModel = new Gson().fromJson(myProfileObj, ProfileResModel.class);
        setOldData();

        mCheckInAppPurchasesLocally = true;

        mTypeList = new ArrayList<>();
        mTypeList.addAll(Arrays.asList(getResources().getStringArray(R.array.racer_type)));
        setProfileTypeAdapter();
        mProfileTypeSpin.setSelection(0);
        mProfileType = getProfileType(CAR_STR);
        mProfileTypeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mProfileTypeStr = mTypeList.get(pos);
                mProfileType = getProfileType(mProfileTypeStr);
                setProfileReady(mProfileTypeStr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setOldData() {
        mDriverOrNameEt.setText(mMyProfileResModel.getSpectatorName());
        String mCoverPicUrl = mMyProfileResModel.getCoverPicture();
        String mProfilePicUrl = mMyProfileResModel.getProfilePicture();
        if (mCoverPicUrl.isEmpty()) {
            setImageWithGlide(
                    mCoverImgView, mCoverPicUrl,
                    R.drawable.default_cover_img);
        }
        if (mProfilePicUrl.isEmpty()) {
            setImageWithGlide(
                    mCirProfileImgView,
                    mProfilePicUrl,
                    R.drawable.default_profile_icon);
        }
    }

    private void setProfileTypeAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_black, mTypeList);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mProfileTypeSpin.setAdapter(adapter);
        mProfileTypeSpin.setSelection(0);
    }

    private String getProfileType(String mProfileTypeStr) {
        String mProfileType = "";
        switch (mProfileTypeStr) {
            case BIKE_STR:
                mProfileType = BIKE;
                break;
            case BOAT_STR:
                mProfileType = BOAT;
                break;
            case CAR_STR:
                mProfileType = CAR;
                break;
            case KART_STR:
                mProfileType = KART;
                break;
        }
        return mProfileType;
    }

    private void setProfileReady(String mProfileTitleStr) {
        mProfileTitleTv.setText(mProfileTitleStr);
        String mNameOfMotoStr = (
                getString(R.string.name_of) + " " + mProfileTitleStr);
        mNameOfMotoTv.setText(mNameOfMotoStr);
        mNameOfMotoEt.setHint(mNameOfMotoStr);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        isCoverPicture = savedInstanceState.getBoolean(ProfileModel.IS_COVER_PICTURE);
        myProfileObj = savedInstanceState.getString(AppConstants.MY_PROFILE_OBJ);
        mMyProfileResModel = new Gson().fromJson(myProfileObj, ProfileResModel.class);
        mCoverImgUri = savedInstanceState.getString(ProfileModel.COVER_PICTURE);
        mProfilePicImgUri = savedInstanceState.getString(ProfileModel.PROFILE_PICTURE);
        if (mCoverImgUri != null) {
            setImageWithGlide(
                    mCoverImgView,
                    Uri.parse(mCoverImgUri),
                    R.drawable.default_cover_img);
        }
        if (mProfilePicImgUri != null) {
            setImageWithGlide(
                    mCirProfileImgView,
                    Uri.parse(mProfilePicImgUri),
                    R.drawable.default_profile_icon);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(ProfileModel.IS_COVER_PICTURE, isCoverPicture);
        outState.putString(AppConstants.MY_PROFILE_OBJ, myProfileObj);
        outState.putString(ProfileModel.COVER_PICTURE, mCoverImgUri);
        outState.putString(ProfileModel.PROFILE_PICTURE, mProfilePicImgUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }


    @OnClick({R.id.add_profile_img, R.id.upload_profile_img_tv, R.id.upgrade_btn,
            R.id.toolbar_back_img_btn, R.id.coverImage, R.id.ib_add_cover_photo})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.coverImage:
            case R.id.ib_add_cover_photo:
                isCoverPicture = true;
                showAppDialog(AppDialogFragment.BOTTOM_ADD_IMG_DIALOG, null);
                break;
            case R.id.add_profile_img:
                isCoverPicture = false;
                showAppDialog(AppDialogFragment.BOTTOM_ADD_IMG_DIALOG, null);
                break;
            case R.id.upload_profile_img_tv:
                isCoverPicture = false;
                showAppDialog(AppDialogFragment.BOTTOM_ADD_IMG_DIALOG, null);
                break;
            case R.id.upgrade_btn:
                submitMotoProfileData(mProfilePicImgUri, mCoverImgUri);
                mCheckInAppPurchasesLocally = true;
                mSubmitProfileData = true;
                break;
        }
    }

    private void uploadCoverPicture(String imgUri) {
        File mFile = new File(Uri.parse(imgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart =
                MultipartBody.Part.createFormData("files", mFile.getName(), requestBody);
        RetrofitClient.getRetrofitInstance().callUploadProfileCoverImg(
                this,
                filePart,
                RetrofitClient.UPLOAD_COVER_IMAGE_FILE_RESPONSE);
    }

    private void uploadProfilePicture(String imgUri) {
        File mFile = new File(Uri.parse(imgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart =
                MultipartBody.Part.createFormData("files", mFile.getName(), requestBody);
        RetrofitClient.getRetrofitInstance().callUploadProfileImg(
                this,
                filePart,
                RetrofitClient.UPLOAD_PROFILE_IMAGE_FILE_RESPONSE);
    }

    private void submitMotoProfileData(String profileImgUrl, String coverImgUrl) {
        String mDriverOrNameStr = mDriverOrNameEt.getText().toString().trim();
        String mNameOfMotoStr = mNameOfMotoEt.getText().toString().trim();
        String mMakeStr = mMakeEt.getText().toString().trim();
        String mModelStr = mModelEt.getText().toString().trim();
        String mPhoneStr = mPhoneEt.getText().toString().trim();
        if (mDriverOrNameStr.isEmpty() && mNameOfMotoStr.isEmpty() && mMakeStr.isEmpty()
                && mModelStr.isEmpty() && mPhoneStr.isEmpty()) {
            showSnackBar(mCoordinatorLayout, mEnterAllFieldsErr);
            return;
        }
        if (mDriverOrNameStr.isEmpty()) {
            showSnackBar(mCoordinatorLayout, mDriverNameErr);
            return;
        }
        if (mNameOfMotoStr.isEmpty()) {
            showSnackBar(mCoordinatorLayout, "Please enter name of " + mProfileTypeStr);
            return;
        }
        if (mMakeStr.isEmpty()) {
            showSnackBar(mCoordinatorLayout, mMakeErr);
            return;
        }
        if (mModelStr.isEmpty()) {
            showSnackBar(mCoordinatorLayout, mModelErr);
            return;
        }
        if (mPhoneStr.isEmpty()) {
            showSnackBar(mCoordinatorLayout, mPhoneErr);
            return;
        }
        String mTempProfileImgUrl, mTempCoverImgUrl;
        if (mProfilePicImgUri != null && mCoverImgUri != null) {
            uploadCoverPicture(mCoverImgUri);
            return;
        }
        if (mCoverImgUri != null) {
            uploadCoverPicture(mCoverImgUri);
            return;
        }
        if (mProfilePicImgUri != null) {
            uploadProfilePicture(mProfilePicImgUri);
            return;
        }
        mTempProfileImgUrl = profileImgUrl;
        if (profileImgUrl == null || TextUtils.isEmpty(profileImgUrl)) {
            mTempProfileImgUrl = "";
        }
        mTempCoverImgUrl = coverImgUrl;
        if (coverImgUrl == null || TextUtils.isEmpty(coverImgUrl)) {
            mTempCoverImgUrl = "";
        }

        String mCoverPicUrl = mMyProfileResModel.getCoverPicture();
        String mProfilePicUrl = mMyProfileResModel.getProfilePicture();

        if (mTempProfileImgUrl.isEmpty()) {
            mTempProfileImgUrl = mProfilePicUrl;
        }
        if (mTempCoverImgUrl.isEmpty()) {
            mTempCoverImgUrl = mCoverPicUrl;
        }
        //TODO Before calling API integrate Inapp flow.

        int mUserId = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        JsonObject mJsonObject = new JsonObject();
        mJsonObject.addProperty(ProfileModel.ID, mMyProfileResModel.getID());
        mJsonObject.addProperty(ProfileModel.PROFILE_PICTURE, mTempProfileImgUrl);
        mJsonObject.addProperty(ProfileModel.COVER_PICTURE, mTempCoverImgUrl);
        mJsonObject.addProperty(ProfileModel.DRIVER, mDriverOrNameStr);
        mJsonObject.addProperty(ProfileModel.MOTO_NAME, mNameOfMotoStr);
        mJsonObject.addProperty(ProfileModel.MAKE, mMakeStr);
        mJsonObject.addProperty(ProfileModel.MODEL, mModelStr);
        mJsonObject.addProperty(ProfileModel.PHONE, mPhoneStr);
        mJsonObject.addProperty(ProfileModel.PURCHASED, true);
        mJsonObject.addProperty(ProfileModel.PURCHASE_DATE, String.valueOf(new Date()));
        mJsonObject.addProperty(ProfileModel.PURCHASE_TYPE, mPurchaseType);
        mJsonObject.addProperty(ProfileModel.FOLLOWERS, "");
        mJsonObject.addProperty(ProfileModel.FOLLOWING, "");
        mJsonObject.addProperty(ProfileModel.USER_ID, mUserId);
        mJsonObject.addProperty(ProfileModel.PROFILE_TYPE, Integer.parseInt(mProfileType));
        mJsonObject.addProperty(ProfileModel.HP, "");
        mJsonObject.addProperty(ProfileModel.ENGINE_SPECS, "");
        mJsonObject.addProperty(ProfileModel.PANEL_AND_PAINT, "");
        mJsonObject.addProperty(ProfileModel.WHEELS_AND_TYRES, "");
        mJsonObject.addProperty(ProfileModel.ECU, "");
        mJsonObject.addProperty(ProfileModel.GEARBOX, "");
        mJsonObject.addProperty(ProfileModel.DIFFERENTIAL, "");
        mJsonObject.addProperty(ProfileModel.CLUTCH, "");
        mJsonObject.addProperty(ProfileModel.INJECTION, "");
        mJsonObject.addProperty(ProfileModel.FUELSYSTEM, "");
        mJsonObject.addProperty(ProfileModel.TURBO, "");
        mJsonObject.addProperty(ProfileModel.SUSPENSION, "");
        mJsonObject.addProperty(ProfileModel.TYRES, "");
        mJsonObject.addProperty(ProfileModel.EXHAUST, "");
        mJsonObject.addProperty(ProfileModel.FORKS, "");
        mJsonObject.addProperty(ProfileModel.SHOCK, "");
        mJsonObject.addProperty(ProfileModel.LAPTIMER, "");
        mJsonObject.addProperty(ProfileModel.REARSETS, "");
        mJsonObject.addProperty(ProfileModel.FAIRINGS, "");
        mJsonObject.addProperty(ProfileModel.SPROCKETS, "");
        mJsonObject.addProperty(ProfileModel.ENGINECOVERS, "");
        mJsonObject.addProperty(ProfileModel.BRAKEMASTER, "");
        mJsonObject.addProperty(ProfileModel.BRAKEPAD, "");
        mJsonObject.addProperty(ProfileModel.FILTER, "");
        mJsonObject.addProperty(ProfileModel.BRAKEFLUID, "");
        mJsonObject.addProperty(ProfileModel.ENGINEOIL, "");
        mJsonObject.addProperty(ProfileModel.CHAINLUBE, "");
        mJsonObject.addProperty(ProfileModel.HANDLEBARS, "");
        mJsonObject.addProperty(ProfileModel.CHAIN, "");
        mJsonObject.addProperty(ProfileModel.BOOTS, "");
        mJsonObject.addProperty(ProfileModel.HELMET, "");
        mJsonObject.addProperty(ProfileModel.SUIT, "");
        mJsonObject.addProperty(ProfileModel.GLOVES, "");
        mJsonObject.addProperty(ProfileModel.BACKPROTECTION, "");
        mJsonObject.addProperty(ProfileModel.CHESTPROTECTION, "");
        mJsonObject.addProperty(ProfileModel.MORE_INFORMATION, "");
        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mJsonObject);
        RetrofitClient.getRetrofitInstance().callUpdateProfile(
                this,
                mJsonArray,
                RetrofitClient.UPDATE_PROFILE_RESPONSE);

    }

    @Override
    public void alertDialogPositiveBtnClick(
            BaseActivity activity,
            String dialogType,
            StringBuilder profileTypesStr,
            ArrayList<String> profileTypes,
            int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);
        switch (dialogType) {
            case AppDialogFragment.ALERT_TRIAL_DIALOG:
                saveInAppPurchasesLocally(0, mProfileType);
                handleProfileControls();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_CAPTURE_REQ:
                    Uri mCameraPicUri = getImgResultUri(data);
                    if (mCameraPicUri != null) {
                        try {
                            if (isCoverPicture) {
                                File mCoverImgFile = compressedImgFile(mCameraPicUri,
                                        POST_IMAGE_NAME_TYPE, "");
                                mCoverImgUri = Uri.fromFile(mCoverImgFile).toString();
                                setImageWithGlide(
                                        mCoverImgView,
                                        Uri.parse(mCoverImgUri),
                                        R.drawable.default_cover_img);
                            } else {
                                File mProfilePicFile = compressedImgFile(mCameraPicUri,
                                        POST_IMAGE_NAME_TYPE, "");
                                mProfilePicImgUri = Uri.fromFile(mProfilePicFile).toString();
                                setImageWithGlide(
                                        mCirProfileImgView,
                                        Uri.parse(mProfilePicImgUri),
                                        R.drawable.default_profile_icon);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mProfilePicImgUri = null;
                            mCoverImgUri = null;
                            showSnackBar(mCoordinatorLayout, e.getMessage());
                        }
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
                case GALLERY_PIC_REQ:
                    assert data.getExtras() != null;
                    Uri mSelectedImgFileUri = (Uri) data.getExtras()
                            .get(PickerImageActivity.EXTRA_RESULT_DATA);
                    if (mSelectedImgFileUri != null) {
                        try {
                            if (isCoverPicture) {
                                File mCoverImgFile = compressedImgFile(mSelectedImgFileUri,
                                        POST_IMAGE_NAME_TYPE, "");
                                mCoverImgUri = Uri.fromFile(mCoverImgFile).toString();
                                setImageWithGlide(
                                        mCoverImgView,
                                        Uri.parse(mCoverImgUri),
                                        R.drawable.default_cover_img);
                            } else {
                                File mProfilePicFile = compressedImgFile(mSelectedImgFileUri,
                                        POST_IMAGE_NAME_TYPE, "");
                                mProfilePicImgUri = Uri.fromFile(mProfilePicFile).toString();
                                setImageWithGlide(
                                        mCirProfileImgView,
                                        Uri.parse(mProfilePicImgUri),
                                        R.drawable.default_profile_icon);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mProfilePicImgUri = null;
                            mCoverImgUri = null;
                            showSnackBar(mCoordinatorLayout, e.getMessage());
                        }
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
            }

        }

    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof ProfileModel) {
            ProfileModel mProfileModel = (ProfileModel) responseObj;
            if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                switch (responseType) {
                    case RetrofitClient.UPDATE_PROFILE_RESPONSE:
                        saveInAppPurchasesLocally(0, mProfileType);
                        handleProfileControls();
                        break;
                }
            } else {
                showSnackBar(mCoordinatorLayout, mFailed);
            }
        } else if (responseObj instanceof ImageModel) {
            ImageModel mImageModel = (ImageModel) responseObj;
            switch (responseType) {
                case RetrofitClient.UPLOAD_COVER_IMAGE_FILE_RESPONSE:
                    mUploadedServerCoverImgFileUrl =
                            getHttpFilePath(mImageModel.getmModels().get(0).getPath());
                    mCoverImgUri = null;
                    if (mProfilePicImgUri != null) {
                        uploadProfilePicture(mProfilePicImgUri);
                    } else {
                        mSubmitProfileData = false;
                        submitMotoProfileData("", mUploadedServerCoverImgFileUrl);
                    }
                    break;
                case RetrofitClient.UPLOAD_PROFILE_IMAGE_FILE_RESPONSE:
                    String mUploadedServerProfileImgFileUrl = getHttpFilePath(mImageModel
                            .getmModels().get(0).getPath());
                    mProfilePicImgUri = null;
                    mSubmitProfileData = false;
                    submitMotoProfileData(
                            mUploadedServerProfileImgFileUrl,
                            mUploadedServerCoverImgFileUrl);
                    break;
            }
        } else if (responseObj instanceof SessionModel) {
            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(
                        PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(
                        PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            showSnackBar(mCoordinatorLayout, mSessionUpdated);
        }
    }

    private void handleProfileControls() {
        PreferenceUtils.getInstance(this)
                .saveBooleanData(PreferenceUtils.USER_PROFILE_COMPLETED, true);
        startActivity(new Intent(this, ViewProfileActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(
                    this,
                    RetrofitClient.UPDATE_SESSION_RESPONSE);
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
