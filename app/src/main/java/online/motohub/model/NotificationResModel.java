package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationResModel {

    @SerializedName("SenderID")
    @Expose
    private int senderID;

    @SerializedName("ReceiverID")
    @Expose
    private String receiverID;

    @SerializedName("Type")
    @Expose
    private String type;

    @SerializedName("Data")
    @Expose
    private String data;

    @SerializedName("Msg")
    @Expose
    private String msg;

    @SerializedName("Created")
    @Expose
    private String created;

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

}
