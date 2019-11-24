package online.motohub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import online.motohub.model.promoter_club_news_media.PromoterFollowerModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerResModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.newdesign.activity.LoginActivity;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;

public class CompleteProfileActivity extends BaseActivity {

    @BindView(R.id.complete_profile_co_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.profile_scroll_view)
    ScrollView mProfileScrollView;
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
    @BindView(R.id.submit_btn)
    Button mSubmitBtn;
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
    @BindString(R.string.give_profile_pic)
    String uploadPic;

    private int mCurrentProfileIndex;
    private ArrayList<String> mProfileTypes;
    private boolean mCreateProfAfterReg = false;
    private boolean mCheckInAppPurchasesLocally = false;
    private boolean mSubmitProfileData = false;
    private boolean isCoverPicture, isFromAfterUploading;
    private String mCoverImgUri, mProfilePicImgUri, mUploadedServerCoverImgFileUrl;
    private String mMyFollowingsIDs = "";
    private int mMyProfileID = 0;
    private String mSpecName = "", mDriverOrNameStr = "",
            mNameOfMotoStr = "",
            mMakeStr = "",
            mModelStr = "", mPhoneStr = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
        ButterKnife.bind(this);
        mProfileTypes = getIntent().getStringArrayListExtra(PROFILE_TYPE);
        mCreateProfAfterReg = getIntent().getBooleanExtra(CREATE_PROF_AFTER_REG, false);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        if (mCreateProfAfterReg) {
            mSubmitBtn.setText(mDoneStr);
        }
        mCurrentProfileIndex = 0;
        setProfileReady();
        mCheckInAppPurchasesLocally = true;
    }

    private void setProfileReady() {
        String mProfileTitleStr =
                getProfileTypeStr(mProfileTypes.get(mCurrentProfileIndex)) + " Profile";
        mProfileTitleTv.setText(mProfileTitleStr);
        String mNameOfMotoStr =
                getString(R.string.name_of) + " " + getProfileTypeStr(mProfileTypes.get(mCurrentProfileIndex));
        mNameOfMotoTv.setText(mNameOfMotoStr);
        mNameOfMotoEt.setHint(mNameOfMotoStr);
        if (mProfileTypes.get(mCurrentProfileIndex).equals(SPECTATOR)) {
            showHideWidgets(View.GONE);
        }
    }


    private void showHideWidgets(int visibility) {
        if (visibility == View.GONE) {
            mDriverOrNameTv.setText(mNameStr);
            mDriverOrNameEt.setHint(mNameStr);
            mUploadProfileImgTv.setText(mUploadPhotoStr);
        } else {
            mDriverOrNameTv.setText(mDriverStr);
            mDriverOrNameEt.setHint(mDriverStr);
            mUploadProfileImgTv.setText(mUploadMotoPhotoStr);
        }
        mDriverOrNameLine.setVisibility(visibility);
        mNameOfMotoBox.setVisibility(visibility);
        mNameOfMotoLine.setVisibility(visibility);
        mMakeBox.setVisibility(visibility);
        mMakeLine.setVisibility(visibility);
        mModelBox.setVisibility(visibility);
    }

    private void clearFields() {
        mCirProfileImgView.setImageResource(R.drawable.default_profile_icon);
        mProfilePicImgUri = null;
        mDriverOrNameEt.setText("");
        mNameOfMotoEt.setText("");
        mMakeEt.setText("");
        mModelEt.setText("");
        mPhoneEt.setText("");
    }

    @OnClick({R.id.add_profile_img, R.id.upload_profile_img_tv, R.id.submit_btn,
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
            case R.id.submit_btn:
//                if (isValidateSuccess()) {
//                    getAllUserProfiles();
//                }
                if (mProfileTypes.get(mCurrentProfileIndex).equals(SPECTATOR)) {
                    submitSpectatorData(mProfilePicImgUri, mCoverImgUri);
                } else {
                    submitMotoProfileData(mProfilePicImgUri, mCoverImgUri);
                    mCheckInAppPurchasesLocally = true;
                    mSubmitProfileData = true;
                }
                break;
        }
    }

    private void uploadCoverPicture(String imgUri) {
        File mFile = new File(Uri.parse(imgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart =
                MultipartBody.Part.createFormData("files", "CoverPicture_" + mFile.getName(), requestBody);
        RetrofitClient.getRetrofitInstance().callUploadProfileCoverImg(
                this,
                filePart,
                RetrofitClient.UPLOAD_COVER_IMAGE_FILE_RESPONSE);
    }

    private void getAllPromoterProfiles() {
        RetrofitClient.getRetrofitInstance().callGetPromotersList(
                this,
                RetrofitClient.GET_ALL_PROMOTER_PROFILES);
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

    private boolean isValidateSuccess() {
        if (mProfileTypes.get(mCurrentProfileIndex).equals(SPECTATOR)) {
            mSpecName = mDriverOrNameEt.getText().toString().trim();
            mPhoneStr = mPhoneEt.getText().toString().trim();
            if (mSpecName.isEmpty()) {
                showSnackBar(mCoordinatorLayout, mEnterNameErr);
                return false;
            }
            if (mPhoneStr.isEmpty()) {
                showSnackBar(mCoordinatorLayout, mPhoneErr);
                return false;
            }
        } else {
            mDriverOrNameStr = mDriverOrNameEt.getText().toString().trim();
            mNameOfMotoStr = mNameOfMotoEt.getText().toString().trim();
            mMakeStr = mMakeEt.getText().toString().trim();
            mModelStr = mModelEt.getText().toString().trim();
            mPhoneStr = mPhoneEt.getText().toString().trim();

            if (mDriverOrNameStr.isEmpty() && mNameOfMotoStr.isEmpty() && mMakeStr.isEmpty()
                    && mModelStr.isEmpty() && mPhoneStr.isEmpty()) {
                showSnackBar(mCoordinatorLayout, mEnterAllFieldsErr);
                return false;
            }
            if (mDriverOrNameStr.isEmpty()) {
                showSnackBar(mCoordinatorLayout, mDriverNameErr);
                return false;
            }
            if (mNameOfMotoStr.isEmpty()) {
                showSnackBar(mCoordinatorLayout, "Please enter name of " +
                        getProfileTypeStr(mProfileTypes.get(mCurrentProfileIndex)));
                return false;
            }
            if (mMakeStr.isEmpty()) {
                showSnackBar(mCoordinatorLayout, mMakeErr);
                return false;
            }
            if (mModelStr.isEmpty()) {
                showSnackBar(mCoordinatorLayout, mModelErr);
                return false;
            }
            if (mPhoneStr.isEmpty()) {
                showSnackBar(mCoordinatorLayout, mPhoneErr);
                return false;
            }
            if (!isFromAfterUploading && mProfilePicImgUri == null) {
                showSnackBar(mCoordinatorLayout, uploadPic);
                return false;
            }
        }

        return true;
    }

    private void submitSpectatorData(String profileImgUrl, String coverImgUrl) {
        if (!isValidateSuccess()) {
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
        int mUserId = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        JsonObject mJsonObject = new JsonObject();
        mJsonObject.addProperty(ProfileModel.PROFILE_PICTURE, mTempProfileImgUrl);
        mJsonObject.addProperty(ProfileModel.COVER_PICTURE, mTempCoverImgUrl);
        mJsonObject.addProperty(ProfileModel.FOLLOWERS, "");
        mJsonObject.addProperty(ProfileModel.FOLLOWING, mMyFollowingsIDs);
        mJsonObject.addProperty(ProfileModel.SPECTATOR_NAME, mSpecName);
        mJsonObject.addProperty(ProfileModel.USER_ID, mUserId);
        mJsonObject.addProperty(ProfileModel.PHONE, mPhoneStr);
        mJsonObject.addProperty(ProfileModel.PROFILE_TYPE, mProfileTypes.get(mCurrentProfileIndex));
        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mJsonObject);
        JsonObject mJsonObject1 = new JsonObject();
        mJsonObject1.add("resource", mJsonArray);
        RetrofitClient.getRetrofitInstance()
                .callCreateProfile(this, mJsonObject1, RetrofitClient.CREATE_PROFILE_RESPONSE);

    }

    private void submitMotoProfileData(String profileImgUrl, String coverImgUrl) {
        if (!isValidateSuccess()) {
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
        int mUserId = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        JsonObject mJsonObject = new JsonObject();
        mJsonObject.addProperty(ProfileModel.PROFILE_PICTURE, mTempProfileImgUrl);
        mJsonObject.addProperty(ProfileModel.COVER_PICTURE, mTempCoverImgUrl);
        mJsonObject.addProperty(ProfileModel.DRIVER, mDriverOrNameStr);
        mJsonObject.addProperty(ProfileModel.MOTO_NAME, mNameOfMotoStr);
        mJsonObject.addProperty(ProfileModel.MAKE, mMakeStr);
        mJsonObject.addProperty(ProfileModel.MODEL, mModelStr);
        mJsonObject.addProperty(ProfileModel.PHONE, mPhoneStr);
        mJsonObject.addProperty(ProfileModel.PURCHASED, true);
        mJsonObject.addProperty(ProfileModel.PURCHASE_DATE, String.valueOf(new Date()));
        mJsonObject.addProperty(ProfileModel.PURCHASE_TYPE, MONTHLY);
        mJsonObject.addProperty(ProfileModel.FOLLOWERS, "");
        mJsonObject.addProperty(ProfileModel.FOLLOWING, mMyFollowingsIDs);
        mJsonObject.addProperty(ProfileModel.USER_ID, mUserId);
        mJsonObject.addProperty(ProfileModel.PROFILE_TYPE, mProfileTypes.get(mCurrentProfileIndex));
        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mJsonObject);
        JsonObject mJsonObject1 = new JsonObject();
        mJsonObject1.add("resource", mJsonArray);
        RetrofitClient.getRetrofitInstance().callCreateProfile(
                this,
                mJsonObject1,
                RetrofitClient.CREATE_PROFILE_RESPONSE);
    }

    public void callUpdateFollowPromoters(ArrayList<PromotersResModel> mPromoterList, int mProfileID) {

        JsonArray mJsonArray = new JsonArray();
        for (PromotersResModel mPromoterObj : mPromoterList) {
            String mFollowRelation = mProfileID + "_" + mPromoterObj.getUserId();
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(PromoterFollowerResModel.PROFILE_ID, mProfileID);
            mJsonObject.addProperty(PromoterFollowerResModel.PROMOTER_USER_ID, mPromoterObj.getUserId());
            mJsonObject.addProperty(PromoterFollowerResModel.FOLLOW_RELATION, mFollowRelation);
            mJsonArray.add(mJsonObject);
        }

        RetrofitClient.getRetrofitInstance().callFollowPromoter(this,
                mJsonArray, RetrofitClient.GET_PROMOTER_FOLLOW_RESPONSE);

    }

    @Override
    public void alertDialogPositiveBtnClick(
            BaseActivity activity,
            String dialogType,
            StringBuilder profileTypesStr,
            ArrayList<String> profileTypes,
            int position) {
        //  super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);
        switch (dialogType) {
            case AppDialogFragment.ALERT_TRIAL_DIALOG:
                saveInAppPurchasesLocally(0, mProfileTypes.get(mCurrentProfileIndex));
                handleProfileControls();
                if (mCreateProfAfterReg) {
                    Intent mIntent = new Intent();
                    setResult(RESULT_OK, mIntent);
                }
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

   /* private void checkInAppPurchasesLocally(Inventory inventory) {
        if (!PreferenceUtils.getInstance(this)
                .getStrData(PreferenceUtils.BIKE_IN_APP).equals(PROFILE_PURCHASED)
                && !PreferenceUtils.getInstance(this)
                .getStrData(PreferenceUtils.BIKE_IN_APP).equals("")) {
        } else if (!PreferenceUtils.getInstance(this)
                .getStrData(PreferenceUtils.BOAT_IN_APP).equals(PROFILE_PURCHASED)
                && !PreferenceUtils.getInstance(this)
                .getStrData(PreferenceUtils.BOAT_IN_APP).equals("")) {
        } else if (!PreferenceUtils.getInstance(this)
                .getStrData(PreferenceUtils.CAR_IN_APP).equals(PROFILE_PURCHASED)
                && !PreferenceUtils.getInstance(this)
                .getStrData(PreferenceUtils.CAR_IN_APP).equals("")) {
        } else if (!PreferenceUtils.getInstance(this)
                .getStrData(PreferenceUtils.KART_IN_APP).equals(PROFILE_PURCHASED)
                && !PreferenceUtils.getInstance(this)
                .getStrData(PreferenceUtils.KART_IN_APP).equals("")) {
        } else if (mSubmitProfileData) {
            submitMotoProfileData(mProfilePicImgUri, mCoverImgUri);
        }
    }*/


    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof ProfileModel) {
            ProfileModel mProfileModel = (ProfileModel) responseObj;
            if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                switch (responseType) {
                    case RetrofitClient.CREATE_PROFILE_RESPONSE:
                        PreferenceUtils.getInstance(this)
                                .saveBooleanData(mProfileTypes.get(mCurrentProfileIndex), true);
                       /* saveInAppPurchasesLocally(
                                mProfileModel.getResource().get(0).getID(),
                                mProfileTypes.get(mCurrentProfileIndex));*/
                        mMyProfileID = mProfileModel.getResource().get(0).getID();
                        getAllPromoterProfiles();
                        setResultIntent();
                        break;
                    case RetrofitClient.UPDATE_PROFILE_RESPONSE:
                        //  saveInAppPurchasesLocally(0, mProfileTypes.get(mCurrentProfileIndex));
                        handleProfileControls();
                        setResultIntent();
                        break;
                    case RetrofitClient.GET_ALL_USER_PROFILES:
                        mMyFollowingsIDs = getAllUserProfileIDs(mProfileModel.getResource());
                        if (mProfileTypes.get(mCurrentProfileIndex).equals(SPECTATOR)) {
                            submitSpectatorData("", mUploadedServerCoverImgFileUrl);
                        } else {
                            mSubmitProfileData = false;
                            submitMotoProfileData("", mUploadedServerCoverImgFileUrl);
                        }

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
                        if (mProfileTypes.get(mCurrentProfileIndex).equals(SPECTATOR)) {
                            submitSpectatorData("", mUploadedServerCoverImgFileUrl);
                        } else {
                            mSubmitProfileData = false;
                            submitMotoProfileData("", mUploadedServerCoverImgFileUrl);
                        }
                    }
                    break;
                case RetrofitClient.UPLOAD_PROFILE_IMAGE_FILE_RESPONSE:
                    String mUploadedServerProfileImgFileUrl = getHttpFilePath(mImageModel
                            .getmModels().get(0).getPath());
                    mProfilePicImgUri = null;
                    isFromAfterUploading = true;
                    if (mProfileTypes.get(mCurrentProfileIndex).equals(SPECTATOR)) {
                        submitSpectatorData(
                                mUploadedServerProfileImgFileUrl,
                                mUploadedServerCoverImgFileUrl);
                    } else {
                        mSubmitProfileData = false;
                        submitMotoProfileData(
                                mUploadedServerProfileImgFileUrl,
                                mUploadedServerCoverImgFileUrl);
                    }
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
        } else if (responseObj instanceof PromoterFollowerModel) {
            switch (responseType) {
                case RetrofitClient.GET_PROMOTER_FOLLOW_RESPONSE:
                    if (mProfileTypes.get(mCurrentProfileIndex).equals(SPECTATOR)) {
                        showToast(getApplicationContext(), mProfileCompleted);
                        handleProfileControls();
                    } else {
                        saveInAppPurchasesLocally(0, mProfileTypes.get(mCurrentProfileIndex));
                        PreferenceUtils.getInstance(this)
                                .saveBooleanData(PreferenceUtils.USER_PROFILE_COMPLETED, true);

                        setResultIntent();

                    }
                    break;
            }
        } else if (responseObj instanceof PromotersModel) {
            PromotersModel mPromotersModel = (PromotersModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_ALL_PROMOTER_PROFILES:
                    if (mPromotersModel.getResource() != null && mPromotersModel.getResource().size() > 0) {
                        callUpdateFollowPromoters(mPromotersModel.getResource(), mMyProfileID);
                    } else {
                        if (mProfileTypes.get(mCurrentProfileIndex).equals(SPECTATOR)) {
                            showToast(getApplicationContext(), mProfileCompleted);
                            handleProfileControls();
                        } else {
                            saveInAppPurchasesLocally(0, mProfileTypes.get(mCurrentProfileIndex));
                            PreferenceUtils.getInstance(this)
                                    .saveBooleanData(PreferenceUtils.USER_PROFILE_COMPLETED, true);
                            setResultIntent();
                        }
                    }

                    break;
            }

        }
    }

    private void setResultIntent() {
        String mTAG = getIntent().getStringExtra(AppConstants.TAG);
        if (mTAG.equals(LoginActivity.TAG)) {
            PreferenceUtils.getInstance(this).saveBooleanData(PreferenceUtils.USER_PROFILE_COMPLETED, true);
            startActivity(new Intent(this, ViewProfileActivity.class));
        } else {
            Intent mIntent = new Intent();
            setResult(RESULT_OK, mIntent);
        }
        finish();
    }

    private String getAllUserProfileIDs(List<ProfileResModel> mProfileList) {
        String divider = ",";
        StringBuilder mStringBuilder = new StringBuilder();
        for (int i = 0; i < mProfileList.size(); i++) {
            if (i == mProfileList.size() - 1)
                divider = "";
            ProfileResModel mProfile = mProfileList.get(i);
            mStringBuilder.append(mProfile.getID()).append(divider);
        }
        return mStringBuilder.toString();
    }

    private void handleProfileControls() {
        PreferenceUtils.getInstance(this)
                .saveBooleanData(PreferenceUtils.USER_PROFILE_COMPLETED, true);
        if (mCreateProfAfterReg) {
            Intent mIntent = new Intent();
            setResult(RESULT_OK, mIntent);
            finish();
        } else {
            startActivity(new Intent(this, ViewProfileActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
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
