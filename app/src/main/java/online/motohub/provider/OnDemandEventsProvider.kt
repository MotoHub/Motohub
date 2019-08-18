package online.motohub.provider

import online.motohub.bl.DefaultResponse
import online.motohub.interfaces.ResponseCallback
import online.motohub.interfaces.ResponseSuccessCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.OndemandNewResponse
import online.motohub.model.PostsModel
import online.motohub.model.PromoterVideoModel
import retrofit2.Call
import java.util.ArrayList

class OnDemandEventsProvider : BaseProvider() {

    fun getOnDemandEvents(inputModel: ApiInputModel, response: ResponseCallback<ArrayList<OndemandNewResponse>>): Call<ArrayList<OndemandNewResponse>> {
        val call = apiService.apiInterface.getOnDemandEvents(inputModel.apiKey)
        call.enqueue(DefaultResponse(response, ResponseSuccessCallback {}))
        return call
    }
}