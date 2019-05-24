package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class FeedShareModel implements Serializable {

    @SerializedName("ID")
    @Expose
    private int mID;

    @SerializedName("PostID")
    @Expose
    private String mPostID;

    @SerializedName("ProfileID")
    @Expose
    private int mProfileID;

    @SerializedName("SharedAt")
    @Expose
    private String mSharedAt;

    @SerializedName("PostOwnerID")
    @Expose
    private String mPostOwnerID;

    @SerializedName("OriginalPostID")
    @Expose
    private int mPostOriginalID;


    @SerializedName("profiles_by_ProfileID")
    @Expose
    private ProfileResModel mProfilesByProfileID;

    @SerializedName("promoter_by_ProfileID")
    @Expose
    private PromotersResModel mPromoterByProfileID;


    @SerializedName("resource")
    @Expose
    private ArrayList<FeedShareModel> mResource = null;

    public ArrayList<FeedShareModel> getResource() {
        return mResource;
    }

    public void setResource(ArrayList<FeedShareModel> resource) {
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

    public PromotersResModel getPromoterByProfileID() {
        return mPromoterByProfileID;
    }

    public void setPromoterByProfileID(PromotersResModel mPromoterByProfileID) {
        this.mPromoterByProfileID = mPromoterByProfileID;
    }
}
