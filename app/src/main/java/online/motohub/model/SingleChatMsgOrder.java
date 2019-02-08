package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SingleChatMsgOrder implements Serializable {

    @SerializedName("ID")
    @Expose
    private int mID;

    @SerializedName("FromProfileID")
    @Expose
    private int mFromProfileID;

    @SerializedName("ToProfileID")
    @Expose
    private int mToProfileID;

    @SerializedName("FromUserID")
    @Expose
    private int mFromUserID;

    @SerializedName("ToUserID")
    @Expose
    private int mToUserID;

    @SerializedName("Message")
    @Expose
    private String mMessage;

    @SerializedName("CreatedAt")
    @Expose
    private String mCreatedAt;

    @SerializedName("messagestatus")
    @Expose
    private boolean mmessagestatus;

    @SerializedName("msg_type")
    @Expose
    private String mmsg_type;

    @SerializedName("photo_message")
    @Expose
    private String mphoto_message;

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

    public int getmFromUserID() {
        return mFromUserID;
    }

    public void setmFromUserID(int mFromUserID) {
        this.mFromUserID = mFromUserID;
    }

    public int getmToUserID() {
        return mToUserID;
    }

    public void setmToUserID(int mToUserID) {
        this.mToUserID = mToUserID;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getmCreatedAt() {
        return mCreatedAt;
    }

    public void setmCreatedAt(String mCreatedAt) {
        this.mCreatedAt = mCreatedAt;
    }

    public boolean isMmessagestatus() {
        return mmessagestatus;
    }

    public void setMmessagestatus(boolean mmessagestatus) {
        this.mmessagestatus = mmessagestatus;
    }

    public String getMmsg_type() {
        return mmsg_type;
    }

    public void setMmsg_type(String mmsg_type) {
        this.mmsg_type = mmsg_type;
    }

    public String getMphoto_message() {
        return mphoto_message;
    }

    public void setMphoto_message(String mphoto_message) {
        this.mphoto_message = mphoto_message;
    }
}