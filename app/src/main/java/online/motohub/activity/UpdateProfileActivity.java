package online.motohub.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import online.motohub.BuildConfig;
import online.motohub.R;
import online.motohub.adapter.VehicleInfoLikeAdapter;
import online.motohub.dialog.DialogManager;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.ImageModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.model.VehicleInfoLikeModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.Utility;

public class UpdateProfileActivity extends BaseActivity {


    @BindView(R.id.complete_profile_co_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.profile_title_tv)
    TextView mProfileTitleTv;
    @BindView(R.id.add_profile_img)
    ImageView mCirProfileImgView;
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
    @BindView(R.id.model_line)
    View mModelLine;
    @BindView(R.id.hp_box)
    LinearLayout mHPBox;
    @BindView(R.id.hp_tv)
    TextView mHPTv;
    @BindView(R.id.hp_et)
    EditText mHPEt;
    @BindView(R.id.hp_line)
    View mHPLine;
    @BindView(R.id.engine_specs_box)
    LinearLayout mEngineSpecsBox;
    @BindView(R.id.engine_specs_et)
    EditText mEngineSpecsEt;
    @BindView(R.id.engine_specs_line)
    View mEngineSpecsLine;
    @BindView(R.id.panel_and_paint_box)
    LinearLayout mPanelAndPaintBox;
    @BindView(R.id.panel_and_paint_et)
    EditText mPanelAndPaintEt;
    @BindView(R.id.panel_and_paint_line)
    View mPanelAndPaintLine;
    @BindView(R.id.wheels_and_tyres_box)
    LinearLayout mWheelsAndTyresBox;
    @BindView(R.id.wheels_and_tyres_et)
    EditText mWheelsAndTiresEt;
    @BindView(R.id.wheels_and_tyres_line)
    View mWheelsAndTyresLine;
    @BindView(R.id.more_info_box)
    LinearLayout mMoreInfoBox;
    @BindView(R.id.more_info_et)
    EditText mMoreInfoEt;
    @BindView(R.id.submit_btn)
    Button mSubmitBtn;
    @BindView(R.id.ecu_box)
    LinearLayout mEcuBox;
    @BindView(R.id.gear_box)
    LinearLayout mGearBox;
    @BindView(R.id.diff_box)
    LinearLayout mDiffBox;
    @BindView(R.id.clutch_box)
    LinearLayout mClutchBox;
    @BindView(R.id.injection_box)
    LinearLayout mInjectionBox;
    @BindView(R.id.fuel_box)
    LinearLayout mFuelBox;
    @BindView(R.id.turbo_box)
    LinearLayout mTurboBox;
    @BindView(R.id.suspension_box)
    LinearLayout mSuspensionBox;
    @BindView(R.id.upgrade_box)
    RelativeLayout mUpgradeBox;
    @BindView(R.id.ecu_line)
    View mEcuLine;
    @BindView(R.id.gear_line)
    View mGearLine;
    @BindView(R.id.diff_line)
    View mDiffLine;
    @BindView(R.id.clutch_line)
    View mClutchLine;
    @BindView(R.id.injection_line)
    View mInjectionLine;
    @BindView(R.id.fuel_line)
    View mFuelLine;
    @BindView(R.id.turbo_line)
    View mTurboLine;
    @BindView(R.id.suspension_line)
    View mSuspensionLine;
    @BindView(R.id.ecu_et)
    EditText mEcuEt;
    @BindView(R.id.gear_et)
    EditText mGearEt;
    @BindView(R.id.diff_et)
    EditText mDifferentialEt;
    @BindView(R.id.clutch_et)
    EditText mClutchEt;
    @BindView(R.id.injection_et)
    EditText mInjectionEt;
    @BindView(R.id.fuel_et)
    EditText mFuelEt;
    @BindView(R.id.turbo_et)
    EditText mTurboEt;
    @BindView(R.id.suspension_et)
    EditText mSuspensionEt;
    @BindView(R.id.tyres_et)
    EditText mTyresEt;
    @BindView(R.id.exhaust_et)
    EditText mExhaustEt;
    @BindView(R.id.fork_et)
    EditText mForkEt;
    @BindView(R.id.shock_et)
    EditText mShockEt;
    @BindView(R.id.lap_timer_et)
    EditText mLapTimerEt;
    @BindView(R.id.rear_set_et)
    EditText mRearSetEt;
    @BindView(R.id.handle_bar_et)
    EditText mHandleBarEt;
    @BindView(R.id.fairings_et)
    EditText mFairingsEt;
    @BindView(R.id.chain_et)
    EditText mChainEt;
    @BindView(R.id.sprockets_et)
    EditText mSprocketsEt;
    @BindView(R.id.engine_cover_et)
    EditText mEngineCoverEt;
    @BindView(R.id.brake_master_et)
    EditText mBrakeMasterEt;
    @BindView(R.id.brake_pad_et)
    EditText mBrakePadEt;
    @BindView(R.id.filter_et)
    EditText mFilterEt;
    @BindView(R.id.brake_fluid_et)
    EditText mBrakeFluidEt;
    @BindView(R.id.engine_oil_et)
    EditText mEngineOilEt;
    @BindView(R.id.chain_lube_et)
    EditText mChainLubeEt;
    @BindView(R.id.boots_et)
    EditText mBootsEt;
    @BindView(R.id.helmet_et)
    EditText mHelmetEt;
    @BindView(R.id.suit_et)
    EditText mSuitEt;
    @BindView(R.id.gloves_et)
    EditText mGloveEt;
    @BindView(R.id.back_protection_et)
    EditText mBackProtectionEt;
    @BindView(R.id.chest_protection_et)
    EditText mChestProtectionEt;
    @BindView(R.id.trailer_et)
    EditText mTrailerEt;
    @BindView(R.id.trailer_line)
    View mTrailerLine;
    @BindView(R.id.trailer_box)
    LinearLayout mTrailerBox;
    @BindView(R.id.tyres_line)
    View mTyresline;
    @BindView(R.id.exhaust_line)
    View mExhaustline;
    @BindView(R.id.fork_line)
    View mForkline;
    @BindView(R.id.shock_line)
    View mShockline;
    @BindView(R.id.lap_timer_line)
    View mLapTimerline;
    @BindView(R.id.rear_set_line)
    View mRearSetline;
    @BindView(R.id.handle_bar_line)
    View mHandleBarline;
    @BindView(R.id.fairings_line)
    View mFairingsline;
    @BindView(R.id.chain_line)
    View mChainline;
    @BindView(R.id.sprockets_line)
    View mSprocketsline;
    @BindView(R.id.engine_cover_line)
    View mEngineCoverline;
    @BindView(R.id.brake_master_line)
    View mBrakeMasterline;
    @BindView(R.id.brake_pad_line)
    View mBrakePadline;
    @BindView(R.id.filter_line)
    View mFilterline;
    @BindView(R.id.brake_fluid_line)
    View mBrakeFluidline;
    @BindView(R.id.engine_oil_line)
    View mEngineOilline;
    @BindView(R.id.chain_lube_line)
    View mChainLubeline;
    @BindView(R.id.boots_line)
    View mBootsline;
    @BindView(R.id.helmet_line)
    View mHelmetline;
    @BindView(R.id.suit_line)
    View mSuitline;
    @BindView(R.id.gloves_line)
    View mGloveline;
    @BindView(R.id.back_protection_line)
    View mBackProtectionline;
    @BindView(R.id.chest_protection_line)
    View mChestProtectionline;
    @BindView(R.id.tyres_box)
    LinearLayout mTyresbox;
    @BindView(R.id.exhaust_box)
    LinearLayout mExhaustbox;
    @BindView(R.id.fork_box)
    LinearLayout mForkBox;
    @BindView(R.id.shock_box)
    LinearLayout mShockbox;
    @BindView(R.id.lap_timer_box)
    LinearLayout mLapTimerbox;
    @BindView(R.id.rear_set_box)
    LinearLayout mRearSetbox;
    @BindView(R.id.handle_bar_box)
    LinearLayout mHandleBarbox;
    @BindView(R.id.fairings_box)
    LinearLayout mFairingsbox;
    @BindView(R.id.chain_box)
    LinearLayout mChainbox;
    @BindView(R.id.sprockets_box)
    LinearLayout mSprocketsbox;
    @BindView(R.id.engine_cover_box)
    LinearLayout mEngineCoverbox;
    @BindView(R.id.brake_master_box)
    LinearLayout mBrakeMasterbox;
    @BindView(R.id.brake_pad_box)
    LinearLayout mBrakePadbox;
    @BindView(R.id.filter_box)
    LinearLayout mFilterbox;
    @BindView(R.id.brake_fluid_box)
    LinearLayout mBrakeFluidbox;
    @BindView(R.id.engine_oil_box)
    LinearLayout mEngineOilbox;
    @BindView(R.id.chain_lube_box)
    LinearLayout mChainLubebox;
    @BindView(R.id.boots_box)
    LinearLayout mBootsbox;
    @BindView(R.id.helmet_box)
    LinearLayout mHelmetbox;
    @BindView(R.id.suit_box)
    LinearLayout mSuitbox;
    @BindView(R.id.gloves_box)
    LinearLayout mGlovebox;
    @BindView(R.id.back_protection_box)
    LinearLayout mBackProtectionbox;
    @BindView(R.id.chest_protection_box)
    LinearLayout mChestProtectionbox;
    @BindView(R.id.coverImage)
    ImageView mCoverImgView;
    @BindView(R.id.txt_version)
    TextView txtVersion;
    @BindString(R.string.update_your_profile)
    String mToolbarTitle;
    @BindString(R.string.name)
    String mNameStr;
    @BindString(R.string.upload_photo)
    String mUploadPhotoStr;
    @BindString(R.string.driver)
    String mDriverStr;
    @BindString(R.string.upload_moto_photo)
    String mUploadMotoPhotoStr;
    @BindString(R.string.camera_permission_denied)
    String mNoCameraPer;
    @BindString(R.string.storage_permission_denied)
    String mNoStoragePer;
    @BindString(R.string.driver_name_err)
    String mDriverNameErr;
    @BindString(R.string.make_name_err)
    String mMakeErr;
    @BindString(R.string.model_err)
    String mModelErr;
    @BindString(R.string.enter_phone_number)
    String mPhoneErr;
    @BindString(R.string.engine_specs_err)
    String mEngineSpecsErr;
    @BindString(R.string.panel_and_paint_err)
    String mPanelAndPaintErr;
    @BindString(R.string.wheels_and_tyres_err)
    String mWheelsAndTyresErr;
    @BindString(R.string.more_info_err)
    String mMoreInfoErr;
    @BindString(R.string.cc)
    String mCCStr;
    @BindString(R.string.hp_err)
    String mHPErr;
    @BindString(R.string.enter_all_fields)
    String mEnterAllFieldsErr;
    @BindString(R.string.enter_name)
    String mEnterNameErr;
    @BindString(R.string.give_moto_photo)
    String mGiveMotoPhotoErr;
    @BindString(R.string.update)
    String mUpdateStr;
    @BindString(R.string.update_profile_success)
    String mUpdateProfileSuccessStr;

