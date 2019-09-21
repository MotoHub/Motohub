package online.motohub.interfaces;


import online.motohub.newdesign.bl.MTError;

public interface ResponseSuccessCallback<T> {

    public void onSuccess(T data) throws MTError;

}
