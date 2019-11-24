package online.motohub.newdesign.provider

import online.motohub.interfaces.ResponseCallback
import online.motohub.interfaces.ResponseSuccessCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.GalleryImgModel
import online.motohub.newdesign.bl.DefaultResponse
import retrofit2.Call

class BusinessPhotosProvider : BaseProvider() {

    fun getPhotosList(inputModel: ApiInputModel, response: ResponseCallback<GalleryImgModel>): Call<GalleryImgModel> {
        val call = apiService.apiInterface.getPhotosList(inputModel.filter, inputModel.order)
        call.enqueue(DefaultResponse(response, ResponseSuccessCallback {}))
        return call
    }
}