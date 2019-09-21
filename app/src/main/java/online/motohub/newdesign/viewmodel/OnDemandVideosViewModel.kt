package online.motohub.newdesign.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import android.text.TextUtils
import com.google.gson.Gson
import online.motohub.newdesign.constants.BundleConstants
import online.motohub.newdesign.constants.OtherConstants
import online.motohub.newdesign.constants.RelationConstants
import online.motohub.interfaces.ResponseCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.ProfileResModel
import online.motohub.model.PromoterVideoModel
import online.motohub.newdesign.provider.OnDemandVideosProvider
import java.util.*

class OnDemandVideosViewModel(application: Application, bundle: Bundle?) : BaseViewModel(application) {

    val provider = OnDemandVideosProvider()
    val onDemandVideoLiveData = MutableLiveData<ArrayList<PromoterVideoModel.PromoterVideoResModel>>()
    var profileObj: ProfileResModel? = null
    var totalCount: Int = 0

    init {
        profileObj = Gson().fromJson(bundle!!.getString(BundleConstants.MY_PROFILE_OBJ), ProfileResModel::class.java)
    }

    override fun initialize(firstTime: Boolean) {
        super.initialize(firstTime)
        getOnDemandVideos("", 0, true)

    }

    fun getOnDemandVideos(searchStr: String, offset: Int, showProgress: Boolean) {
        if (showProgress)
            callback!!.showProgress()
        provider.getOnDemandVideos(getInputModel(searchStr, offset), ResponseCallback {
            if (showProgress)
                callback!!.hideProgress()
            if (it.isSuccess && it.data != null && it.data.resource != null) {
                if (it.data.resource.size == 0) {
                    callback!!.showMessage("No ondemand videos are available")
                }
                totalCount = it.data.meta.count
                onDemandVideoLiveData.value = it.data.resource
            }
        })
    }

    private fun getInputModel(searchStr: String, offset: Int): ApiInputModel {
        val inputModel = ApiInputModel()
        inputModel.fields = "*"
        if (TextUtils.isEmpty(searchStr)) {
            inputModel.filter = "(UserType != usereventvideos) AND (ReportStatus == 0)"
        } else {
            inputModel.filter = "(UserType != usereventvideos) AND (ReportStatus == 0) AND (Caption LIKE '%$searchStr%')"
        }
        inputModel.related = RelationConstants.ON_DEMAND_VIDEO_RELATION
        inputModel.order = "CreatedAt DESC"
        inputModel.limit = OtherConstants.LIMIT_10
        inputModel.offset = offset
        inputModel.includeCount = true
        return inputModel
    }
}