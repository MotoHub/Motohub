package online.motohub.provider

import online.motohub.bl.DefaultResponse
import online.motohub.interfaces.ResponseCallback
import online.motohub.interfaces.ResponseSuccessCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.PostsModel
import retrofit2.Call

class NewsFeedProvider : BaseProvider() {

    fun getAllFeeds(inputModel: ApiInputModel, response: ResponseCallback<PostsModel>): Call<PostsModel> {
        val call = apiService.retrofitApiInterface.getAllFeeds(inputModel.userID, inputModel.related, inputModel.order,
                inputModel.limit, inputModel.offset, inputModel.includeCount)
        call.enqueue(DefaultResponse(response, ResponseSuccessCallback {}))
        return call
    }
}