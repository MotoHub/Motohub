package online.motohub.newdesign.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import android.os.Bundle
import com.google.gson.Gson
import online.motohub.interfaces.ResponseCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.GalleryImgResModel
import online.motohub.model.ProfileResModel
import online.motohub.model.promoter_club_news_media.PromotersResModel
import online.motohub.newdesign.constants.BundleConstants
import online.motohub.newdesign.provider.BusinessPhotosProvider
import online.motohub.retrofit.APIConstants
import java.util.*


class BusinessPhotosViewModel(application: Application, bundle: Bundle?) : BaseViewModel(application) {

    val provider = BusinessPhotosProvider()
    val photosLiveData = MutableLiveData<ArrayList<GalleryImgResModel>>()
    var profileObj: ProfileResModel? = null
    var businessProfileObj: PromotersResModel? = null
    var totalCount: Int = 0

    init {
        profileObj = Gson().fromJson(bundle!!.getString(BundleConstants.MY_PROFILE_OBJ), ProfileResModel::class.java)
        businessProfileObj = Gson().fromJson(bundle.getString(BundleConstants.BUSINESS_PROFILE_OBJ), PromotersResModel::class.java)
    }

    override fun initialize(firstTime: Boolean) {
        super.initialize(firstTime)
        getPhotosList(0, true)

    }

    fun getPhotosList(offset: Int, showProgress: Boolean) {
        if (showProgress)
            callback!!.showProgress()
        provider.getPhotosList(getInputModel(offset), ResponseCallback {
            if (showProgress)
                callback!!.hideProgress()
            if (it.isSuccess && it.data != null && it.data.resource != null) {
//                if (it.data.meta != null)
//                    totalCount = it.data.meta.count
                photosLiveData.value = it.data.resource
            }
        })
    }

    private fun getInputModel(offset: Int): ApiInputModel {
        val inputModel = ApiInputModel()
        val filter = "(UserID=" + businessProfileObj!!.userId + ") AND (" + APIConstants.UserType + "=" + businessProfileObj!!.userType + ")"
        inputModel.filter = filter
        inputModel.order = "CreatedAt DESC"
        return inputModel
    }
}