package online.motohub.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pyd10 on 3/6/2018.
 */

public class EventLiveGroupChatModel {

    public static final String USER_ID = "UserID";
    public static final String PROFILE_ID = "ProfileID";
    public static final String EVENT_ID = "EventID";

    @SerializedName("ID")
    private int mID;

    @SerializedName("UserID")
    private int mUserID;

    @SerializedName("ProfileID")
    private int mProfileID;

    @SerializedName("EventID")
    private int mEventID;

    public int getID() {
        return mID;
    }

    public void setID(int mID) {
        this.mID = mID;
    }

    public int getUserID() {
        return mUserID;
    }

    public void setUserID(int mUserID) {
        this.mUserID = mUserID;
    }


    public void setProfileID(int ProfileID) {
        this.mProfileID = mProfileID;
    }

    public void setEventID(int mEventID) {
        this.mEventID = mEventID;
    }
}
