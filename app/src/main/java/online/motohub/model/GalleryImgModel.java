package online.motohub.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GalleryImgModel implements Parcelable {

    public static final String ID = "ID";
    public static final String USER_ID = "UserID";
    public static final String MOTO_ID = "MotoID";
    public static final String GALLERY_IMG = "GalleryImage";
    public static final String PROFILE_ID = "ProfileID";
    public static final String THUMBNAIL = "Thumbnail";
    public static final String VIDEO_URL = "VideoUrl";
    public static final String USER_TYPE = "UserType";
    public static final String CAPTION = "Caption";
    public static final Creator<GalleryImgModel> CREATOR = new Creator<GalleryImgModel>() {
        @Override
        public GalleryImgModel createFromParcel(Parcel in) {
            return new GalleryImgModel(in);
        }

        @Override
        public GalleryImgModel[] newArray(int size) {
            return new GalleryImgModel[size];
        }
    };
    @SerializedName("resource")
    private List<GalleryImgResModel> galleryResModelList;

    public GalleryImgModel() {
    }

    private GalleryImgModel(Parcel in) {
        galleryResModelList = in.createTypedArrayList(GalleryImgResModel.CREATOR);
    }

    public List<GalleryImgResModel> getGalleryResModelList() {
        return galleryResModelList;
    }

    public void setGalleryResModelList(List<GalleryImgResModel> galleryResModelList) {
        this.galleryResModelList = galleryResModelList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(galleryResModelList);
    }

}
