package online.motohub.newdesign.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import android.os.Bundle
import com.google.gson.Gson
import online.motohub.R
import online.motohub.interfaces.ResponseCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.EventsResModel
import online.motohub.model.ProfileResModel
import online.motohub.newdesign.constants.AppConstants
import online.motohub.newdesign.constants.BundleConstants
import online.motohub.newdesign.constants.RelationConstants
import online.motohub.newdesign.provider.FindEventProvider
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

    private fun getUpcomingEvents() {
        callback!!.showProgress()
        provider.getUpcomingEvents(getInputModel(), ResponseCallback {
            callback!!.hideProgress()
            if (it.isSuccess && it.data != null && it.data.resource != null) {
                if (it.data.resource.isEmpty())
                    callback!!.showMessage(context.getString(R.string.no_events_err))
                eventsLiveData.value = it.data.resource
            }
        })
    }

    private fun getInputModel(): ApiInputModel {
        val status = AppConstants.EVENT_STATUS
        val currentDate = Utility.getInstance().currentDateTime
        val filter = "(( Date >= $currentDate ) OR ( Finish >= $currentDate )) AND ( EventStatus = $status)"
        val inputModel = ApiInputModel()
        inputModel.filter = filter
        inputModel.related = RelationConstants.EVENT_RELATION
        inputModel.order = "Date ASC"
        return inputModel
    }
}