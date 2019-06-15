package online.motohub.util;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import online.motohub.constants.AppConstants;
import online.motohub.database.DatabaseHandler;
import online.motohub.model.GalleryImgModel;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.VideoUploadModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinnesPostFileUploadService extends IntentService implements ProgressRequestBody.UploadCallbacks {

    public static final String NOTIFY_POST_VIDEO_UPDATED = "online.motohub.NOTIFY_POST_VIDEO_UPDATED";
    //S3 server
    /*AmazonS3Client s3;
    BasicAWSCredentials credentials;
    TransferUtility transferUtility;*/
    TransferObserver observer, observer1;
    private Integer flag;
    private String mUserType, mPostStr;
    private int count = 111;
    private VideoUploadModel videoUploadModel;
    private NotificationManager mNotificationManager;
    private Notification.Builder mNotificationBuilder;
    private NotificationCompat.Builder mNotificationCompatBuilder;
    private Notification mNotification;
    private int mSubscriptionID;

    public BusinnesPostFileUploadService(String name) {
        super(name);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        count = 111;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {

            if (intent != null) {
                flag = intent.getIntExtra("flag", 0);
                AWSMobileClient.getInstance().initialize(this).execute();
                int mNotificationID = intent.getIntExtra("running", 0);
                String mVideoPath = intent.getStringExtra("videofile");
                File mThumbImgFile = new File(intent.getStringExtra("imagefile"));
                if (intent.hasExtra("usertype")) {
                    mUserType = intent.getStringExtra("usertype");
                    if (mUserType.equals(AppConstants.CLUB_USER)) {
                        if (intent.hasExtra(AppConstants.TO_SUBSCRIBED_USER_ID))
                            mSubscriptionID = intent.getIntExtra(AppConstants.TO_SUBSCRIBED_USER_ID, 0);
                    }
                }
                mPostStr = intent.getStringExtra("posttext");
                String mTaggedProfileID = intent.getStringExtra(PostsModel.TAGGED_PROFILE_ID);
                int mProfileID = intent.getIntExtra("profileid", 0);

                UploadVideoFile(this, mVideoPath, mThumbImgFile, mPostStr, mProfileID, mTaggedProfileID, mNotificationID);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onProgressUpdate(int percentage, int notificationId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationBuilder.setProgress(100, percentage, false);
            mNotification = mNotificationBuilder.build();
        } else {
            mNotificationCompatBuilder.setProgress(100, percentage, false);
            mNotification = mNotificationCompatBuilder.build();
        }
        mNotificationManager.notify(notificationId, mNotification);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }

    private void UploadVideoFile(Context mContext, String compressedFilePath, File mImageFile, String mPostStr,
                                 int mProfileID, String mTaggedProfileID, int mNotificationID) {
        File videoFile = new File(compressedFilePath);
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        //  int count = databaseHandler.getPendingCount();
        videoUploadModel = new VideoUploadModel();
        String s = videoFile.toString();
        videoUploadModel.setVideoURL(s.substring(s.lastIndexOf("/") + 1));
        videoUploadModel.setFlag(1);
        videoUploadModel.setThumbnailURl(mImageFile.toString());
        videoUploadModel.setPosts(mPostStr);
        videoUploadModel.setProfileID(mProfileID);
        videoUploadModel.setTaggedProfileID(mTaggedProfileID);
        videoUploadModel.setNotificationflag(mNotificationID);
        mPostStr = "";
        databaseHandler.addVideoDetails(videoUploadModel);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(NotificationUtils.getInstance().getNotificationChannel(mNotificationID));
            }
            mNotificationBuilder = NotificationUtils.getInstance().getNotificationBuilder(this, false, "Uploading Video",
                    mNotificationID, 100, 0);
            mNotification = mNotificationBuilder.build();
            mNotificationManager.notify(mNotificationID, mNotification);
        } else {
            mNotificationCompatBuilder = NotificationUtils.getInstance().getNotificationCompatBuilder(this, false, "Uploading Video",
                    mNotificationID, 100, 0);
            mNotification = mNotificationCompatBuilder.build();
            mNotificationManager.notify(mNotificationID, mNotification);
        }
        startForeground(mNotificationID, mNotification);
        UploadAsync uploadAsync = new UploadAsync(mNotificationID, videoFile, mImageFile);
        uploadAsync.execute();
    }

    private void amazoneUpload(final File video, final File image, final int notificationid) {

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(NotificationUtils.getInstance().getNotificationChannel(notificationid));
            }
            mNotificationBuilder = NotificationUtils.getInstance().getNotificationBuilder(this, false, "Uploading Video",
                    notificationid, 100, 0);
            mNotification = mNotificationBuilder.build();
            mNotificationManager.notify(notificationid, mNotification);
        } else {
            mNotificationCompatBuilder = NotificationUtils.getInstance().getNotificationCompatBuilder(this, false, "Uploading Video",
                    notificationid, 100, 0);
            mNotification = mNotificationCompatBuilder.build();
            mNotificationManager.notify(notificationid, mNotification);
        }
        startForeground(notificationid, mNotification);

       /* ClientConfiguration configuration = new ClientConfiguration();
        configuration.setMaxErrorRetry(3);
        configuration.setConnectionTimeout(501000);
        configuration.setSocketTimeout(501000);
        configuration.setProtocol(Protocol.HTTP);

        credentials = new BasicAWSCredentials(AppConstants.KEY, AppConstants.SECRET);
        s3 = new AmazonS3Client(credentials, configuration);
        s3.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));
        transferUtility = new TransferUtility(s3, BusinnesPostFileUploadService.this);*/

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                        .build();

        if (!video.exists()) {
            Toast.makeText(BusinnesPostFileUploadService.this, "File Not Found!", Toast.LENGTH_SHORT).show();
            return;
        }

        observer = transferUtility.upload(AppConstants.BUCKET_NAME, UrlUtils.FILE_WRITE_POST + video.getName(), video);

        observer1 = transferUtility.upload(AppConstants.BUCKET_NAME, UrlUtils.FILE_WRITE_POST + image.getName(), image);

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (TransferState.COMPLETED.equals(observer.getState())) {

                    String imageFileName = image.getName().substring(image.getName().lastIndexOf("/") + 1);
                    String videoFileName = video.getName().substring(video.getName().lastIndexOf("/") + 1);

                    createPostJson(videoFileName, imageFileName, videoUploadModel.getPosts(), videoUploadModel.getProfileID(), videoUploadModel.getTaggedProfileID());
                    String postText = "";
                    try {
                        postText = URLEncoder.encode(videoUploadModel.getPosts(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    JsonArray jsonElements = new JsonArray();
                    JsonObject obj = new JsonObject();
                    obj.addProperty(GalleryImgModel.USER_ID, getUserId());
                    obj.addProperty(GalleryImgModel.PROFILE_ID, videoUploadModel.getProfileID());
                    obj.addProperty(GalleryImgModel.VIDEO_URL, UrlUtils.FILE_WRITE_POST + videoFileName);
                    obj.addProperty(GalleryImgModel.THUMBNAIL, UrlUtils.FILE_WRITE_POST + imageFileName);
                    obj.addProperty(GalleryImgModel.USER_TYPE, videoUploadModel.getUserType());
                    obj.addProperty(GalleryImgModel.CAPTION, postText != null ? postText : " ");
                    jsonElements.add(obj);
                    callAddVideoToGallery(jsonElements);
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                long _bytesCurrent = bytesCurrent;
                long _bytesTotal = bytesTotal;

                float percentage = ((float) _bytesCurrent / (float) _bytesTotal * 100);
                //Log.d("percentage", "" + percentage);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
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
                Toast.makeText(BusinnesPostFileUploadService.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        observer1.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (TransferState.COMPLETED.equals(observer1.getState())) {
                    //Toast.makeText(ProfileUploadService.this, "File Upload Complete", Toast.LENGTH_SHORT).show();
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                long _bytesCurrent = bytesCurrent;
                long _bytesTotal = bytesTotal;
                float percentage = ((float) _bytesCurrent / (float) _bytesTotal * 100);
                Log.d("percentage", "" + percentage);

            }

            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(BusinnesPostFileUploadService.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createPostJson(String videourl, String thumbail, String post, Integer profileID, String taggedProfileID) {
        try {
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(PostsModel.POST_TEXT, URLEncoder.encode(post, "UTF-8"));

            String mVideoUrl = "[\"" + UrlUtils.FILE_WRITE_POST + videourl + "\"]";
            String mThumbnail = "[\"" + UrlUtils.FILE_WRITE_POST + thumbail + "\"]";

            mJsonObject.addProperty(PostsModel.PostVideoURL, mVideoUrl);
            mJsonObject.addProperty(PostsModel.PostVideoThumbnailurl, mThumbnail);
            mJsonObject.addProperty(PostsModel.PROFILE_ID, profileID);
            mJsonObject.addProperty(PostsModel.TAGGED_PROFILE_ID, taggedProfileID);
            mJsonObject.addProperty(PostsModel.WHO_POSTED_PROFILE_ID, profileID);
            mJsonObject.addProperty(PostsModel.USER_TYPE, mUserType);
            mJsonObject.addProperty(PostsModel.WHO_POSTED_USER_ID, PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID));
            mJsonObject.addProperty(PostsModel.IS_NEWS_FEED_POST, true);

            if (mUserType.equals(AppConstants.CLUB_USER))
                mJsonObject.addProperty(PostsModel.TO_SUBSCRIBED_USER_ID, mSubscriptionID);

            JsonArray mJsonArray = new JsonArray();
            mJsonArray.add(mJsonObject);
            callCreateProfilePosts(mJsonArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void callCreateProfilePosts(JsonArray jsonArray) {
        String mFields = "*";
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callCreateProfilePosts(mFields, APIConstants.POST_FEED_RELATION, jsonArray)
                .enqueue(new Callback<PostsModel>() {
                    @Override
                    public void onResponse(Call<PostsModel> call, Response<PostsModel> response) {
                        if (response.isSuccessful()) {
                            PostsModel mResponseObj = response.body();
                            PostsResModel mPostResModel = mResponseObj.getResource().get(0);
                            Intent mIntent = new Intent(NOTIFY_POST_VIDEO_UPDATED);
                            mIntent.putExtra(PostsModel.POST_MODEL, mPostResModel);
                            mIntent.putExtra(AppConstants.USER_TYPE, mUserType);
                            sendBroadcast(mIntent);
                        }
                    }

                    @Override
                    public void onFailure(Call<PostsModel> call, Throwable t) {
                    }
                });
    }

    private void callAddVideoToGallery(JsonArray jsonArray) {
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().postGalleryVideo(jsonArray)
                .enqueue(new Callback<GalleryImgModel>() {
                    @Override
                    public void onResponse(Call<GalleryImgModel> call, Response<GalleryImgModel> response) {
                        onDownloadComplete("File Uploaded", videoUploadModel.getNotificationflag());
                    }

                    @Override
                    public void onFailure(Call<GalleryImgModel> call, Throwable t) {
                        // onDownloadComplete(t.getMainObj(), 0);
                    }
                });

    }

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
            if (checkCount == 0 || mNotificationID == 0) {
                count = count + 1;
                mNotificationManager.cancelAll();
                mNotificationManager.notify(count, mNotification);
            } else if (mNotificationID > 0) {
                databaseHandler.deletepost(mNotificationID);
                mNotificationManager.notify(mNotificationID, mNotification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String getHttpFilePath(String filePath) {
        return filePath;
    }

    //Some Stuff

    protected String getUserId() {
        return String.valueOf(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID));
    }

    @SuppressLint("StaticFieldLeak")
    private class UploadAsync extends AsyncTask<String, String, Void> {

        private Integer notificationid;
        private File imagfile;
        private File videofile;

        public UploadAsync(Integer notificationid, File videofile, File ImageFile) {
            this.notificationid = notificationid;
            imagfile = ImageFile;
            this.videofile = videofile;
        }

        @Override
        protected Void doInBackground(String... paths) {
            amazoneUpload(videofile, imagfile, notificationid);
            return null;
        }
    }
}
