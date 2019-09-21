package online.motohub.newdesign.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.google.gson.Gson
import online.motohub.interfaces.ResponseCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.PostsResModel
import online.motohub.model.ProfileResModel
import online.motohub.newdesign.constants.BundleConstants
import online.motohub.newdesign.constants.OtherConstants
import online.motohub.newdesign.constants.RelationConstants
import online.motohub.newdesign.provider.NewsFeedProvider
import java.util.*

class NewsFeedViewModel(application: Application, bundle: Bundle?) : BaseViewModel(application) {

    val provider = NewsFeedProvider()
    val newsFeedLiveData = MutableLiveData<ArrayList<PostsResModel>>()
    var profileObj: ProfileResModel? = null
    var totalCount: Int = 0

    init {
        profileObj = Gson().fromJson(bundle!!.getString(BundleConstants.MY_PROFILE_OBJ), ProfileResModel::class.java)
    }

    override fun initialize(firstTime: Boolean) {
        super.initialize(firstTime)
        getFeeds(0, true)

    }

    fun getFeeds(offset: Int, showProgress: Boolean) {
        if (showProgress)
            callback!!.showProgress()
        provider.getAllFeeds(getInputModel(offset), ResponseCallback {
            if (showProgress)
                callback!!.hideProgress()
            if (it.isSuccess && it.data != null && it.data.resource != null) {
                if (it.data.meta != null)
                    totalCount = it.data.meta.count
                newsFeedLiveData.value = it.data.resource
            }
        })
    }

    private fun getInputModel(offset: Int): ApiInputModel {
        val inputModel = ApiInputModel()
        inputModel.userID = profileObj!!.id
        inputModel.related = RelationConstants.POST_FEED_RELATION
        inputModel.order = "CreatedAt DESC"
        inputModel.limit = OtherConstants.LIMIT_10
        inputModel.offset = offset
        inputModel.includeCount = true
        return inputModel
    }
}