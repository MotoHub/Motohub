package online.motohub.model;


import java.io.Serializable;

public class LiveStreamRequestEntity implements Serializable {

    private int ID;
    private int RequestedUserID;
    private int RequestedProfileID;
    private int ReceiverProfileID;
    private int Status;

    private ProfileResModel profiles_by_RequestedProfileID;
    private ProfileResModel profiles_by_ReceiverProfileID;

    public int getID() {
        return ID;
    }

    public int getRequestedUserID() {
        return RequestedUserID;
    }

    public int getRequestedProfileID() {
        return RequestedProfileID;
    }

    public int getReceiverProfileID() {
        return ReceiverProfileID;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public ProfileResModel getProfiles_by_RequestedProfileID() {
        return profiles_by_RequestedProfileID;
    }

    public ProfileResModel getProfiles_by_ReceiverProfileID() {
        return profiles_by_ReceiverProfileID;
    }
}
