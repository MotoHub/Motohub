package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class FeedLikesModel implements Serializable {

    @SerializedName("ID")
    private int ID;

    @SerializedName("PostID")
    private int PostID;

    @SerializedName("ProfileID")
    private int ProfileID;

    @SerializedName("profiles_by_ProfileID")
    private ProfileResModel ProfilesByProfileID;
    @SerializedName("resource")
    @Expose
    private ArrayList<FeedLikesModel> Resource = null;

    public FeedLikesModel(int postID, int profileID) {
        this.PostID = postID;
        this.ProfileID = profileID;
    }

    public ArrayList<FeedLikesModel> getResource() {
        return Resource;
    }

    public void setResource(ArrayList<FeedLikesModel> resource) {
        this.Resource = resource;
    }

    public int getId() {
        return this.ID;
    }

    public int getProfileID() {
        return this.ProfileID;
    }

    public int getFeedID() {
        return this.PostID;
    }

    public ProfileResModel getProfiles_by_ProfileID() {
        return ProfilesByProfileID;
    }

    public void setProfiles_by_ProfileID(ProfileResModel profiles_by_ProfileID) {
        this.ProfilesByProfileID = profiles_by_ProfileID;
    }

}
