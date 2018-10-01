package online.motohub.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GalleryVideoModel implements Parcelable {

    @SerializedName("resource")
    private List<GalleryVideoResModel> resModelList;

    private GalleryVideoModel(Parcel in) {
        resModelList = in.createTypedArrayList(GalleryVideoResModel.CREATOR);
    }

    public static final Creator<GalleryVideoModel> CREATOR = new Creator<GalleryVideoModel>() {
        @Override
        public GalleryVideoModel createFromParcel(Parcel in) {
            return new GalleryVideoModel(in);
        }

        @Override
        public GalleryVideoModel[] newArray(int size) {
            return new GalleryVideoModel[size];
        }
    };

    public List<GalleryVideoResModel> getResModelList() {
        return resModelList;
    }

    public void setResModelList(List<GalleryVideoResModel> resModelList) {
        this.resModelList = resModelList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(resModelList);
    }

}
