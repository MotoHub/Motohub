package online.motohub.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class RacingModel implements Serializable {

    public static final String CATEGORY_ID = "CategoryID";
    public static final String EVENT_ID = "EventID";
    public static final String PROFILE_ID = "ProfileID";
    public static final String RACE_NAME = "RaceName";
    public static final String CAR_NUMBER = "car_number";
    public static final String RACE_STATUS = "race_status";
    public static final String RACE_NUMBER = "race_number";
    public static final String STATUS = "status";
    public static final String TIME = "Time";

    @SerializedName("CategoryID")
    private int mCategoryID;

    @SerializedName("EventID")
    private int mEventID;

    @SerializedName("ProfileID")
    private int mProfileID;

    @SerializedName("RaceName")
    private int mRaceName;

    @SerializedName("car_number")
    private String mCarNumber;

    @SerializedName("race_status")
    private int mRaceStatus;

    @SerializedName("race_number")
    private int mRaceNumber;

    @SerializedName("status")
    private boolean mStatus;

    @SerializedName("Time")
    private String mTime;

 /*   @SerializedName("Time")
    private Date mTime;*/

    public int getCategoryID() {
        return mCategoryID;
    }

    public void setCategoryID(int mCategoryID) {
        this.mCategoryID = mCategoryID;
    }

    public int getEventID() {
        return mEventID;
    }

    public void setEventID(int mEventID) {
        this.mEventID = mEventID;
    }

    public int getProfileID() {
        return mProfileID;
    }

    public void setProfileID(int mProfileID) {
        this.mProfileID = mProfileID;
    }

    public String getCarNumber() {
        return mCarNumber;
    }

    public void setCarNumber(String mCarNumber) {
        this.mCarNumber = mCarNumber;
    }

    public int getRaceStatus() {
        return mRaceStatus;
    }

    public void setRaceStatus(int mRaceStatus) {
        this.mRaceStatus = mRaceStatus;
    }

    public int getRaceNumber() {
        return mRaceNumber;
    }

    public void setRaceNumber(int mRaceNumber) {
        this.mRaceNumber = mRaceNumber;
    }

    public boolean isStatus() {
        return mStatus;
    }

    public void setStatus(boolean mStatus) {
        this.mStatus = mStatus;
    }
}
