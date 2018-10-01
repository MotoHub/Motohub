package online.motohub.model;

import java.io.Serializable;

public class FollowProfileEntity implements Serializable{

    private int ID;

    private int ProfileID;

    private int FollowProfileID;

    private String FollowRelation;

    private ProfileResModel profiles_by_ProfileID;

    private ProfileResModel profiles_by_FollowProfileID;

    public int getID() {
        return ID;
    }

    public int getProfileID() {
        return ProfileID;
    }

    public int getFollowProfileID() {
        return FollowProfileID;
    }

    public ProfileResModel getProfiles_by_ProfileID() {
        return profiles_by_ProfileID;
    }

    public ProfileResModel getProfiles_by_FollowProfileID() {
        return profiles_by_FollowProfileID;
    }

    public String getFollowRelation() {
        return FollowRelation;
    }

    public void setFollowRelation(String followRelation) {
        FollowRelation = followRelation;
    }
}
