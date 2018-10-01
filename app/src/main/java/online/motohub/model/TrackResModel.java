package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class TrackResModel implements Serializable {

    @SerializedName("ID")
    private int id;

    @SerializedName("track_name")
    private String trackName;

    @SerializedName("track_description")
    private String trackDescription;

    @SerializedName("track_date")
    private String trackDate;

    @SerializedName("owner_id")
    private int ownerId;

    @SerializedName("ContactName")
    private String contactName;

    @SerializedName("ContactEmail")
    private String contactMailId;

    @SerializedName("PhoneNumber")
    private String contactNumber;

    @SerializedName("Address")
    private String address;

    @SerializedName("Latitude")
    private String latitude;

    @SerializedName("Longitude")
    private String longitude;

    @SerializedName("Picture")
    private String coverImg;

    @SerializedName("promoter_by_owner_id")
    @Expose
    private PromotersResModel mPromoterByOwnerID;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackDescription() {
        return trackDescription;
    }

    public void setTrackDescription(String trackDescription) {
        this.trackDescription = trackDescription;
    }

    public String getTrackDate() {
        return trackDate;
    }

    public void setTrackDate(String trackDate) {
        this.trackDate = trackDate;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactMailId() {
        return contactMailId;
    }

    public void setContactMailId(String contactMailId) {
        this.contactMailId = contactMailId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCoverImg() {
        if(coverImg == null)
            coverImg = "";
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public PromotersResModel getPromoterByOwnerID() {
        return mPromoterByOwnerID;
    }

    public void setPromoterByOwnerID(PromotersResModel mPromoterByOwnerID) {
        this.mPromoterByOwnerID = mPromoterByOwnerID;
    }
}
