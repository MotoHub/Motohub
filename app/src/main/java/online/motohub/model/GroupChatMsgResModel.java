package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupChatMsgResModel {

    @SerializedName("ID")
    @Expose
    private Integer mID;

    @SerializedName("GroupChatRoomID")
    @Expose
    private Integer mGroupChatRoomID;

    @SerializedName("Message")
    @Expose
    private String mMessage;

    @SerializedName("SenderProfileID")
    @Expose
    private Integer mSenderProfileID;

    @SerializedName("SenderUserID")
    @Expose
    private Integer mSenderUserID;

    @SerializedName("CreatedAt")
    @Expose
    private String mCreatedAt;

    @SerializedName("IsChecked")
    @Expose
    private Boolean IsDateVisible=false;

    @SerializedName("msg_type")
    @Expose
    private String msgType;

    @SerializedName("photo_message")
    @Expose
    private String photoMessage;

    public Boolean getDateVisible() {
        return IsDateVisible;
    }

    public void setDateVisible(Boolean dateVisible) {
        IsDateVisible = dateVisible;
    }

    @SerializedName("profiles_by_SenderProfileID")
    @Expose
    private ProfileResModel mProfilesBySenderProfileID;

    public Integer getID() {
        return mID;
    }

    public void setID(Integer id) {
        this.mID = id;
    }

    public Integer getGroupChatRoomID() {
        return mGroupChatRoomID;
    }

    public void setGroupChatRoomID(Integer groupChatRoomID) {
        this.mGroupChatRoomID = groupChatRoomID;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public Integer getSenderProfileID() {
        return mSenderProfileID;
    }

    public void setSenderProfileID(Integer senderProfileID) {
        this.mSenderProfileID = senderProfileID;
    }

    public Integer getSenderUserID() {
        return mSenderUserID;
    }

    public void setSenderUserID(Integer senderUserID) {
        this.mSenderUserID = senderUserID;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.mCreatedAt = createdAt;
    }

    public ProfileResModel getProfilesBySenderProfileID() {
        return mProfilesBySenderProfileID;
    }

    public void setProfilesBySenderProfileID(ProfileResModel profilesBySenderProfileID) {
        this.mProfilesBySenderProfileID = profilesBySenderProfileID;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getPhotoMessage() {
        if(photoMessage == null)
            photoMessage = "";
        return photoMessage;
    }

    public void setPhotoMessage(String photoMessage) {
        this.photoMessage = photoMessage;
    }

}
