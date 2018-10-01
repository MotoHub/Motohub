package online.motohub.retrofit;

import online.motohub.model.EventsModel;
import online.motohub.model.PostsModel;
import online.motohub.model.VideoCommentsModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;

public class APIConstants {
    /**
     * API KEYS
     */

    public static final String ID = "ID";
    public static final String RequestedUserID = "RequestedUserID";
    public static final String RequestedProfileID = "RequestedProfileID";
    public static final String ReceiverProfileID = "ReceiverProfileID";
    public static final String Status = "Status";


    public static final String StreamName = "StreamName";
    public static final String CreatedProfileID = "CreatedProfileID";
    public static final String StreamProfileID = "StreamProfileID";
    public static final String EventID = "EventID";
    public static final String UserType = "UserType";
    public static final String user_type = "user_type";
    public static final String user_id = "user_id";
    public static final String TransactionID = "TransactionID";
    public static final String Amount = "Amount";
    public static final String email = "email";
    public static final String new_password = "new_password";
    public static final String code = "code";

    public static final String PromoterID = "PromoterID";
    public static final String ViewUserID = "ViewUserID";

    public static final String Priority = "Priority";
    public static final String ProfileID = "ProfileID";
    public static final String BlockedProfileID = "BlockedProfileID";
    public static final String UniqueRelation = "UniqueRelation";


    public static final String FollowProfileID = "FollowProfileID";
    public static final String FollowRelation = "FollowRelation";

    public static final String FromProfileID = "FromProfileID";
    public static final String ToProfileID = "ToProfileID";
    public static final String FromUserID = "FromUserID";
    public static final String ToUserID = "ToUserID";
    public static final String Message = "Message";
    public static final String Message_Type = "msg_type";
    public static final String Image_Url = "photo_message";


    /**
     * API RELATION
     */

    public static final String VEHICLE_INFO_LIKE_RELATED = "profiles_by_WhoLikedProfileID";

    public static final String profiles_by_ProfileID = "profiles_by_ProfileID";

    public static final String profiles_by_RequestedProfileID = "profiles_by_RequestedProfileID";
    public static final String profiles_by_ReceiverProfileID = "profiles_by_ReceiverProfileID";
    public static final String profiles_by_StreamProfileID = "profiles_by_StreamProfileID";
    public static final String promoter_by_StreamProfileID = "promoter_by_StreamProfileID";
    public static final String livestream_by_StreamProfileID = "livestream_by_StreamProfileID";
    public static final String livestreampayment_by_PromoterID = "livestreampayment_by_PromoterID";
    public static final String livestreamrequest_by_ReceiverProfileID = "livestreamrequest_by_ReceiverProfileID";
    public static final String livestreamrequest_by_RequestedProfileID = "livestreamrequest_by_RequestedProfileID";

    /**
     * Profile Relation
     */
    private static final String pushtoken_by_UserID = "pushtoken_by_UserID";
    private static final String vehicleinfolikes_by_LikedProfileID = "vehicleinfolikes_by_LikedProfileID";
    private static final String promoterfollower_by_ProfileID = "promoterfollower_by_ProfileID";
    private static final String followprofile_by_FollowProfileID = "followprofile_by_FollowProfileID";
    private static final String followprofile_by_ProfileID = "followprofile_by_ProfileID";
    private static final String blockeduserprofiles_by_ProfileID = "blockeduserprofiles_by_ProfileID";
    private static final String blockeduserprofiles_by_BlockedProfileID = "blockeduserprofiles_by_BlockedProfileID";

    /**
     * Feed Post Relation
     */

    public static final String PROFILES_BY_WHO_POSTED_PROFILE_ID = "profiles_by_WhoPostedProfileID";
    public static final String LIKES_BY_POSTID = "postlikes_by_PostID";
    public static final String COMMENTS_BY_POSTID = "postcomments_by_PostID";
    public static final String SHARES_BY_POSTID = "postshares_by_OriginalPostID";
    public static final String PROMOTER_BY_WHO_POSTED_PROFILEID = "promoter_by_WhoPostedProfileID";
    public static final String PROMOTER_BY_PROFILE_ID = "promoter_by_ProfileID";
    public static final String SHARES_BY_NEW_SHARED_POST_ID = "postshares_by_NewSharedPostID";
    public static final String VIDEO_SHARES_BY_NEW_SHARED_POST_ID = "videoshares_by_NewSharedPostID";
    public static final String PROFILES_BY_PROFILE_ID = "profiles_by_ProfileID";

