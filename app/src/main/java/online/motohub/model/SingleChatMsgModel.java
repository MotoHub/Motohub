package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SingleChatMsgModel {

    @SerializedName("resource")
    @Expose
    private List<SingleChatMsgResModel> resource = null;

    @SerializedName("meta")
    @Expose
    private MetaModel meta;

    public List<SingleChatMsgResModel> getResource() {
        return resource;
    }

    public void setResource(List<SingleChatMsgResModel> resource) {
        this.resource = resource;
    }

    public MetaModel getMeta() {
        return meta;
    }

    public void setMeta(MetaModel meta) {
        this.meta = meta;
    }

}
