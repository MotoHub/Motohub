/**
 * This is sample code provided by Wowza Media Systems, LLC.  All sample code is intended to be a reference for the
 * purpose of educating developers, and is not intended to be used in any production environment.
 * <p>
 * IN NO EVENT SHALL WOWZA MEDIA SYSTEMS, LLC BE LIABLE TO YOU OR ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL,
 * OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
 * EVEN IF WOWZA MEDIA SYSTEMS, LLC HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p>
 * WOWZA MEDIA SYSTEMS, LLC SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. ALL CODE PROVIDED HEREUNDER IS PROVIDED "AS IS".
 * WOWZA MEDIA SYSTEMS, LLC HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * <p>
 * Copyright © 2015 Wowza Media Systems, LLC. All rights reserved.
 */

package online.motohub.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.devices.WZAudioDevice;
import com.wowza.gocoder.sdk.api.devices.WZCamera;
import com.wowza.gocoder.sdk.api.devices.WZCameraView;
import com.wowza.gocoder.sdk.api.errors.WZError;
import com.wowza.gocoder.sdk.api.errors.WZStreamingError;
import com.wowza.gocoder.sdk.api.geometry.WZSize;
import com.wowza.gocoder.sdk.api.graphics.WZColor;
import com.wowza.gocoder.sdk.api.status.WZStatus;

import java.util.Arrays;

import online.motohub.R;
import online.motohub.application.MotoHub;
import online.motohub.config.GoCoderSDKPrefs;
import online.motohub.dialog.DialogManager;
import online.motohub.util.MultiStateButton;
import online.motohub.util.StatusView;

