package online.motohub.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.util.Utility;

public class PostsResModel implements Serializable {

    @SerializedName("ID")
    private int ID;

    @SerializedName("ViewCount")
    private int ViewCount;

    @SerializedName("PostVideoThumbnailUrl")
    private String PostVideoThumbnailURL;

    @SerializedName("PostVideoUrl")
    private String PostVideoURL;

    @SerializedName("ProfileID")
    private int ProfileID;

    @SerializedName("WhoPostedProfileID")
    private int WhoPostedProfileID;

    @SerializedName("WhoPostedUserID")
    private int WhoPostedUserID;

    @SerializedName("PostText")
    private String PostText;

    @SerializedName("PostPicture")
    private String PostPicture;

    @SerializedName("TaggedProfileID")
    private String TaggedProfileID;

    @SerializedName("SharedProfileID")
    private String SharedProfileID;

    @SerializedName("WhoSharedProfileID")
    private String WhoSharedProfileID;

    @SerializedName("NewSharedPostID")
    private String NewSharedPostID;

    @SerializedName("OldPostID")
    private int OldPostID;

    @SerializedName("CreatedAt")
    private String CreatedAt;

    @SerializedName("IsNewsFeedPost")
    private Boolean IsNewsFeedPost;

    @SerializedName("user_type")
    private String userType;

    @SerializedName("ReportStatus")
    private boolean ReportStatus;

    @SerializedName("SharedText")
    private String SharedText;

    @SerializedName("Post_on")
    private String Post_on;

    @SerializedName("is_scheduled")
    private boolean is_scheduled;

    @SerializedName("profiles_by_WhoPostedProfileID")

    private ProfileResModel mProfilesByWhoPostedProfileID;
    @SerializedName("profiles_by_ProfileID")

    private ProfileResModel mProfilesByProfileID;
    @SerializedName("postcomments_by_PostID")

    private ArrayList<FeedCommentModel> mPostComments;
    @SerializedName("postlikes_by_PostID")

    private ArrayList<FeedLikesModel> mPostLikes;
    @SerializedName("postshares_by_OriginalPostID")

    private ArrayList<FeedShareModel> mPostSharesList;
    @SerializedName("postshares_by_NewSharedPostID")

    private FeedShareModel mNewSharedPost;
    @SerializedName("promoter_by_ProfileID")

    private PromotersResModel mPromoterByProfileID;
    @SerializedName("promoter_by_WhoPostedProfileID")

    private PromotersResModel mPromoterByWhoPostedProfileID;
    @SerializedName("videoshares_by_NewSharedPostID")

    private VideoShareModel mVideoSharesByNewSharedPostID;
    @SerializedName("postnotificationblockedusers_by_PostID")

    private ArrayList<NotificationBlockedUsersModel> mNotificationBlockedUsersID;

    public String getSharedTxt() {
        if (SharedText == null)
            SharedText = "";
        return SharedText;
    }

    public void setSharedTxt(String mSharedTxt) {
        this.SharedText = mSharedTxt;
    }

    public ArrayList<NotificationBlockedUsersModel> getmNotificationBlockedUsersID() {
        if (mNotificationBlockedUsersID == null)
            mNotificationBlockedUsersID = new ArrayList<>();
        return mNotificationBlockedUsersID;
    }

    public void setmNotificationBlockedUsersID(ArrayList<NotificationBlockedUsersModel> mNotificationBlockedUsersID) {
        this.mNotificationBlockedUsersID = mNotificationBlockedUsersID;
    }

    public int getViewCount() {
        return ViewCount;
    }

