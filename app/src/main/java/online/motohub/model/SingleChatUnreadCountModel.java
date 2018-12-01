package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SingleChatUnreadCountModel implements Serializable{

    @SerializedName("Count")
    @Expose
    private int mCount;

    @SerializedName("ID")
    @Expose
    private int mID;

    @SerializedName("FromProfileID")
    @Expose
    private int mFromProfileID;

    @SerializedName("ToProfileID")
    @Expose
    private int mToProfileID;

    public int getmCount() {
        return mCount;
    }

    public void setmCount(int mCount) {
        this.mCount = mCount;
    }

    public int getmID() {
        return mID;
    }

    public void setmID(int mID) {
        this.mID = mID;
    }

    public int getmFromProfileID() {
        return mFromProfileID;
    }

    public void setmFromProfileID(int mFromProfileID) {
        this.mFromProfileID = mFromProfileID;
    }

    public int getmToProfileID() {
        return mToProfileID;
    }

    public void setmToProfileID(int mToProfileID) {
        this.mToProfileID = mToProfileID;
    }

}