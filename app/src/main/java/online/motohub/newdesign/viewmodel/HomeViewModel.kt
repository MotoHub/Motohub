package online.motohub.newdesign.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import online.motohub.R
import online.motohub.interfaces.ResponseCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.ProfileResModel
import online.motohub.newdesign.constants.RelationConstants
import online.motohub.newdesign.provider.HomeProvider
import online.motohub.util.PreferenceUtils
import online.motohub.util.Utility
import java.util.*

class HomeViewModel(application: Application, bundle: Bundle?) : BaseViewModel(application) {

    val provider = HomeProvider()
    val profileListLiveData = MutableLiveData<ArrayList<ProfileResModel>>()
    var profileObj: ProfileResModel? = null
    private var cacheProfile: ArrayList<ProfileResModel>? = null
    var profileSpinnerList = ArrayList<String>()

    override fun initialize(firstTime: Boolean) {
        super.initialize(firstTime)
        cacheProfile = provider.fetchProfileFromCache(getInputModel())
        if (cacheProfile == null) {
            getProfileList()
        } else {
            setProfileObj(cacheProfile!!)
        }
    }

    private fun getProfileList() {
        callback!!.showProgress()
        provider.getProfiles(getInputModel(), ResponseCallback {
            callback!!.hideProgress()
            if (it.isSuccess && it.data != null && it.data.resource != null && it.data.resource.size > 0) {
                setProfileObj(it.data.resource)
            }
        })
    }

    private fun getInputModel(): ApiInputModel {
        val inputModel = ApiInputModel()
        val userID = provider.userPreference.getInt(PreferenceUtils.USER_ID)
        inputModel.filter = "UserID = $userID"
        inputModel.related = RelationConstants.PROFILE_RELATION_ALL
        return inputModel
    }

    private fun setProfileObj(list: ArrayList<ProfileResModel>) {
        profileObj = list[0]
        profileSpinnerList.clear()
        for (data in list) {
            if (Utility.getInstance().isSpectator(data)) {
                profileSpinnerList.add(data.spectatorName)
            } else {
                profileSpinnerList.add(data.motoName)
            }
        }
        profileListLiveData.value = list
    }

    fun changeProfile(pos: Int) {
        if (profileListLiveData.value!!.size > pos) {
            profileObj = profileListLiveData.value!![pos]
            profileListLiveData.value = profileListLiveData.value
        } else {
            callback!!.showMessage(context.getString(R.string.err))
        }
    }
}