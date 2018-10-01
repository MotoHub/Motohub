package online.motohub.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pickzy01 on 24/05/2018.
 */

public class OndemandNewResponse {


    @SerializedName("Count")
    private int Count;
    @SerializedName("ProfilePicture")
    private String ProfilePicture;
    @SerializedName("Name")
    private String Name;
    @SerializedName("ProfileID")
    private int ProfileID;
    @SerializedName("UserID")
    private int UserID;
    @SerializedName("EventID")
    private int EventID;
    @SerializedName("Type")
    private String Type;

    public int getEventID() {
        return EventID;
    }

    public void setEventID(int eventID) {
        EventID = eventID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int Count) {
        this.Count = Count;
    }

    public String getProfilePicture() {
        return ProfilePicture;
    }

    public void setProfilePicture(String ProfilePicture) {
        this.ProfilePicture = ProfilePicture;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public int getProfileID() {
        return ProfileID;
    }

    public void setProfileID(int ProfileID) {
        this.ProfileID = ProfileID;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }
}
