package online.motohub.retrofit;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.application.MotoHub;
import online.motohub.fcm.MyFireBaseInstanceIdService;
import online.motohub.fragment.BaseFragment;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.RetrofitApiInterface;
import online.motohub.interfaces.RetrofitResInterface;
import online.motohub.model.BlockedUserModel;
import online.motohub.model.ClubGroupModel;
import online.motohub.model.CommonResponse;
import online.motohub.model.ErrorMessage;
import online.motohub.model.EventAddOnModel;
import online.motohub.model.EventAnswersModel;
import online.motohub.model.EventCategoryModel;
import online.motohub.model.EventGrpChatMsgModel;
import online.motohub.model.EventLiveGroupChatModel;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsWhoIsGoingModel;
import online.motohub.model.FeedCommentLikeModel;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedCommentReplyModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.FollowProfileModel;
import online.motohub.model.FollowProfileModel1;
import online.motohub.model.GalleryImgModel;
import online.motohub.model.GalleryVideoModel;
import online.motohub.model.GroupChatMsgModel;
import online.motohub.model.GroupChatRoomModel;
import online.motohub.model.ImageModel;
import online.motohub.model.LiveStreamPaymentResponse;
import online.motohub.model.LiveStreamRequestResponse;
import online.motohub.model.LiveStreamResponse;
import online.motohub.model.LoginModel;
import online.motohub.model.NotificationBlockedUsersModel;
import online.motohub.model.NotificationModel;
import online.motohub.model.OnDemandVideoUploadedResponse;
import online.motohub.model.OndemandNewResponse;
import online.motohub.model.PaymentCardDetailsModel;
import online.motohub.model.PaymentModel;
import online.motohub.model.PostReportModel;
import online.motohub.model.PostsModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.PromoterSubsResModel;
import online.motohub.model.PromoterVideoModel;
import online.motohub.model.PromotersFollowers1;
import online.motohub.model.PurchasedAddOnModel;
import online.motohub.model.PushTokenModel;
import online.motohub.model.RacingModel;
import online.motohub.model.ReplyLikeModel;
import online.motohub.model.SessionModel;
import online.motohub.model.SignUpResModel;
import online.motohub.model.SingleChatCountModel;
import online.motohub.model.SingleChatMsgModel;
import online.motohub.model.SingleChatRoomModel;
import online.motohub.model.SingleChatUnreadMsgModel;
import online.motohub.model.SpectatorLiveModel;
import online.motohub.model.VehicleInfoLikeModel;
import online.motohub.model.VideoCommentLikeModel;
import online.motohub.model.VideoCommentReplyModel;
import online.motohub.model.VideoCommentsModel;
import online.motohub.model.VideoLikesModel;
import online.motohub.model.VideoReplyLikeModel;
import online.motohub.model.VideoShareModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.UrlUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final int FACEBOOK_LOGIN_RESPONSE = 0;

    public static final int UPDATE_SESSION_RESPONSE = 1;

    public static final int CREATE_PROFILE_RESPONSE = 2;

    public static final int UPDATE_PROFILE_RESPONSE = 3;

    public static final int GET_PROFILE_RESPONSE = 4;

    public static final int GET_OTHER_PROFILES_RESPONSE = 5;

    public static final int GET_FOLLOWERS_FOLLOWING_PROFILE_RESPONSE = 6;

    public static final int GET_FOLLOWING_PROFILE_RESPONSE = 7;

    public static final int FOLLOW_PROFILE_RESPONSE = 8;

    public static final int UN_FOLLOW_PROFILE_RESPONSE = 9;

    public static final int CREATE_PROFILE_POSTS_RESPONSE = 10;

    public static final int UPDATE_PROFILE_POSTS_RESPONSE = 11;

    public static final int DELETE_PROFILE_POSTS_RESPONSE = 12;

    public static final int GET_PROFILE_POSTS_RESPONSE = 13;

    public static final int GET_FEED_POSTS_RESPONSE = 14;

    public static final int GET_EVENTS_RESPONSE = 15;

    public static final int GET_EVENTS_WHO_IS_GOING = 16;

    public static final int EVENTS_BOOKING_RESPONSE = 17;

    public static final int GET_REGISTRATION_TOKEN = 18;

    public static final int CREATE_REGISTRATION_TOKEN = 19;

    public static final int UPDATE_REGISTRATION_TOKEN = 20;

    public static final int CREATE_SINGLE_CHAT_ROOM = 21;

    public static final int GET_SINGLE_CHAT_ROOM = 22;

    public static final int GET_GALLERY_DATA_RESPONSE = 23;

    public static final int POST_GALLERY_DATA_RESPONSE = 24;

    public static final int UPLOAD_IMAGE_FILE_RESPONSE = 25;

    public static final int POST_GALLERY_VIDEO_DATA_RESPONSE = 26;

    public static final int UPLOAD_VIDEO_FILE_RESPONSE = 27;

    public static final int GET_VIDEO_FILE_RESPONSE = 28;

    public static final int SEND_SINGLE_CHAT_MSG = 29;

    public static final int GET_SINGLE_CHAT_MSG = 30;

    public static final int SEND_EVENT_GRP_CHAT_MSG = 31;

    public static final int GET_EVENT_GRP_CHAT_MSG = 32;

    public static final int GET_EVENT_SESSION_RESPONSE = 33;

    public static final int GET_ALL_TRACK_RESPONSE = 34;

    public static final int GET_ALL_EVENTS_FROM_TRACK_RESPONSE = 35;

    public static final int CREATE_GRP_CHAT_ROOM_RESPONSE = 36;

    public static final int GET_GRP_CHAT_ROOM_RESPONSE = 37;

    public static final int SEND_GRP_CHAT_MSG = 38;

    public static final int GET_GRP_CHAT_MSG = 39;

    public static final int POST_DATA_FOR_PAYMENT = 40;

    public static final int POST_LIKES = 41;

    public static final int POST_UNLIKE = 42;

    public static final int POST_COMMENTS = 43;

    public static final int POST_SHARES = 44;

    public static final int SHARED_POST_RESPONSE = 45;

    public static final int GET_PROMOTERS_RESPONSE = 46;

    public static final int CALL_BLOCK_USER_PROFILE = 47;

    public static final int CALL_UN_BLOCK_USER_PROFILE = 48;

    public static final int CALL_GET_BLOCKED_USER_PROFILE = 49;

    public static final int POST_COMMENTS_REPLY = 50;

    public static final int CALL_GET_NOTIFICATIONS = 51;

    public static final int GET_OTHER_PROFILE_RESPONSE = 52;

    public static final int FACEBOOK_LOGOUT = 57;

    public static final int RACING_RESPONSE = 58;

    public static final int POST_EVENT_ANSWERS = 61;

    public static final int PROFILE_FIND_FRIENDS_RESPONSE = 62;

    public static final int UPDATE_EVENT_ANSWERS = 63;

    public static final int UPLOAD_COVER_IMAGE_FILE_RESPONSE = 64;

    public static final int GET_PROMOTER_FOLLOW_RESPONSE = 65;

    public static final int GET_PROMOTER_UN_FOLLOW_RESPONSE = 66;

    public static final int UPLOAD_PROFILE_IMAGE_FILE_RESPONSE = 67;

    public static final int DELETE_SHARED_POST_RESPONSE = 68;

    public static final int PROFILE_FIND_FRIENDS_LOAD_MORE_RESPONSE = 69;

    public static final int EMAIL_SIGN_UP_RESPONSE = 70;

    public static final int EMAIL_LOGIN_RESPONSE = 71;

    public static final int POST_SUBSCRIBE_CLUB = 72;

    public static final int POST_UNSUBSCRIBE_CLUB = 73;

    public static final int COMMENT_LIKES = 74;

    public static final int COMMENT_UNLIKE = 75;

    public static final int DELETE_UNSUBSCRIBE_CLUB = 76;

    public static final int GET_EVENT_ANSWER = 77;

    public static final int VEHICLE_INFO_LIKE = 78;

    public static final int VEHICLE_INFO_UN_LIKE = 79;

    public static final int CALL_GET_ADD_ON_RESPONSE = 80;

    public static final int POST_ADD_ON = 81;

    public static final int CALL_GET_COMMENTS = 82;

    public static final int REPLY_LIKE = 83;

    public static final int REPLY_UNLIKE = 84;

    public static final int CALL_GET_COMMENTS_REPLY = 85;

    public static final int GET_LIVE_EVENT_GRP_CHAT_MSG = 86;

    public static final int SEND_LIVE_EVENT_GRP_CHAT_MSG = 87;

    public static final int GET_ALL_USER_PROFILES = 88;

    public static final int GET_ALL_PROMOTER_PROFILES = 89;

    public static final int POST_EVENT_LIVE_GROUP_CHAT_MEMBER = 90;

    public static final int DELETE_EVENT_LIVE_GROUP_CHAT_MEMBER = 91;

    public static final int VIDEO_UNLIKE = 92;

    public static final int VIDEO_LIKES = 93;

    public static final int CALL_GET_VIDEO_COMMENTS = 94;

    public static final int VIDEO_COMMENTS = 95;

    public static final int VIDEO_COMMENTS_LIKE = 96;

    public static final int VIDEO_COMMENTS_UNLIKE = 97;

    public static final int POST_VIDEO_COMMENTS_REPLY = 98;

    public static final int VIDEO_REPLY_LIKE = 99;

    public static final int VIDEO_REPLY_UNLIKE = 100;

    public static final int CALL_GET_VIDEO_REPLIES = 101;

    public static final int VIDEO_SHARES = 102;

    public static final int SPECTATOR_LIVE_RESPONSE = 103;

    public static final int UN_FOLLOW_MY_PROFILE_RESPONSE = 104;

    public static final int UN_FOLLOW_BOTH_PROFILE_RESPONSE = 105;

    public static final int CALL_GET_VEHICLE_INFO_LIKES = 106;

    public static final int CALL_GET_EVENT_LIVE_PROMOTER_GRP_CHAT_MSG = 107;

    public static final int CALL_GET_PROMOTER_SUBS = 108;

    public static final int UPDATE_SUBSCRIPTION = 109;

    public static final int PROMOTER_PAYMENT_SUBSCRIPTION = 110;

    public static final int PROMOTER_PAYMENT_UNSUBSCRIPTION = 111;

    public static final int CALL_REMOVE_PROMOTER_SUBS = 112;

    public static final int GET_SEARCH_VIDEO_FILE_RESPONSE = 113;

    public static final int CHECK_FOLLOWER_STATUS = 114;

    public static final int DELETE_MY_PROFILE_IMAGE = 115;

    public static final int REPORT_POST_RESPONSE = 116;

    public static final int PROMOTER_IS_ALREADY_FOLLOWED = 117;

    public static final int PROFILE_IS_ALREADY_FOLLOWED = 118;

    public static final int CALL_GET_CLUB_USERS = 119;

    public static final int GET_follower_count = 120;

    public static final int GET_following_count = 121;

    public static final int DELETE_VIDEO_SHARED_POST_RESPONSE = 122;

    public static final int GET_PHONE_EMAIL_PROFILES_RESPONSE = 123;

    public static final int PROFILE_FIND_PHONE_EMAIL_FRIENDS_RESPONSE = 124;

    public static final int GET_SINGLE_CHAT_COUNT = 125;

    public static final int GET_SINGLE_CHAT_LAST_MSG = 126;

    public static final int SET_MSG_STATUS = 127;

    public static final int UPDATE_VIDEO_POST_RESPONSE = 128;

    public static final int CALL_GET_PAYMENT_CARD_DETAILS = 129;

    public static final int CALL_ADD_PAYMENT_CARD_DETAILS = 130;

    public static final int DELETE_PAYMENT_CARD_DETAILS = 131;

    public static final int USER_COUNT = 132;

    public static final int BLOCK_NOTIFY = 133;

    public static final int UNBLOCK_NOTIFY = 134;

    public static final int GET_CLUB_USERS = 135;

    public static final int GET_SHOP_VEHICLES = 136;

    public static final int GET_OTHER_PROFILES_FOLLOW_RESPONSE = 137;


    public static final int SEARCH_VIDEO_FILE_RESPONSE = 138;


    public static final int CALL_GET_PROFILE_USER_TYPE = 139;

    public static final int GET_PROMOTER_RESPONSE = 140;

    public static final int GET_PROMOTERS_FOLLOWER_RESPONSE = 141;

    public static final int EVENTS_FREE_BOOKING_RESPONSE = 142;

    public static final int GET_FOLLOW_FOLLOWERS = 143;

    public static final int ON_DEMAND_NOTIFY = 144;

    public static final int FEED_VIDEO_COUNT = 145;

    public static final int ADD_FEED_COUNT = 146;

    public static final int UPDATE_FEED_COUNT = 147;

    private static RetrofitClient mRetrofitInstance;

    private static Converter<ResponseBody, ErrorMessage> errorConverter;

    private RetrofitClient() {
    }

    /**
     * @return RetrofitInstance.
     */
    public static RetrofitClient getRetrofitInstance() {
        if (mRetrofitInstance == null) {
            mRetrofitInstance = new RetrofitClient();
        }
        return mRetrofitInstance;
    }

    private ErrorMessage getErrorMessage(Response response) {
        ErrorMessage error = null;
        try {
            error = errorConverter.convert(response.errorBody());
        } catch (Exception e) {
            error = new ErrorMessage("Unexpected error");
        }
        return error;
    }

    public RetrofitApiInterface getRetrofitApiInterface() {

        final String mDreamFactoryApiKey = MotoHub.getApplicationInstance().getResources().getString(R.string.dream_factory_api_key);
        final String sessionToken = PreferenceUtils.getInstance(MotoHub.getApplicationInstance()).getStrData(PreferenceUtils.SESSION_TOKEN);

        Log.e("SESSION KEY: ", mDreamFactoryApiKey);
        Log.e("SESSION TOKEN: ", sessionToken);
        // the interceptor with authentication headers
        Interceptor mInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request.Builder mRequestBuilder = chain.request().newBuilder();
                mRequestBuilder.addHeader("X-DreamFactory-Api-Key", mDreamFactoryApiKey);
                if (!sessionToken.isEmpty()) {
                    mRequestBuilder.addHeader("X-DreamFactory-Session-Token", sessionToken);
                }
                return chain.proceed(mRequestBuilder.build());
            }
        };

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        // Add the interceptor to OkHttpClient
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        mBuilder.readTimeout(60, TimeUnit.SECONDS);
        mBuilder.connectTimeout(60, TimeUnit.SECONDS);
        mBuilder.writeTimeout(60, TimeUnit.SECONDS);
        mBuilder.interceptors().add(mInterceptor);
        OkHttpClient mHttpClient = mBuilder.build();

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(UrlUtils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(mHttpClient)
                .build();
        errorConverter = mRetrofit.responseBodyConverter(ErrorMessage.class, new Annotation[0]);
        return mRetrofit.create(RetrofitApiInterface.class);

    }


    public RetrofitApiInterface getRetrofitApiInterfaceRxjava() {

        final String mDreamFactoryApiKey = MotoHub.getApplicationInstance().getResources().getString(R.string.dream_factory_api_key);
        final String sessionToken = PreferenceUtils.getInstance(MotoHub.getApplicationInstance()).getStrData(PreferenceUtils.SESSION_TOKEN);

        Log.e("SESSION KEY: ", mDreamFactoryApiKey);
        Log.e("SESSION TOKEN: ", sessionToken);
        // the interceptor with authentication headers
        Interceptor mInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request.Builder mRequestBuilder = chain.request().newBuilder();
                mRequestBuilder.addHeader("X-DreamFactory-Api-Key", mDreamFactoryApiKey);
                if (!sessionToken.isEmpty()) {
                    mRequestBuilder.addHeader("X-DreamFactory-Session-Token", sessionToken);
                }
                return chain.proceed(mRequestBuilder.build());
            }
        };

        // Add the interceptor to OkHttpClient
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        mBuilder.readTimeout(60, TimeUnit.SECONDS);
        mBuilder.connectTimeout(60, TimeUnit.SECONDS);
        mBuilder.writeTimeout(60, TimeUnit.SECONDS);
        mBuilder.interceptors().add(mInterceptor);
        OkHttpClient mHttpClient = mBuilder.build();

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(UrlUtils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mHttpClient)
                .build();
        errorConverter = mRetrofit.responseBodyConverter(ErrorMessage.class, new Annotation[0]);
        return mRetrofit.create(RetrofitApiInterface.class);

    }


    private RetrofitApiInterface getPromoterPaymentInterface() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(UrlUtils.PAYMENT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        errorConverter = mRetrofit.responseBodyConverter(ErrorMessage.class, new Annotation[0]);
        return mRetrofit.create(RetrofitApiInterface.class);

    }

    public void callFacebookLogin(final BaseActivity activity, String service, String code, String state, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callFacebookLogin(service, code, state)
                .enqueue(new Callback<LoginModel>() {

                    @Override
                    public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }

                });
    }

    public void callSignUp(final BaseActivity activity, JsonObject jsonObject, final int responseType) {
        activity.sysOut("API-INPUT: " + jsonObject.toString());
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.EMAIL_SIGN_UP);
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callSignUp(jsonObject)
                .enqueue(new Callback<SignUpResModel>() {
                    @Override
                    public void onResponse(Call<SignUpResModel> call, Response<SignUpResModel> response) {
                        DialogManager.hideProgress();
                        if (response.body() != null) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else if (response.errorBody() != null) {
                            ErrorMessage mErrorMessage = getErrorMessage(response);
                            activity.onRequestError(mErrorMessage);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<SignUpResModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void callEmailLogin(final BaseActivity activity, JsonObject jsonObject, final int
            responseType) {
        activity.sysOut("API-INPUT: " + jsonObject.toString());
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.USER_SESSION);
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callEmailLogin(jsonObject)
                .enqueue(new Callback<LoginModel>() {
                    @Override
                    public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                        DialogManager.hideProgress();
                        if (response.body() != null) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message(), responseType);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void callUpdateSession(final BaseActivity activity, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callUpdateSession()
                .enqueue(new Callback<SessionModel>() {
                    @Override
                    public void onResponse(Call<SessionModel> call, Response<SessionModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnSessionError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<SessionModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void callUpdateSession(final BaseFragment activity, final int responseType) {

        DialogManager.showProgress(activity.getActivity());

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callUpdateSession()
                .enqueue(new Callback<SessionModel>() {
                    @Override
                    public void onResponse(Call<SessionModel> call, Response<SessionModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnSessionError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<SessionModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callUpdateSession(final Context context, final RetrofitResInterface mResInterface, final int responseType) {

        DialogManager.showProgress(context);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callUpdateSession()
                .enqueue(new Callback<SessionModel>() {
                    @Override
                    public void onResponse(Call<SessionModel> call, Response<SessionModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            mResInterface.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            mResInterface.retrofitOnSessionError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<SessionModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        mResInterface.retrofitOnFailure();
                    }
                });

    }

    public void callCreatePushToken(final MyFireBaseInstanceIdService context, final PushTokenModel pushTokenModel, final int responseType) {

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callCreatePushToken(pushTokenModel)
                .enqueue(new Callback<PushTokenModel>() {
                    @Override
                    public void onResponse(Call<PushTokenModel> call, Response<PushTokenModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            context.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            context.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PushTokenModel> call, Throwable t) {
                        context.retrofitOnFailure();
                    }
                });

    }


    public void callUpdatePushToken(final MyFireBaseInstanceIdService context, final PushTokenModel pushTokenModel, final String filter, final int responseType) {

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callUpdatePushToken(filter, pushTokenModel)
                .enqueue(new Callback<PushTokenModel>() {
                    @Override
                    public void onResponse(Call<PushTokenModel> call, Response<PushTokenModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            context.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            context.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PushTokenModel> call, Throwable t) {
                        context.retrofitOnFailure();
                    }
                });

    }

    public void callDeletePushToken(final BaseActivity activity, final String filter, final int responseType) {
        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callDeletePushToken(filter)
                .enqueue(new Callback<PushTokenModel>() {
                    @Override
                    public void onResponse(Call<PushTokenModel> call, Response<PushTokenModel> response) {

                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PushTokenModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callCreateProfile(final BaseActivity activity, JsonObject mJsonObject1, final int responseType) {
        activity.sysOut("API-INPUT: " + mJsonObject1.toString());
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROFILES);

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callCreateProfile(mJsonObject1)
                .enqueue(new Callback<ProfileModel>() {

                    @Override
                    public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callUpdateProfile(final BaseActivity activity, JsonArray jsonArray, final int responseType) {

        String mFields = "*";

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callUpdateProfile(mFields, APIConstants.mProfileRelated, jsonArray)
                .enqueue(new Callback<ProfileModel>() {

                    @Override
                    public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void callGetProfiles(final BaseActivity activity, String filter, final int responseType) {
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROFILES + "?filter=" +
                filter + "&related=" + APIConstants.PROFILE_RELATION_ALL);

        //  DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetProfiles(filter, APIConstants.PROFILE_RELATION_ALL)
                .enqueue(new Callback<ProfileModel>() {

                    @Override
                    public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                        // DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message(),responseType);
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileModel> call, Throwable t) {
                        //  DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }


    public void callGetProfilesWithoutBuffering(final BaseActivity activity, String filter, final int responseType) {
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROFILES + "?filter=" +
                filter + "&related=" + APIConstants.PROFILE_RELATION_ALL);
        //DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetProfiles(filter, APIConstants.PROFILE_RELATION_ALL)
                .enqueue(new Callback<ProfileModel>() {

                    @Override
                    public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                        //DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileModel> call, Throwable t) {
                        //DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callfollowfollowerscount(final BaseActivity activity, String filter, final int querytype) {
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callFollowingfollowerscount(filter, true)
                .enqueue(new Callback<FollowProfileModel1>() {
                    @Override
                    public void onResponse(Call<FollowProfileModel1> call, Response<FollowProfileModel1> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, querytype);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<FollowProfileModel1> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void callGetProfiles(final BaseFragment activity, String filter, final int responseType) {
        DialogManager.showProgress(activity.getActivity());
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetProfiles(filter, APIConstants.PROFILE_RELATION_ALL)
                .enqueue(new Callback<ProfileModel>() {

                    @Override
                    public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure(responseType);
                    }
                });

    }

    public void callGetProfilesWithPushToken(final BaseActivity activity, String filter, final int responseType) {
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROFILES + "?filter=" +
                filter + "&related=" + APIConstants.PROFILE_RELATION_PUSH);
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetProfiles(filter, APIConstants.PROFILE_RELATION_PUSH)
                .enqueue(new Callback<ProfileModel>() {

                    @Override
                    public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callGetProfilesWithFollowBlock(final BaseActivity activity, String filter, final int responseType) {
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROFILES + "?filter=" +
                filter + "&related=" + APIConstants.PROFILE_RELATION_FOLLOW_BLOCK);
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetProfiles(filter, APIConstants.PROFILE_RELATION_FOLLOW_BLOCK)
                .enqueue(new Callback<ProfileModel>() {

                    @Override
                    public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callGetSearchProfiles(final BaseActivity activity, String filter, final int responseType, int limit, int offset) {
        String mOrder = APIConstants.Priority + " ASC";
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROFILES + "?filter=" +
                filter + "&related=" + APIConstants.PROFILE_RELATION_FOLLOW + "&limit=" + limit + "&offset=" + offset + "&order=" + mOrder);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callAllOtherSearchProfiles(filter, APIConstants.PROFILE_RELATION_FOLLOW, limit, offset, true)
                .enqueue(new Callback<ProfileModel>() {

                    @Override
                    public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileModel> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void callAllOtherProfiles(final BaseActivity activity, String filter, final int responseType, int limit, int offset) {
        String mOrder = APIConstants.Priority + " ASC";
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROFILES + "?filter=" +
                filter + "&related=" + APIConstants.PROFILE_RELATION_ALL + "&limit=" + limit + "&offset=" + offset);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callAllOtherProfiles(filter, APIConstants.PROFILE_RELATION_FOLLOW, limit, offset, mOrder, true)
                .enqueue(new Callback<ProfileModel>() {

                    @Override
                    public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {

                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message(),responseType);
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileModel> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void callAllOtherProfiles1(final BaseActivity activity, String filter, final int responseType, int limit, int offset, boolean isLoading) {
        String mOrder = APIConstants.Priority + " ASC";
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROFILES + "?filter=" +
                filter + "&related=" + APIConstants.PROFILE_RELATION_ALL + "&limit=" + limit + "&offset=" + offset);
        if (!isLoading)
            DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callAllOtherProfiles(filter, APIConstants.PROFILE_RELATION_FOLLOW, limit, offset, mOrder, true)
                .enqueue(new Callback<ProfileModel>() {

                    @Override
                    public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void callForgotPassword(final BaseActivity mActivity, JsonObject mInputObj) {
        mActivity.sysOut("API-INPUT: " + mInputObj.toString());
        mActivity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + "user/password?reset=true");
        DialogManager.showProgress(mActivity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callForgotPassword(mInputObj).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                DialogManager.hideProgress();
                if (response.body() != null) {
                    Object mObj = response.body();
                    mActivity.retrofitOnResponse(mObj, 0);
                } else {
                    ErrorMessage mErrorMessage = getErrorMessage(response);
                    mActivity.onRequestError(mErrorMessage);
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                DialogManager.hideProgress();
                mActivity.retrofitOnFailure();
            }
        });
    }

    public void callResetPassword(final BaseActivity mActivity, JsonObject mInputObj) {
        mActivity.sysOut("API-INPUT: " + mInputObj.toString());
        mActivity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + "user/password");
        DialogManager.showProgress(mActivity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callResetPassword(mInputObj).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                DialogManager.hideProgress();
                if (response.body() != null) {
                    Object mObj = response.body();
                    mActivity.retrofitOnResponse(mObj, 0);
                } else if (response.errorBody() != null) {
                    ErrorMessage mErrorMessage = getErrorMessage(response);
                    mActivity.onRequestError(mErrorMessage);
                } else {
                    mActivity.retrofitOnFailure();
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                DialogManager.hideProgress();
                mActivity.retrofitOnFailure();
            }
        });
    }

    public void callGetPromoters(final BaseActivity activity, String filter, final int responseType) {

        String mOrder = APIConstants.Priority + " ASC";

        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROMOTERS + "?filter=" +
                filter);
        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetPromotersList(filter, mOrder, APIConstants.PROMOTER_RELATION)
                .enqueue(new Callback<PromotersModel>() {
                    @Override
                    public void onResponse(Call<PromotersModel> call, Response<PromotersModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromotersModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    //TODO this is for getting followers count -----RAMESH
    public void callGetPromotersFollowers(final BaseActivity activity, String filter, final int responseType) {

        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROMOTERS + "?filter=" +
                filter);
        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetPromotersFollowers(filter, 1, true)
                .enqueue(new Callback<PromotersFollowers1>() {
                    @Override
                    public void onResponse(Call<PromotersFollowers1> call, Response<PromotersFollowers1> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromotersFollowers1> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    //TODO for check the follower or not --- RAMESH
    public void callCheckFollowers(final BaseActivity activity, String filter, final int responseType) {

        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROMOTERS + "?filter=" +
                filter);
        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callCheckFollowers(filter, 1, true)
                .enqueue(new Callback<PromotersFollowers1>() {
                    @Override
                    public void onResponse(Call<PromotersFollowers1> call, Response<PromotersFollowers1> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            String data = new Gson().toJson(response.body());
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromotersFollowers1> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callGetPromotersList(final BaseActivity mActivity, final int responseType) {

        mActivity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROMOTERS);
        DialogManager.showProgress(mActivity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callGetPromotersList().enqueue(new Callback<PromotersModel>() {
            @Override
            public void onResponse(Call<PromotersModel> call, Response<PromotersModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    mActivity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    mActivity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<PromotersModel> call, Throwable t) {
                DialogManager.hideProgress();
                mActivity.retrofitOnFailure();
            }
        });

    }

    public void callGetPromotersList(final AppDialogFragment fragment, String filter, final int responseType) {
        ((BaseActivity) fragment.getContext()).sysOut("API-TYPE: " + "GET");
        ((BaseActivity) fragment.getContext()).sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROMOTERS + "?filter=" +
                filter);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callGetPromotersList(filter).enqueue(new Callback<PromotersModel>() {
            @Override
            public void onResponse(Call<PromotersModel> call, Response<PromotersModel> response) {

                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    fragment.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    fragment.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<PromotersModel> call, Throwable t) {
                fragment.retrofitOnFailure();
            }
        });

    }

    public void callFollowPromoter(final Context context, JsonArray mJsonArray, final int responseType) {
        String input = new Gson().toJson(mJsonArray);
        ((BaseActivity) context).sysOut("API-INPUT: " + input);
        ((BaseActivity) context).sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROMOTER_FOLLOWERS);
        String mFields = "*";
        DialogManager.showProgress(context);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callFollowPromoter
                (mFields, mJsonArray)
                .enqueue(new Callback<PromoterFollowerModel>() {
                    @Override
                    public void onResponse(Call<PromoterFollowerModel> call, Response<PromoterFollowerModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            ((BaseActivity) context).retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            ((BaseActivity) context).retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromoterFollowerModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        ((BaseActivity) context).retrofitOnFailure();
                    }
                });
    }

    public void callUnFollowPromoter(final BaseActivity activity, String filter, final int responseType) {
        String mFields = "*";
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callUnFollowPromoter(mFields, filter)
                .enqueue(new Callback<PromoterFollowerModel>() {
                    @Override
                    public void onResponse(Call<PromoterFollowerModel> call, Response<PromoterFollowerModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromoterFollowerModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }


    public void callUploadProfileCoverImg(final BaseActivity activity, MultipartBody.Part imgPart, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postProfileCoverImgFile(imgPart).enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callUploadProfileImg(final BaseActivity activity, MultipartBody.Part imgPart, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postProfileImgFile(imgPart).enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callUploadProfilePostImg(final BaseActivity activity, MultipartBody.Part imgPart, final int responseType) {
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postProfilePostImgFile(imgPart).enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callUploadCommentReplyPostImg(final BaseActivity activity, MultipartBody.Part imgPart, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postCommentReplyImgFile(imgPart).enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }

            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });

    }

    public void callUploadGrpChatImg(final BaseActivity activity, MultipartBody.Part imgPart, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postGrpChatImgFile(imgPart).enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callCreateProfilePosts(final Context context, JsonArray jsonArray, final int responseType) {

        String mFields = "*";

        DialogManager.showProgress(context);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callCreateProfilePosts(mFields, APIConstants.POST_FEED_RELATION, jsonArray)
                .enqueue(new Callback<PostsModel>() {
                    @Override
                    public void onResponse(Call<PostsModel> call, Response<PostsModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            ((BaseActivity) context).retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            ((BaseActivity) context).retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PostsModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        ((BaseActivity) context).retrofitOnFailure();
                    }
                });

    }

    public void callCreateProfilePosts(final BaseFragment context, JsonArray jsonArray, final int responseType) {

        String mFields = "*";

        DialogManager.showProgress(context.getActivity());

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callCreateProfilePosts(mFields, APIConstants.POST_FEED_RELATION, jsonArray)
                .enqueue(new Callback<PostsModel>() {
                    @Override
                    public void onResponse(Call<PostsModel> call, Response<PostsModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            context.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            context.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PostsModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        context.retrofitOnFailure();
                    }
                });

    }

    public void callUpdateProfilePosts(final BaseActivity activity, JsonArray jsonArray, final int responseType) {

        String mFields = "*";

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callUpdateProfilePosts(mFields, APIConstants.POST_FEED_RELATION, jsonArray)
                .enqueue(new Callback<PostsModel>() {
                    @Override
                    public void onResponse(Call<PostsModel> call, Response<PostsModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PostsModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callDeleteProfilePosts(final BaseActivity activity, int postID, final int responseType) {

        String mFilter = "ID=" + postID;

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callDeleteProfilePost(mFilter)
                .enqueue(new Callback<PostsModel>() {
                    @Override
                    public void onResponse(Call<PostsModel> call, Response<PostsModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PostsModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callGetProfilePosts(final BaseActivity activity, String filter, final int queryType, int limit, int offset) {

        String mOrderBy = "CreatedAt DESC";
        // DialogManager.showProgress(activity);
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.POSTS + "?filter=" +
                filter + "&related=" + APIConstants.POST_FEED_RELATION + "&order=" + mOrderBy + "&limit=" + limit + "&offset=" + offset);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetProfilePosts(filter, APIConstants.POST_FEED_RELATION, mOrderBy, limit, offset, true)
                .enqueue(new Callback<PostsModel>() {
                    @Override
                    public void onResponse(Call<PostsModel> call, Response<PostsModel> response) {
                        if (response.isSuccessful()) {
                            //  DialogManager.hideProgress();
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, queryType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PostsModel> call, Throwable t) {
                        //  DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callGetProfilePosts(final BaseActivity activity, final int queryType, int limit, int offset) {

        String mOrderBy = "CreatedAt DESC";
        String mFilter = "(ReportStatus == false) AND (user_type != club_user)";
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.POSTS + "&related=" + APIConstants.POST_FEED_RELATION + "&order=" + mOrderBy + "&limit=" + limit + "&offset=" + offset);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetProfilePosts(mFilter, APIConstants.POST_FEED_RELATION, mOrderBy, limit, offset, true)
                .enqueue(new Callback<PostsModel>() {
                    @Override
                    public void onResponse(Call<PostsModel> call, Response<PostsModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, queryType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PostsModel> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callGetEvents(final BaseActivity activity, String dateFilter, final int responseType) {

        DialogManager.showProgress(activity);
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.EVENTS + "?filter=" +
                dateFilter + "&related=" + APIConstants.EVENT_RELATION);

        String mOrderBy = "Date ASC";
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetEvents(dateFilter, APIConstants.EVENT_RELATION, mOrderBy)
                .enqueue(new Callback<EventsModel>() {
                    @Override
                    public void onResponse(Call<EventsModel> call, Response<EventsModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<EventsModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callGetEventsWhoIsGoing(final BaseActivity activity, String filter, final int queryType) {

        String mRelated = EventsWhoIsGoingModel.PROFILES_BY_PROFILE_ID;

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetEventsWhoIsGoing(filter, mRelated)
                .enqueue(new Callback<EventsWhoIsGoingModel>() {
                    @Override
                    public void onResponse(Call<EventsWhoIsGoingModel> call, Response<EventsWhoIsGoingModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, queryType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<EventsWhoIsGoingModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callBookNowEvent(final BaseActivity activity, JsonArray jsonArray, final int responseType) {

        String mFields = "*";

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callBookAnEvent(mFields, jsonArray)
                .enqueue(new Callback<EventsWhoIsGoingModel>() {
                    @Override
                    public void onResponse(Call<EventsWhoIsGoingModel> call, Response<EventsWhoIsGoingModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<EventsWhoIsGoingModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure(responseType);
                    }
                });

    }

    public void callGetPushToken(final MyFireBaseInstanceIdService context, String filter, final int responseType) {

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetPushToken(filter)
                .enqueue(new Callback<PushTokenModel>() {
                    @Override
                    public void onResponse(Call<PushTokenModel> call, Response<PushTokenModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            context.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            context.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PushTokenModel> call, Throwable t) {
                        context.retrofitOnFailure();
                    }
                });

    }

    public void callCreateSingleChatRoom(final BaseActivity activity, SingleChatRoomModel singleChatRoomModel, final int responseType) {

        String mFields = "*";

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callCreateSingleChatRoom(mFields, singleChatRoomModel)
                .enqueue(new Callback<SingleChatRoomModel>() {
                    @Override
                    public void onResponse(Call<SingleChatRoomModel> call, Response<SingleChatRoomModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleChatRoomModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });


    }

    public void callGetSingleChatRoom(final BaseActivity activity, String filter, final int responseType) {

        DialogManager.showProgress(activity);
        String mOrderBy = "RecentUserChatTime DESC";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetSingleChatRoom(filter, mOrderBy)
                .enqueue(new Callback<SingleChatRoomModel>() {
                    @Override
                    public void onResponse(Call<SingleChatRoomModel> call, Response<SingleChatRoomModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleChatRoomModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callSendSingleChatMsg(final Context context, JsonArray jsonArray, final int responseType) {

        String mFields = "*";

        DialogManager.showProgress(context);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callSendSingleChatMsg(mFields, jsonArray)
                .enqueue(new Callback<SingleChatMsgModel>() {
                    @Override
                    public void onResponse(Call<SingleChatMsgModel> call, Response<SingleChatMsgModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            ((BaseActivity) context).retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            ((BaseActivity) context).retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleChatMsgModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        ((BaseActivity) context).retrofitOnFailure();
                    }
                });

    }

    public void callGetSingleChatMsg(final BaseActivity activity, String filter, final int responseType, int limit, int offset) {

        String mOrderBy = "CreatedAt DESC";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetSingleChatMsg(filter, mOrderBy, limit, offset, true)
                .enqueue(new Callback<SingleChatMsgModel>() {
                    @Override
                    public void onResponse(Call<SingleChatMsgModel> call, Response<SingleChatMsgModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleChatMsgModel> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callSendEventGrpChatMsg(final BaseActivity activity, EventGrpChatMsgModel eventGrpChatMsgModel, final int responseType) {

        String mFields = "*";

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callSendEventGrpChatMsg(mFields, eventGrpChatMsgModel)
                .enqueue(new Callback<EventGrpChatMsgModel>() {
                    @Override
                    public void onResponse(Call<EventGrpChatMsgModel> call, Response<EventGrpChatMsgModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<EventGrpChatMsgModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }


    public void callSendLiveEventGrpChatMsg(final BaseActivity activity, EventGrpChatMsgModel eventGrpChatMsgModel, final int responseType) {

        String mFields = "*";

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callSendLiveEventGrpChatMsg(mFields, eventGrpChatMsgModel)
                .enqueue(new Callback<EventGrpChatMsgModel>() {
                    @Override
                    public void onResponse(Call<EventGrpChatMsgModel> call, Response<EventGrpChatMsgModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<EventGrpChatMsgModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void setActiveUserCount(final BaseActivity activity, JsonArray jsonArray, final int responseType) {
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().setAvticeUsersCount(jsonArray)
                .enqueue(new Callback<ProfileModel>() {
                    @Override
                    public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileModel> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void CallSetMsgStatus(final BaseActivity activity, String filter, JsonArray jsonArray, final int responseType) {
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callSetMsgStatus(filter, jsonArray)
                .enqueue(new Callback<SingleChatMsgModel>() {
                    @Override
                    public void onResponse(Call<SingleChatMsgModel> call, Response<SingleChatMsgModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleChatMsgModel> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void CallGetSingleChatCount(final BaseActivity activity, String filter, final int responseType) {

        String group = "messagestatus,FromProfileID";
        String fields = "Count,ID,FromProfileID,ToProfileID";

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callGetSingleChatUnreadCount(filter, group, fields)
                .enqueue(new Callback<SingleChatCountModel>() {
                    @Override
                    public void onResponse(Call<SingleChatCountModel> call, Response<SingleChatCountModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleChatCountModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void CallGetSingleChatUnreadMsg(final BaseActivity activity, String filter, final int responseType) {

        String related = "singlechatmsg_by_Order";
        String group = "ChatRelation";
        String fields = "Order,ID";

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callGetSingleChatUnreadMsg(filter, related, group, fields)
                .enqueue(new Callback<SingleChatUnreadMsgModel>() {
                    @Override
                    public void onResponse(Call<SingleChatUnreadMsgModel> call, Response<SingleChatUnreadMsgModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleChatUnreadMsgModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void callGetEventGrpChatMsg(final BaseActivity activity, String filter, final int responseType, int limit, int offset) {

        String mRelated = EventGrpChatMsgModel.PROFILES_BY_SENDER_PROFILE_ID + "," +
                EventGrpChatMsgModel.PROMOTER_BY_SENDER_USER_ID;

        String mOrderBy = "CreatedAt DESC";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetEventGrpChatMsg(filter, mRelated, mOrderBy, limit, offset, true)
                .enqueue(new Callback<EventGrpChatMsgModel>() {
                    @Override
                    public void onResponse(Call<EventGrpChatMsgModel> call, Response<EventGrpChatMsgModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<EventGrpChatMsgModel> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });

    }


    public void callGetLiveEventGrpChatMsg(final BaseActivity activity, String filter, final int responseType, int limit, int offset) {

        String mRelated = EventGrpChatMsgModel.PROFILES_BY_SENDER_PROFILE_ID + "," +
                EventGrpChatMsgModel.PROMOTER_BY_SENDER_USER_ID;

        String mOrderBy = "CreatedAt DESC";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetLiveEventGrpChatMsg(filter, mRelated, mOrderBy, limit, offset, true)
                .enqueue(new Callback<EventGrpChatMsgModel>() {
                    @Override
                    public void onResponse(Call<EventGrpChatMsgModel> call, Response<EventGrpChatMsgModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<EventGrpChatMsgModel> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });

    }


    public void callGetLiveEventPromoterGrpChatMsg(final BaseActivity activity, String filter, final int responseType) {

        String mRelated = EventGrpChatMsgModel.PROFILES_BY_SENDER_PROFILE_ID + "," +
                EventGrpChatMsgModel.PROMOTER_BY_SENDER_USER_ID;

        String mOrderBy = "CreatedAt DESC";
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetLiveEventPromoterGrpChatMsg(filter, mRelated, mOrderBy)
                .enqueue(new Callback<EventGrpChatMsgModel>() {
                    @Override
                    public void onResponse(Call<EventGrpChatMsgModel> call, Response<EventGrpChatMsgModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<EventGrpChatMsgModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }


    public void callCreateGrpChatRoom(final BaseActivity activity, GroupChatRoomModel groupChatRoomModel, final int responseType) {

        String mFields = "*";

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callCreateGrpChatRoom(mFields, groupChatRoomModel)
                .enqueue(new Callback<GroupChatRoomModel>() {
                    @Override
                    public void onResponse(Call<GroupChatRoomModel> call, Response<GroupChatRoomModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GroupChatRoomModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callGetGrpChatRoom(final BaseActivity activity, String filter, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetGrpChatRoom(filter)
                .enqueue(new Callback<GroupChatRoomModel>() {
                    @Override
                    public void onResponse(Call<GroupChatRoomModel> call, Response<GroupChatRoomModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GroupChatRoomModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callSendGrpChatMsg(final BaseActivity activity, GroupChatMsgModel groupChatMsgModel, final int responseType) {

        String mFields = "*";

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callSendGrpChatMsg(mFields, groupChatMsgModel)
                .enqueue(new Callback<GroupChatMsgModel>() {
                    @Override
                    public void onResponse(Call<GroupChatMsgModel> call, Response<GroupChatMsgModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GroupChatMsgModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callGetGrpChatMsg(final BaseActivity activity, String filter, final int responseType, int limit, int offset) {

        String mRelated = GroupChatMsgModel.PROFILES_BY_SENDER_PROFILE_ID;

        String mOrderBy = "CreatedAt DESC";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetGrpChatMsg(filter, mRelated, mOrderBy, limit, offset, true)
                .enqueue(new Callback<GroupChatMsgModel>() {
                    @Override
                    public void onResponse(Call<GroupChatMsgModel> call, Response<GroupChatMsgModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GroupChatMsgModel> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callGetNotifications(final BaseActivity activity, String filter, int limit, int offset, final int responseType) {

        String mOrderBy = "Updated DESC";
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetNotifications(filter, mOrderBy, limit, offset, true)
                .enqueue(new Callback<NotificationModel>() {
                    @Override
                    public void onResponse(Call<NotificationModel> call, Response<NotificationModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callGetImageGallery(final BaseActivity activity, String filter, final int
            responseType) {

        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.GALLERY_IMAGE + "?filter=" + filter);
        DialogManager.showProgress(activity);
        String mOrderBy = "CreatedAt DESC";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetImageGallery(filter, mOrderBy)
                .enqueue(new Callback<GalleryImgModel>() {
                    @Override
                    public void onResponse(Call<GalleryImgModel> call, Response<GalleryImgModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GalleryImgModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void postImgToGallery(final BaseActivity activity, JsonArray jsonArray, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().postGalleryImg(jsonArray)
                .enqueue(new Callback<GalleryImgModel>() {
                    @Override
                    public void onResponse(Call<GalleryImgModel> call, Response<GalleryImgModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GalleryImgModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void postVideoToGallery(final BaseActivity activity, JsonArray jsonArray, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().postGalleryVideo(jsonArray)
                .enqueue(new Callback<GalleryImgModel>() {
                    @Override
                    public void onResponse(Call<GalleryImgModel> call, Response<GalleryImgModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GalleryImgModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void postVideoToGallery(final Context mContext, final RetrofitResInterface mRetrofitResInterface,
                                   JsonArray jsonArray, final int responseType) {
        ((BaseActivity) mContext).sysOut("API-INPUT: " + jsonArray.toString());
        ((BaseActivity) mContext).sysOut("API-TYPE: " + "POST");
        ((BaseActivity) mContext).sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.GALLERY_VIDEO);
        DialogManager.showProgress(mContext);

        /*RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().postGalleryVideo(jsonArray)
                .enqueue(new Callback<GalleryImgModel>() {
                    @Override
                    public void onResponse(Call<GalleryImgModel> call, Response<GalleryImgModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            mRetrofitResInterface.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            mRetrofitResInterface.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GalleryImgModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        mRetrofitResInterface.retrofitOnFailure();
                    }
                });*/

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().postGalleryVideo1(jsonArray)
                .enqueue(new Callback<OnDemandVideoUploadedResponse>() {
                    @Override
                    public void onResponse(Call<OnDemandVideoUploadedResponse> call, Response<OnDemandVideoUploadedResponse> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            mRetrofitResInterface.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            mRetrofitResInterface.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<OnDemandVideoUploadedResponse> call, Throwable t) {
                        DialogManager.hideProgress();
                        mRetrofitResInterface.retrofitOnFailure();
                    }
                });

    }

    public void callGetVideoGallery(final BaseActivity activity, String mFilter, final int responseType) {
        DialogManager.showProgress(activity);

        String mOrderBy = "CreatedAt DESC";
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetVideoGallery(mFilter, mOrderBy)
                .enqueue(new Callback<GalleryVideoModel>() {
                    @Override
                    public void onResponse(Call<GalleryVideoModel> call, Response<GalleryVideoModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GalleryVideoModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void getPromoterVideoGallery1(final BaseActivity activity, String mFilter, final int responseType, int datalimit, int offset) {
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.GALLERY_VIDEO + "?filter=" + mFilter);
        DialogManager.showProgress(activity);
        String mOrderBy = "CreatedAt DESC";
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetVideoGallery1(mFilter, mOrderBy, datalimit, offset, true)
                .enqueue(new Callback<GalleryVideoModel>() {
                    @Override
                    public void onResponse(Call<GalleryVideoModel> call, Response<GalleryVideoModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GalleryVideoModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void getPromoterVideoGallery(final BaseActivity activity, String mFilter, final int responseType) {
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.GALLERY_VIDEO + "?filter=" + mFilter);
        DialogManager.showProgress(activity);
        String mOrderBy = "CreatedAt DESC";
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetVideoGallery(mFilter, mOrderBy)
                .enqueue(new Callback<GalleryVideoModel>() {
                    @Override
                    public void onResponse(Call<GalleryVideoModel> call, Response<GalleryVideoModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GalleryVideoModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }


    public void getPromoterVideoGalleryForSpectator(final BaseActivity activity, String mFilter, final int responseType) {
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.GALLERY_VIDEO + "?filter=" + mFilter);
        DialogManager.showProgress(activity);
        String mOrderBy = "CreatedAt DESC";
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetVideoGallery(mFilter, mOrderBy)
                .enqueue(new Callback<GalleryVideoModel>() {
                    @Override
                    public void onResponse(Call<GalleryVideoModel> call, Response<GalleryVideoModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GalleryVideoModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void uploadImageFileToServer(final BaseActivity activity, MultipartBody.Part file,
                                        final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postImageFile(file).enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });

    }

    public void uploadVideoFileToServer(final BaseActivity activity, MultipartBody.Part video, MultipartBody.Part image,
                                        final int responseType) {
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postVideoFile(video, image).enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void getEventSessions(final BaseActivity activity, int eventId, final int responseType) {

        DialogManager.showProgress(activity);

        String filter = "EventID = " + eventId;

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().getEventSessions(filter)
                .enqueue(new Callback<EventCategoryModel>() {
                    @Override
                    public void onResponse(Call<EventCategoryModel> call, Response<EventCategoryModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<EventCategoryModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void callGetAllEventsFromTrack(final BaseActivity activity, String mFilter, final int responseType) {

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callGetAllEventsFromTracks(mFilter, APIConstants.EVENT_RELATION)
                .enqueue(new Callback<EventsModel>() {
                    @Override
                    public void onResponse(Call<EventsModel> call, Response<EventsModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<EventsModel> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });

    }


    public void postPaymentForEventBooking(final BaseActivity activity, String mToken, String mAccNo,
                                           int mAmount, String mType, final int responseType) {
        DialogManager.showProgress(activity);
        RetrofitApiInterface service = RetrofitClient.getRetrofitInstance().getPromoterPaymentInterface();
        Call<PaymentModel> call = service.postPaymentForEventBooking(mToken, mAccNo, mAmount, mType);
        call.enqueue(new Callback<PaymentModel>() {
            @Override
            public void onResponse(Call<PaymentModel> call, Response<PaymentModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }

            }

            @Override
            public void onFailure(Call<PaymentModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure(responseType);
            }

        });

    }


    public void postLikesForPosts(final BaseActivity activity, JsonObject mFeedLikesEntity, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postLikesForPosts("*", mFeedLikesEntity).enqueue(new Callback<FeedLikesModel>() {
            @Override
            public void onResponse(Call<FeedLikesModel> call, Response<FeedLikesModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<FeedLikesModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callPostFeedComments(final BaseActivity activity, JsonObject jsonArray, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callPostFeedComments("*", "profiles_by_ProfileID", jsonArray).enqueue(new Callback<FeedCommentModel>() {
            @Override
            public void onResponse(Call<FeedCommentModel> call, Response<FeedCommentModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<FeedCommentModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callUnLikeForPosts(final BaseActivity activity, String mFilter, final int responseType) {
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postUnLikesForPosts("*", mFilter).enqueue(new Callback<FeedLikesModel>() {
            @Override
            public void onResponse(Call<FeedLikesModel> call, Response<FeedLikesModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<FeedLikesModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callPostShares(final Context context, JsonObject jsonObject, final int responseType) {
        DialogManager.showProgress(context);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callPostShares("*", jsonObject).enqueue(new Callback<FeedShareModel>() {
            @Override
            public void onResponse(Call<FeedShareModel> call, Response<FeedShareModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    ((BaseActivity) context).retrofitOnResponse(mResponseObj, responseType);
                } else {
                    ((BaseActivity) context).retrofitOnError(response.code(), response.message(), responseType);
                }
            }

            @Override
            public void onFailure(Call<FeedShareModel> call, Throwable t) {
                DialogManager.hideProgress();
                ((BaseActivity) context).retrofitOnFailure();
            }
        });
    }

    public void callPostFeedCommentReply(final BaseActivity activity, JsonObject jsonArray, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callPostFeedCommentReply("*", jsonArray).enqueue(new Callback<FeedCommentReplyModel>() {
            @Override
            public void onResponse(Call<FeedCommentReplyModel> call, Response<FeedCommentReplyModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<FeedCommentReplyModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callPostRacingData(final BaseActivity activity, JsonArray mJsonArray, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callPostRacingData(mJsonArray).enqueue(new Callback<RacingModel>() {
            @Override
            public void onResponse(Call<RacingModel> call, Response<RacingModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<RacingModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure(responseType);
            }
        });
    }

    public void callPostAnswerForEventRegistrationQuestions(final BaseActivity activity, JsonArray mJsonArray, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callPostAnswerForEventRegistrationQuestions(mJsonArray).enqueue(new Callback<EventAnswersModel>() {
            @Override
            public void onResponse(Call<EventAnswersModel> call, Response<EventAnswersModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<EventAnswersModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure(responseType);
            }
        });
    }

    public void callUpdateAnswerForEventRegistrationQuestions(final BaseActivity activity, JsonArray mJsonArray, final int responseType) {
        DialogManager.showProgress(activity);

        String mFields = "*";
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callUpdateAnswerForEventRegistrationQuestions(mFields, mJsonArray).enqueue(new Callback<EventAnswersModel>() {
            @Override
            public void onResponse(Call<EventAnswersModel> call, Response<EventAnswersModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<EventAnswersModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure(responseType);
            }
        });
    }

    public void callGetAnswerForEventRegistrationQuestions(final BaseActivity activity, String mFilter, final int responseType) {
        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callGetAnswerForEventRegistrationQuestions(mFilter).enqueue(new Callback<EventAnswersModel>() {
            @Override
            public void onResponse(Call<EventAnswersModel> call, Response<EventAnswersModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<EventAnswersModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure(responseType);
            }
        });
    }

    public void callGetAllStreamUserProfile(final BaseActivity activity, String mFilter) {
        String mRelated = APIConstants.livestreamrequest_by_ReceiverProfileID + "," + APIConstants.livestreamrequest_by_RequestedProfileID;
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROFILES + "?filter=" +
                mFilter);
        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetProfiles(mFilter, mRelated)
                .enqueue(new Callback<ProfileModel>() {
                    @Override
                    public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, 0);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }

                });
    }

    public void callSendStreamRequest(final Context context, final RetrofitResInterface mResInterface, JsonArray mJsonArray) {
        String mFields = "*";
        ((BaseActivity) context).sysOut("API-TYPE: " + "POST");
        ((BaseActivity) context).sysOut("API-INPUT: " + mJsonArray.toString());
        ((BaseActivity) context).sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.LIVE_STREAM_REQUEST + "?fields=" + mFields);
        DialogManager.showProgress(context);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callSendStreamRequest(mJsonArray, mFields)
                .enqueue(new Callback<LiveStreamRequestResponse>() {
                    @Override
                    public void onResponse(Call<LiveStreamRequestResponse> call, Response<LiveStreamRequestResponse> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            mResInterface.retrofitOnResponse(mResponseObj, 0);
                        } else {
                            mResInterface.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<LiveStreamRequestResponse> call, Throwable t) {
                        DialogManager.hideProgress();
                        mResInterface.retrofitOnFailure();
                    }

                });
    }

    public void callGetRequestedUsersList(final BaseActivity activity, String mFilter) {
        String mRelated = APIConstants.profiles_by_RequestedProfileID;
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.LIVE_STREAM_REQUEST + "?filter=" +
                mFilter);
        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetRequestedUsersList(mFilter, mRelated)
                .enqueue(new Callback<LiveStreamRequestResponse>() {
                    @Override
                    public void onResponse(Call<LiveStreamRequestResponse> call, Response<LiveStreamRequestResponse> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, 0);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<LiveStreamRequestResponse> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }

                });
    }

    public void callAcceptStreamRequest(final Context context, final RetrofitResInterface mResInterface, JsonArray mJsonArray) {
        String mFields = "*";
        ((BaseActivity) context).sysOut("API-TYPE: " + "PATCH");
        ((BaseActivity) context).sysOut("API-INPUT: " + mJsonArray.toString());
        ((BaseActivity) context).sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.LIVE_STREAM_REQUEST + "?fields=" + mFields);
        DialogManager.showProgress(context);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callAcceptStreamRequest(mJsonArray, mFields)
                .enqueue(new Callback<LiveStreamRequestResponse>() {
                    @Override
                    public void onResponse(Call<LiveStreamRequestResponse> call, Response<LiveStreamRequestResponse> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            mResInterface.retrofitOnResponse(mResponseObj, 0);
                        } else {
                            mResInterface.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<LiveStreamRequestResponse> call, Throwable t) {
                        DialogManager.hideProgress();
                        mResInterface.retrofitOnFailure();
                    }

                });
    }

    public void callDeclineStreamRequest(final Context context, final RetrofitResInterface mResInterface, String mFilter) {
        String mFields = "*";
        ((BaseActivity) context).sysOut("API-TYPE: " + "DELETE");
        ((BaseActivity) context).sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.LIVE_STREAM_REQUEST + "?fields=" + mFields);
        DialogManager.showProgress(context);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callDeclineStreamRequest(mFields, mFilter)
                .enqueue(new Callback<LiveStreamRequestResponse>() {
                    @Override
                    public void onResponse(Call<LiveStreamRequestResponse> call, Response<LiveStreamRequestResponse> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            mResInterface.retrofitOnResponse(mResponseObj, 0);
                        } else {
                            mResInterface.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<LiveStreamRequestResponse> call, Throwable t) {
                        DialogManager.hideProgress();
                        mResInterface.retrofitOnFailure();
                    }

                });
    }

    public void callPostLiveStream(final Context context, final RetrofitResInterface mResInterface, JsonArray mJsonArray) {
        String mFields = "*";
        ((BaseActivity) context).sysOut("API-TYPE: " + "POST");
        ((BaseActivity) context).sysOut("API-INPUT: " + mJsonArray.toString());
        ((BaseActivity) context).sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.LIVE_STREAM + "?fields=" + mFields);

        DialogManager.showProgress(context);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().
                callPostLiveStream(mFields, mJsonArray)
                .enqueue(new Callback<LiveStreamResponse>() {
                    @Override
                    public void onResponse(Call<LiveStreamResponse> call, Response<LiveStreamResponse> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            mResInterface.retrofitOnResponse(mResponseObj, 0);
                        } else {
                            mResInterface.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<LiveStreamResponse> call, Throwable t) {
                        DialogManager.hideProgress();
                        mResInterface.retrofitOnFailure();
                    }
                });
    }

    public void callPostLiveStream(final BaseActivity activity, JsonArray mJsonArray) {
        String mFields = "*";
        activity.sysOut("API-TYPE: " + "POST");
        activity.sysOut("API-INPUT: " + mJsonArray.toString());
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.LIVE_STREAM + "?fields=" + mFields);
        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().
                callPostLiveStream(mFields, mJsonArray)
                .enqueue(new Callback<LiveStreamResponse>() {
                    @Override
                    public void onResponse(Call<LiveStreamResponse> call, Response<LiveStreamResponse> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, 0);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<LiveStreamResponse> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }

                });
    }

    public void callDeleteLiveStream(final Context context, final RetrofitResInterface mResInterface, String mFilter) {
        String mFields = "*";
        ((BaseActivity) context).sysOut("API-TYPE: " + "DELETE");
        ((BaseActivity) context).sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.LIVE_STREAM + "?fields=" + mFields + "&filter=" + mFilter);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().
                callDeleteLiveStream(mFields, mFilter)
                .enqueue(new Callback<LiveStreamResponse>() {
                    @Override
                    public void onResponse(Call<LiveStreamResponse> call, Response<LiveStreamResponse> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            mResInterface.retrofitOnResponse(mResponseObj, 0);
                        } else {
                            mResInterface.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<LiveStreamResponse> call, Throwable t) {
                        mResInterface.retrofitOnFailure();
                    }

                });
    }

    public void callDeleteLiveStream(final BaseActivity activity, String mFilter) {
        String mFields = "*";
        activity.sysOut("API-TYPE: " + "DELETE");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.LIVE_STREAM + "?fields=" + mFields + "&filter=" + mFilter);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().
                callDeleteLiveStream(mFields, mFilter)
                .enqueue(new Callback<LiveStreamResponse>() {
                    @Override
                    public void onResponse(Call<LiveStreamResponse> call, Response<LiveStreamResponse> response) {
                        // DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, 0);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<LiveStreamResponse> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }

                });
    }

    public void callGetFriendsStream(final BaseActivity activity, String mFilter) {
        String mRelated = APIConstants.profiles_by_StreamProfileID + "," + APIConstants.promoter_by_StreamProfileID;
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.LIVE_STREAM + "?filter=" +
                mFilter + "&related=" + mRelated);
        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetFriendsStream(mFilter, mRelated)
                .enqueue(new Callback<LiveStreamResponse>() {
                    @Override
                    public void onResponse(Call<LiveStreamResponse> call, Response<LiveStreamResponse> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, 0);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<LiveStreamResponse> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }

                });
    }

    public void callDeleteSharedPost(final BaseActivity activity, String postID, final int responseType) {
        String mFilter = "PostID=" + postID;

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callDeleteSharedPost(mFilter)
                .enqueue(new Callback<FeedShareModel>() {
                    @Override
                    public void onResponse(Call<FeedShareModel> call, Response<FeedShareModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<FeedShareModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void callGetStreamPromoters(final BaseActivity activity, String mFilter) {
        String mRelated = APIConstants.livestream_by_StreamProfileID + "," + APIConstants.livestreampayment_by_PromoterID;
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROMOTERS + "?filter=" +
                mFilter + "&related=" + mRelated);
        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetStreamPromoters(mFilter, mRelated)
                .enqueue(new Callback<PromotersModel>() {
                    @Override
                    public void onResponse(Call<PromotersModel> call, Response<PromotersModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, 0);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromotersModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void postPayForViewLiveStream(final Context context, final RetrofitResInterface mResInterface, String mToken, String mAccNo,
                                         int mAmount, String mType) {

        DialogManager.showProgress(context);

        RetrofitApiInterface service = RetrofitClient.getRetrofitInstance().getPromoterPaymentInterface();

        Call<PaymentModel> call = service.postPayForViewLiveStream(mToken, mAccNo, mAmount, mType);

        call.enqueue(new Callback<PaymentModel>() {
            @Override
            public void onResponse(Call<PaymentModel> call, Response<PaymentModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    mResInterface.retrofitOnResponse(mResponseObj, 0);
                } else {
                    mResInterface.retrofitOnError(response.code(), response.message());
                }

            }

            @Override
            public void onFailure(Call<PaymentModel> call, Throwable t) {
                DialogManager.hideProgress();
                mResInterface.retrofitOnFailure();
            }

        });

    }

    public void callUpdateLiveStreamPayment(final Context context, final RetrofitResInterface mResInterface, JsonArray mJsonArray) {
        String mFields = "*";
        ((BaseActivity) context).sysOut("API-TYPE: " + "POST");
        ((BaseActivity) context).sysOut("API-INPUT: " + mJsonArray.toString());
        ((BaseActivity) context).sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.LIVE_STREAM_PAYMENT + "?fields=" + mFields);
        DialogManager.showProgress(context);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callUpdateLiveStreamPayment(mJsonArray, mFields)
                .enqueue(new Callback<LiveStreamPaymentResponse>() {
                    @Override
                    public void onResponse(Call<LiveStreamPaymentResponse> call, Response<LiveStreamPaymentResponse> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            mResInterface.retrofitOnResponse(mResponseObj, 0);
                        } else {
                            mResInterface.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<LiveStreamPaymentResponse> call, Throwable t) {
                        DialogManager.hideProgress();
                        mResInterface.retrofitOnFailure();
                    }

                });
    }

    public void postSubScribeRequestToClub(final BaseActivity activity, String plan, String email, String token, String type, String account, int subscription_fee, final int responseType) {

        DialogManager.showProgress(activity);
        RetrofitApiInterface service = RetrofitClient.getRetrofitInstance().getPromoterPaymentInterface();
        Call<PaymentModel> call = service.postSubscribeRequestToClub(plan, email, token, type, account, subscription_fee);
        call.enqueue(new Callback<PaymentModel>() {
            @Override
            public void onResponse(Call<PaymentModel> call, Response<PaymentModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }

            }

            @Override
            public void onFailure(Call<PaymentModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure(responseType);
            }

        });

    }


    public void postUnSubScribeRequestToClub(final BaseActivity activity, String subsId, String type, String accessToken, final int responseType) {


        DialogManager.showProgress(activity);
        RetrofitApiInterface service = RetrofitClient.getRetrofitInstance().getPromoterPaymentInterface();
        Call<ResponseBody> call = service.postUnSubscribeRequestToClub(subsId, type, accessToken);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure(responseType);
            }

        });

    }

    public void postLikesForComments(final BaseActivity activity, JsonObject mJsonObject, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postLikesForComments("*", mJsonObject).enqueue(new Callback<FeedCommentLikeModel>() {
            @Override
            public void onResponse(Call<FeedCommentLikeModel> call, Response<FeedCommentLikeModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<FeedCommentLikeModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callUnLikeForComments(final BaseActivity activity, int filter, final int responseType) {

        DialogManager.showProgress(activity);

        String mFilter = "ID=" + filter;

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postUnLikesForComments(mFilter).enqueue(new Callback<FeedCommentLikeModel>() {
            @Override
            public void onResponse(Call<FeedCommentLikeModel> call, Response<FeedCommentLikeModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<FeedCommentLikeModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });

    }


    public void callUnSubScribeClub(final BaseActivity activity, String filter, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callUnSubScribeClub(filter).enqueue(new Callback<ClubGroupModel>() {
            @Override
            public void onResponse(Call<ClubGroupModel> call, Response<ClubGroupModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<ClubGroupModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });

    }

    public void postLikeForVehicleInfo(final BaseActivity activity, JsonArray mJsonObject, final int responseType) {
        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postLikeForVehicleInfo("*", mJsonObject).enqueue(new Callback<VehicleInfoLikeModel>() {
            @Override
            public void onResponse(Call<VehicleInfoLikeModel> call, Response<VehicleInfoLikeModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<VehicleInfoLikeModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callUnLikeForVehicleInfo(final BaseActivity activity, int filter, final int responseType) {

        DialogManager.showProgress(activity);

        String mFilter = "ID=" + filter;

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postUnLikesForVehicleInfo(mFilter).enqueue(new Callback<VehicleInfoLikeModel>() {
            @Override
            public void onResponse(Call<VehicleInfoLikeModel> call, Response<VehicleInfoLikeModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<VehicleInfoLikeModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });

    }

    public void callGetEventAddOn(final BaseActivity activity, String mFilter, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callGetEventAddOn(mFilter).enqueue(new Callback<EventAddOnModel>() {
            @Override
            public void onResponse(Call<EventAddOnModel> call, Response<EventAddOnModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<EventAddOnModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callPostPurchasedAddOn(final BaseActivity activity, JsonArray mJsonArray, final int responseType) {
        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callPostPurchasedAddOn(mJsonArray, "*").enqueue(new Callback<PurchasedAddOnModel>() {
            @Override
            public void onResponse(Call<PurchasedAddOnModel> call, Response<PurchasedAddOnModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<PurchasedAddOnModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callGetComments(final BaseActivity activity, String mFilter, final int responseType) {

        DialogManager.showProgress(activity);

        String mOrderBy = "CreateTime DESC";

        String mRelation = FeedCommentModel.COMMENT_LIKES_BY_COMMENT_ID + "," + FeedCommentModel.COMMENT_REPLY_BY_COMMENT_ID;

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callGetComments(mFilter, mRelation, mOrderBy).enqueue(new Callback<FeedCommentModel>() {
            @Override
            public void onResponse(Call<FeedCommentModel> call, Response<FeedCommentModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<FeedCommentModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void postLikesForReply(final BaseActivity activity, JsonArray mJsonArray, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postLikesForReply("*", mJsonArray).enqueue(new Callback<ReplyLikeModel>() {
            @Override
            public void onResponse(Call<ReplyLikeModel> call, Response<ReplyLikeModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<ReplyLikeModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callUnLikeReply(final BaseActivity activity, int filter, final int responseType) {
        DialogManager.showProgress(activity);

        String mFilter = "ID=" + filter;


        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postUnLikesForReply(mFilter).enqueue(new Callback<ReplyLikeModel>() {
            @Override
            public void onResponse(Call<ReplyLikeModel> call, Response<ReplyLikeModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<ReplyLikeModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });

    }

    public void callGetCommentsReply(final BaseActivity activity, String mFilter, final int responseType) {
        DialogManager.showProgress(activity);

        String mOrderBy = "CreateTime DESC";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callGetFeedCommentReply(mFilter, APIConstants.mPostCommentReplyRelated, mOrderBy).enqueue(new Callback<FeedCommentReplyModel>() {
            @Override
            public void onResponse(Call<FeedCommentReplyModel> call, Response<FeedCommentReplyModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<FeedCommentReplyModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callFollowProfile(final Context mContext, final RetrofitResInterface mResInterface,
                                  JsonArray mJsonArray, final int responseType) {
        String mFields = "*";
        ((BaseActivity) mContext).sysOut("API-TYPE: " + "POST");
        ((BaseActivity) mContext).sysOut("API-INPUT: " + mJsonArray.toString());
        ((BaseActivity) mContext).sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.FOLLOW_PROFILE + "?fields=" + mFields);
        DialogManager.showProgress(mContext);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callFollowProfile(mFields, mJsonArray)
                .enqueue(new Callback<FollowProfileModel>() {
                    @Override
                    public void onResponse(Call<FollowProfileModel> call, Response<FollowProfileModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            mResInterface.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            mResInterface.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<FollowProfileModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        mResInterface.retrofitOnFailure();
                    }

                });
    }

    public void callUnFollowProfile(final Context mContext, final RetrofitResInterface mResInterface, String mFilter, final int responseType) {
        String mFields = "*";
        ((BaseActivity) mContext).sysOut("API-TYPE: " + "DELETE");
        ((BaseActivity) mContext).sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.FOLLOW_PROFILE + "?fields=" + mFields + "&filter=" + mFilter);
        DialogManager.showProgress(mContext);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callUnFollowProfile(mFields, mFilter)
                .enqueue(new Callback<FollowProfileModel>() {
                    @Override
                    public void onResponse(Call<FollowProfileModel> call, Response<FollowProfileModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            mResInterface.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            mResInterface.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<FollowProfileModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        mResInterface.retrofitOnFailure();
                    }

                });
    }

    public void callFollowProfile(final Context mContext, JsonArray mJsonArray, final int responseType) {
        String mFields = "*";
        ((BaseActivity) mContext).sysOut("API-TYPE: " + "POST");
        ((BaseActivity) mContext).sysOut("API-INPUT: " + mJsonArray.toString());
        ((BaseActivity) mContext).sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.FOLLOW_PROFILE + "?fields=" + mFields);
        DialogManager.showProgress(mContext);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callFollowProfile(mFields, mJsonArray)
                .enqueue(new Callback<FollowProfileModel>() {
                    @Override
                    public void onResponse(Call<FollowProfileModel> call, Response<FollowProfileModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            ((BaseActivity) mContext).retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            ((BaseActivity) mContext).retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<FollowProfileModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        ((BaseActivity) mContext).retrofitOnFailure();
                    }
                });
    }

    public void callUnFollowProfile(final Context mContext, String mFilter, final int responseType) {
        String mFields = "*";
        ((BaseActivity) mContext).sysOut("API-TYPE: " + "DELETE");
        ((BaseActivity) mContext).sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.FOLLOW_PROFILE + "?fields=" + mFields + "&filter=" + mFilter);
        DialogManager.showProgress(mContext);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callUnFollowProfile(mFields, mFilter)
                .enqueue(new Callback<FollowProfileModel>() {
                    @Override
                    public void onResponse(Call<FollowProfileModel> call, Response<FollowProfileModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            ((BaseActivity) mContext).retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            ((BaseActivity) mContext).retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<FollowProfileModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        ((BaseActivity) mContext).retrofitOnFailure();
                    }
                });
    }

    public void callBlockProfile(final Context mContext, final RetrofitResInterface mResInterface, JsonArray mInputArray, final int responseType) {

        String mFields = "*";
        String input = new Gson().toJson(mInputArray);
        ((BaseActivity) mContext).sysOut("API-TYPE: " + "POST");
        ((BaseActivity) mContext).sysOut("API-INPUT: " + input);
        ((BaseActivity) mContext).sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.BLOCKED_USER_PROFILES + "?fields=" + mFields);
        DialogManager.showProgress(mContext);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callSendBlockUserProfile(mFields, mInputArray)
                .enqueue(new Callback<BlockedUserModel>() {
                    @Override
                    public void onResponse(Call<BlockedUserModel> call, Response<BlockedUserModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            mResInterface.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            mResInterface.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<BlockedUserModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        mResInterface.retrofitOnFailure();
                    }
                });

    }

    public void callUnBlockProfile(final Context mContext, final RetrofitResInterface mResInterface, String mFilter, final int responseType) {
        String mFields = "*";
        ((BaseActivity) mContext).sysOut("API-TYPE: " + "POST");
        ((BaseActivity) mContext).sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.BLOCKED_USER_PROFILES + "?fields=" + mFields + "&filter=" + mFilter);
        DialogManager.showProgress(mContext);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callUnBlockUserProfile(mFields, mFilter)
                .enqueue(new Callback<BlockedUserModel>() {
                    @Override
                    public void onResponse(Call<BlockedUserModel> call, Response<BlockedUserModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            mResInterface.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            mResInterface.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<BlockedUserModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        mResInterface.retrofitOnFailure();
                    }
                });
    }

    public void callGetPromotersGallery(final BaseActivity activity, String filter, final int responseType, int mOffset, boolean mIsLoadMore) {
        int mLimit = 10;
        String fields = "*";
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.GALLERY_VIDEO + "?fields=" + fields + "&filter=" + filter +
                "&related=" + APIConstants.mPromoterGalleryRelated + "&limit=" + mLimit + "&offset=" + mOffset + "&include_count=true");
        /*if (!mIsLoadMore)
            DialogManager.showProgress(activity);*/

        String mOrderBy = "CreatedAt DESC";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetPromotersVideos(fields, filter,
                APIConstants.mPromoterGalleryRelated, mOrderBy, mLimit, mOffset, true)
                .enqueue(new Callback<PromoterVideoModel>() {
                    @Override
                    public void onResponse(Call<PromoterVideoModel> call, Response<PromoterVideoModel> response) {
                        /*DialogManager.hideProgress();*/
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromoterVideoModel> call, Throwable t) {
                        /*DialogManager.hideProgress();*/
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void callGetPromotersGalleryNotifyTray(final BaseActivity activity, String filter, final int responseType) {
        String fields = "*";
        /*activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.GALLERY_VIDEO + "?fields=" + fields + "&filter=" + filter +
                "&related=" + APIConstants.mPromoterGalleryRelated + "&limit=" + mLimit + "&offset=" + mOffset + "&include_count=true");*/
        /*if (!mIsLoadMore)
            DialogManager.showProgress(activity);*/

        String mOrderBy = "CreatedAt DESC";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetPromotersVideosNotifyTray(fields, filter,
                APIConstants.mPromoterGalleryRelated, mOrderBy, true)
                .enqueue(new Callback<PromoterVideoModel>() {
                    @Override
                    public void onResponse(Call<PromoterVideoModel> call, Response<PromoterVideoModel> response) {
                        /*DialogManager.hideProgress();*/
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromoterVideoModel> call, Throwable t) {
                        /*DialogManager.hideProgress();*/
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void callGetPromotersGallery(final BaseFragment activity, String filter, final int responseType, int mOffset, boolean mIsLoadMore) {
        int mLimit = 10;
        String fields = "*";
        //String mOrderBy = "ID DESC";
        String mOrderBy = "CreatedAt DESC";

        /*DialogManager.showProgress(activity.getActivity());*/

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetPromotersVideos(fields, filter,
                APIConstants.mPromoterGalleryRelated, mOrderBy, mLimit, mOffset, true)
                .enqueue(new Callback<PromoterVideoModel>() {
                    @Override
                    public void onResponse(Call<PromoterVideoModel> call, Response<PromoterVideoModel> response) {
                        /*DialogManager.hideProgress();*/
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromoterVideoModel> call, Throwable t) {
                        /*DialogManager.hideProgress();*/
                        activity.retrofitOnFailure(responseType);
                    }
                });
    }

    public void callGetPromotersGallery(final BaseActivity activity, String filter, final int responseType, int mOffset) {
        int mLimit = 10;
        String fields = "*";
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.GALLERY_VIDEO + "?fields=" + fields + "&filter=" + filter +
                "&related=" + APIConstants.mPromoterGalleryRelated + "&limit=" + mLimit + "&offset=" + mOffset + "&include_count=true");

        String mOrderBy = "CreatedAt DESC";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetPromotersVideos(fields, filter,
                APIConstants.mPromoterGalleryRelated, mOrderBy, mLimit, mOffset, true)
                .enqueue(new Callback<PromoterVideoModel>() {
                    @Override
                    public void onResponse(Call<PromoterVideoModel> call, Response<PromoterVideoModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromoterVideoModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void callGetPromotersGallery(final BaseFragment activity, String filter, final int responseType, int mOffset) {
        int mLimit = 10;
        String fields = "*";
        String mOrderBy = "CreatedAt DESC";
        //DialogManager.showProgress(activity.getActivity());

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetPromotersVideos(fields, filter,
                APIConstants.mPromoterGalleryRelated, mOrderBy, mLimit, mOffset, true)
                .enqueue(new Callback<PromoterVideoModel>() {
                    @Override
                    public void onResponse(Call<PromoterVideoModel> call, Response<PromoterVideoModel> response) {
                        //DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromoterVideoModel> call, Throwable t) {
                        //DialogManager.hideProgress();
                        activity.retrofitOnFailure(responseType);
                    }
                });
    }

    public void callPostEventLiveGroupChatMember(final BaseActivity activity, JsonArray mJsonArray, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callPostEventLiveGroupChatMember("*", mJsonArray)
                .enqueue(new Callback<EventLiveGroupChatModel>() {

                    @Override
                    public void onResponse(Call<EventLiveGroupChatModel> call, Response<EventLiveGroupChatModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<EventLiveGroupChatModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callDeleteEventLiveGroupChatMember(final BaseActivity activity, String mFilter, final int responseType) {

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callDeleteEventLiveGroupChatMember(mFilter)
                .enqueue(new Callback<EventLiveGroupChatModel>() {

                    @Override
                    public void onResponse(Call<EventLiveGroupChatModel> call, Response<EventLiveGroupChatModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<EventLiveGroupChatModel> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callUploadSpectatorLive(final BaseActivity activity, MultipartBody.Part videoPart, MultipartBody.Part imgPart, final int responseType) {

        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.FILE_SPECTATOR_LIVE);

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postSpectatorLiveImgFile(videoPart, imgPart).enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }


    public void callUnLikeForVideos(final BaseActivity activity, int id, final int responseType) {

        DialogManager.showProgress(activity);

        String mFilter = "ID=" + id;

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postUnLikesForVideos(mFilter).enqueue(new Callback<VideoLikesModel>() {
            @Override
            public void onResponse(Call<VideoLikesModel> call, Response<VideoLikesModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<VideoLikesModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callUnLikeForVideos(final BaseFragment activity, int id, final int responseType) {

        DialogManager.showProgress(activity.getActivity());

        String mFilter = "ID=" + id;

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postUnLikesForVideos(mFilter).enqueue(new Callback<VideoLikesModel>() {
            @Override
            public void onResponse(Call<VideoLikesModel> call, Response<VideoLikesModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<VideoLikesModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void postLikesForVideos(final BaseActivity activity, JsonObject mJsonObject, final int responseType) {
        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postLikesForVideos("*", mJsonObject).enqueue(new Callback<VideoLikesModel>() {
            @Override
            public void onResponse(Call<VideoLikesModel> call, Response<VideoLikesModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<VideoLikesModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });

    }

    public void postLikesForVideos(final BaseFragment activity, JsonObject mJsonObject, final int responseType) {
        DialogManager.showProgress(activity.getActivity());

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postLikesForVideos("*", mJsonObject).enqueue(new Callback<VideoLikesModel>() {
            @Override
            public void onResponse(Call<VideoLikesModel> call, Response<VideoLikesModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<VideoLikesModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });

    }

    public void callGetVideoComments(final BaseActivity activity, String mFilter, final int responseType) {

        DialogManager.showProgress(activity);

        String mOrderBy = "CreateTime DESC";
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callGetVideoComments("*", mFilter, APIConstants.mVideoCommentRelated, mOrderBy).enqueue(new Callback<VideoCommentsModel>() {
            @Override
            public void onResponse(Call<VideoCommentsModel> call, Response<VideoCommentsModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {
                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<VideoCommentsModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callPostVideoCommentReply(final BaseActivity activity, JsonObject jsonArray, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callPostVideoCommentReply("*", jsonArray).enqueue(new Callback<VideoCommentReplyModel>() {
            @Override
            public void onResponse(Call<VideoCommentReplyModel> call, Response<VideoCommentReplyModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<VideoCommentReplyModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void postLikesForVideoReply(final BaseActivity activity, JsonArray mJsonArray, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postLikesForVideoReply("*", mJsonArray).enqueue(new Callback<VideoReplyLikeModel>() {
            @Override
            public void onResponse(Call<VideoReplyLikeModel> call, Response<VideoReplyLikeModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<VideoReplyLikeModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callUnLikeVideoReply(final BaseActivity activity, int filter, final int responseType) {
        DialogManager.showProgress(activity);

        String mFilter = "ID=" + filter;

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postUnLikesForVideoReply(mFilter).enqueue(new Callback<VideoReplyLikeModel>() {
            @Override
            public void onResponse(Call<VideoReplyLikeModel> call, Response<VideoReplyLikeModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<VideoReplyLikeModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });

    }

    public void callUnLikeForVideoComments(final BaseActivity activity, int filter, final int responseType) {

        DialogManager.showProgress(activity);
        String mFilter = "ID=" + filter;

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postUnLikesForVideoComments(mFilter).enqueue(new Callback<VideoCommentLikeModel>() {
            @Override
            public void onResponse(Call<VideoCommentLikeModel> call, Response<VideoCommentLikeModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<VideoCommentLikeModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });

    }

    public void postLikesForVideoComments(final BaseActivity activity, JsonObject mJsonObject, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postLikesForVideoComments("*", mJsonObject).enqueue(new Callback<VideoCommentLikeModel>() {
            @Override
            public void onResponse(Call<VideoCommentLikeModel> call, Response<VideoCommentLikeModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<VideoCommentLikeModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callPostVideoComments(final BaseActivity activity, JsonObject jsonArray, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callPostVideoComments("*", APIConstants.mVideoCommentRelated, jsonArray).enqueue(new Callback<VideoCommentsModel>() {
            @Override
            public void onResponse(Call<VideoCommentsModel> call, Response<VideoCommentsModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<VideoCommentsModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callGetVideoCommentsReply(final BaseActivity activity, String mFilter, final int responseType) {

        DialogManager.showProgress(activity);

        String mOrderBy = "CreateTime DESC";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callGetVideoCommentsReply("*", mFilter, APIConstants.mVideoCommentReplyRelated, mOrderBy).enqueue(new Callback<VideoCommentReplyModel>() {
            @Override
            public void onResponse(Call<VideoCommentReplyModel> call, Response<VideoCommentReplyModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {
                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<VideoCommentReplyModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callPostVideoShares(final BaseActivity activity, JsonObject jsonObject, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callPostVideoShares("*", jsonObject).enqueue(new Callback<VideoShareModel>() {
            @Override
            public void onResponse(Call<VideoShareModel> call, Response<VideoShareModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message(), responseType);
                }
            }

            @Override
            public void onFailure(Call<VideoShareModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callPostVideoShares(final BaseFragment activity, JsonObject jsonObject, final int responseType) {

        DialogManager.showProgress(activity.getActivity());

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callPostVideoShares("*", jsonObject).enqueue(new Callback<VideoShareModel>() {
            @Override
            public void onResponse(Call<VideoShareModel> call, Response<VideoShareModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message(), responseType);
                }
            }

            @Override
            public void onFailure(Call<VideoShareModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callToPostSpectatorData(final BaseActivity activity, JsonArray mJsonObject, final int responseType) {

        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.GALLERY_VIDEO);
        activity.sysOut("API-INPUT: " + mJsonObject.toString());

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callPostSpectatorLiveStory("*", mJsonObject).enqueue(new Callback<SpectatorLiveModel>() {
            @Override
            public void onResponse(Call<SpectatorLiveModel> call, Response<SpectatorLiveModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<SpectatorLiveModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure(responseType);
            }
        });
    }

    public void callPostLiveStream(final BaseActivity activity, JsonObject mJsonObject) {
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().
                callPostLiveStream("*", mJsonObject)
                .enqueue(new Callback<LiveStreamResponse>() {
                    @Override
                    public void onResponse(Call<LiveStreamResponse> call, Response<LiveStreamResponse> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, 0);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<LiveStreamResponse> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }

                });
    }

    public void callGetLiveStream(final BaseActivity activity, String mFilter) {
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.LIVE_STREAM + "?filter=" +
                mFilter);
        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().
                callGetLiveStream("*", mFilter)
                .enqueue(new Callback<LiveStreamResponse>() {
                    @Override
                    public void onResponse(Call<LiveStreamResponse> call, Response<LiveStreamResponse> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, 0);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<LiveStreamResponse> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }

                });
    }

    public void callGetVehicleInfoLikes(final BaseActivity activity, String mFilter, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callGetVehicleInfoLikes("*", mFilter, APIConstants.VEHICLE_INFO_LIKE_RELATED).enqueue(new Callback<VehicleInfoLikeModel>() {
            @Override
            public void onResponse(Call<VehicleInfoLikeModel> call, Response<VehicleInfoLikeModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {
                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<VehicleInfoLikeModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callUnFollowPromoter(final Context mContext, final RetrofitResInterface mRetrofitInterface,
                                     String mFilter, final int responseType) {
        String mFields = "*";
        ((BaseActivity) mContext).sysOut("API-TYPE: " + "DELETE");
        ((BaseActivity) mContext).sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROMOTER_FOLLOWERS + "?fields=" + mFields +
                "&filter=" + mFilter);
        DialogManager.showProgress(mContext);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callUnFollowPromoter(mFields, mFilter)
                .enqueue(new Callback<PromoterFollowerModel>() {
                    @Override
                    public void onResponse(Call<PromoterFollowerModel> call, Response<PromoterFollowerModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            mRetrofitInterface.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            mRetrofitInterface.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromoterFollowerModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        mRetrofitInterface.retrofitOnFailure();
                    }
                });
    }

    public void callFollowPromoter(final Context mContext, final RetrofitResInterface mRetrofitResInterface,
                                   JsonArray mJsonArray, final int responseType) {
        String input = new Gson().toJson(mJsonArray);
        String mFields = "*";
        ((BaseActivity) mContext).sysOut("API-INPUT: " + input);
        ((BaseActivity) mContext).sysOut("API-TYPE: " + "POST");
        ((BaseActivity) mContext).sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROMOTER_FOLLOWERS + "?fields=" + mFields);
        DialogManager.showProgress(mContext);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callFollowPromoter
                (mFields, mJsonArray)
                .enqueue(new Callback<PromoterFollowerModel>() {
                    @Override
                    public void onResponse(Call<PromoterFollowerModel> call, Response<PromoterFollowerModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            mRetrofitResInterface.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            mRetrofitResInterface.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromoterFollowerModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        mRetrofitResInterface.retrofitOnFailure();
                    }
                });
    }

    public void GetPromoterSubs(final BaseActivity activity, String mFilter, final int responseType) {
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROMOTER_SUBSCRIPTION + "?filter=" + mFilter);
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callGetPromoterSubs(mFilter).enqueue(new Callback<PromoterSubsResModel>() {
            @Override
            public void onResponse(Call<PromoterSubsResModel> call, Response<PromoterSubsResModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<PromoterSubsResModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callUpdatePromoterSubs(final BaseActivity activity, JsonArray mInputObj, final int responseType) {

        activity.sysOut("API-TYPE: " + "POST");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROMOTER_SUBSCRIPTION);
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().postPromoterSubscribeRequest(mInputObj, "*").enqueue(new Callback<PromoterSubsResModel>() {
            @Override
            public void onResponse(Call<PromoterSubsResModel> call, Response<PromoterSubsResModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<PromoterSubsResModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callRemovePromoterSubs(final BaseActivity activity, String mFilter, final int responseType) {
        activity.sysOut("API-TYPE: " + "DELETE");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROMOTER_SUBSCRIPTION + "?filter=" + mFilter);
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callDltPromoterSubs("*", mFilter).enqueue(new Callback<PromoterSubsResModel>() {
            @Override
            public void onResponse(Call<PromoterSubsResModel> call, Response<PromoterSubsResModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<PromoterSubsResModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callPostReportpost(final BaseActivity activity, JsonArray mInputObj, final int responseType) {

        activity.sysOut("API-TYPE: " + "POST");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROMOTER_SUBSCRIPTION);
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callPostReportpost("*", mInputObj).enqueue(new Callback<PostReportModel>() {
            @Override
            public void onResponse(Call<PostReportModel> call, Response<PostReportModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<PostReportModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callUploadChatImg(final BaseActivity activity, MultipartBody.Part imgPart, final int responseType) {
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postSingleChatImgFile(imgPart).enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callGetOndemandList(final BaseFragment activity, String api, final int responseType) {

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetOndemandList(api).enqueue(new Callback<ArrayList<OndemandNewResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<OndemandNewResponse>> call, Response<ArrayList<OndemandNewResponse>> response) {
                if (response.isSuccessful()) {
                    ArrayList<OndemandNewResponse> mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<OndemandNewResponse>> call, Throwable t) {
                activity.retrofitOnFailure();
            }
        });
    }

    public void callGetIsAlreadyFollowedPromoter(final BaseActivity activity, String mFollowRelation, final int responseType) {
        String mFilter = "FollowRelation = " + mFollowRelation;

        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetIsAlreadyFollowedPromoter(mFilter).enqueue(new Callback<PromoterFollowerModel>() {
            @Override
            public void onResponse(Call<PromoterFollowerModel> call, Response<PromoterFollowerModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    PromoterFollowerModel mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<PromoterFollowerModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callGetIsAlreadyFollowedUser(final BaseActivity activity, String mFollowRelation, final int responseType) {
        String mFilter = "FollowRelation = " + mFollowRelation;

        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetIsAlreadyFollowedProfile(mFilter).enqueue(new Callback<FollowProfileModel>() {
            @Override
            public void onResponse(Call<FollowProfileModel> call, Response<FollowProfileModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    FollowProfileModel mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<FollowProfileModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callGetClubUsers(final BaseActivity activity, String mFilter, final int responseType) {

        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetClubUsers(mFilter).enqueue(new Callback<PromoterSubsResModel>() {
            @Override
            public void onResponse(Call<PromoterSubsResModel> call, Response<PromoterSubsResModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    PromoterSubsResModel mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<PromoterSubsResModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callDeleteVideoSharedPost(final BaseActivity activity, String postID, final int responseType) {
        String mFilter = "VideoID=" + postID;

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callDeleteVideoSharedPost(mFilter)
                .enqueue(new Callback<VideoShareModel>() {
                    @Override
                    public void onResponse(Call<VideoShareModel> call, Response<VideoShareModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<VideoShareModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }


    public void postLikesForVideosWithoutBuffering(final BaseActivity activity, JsonObject mJsonObject, final int responseType) {

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postLikesForVideos("*", mJsonObject).enqueue(new Callback<VideoLikesModel>() {
            @Override
            public void onResponse(Call<VideoLikesModel> call, Response<VideoLikesModel> response) {
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<VideoLikesModel> call, Throwable t) {
                activity.retrofitOnFailure();
            }
        });

    }

    public void callUnLikeForVideoswithoutBuffering(final BaseActivity activity, Integer id, final int responseType) {

        String mFilter = "ID=" + id;

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postUnLikesForVideos(mFilter).enqueue(new Callback<VideoLikesModel>() {
            @Override
            public void onResponse(Call<VideoLikesModel> call, Response<VideoLikesModel> response) {

                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<VideoLikesModel> call, Throwable t) {
                activity.retrofitOnFailure();
            }
        });
    }

    public void callGetIsAlreadyFollowedUserWithoutBuffering(final BaseActivity activity, String mFollowRelation, final int responseType) {
        String mFilter = "FollowRelation = " + mFollowRelation;

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetIsAlreadyFollowedProfile(mFilter).enqueue(new Callback<FollowProfileModel>() {
            @Override
            public void onResponse(Call<FollowProfileModel> call, Response<FollowProfileModel> response) {
                if (response.isSuccessful()) {
                    FollowProfileModel mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<FollowProfileModel> call, Throwable t) {
                activity.retrofitOnFailure();
            }
        });
    }

    public void callGetIsAlreadyFollowedPromoterWithoutBuffering(final BaseActivity activity, String mFollowRelation, final int responseType) {
        String mFilter = "FollowRelation = " + mFollowRelation;

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetIsAlreadyFollowedPromoter(mFilter).enqueue(new Callback<PromoterFollowerModel>() {
            @Override
            public void onResponse(Call<PromoterFollowerModel> call, Response<PromoterFollowerModel> response) {
                if (response.isSuccessful()) {
                    PromoterFollowerModel mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<PromoterFollowerModel> call, Throwable t) {
                activity.retrofitOnFailure();
            }
        });
    }

    public void callUnFollowProfileWithoutBuffering(final BaseActivity activity, String mFilter, final int responseType) {
        String mFields = "*";
        activity.sysOut("API-TYPE: " + "DELETE");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.FOLLOW_PROFILE + "?fields=" + mFields + "&filter=" + mFilter);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callUnFollowProfile(mFields, mFilter)
                .enqueue(new Callback<FollowProfileModel>() {
                    @Override
                    public void onResponse(Call<FollowProfileModel> call, Response<FollowProfileModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<FollowProfileModel> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void callUnFollowPromoterWithoutBuffering(final BaseActivity activity, String mFilter, final int responseType) {
        String mFields = "*";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callUnFollowPromoter(mFields, mFilter)
                .enqueue(new Callback<PromoterFollowerModel>() {
                    @Override
                    public void onResponse(Call<PromoterFollowerModel> call, Response<PromoterFollowerModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromoterFollowerModel> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void callUpdateOnDemandProfilePosts(final BaseActivity activity, JsonArray mJsonArray, final int responseType) {

        String mFields = "*";

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callUpdateOnDemandProfilePosts(mFields, mJsonArray)
                .enqueue(new Callback<PromoterVideoModel>() {
                    @Override
                    public void onResponse(Call<PromoterVideoModel> call, Response<PromoterVideoModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromoterVideoModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void callGetPaymentCardDetails(final BaseActivity activity, String mFilter, final int responseType) {

        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetPaymentCardDetails(mFilter).enqueue(new Callback<PaymentCardDetailsModel>() {
            @Override
            public void onResponse(Call<PaymentCardDetailsModel> call, Response<PaymentCardDetailsModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    PaymentCardDetailsModel mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<PaymentCardDetailsModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callAddPaymentCardDetails(final BaseActivity activity, JsonArray mJsonArray, final int responseType) {

        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callAddPaymentCardDetails("*", mJsonArray).enqueue(new Callback<PaymentCardDetailsModel>() {
            @Override
            public void onResponse(Call<PaymentCardDetailsModel> call, Response<PaymentCardDetailsModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    PaymentCardDetailsModel mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message(), responseType);
                }
            }

            @Override
            public void onFailure(Call<PaymentCardDetailsModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callDeletePaymentCardDetails(final BaseActivity activity, String mFilter, final int responseType) {
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callDeletePaymentCardDetails("*", mFilter).enqueue(new Callback<PaymentCardDetailsModel>() {
            @Override
            public void onResponse(Call<PaymentCardDetailsModel> call, Response<PaymentCardDetailsModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    PaymentCardDetailsModel mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<PaymentCardDetailsModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void blockNotifications(final BaseActivity activity, JsonObject jsonobj, final int responseType) {
        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().blockNotifications("*", jsonobj)
                .enqueue(new Callback<NotificationBlockedUsersModel>() {
                    @Override
                    public void onResponse(Call<NotificationBlockedUsersModel> call, Response<NotificationBlockedUsersModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationBlockedUsersModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void getViewCount(final BaseActivity activity, String filter, final int responseType) {
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetViewCount(filter)
                .enqueue(new Callback<PostsModel>() {
                    @Override
                    public void onResponse(Call<PostsModel> call, Response<PostsModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PostsModel> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void getViewCountOnDemand(final BaseActivity activity, String filter, final int responseType) {
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetViewCountOnDemand(filter)
                .enqueue(new Callback<PromoterVideoModel>() {
                    @Override
                    public void onResponse(Call<PromoterVideoModel> call, Response<PromoterVideoModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromoterVideoModel> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void setViewCount(final BaseActivity activity, JsonObject mJsonArray, final int responseType) {
        String mFields = "*";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callsetViewCount(mFields, mJsonArray)
                .enqueue(new Callback<PostsModel>() {
                    @Override
                    public void onResponse(Call<PostsModel> call, Response<PostsModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PostsModel> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void setViewCountOnDemand(final BaseActivity activity, JsonObject mJsonArray, final int responseType) {
        String mFields = "*";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callsetViewCountOnDemand(mFields, mJsonArray)
                .enqueue(new Callback<PromoterVideoModel>() {
                    @Override
                    public void onResponse(Call<PromoterVideoModel> call, Response<PromoterVideoModel> response) {
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromoterVideoModel> call, Throwable t) {
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void unBlockNotifications(final BaseActivity activity, String filter, final int responseType) {
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().unBlockNotifications("*", filter)
                .enqueue(new Callback<NotificationBlockedUsersModel>() {
                    @Override
                    public void onResponse(Call<NotificationBlockedUsersModel> call, Response<NotificationBlockedUsersModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationBlockedUsersModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });
    }

    public void getPromotersSubs(final BaseActivity activity, String mFilter, final int responseType, int limit, int offset) {
        DialogManager.showProgress(activity);
        String mRelated = "profiles_by_ProfileID";
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callGetClubSubs(mRelated, mFilter, limit, offset, true).enqueue(new Callback<PromoterSubsResModel>() {
            @Override
            public void onResponse(Call<PromoterSubsResModel> call, Response<PromoterSubsResModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<PromoterSubsResModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callGetFollowings(final BaseActivity activity, String mFilter, int limit, int offset, final int responseType) {
        DialogManager.showProgress(activity);
        String mRelated = "profiles_by_ProfileID";
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callGetFollowings(mRelated, mFilter, limit, offset, true).enqueue(new Callback<FollowProfileModel>() {
            @Override
            public void onResponse(Call<FollowProfileModel> call, Response<FollowProfileModel> response) {
                DialogManager.hideProgress();
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<FollowProfileModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    public void callGetPhoneEmailProfiles(final BaseActivity activity, JsonObject mParameteres, final int responseType) {
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callGetPhoneEmailProfiles("*", mParameteres).enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message(), responseType);
                }
            }

            @Override
            public void onFailure(Call<ProfileModel> call, Throwable t) {
                activity.retrofitOnFailure();
            }
        });
    }

    public void callGetProfileUserType(final BaseActivity activity, final int responseType) {
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .callGetProfileUserType("*").enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                if (response.isSuccessful()) {
                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);
                } else {
                    activity.retrofitOnError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                activity.retrofitOnFailure();
            }
        });


    }

    public void callGetPromoterWithPushToken(final BaseActivity activity, String mFilter, final int responseType) {

        DialogManager.showProgress(activity);

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetPromoterWithPushToken(mFilter)
                .enqueue(new Callback<PromotersModel>() {
                    @Override
                    public void onResponse(Call<PromotersModel> call, Response<PromotersModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PromotersModel> call, Throwable t) {
                        DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }

    public void callGetAllPosts(final BaseActivity activity, int filter, final int responseType, int limit, int offset) {
        String mOrderBy = "CreatedAt DESC";
        // DialogManager.showProgress(activity);
        activity.sysOut("API-TYPE: " + "GET");
        activity.sysOut("API-OPERATION: https://df.motohub.online/api/v2/allposts?filter=" +
                filter + "&related=" + APIConstants.POST_FEED_RELATION + "&order=" + mOrderBy + "&limit=" + limit + "&offset=" + offset);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetAllPosts(filter, APIConstants.POST_FEED_RELATION, limit, offset, true)
                .enqueue(new Callback<PostsModel>() {
                    @Override
                    public void onResponse(Call<PostsModel> call, Response<PostsModel> response) {
                        if (response.isSuccessful()) {
                            //  DialogManager.hideProgress();
                            Object mResponseObj = response.body();
                            activity.retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            activity.retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PostsModel> call, Throwable t) {
                        //  DialogManager.hideProgress();
                        activity.retrofitOnFailure();
                    }
                });

    }


    public void callUpdateUnSubscription(final BaseActivity activity, JsonArray mArray, final int responseType) {
        activity.sysOut("API-TYPE: " + "POST");
        activity.sysOut("API-OPERATION: " + UrlUtils.BASE_URL + UrlUtils.PROMOTER_SUBSCRIPTION);
        DialogManager.showProgress(activity);
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callUpdateUnSubscription(mArray, "*").enqueue(new Callback<PromoterSubsResModel>() {
            @Override
            public void onResponse(Call<PromoterSubsResModel> call, Response<PromoterSubsResModel> response) {
                DialogManager.hideProgress();

                if (response.isSuccessful()) {

                    Object mResponseObj = response.body();
                    activity.retrofitOnResponse(mResponseObj, responseType);

                } else {

                    activity.retrofitOnError(response.code(), response.message());

                }
            }

            @Override
            public void onFailure(Call<PromoterSubsResModel> call, Throwable t) {
                DialogManager.hideProgress();
                activity.retrofitOnFailure();
            }
        });
    }

    /**
     *  Don't Remove - Future Purpose
     */
    //    public void startDeviceStream(final BaseActivity activity, final int responseType) {
//
//        RetrofitClient.getRetrofitInstance().getPromoterPaymentInterface( )
//                .callStartDeviceStream( "1234567890123",1,1).enqueue(new Callback<DeviceStreamModel>() {
//            @Override
//            public void onResponse(Call<DeviceStreamModel> call, Response<DeviceStreamModel> response) {
//                DialogManager.hideProgress();
//                if (response.isSuccessful()) {
//                    Object mResponseObj = response.body();
//                    activity.retrofitOnResponse(mResponseObj, responseType);
//                } else {
//                    activity.retrofitOnError(response.code(), response.message(),responseType);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DeviceStreamModel> call, Throwable t) {
//                DialogManager.hideProgress();
//                activity.retrofitOnFailure();
//            }
//        });
//    }
//
//    public void getDeviceInfo(final BaseActivity activity, final int responseType) {
//
//        RetrofitClient.getRetrofitInstance().getPromoterPaymentInterface( )
//                .callGetDeviceInfo( "1234567890123").enqueue(new Callback<DeviceInfoModel>() {
//            @Override
//            public void onResponse(Call<DeviceInfoModel> call, Response<DeviceInfoModel> response) {
//                DialogManager.hideProgress();
//                if (response.isSuccessful()) {
//                    Object mResponseObj = response.body();
//                    activity.retrofitOnResponse(mResponseObj, responseType);
//                } else {
//                    activity.retrofitOnError(response.code(), response.message(),responseType);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DeviceInfoModel> call, Throwable t) {
//                DialogManager.hideProgress();
//                activity.retrofitOnFailure();
//            }
//        });
//    }
//
//    public void getDeviceStream(final BaseActivity activity, final int responseType) {
//        String mDreamFactoryApiKey = activity.getResources().getString(R.string.dream_factory_api_key);
//
//        String sessionToken = PreferenceUtils.getInstance(activity).getStrData(PreferenceUtils.SESSION_TOKEN);
//
//        DialogManager.showProgress(activity);
//
//        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface(mDreamFactoryApiKey,sessionToken)
//                .callGetDeviceStream("1234567890123",1,"rtmp",1 ).enqueue(new Callback<GetDeviceStreamModel>() {
//            @Override
//            public void onResponse(Call<GetDeviceStreamModel> call, Response<GetDeviceStreamModel> response) {
//                DialogManager.hideProgress();
//                if (response.isSuccessful()) {
//                    Object mResponseObj = response.body();
//                    activity.retrofitOnResponse(mResponseObj, responseType);
//                } else {
//                    activity.retrofitOnError(response.code(), response.message(),responseType);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GetDeviceStreamModel> call, Throwable t) {
//                DialogManager.hideProgress();
//                activity.retrofitOnFailure();
//            }
//        });
//    }
}