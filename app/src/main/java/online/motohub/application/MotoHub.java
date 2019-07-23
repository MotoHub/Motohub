package online.motohub.application;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.bumptech.glide.request.target.ViewTarget;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.gson.JsonObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import online.motohub.R;
import online.motohub.bl.MotoHubApp;
import online.motohub.model.EventAddOnModel;
import online.motohub.model.EventCategoryModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PromoterVideoModel;
import online.motohub.util.CommonAPI;
import online.motohub.util.ConnectivityChangeReceiver;
import online.motohub.util.DateUtil;
import online.motohub.util.ScreenSize;
import online.motohub.util.Utility;

public class MotoHub extends Application {

    public static boolean mIsFirstLaunch = false;
    //Utility Class
    public static Utility UTILITY_INSTANCE;
    public static CommonAPI COMMON_API_INSTANCE;
    private static MotoHub sMotoHub;
    private static boolean mIsSingleChatOnline;
    private static boolean mIsGrpChatOnline;
    private static boolean mIsEventGrpChatOnline;
    private static boolean mIsLiveEventGrpChatOnline;
    private static boolean mIsNewsFeedFragmentOnline;
    private static boolean mIsMyMotoFileOnline;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private int profileId;
    /* private ProfileResModel mProfileResModel;
     private PromotersResModel mPromoterResModel;
     private ProfileResModel mOthersProfileResModel;*/
    private ProfileResModel mOthersProfileResModel;

    private String liveStreamName;
    private ArrayList<EventCategoryModel> mSelectedEventCategoryList;
    private ArrayList<ProfileResModel> mFullProfileList;
    private JsonObject mEventQuestionAnswer;
    private ArrayList<EventAddOnModel> mSelectedEventAddOn = new ArrayList<>();
    private ArrayList<PromoterVideoModel.Resource> mPostsList = new ArrayList<>();

    public static boolean ismIsFirstLaunch() {
        return mIsFirstLaunch;
    }

    public static void setmIsFirstLaunch(boolean mIsFirstLaunch) {
        MotoHub.mIsFirstLaunch = mIsFirstLaunch;
    }

    public static synchronized MotoHub getApplicationInstance() {
        return sMotoHub;
    }

    public ArrayList<PromoterVideoModel.Resource> getmPostsList() {
        return mPostsList;
    }

    /*public ProfileResModel getmOthersProfileResModel() {
        return mOthersProfileResModel;
    }

    public void setmOthersProfileResModel(ProfileResModel mOthersProfileResModel) {
        this.mOthersProfileResModel = mOthersProfileResModel;
    }

    public ProfileResModel getmProfileResModel() {
        return mProfileResModel;
    }

    public void setmProfileResModel(ProfileResModel mProfileResModel) {
        this.mProfileResModel = mProfileResModel;
    }

    public PromotersResModel getmPromoterResModel() {
        return mPromoterResModel;
    }

    public void setmPromoterResModel(PromotersResModel mPromoterResModel) {
        this.mPromoterResModel = mPromoterResModel;
    }*/

    public void setmPostsList(ArrayList<PromoterVideoModel.Resource> mPostsList) {
        this.mPostsList = mPostsList;
    }

    public ProfileResModel getmOthersProfileResModel() {
        return mOthersProfileResModel;
    }

    public void setmOthersProfileResModel(ProfileResModel mOthersProfileResModel) {
        this.mOthersProfileResModel = mOthersProfileResModel;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        ViewTarget.setTagId(R.id.glide_tag);
        sMotoHub = this;
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        ScreenSize.getInstance(getApplicationContext());
        DateUtil.getInstance();
        registerReceiver(new ConnectivityChangeReceiver(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MotoHubApp.Builder mhBuilder = new MotoHubApp.Builder(this);
        MotoHubApp.initialize(mhBuilder);
        UTILITY_INSTANCE = new Utility();
        COMMON_API_INSTANCE = new CommonAPI();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void singleChatOnResume() {
        mIsSingleChatOnline = true;
    }

    public void singleChatOnPause() {
        mIsSingleChatOnline = false;
    }

    public boolean isSingleChatOnline() {
        return mIsSingleChatOnline;
    }

    public void grpChatOnResume() {
        mIsGrpChatOnline = true;
    }

    public void grpChatOnPause() {
        mIsGrpChatOnline = false;
    }

    public boolean isGrpChatOnline() {
        return mIsGrpChatOnline;
    }

    public void eventGrpChatOnResume() {
        mIsEventGrpChatOnline = true;
    }

    public void eventGrpChatOnPause() {
        mIsEventGrpChatOnline = false;
    }

    public boolean isEventGrpChatOnline() {
        return mIsEventGrpChatOnline;
    }

    public boolean isEventLiveGrpChatOnline() {
        return mIsLiveEventGrpChatOnline;
    }

    public void newsFeedFragmentOnResume() {
        mIsNewsFeedFragmentOnline = true;
    }

    public void newsFeedFragmentOnPause() {
        mIsNewsFeedFragmentOnline = false;
    }

    public boolean isNewsFeedFragmentOnline() {
        return mIsNewsFeedFragmentOnline;
    }

    public void myMotoFileOnResume() {
        mIsMyMotoFileOnline = true;
    }

    public void myMotoFileOnPause() {
        mIsMyMotoFileOnline = false;
    }

    public boolean isMyMotoFileOnline() {
        return mIsMyMotoFileOnline;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public String getLiveStreamName() {
        return liveStreamName;
    }

    public void setLiveStreamName(String liveStreamName) {
        this.liveStreamName = liveStreamName;
    }

    public ArrayList<EventCategoryModel> getSelectedEventCategoryList() {
        return mSelectedEventCategoryList;
    }

    public void setSelectedEventCategoryList(ArrayList<EventCategoryModel> mSelectedEventCategoryList) {

        this.mSelectedEventCategoryList = mSelectedEventCategoryList;
    }

    public ArrayList<ProfileResModel> getFullProfileList() {
        return mFullProfileList;
    }

    public void setFullProfileList(ArrayList<ProfileResModel> mFullMPList) {
        this.mFullProfileList = mFullMPList;
    }

    public JsonObject getEventQuestionAnswerObject() {
        return mEventQuestionAnswer;
    }

    public void setEventQuestionAnswerObject(JsonObject mQuestionAnswer) {
        this.mEventQuestionAnswer = mQuestionAnswer;
    }

    public ArrayList<EventAddOnModel> getPurchasedAddOn() {
        return mSelectedEventAddOn;
    }

    public void setPurchasedAddOn(ArrayList<EventAddOnModel> selectedEventAddOn) {
        this.mSelectedEventAddOn = selectedEventAddOn;
    }

    public void liveEventGrpChatOnResume() {
        mIsLiveEventGrpChatOnline = true;
    }

    public void liveEventGrpChatOnPause() {
        mIsLiveEventGrpChatOnline = false;
    }


}
