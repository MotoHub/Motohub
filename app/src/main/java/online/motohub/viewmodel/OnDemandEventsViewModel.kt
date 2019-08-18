package online.motohub.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.google.gson.Gson
import online.motohub.R
import online.motohub.constants.BundleConstants
import online.motohub.interfaces.ResponseCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.OndemandNewResponse
import online.motohub.model.ProfileResModel
import online.motohub.provider.OnDemandEventsProvider
import java.util.*

class OnDemandEventsViewModel(application: Application, bundle: Bundle?) : BaseViewModel(application) {

    val provider = OnDemandEventsProvider()
    val onDemandVideoLiveData = MutableLiveData<ArrayList<OndemandNewResponse>>()
    var profileObj: ProfileResModel? = null

    init {
        profileObj = Gson().fromJson(bundle!!.getString(BundleConstants.MY_PROFILE_OBJ), ProfileResModel::class.java)
    }

    override fun initialize(firstTime: Boolean) {
        super.initialize(firstTime)
        getOnDemandEvents()

    }

    fun getOnDemandEvents() {
        callback!!.showProgress()
        provider.getOnDemandEvents(getInputModel(), ResponseCallback {
            callback!!.hideProgress()
            if (it.isSuccess && it.data != null) {
                if (it.data.size == 0) {
                    callback!!.showMessage("No ondemand events are available")
                }
                onDemandVideoLiveData.value = it.data
            }
        })
    }

    private fun getInputModel(): ApiInputModel {
        val inputModel = ApiInputModel()
        inputModel.apiKey = context.getString(R.string.dream_factory_api_key)
        return inputModel
    }
}