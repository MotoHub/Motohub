package online.motohub.bl;

import retrofit2.Response;


public class ApiResponse<T> {

    boolean isSuccess;
    T data;
    public MTError error;
    public boolean errorFromServer = false;


    /**
     *
     * @return Call this only if isSuccess returns false
     */
    public String getErrorMessage() {
        return error.getErrorMessage();
    }


    public int getErrorCode(){
        return error.getServerErrorCode();
    }

    /**
     *
     * @return if network operation was success or failure
     */
    public boolean isSuccess() {
        return isSuccess;
    }

    /**
     *
     * @return Call this only if isSuccess returns true
     */
    public T getData() {
        return data;
    }


    public static <V> ApiResponse<V> success(Response<V> response) {
        ApiResponse<V> output = new ApiResponse<>();
        output.isSuccess = true;
        output.data = response.body();
        return output;
    }

    public static <V> ApiResponse<V> success(V data) {
        ApiResponse<V> output = new ApiResponse<>();
        output.isSuccess = true;
        output.data = data;
        return output;
    }

    public static <V> ApiResponse<V> failure(MTError mtError) {
        return failure(mtError, true);
    }

    public static <V> ApiResponse<V> failure(MTError mtError, boolean errorFromServer) {
        ApiResponse<V> output = new ApiResponse<>();
        output.isSuccess = false;
        output.error = mtError;
        output.errorFromServer = errorFromServer;
        return output;
    }

}
