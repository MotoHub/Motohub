package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pyd10 on 3/8/2018.
 */

public class VideoLikesModel implements Serializable {

    @SerializedName("ID")
    @Expose
    private int mID;

    @SerializedName("VideoID")
    @Expose
    private int mVideoID;

    @SerializedName("ProfileID")
    private int mProfileID;

    @SerializedName("profiles_by_ProfileID")
    @Expose
    private ProfileResModel mProfilesByProfileID;
    @SerializedName("resource")
    @Expose
    private ArrayList<VideoLikesModel> mResource = null;


    public VideoLikesModel(int postID, int profileID) {
        this.mVideoID = postID;
        this.mProfileID = profileID;
    }

    public ArrayList<VideoLikesModel> getResource() {
        return mResource;
    }

    public void setResource(ArrayList<VideoLikesModel> resource) {
        this.mResource = resource;
    }

    public int getId() {
        return this.mID;
    }

    public int getOwnerID() {
        return this.mProfileID;
    }

    public int getFeedID() {
        return this.mVideoID;
    }

    public ProfileResModel getProfiles_by_ProfileID() {
        if (mProfilesByProfileID == null)
            mProfilesByProfileID = new ProfileResModel();
        return mProfilesByProfileID;
    }

    public void setProfiles_by_ProfileID(ProfileResModel profiles_by_ProfileID) {
        this.mProfilesByProfileID = profiles_by_ProfileID;
    }
}
