package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SingleChatUnreadMsgModel {

    @SerializedName("resource")
    @Expose
    private List<SingleChatMsg> resource = null;

    public List<SingleChatMsg> getResource() {
        return resource;
    }

    public void setResource(List<SingleChatMsg> resource) {
        this.resource = resource;
    }

}