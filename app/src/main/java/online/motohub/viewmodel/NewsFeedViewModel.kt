package online.motohub.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.google.gson.Gson
import online.motohub.constants.BundleConstants
import online.motohub.constants.RelationConstants
import online.motohub.interfaces.ResponseCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.PostsResModel
import online.motohub.model.ProfileResModel
import online.motohub.provider.NewsFeedProvider
import java.util.*

class NewsFeedViewModel(application: Application, bundle: Bundle?) : BaseViewModel(application) {

    val provider = NewsFeedProvider()
    val newsFeedLiveData = MutableLiveData<ArrayList<PostsResModel>>()
    var profileObj: ProfileResModel? = null
    val activeBundle = bundle;

    init {
        profileObj = Gson().fromJson(bundle!!.getString(BundleConstants.MY_PROFILE_OBJ), ProfileResModel::class.java)
    }

    override fun initialize(firstTime: Boolean) {
        super.initialize(firstTime)
        getFeeds()

    }

    override fun initializeWithNetworkAvailable() {
        super.initializeWithNetworkAvailable()
    }

    private fun getFeeds() {

        callback!!.showProgress()
        provider.getAllFeeds(getInputModel(10, 0), ResponseCallback {
                        callback!!.hideProgress()
            if (it.isSuccess && it.data != null && it.data.resource != null) {
                newsFeedLiveData.value = it.data.resource
            }
        })
    }

    private fun getInputModel(limit: Int, offset: Int): ApiInputModel {
        val inputModel = ApiInputModel()
        inputModel.userID = profileObj!!.id
        inputModel.related = RelationConstants.POST_FEED_RELATION
        inputModel.order = "CreatedAt DESC"
        inputModel.limit = limit
        inputModel.offset = offset
        inputModel.includeCount = true
        return inputModel
    }
}