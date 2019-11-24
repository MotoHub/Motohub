package online.motohub.newdesign.provider

import online.motohub.interfaces.ResponseCallback
import online.motohub.interfaces.ResponseSuccessCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.OndemandNewResponse
import online.motohub.newdesign.bl.DefaultResponse
import retrofit2.Call
import java.util.*

class OnDemandEventsProvider : BaseProvider() {

    fun getOnDemandEvents(inputModel: ApiInputModel, response: ResponseCallback<ArrayList<OndemandNewResponse>>): Call<ArrayList<OndemandNewResponse>> {
        val call = apiService.apiInterface.getOnDemandEvents(inputModel.apiKey)
        call.enqueue(DefaultResponse(response, ResponseSuccessCallback {}))
        return call
    }
}