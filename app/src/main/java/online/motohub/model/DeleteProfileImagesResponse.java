package online.motohub.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Prithiv on 5/16/2018.
 */

public class DeleteProfileImagesResponse {

    @SerializedName("resource")
    private List<Resource> resource;

    public List<Resource> getResource() {
        return resource;
    }

    public void setResource(List<Resource> resource) {
        this.resource = resource;
    }

    public static class Resource {
        @SerializedName("ID")
        private int ID;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }
    }
}
