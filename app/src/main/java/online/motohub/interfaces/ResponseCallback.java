package online.motohub.interfaces;

import online.motohub.bl.ApiResponse;

public interface ResponseCallback<T> {

    public void onResponse(ApiResponse<T> apiResponse);

}
