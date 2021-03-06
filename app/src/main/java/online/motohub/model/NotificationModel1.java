package online.motohub.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class NotificationModel1 {

    @SerializedName("mainObj")
    private JSONObject mainObj;
    @SerializedName("Msg")
    private String Msg;
    @SerializedName("Type")
    private String Type;
    @SerializedName("Details")
    private JSONObject Details;
    @SerializedName("forceNotification")
    private boolean forceNotification;
    @SerializedName("testNotification")
    private boolean testNotification;
    @SerializedName("notificationID")
    private int notificationID;
    public JSONObject getMainObj() {
        return mainObj;
    }

    public void setMainObj(JSONObject mainObj) {
        this.mainObj = mainObj;
    }

    public String getMsg() {
        if (testNotification) {
            return "TestNotification";
        }
        try {
            Msg = URLDecoder.decode(mainObj.getString("Msg"), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Msg;
    }

    public String getType() {
        if (testNotification) {
            return "TestNotification";
        }
        try {
            Type = mainObj.getString("Type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Type;
    }

    public JSONObject getDetails() {
        try {
            Details = mainObj.getJSONObject("Details");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Details;
    }

    public boolean isForceNotification() {
        return forceNotification;
    }

    public void setForceNotification(boolean forceNotification) {
        this.forceNotification = forceNotification;
    }

    public boolean isTestNotification() {
        return testNotification;
    }

    public void setTestNotification(boolean testNotification) {
        this.testNotification = testNotification;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }
}
