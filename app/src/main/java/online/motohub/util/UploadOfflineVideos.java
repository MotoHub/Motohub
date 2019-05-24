package online.motohub.util;

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
import android.widget.Toast;

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
import java.util.ArrayList;

import online.motohub.database.DatabaseHandler;
import online.motohub.model.SpectatorLiveEntity;
import online.motohub.model.SpectatorLiveModel;
import online.motohub.model.VideoUploadModel;
import online.motohub.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadOfflineVideos extends IntentService implements ProgressRequestBody.UploadCallbacks {

    DatabaseHandler handler = new DatabaseHandler(this);
    /*private AmazonS3Client s3;
    private BasicAWSCredentials credentials;K
    private TransferUtility transferUtility;*/
    private TransferObserver observerVideo;
    private TransferObserver observerImage;
    private int count = 111;
    private NotificationManager mNotificationManager;
    private Notification.Builder mNotificationBuilder;
    private NotificationCompat.Builder mNotificationCompatBuilder;
    private Notification mNotification;
    private int mProfileID, mUserId;
    private String mUserType = AppConstants.USER_EVENT_VIDEOS, mCaption = "";
    private VideoUploadModel post, videoUploadModel;
    private String mVideoPath;
    private File mThumbImgFile;
    private int mEventId;
    private String mEventFinishDate;
    private int mLivePostProfileID;
    private ArrayList<SpectatorLiveEntity> list;

    public UploadOfflineVideos(String name) {
        super(UploadOfflineVideos.class.getName());
    }

    public UploadOfflineVideos() {
        super(UploadOfflineVideos.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        AWSMobileClient.getInstance().initialize(this).execute();
        String data = intent.getStringExtra("data");
        Gson g = new Gson();
        SpectatorLiveEntity myObj = g.fromJson(data, SpectatorLiveEntity.class);
        doTask(myObj);
    }

    private void doTask(SpectatorLiveEntity myObj) {
        /*if (list.size() > 0) {
            SpectatorLiveEntity myObj;
            for (int mFinalValue = 0; mFinalValue < list.size(); mFinalValue++) {
                try {
                    myObj = list.get(mFinalValue);
                    uploadFileNotification(UploadOfflineVideos.this, myObj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            *//*Runnable r = new Runnable() {
                public void run() {
                    for (int mFinalValue = 0; mFinalValue < list.size(); mFinalValue++) {
                        synchronized (this) {
                            try {
                                uploadFileNotification(UploadOfflineVideos.this, list.get(mFinalValue));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            Thread t = new Thread(r);
            t.start();*//*
        }*/
        uploadFileNotification(UploadOfflineVideos.this, myObj);
    }

    @Override
    public void onProgressUpdate(int percentage, int notificationID) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationBuilder.setProgress(100, percentage, false);
            mNotification = mNotificationBuilder.build();
        } else {
            mNotificationCompatBuilder.setProgress(100, percentage, false);
            mNotification = mNotificationCompatBuilder.build();
        }
        mNotificationManager.notify(notificationID, mNotification);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onDestroy() {
        count = 111;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void uploadFileNotification(Context mContext, final SpectatorLiveEntity entity) {
        //SpectatorLiveEntity entity = list.get(mFinalValue);
        mProfileID = Integer.parseInt(entity.getProfileID());
        mUserId = Integer.parseInt(entity.getUserID());
        mCaption = entity.getCaption();
        mVideoPath = entity.getVideoUrl();
        mThumbImgFile = new File(entity.getThumbnail());
        mEventId = Integer.parseInt(entity.getEventID());
        mEventFinishDate = entity.getEventFinishDate();
        mLivePostProfileID = Integer.parseInt(entity.getLivePostProfileID());

        final File videoFile = new File(mVideoPath);
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        videoUploadModel = new VideoUploadModel();
        int count = databaseHandler.getPendingCount();
        final int notificationid = 1;
        String s = videoFile.toString();
        videoUploadModel.setVideoURL(s.substring(s.lastIndexOf("/") + 1));
        videoUploadModel.setFlag(1);
        videoUploadModel.setThumbnailURl(mThumbImgFile.toString());
        videoUploadModel.setProfileID(mProfileID);
        videoUploadModel.setNotificationflag(notificationid);
        videoUploadModel.setUserType(mUserType);
        databaseHandler.addVideoDetails(videoUploadModel);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(NotificationUtils.getInstance().getNotificationChannel(notificationid));
            }
            mNotificationBuilder = NotificationUtils.getInstance().getNotificationBuilder(this, false, "Uploading Video ",
                    notificationid, 100, 0);
            mNotification = mNotificationBuilder.build();
            mNotificationManager.notify(notificationid, mNotification);
        } else {
            mNotificationCompatBuilder = NotificationUtils.getInstance().getNotificationCompatBuilder(this, false, "Uploading Video ",
                    notificationid, 100, 0);
            mNotification = mNotificationCompatBuilder.build();
            mNotificationManager.notify(notificationid, mNotification);
        }
        startForeground(notificationid, mNotification);

        /*ClientConfiguration configuration = new ClientConfiguration();
        configuration.setMaxErrorRetry(3);
        configuration.setConnectionTimeout(501000);
        configuration.setSocketTimeout(501000);
        configuration.setProtocol(Protocol.HTTP);

        credentials = new BasicAWSCredentials(AppConstants.KEY, AppConstants.SECRET);
        s3 = new AmazonS3Client(credentials, configuration);
        s3.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));
        transferUtility = new TransferUtility(s3, UploadOfflineVideos.this);*/

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                        .build();

        observerVideo = transferUtility.upload(AppConstants.BUCKET_NAME, UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + videoFile.getName(), videoFile);

        observerImage = transferUtility.upload(AppConstants.BUCKET_NAME, UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + mThumbImgFile.getName(), mThumbImgFile);

        observerVideo.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (TransferState.COMPLETED.equals(observerVideo.getState())) {

                    try {
                        JsonObject mObject = new JsonObject();
                        mObject.addProperty(SpectatorLiveModel.PROFILE_ID, mProfileID);
                        mObject.addProperty(SpectatorLiveModel.USERID, mUserId);
                        mObject.addProperty(SpectatorLiveModel.USERTYPE, AppConstants.USER_EVENT_VIDEOS);
                        mObject.addProperty(SpectatorLiveModel.CAPTION, mCaption);
                        mObject.addProperty(SpectatorLiveModel.FILEURL, UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + videoFile.getName());
                        mObject.addProperty(SpectatorLiveModel.THUMBNAIL, UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + mThumbImgFile.getName());
                        mObject.addProperty(SpectatorLiveModel.EVENTID, mEventId);
                        mObject.addProperty(SpectatorLiveModel.EVENT_FINISH_DATE, mEventFinishDate);
                        mObject.addProperty(SpectatorLiveModel.LIVE_POST_PROFILE_ID, mLivePostProfileID);

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
                    mNotificationBuilder.setProgress(100, (int) percentage, false);
                    mNotification = mNotificationBuilder.build();
                } else {
                    mNotificationCompatBuilder.setProgress(100, (int) percentage, false);
                    mNotification = mNotificationCompatBuilder.build();
                }
                mNotificationManager.notify(notificationid, mNotification);
            }

            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(UploadOfflineVideos.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        observerImage.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (TransferState.COMPLETED.equals(observerImage.getState())) {
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentage = ((float) bytesCurrent / (float) bytesTotal * 100);
            }

            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(UploadOfflineVideos.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UploadData(JsonArray jsonArray, final int notificationId, final String id) {

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callPostSpectatorLiveStory("*", jsonArray).enqueue(new Callback<SpectatorLiveModel>() {
            @Override
            public void onResponse(Call<SpectatorLiveModel> call, Response<SpectatorLiveModel> response) {
                onDownloadComplete("File Uploaded", notificationId, id);
            }

            @Override
            public void onFailure(Call<SpectatorLiveModel> call, Throwable t) {
                onDownloadComplete("File failed", videoUploadModel.getNotificationflag(), id);
            }
        });
    }

    @SuppressWarnings("SameParameterValue")
    private void onDownloadComplete(String value, int mNotificationID, String id) {
        try {
            DatabaseHandler databaseHandler = new DatabaseHandler(this);
            int checkCount = databaseHandler.getPendingCount();
            stopForeground(true);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                if (mNotificationManager != null) {
                    mNotificationManager.createNotificationChannel(NotificationUtils.getInstance().getNotificationChannel(mNotificationID));
                }
                mNotificationBuilder = NotificationUtils.getInstance().getNotificationBuilder(this, false, value, mNotificationID, 0, 0);
                mNotification = mNotificationBuilder.build();
            } else {
                mNotificationCompatBuilder = NotificationUtils.getInstance().getNotificationCompatBuilder(this, false, value, mNotificationID, 0, 0);
                mNotification = mNotificationCompatBuilder.build();
            }
            if (mNotificationID > 0) {
                databaseHandler.deletepost(mNotificationID);
                if (id != null && !id.equals(""))
                    databaseHandler.deleteRow(id);
                mNotificationManager.cancelAll();
                mNotificationManager.notify(mNotificationID, mNotification);
                sendBroadcast(new Intent().setAction("UPLOAD_STATUS").putExtra("status", value));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
