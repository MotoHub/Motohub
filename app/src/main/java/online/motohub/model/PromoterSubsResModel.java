package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import online.motohub.model.promoter_club_news_media.PromoterSubs;

public class PromoterSubsResModel {

    @SerializedName("resource")
    @Expose
    private ArrayList<PromoterSubs> resource = null;

    @SerializedName("meta")
    @Expose
    private MetaModel meta;

    public ArrayList<PromoterSubs> getResource() {
        return resource;
    }

    public void setResource(ArrayList<PromoterSubs> resource) {
        this.resource = resource;
    }

    public MetaModel getMeta() {
        return meta;
    }

    public void setMeta(MetaModel meta) {
        this.meta = meta;
    }

}