package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MetaModel {

    @SerializedName("count")
    @Expose
    private Integer mCount;

    @SerializedName("next")
    @Expose
    private Integer mNext;

    public Integer getCount() {
        return mCount;
    }

    public void setCount(Integer count) {
        this.mCount = count;
    }

    public Integer getNext() {
        return mNext;
    }

    public void setNext(Integer next) {
        this.mNext = next;
    }

}
