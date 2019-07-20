package online.motohub.viewmodel

import android.app.Application
import android.os.Bundle
import online.motohub.provider.NewsFeedProvider

class NewsFeedViewModel(application: Application, bundle: Bundle?) : BaseViewModel(application) {

    val newsFeedProvider: NewsFeedProvider? = null

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

    }
}