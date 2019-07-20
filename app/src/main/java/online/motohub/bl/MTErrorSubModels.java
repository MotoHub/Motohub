package online.motohub.bl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MTErrorSubModels {

    public final String type;
    final ArrayList<String> messages;

    public MTErrorSubModels(String key, JSONArray value) {
        type = key;
        messages = new ArrayList<>();
        int count = value.length();
        for (int i = 0; i < count; i++) {
            try {
                messages.add(value.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
