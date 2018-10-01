package online.motohub.model;

import java.util.List;

/**
 * Created by Prithiv on 5/10/2018.
 */

public class OnDemandVideoUploadedResponse {

    private List<Resource> resource;

    public List<Resource> getResource() {
        return resource;
    }

    public void setResource(List<Resource> resource) {
        this.resource = resource;
    }

    public static class Resource {
        private int ID;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }
    }
}
