package online.motohub.provider

import online.motohub.bl.DefaultResponse
import online.motohub.interfaces.ResponseCallback
import online.motohub.interfaces.ResponseSuccessCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.PostsModel
import online.motohub.model.ProfileModel
import retrofit2.Call

class HomeProvider : BaseProvider() {

    fun getProfiles(inputModel: ApiInputModel, response: ResponseCallback<ProfileModel>): Call<ProfileModel> {
        val call = apiService.apiInterface.getProfiles(inputModel.filter, inputModel.related)
        call.enqueue(DefaultResponse(response, ResponseSuccessCallback {}))
        return call
    }
}