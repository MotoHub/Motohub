package online.motohub.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.SessionType;
import com.otaliastudios.cameraview.Size;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.interfaces.PermissionCallback;
import online.motohub.util.story.ControlView;

public class CameraStoryActivity extends BaseActivity implements View.OnClickListener {

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
    PermissionCallback mPermissionCallBack = new PermissionCallback() {
        @Override
        public void permissionOkClick() {
            initView();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_camera);
        ButterKnife.bind(this);
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

    private boolean isPermissionAdded() {
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
        if (camera.getSessionType() != SessionType.VIDEO) {
            message("Can't record video while session type is 'picture'.", false);
            return;
        }
        if (mCapturingPicture || mCapturingVideo) return;
        mCapturingVideo = true;
        message("Recording for 30 seconds...", true);
        camera.startCapturingVideo(createNewFile(), 30 * 1000);
        startTimer();
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
        camera.destroy();
        stopTimer();
    }
}
