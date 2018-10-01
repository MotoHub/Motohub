package online.motohub.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class LocalVideoModel implements Parcelable {

    private Uri videoFile;
    private String name;
    private String path;
    private Bitmap thumbnail;

    public LocalVideoModel() {
    }

    private LocalVideoModel(Parcel in) {
        videoFile = in.readParcelable(Uri.class.getClassLoader());
        name = in.readString();
        path = in.readString();
        thumbnail = in.readParcelable(Bitmap.class.getClassLoader());

    }

    public static final Creator<LocalVideoModel> CREATOR = new Creator<LocalVideoModel>() {
        @Override
        public LocalVideoModel createFromParcel(Parcel in) {
            return new LocalVideoModel(in);
        }

        @Override
        public LocalVideoModel[] newArray(int size) {
            return new LocalVideoModel[size];
        }
    };

    public Uri getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(Uri videoFile) {
        this.videoFile = videoFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(videoFile, i);
        parcel.writeString(name);
        parcel.writeString(path);
        parcel.writeParcelable(thumbnail, i);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
