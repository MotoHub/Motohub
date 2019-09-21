package online.motohub.newdesign.bl;

import okhttp3.Response;
import retrofit2.Call;

public interface FileCacheImplementor {
    void saveResponseBody(Response response, String overRideFileName);

    <T> T getCachedResponseBody(Call<T> call, Class<T> classObject);

    void deleteAll();
}
