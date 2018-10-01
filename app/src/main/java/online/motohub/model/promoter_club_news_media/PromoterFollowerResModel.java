package online.motohub.model.promoter_club_news_media;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PromoterFollowerResModel implements
        Serializable,
        Comparable<PromoterFollowerResModel> {

    public static final String ID ="ID";
    public static final String PROFILE_ID = "ProfileID";
    public static final String PROMOTER_USER_ID = "PromoterUserID";
    public static final String CREATED_AT = "CreatedAt";
    public static final String FOLLOW_RELATION = "FollowRelation";

    @SerializedName("ID")
    @Expose
    private int mID;

    @SerializedName("ProfileID")
    @Expose
    private int profileID;

    @SerializedName("PromoterUserID")
    @Expose
    private int promoterUserID;

    @SerializedName("CreatedAt")
    @Expose
    private String createdAt;

    @SerializedName("FollowRelation")
    @Expose
    private String followRelation;

    public int getID() {
        return mID;
    }

    public void setID(int id) {
        this.mID = id;
    }

    public int getProfileID() {
        return profileID;
    }

    public void setProfileID(int profileID) {
        this.profileID = profileID;
    }

    public int getPromoterUserID() {
        return promoterUserID;
    }

    public void setPromoterUserID(int promoterUserID) {
        this.promoterUserID = promoterUserID;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFollowRelation() {
        return followRelation;
    }

    public void setFollowRelation(String followRelation) {
        this.followRelation = followRelation;
    }

    @Override
    public int compareTo(@NonNull PromoterFollowerResModel promoterFollowerResModel) {
        return this.mID - promoterFollowerResModel.getID();
    }

}
