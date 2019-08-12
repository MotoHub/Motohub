package online.motohub.bl;

import android.support.annotation.NonNull;

import okhttp3.ResponseBody;
import online.motohub.interfaces.ResponseCallback;
import online.motohub.interfaces.ResponseSuccessCallback;
import online.motohub.model.ErrorMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;


/**
 * Use this to override Retrofit.Callback with app's response
 *
 * @param <T>
 */
public class DefaultResponse<T> implements Callback<T> {

    private final ResponseCallback<T> callback;
    private final ResponseSuccessCallback<T> successCallback;


    public DefaultResponse(@NonNull ResponseCallback<T> callback) {
        this.callback = callback;
        this.successCallback = null;
    }

    public DefaultResponse(@NonNull ResponseCallback<T> callback, ResponseSuccessCallback<T> successCallback) {
        this.callback = callback;
        this.successCallback = successCallback;
    }
//    private ErrorMessage getErrorMessage(Converter<ResponseBody, ErrorMessage> errorConverter,Response response) {
//        ErrorMessage error = null;
//        try {
//            error = errorConverter.convert(response.errorBody());
//        } catch (Exception e) {
//            error = new ErrorMessage("Unexpected error");
//        }
//        return error;
//    }
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            try {
                if (this.successCallback != null) {
                    this.successCallback.onSuccess(response.body());
                }
                this.callback.onResponse(ApiResponse.success(response));
            } catch (MTError e) {
                this.callback.onResponse(ApiResponse.<T>failure(e));
            }

        } else {
            this.callback.onResponse(ApiResponse.<T>failure(new MTError(response)));
        }

    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (!call.isCanceled()) {
            this.callback.onResponse(ApiResponse.<T>failure(new MTError(t, null), false));
        }
    }
}