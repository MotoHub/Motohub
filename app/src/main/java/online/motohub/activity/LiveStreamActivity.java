package online.motohub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.activity.promoter.PromotersListActivity;
import online.motohub.adapter.FriendsStreamAdapter;
import online.motohub.adapter.PromotersStreamAdapter;
import online.motohub.application.MotoHub;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.CommonInterface;
import online.motohub.interfaces.CommonReturnInterface;
import online.motohub.model.LiveStreamEntity;
import online.motohub.model.LiveStreamResponse;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.dialog.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.StringUtils;

public class LiveStreamActivity extends BaseActivity implements CommonInterface {


    private final int PROMOTERS_LIVE_STREAM = 1;
    private final int FRIENDS_LIVE_STREAM = 2;
    private final int START_LIVE_STREAM = 3;
    private final int DELETE_LIVE_STREAM = 4;
    @BindView(R.id.parent_lay)
    CoordinatorLayout mParentLay;
    @BindView(R.id.promoters_stream_list_view)
    RecyclerView mPromoterListView;
    @BindView(R.id.friends_stream_list_view)
    RecyclerView mFriendsListView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.promoters_live_streams_txt)
    TextView mPromotersStreamTxt;
    @BindView(R.id.friends_live_streams_txt)
    TextView mFriendsStreamTxt;
    @BindString(R.string.live)
    String mToolbarTitle;
    private ArrayList<LiveStreamEntity> mFriendsStreamList = new ArrayList<>();
    private ArrayList<PromotersResModel> mPromotersStreamList = new ArrayList<>();
    private FriendsStreamAdapter mFriendsStreamAdapter;
    private PromotersStreamAdapter mPromotersStreamAdapter;
    private int mCurrentProfileID = 0;
    private String mMyFollowings = "";
    private String mMyPromoterFollowings = "";
    private int liveStreamID = 0;
    private String mLiveStreamName = "";
    private ProfileResModel mMyProfileResModel;
    private int LIVE_STREAM_RES_TYPE = 0;
    CommonReturnInterface mCommonReturnInterface = new CommonReturnInterface() {
        @Override
        public void onSuccess(int type) {
            if (type == 1) {
                startSingleStream();
            } else {
                startActivity(new Intent(LiveStreamActivity.this, ViewStreamUsersActivity.class).putExtra(AppConstants.PROFILE_ID, mCurrentProfileID)
                        .putExtra(AppConstants.MY_FOLLOWINGS, mMyFollowings));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_stream);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initView() {
        AppConstants.LIVE_STREAM_CALL_BACK = this;
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mCurrentProfileID = mBundle.getInt(AppConstants.PROFILE_ID, 0);
            mMyFollowings = mBundle.getString(AppConstants.MY_FOLLOWINGS, "");
            mMyPromoterFollowings = mBundle.getString(AppConstants.MY_PROMOTERS_FOLLOWINGS, "");
            //mMyProfileResModel = (ProfileResModel) mBundle.getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
            //mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
            mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
        }
        setupUI(mParentLay);
        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        mFriendsListView.setLayoutManager(new LinearLayoutManager(this));
        mPromoterListView.setLayoutManager(new LinearLayoutManager(this));
        callGetPromotersStream();

    }
//    private void callGetPromotersStream() {
//        if (mMyPromoterFollowings.isEmpty()) {
//            String mStr = getString(R.string.on_demand) + "\n" + getString(R.string.empty_promoters);
//            mPromotersStreamTxt.setText(mStr);
//            callGetFriendsStream();
//        } else {
//            String mFilter = "(" + APIConstants.StreamProfileID + " in (" + mMyPromoterFollowings + ")) AND (" + APIConstants.UserType + "!=user)";
//            LIVE_STREAM_RES_TYPE = PROMOTERS_LIVE_STREAM;
//            ApiClient.getRetrofitInstance().callGetFriendsStream(this, mFilter);
//        }
//    }

    private void callGetPromotersStream() {
        if (mMyPromoterFollowings.isEmpty()) {
            String mStr = getString(R.string.on_demand) + "\n" + getString(R.string.empty_promoters);
            mPromotersStreamTxt.setText(mStr);
        } else {
            String mFilter = "(" + APIConstants.user_id + " in (" + mMyPromoterFollowings + ")) AND (" + APIConstants.user_type + "!=newsmedia)";
            LIVE_STREAM_RES_TYPE = PROMOTERS_LIVE_STREAM;
            RetrofitClient.getRetrofitInstance().callGetStreamPromoters(this, mFilter);
        }
    }

    private void callGetFriendsStream() {
        if (mMyFollowings.isEmpty()) {
            String mStr = getString(R.string.empty_friends);
            mFriendsStreamTxt.setText(mStr);
        } else {
            String mFilter = "(" + APIConstants.StreamProfileID + " in (" + mMyFollowings + ")) AND (" + APIConstants.UserType + "=user)";
            LIVE_STREAM_RES_TYPE = FRIENDS_LIVE_STREAM;
            RetrofitClient.getRetrofitInstance().callGetFriendsStream(this, mFilter);
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof ProfileModel) {
            ProfileModel mProfileResponse = (ProfileModel) responseObj;
            if (mProfileResponse.getResource().size() > 0) {

            }

        } else if (responseObj instanceof PromotersModel) {
            PromotersModel mPromoterResponse = (PromotersModel) responseObj;
            if (mPromoterResponse.getResource().size() > 0) {
                if (mPromoterResponse.getResource().size() > 0) {
                    for (PromotersResModel promotersResModel : mPromoterResponse.getResource()) {
                        if (promotersResModel.getLivestream_by_StreamProfileID().size() > 0)
                            mPromotersStreamList.add(promotersResModel);
                    }
                    setPromoterStreamAdapter();
                } else {
                    String mStr = getString(R.string.on_demand) + "\n" + getString(R.string.empty_event_live_streams);
                    mPromotersStreamTxt.setText(mStr);
                }
            }

        } else if (responseObj instanceof LiveStreamResponse) {
            LiveStreamResponse mLiveStreamResponse = (LiveStreamResponse) responseObj;
            if (LIVE_STREAM_RES_TYPE == PROMOTERS_LIVE_STREAM) {
                if (mLiveStreamResponse.getResource().size() > 0) {
//                    mPromotersStreamList.addAll(mLiveStreamResponse.getResource());
                    setPromoterStreamAdapter();
                } else {
                    String mStr = getString(R.string.on_demand) + "\n" + getString(R.string.empty_event_live_streams);
                    mPromotersStreamTxt.setText(mStr);
                }
                callGetFriendsStream();
            } else if (LIVE_STREAM_RES_TYPE == FRIENDS_LIVE_STREAM) {
                if (mLiveStreamResponse.getResource().size() > 0) {
                    mFriendsStreamList.addAll(mLiveStreamResponse.getResource());
                    setFriendStreamAdapter();
                } else {
                    String mStr = getString(R.string.empty_friends_live_streams);
                    mFriendsStreamTxt.setText(mStr);
                }
            } else if (LIVE_STREAM_RES_TYPE == START_LIVE_STREAM) {
                if (mLiveStreamResponse.getResource().size() > 0) {
                    liveStreamID = mLiveStreamResponse.getResource().get(0).getID();
                    mLiveStreamName = mLiveStreamResponse.getResource().get(0).getStreamName();
                }
                MotoHub.getApplicationInstance().setLiveStreamName(mLiveStreamName);
                Intent mCameraActivity = new Intent(this, CameraActivity.class);
                startActivity(mCameraActivity);
            }

        } else if (responseObj instanceof SessionModel) {
            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            callGetPromotersStream();
        }

    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showToast(this, mErrorMsg);
        }
    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
    }

    private void setFriendStreamAdapter() {
        if (mFriendsStreamAdapter != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mFriendsStreamAdapter.notifyDataSetChanged();
                }
            });
        } else {
            mFriendsStreamAdapter = new FriendsStreamAdapter(this, mCurrentProfileID, mFriendsStreamList);
            mFriendsListView.setAdapter(mFriendsStreamAdapter);
        }
    }

    private void setPromoterStreamAdapter() {
        String mStr = getString(R.string.on_demand);
        mPromotersStreamTxt.setText(mStr);
        if (mPromotersStreamAdapter != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPromotersStreamAdapter.notifyDataSetChanged();
                }
            });
        } else {
            mPromotersStreamAdapter = new PromotersStreamAdapter(this, mPromotersStreamList, mMyProfileResModel);
            mPromoterListView.setAdapter(mPromotersStreamAdapter);
        }
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.go_live_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                break;
            case R.id.go_live_btn:
                DialogManager.showMultiLiveOptionPopup(this, mCommonReturnInterface, getString(R.string.single_stream), getString(R.string.live_stream));
