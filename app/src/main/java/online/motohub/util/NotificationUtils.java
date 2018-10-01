package online.motohub.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import online.motohub.R;

public class NotificationUtils {
    public static NotificationUtils INSTANCE = new NotificationUtils();

    public static NotificationUtils getInstance() {
        return INSTANCE;
    }

    public Notification.Builder getNotificationBuilder(Context mContext, boolean isOngoing, String mContentValue,
                                                       int mNotificationID, int maxProgress, int progress) {
        Notification.Builder mNotificationBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationBuilder = new Notification.Builder(mContext, String.valueOf(mNotificationID))
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.app_icon))
                    .setOngoing(isOngoing)
                    .setContentTitle("MotoHUB")
                    .setOnlyAlertOnce(true)
                    .setContentText(mContentValue).setChannelId(String.valueOf(mNotificationID))
                    .setSmallIcon(R.drawable.notification_tray_icon)
                    .setColor(ContextCompat.getColor(mContext, R.color.colorOrange))
                    .setChannelId(String.valueOf(mNotificationID))
                    .setProgress(maxProgress, progress, false)
                    .setAutoCancel(true);
        }
        return mNotificationBuilder;
    }

    public NotificationCompat.Builder getNotificationCompatBuilder(Context mContext, boolean isOngoing, String mContentValue,
                                                                   int mNotificationID, int maxProgress, int progress) {
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(mContext, String.valueOf(mNotificationID))
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.app_icon))
                .setOngoing(isOngoing)
                .setContentTitle("MotoHUB")
                .setContentText(mContentValue).setChannelId(String.valueOf(mNotificationID))
                .setSmallIcon(R.drawable.notification_tray_icon)
                .setOnlyAlertOnce(true)
                .setColor(ContextCompat.getColor(mContext, R.color.colorOrange))
                .setProgress(maxProgress, progress, false)
                .setAutoCancel(true);
        return mNotificationBuilder;
    }

    public Notification.Builder getNotificationBuilder(Context mContext, PendingIntent mPendingIntent, String mContentTitle, String mContentValue, int mNotificationID) {
        Notification.Builder mNotificationBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationBuilder = new Notification.Builder(mContext, String.valueOf(mNotificationID))
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.app_icon))
                    .setContentTitle(mContentTitle)
                    .setContentText(mContentValue).setChannelId(String.valueOf(mNotificationID))
                    .setSmallIcon(R.drawable.notification_tray_icon)
                    .setOnlyAlertOnce(true)
                    .setColor(ContextCompat.getColor(mContext, R.color.colorOrange))
                    .setChannelId(String.valueOf(mNotificationID))
                    .setContentIntent(mPendingIntent)
                    .setAutoCancel(true);
        }
        return mNotificationBuilder;
    }

    public NotificationCompat.Builder getNotificationCompatBuilder(Context mContext, PendingIntent mPendingIntent,
                                                                   String mContentTitle, String mContentValue, int mNotificationID) {
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(mContext, String.valueOf(mNotificationID))
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.app_icon))
                .setContentTitle(mContentTitle)
                .setContentText(mContentValue).setChannelId(String.valueOf(mNotificationID))
                .setSmallIcon(R.drawable.notification_tray_icon)
                .setOnlyAlertOnce(true)
                .setColor(ContextCompat.getColor(mContext, R.color.colorOrange))
                .setContentIntent(mPendingIntent)
                .setAutoCancel(true);
        return mNotificationBuilder;
    }

    public NotificationChannel getNotificationChannel(int notificationid) {
        CharSequence name = "MotoHUB";
        NotificationChannel mNotificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationChannel = new NotificationChannel(String.valueOf(notificationid),
                    name, NotificationManager.IMPORTANCE_DEFAULT);
            // Sets whether notifications posted to this channel should display notification lights
            mNotificationChannel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            mNotificationChannel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            mNotificationChannel.setLightColor(Color.GREEN);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            mNotificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        }
        return mNotificationChannel;
    }

}
