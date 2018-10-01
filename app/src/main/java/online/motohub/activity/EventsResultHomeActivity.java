package online.motohub.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import online.motohub.R;
import online.motohub.adapter.EventsResultAdapter;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsResModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;

import static butterknife.OnItemSelected.Callback.NOTHING_SELECTED;

public class EventsResultHomeActivity extends BaseActivity {

    @BindView(R.id.events_list_co_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.moto_type_spinner)
    Spinner mMTSpinner;

    @BindView(R.id.events_list_view)
    ListView mEventsResultListView;

    @BindString(R.string.event_results)
    String mToolbarTitle;

    @BindString(R.string.no_events_err)
    String mNoEventsErr;

    private List<EventsResModel> mEventsResultListData;
    private EventsResultAdapter mEventsResultAdapter;
    private boolean mEventsLoaded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        ButterKnife.bind(this);

        initView();

    }

    private void initView() {

        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        ArrayAdapter<CharSequence> mMPSpinnerAdapter = new ArrayAdapter<CharSequence>(this, R.layout.widget_spinner_item, getResources().getStringArray(R.array.moto_type));
        mMPSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mMTSpinner.setAdapter(mMPSpinnerAdapter);

        mEventsResultListData = new ArrayList<>();
        mEventsResultAdapter = new EventsResultAdapter(this, mEventsResultListData);
        mEventsResultListView.setAdapter(mEventsResultAdapter);

        getEventResults();

    }

    private void getEventResults() {
        String mDateFilter = "( Finish < " + getCurrentDate() + " )";
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
    public void onBackPressed() {
        finish();
    }

    @OnItemSelected(R.id.moto_type_spinner)
    public void onItemSelected(int position) {

        if(mEventsLoaded) {
            mEventsResultAdapter.getFilter().filter((CharSequence) mMTSpinner.getItemAtPosition(position));
        }

    }

    @OnItemSelected(value = R.id.moto_type_spinner, callback = NOTHING_SELECTED)
    void onNothingSelected() {

    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        if(responseObj instanceof EventsModel) {

            EventsModel mEventsModel = (EventsModel) responseObj;

            if (mEventsModel.getResource() != null && mEventsModel.getResource().size() > 0) {
                mEventsResultListData.clear();
                mEventsResultListData.addAll(mEventsModel.getResource());
                mEventsLoaded = true;
                mEventsResultAdapter.notifyDataSetChanged();
            } else {
                showSnackBar(mCoordinatorLayout, mNoEventsErr);
            }

        } else if(responseObj instanceof SessionModel) {

            SessionModel mSessionModel = (SessionModel) responseObj;
            if(mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            mEventsResultListData.clear();
            mEventsLoaded = false;
            getEventResults();

        }

    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);

        if(message.equals("Unauthorized") || code == 401) {
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
