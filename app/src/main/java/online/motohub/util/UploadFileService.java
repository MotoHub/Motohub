package online.motohub.util;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import online.motohub.R;
import online.motohub.database.DatabaseHandler;
import online.motohub.model.GalleryImgModel;
import online.motohub.model.ImageModel;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.VideoUploadModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UploadFileService extends IntentService implements ProgressRequestBody.UploadCallbacks {

    public static final String NOTIFY_POST_VIDEO_UPDATED = "online.motohub.NOTIFY_POST_VIDEO_UPDATED";

    private Integer flag;
    private String mUserType, mPostStr;
    private int count = 111;

    private NotificationManager mNotificationManager;
    private Notification.Builder mNotificationBuilder;
    private NotificationCompat.Builder mNotificationCompatBuilder;
    private Notification mNotification;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UploadFileService(String name) {
        super(UploadFileService.class.getName());
    }

    public UploadFileService() {
        super(UploadFileService.class.getName());
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {

            if (intent != null) {
                flag = intent.getIntExtra("flag", 0);

                int mNotificationID = intent.getIntExtra("running", 0);
                String mVideoPath = intent.getStringExtra("videofile");
                File mThumbImgFile = new File(intent.getStringExtra("imagefile"));
                if (intent.hasExtra("usertype"))
                    mUserType = intent.getStringExtra("usertype");
                else
                    mUserType = "user";
                mPostStr = intent.getStringExtra("posttext");
                String mTaggedProfileID = intent.getStringExtra(PostsModel.TAGGED_PROFILE_ID);
                int mProfileID = intent.getIntExtra("profileid", 0);

                UploadVideoFile(this, mVideoPath, mThumbImgFile, mPostStr, mProfileID, mTaggedProfileID, mNotificationID);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void uploadImgAndVidToServer(int notificationid, File videoFile, File imageFile) {
        ProgressRequestBody fileBody = new ProgressRequestBody(videoFile, this, notificationid);
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("files[]",
                videoFile.getName(), fileBody);
        RequestBody imageBody = RequestBody.create(MediaType.parse("*/*"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("files[]",
                imageFile.getName(), imageBody);
        UploadVideo(videoPart, imagePart, this, notificationid);
    }

    public void UploadVideo(MultipartBody.Part video, MultipartBody.Part image, final Context context, int notificationid) {

        final DatabaseHandler databaseHandler = new DatabaseHandler(context);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .uploadpostview(video, image).enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    ImageModel imageModel = (ImageModel) mResponseObj;
                    String path = imageModel.getmModels().get(0).getPath();
                    String thumbnail = imageModel.getmModels().get(1).getPath();
                    String[] splited_text = path.split("/");
                    String destFilePath = Environment.getExternalStorageDirectory().getPath()
                            + getString(R.string.util_app_folder_root_path);
                    File file = new File(destFilePath + "/" + splited_text[1]);
                    if (file.exists()) {
                        file.delete();
                    }
                    VideoUploadModel post = databaseHandler.getPost(splited_text[1]);
                    Json(path, thumbnail, post.getPosts(), post.getProfileID(), post.getTaggedProfileID());
                    onDownloadComplete("File Uploaded", post.getNotificationflag());
                    JsonArray jsonElements = new JsonArray();
                    JsonObject obj = new JsonObject();
                    obj.addProperty(GalleryImgModel.USER_ID, getUserId());
                    obj.addProperty(GalleryImgModel.PROFILE_ID, post.getProfileID());
                    obj.addProperty(GalleryImgModel.VIDEO_URL, getHttpFilePath(imageModel.getmModels().get(0).getPath()));
                    obj.addProperty(GalleryImgModel.THUMBNAIL, getHttpFilePath(imageModel.getmModels().get(1).getPath()));
                    obj.addProperty(GalleryImgModel.USER_TYPE, post.getUserType());
                    obj.addProperty(GalleryImgModel.CAPTION, mPostStr != null ? mPostStr : " ");
                    jsonElements.add(obj);
                    callAddVideoToGallery(jsonElements);

                } else {
                    databaseHandler.clearTable();
                    onDownloadComplete(getString(R.string.internet_err), 0);
                }
            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                databaseHandler.clearTable();
                onDownloadComplete(getString(R.string.internet_err), 0);
            }
        });

    }

    protected String getHttpFilePath(String filePath) {
        return filePath;
    }

    protected String getUserId() {
        return String.valueOf(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID));
    }

    private void callAddVideoToGallery(JsonArray jsonArray){
            RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().postGalleryVideo(jsonArray)
                    .enqueue(new Callback<GalleryImgModel>() {
                        @Override
                        public void onResponse(Call<GalleryImgModel> call, Response<GalleryImgModel> response) {

                        }

                        @Override
                        public void onFailure(Call<GalleryImgModel> call, Throwable t) {
                            // onDownloadComplete(t.getMessage(), 0);
                        }
                    });

    }

    public void Json(String videourl, String thumbail, String post, Integer profileID, String
            taggedProfileID) {
        try {
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(PostsModel.POST_TEXT, URLEncoder.encode(post, "UTF-8"));

            String mVideoUrl = "[\"" + videourl + "\"]";
            String mThumbnail = "[\"" + thumbail + "\"]";

            mJsonObject.addProperty(PostsModel.PostVideoURL, mVideoUrl);
            mJsonObject.addProperty(PostsModel.PostVideoThumbnailurl, mThumbnail);
            mJsonObject.addProperty(PostsModel.PROFILE_ID, profileID);
            mJsonObject.addProperty(PostsModel.TAGGED_PROFILE_ID, taggedProfileID);
            mJsonObject.addProperty(PostsModel.WHO_POSTED_PROFILE_ID, profileID);
            mJsonObject.addProperty(PostsModel.USER_TYPE, mUserType);
            mJsonObject.addProperty(PostsModel.WHO_POSTED_USER_ID, PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID));
            mJsonObject.addProperty(PostsModel.IS_NEWS_FEED_POST, true);
          /*  if (flag == 1) {
                mJsonObject.addProperty(PostsModel.IS_NEWS_FEED_POST, false);
            } else {
                mJsonObject.addProperty(PostsModel.IS_NEWS_FEED_POST, true);
            }*/
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
                            sendBroadcast(mIntent);
                        }
                    }

                    @Override
                    public void onFailure(Call<PostsModel> call, Throwable t) {
                    }
                });
    }

    private void onDownloadComplete(String value, int mNotificationID) {
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
    public void onDestroy() {
        count = 111;
    }

    @Override
    public void onError() {
    }

    @Override
    public void onFinish() {
    }

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
            uploadImgAndVidToServer(notificationid, videofile, imagfile);
            return null;
        }
    }

    private void UploadVideoFile(Context mContext, String compressedFilePath, File mImageFile, String mPostStr,
                                 int mProfileID, String mTaggedProfileID, int mNotificationID) {
        File videoFile = new File(compressedFilePath);
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        //  int count = databaseHandler.getPendingCount();
        VideoUploadModel videoUploadModel = new VideoUploadModel();
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
        startForeground(mNotificationID,
                mNotification);
        UploadAsync uploadAsync = new UploadAsync(mNotificationID, videoFile, mImageFile);
        uploadAsync.execute();
    }

}
