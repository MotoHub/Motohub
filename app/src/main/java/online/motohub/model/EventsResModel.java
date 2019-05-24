package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class EventsResModel implements Serializable {

    public static final String EVENT_REGISTRATION_QUESTIONS = "EventRegistrationQuestions";

    @SerializedName("ID")
    @Expose
    private Integer mID;

    @SerializedName("EventType")
    @Expose
    private String mEventType;

    @SerializedName("EventImage")
    @Expose
    private String mEventImage;

    @SerializedName("MotorVehicle")
    @Expose
    private String mMotorVehicle;

    @SerializedName("Name")
    @Expose
    private String mName;

    @SerializedName("Finish")
    @Expose
    private String mFinish;

    @SerializedName("Date")
    @Expose
    private String mDate;

    @SerializedName("Where")
    @Expose
    private String mWhere;

    @SerializedName("WhereNotes")
    @Expose
    private String mWhereNotes;

    @SerializedName("Groups")
    @Expose
    private Integer mGroups;

    @SerializedName("AutomaticBookingNumber")
    @Expose
    private String mAutomaticBookingNumber;

    @SerializedName("BookingNumberIndex")
    @Expose
    private String mBookingNumberIndex;

    @SerializedName("UserID")
    @Expose
    private Integer mUserID;

    @SerializedName("EventStatus")
    @Expose
    private Integer mEventStatus;

    @SerializedName("CameraSSID")
    @Expose
    private String mCameraSSID;

    @SerializedName("Price")
    @Expose
    private Integer mPrice;

    @SerializedName("registration_form")
    @Expose
    private Integer mRegistrationForm;

    @SerializedName("track_id")
    @Expose
    private Integer mTrackID;

    @SerializedName("eventad_by_EventID")
    @Expose
    private ArrayList<EventadByEventID> eventadByEventID = null;

    public ArrayList<EventadByEventID> getEventadByEventID() {
        return eventadByEventID;
    }

    public void setEventadByEventID(ArrayList<EventadByEventID> eventadByEventID) {
        this.eventadByEventID = eventadByEventID;
    }

    @SerializedName("whosgoing_by_EventID")
    @Expose
    private ArrayList<EventsWhoIsGoingResModel> mWhoIsGoingByEventID;

    @SerializedName("event_groups_by_EventID")
    @Expose
    private ArrayList<EventCategoryModel> mEventGroupsByEventID;

    @SerializedName("racing_by_EventID")
    @Expose
    private ArrayList<RacingModel> mRacingList;

    @SerializedName("event_registration_question_by_group_id")
    @Expose
    private ArrayList<EventRegistrationQuestionModel> mEventRegistrationQuestion;

    @SerializedName("event_registration_answer_by_EventID")
    @Expose
    private ArrayList<EventAnswersModel> mEventAnswerList;

    @SerializedName("promoter_by_UserID")
    @Expose
    private PromotersResModel mPromoterByUserID;

    @SerializedName("livestream_by_EventID")
    @Expose
    private ArrayList<LiveStreamEntity> livestream_by_EventID;


    @SerializedName("livestreampayment_by_EventID")
    @Expose
    private ArrayList<LiveStreamPaymentEntity> livestreampayment_by_EventID;

    @SerializedName("stream_amount")
    @Expose
    private Integer stream_amount;

    public ArrayList<RacingModel> getRacingList() {
        return mRacingList;
    }

    public void setRacingList(ArrayList<RacingModel> mRacingList) {
        this.mRacingList = mRacingList;
    }

    public ArrayList<EventCategoryModel> getEventGroupsByEventID() {
        return mEventGroupsByEventID;
    }

    public void setEventGroupsByEventID(ArrayList<EventCategoryModel> mEventGroupsByEventID) {
        this.mEventGroupsByEventID = mEventGroupsByEventID;
    }

    public String getmEventImage() {
        if (mEventImage == null)
            mEventImage = "";
        return mEventImage;
    }

    public void setmEventImage(String mEventImage) {
        this.mEventImage = mEventImage;
    }

    public Integer getID() {
        return mID;
    }

    public void setID(Integer iD) {
        this.mID = iD;
    }

    public String getEventType() {
        return mEventType;
    }

    public void setEventType(String eventType) {
        this.mEventType = eventType;
    }

    public String getMotorVehicle() {
        return mMotorVehicle;
    }

    public void setMotorVehicle(String motorVehicle) {
        this.mMotorVehicle = motorVehicle;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getBookingNumberIndex() {
        return mBookingNumberIndex;
    }

    public void setBookingNumberIndex(String mBookingNumberIndex) {
        this.mBookingNumberIndex = mBookingNumberIndex;
    }

    public String getFinish() {
        return mFinish;
    }

    public void setFinish(String finish) {
        this.mFinish = finish;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public String getWhere() {
        return mWhere;
    }

    public void setWhere(String where) {
        this.mWhere = where;
    }

    public String getWhereNotes() {
        return mWhereNotes;
    }

    public void setWhereNotes(String whereNotes) {
        this.mWhereNotes = whereNotes;
    }

    public Integer getGroups() {
        return mGroups;
    }

    public void setGroups(Integer groups) {
        this.mGroups = groups;
    }

    public String getAutomaticBookingNumber() {
        return mAutomaticBookingNumber;
    }

    public void setAutomaticBookingNumber(String automaticBookingNumber) {
        this.mAutomaticBookingNumber = automaticBookingNumber;
    }

    public Integer getUserID() {
        return mUserID;
    }

    public void setUserID(Integer userID) {
        this.mUserID = userID;
    }

    public ArrayList<EventsWhoIsGoingResModel> getWhoIsGoingByEventID() {
        return mWhoIsGoingByEventID;
    }

    public void setWhoIsGoingByEventID(ArrayList<EventsWhoIsGoingResModel> whoIsGoingByEventID) {
        this.mWhoIsGoingByEventID = whoIsGoingByEventID;
    }

    public Integer getEventStatus() {
        return mEventStatus;
    }

    public void setEventStatus(Integer mEventStatus) {
        this.mEventStatus = mEventStatus;
    }

    public String getCameraSSID() {
        return mCameraSSID;
    }

    public void setCameraSSID(String mCameraSSID) {
        this.mCameraSSID = mCameraSSID;
    }

    public Integer getPrice() {
        return mPrice;
    }

    public void setPrice(Integer mPrice) {
        this.mPrice = mPrice;
    }

    public ArrayList<EventRegistrationQuestionModel> getEventRegistrationQuestion() {
        return mEventRegistrationQuestion;
    }

    public void setEventRegistrationQuestion(ArrayList<EventRegistrationQuestionModel> mEventRegistrationQuestion) {
        this.mEventRegistrationQuestion = mEventRegistrationQuestion;
    }

    public Integer getRegistrationForm() {
        return mRegistrationForm;
    }

    public void setRegistrationForm(Integer mRegistrationForm) {
        this.mRegistrationForm = mRegistrationForm;
    }

    public ArrayList<EventAnswersModel> getEventAnswerList() {
        return mEventAnswerList;
    }

    public void setEventAnswerList(ArrayList<EventAnswersModel> mEventAnswerList) {
        this.mEventAnswerList = mEventAnswerList;
    }

    public Integer getTrackID() {
        return mTrackID;
    }

    public void setTrackID(Integer mTrackID) {
        this.mTrackID = mTrackID;
    }

    public PromotersResModel getPromoterByUserID() {
        return mPromoterByUserID;
    }

    public void setPromoterByUserID(PromotersResModel mPromoterByUserID) {
        this.mPromoterByUserID = mPromoterByUserID;
    }

    public ArrayList<LiveStreamEntity> getLivestream_by_EventID() {
        if (livestream_by_EventID == null)
            livestream_by_EventID = new ArrayList<>();
        return livestream_by_EventID;
    }

    public void setLivestream_by_EventID(ArrayList<LiveStreamEntity> livestream_by_EventID) {
        this.livestream_by_EventID = livestream_by_EventID;
    }

    public ArrayList<LiveStreamPaymentEntity> getLivestreampayment_by_EventID() {
        if (livestreampayment_by_EventID == null)
            livestreampayment_by_EventID = new ArrayList<>();
        return livestreampayment_by_EventID;
    }

    public void setLivestreampayment_by_EventID(ArrayList<LiveStreamPaymentEntity> livestreampayment_by_EventID) {
        this.livestreampayment_by_EventID = livestreampayment_by_EventID;
    }

    public Integer getStream_amount() {
        if (stream_amount == null)
            stream_amount = 0;
        return stream_amount;
    }

    public static class EventadByEventID implements Serializable {

        @SerializedName("ID")
        @Expose
        private Integer iD;
        @SerializedName("UserID")
        @Expose
        private Integer userID;
        @SerializedName("EventID")
        @Expose
        private Integer eventID;
        @SerializedName("EventAd")
        @Expose
        private String eventAd;
        @SerializedName("CreatedAt")
        @Expose
        private String createdAt;

        public Integer getID() {
            return iD;
        }

        public void setID(Integer iD) {
            this.iD = iD;
        }

        public Integer getUserID() {
            return userID;
        }

        public void setUserID(Integer userID) {
            this.userID = userID;
        }

        public Integer getEventID() {
            return eventID;
        }

        public void setEventID(Integer eventID) {
            this.eventID = eventID;
        }

        public String getEventAd() {
            return eventAd;
        }

        public void setEventAd(String eventAd) {
            this.eventAd = eventAd;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

    }
}
