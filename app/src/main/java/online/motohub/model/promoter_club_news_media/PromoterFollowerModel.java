package online.motohub.model.promoter_club_news_media;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class PromoterFollowerModel {

    @SerializedName("resource")
    @Expose
    private ArrayList<PromoterFollowerResModel> resource = null;

    @SerializedName("meta")
    @Expose
    private Meta meta;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public ArrayList<PromoterFollowerResModel> getResource() {
        if(resource == null)
            resource = new ArrayList<>();
        return resource;
    }

    public void setResource(ArrayList<PromoterFollowerResModel> resource) {
        this.resource = resource;
    }

    public static class Meta {
        private int next;
        private int count;

        public int getNext() {
            return next;
        }

        public void setNext(int next) {
            this.next = next;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

}
