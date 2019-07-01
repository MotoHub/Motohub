package online.motohub.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import online.motohub.bl.MotoHubApp
import online.motohub.interfaces.PermissionViewModelCallback
import online.motohub.interfaces.ViewModelCallback
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationTargetException

class BaseViewModel(application: Application) : AndroidViewModel(application) {

    private var wr_callback: WeakReference<ViewModelCallback>? = null
    private var wr_navcallback: WeakReference<PermissionViewModelCallback>? = null

    var isFirstTime = true
    var isFirstTimeWithInternet = true


    val context: Context
        get() = getApplication()

    var callback: ViewModelCallback?
        get() = if (wr_callback != null) wr_callback!!.get() else null
        set(callback) = if (callback != null) {
            this.wr_callback = WeakReference(callback)
        } else {
            this.wr_callback = null
        }

    var navCallback: PermissionViewModelCallback?
        get() = if (wr_navcallback != null) wr_navcallback!!.get() else null
        set(callback) = if (callback != null) {
            this.wr_navcallback = WeakReference(callback)
        } else {
            this.wr_navcallback = null
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
    class BaseViewModelFactory : ViewModelProvider.AndroidViewModelFactory {

        private val application: Application
        private var bundle: Bundle? = null

        /**
         * Creates a `AndroidViewModelFactory`
         *
         * @param application an application to pass in [AndroidViewModel]
         */
        constructor(application: Application) : super(application) {
            this.application = application
        }

        constructor(application: Application, bundle: Bundle?) : super(application) {
            this.application = application
            this.bundle = bundle
        }

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (BaseViewModel::class.java.isAssignableFrom(modelClass)) {

                try {
                    return modelClass.getConstructor(Application::class.java, Bundle::class.java).newInstance(application, bundle)
                } catch (e: NoSuchMethodException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: IllegalAccessException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: InstantiationException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: InvocationTargetException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                }

            }
            return super.create(modelClass)
        }
    }
    override fun onCleared() {
        super.onCleared()
    }
}