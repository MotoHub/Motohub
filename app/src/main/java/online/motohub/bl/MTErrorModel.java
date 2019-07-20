package online.motohub.bl;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class MTErrorModel {

    @SerializedName("type")
    public String error_type;

    @SerializedName("message")
    public String error_message;

    @SerializedName("code")
    public int error_code;

    @SerializedName("suberror")
    public ArrayList<MTErrorSubModels> subError = new ArrayList<>();

    public MTErrorModel(String errorMessage) throws JSONException {
        JSONObject jsonObject = new JSONObject(errorMessage);

        if (!jsonObject.isNull("error")) {
            JSONObject errorObject = jsonObject.getJSONObject("error");
            if (!errorObject.isNull("type")) {
                error_type = errorObject.getString("type");
            }
            if (!errorObject.isNull("message")) {
                error_message = errorObject.getString("message");
            }
            if (!errorObject.isNull("code")) {
                error_code = errorObject.getInt("code");
            }
        }
        if (!jsonObject.isNull("errors")) {
            JSONObject errorsObject = jsonObject.getJSONObject("errors");
            Iterator<String> iter = errorsObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    JSONArray value = errorsObject.getJSONArray(key);
                    subError.add(new MTErrorSubModels(key, value));
                } catch (JSONException e) {
                    // Something went wrong!
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isUnautherized() {
        return "unauthorized".equals(error_type);
    }

    public boolean isResourceNotFound() {
        return "not_found".equals(error_type);
    }

    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(error_message);
        if (subError.size() > 0) {
            builder.append(" - ");
            for (int i = 0; i < subError.size(); i++) {
                ArrayList<String> a = subError.get(i).messages;
                builder.append(TextUtils.join(", ", a));
            }
        }
        return builder.toString();
    }
}
