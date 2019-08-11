package online.motohub.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.google.gson.Gson
import online.motohub.constants.AppConstants
import online.motohub.constants.BundleConstants
import online.motohub.constants.RelationConstants
import online.motohub.interfaces.ResponseCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.EventsResModel
import online.motohub.model.ProfileResModel
import online.motohub.provider.FindEventProvider
import online.motohub.util.Utility
import java.util.*

class FindEventViewModel(application: Application, bundle: Bundle?) : BaseViewModel(application) {

    val provider = FindEventProvider()
    val eventsLiveData = MutableLiveData<ArrayList<EventsResModel>>()
    var profileObj: ProfileResModel? = null

    init {
        profileObj = Gson().fromJson(bundle!!.getString(BundleConstants.MY_PROFILE_OBJ), ProfileResModel::class.java)
    }

    override fun initialize(firstTime: Boolean) {
        super.initialize(firstTime)
        getUpcomingEvents()

    }

    override fun initializeWithNetworkAvailable() {
        super.initializeWithNetworkAvailable()
    }

    private fun getUpcomingEvents() {
        callback!!.showProgress()
        provider.getUpcomingEvents(getInputModel(), ResponseCallback {
            callback!!.hideProgress()
            if (it.isSuccess && it.data != null && it.data.resource != null) {
                eventsLiveData.value = it.data.resource
            }
        })
    }

    private fun getInputModel(): ApiInputModel {
        val status = AppConstants.EVENT_STATUS
        val currentDate =Utility.getInstance().getCurrentDateTime()
        val filter = "(( Date >= $currentDate ) OR ( Finish >= $currentDate )) AND ( EventStatus = $status)"
        val inputModel = ApiInputModel()
        inputModel.filter = filter
        inputModel.related = RelationConstants.EVENT_RELATION
        inputModel.order = "Date ASC"
        return inputModel
    }
}