abstract public class CameraActivityBase extends GoCoderSDKActivityBase
        implements WZCameraView.PreviewStatusListener {

    private final static String BASE_TAG = CameraActivityBase.class.getSimpleName();

    private final static String[] CAMERA_CONFIG_PREFS_SORTED = new String[]{"wz_video_enabled", "wz_video_frame_size", "wz_video_preset"};

    // UI controls
    protected MultiStateButton mBtnBroadcast = null;
    protected MultiStateButton mBtnSettings = null;
    protected StatusView mStatusView = null;

    // The GoCoder SDK camera preview display view
    protected WZCameraView mWZCameraView = null;
    protected WZAudioDevice mWZAudioDevice = null;

    private boolean mDevicesInitialized = false;
    private boolean mUIInitialized = false;

    private SharedPreferences.OnSharedPreferenceChangeListener mPrefsChangeListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    /**
     * Android Activity lifecycle methods
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (!mUIInitialized) {
            initUIControls();
        }
        if (!mDevicesInitialized) {
            initGoCoderDevices();
        }

        this.hasDevicePermissionToAccess(new PermissionCallbackInterface() {

            @Override
            public void onPermissionResult(boolean result) {
                if (!mDevicesInitialized || result) {
                    initGoCoderDevices();
                }
            }
        });

        if (sGoCoderSDK != null && this.hasDevicePermissionToAccess(Manifest.permission.CAMERA)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

            // Update the camera preview display config based on the stored shared preferences
            mWZCameraView.setCameraConfig(getBroadcastConfig());
            mWZCameraView.setScaleMode(GoCoderSDKPrefs.getScaleMode(sharedPrefs));
            mWZCameraView.setVideoBackgroundColor(WZColor.DARKGREY);

            // Setup up a shared preferences change listener to update the camera preview
            // as the related preference values change
            mPrefsChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String prefsKey) {
                    if (mWZCameraView != null && Arrays.binarySearch(CAMERA_CONFIG_PREFS_SORTED, prefsKey) != -1) {
                        // Update the camera preview display frame size
                        WZSize currentFrameSize = mWZCameraView.getFrameSize();
                        int prefsFrameWidth = sharedPreferences.getInt("wz_video_frame_width", currentFrameSize.getWidth());
                        int prefsFrameHeight = sharedPreferences.getInt("wz_video_frame_height", currentFrameSize.getHeight());
                        WZSize prefsFrameSize = new WZSize(prefsFrameWidth, prefsFrameHeight);
                        if (!prefsFrameSize.equals(currentFrameSize))
                            mWZCameraView.setFrameSize(prefsFrameSize);

                        // Toggle the camera preview on or off
                        boolean videoEnabled = sharedPreferences.getBoolean("wz_video_enabled", mWZBroadcastConfig.isVideoEnabled());
                        if (videoEnabled && !mWZCameraView.isPreviewing())
                            mWZCameraView.startPreview();
                        else if (!videoEnabled && mWZCameraView.isPreviewing()) {
                            mWZCameraView.stopPreview();
                        }
                    }
                }
            };

            sharedPrefs.registerOnSharedPreferenceChangeListener(mPrefsChangeListener);

            if (mWZBroadcastConfig.isVideoEnabled()) {
                // Start the camera preview display
                mWZCameraView.startPreview(getBroadcastConfig(), this);
            } else {
                Toast.makeText(this, "The video stream is currently turned off", Toast.LENGTH_LONG).show();
            }
        }

        syncUIControlState();
    }

    @Override
    public void onWZCameraPreviewStarted(WZCamera wzCamera, WZSize wzSize, int i) {
        // Briefly display the video configuration
//        Toast.makeText(this, getBroadcastConfig().getLabel(true, true, false, true), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onWZCameraPreviewStopped(int cameraId) {
    }

    @Override
    public void onWZCameraPreviewError(WZCamera wzCamera, WZError wzError) {
        displayErrorDialog(wzError);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mPrefsChangeListener != null) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPrefs.unregisterOnSharedPreferenceChangeListener(mPrefsChangeListener);
        }

        if (mWZCameraView != null && mWZCameraView.isPreviewing())
            mWZCameraView.stopPreview();
    }

    /**
     * WZStatusCallback interface methods
     */
    @Override
    public void onWZStatus(final WZStatus goCoderStatus) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (goCoderStatus.isRunning()) {
                    // Keep the screen on while we are broadcasting
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    // Since we have successfully opened up the server connection, store the connection info for auto complete
                    GoCoderSDKPrefs.storeHostConfig(PreferenceManager.getDefaultSharedPreferences(CameraActivityBase.this), mWZBroadcastConfig);
                } else if (goCoderStatus.isIdle()) {
                    // Clear the "keep screen on" flag
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
                if (mStatusView != null) mStatusView.setStatus(goCoderStatus);
                syncUIControlState();
            }
        });
    }

    @Override
    public void onWZError(final WZStatus goCoderStatus) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mStatusView != null) mStatusView.setStatus(goCoderStatus);
                syncUIControlState();
            }
        });
    }

    /**
     * Click handler for the broadcast button
     */
    public void onToggleBroadcast(View v) {
//        if (getBroadcast() == null) return;

        String sName = MotoHub.getApplicationInstance().getLiveStreamName().trim();


        mWZBroadcastConfig.setHostAddress(getString(R.string.wz_live_host_address_default_value));
        mWZBroadcastConfig.setApplicationName(getString(R.string.wz_live_app_name_default_value));
        mWZBroadcastConfig.setStreamName(sName);
        mWZBroadcastConfig.setPortNumber(Integer.parseInt(getString(R.string.wz_live_port_number_default_value)));
        mWZBroadcastConfig.setUsername(getString(R.string.wz_live_username_default_value));
        mWZBroadcastConfig.setPassword(getString(R.string.wz_live_password_default_value));


        if (getBroadcast().getStatus().isIdle()) {
            if (!mWZBroadcastConfig.isAudioEnabled()) {
                Toast.makeText(this, "The audio stream is currently turned off", Toast.LENGTH_LONG).show();
            }
            if (mWZBroadcastConfig.isVideoEnabled()) {

                WZStreamingError configError = startBroadcast();
                if (configError != null) {
                    if (mStatusView != null)
                        mStatusView.setErrorMessage(configError.getErrorDescription());
                }
            } else {
                Toast.makeText(this, "The video stream is currently turned off", Toast.LENGTH_LONG).show();
            }
        } else {

            endBroadcast();
        }
    }

    private void showSettings(View v) {
        // Display the prefs fragment
        GoCoderSDKPrefs.PrefsFragment prefsFragment = new GoCoderSDKPrefs.PrefsFragment();
        prefsFragment.setActiveCamera(mWZCameraView != null ? mWZCameraView.getCamera() : null);

//        getFragmentManager().addOnBackStackChangedListener(backStackListener);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, prefsFragment)
                .addToBackStack(null)
                .commit();
    }

