package online.motohub.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daasuu.gpuv.composer.FillMode;
import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.SessionType;
import com.otaliastudios.cameraview.Size;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.interfaces.PermissionCallback;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsResModel;
import online.motohub.util.CustomWatermarkFilter;
import online.motohub.util.DialogManager;
import online.motohub.util.story.ControlView;

public class CameraStoryActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = CameraStoryActivity.class.getSimpleName();
    @BindView(R.id.camera)
    CameraView camera;
    @BindView(R.id.capturePhoto)
    ImageButton mCapturePic;
    @BindView(R.id.stopVideo)
    ImageButton mStopVideo;
    @BindView(R.id.captureVideo)
    ImageButton mRecordVideo;
    @BindView(R.id.toggleCamera)
    ImageButton mSwitchCamera;
    @BindView(R.id.controls)
    ViewGroup controlPanel;
    @BindView(R.id.toolbar_back_img_btn)
    ImageButton mBackBtn;
    @BindView(R.id.tv_timer)
    TextView mTvTime;
    int i = 0;
    private boolean mCapturingPicture;
    private boolean mCapturingVideo;
    // To show stuff in the callback
    private Size mCaptureNativeSize;
    private long mCaptureTime;
    private Handler mHandler;
    private Runnable mRunnable;
    private boolean isExit = false;
    //private String videoPath;
    PermissionCallback mPermissionCallBack = new PermissionCallback() {
        @Override
        public void permissionOkClick() {
            initView();
        }
    };
    private com.daasuu.gpuv.composer.GPUMp4Composer GPUMp4Composer;
    private Bitmap bitmap;
    private ProgressDialog pDialog;
    private EventsResModel mEventResModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_camera);
        ButterKnife.bind(this);
        pDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        pDialog.setCancelable(false);
        Bundle mBunlde = getIntent().getExtras();
        assert mBunlde != null;
        mEventResModel = (EventsResModel) mBunlde.getSerializable(EventsModel.EVENTS_RES_MODEL);
        if (isPermissionAdded())
            initView();
    }

    private void initView() {
        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE);
        mBackBtn.setVisibility(View.VISIBLE);
        camera.addCameraListener(new CameraListener() {
            public void onCameraOpened(CameraOptions options) {
                onOpened();
            }

            public void onPictureTaken(byte[] jpeg) {
                onPicture(jpeg);
            }

            @Override
            public void onVideoTaken(File video) {
                super.onVideoTaken(video);
                stopTimer();
                if (!isExit)
                    onVideo(video);
            }
        });
        camera.setSessionType(SessionType.VIDEO);
        camera.start();
    }

    private void stopTimer() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    private void message(String content, boolean important) {
        int length = important ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast.makeText(this, content, length).show();
    }

    private void onOpened() {
        ViewGroup group = (ViewGroup) controlPanel.getChildAt(0);
        for (int i = 0; i < group.getChildCount(); i++) {
            ControlView view = (ControlView) group.getChildAt(i);
            view.onCameraOpened(camera);
        }
    }

    public boolean isPermissionAdded() {
        boolean addPermission = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int permissionCamera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
            int readStoragePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int storagePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            List<String> listPermissionsNeeded = new ArrayList<>();
            if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
            }
            if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                addPermission = isPermission(mPermissionCallBack, listPermissionsNeeded);
            }
        }
        return addPermission;
    }

    private void onPicture(byte[] jpeg) {
        mCapturingPicture = false;
        long callbackTime = System.currentTimeMillis();
        // This can happen if picture was taken with a gesture.
        if (mCaptureTime == 0) mCaptureTime = callbackTime - 300;
        if (mCaptureNativeSize == null) mCaptureNativeSize = camera.getPictureSize();
        convertByteArrayToFile(jpeg);
        mCaptureTime = 0;
        mCaptureNativeSize = null;
    }

    private void convertByteArrayToFile(byte[] image) {
        try {
            File mStoryFile = createNewFile();
            FileOutputStream mOutputStream = new FileOutputStream(mStoryFile);
            mOutputStream.write(image);
            moveToPreviewActivity(Uri.fromFile(mStoryFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveToPreviewActivity(Uri mStoryFilePath) {
        Intent mImagePreviewIntent = new Intent(CameraStoryActivity.this, PicturePreviewActivity.class);
        mImagePreviewIntent.putExtra("file_uri", mStoryFilePath);
        Bundle mBunlde = getIntent().getExtras();
        if (mBunlde != null)
            mImagePreviewIntent.putExtra("bundle_data", mBunlde);
        startActivity(mImagePreviewIntent);
    }

    private File createNewFile() {
        boolean createDir = true, createFile = true;
        File dir = new File(Environment.getExternalStorageDirectory().getPath(), "/SpecLive");
        if (!dir.exists())
            createDir = dir.mkdir();
        File newFile = new File(dir, "spec_live_videos_" + System.currentTimeMillis() + ".mp4");
        if (!newFile.exists() && createDir)
            try {
                createFile = newFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return newFile;
    }


    private void onVideo(File video) {
        mCapturingVideo = false;
        Intent intent = new Intent(CameraStoryActivity.this, VideoStoryPreviewActivity.class);
        intent.putExtra("file_uri", Uri.fromFile(video));
        Bundle mBunlde = getIntent().getExtras();
        if (mBunlde != null)
            intent.putExtra("bundle_data", mBunlde);
        startActivity(intent);
        finish();

        /*EventsResModel.EventadByEventID eventadByEventID = mEventResModel.getEventadByEventID().get(0);

        GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + eventadByEventID.getEventAd(), new LazyHeaders.Builder()
                .addHeader("X-DreamFactory-Api-Key", getString(R.string.dream_factory_api_key))
                .build());


        Glide.with(getApplicationContext())
                .asBitmap().load(glideUrl)
                .apply(new RequestOptions().override(100, 100))
                .listener(new RequestListener<Bitmap>() {
                              @Override
                              public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                  Bitmap bMapScaled = BitmapFactory.decodeResource(getResources(), R.drawable.motohub_logo);
                                  bitmap = Bitmap.createScaledBitmap(bMapScaled, 100, 100, true);
                                  startCodec(video);
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Bitmap bitmap1, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                  //zoomImage.setImage(ImageSource.bitmap(bitmap));
                                  //bitmap = Bitmap.createScaledBitmap(bitmap1, 120, 120, true);
                                  bitmap = bitmap1;
                                  startCodec(video);
                                  return false;
                              }
                          }
                ).submit();*/


        //Bitmap bMapScaled = BitmapFactory.decodeResource(getResources(), R.drawable.motohub_logo);

    }

    @OnClick({R.id.captureVideo, R.id.capturePhoto, R.id.toggleCamera, R.id.stopVideo, R.id.toolbar_back_img_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.capturePhoto:
                capturePhoto();
                break;
            case R.id.captureVideo:
                captureVideo();
                setBtnsVisibility(true);
                break;
            case R.id.toggleCamera:
                toggleCamera();
                break;
            case R.id.stopVideo:
                camera.stopCapturingVideo();
                setBtnsVisibility(false);
                break;
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                break;
        }
    }

    private void setBtnsVisibility(boolean visibility) {
        if (visibility) {
            mStopVideo.setVisibility(View.VISIBLE);
            mRecordVideo.setVisibility(View.INVISIBLE);
            //mCapturePic.setVisibility(View.INVISIBLE);
            mTvTime.setVisibility(View.VISIBLE);
            mSwitchCamera.setVisibility(View.INVISIBLE);
        } else {
            mStopVideo.setVisibility(View.INVISIBLE);
            mRecordVideo.setVisibility(View.VISIBLE);
            // mCapturePic.setVisibility(View.VISIBLE);
            mTvTime.setVisibility(View.INVISIBLE);
            mSwitchCamera.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        isExit = true;
        finish();
    }

    private void capturePhoto() {
        if (mCapturingPicture) return;
        mCapturingPicture = true;
        mCaptureTime = System.currentTimeMillis();
        mCaptureNativeSize = camera.getPictureSize();
        message("Capturing picture...", false);
        camera.capturePicture();
    }


    private void captureVideo() {
        try {
            if (camera.getSessionType() != SessionType.VIDEO) {
                message("Can't record video while session type is 'picture'.", false);
                return;
            }
            if (mCapturingPicture || mCapturingVideo) return;
            mCapturingVideo = true;
            message("Recording for 30 seconds...", true);
            camera.startCapturingVideo(createNewFile(), 30 * 1000);
            startTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startTimer() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                i = i + 1;
                mTvTime.setText(String.format("%02d:%02d", i / 60, i % 60));
                mHandler.postDelayed(mRunnable, 1000);
            }
        };
        mHandler.postDelayed(mRunnable, 1000);
    }

    private void toggleCamera() {
        if (mCapturingPicture) return;
        switch (camera.toggleFacing()) {
            case BACK:
                message("Switched to back camera!", false);
                break;
            case FRONT:
                message("Switched to front camera!", false);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogManager.hideProgress();
        camera.destroy();
        stopTimer();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void startCodec(final File videoFile) {
        pDialog.setTitle("Please wait...");
        showDialog();
        final String videoPath = getVideoFilePath();
        GPUMp4Composer = null;
        GPUMp4Composer = new GPUMp4Composer(videoFile.toString(), videoPath)
                .fillMode(FillMode.PRESERVE_ASPECT_CROP)
                .filter(new CustomWatermarkFilter(bitmap, CustomWatermarkFilter.Position.RIGHT_BOTTOM))
                .filter(new CustomWatermarkFilter(bitmap, CustomWatermarkFilter.Position.RIGHT_BOTTOM))
                .listener(new GPUMp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {
                        runOnUiThread(() -> {
                            double percen = progress * 100;
                            pDialog.setTitle("Please wait... " + (int) percen + "%");
                        });
                    }

                    @Override
                    public void onCompleted() {
                        hideDialog();
                        //exportMp4ToGallery(getApplicationContext(), videoPath);
                        runOnUiThread(() -> {
                            //Toast.makeText(CameraStoryActivity.this, "codec complete path =" + videoPath, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CameraStoryActivity.this, VideoStoryPreviewActivity.class);
                            intent.putExtra("file_uri", Uri.parse(videoPath));
                            Bundle mBunlde = getIntent().getExtras();
                            if (mBunlde != null)
                                intent.putExtra("bundle_data", mBunlde);
                            startActivity(intent);
                            finish();
                        });
                    }

                    @Override
                    public void onCanceled() {
                        runOnUiThread(() -> {
                            hideDialog();
                        });
                    }

                    @Override
                    public void onFailed(Exception exception) {
                        runOnUiThread(() -> {
                            hideDialog();
                        });
                    }
                })
                .start();
    }

    public File getAndroidMoviesFolder() {
        //return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        File dir = new File(Environment.getExternalStorageDirectory().getPath(), "/SpecLive");
        return dir;
    }

    @SuppressLint("SimpleDateFormat")
    public String getVideoFilePath() {
        return getAndroidMoviesFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "filter_apply.mp4";
    }
}
