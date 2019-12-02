package online.motohub.newdesign.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import android.content.Context
import online.motohub.interfaces.PermissionViewModelCallback
import online.motohub.interfaces.ViewModelCallback
import online.motohub.newdesign.bl.MotoHubApp
import java.lang.ref.WeakReference

open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    private var wrCallback: WeakReference<ViewModelCallback>? = null
    private var wrNavCallback: WeakReference<PermissionViewModelCallback>? = null

    var isFirstTime = true
    var isFirstTimeWithInternet = true

    val context: Context
        get() = getApplication()

    var callback: ViewModelCallback?
        get() = if (wrCallback != null) wrCallback!!.get() else null
        set(callback) = if (callback != null) {
            this.wrCallback = WeakReference(callback)
        } else {
            this.wrCallback = null
        }

    var navCallback: PermissionViewModelCallback?
        get() = if (wrNavCallback != null) wrNavCallback!!.get() else null
        set(callback) = if (callback != null) {
            this.wrNavCallback = WeakReference(callback)
        } else {
            this.wrNavCallback = null
        }

    fun initialize(callback: ViewModelCallback, navCallback: PermissionViewModelCallback) {
        this.callback = callback
        this.navCallback = navCallback
        initialize()
    }

    // Call every time on ViewCreated/onCreate. So for a given session it can be called many time.
    fun initialize() {
        initialize(isFirstTime)
        isFirstTime = false
        MotoHubApp.getInstance().internetUtil.observeForever(networkChangeObserver)
    }

    // Called every time fragment is created/ Activity been created even due to configuration change.
    open fun initialize(firstTime: Boolean) {

    }

    // Called first time and only one time when view model is created. Only API Call will be part of it
    open fun initializeWithNetworkAvailable() {

    }

    private val networkChangeObserver: Observer<Boolean> = Observer {
        if (it != null) {
            onNetworkStatusChange(it)
        }
    }

    private fun onNetworkStatusChange(it: Boolean) {
        if (it) {
            MotoHubApp.getInstance().internetUtil.removeObserver(networkChangeObserver)
            if (isFirstTimeWithInternet) {
                isFirstTimeWithInternet = false
                initializeWithNetworkAvailable()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}