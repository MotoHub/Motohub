package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EventGrpChatMsgModel {

    public static final String PROFILES_BY_SENDER_PROFILE_ID =  "profiles_by_SenderProfileID";
    public static final String PROMOTER_BY_SENDER_USER_ID =  "promoter_by_SenderUserID";

    @SerializedName("resource")
    @Expose
    private List<EventGrpChatMsgResModel> resource = null;

    @SerializedName("meta")
    @Expose
    private MetaModel meta;

    public List<EventGrpChatMsgResModel> getResource() {
        if(resource == null)
            resource = new ArrayList<>();
        return resource;
    }

    public void setResource(List<EventGrpChatMsgResModel> resource) {
        this.resource = resource;
    }

    public MetaModel getMeta() {
        return meta;
    }

    public void setMeta(MetaModel meta) {
        this.meta = meta;
    }

}
