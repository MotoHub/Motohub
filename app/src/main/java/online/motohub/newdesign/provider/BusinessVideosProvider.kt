package online.motohub.newdesign.provider

import online.motohub.newdesign.bl.DefaultResponse
import online.motohub.interfaces.ResponseCallback
import online.motohub.interfaces.ResponseSuccessCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.GalleryVideoModel
import online.motohub.model.PostsModel
import retrofit2.Call

class BusinessVideosProvider : BaseProvider() {

    fun getVideosList(inputModel: ApiInputModel, response: ResponseCallback<GalleryVideoModel>): Call<GalleryVideoModel> {
        val call = apiService.apiInterface.getVideosList(inputModel.filter, inputModel.order,
                inputModel.limit, inputModel.offset, inputModel.includeCount)
        call.enqueue(DefaultResponse(response, ResponseSuccessCallback {}))
        return call
    }
}