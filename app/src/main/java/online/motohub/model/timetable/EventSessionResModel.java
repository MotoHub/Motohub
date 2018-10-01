package online.motohub.model.timetable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import online.motohub.model.EventsResModel;
import online.motohub.model.EventsWhoIsGoingResModel;

public class EventSessionResModel implements Serializable {

    @SerializedName("ID")
    private int id;

    @SerializedName("SessionName")
    private String sessionName;

    @SerializedName("TrackID")
    private String trackId;

    @SerializedName("CreatedOn")
    private String createdOn;

    @SerializedName("OwnerID")
    private int ownerId;

    @SerializedName("EventID")
    private int eventId;

    @SerializedName("StartTime")
    private String startTime;

    @SerializedName("EndTime")
    private String endTime;

    @SerializedName("event_by_EventID")
    private EventsResModel eventsResModel;

    @SerializedName("whosgoing_by_EventID")
    @Expose
    private ArrayList<EventsWhoIsGoingResModel> mWhoIsGoingByEventID;

    public EventSessionResModel() {
    }

    public EventsResModel getEventsResModel() {
        return eventsResModel;
    }

    public void setEventsResModel(EventsResModel eventsResModel) {
        this.eventsResModel = eventsResModel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<EventsWhoIsGoingResModel> getWhoIsGoingByEventID() {
        return mWhoIsGoingByEventID;
    }

    public void setWhoIsGoingByEventID(ArrayList<EventsWhoIsGoingResModel> whoIsGoingByEventID) {
        this.mWhoIsGoingByEventID = whoIsGoingByEventID;
    }

}