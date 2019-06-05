package online.motohub.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class LocalImgModel implements Parcelable {

    public static final Creator<LocalImgModel> CREATOR = new Creator<LocalImgModel>() {
        @Override
        public LocalImgModel createFromParcel(Parcel in) {
            return new LocalImgModel(in);
        }

        @Override
        public LocalImgModel[] newArray(int size) {
            return new LocalImgModel[size];
        }
    };
    private int id;
    private Uri fileUri;
    private String fileName;
    private String folderName;
    private int folderId;

    public LocalImgModel() {
    }

    protected LocalImgModel(Parcel in) {
        id = in.readInt();
        fileUri = in.readParcelable(Uri.class.getClassLoader());
        fileName = in.readString();
        folderName = in.readString();
        folderId = in.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeParcelable(fileUri, i);
        parcel.writeString(fileName);
        parcel.writeString(folderName);
        parcel.writeInt(folderId);
    }
}
