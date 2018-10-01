package online.motohub.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ImageResModel implements Parcelable {

    @SerializedName("name")
    private String name;

    @SerializedName("path")
    private String path;

    @SerializedName("type")
    private String type;

    public ImageResModel() {
    }

    private ImageResModel(Parcel in) {
        name = in.readString();
        path = in.readString();
        type = in.readString();
    }

    public static final Creator<ImageResModel> CREATOR = new Creator<ImageResModel>() {
        @Override
        public ImageResModel createFromParcel(Parcel in) {
            return new ImageResModel(in);
        }

        @Override
        public ImageResModel[] newArray(int size) {
            return new ImageResModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(path);
        parcel.writeString(type);
    }

}
