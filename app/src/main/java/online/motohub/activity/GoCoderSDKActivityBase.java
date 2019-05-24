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

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcastConfig;
import com.wowza.gocoder.sdk.api.configuration.WZMediaConfig;
import com.wowza.gocoder.sdk.api.data.WZDataMap;
import com.wowza.gocoder.sdk.api.errors.WZError;
import com.wowza.gocoder.sdk.api.errors.WZStreamingError;
import com.wowza.gocoder.sdk.api.logging.WZLog;
import com.wowza.gocoder.sdk.api.status.WZStatus;
import com.wowza.gocoder.sdk.api.status.WZStatusCallback;

import online.motohub.R;
import online.motohub.config.GoCoderSDKPrefs;


public abstract class GoCoderSDKActivityBase extends BaseActivity
        implements WZStatusCallback {

    private final static String TAG = GoCoderSDKActivityBase.class.getSimpleName();

    private static final String SDK_SAMPLE_APP_LICENSE_KEY = "GOSK-8044-0103-5C69-FD49-FE24";
    private static final int PERMISSIONS_REQUEST_CODE = 0x1;
    // indicates whether this is a full screen activity or note
    protected static boolean sFullScreenActivity = true;
    // GoCoder SDK top level interface
    protected static WowzaGoCoder sGoCoderSDK = null;
    private static Object sBroadcastLock = new Object();
    private static boolean sBroadcastEnded = true;
    protected String[] mRequiredPermissions = {};
    protected boolean mPermissionsGranted = false;
    protected WZBroadcast mWZBroadcast = null;
    protected int mWZNetworkLogLevel = WZLog.LOG_LEVEL_DEBUG;
    protected GoCoderSDKPrefs mGoCoderSDKPrefs;
    protected WZBroadcastConfig mWZBroadcastConfig = null;
    private boolean hasRequestedPermissions = false;
    private CameraActivityBase.PermissionCallbackInterface callbackFunction = null;

    public WZBroadcast getBroadcast() {
        return mWZBroadcast;
    }

    public WZBroadcastConfig getBroadcastConfig() {
        return mWZBroadcastConfig;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (sGoCoderSDK == null) {
            // Enable detailed logging from the GoCoder SDK
            WZLog.LOGGING_ENABLED = true;

            // Initialize the GoCoder SDK
            sGoCoderSDK = WowzaGoCoder.init(this, SDK_SAMPLE_APP_LICENSE_KEY);

            if (sGoCoderSDK == null) {
                WZLog.error(TAG, WowzaGoCoder.getLastError());
            }
        }

        if (sGoCoderSDK != null) {
            // Create a new instance of the preferences mgr
            mGoCoderSDKPrefs = new GoCoderSDKPrefs();

            // Create an instance for the broadcast configuration
            mWZBroadcastConfig = new WZBroadcastConfig(WZMediaConfig.FRAME_SIZE_1280x720);

            // Create a broadcaster instance
            mWZBroadcast = new WZBroadcast();
            mWZBroadcast.setLogLevel(WZLog.LOG_LEVEL_DEBUG);
        }
    }

    protected void hasDevicePermissionToAccess(CameraActivityBase.PermissionCallbackInterface callback) {
        this.callbackFunction = callback;
        if (mWZBroadcast != null) {
            boolean result = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                result = (mRequiredPermissions.length <= 0 || WowzaGoCoder.hasPermissions(this, mRequiredPermissions));
                if (!result && !hasRequestedPermissions) {
                    ActivityCompat.requestPermissions(this, mRequiredPermissions, PERMISSIONS_REQUEST_CODE);
                    hasRequestedPermissions = true;
                } else {
                    this.callbackFunction.onPermissionResult(result);
                }
            } else {
                this.callbackFunction.onPermissionResult(result);
            }
        }
    }

    protected boolean hasDevicePermissionToAccess(String source) {

        String[] permissionRequestArr = new String[]{
                source
        };
        boolean result = false;
        if (mWZBroadcast != null) {
            result = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                result = (mRequiredPermissions.length <= 0 || WowzaGoCoder.hasPermissions(this, permissionRequestArr));
            }
        }
        return result;
    }

    protected boolean hasDevicePermissionToAccess() {
        boolean result = false;
        if (mWZBroadcast != null) {
            result = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                result = (mRequiredPermissions.length <= 0 || WowzaGoCoder.hasPermissions(this, mRequiredPermissions));
                if (!result && !hasRequestedPermissions) {
                    WZLog.debug("ACCESS[hasDevicePermissionToAccess()-3]::requestPermissions");
                    ActivityCompat.requestPermissions(this, mRequiredPermissions, PERMISSIONS_REQUEST_CODE);
                    hasRequestedPermissions = true;
                }
            }
        }
        return result;
    }

    /**
     * Android Activity lifecycle methods
     */
    @Override
    protected void onResume() {
        super.onResume();

        mPermissionsGranted = this.hasDevicePermissionToAccess();
        if (mPermissionsGranted) {
            syncPreferences();
        }
    }

    @Override
    protected void onPause() {
        // Stop any active live stream
        if (mWZBroadcast != null && mWZBroadcast.getStatus().isRunning()) {
            mWZBroadcast.endBroadcast(this);
        }

        super.onPause();
    }

    /*   */

    /**
     * Click handler for the in button
     *//*
    public void onAbout(View v) {
        // Display the About fragment
        AboutFragment aboutFragment = AboutFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, aboutFragment)
                .addToBackStack(null)
                .commit();
    }
*/
    // Return correctly from any fragments launched and placed on the back stack
    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        mPermissionsGranted = true;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mPermissionsGranted = false;
                    }
                }
            }
        }
        if (this.callbackFunction != null)
            this.callbackFunction.onPermissionResult(mPermissionsGranted);
    }

    /**
     * Enable Android's sticky immersive full-screen mode
     * See http://developer.android.com/training/system-ui/immersive.html#sticky
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (sFullScreenActivity && hasFocus)
            hideSystemUI();
    }

    public void hideSystemUI() {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null)
            rootView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void showSystemUI() {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null)
            rootView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    }

    /**
     * WZStatusCallback interface methods
     */
    @Override
    public void onWZStatus(final WZStatus goCoderStatus) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (goCoderStatus.isReady()) {
                    // Keep the screen on while the broadcast is active
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    // Since we have successfully opened up the server connection, store the connection info for auto complete
                    GoCoderSDKPrefs.storeHostConfig(PreferenceManager.getDefaultSharedPreferences(GoCoderSDKActivityBase.this), mWZBroadcastConfig);
                } else if (goCoderStatus.isIdle()) {
                    // Clear the "keep screen on" flag
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }

            }
        });
    }

    @Override
    public void onWZError(final WZStatus goCoderStatus) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                WZLog.error(TAG, goCoderStatus.getLastError());
                Toast.makeText(GoCoderSDKActivityBase.this, "Oops! Something went wrong!!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    protected synchronized WZStreamingError startBroadcast() {
        WZStreamingError configValidationError = null;

        if (mWZBroadcast.getStatus().isIdle()) {

            // Set the detail level for network logging output
            mWZBroadcast.setLogLevel(mWZNetworkLogLevel);

            //
            // An example of adding metadata values to the stream for use with the onMetadata()
            // method of the IMediaStreamActionNotify2 interface of the Wowza Streaming Engine Java
            // API for server modules.
            //
            // See http://www.wowza.com/resources/serverapi/com/wowza/wms/stream/IMediaStreamActionNotify2.html
            // for additional usage information on IMediaStreamActionNotify2.
            //

            // Add stream metadata describing the current device and platform
            WZDataMap streamMetadata = new WZDataMap();
            streamMetadata.put("androidRelease", Build.VERSION.RELEASE);
            streamMetadata.put("androidSDK", Build.VERSION.SDK_INT);
            streamMetadata.put("deviceProductName", Build.PRODUCT);
            streamMetadata.put("deviceManufacturer", Build.MANUFACTURER);
            streamMetadata.put("deviceModel", Build.MODEL);

            mWZBroadcastConfig.setStreamMetadata(streamMetadata);

            //
            // An example of adding query strings for use with the getQueryStr() method of
            // the IClient interface of the Wowza Streaming Engine Java API for server modules.
            //
            // See http://www.wowza.com/resources/serverapi/com/wowza/wms/client/IClient.html#getQueryStr()
            // for additional usage information on getQueryStr().
            //
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

                // Add query string parameters describing the current app
                WZDataMap connectionParameters = new WZDataMap();
                connectionParameters.put("appPackageName", pInfo.packageName);
                connectionParameters.put("appVersionName", pInfo.versionName);
                connectionParameters.put("appVersionCode", pInfo.versionCode);

                mWZBroadcastConfig.setConnectionParameters(connectionParameters);

            } catch (PackageManager.NameNotFoundException e) {
                WZLog.error(TAG, e);
            }

            WZLog.info(TAG, "=============== Broadcast Configuration ===============\n"
                    + mWZBroadcastConfig.toString()
                    + "\n=======================================================");

            configValidationError = mWZBroadcastConfig.validateForBroadcast();
            if (configValidationError == null) {
                mWZBroadcast.startBroadcast(mWZBroadcastConfig, this);
            }
        } else {
            WZLog.error(TAG, "startBroadcast() called while another broadcast is active");
        }
        return configValidationError;
    }

    protected synchronized void endBroadcast(boolean appPausing) {
        if (!mWZBroadcast.getStatus().isIdle()) {
            if (appPausing) {
                // Stop any active live stream
                sBroadcastEnded = false;
                mWZBroadcast.endBroadcast(new WZStatusCallback() {
                    @Override
                    public void onWZStatus(WZStatus wzStatus) {
                        synchronized (sBroadcastLock) {
                            sBroadcastEnded = true;
                            sBroadcastLock.notifyAll();
                        }
                    }

                    @Override
                    public void onWZError(WZStatus wzStatus) {
                        WZLog.error(TAG, wzStatus.getLastError());
                        synchronized (sBroadcastLock) {
                            sBroadcastEnded = true;
                            sBroadcastLock.notifyAll();
                        }
                    }
                });

                while (!sBroadcastEnded) {
                    try {
                        sBroadcastLock.wait();
                    } catch (InterruptedException e) {
                    }
                }
            } else {
                mWZBroadcast.endBroadcast(this);
            }
        } else {
            WZLog.error(TAG, "endBroadcast() called without an active broadcast");
        }
    }

    protected synchronized void endBroadcast() {

        endBroadcast(false);
    }

    public void syncPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mWZNetworkLogLevel = Integer.valueOf(prefs.getString("wz_debug_net_log_level", String.valueOf(WZLog.LOG_LEVEL_DEBUG)));

        int bandwidthMonitorLogLevel = Integer.valueOf(prefs.getString("wz_debug_bandwidth_monitor_log_level", String.valueOf(0)));
        WZBroadcast.LOG_STAT_SUMMARY = (bandwidthMonitorLogLevel > 0);
        WZBroadcast.LOG_STAT_SAMPLES = (bandwidthMonitorLogLevel > 1);

        if (mWZBroadcastConfig != null)
            GoCoderSDKPrefs.updateConfigFromPrefs(prefs, mWZBroadcastConfig);
    }

    /**
     * Display an alert dialog containing an error message.
     *
     * @param errorMessage The error message text
     */
    protected void displayErrorDialog(String errorMessage) {
        // Log the error message
        WZLog.error(TAG, errorMessage);

        // Display an alert dialog containing the error message
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        //AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setMessage(errorMessage)
                .setTitle(R.string.dialog_title_error);
        builder.setPositiveButton(R.string.dialog_button_close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    /**
     * Display an alert dialog containing the error message for
     * an error returned from the GoCoder SDK.
     *
     * @param goCoderSDKError An error returned from the GoCoder SDK.
     */
    protected void displayErrorDialog(WZError goCoderSDKError) {
        displayErrorDialog(goCoderSDKError.getErrorDescription());
    }
}
