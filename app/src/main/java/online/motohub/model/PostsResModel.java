package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class PostsResModel implements Serializable {

    public boolean isCurrentProfileId;

    @SerializedName("ID")
    @Expose
    private int mID;

    @SerializedName("ViewCount")
    @Expose
    private int mViewCount;

    @SerializedName("PostVideoThumbnailUrl")
    @Expose
    private String PostVideoThumbnailURL;

    @SerializedName("PostVideoUrl")
    @Expose
    private String PostVideoURL;

    @SerializedName("ProfileID")
    @Expose
    private int mProfileID;

    @SerializedName("WhoPostedProfileID")
    @Expose
    private int mWhoPostedProfileID;

    @SerializedName("WhoPostedUserID")
    @Expose
    private int mWhoPostedUserID;

    @SerializedName("PostText")
    @Expose
    private String mPostText;

    @SerializedName("PostPicture")
    @Expose
    private String mPostPicture;

    @SerializedName("TaggedProfileID")
    @Expose
    private String mTaggedProfileID;

    @SerializedName("SharedProfileID")
    @Expose
    private String mSharedProfileID;

    @SerializedName("WhoSharedProfileID")
    @Expose
    private String mWhoSharedProfileID;

    @SerializedName("NewSharedPostID")
    @Expose
    private String mNewSharedPostID;

    @SerializedName("OldPostID")
    @Expose
    private int mOldPostID;

    @SerializedName("CreatedAt")
    @Expose
    private String mDateCreatedAt;

    @SerializedName("IsNewsFeedPost")
    @Expose
    private Boolean mIsNewsFeedPost;

    @SerializedName("user_type")
    @Expose
    private String userType;

    @SerializedName("ReportStatus")
    @Expose
    private boolean mReportStatus;

    @SerializedName("SharedText")
    @Expose
    private String mSharedTxt;

    @SerializedName("Post_on")
    @Expose
    private String Post_on;
    @SerializedName("is_scheduled")
    @Expose
    private boolean is_scheduled;

    @SerializedName("profiles_by_WhoPostedProfileID")
    @Expose
    private ProfileResModel mProfilesByWhoPostedProfileID;

    public String getSharedTxt() {
        if (mSharedTxt == null)
            mSharedTxt = "";
        return mSharedTxt;
    }

    public void setSharedTxt(String mSharedTxt) {
        this.mSharedTxt = mSharedTxt;
    }

    @SerializedName("profiles_by_ProfileID")
    @Expose
    private ProfileResModel mProfilesByProfileID;

    @SerializedName("postcomments_by_PostID")
    @Expose
    private ArrayList<FeedCommentModel> mPostComments;

    @SerializedName("postlikes_by_PostID")
    @Expose
    private ArrayList<FeedLikesModel> mPostLikes;

    @SerializedName("postshares_by_OriginalPostID")
    @Expose
    private ArrayList<FeedShareModel> mPostSharesList;

    @SerializedName("postshares_by_NewSharedPostID")
    @Expose
    private FeedShareModel mNewSharedPost;

    @SerializedName("promoter_by_ProfileID")
    @Expose
    private PromotersResModel mPromoterByProfileID;

    @SerializedName("promoter_by_WhoPostedProfileID")
    @Expose
    private PromotersResModel mPromoterByWhoPostedProfileID;


    @SerializedName("videoshares_by_NewSharedPostID")
    @Expose
    private VideoShareModel mVideoSharesByNewSharedPostID;

    @SerializedName("postnotificationblockedusers_by_PostID")
    @Expose
    private ArrayList<NotificationBlockedUsersModel> mNotificationBlockedUsersID;

    public ArrayList<NotificationBlockedUsersModel> getmNotificationBlockedUsersID() {
        if (mNotificationBlockedUsersID == null)
            mNotificationBlockedUsersID = new ArrayList<>();
        return mNotificationBlockedUsersID;
    }

    public void setmNotificationBlockedUsersID(ArrayList<NotificationBlockedUsersModel> mNotificationBlockedUsersID) {
        this.mNotificationBlockedUsersID = mNotificationBlockedUsersID;
    }

    public int getmViewCount() {
        return mViewCount;
    }

    public void setmViewCount(int mViewCount) {
        this.mViewCount = mViewCount;
    }

    public Integer getID() {
        return mID;
    }

    public void setID(int iD) {
        this.mID = iD;
    }

    public int getProfileID() {
        return mProfileID;
    }

    public void setProfileID(int profileID) {
        this.mProfileID = profileID;
    }

    public int getWhoPostedProfileID() {
        return mWhoPostedProfileID;
    }

    public void setWhoPostedProfileID(int whoPostedProfileID) {
        this.mWhoPostedProfileID = whoPostedProfileID;
    }

    public int getWhoPostedUserID() {
        return mWhoPostedUserID;
    }

    public void setWhoPostedUserID(int userID) {
        this.mWhoPostedUserID = userID;
    }

    public String getPostText() {
        if (mPostText == null)
            mPostText = "";
        return mPostText;
    }

    public void setPostText(String postText) {
        this.mPostText = postText;
    }

    public String getPostPicture() {
        if (mPostPicture == null)
            mPostPicture = "";
        return mPostPicture;
    }

    public void setPostPicture(String postPicture) {
        this.mPostPicture = postPicture;
    }

    public String getDateCreatedAt() {
        if(is_scheduled){
            mDateCreatedAt=getPost_on();
        }
        return mDateCreatedAt;
    }

    public void setDateCreatedAt(String dateCreatedAt) {
        this.mDateCreatedAt = dateCreatedAt;
    }

    public String getTaggedProfileID() {
        return mTaggedProfileID;
    }

    public void setTaggedProfileID(String taggedProfileID) {
        this.mTaggedProfileID = taggedProfileID;
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

    public void setPostLikes(ArrayList<FeedLikesModel> likes) {

        this.mPostLikes = likes;
    }

    public void setFeedComments(ArrayList<FeedCommentModel> comments) {

        this.mPostComments = comments;
    }

    public ArrayList<FeedLikesModel> getPostLikes() {
        if (mPostLikes == null)
            mPostLikes = new ArrayList<>();
        return this.mPostLikes;
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
        return mSharedProfileID;
    }

    public void setSharedProfileID(String mSharedProfileID) {
        this.mSharedProfileID = mSharedProfileID;
    }

    public String getWhoSharedProfileID() {
        return mWhoSharedProfileID;
    }

    public void setWhoSharedProfileID(String mWhoSharedProfileID) {
        this.mWhoSharedProfileID = mWhoSharedProfileID;
    }

    public String getNewSharedPostID() {
        if (mNewSharedPostID == null)
            mNewSharedPostID = "";
        return mNewSharedPostID;
    }

    public void setNewSharedPostID(String mNewSharedPostID) {
        this.mNewSharedPostID = mNewSharedPostID;
    }

    public FeedShareModel getNewSharedPost() {
        return mNewSharedPost;
    }

    public void setNewSharedPost(FeedShareModel mNewSharedPost) {
        this.mNewSharedPost = mNewSharedPost;
    }

    public int getOldPostID() {
        return mOldPostID;
    }

    public void setOldPostID(int mOldPostID) {
        this.mOldPostID = mOldPostID;
    }

    public Boolean getIsNewsFeedPost() {
        return mIsNewsFeedPost;
    }

    public void setIsNewsFeedPost(Boolean mIsNewsFeedPost) {
        this.mIsNewsFeedPost = mIsNewsFeedPost;
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
        return mReportStatus;
    }

    public void setReportStatus(boolean mReportStatus) {
        this.mReportStatus = mReportStatus;
    }

    public String getPost_on() {
        return Post_on;
    }

    public boolean isIs_scheduled() {
        return is_scheduled;
    }
}
