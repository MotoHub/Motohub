package online.motohub.model;

import java.util.ArrayList;

public class LiveStreamPaymentResponse {

    private ArrayList<LiveStreamPaymentEntity> resource;

    public ArrayList<LiveStreamPaymentEntity> getResource() {
        if (resource == null)
            resource = new ArrayList<>();
        return resource;
    }
}