//                showAppDialog(AppDialogFragment.BOTTOM_LIVE_STREAM_OPTION_DIALOG, null);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String dialogType, StringBuilder profileTypesStr, ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);
        switch (dialogType) {
            case AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG:
                callGetPromotersStream();
                break;
            case AppDialogFragment.BOTTOM_LIVE_STREAM_OPTION_DIALOG:
                startSingleStream();
                break;
            case AppDialogFragment.BOTTOM_LIVE_STREAM_OPTION_MULTI:
                startActivity(new Intent(this, ViewStreamUsersActivity.class).putExtra(AppConstants.PROFILE_ID, mCurrentProfileID)
                        .putExtra(AppConstants.MY_FOLLOWINGS, mMyFollowings));
                break;
        }
    }

    private void startSingleStream() {
        try {
            String mStreamName = StringUtils.genRandomStreamName(this);
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(APIConstants.StreamName, mStreamName);
            mJsonObject.addProperty(APIConstants.CreatedProfileID, mCurrentProfileID);
            mJsonObject.addProperty(APIConstants.StreamProfileID, mCurrentProfileID);
            JsonArray mJsonArray = new JsonArray();
            mJsonArray.add(mJsonObject);
            LIVE_STREAM_RES_TYPE = START_LIVE_STREAM;
            RetrofitClient.getRetrofitInstance().callPostLiveStream(this, mJsonArray);
        } catch (Exception e) {
            sysOut("" + e.getMessage());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PromotersListActivity.PROMOTER_FOLLOW_RESPONSE:
                    assert data.getExtras() != null;
                    //PromotersResModel mPromotersResModel = (PromotersResModel) data.getExtras().getSerializable(PromotersModel.PROMOTERS_RES_MODEL);
                    //ProfileResModel mMyProfileResModel = (ProfileResModel) data.getExtras().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
                    //TODO getting data
                    /*PromotersResModel mPromotersResModel = MotoHub.getApplicationInstance().getmPromoterResModel();
                    ProfileResModel mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();*/
                    PromotersResModel mPromotersResModel = EventBus.getDefault().getStickyEvent(PromotersResModel.class);
                    ProfileResModel mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
                    mPromotersStreamAdapter.updatePromoterFollowResponse(mPromotersResModel);

                    //TODO setting data
                    /*MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                    MotoHub.getApplicationInstance().setmPromoterResModel(mPromotersResModel);*/
                    EventBus.getDefault().postSticky(mMyProfileResModel);
                    EventBus.getDefault().postSticky(mPromotersResModel);
                    setResult(RESULT_OK, new Intent()
                            /*.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel)
                            .putExtra(PromotersModel.PROMOTERS_RES_MODEL, mPromotersResModel)*/);
                    break;

                case PromotersStreamAdapter.LIVE_PAYMENT_REQ_CODE:
                    mPromotersStreamAdapter.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

    @Override
    public void onSuccess() {
        String mFilter = "ID=" + liveStreamID;
        LIVE_STREAM_RES_TYPE = DELETE_LIVE_STREAM;
        RetrofitClient.getRetrofitInstance().callDeleteLiveStream(this, mFilter);
    }

}
