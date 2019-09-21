package online.motohub.interfaces;

import online.motohub.newdesign.bl.ApiResponse;

public interface ResponseCallback<T> {

    public void onResponse(ApiResponse<T> apiResponse);

}
