package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SessionModel {

    @SerializedName("session_token")
    @Expose
    private String mSessionToken;

    @SerializedName("session_id")
    @Expose
    private String mSessionID;

    public String getSessionToken() {
        return mSessionToken;
    }

    public String getSessionId() {
        return mSessionID;
    }

}
