package online.motohub.model.timetable;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EventSessionModel implements Serializable {

    @SerializedName("resource")
    private List<EventSessionResModel> mEventSessionResModels;

    public List<EventSessionResModel> getEventSessionResModels() {
        return mEventSessionResModels;
    }

    public void setEventSessionResModels(List<EventSessionResModel> mEventSessionResModels) {
        this.mEventSessionResModels = mEventSessionResModels;
    }

}