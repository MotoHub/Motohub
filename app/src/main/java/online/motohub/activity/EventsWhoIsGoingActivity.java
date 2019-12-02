package online.motohub.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import online.motohub.R;
import online.motohub.adapter.EventsWhoIsGoingAdapter;
import online.motohub.dialog.DialogManager;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsWhoIsGoingModel;
import online.motohub.model.EventsWhoIsGoingResModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;

public class EventsWhoIsGoingActivity extends BaseActivity {

    public static final String TOOLBAR_TITLE = "ToolbarTitle";
    @BindView(R.id.list_view_co_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.searchEt)
    EditText mSearchFollowingEt;
    @BindView(R.id.widget_list_view)
    ListView mWhoIsGoingListView;
    private ProfileResModel mMyProfileResModel;

    private ArrayList<EventsWhoIsGoingResModel> mWhoIsGoingListData = new ArrayList<>();

    private EventsWhoIsGoingAdapter mWhoIsGoingAdapter;

    private int mEventID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_list_view_with_search_box);

        ButterKnife.bind(this);

        initView();

    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    @SuppressWarnings("unchecked")
    private void initView() {

        assert getIntent().getExtras() != null;

        setToolbar(mToolbar, getIntent().getExtras().getString(TOOLBAR_TITLE));

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        mEventID = getIntent().getExtras().getInt(EventsModel.EVENT_ID);

        getWhoIsGoingList();

      /*  if (getIntent().getExtras().getSerializable(EventsWhoIsGoingModel.WHO_IS_GOING_RES_MODEL) == null) {

            mEventID = getIntent().getExtras().getInt(EventsModel.EVENT_ID);

            getWhoIsGoingList();

        } else {

            List<EventsWhoIsGoingResModel> mEventsWhoIsGoingResModels = (List<EventsWhoIsGoingResModel>) getIntent().getExtras().getSerializable(EventsWhoIsGoingModel.WHO_IS_GOING_RES_MODEL);

            assert mEventsWhoIsGoingResModels != null;

            mWhoIsGoingListData.addAll(mEventsWhoIsGoingResModels);
        }*/

        //mMyProfileResModel = (ProfileResModel) getIntent().getExtras().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        //mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);

        mWhoIsGoingAdapter = new EventsWhoIsGoingAdapter(this, mWhoIsGoingListData);
        mWhoIsGoingListView.setAdapter(mWhoIsGoingAdapter);

        mSearchFollowingEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mWhoIsGoingAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private void getWhoIsGoingList() {

        String mFilter = "EventID=" + mEventID;

        RetrofitClient.getRetrofitInstance().callGetEventsWhoIsGoing(this, mFilter, RetrofitClient.GET_EVENTS_WHO_IS_GOING);

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

    @OnItemClick(R.id.widget_list_view)
    public void onItemClick(int position) {
        try {
            if (mWhoIsGoingListData.get(position).getProfileByProfileID() != null && mWhoIsGoingListData.get(position).getProfileByProfileID().getID() != 0) {
                if (mWhoIsGoingListData.get(position).getProfileByProfileID().getID() == mMyProfileResModel.getID()) {
                    moveMyProfileScreen(this, 0);
                } else {
                    moveOtherProfileScreenWithResult(this, mMyProfileResModel.getID(),
                            mWhoIsGoingListData.get(position).getProfileByProfileID().getID(), AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        if (responseObj instanceof EventsWhoIsGoingModel) {

            EventsWhoIsGoingModel mEventsWhoIsGoingModel = (EventsWhoIsGoingModel) responseObj;

            switch (responseType) {

                case RetrofitClient.GET_EVENTS_WHO_IS_GOING:

                    if (mEventsWhoIsGoingModel.getResource() != null && mEventsWhoIsGoingModel.getResource().size() > 0) {

                        mWhoIsGoingListData.addAll(mEventsWhoIsGoingModel.getResource());
                        Set<EventsWhoIsGoingResModel> hs = new HashSet<>(mWhoIsGoingListData);
                        mWhoIsGoingListData.clear();
                        mWhoIsGoingListData.addAll(hs);
                        mWhoIsGoingAdapter.notifyDataSetChanged();

                    }

                    break;

            }

        } else if (responseObj instanceof SessionModel) {

            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }

            getWhoIsGoingList();

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
