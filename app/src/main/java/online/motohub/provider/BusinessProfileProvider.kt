package online.motohub.provider

import online.motohub.bl.DefaultResponse
import online.motohub.interfaces.ResponseCallback
import online.motohub.interfaces.ResponseSuccessCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.PromotersFollowers1
import online.motohub.model.promoter_club_news_media.PromotersModel
import online.motohub.model.promoter_club_news_media.PromotersResModel
import retrofit2.Call
import java.util.*

class BusinessProfileProvider : BaseProvider() {

    fun getBusinessProfileList(inputModel: ApiInputModel, response: ResponseCallback<PromotersModel>): Call<PromotersModel> {
        val call = apiService.apiInterface.getBusinessProfileList(inputModel.filter, inputModel.order, inputModel.related)
        call.enqueue(DefaultResponse(response, ResponseSuccessCallback {}))
        return call
    }

    fun fetchBusinessProfileListFromCache(inputModel: ApiInputModel): ArrayList<PromotersResModel>? {
        val call = apiService.apiInterface.getBusinessProfileList(inputModel.filter, inputModel.order, inputModel.related)
        return fileCache.getCachedResponseBody(call, PromotersModel::class.java)?.resource
    }
    fun getBusinessFollowersCount(inputModel: ApiInputModel, response: ResponseCallback<PromotersFollowers1>): Call<PromotersFollowers1> {
        val call = apiService.apiInterface.getBusinessFollowersCount(inputModel.filter, inputModel.limit, inputModel.includeCount)
        call.enqueue(DefaultResponse(response, ResponseSuccessCallback {}))
        return call
    }

}