package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EventsModel {

    public static final String EVENT_ID = "EventID";
    public static final String EVENTS_RES_MODEL = "EventsResModel";
    public static final String WHO_IS_GOING_BY_EVENT_ID = "whosgoing_by_EventID";
    public static final String EVENT_GROUPS_BY_EVENT_ID = "event_groups_by_EventID";
    public static final String RACING_BY_EVENT_ID = "racing_by_EventID";
    public static final String EVENT_REGISTRATION_QUESTION_BY_GROUP_ID = "event_registration_question_by_group_id";
    public static final String EVENT_REGISTRATION_ANSWER_BY_EVENT_ID ="event_registration_answer_by_EventID";
    public static final String PROMOTER_BY_USER_ID = "promoter_by_UserID";
    public static final String LIVESTREAM_BY_EVENTID = "livestream_by_EventID";
    public static final String LIVESTREAMPAYMENT_BY_EVENTID = "livestreampayment_by_EventID";
    public static final String EVENT_AMOUNT = "EventAmount";
    public static final String PROMOTER_FOLLOWER_BY_PROMOTER_USER_ID = "promoterfollower_by_PromoterUserID";
    public static final String EVENT_AD_BY_EVENTID = "eventad_by_EventID";

    @SerializedName("resource")
    @Expose
    private ArrayList<EventsResModel> mResource = null;

    public ArrayList<EventsResModel> getResource() {
        return mResource;
    }

    public void setResource(ArrayList<EventsResModel> resource) {
        this.mResource = resource;
    }

}
