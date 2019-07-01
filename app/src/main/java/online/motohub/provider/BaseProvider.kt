package online.motohub.provider

import okhttp3.RequestBody

class BaseProvider {

    fun getRequestBody(inputBody: String): RequestBody {
        return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), inputBody)
    }
}