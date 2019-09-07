package online.motohub.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.google.gson.Gson
import online.motohub.constants.BundleConstants
import online.motohub.constants.RelationConstants
import online.motohub.interfaces.ResponseCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.ProfileResModel
import online.motohub.model.promoter_club_news_media.PromotersResModel
import online.motohub.provider.BusinessProfileProvider
import java.util.*

class BusinessProfileListViewModel(application: Application, bundle: Bundle?) : BaseViewModel(application) {

    private val provider = BusinessProfileProvider()
    val businessProfileListLiveData = MutableLiveData<ArrayList<PromotersResModel>>()

     var profileObj: ProfileResModel? = null

    private var businessProfileType: String? = null

    private var cacheProfile: ArrayList<PromotersResModel>? = null

    init {
        profileObj = Gson().fromJson(bundle!!.getString(BundleConstants.MY_PROFILE_OBJ), ProfileResModel::class.java)
        businessProfileType = bundle.getString(BundleConstants.BUSINESS_PROFILE_TYPE)
    }

    override fun initialize(firstTime: Boolean) {
        super.initialize(firstTime)
        //TODO pass usertype in header for save response in cache based on user type
        cacheProfile = provider.fetchBusinessProfileListFromCache(getInputModel())
        if (cacheProfile == null) {
            getBusinessProfileList()
        } else {
            setBusinessProfileList(cacheProfile!!)
        }
    }

    private fun getBusinessProfileList() {
        callback!!.showProgress()
        provider.getBusinessProfileList(getInputModel(), ResponseCallback {
            callback!!.hideProgress()
            if (it.isSuccess && it.data != null && it.data.resource != null && it.data.resource.size > 0) {
                setBusinessProfileList(it.data.resource)
            }
        })
    }

    private fun getInputModel(): ApiInputModel {
        val inputModel = ApiInputModel()
        inputModel.businessProfileType = businessProfileType!!
        inputModel.filter = "(user_type=$businessProfileType) AND (Status=2)"
        inputModel.order = "Priority ASC"
        inputModel.related = RelationConstants.PROMOTER_RELATION
        return inputModel
    }

    private fun setBusinessProfileList(list: ArrayList<PromotersResModel>) {
        businessProfileListLiveData.value = list
    }
}