package online.motohub.model;

import android.os.Parcel;
import android.os.Parcelable;

public class LocalFolderModel implements Parcelable {
    private int id;
    private String displayName;
    private String path;

    public LocalFolderModel() {
    }

    protected LocalFolderModel(Parcel in) {
        id = in.readInt();
        displayName = in.readString();
        path = in.readString();
    }

    public static final Creator<LocalFolderModel> CREATOR = new Creator<LocalFolderModel>() {
        @Override
        public LocalFolderModel createFromParcel(Parcel in) {
            return new LocalFolderModel(in);
        }

        @Override
        public LocalFolderModel[] newArray(int size) {
            return new LocalFolderModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(displayName);
        parcel.writeString(path);
    }
}
