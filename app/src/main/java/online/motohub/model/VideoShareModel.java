package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;


public class VideoShareModel implements Serializable {

    @SerializedName("ID")
    @Expose
    private int mID;

    @SerializedName("VideoID")
    @Expose
    private String mPostID;

    @SerializedName("ProfileID")
    @Expose
    private int mProfileID;

    @SerializedName("SharedAt")
    @Expose
    private String mSharedAt;

    @SerializedName("VideoOwnerID")
    @Expose
    private String mPostOwnerID;

    @SerializedName("OriginalVideoID")
    @Expose
    private int mPostOriginalID;


    @SerializedName("profiles_by_ProfileID")
    @Expose
    private ProfileResModel mProfilesByProfileID;


    @SerializedName("resource")
    @Expose
    private ArrayList<VideoShareModel> mResource = null;

    public ArrayList<VideoShareModel> getResource() {
        if (mResource == null)
            mResource = new ArrayList<>();
        return mResource;
    }

    public void setResource(ArrayList<VideoShareModel> resource) {
        this.mResource = resource;
    }

    public int getId() {
        return this.mID;
    }

    public int getProfileID() {
        return this.mProfileID;
    }


    public ProfileResModel getProfiles_by_ProfileID() {
        return mProfilesByProfileID;
    }

    public void setProfiles_by_ProfileID(ProfileResModel profiles_by_ProfileID) {
        this.mProfilesByProfileID = profiles_by_ProfileID;
    }

    public String getSharedAt() {
        if (mSharedAt == null)
            mSharedAt = "";
        return mSharedAt;
    }

    public void setSharedAt(String mSharedAt) {
        this.mSharedAt = mSharedAt;
    }

    public int getPostOriginalID() {
        return mPostOriginalID;
    }

    public void setPostOriginalID(int mPostOriginalID) {
        this.mPostOriginalID = mPostOriginalID;
    }

}
