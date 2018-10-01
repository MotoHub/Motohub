package online.motohub.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class GalleryVideoResModel implements Parcelable {

    @SerializedName("ID")
    private int id;

    @SerializedName("UserID")
    private int userId;

    @SerializedName("ProfileID")
    private int profileId;

    @SerializedName("Thumbnail")
    private String thumbnail;

    @SerializedName("VideoUrl")
    private String videoUrl;

    @SerializedName("Caption")
    private String caption;

    private GalleryVideoResModel(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        profileId = in.readInt();
        thumbnail = in.readString();
        videoUrl = in.readString();
        caption = in.readString();
    }

    public static final Creator<GalleryVideoResModel> CREATOR = new Creator<GalleryVideoResModel>() {
        @Override
        public GalleryVideoResModel createFromParcel(Parcel in) {
            return new GalleryVideoResModel(in);
        }

        @Override
        public GalleryVideoResModel[] newArray(int size) {
            return new GalleryVideoResModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(userId);
        parcel.writeInt(profileId);
        parcel.writeString(thumbnail);
        parcel.writeString(videoUrl);
        parcel.writeString(caption);
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
