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

    public JSONObject getMainObj() {
        return mainObj;
    }

    public void setMainObj(JSONObject mainObj) {
        this.mainObj = mainObj;
    }

    public String getMsg() {
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
}
