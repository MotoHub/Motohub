package online.motohub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import online.motohub.R;
import online.motohub.adapter.ChatSingleAddAdapter;
import online.motohub.application.MotoHub;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.model.SingleChatRoomModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.Utility;

public class ChatSingleAddActivity extends BaseActivity {

    @BindView(R.id.list_view_co_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.widget_list_view)
    ListView mFollowingListView;

    @BindView(R.id.done_floating_btn)
    FloatingActionButton mFloatingActionBtn;

    @BindString(R.string.select_profile)
    String mToolbarTitle;

    @BindString(R.string.no_following_found_err)
    String mNoFollowingErr;

    private ProfileResModel mMyProfileModel;

    private List<ProfileResModel> mFollowingListData;
    private ChatSingleAddAdapter mChatSingleAddAdapter;
    private int mCurrentAdapterPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_list_view);

        ButterKnife.bind(this);

        initViews();

    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initViews() {

        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        //mMyProfileModel = (ProfileResModel) getIntent().getSerializableExtra(ProfileModel.MY_PROFILE_RES_MODEL);
        //mMyProfileModel = MotoHub.getApplicationInstance().getmProfileResModel();
        mMyProfileModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);

        mFloatingActionBtn.setVisibility(View.VISIBLE);

        mFollowingListData = new ArrayList<>();
        mChatSingleAddAdapter = new ChatSingleAddAdapter(this, mFollowingListData);
        mFollowingListView.setAdapter(mChatSingleAddAdapter);
        getFollowingList();


    }

    private void getFollowingList() {
        String mMyFollowingsID = Utility.getInstance().getMyFollowersFollowingsID(mMyProfileModel.getFollowprofile_by_ProfileID(), false);
        if (!mMyFollowingsID.isEmpty()) {
            String mSingleChatRoomIds = getIntent().getStringExtra(SingleChatRoomModel.SINGLE_CHAT_ROOM_ID);
            String mFilter;
            if (mSingleChatRoomIds.isEmpty()) {
                mFilter = "(id IN (" + mMyFollowingsID + "))";
            } else {
                mFilter = "(id IN (" + mMyFollowingsID + ")) AND (id NOT IN (" + mSingleChatRoomIds + "))";
            }
            RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_FOLLOWING_PROFILE_RESPONSE);
        } else {
            showSnackBar(mCoordinatorLayout, mNoFollowingErr);
        }


    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.done_floating_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.done_floating_btn:
                if (mCurrentAdapterPos != -1) {
                    //MotoHub.getApplicationInstance().setmOthersProfileResModel(mFollowingListData.get(mCurrentAdapterPos));
                    EventBus.getDefault().postSticky(mFollowingListData.get(mCurrentAdapterPos));
                    setResult(RESULT_OK, new Intent()/*.putExtra(ProfileModel.OTHERS_PROFILE_RES_MODEL, mFollowingListData.get(mCurrentAdapterPos))*/);
                }
                finish();
                break;
        }
    }

    @OnItemClick(R.id.widget_list_view)
    void onItemClick(int position) {
        boolean isSelectedProfile = mFollowingListData.get(position).getIsSelected();
        mFollowingListData.get(position).setIsSelected(!isSelectedProfile);
        if (mCurrentAdapterPos != -1 && mCurrentAdapterPos != position) {
            mFollowingListData.get(mCurrentAdapterPos).setIsSelected(false);
        }
        mChatSingleAddAdapter.notifyDataSetChanged();
        mCurrentAdapterPos = position;

    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        if (responseObj instanceof ProfileModel) {

            ProfileModel mProfileModel = (ProfileModel) responseObj;

            switch (responseType) {

                case RetrofitClient.GET_FOLLOWING_PROFILE_RESPONSE:

                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        mFollowingListData.clear();
                        mFollowingListData.addAll(mProfileModel.getResource());
                        mChatSingleAddAdapter.notifyDataSetChanged();
                    } else {
                        showSnackBar(mCoordinatorLayout, mNoFollowingErr);
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

            getFollowingList();

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
