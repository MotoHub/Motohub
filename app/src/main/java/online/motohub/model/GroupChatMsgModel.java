package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupChatMsgModel {

    public static final String PROFILES_BY_SENDER_PROFILE_ID =  "profiles_by_SenderProfileID";

    @SerializedName("resource")
    @Expose
    private List<GroupChatMsgResModel> resource = null;

    @SerializedName("meta")
    @Expose
    private MetaModel meta;

    public List<GroupChatMsgResModel> getResource() {
        return resource;
    }

    public void setResource(List<GroupChatMsgResModel> resource) {
        this.resource = resource;
    }

    public MetaModel getMeta() {
        return meta;
    }

    public void setMeta(MetaModel meta) {
        this.meta = meta;
    }

}
