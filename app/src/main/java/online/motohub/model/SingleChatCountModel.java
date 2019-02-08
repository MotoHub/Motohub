package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SingleChatCountModel {

    @SerializedName("resource")
    @Expose
    private List<SingleChatUnreadCountModel> resource = null;

    public List<SingleChatUnreadCountModel> getResource() {
        return resource;
    }

    public void setResource(List<SingleChatUnreadCountModel> resource) {
        this.resource = resource;
    }

}