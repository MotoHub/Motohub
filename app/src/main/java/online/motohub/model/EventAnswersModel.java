package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;


public class EventAnswersModel implements Serializable {

    public static final String ID = "ID";
    public static final String ANSWER = "Answer";
    public static final String PROFILE_ID = "ProfileID";
    public static final String EVENT_ID = "EventID";
    public static final String QUESTION_ID = "QuestionID";
    public static final String GROUP_ID = "group_id";

    @SerializedName("ID")
    @Expose
    private int mID;

    @SerializedName("Answer")
    private String mAnswer;

    @SerializedName("ProfileID")
    private int mProfileID;

    @SerializedName("EventID")
    private int mEventID;

    @SerializedName("QuestionID")
    private int mQuestionID;

    @SerializedName("group_id")
    private int mGroupID;

    @SerializedName("resource")
    @Expose
    private ArrayList<EventAnswersModel> mResource = null;

    public ArrayList<EventAnswersModel> getResource() {
        if (mResource == null)
            mResource = new ArrayList<>();
        return mResource;
    }

    public void setResource(ArrayList<EventAnswersModel> resource) {
        this.mResource = resource;
    }

    public int getID() {
        return mID;
    }

    public void setID(int id) {
        this.mID = id;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public void setAnswer(String mAnswer) {
        this.mAnswer = mAnswer;
    }

    public int getProfileID() {
        return mProfileID;
    }

    public void setProfileID(int mProfileID) {
        this.mProfileID = mProfileID;
    }

    public int getEventID() {
        return mEventID;
    }

    public void setEventID(int mEventID) {
        this.mEventID = mEventID;
    }

    public int getQuestionID() {
        return mQuestionID;
    }

    public void setQuestionID(int mQuestionID) {
        this.mQuestionID = mQuestionID;
    }

    public int getGroupID() {
        return mGroupID;
    }

    public void setGroupID(int mGroupID) {
        this.mGroupID = mGroupID;
    }
}
