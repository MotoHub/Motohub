package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import online.motohub.model.promoter_club_news_media.PromotersResModel;

/**
 * Created by pyd10 on 3/9/2018.
 */

public class VideoCommentReplyModel implements Serializable {
    public static final String REPLY_LIST = "ReplyList";
    @SerializedName("ID")
    @Expose
    private int mID;

    @SerializedName("ProfileID")
    @Expose
    private int mProfileID;

    @SerializedName("CommentID")
    @Expose
    private int mCommentID;

    @SerializedName("ReplyText")
    @Expose
    private String mReplyText;

    @SerializedName("ReplyImages")
    @Expose
    private String mReplyImages;

    @SerializedName("CreateTime")
    @Expose
    private String mCreateTime;

    @SerializedName("user_type")
    @Expose
    private String mUserType;

    @SerializedName("promoter_by_ProfileID")
    @Expose
    private PromotersResModel mPromoterByProfileID;


    @SerializedName("ReplyTaggedUserNames")
    @Expose
    private String mReplyTaggedUserNames;
    @SerializedName("ReplyTaggedUserID")
    @Expose
    private String mReplyTaggedUserIDs;
    @SerializedName("resource")
    @Expose
    private ArrayList<VideoCommentReplyModel> mResource;
    @SerializedName("profiles_by_ProfileID")
    @Expose
    private ProfileResModel mProfilesByProfileID;
    @SerializedName("videoreplylike_by_ReplyID")
    @Expose
    private ArrayList<VideoReplyLikeModel> mReplyLikeByReplyID;
    @SerializedName("videocomments_by_CommentID")
    @Expose
    private VideoCommentsModel mVideoCommentsByVideoID;

    public String getReplyTaggedUserNames() {
        if (mReplyTaggedUserNames == null)
            mReplyTaggedUserNames = "";
        return mReplyTaggedUserNames;
    }

    public void setReplyTaggedUserNames(String mReplyTaggedUserNames) {
        this.mReplyTaggedUserNames = mReplyTaggedUserNames;
    }

    public String getReplyTaggedUserIDs() {
        return mReplyTaggedUserIDs;
    }

    public void setReplyTaggedUserIDs(String mReplyTaggedUserIDs) {
        this.mReplyTaggedUserIDs = mReplyTaggedUserIDs;
    }

    public int getID() {
        return mID;
    }

    public void setID(int mID) {
        this.mID = mID;
    }

    public int getProfileID() {
        return mProfileID;
    }

    public void setProfileID(int mProfileID) {
        this.mProfileID = mProfileID;
    }

    public int getCommentID() {
        return mCommentID;
    }

    public void setCommentID(int mCommentID) {
        this.mCommentID = mCommentID;
    }

    public String getReplyText() {
        if (mReplyText == null)
            mReplyText = "";
        return mReplyText;
    }

    public void setReplyText(String mReplyText) {
        this.mReplyText = mReplyText;
    }

    public ArrayList<VideoCommentReplyModel> getResource() {
        return mResource;
    }

    public void setResource(ArrayList<VideoCommentReplyModel> mResource) {
        this.mResource = mResource;
    }

    public ProfileResModel getProfilesByProfileID() {
        return mProfilesByProfileID;
    }

    public void setProfilesByProfileID(ProfileResModel mProfilesByProfileID) {
        this.mProfilesByProfileID = mProfilesByProfileID;
    }

    public ArrayList<VideoReplyLikeModel> getReplyLikeByReplyID() {
        if (mReplyLikeByReplyID == null)
            mReplyLikeByReplyID = new ArrayList<>();
        return mReplyLikeByReplyID;
    }

    public void setReplyLikeByReplyID(ArrayList<VideoReplyLikeModel> mReplyLikeByReplyID) {
        this.mReplyLikeByReplyID = mReplyLikeByReplyID;
    }

    public String getReplyImages() {
        if (mReplyImages == null)
            mReplyImages = "";
        return mReplyImages;
    }

    public void setReplyImages(String mReplyImages) {
        this.mReplyImages = mReplyImages;
    }

    public String getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(String mCreateTime) {
        this.mCreateTime = mCreateTime;
    }

    public String getUserType() {
        if (mUserType == null)
            mUserType = "";
        return mUserType;
    }

    public void setUserType(String mUserType) {
        this.mUserType = mUserType;
    }

    public PromotersResModel getPromoterByProfileID() {
        return mPromoterByProfileID;
    }

    public void setPromoterByProfileID(PromotersResModel mPromoterByProfileID) {
        this.mPromoterByProfileID = mPromoterByProfileID;
    }


    public VideoCommentsModel getVideoCommentsByVideoID() {
        return mVideoCommentsByVideoID;
    }

    public void setVideoCommentsByVideoID(VideoCommentsModel mVideoCommentsByVideoID) {
        this.mVideoCommentsByVideoID = mVideoCommentsByVideoID;
    }
}