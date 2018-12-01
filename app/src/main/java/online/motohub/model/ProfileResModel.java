package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import online.motohub.model.promoter_club_news_media.PromoterFollowerResModel;

public class ProfileResModel implements Serializable {

    boolean isSelected;

    @SerializedName("ID")
    @Expose
    private int mID;

    @SerializedName("UserID")
    @Expose
    private int mUserID;

    @SerializedName("ActiveUserCount")
    @Expose
    private int mActiveUserCount;

    @SerializedName("ProfilePicture")
    @Expose
    private String mProfilePicture;

    @SerializedName("CoverPicture")
    @Expose
    private String mCoverPicture;

    @SerializedName("ProfileType")
    @Expose
    private int mProfileType;

    @SerializedName("FollowMoto")
    @Expose
    private String mFollowMoto;

    @SerializedName("SpectatorName")
    @Expose
    private String mSpectatorName;

    @SerializedName("Driver")
    @Expose
    private String mDriver;

    @SerializedName("Make")
    @Expose
    private String mMake;

    @SerializedName("Model")
    @Expose
    private String mModel;
    @SerializedName("PhoneNumber")
    @Expose
    private String mPhone;
    @SerializedName("DriveLine")
    @Expose
    private String mDriveLine;

    @SerializedName("EngineSpecs")
    @Expose
    private String mEngineSpecs;

    @SerializedName("PanelPaint")
    @Expose
    private String mPanelPaint;

    @SerializedName("HP")
    @Expose
    private float mHP;

    @SerializedName("MoreInformation")
    @Expose
    private String mMoreInformation;

    @SerializedName("MotoName")
    @Expose
    private String mMotoName;

    @SerializedName("WheelsTyres")
    @Expose
    private String mWheelsTyres;

    @SerializedName("Purchased")
    @Expose
    private boolean mPurchased;

    @SerializedName("PurchaseDate")
    @Expose
    private Date mPurchaseDate;

    @SerializedName("PurchaseType")
    @Expose
    private String mPurchaseType;

