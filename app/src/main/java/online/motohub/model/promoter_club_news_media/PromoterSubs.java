package online.motohub.model.promoter_club_news_media;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import online.motohub.model.FailedSubscriptionModel;
import online.motohub.model.ProfileResModel;

/**
 * Created by pickzy01 on 05/04/2018.
 */

public class PromoterSubs {

    public static final String ID_ = "ID";
    public static final String PURCHASE_TOKEN = "PurchaseToken";
    public static final String PURCHASE_DATE = "PurchasedDate";
    public static final String PROMOTER_ID = "PromoterID";
    public static final String PROMOTER_TYPE = "PromoterType";
    public static final String SUBS_STATUS = "SubscriptionStatus";
    public static final String PROFILE_ID = "ProfileID";
    public static final String SUBS_ID = "SubscriptionID";
    public static final String CUSTOMER_ID = "CustomerID";
    public static final String PLAN_ID = "PlanID";
    public static final String TYPE = "type";
    public static final String USER_ID = "UserID";

    private String PurchaseToken;
    private String PurchasedDate;
    private int SubscriptionStatus;
    private int ID;
    private int ProfileID;
    private String SubscriptionID;
    private String CustomerID;
    private String UserID;
    private String PlanID;
    private String PromoterID;
    private String PromoterType;
    @SerializedName("profiles_by_ProfileID")
    @Expose
    private ProfileResModel mprofiles_by_ProfileID;
    @SerializedName("failedsubscription_by_CustomerID")
    @Expose
    private FailedSubscriptionModel mFailedSubscription_by_CustomerID;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public FailedSubscriptionModel getFailedSubscription_by_CustomerID() {
        return mFailedSubscription_by_CustomerID;
    }

    public void setFailedSubscription_by_CustomerID(FailedSubscriptionModel mFailedSubscription_by_CustomerID) {
        this.mFailedSubscription_by_CustomerID = mFailedSubscription_by_CustomerID;
    }

    public ProfileResModel getMprofiles_by_ProfileID() {
        return mprofiles_by_ProfileID;
    }

    public void setMprofiles_by_ProfileID(ProfileResModel mprofiles_by_ProfileID) {
        this.mprofiles_by_ProfileID = mprofiles_by_ProfileID;
    }

    public int getProfileID() {
        return ProfileID;
    }

    public void setProfileID(int profileID) {
        ProfileID = profileID;
    }

    public String getSubscriptionID() {
        return SubscriptionID;
    }

    public void setSubscriptionID(String subscriptionID) {
        SubscriptionID = subscriptionID;
    }

    public String getPlanID() {
        return PlanID;
    }

    public void setPlanID(String planID) {
        PlanID = planID;
    }

    public int getSubscriptionStatus() {
        return SubscriptionStatus;
    }

    public void setSubscriptionStatus(int subscriptionStatus) {
        SubscriptionStatus = subscriptionStatus;
    }

    public String getPromoterID() {
        return PromoterID;
    }

    public void setPromoterID(String promoterID) {
        PromoterID = promoterID;
    }

    public String getPromoterType() {
        return PromoterType;
    }

    public void setPromoterType(String promoterType) {
        PromoterType = promoterType;
    }


    public String getPurchaseToken() {
        return PurchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        PurchaseToken = purchaseToken;
    }

    public String getPurchasedDate() {
        return PurchasedDate;
    }

    public void setPurchasedDate(String purchasedDate) {
        PurchasedDate = purchasedDate;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
