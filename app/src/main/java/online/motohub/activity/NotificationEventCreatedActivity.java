package online.motohub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.EventsFindAdapter;
import online.motohub.application.MotoHub;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.EventsInterface;
import online.motohub.model.EventAddOnModel;
import online.motohub.model.EventAnswersModel;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsResModel;
import online.motohub.model.EventsWhoIsGoingModel;
import online.motohub.model.PaymentModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PurchasedAddOnModel;
import online.motohub.model.RacingModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.dialog.DialogManager;
import online.motohub.util.PreferenceUtils;


public class NotificationEventCreatedActivity extends BaseActivity {

    @BindView(R.id.events_list_co_layout)
    RelativeLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.moto_type_spinner)
    Spinner mMTSpinner;

    @BindView(R.id.events_list_view)
    RecyclerView mEventsFindListView;

    @BindString(R.string.find_event)
    String mToolbarTitle;

    @BindString(R.string.no_events_err)
    String mNoEventsErr;
    ProfileResModel mMyProfileResModel;
    private ArrayList<EventsResModel> mEventsFindListData = new ArrayList<>();
    private EventsFindAdapter mEventsFindAdapter;
    private PaymentModel mTempPaymentModel;
    private int mFailureResponseType;
    private int mEventID;
    private ArrayList<ProfileResModel> mFullMPList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initView() {

        mMTSpinner.setVisibility(View.GONE);

        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        assert getIntent().getExtras() != null;
        try {
            JSONObject mJsonObject = new JSONObject(getIntent().getExtras().getString(MyFireBaseMessagingService.ENTRY_JSON_OBJ));

            mEventID = Integer.parseInt(mJsonObject.getJSONObject("Details").get("EventID").toString());

            getMotoProfiles();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getMotoProfiles() {


        int mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        String mFilter = "UserID = " + mUserID;

        RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);

    }

    private void getUpcomingEvents() {
        int status = AppConstants.EVENT_STATUS;
        String mDateFilter = "(ID=" + mEventID + ") AND ( EventStatus=" + status + ")";
        RetrofitClient.getRetrofitInstance().callGetEvents(this, mDateFilter, RetrofitClient.GET_EVENTS_RESPONSE);
    }

    @OnClick(R.id.toolbar_back_img_btn)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
        }
    }


    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String dialogType, StringBuilder profileTypesStr, ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);
        switch (dialogType) {
            case AppDialogFragment.ALERT_API_FAILURE_DIALOG:
                switch (mFailureResponseType) {
                    case RetrofitClient.EVENTS_BOOKING_RESPONSE:
                        mEventsFindAdapter.bookAnEventRequest(mTempPaymentModel);
                        break;
                    case RetrofitClient.RACING_RESPONSE:
                        mEventsFindAdapter.callPostRacingData();
                        break;
                }
                break;
            case AppDialogFragment.ALERT_SPECTATOR_UPDATE_DIALOG:
                String myProfileObj = new Gson().toJson(mMyProfileResModel);
                startActivity(new Intent(this, UpgradeProfileActivity.class).putExtra(AppConstants.MY_PROFILE_OBJ, myProfileObj));
                dismissAppDialog();
                break;
            case AppDialogFragment.EVENT_CATEGORY_DIALOG:
                mEventsFindAdapter.alertDialogPositiveBtnClick(dialogType);
                dismissAppDialog();
                break;


        }
    }

    @Override
    public void alertDialogNegativeBtnClick() {
        super.alertDialogNegativeBtnClick();
        dismissAppDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case EventsFindAdapter.EVENT_PAYMENT_REQ_CODE:
                    mEventsFindAdapter.onActivityResult(requestCode, resultCode, data);
                    break;
                case EventsFindAdapter.EVENT_QUESTIONS_REQ_CODE:
                    mEventsFindAdapter.onActivityResult(requestCode, resultCode, data);
                    break;
                case EventsFindAdapter.EVENT_LIVE_PAYMENT_REQ_CODE:
                    mEventsFindAdapter.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        if (responseObj instanceof ProfileModel) {

            ProfileModel mProfileModel = (ProfileModel) responseObj;

            switch (responseType) {

                case RetrofitClient.GET_PROFILE_RESPONSE:

                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {

                        mFullMPList.addAll(mProfileModel.getResource());

                        getUpcomingEvents();

                    } else {
                        showSnackBar(mCoordinatorLayout, mNoProfileErr);
                    }

                    break;

            }

        } else if (responseObj instanceof EventsModel) {

            EventsModel mEventsModel = (EventsModel) responseObj;

            if (mEventsModel.getResource() != null && mEventsModel.getResource().size() > 0) {
                ArrayList<EventsResModel> mEventResModel = mEventsModel.getResource();
                mEventsFindListData.add(mEventResModel.get(0));
                mMyProfileResModel = mFullMPList.get(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS));
                if (mMyProfileResModel != null && mMyProfileResModel.getID() != 0) {
                    mEventsFindAdapter = new EventsFindAdapter(this, mEventsFindListData, mMyProfileResModel, null, true);
                    mEventsFindListView.setLayoutManager(new LinearLayoutManager(this));
                    mEventsFindListView.setAdapter(mEventsFindAdapter);
                }
            } else {
                showSnackBar(mCoordinatorLayout, mNoEventsErr);
            }

        } else if (responseObj instanceof EventsWhoIsGoingModel) {

            EventsWhoIsGoingModel mEventsWhoIsGoingModel = (EventsWhoIsGoingModel) responseObj;

            if (mEventsWhoIsGoingModel.getResource() != null && mEventsWhoIsGoingModel.getResource().size() > 0) {

                EventsInterface mEventsInterface = mEventsFindAdapter;
                mEventsInterface.bookAnEventSuccess(mEventsWhoIsGoingModel.getResource().get(0));
                mEventsFindAdapter.callPostRacingData();

            }

        } else if (responseObj instanceof SessionModel) {

            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }

            mEventsFindListData.clear();
            getMotoProfiles();

        } else if (responseObj instanceof PaymentModel) {
            PaymentModel mResponse = (PaymentModel) responseObj;
            if (mResponse.getStatus() != null && mResponse.getStatus().equals("succeeded")) {
                mTempPaymentModel = mResponse;
                mEventsFindAdapter.bookAnEventRequest(mResponse);
            } else {
                String mErrorMsg = "Your card was declined.";
                if (mResponse.getMessage() != null) {
                    mErrorMsg = mResponse.getMessage();
                }
                mErrorMsg = mErrorMsg + " " + getString(R.string.try_again);
                showToast(this, mErrorMsg);
            }
        } else if (responseObj instanceof RacingModel) {
            ArrayList<EventAddOnModel> mSelectedEventAddOn = MotoHub.getApplicationInstance().getPurchasedAddOn();
            if (mSelectedEventAddOn.size() > 0)
                mEventsFindAdapter.callPostSelectedAddOns(mSelectedEventAddOn);
            else
                showSnackBar(mCoordinatorLayout, "Successfully booked an event.");
        } else if (responseObj instanceof PurchasedAddOnModel) {
            showSnackBar(mCoordinatorLayout, "Successfully booked an event.");
        } else if (responseObj instanceof EventAnswersModel) {
            EventAnswersModel mEventAnswerList = (EventAnswersModel) responseObj;
            ArrayList<EventAnswersModel> mResEventAnswerModel = mEventAnswerList.getResource();
            mEventsFindAdapter.callUpdateEventAnswerScreen(mResEventAnswerModel);
        }
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mCoordinatorLayout, mErrorMsg);
        }
    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
        String mErrorMsg = code + " - " + message;
        showSnackBar(mCoordinatorLayout, mErrorMsg);
    }

    @Override
    public void retrofitOnFailure(final int responseType) {
        super.retrofitOnFailure(responseType);
        if (responseType == RetrofitClient.POST_DATA_FOR_PAYMENT) {
            showSnackBar(mCoordinatorLayout, getString(R.string.promoter_account_err));
        } else {
            mFailureResponseType = responseType;
            showAppDialog(AppDialogFragment.ALERT_API_FAILURE_DIALOG, null);
        }
    }

}
