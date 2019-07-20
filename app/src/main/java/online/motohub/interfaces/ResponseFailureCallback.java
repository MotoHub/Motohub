package online.motohub.interfaces;


import online.motohub.bl.MTError;

public interface ResponseFailureCallback<T> {

    public void onFailure(MTError error);

}
