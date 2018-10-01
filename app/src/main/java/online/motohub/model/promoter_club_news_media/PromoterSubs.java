package online.motohub.model.promoter_club_news_media;

/**
 * Created by pickzy01 on 05/04/2018.
 */

public class PromoterSubs {

    public static final String ID_ = "ID";
    public static final String PURCHASE_TOKEN = "PurchaseToken";
    public static final String PURCHASE_DATE = "PurchasedDate";
    public static final String USER_ID = "UserID";
    public static final String PROMOTER_ID = "PromoterID";
    public static final String PROMOTER_TYPE = "PromoterType";
    public static final String SUBS_STATUS = "SubscriptionStatus";
    public static final String PROFILE_ID = "ProfileID";
    public static final String SUBS_ID = "SubscriptionID";
    public static final String PLAN_ID = "PlanID";
    public static final String TYPE = "type";
	
    private String UserID;
    private String PurchaseToken;
    private String PurchasedDate;
    private int SubscriptionStatus;
    private String ID;
    private String ProfileID;
    private String SubscriptionID;
    private String PlanID;
    private String PromoterID;
    private String PromoterType;

    public String getProfileID() {
        return ProfileID;
    }

    public void setProfileID(String profileID) {
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

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
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

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
