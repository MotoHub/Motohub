package online.motohub.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.EventsFindAdapter;
import online.motohub.dialog.DialogManager;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsResModel;
import online.motohub.model.EventsWhoIsGoingModel;
import online.motohub.model.EventsWhoIsGoingResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerModel;
import online.motohub.newdesign.activity.ComingSoonActivity;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;

public class EventsHomeActivity extends BaseActivity {

    @BindView(R.id.events_home_co_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindString(R.string.event)
    String mToolbarTitle;

    @BindView(R.id.events_list_view)
    RecyclerView mEventsBookedListView;

    @BindString(R.string.no_events_err)
    String mNoEventsErr;

    private ArrayList<EventsResModel> mEventsFindListData = new ArrayList<>();
    private ArrayList<EventsResModel> mNewEventsBookedListData = new ArrayList<>();

    private EventsFindAdapter mEventsFindAdapter;
    private ProfileResModel mMyProfileResModel;
    private int mCurrentProfileID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_home);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {

        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null)
            mCurrentProfileID = mBundle.getInt(AppConstants.PROFILE_ID, 0);
        getMyProfiles();

    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void setAdapter() {
        if (mEventsFindAdapter == null && mMyProfileResModel.getID() != 0) {
            mEventsFindAdapter = new EventsFindAdapter(this, mNewEventsBookedListData, mMyProfileResModel, null, true);
            mEventsBookedListView.setLayoutManager(new LinearLayoutManager(this));
            mEventsBookedListView.setAdapter(mEventsFindAdapter);
        } else {
            mEventsFindAdapter.notifyDataSetChanged();
        }
    }

    private void getMyProfiles() {
        String mFilter = "ID=" + mCurrentProfileID;
        if (isNetworkConnected(this))
            RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);
        else
            showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
    }

    private void getUpcomingEvents() {
        int status = AppConstants.EVENT_STATUS;
        String mDateFilter = "(( Date >= " + getCurrentDate() + " ) OR ( Finish >= " + getCurrentDate() + " )) AND ( EventStatus = " + status + ")";
        RetrofitClient.getRetrofitInstance().callGetEvents(this, mDateFilter, RetrofitClient.GET_EVENTS_RESPONSE);
    }

    private boolean isAlreadyBooked(ArrayList<EventsWhoIsGoingResModel> eventsWhoIsGoingResModel) {
        boolean isAlreadyBooked = false;
        int mUserId = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        for (int i = 0; i < eventsWhoIsGoingResModel.size(); i++) {
            if (mUserId == eventsWhoIsGoingResModel.get(i).getUserID()) {
                isAlreadyBooked = true;
                break;
            }
        }
        return isAlreadyBooked;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.find_event_box, R.id.event_results_box})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.find_event_box:
                if (isNetworkConnected(this)) {
                    if (mMyProfileResModel != null) {
                        //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                        EventBus.getDefault().postSticky(mMyProfileResModel);
                        startActivityForResult(new Intent(this, EventsFindActivity.class), AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                    }
                } else {
                    showToast(EventsHomeActivity.this, getResources().getString(R.string.internet_err));
                }
                break;
            case R.id.event_results_box:
                if (isNetworkConnected(this))
                    startActivity(new Intent(this, ComingSoonActivity.class));
                else
                    showToast(EventsHomeActivity.this, getResources().getString(R.string.internet_err));
                break;
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        if (responseObj instanceof ProfileModel) {
            ProfileModel mProfileModel = (ProfileModel) responseObj;
            if (mProfileModel.getResource().size() > 0) {
                mMyProfileResModel = mProfileModel.getResource().get(0);
                getUpcomingEvents();
            } else {
                showSnackBar(mCoordinatorLayout, mNoProfileErr);
            }
        } else if (responseObj instanceof EventsModel) {
            EventsModel mEventsModel = (EventsModel) responseObj;
            if (mEventsModel.getResource() != null && mEventsModel.getResource().size() > 0) {
                mEventsFindListData.clear();
                mEventsFindListData.addAll(mEventsModel.getResource());

                for (int i = 0; i < mEventsFindListData.size(); i++) {
                    if (isAlreadyBooked(mEventsFindListData.get(i).getWhoIsGoingByEventID())) {
                        mNewEventsBookedListData.add(mEventsFindListData.get(i));
                    }
                }

                if (mNewEventsBookedListData.size() == 0) {
                    Toast.makeText(this, "No Events booked.", Toast.LENGTH_SHORT).show();
                }
                setAdapter();
            } else {
                showSnackBar(mCoordinatorLayout, mNoEventsErr);
            }

        } else if (responseObj instanceof EventsWhoIsGoingModel) {

            EventsWhoIsGoingModel mEventsWhoIsGoingModel = (EventsWhoIsGoingModel) responseObj;

            if (mEventsWhoIsGoingModel.getResource() != null && mEventsWhoIsGoingModel.getResource().size() > 0) {

                mEventsFindAdapter.bookAnEventSuccess(mEventsWhoIsGoingModel.getResource().get(0));

            }

        } else if (responseObj instanceof SessionModel) {

            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }

            getMyProfiles();

        } else if (responseObj instanceof PromoterFollowerModel) {
            PromoterFollowerModel mPromoterFollowerModel = (PromoterFollowerModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_PROMOTER_FOLLOW_RESPONSE:

                    break;
                case RetrofitClient.GET_PROMOTER_UN_FOLLOW_RESPONSE:

                    break;
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.FOLLOWERS_FOLLOWING_RESULT:
                    getMyProfiles();
//                    mMyProfileResModel = (ProfileResModel) data.getExtras().get(AppConstants.MY_PROFILE_OBJ);
//                    //TODO refresh list
//                    setAdapter();
                    break;
            }
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
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        showSnackBar(mCoordinatorLayout, mInternetFailed);
    }

}
