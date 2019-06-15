package online.motohub.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import online.motohub.constants.AppConstants;
import online.motohub.database.DatabaseHandler;
import online.motohub.enums.UploadStatus;
import online.motohub.model.SpectatorLiveEntity;
import online.motohub.services.SpectatorFileUploadService;

public class ConnectivityChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase("android.net.conn.CONNECTIVITY_CHANGE")) {
            Log.e("CONNECTIVITY_STATUS -->", "TRUE");
            boolean isLoggedIn = PreferenceUtils.getInstance(context).getBooleanData(PreferenceUtils.USER_KEEP_LOGGED_IN);
            if (isLoggedIn && isNetworkConnected(context)&& AppConstants.UPLOAD_STATUS!= UploadStatus.STARTED)
                Log.e("CONNECTIVITY -->", "TRUE");
            uploadOffline(context);
        }
    }

    private void uploadOffline(Context context) {
        DatabaseHandler handler = new DatabaseHandler(context);
        ArrayList<SpectatorLiveEntity> mList = handler.getSpectatorLiveVideos();
        if (mList.size() > 0) {
            for (int i = 0; i < mList.size(); i++) {
                Intent service_intent = new Intent(context, SpectatorFileUploadService.class);
                String data = new Gson().toJson(mList.get(i));
                service_intent.putExtra("data", data);
                context.startService(service_intent);
            }
        }
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert conMgr != null;
        if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            return true;
        } else
            return conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.DISCONNECTED
                    && conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.DISCONNECTED;
    }
}
