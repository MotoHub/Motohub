package online.motohub.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;

import okhttp3.MultipartBody;
import online.motohub.database.DatabaseHandler;
import online.motohub.model.GalleryImgModel;
import online.motohub.model.ImageModel;
import online.motohub.model.VideoUploadModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.NotificationUtils;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.ProgressRequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileImageService extends IntentService implements ProgressRequestBody.UploadCallbacks {


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
    public ProfileImageService(String name) {
        super(ProfileImageService.class.getName());
    }

    public ProfileImageService() {
        super(ProfileImageService.class.getName());
    }

    private void uploadImageToServer(File mSelectedFile, int notificationid) {
        ProgressRequestBody fileBody = new ProgressRequestBody(mSelectedFile, this, notificationid);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", mSelectedFile.getName(), fileBody);
        UploadImage(filePart, this, notificationid);
    }

    private void uploadPicture(int id, String path) {

        JsonObject obj = new JsonObject();
        obj.addProperty(GalleryImgModel.USER_ID, getUserId());
        obj.addProperty(GalleryImgModel.MOTO_ID, id);
        obj.addProperty(GalleryImgModel.GALLERY_IMG, path);

        JsonArray jsonElements = new JsonArray();
        jsonElements.add(obj);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().postGalleryImg(jsonElements)
                .enqueue(new Callback<GalleryImgModel>() {
                    @Override
                    public void onResponse(Call<GalleryImgModel> call, Response<GalleryImgModel> response) {
                        //  activity.dismissAppDialog();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                        } else {
                        }
                    }

                    @Override
                    public void onFailure(Call<GalleryImgModel> call, Throwable t) {
                        //      activity.dismissAppDialog();
                        //      activity.retrofitOnFailure();
                    }
                });
    }


    public void UploadImage(MultipartBody.Part image, final Context context, int notificationid) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(NotificationUtils.getInstance().getNotificationChannel(notificationid));
            }
            mNotificationBuilder = NotificationUtils.getInstance().getNotificationBuilder(this, false, "Uploading Image",
                    notificationid, 100, 0);
            mNotification = mNotificationBuilder.build();
            mNotificationManager.notify(notificationid, mNotification);
        } else {
            mNotificationCompatBuilder = NotificationUtils.getInstance().getNotificationCompatBuilder(this, false, "Uploading Image",
                    notificationid, 100, 0);
            mNotification = mNotificationCompatBuilder.build();
            mNotificationManager.notify(notificationid, mNotification);
        }
        startForeground(notificationid, mNotification);
        final DatabaseHandler databaseHandler = new DatabaseHandler(context);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postImageFile(image).enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    ImageModel mImageModel = (ImageModel) mResponseObj;
                    if (mImageModel.getmModels() != null) {
                        String path = mImageModel.getmModels().get(0).getPath();
                        String[] splited_text = path.split("/");
                        VideoUploadModel post = databaseHandler.getImage(splited_text[2]);
                        if (post.getProfileID() != null) {
                            uploadPicture(post.getProfileID(), path);
                            onDownloadComplete("File Uploaded", post.getNotificationflag());
                        } else {
                            onDownloadComplete("Something Went Wrong", 0);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                databaseHandler.clearTable();
                onDownloadComplete("Something Went Wrong", 0);
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
            sendBroadcast(new Intent().setAction("UPLOAD_IMAGE_STATUS").putExtra("status", value));
            mNotificationManager.cancelAll();
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
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            if (intent != null) {
                UploadAsync uploadAsync = new UploadAsync(intent.getIntExtra("notification", 0), new File(intent.getStringExtra("imagefile")));
                uploadAsync.execute();
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

        public UploadAsync(Integer notificationid, File ImageFile) {
            this.notificationid = notificationid;
            imagfile = ImageFile;
        }

        @Override
        protected Void doInBackground(String... paths) {
            uploadImageToServer(imagfile, notificationid);
            return null;
        }
    }

}


