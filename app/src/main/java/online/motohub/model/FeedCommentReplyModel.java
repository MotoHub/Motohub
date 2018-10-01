package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import online.motohub.model.promoter_club_news_media.PromotersResModel;

/**
 * Created by pyd10 on 10/4/2017.
 */

public class FeedCommentReplyModel implements Serializable {
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

    @SerializedName("resource")
    @Expose
    private ArrayList<FeedCommentReplyModel> mResource;

    @SerializedName("profiles_by_ProfileID")
    @Expose
    private ProfileResModel mProfilesByProfileID;

    @SerializedName("replylike_by_ReplyID")
    @Expose
    private ArrayList<ReplyLikeModel> mReplyLikeByReplyID;

    @SerializedName("postcomments_by_CommentID")
    @Expose
    private FeedCommentModel mPostCommentsByCommentID;


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
        if(mReplyText == null)
            mReplyText = "";
        return mReplyText;
    }

    public void setReplyText(String mReplyText) {
        this.mReplyText = mReplyText;
    }

    public ArrayList<FeedCommentReplyModel> getResource() {
        return mResource;
    }

    public void setResource(ArrayList<FeedCommentReplyModel> mResource) {
        this.mResource = mResource;
    }

    public ProfileResModel getProfilesByProfileID() {
        return mProfilesByProfileID;
    }

    public void setProfilesByProfileID(ProfileResModel mProfilesByProfileID) {
        this.mProfilesByProfileID = mProfilesByProfileID;
    }

    public ArrayList<ReplyLikeModel> getReplyLikeByReplyID() {
        if(mReplyLikeByReplyID==null)
            mReplyLikeByReplyID = new ArrayList<>();
        return mReplyLikeByReplyID;
    }

    public void setReplyLikeByReplyID(ArrayList<ReplyLikeModel> mReplyLikeByReplyID) {
        this.mReplyLikeByReplyID = mReplyLikeByReplyID;
    }

    public String getReplyImages() {
        if(mReplyImages == null)
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
        if(mUserType == null)
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

    public FeedCommentModel getPostCommentsByCommentID() {
        return mPostCommentsByCommentID;
    }

    public void setPostCommentsByCommentID(FeedCommentModel mPostCommentsByCommentID) {
        this.mPostCommentsByCommentID = mPostCommentsByCommentID;
    }
}
