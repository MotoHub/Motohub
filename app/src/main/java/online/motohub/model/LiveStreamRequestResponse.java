package online.motohub.model;


import java.util.ArrayList;

public class LiveStreamRequestResponse {

    private ArrayList<LiveStreamRequestEntity> resource;
    private MetaModel meta;
    public ArrayList<LiveStreamRequestEntity> getResource() {
        if(resource==null)
            resource=new ArrayList<>();
        return resource;
    }

    public MetaModel getMeta() {
        return meta;
    }
}
