package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class PromoterVideoModel implements Serializable {

    private ArrayList<Resource> resource;

    public ArrayList<Resource> getResource() {
        if (resource == null)
            resource = new ArrayList<>();
        return resource;
    }

    @SerializedName("meta")
    @Expose
    private MetaModel meta;

    public MetaModel getMeta() {
        return meta;
    }

    public void setResource(ArrayList<Resource> resource) {
        this.resource = resource;
    }

    public class Resource implements Serializable {

        private String UserType;

        private String Caption;

        public PromotersResModel promoter_by_UserID;

        public ProfileResModel profiles_by_ProfileID;

        private String ProfileID;

        private int ID;

        private String VideoUrl;

        private String UserID;

        private String Thumbnail;

        private String CreatedAt;

        private int ViewCount;

        private ArrayList<VideoLikesModel> videolikes_by_VideoID;

        private EventsResModel event_by_EventID;

        private ArrayList<VideoCommentsModel> videocomments_by_VideoID;

        private ArrayList<VideoShareModel> videoshares_by_OriginalVideoID;


        public String getUserType() {
            if (UserType == null)
                UserType = "";
            return UserType;
        }

        public void setUserType(String UserType) {
            this.UserType = UserType;
        }

        public String getCaption() {
            if (Caption == null) {
                Caption = "";
            }
            return Caption;
        }

        public void setCaption(String Caption) {
            this.Caption = Caption;
        }

        public ProfileResModel getProfiles_by_ProfileID() {
            return profiles_by_ProfileID;
        }

        public void setProfiles_by_ProfileID(ProfileResModel profiles_by_ProfileID) {
            this.profiles_by_ProfileID = profiles_by_ProfileID;
        }

        public PromotersResModel getPromoter_by_UserID() {
            return promoter_by_UserID;
        }


        public void setPromoter_by_UserID(PromotersResModel promoter_by_UserID) {
            this.promoter_by_UserID = promoter_by_UserID;
        }

        public String getProfileID() {
            if (ProfileID == null)
                return "";
            return ProfileID;
        }

        public void setProfileID(String ProfileID) {
            this.ProfileID = ProfileID;
        }

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getVideoUrl() {
            if (VideoUrl == null)
                VideoUrl = "";
            return VideoUrl;
        }

        public void setVideoUrl(String VideoUrl) {
            this.VideoUrl = VideoUrl;
        }

        public String getUserID() {
            if (UserID == null)
                return "";
            return UserID;
        }

        public void setUserID(String UserID) {
            this.UserID = UserID;
        }

        public String getThumbnail() {
            return Thumbnail;
        }

        public void setThumbnail(String Thumbnail) {
            this.Thumbnail = Thumbnail;
        }

        public String getCreatedAt() {
            return CreatedAt;
        }

        public void setCreatedAt(String CreatedAt) {
            this.CreatedAt = CreatedAt;
        }

        public int getViewCount() {
            return ViewCount;
        }

        public void setViewCount(int viewCount) {
            ViewCount = viewCount;
        }

        public ArrayList<VideoLikesModel> getVideolikes_by_VideoID() {
            if (videolikes_by_VideoID == null)
                videolikes_by_VideoID = new ArrayList<>();
            return videolikes_by_VideoID;
        }

        public void setVideolikes_by_VideoID(ArrayList<VideoLikesModel> videolikes_by_VideoID) {
            this.videolikes_by_VideoID = videolikes_by_VideoID;
        }

        public ArrayList<VideoCommentsModel> getVideoComments_by_VideoID() {
            if (videocomments_by_VideoID == null)
                videocomments_by_VideoID = new ArrayList<>();
            return videocomments_by_VideoID;
        }

        public void setVideoComments_by_VideoID(ArrayList<VideoCommentsModel> videoComments_by_VideoID) {
            this.videocomments_by_VideoID = videoComments_by_VideoID;
        }


        public ArrayList<VideoShareModel> getVideoshares_by_OriginalVideoID() {
            if (videoshares_by_OriginalVideoID == null)
                videoshares_by_OriginalVideoID = new ArrayList<>();
            return videoshares_by_OriginalVideoID;
        }

        public void setVideoshares_by_OriginalVideoID(ArrayList<VideoShareModel> videoshares_by_OriginalVideoID) {
            this.videoshares_by_OriginalVideoID = videoshares_by_OriginalVideoID;
        }


        public EventsResModel getEvent_by_EventID() {
            if (event_by_EventID == null)
                event_by_EventID = new EventsResModel();
            return event_by_EventID;
        }

        public void setEvent_by_EventID(EventsResModel event_by_EventID) {
            this.event_by_EventID = event_by_EventID;
        }
    }

}
