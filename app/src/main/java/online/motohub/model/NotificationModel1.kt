package online.motohub.model

import com.google.gson.annotations.SerializedName

import org.json.JSONException
import org.json.JSONObject

import java.io.UnsupportedEncodingException
import java.net.URLDecoder

class NotificationModel1 {

    @SerializedName("mainObj")
    var mainObj: JSONObject? = null
    @SerializedName("Msg")
    private var Msg: String? = null
    @SerializedName("Type")
    private var Type: String? = null
    @SerializedName("Details")
    private var Details: JSONObject? = null
    @SerializedName("forceNotification")
    var isForceNotification: Boolean = false
    @SerializedName("testNotification")
    var isTestNotification: Boolean = false
    @SerializedName("notificationID")
    var notificationID: Int = 0

    val msg: String?
        get() {
            if (isTestNotification) {
                return "TestNotification"
            }
            try {
                Msg = URLDecoder.decode(mainObj!!.getString("Msg"), "UTF-8")
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            return Msg
        }

    val type: String?
        get() {
            if (isTestNotification) {
                return "TestNotification"
            }
            try {
                Type = mainObj!!.getString("Type")
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return Type
        }

    val details: JSONObject?
        get() {
            try {
                Details = mainObj!!.getJSONObject("Details")
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return Details
        }
}
