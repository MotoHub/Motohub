package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pyd10 on 3/9/2018.
 */

public class VideoCommentLikeModel implements Serializable {

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
    private ArrayList<VideoCommentLikeModel> mResource;

    public ArrayList<VideoCommentLikeModel> getResource() {
        return mResource;
    }

    public void setResource(ArrayList<VideoCommentLikeModel> resource) {
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
