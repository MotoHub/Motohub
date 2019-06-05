package online.motohub.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class GalleryImgResModel implements Parcelable {

    public static final Creator<GalleryImgResModel> CREATOR = new Creator<GalleryImgResModel>() {
        @Override
        public GalleryImgResModel createFromParcel(Parcel in) {
            return new GalleryImgResModel(in);
        }

        @Override
        public GalleryImgResModel[] newArray(int size) {
            return new GalleryImgResModel[size];
        }
    };
    @SerializedName("ID")
    private int id;
    @SerializedName("UserID")
    private int userId;
    @SerializedName("MotoID")
    private int motoId;
    @SerializedName("GalleryImage")
    private String galleryImage;

    public GalleryImgResModel() {
    }

    private GalleryImgResModel(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        motoId = in.readInt();
        galleryImage = in.readString();
    }

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

    public int getMotoId() {
        return motoId;
    }

    public void setMotoId(int motoId) {
        this.motoId = motoId;
    }

    public String getGalleryImage() {
        return galleryImage;
    }

    public void setGalleryImage(String galleryImage) {
        this.galleryImage = galleryImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(userId);
        parcel.writeInt(motoId);
        parcel.writeString(galleryImage);
    }

}