    public void setViewCount(int viewCount) {
        this.ViewCount = viewCount;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(int iD) {
        this.ID = iD;
    }

    public int getProfileID() {
        return ProfileID;
    }

    public void setProfileID(int profileID) {
        this.ProfileID = profileID;
    }

    public int getWhoPostedProfileID() {
        return WhoPostedProfileID;
    }

    public void setWhoPostedProfileID(int whoPostedProfileID) {
        this.WhoPostedProfileID = whoPostedProfileID;
    }

    public int getWhoPostedUserID() {
        return WhoPostedUserID;
    }

    public void setWhoPostedUserID(int userID) {
        this.WhoPostedUserID = userID;
    }

    public String getPostText() {
        if (PostText == null)
            PostText = "";
        return PostText;
    }

    public void setPostText(String postText) {
        this.PostText = postText;
    }

    public String getPostPicture() {
        if (PostPicture == null)
            PostPicture = "";
        return PostPicture;
    }

    public void setPostPicture(String postPicture) {
        this.PostPicture = postPicture;
    }

    public String getDateCreatedAt() {
        if (is_scheduled) {
            CreatedAt = getPost_on();
        }
        return Utility.getInstance().findTime(CreatedAt);
    }

    public void setDateCreatedAt(String dateCreatedAt) {
        this.CreatedAt = dateCreatedAt;
    }

    public String getTaggedProfileID() {
        return TaggedProfileID;
    }

    public void setTaggedProfileID(String taggedProfileID) {
        this.TaggedProfileID = taggedProfileID;
    }

    public ProfileResModel getProfilesByWhoPostedProfileID() {
        return mProfilesByWhoPostedProfileID;
    }

    public void setProfilesByWhoPostedProfileID(ProfileResModel profilesByProfileID) {
        this.mProfilesByWhoPostedProfileID = profilesByProfileID;
    }

    public ArrayList<FeedCommentModel> getPostComments() {
        if (mPostComments == null)
            mPostComments = new ArrayList<>();
        return this.mPostComments;
    }

    public void setFeedComments(ArrayList<FeedCommentModel> comments) {

        this.mPostComments = comments;
    }

    public ArrayList<FeedLikesModel> getPostLikes() {
        if (mPostLikes == null)
            mPostLikes = new ArrayList<>();
        return this.mPostLikes;
    }

    public void setPostLikes(ArrayList<FeedLikesModel> likes) {

        this.mPostLikes = likes;
    }

    public ArrayList<FeedShareModel> getPostShares() {
        if (mPostSharesList == null)
            mPostSharesList = new ArrayList<>();
        return this.mPostSharesList;
    }

    public void setPostShares(ArrayList<FeedShareModel> shares) {

        this.mPostSharesList = shares;
    }

    public String getSharedProfileID() {
        return SharedProfileID;
    }

    public void setSharedProfileID(String mSharedProfileID) {
        this.SharedProfileID = mSharedProfileID;
    }

    public String getWhoSharedProfileID() {
        return WhoSharedProfileID;
    }

    public void setWhoSharedProfileID(String mWhoSharedProfileID) {
        this.WhoSharedProfileID = mWhoSharedProfileID;
    }

    public String getNewSharedPostID() {
        if (NewSharedPostID == null)
            NewSharedPostID = "";
        return NewSharedPostID;
    }

    public void setNewSharedPostID(String mNewSharedPostID) {
        this.NewSharedPostID = mNewSharedPostID;
    }

    public FeedShareModel getNewSharedPost() {
        return mNewSharedPost;
    }

    public void setNewSharedPost(FeedShareModel mNewSharedPost) {
        this.mNewSharedPost = mNewSharedPost;
    }

    public int getOldPostID() {
        return OldPostID;
    }

    public void setOldPostID(int mOldPostID) {
        this.OldPostID = mOldPostID;
    }

    public Boolean getIsNewsFeedPost() {
        return IsNewsFeedPost;
    }

    public void setIsNewsFeedPost(Boolean mIsNewsFeedPost) {
        this.IsNewsFeedPost = mIsNewsFeedPost;
    }

    public String getUserType() {
        if (userType == null)
            userType = "";
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPostVideoThumbnailURL() {
        if (PostVideoThumbnailURL == null) {
            PostVideoThumbnailURL = "";
        } else if (PostVideoThumbnailURL.trim().equals("null")) {
            PostVideoThumbnailURL = "";
        }
        return PostVideoThumbnailURL;
    }

    public void setPostVideoThumbnailURL(String postVideoThumbnailURL) {
        PostVideoThumbnailURL = postVideoThumbnailURL;
    }

    public String getPostVideoURL() {
        return PostVideoURL;
    }

    public void setPostVideoURL(String postVideoURL) {
        PostVideoURL = postVideoURL;
    }

    public PromotersResModel getPromoterByProfileID() {
        return mPromoterByProfileID;
    }

    public void setPromoterByProfileID(PromotersResModel promotersResModel) {
        mPromoterByProfileID = promotersResModel;
    }

    public PromotersResModel getPromoterByWhoPostedProfileID() {
        return mPromoterByWhoPostedProfileID;
    }

    public void setPromoterByWhoPostedProfileID(PromotersResModel mPromoterByWhoPostedProfileID) {
        this.mPromoterByWhoPostedProfileID = mPromoterByWhoPostedProfileID;
    }

    public ProfileResModel getProfilesByProfileID() {
        return mProfilesByProfileID;
    }

    public void setProfilesByProfileID(ProfileResModel mProfilesByProfileID) {
        this.mProfilesByProfileID = mProfilesByProfileID;
    }

    public VideoShareModel getVideoSharesByNewSharedPostID() {
        if (mVideoSharesByNewSharedPostID == null)
            mVideoSharesByNewSharedPostID = new VideoShareModel();
        return mVideoSharesByNewSharedPostID;
    }

    public void setVideoSharesByNewSharedPostID(VideoShareModel mVideoSharesByNewSharedPostID) {
        this.mVideoSharesByNewSharedPostID = mVideoSharesByNewSharedPostID;
    }

    public boolean getReportStatus() {
        return ReportStatus;
    }

    public void setReportStatus(boolean mReportStatus) {
        this.ReportStatus = mReportStatus;
    }

    public String getPost_on() {
        return Post_on;
    }

    public boolean isIs_scheduled() {
        return is_scheduled;
    }
}
