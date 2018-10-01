package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pyd10 on 3/8/2018.
 */

public class VideoCommentsModel implements Serializable {

    public static final String COMMENT_ID = "CommentID";
    public static final String COMMENT_LIST = "CommentList";
    public static final String COMMENT_LIKES_BY_COMMENT_ID = "videocommentlike_by_CommentID";
    public static final String COMMENT_REPLY_BY_COMMENT_ID = "videocommentreply_by_CommentID";
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

    @SerializedName("VideoID")
    @Expose
    private int mVideoID;

    @SerializedName("ProfileID")
    @Expose
    private int mProfileId;

    @SerializedName("CreateTime")
    @Expose
    private String mCreateTime;

    @SerializedName("profiles_by_ProfileID")
    @Expose
    private ProfileResModel mProfilesByProfileID;

    @SerializedName("videocommentreply_by_CommentID")
    @Expose
    private ArrayList<VideoCommentReplyModel> mFeedCommentReplyModel;

    @SerializedName("videocommentlike_by_CommentID")
    @Expose
    private ArrayList<VideoCommentLikeModel> mFeedCommentLikeModel;

    @SerializedName("resource")
    @Expose
    private ArrayList<VideoCommentsModel> mResource;

    public ArrayList<VideoCommentsModel> getResource() {
        return mResource;
    }

    public void setResource(ArrayList<VideoCommentsModel> resource) {
        if(mResource == null)
            mResource = new ArrayList<>();
        this.mResource = resource;
    }


    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getmComment() {
        if(mComment == null)
            mComment = "";
        return mComment;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

    public int getmPostId() {
        return mVideoID;
    }

    public void setmPostId(int mPostId) {
        this.mVideoID = mPostId;
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

    public ArrayList<VideoCommentReplyModel> getFeedCommentReplyModel() {
        if(mFeedCommentReplyModel==null)
            mFeedCommentReplyModel = new ArrayList<>();
        return mFeedCommentReplyModel;
    }

    public void setFeedCommentReplyModel(ArrayList<VideoCommentReplyModel> mFeedCommentReplyModel) {
        this.mFeedCommentReplyModel = mFeedCommentReplyModel;
    }

    public ArrayList<VideoCommentLikeModel> getFeedCommentLikeModel() {
        if(mFeedCommentLikeModel == null)
            mFeedCommentLikeModel = new ArrayList<>();
        return mFeedCommentLikeModel;
    }

    public void setFeedCommentLikeModel(ArrayList<VideoCommentLikeModel> mFeedCommentLikeModel) {
        this.mFeedCommentLikeModel = mFeedCommentLikeModel;
    }

    public String getCommentImages() {
        if(mCommentImages == null)
            mCommentImages = "";
        return mCommentImages;
    }

    public void setCommentImages(String mCommentImages) {
        this.mCommentImages = mCommentImages;
    }
}
