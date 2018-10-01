package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GroupChatRoomResModel implements Serializable {

    @SerializedName("ID")
    @Expose
    private Integer mID;

    @SerializedName("GroupName")
    @Expose
    private String mGroupName;

    @SerializedName("CreatedByProfileID")
    @Expose
    private Integer mCreatedByProfileID;

    @SerializedName("GroupPicture")
    @Expose
    private String mGroupPicture;

    @SerializedName("GroupMemProfileID")
    @Expose
    private String mGroupMemProfileID;

    @SerializedName("GroupMemUserID")
    @Expose
    private String mGroupMemUserID;

    public Integer getID() {
        return mID;
    }

    public void setID(Integer id) {
        this.mID = id;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String groupName) {
        this.mGroupName = groupName;
    }

    public Integer getCreatedByProfileID() {
        return mCreatedByProfileID;
    }

    public void setCreatedByProfileID(Integer createdByProfileID) {
        this.mCreatedByProfileID = createdByProfileID;
    }

    public String getGroupPicture() {
        return mGroupPicture;
    }

    public void setGroupPicture(String groupPicture) {
        this.mGroupPicture = groupPicture;
    }

    public String getGroupMemProfileID() {
        return mGroupMemProfileID;
    }

    public void setGroupMemProfileID(String groupMemProfileID) {
        this.mGroupMemProfileID = groupMemProfileID;
    }

    public String getGroupMemUserID() {
        return mGroupMemUserID;
    }

    public void setGroupMemUserID(String groupMemUserID) {
        this.mGroupMemUserID = groupMemUserID;
    }

}
