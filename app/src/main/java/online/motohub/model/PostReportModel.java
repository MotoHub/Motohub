package online.motohub.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PostReportModel implements Serializable {

    @SerializedName("id")
    private int mID;

    @SerializedName("PostID")
    private String mPostID;

    @SerializedName("ProfileID")
    private int mProfileID;

    @SerializedName("ReportString")
    private String mReportString;

    @SerializedName("CreatedAt")
    private String mCreatedAt;

    @SerializedName("Status")
    private String mStatus;

    @SerializedName("UserID")
    private String mUserID;


    public int getmID() {
        return mID;
    }

    public void setmID(int mID) {
        this.mID = mID;
    }

    public String getmPostID() {
        return mPostID;
    }

    public void setmPostID(String mPostID) {
        this.mPostID = mPostID;
    }

    public int getmProfileID() {
        return mProfileID;
    }

    public void setmProfileID(int mProfileID) {
        this.mProfileID = mProfileID;
    }

    public String getmReportString() {
        return mReportString;
    }

    public void setmReportString(String mReportString) {
        this.mReportString = mReportString;
    }

    public String getmCreatedAt() {
        return mCreatedAt;
    }

    public void setmCreatedAt(String mCreatedAt) {
        this.mCreatedAt = mCreatedAt;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmUserID() {
        return mUserID;
    }

    public void setmUserID(String mUserID) {
        this.mUserID = mUserID;
    }
}
