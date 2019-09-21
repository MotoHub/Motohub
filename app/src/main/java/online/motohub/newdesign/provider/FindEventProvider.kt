package online.motohub.newdesign.provider

import online.motohub.newdesign.bl.DefaultResponse
import online.motohub.interfaces.ResponseCallback
import online.motohub.interfaces.ResponseSuccessCallback
import online.motohub.model.ApiInputModel
import online.motohub.model.EventsModel
import retrofit2.Call

class FindEventProvider : BaseProvider() {

    fun getUpcomingEvents(inputModel: ApiInputModel, response: ResponseCallback<EventsModel>): Call<EventsModel> {
        val call = apiService.apiInterface.getEvents(inputModel.filter, inputModel.related, inputModel.order)
        call.enqueue(DefaultResponse(response, ResponseSuccessCallback {}))
        return call
    }
}