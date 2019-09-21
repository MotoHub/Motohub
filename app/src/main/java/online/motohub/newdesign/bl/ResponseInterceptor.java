package online.motohub.newdesign.bl;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Add this Interceptor to okhttp networkInterceptor. This will ensure that if server does not support etag, it will force caching response so subsequent response will be delivered from local cache
 * <p>
 * Configure CACHE_MAX_AGE_SEC with max_age limit you want to keep cache valid.
 * <p>
 * Valid until server implements etag. Can be removed after server implements etag.
 * <p>
 * Test Cases:
 * 1. If CACHE_MAX_AGE_SEC is provided, subsequent request is served from cache.
 * 2. If CACHE_MAX_AGE_SEC is not provided, it will fetch the latest code from server.
 * 3. if server sends no-cache it will cache the result
 * 4. if server implements cache-control it will bypass this mechanism and uses server value.
 * 5. For force network, best practice is to delete request from cache.
 * <p>
 * <p>
 * To force cache use below code
 * <p>
 * try {
 * String s1 = call.request().url().toString();
 * Iterator<String> iterator = cache.urls();
 * boolean isDone = false;
 * while (iterator.hasNext() && !isDone) {
 * String s = iterator.next();
 * if (s.equals(s1)) {
 * iterator.remove();
 * Log.e("Cache Removed", "Cache is successfully removed");
 * isDone = true;
 * }
 * }
 * } catch (IOException e) {
 * e.printStackTrace();
 * }
 */
public class ResponseInterceptor implements Interceptor {

    private final FileCacheImplementor fileCacheImplementor;

    public ResponseInterceptor(FileCacheImplementor fileCacheImplementor) {
        this.fileCacheImplementor = fileCacheImplementor;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        // saving cache
        String save_response = request.header("SAVE_RESPONSE");
        String saveResponseAs = request.header("SAVE_RESPONSE_AS");

        Request.Builder newRequest = request.newBuilder();
        if (save_response != null) {
            newRequest.removeHeader("SAVE_RESPONSE");
        }

        if (saveResponseAs != null) {
            newRequest.removeHeader("SAVE_RESPONSE_AS");
        }

        Response originalResponse = chain.proceed(newRequest.build());


        if (parseBoolean(save_response) || !TextUtils.isEmpty(saveResponseAs)) {
            if (fileCacheImplementor != null)
                fileCacheImplementor.saveResponseBody(originalResponse, saveResponseAs);
        }

        // Forcing local cache
        String cache_request_max_age = request.header("CACHE_MAX_AGE_SEC");
        if (cache_request_max_age != null) {
            int max_age = parseInt(cache_request_max_age);
            if (max_age != 0) {
                // overriding cache from server as server was supporting etag at the time of this writing
                CacheControl c = originalResponse.cacheControl();
                if (c.noCache()) {

                    Log.e("","Using local etag mechanism");
                    return originalResponse.newBuilder()
                            .removeHeader("CACHE_MAX_AGE_SEC")
                            .header("Cache-Control", "max-age=" + max_age)
                            .build();
                }
            }
        }

        return originalResponse;
    }

    public int parseInt(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                return Integer.parseInt(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public boolean parseBoolean(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                return Boolean.parseBoolean(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
