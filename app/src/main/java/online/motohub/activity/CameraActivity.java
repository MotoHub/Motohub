package online.motohub.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GestureDetectorCompat;

import com.wowza.gocoder.sdk.api.devices.WZCamera;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.dialog.DialogManager;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.util.AutoFocusListener;
import online.motohub.util.MultiStateButton;
import online.motohub.util.TimerView;

public class CameraActivity extends CameraActivityBase {

    private final static String TAG = CameraActivity.class.getSimpleName();
    // UI controls
    protected MultiStateButton mBtnSwitchCamera = null;
    protected MultiStateButton mBtnTorch = null;
    protected TimerView mTimerView = null;
    // Gestures are used to toggle the focus modes
    protected GestureDetectorCompat mAutoFocusDetector = null;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindString(R.string.live_stream)
    String mToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        mRequiredPermissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        };

        // Initialize the UI controls
        mBtnTorch = findViewById(R.id.ic_torch);
        mBtnSwitchCamera = findViewById(R.id.ic_switch_camera);
        mTimerView = findViewById(R.id.txtTimer);

        setToolbar(mToolbar, mToolbarTitle);

        setToolbarLeftBtn(mToolbar);
    }


    @OnClick({R.id.toolbar_back_img_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogManager.hideProgress();
        if (AppConstants.LIVE_STREAM_CALL_BACK != null) {
            AppConstants.LIVE_STREAM_CALL_BACK.onSuccess();
        }
    }

    protected void setToolbar(Toolbar toolbar, String toolbarTitle) {
        if (toolbar != null) {
            toolbar.setTitle("");
            toolbar.setContentInsetsAbsolute(0, 0);
            TextView mToolbarTitle = toolbar.findViewById(R.id.toolbar_title);
            mToolbarTitle.setText(toolbarTitle);
            setSupportActionBar(toolbar);
        }
    }

    protected void setToolbarLeftBtn(Toolbar toolbar) {
        if (toolbar != null) {
            ImageButton mToolbarLeftBtn = toolbar.findViewById(R.id.toolbar_back_img_btn);
            mToolbarLeftBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Android Activity lifecycle methods
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (this.hasDevicePermissionToAccess() && sGoCoderSDK != null && mWZCameraView != null) {
            if (mAutoFocusDetector == null)
                mAutoFocusDetector = new GestureDetectorCompat(this, new AutoFocusListener(this, mWZCameraView));

            WZCamera activeCamera = mWZCameraView.getCamera();
            if (activeCamera != null && activeCamera.hasCapability(WZCamera.FOCUS_MODE_CONTINUOUS))
                activeCamera.setFocusMode(WZCamera.FOCUS_MODE_CONTINUOUS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Click handler for the switch camera button
     */
    public void onSwitchCamera(View v) {
        if (mWZCameraView == null) return;

        mBtnTorch.setState(false);
        mBtnTorch.setEnabled(false);

        WZCamera newCamera = mWZCameraView.switchCamera();
        if (newCamera != null) {
            if (newCamera.hasCapability(WZCamera.FOCUS_MODE_CONTINUOUS))
                newCamera.setFocusMode(WZCamera.FOCUS_MODE_CONTINUOUS);

            boolean hasTorch = newCamera.hasCapability(WZCamera.TORCH);
            if (hasTorch) {
                mBtnTorch.setState(newCamera.isTorchOn());
                mBtnTorch.setEnabled(true);
            }
        }
    }

    /**
     * Click handler for the torch/flashlight button
     */
    public void onToggleTorch(View v) {
        if (mWZCameraView == null) return;

        WZCamera activeCamera = mWZCameraView.getCamera();
        activeCamera.setTorchOn(mBtnTorch.toggleState());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAutoFocusDetector != null)
            mAutoFocusDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    /**
     * Update the state of the UI controls
     */
    @Override
    protected boolean syncUIControlState() {
        boolean disableControls = super.syncUIControlState();

        if (disableControls) {
            mBtnSwitchCamera.setEnabled(false);
            mBtnTorch.setEnabled(false);
        } else {
            boolean isDisplayingVideo = (this.hasDevicePermissionToAccess(Manifest.permission.CAMERA) && getBroadcastConfig().isVideoEnabled() && mWZCameraView.getCameras().length > 0);
            boolean isStreaming = getBroadcast().getStatus().isRunning();

            if (isDisplayingVideo) {
                WZCamera activeCamera = mWZCameraView.getCamera();

                boolean hasTorch = (activeCamera != null && activeCamera.hasCapability(WZCamera.TORCH));
                mBtnTorch.setEnabled(hasTorch);
                if (hasTorch) {
                    mBtnTorch.setState(activeCamera.isTorchOn());
                }

                mBtnSwitchCamera.setEnabled(mWZCameraView.getCameras().length > 0);
            } else {
                mBtnSwitchCamera.setEnabled(false);
                mBtnTorch.setEnabled(false);
            }

            if (isStreaming && !mTimerView.isRunning()) {
                mTimerView.startTimer();
            } else if (getBroadcast().getStatus().isIdle() && mTimerView.isRunning()) {
                mTimerView.stopTimer();
            } else if (!isStreaming) {
                mTimerView.setVisibility(View.GONE);
            }
        }

        return disableControls;
    }

}
