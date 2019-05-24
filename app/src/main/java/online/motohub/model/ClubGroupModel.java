package online.motohub.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ClubGroupModel implements Serializable {

    public static final String CLUB_USER_ID = "ClubUserID";
    public static final String ID = "ID";
    public static final String MEMBER_PROFILE_ID = "MemberProfileID";
    public static final String MEMBER_USER_ID = "MemberUserID";
    public static final String STATUS = "Status";

    @SerializedName("ID")
    private int mID;

    @SerializedName("ClubUserID")
    private int mClubUserID;

    @SerializedName("MemberProfileID")
    private int mMemberProfileID;

    @SerializedName("MemberUserID")
    private int mMemberUserID;

    @SerializedName("Status")
    private int mStatus;

    public int getID() {
        return mID;
    }

    public void setID(int mID) {
        this.mID = mID;
    }

    public int getClubUserID() {
        return mClubUserID;
    }

    public void setClubUserID(int mClubUserID) {
        this.mClubUserID = mClubUserID;
    }

    public int getMemberProfileID() {
        return mMemberProfileID;
    }

    public void setMemberProfileID(int mMemberProfileID) {
        this.mMemberProfileID = mMemberProfileID;
    }

    public int getMemberUserID() {
        return mMemberUserID;
    }

    public void setMemberUserID(int mMemberUserID) {
        this.mMemberUserID = mMemberUserID;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }
}
