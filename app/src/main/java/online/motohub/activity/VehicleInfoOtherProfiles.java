package online.motohub.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.VehicleInfoLikeAdapter;
import online.motohub.application.MotoHub;
import online.motohub.model.ProfileResModel;
import online.motohub.model.VehicleInfoLikeModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.dialog.DialogManager;

public class VehicleInfoOtherProfiles extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.profile_title_tv)
    TextView mProfileTitleTv;
    @BindView(R.id.coverImage)
    ImageView mCoverImg;
    @BindView(R.id.add_profile_img)
    ImageView mCirProfileImgView;
    @BindView(R.id.driver_tv)
    TextView mDriverOrNameTv;
    @BindView(R.id.driver_et)
    TextView mDriverOrNameEt;
    @BindView(R.id.driver_line)
    View mDriverOrNameLine;
    @BindView(R.id.name_of_moto_box)
    LinearLayout mNameOfMotoBox;
    @BindView(R.id.name_of_moto_tv)
    TextView mNameOfMotoTv;
    @BindView(R.id.name_of_moto_et)
    TextView mNameOfMotoEt;
    @BindView(R.id.name_of_moto_line)
    View mNameOfMotoLine;
    @BindView(R.id.make_box)
    LinearLayout mMakeBox;
    @BindView(R.id.make_et)
    TextView mMakeEt;
    @BindView(R.id.make_line)
    View mMakeLine;
    @BindView(R.id.model_box)
    LinearLayout mModelBox;
    @BindView(R.id.model_et)
    TextView mModelEt;
    @BindView(R.id.model_line)
    View mModelLine;
    @BindView(R.id.phone_box)
    LinearLayout mPhoneBox;
    @BindView(R.id.phone_et)
    TextView mPhoneEt;
    @BindView(R.id.phone_line)
    View mPhoneLine;
    @BindView(R.id.hp_box)
    LinearLayout mHPBox;
    @BindView(R.id.hp_et)
    TextView mHPEt;
    @BindView(R.id.hp_line)
    View mHPLine;
    @BindView(R.id.engine_specs_box)
    LinearLayout mEngineSpecsBox;
    @BindView(R.id.engine_specs_et)
    TextView mEngineSpecsEt;
    @BindView(R.id.engine_specs_line)
    View mEngineSpecsLine;
    @BindView(R.id.panel_and_paint_box)
    LinearLayout mPanelAndPaintBox;
    @BindView(R.id.panel_and_paint_et)
    TextView mPanelAndPaintEt;
    @BindView(R.id.panel_and_paint_line)
    View mPanelAndPaintLine;
    @BindView(R.id.wheels_and_tyres_box)
    LinearLayout mWheelsAndTyresBox;
    @BindView(R.id.wheels_and_tyres_et)
    TextView mWheelsAndTiresEt;
    @BindView(R.id.wheels_and_tyres_line)
    View mWheelsAndTyresLine;
    @BindView(R.id.more_info_box)
    LinearLayout mMoreInfoBox;
    @BindView(R.id.more_info_et)
    TextView mMoreInfoEt;
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
    TextView mEcuEt;
    @BindView(R.id.gear_et)
    TextView mGearEt;
    @BindView(R.id.diff_et)
    TextView mDiffEt;
    @BindView(R.id.clutch_et)
    TextView mClutchEt;
    @BindView(R.id.injection_et)
    TextView mInjectionEt;
    @BindView(R.id.fuel_et)
    TextView mFuelEt;
    @BindView(R.id.turbo_et)
    TextView mTurboEt;
    @BindView(R.id.suspension_et)
    TextView mSuspensionEt;
    @BindView(R.id.tyres_et)
    TextView mTyresEt;
    @BindView(R.id.exhaust_et)
    TextView mExhaustEt;
    @BindView(R.id.fork_et)
    TextView mForkEt;
    @BindView(R.id.shock_et)
    TextView mShockEt;
    @BindView(R.id.lap_timer_et)
    TextView mLapTimerEt;
    @BindView(R.id.rear_set_et)
    TextView mRearSetEt;
    @BindView(R.id.handle_bar_et)
    TextView mHandleBarEt;
    @BindView(R.id.fairings_et)
    TextView mFairingsEt;
    @BindView(R.id.chain_et)
    TextView mChainEt;
    @BindView(R.id.sprockets_et)
    TextView mSprocketsEt;
    @BindView(R.id.engine_cover_et)
    TextView mEngineCoverEt;
    @BindView(R.id.brake_master_et)
    TextView mBrakeMasterEt;
    @BindView(R.id.brake_pad_et)
    TextView mBrakePadEt;
    @BindView(R.id.filter_et)
    TextView mFilterEt;
    @BindView(R.id.brake_fluid_et)
    TextView mBrakeFluidEt;
    @BindView(R.id.engine_oil_et)
    TextView mEngineOilEt;
    @BindView(R.id.chain_lube_et)
    TextView mChainLubeEt;
    @BindView(R.id.boots_et)
    TextView mBootsEt;
    @BindView(R.id.helmet_et)
    TextView mHelmetEt;
    @BindView(R.id.suit_et)
    TextView mSuitEt;
    @BindView(R.id.gloves_et)
    TextView mGloveEt;
    @BindView(R.id.back_protection_et)
    TextView mBackProtectionEt;
    @BindView(R.id.chest_protection_et)
    TextView mChestProtectionEt;
    @BindView(R.id.trailer_et)
    TextView mTrailerEt;
    @BindView(R.id.tyres_line)
    View mTyresLine;
    @BindView(R.id.exhaust_line)
    View mExhaustLine;
    @BindView(R.id.fork_line)
    View mForkLine;
    @BindView(R.id.shock_line)
    View mShockLine;
    @BindView(R.id.lap_timer_line)
    View mLapTimerLine;
    @BindView(R.id.rear_set_line)
    View mRearSetLine;
    @BindView(R.id.handle_bar_line)
    View mHandleBarLine;
    @BindView(R.id.fairings_line)
    View mFairingsLine;
    @BindView(R.id.chain_line)
    View mChainLine;
    @BindView(R.id.sprockets_line)
    View mSprocketsLine;
    @BindView(R.id.engine_cover_line)
    View mEngineCoverLine;
    @BindView(R.id.brake_master_line)
    View mBrakeMasterLine;
    @BindView(R.id.brake_pad_line)
    View mBrakePadLine;
    @BindView(R.id.filter_line)
    View mFilterLine;
    @BindView(R.id.brake_fluid_line)
    View mBrakeFluidLine;
    @BindView(R.id.engine_oil_line)
    View mEngineOilLine;
    @BindView(R.id.chain_lube_line)
    View mChainLubeLine;
    @BindView(R.id.boots_line)
    View mBootsLine;
    @BindView(R.id.helmet_line)
    View mHelmetLine;
    @BindView(R.id.suit_line)
    View mSuitLine;
    @BindView(R.id.gloves_line)
    View mGloveLine;
    @BindView(R.id.back_protection_line)
    View mBackProtectionLine;
    @BindView(R.id.chest_protection_line)
    View mChestProtectionLine;
    @BindView(R.id.trailer_line)
    View mTrailerLine;
    @BindView(R.id.tyres_box)
    LinearLayout mTyresBox;
    @BindView(R.id.exhaust_box)
    LinearLayout mExhaustBox;
    @BindView(R.id.fork_box)
    LinearLayout mForkBox;
    @BindView(R.id.shock_box)
    LinearLayout mShockBox;
    @BindView(R.id.lap_timer_box)
    LinearLayout mLapTimerBox;
    @BindView(R.id.rear_set_box)
    LinearLayout mRearSetBox;
    @BindView(R.id.handle_bar_box)
    LinearLayout mHandleBarBox;
    @BindView(R.id.fairings_box)
    LinearLayout mFairingsBox;
    @BindView(R.id.chain_box)
    LinearLayout mChainBox;
    @BindView(R.id.sprockets_box)
    LinearLayout mSprocketsBox;
    @BindView(R.id.engine_cover_box)
    LinearLayout mEngineCoverBox;
    @BindView(R.id.brake_master_box)
    LinearLayout mBrakeMasterBox;
    @BindView(R.id.brake_pad_box)
    LinearLayout mBrakePadBox;
    @BindView(R.id.filter_box)
    LinearLayout mFilterBox;
    @BindView(R.id.brake_fluid_box)
    LinearLayout mBrakeFluidBox;
    @BindView(R.id.engine_oil_box)
    LinearLayout mEngineOilBox;
    @BindView(R.id.chain_lube_box)
    LinearLayout mChainLubeBox;
    @BindView(R.id.boots_box)
    LinearLayout mBootsBox;
    @BindView(R.id.helmet_box)
    LinearLayout mHelmetBox;
    @BindView(R.id.suit_box)
    LinearLayout mSuitBox;
    @BindView(R.id.gloves_box)
    LinearLayout mGloveBox;
    @BindView(R.id.back_protection_box)
    LinearLayout mBackProtectionBox;
    @BindView(R.id.chest_protection_box)
    LinearLayout mChestProtectionBox;
    @BindView(R.id.trailer_box)
    LinearLayout mTrailerBox;
    @BindView(R.id.likeBtn)
    ImageView mLikeBtn;
    @BindView(R.id.like_count_txt)
    TextView mLikeCountTxt;

    @BindString(R.string.vehicle_information)
    String mToolbarTitle;
    @BindString(R.string.name)
    String mNameStr;
    @BindString(R.string.driver)
    String mDriverStr;

    private ProfileResModel mOthersProfileResModel;
    private ProfileResModel mMyProfileResModel;
    private int mDeleteLikeID;

    private String mLikes;
    private Dialog mCommentListPopup;
    private RecyclerView mFeedLikesListView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_info_others_profile);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initView() {
        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        assert getIntent().getExtras() != null;
        /*mOthersProfileResModel =
                (ProfileResModel) getIntent()
                        .getExtras()
                        .getSerializable(ProfileModel.OTHERS_PROFILE_RES_MODEL);*/
        //mMyProfileResModel = (ProfileResModel) getIntent().getExtras().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        mOthersProfileResModel = MotoHub.getApplicationInstance().getmOthersProfileResModel();
        //mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
        setProfileReady();
    }

    private void setProfileReady() {

        String mProfileTitleStr =
                getProfileTypeStr(String.valueOf(mOthersProfileResModel.getProfileType()))
                        + " Profile";
        mProfileTitleTv.setText(mProfileTitleStr);
        String mNameOfMotoStr =
                getString(R.string.name_of) + " "
                        + getProfileTypeStr(String.valueOf(mOthersProfileResModel.getProfileType()));
        mNameOfMotoTv.setText(mNameOfMotoStr);
        if (mOthersProfileResModel.getProfilePicture() != null
                && !TextUtils.isEmpty(mOthersProfileResModel.getProfilePicture())) {
            setImageWithGlide(mCirProfileImgView,
                    mOthersProfileResModel.getProfilePicture(),
                    R.drawable.add_img_icon);
        }
        if (mOthersProfileResModel.getCoverPicture() != null
                && !TextUtils.isEmpty(mOthersProfileResModel.getCoverPicture())) {
            setImageWithGlide(mCoverImg,
                    mOthersProfileResModel.getCoverPicture(),
                    R.drawable.app_logo);
        }
        if (String.valueOf(mOthersProfileResModel.getProfileType()).equals(BOAT)) {
            showHideBikeProperties(View.GONE);
            showHideCarProperties(View.GONE);
            showHideCarAndBikeProperties(View.GONE);
            showHideWidgets(View.VISIBLE);
            mWheelsAndTyresBox.setVisibility(View.GONE);
            mWheelsAndTyresLine.setVisibility(View.GONE);
            setMotoProfileVal();
        } else if (String.valueOf(mOthersProfileResModel.getProfileType()).equals(SPECTATOR)) {
            showHideWidgets(View.GONE);
            showHideBikeProperties(View.GONE);
            showHideCarProperties(View.GONE);
            showHideCarAndBikeProperties(View.GONE);
            mDriverOrNameEt.setText(mOthersProfileResModel.getSpectatorName());
            /*mPhoneEt.setText(mOthersProfileResModel.getPhone());
            hideWidgetWhileEmpty(mPhoneEt, mPhoneBox.getId(), mPhoneLine.getId());*/
        } else if (String.valueOf(mOthersProfileResModel.getProfileType()).equals(CAR)) {
            showHideCarProperties(View.VISIBLE);
            showHideWidgets(View.VISIBLE);
            showHideCarAndBikeProperties(View.VISIBLE);
            showHideBikeProperties(View.GONE);
            setMotoProfileVal();
        } else if (String.valueOf(mOthersProfileResModel.getProfileType()).equals(BIKE)) {
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

        setLikes();
    }

    private void setLikes() {

        ArrayList<VehicleInfoLikeModel> mFeedLikes = mOthersProfileResModel.getVehicleInfoLikesByID();
        if (mFeedLikes.size() > 0) {
            int mLikeCount = mFeedLikes.size();
            if (mLikeCount == 0) {
                mLikeCountTxt.setVisibility(View.GONE);
            }
            if (mLikeCount == 1) {
                mLikes = mLikeCount + " like";
                mLikeCountTxt.setText(mLikes);
            } else if (mLikeCount > 1) {
                mLikes = mLikeCount + " likes";
                mLikeCountTxt.setText(mLikes);
            }
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
    }

    private void setMotoProfileVal() {
        mDriverOrNameEt.setText(mOthersProfileResModel.getDriver());
        mNameOfMotoEt.setText(mOthersProfileResModel.getMotoName());
        hideWidgetWhileEmpty(mNameOfMotoEt, mNameOfMotoBox.getId(), mNameOfMotoLine.getId());
        mMakeEt.setText(mOthersProfileResModel.getMake());
        hideWidgetWhileEmpty(mMakeEt, mMakeBox.getId(), mMakeLine.getId());
        mModelEt.setText(mOthersProfileResModel.getModel());
        hideWidgetWhileEmpty(mModelEt, mModelBox.getId(), mModelLine.getId());
        /*mPhoneEt.setText(mOthersProfileResModel.getPhone());
        hideWidgetWhileEmpty(mPhoneEt, mPhoneBox.getId(), mPhoneLine.getId());*/
        mHPEt.setText(String.valueOf(mOthersProfileResModel.getHP()));
        hideWidgetWhileEmpty(mHPEt, mHPBox.getId(), mHPLine.getId());
        mEngineSpecsEt.setText(mOthersProfileResModel.getEngineSpecs());
        hideWidgetWhileEmpty(mEngineSpecsEt, mEngineSpecsBox.getId(), mEngineSpecsLine.getId());
        mPanelAndPaintEt.setText(mOthersProfileResModel.getPanelPaint());
        hideWidgetWhileEmpty(mPanelAndPaintEt, mPanelAndPaintBox.getId(), mPanelAndPaintLine.getId());
        if (!String.valueOf(mOthersProfileResModel.getProfileType()).equals(BOAT)) {
            mWheelsAndTiresEt.setText(mOthersProfileResModel.getWheelsTyres());
            hideWidgetWhileEmpty(mWheelsAndTiresEt, mWheelsAndTyresBox.getId(),
                    mWheelsAndTyresLine.getId());
        }
        if (String.valueOf(mOthersProfileResModel.getProfileType()).equals(CAR)) {
            mGearEt.setText(mOthersProfileResModel.getGearBox());
            hideWidgetWhileEmpty(mGearEt, mGearBox.getId(), mGearLine.getId());
            mDiffEt.setText(mOthersProfileResModel.getDifferencial());
            hideWidgetWhileEmpty(mDiffEt, mDiffBox.getId(), mDiffLine.getId());
            mClutchEt.setText(mOthersProfileResModel.getClutch());
            hideWidgetWhileEmpty(mClutchEt, mClutchBox.getId(), mClutchLine.getId());
            mInjectionEt.setText(mOthersProfileResModel.getInjection());
            hideWidgetWhileEmpty(mInjectionEt, mInjectionBox.getId(), mInjectionLine.getId());
            mFuelEt.setText(mOthersProfileResModel.getFuelSystem());
            hideWidgetWhileEmpty(mFuelEt, mFuelBox.getId(), mFuelLine.getId());
            mTurboEt.setText(mOthersProfileResModel.getTurbo());
            hideWidgetWhileEmpty(mTurboEt, mTurboBox.getId(), mTurboLine.getId());
            mSuspensionEt.setText(mOthersProfileResModel.getSuspension());
            hideWidgetWhileEmpty(mSuspensionEt, mSuspensionBox.getId(), mSuspensionLine.getId());
        }
        if (String.valueOf(mOthersProfileResModel.getProfileType()).equals(BIKE)) {
            mTyresEt.setText(mOthersProfileResModel.getTyres());
            hideWidgetWhileEmpty(mTyresEt, mTyresBox.getId(), mTyresLine.getId());
            mExhaustEt.setText(mOthersProfileResModel.getExhaust());
            hideWidgetWhileEmpty(mExhaustEt, mExhaustBox.getId(), mExhaustLine.getId());
            mForkEt.setText(mOthersProfileResModel.getForks());
            hideWidgetWhileEmpty(mForkEt, mForkBox.getId(), mForkLine.getId());
            mShockEt.setText(mOthersProfileResModel.getShock());
            hideWidgetWhileEmpty(mShockEt, mShockBox.getId(), mShockLine.getId());
            mLapTimerEt.setText(mOthersProfileResModel.getLapTimer());
            hideWidgetWhileEmpty(mLapTimerEt, mLapTimerBox.getId(), mLapTimerLine.getId());
            mRearSetEt.setText(mOthersProfileResModel.getRearSets());
            hideWidgetWhileEmpty(mRearSetEt, mRearSetBox.getId(), mRearSetLine.getId());
            mChainEt.setText(mOthersProfileResModel.getChain());
            hideWidgetWhileEmpty(mChainEt, mChainBox.getId(), mChainLine.getId());
            mFairingsEt.setText(mOthersProfileResModel.getFairings());
            hideWidgetWhileEmpty(mFairingsEt, mFairingsBox.getId(), mFairingsLine.getId());
            mSprocketsEt.setText(mOthersProfileResModel.getSprockets());
            hideWidgetWhileEmpty(mSprocketsEt, mSprocketsBox.getId(), mSprocketsLine.getId());
            mEngineCoverEt.setText(mOthersProfileResModel.getEngineCovers());
            hideWidgetWhileEmpty(mEngineCoverEt, mEngineCoverBox.getId(), mEngineCoverLine.getId());
            mBrakeMasterEt.setText(mOthersProfileResModel.getBrakeMaster());
            hideWidgetWhileEmpty(mBrakeMasterEt, mBrakeMasterBox.getId(), mBrakeMasterLine.getId());
            mBrakePadEt.setText(mOthersProfileResModel.getBrakePad());
            hideWidgetWhileEmpty(mBrakePadEt, mBrakePadBox.getId(), mBrakePadLine.getId());
            mBrakeFluidEt.setText(mOthersProfileResModel.getBrakeFluid());
            hideWidgetWhileEmpty(mBrakeFluidEt, mBrakeFluidBox.getId(), mBrakeFluidLine.getId());
            mFilterEt.setText(mOthersProfileResModel.getFilter());
            hideWidgetWhileEmpty(mFilterEt, mFilterBox.getId(), mFilterLine.getId());
            mHandleBarEt.setText(mOthersProfileResModel.getHandleBars());
            hideWidgetWhileEmpty(mHandleBarEt, mHandleBarBox.getId(), mHandleBarLine.getId());
            mChainLubeEt.setText(mOthersProfileResModel.getChainLube());
            hideWidgetWhileEmpty(mChainLubeEt, mChainLubeBox.getId(), mChainLubeLine.getId());
            mEngineOilEt.setText(mOthersProfileResModel.getEngineOil());
            hideWidgetWhileEmpty(mEngineOilEt, mEngineOilBox.getId(), mEngineOilLine.getId());
        }
        if (String.valueOf(mOthersProfileResModel.getProfileType()).equals(BIKE)
                || (String.valueOf(mOthersProfileResModel.getProfileType()).equals(CAR))) {
            mEcuEt.setText(mOthersProfileResModel.getECU());
            hideWidgetWhileEmpty(mEcuEt, mEcuBox.getId(), mEcuLine.getId());
            mBootsEt.setText(mOthersProfileResModel.getBoots());
            hideWidgetWhileEmpty(mBootsEt, mBootsBox.getId(), mBootsLine.getId());
            mHelmetEt.setText(mOthersProfileResModel.getHelmet());
            hideWidgetWhileEmpty(mHelmetEt, mHelmetBox.getId(), mHelmetLine.getId());
            mSuitEt.setText(mOthersProfileResModel.getSuit());
            hideWidgetWhileEmpty(mSuitEt, mSuitBox.getId(), mSuitLine.getId());
            mGloveEt.setText(mOthersProfileResModel.getGloves());
            hideWidgetWhileEmpty(mGloveEt, mGloveBox.getId(), mGloveLine.getId());
            mBackProtectionEt.setText(mOthersProfileResModel.getBackProtection());
            hideWidgetWhileEmpty(mBackProtectionEt, mBackProtectionBox.getId(),
                    mBackProtectionLine.getId());
            mChestProtectionEt.setText(mOthersProfileResModel.getChestProtection());
            hideWidgetWhileEmpty(mChestProtectionEt, mChestProtectionBox.getId(),
                    mChestProtectionLine.getId());
            mTrailerEt.setText(mOthersProfileResModel.getTrailer());
            hideWidgetWhileEmpty(mTrailerEt, mTrailerBox.getId(), mTrailerLine.getId());
        }
        mMoreInfoEt.setText(mOthersProfileResModel.getMoreInformation());
        if (mMoreInfoEt.getText().toString().trim().isEmpty()) {
            mMoreInfoBox.setVisibility(View.GONE);
        }
    }

    private void hideWidgetWhileEmpty(final TextView textView,
                                      final int textViewID,
                                      final int textViewLineID) {
        if (textView.getText().toString().trim().isEmpty()) {
            findViewById(textViewID).setVisibility(View.GONE);
            findViewById(textViewLineID).setVisibility(View.GONE);
        }
    }

    private void showHideWidgets(int visibility) {
        if (visibility == View.GONE) {
            mDriverOrNameTv.setText(mNameStr);
            mDriverOrNameEt.setHint(mNameStr);
        } else {
            mDriverOrNameTv.setText(mDriverStr);
            mDriverOrNameEt.setHint(mDriverStr);
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

    private void showHideBikeProperties(int visibility) {
        mTyresBox.setVisibility(visibility);
        mExhaustBox.setVisibility(visibility);
        mForkBox.setVisibility(visibility);
        mShockBox.setVisibility(visibility);
        mLapTimerBox.setVisibility(visibility);
        mRearSetBox.setVisibility(visibility);
        mHandleBarBox.setVisibility(visibility);
        mFairingsBox.setVisibility(visibility);

        mChainBox.setVisibility(visibility);
        mBrakePadBox.setVisibility(visibility);
        mBrakeMasterBox.setVisibility(visibility);
        mFilterBox.setVisibility(visibility);
        mEngineOilBox.setVisibility(visibility);
        mChainLubeBox.setVisibility(visibility);
        mSprocketsBox.setVisibility(visibility);
        mEngineCoverBox.setVisibility(visibility);
        mBrakeFluidBox.setVisibility(visibility);

        mTyresLine.setVisibility(visibility);
        mExhaustLine.setVisibility(visibility);
        mForkLine.setVisibility(visibility);
        mShockLine.setVisibility(visibility);
        mLapTimerLine.setVisibility(visibility);
        mRearSetLine.setVisibility(visibility);
        mHandleBarLine.setVisibility(visibility);
        mFairingsLine.setVisibility(visibility);
        mChainLine.setVisibility(visibility);
        mBrakePadLine.setVisibility(visibility);
        mBrakeMasterLine.setVisibility(visibility);
        mFilterLine.setVisibility(visibility);
        mEngineOilLine.setVisibility(visibility);
        mChainLubeLine.setVisibility(visibility);
        mSprocketsLine.setVisibility(visibility);
        mEngineCoverLine.setVisibility(visibility);
        mBrakeFluidLine.setVisibility(visibility);

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
        mTrailerBox.setVisibility(visibility);
        mBootsBox.setVisibility(visibility);
        mHelmetBox.setVisibility(visibility);
        mSuitBox.setVisibility(visibility);
        mGloveBox.setVisibility(visibility);
        // mBackProtectionBox.setVisibility(visibility);
        // mChestProtectionBox.setVisibility(visibility);
        mTrailerLine.setVisibility(visibility);
        mEcuLine.setVisibility(visibility);
        mBootsLine.setVisibility(visibility);
        mHelmetLine.setVisibility(visibility);
        mSuitLine.setVisibility(visibility);
        mGloveLine.setVisibility(visibility);
        // mBackProtectionLine.setVisibility(visibility);
        // mChestProtectionLine.setVisibility(visibility);
        mWheelsAndTyresBox.setVisibility(visibility);
        mWheelsAndTyresLine.setVisibility(visibility);
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

    private void getVehileInfoLikes() {
        String mFilter = VehicleInfoLikeModel.LIKED_PROFILE_ID + "=" + mOthersProfileResModel.getID();

        RetrofitClient.getRetrofitInstance().callGetVehicleInfoLikes(this, mFilter, RetrofitClient.CALL_GET_VEHICLE_INFO_LIKES);
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.likeBtn, R.id.like_count_txt})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.like_count_txt:
                getVehileInfoLikes();
                break;
            case R.id.likeBtn:
                if (!isMultiClicked()) {
                    switch (v.getTag().toString()) {
                        case "unlike":

                            ArrayList<VehicleInfoLikeModel> mFeedLikeList = mOthersProfileResModel.getVehicleInfoLikesByID();

                            if (mFeedLikeList.size() > 0) {

                                for (VehicleInfoLikeModel likesEntity : mFeedLikeList) {
                                    if (likesEntity.getWhoLikedProfileID() == mMyProfileResModel.getID()) {
                                        mDeleteLikeID = likesEntity.getID();
                                        break;
                                    }

                                }
                                callUnLikeVehicleInfo(mDeleteLikeID);
                            }

                            break;

                        case "like":

                            callLikeVehicleInfo(mOthersProfileResModel.getID(), mMyProfileResModel.getID());

                            break;
                    }
                }
                break;
        }
    }

    private void callLikeVehicleInfo(int mLikedProfileID, int mWhoLikedProfileID) {

        JsonObject mItem = new JsonObject();

        mItem.addProperty(VehicleInfoLikeModel.LIKED_PROFILE_ID, mLikedProfileID);
        mItem.addProperty(VehicleInfoLikeModel.WHO_LIKED_PROFILE_ID, mWhoLikedProfileID);

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mItem);

        RetrofitClient.getRetrofitInstance().postLikeForVehicleInfo(this, mJsonArray, RetrofitClient.VEHICLE_INFO_LIKE);
    }

    private void callUnLikeVehicleInfo(int mDeleteLikeID) {
        RetrofitClient.getRetrofitInstance().callUnLikeForVehicleInfo(this, mDeleteLikeID, RetrofitClient.VEHICLE_INFO_UN_LIKE);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
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

        ArrayList<VehicleInfoLikeModel> mVehicleInfoLikeList = mOthersProfileResModel.getVehicleInfoLikesByID();
        mVehicleInfoLikeList.add(mLikeModel);
        mOthersProfileResModel.setVehicleInfoLikesByID(mVehicleInfoLikeList);

        if (mVehicleInfoLikeList.size() == 1) {
            mLikes = mVehicleInfoLikeList.size() + " like";
        } else if (mVehicleInfoLikeList.size() > 1) {
            mLikes = mVehicleInfoLikeList.size() + " likes";
        }
        mLikeCountTxt.setText(mLikes);
        MotoHub.getApplicationInstance().setmOthersProfileResModel(mOthersProfileResModel);
        setResult(RESULT_OK, new Intent()/*.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mOthersProfileResModel)*/);

    }

    private void resetUnLikeCount() {

        mLikeBtn.setImageResource(R.drawable.like_icon);
        mLikeBtn.setTag("like");

        ArrayList<VehicleInfoLikeModel> mVehicleInfoLikeList = mOthersProfileResModel.getVehicleInfoLikesByID();
        for (int i = 0; i < mVehicleInfoLikeList.size(); i++) {
            if (mVehicleInfoLikeList.get(i).getWhoLikedProfileID() == mMyProfileResModel.getID() && mVehicleInfoLikeList.get(i).getLikedProfiledID() == mOthersProfileResModel.getID()) {
                mVehicleInfoLikeList.remove(i);
                break;
            }
        }
        mOthersProfileResModel.setVehicleInfoLikesByID(mVehicleInfoLikeList);
        if (mVehicleInfoLikeList.size() == 0) {
            mLikes = "";
        } else if (mVehicleInfoLikeList.size() == 1) {
            mLikes = mVehicleInfoLikeList.size() + " like";
        } else if (mVehicleInfoLikeList.size() > 1) {
            mLikes = mVehicleInfoLikeList.size() + " likes";
        }

        mLikeCountTxt.setText(mLikes);
        MotoHub.getApplicationInstance().setmOthersProfileResModel(mOthersProfileResModel);
        setResult(RESULT_OK, new Intent()/*.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mOthersProfileResModel)*/);
    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showToast(this, mErrorMsg);
        }

    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
    }
}



