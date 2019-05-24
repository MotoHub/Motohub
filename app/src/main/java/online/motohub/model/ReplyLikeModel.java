package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;


public class ReplyLikeModel implements Serializable {

    @SerializedName("profiles_by_ProfileID")
    ProfileResModel mProfilesByProfileID;
    @SerializedName("ID")
    private int mID;
    @SerializedName("ReplyID")
    private int mReplyID;
    @SerializedName("PostCommentID")
    private int mPostCommentID;
    @SerializedName("ProfileID")
    private int mProfileID;
    @SerializedName("resource")
    @Expose
    private ArrayList<ReplyLikeModel> mResource;

    public ArrayList<ReplyLikeModel> getResource() {
        if (mResource == null)
            mResource = new ArrayList<>();
        return mResource;
    }

    public int getID() {
        return mID;
    }

    public void setID(int mID) {
        this.mID = mID;
    }

    public int getReplyID() {
        return mReplyID;
    }

    public void setReplyID(int mReplyID) {
        this.mReplyID = mReplyID;
    }

    public int getPostCommentID() {
        return mPostCommentID;
    }

    public void setPostCommentID(int mPostCommentID) {
        this.mPostCommentID = mPostCommentID;
    }

    public int getProfileID() {
        return mProfileID;
    }

    public void setProfileID(int mProfileID) {
        this.mProfileID = mProfileID;
    }

    public ProfileResModel getProfilesByProfileID() {
        return mProfilesByProfileID;
    }

    public void setProfilesByProfileID(ProfileResModel mProfilesByProfileID) {
        this.mProfilesByProfileID = mProfilesByProfileID;
    }
}
