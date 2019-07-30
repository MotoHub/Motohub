package online.motohub.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import online.motohub.constants.RelationConstants
import online.motohub.interfaces.ResponseCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.ProfileResModel
import online.motohub.provider.HomeProvider
import online.motohub.util.PreferenceUtils
import java.util.*

class HomeViewModel(application: Application, bundle: Bundle?) : BaseViewModel(application) {

    val provider = HomeProvider()
    val profileListLiveData = MutableLiveData<ArrayList<ProfileResModel>>()
    var profileObj: ProfileResModel? = null

    override fun initialize(firstTime: Boolean) {
        super.initialize(firstTime)
        getProfileList()
    }

    override fun initializeWithNetworkAvailable() {
        super.initializeWithNetworkAvailable()
    }

    private fun getProfileList() {
        val inputModel = ApiInputModel()

        val userID = provider.userPreference.getInt(PreferenceUtils.USER_ID)
        inputModel.filter = "UserID = $userID"
        inputModel.related = RelationConstants.PROFILE_RELATION_ALL
        callback!!.showProgress()
        provider.getProfiles(inputModel, ResponseCallback {
            callback!!.hideProgress()
            if (it.isSuccess && it.data != null && it.data.resource != null && it.data.resource.size > 0) {
                setProfileObj(it.data.resource)
                profileListLiveData.value = it.data.resource

            }
        })
    }

    private fun setProfileObj(list: ArrayList<ProfileResModel>) {
        profileObj = list.get(0)
    }
}