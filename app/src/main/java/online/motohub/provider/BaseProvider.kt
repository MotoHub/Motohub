package online.motohub.provider

import okhttp3.RequestBody
import online.motohub.bl.ApiClient
import online.motohub.bl.MHFileCacheImplementor
import online.motohub.bl.MotoHubApp
import online.motohub.interfaces.UserPreferences

open class BaseProvider {

    fun getRequestBody(inputBody: String): RequestBody {
        return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), inputBody)
    }

    val apiService: ApiClient
        get() = MotoHubApp.getInstance().apiClients

    val userPreference: UserPreferences
        get() = MotoHubApp.getInstance().userPreferences

    val fileCache: MHFileCacheImplementor
        get() = MotoHubApp.getInstance().fileCacheImplementor
}