package online.motohub.newdesign.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import java.lang.reflect.InvocationTargetException

class BaseViewModelFactory : ViewModelProvider.AndroidViewModelFactory {

    private val application: Application
    private var bundle: Bundle? = null

    /**
     * Creates a `AndroidViewModelFactory`
     *
     * @param application an application to pass in [AndroidViewModel]
     */
    public constructor(application: Application) : super(application) {
        this.application = application
    }

    public  constructor(application: Application, bundle: Bundle?) : super(application) {
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