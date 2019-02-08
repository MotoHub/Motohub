package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NotificationModel {

    @SerializedName("resource")
    @Expose
    private ArrayList<NotificationResModel> resource = null;

    @SerializedName("meta")
    @Expose
    private MetaModel meta;

    public ArrayList<NotificationResModel> getResource() {
        if(resource == null)
            resource = new ArrayList<>();
        return resource;
    }

    public void setResource(ArrayList<NotificationResModel> resource) {
        this.resource = resource;
    }

    public MetaModel getMeta() {
        return meta;
    }

    public void setMeta(MetaModel meta) {
        this.meta = meta;
    }
}
