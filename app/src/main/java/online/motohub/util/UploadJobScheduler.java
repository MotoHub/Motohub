package online.motohub.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
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

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UploadJobScheduler extends JobService implements ProgressRequestBody.UploadCallbacks {
    private static final String TAG = "UploadJobService";
    private DatabaseHandler databaseHandler = new DatabaseHandler(this);
    private AmazonS3Client s3;
    private BasicAWSCredentials credentials;
    private TransferUtility transferUtility;
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
    private JobParameters param;


    @Override
    public boolean onStartJob(JobParameters params) {
        String data = params.getExtras().getString("data");
        Gson g = new Gson();
        SpectatorLiveEntity myObj = g.fromJson(data, SpectatorLiveEntity.class);
        this.param = params;
        new JobTask(myObj).execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        return false;
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

    @SuppressLint("StaticFieldLeak")
    private class JobTask extends AsyncTask<JobParameters, Void, JobParameters> {
        private SpectatorLiveEntity myObj;

        JobTask(SpectatorLiveEntity myObj) {
            this.myObj = myObj;
        }

        @Override
        protected JobParameters doInBackground(final JobParameters... params) {
            /*Runnable r = new Runnable() {
                public void run() {
                    synchronized (this) {
                        try {
                            amazoneUpload(UploadJobScheduler.this, myObj, params[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Thread t = new Thread(r);
            t.start();*/

            uploadFileNotification(UploadJobScheduler.this, myObj, params[0]);
            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
        }
    }

    private void uploadFileNotification(Context mContext, SpectatorLiveEntity entity, final JobParameters param) {
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
        videoUploadModel.setVideoURL(s.substring(s.lastIndexOf("/") + 1, s.length()));
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

        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setMaxErrorRetry(3);
        configuration.setConnectionTimeout(501000);
        configuration.setSocketTimeout(501000);
        configuration.setProtocol(Protocol.HTTP);

        credentials = new BasicAWSCredentials(AppConstants.KEY, AppConstants.SECRET);
        s3 = new AmazonS3Client(credentials, configuration);
        s3.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));
        transferUtility = new TransferUtility(s3, UploadJobScheduler.this);

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
                        UploadData(mJsonArray, notificationid, param);
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
                Toast.makeText(UploadJobScheduler.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(UploadJobScheduler.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UploadData(JsonArray jsonArray, final int notificationId, final JobParameters param) {

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callPostSpectatorLiveStory("*", jsonArray).enqueue(new Callback<SpectatorLiveModel>() {
            @Override
            public void onResponse(Call<SpectatorLiveModel> call, Response<SpectatorLiveModel> response) {
                jobFinished(param, false);
                onDownloadComplete("File Uploaded", notificationId);
            }

            @Override
            public void onFailure(Call<SpectatorLiveModel> call, Throwable t) {
                onDownloadComplete("File failed", videoUploadModel.getNotificationflag());
            }
        });
    }

    @SuppressWarnings("SameParameterValue")
    private void onDownloadComplete(String value, int mNotificationID) {
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
                //mNotificationManager.cancelAll();
                mNotificationManager.notify(mNotificationID, mNotification);
                sendBroadcast(new Intent().setAction("UPLOAD_STATUS").putExtra("status", value));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
