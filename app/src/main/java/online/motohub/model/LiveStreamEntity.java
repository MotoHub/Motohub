package online.motohub.model;


import java.io.Serializable;

import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class LiveStreamEntity implements Serializable {

    private int ID;
    private String StreamName;
    private int CreatedProfileID;
    private int StreamProfileID;
    private int EventID;

    private ProfileResModel profiles_by_StreamProfileID;
    private PromotersResModel promoter_by_StreamProfileID;

    public int getID() {
        return ID;
    }

    public String getStreamName() {
              return StreamName;
    }

    public ProfileResModel getProfiles_by_StreamProfileID() {
        return profiles_by_StreamProfileID;
    }

    public PromotersResModel getPromoter_by_StreamProfileID() {
        return promoter_by_StreamProfileID;
    }

    public int getCreatedProfileID() {
        return CreatedProfileID;
    }

    public int getStreamProfileID() {
        return StreamProfileID;
    }

    public int getEventID() {
        return EventID;
    }
}
