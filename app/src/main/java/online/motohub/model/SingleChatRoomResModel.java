package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SingleChatRoomResModel implements Serializable {

    @SerializedName("ID")
    @Expose
    private Integer mID;

    @SerializedName("FromProfileID")
    @Expose
    private Integer mFromProfileID;

    @SerializedName("ToProfileID")
    @Expose
    private Integer mToProfileID;

    @SerializedName("FromToRelation")
    @Expose
    private String mFromToRelation;

    @SerializedName("profiles_by_ToProfileID")
    @Expose
    private ProfileResModel mProfilesByToProfileID;

    @SerializedName("DeleteFlag")
    @Expose
    private boolean mDeleteFlag;

    public boolean getmDeleteFlag() {
        return mDeleteFlag;
    }

    public void setDeleteFlag(boolean mDeleteFlag) {
        this.mDeleteFlag = mDeleteFlag;
    }

    public Integer getID() {
        return mID;
    }

    public void setID(Integer iD) {
        this.mID = iD;
    }

    public Integer getFromProfileID() {
        return mFromProfileID;
    }

    public void setFromProfileID(Integer fromProfileID) {
        this.mFromProfileID = fromProfileID;
    }

    public Integer getToProfileID() {
        return mToProfileID;
    }

    public void setToProfileID(Integer toProfileID) {
        this.mToProfileID = toProfileID;
    }

    public String getFromToRelation() {
        return mFromToRelation;
    }

    public void setFromToRelation(String fromToRelation) {
        this.mFromToRelation = fromToRelation;
    }

    public ProfileResModel getProfilesByToProfileID() {
        return mProfilesByToProfileID;
    }

    public void setProfilesByToProfileID(ProfileResModel profilesByToProfileID) {
        this.mProfilesByToProfileID = profilesByToProfileID;
    }

}


