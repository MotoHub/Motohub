package online.motohub.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PurchasedAddOnModel implements Serializable {

    public static final String ADDON_PRICE = "addon_price";
    public static final String EVENT_ID = "event_id";
    public static final String PROFILE_ID = "profile_id";
    public static final String ADDON_ID = "addon_id";
    public static final String ADDON_DESC = "addon_desc";

    @SerializedName("ID")
    private int mID;

    @SerializedName("addon_price")
    private int mAddOnPrice;

    @SerializedName("event_id")
    private int mEventID;

    @SerializedName("profile_id")
    private int mProfileID;

    @SerializedName("addon_id")
    private int mAddOnID;

    @SerializedName("addon_desc")
    private String mAddOnDesc;

    public int getID() {
        return mID;
    }

    public void setID(int mID) {
        this.mID = mID;
    }

    public int getAddOnPrice() {
        return mAddOnPrice;
    }

    public void setAddOnPrice(int mAddOnPrice) {
        this.mAddOnPrice = mAddOnPrice;
    }

    public int getEventID() {
        return mEventID;
    }

    public void setEventID(int mEventID) {
        this.mEventID = mEventID;
    }

    public int getProfileID() {
        return mProfileID;
    }

    public void setProfileID(int mProfileID) {
        this.mProfileID = mProfileID;
    }

    public int getAddOnID() {
        return mAddOnID;
    }

    public void setAddOnID(int mAddOnID) {
        this.mAddOnID = mAddOnID;
    }

    public String getAddOnDesc() {
        return mAddOnDesc;
    }

    public void setAddOnDesc(String mAddOnDesc) {
        this.mAddOnDesc = mAddOnDesc;
    }
}
