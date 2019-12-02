package online.motohub.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.FriendsStreamAdapter;
import online.motohub.application.MotoHub;
import online.motohub.dialog.DialogManager;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.CommonInterface;
import online.motohub.model.LiveStreamEntity;
import online.motohub.model.LiveStreamResponse;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.StringUtils;

public class ViewStreamAcceptedUsersActivity extends BaseActivity implements CommonInterface, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.parent_lay)
    CoordinatorLayout mParentLay;
    @BindView(R.id.users_list)
    RecyclerView mUserListView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.search_lay)
    LinearLayout mSearchLay;
    @BindView(R.id.search_edt)
    EditText mSearchEdt;
    @BindString(R.string.live)
    String mToolbarTitle;
    @BindView(R.id.swipe_to_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<LiveStreamEntity> mStreamList = new ArrayList<>();
    private FriendsStreamAdapter mAdapter;
    private int mCurrentProfileID = 0;
    private String mMyFollowings = "";
    private LinearLayoutManager mLayoutManager;
    private boolean isDelete = false, isGet = false;
    private int liveStreamID = 0;
    private String mLiveStreamName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_accpeted_users);
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
        mSwipeRefreshLayout.setOnRefreshListener(this);
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mCurrentProfileID = mBundle.getInt(AppConstants.PROFILE_ID, 0);
            mMyFollowings = mBundle.getString(AppConstants.MY_FOLLOWINGS, "");
        }
        setupUI(mParentLay);
        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        mLayoutManager = new LinearLayoutManager(this);
        mUserListView.setLayoutManager(mLayoutManager);
        callGetUsersList();

        mSearchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void searchUser(String searchStr) {
        ArrayList<LiveStreamEntity> filteredNames = new ArrayList<>();
        for (LiveStreamEntity entity : mStreamList) {

            if (entity != null) {
                ProfileResModel proEntity = entity.getProfiles_by_StreamProfileID();
                if (proEntity != null) {


                    String mName = "";
                    if (proEntity.getProfileType() == 5) {
                        mName = proEntity.getSpectatorName();
                    } else {
                        mName = proEntity.getDriver();
                    }
                    if (mName.toLowerCase().contains(searchStr.toLowerCase())) {
                        filteredNames.add(entity);
                    }
                }
            }
        }
        if (mAdapter != null)
            mAdapter.filterList(filteredNames);
    }


    private void callGetUsersList() {
        mSwipeRefreshLayout.setRefreshing(false);
        if (mMyFollowings.isEmpty()) {
            showToast(this, "No friends are found!!!. Please follow your friends first!!!");
        } else {
            isGet = true;
            String mFilter = "(" + APIConstants.StreamProfileID + " in (" + mMyFollowings + "))";
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

        } else if (responseObj instanceof LiveStreamResponse) {
            LiveStreamResponse mLiveStreamResponse = (LiveStreamResponse) responseObj;
            if (isGet) {
                isGet = false;
                if (mLiveStreamResponse.getResource().size() > 0) {
                    mStreamList.addAll(mLiveStreamResponse.getResource());
                    setAdapter();
                }
            } else if (isDelete) {
                isDelete = false;
            } else {
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
            callGetUsersList();
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

    private void setAdapter() {
        if (mStreamList.size() == 0) {
            mSearchLay.setVisibility(View.GONE);
        } else {
            mSearchLay.setVisibility(View.VISIBLE);
        }
        if (mAdapter != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        } else {
            mAdapter = new FriendsStreamAdapter(this, mCurrentProfileID, mStreamList);
            mUserListView.setAdapter(mAdapter);
        }
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.view_stream_btn, R.id.view_request_btn, R.id.moto_live_btn, R.id.multi_stream_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                break;
            case R.id.view_stream_btn:
                Intent mGoWatchActivity = new Intent(this, ViewLiveVideoViewScreen2.class);
                mGoWatchActivity.putExtra(AppConstants.PROFILE_ID, mCurrentProfileID);
                startActivity(mGoWatchActivity);
                break;
            case R.id.view_request_btn:
                startActivity(new Intent(this, ViewRequestUsersActivity.class).putExtra(AppConstants.PROFILE_ID, mCurrentProfileID));
                break;
            case R.id.moto_live_btn:
                startSingleStream();
                break;
            case R.id.multi_stream_btn:
                showAppDialog(AppDialogFragment.BOTTOM_LIVE_STREAM_OPTION_DIALOG, null);
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
            isGet = false;
            isDelete = false;
            RetrofitClient.getRetrofitInstance().callPostLiveStream(this, mJsonArray);
        } catch (Exception e) {
            sysOut("" + e.getMessage());
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onSuccess() {
        isDelete = true;
        String mFilter = "ID=" + liveStreamID;
        RetrofitClient.getRetrofitInstance().callDeleteLiveStream(this, mFilter);
    }

    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String dialogType, StringBuilder profileTypesStr, ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);
        switch (dialogType) {
            case AppDialogFragment.BOTTOM_LIVE_STREAM_OPTION_DIALOG:
                startSingleStream();
                break;
            case AppDialogFragment.BOTTOM_LIVE_STREAM_OPTION_MULTI:
                startActivity(new Intent(this, ViewStreamUsersActivity.class).putExtra(AppConstants.PROFILE_ID, mCurrentProfileID)
                        .putExtra(AppConstants.MY_FOLLOWINGS, mMyFollowings));
                break;
        }
    }

    @Override
    public void onRefresh() {
        callGetUsersList();
    }
}