    @BindString(R.string.vehicle_information)
    String mToolbarVehicleInfoTitle;

    @BindView(R.id.likeBtn)
    ImageView mLikeBtn;
    @BindView(R.id.like_count_txt)
    TextView mLikeCountTxt;

    String mCoverImgUri, mProfilePicImgUri, mUploadedServerCoverImgFileUrl;
    private String mECU, mGear, mClutch, mInjection, mFuelSystem, mDifferential, mTurbo,
            mSuspension;
    private String mBoots, mHelmet, mSuit, mGloves, mBackProtection, mChestProtection;
    private String mTyres, mExhaust, mForks, mShock, mRearSets, mLapTimer, mHandleBars, mChain,
            mEngineCover, mFairings, mSprockets, mBrakeMaster, mBrakePad, mFilter, mBrakeFluid,
            mEngineOil, mChainLube, mTrailer;
    private ProfileResModel mMyProfileResModel;
    private ProfileResModel mUpdatedProfileResModel = new ProfileResModel();
    private boolean isCoverPicture;

    private Dialog mCommentListPopup;
    private RecyclerView mFeedLikesListView;
    private int mDeleteLikeID;
    private String mLikesStr = "";
    private String mLikes;
    private int mProfileID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        ButterKnife.bind(this);
        initView();
    }


    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    /*@Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        *//*mMyProfileResModel = (ProfileResModel) savedInstanceState
                .getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);*//*
        mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        isCoverPicture = savedInstanceState.getBoolean(ProfileModel.IS_COVER_PICTURE);
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
    }*/

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
        outState.putBoolean(ProfileModel.IS_COVER_PICTURE, isCoverPicture);
        outState.putString(ProfileModel.COVER_PICTURE, mCoverImgUri);
        outState.putString(ProfileModel.PROFILE_PICTURE, mProfilePicImgUri);
        super.onSaveInstanceState(outState);
    }*/

    private void initView() {
        setupUI(mCoordinatorLayout);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        if (getIntent().hasExtra(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY)) {
            try {
                setToolbar(mToolbar, mToolbarVehicleInfoTitle);
                JSONObject mJsonObject = new JSONObject(getIntent().getExtras().getString(MyFireBaseMessagingService.ENTRY_JSON_OBJ));
                mProfileID = Integer.parseInt(mJsonObject.getJSONObject("Details").get(VehicleInfoLikeModel.LIKED_PROFILE_ID).toString());
                getProfile(mProfileID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        } else {
            //if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(AppConstants.IS_FROM_VEHICLE_INFO))
                setToolbar(mToolbar, mToolbarVehicleInfoTitle);
            else
                setToolbar(mToolbar, mToolbarTitle);
            /*mMyProfileResModel = (ProfileResModel) getIntent().getExtras()
                    .getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);*/
            //mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
            mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
            mUpdatedProfileResModel = mMyProfileResModel;
        }
        setProfileReady();
    }

    private void getProfile(int mProfileID) {
        mFilter = "ID = " + mProfileID;
        RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);
    }

    private void setLikes() {
        ArrayList<VehicleInfoLikeModel> mFeedLikes = mMyProfileResModel.getVehicleInfoLikesByID();
        if (mFeedLikes.size() > 0) {
            for (final VehicleInfoLikeModel likesEntity : mFeedLikes) {

                if ((likesEntity.getWhoLikedProfileID() == mMyProfileResModel.getID())) {
                    mLikeBtn.setImageResource(R.drawable.liked_icon);
                    mLikeBtn.setTag("unlike");
                    break;
                } else {
                    mLikeBtn.setImageResource(R.drawable.like_icon);
                    mLikeBtn.setTag("like");
                }
            }
        } else {
            mLikeBtn.setImageResource(R.drawable.like_icon);
            mLikeBtn.setTag("like");
        }
        int mLikeCount = mFeedLikes.size();
        if (mLikeCount == 1)
            mLikes = mLikeCount + " like";
        else if (mLikeCount > 1)
            mLikes = mLikeCount + " likes";
        mLikeCountTxt.setText(mLikes);

        showToast(this, getString(R.string.field_alert_msg));
    }

    private void setProfileReady() {
        try {
            String mProfileTitleStr = getProfileTypeStr(String.valueOf(mMyProfileResModel.getProfileType())) + " Profile";
            mProfileTitleTv.setText(mProfileTitleStr);
            String mNameOfMotoStr = getString(R.string.name_of) + " " + getProfileTypeStr(String.valueOf(mMyProfileResModel.getProfileType()));
            mNameOfMotoTv.setText(mNameOfMotoStr);
            if (mMyProfileResModel.getProfilePicture() != null && !TextUtils.isEmpty(mMyProfileResModel.getProfilePicture())) {
                setImageWithGlide(mCirProfileImgView, mMyProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);
            }
            if (mMyProfileResModel.getCoverPicture() != null && !TextUtils.isEmpty(mMyProfileResModel.getCoverPicture())) {
                setImageWithGlide(mCoverImgView, mMyProfileResModel.getCoverPicture(), R.drawable.app_logo);
            }
            if (String.valueOf(mMyProfileResModel.getProfileType()).equals(BOAT)) {
                showHideBikeProperties(View.GONE);
                showHideCarProperties(View.GONE);
                showHideCarAndBikeProperties(View.GONE);
                showHideWidgets(View.VISIBLE);
                mWheelsAndTyresBox.setVisibility(View.GONE);
                mWheelsAndTyresLine.setVisibility(View.GONE);
                setMotoProfileVal();
            } else if (String.valueOf(mMyProfileResModel.getProfileType()).equals(SPECTATOR)) {
                mUpgradeBox.setVisibility(View.VISIBLE);
                showHideWidgets(View.GONE);
                showHideBikeProperties(View.GONE);
                showHideCarProperties(View.GONE);
                showHideCarAndBikeProperties(View.GONE);
                mDriverOrNameEt.setText(mMyProfileResModel.getSpectatorName());
                mDriverOrNameLine.setVisibility(View.VISIBLE);
                mPhoneEt.setText(mMyProfileResModel.getPhone());
            } else if (String.valueOf(mMyProfileResModel.getProfileType()).equals(CAR)) {
                showHideCarProperties(View.VISIBLE);
                showHideWidgets(View.VISIBLE);
                showHideCarAndBikeProperties(View.VISIBLE);
                showHideBikeProperties(View.GONE);
                setMotoProfileVal();
            } else if (String.valueOf(mMyProfileResModel.getProfileType()).equals(BIKE)) {
                showHideCarProperties(View.GONE);
                showHideWidgets(View.VISIBLE);
                showHideBikeProperties(View.VISIBLE);
                showHideCarAndBikeProperties(View.VISIBLE);
                setMotoProfileVal();
            } else {
                showHideBikeProperties(View.GONE);
                showHideCarProperties(View.GONE);
                showHideCarAndBikeProperties(View.GONE);
                showHideWidgets(View.VISIBLE);
                setMotoProfileVal();
            }
            mSubmitBtn.setText(mUpdateStr);
            setLikes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setMotoProfileVal() {

        mDriverOrNameEt.setText(mMyProfileResModel.getDriver());
        mNameOfMotoEt.setText(mMyProfileResModel.getMotoName());
        mMakeEt.setText(mMyProfileResModel.getMake());
        mModelEt.setText(mMyProfileResModel.getModel());
        mPhoneEt.setText(mMyProfileResModel.getPhone());
        mHPEt.setText(String.valueOf(mMyProfileResModel.getHP()));
        mEngineSpecsEt.setText(mMyProfileResModel.getEngineSpecs());

        mPanelAndPaintEt.setText(mMyProfileResModel.getPanelPaint());
        //show version
        txtVersion.setText("version  : " + BuildConfig.VERSION_NAME);

        if (!String.valueOf(mMyProfileResModel.getProfileType()).equals(BOAT)) {
            mWheelsAndTiresEt.setText(mMyProfileResModel.getWheelsTyres());
        }
        if (String.valueOf(mMyProfileResModel.getProfileType()).equals(CAR)) {
            mEcuEt.setText(mMyProfileResModel.getECU());
            mGearEt.setText(mMyProfileResModel.getGearBox());
            mDifferentialEt.setText(mMyProfileResModel.getDifferencial());
            mClutchEt.setText(mMyProfileResModel.getClutch());
            mInjectionEt.setText(mMyProfileResModel.getInjection());
            mFuelEt.setText(mMyProfileResModel.getFuelSystem());
            mTurboEt.setText(mMyProfileResModel.getTurbo());
            mSuspensionEt.setText(mMyProfileResModel.getSuspension());
        }
        if (String.valueOf(mMyProfileResModel.getProfileType()).equals(BIKE)) {
            //mHPTv.setText(mCCStr);
            //mHPEt.setHint(mCCStr);
            mTyresEt.setText(mMyProfileResModel.getTyres());
            mExhaustEt.setText(mMyProfileResModel.getExhaust());
            mForkEt.setText(mMyProfileResModel.getForks());
            mShockEt.setText(mMyProfileResModel.getShock());
            mLapTimerEt.setText(mMyProfileResModel.getLapTimer());
            mRearSetEt.setText(mMyProfileResModel.getRearSets());
            mChainEt.setText(mMyProfileResModel.getChain());
            mEcuEt.setText(mMyProfileResModel.getECU());
            mFairingsEt.setText(mMyProfileResModel.getFairings());
            mSprocketsEt.setText(mMyProfileResModel.getSprockets());
            mEngineCoverEt.setText(mMyProfileResModel.getEngineCovers());
            mBrakeMasterEt.setText(mMyProfileResModel.getBrakeMaster());
            mBrakePadEt.setText(mMyProfileResModel.getBrakePad());
            mBrakeFluidEt.setText(mMyProfileResModel.getBrakeFluid());
            mFilterEt.setText(mMyProfileResModel.getFilter());
            mHandleBarEt.setText(mMyProfileResModel.getHandleBars());
            mChainLubeEt.setText(mMyProfileResModel.getChainLube());
            mEngineOilEt.setText(mMyProfileResModel.getEngineOil());
        }
        if (String.valueOf(mMyProfileResModel.getProfileType()).equals(BIKE) || (String.valueOf(mMyProfileResModel.getProfileType()).equals(CAR))) {
            mBootsEt.setText(mMyProfileResModel.getBoots());
            mHelmetEt.setText(mMyProfileResModel.getHelmet());
            mSuitEt.setText(mMyProfileResModel.getSuit());
            mGloveEt.setText(mMyProfileResModel.getGloves());
            mBackProtectionEt.setText(mMyProfileResModel.getBackProtection());
            mChestProtectionEt.setText(mMyProfileResModel.getChestProtection());
            mTrailerEt.setText(mMyProfileResModel.getTrailer());
        }

        mMoreInfoEt.setText(mMyProfileResModel.getMoreInformation());

    }

    private void showHideCarProperties(int visibility) {

        mGearBox.setVisibility(visibility);
        mDiffBox.setVisibility(visibility);
        mClutchBox.setVisibility(visibility);
        mInjectionBox.setVisibility(visibility);
        mFuelBox.setVisibility(visibility);
        mTurboBox.setVisibility(visibility);
        mSuspensionBox.setVisibility(visibility);

        mGearLine.setVisibility(visibility);
        mDiffLine.setVisibility(visibility);
        mClutchLine.setVisibility(visibility);
        mInjectionLine.setVisibility(visibility);
        mFuelLine.setVisibility(visibility);
        mTurboLine.setVisibility(visibility);
        mSuspensionLine.setVisibility(visibility);
    }

    private void showHideCarAndBikeProperties(int visibility) {
        mEcuBox.setVisibility(visibility);
        mBootsbox.setVisibility(visibility);
        mHelmetbox.setVisibility(visibility);
        mSuitbox.setVisibility(visibility);
        mGlovebox.setVisibility(visibility);
        mBackProtectionbox.setVisibility(visibility);
        if (String.valueOf(mMyProfileResModel.getProfileType()).equals(CAR)) {
            mChestProtectionbox.setVisibility(View.GONE);
        }
        mChestProtectionbox.setVisibility(visibility);
        mTrailerBox.setVisibility(visibility);

        mEcuLine.setVisibility(visibility);
        mBootsline.setVisibility(visibility);
        mHelmetline.setVisibility(visibility);
        mSuitline.setVisibility(visibility);
        mGloveline.setVisibility(visibility);
        mBackProtectionline.setVisibility(visibility);
        mChestProtectionline.setVisibility(visibility);
        mTrailerLine.setVisibility(visibility);

        mWheelsAndTyresBox.setVisibility(visibility);
        mWheelsAndTyresLine.setVisibility(visibility);
    }

    private void showHideBikeProperties(int visibility) {

        mTyresbox.setVisibility(visibility);
        mExhaustbox.setVisibility(visibility);
        mForkBox.setVisibility(visibility);
        mShockbox.setVisibility(visibility);
        mLapTimerbox.setVisibility(visibility);
        mRearSetbox.setVisibility(visibility);
        mHandleBarbox.setVisibility(visibility);
        mFairingsbox.setVisibility(visibility);
        mChainbox.setVisibility(visibility);
        mBrakePadbox.setVisibility(visibility);
        mBrakeMasterbox.setVisibility(visibility);
        mFilterbox.setVisibility(visibility);
        mEngineOilbox.setVisibility(visibility);
        mChainLubebox.setVisibility(visibility);
        mSprocketsbox.setVisibility(visibility);
        mEngineCoverbox.setVisibility(visibility);
        mBrakeFluidbox.setVisibility(visibility);

        mTyresline.setVisibility(visibility);
        mExhaustline.setVisibility(visibility);
        mForkline.setVisibility(visibility);
        mShockline.setVisibility(visibility);
        mLapTimerline.setVisibility(visibility);
        mRearSetline.setVisibility(visibility);
        mHandleBarline.setVisibility(visibility);
        mFairingsline.setVisibility(visibility);
        mChainline.setVisibility(visibility);
        mBrakePadline.setVisibility(visibility);
        mBrakeMasterline.setVisibility(visibility);
        mFilterline.setVisibility(visibility);
        mEngineOilline.setVisibility(visibility);
        mChainLubeline.setVisibility(visibility);
        mSprocketsline.setVisibility(visibility);
        mEngineCoverline.setVisibility(visibility);
        mBrakeFluidline.setVisibility(visibility);

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
        mModelLine.setVisibility(visibility);
        mHPBox.setVisibility(visibility);
        mHPLine.setVisibility(visibility);
        mEngineSpecsBox.setVisibility(visibility);
        mEngineSpecsLine.setVisibility(visibility);
        mPanelAndPaintBox.setVisibility(visibility);
        mPanelAndPaintLine.setVisibility(visibility);
        mWheelsAndTyresBox.setVisibility(visibility);
        mWheelsAndTyresLine.setVisibility(visibility);
        mMoreInfoBox.setVisibility(visibility);

    }

    private void showLikeListPopup(String title) {

        if (mCommentListPopup != null && mCommentListPopup.isShowing()) {
            mCommentListPopup.dismiss();
        }

        // Create custom dialog object
        mCommentListPopup = new Dialog(this, R.style.MyDialogBottomSheet);
        mCommentListPopup.setContentView(R.layout.popup_feed_like_list);

        ImageView mCloseIcon = mCommentListPopup.findViewById(R.id.close_btn);
        TextView mTitleTxt = mCommentListPopup.findViewById(R.id.title_txt);
        mFeedLikesListView = mCommentListPopup.findViewById(R.id.feeds_likes_list_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mFeedLikesListView.setLayoutManager(mLayoutManager);
        mFeedLikesListView.setItemAnimator(new DefaultItemAnimator());

        mTitleTxt.setText(title);


        mCloseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentListPopup.dismiss();
            }
        });

        mCommentListPopup.show();

    }

    private void setFeedLikeAdapter(ArrayList<VehicleInfoLikeModel> mFeedLikeList) {
        VehicleInfoLikeAdapter mFeedLikesAdapter = new VehicleInfoLikeAdapter(this, mFeedLikeList, mMyProfileResModel);
        mFeedLikesListView.setAdapter(mFeedLikesAdapter);
        mFeedLikesListView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void callUnLikeVehicleInfo(int mDeleteLikeID) {
        RetrofitClient.getRetrofitInstance().callUnLikeForVehicleInfo(this, mDeleteLikeID, RetrofitClient.VEHICLE_INFO_UN_LIKE);
    }

    private void callLikeVehicleInfo(int mLikedProfileID, int mWhoLikedProfileID) {

        JsonObject mItem = new JsonObject();

        mItem.addProperty(VehicleInfoLikeModel.LIKED_PROFILE_ID, mLikedProfileID);
        mItem.addProperty(VehicleInfoLikeModel.WHO_LIKED_PROFILE_ID, mWhoLikedProfileID);

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mItem);

        RetrofitClient.getRetrofitInstance().postLikeForVehicleInfo(this, mJsonArray, RetrofitClient.VEHICLE_INFO_LIKE);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.add_profile_img, R.id.upload_profile_img_tv, R.id.submit_btn,
            R.id.tag_sponsor_eng_specs_tv, R.id.tag_sponsor_panel_and_paint_tv, R.id.tag_sponsor_wheels_and_tyres_tv,
            R.id.tag_sponsor_more_info_tv, R.id.upgrade_btn, R.id.coverImage, R.id.likeBtn, R.id.like_count_txt, R.id.ib_add_cover_photo})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.likeBtn:
                if (!isMultiClicked()) {
                    switch (v.getTag().toString()) {
                        case "unlike":

                            ArrayList<VehicleInfoLikeModel> mFeedLikeList = mMyProfileResModel.getVehicleInfoLikesByID();

                            if (mFeedLikeList.size() > 0) {

                                for (VehicleInfoLikeModel likesEntity : mFeedLikeList) {
                                    if (likesEntity.getWhoLikedProfileID() == mMyProfileResModel.getID() && likesEntity.getLikedProfiledID() == mMyProfileResModel.getID()) {
                                        mDeleteLikeID = likesEntity.getID();
                                        break;
                                    }

                                }
                                callUnLikeVehicleInfo(mDeleteLikeID);
                            }

                            break;

                        case "like":

                            callLikeVehicleInfo(mMyProfileResModel.getID(), mMyProfileResModel.getID());

                            break;
                    }
                }
                break;

            case R.id.like_count_txt:
               /* ArrayList<VehicleInfoLikeModel> mLikeModel = mMyProfileResModel.getVehicleInfoLikesByID();
                if (mLikeModel == null) {
                    mLikeModel = new ArrayList<>();
                }
                showLikeListPopup(getString(R.string.likes));
                setFeedLikeAdapter(mLikeModel);*/

                getVehileInfoLikes();
                break;
            case R.id.add_profile_img:
            case R.id.upload_profile_img_tv:
                isCoverPicture = false;
                showAppDialog(AppDialogFragment.BOTTOM_ADD_IMG_DIALOG, null);
                break;
            case R.id.ib_add_cover_photo:
            case R.id.coverImage:
                isCoverPicture = true;
                showAppDialog(AppDialogFragment.BOTTOM_ADD_IMG_DIALOG, null);
                break;
            case R.id.submit_btn:
                if (Utility.getInstance().isSpectator(mMyProfileResModel)) {
                    submitSpectatorData(mProfilePicImgUri, mCoverImgUri);
                } else {
                    submitMotoProfileData(mProfilePicImgUri, mCoverImgUri);
                }
                break;
            case R.id.tag_sponsor_eng_specs_tv:
            case R.id.tag_sponsor_ecu:
            case R.id.tag_sponsor_gear:
            case R.id.tag_sponsor_diff:
            case R.id.tag_sponsor_clutch:
            case R.id.tag_sponsor_fuel:
            case R.id.tag_sponsor_injection:
            case R.id.tag_sponsor_turbo:
            case R.id.tag_sponsor_suspension:
            case R.id.tag_sponsor_panel_and_paint_tv:
            case R.id.tag_sponsor_wheels_and_tyres_tv:
            case R.id.tag_sponsor_tyres:
            case R.id.tag_sponsor_exhaust:
            case R.id.tag_sponsor_fork:
            case R.id.tag_sponsor_shock:
            case R.id.tag_sponsor_lap_timer:
            case R.id.tag_sponsor_rear_set:
            case R.id.tag_sponsor_handle_bar:
            case R.id.tag_sponsor_chain:
            case R.id.tag_sponsor_sprockets:
            case R.id.tag_sponsor_engine_cover:
            case R.id.tag_sponsor_brake_master:
            case R.id.tag_sponsor_brake_pad:
            case R.id.tag_sponsor_filter:
            case R.id.tag_sponsor_brake_fluid:
            case R.id.tag_sponsor_engine_oil:
            case R.id.tag_sponsor_chain_lube:
            case R.id.tag_sponsor_fairings:
            case R.id.tag_sponsor_more_info_tv:
                String mCurrentTagSponsorType = v.getTag().toString();
                showTagSponsorsDialog(AppDialogFragment.ALERT_TAG_SPONSOR_DIALOG, mCurrentTagSponsorType);
                break;
            case R.id.upgrade_btn:
                String myProfileObj = new Gson().toJson(mMyProfileResModel);
                startActivity(new Intent(this, UpgradeProfileActivity.class).putExtra(AppConstants.MY_PROFILE_OBJ, myProfileObj));
                finish();
                break;
        }
    }

    private void getVehileInfoLikes() {
        String mFilter = VehicleInfoLikeModel.LIKED_PROFILE_ID + "=" + mMyProfileResModel.getID();
        RetrofitClient.getRetrofitInstance().callGetVehicleInfoLikes(this, mFilter, RetrofitClient.CALL_GET_VEHICLE_INFO_LIKES);
    }

    private void uploadProfilePicture(String imgUri) {
        File mFile = new File(Uri.parse(imgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", mFile.getName(), requestBody);
        RetrofitClient.getRetrofitInstance().callUploadProfileImg(this, filePart, RetrofitClient.UPLOAD_PROFILE_IMAGE_FILE_RESPONSE);
    }

    private void submitSpectatorData(String profileImgUrl, String coverImgUrl) {
        String mName = mDriverOrNameEt.getText().toString().trim(), mTempProfileImgUrl,
                mTempCoverImgUrl;
        String mPhone = mPhoneEt.getText().toString().trim();
        if (mName.isEmpty()) {
            showSnackBar(mCoordinatorLayout, mEnterNameErr);
            return;
        }
        if (mPhone.isEmpty()) {
            showSnackBar(mCoordinatorLayout, mPhoneErr);
            return;
        }
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
            mTempProfileImgUrl = mMyProfileResModel.getProfilePicture();
        }
        mTempCoverImgUrl = coverImgUrl;
        if (coverImgUrl == null || TextUtils.isEmpty(coverImgUrl)) {
            mTempCoverImgUrl = mMyProfileResModel.getCoverPicture();
        }
        int mUserId = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        JsonObject mJsonObject = new JsonObject();
        mJsonObject.addProperty(ProfileModel.SPECTATOR_NAME, mName);
        mJsonObject.addProperty(ProfileModel.PROFILE_TYPE, String.valueOf(mMyProfileResModel.getProfileType()));
        mJsonObject.addProperty(ProfileModel.USER_ID, mUserId);
        mJsonObject.addProperty(ProfileModel.ID, mMyProfileResModel.getID());
        mJsonObject.addProperty(ProfileModel.PHONE, mPhone);
        mJsonObject.addProperty(ProfileModel.PROFILE_PICTURE, mTempProfileImgUrl);
        mJsonObject.addProperty(ProfileModel.COVER_PICTURE, mTempCoverImgUrl);
        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mJsonObject);
        RetrofitClient.getRetrofitInstance().callUpdateProfile(this, mJsonArray, RetrofitClient.UPDATE_PROFILE_RESPONSE);
    }

    private void uploadCoverPicture(String imgUri) {
        File mFile = new File(Uri.parse(imgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", mFile.getName(), requestBody);
        RetrofitClient.getRetrofitInstance().callUploadProfileCoverImg(this, filePart, RetrofitClient.UPLOAD_COVER_IMAGE_FILE_RESPONSE);
    }

    private void submitMotoProfileData(String profileImgUrl, String coverImgUrl) {
        float mHPFloatValue = 0.0f;
        if (String.valueOf(mMyProfileResModel.getProfileType()).equals(CAR)) {
            mECU = mEcuEt.getText().toString().trim();
            mGear = mGearEt.getText().toString().trim();
            mDifferential = mDifferentialEt.getText().toString().trim();
            mClutch = mClutchEt.getText().toString().trim();
            mInjection = mInjectionEt.getText().toString().trim();
            mFuelSystem = mFuelEt.getText().toString().trim();
            mTurbo = mTurboEt.getText().toString().trim();
            mSuspension = mSuspensionEt.getText().toString().trim();
        }
        if (String.valueOf(mMyProfileResModel.getProfileType()).equals(BIKE)) {
            mECU = mEcuEt.getText().toString().trim();
            mTyres = mTyresEt.getText().toString().trim();
            mExhaust = mExhaustEt.getText().toString().trim();
            mForks = mForkEt.getText().toString().trim();
            mLapTimer = mLapTimerEt.getText().toString().trim();
            mRearSets = mRearSetEt.getText().toString().trim();
            mHandleBars = mHandleBarEt.getText().toString().trim();
            mChain = mChainEt.getText().toString().trim();
            mShock = mShockEt.getText().toString().trim();
            mFairings = mFairingsEt.getText().toString().trim();
            mSprockets = mSprocketsEt.getText().toString().trim();
            mBrakeMaster = mBrakeMasterEt.getText().toString().trim();
            mBrakePad = mBrakePadEt.getText().toString().trim();
            mFilter = mFilterEt.getText().toString().trim();
            mBrakeFluid = mBrakeFluidEt.getText().toString().trim();
            mEngineOil = mEngineOilEt.getText().toString().trim();
            mChainLube = mChainLubeEt.getText().toString().trim();
            mEngineCover = mEngineCoverEt.getText().toString().trim();

        }
        if (String.valueOf(mMyProfileResModel.getProfileType()).equals(CAR) || String.valueOf(mMyProfileResModel.getProfileType()).equals(BIKE)) {
            mBoots = mBootsEt.getText().toString().trim();
            mHelmet = mHelmetEt.getText().toString().trim();
            mSuit = mSuitEt.getText().toString().trim();
            mGloves = mGloveEt.getText().toString().trim();
            mBackProtection = mBackProtectionEt.getText().toString().trim();
            mChestProtection = mChestProtectionEt.getText().toString().trim();
            mTrailer = mTrailerEt.getText().toString().trim();
        }
        String mDriverOrNameStr = mDriverOrNameEt.getText().toString().trim();
        String mNameOfMotoStr = mNameOfMotoEt.getText().toString().trim();
        String mMakeStr = mMakeEt.getText().toString().trim();
        String mModelStr = mModelEt.getText().toString().trim();
        String mPhoneStr = mPhoneEt.getText().toString().trim();
        String mEngineSpecsStr = mEngineSpecsEt.getText().toString().trim();
        String mPanelAndPaintStr = mPanelAndPaintEt.getText().toString().trim();
        String mWheelsAndTyresStr = mWheelsAndTiresEt.getText().toString().trim();
        String mMoreInfoStr = mMoreInfoEt.getText().toString().trim();
        if (!mHPEt.getText().toString().isEmpty()) {
            mHPFloatValue = Float.valueOf(mHPEt.getText().toString());
        }
        if (mDriverOrNameStr.isEmpty()) {
            showSnackBar(mCoordinatorLayout, mDriverNameErr);
            return;
        }
        if (mNameOfMotoStr.isEmpty()) {
            showSnackBar(mCoordinatorLayout, "Please enter name of " + getProfileTypeStr(String.valueOf(mMyProfileResModel.getProfileType())));
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
            mTempProfileImgUrl = mMyProfileResModel.getProfilePicture();
        }
        mTempCoverImgUrl = coverImgUrl;
        if (coverImgUrl == null || TextUtils.isEmpty(coverImgUrl)) {
            mTempCoverImgUrl = mMyProfileResModel.getCoverPicture();
        }
        int mUserId = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        JsonObject mJsonObject = new JsonObject();
        mJsonObject.addProperty(ProfileModel.DRIVER, mDriverOrNameStr);
        mJsonObject.addProperty(ProfileModel.MOTO_NAME, mNameOfMotoStr);
        mJsonObject.addProperty(ProfileModel.MAKE, mMakeStr);
        mJsonObject.addProperty(ProfileModel.MODEL, mModelStr);
        mJsonObject.addProperty(ProfileModel.PHONE, mPhoneStr);
        mJsonObject.addProperty(ProfileModel.HP, mHPFloatValue);
        mJsonObject.addProperty(ProfileModel.ENGINE_SPECS, mEngineSpecsStr);
        mJsonObject.addProperty(ProfileModel.PANEL_AND_PAINT, mPanelAndPaintStr);
        if (String.valueOf(mMyProfileResModel.getProfileType()).equals(CAR)) {
            mJsonObject.addProperty(ProfileModel.ECU, mECU);
            mJsonObject.addProperty(ProfileModel.GEARBOX, mGear);
            mJsonObject.addProperty(ProfileModel.DIFFERENTIAL, mDifferential);
            mJsonObject.addProperty(ProfileModel.CLUTCH, mClutch);
            mJsonObject.addProperty(ProfileModel.INJECTION, mInjection);
            mJsonObject.addProperty(ProfileModel.FUELSYSTEM, mFuelSystem);
            mJsonObject.addProperty(ProfileModel.TURBO, mTurbo);
            mJsonObject.addProperty(ProfileModel.SUSPENSION, mSuspension);
        }
        if (String.valueOf(mMyProfileResModel.getProfileType()).equals(BIKE)) {
            mJsonObject.addProperty(ProfileModel.ECU, mECU);
            mJsonObject.addProperty(ProfileModel.TYRES, mTyres);
            mJsonObject.addProperty(ProfileModel.EXHAUST, mExhaust);
            mJsonObject.addProperty(ProfileModel.FORKS, mForks);
            mJsonObject.addProperty(ProfileModel.SHOCK, mShock);
            mJsonObject.addProperty(ProfileModel.LAPTIMER, mLapTimer);
            mJsonObject.addProperty(ProfileModel.REARSETS, mRearSets);
            mJsonObject.addProperty(ProfileModel.FAIRINGS, mFairings);
            mJsonObject.addProperty(ProfileModel.SPROCKETS, mSprockets);
            mJsonObject.addProperty(ProfileModel.ENGINECOVERS, mEngineCover);
            mJsonObject.addProperty(ProfileModel.BRAKEMASTER, mBrakeMaster);
            mJsonObject.addProperty(ProfileModel.BRAKEPAD, mBrakePad);
            mJsonObject.addProperty(ProfileModel.FILTER, mFilter);
            mJsonObject.addProperty(ProfileModel.BRAKEFLUID, mBrakeFluid);
            mJsonObject.addProperty(ProfileModel.ENGINEOIL, mEngineOil);
            mJsonObject.addProperty(ProfileModel.CHAIN, mChain);
            mJsonObject.addProperty(ProfileModel.HANDLEBARS, mHandleBars);
            mJsonObject.addProperty(ProfileModel.CHAINLUBE, mChainLube);
        }
        if (String.valueOf(mMyProfileResModel.getProfileType()).equals(CAR) || String.valueOf(mMyProfileResModel.getProfileType()).equals(BIKE)) {
            mJsonObject.addProperty(ProfileModel.BOOTS, mBoots);
            mJsonObject.addProperty(ProfileModel.HELMET, mHelmet);
            mJsonObject.addProperty(ProfileModel.SUIT, mSuit);
            mJsonObject.addProperty(ProfileModel.GLOVES, mGloves);
            mJsonObject.addProperty(ProfileModel.BACKPROTECTION, mBackProtection);
            mJsonObject.addProperty(ProfileModel.CHESTPROTECTION, mChestProtection);
            mJsonObject.addProperty(ProfileModel.TRAILER, mTrailer);
        }
        if (!String.valueOf(mMyProfileResModel.getProfileType()).equals(BOAT)) {
            mJsonObject.addProperty(ProfileModel.WHEELS_AND_TYRES, mWheelsAndTyresStr);
        }
        mJsonObject.addProperty(ProfileModel.MORE_INFORMATION, mMoreInfoStr);
        mJsonObject.addProperty(ProfileModel.PROFILE_TYPE, String.valueOf(mMyProfileResModel.getProfileType()));
        mJsonObject.addProperty(ProfileModel.USER_ID, mUserId);
        mJsonObject.addProperty(ProfileModel.ID, mMyProfileResModel.getID());
        mJsonObject.addProperty(ProfileModel.COVER_PICTURE, mTempCoverImgUrl);
        mJsonObject.addProperty(ProfileModel.PROFILE_PICTURE, mTempProfileImgUrl);
        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mJsonObject);
        RetrofitClient.getRetrofitInstance().callUpdateProfile(this, mJsonArray, RetrofitClient.UPDATE_PROFILE_RESPONSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                                File mProfilePictureFile = compressedImgFile(mCameraPicUri,
                                        POST_IMAGE_NAME_TYPE, "");
                                mProfilePicImgUri = Uri.fromFile(mProfilePictureFile).toString();
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
                    Uri mSelectedImgFileUri = (Uri) data.getExtras().get(PickerImageActivity.EXTRA_RESULT_DATA);
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
                                File mProfilePictureFile = compressedImgFile(mSelectedImgFileUri,
                                        POST_IMAGE_NAME_TYPE, "");
                                mProfilePicImgUri = Uri.fromFile(mProfilePictureFile).toString();
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
            switch (responseType) {
                case RetrofitClient.UPDATE_PROFILE_RESPONSE:
                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        showToast(this, mUpdateProfileSuccessStr);
                        mUpdatedProfileResModel = mProfileModel.getResource().get(0);
                        //MotoHub.getApplicationInstance().setmProfileResModel(mUpdatedProfileResModel);
                        EventBus.getDefault().postSticky(mUpdatedProfileResModel);
                        setResult(RESULT_OK, new Intent()/*.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mUpdatedProfileResModel)*/);
                        onBackPressed();
                    }
                    break;
                case RetrofitClient.GET_PROFILE_RESPONSE:
                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        mMyProfileResModel = mProfileModel.getResource().get(0);
                        mUpdatedProfileResModel = mMyProfileResModel;
                        setProfileReady();
                    }
                    break;
            }
        } else if (responseObj instanceof ImageModel) {

            ImageModel mImageModel = (ImageModel) responseObj;

            switch (responseType) {
                case RetrofitClient.UPLOAD_COVER_IMAGE_FILE_RESPONSE:
                    mUploadedServerCoverImgFileUrl = getHttpFilePath(mImageModel.getmModels().get(0).getPath());
                    mCoverImgUri = null;
                    if (mProfilePicImgUri != null) {
                        uploadProfilePicture(mProfilePicImgUri);
                    } else {
                        if (Utility.getInstance().isSpectator(mMyProfileResModel)) {
                            submitSpectatorData("", mUploadedServerCoverImgFileUrl);
                        } else {
                            submitMotoProfileData("", mUploadedServerCoverImgFileUrl);
                        }
                    }
                    break;
                case RetrofitClient.UPLOAD_PROFILE_IMAGE_FILE_RESPONSE:
                    String mUploadedServerProfileImgFileUrl = getHttpFilePath(mImageModel.getmModels().get(0).getPath());
                    mProfilePicImgUri = null;
                    if (Utility.getInstance().isSpectator(mMyProfileResModel)) {
                        submitSpectatorData(
                                mUploadedServerProfileImgFileUrl,
                                mUploadedServerCoverImgFileUrl);
                    } else {
                        submitMotoProfileData(
                                mUploadedServerProfileImgFileUrl,
                                mUploadedServerCoverImgFileUrl);
                    }
                    break;
            }
        } else if (responseObj instanceof SessionModel) {

            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            showSnackBar(mCoordinatorLayout, mSessionUpdated);

        }
        if (responseObj instanceof VehicleInfoLikeModel) {
            VehicleInfoLikeModel mLikeModel = (VehicleInfoLikeModel) responseObj;
            ArrayList<VehicleInfoLikeModel> mLikeList = mLikeModel.getResource();
            switch (responseType) {
                case RetrofitClient.VEHICLE_INFO_LIKE:
                    resetLikeCount(mLikeList.get(0));
                    break;
                case RetrofitClient.VEHICLE_INFO_UN_LIKE:
                    resetUnLikeCount();
                    break;
                case RetrofitClient.CALL_GET_VEHICLE_INFO_LIKES:
                    showLikeListPopup(getString(R.string.likes));
                    setFeedLikeAdapter(mLikeList);
                    break;
            }
        }

    }

    private void resetLikeCount(VehicleInfoLikeModel mLikeModel) {

        mLikeBtn.setImageResource(R.drawable.liked_icon);
        mLikeBtn.setTag("unlike");

        ArrayList<VehicleInfoLikeModel> mVehicleInfoLikeList = mMyProfileResModel.getVehicleInfoLikesByID();
        mVehicleInfoLikeList.add(mLikeModel);
        mMyProfileResModel.setVehicleInfoLikesByID(mVehicleInfoLikeList);
        mUpdatedProfileResModel.setVehicleInfoLikesByID(mVehicleInfoLikeList);

        if (mVehicleInfoLikeList.size() == 1) {
            mLikesStr = mVehicleInfoLikeList.size() + " like";
        } else if (mVehicleInfoLikeList.size() > 1) {
            mLikesStr = mVehicleInfoLikeList.size() + " likes";
        }
        mLikeCountTxt.setText(mLikesStr);
        //MotoHub.getApplicationInstance().setmProfileResModel(mUpdatedProfileResModel);
        EventBus.getDefault().postSticky(mUpdatedProfileResModel);
        setResult(RESULT_OK, new Intent()/*.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mUpdatedProfileResModel)*/);

    }

    private void resetUnLikeCount() {

        mLikeBtn.setImageResource(R.drawable.like_icon);
        mLikeBtn.setTag("like");

        ArrayList<VehicleInfoLikeModel> mVehicleInfoLikeList = mMyProfileResModel.getVehicleInfoLikesByID();
        for (int i = 0; i < mVehicleInfoLikeList.size(); i++) {
            if (mVehicleInfoLikeList.get(i).getWhoLikedProfileID() == mMyProfileResModel.getID() && mVehicleInfoLikeList.get(i).getLikedProfiledID() == mMyProfileResModel.getID()) {
                mVehicleInfoLikeList.remove(i);
                break;
            }
        }
        mMyProfileResModel.setVehicleInfoLikesByID(mVehicleInfoLikeList);
        mUpdatedProfileResModel.setVehicleInfoLikesByID(mVehicleInfoLikeList);
        if (mVehicleInfoLikeList.size() == 0) {
            mLikesStr = "";
        } else if (mVehicleInfoLikeList.size() == 1) {
            mLikesStr = mVehicleInfoLikeList.size() + " like";
        } else if (mVehicleInfoLikeList.size() > 1) {
            mLikesStr = mVehicleInfoLikeList.size() + " likes";
        }
        mLikeCountTxt.setText(mLikesStr);
        //MotoHub.getApplicationInstance().setmProfileResModel(mUpdatedProfileResModel);
        EventBus.getDefault().postSticky(mUpdatedProfileResModel);
        setResult(RESULT_OK, new Intent()/*.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mUpdatedProfileResModel)*/);
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
