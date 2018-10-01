package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;


public class FeedCommentLikeModel implements Serializable {

    @SerializedName("ID")
    private int mID;

    @SerializedName("CommentID")
    private int mCommentID;

    @SerializedName("ProfileID")
    private int mProfileID;

    @SerializedName("profiles_by_ProfileID")
    private ProfileResModel mProfileByProfileID;

    @SerializedName("resource")
    @Expose
    private ArrayList<FeedCommentLikeModel> mResource;

    public ArrayList<FeedCommentLikeModel> getResource() {
        return mResource;
    }

    public void setResource(ArrayList<FeedCommentLikeModel> resource) {
        this.mResource = resource;
    }

    public int getID() {
        return mID;
    }

    public void setID(int mID) {
        this.mID = mID;
    }

    public int getCommentID() {
        return mCommentID;
    }

    public void setCommentID(int mCommentID) {
        this.mCommentID = mCommentID;
    }

    public int getProfileID() {
        return mProfileID;
    }

    public void setProfileID(int mProfileID) {
        this.mProfileID = mProfileID;
    }

    public ProfileResModel getProfileByProfileID() {
        return mProfileByProfileID;
    }

    public void setProfileByProfileID(ProfileResModel mProfileResModel) {
        this.mProfileByProfileID = mProfileResModel;
    }
}
