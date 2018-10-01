package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import online.motohub.model.promoter_club_news_media.PromoterSubs;

/**
 * Created by pickzy01 on 05/04/2018.
 */

public class PromoterSubsResModel {

    @SerializedName("resource")
    @Expose
    private ArrayList<PromoterSubs> resource = null;

    public ArrayList<PromoterSubs> getResource() {
        return resource;
    }

    public void setResource(ArrayList<PromoterSubs> resource) {
        this.resource = resource;
    }
}
