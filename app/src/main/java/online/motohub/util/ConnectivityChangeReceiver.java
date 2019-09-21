package online.motohub.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import online.motohub.newdesign.constants.AppConstants;
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
            if (isLoggedIn && isNetworkConnected(context) && AppConstants.UPLOAD_STATUS != UploadStatus.STARTED)
                Log.e("CONNECTIVITY -->", "TRUE");
            uploadOffline(context);
        }
    }

    private void uploadOffline(Context context) {
        try {
            DatabaseHandler handler = new DatabaseHandler(context);
            ArrayList<SpectatorLiveEntity> mList = handler.getSpectatorLiveVideos();
            if (mList.size() > 0) {
                for (int i = 0; i < mList.size(); i++) {
                    if(AppConstants.UPLOAD_STATUS == UploadStatus.STARTED){
                        break;
                    }
                    Intent service_intent = new Intent(context, SpectatorFileUploadService.class);
                    String data = new Gson().toJson(mList.get(i));
                    service_intent.putExtra("data", data);
                    context.startService(service_intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                final NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null) {
                    return (ni.isConnected() && (ni.getType() == ConnectivityManager.TYPE_WIFI || ni.getType() == ConnectivityManager.TYPE_MOBILE));
                }
            } else {
                Network n = cm.getActiveNetwork();
                if (n != null) {
                    final NetworkCapabilities nc = cm.getNetworkCapabilities(n);
                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                }
            }
        }

        return false;
    }
}
