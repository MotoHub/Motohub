package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PostsModel {

    public static final String POST_MODEL = "PostModel";
    public static final String POST_ID = "ID";
    public static final String PROFILE_ID = "ProfileID";
    public static final String WHO_POSTED_PROFILE_ID = "WhoPostedProfileID";
    public static final String WHO_POSTED_USER_ID = "WhoPostedUserID";
    public static final String POST_TEXT = "PostText";
    public static final String POST_PICTURE = "PostPicture";
    public static final String TAGGED_PROFILE_ID = "TaggedProfileID";
    public static final String PROFILES_BY_WHO_POSTED_PROFILE_ID = "profiles_by_WhoPostedProfileID";
    public static final String LIKES_BY_POSTID = "postlikes_by_PostID";
    public static final String COMMENTS_BY_POSTID = "postcomments_by_PostID";
    public static final String SHARES_BY_POSTID = "postshares_by_OriginalPostID";
    public static final String CREATED_AT = "CreatedAt";
    public static final String SHARED_PROFILE_ID = "SharedProfileID";
    public static final String WHO_SHARED_PROFILE_ID = "WhoSharedProfileID";
    public static final String NEW_SHARED_POST_ID = "NewSharedPostID";
    public static final String OLD_POST_ID = "OldPostID";
    public static final String IS_NEWS_FEED_POST = "IsNewsFeedPost";
    public static final String PostVideoThumbnailurl = "PostVideoThumbnailUrl";
    public static final String PostVideoURL = "PostVideoUrl";
    public static final String PROMOTER_BY_PROFILE_ID = "promoter_by_ProfileID";
    public static final String PROMOTER_BY_WHO_POSTED_PROFILEID = "promoter_by_WhoPostedProfileID";
    public static final String USER_TYPE = "user_type";
    public static final String IS_UPDATE = "is_update";
    public static final String POST_LIST = "POST_LIST";
    public static final String SHARES_BY_NEW_SHARED_POST_ID = "postshares_by_NewSharedPostID";
    public static final String VIDEO_SHARES_BY_NEW_SHARED_POST_ID = "videoshares_by_NewSharedPostID";
    public static final String PROFILES_BY_PROFILE_ID = "profiles_by_ProfileID";
    public static final String REPORT_STATUS = "ReportStatus";
    public static final String TO_SUBSCRIBED_USER_ID = "to_subscribedUserID";
    public static final String SHARED_TEXT = "SharedText";
    public static final String viewCount = "ViewCount";


    @SerializedName("resource")
    @Expose
    private ArrayList<PostsResModel> mResource = null;

    @SerializedName("meta")
    @Expose
    private MetaModel meta;

    public ArrayList<PostsResModel> getResource() {
        return mResource;
    }

    public void setResource(ArrayList<PostsResModel> resource) {
        this.mResource = resource;
    }

    public MetaModel getMeta() {
        return meta;
    }

    public void setMeta(MetaModel meta) {
        this.meta = meta;
    }

}