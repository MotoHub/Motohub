package online.motohub.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;


public class EventAddOnModel implements Serializable{

    @SerializedName("ID")
    private int mID;

    @SerializedName("addon_description")
    private String mAddOnDescription;

    @SerializedName("addon_price")
    private int mAddOnPrice;

    @SerializedName("event_id")
    private int mEventID;

    @SerializedName("resource")
    private ArrayList<EventAddOnModel> mResource;

    public ArrayList<EventAddOnModel> getResource() {
        if(mResource == null)
            mResource = new ArrayList<>();
        return mResource;
    }

    public void setResource(ArrayList<EventAddOnModel> mResource) {
        this.mResource = mResource;
    }

    public int getID() {
        return mID;
    }

    public void setID(int mID) {
        this.mID = mID;
    }

    public String getAddOnDescription() {
        return mAddOnDescription;
    }

    public void setAddOnDescription(String mAddOnDescription) {
        this.mAddOnDescription = mAddOnDescription;
    }

    public int getAddOnPrice() {
        return mAddOnPrice;
    }

    public void setAddOnPrice(int mAddOnPrice) {
        this.mAddOnPrice = mAddOnPrice;
    }

    public int getEventID() {
        return mEventID;
    }

    public void setEventID(int mEventID) {
        this.mEventID = mEventID;
    }
}
