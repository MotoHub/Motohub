package online.motohub.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageModel implements Parcelable {

    public static final String POST_IMAGE = "PostImage";
    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };
    @SerializedName("resource")
    private List<ImageResModel> mModels;

    public ImageModel() {
    }

    private ImageModel(Parcel in) {
        mModels = in.createTypedArrayList(ImageResModel.CREATOR);
    }

    public List<ImageResModel> getmModels() {
        return mModels;
    }

    public void setmModels(List<ImageResModel> mModels) {
        this.mModels = mModels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(mModels);
    }

}
