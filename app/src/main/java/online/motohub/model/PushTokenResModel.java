package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PushTokenResModel {

    @SerializedName("ID")
    @Expose
    private int mID;

    @SerializedName("UserID")
    @Expose
    private int mUserID;

    @SerializedName("Token")
    @Expose
    private String mRegistrationToken;

    @SerializedName("Platform")
    @Expose
    private String platform;

    public int getID() {
        return mID;
    }

    public void setID(int iD) {
        this.mID = iD;
    }

    public int getUserID() {
        return mUserID;
    }

    public void setUserID(int mUserID) {
        this.mUserID = mUserID;
    }

    public String getRegistrationtoken() {
        return mRegistrationToken;
    }

    public void setRegistrationtoken(String mRegistrationtoken) {
        this.mRegistrationToken = mRegistrationtoken;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

}
