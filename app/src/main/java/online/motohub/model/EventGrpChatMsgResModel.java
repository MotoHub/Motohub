package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class EventGrpChatMsgResModel {

    @SerializedName("ID")
    @Expose
    private Integer mID;

    @SerializedName("EventID")
    @Expose
    private Integer mEventID;

    @SerializedName("Message")
    @Expose
    private String mMessage;

    @SerializedName("user_type")
    @Expose
    private String mUserType;

    @SerializedName("SenderUserID")
    @Expose
    private Integer mSenderUserID;

    @SerializedName("SenderProfileID")
    @Expose
    private Integer mSenderProfileID;

    @SerializedName("CreatedAt")
    @Expose
    private String mCreatedAt;

    @SerializedName("sender_name")
    @Expose
    private String mSenderName;

    @SerializedName("msg_type")
    @Expose
    private String msgType;

    @SerializedName("photo_message")
    @Expose
    private String photoMessage;

    @SerializedName("profiles_by_SenderProfileID")
    @Expose
    private ProfileResModel mProfilesBySenderProfileID;

    @SerializedName("promoter_by_SenderUserID")
    @Expose
    private PromotersResModel mPromoterBySenderUserID;

    @SerializedName("IsChecked")
    @Expose
    private Boolean IsDateVisible = false;

    @SerializedName("IsRepliedMsg")
    @Expose
    private Boolean mIsRepliedMsg;

    @SerializedName("ReplyUserProfileID")
    @Expose
    int mReplyUserProfileID;

    @SerializedName("Profiles By ReplyUserProfileID")
    @Expose
    private ProfileResModel mProfilesByReplyUserProfileID;

    @SerializedName("ReplyUserName")
    @Expose
    private String mReplyUserName;

    @SerializedName("ReplyMessage")
    @Expose
    private String mReplyMessage;

    @SerializedName("RepiedMsgID")
    @Expose
    private int mRepliedMsgID;

    @SerializedName("ReplyImage")
    @Expose
    private String mReplyImage;

    private Boolean IsSelectedMsg = false;

    public Boolean getDateVisible() {
        return IsDateVisible;
    }

    public void setDateVisible(Boolean dateVisible) {
        IsDateVisible = dateVisible;
    }

    public Integer getID() {
        return mID;
    }

    public void setID(Integer id) {
        this.mID = id;
    }

    public Integer getEventID() {
        return mEventID;
    }

    public void setEventID(Integer eventID) {
        this.mEventID = eventID;
    }

    public String getMessage() {
        if (mMessage == null)
            mMessage = "";
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public String getUserType() {
        if (mUserType == null)
            mUserType = "";
        return mUserType;
    }

    public void setUserType(String mUserType) {
        this.mUserType = mUserType;
    }

    public Integer getSenderUserID() {
        return mSenderUserID;
    }

    public void setSenderUserID(Integer senderUserID) {
        this.mSenderUserID = senderUserID;
    }

    public Integer getSenderProfileID() {
        return mSenderProfileID;
    }

    public void setSenderProfileID(Integer senderProfileID) {
        this.mSenderProfileID = senderProfileID;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.mCreatedAt = createdAt;
    }

    public String getSenderName() {
        return mSenderName;
    }

    public void setSenderName(String senderName) {
        this.mSenderName = senderName;
    }

    public ProfileResModel getProfilesBySenderProfileID() {
        return mProfilesBySenderProfileID;
    }

    public void setProfilesBySenderProfileID(ProfileResModel profilesByToProfileID) {
        this.mProfilesBySenderProfileID = profilesByToProfileID;
    }

    public PromotersResModel getPromoterBySenderUserID() {
        return mPromoterBySenderUserID;
    }

    public void setPromoterBySenderUserID(PromotersResModel promoterBySenderUserID) {
        this.mPromoterBySenderUserID = promoterBySenderUserID;
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
    public Boolean getIsSelectedMsg() {
        return IsSelectedMsg;
    }

    public void setIsSelectedMsg(Boolean selectedMsg) {
        IsSelectedMsg = selectedMsg;
    }

    public Boolean getIsRepliedMsg() {
        if(mIsRepliedMsg == null)
            mIsRepliedMsg = false;
        return mIsRepliedMsg;
    }

    public void setIsRepliedMsg(Boolean mIsRepliedMsg) {
        this.mIsRepliedMsg = mIsRepliedMsg;
    }

    public int getReplyUserProfileID() {
        return mReplyUserProfileID;
    }

    public void setReplyUserProfileID(int mReplyUserProfileID) {
        this.mReplyUserProfileID = mReplyUserProfileID;
    }

    public ProfileResModel getProfilesByReplyUserProfileID() {
        return mProfilesByReplyUserProfileID;
    }

    public void setProfilesByReplyUserProfileID(ProfileResModel mProfilesByReplyUserProfileID) {
        this.mProfilesByReplyUserProfileID = mProfilesByReplyUserProfileID;
    }

    public String getReplyUserName() {
        return mReplyUserName;
    }

    public void setReplyUserName(String mReplyUserName) {
        this.mReplyUserName = mReplyUserName;
    }

    public String getReplyMessage() {
        return mReplyMessage;
    }

    public void setReplyMessage(String mReplyMessage) {
        this.mReplyMessage = mReplyMessage;
    }

    public int getRepliedMsgID() {
        return mRepliedMsgID;
    }

    public void setRepliedMsgID(int mRepliedMsgID) {
        this.mRepliedMsgID = mRepliedMsgID;
    }

    public String getReplyImage() {
        if(mReplyImage == null)
            mReplyImage = "";
        return mReplyImage;
    }

    public void setReplyImage(String mReplyImage) {
        this.mReplyImage = mReplyImage;
    }
}
