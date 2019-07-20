package online.motohub.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import online.motohub.interfaces.ResponseCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.PostsResModel
import online.motohub.provider.NewsFeedProvider
import java.util.*

class NewsFeedViewModel(application: Application, bundle: Bundle?) : BaseViewModel(application) {

    val provider: NewsFeedProvider? = NewsFeedProvider()
    val newsFeedLiveData = MutableLiveData<ArrayList<PostsResModel>>()

    init {
        getFeeds()
    }

    override fun initialize(firstTime: Boolean) {
        super.initialize(firstTime)
    }

    override fun initializeWithNetworkAvailable() {
        super.initializeWithNetworkAvailable()
    }

    private fun getFeeds() {
        val ss = ApiInputModel()
        provider!!.getAllFeeds(ss, ResponseCallback {
            if (it.isSuccess && it.data != null && it.data.resource != null) {
                newsFeedLiveData.value = it.data.resource
            }
        })
    }
}