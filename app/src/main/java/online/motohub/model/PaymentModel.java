package online.motohub.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentModel implements Serializable{

    public class source{
        @SerializedName("name")
        private String mName;

        public String getName() {
            return mName;
        }

        public void setName(String mName) {
            this.mName = mName;
        }
    }

    public class outcome{
        @SerializedName("seller_message")
        private String mSellerMessage;

        public String getSellerMessage() {
            return mSellerMessage;
        }

        public void setSellerMessage(String mSellerMessage) {
            this.mSellerMessage = mSellerMessage;
        }
    }

    @SerializedName("id")
    private String mID;

    @SerializedName("amount")
    private int mAmount;

    @SerializedName("status")
    private String mStatus;

    @SerializedName("transfer")
    private String mTransfer;

    @SerializedName("currency")
    private String mCurrency;

    @SerializedName("destination")
    private String mDestination;

    @SerializedName("failure_message")
    private String mFailureMessage;

    @SerializedName("source")
    private source mSource;

    @SerializedName("outcome")
    private outcome mOutcome;

    @SerializedName("message")
    private String message;

    @SerializedName("type")
    private String type;

    @SerializedName("code")
    private String code;

    @SerializedName("decline_code")
    private String decline_code;

    @SerializedName("charge")
    private String charge;

    @SerializedName("error")
    private int error;

    public String getID() {
        return mID;
    }

    public void setID(String mID) {
        this.mID = mID;
    }

    public int getAmount() {
        return mAmount;
    }

    public String getStatus() {
        return mStatus;
    }

    public String getTransfer() {
        return mTransfer;
    }

    public void setTransfer(String mTransfer) {
        this.mTransfer = mTransfer;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String mCurrency) {
        this.mCurrency = mCurrency;
    }

    public String getDestination() {
        return mDestination;
    }

    public void setDestination(String mDestination) {
        this.mDestination = mDestination;
    }

    public String getFailureMessage() {
        return mFailureMessage;
    }

    public void setFailureMessage(String mFailureMessage) {
        this.mFailureMessage = mFailureMessage;
    }

    public source getSource() {
        return mSource;
    }

    public void setSource(source mSource) {
        this.mSource = mSource;
    }

    public outcome getOutcome() {
        return mOutcome;
    }

    public void setOutcome(outcome mOutcome) {
        this.mOutcome = mOutcome;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getDecline_code() {
        return decline_code;
    }

    public String getCharge() {
        return charge;
    }

    public int getError() {
        return error;
    }
	
}
