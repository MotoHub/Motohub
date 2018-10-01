package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PushTokenModel {

    @SerializedName("resource")
    @Expose
    private List<PushTokenResModel> mResource = null;

    public List<PushTokenResModel> getResource() {
        return mResource;
    }

    public void setResource(List<PushTokenResModel> resource) {
        this.mResource = resource;
    }

}
