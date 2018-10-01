package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;


public class VehicleInfoLikeModel implements Serializable {

    public static final String LIKED_PROFILE_ID = "LikedProfileID";
    public static final String WHO_LIKED_PROFILE_ID = "WhoLikedProfileID";
    public static final String PROFILES_BY_WHO_LIKED_PROFILE_ID = "profiles_by_WhoLikedProfileID";

    @SerializedName("LikedProfileID")
    @Expose
    private int mLikedProfiledID;

    @SerializedName("WhoLikedProfileID")
    @Expose
    private int mWhoLikedProfileID;

    @SerializedName("profiles_by_WhoLikedProfileID")
    @Expose
    private ProfileResModel mProfilesByWhoLikedProfileID;

    @SerializedName("ID")
    @Expose
    private int mID;

    @SerializedName("resource")
    @Expose
    private ArrayList<VehicleInfoLikeModel> mResource;

    public ArrayList<VehicleInfoLikeModel> getResource() {
        if (mResource == null)
            mResource = new ArrayList<>();
        return mResource;
    }

    public int getLikedProfiledID() {
        return mLikedProfiledID;
    }

    public void setLikedProfiledID(int mLikedProfiledID) {
        this.mLikedProfiledID = mLikedProfiledID;
    }

    public int getWhoLikedProfileID() {
        return mWhoLikedProfileID;
    }

    public void setWhoLikedProfileID(int mWhoLikedProfileID) {
        this.mWhoLikedProfileID = mWhoLikedProfileID;
    }

    public int getID() {
        return mID;
    }

    public void setID(int mID) {
        this.mID = mID;
    }

    public ProfileResModel getProfilesByWhoLikedProfileID() {
        return mProfilesByWhoLikedProfileID;
    }

    public void setProfilesByWhoLikedProfileID(ProfileResModel mProfilesByWhoLikedProfileID) {
        this.mProfilesByWhoLikedProfileID = mProfilesByWhoLikedProfileID;
    }
}
