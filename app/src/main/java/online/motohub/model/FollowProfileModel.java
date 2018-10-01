package online.motohub.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class FollowProfileModel implements Serializable {


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
}
