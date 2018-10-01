package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SingleChatMsgResModel {

    @SerializedName("ID")
    @Expose
    private int mID;

    @SerializedName("FromProfileID")
    @Expose
    private int mFromProfileID;

    @SerializedName("ToProfileID")
    @Expose
    private int mToProfileID;

    @SerializedName("Message")
    @Expose
    private String mMessage;

    @SerializedName("FromUserID")
    @Expose
    private int mFromUserID;

    @SerializedName("ToUserID")
    @Expose
    private int mToUserID;

    @SerializedName("CreatedAt")
    @Expose
    private String mCreatedAt;



    @SerializedName("msg_type")
    @Expose
    private String msgType;

    @SerializedName("photo_message")
    @Expose
    private String photoMessage;

    @SerializedName("IsChecked")
    @Expose
    private Boolean IsDateVisible = false;

    public Boolean getIsDateVisible() {
        return IsDateVisible;
    }

    public void setIsDateVisible(Boolean checked) {
        IsDateVisible = checked;
    }

    public int getID() {
        return mID;
    }

    public void setID(int iD) {
        this.mID = iD;
    }

    public int getFromProfileID() {
        return mFromProfileID;
    }

    public void setFromProfileID(int fromProfileID) {
        this.mFromProfileID = fromProfileID;
    }

    public int getToProfileID() {
        return mToProfileID;
    }

    public void setToProfileID(int toProfileID) {
        this.mToProfileID = toProfileID;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public int getFromUserID() {
        return mFromUserID;
    }

    public void setFromUserID(int fromUserID) {
        this.mFromUserID = fromUserID;
    }

    public int getToUserID() {
        return mToUserID;
    }

    public void setToUserID(int toUserID) {
        this.mToUserID = toUserID;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.mCreatedAt = createdAt;
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
