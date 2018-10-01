package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class EventsWhoIsGoingModel implements Serializable {

    public static final String WHO_IS_GOING_RES_MODEL = "WhoIsGoingResModel";

    public static final String PROFILES_BY_PROFILE_ID = "profiles_by_ProfileID";

    @SerializedName("resource")
    @Expose
    private ArrayList<EventsWhoIsGoingResModel> mResource = null;

    public ArrayList<EventsWhoIsGoingResModel> getResource() {
        return mResource;
    }

    public void setResource(ArrayList<EventsWhoIsGoingResModel> resource) {
        this.mResource = resource;
    }

}
