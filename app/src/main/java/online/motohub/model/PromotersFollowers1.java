package online.motohub.model;

import java.util.ArrayList;

public class PromotersFollowers1 {

    private Meta meta;
    private ArrayList<Resource> resource;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public ArrayList<Resource> getResource() {
        return resource;
    }

    public void setResource(ArrayList<Resource> resource) {
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

    public static class Resource {
        private int ID;
        private String FollowRelation;
        private String CreatedAt;
        private int PromoterUserID;
        private int ProfileID;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getFollowRelation() {
            return FollowRelation;
        }

        public void setFollowRelation(String FollowRelation) {
            this.FollowRelation = FollowRelation;
        }

        public String getCreatedAt() {
            return CreatedAt;
        }

        public void setCreatedAt(String CreatedAt) {
            this.CreatedAt = CreatedAt;
        }

        public int getPromoterUserID() {
            return PromoterUserID;
        }

        public void setPromoterUserID(int PromoterUserID) {
            this.PromoterUserID = PromoterUserID;
        }

        public int getProfileID() {
            return ProfileID;
        }

        public void setProfileID(int ProfileID) {
            this.ProfileID = ProfileID;
        }
    }
}
