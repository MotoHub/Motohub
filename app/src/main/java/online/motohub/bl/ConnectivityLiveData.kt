package online.motohub.bl

import android.arch.lifecycle.LiveData
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import android.support.annotation.RequiresPermission
import android.support.annotation.VisibleForTesting

class ConnectivityLiveData @VisibleForTesting internal constructor(private val context: Context,
                                                                   private val connectivityManager: ConnectivityManager)
    : LiveData<Boolean>() {

    private var broadcastReceiver: BroadcastReceiver? = null

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    constructor(context: Context) : this(context, context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager)

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network?) {
            postValue(true)
        }

        override fun onLost(network: Network?) {
            postValue(false)
        }
    }

    override fun onActive() {
        super.onActive()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val builder = NetworkRequest.Builder()
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
        } else {
            registerBroadCastReceiver()
        }

        postValue(isInternetOn())
    }

    override fun onInactive() {
        super.onInactive()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } else {
            unRegisterBroadCastReceiver()
        }
    }

    fun isInternetOn(): Boolean {
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    private fun registerBroadCastReceiver() {
        if (broadcastReceiver == null) {
            val filter = IntentFilter()
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
            filter.addAction("android.net.wifi.WIFI_STATE_CHANGED")

            broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(_context: Context, intent: Intent) {
                    val extras = intent.extras
                    val info = extras?.getParcelable<NetworkInfo>("networkInfo")
                    value = info?.state == NetworkInfo.State.CONNECTED
                }
            }

            context.registerReceiver(broadcastReceiver, filter)
        }
    }

    private fun unRegisterBroadCastReceiver() {
        if (broadcastReceiver != null) {
            context.unregisterReceiver(broadcastReceiver)
            broadcastReceiver = null
        }
    }
}
