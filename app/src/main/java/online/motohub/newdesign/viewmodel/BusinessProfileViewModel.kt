package online.motohub.newdesign.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.google.gson.Gson
import online.motohub.interfaces.ResponseCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.ProfileResModel
import online.motohub.model.promoter_club_news_media.PromotersResModel
import online.motohub.newdesign.constants.BundleConstants
import online.motohub.newdesign.provider.BusinessProfileProvider

class BusinessProfileViewModel(application: Application, bundle: Bundle?) : BaseViewModel(application) {

    private val provider = BusinessProfileProvider()
    var profileObj: ProfileResModel? = null
    var businessProfileObj: PromotersResModel? = null

    var isMyProfile: Boolean = false
    var followersCount = MutableLiveData<Int>()
    var isAlreadyFollowedByUser = MutableLiveData<Boolean>()
    var isAlreadyFollowedByBusiness = MutableLiveData<Boolean>()
    var isAlreadySubscribedByUser = MutableLiveData<Boolean>()

    init {
        profileObj = Gson().fromJson(bundle!!.getString(BundleConstants.MY_PROFILE_OBJ), ProfileResModel::class.java)
        businessProfileObj = Gson().fromJson(bundle.getString(BundleConstants.BUSINESS_PROFILE_OBJ), PromotersResModel::class.java)
    }

    override fun initialize(firstTime: Boolean) {
        super.initialize(firstTime)
        getBusinessFollowersCount()
    }

    private fun getBusinessFollowersCount() {
//        callback!!.showProgress()
        provider.getBusinessFollowersCount(getInputModel(), ResponseCallback {
            //            callback!!.hideProgress()
            if (it.isSuccess && it.data != null && it.data.resource != null) {
                if (it.data.meta != null)
                    followersCount.value = it.data.meta.count
            }
        })
    }

    private fun getInputModel(): ApiInputModel {
        val inputModel = ApiInputModel()
        inputModel.filter = "PromoterUserID=${businessProfileObj!!.id}"
        inputModel.limit = 1
        inputModel.includeCount = true
        return inputModel
    }

}