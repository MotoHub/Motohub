package online.motohub.model;

/**
 * Created by Pickzy-05 on 3/13/2018.
 */

public class SpectatorLiveEntity {

    String Caption;

    String ProfileID;

    String VideoUrl;

    String Thumbnail;

    String FileType;

    String ID;

    String EventID;

    String OtherProfileID;

    String UserID;

    String UserType;

    String EventFinishDate;

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }

    public String getEventFinishDate() {
        return EventFinishDate;
    }

    public void setEventFinishDate(String eventFinishDate) {
        EventFinishDate = eventFinishDate;
    }

    public String getLivePostProfileID() {
        return LivePostProfileID;
    }

    public void setLivePostProfileID(String livePostProfileID) {
        LivePostProfileID = livePostProfileID;
    }

    String LivePostProfileID;

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String Caption) {
        this.Caption = Caption;
    }

    public String getProfileID() {
        return ProfileID;
    }

    public void setProfileID(String ProfileID) {
        this.ProfileID = ProfileID;
    }

    public String getFileType() {
        return FileType;
    }

    public void setFileType(String FileType) {
        this.FileType = FileType;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEventID() {
        return EventID;
    }

    public void setEventID(String EventID) {
        this.EventID = EventID;
    }

    public String getOtherProfileID() {
        return OtherProfileID;
    }

    public void setOtherProfileID(String OtherProfileID) {
        this.OtherProfileID = OtherProfileID;
    }
}

