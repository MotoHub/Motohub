package online.motohub.model.promoter_club_news_media;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PromotersModel {

    public static final String PROMOTERS_RES_MODEL = "PromotersResModel";
    public static final String NEWS_AND_MEDIA = "newsmedia";
    public static final String CLUB = "club";
    public static final String PROMOTER = "promoter";
    public static final String TRACK = "track";
    public static final String SHOP = "shop";
    public static final String PROMOTER_BY_USERID = "promoter_by_UserID";
    public static final String VIDEO_COMMENTS_BY_VIDEO_ID = "videocomments_by_VideoID";
    public static final String VIDEO_LIKES_BY_VIDEO_ID = "videolikes_by_VideoID";
    public static final String VIDEO_SHARES_BY_VIDEO_ID = "videoshares_by_VideoID";
    public static final String VIDEO_SHARES_BY_ORIGINAL_VIDEO_ID = "videoshares_by_OriginalVideoID";

    @SerializedName("resource")
    @Expose
    private ArrayList<PromotersResModel> resource = null;

    public ArrayList<PromotersResModel> getResource() {
        return resource;
    }

    public void setResource(ArrayList<PromotersResModel> resource) {
        this.resource = resource;
    }

}
