package online.motohub.provider

import online.motohub.bl.DefaultResponse
import online.motohub.interfaces.ResponseCallback
import online.motohub.interfaces.ResponseSuccessCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.PostsModel
import online.motohub.model.PromoterVideoModel
import retrofit2.Call

class OnDemandProvider : BaseProvider() {

    fun getOnDemandVideos(inputModel: ApiInputModel, response: ResponseCallback<PromoterVideoModel>): Call<PromoterVideoModel> {
        val call = apiService.apiInterface.getOnDemandVideos(inputModel.fields,inputModel.filter, inputModel.related, inputModel.order,
                inputModel.limit, inputModel.offset, inputModel.includeCount)
        call.enqueue(DefaultResponse(response, ResponseSuccessCallback {}))
        return call
    }
}