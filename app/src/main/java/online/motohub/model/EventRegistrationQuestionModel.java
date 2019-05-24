package online.motohub.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class EventRegistrationQuestionModel implements Serializable {

    @SerializedName("question")
    private String mQuestion;

    @SerializedName("group_name")
    private String mGroupName;

    @SerializedName("group_slug")
    private String mGroupSlug;

    @SerializedName("user_id")
    private int mUserID;

    @SerializedName("group_id")
    private int mGroupID;

    @SerializedName("ID")
    private int mQuestionID;

    @SerializedName("resource")
    private ArrayList<EventRegistrationQuestionModel> mEventQuestionList;

    public ArrayList<EventRegistrationQuestionModel> getEventQuestionList() {
        return mEventQuestionList;
    }

    public void setEventQuestionList(ArrayList<EventRegistrationQuestionModel> mEventQuestionList) {
        this.mEventQuestionList = mEventQuestionList;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public void setQuestion(String mQuestion) {
        this.mQuestion = mQuestion;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String mGroupName) {
        this.mGroupName = mGroupName;
    }

    public String getGroupSlug() {
        return mGroupSlug;
    }

    public void setGroupSlug(String mGroupSlug) {
        this.mGroupSlug = mGroupSlug;
    }

    public int getUserID() {
        return mUserID;
    }

    public void setUserID(int mUserID) {
        this.mUserID = mUserID;
    }

    public int getGroupID() {
        return mGroupID;
    }

    public void setGroupID(int mGroupID) {
        this.mGroupID = mGroupID;
    }

    public int getQuestionID() {
        return mQuestionID;
    }

    public void setQuestionID(int mQuestionID) {
        this.mQuestionID = mQuestionID;
    }
}
