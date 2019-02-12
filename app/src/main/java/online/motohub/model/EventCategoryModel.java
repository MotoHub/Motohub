package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;


public class EventCategoryModel implements Serializable {

    public static final String EVENT_CATEGORY_RES_MODEL = "EventCategoryResModel";

    @SerializedName("ID")
    @Expose
    private int ID;

    @SerializedName("Section")
    @Expose
    private String mSection;

    @SerializedName("GroupName")
    @Expose
    private String mGroupName;

    @SerializedName("EventID")
    @Expose
    private Integer mEventID;

    @SerializedName("GroupNo")
    @Expose
    private Integer mGroupNo;

    @SerializedName("start_time")
    @Expose
    private String mStartTime;

    @SerializedName("end_time")
    @Expose
    private String mEndTime;

    @SerializedName("resource")
    @Expose
    private ArrayList<EventCategoryModel> mResource;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public ArrayList<EventCategoryModel> getResource() {
        return mResource;
    }

    public String getSection() {
        return mSection;
    }

    public void setSection(String mSection) {
        this.mSection = mSection;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String mGroupName) {
        this.mGroupName = mGroupName;
    }

    public Integer getEventID() {
        return mEventID;
    }

    public void setEventID(Integer mEventID) {
        this.mEventID = mEventID;
    }

    public Integer getGroupNo() {
        return mGroupNo;
    }

    public void setGroupNo(Integer mGroupNo) {
        this.mGroupNo = mGroupNo;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String mStartTime) {
        this.mStartTime = mStartTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(String mEndTime) {
        this.mEndTime = mEndTime;
    }
}
