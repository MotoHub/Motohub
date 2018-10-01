package online.motohub.model;

import java.io.Serializable;

public class VideoUploadModel implements Serializable {
    public static String VideoUploadTable="video_table";
    public static String VideoUploadID="id";
    public static String VideoUploadURL="url";
    public static String VideoUploadThumbnailurl="thumbnailurl";
    public static String VideoPost="text";
    public static String VideoFlag="flag";
    public static String Notificationflag="notificationflag";
    public static String ProfileID="profileid";
    public static String NotificationIsRunning="notificationrunning";
    public static String TaggedProfileID="taggedProfileId";
    public static String UploadUserType="UserType";
    private String UserType ="user";
    private Integer flag;
    private Integer ID;
    private String VideoURL;
    private String ThumbnailURl;
    private String Posts;
    private Integer NotificationFlag;
    private Integer profileID;
    private String taggedProfileID;
    public Integer getProfileID() {
        return profileID;
    }

    public void setProfileID(Integer profileID) {
        this.profileID = profileID;
    }

    public Integer getNotificationflag() {
        return NotificationFlag;
    }

    public void setNotificationflag(Integer notificationflag) {
        NotificationFlag = notificationflag;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getVideoURL() {
        return VideoURL;
    }

    public void setVideoURL(String videoURL) {
        VideoURL = videoURL;
    }

    public String getThumbnailURl() {
        return ThumbnailURl;
    }

    public void setThumbnailURl(String thumbnailURl) {
        ThumbnailURl = thumbnailURl;
    }

    public String getPosts() {
        return Posts;
    }

    public void setPosts(String posts) {
        Posts = posts;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getTaggedProfileID() {
        return taggedProfileID;
    }

    public void setTaggedProfileID(String taggedProfileID) {
        this.taggedProfileID = taggedProfileID;
    }

    public String getUserType() {
        if(UserType == null)
            UserType="";
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }
}