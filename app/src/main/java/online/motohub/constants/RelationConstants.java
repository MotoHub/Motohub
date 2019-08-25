package online.motohub.constants;

public class RelationConstants {

    /**
     * Should not edit this class without getting Deepan permission
     */

    public static final String PROFILE_RELATION_ALL = "vehicleinfolikes_by_LikedProfileID,followprofile_by_FollowProfileID,followprofile_by_ProfileID," +
            "promoterfollower_by_ProfileID,blockeduserprofiles_by_ProfileID,blockeduserprofiles_by_BlockedProfileID";

    public static final String POST_FEED_RELATION = "profiles_by_WhoPostedProfileID, postlikes_by_PostID," +
            "postshares_by_OriginalPostID, promoter_by_WhoPostedProfileID, promoter_by_ProfileID, postshares_by_NewSharedPostID," +
            " videoshares_by_NewSharedPostID, profiles_by_ProfileID,clubgroup_by_ClubUserID,promoterfollower_by_PromoterUserID,track_by_user_id";

    public static final String EVENT_RELATION = "whosgoing_by_EventID, event_groups_by_EventID, racing_by_EventID, event_registration_question_by_group_id," +
            "event_registration_answer_by_EventID,promoter_by_UserID, livestream_by_EventID, livestreampayment_by_EventID, eventad_by_EventID";

    public static final String ON_DEMAND_VIDEO_RELATION = "promoter_by_UserID,videocomments_by_VideoID,videolikes_by_VideoID,videoshares_by_OriginalVideoID,profiles_by_ProfileID,event_by_EventID";

    public static final String PROMOTER_RELATION = "clubgroup_by_ClubUserID,promoterfollower_by_PromoterUserID,track_by_user_id";
}
