package online.motohub.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import online.motohub.R;
import online.motohub.activity.ViewProfileActivity;
import online.motohub.application.MotoHub;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.model.EventsModel;
import online.motohub.model.GroupChatRoomModel;
import online.motohub.model.NotificationModel1;
import online.motohub.model.PostsModel;
import online.motohub.model.VehicleInfoLikeModel;
import online.motohub.retrofit.APIConstants;


public class NotificationUtils1 {

    private Ringtone notificationTone;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationCompatBuilder;
    private Notification mNotification;
    private Context mContext;
    private NotificationModel1 notificationModel;

    private int mNotificationID;
    private String mIntentAction;

    public NotificationUtils1(Context mContext, NotificationModel1 model) {
        this.mContext = mContext;
        this.notificationModel = model;
        notificationTone = RingtoneManager.getRingtone(mContext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        notificationAction();
    }

    // Playing notification sound
    private void playNotificationSound() {
        try {
            if (notificationTone == null) {
                notificationTone = RingtoneManager.getRingtone(mContext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            }
            if (notificationTone.isPlaying()) {
                notificationTone.stop();
            }
            notificationTone.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
            notificationManager.cancelAll();
    }

    private PendingIntent getPendingIntent() {
        //TODO default action="online.motohub.activity.ViewProfileActivity";
        if (TextUtils.isEmpty(mIntentAction)) {
            mIntentAction = "online.motohub.activity.ViewProfileActivity";
        }
        Intent intent = new Intent(mContext, ViewProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(AppConstants.PROFILE_ID, mProfileID);
//        intent.putExtra(NOTIFICATION_BUNDLE, new Gson().toJson(notificationModel));
        intent.putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, notificationModel.getMainObj().toString());
        intent.putExtra(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY, true);
        return PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
    }

    private void generateNotification() {
        //If needed generate random number for notificationID
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                mNotificationManager.createNotificationChannel(getNotificationChannel());
            }
            mNotificationCompatBuilder = getNotificationCompatBuilder();
            mNotification = mNotificationCompatBuilder.build();
            mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
            mNotification.defaults |= Notification.DEFAULT_LIGHTS;
            mNotificationManager.notify(mNotificationID, mNotification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private NotificationCompat.Builder getNotificationCompatBuilder() {
        boolean allow_sound_status = PreferenceUtils.getInstance(mContext).getBooleanData(PreferenceUtils.ALLOW_NOTIFICATION_Sound);
        boolean allow_sound_vib = PreferenceUtils.getInstance(mContext).getBooleanData(PreferenceUtils.ALLOW_NOTIFICATION_VIB);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap mBitmap = getBitmapFromURL("");
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(mContext, String.valueOf(mNotificationID))
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.app_icon))
                .setSmallIcon(R.drawable.notification_tray_icon)
                .setContentTitle(mContext.getString(R.string.app_name))
                .setContentText(notificationModel.getMsg())
                .setContentIntent(getPendingIntent())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setWhen(System.currentTimeMillis())
                .setColor(ContextCompat.getColor(mContext, R.color.colorOrange));
        if (allow_sound_status) {
            mNotificationBuilder.setSound(uri);
        }
        if (allow_sound_vib) {
            mNotificationBuilder.setVibrate(new long[]{1000, 1000});
        }
        if (mBitmap != null) {
            mNotificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(mBitmap).setSummaryText(notificationModel.getMsg()));
        }
        return mNotificationBuilder;
    }

    /**
     * When the OS version is equal or greater than orea this method will trigger.
     * We must set the Channel ID while creating this NotificationChannel
     *
     * @return NotificationChannel
     */
    private NotificationChannel getNotificationChannel() {
        CharSequence name = mContext.getString(R.string.notification_channel_id);
        NotificationChannel mNotificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationChannel = new NotificationChannel(String.valueOf(mNotificationID),
                    name, NotificationManager.IMPORTANCE_HIGH);
            // Sets whether notifications posted to this channel sahould display notification lights
            mNotificationChannel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            mNotificationChannel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
//            mNotificationChannel.setLightColor(Color.GREEN);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
//            mNotificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        }
        return mNotificationChannel;
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int mProfileID = 0;

    private void notificationAction() {
        try {
            boolean allow_notification_status = PreferenceUtils.getInstance(mContext).getBooleanData(PreferenceUtils.ALLOW_NOTIFICATION);
            if (!allow_notification_status) {
                return;
            }
//Have to remove commented lines
            String mContentTitle;
            JSONObject mDetailsObj = notificationModel.getDetails();
            //TODO this is default navigation screen if you need to redirect specific screen mention the package name in the specific type
            mIntentAction = "online.motohub.activity.ViewProfileActivity";
            switch (notificationModel.getType()) {
                case "LIVE_STREAM":
                    mNotificationID = Integer.parseInt(mDetailsObj.get(APIConstants.ID).toString());
                    mProfileID = notificationModel.getDetails().getInt(APIConstants.StreamProfileID);
                    //composeStreamNotification(mEntryObj, t his, mNotificationPostID, ViewLiveVideoViewScreen2.class);
                    generateNotification();
                    break;
                case "LIVE_REQUEST":
                    mNotificationID = Integer.parseInt(mDetailsObj.get(APIConstants.ID).toString());
                    mProfileID = notificationModel.getDetails().getInt(APIConstants.StreamProfileID);
                    //composeStreamNotification(mEntryObj, this, mNotificationPostID, ViewRequestUsersActivity.class);
                    generateNotification();
                    break;
                case "LIVE_ACCEPT":
                    mNotificationID = Integer.parseInt(mDetailsObj.get(APIConstants.ID).toString());
                    mProfileID = notificationModel.getDetails().getInt(APIConstants.StreamProfileID);
                    //composeStreamNotification(mEntryObj, this, mNotificationPostID, ViewStreamUsersActivity.class);
                    generateNotification();
                    break;
                case "FOLLOW":
                    mNotificationID = Integer.parseInt(mDetailsObj.get(PostsModel.PROFILE_ID).toString());
                    //composeNotification(mEntryObj, this, mNotificationPostID, OthersMotoFileActivity.class);
                    generateNotification();
                    break;
                case "FOLLOWER_POST":
                case "TAGGED":
                    mNotificationID = Integer.parseInt(mDetailsObj.get("ID").toString());
                    //composeNotification(mEntryObj, this, mNotificationPostID, PostViewActivity.class);
                    generateNotification();
                    break;
                case "COMMENT_LIKE":
                    mNotificationID = Integer.parseInt(mDetailsObj.get("CommentID").toString());
                    //composeNotification(mEntryObj, this, mNotificationPostID, PostCommentLikeViewActivity.class);
                    generateNotification();
                    break;
                case "COMMENT_REPLY_LIKE":
                    mNotificationID = Integer.parseInt(mDetailsObj.get("ReplyID").toString());
                    //composeNotification(mEntryObj, this, mNotificationPostID, CommentReplyActivity.class);
                    generateNotification();
                    break;
                case "TAGGED_COMMENT_REPLY":
                case "COMMENT_REPLY":
                    mNotificationID = Integer.parseInt(mDetailsObj.get("CommentID").toString());
                    //composeNotification(mEntryObj, this, mNotificationPostID, CommentReplyActivity.class);
                    generateNotification();
                    break;
                case "TAGGED_POST_COMMENTS":
                case "POST_COMMENTS":
                case "POST_LIKES":
                    mNotificationID = Integer.parseInt(mDetailsObj.get("PostID").toString());
                    //composeNotification(mEntryObj, this, mNotificationPostID, PostViewActivity.class);
                    generateNotification();
                    break;
                case "VIDEO_SHARE":
                    mNotificationID = Integer.parseInt(mDetailsObj.get("ID").toString());
                    //composeNotification(mEntryObj, this, mNotificationPostID, PostViewActivity.class);
                    generateNotification();
                    break;
                case "POST":
                case "POST_SHARE":
                    mNotificationID = Integer.parseInt(mDetailsObj.get("ID").toString());
                    //composeNotification(mEntryObj, this, mNotificationPostID, PostViewActivity.class);
                    generateNotification();
                    break;
                case "VEHICLE_LIKE":
                    mNotificationID = Integer.parseInt(mDetailsObj.get(VehicleInfoLikeModel.LIKED_PROFILE_ID).toString());
                    //composeNotification(mEntryObj, this, mNotificationPostID, UpdateProfileActivity.class);
                    generateNotification();
                    break;
                case "EVENT_CREATION":
                    int mEventCreationID = Integer.parseInt((mDetailsObj.get("PromoterID").toString()) + (mDetailsObj.get("EventID").toString()));
                    //composeNotification(mEntryObj, this, mEventCreationID, NotificationEventCreatedActivity.class);
                    generateNotification();
                    break;
                case "VIDEO_COMMENT_LIKE":
                    mNotificationID = mDetailsObj.getInt("VideoCommentID");
                    //composeNotification(mEntryObj, this, mNotificationPostID, VideoCommentsActivity.class);
                    generateNotification();
                    break;
                case "TAGGED_VIDEO_COMMENT_REPLY":
                case "VIDEO_COMMENT_REPLY":
                    mNotificationID = mDetailsObj.getInt("CommentID");
                    //composeNotification(mEntryObj, this, mNotificationPostID, VideoCommentReplyActivity.class);
                    generateNotification();
                    break;
                case "VIDEO_COMMENT_REPLY_LIKE":
                    mNotificationID = mDetailsObj.getInt("VideoCommentID");
                    //composeNotification(mEntryObj, this, mNotificationPostID, VideoCommentReplyActivity.class);
                    generateNotification();
                    break;
                case "TAGGED_POST_VIDEO_COMMENTS":
                case "VIDEO_COMMENTS":
                case "VIDEO_LIKES":
                    mNotificationID = mDetailsObj.getInt("VideoID");
                    //composeNotification(mEntryObj, this, mNotificationPostID, PromoterVideoGalleryActivity.class);
                    generateNotification();
                    break;

                case "EVENT_CHAT":
                    if (!notificationModel.isForceNotification()&&MotoHub.getApplicationInstance().isEventGrpChatOnline()) {
                        Intent mIntent = new Intent(MyFireBaseMessagingService.PUSH_MSG_RECEIVER_ACTION);
                        mIntent.putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, notificationModel.getMainObj().toString());
                        mIntent.putExtra((EventsModel.EVENT_ID), (mDetailsObj.get("EventID").toString()));
                        mIntent.putExtra(AppConstants.IS_FROM_LIVE_EVENT_CHAT, false);
                        mContext.sendBroadcast(mIntent);
                    } else {
                        mNotificationID = Integer.parseInt(mDetailsObj.get("EventID").toString());
                        generateNotification();
                    }
                    break;
                case "EVENT_LIVE_CHAT":
                    if (!notificationModel.isForceNotification()&&MotoHub.getApplicationInstance().isEventLiveGrpChatOnline()) {
                        Intent mIntent = new Intent(MyFireBaseMessagingService.PUSH_MSG_RECEIVER_ACTION);
                        mIntent.putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, notificationModel.getMainObj().toString());
                        mIntent.putExtra((EventsModel.EVENT_ID), (mDetailsObj.get("EventID").toString()));
                        mIntent.putExtra(AppConstants.IS_FROM_LIVE_EVENT_CHAT, true);
                        mContext.sendBroadcast(mIntent);
                    } else {
                        mNotificationID = Integer.parseInt(mDetailsObj.get("EventID").toString());
                        generateNotification();
                    }

                    break;
                case "GROUP_CHAT_MSG":
                    if (!notificationModel.isForceNotification()&&MotoHub.getApplicationInstance().isGrpChatOnline()) {
                        Intent mIntent = new Intent(MyFireBaseMessagingService.PUSH_MSG_RECEIVER_ACTION);
                        mIntent.putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, notificationModel.getMainObj().toString());
                        mIntent.putExtra((GroupChatRoomModel.GRP_CHAT_ROOM_ID), (mDetailsObj.get("GroupChatRoomID").toString()));
                        mIntent.putExtra(AppConstants.IS_FROM_GROUP_CHAT, true);
                        mContext.sendBroadcast(mIntent);
                    } else {
                        mNotificationID = Integer.parseInt(mDetailsObj.get("GroupChatRoomID").toString());
                        generateNotification();
                    }
                    break;
                case "SINGLE_CHAT":
                    if (!notificationModel.isForceNotification()&&MotoHub.getApplicationInstance().isSingleChatOnline()) {
                        Intent mIntent = new Intent(MyFireBaseMessagingService.PUSH_MSG_RECEIVER_ACTION);
                        mIntent.putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, notificationModel.getMainObj().toString());
                        mContext.sendBroadcast(mIntent);
                    } else {
                        mNotificationID = Integer.parseInt(mDetailsObj.get("FromProfileID").toString());
                        generateNotification();
                    }
                    break;
                default:
                    mNotificationID = 1000;
                    generateNotification();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setSound() {
        switch (notificationModel.getType()) {
            case "POST":
            case "FOLLOWER_POST":
            case "POST_SHARE":
                //TODO don't want to set sound for notifications
                break;
            case "LIVE_STREAM":
            case "LIVE_REQUEST":
            case "LIVE_ACCEPT":
            case "FOLLOW":
            case "TAGGED":
            case "COMMENT_LIKE":
            case "COMMENT_REPLY_LIKE":
            case "TAGGED_COMMENT_REPLY":
            case "COMMENT_REPLY":
            case "TAGGED_POST_COMMENTS":
            case "POST_COMMENTS":
            case "POST_LIKES":
            case "VIDEO_SHARE":
            case "VEHICLE_LIKE":
            case "EVENT_CREATION":
            case "VIDEO_COMMENT_LIKE":
            case "TAGGED_VIDEO_COMMENT_REPLY":
            case "VIDEO_COMMENT_REPLY":
            case "VIDEO_COMMENT_REPLY_LIKE":
            case "TAGGED_POST_VIDEO_COMMENTS":
            case "VIDEO_COMMENTS":
            case "VIDEO_LIKES":
            case "EVENT_CHAT":
            case "EVENT_LIVE_CHAT":
            case "GROUP_CHAT_MSG":
            case "SINGLE_CHAT":
            default:
                playNotificationSound();
                break;
        }
    }

}