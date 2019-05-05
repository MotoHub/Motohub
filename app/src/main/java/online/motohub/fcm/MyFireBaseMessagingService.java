package online.motohub.fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import online.motohub.model.NotificationModel1;
import online.motohub.util.NotificationUtils1;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = MyFireBaseMessagingService.class.getSimpleName();
    public static final String PUSH_MSG_RECEIVER_ACTION = "online.motohub.PUSH_MSG_RECEIVER_ACTION";
    public static final String ENTRY_JSON_OBJ = "entry";
    public static final String SENDER_NAME = "sender_name";
    public static final String ID = "ID";
    public static final String FROM_USER_ID = "FromUserID";
    public static final String TO_USER_ID = "ToUserID";
    public static final String FROM_PROFILE_ID = "FromProfileID";
    public static final String TO_PROFILE_ID = "ToProfileID";
    public static final String CREATED_AT = "CreatedAt";
    public static final String MSG_TYPE = "msg_type";
    public static final String PHOTO_MESSAGE = "photo_message";
    public static final String NAME = "Name";
    public static final String EVENT_GRP_CHAT_EVENT_NAME = "EventName";
    public static final String EVENT_USER_TYPE = "user_type";
    public static final String GRP_CHAT_SENDER_USER_ID = "SenderUserID";
    public static final String GRP_CHAT_SENDER_PROFILE_ID = "SenderProfileID";
    public static final String EVENT_GRP_CHAT_SENDER_NAME = "sender_name";
    public static final String MESSAGE = "Message";
    public static final String PROFILE_PICTURE = "profile_picture";
    public static final String GRP_CHAT = "GroupChat";
    public static final String GRP_NAME = "GroupName";
    public static final String IS_FROM_NOTIFICATION_TRAY = "isFromNotificationTray";
    public static final String GROUP_SENDER_PIC = "SenderProfilePicture";
    public static final String GROUP_SENDER_NAME = "SenderName";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        System.out.println("Token1: " + s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            NotificationModel1 model1 = new NotificationModel1();
            try {
                JSONObject mJsonObject = new JSONObject(remoteMessage.getData());
                if (mJsonObject.optString("title") != null && mJsonObject.optString("title").equals("Notification")) {
                    JSONObject mEntryObj = new JSONObject((String) mJsonObject.opt("message"));
                    model1.setMainObj(mEntryObj);
                    new NotificationUtils1(this, model1);
                } else {
                    model1.setTestNotification(true);
                    new NotificationUtils1(this, model1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            JSONObject mJsonObject = new JSONObject(remoteMessage.getData());
//            if (mJsonObject.optString("title") != null && mJsonObject.optString("title").equals("Notification")) {
//                try {
//
//                    JSONObject mEntryObj = new JSONObject((String) mJsonObject.opt("message"));
//                    NotificationModel1 model1=new NotificationModel1();
//                    model1.setMainObj(mEntryObj);
////                    int mNotificationPostID, mNotificationEventID;
////                    String mContentTitle;
////                    JSONObject mDetailsObj = mEntryObj.getJSONObject("Details");
////                    mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//                    new NotificationUtils1(this,model1);
//                    return;
//                    //
////                    switch (mEntryObj.getString("Type")) {
////                        case "LIVE_STREAM":
////                            mNotificationPostID = Integer.parseInt(mDetailsObj.get(APIConstants.ID).toString());
////                            //composeStreamNotification(mEntryObj, t his, mNotificationPostID, ViewLiveVideoViewScreen2.class);
////                            composeStreamNotification(mEntryObj, this, mNotificationPostID, ViewProfileActivity.class);
////                            break;
////                        case "LIVE_REQUEST":
////                            mNotificationPostID = Integer.parseInt(mDetailsObj.get(APIConstants.ID).toString());
////                            //composeStreamNotification(mEntryObj, this, mNotificationPostID, ViewRequestUsersActivity.class);
////                            composeStreamNotification(mEntryObj, this, mNotificationPostID, ViewProfileActivity.class);
////                            break;
////                        case "LIVE_ACCEPT":
////                            mNotificationPostID = Integer.parseInt(mDetailsObj.get(APIConstants.ID).toString());
////                            //composeStreamNotification(mEntryObj, this, mNotificationPostID, ViewStreamUsersActivity.class);
////                            composeStreamNotification(mEntryObj, this, mNotificationPostID, ViewProfileActivity.class);
////                            break;
////                        case "FOLLOW":
////                            mNotificationPostID = Integer.parseInt(mDetailsObj.get(PostsModel.PROFILE_ID).toString());
////                            //composeNotification(mEntryObj, this, mNotificationPostID, OthersMotoFileActivity.class);
////                            composeNotification(mEntryObj, this, mNotificationPostID, ViewProfileActivity.class);
////                            break;
////                        case "FOLLOWER_POST":
////                        case "TAGGED":
////                            mNotificationPostID = Integer.parseInt(mDetailsObj.get("ID").toString());
////                            //composeNotification(mEntryObj, this, mNotificationPostID, PostViewActivity.class);
////                            composeNotification(mEntryObj, this, mNotificationPostID, ViewProfileActivity.class);
////                            break;
////                        case "COMMENT_LIKE":
////                            mNotificationPostID = Integer.parseInt(mDetailsObj.get("CommentID").toString());
////                            //composeNotification(mEntryObj, this, mNotificationPostID, PostCommentLikeViewActivity.class);
////                            composeNotification(mEntryObj, this, mNotificationPostID, ViewProfileActivity.class);
////                            break;
////                        case "COMMENT_REPLY_LIKE":
////                            mNotificationPostID = Integer.parseInt(mDetailsObj.get("ReplyID").toString());
////                            //composeNotification(mEntryObj, this, mNotificationPostID, CommentReplyActivity.class);
////                            composeNotification(mEntryObj, this, mNotificationPostID, ViewProfileActivity.class);
////                            break;
////                        case "TAGGED_COMMENT_REPLY":
////                        case "COMMENT_REPLY":
////                            mNotificationPostID = Integer.parseInt(mDetailsObj.get("CommentID").toString());
////                            //composeNotification(mEntryObj, this, mNotificationPostID, CommentReplyActivity.class);
////                            composeNotification(mEntryObj, this, mNotificationPostID, ViewProfileActivity.class);
////                            break;
////                        case "TAGGED_POST_COMMENTS":
////                        case "POST_COMMENTS":
////                        case "POST_LIKES":
////                            mNotificationPostID = Integer.parseInt(mDetailsObj.get("PostID").toString());
////                            //composeNotification(mEntryObj, this, mNotificationPostID, PostViewActivity.class);
////                            composeNotification(mEntryObj, this, mNotificationPostID, ViewProfileActivity.class);
////                            break;
////                        case "VIDEO_SHARE":
////                            mNotificationPostID = Integer.parseInt(mDetailsObj.get("ID").toString());
////                            //composeNotification(mEntryObj, this, mNotificationPostID, PostViewActivity.class);
////                            composeNotification(mEntryObj, this, mNotificationPostID, ViewProfileActivity.class);
////                            break;
////                        case "POST":
////                        case "POST_SHARE":
////                            mNotificationPostID = Integer.parseInt(mDetailsObj.get("ID").toString());
////                            //composeNotification(mEntryObj, this, mNotificationPostID, PostViewActivity.class);
////                            composeNotification(mEntryObj, this, mNotificationPostID, ViewProfileActivity.class);
////                            break;
////                        case "VEHICLE_LIKE":
////                            mNotificationPostID = Integer.parseInt(mDetailsObj.get(VehicleInfoLikeModel.LIKED_PROFILE_ID).toString());
////                            //composeNotification(mEntryObj, this, mNotificationPostID, UpdateProfileActivity.class);
////                            composeNotification(mEntryObj, this, mNotificationPostID, ViewProfileActivity.class);
////                            break;
////                        case "EVENT_CREATION":
////                            int mEventCreationID = Integer.parseInt((mDetailsObj.get("PromoterID").toString()) + (mDetailsObj.get("EventID").toString()));
////                            //composeNotification(mEntryObj, this, mEventCreationID, NotificationEventCreatedActivity.class);
////                            composeNotification(mEntryObj, this, mEventCreationID, ViewProfileActivity.class);
////                            break;
////                        case "VIDEO_COMMENT_LIKE":
////                            mNotificationPostID = mDetailsObj.getInt("VideoCommentID");
////                            //composeNotification(mEntryObj, this, mNotificationPostID, VideoCommentsActivity.class);
////                            composeNotification(mEntryObj, this, mNotificationPostID, ViewProfileActivity.class);
////                            break;
////                        case "TAGGED_VIDEO_COMMENT_REPLY":
////                        case "VIDEO_COMMENT_REPLY":
////                            mNotificationPostID = mDetailsObj.getInt("CommentID");
////                            //composeNotification(mEntryObj, this, mNotificationPostID, VideoCommentReplyActivity.class);
////                            composeNotification(mEntryObj, this, mNotificationPostID, ViewProfileActivity.class);
////                            break;
////                        case "VIDEO_COMMENT_REPLY_LIKE":
////                            mNotificationPostID = mDetailsObj.getInt("VideoCommentID");
////                            //composeNotification(mEntryObj, this, mNotificationPostID, VideoCommentReplyActivity.class);
////                            composeNotification(mEntryObj, this, mNotificationPostID, ViewProfileActivity.class);
////                            break;
////                        case "TAGGED_POST_VIDEO_COMMENTS":
////                        case "VIDEO_COMMENTS":
////                        case "VIDEO_LIKES":
////                            mNotificationPostID = mDetailsObj.getInt("VideoID");
////                            //composeNotification(mEntryObj, this, mNotificationPostID, PromoterVideoGalleryActivity.class);
////                            composeNotification(mEntryObj, this, mNotificationPostID, ViewProfileActivity.class);
////                            break;
////
////                        case "EVENT_CHAT":
////                            if (MotoHub.getApplicationInstance().isEventGrpChatOnline()) {
////                                Intent mIntent = new Intent(PUSH_MSG_RECEIVER_ACTION);
////                                mIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
////                                mIntent.putExtra((EventsModel.EVENT_ID), (mDetailsObj.get("EventID").toString()));
////                                mIntent.putExtra(AppConstants.IS_FROM_LIVE_EVENT_CHAT, false);
////                                sendBroadcast(mIntent);
////                            } else {
////                                try {
////                                    JSONObject mNotificationJsonObject = new JSONObject();
////                                    mNotificationJsonObject.put(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mEntryObj);
////                                    mNotificationJsonObject.put((EventsModel.EVENT_ID), (mDetailsObj.get("EventID").toString()));
////                                    String mEventChatID = "EVENT_CHAT" + (mDetailsObj.get("EventID").toString());
////                                    mNotificationEventID = Integer.parseInt(mDetailsObj.get("EventID").toString());
////                                    mNotificationJsonObject.put(AppConstants.IS_FROM_LIVE_EVENT_CHAT, false);
////                                    mContentTitle = "EVENT :" + (mDetailsObj.get(EVENT_GRP_CHAT_EVENT_NAME).toString());
////                                    //composeChatNotification(mNotificationJsonObject, this, mNotificationEventID, mEventChatID, mContentTitle, ChatBoxEventGrpActivity.class);
////                                    composeChatNotification(mNotificationJsonObject, this, mNotificationEventID, mEventChatID, mContentTitle, ViewProfileActivity.class);
////                                } catch (JSONException e) {
////                                    e.printStackTrace();
////                                }
////                            }
////                            break;
////                        case "EVENT_LIVE_CHAT":
////                            if (MotoHub.getApplicationInstance().isEventLiveGrpChatOnline()) {
////                                Intent mIntent = new Intent(PUSH_MSG_RECEIVER_ACTION);
////                                mIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
////                                mIntent.putExtra((EventsModel.EVENT_ID), (mDetailsObj.get("EventID").toString()));
////                                mIntent.putExtra(AppConstants.IS_FROM_LIVE_EVENT_CHAT, true);
////                                sendBroadcast(mIntent);
////                            } else {
////                                try {
////                                    JSONObject mNotificationJsonObject = new JSONObject();
////                                    mNotificationJsonObject.put(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mEntryObj);
////                                    mNotificationJsonObject.put((EventsModel.EVENT_ID), (mDetailsObj.get("EventID").toString()));
////                                    String mEventLiveChatID = "EVENT_LIVE_CHAT" + (mDetailsObj.get("EventID").toString());
////                                    mNotificationEventID = Integer.parseInt(mDetailsObj.get("EventID").toString());
////                                    mContentTitle = "LIVE EVENT";
////                                    //composeChatNotification(mNotificationJsonObject, this, mNotificationEventID, mEventLiveChatID, mContentTitle, EventLiveActivity.class);
////                                    composeChatNotification(mNotificationJsonObject, this, mNotificationEventID, mEventLiveChatID, mContentTitle, ViewProfileActivity.class);
////                                } catch (JSONException e) {
////                                    e.printStackTrace();
////                                }
////
////                            }
////
////                            break;
////                        case "GROUP_CHAT_MSG":
////                            if (MotoHub.getApplicationInstance().isGrpChatOnline()) {
////                                Intent mIntent = new Intent(PUSH_MSG_RECEIVER_ACTION);
////                                mIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
////                                mIntent.putExtra((GroupChatRoomModel.GRP_CHAT_ROOM_ID), (mDetailsObj.get("GroupChatRoomID").toString()));
////                                mIntent.putExtra(AppConstants.IS_FROM_GROUP_CHAT, true);
////                                sendBroadcast(mIntent);
////                            } else {
////                                try {
////                                    JSONObject mNotificationJsonObject = new JSONObject();
////                                    mNotificationJsonObject.put(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mEntryObj);
////                                    JSONObject mGroupChatObj = mDetailsObj.getJSONObject("GroupChat");
////                                    String mGroupChatRoomID = (mDetailsObj.get("GroupChatRoomID").toString());
////                                    mNotificationEventID = Integer.parseInt(mDetailsObj.get("GroupChatRoomID").toString());
////                                    mNotificationJsonObject.put((GroupChatRoomModel.GRP_CHAT_ROOM_ID), mGroupChatRoomID);
////                                    mContentTitle = "Group : " + ((mGroupChatObj.get("GroupName").toString()));
////                                    //composeChatNotification(mNotificationJsonObject, this, mNotificationEventID, mGroupChatRoomID, mContentTitle, ChatBoxGroupActivity.class);
////                                    composeChatNotification(mNotificationJsonObject, this, mNotificationEventID, mGroupChatRoomID, mContentTitle, ViewProfileActivity.class);
////                                } catch (JSONException e) {
////                                    e.printStackTrace();
////                                }
////                            }
////                            break;
////                        case "SINGLE_CHAT":
////                            if (MotoHub.getApplicationInstance().isSingleChatOnline()) {
////                                Intent mIntent = new Intent(PUSH_MSG_RECEIVER_ACTION);
////                                mIntent.putExtra(ENTRY_JSON_OBJ, mEntryObj.toString());
////                                sendBroadcast(mIntent);
////                            } else {
////                                try {
////                                    JSONObject mNotificationJsonObject = new JSONObject();
////                                    mNotificationJsonObject.put(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mEntryObj);
////                                    String mSingleChatID = mDetailsObj.get("FromProfileID").toString();
////                                    int mNotificationID = Integer.parseInt(mDetailsObj.get("FromProfileID").toString());
////                                    mContentTitle = "From : " + ((mDetailsObj.get(SENDER_NAME).toString()));
////                                    //composeChatNotification(mNotificationJsonObject, this, mNotificationID, mSingleChatID, mContentTitle, ChatBoxSingleActivity.class);
////                                    composeChatNotification(mNotificationJsonObject, this, mNotificationID, mSingleChatID, mContentTitle, ViewProfileActivity.class);
////                                } catch (JSONException e) {
////                                    e.printStackTrace();
////                                }
////
////                            }
////                            break;
////                            default:
////                                composeNotification(mEntryObj, this, 1000, ViewProfileActivity.class);
////                                break;
////                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    }
//    private void composeNotification(JSONObject jsonObject, Context mContext, int mNotificationID, Class className) {
//        boolean allow_noticiation_status = PreferenceUtils.getInstance(this).getBooleanData(PreferenceUtils.ALLOW_NOTIFICATION);
//        boolean allow_sound_status = PreferenceUtils.getInstance(this).getBooleanData(PreferenceUtils.ALLOW_NOTIFICATION_Sound);
//        boolean allow_sound_vib = PreferenceUtils.getInstance(this).getBooleanData(PreferenceUtils.ALLOW_NOTIFICATION_VIB);
//        if (allow_noticiation_status) {
//
//            try {
//                Intent mResultIntent = new Intent(mContext, className);
//
//                mResultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                mResultIntent.putExtra(ENTRY_JSON_OBJ, jsonObject.toString());
//
//                mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
//
//                PendingIntent mResultPendingIntent = PendingIntent.getActivity(mContext, mNotificationID,
//                        mResultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                String mMsg = URLDecoder.decode(jsonObject.getString("Msg"), "UTF-8");
//                //String mMsg = replacer(sb.append(jsonObject.getString("Msg")));
//
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    if (mNotificationManager != null) {
//                        mNotificationManager.createNotificationChannel(NotificationUtils.getInstance().getNotificationChannel(mNotificationID));
//                    }
//                    mNotificationBuilder = NotificationUtils.getInstance().getNotificationBuilder(mContext, mResultPendingIntent, "MotoHUB", mMsg, mNotificationID);
//                    mNotification = mNotificationBuilder.build();
//                    mNotificationManager.notify(mNotificationID, mNotification);
//                } else {
//                    mNotificationCompatBuilder = NotificationUtils.getInstance().getNotificationCompatBuilder(mContext, mResultPendingIntent, "MotoHUB", mMsg, mNotificationID);
//                    if (allow_sound_status) {
//                        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                        mNotificationCompatBuilder.setSound(uri);
//                    }
//                    if (allow_sound_vib) {
//                        mNotificationCompatBuilder.setVibrate(new long[]{1000, 1000});
//                    }
//                    mNotification = mNotificationCompatBuilder.build();
//                    mNotificationManager.notify(mNotificationID, mNotification);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    public static String replacer(StringBuffer outBuffer) {
//
//        String data = outBuffer.toString();
//        try {
//            StringBuffer tempBuffer = new StringBuffer();
//            int incrementor = 0;
//            int dataLength = data.length();
//            while (incrementor < dataLength) {
//                char charecterAt = data.charAt(incrementor);
//                if (charecterAt == '%') {
//                    tempBuffer.append("<percentage>");
//                } else if (charecterAt == '+') {
//                    tempBuffer.append("<plus>");
//                } else {
//                    tempBuffer.append(charecterAt);
//                }
//                incrementor++;
//            }
//            data = tempBuffer.toString();
//            data = URLDecoder.decode(data, "utf-8");
//            data = data.replaceAll("<percentage>", "%");
//            data = data.replaceAll("<plus>", "+");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return data;
//    }
//    private static StringBuffer sb = new StringBuffer();
//    public static void composeChatNotification(JSONObject jsonObject, Context mContext, int mNotificationID,
//                                               String notificationChannelName, String mContentTitle, Class className) {
//        boolean allow_noticiation_status = PreferenceUtils.getInstance(mContext).getBooleanData(PreferenceUtils.ALLOW_NOTIFICATION);
//        boolean allow_sound_status = PreferenceUtils.getInstance(mContext).getBooleanData(PreferenceUtils.ALLOW_NOTIFICATION_Sound);
//        boolean allow_sound_vib = PreferenceUtils.getInstance(mContext).getBooleanData(PreferenceUtils.ALLOW_NOTIFICATION_VIB);
//        if (allow_noticiation_status) {
//            try {
//                Intent mResultIntent = new Intent(mContext, className);
//                mResultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                JSONObject mJsonEntryObject = new JSONObject(jsonObject.opt(ENTRY_JSON_OBJ).toString());
//                mResultIntent.putExtra(ENTRY_JSON_OBJ, mJsonEntryObject.toString());
//                mResultIntent.putExtra(IS_FROM_NOTIFICATION_TRAY, true);
//                PendingIntent mResultPendingIntent = PendingIntent.getActivity(mContext, mNotificationID, mResultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                String mMsg = URLDecoder.decode(mJsonEntryObject.getString("Msg"), "UTF-8");
//                //String mMsg = replacer(sb.append(mJsonEntryObject.getString("Msg")));
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    if (mNotificationManager != null) {
//                        mNotificationManager.createNotificationChannel(NotificationUtils.getInstance().getNotificationChannel(mNotificationID));
//                    }
//                    mNotificationBuilder = NotificationUtils.getInstance().getNotificationBuilder(mContext, mResultPendingIntent, mContentTitle, mMsg, mNotificationID);
//                    mNotification = mNotificationBuilder.build();
//                    mNotificationManager.notify(mNotificationID, mNotification);
//                } else {
//                    mNotificationCompatBuilder = NotificationUtils.getInstance().getNotificationCompatBuilder(mContext, mResultPendingIntent, mContentTitle, mMsg, mNotificationID);
//                    if (allow_sound_status) {
//                        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                        mNotificationCompatBuilder.setSound(uri);
//                    }
//                    if (allow_sound_vib) {
//                        mNotificationCompatBuilder.setVibrate(new long[]{1000, 1000});
//                    }
//                    mNotification = mNotificationCompatBuilder.build();
//                    mNotificationManager.notify(mNotificationID, mNotification);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    private void composeStreamNotification(JSONObject jsonObject, Context mContext, int mNotificationID, Class className) {
//        boolean allow_noticiation_status = PreferenceUtils.getInstance(this).getBooleanData(PreferenceUtils.ALLOW_NOTIFICATION);
//        boolean allow_sound_status = PreferenceUtils.getInstance(this).getBooleanData(PreferenceUtils.ALLOW_NOTIFICATION_Sound);
//        boolean allow_sound_vib = PreferenceUtils.getInstance(this).getBooleanData(PreferenceUtils.ALLOW_NOTIFICATION_VIB);
//        if (allow_noticiation_status) {
//            try {
//                Intent mResultIntent = new Intent(mContext, className);
//                mResultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                mResultIntent.putExtra(AppConstants.PROFILE_ID, jsonObject.getJSONObject("Details").getInt(APIConstants.StreamProfileID));
//                PendingIntent mResultPendingIntent = PendingIntent.getActivity(mContext, mNotificationID,
//                        mResultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                String mMsg = URLDecoder.decode(jsonObject.getString("Msg"), "UTF-8");
//                //String mMsg = replacer(sb.append(jsonObject.getString("Msg")));
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    if (mNotificationManager != null) {
//                        mNotificationManager.createNotificationChannel(NotificationUtils.getInstance().getNotificationChannel(mNotificationID));
//                    }
//                    mNotificationBuilder = NotificationUtils.getInstance().getNotificationBuilder(mContext, mResultPendingIntent, "MotoHUB", mMsg, mNotificationID);
//                    mNotification = mNotificationBuilder.build();
//                    mNotificationManager.notify(mNotificationID, mNotification);
//                } else {
//                    mNotificationCompatBuilder = NotificationUtils.getInstance().getNotificationCompatBuilder(mContext, mResultPendingIntent, "MotoHUB", mMsg, mNotificationID);
//                    if (allow_sound_status) {
//                        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                        mNotificationCompatBuilder.setSound(uri);
//                    }
//                    if (allow_sound_vib) {
//                        mNotificationCompatBuilder.setVibrate(new long[]{1000, 1000});
//                    }
//                    mNotification = mNotificationCompatBuilder.build();
//                    mNotificationManager.notify(mNotificationID, mNotification);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}