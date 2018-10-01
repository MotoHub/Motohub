package online.motohub.model.promoter_club_news_media;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import online.motohub.model.ClubGroupModel;
import online.motohub.model.GalleryVideoResModel;
import online.motohub.model.LiveStreamEntity;
import online.motohub.model.LiveStreamPaymentEntity;
import online.motohub.model.TrackResModel;

public class PromotersResModel implements Serializable {

    @SerializedName("ID")
    @Expose
    private int iD;

    @SerializedName("Name")
    @Expose
    private String name;

    @SerializedName("CreatedOn")
    @Expose
    private String createdOn;

    @SerializedName("profile_image")
    @Expose
    private String profileImage;

    @SerializedName("user_id")
    @Expose
    private int userId;

    @SerializedName("user_type")
    @Expose
    private String userType;

    @SerializedName("cover_image")
    @Expose
    private String coverImage;

    @SerializedName("about_us")
    @Expose
    private String aboutUs;

    @SerializedName("Status")
    @Expose
    private int status;

    @SerializedName("stripe_publishable_key")
    @Expose
    private String stripePublishableKey;

    @SerializedName("subscription_planID")
    @Expose
    private String subscription_planID;

    @SerializedName("access_token")
    @Expose
    private String accessToken;

    @SerializedName("refresh_token")
    @Expose
    private String refreshToken;

    @SerializedName("stripe_user_id")
    @Expose
    private String stripeUserId;

    @SerializedName("promoterfollower_by_PromoterUserID")
    @Expose
    private ArrayList<PromoterFollowerResModel> mPromoterFollowerByPromoterUserID;

    @SerializedName("track_by_user_id")
    @Expose
    private TrackResModel mTrackByUserID;

    @SerializedName("livestream_by_StreamProfileID")
    @Expose
    private ArrayList<LiveStreamEntity> livestream_by_StreamProfileID;


    @SerializedName("livestreampayment_by_PromoterID")
    @Expose
    private ArrayList<LiveStreamPaymentEntity> livestreampayment_by_PromoterID;

    @SerializedName("clubgroup_by_ClubUserID")
    @Expose
    private ArrayList<ClubGroupModel> mClubGroupByClubUserID;

    @SerializedName("subscription_fee")
    @Expose
    private int subscription_fee = 0;

    private boolean mIsFollowing;

    private boolean mIsSelected;
    @SerializedName("profilevideogallery_by_UserID")
    @Expose
    private ArrayList<GalleryVideoResModel> gallery = null;

    public int getID() {
        return iD;
    }

    public void setID(int iD) {
        this.iD = iD;
    }

    public String getName() {
        if (name == null)
            name = "";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getProfileImage() {
        if (profileImage == null)
            profileImage = "";
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getCoverImage() {
        if (coverImage == null)
            coverImage = "";
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStripePublishableKey() {
        return stripePublishableKey;
    }

    public void setStripePublishableKey(String stripePublishableKey) {
        this.stripePublishableKey = stripePublishableKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getStripeUserId() {
        return stripeUserId;
    }

    public void setStripeUserId(String stripeUserId) {
        this.stripeUserId = stripeUserId;
    }

    public boolean getIsSelected() {
        return mIsSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.mIsSelected = isSelected;
    }

    public ArrayList<PromoterFollowerResModel> getPromoterFollowerByPromoterUserID() {
        return mPromoterFollowerByPromoterUserID;
    }

    public void setPromoterFollowerByPromoterUserID(ArrayList<PromoterFollowerResModel>
                                                            promoterFollowerByPromoterUserID) {
        this.mPromoterFollowerByPromoterUserID = promoterFollowerByPromoterUserID;
    }

    public boolean getIsFollowing() {
        return mIsFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        this.mIsFollowing = isFollowing;
    }

    public TrackResModel getTrackByUserID() {
        return mTrackByUserID;
    }

    public void setTrackByUserID(TrackResModel mTrackByUserID) {
        this.mTrackByUserID = mTrackByUserID;
    }

    public ArrayList<LiveStreamEntity> getLivestream_by_StreamProfileID() {
        if (livestream_by_StreamProfileID == null)
            livestream_by_StreamProfileID = new ArrayList<>();
        return livestream_by_StreamProfileID;
    }

    public ArrayList<LiveStreamPaymentEntity> getLivestreampayment_by_PromoterID() {
        if (livestreampayment_by_PromoterID == null)
            livestreampayment_by_PromoterID = new ArrayList<>();
        return livestreampayment_by_PromoterID;
    }

    public void setLivestreampayment_by_PromoterID(ArrayList<LiveStreamPaymentEntity> livestreampayment_by_PromoterID) {
        this.livestreampayment_by_PromoterID = livestreampayment_by_PromoterID;
    }

    public ArrayList<ClubGroupModel> getClubGroupByClubUserID() {
        if (mClubGroupByClubUserID == null)
            mClubGroupByClubUserID = new ArrayList<>();
        return mClubGroupByClubUserID;
    }

    public void setClubGroupByClubUserID(ArrayList<ClubGroupModel> mClubGroupByClubUserID) {
        this.mClubGroupByClubUserID = mClubGroupByClubUserID;
    }

    public ArrayList<GalleryVideoResModel> getGallery() {
        return gallery;
    }

    public void setGallery(ArrayList<GalleryVideoResModel> gallery) {
        this.gallery = gallery;
    }

    public int getSubscription_fee() {
        return subscription_fee;
    }

    public void setSubscription_fee(int subscription_fee) {
        this.subscription_fee = subscription_fee;
    }

    public String getSubscription_planID() {
        return subscription_planID;
    }

    public void setSubscription_planID(String subscription_planID) {
        this.subscription_planID = subscription_planID;
    }
}
