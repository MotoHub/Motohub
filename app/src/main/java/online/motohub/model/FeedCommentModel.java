package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class FeedCommentModel implements Serializable {

    public static final String COMMENT_ID = "CommentID";
    public static final String COMMENT_LIST = "CommentList";
    public static final String COMMENT_LIKES_BY_COMMENT_ID = "commentlikes_by_CommentID";
    public static final String COMMENT_REPLY_BY_COMMENT_ID = "commentreply_by_CommentID";
    public static final String PROFILES_BY_PROFILE_ID = "profiles_by_ProfileID";

    @SerializedName("ID")
    @Expose
    private int mId;

    @SerializedName("Comment")
    @Expose
    private String mComment;

    @SerializedName("CommentImages")
    @Expose
    private String mCommentImages;

    @SerializedName("PostID")
    @Expose
    private int mPostId;

    @SerializedName("ProfileID")
    @Expose
    private int mProfileId;

    @SerializedName("CreateTime")
    @Expose
    private String mCreateTime;

    @SerializedName("CommentTaggedUserNames")
    @Expose
    private String mCommentTaggedUserNames;

    @SerializedName("CommentTaggedUserID")
    @Expose
    private String mCommentTaggedUserID;

    @SerializedName("profiles_by_ProfileID")
    @Expose
    private ProfileResModel mProfilesByProfileID;
    @SerializedName("commentreply_by_CommentID")
    @Expose
    private ArrayList<FeedCommentReplyModel> mFeedCommentReplyModel;
    @SerializedName("commentlikes_by_CommentID")
    @Expose
    private ArrayList<FeedCommentLikeModel> mFeedCommentLikeModel;
    @SerializedName("resource")
    @Expose
    private ArrayList<FeedCommentModel> mResource;

    public String getCommentTaggedUserNames() {
        if (mCommentTaggedUserNames == null)
            mCommentTaggedUserNames = "";
        return mCommentTaggedUserNames;
    }

    public void setCommentTaggedUserNames(String mCommentTaggedUserNames) {
        this.mCommentTaggedUserNames = mCommentTaggedUserNames;
    }

    public String getCommentTaggedUserID() {
        if (mCommentTaggedUserID == null)
            mCommentTaggedUserID = "";
        return mCommentTaggedUserID;
    }

    public void setCommentTaggedUserID(String mCommentTaggedUserID) {
        this.mCommentTaggedUserID = mCommentTaggedUserID;
    }

    public ArrayList<FeedCommentModel> getResource() {
        return mResource;
    }

    public void setResource(ArrayList<FeedCommentModel> resource) {
        this.mResource = resource;
    }


    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getmComment() {
        if (mComment == null)
            mComment = "";
        return mComment;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

    public int getmPostId() {
        return mPostId;
    }

    public void setmPostId(int mPostId) {
        this.mPostId = mPostId;
    }

    public int getmProfileId() {
        return mProfileId;
    }

    public void setmProfileId(int mProfileId) {
        this.mProfileId = mProfileId;
    }

    public String getmCreateTime() {
        return mCreateTime;
    }

    public void setmCreateTime(String mCreateTime) {
        this.mCreateTime = mCreateTime;
    }

    public ProfileResModel getProfiles_by_ProfileID() {
        return mProfilesByProfileID;
    }

    public void setProfiles_by_ProfileID(ProfileResModel profiles_by_ProfileID) {
        this.mProfilesByProfileID = profiles_by_ProfileID;
    }

    public ArrayList<FeedCommentReplyModel> getFeedCommentReplyModel() {
        if (mFeedCommentReplyModel == null)
            mFeedCommentReplyModel = new ArrayList<>();
        return mFeedCommentReplyModel;
    }

    public void setFeedCommentReplyModel(ArrayList<FeedCommentReplyModel> mFeedCommentReplyModel) {
        this.mFeedCommentReplyModel = mFeedCommentReplyModel;
    }

    public ArrayList<FeedCommentLikeModel> getFeedCommentLikeModel() {
        if (mFeedCommentLikeModel == null)
            mFeedCommentLikeModel = new ArrayList<>();
        return mFeedCommentLikeModel;
    }

    public void setFeedCommentLikeModel(ArrayList<FeedCommentLikeModel> mFeedCommentLikeModel) {
        this.mFeedCommentLikeModel = mFeedCommentLikeModel;
    }

    public String getCommentImages() {
        if (mCommentImages == null)
            mCommentImages = "";
        return mCommentImages;
    }

    public void setCommentImages(String mCommentImages) {
        this.mCommentImages = mCommentImages;
    }
}
