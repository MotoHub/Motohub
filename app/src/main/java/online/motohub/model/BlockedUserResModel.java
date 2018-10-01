package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class BlockedUserResModel implements Serializable {

    @SerializedName("ProfileID")
    @Expose
    private int mProfileID;

    @SerializedName("BlockedProfileID")
    @Expose
    private int mBlockedProfileID;

    @SerializedName("ID")
    @Expose
    private int mID;

    @SerializedName("UniqueRelation")
    @Expose
    private String mUniqueRelation;

    @SerializedName("profiles_by_BlockedProfileID")
    @Expose
    private ProfileResModel mProfilesByBlockedProfileID;

    public int getProfileID() {
        return mProfileID;
    }

    public void setProfileID(int profileID) {
        this.mProfileID = profileID;
    }

    public int getBlockedProfileID() {
        return mBlockedProfileID;
    }

    public void setBlockedProfileID(int blockedProfileID) {
        this.mBlockedProfileID = blockedProfileID;
    }

    public int getID() {
        return mID;
    }

    public void setID(int iD) {
        this.mID = iD;
    }

    public String getUniqueRelation() {
        return mUniqueRelation;
    }

    public void setUniqueRelation(String uniqueRelation) {
        this.mUniqueRelation = uniqueRelation;
    }

    public ProfileResModel getProfilesByBlockedProfileID() {
        return mProfilesByBlockedProfileID;
    }

    public void setProfilesByBlockedProfileID(ProfileResModel profilesByBlockedProfileID) {
        this.mProfilesByBlockedProfileID = profilesByBlockedProfileID;
    }

}
