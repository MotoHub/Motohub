package online.motohub.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import online.motohub.application.MotoHub;
import online.motohub.interfaces.RetrofitResInterface;
import online.motohub.model.NotificationModel1;
import online.motohub.model.PushTokenModel;
import online.motohub.model.PushTokenResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.NotificationUtils1;
import online.motohub.util.PreferenceUtils;

public class MyFireBaseMessagingService extends FirebaseMessagingService implements RetrofitResInterface {

    public static final String TAG = MyFireBaseMessagingService.class.getSimpleName();
    public static final String PUSH_MSG_RECEIVER_ACTION = "online.motohub.PUSH_MSG_RECEIVER_ACTION";
    public static final String ENTRY_JSON_OBJ = "entry";
    public static final String SENDER_NAME = "sender_name";
    public static final String ID = "ID";
    public static final String FROM_USER_ID = "FromUserID";
    public static final String TO_USER_ID = "ToUserID";
    public static final String FROM_PROFILE_ID = "FromProfileID";
    public static final String TO_PROFILE_ID = "ToProfileID";
    public static final String CREATED_AT = "CreatedAt";
    public static final String MSG_TYPE = "msg_type";
    public static final String PHOTO_MESSAGE = "photo_message";
    public static final String NAME = "Name";
    public static final String EVENT_GRP_CHAT_EVENT_NAME = "EventName";
    public static final String EVENT_USER_TYPE = "user_type";
    public static final String GRP_CHAT_SENDER_USER_ID = "SenderUserID";
    public static final String GRP_CHAT_SENDER_PROFILE_ID = "SenderProfileID";
    public static final String EVENT_GRP_CHAT_SENDER_NAME = "sender_name";
    public static final String MESSAGE = "Message";
    public static final String PROFILE_PICTURE = "profile_picture";
    public static final String GRP_CHAT = "GroupChat";
    public static final String GRP_NAME = "GroupName";
    public static final String IS_FROM_NOTIFICATION_TRAY = "isFromNotificationTray";
    public static final String GROUP_SENDER_PIC = "SenderProfilePicture";
    public static final String GROUP_SENDER_NAME = "SenderName";

    public static final int UPDATE_PUSH_TOKEN = 1, CREATE_PUSH_TOKEN = 2;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        getRegistrationToken();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            NotificationModel1 model1 = new NotificationModel1();
            try {
                JSONObject mJsonObject = new JSONObject(remoteMessage.getData());
                if (mJsonObject.optString("title") != null && mJsonObject.optString("title").equals("Notification")) {
                    JSONObject mEntryObj = new JSONObject((String) mJsonObject.opt("message"));
                    model1.setMainObj(mEntryObj);
                    new NotificationUtils1(this, model1);
                } else {
                    model1.setTestNotification(true);
                    new NotificationUtils1(this, model1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getRegistrationToken() {
        //TODO check logged in or not
        Integer mUserId = PreferenceUtils.getInstance(MotoHub.getApplicationInstance()).getIntData(PreferenceUtils.USER_ID);

        String mFilter = "UserID=" + mUserId;

        RetrofitClient.getRetrofitInstance().callGetPushToken(this, mFilter, RetrofitClient.GET_REGISTRATION_TOKEN);

    }


    public void sendRegistrationToken(String refreshedToken, int type) {
        System.out.println("Token: " + refreshedToken);
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
    }
}