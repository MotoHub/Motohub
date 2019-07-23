package online.motohub.constants;

import online.motohub.enums.UploadStatus;
import online.motohub.interfaces.CommonInterface;

public class AppConstants {

    public static final String BUCKET_NAME = "motohub";

    public static final String ENCRYPT_KEY = "MotoHUB";

    //Notification Constants
    public static final String REPORT_POST = "ReportPost";
    public static final String REPORT_VIDEO = "ReportVideo";
    public static final String REPORT = "Report";

    public static final String NOTIFICATION_PROFILE_PICTURE = "ProfilePicture";
    public static final String NOTIFICATION_SENDER_NAME = "SenderName";
    public static final String NOTIFICATION_PROFILE_ID = "ProfileID";
    //Nez Time Zone
    public static final String NZ_TIME_ZONE = "Europe/London";

    public static final String POST_ID = "PostID";
    public static final String REPORT_STRING = "ReportString";
    public static final String PROFILE_PICTURE = "profile_picture";
    public static final String GROUP_CHAT_SENDER_NAME = "sender_name";
    public static final String IS_FROM_GROUP_CHAT = "IS_FROM_GROUP_CHAT";
    public static final String VIDEOLIST = "videolist";
    public static final String IS_NEWSFEED_POST = "isNewsFeedPost";
    public static final String CLUB_USER = "club_user";
    public static final String SHOP_USER = "shop_user";
    public static final String TO_SUBSCRIBED_USER_ID = "to_subscribedUserID";

    public static final String FROM_OTHER_PROFILE = "FROM_OTHER_PROFILE";
    public static final String IS_FROM_NOTIFICATION_TRAY = "isFromNotificationTray";
    public static final String PROFILE_ID = "PROFILE_ID";
    public static final String EVENT_ID = "EVENT_ID";
    public static final String MY_FOLLOWINGS = "MY_FOLLOWINGS";
    public static final String MY_PROMOTERS_FOLLOWINGS = "MY_PROMOTERS_FOLLOWINGS";
    public static final String LIVE_BASE_URL = "rtsp://208.109.95.214:1935/live/";
    public static final String VIDEO_PATH = "VIDEO_PATH";
    public static final String IMAGE_PATH = "IMAGE_PATH";
    public static final String DEST_PATH = "DEST_PATH";
    public static final String EVENT_PAYMENT = "EVENT_PAYMENT";
    public static final String LIVE_STREAM_PAYMENT = "LIVE_STREAM_PAYMENT";
    public static final String MY_PROFILE_OBJ = "MY_PROFILE_OBJ";
    public static final String IS_FROM_LIVE_EVENT_CHAT = "IsFromLiveEventChat";
    public static final String IS_FROM_TERMS = "IS_FROM_TERMS";
    public static final String IS_FROM_VEHICLE_INFO = "IsFromVehicleInfo";
    public static final String IS_FOLLOW_RESULT = "IS_FOLLOW_RESULT";
    public static final String IS_FOLLOWERS = "IS_FOLLOWERS";
    public static final String MY_PROFILE_ID = "MY_PROFILE_ID";
    public static final String OTHER_PROFILE_ID = "OTHER_PROFILE_ID";
    public static final String USER_TYPE = "user_type";
    public static final String USER = "user";
    public static final String PROMOTER = "promoter";
    public static final String EVENTS = "Events";
    public static final String TRACK = "track";
    public static final String NEWS_MEDIA = "newsmedia";
    public static final String CLUB = "club";
    public static final String SHOP = "shop";
    public static final String ONDEMAND = "ondemand";
    public static final String ONDEMAND_DATA = "ondemand_data";
    public static final String CAPTION = "Caption";
    public static final String VIDEO_SHARED_POST = "VideoSharedPost";
    public static final String USER_VIDEO_SHARED_POST = "userVideoSharedPost";

    public static final String SHARED_POST = "sharedPost";
    public static final String COMMENT_REPLY_FOR_POST_VIDEOS = "COMMENT_REPLY";
    public static final String VIDEO_ID = "VideoID";

    public static final int POST_COMMENT_REQUEST = 2003;
    public static final int VIDEO_COMMENT_REQUEST = 2004;
    public static final int POST_COMMENT_REPLY_REQUEST = 2005;
    public static final int VIDEO_COMMENT_REPLY_REQUEST = 2006;
    public static final int REPORT_POST_SUCCESS = 2007;
    public static final int REPORT_WRITE_POST_RESPONSE = 2008;
    public static final int FOLLOWERS_FOLLOWING_RESULT = 12345;
    public static final int POST_UPDATE_SUCCESS = 1512;
    public static final int CREATE_PROFILE_RES = 1110;
    public static final int WRITE_POST_REQUEST = 2009;
    public static final int ONDEMAND_REQUEST = 2010;
    public static final int VIEW_COUNT = 2020;

    public static final int IMAGE = 1;
    public static final int VIDEO = 2;
    public static final String POSITION = "position";
    public static final String USER_EVENT_VIDEOS = "usereventvideos";
    public static final String PURCHASE_DIALOG = "PURCHASE_DIALOG";
    public static final String VIDEO_LIST = "VIDEO_LIST";
    public static final String USER_ID = "USER_ID";
    public static final int CREATE_NEW_PAYMENT_REQUEST = 2011;
    public static final String NEW_PAYMENT_CARD_DETAILS = "NEW_PAYMENT_CARD_DETAILS";
    public static final String CARD_SETTINGS = "CARD_SETTINGS";
    public static final String PROMOTER_ID = "PROMOTER_ID";
    public static final String FREE_EVENT = "FREE_EVENT";
    public static final String FREE_SUBSCRIPTION = "FREE_SUBSCRIPTION";

    //TODO this is for Event Status
    public static final int EVENT_STATUS = 2;
    public static final int FAILED_SUBSCRIPTION_STATUS = 1;
    public static final String ACTION_PICK = "motohub.ACTION_PICK";
    public static final String ACTION_MULTIPLE_PICK = "motohub.ACTION_MULTIPLE_PICK";
    public static final String CACHE_DIR = ".MotoHUB/Cache";
    public static final String FILE_CACHE_FOLDER_NAME = "MH" ;

    public static UploadStatus UPLOAD_STATUS =UploadStatus.FAILED;
    public static String TAG = "MOTOHUB";
    public static CommonInterface LIVE_STREAM_CALL_BACK = null;
}