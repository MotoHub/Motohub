package online.motohub.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;

import online.motohub.R;
import online.motohub.database.DatabaseHandler;
import online.motohub.enums.UploadStatus;
import online.motohub.model.SpectatorLiveEntity;
import online.motohub.model.SpectatorLiveModel;
import online.motohub.model.VideoUploadModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.NotificationUtils;
import online.motohub.util.ProgressRequestBody;
import online.motohub.util.UrlUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpectatorFileUploadOfflineService extends IntentService implements ProgressRequestBody.UploadCallbacks {

    private TransferObserver observerVideo;
    private TransferObserver observerImage;

    private NotificationManager notificationManager;
    private Notification.Builder notificationBuilder;
    private NotificationCompat.Builder notificationCompatBuilder;
    private Notification notification;

    private int profileID, userID, eventID, livePostProfileID;
    private String caption = "", eventFinishDate = "";
    private VideoUploadModel videoUploadModel;
    private File thumbImgFile;


    public SpectatorFileUploadOfflineService() {
        super(SpectatorFileUploadOfflineService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        AWSMobileClient.getInstance().initialize(this).execute();

        AppConstants.UPLOAD_STATUS = UploadStatus.STARTED;

        uploadFileNotification(this, new Gson().fromJson(intent.getStringExtra("data"), SpectatorLiveEntity.class));
    }

    @Override
    public void onProgressUpdate(int percentage, int notificationID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setProgress(100, percentage, false);
            notification = notificationBuilder.build();
        } else {
            notificationCompatBuilder.setProgress(100, percentage, false);
            notification = notificationCompatBuilder.build();
        }
        notificationManager.notify(notificationID, notification);
    }

    private void uploadFileNotification(Context mContext, final SpectatorLiveEntity entity) {
        profileID = Integer.parseInt(entity.getProfileID());
        userID = Integer.parseInt(entity.getUserID());
        caption = entity.getCaption();
        String mVideoPath = entity.getVideoUrl();
        thumbImgFile = new File(entity.getThumbnail());
        eventID = Integer.parseInt(entity.getEventID());
        eventFinishDate = entity.getEventFinishDate();
        livePostProfileID = Integer.parseInt(entity.getLivePostProfileID());

        final File videoFile = new File(mVideoPath);
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        videoUploadModel = new VideoUploadModel();
        int count = databaseHandler.getPendingCount();
        final int notificationid = 1;
        String s = videoFile.toString();
        videoUploadModel.setVideoURL(s.substring(s.lastIndexOf("/") + 1));
        videoUploadModel.setFlag(1);
        videoUploadModel.setThumbnailURl(thumbImgFile.toString());
        videoUploadModel.setProfileID(profileID);
        videoUploadModel.setNotificationflag(notificationid);
        String mUserType = AppConstants.USER_EVENT_VIDEOS;
        videoUploadModel.setUserType(mUserType);
        databaseHandler.addVideoDetails(videoUploadModel);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(NotificationUtils.getInstance().getNotificationChannel(notificationid));
            }
            notificationBuilder = NotificationUtils.getInstance().getNotificationBuilder(this, false, "Uploading Video ",
                    notificationid, 100, 0);
            notification = notificationBuilder.build();
            notificationManager.notify(notificationid, notification);
        } else {
            notificationCompatBuilder = NotificationUtils.getInstance().getNotificationCompatBuilder(this, false, "Uploading Video ",
                    notificationid, 100, 0);
            notification = notificationCompatBuilder.build();
            notificationManager.notify(notificationid, notification);
        }
        startForeground(notificationid, notification);

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                        .build();

        observerVideo = transferUtility.upload(AppConstants.BUCKET_NAME, UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + videoFile.getName(), videoFile);

        observerImage = transferUtility.upload(AppConstants.BUCKET_NAME, UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + thumbImgFile.getName(), thumbImgFile);

        observerVideo.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (TransferState.COMPLETED.equals(observerVideo.getState())) {
                    try {
                        JsonObject mObject = new JsonObject();
                        mObject.addProperty(SpectatorLiveModel.PROFILE_ID, profileID);
                        mObject.addProperty(SpectatorLiveModel.USERID, userID);
                        mObject.addProperty(SpectatorLiveModel.USERTYPE, AppConstants.USER_EVENT_VIDEOS);
                        mObject.addProperty(SpectatorLiveModel.CAPTION, caption);
                        mObject.addProperty(SpectatorLiveModel.FILEURL, UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + videoFile.getName());
                        mObject.addProperty(SpectatorLiveModel.THUMBNAIL, UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + thumbImgFile.getName());
                        mObject.addProperty(SpectatorLiveModel.EVENTID, eventID);
                        mObject.addProperty(SpectatorLiveModel.EVENT_FINISH_DATE, eventFinishDate);
                        mObject.addProperty(SpectatorLiveModel.LIVE_POST_PROFILE_ID, livePostProfileID);

                        JsonArray mJsonArray = new JsonArray();
                        mJsonArray.add(mObject);
                        UploadData(mJsonArray, notificationid, entity.getID());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                float percentage = ((float) bytesCurrent / (float) bytesTotal * 100);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationBuilder.setProgress(100, (int) percentage, false);
                    notification = notificationBuilder.build();
                } else {
                    notificationCompatBuilder.setProgress(100, (int) percentage, false);
                    notification = notificationCompatBuilder.build();
                }
                notificationManager.notify(notificationid, notification);
            }

            @Override
            public void onError(int id, Exception ex) {
                onDownloadComplete(getString(R.string.file_upload_failure), videoUploadModel.getNotificationflag(), String.valueOf(notificationid), false);
            }
        });

        observerImage.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @Override
            public void onError(int id, Exception ex) {
                onDownloadComplete(getString(R.string.file_upload_failure), videoUploadModel.getNotificationflag(), String.valueOf(notificationid), false);

            }
        });
    }

    private void UploadData(JsonArray jsonArray, final int notificationId, final String id) {

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callPostSpectatorLiveStory("*", jsonArray).enqueue(new Callback<SpectatorLiveModel>() {
            @Override
            public void onResponse(Call<SpectatorLiveModel> call, Response<SpectatorLiveModel> response) {
                onDownloadComplete(getString(R.string.file_upload_success), notificationId, id, true);
            }

            @Override
            public void onFailure(Call<SpectatorLiveModel> call, Throwable t) {
                onDownloadComplete(getString(R.string.file_upload_failure), videoUploadModel.getNotificationflag(), id, false);
            }
        });
    }

    private void onDownloadComplete(String value, int mNotificationID, String id, boolean isSuccess) {
        AppConstants.UPLOAD_STATUS = UploadStatus.FAILED;
        try {
            DatabaseHandler databaseHandler = new DatabaseHandler(this);
            stopForeground(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(NotificationUtils.getInstance().getNotificationChannel(mNotificationID));
                }
                notificationBuilder = NotificationUtils.getInstance().getNotificationBuilder(this, false, value, mNotificationID, 0, 0);
                notification = notificationBuilder.build();
            } else {
                notificationCompatBuilder = NotificationUtils.getInstance().getNotificationCompatBuilder(this, false, value, mNotificationID, 0, 0);
                notification = notificationCompatBuilder.build();
            }
            if (mNotificationID > 0) {
                if (isSuccess) {
                    AppConstants.UPLOAD_STATUS = UploadStatus.COMPLETED;
                    databaseHandler.deletepost(mNotificationID);
                    if (id != null && !id.equals(""))
                        databaseHandler.deleteRow(id);
                }
                notificationManager.cancelAll();
                notificationManager.notify(mNotificationID, notification);
                sendBroadcast(new Intent().setAction("UPLOAD_STATUS").putExtra("status", value));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError() {
    }

    @Override
    public void onFinish() {
    }

    @Override
    public void onDestroy() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
