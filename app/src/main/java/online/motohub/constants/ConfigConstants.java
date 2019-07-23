package online.motohub.constants;

import android.text.TextUtils;

import online.motohub.util.UrlUtils;

public class ConfigConstants {

    private static String BASE_URL;

    public static String getBaseUrl() {
        if (TextUtils.isEmpty(BASE_URL)) {
            BASE_URL = UrlUtils.BASE_URL;
        }
        return BASE_URL;
    }

    public void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }
}
