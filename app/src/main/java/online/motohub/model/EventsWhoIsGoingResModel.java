package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EventsWhoIsGoingResModel implements Serializable {

    @SerializedName("ID")
    @Expose
    private int mID;

    @SerializedName("EventID")
    @Expose
    private int mEventID;

    @SerializedName("ProfileID")
    @Expose
    private int mProfileID;

    @SerializedName("UserID")
    @Expose
    private int mUserID;

    @SerializedName("Payment Details")
    @Expose
    private String mPaymentDetails;

    @SerializedName("Amount")
    @Expose
    private int mAmount;

    @SerializedName("To Account")
    @Expose
    private String mToAccount;

    @SerializedName("Payment Status")
    @Expose
    private String mPaymentStatus;

    @SerializedName("profiles_by_ProfileID")
    @Expose
    private ProfileResModel mProfileByProfileID;

    @SerializedName("registration_questions")
    @Expose
    private String mRegistrationQuestions;

    @SerializedName("Answers")
    @Expose
    private String mAnswers;

    public int getID() {
        return mID;
    }

    public void setID(int iD) {
        this.mID = iD;
    }

    public int getProfileID() {
        return mProfileID;
    }

    public void setProfileID(int profileID) {
        this.mProfileID = profileID;
    }

    public int getEventID() {
        return mEventID;
    }

    public void setEventID(int eventID) {
        this.mEventID = eventID;
    }

    public int getUserID() {
        return mUserID;
    }

    public void setUserID(int userID) {
        this.mUserID = userID;
    }

    public ProfileResModel getProfileByProfileID() {
        return mProfileByProfileID;
    }

    public void setProfileByProfileID(ProfileResModel profileByProfileID) {
        this.mProfileByProfileID = profileByProfileID;
    }

    public String getRegistrationQuestions() {
        return mRegistrationQuestions;
    }

    public void setRegistrationQuestions(String mRegistrationQuestions) {
        this.mRegistrationQuestions = mRegistrationQuestions;
    }

    public String getAnswers() {
        return mAnswers;
    }

    public void setAnswers(String mAnswers) {
        this.mAnswers = mAnswers;
    }

}