    /**
     * Event Relation
     */

    public static final String PROFILE_RELATION_PUSH = pushtoken_by_UserID;

    public static final String PROFILE_RELATION_FOLLOW = followprofile_by_FollowProfileID
            + "," + followprofile_by_ProfileID;

    public static final String PROFILE_RELATION_FOLLOW_BLOCK = followprofile_by_FollowProfileID
            + "," + followprofile_by_ProfileID
            + "," + blockeduserprofiles_by_ProfileID
            + "," + blockeduserprofiles_by_BlockedProfileID;

    public static final String PROFILE_RELATION_ALL = vehicleinfolikes_by_LikedProfileID
            + "," + followprofile_by_FollowProfileID
            + "," + followprofile_by_ProfileID
            + "," + promoterfollower_by_ProfileID
            + "," + blockeduserprofiles_by_ProfileID
            + "," + blockeduserprofiles_by_BlockedProfileID;

    public static final String POST_FEED_RELATION = PROFILES_BY_WHO_POSTED_PROFILE_ID
            + "," + COMMENTS_BY_POSTID
            + "," + LIKES_BY_POSTID
            + "," + SHARES_BY_POSTID
            + "," + PROMOTER_BY_WHO_POSTED_PROFILEID
            + "," + PROMOTER_BY_PROFILE_ID
            + "," + SHARES_BY_NEW_SHARED_POST_ID
            + "," + VIDEO_SHARES_BY_NEW_SHARED_POST_ID
            + "," + PROFILES_BY_PROFILE_ID;

    public static final String EVENT_RELATION = EventsModel.WHO_IS_GOING_BY_EVENT_ID
            + "," + EventsModel.EVENT_GROUPS_BY_EVENT_ID
            + "," + EventsModel.RACING_BY_EVENT_ID
            + "," + EventsModel.EVENT_REGISTRATION_QUESTION_BY_GROUP_ID
            + "," + EventsModel.EVENT_REGISTRATION_ANSWER_BY_EVENT_ID
            + "," + EventsModel.PROMOTER_BY_USER_ID
            + "," + EventsModel.LIVESTREAM_BY_EVENTID
            + "," + EventsModel.LIVESTREAMPAYMENT_BY_EVENTID;


    public static final String mVideoCommentRelated = VideoCommentsModel.COMMENT_LIKES_BY_COMMENT_ID + " , " +
            VideoCommentsModel.COMMENT_REPLY_BY_COMMENT_ID + " , " + VideoCommentsModel.PROFILES_BY_PROFILE_ID;

    public static final String mProfileRelated = "blockeduserprofiles_by_BlockedProfileID,followprofile_by_FollowProfileID," +
            "followprofile_by_ProfileID,promoterfollower_by_ProfileID,vehicleinfolikes_by_LikedProfileID";
    public static final String mVideoCommentReplyRelated = "profiles_by_ProfileID,promoter_by_ProfileID,videoreplylike_by_ReplyID,videocomments_by_CommentID";

  /*  public static final String mPromoterGalleryRelated = PromotersModel.PROMOTER_BY_USERID + "," + PromotersModel.VIDEO_COMMENTS_BY_VIDEO_ID +
            "," + PromotersModel.VIDEO_LIKES_BY_VIDEO_ID + "," + PromotersModel.VIDEO_SHARES_BY_ORIGINAL_VIDEO_ID + "," + profiles_by_ProfileID + ",event_by_EventID";*/

    public static final String mPromoterGalleryRelated = "promoter_by_UserID,videocomments_by_VideoID,videolikes_by_VideoID,videoshares_by_OriginalVideoID,profiles_by_ProfileID,event_by_EventID";

    public static final String mPostCommentReplyRelated = "postcomments_by_CommentID,Profiles By ProfileID,promoter_by_ProfileID,replylike_by_ReplyID";

    public static final String PROMOTER_RELATION = "clubgroup_by_ClubUserID,promoterfollower_by_PromoterUserID,track_by_user_id";
}