//    private FragmentManager.OnBackStackChangedListener backStackListener =  new FragmentManager.OnBackStackChangedListener() {
//
//        @Override
//        public void onBackStackChanged() {
//            syncUIControlState();
//        }
//    };

    /**
     * Click handler for the settings button
     */
    public void onSettings(View v) {

        if (this.hasDevicePermissionToAccess(Manifest.permission.CAMERA) && this.hasDevicePermissionToAccess(Manifest.permission.RECORD_AUDIO)) {
            this.showSettings(v);
        } else {
            Toast.makeText(this, "You must enable audio / video access to update the settings", Toast.LENGTH_LONG).show();
        }
    }

    protected void initGoCoderDevices() {
        if (sGoCoderSDK != null) {

            boolean videoIsInitialized = false;
            boolean audioIsInitialized = false;

            // Initialize the camera preview
            if (this.hasDevicePermissionToAccess(Manifest.permission.CAMERA)) {
                if (mWZCameraView != null) {
                    WZCamera[] availableCameras = mWZCameraView.getCameras();
                    // Ensure we can access to at least one camera
                    if (availableCameras.length > 0) {
                        // Set the video broadcaster in the broadcast config
                        getBroadcastConfig().setVideoBroadcaster(mWZCameraView);
                        videoIsInitialized = true;
                    } else {
                        mStatusView.setErrorMessage("Could not detect or gain access to any cameras on this device");
                        getBroadcastConfig().setVideoEnabled(false);
                    }
                } else {
                    getBroadcastConfig().setVideoEnabled(false);
                }
            }

            if (this.hasDevicePermissionToAccess(Manifest.permission.RECORD_AUDIO)) {
                // Initialize the audio input device interface
                mWZAudioDevice = new WZAudioDevice();

                // Set the audio broadcaster in the broadcast config
                getBroadcastConfig().setAudioBroadcaster(mWZAudioDevice);
                audioIsInitialized = true;
            }

            if (videoIsInitialized && audioIsInitialized)
                mDevicesInitialized = true;
        }
    }

    protected void initUIControls() {
        // Initialize the UI controls
        mBtnBroadcast = findViewById(R.id.ic_broadcast);
        mBtnSettings = findViewById(R.id.ic_settings);
        mStatusView = findViewById(R.id.statusView);

        // The GoCoder SDK camera view
        mWZCameraView = findViewById(R.id.cameraPreview);
        mUIInitialized = true;

        if (sGoCoderSDK == null && mStatusView != null)
            mStatusView.setErrorMessage(WowzaGoCoder.getLastError().getErrorDescription());
    }

    protected boolean syncUIControlState() {
        boolean disableControls = (getBroadcast() == null ||
                !(getBroadcast().getStatus().isIdle() ||
                        getBroadcast().getStatus().isRunning()));
        boolean isStreaming = (getBroadcast() != null && getBroadcast().getStatus().isRunning());

        if (disableControls) {
            if (mBtnBroadcast != null) mBtnBroadcast.setEnabled(false);
            if (mBtnSettings != null) mBtnSettings.setEnabled(false);
        } else {
            if (mBtnBroadcast != null) {
                mBtnBroadcast.setState(isStreaming);
                mBtnBroadcast.setEnabled(true);
            }
            if (mBtnSettings != null)
                mBtnSettings.setEnabled(!isStreaming);
        }

        return disableControls;
    }

    //define callback interface
    interface PermissionCallbackInterface {
        void onPermissionResult(boolean result);
    }
}
