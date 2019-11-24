package online.motohub.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GalleryVideoModel implements Parcelable {

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
    @SerializedName("resource")
    private ArrayList<GalleryVideoResModel> resource;
    @SerializedName("meta")
    @Expose
    private MetaModel meta;

    private GalleryVideoModel(Parcel in) {
        resource = in.createTypedArrayList(GalleryVideoResModel.CREATOR);
    }

    public MetaModel getMeta() {
        return meta;
    }

    public void setMeta(MetaModel meta) {
        this.meta = meta;
    }

    public ArrayList<GalleryVideoResModel> getResource() {
        return resource;
    }

    public void setResource(ArrayList<GalleryVideoResModel> resource) {
        this.resource = resource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(resource);
    }

}
