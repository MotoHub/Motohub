package online.motohub.interfaces;


import online.motohub.newdesign.bl.MTError;

public interface ResponseFailureCallback<T> {

    public void onFailure(MTError error);

}
