package online.motohub.newdesign.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.google.gson.Gson
import online.motohub.interfaces.ResponseCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.GalleryVideoResModel
import online.motohub.model.ProfileResModel
import online.motohub.model.promoter_club_news_media.PromotersResModel
import online.motohub.newdesign.constants.AppConstants
import online.motohub.newdesign.constants.BundleConstants
import online.motohub.newdesign.constants.OtherConstants
import online.motohub.newdesign.provider.BusinessVideosProvider
import online.motohub.retrofit.APIConstants


class BusinessVideosViewModel(application: Application, bundle: Bundle?) : BaseViewModel(application) {

    val provider = BusinessVideosProvider()
    val videosLiveData = MutableLiveData<ArrayList<GalleryVideoResModel>>()
    var profileObj: ProfileResModel? = null
    var businessProfileObj: PromotersResModel? = null
    var totalCount: Int = 0

    init {
        profileObj = Gson().fromJson(bundle!!.getString(BundleConstants.MY_PROFILE_OBJ), ProfileResModel::class.java)
        businessProfileObj = Gson().fromJson(bundle.getString(BundleConstants.BUSINESS_PROFILE_OBJ), PromotersResModel::class.java)
    }

    override fun initialize(firstTime: Boolean) {
        super.initialize(firstTime)
        getVideosList(0, true)

    }

    fun getVideosList(offset: Int, showProgress: Boolean) {
        if (showProgress)
            callback!!.showProgress()
        provider.getVideosList(getInputModel(offset), ResponseCallback {
            if (showProgress)
                callback!!.hideProgress()
            if (it.isSuccess && it.data != null && it.data.resource != null) {
                if (it.data.meta != null)
                    totalCount = it.data.meta.count
                videosLiveData.value = it.data.resource
            }
        })
    }

    private fun getInputModel(offset: Int): ApiInputModel {
        val inputModel = ApiInputModel()
        val filter = ("(UserID=" + businessProfileObj!!.userId + ") AND ((" + APIConstants.UserType + "=" + businessProfileObj!!.getUserType() + ") OR ("
                + APIConstants.UserType + " = " + AppConstants.USER_EVENT_VIDEOS + "))")
        inputModel.filter = filter
        inputModel.order = "CreatedAt DESC"
        inputModel.limit = OtherConstants.LIMIT_20
        inputModel.offset = offset
        inputModel.includeCount = true
        return inputModel
    }
}