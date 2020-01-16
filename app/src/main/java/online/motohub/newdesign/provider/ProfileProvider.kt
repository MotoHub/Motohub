package online.motohub.newdesign.provider

import online.motohub.interfaces.ResponseCallback
import online.motohub.interfaces.ResponseSuccessCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.ProfileModel
import online.motohub.model.ProfileResModel
import online.motohub.newdesign.bl.DefaultResponse
import retrofit2.Call
import java.util.*

class ProfileProvider : BaseProvider() {

    fun getProfiles(inputModel: ApiInputModel, response: ResponseCallback<ProfileModel>): Call<ProfileModel> {
        val call = apiService.apiInterface.getProfiles(inputModel.filter, inputModel.related)
        call.enqueue(DefaultResponse(response, ResponseSuccessCallback {}))
        return call
    }

    fun callLogin(inputModel: ApiInputModel, response: ResponseCallback<ProfileModel>): Call<ProfileModel> {
        val call = apiService.apiInterface.callEmailLogin(inputModel.email, inputModel.pwd,inputModel.type)
        call.enqueue(DefaultResponse(response, ResponseSuccessCallback {}))
        return call
    }

    fun fetchProfileFromCache(inputModel: ApiInputModel): ArrayList<ProfileResModel>? {
        val call = apiService.apiInterface.getProfiles(inputModel.filter, inputModel.related)
        return fileCache.getCachedResponseBody(call, ProfileModel::class.java)?.resource
    }
}