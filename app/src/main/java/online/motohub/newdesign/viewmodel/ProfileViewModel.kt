package online.motohub.newdesign.viewmodel

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import online.motohub.R
import online.motohub.interfaces.ResponseCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.ProfileResModel
import online.motohub.newdesign.constants.RelationConstants
import online.motohub.newdesign.provider.ProfileProvider
import online.motohub.util.PreferenceUtils
import online.motohub.util.Utility
import java.util.*

class ProfileViewModel(application: Application, bundle: Bundle?) : BaseViewModel(application) {

    val provider = ProfileProvider()
    val profileListLiveData = MutableLiveData<ArrayList<ProfileResModel>>()
    var profileObj: ProfileResModel? = null
    private var cacheProfile: ArrayList<ProfileResModel>? = null
    var profileSpinnerList = ArrayList<String>()

    fun getProfileList() {
        callback!!.showProgress()
        provider.getProfiles(getInputModel(), ResponseCallback {
            callback!!.hideProgress()
            if (it.isSuccess && it.data != null && it.data.resource != null && it.data.resource.size > 0) {
                setProfileObj(it.data.resource)
            }
        })
    }

    fun callLogin(email: String, pwd: String, type: String) {
        callback!!.showProgress()
        val inputModel = ApiInputModel()
        inputModel.email = email
        inputModel.pwd = pwd
        inputModel.type=type

        provider.callLogin(inputModel, ResponseCallback {
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