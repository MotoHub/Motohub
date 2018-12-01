package online.motohub.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class FollowProfileModel1 implements Serializable {

    private FollowProfileModel1.Meta meta;

    @SerializedName("resource")
    private ArrayList<FollowProfileEntity> resource;

    public ArrayList<FollowProfileEntity> getResource() {
        if (resource == null)
            resource = new ArrayList<>();
        return resource;
    }

    public void setResource(ArrayList<FollowProfileEntity> resource) {
        this.resource = resource;
    }

    public FollowProfileModel1.Meta getMeta() {
        return meta;
    }

    public void setMeta(FollowProfileModel1.Meta meta) {
        this.meta = meta;
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