package online.motohub.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import online.motohub.activity.BaseActivity;
import online.motohub.database.DatabaseHandler;
import online.motohub.model.SpectatorLiveEntity;

public class ConnectivityChangeReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equalsIgnoreCase("android.net.conn.CONNECTIVITY_CHANGE")) {
            Log.e("CONNECTIVITY_STATUS -->","TRUE");
            if(((BaseActivity)context).isNetworkConnected(context))
                Log.e("CONNECTIVITY -->","TRUE");
            uploadOffline(context);
        }
    }

    private void uploadOffline(Context context) {
        DatabaseHandler handler = new DatabaseHandler(context);
        ArrayList<SpectatorLiveEntity> mList = handler.getSpectatorLiveVideos();
        if (mList.size() > 0) {
                for (int i = 0; i < mList.size(); i++) {
                    Intent service_intent = new Intent(context, UploadOfflineVideos.class);
                    String data = new Gson().toJson(mList.get(i));
                    service_intent.putExtra("data", data);
                    context.startService(service_intent);
                }
        }
    }
}