    public String getEmail() {
        if(mEmail == null)
            mEmail = "";
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    @SerializedName("Email")
    @Expose
    private String mEmail;

    @SerializedName("blockeduserprofiles_by_ProfileID")
    @Expose
    private ArrayList<BlockedUserResModel> mBlockedUserProfilesByProfileID;

    @SerializedName("blockeduserprofiles_by_BlockedProfileID")
    @Expose
    private ArrayList<BlockedUserResModel> blockeduserprofiles_by_BlockedProfileID;

    @SerializedName("promoterfollower_by_ProfileID")
    @Expose
    private ArrayList<PromoterFollowerResModel> mPromoterFollowerByProfileID;

    @SerializedName("livestreamrequest_by_ReceiverProfileID")
    @Expose
    private ArrayList<LiveStreamRequestEntity> livestreamrequest_by_ReceiverProfileID;

    @SerializedName("livestreamrequest_by_RequestedProfileID")
    @Expose
    private ArrayList<LiveStreamRequestEntity> livestreamrequest_by_RequestedProfileID;

    @SerializedName("vehicleinfolikes_by_LikedProfileID")
    @Expose
    private ArrayList<VehicleInfoLikeModel> mVehicleInfoLikesByID;

    @SerializedName("followprofile_by_ProfileID")
    @Expose
    private ArrayList<FollowProfileEntity> followprofile_by_ProfileID;

    @SerializedName("followprofile_by_FollowProfileID")
    @Expose
    private ArrayList<FollowProfileEntity> followprofile_by_FollowProfileID;

    @SerializedName("ECU")
    @Expose
    private String mECU;

    @SerializedName("Trailer")
    @Expose
    private String mTrailer;

    @SerializedName("GearBox")
    @Expose
    private String mGearBox;

    @SerializedName("Injection")
    @Expose
    private String mInjection;

    @SerializedName("Clutch")
    @Expose
    private String mClutch;

    @SerializedName("FuelSystem")
    @Expose
    private String mFuelSystem;

    @SerializedName("Turbo")
    @Expose
    private String mTurbo;

    @SerializedName("Suspension")
    @Expose
    private String mSuspension;

    @SerializedName("Subscription")
    @Expose
    private String Subscription;

    public String getSubscription() {
        if (Subscription == null)
            Subscription = "";
        return Subscription;
    }

    @SerializedName("Differencial")
    @Expose
    private String mDifferencial;

    @SerializedName("Tyres")
    @Expose
    private String mTyres;

    @SerializedName("Exhaust")
    @Expose
    private String mExhaust;

    @SerializedName("Forks")
    @Expose
    private String mForks;

    @SerializedName("RearSets")
    @Expose
    private String mRearSets;

    @SerializedName("LapTimer")
    @Expose
    private String mLapTimer;

    @SerializedName("Shock")
    @Expose
    private String mShock;

    @SerializedName("Chain")
    @Expose
    private String mChain;

    @SerializedName("EngineCovers")
    @Expose
    private String mEngineCovers;

    @SerializedName("Sprockets")
    @Expose
    private String mSprockets;

    @SerializedName("Fairings")
    @Expose
    private String mFairings;

    @SerializedName("BrakeMaster")
    @Expose
    private String mBrakeMaster;

    @SerializedName("BrakeFluid")
    @Expose
    private String mBrakeFluid;

    @SerializedName("BrakePads")
    @Expose
    private String mBrakePad;

    @SerializedName("ChainLube")
    @Expose
    private String mChainLube;

    @SerializedName("HandleBars")
    @Expose
    private String mHandleBars;

    @SerializedName("Filter")
    @Expose
    private String mFilter;

    @SerializedName("EngineOil")
    @Expose
    private String mEngineOil;

    @SerializedName("Boots")
    @Expose
    private String mBoots;

    @SerializedName("Suit")
    @Expose
    private String mSuit;

    @SerializedName("Helmet")
    @Expose
    private String mHelmet;

    @SerializedName("Gloves")
    @Expose
    private String mGloves;

    @SerializedName("BackProtection")
    @Expose
    private String mBackProtection;

    @SerializedName("ChestProtection")
    @Expose
    private String mChestProtection;

    private boolean mIsFollowing;

    private boolean mIsProfileTagged;

    private boolean mIsFollowingChecked;

    private boolean mIsSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

   /* public String getSubscription() {
        if (Subscription == null)
            Subscription = "";
        return Subscription;
    }*/

    public int getID() {
        return mID;
    }

    public void setID(int id) {
        this.mID = id;
    }

    public int getUserID() {
        return mUserID;
    }

    public void setUserID(int userID) {
        this.mUserID = userID;
    }

    public String getProfilePicture() {
        if (mProfilePicture == null)
            mProfilePicture = "";
        return mProfilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.mProfilePicture = profilePicture;
    }

    public String getCoverPicture() {
        if (mCoverPicture == null)
            mCoverPicture = "";
        return mCoverPicture;
    }

    public void setCoverPicture(String mCoverPicture) {
        this.mCoverPicture = mCoverPicture;
    }

    public int getProfileType() {
        return mProfileType;
    }

    public void setProfileType(int profileType) {
        this.mProfileType = profileType;
    }

    public String getFollowMoto() {
        if (mFollowMoto == null)
            mFollowMoto = "";
        return mFollowMoto;
    }

    public void setFollowMoto(String followMoto) {
        this.mFollowMoto = followMoto;
    }

    public String getSpectatorName() {
        if (mSpectatorName == null)
            mSpectatorName = "";
        return mSpectatorName;
    }

    public void setSpectatorName(String spectatorName) {
        this.mSpectatorName = spectatorName;
    }

    public String getDriver() {
        if (mDriver == null)
            mDriver = "";
        return mDriver;
    }

    public void setDriver(String driver) {
        this.mDriver = driver;
    }

    public String getMake() {
        if (mMake == null)
            mMake = "";
        return mMake;
    }

    public void setMake(String make) {
        this.mMake = make;
    }

    public String getModel() {
        if (mModel == null)
            mModel = "";
        return mModel;
    }

    public void setModel(String model) {
        this.mModel = model;
    }

    public String getDriveLine() {

        return mDriveLine;
    }

    public void setDriveLine(String driveLine) {
        this.mDriveLine = driveLine;
    }

    public String getEngineSpecs() {
        return mEngineSpecs;
    }

    public void setEngineSpecs(String engineSpecs) {
        this.mEngineSpecs = engineSpecs;
    }

    public String getPanelPaint() {
        return mPanelPaint;
    }

    public void setPanelPaint(String panelPaint) {
        this.mPanelPaint = panelPaint;
    }

    public float getHP() {
        return mHP;
    }

    public void setHP(float hp) {
        this.mHP = hp;
    }

    public String getMoreInformation() {
        return mMoreInformation;
    }

    public void setMoreInformation(String moreInformation) {
        this.mMoreInformation = moreInformation;
    }

    public String getMotoName() {
        if (mMotoName == null)
            mMotoName = "";
        return mMotoName;
    }

    public void setMotoName(String motoName) {
        this.mMotoName = motoName;
    }

    public String getWheelsTyres() {
        return mWheelsTyres;
    }

    public void setWheelsTyres(String wheelsTyres) {
        this.mWheelsTyres = wheelsTyres;
    }

    public boolean getPurchased() {

        return mPurchased;
    }

    public void setPurchased(boolean purchased) {
        this.mPurchased = purchased;
    }

    public String getTyres() {
        if (mTyres == null)
            mTyres = "";
        return mTyres;
    }

    public void setTyres(String mTyres) {
        this.mTyres = mTyres;
    }

    public String getExhaust() {
        if (mExhaust == null)
            mExhaust = "";
        return mExhaust;
    }

    public void setExhaust(String mExhaust) {
        this.mExhaust = mExhaust;
    }

    public String getForks() {
        if (mForks == null)
            mForks = "";
        return mForks;
    }

    public void setForks(String mForks) {
        this.mForks = mForks;
    }

    public String getRearSets() {
        if (mRearSets == null)
            mRearSets = "";
        return mRearSets;
    }

    public void setRearSets(String mRearSets) {
        this.mRearSets = mRearSets;
    }

    public String getLapTimer() {
        if (mLapTimer == null)
            mLapTimer = "";
        return mLapTimer;
    }

    public void setLapTimer(String mLapTimer) {
        this.mLapTimer = mLapTimer;
    }

    public String getShock() {
        return mShock;
    }

    public void setShock(String mShock) {
        this.mShock = mShock;
    }

    public String getChain() {
        return mChain;
    }

    public void setChain(String mChain) {
        this.mChain = mChain;
    }

    public String getEngineCovers() {
        return mEngineCovers;
    }

    public void setEngineCovers(String mEngineCovers) {
        this.mEngineCovers = mEngineCovers;
    }

    public String getSprockets() {
        return mSprockets;
    }

    public void setSprockets(String mSprockets) {
        this.mSprockets = mSprockets;
    }

    public String getFairings() {
        return mFairings;
    }

    public void setFairings(String mFairings) {
        this.mFairings = mFairings;
    }

    public String getBrakeMaster() {
        return mBrakeMaster;
    }

    public void setBrakeMaster(String mBrakeMaster) {
        this.mBrakeMaster = mBrakeMaster;
    }

    public String getBrakeFluid() {
        return mBrakeFluid;
    }

    public void setBrakeFluid(String mBrakeFluid) {
        this.mBrakeFluid = mBrakeFluid;
    }

    public String getBrakePad() {
        return mBrakePad;
    }

    public void setBrakePad(String mBrakePad) {
        this.mBrakePad = mBrakePad;
    }

    public String getChainLube() {
        return mChainLube;
    }

    public void setChainLube(String mChainLube) {
        this.mChainLube = mChainLube;
    }

    public String getHandleBars() {
        return mHandleBars;
    }

    public void setHandleBars(String mHandleBars) {
        this.mHandleBars = mHandleBars;
    }

    public String getFilter() {
        return mFilter;
    }

    public void setFilter(String mFilter) {
        this.mFilter = mFilter;
    }

    public String getEngineOil() {
        return mEngineOil;
    }

    public void setEngineOil(String mEngineOil) {
        this.mEngineOil = mEngineOil;
    }

    public String getBoots() {
        return mBoots;
    }

    public void setBoots(String mBoots) {
        this.mBoots = mBoots;
    }

    public String getSuit() {
        return mSuit;
    }

    public void setSuit(String mSuit) {
        this.mSuit = mSuit;
    }

    public String getHelmet() {
        return mHelmet;
    }

    public void setHelmet(String mHelmet) {
        this.mHelmet = mHelmet;
    }

    public String getGloves() {
        return mGloves;
    }

    public void setGloves(String mGloves) {
        this.mGloves = mGloves;
    }

    public String getBackProtection() {
        return mBackProtection;
    }

    public void setBackProtection(String mBackProtection) {
        this.mBackProtection = mBackProtection;
    }

    public String getChestProtection() {
        return mChestProtection;
    }

    public void setChestProtection(String mChestProtection) {
        this.mChestProtection = mChestProtection;
    }

    /*public void setPurchased(boolean purchased) {
        this.mPurchased = purchased;
    }*/

    public Date getPurchaseDate() {
        return mPurchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.mPurchaseDate = purchaseDate;
    }

    public String getPurchaseType() {
        return mPurchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.mPurchaseType = purchaseType;
    }


    public boolean getIsFollowing() {
        return mIsFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        this.mIsFollowing = isFollowing;
    }

    public boolean getIsFollowingChecked() {
        return mIsFollowingChecked;
    }

    public void setIsFollowingChecked(boolean isFollowingChecked) {
        this.mIsFollowingChecked = isFollowingChecked;
    }

    public boolean getIsProfileTagged() {
        return mIsProfileTagged;
    }

    public void setIsProfileTagged(boolean isProfileTagged) {
        this.mIsProfileTagged = isProfileTagged;
    }

    public int getmActiveUserCount() {
        return mActiveUserCount;
    }

    public void setmActiveUserCount(int mActiveUserCount) {
        this.mActiveUserCount = mActiveUserCount;
    }

    public boolean getIsSelected() {
        return mIsSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.mIsSelected = isSelected;
    }

    public ArrayList<BlockedUserResModel> getBlockedUserProfilesByProfileID() {
        if (mBlockedUserProfilesByProfileID == null)
            mBlockedUserProfilesByProfileID = new ArrayList<>();

        return mBlockedUserProfilesByProfileID;
    }

    public void setBlockedUserProfilesByProfileID(ArrayList<BlockedUserResModel> blockedUserProfilesByProfileID) {
        this.mBlockedUserProfilesByProfileID = blockedUserProfilesByProfileID;
    }

    public ArrayList<BlockedUserResModel> getBlockeduserprofiles_by_BlockedProfileID() {
        if (blockeduserprofiles_by_BlockedProfileID == null)
            blockeduserprofiles_by_BlockedProfileID = new ArrayList<>();
        return blockeduserprofiles_by_BlockedProfileID;
    }

    public void setBlockeduserprofiles_by_BlockedProfileID(ArrayList<BlockedUserResModel> blockeduserprofiles_by_BlockedProfileID) {
        this.blockeduserprofiles_by_BlockedProfileID = blockeduserprofiles_by_BlockedProfileID;
    }

    public String getECU() {
        return mECU;
    }

    public void setECU(String mECU) {
        this.mECU = mECU;
    }

    public String getGearBox() {
        return mGearBox;
    }

    public void setGearBox(String mGearBox) {
        this.mGearBox = mGearBox;
    }

    public String getInjection() {
        return mInjection;
    }

    public void setInjection(String mInjection) {
        this.mInjection = mInjection;
    }

    public String getClutch() {
        return mClutch;
    }

    public void setClutch(String mClutch) {
        this.mClutch = mClutch;
    }

    public String getFuelSystem() {
        return mFuelSystem;
    }

    public void setFuelSystem(String mFuelSystem) {
        this.mFuelSystem = mFuelSystem;
    }

    public String getTurbo() {
        return mTurbo;
    }

    public void setTurbo(String mTurbo) {
        this.mTurbo = mTurbo;
    }

    public String getSuspension() {
        return mSuspension;
    }

    public void setSuspension(String mSuspension) {
        this.mSuspension = mSuspension;
    }

    public String getDifferencial() {
        return mDifferencial;
    }

    public void setDifferencial(String mDifferencial) {
        this.mDifferencial = mDifferencial;
    }


    public String getPhone() {
        if (mPhone == null)
            return "";
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public ArrayList<LiveStreamRequestEntity> getLivestreamrequest_by_ReceiverProfileID() {
        if (livestreamrequest_by_ReceiverProfileID == null) {
            livestreamrequest_by_ReceiverProfileID = new ArrayList<>();
        }
        return livestreamrequest_by_ReceiverProfileID;
    }

    public void setLivestreamrequest_by_ReceiverProfileID(ArrayList<LiveStreamRequestEntity> livestreamrequest_by_ReceiverProfileID) {
        this.livestreamrequest_by_ReceiverProfileID = livestreamrequest_by_ReceiverProfileID;
    }

    public ArrayList<LiveStreamRequestEntity> getLivestreamrequest_by_RequestedProfileID() {
        if (livestreamrequest_by_RequestedProfileID == null) {
            livestreamrequest_by_RequestedProfileID = new ArrayList<>();
        }
        return livestreamrequest_by_RequestedProfileID;
    }

    public void setLivestreamrequest_by_RequestedProfileID(ArrayList<LiveStreamRequestEntity> livestreamrequest_by_RequestedProfileID) {
        this.livestreamrequest_by_RequestedProfileID = livestreamrequest_by_RequestedProfileID;
    }

    public ArrayList<PromoterFollowerResModel> getPromoterFollowerByProfileID() {
        if (mPromoterFollowerByProfileID == null)
            mPromoterFollowerByProfileID = new ArrayList<>();
        return mPromoterFollowerByProfileID;
    }

    public void setPromoterFollowerByProfileID(ArrayList<PromoterFollowerResModel>
                                                       promoterFollowerByProfileID) {
        this.mPromoterFollowerByProfileID = promoterFollowerByProfileID;
    }

    public String getTrailer() {
        return mTrailer;
    }

    public void setTrailer(String mTrailer) {
        this.mTrailer = mTrailer;
    }

    public ArrayList<VehicleInfoLikeModel> getVehicleInfoLikesByID() {
        if (mVehicleInfoLikesByID == null)
            mVehicleInfoLikesByID = new ArrayList<>();
        return mVehicleInfoLikesByID;
    }

    public void setVehicleInfoLikesByID(ArrayList<VehicleInfoLikeModel> mVehicleInfoLikesByID) {
        this.mVehicleInfoLikesByID = mVehicleInfoLikesByID;
    }

    public ArrayList<FollowProfileEntity> getFollowprofile_by_ProfileID() {
        if (followprofile_by_ProfileID == null)
            followprofile_by_ProfileID = new ArrayList<>();
        return followprofile_by_ProfileID;
    }

    public void setFollowprofile_by_ProfileID(ArrayList<FollowProfileEntity> followprofile_by_ProfileID) {
        this.followprofile_by_ProfileID = followprofile_by_ProfileID;
    }

    public ArrayList<FollowProfileEntity> getFollowprofile_by_FollowProfileID() {
        if (followprofile_by_FollowProfileID == null)
            followprofile_by_FollowProfileID = new ArrayList<>();
        return followprofile_by_FollowProfileID;
    }

    public void setFollowprofile_by_FollowProfileID(ArrayList<FollowProfileEntity> followprofile_by_FollowProfileID) {
        this.followprofile_by_FollowProfileID = followprofile_by_FollowProfileID;
    }

//TODO OLD Followers Followings flow

//    @SerializedName("Followers")
//    @Expose
//    private String mFollowers;
//
//    @SerializedName("Following")
//    @Expose
//    private String mFollowing;
//
//    public String getFollowing() {
//        if (mFollowing == null)
//            mFollowing = "";
//        return mFollowing;
//    }
//
//    public void setFollowing(String following) {
//        this.mFollowing = following;
//    }
//
//    public String getFollowers() {
//        if (mFollowers == null)
//            mFollowers = "";
//        return mFollowers;
//    }
//
//    public void setFollowers(String followers) {
//        this.mFollowers = followers;
//    }
}
