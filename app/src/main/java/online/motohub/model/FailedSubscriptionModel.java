package online.motohub.model;


public class FailedSubscriptionModel {

    private String DateOfFailure;
    private String Message;
    private String SubscriptionID;
    private String Type;
    private String CustomerID;
    private String SubscriptionExpiry;

    public String getSubscriptionExpiry() {
        if(SubscriptionExpiry == null)
            SubscriptionExpiry = "";
        return SubscriptionExpiry;
    }

    public void setSubscriptionExpiry(String subscriptionExpiry) {
        SubscriptionExpiry = subscriptionExpiry;
    }

    public String getDateOfFailure() {
        if(DateOfFailure == null)
            DateOfFailure = "";
        return DateOfFailure;

    }

    public void setDateOfFailure(String dateOfFailure) {
        DateOfFailure = dateOfFailure;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getSubscriptionID() {
        return SubscriptionID;
    }

    public void setSubscriptionID(String subscriptionID) {
        SubscriptionID = subscriptionID;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public int getProfileID() {
        return ProfileID;
    }

    public void setProfileID(int profileID) {
        ProfileID = profileID;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    private int ProfileID;
    private int Status;
    private int ID;


}
