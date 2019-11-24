package online.motohub.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.TimeTableAdapter;
import online.motohub.dialog.DialogManager;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.EventCategoryModel;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsResModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.DateComparator;
import online.motohub.util.DateUtil;
import online.motohub.util.PreferenceUtils;

public class TimeTableActivity extends BaseActivity {

    public static final String EXTRA_EVENT_DATA = "extra_event_data";
    @BindView(R.id.time_table_parent)
    RelativeLayout mParentView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.time_table_event_name_text_view)
    TextView mEventNameTv;
    @BindView(R.id.time_table_event_date_text_view)
    TextView mEventDateTv;
    @BindView(R.id.time_table_event_session_rv)
    RecyclerView mEventRv;
    LinearLayoutManager mLayoutManager;
    TimeTableAdapter mAdapter;
    List<Date> mAdapterIndex;
    Map<Date, List<EventCategoryModel>> mAdapterModel;
    private int mEventID;
    private Date mCurrentDate, mStartDate, mEndDate;
    private EventsResModel mEventsResModel;
    private Map<Date, List<EventCategoryModel>> modelMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        ButterKnife.bind(this);

        getData();

    }

    private void getData() {
        mEventID = getIntent().getIntExtra(EventsModel.EVENT_ID, 0);
        getEvent();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initView() {

        setToolbar(mToolbar, getString(R.string.time_table));
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        mEventNameTv.setText(mEventsResModel.getName());
        try {

            mEventDateTv.setText(DateUtil.getDate(mEventsResModel.getDate(), DateUtil.FORMAT_DMY_HMS, DateUtil.FORMAT_DMY));
            mStartDate = DateUtil.getDateWoTime(mEventsResModel.getDate(), DateUtil.FORMAT_DMY_HMS);
            mEndDate = DateUtil.getDateWoTime(mEventsResModel.getFinish(), DateUtil.FORMAT_DMY_HMS);

        } catch (ParseException e) {

            e.printStackTrace();
            mEventDateTv.setText(mEventsResModel.getDate());

        }

        mLayoutManager = new LinearLayoutManager(TimeTableActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mEventRv.setLayoutManager(mLayoutManager);

        mAdapterIndex = new ArrayList<>();
        mAdapterModel = new HashMap<>();

        mAdapterIndex.clear();
        prepareAdapterIndex(mStartDate);

        mAdapter = new TimeTableAdapter(TimeTableActivity.this, mAdapterIndex, mAdapterModel);
        mEventRv.setAdapter(mAdapter);

        mEventRv.setNestedScrollingEnabled(true);

        getAllEventSessions(mEventsResModel.getID());

    }

    private void getEvent() {

        String mFilter = "ID=" + mEventID;
        RetrofitClient.getRetrofitInstance().callGetEvents(this, mFilter, RetrofitClient.GET_EVENTS_RESPONSE);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.time_table_event_before_iv, R.id.time_table_event_next_iv})
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.time_table_event_before_iv:

                if (mCurrentDate != null) {

                    if (mCurrentDate.compareTo(mStartDate) == 0) {
                        //current date == start date
                        showSnackBar(mParentView, getString(R.string.session_start_date));

                    } else if (mCurrentDate.compareTo(mStartDate) == 1) {
                        //current > start date
                        mCurrentDate = DateUtil.previousDay(mCurrentDate);
                        try {

                            updateAdapter(mCurrentDate);


                        } catch (Exception e) {

                            e.printStackTrace();
                            clearAdapter();
                            showSnackBar(mParentView, getString(R.string.session_no_data_available));

                        }
                        mLayoutManager.scrollToPosition(0);
                    }

                } else {

                    clearAdapter();
                    showSnackBar(mParentView, getString(R.string.session_no_data_available));

                }
                try {

                    String dateStr = DateUtil.getDate(mCurrentDate, DateUtil.FORMAT_DMY);
                    mEventDateTv.setText(dateStr);

                } catch (Exception e) {

                    e.printStackTrace();

                }


                break;
            case R.id.time_table_event_next_iv:
                if (mCurrentDate != null) {

                    if (mCurrentDate.compareTo(mEndDate) == -1) {
                        //current date < end date
                        mCurrentDate = DateUtil.nextDay(mCurrentDate);
                        try {

                            updateAdapter(mCurrentDate);

                        } catch (Exception e) {

                            e.printStackTrace();
                            clearAdapter();
                            showSnackBar(mParentView, getString(R.string.session_no_data_available));

                        }
                        mLayoutManager.scrollToPosition(0);

                    } else {

                        showSnackBar(mParentView, getString(R.string.session_end_date));

                    }

                } else {

                    clearAdapter();
                    showSnackBar(mParentView, getString(R.string.session_no_data_available));

                }

                try {

                    String dateStr = DateUtil.getDate(mCurrentDate, DateUtil.FORMAT_DMY);
                    mEventDateTv.setText(dateStr);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void filterSessions(List<EventCategoryModel> models) throws Exception {

        modelMap = new HashMap<>();
        Collections.sort(models, new DateComparator(DateUtil.FORMAT_DMY_HMS));
        modelMap.putAll(splitDate(models));
        mCurrentDate = mStartDate;
        updateAdapter(mCurrentDate);

    }

    //activity utils
    private Map<Date, List<EventCategoryModel>> splitDate(List<EventCategoryModel> models) throws ParseException {

        Map<String, List<EventCategoryModel>> modelSessionStrMap = new HashMap<>();
        Map<Date, List<EventCategoryModel>> modelSessionDateMap = new HashMap<>();
        Date mKeyDate = null;

        for (EventCategoryModel value : models) {

            String key = DateUtil.getDate(value.getStartTime(), DateUtil.FORMAT_DMY_HMS, DateUtil.FORMAT_DMY);

            mKeyDate = DateUtil.getDateWoTime(value.getStartTime(), DateUtil.FORMAT_DMY_HMS);

            if (modelSessionStrMap.containsKey(key)) {

                modelSessionStrMap.get(key).add(value);

            } else {

                List<EventCategoryModel> list = new ArrayList<>();
                list.add(value);
                modelSessionStrMap.put(key, list);

            }

        }

        for (Map.Entry<String, List<EventCategoryModel>> entrySet : modelSessionStrMap.entrySet()) {

            modelSessionDateMap.put(mKeyDate, entrySet.getValue());

        }

        return modelSessionDateMap;

    }


    private void updateAdapter(Date currentDate) throws Exception {

        prepareSession(modelMap.get(currentDate), currentDate);

    }

    private void clearAdapter() {

        mAdapterModel.clear();
        mAdapter.notifyDataSetChanged();

    }

    private void prepareAdapterIndex(Date sessionDate) {

        mAdapterIndex.clear();
        //preparing indicators
        boolean isFirst = true;

        boolean isLast = true;

        Date lastDate;

        Date previousDate;

        for (int i = 0; i < 18; i++) {

            if (isFirst) {
                isFirst = false;
                mAdapterIndex.add(i, DateUtil.getFirstHourOfDay(sessionDate));

            } else {
                lastDate = mAdapterIndex.get(mAdapterIndex.size() - 1);
                mAdapterIndex.add(i, DateUtil.nextHour(lastDate));
            }
        }

        for (int i = 18; i < 24; i++) {

            if (isLast) {
                isLast = false;
                mAdapterIndex.add(i, DateUtil.getFirstPreviousHour(sessionDate));
            } else {
                previousDate = mAdapterIndex.get(mAdapterIndex.size() - 1);
                mAdapterIndex.add(i, DateUtil.nextHour(previousDate));
            }
        }
    }

    private void prepareSession(List<EventCategoryModel> models, Date sessionDate) throws Exception {

        mAdapterModel.clear();

        prepareAdapterIndex(sessionDate);

        //add keys
        for (Date time : mAdapterIndex) {

            mAdapterModel.put(time, new ArrayList<EventCategoryModel>());

        }

        //add values
        for (int i = 0; i < models.size(); i++) {

            Date dateKey = DateUtil.getDateTimeRoundOff(models.get(i).getStartTime(), DateUtil.FORMAT_DMY_HMS);

            if (mAdapterModel.containsKey(dateKey)) {

                List<EventCategoryModel> value = mAdapterModel.get(dateKey);
                value.add(models.get(i));
                mAdapterModel.put(dateKey, value);

            }

        }

        mAdapter.notifyDataSetChanged();

    }

    //api call
    private void getAllEventSessions(int eventId) {

        RetrofitClient.getRetrofitInstance().getEventSessions(this, eventId, RetrofitClient.GET_EVENT_SESSION_RESPONSE);

    }


    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String dialogType, StringBuilder profileTypesStr, ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);
        finish();
    }

    //api response
    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        switch (responseType) {

            case RetrofitClient.GET_EVENT_SESSION_RESPONSE:

                EventCategoryModel response = (EventCategoryModel) responseObj;
                if (response.getResource() != null && response.getResource().size() > 0) {

                    try {

                        filterSessions(response.getResource());

                    } catch (ParseException e) {

                        logResult(e.getMessage());
                        showSnackBar(mParentView, getString(R.string.session_parser_error));

                    } catch (Exception e) {

                        clearAdapter();
                        logResult(e.getMessage());
                        showSnackBar(mParentView, getString(R.string.session_no_data_available));

                    }

                } else {

                    showAppDialog(AppDialogFragment.ALERT_NO_EVENT_SESSIONS, null);

                }
                break;

            case RetrofitClient.UPDATE_SESSION_RESPONSE:

                SessionModel mSessionModel = (SessionModel) responseObj;
                if (mSessionModel.getSessionToken() == null) {
                    PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
                } else {
                    PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
                }

                getAllEventSessions(mEventsResModel.getID());

                break;

        }
        if (responseObj instanceof EventsModel) {

            EventsModel mEventModel = (EventsModel) responseObj;

            mEventsResModel = mEventModel.getResource().get(0);

            initView();

            //  mEventsWhoIsGoingResModels = mEventModel.getResource().get(0).getWhoIsGoingByEventID();


        }

    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        showSnackBar(mParentView, getString(R.string.internet_failure));

    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);

        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mParentView, mErrorMsg);
        }

    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);

        String mErrorMsg = code + " - " + message;
        showSnackBar(mParentView, mErrorMsg);

    }

}
