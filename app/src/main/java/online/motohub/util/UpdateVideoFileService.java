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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import online.motohub.R;
import online.motohub.database.DatabaseHandler;
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
public class UpdateVideoFileService extends IntentService implements ProgressRequestBody.UploadCallbacks {

    public static final String NOTIFY_POST_VIDEO_UPDATED = "online.motohub.NOTIFY_POST_VIDEO_UPDATED";

    private Integer flag;

    private int count = 111;
    private NotificationManager mNotificationManager;
    private Notification.Builder mNotificationBuilder;
    private NotificationCompat.Builder mNotificationCompatBuilder;
    private Notification mNotification;

    private Integer mPostID;

    private File imagfile;
    private String postText, taggedProfileID;
    private Integer profileid, notificationid;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UpdateVideoFileService(String name) {
        super(UpdateVideoFileService.class.getName());
    }

    public UpdateVideoFileService() {
        super(UpdateVideoFileService.class.getName());
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
                int running = intent.getIntExtra("running", 0);
                intent.getStringExtra("dest_file");
                mPostID = intent.getIntExtra("postid", 0);

                imagfile = new File(intent.getStringExtra("imagefile"));
                postText = intent.getStringExtra("posttext");
                profileid = intent.getIntExtra("profileid", 0);
                taggedProfileID = intent.getStringExtra(PostsModel.TAGGED_PROFILE_ID);
                notificationid = running;
                String mVideoPath = intent.getStringExtra("videofile");
                UploadVideo(mVideoPath, this);
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
                    Json(path, thumbnail, post.getPosts(), post.getProfileID(), mPostID, post.getTaggedProfileID());
                    onDownloadComplete("File Uploaded", post.getNotificationflag());
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

    public void Json(String videourl, String thumbail, String post, Integer profileID, Integer postID, String
            taggedProfileID) {
        try {
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(PostsModel.POST_TEXT, URLEncoder.encode(post, "UTF-8"));

            String mVideoUrl = "[\"" + videourl + "\"]";
            String mThumbnail = "[\"" + thumbail + "\"]";
            mJsonObject.addProperty(PostsModel.POST_ID, postID);
            mJsonObject.addProperty(PostsModel.PostVideoURL, mVideoUrl);
            mJsonObject.addProperty(PostsModel.PostVideoThumbnailurl, mThumbnail);
            mJsonObject.addProperty(PostsModel.POST_PICTURE, "");
            mJsonObject.addProperty(PostsModel.PROFILE_ID, profileID);
            mJsonObject.addProperty(PostsModel.TAGGED_PROFILE_ID, taggedProfileID);
            mJsonObject.addProperty(PostsModel.WHO_POSTED_PROFILE_ID, profileID);
            mJsonObject.addProperty(PostsModel.WHO_POSTED_USER_ID, PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID));
            if (flag == 1) {
                mJsonObject.addProperty(PostsModel.IS_NEWS_FEED_POST, false);
            } else {
                mJsonObject.addProperty(PostsModel.IS_NEWS_FEED_POST, false);
            }
            JsonArray mJsonArray = new JsonArray();
            mJsonArray.add(mJsonObject);
            callUpdateProfilePosts(mJsonArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void callUpdateProfilePosts(JsonArray jsonArray) {
        String mFields = "*";
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callUpdateProfilePosts(mFields, APIConstants.POST_FEED_RELATION, jsonArray)
                .enqueue(new Callback<PostsModel>() {
                    @Override
                    public void onResponse(Call<PostsModel> call, Response<PostsModel> response) {
                        if (response.isSuccessful()) {
                            PostsModel mResponseObj = response.body();
                            PostsResModel mPostResModel = mResponseObj.getResource().get(0);
                            Intent mIntent = new Intent(NOTIFY_POST_VIDEO_UPDATED);
                            mIntent.putExtra(PostsModel.POST_MODEL, mPostResModel);
                            mIntent.putExtra(PostsModel.IS_UPDATE, true);
                            sendBroadcast(mIntent);
                        }
                    }

                    @Override
                    public void onFailure(Call<PostsModel> call, Throwable t) {
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

    private void UploadVideo(String compressedFilePath, Context mContext) {
        File videoFile = new File(compressedFilePath);
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        //  int count = databaseHandler.getPendingCount();
        VideoUploadModel videoUploadModel = new VideoUploadModel();
        String s = videoFile.toString();
        videoUploadModel.setVideoURL(s.substring(s.lastIndexOf("/") + 1, s.length()));
        videoUploadModel.setFlag(1);
        videoUploadModel.setThumbnailURl(imagfile.toString());
        videoUploadModel.setPosts(postText);
        videoUploadModel.setProfileID(profileid);
        videoUploadModel.setTaggedProfileID(taggedProfileID);
        videoUploadModel.setNotificationflag(notificationid);
        postText = "";
        databaseHandler.addVideoDetails(videoUploadModel);
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
        startForeground(notificationid,
                mNotification);
        UploadAsync uploadAsync = new UploadAsync(notificationid, videoFile, imagfile);
        uploadAsync.execute();
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


}
