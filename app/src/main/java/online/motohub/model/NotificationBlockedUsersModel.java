package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class NotificationBlockedUsersModel implements Serializable {

    public static String UserID = "UserID";
    public static String ProfileID = "ProfileID";
    public static String PostID = "PostID";

    @SerializedName("UserID")
    @Expose
    private int mUserID;

    @SerializedName("ProfileID")
    @Expose
    private int mProfileID;

    @SerializedName("PostID")
    @Expose
    private int mPostID;

    @SerializedName("resource")
    @Expose
    private ArrayList<NotificationBlockedUsersModel> mResource = null;

    public ArrayList<NotificationBlockedUsersModel> getResource() {
        if (mResource == null)
            mResource = new ArrayList<>();

        return mResource;
    }

    public void setmResource(ArrayList<NotificationBlockedUsersModel> mResource) {
        this.mResource = mResource;
    }

    public int getmUserID() {
        return mUserID;
    }

    public void setmUserID(int mUserID) {
        this.mUserID = mUserID;
    }

    public int getmProfileID() {
        return mProfileID;
    }

    public void setmProfileID(int mProfileID) {
        this.mProfileID = mProfileID;
    }

    public int getmPostID() {
        return mPostID;
    }

    public void setmPostID(int mPostID) {
        this.mPostID = mPostID;
    }

}