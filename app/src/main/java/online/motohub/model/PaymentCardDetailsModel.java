package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PaymentCardDetailsModel implements Serializable {

    @SerializedName("ID")
    private String mID;

    @SerializedName("UserID")
    private String mUserID;

    @SerializedName("ProfileID")
    private String mProfileID;

    public String getID() {
        return mID;
    }

    public void setID(String mID) {
        this.mID = mID;
    }

    @SerializedName("CardNumber")

    private String mCardNumber;

    @SerializedName("CardName")
    private String mCardName;

    @SerializedName("CardExpiryDate")
    private String mCardExpiryDate;

    @SerializedName("CardExpiryMonth")
    private String mCardExpiryMonth;

    @SerializedName("CardExpiryYear")
    private String mCardExpiryYear;

    @SerializedName("EventID")
    private String mEventID;

    public String getCardRelation() {
        return mCardRelation;
    }

    public void setCardRelation(String mCardRelation) {
        this.mCardRelation = mCardRelation;
    }

    @SerializedName("Payment_type")
    private String mPaymentType;

    @SerializedName("CardRelation")
    private String mCardRelation;

    private boolean isSelectedCard;

    @SerializedName("resource")
    @Expose
    private ArrayList<PaymentCardDetailsModel> mResource = null;

    public boolean isSelectedCard() {
        return isSelectedCard;
    }

    public void setSelectedCard(boolean selectedCard) {
        isSelectedCard = selectedCard;
    }

    public ArrayList<PaymentCardDetailsModel> getResource() {
        if(mResource == null)

            mResource = new ArrayList<>();
        return mResource;
    }

    public void setResource(ArrayList<PaymentCardDetailsModel> resource) {
        this.mResource = resource;
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String mUserID) {
        this.mUserID = mUserID;
    }

    public String getProfileID() {
        return mProfileID;
    }

    public void setProfileID(String mProfileID) {
        this.mProfileID = mProfileID;
    }

    public String getCardNumber() {
        if(mCardNumber.isEmpty())
            mCardNumber = "";
        return mCardNumber;
    }

    public void setCardNumber(String mCardNumber) {
        this.mCardNumber = mCardNumber;
    }

    public String getCardName() {
        return mCardName;
    }

    public void setCardName(String mCardName) {
        this.mCardName = mCardName;
    }

    public String getCardExpiryDate() {
        return mCardExpiryDate;
    }

    public void setCardExpiryDate(String mCardExpiryDate) {
        this.mCardExpiryDate = mCardExpiryDate;
    }

    public String getEventID() {
        return mEventID;
    }

    public void setEventID(String mEventID) {
        this.mEventID = mEventID;
    }

    public String getPaymentType() {
        return mPaymentType;
    }

    public void setPaymentType(String mPaymentType) {
        this.mPaymentType = mPaymentType;
    }

    public String getCardExpiryMonth() {
        return mCardExpiryMonth;
    }

    public void setCardExpiryMonth(String mCardExpiryMonth) {
        this.mCardExpiryMonth = mCardExpiryMonth;
    }

    public String getmCardExpiryYear() {
        return mCardExpiryYear;
    }

    public void setmCardExpiryYear(String mCardExpiryYear) {
        this.mCardExpiryYear = mCardExpiryYear;
    }
}
