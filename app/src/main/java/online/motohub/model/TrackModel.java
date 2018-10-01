package online.motohub.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrackModel {

    public static String PROMOTER_BY_OWNER_ID = "promoter_by_owner_id";


    @SerializedName("resource")
    private List<TrackResModel> mTrackResModels;

    public TrackModel() {
    }

    public List<TrackResModel> getmTrackResModels() {
        return mTrackResModels;
    }

    public void setmTrackResModels(List<TrackResModel> mTrackResModels) {
        this.mTrackResModels = mTrackResModels;
    }
}
