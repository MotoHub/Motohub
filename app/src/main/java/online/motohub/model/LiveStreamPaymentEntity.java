package online.motohub.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LiveStreamPaymentEntity implements Serializable {

    @SerializedName("ID")
    @Expose
    private int ID;
    @SerializedName("ViewUserID")
    @Expose
    private int ViewUserID;
    @SerializedName("PromoterID")
    @Expose
    private int PromoterID;

    @SerializedName("EventID")
    @Expose
    private int EventID;

    @SerializedName("Amount")
    @Expose
    private int Amount;

    @SerializedName("TransactionID")
    @Expose
    private String TransactionID;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getViewUserID() {
        return ViewUserID;
    }

    public int getPromoterID() {
        return PromoterID;
    }

    public int getEventID() {
        return EventID;
    }

    public void setEventID(int eventID) {
        EventID = eventID;
    }

    public int getAmount() {
        return Amount;
    }

    public String getTransactionID() {
        return TransactionID;
    }
}
