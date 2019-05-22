package online.motohub.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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
import java.util.Objects;

import online.motohub.R;
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

import static online.motohub.util.UploadFileService.NOTIFY_POST_VIDEO_UPDATED;

public class MyWorkWithData extends Worker implements ProgressRequestBody.UploadCallbacks {

    private static final String TAB = MyWorkWithData.class.getSimpleName();
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_TEXT = "text";
    public static final String EXTRA_OUTPUT_MESSAGE = "output_message";

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
    private Context context;
    private boolean isSuccess;

    public MyWorkWithData(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        AWSMobileClient.getInstance().initialize(context).execute();
    }

    @NonNull
    @Override
    public Result doWork() {

        flag = getInputData().getInt("flag", 0);

        int mNotificationID = getInputData().getInt("running", 0);
        String mVideoPath = getInputData().getString("videofile");
        File mThumbImgFile = new File(Objects.requireNonNull(getInputData().getString("imagefile")));
        mUserType = getInputData().getString("usertype");
        assert mUserType != null;
        if (mUserType.equals(AppConstants.CLUB_USER)) {
            mSubscriptionID = getInputData().getInt(AppConstants.TO_SUBSCRIBED_USER_ID, 0);
        }
        mPostStr = getInputData().getString("posttext");
        String mTaggedProfileID = getInputData().getString(PostsModel.TAGGED_PROFILE_ID);
        int mProfileID = getInputData().getInt("profileid", 0);

        assert mVideoPath != null;
        File videoFile = new File(mVideoPath);
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        videoUploadModel = new VideoUploadModel();
        String s = videoFile.toString();
        videoUploadModel.setVideoURL(s.substring(s.lastIndexOf("/") + 1, s.length()));
        videoUploadModel.setFlag(1);
        videoUploadModel.setThumbnailURl(mThumbImgFile.toString());
        videoUploadModel.setPosts(mPostStr);
        videoUploadModel.setProfileID(mProfileID);
        videoUploadModel.setTaggedProfileID(mTaggedProfileID);
        videoUploadModel.setNotificationflag(mNotificationID);
        mPostStr = "";
        databaseHandler.addVideoDetails(videoUploadModel);

        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(NotificationUtils.getInstance().getNotificationChannel(mNotificationID));
            }
            mNotificationBuilder = NotificationUtils.getInstance().getNotificationBuilder(context, false, "Uploading Video",
                    mNotificationID, 100, 0);
            mNotification = mNotificationBuilder.build();
            mNotificationManager.notify(mNotificationID, mNotification);
        } else {
            mNotificationCompatBuilder = NotificationUtils.getInstance().getNotificationCompatBuilder(context, false, "Uploading Video",
                    mNotificationID, 100, 0);
            mNotification = mNotificationCompatBuilder.build();
            mNotificationManager.notify(mNotificationID, mNotification);
        }

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                        .build();

        observer = transferUtility.upload(AppConstants.BUCKET_NAME, UrlUtils.FILE_WRITE_POST + videoFile.getName(), videoFile);

        observer1 = transferUtility.upload(AppConstants.BUCKET_NAME, UrlUtils.FILE_WRITE_POST + mThumbImgFile.getName(), mThumbImgFile);

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED.equals(observer.getState())) {

                    String imageFileName = mThumbImgFile.getName().substring(mThumbImgFile.getName().lastIndexOf("/") + 1);
                    String videoFileName = videoFile.getName().substring(videoFile.getName().lastIndexOf("/") + 1);

                    createPostJson(videoFileName, imageFileName, videoUploadModel.getPosts(), videoUploadModel.getProfileID(), videoUploadModel.getTaggedProfileID());

                    JsonArray jsonElements = new JsonArray();
                    JsonObject obj = new JsonObject();
                    String postText = "";
                    try {
                        postText = URLEncoder.encode(videoUploadModel.getPosts(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
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
                Log.d("percentage", "" + percentage);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    mNotificationBuilder.setProgress(100, (int) percentage, false);
                    mNotification = mNotificationBuilder.build();
                } else {
                    mNotificationCompatBuilder.setProgress(100, (int) percentage, false);
                    mNotification = mNotificationCompatBuilder.build();
                }
                mNotificationManager.notify(mNotificationID, mNotification);
            }

            @Override
            public void onError(int id, Exception ex) {
                //Toast.makeText(context, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (isSuccess) {
            return Result.success();
        } else {
            return Result.retry();
        }
    }

    private void UploadVideoFile(Context mContext, String compressedFilePath, File mImageFile, String mPostStr,
                                 int mProfileID, String mTaggedProfileID, int mNotificationID) {
        File videoFile = new File(compressedFilePath);
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        //  int count = databaseHandler.getPendingCount();
        videoUploadModel = new VideoUploadModel();
        String s = videoFile.toString();
        videoUploadModel.setVideoURL(s.substring(s.lastIndexOf("/") + 1, s.length()));
        videoUploadModel.setFlag(1);
        videoUploadModel.setThumbnailURl(mImageFile.toString());
        videoUploadModel.setPosts(mPostStr);
        videoUploadModel.setProfileID(mProfileID);
        videoUploadModel.setTaggedProfileID(mTaggedProfileID);
        videoUploadModel.setNotificationflag(mNotificationID);
        mPostStr = "";
        databaseHandler.addVideoDetails(videoUploadModel);
        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(NotificationUtils.getInstance().getNotificationChannel(mNotificationID));
            }
            mNotificationBuilder = NotificationUtils.getInstance().getNotificationBuilder(context, false, "Uploading Video",
                    mNotificationID, 100, 0);
            mNotification = mNotificationBuilder.build();
            mNotificationManager.notify(mNotificationID, mNotification);
        } else {
            mNotificationCompatBuilder = NotificationUtils.getInstance().getNotificationCompatBuilder(context, false, "Uploading Video",
                    mNotificationID, 100, 0);
            mNotification = mNotificationCompatBuilder.build();
            mNotificationManager.notify(mNotificationID, mNotification);
        }
        //startForeground(mNotificationID, mNotification);
        amazoneUpload(videoFile, mImageFile, mNotificationID);
        /*UploadAsync uploadAsync = new UploadAsync(mNotificationID, videoFile, mImageFile);
        uploadAsync.execute();*/
    }

    private void amazoneUpload(final File video, final File image, final int notificationid) {

        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(NotificationUtils.getInstance().getNotificationChannel(notificationid));
            }
            mNotificationBuilder = NotificationUtils.getInstance().getNotificationBuilder(context, false, "Uploading Video",
                    notificationid, 100, 0);
            mNotification = mNotificationBuilder.build();
            mNotificationManager.notify(notificationid, mNotification);
        } else {
            mNotificationCompatBuilder = NotificationUtils.getInstance().getNotificationCompatBuilder(context, false, "Uploading Video",
                    notificationid, 100, 0);
            mNotification = mNotificationCompatBuilder.build();
            mNotificationManager.notify(notificationid, mNotification);
        }
        //startForeground(notificationid, mNotification);

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                        .build();

        if (!video.exists()) {
            Toast.makeText(context, "File Not Found!", Toast.LENGTH_SHORT).show();
            return;
        }

        observer = transferUtility.upload(AppConstants.BUCKET_NAME, UrlUtils.FILE_WRITE_POST + video.getName(), video);

        observer1 = transferUtility.upload(AppConstants.BUCKET_NAME, UrlUtils.FILE_WRITE_POST + image.getName(), image);

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (state.COMPLETED.equals(observer.getState())) {

                    String imageFileName = image.getName().substring(image.getName().lastIndexOf("/") + 1);
                    String videoFileName = video.getName().substring(video.getName().lastIndexOf("/") + 1);

                    createPostJson(videoFileName, imageFileName, videoUploadModel.getPosts(), videoUploadModel.getProfileID(), videoUploadModel.getTaggedProfileID());

                    JsonArray jsonElements = new JsonArray();
                    JsonObject obj = new JsonObject();
                    String postText = "";
                    try {
                        postText = URLEncoder.encode(videoUploadModel.getPosts(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
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
                Log.d("percentage", "" + percentage);

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

                Toast.makeText(context, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        observer1.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (state.COMPLETED.equals(observer1.getState())) {
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
                Toast.makeText(context, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callAddVideoToGallery(JsonArray jsonArray) {

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().postGalleryVideo(jsonArray)
                .enqueue(new Callback<GalleryImgModel>() {
                    @Override
                    public void onResponse(Call<GalleryImgModel> call, Response<GalleryImgModel> response) {
                        onDownloadComplete("File Uploaded", videoUploadModel.getNotificationflag());
                        isSuccess = true;
                    }

                    @Override
                    public void onFailure(Call<GalleryImgModel> call, Throwable t) {
                        // onDownloadComplete(t.getMainObj(), 0);
                    }
                });

    }

    protected String getUserId() {
        return String.valueOf(PreferenceUtils.getInstance(context).getIntData(PreferenceUtils.USER_ID));
    }

    private void createPostJson(String videourl, String thumbail, String post, Integer profileID, String taggedProfileID) {
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
            mJsonObject.addProperty(PostsModel.WHO_POSTED_USER_ID, PreferenceUtils.getInstance(context).getIntData(PreferenceUtils.USER_ID));
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
                            context.sendBroadcast(mIntent);
                        }
                    }

                    @Override
                    public void onFailure(Call<PostsModel> call, Throwable t) {
                    }
                });
    }

    private void onDownloadComplete(String value, int mNotificationID) {
        try {
            DatabaseHandler databaseHandler = new DatabaseHandler(context);
            int checkCount = databaseHandler.getPendingCount();
            //stopForeground(true);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                if (mNotificationManager != null) {
                    mNotificationManager.createNotificationChannel(NotificationUtils.getInstance().getNotificationChannel(mNotificationID));
                }
                mNotificationBuilder = NotificationUtils.getInstance().getNotificationBuilder(context, false, value, mNotificationID, 0, 0);
                mNotification = mNotificationBuilder.build();
            } else {
                mNotificationCompatBuilder = NotificationUtils.getInstance().getNotificationCompatBuilder(context, false, value, mNotificationID, 0, 0);
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

    @Override
    public void onProgressUpdate(int percentage, int notificationid) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationBuilder.setProgress(100, percentage, false);
            mNotification = mNotificationBuilder.build();
        } else {
            mNotificationCompatBuilder.setProgress(100, percentage, false);
            mNotification = mNotificationCompatBuilder.build();
        }
        mNotificationManager.notify(notificationid, mNotification);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

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
            //uploadImgAndVidToServer(notificationid, videofile, imagfile);
            amazoneUpload(videofile, imagfile, notificationid);
            return null;
        }
    }

}