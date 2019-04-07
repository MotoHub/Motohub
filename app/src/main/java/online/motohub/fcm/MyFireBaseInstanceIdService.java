package online.motohub.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.ArrayList;
import java.util.List;

import online.motohub.application.MotoHub;
import online.motohub.interfaces.RetrofitResInterface;
import online.motohub.model.PushTokenModel;
import online.motohub.model.PushTokenResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;

public class MyFireBaseInstanceIdService extends FirebaseInstanceIdService implements RetrofitResInterface {

    public static final int UPDATE_PUSH_TOKEN = 1, CREATE_PUSH_TOKEN = 2;

    @Override
    public void onTokenRefresh() {

        getRegistrationToken();

    }

    public void getRegistrationToken() {
        //TODO check logged in or not
        Integer mUserId = PreferenceUtils.getInstance(MotoHub.getApplicationInstance()).getIntData(PreferenceUtils.USER_ID);

        String mFilter = "UserID=" + mUserId;

        RetrofitClient.getRetrofitInstance().callGetPushToken(this, mFilter, RetrofitClient.GET_REGISTRATION_TOKEN);

    }


    public void sendRegistrationToken(String refreshedToken, int type) {
        System.out.println("Token: "+refreshedToken);
        int mUserId = PreferenceUtils.getInstance(MotoHub.getApplicationInstance()).getIntData(PreferenceUtils.USER_ID);
        if (mUserId == 0) {
            return;
        }
        PushTokenModel mPushTokenModel = new PushTokenModel();

        List<PushTokenResModel> mPushTokenResModelList = new ArrayList<>();

        PushTokenResModel mPushTokenResModel = new PushTokenResModel();

        mPushTokenResModel.setPlatform("Android");
        mPushTokenResModel.setUserID(mUserId);
        mPushTokenResModel.setRegistrationtoken(refreshedToken);

        mPushTokenResModelList.add(mPushTokenResModel);

        mPushTokenModel.setResource(mPushTokenResModelList);

        String mFilter = "UserID=" + mUserId;

        if (type == UPDATE_PUSH_TOKEN) {
            RetrofitClient.getRetrofitInstance().callUpdatePushToken(this, mPushTokenModel, mFilter, RetrofitClient.UPDATE_REGISTRATION_TOKEN);
        } else {
            RetrofitClient.getRetrofitInstance().callCreatePushToken(this, mPushTokenModel, RetrofitClient.CREATE_REGISTRATION_TOKEN);
        }

    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        if (responseObj instanceof PushTokenModel) {
            PushTokenModel mPushTokenModel = (PushTokenModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_REGISTRATION_TOKEN:
                    if (mPushTokenModel.getResource() != null && mPushTokenModel.getResource().size() > 0) {
                        sendRegistrationToken(FirebaseInstanceId.getInstance().getToken(), UPDATE_PUSH_TOKEN);
                    } else {
                        sendRegistrationToken(FirebaseInstanceId.getInstance().getToken(), CREATE_PUSH_TOKEN);
                    }
                    break;
                case RetrofitClient.UPDATE_REGISTRATION_TOKEN:
                    if (mPushTokenModel.getResource() != null && mPushTokenModel.getResource().size() == 0) {
                        sendRegistrationToken(FirebaseInstanceId.getInstance().getToken(), CREATE_PUSH_TOKEN);
                    }
                    break;

            }

        }

    }

    @Override
    public void retrofitOnError(int code, String message) {

    }

    @Override
    public void retrofitOnSessionError(int code, String message) {

    }

    @Override
    public void retrofitOnFailure() {
        onTokenRefresh();
    }


}
