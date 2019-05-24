package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class FeedLikesModel implements Serializable {

    @SerializedName("ID")
    @Expose
    private int mID;

    @SerializedName("PostID")
    @Expose
    private int mPostID;

    @SerializedName("ProfileID")
    private int mProfileID;

    @SerializedName("profiles_by_ProfileID")
    @Expose
    private ProfileResModel mProfilesByProfileID;
    @SerializedName("resource")
    @Expose
    private ArrayList<FeedLikesModel> mResource = null;

    public FeedLikesModel(int postID, int profileID) {
        this.mPostID = postID;
        this.mProfileID = profileID;
    }

    public ArrayList<FeedLikesModel> getResource() {
        return mResource;
    }

    public void setResource(ArrayList<FeedLikesModel> resource) {
        this.mResource = resource;
    }

    public int getId() {
        return this.mID;
    }

    public int getOwnerID() {
        return this.mProfileID;
    }

    public int getFeedID() {
        return this.mPostID;
    }

    public ProfileResModel getProfiles_by_ProfileID() {
        return mProfilesByProfileID;
    }

    public void setProfiles_by_ProfileID(ProfileResModel profiles_by_ProfileID) {
        this.mProfilesByProfileID = profiles_by_ProfileID;
    }

}
