package online.motohub.viewmodel

import android.app.Application
import android.os.Bundle
import online.motohub.provider.NewsFeedProvider

class NewsFeedViewModel(application: Application, bundle: Bundle?) : BaseViewModel(application) {

    val newsFeedProvider: NewsFeedProvider? = null

    init {

    }

    override fun onCleared() {
        super.onCleared()
    }
}