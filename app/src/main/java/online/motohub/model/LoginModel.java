package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginModel implements Serializable {

    public static final String email = "email";
    public static final String password = "password";

    @SerializedName("session_token")
    @Expose
    private String mSessionToken;

    @SerializedName("session_id")
    @Expose
    private String mSessionID;

    @SerializedName("id")
    @Expose
    private int mID;

    @SerializedName("name")
    @Expose
    private String mName;

    @SerializedName("first_name")
    @Expose
    private String mFirstName;

    @SerializedName("last_name")
    @Expose
    private String mLastName;

    @SerializedName("phone")
    @Expose
    private String mPhone;

    @SerializedName("email")
    @Expose
    private String mEmail;

    @SerializedName("is_sys_admin")
    @Expose
    private boolean mIsSysAdmin;

    @SerializedName("last_login_date")
    @Expose
    private String mLastLoginDate;

    @SerializedName("host")
    @Expose
    private String mHost;

    public String getPhone() {
        if (mPhone == null)
            mPhone = "";
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }


    public String getSessionToken() {
        return mSessionToken;
    }

    public String getSessionId() {
        return mSessionID;
    }

    public int getId() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public boolean isSysAdmin() {
        return mIsSysAdmin;
    }

    public String getLastLoginDate() {
        return mLastLoginDate;
    }

    public String getHost() {
        return mHost;
    }

}
