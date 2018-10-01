package online.motohub.util;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import online.motohub.R;
import online.motohub.database.DatabaseHandler;
import online.motohub.model.GalleryImgModel;
import online.motohub.model.ImageModel;
import online.motohub.model.OnDemandVideoUploadedResponse;
import online.motohub.model.VideoUploadModel;
import online.motohub.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileUploadService extends IntentService implements ProgressRequestBody.UploadCallbacks {


    private int count = 111;
    private NotificationManager mNotificationManager;
    private Notification.Builder mNotificationBuilder;
    private NotificationCompat.Builder mNotificationCompatBuilder;
    private Notification mNotification;

    private String mUserType, mCaption = "";
    private VideoUploadModel post;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ProfileUploadService(String name) {
        super(ProfileUploadService.class.getName());
    }

    public ProfileUploadService() {
        super(ProfileUploadService.class.getName());
    }


    private void uploadImgAndVidToServer(int notificationid, File videoFile, File imageFile) {
        ProgressRequestBody fileBody = new ProgressRequestBody(videoFile, this, notificationid);
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("files[]",
                videoFile.getName(), fileBody);
        RequestBody imageBody = RequestBody.create(MediaType.parse("*/*"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("files[]",
                imageFile.getName(), imageBody);
        UploadVideo(videoPart, imagePart, this);
    }

    public void UploadVideo(MultipartBody.Part video, MultipartBody.Part image, final Context context) {

        final DatabaseHandler databaseHandler = new DatabaseHandler(context);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postVideoFile(video, image).enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    ImageModel mImageModel = (ImageModel) mResponseObj;
                    if (mImageModel.getmModels() != null) {
                        String path = mImageModel.getmModels().get(0).getPath();
                        String[] splited_text = path.split("/");
                        String destFilePath = Environment.getExternalStorageDirectory().getPath()
                                + getString(R.string.util_app_folder_root_path);
                        File file = new File(destFilePath + "/" + splited_text[2]);
                        if (file.exists()) {
                            file.delete();
                        }
                        post = databaseHandler.getPost(splited_text[2]);
                        JsonArray jsonElements = new JsonArray();
                        JsonObject obj = new JsonObject();
                        obj.addProperty(GalleryImgModel.USER_ID, getUserId());
                        obj.addProperty(GalleryImgModel.PROFILE_ID, post.getProfileID());
                        obj.addProperty(GalleryImgModel.VIDEO_URL, getHttpFilePath(mImageModel.getmModels().get(0).getPath()));
                        obj.addProperty(GalleryImgModel.THUMBNAIL, getHttpFilePath(mImageModel.getmModels().get(1).getPath()));
                        obj.addProperty(GalleryImgModel.USER_TYPE, post.getUserType());
                        obj.addProperty(GalleryImgModel.CAPTION, mCaption != null ? mCaption : " ");
                        jsonElements.add(obj);
                        /*if(!post.getUserType().trim().equals(AppConstants.ONDEMAND))
                            UploadData(jsonElements);*/
                        UploadData(jsonElements);
                    } else {
                        databaseHandler.clearTable();
                        onDownloadComplete(getString(R.string.internet_err), 0);
                    }
                }

            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                databaseHandler.clearTable();
                onDownloadComplete(getString(R.string.internet_err), 0);
            }
        });

    }

    public void UploadData(JsonArray jsonArray) {
	
	/*RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().postGalleryVideo(jsonArray)
                .enqueue(new Callback<GalleryImgModel>() {
                    @Override
                    public void onResponse(Call<GalleryImgModel> call, Response<GalleryImgModel> response) {
                        onDownloadComplete("File Uploaded", post.getNotificationflag());
                    }

                    @Override
                    public void onFailure(Call<GalleryImgModel> call, Throwable t) {
                       // onDownloadComplete(t.getMessage(), 0);
                    }
                });*/
	
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().postGalleryVideo1(jsonArray)
                .enqueue(new Callback<OnDemandVideoUploadedResponse>() {
                    @Override
                    public void onResponse(Call<OnDemandVideoUploadedResponse> call, Response<OnDemandVideoUploadedResponse> response) {
                        onDownloadComplete("File Uploaded", post.getNotificationflag());
                    }

                    @Override
                    public void onFailure(Call<OnDemandVideoUploadedResponse> call, Throwable t) {
                       // onDownloadComplete(t.getMessage(), 0);
                    }
                });
    }

    protected String getHttpFilePath(String filePath) {
        return filePath;
    }

    protected String getUserId() {
        return String.valueOf(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID));
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
        sendBroadcast(new Intent().setAction("UPLOAD_STATUS").putExtra("status", value));

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
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            if (intent != null) {
                int mProfileID = intent.getIntExtra(AppConstants.PROFILE_ID, 0);
                String mVideoPath = intent.getStringExtra(AppConstants.VIDEO_PATH);
                mCaption = intent.getStringExtra(AppConstants.CAPTION);
                mUserType = intent.getStringExtra(AppConstants.USER_TYPE);
                File mThumbImgFile = new File(intent.getStringExtra(AppConstants.IMAGE_PATH));
                UploadVideoFile(this, mVideoPath, mThumbImgFile, mProfileID);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    private void UploadVideoFile(Context mContext, String compressedFilePath, File mImageFile, int mProfileID) {
        File videoFile = new File(compressedFilePath);
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        VideoUploadModel videoUploadModel = new VideoUploadModel();
        int count = databaseHandler.getPendingCount();
        int notificationid = count + 1;
        String s = videoFile.toString();
        videoUploadModel.setVideoURL(s.substring(s.lastIndexOf("/") + 1, s.length()));
        videoUploadModel.setFlag(1);
        videoUploadModel.setThumbnailURl(mImageFile.toString());
        videoUploadModel.setProfileID(mProfileID);
        videoUploadModel.setNotificationflag(notificationid);
        videoUploadModel.setUserType(mUserType);
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

        UploadAsync uploadAsync = new UploadAsync(notificationid, videoFile, mImageFile);
        uploadAsync.execute();
    }
}


