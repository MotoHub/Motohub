package online.motohub.newdesign.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import android.os.Bundle
import com.google.gson.Gson
import online.motohub.R
import online.motohub.interfaces.ResponseCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.PostsResModel
import online.motohub.model.ProfileResModel
import online.motohub.model.promoter_club_news_media.PromotersResModel
import online.motohub.newdesign.constants.BundleConstants
import online.motohub.newdesign.constants.OtherConstants
import online.motohub.newdesign.constants.RelationConstants
import online.motohub.newdesign.provider.NewsFeedProvider
import java.util.*

class BusinessNewsFeedViewModel(application: Application, bundle: Bundle?) : BaseViewModel(application) {

    val provider = NewsFeedProvider()
    val newsFeedLiveData = MutableLiveData<ArrayList<PostsResModel>>()
    var profileObj: ProfileResModel? = null
    var totalCount: Int = 0
    var businessProfileObj: PromotersResModel? = null

    init {
        profileObj = Gson().fromJson(bundle!!.getString(BundleConstants.MY_PROFILE_OBJ), ProfileResModel::class.java)
        businessProfileObj = Gson().fromJson(bundle.getString(BundleConstants.BUSINESS_PROFILE_OBJ), PromotersResModel::class.java)
    }

    override fun initialize(firstTime: Boolean) {
        super.initialize(firstTime)
        getFeeds(0, true)

    }

    fun getFeeds(offset: Int, showProgress: Boolean) {
        if (showProgress)
            callback!!.showProgress()
        provider.getAllFeeds1(getInputModel(offset), ResponseCallback {
            if (showProgress)
                callback!!.hideProgress()
            if (it.isSuccess && it.data != null && it.data.resource != null) {
                if (it.data.resource.isEmpty())
                    callback!!.showMessage(context.getString(R.string.no_feeds_found_err))
                if (it.data.meta != null)
                    totalCount = it.data.meta.count
                newsFeedLiveData.value = it.data.resource
            }
        })
    }

    private fun getInputModel(offset: Int): ApiInputModel {
        val inputModel = ApiInputModel()
        val filter = "(ProfileID=" + businessProfileObj!!.userId + ") AND (user_type=" + businessProfileObj!!.userType + ")"
//        val filter = "ProfileID=" + businessProfileObj!!.userId
        inputModel.filter = filter
        inputModel.related = RelationConstants.POST_FEED_RELATION
        inputModel.order = "CreatedAt DESC"
        inputModel.limit = OtherConstants.LIMIT_10
        inputModel.offset = offset
        inputModel.includeCount = true
        return inputModel
    }
}