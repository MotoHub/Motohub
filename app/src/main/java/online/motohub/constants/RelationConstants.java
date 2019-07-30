package online.motohub.constants;

public class RelationConstants {

    /**
     * Should not edit this class without getting permission
     */

    public static final String PROFILE_RELATION_ALL = "vehicleinfolikes_by_LikedProfileID,followprofile_by_FollowProfileID,followprofile_by_ProfileID," +
            "promoterfollower_by_ProfileID,blockeduserprofiles_by_ProfileID,blockeduserprofiles_by_BlockedProfileID";


    public static final String POST_FEED_RELATION = "profiles_by_WhoPostedProfileID, postlikes_by_PostID, postlikes_by_PostID," +
            "postshares_by_OriginalPostID, promoter_by_WhoPostedProfileID, promoter_by_ProfileID, postshares_by_NewSharedPostID," +
            " videoshares_by_NewSharedPostID, profiles_by_ProfileID,clubgroup_by_ClubUserID,promoterfollower_by_PromoterUserID,track_by_user_id";
}
