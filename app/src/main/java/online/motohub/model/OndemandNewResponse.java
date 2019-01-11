package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by pickzy01 on 24/05/2018.
 */

public class OndemandNewResponse {


    @Expose
    @SerializedName("CoverImage")
    private String CoverImage;
    @Expose
    @SerializedName("ProfilePicture")
    private String ProfilePicture;
    @Expose
    @SerializedName("ViewCount")
    private int ViewCount;
    @Expose
    @SerializedName("Count")
    private int Count;
    @Expose
    @SerializedName("EventImage")
    private String EventImage;
    @Expose
    @SerializedName("Name")
    private String Name;
    @Expose
    @SerializedName("EventID")
    private int EventID;
    @Expose
    @SerializedName("Type")
    private String Type;

    public String getCoverImage() {
        return CoverImage;
    }

    public void setCoverImage(String CoverImage) {
        this.CoverImage = CoverImage;
    }

    public String getProfilePicture() {
        return ProfilePicture;
    }

    public void setProfilePicture(String ProfilePicture) {
        this.ProfilePicture = ProfilePicture;
    }

    public int getViewCount() {
        return ViewCount;
    }

    public void setViewCount(int ViewCount) {
        this.ViewCount = ViewCount;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int Count) {
        this.Count = Count;
    }

    public String getEventImage() {
        return EventImage;
    }

    public void setEventImage(String EventImage) {
        this.EventImage = EventImage;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public int getEventID() {
        return EventID;
    }

    public void setEventID(int EventID) {
        this.EventID = EventID;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }
}
