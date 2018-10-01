package online.motohub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.ChatTabPagerAdapter;
import online.motohub.fragment.BaseFragment;
import online.motohub.model.GroupChatRoomModel;
import online.motohub.model.GroupChatRoomResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.model.SingleChatRoomModel;
import online.motohub.model.SingleChatRoomResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;

public class ChatHomeActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    @BindView(R.id.chat_home_root)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.chat_tab_layout)
    TabLayout mChatTabLayout;

    @BindView(R.id.chat_view_pager)
    ViewPager mChatViewPager;

    @BindView(R.id.create_chat)
    FloatingActionButton mCreateChatBtn;

    @BindString(R.string.messages)
    String mToolbarTitle;

    public static final int ADD_NEW_SINGLE_CHAT = 100;
    public static final int ADD_NEW_GROUP_CHAT = 110;

    private ProfileResModel mMyProfileModel;
    private ChatTabPagerAdapter mChatTabPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);

        ButterKnife.bind(this);

        initViews();

    }

    private void initViews() {

        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        mMyProfileModel = (ProfileResModel) getIntent().getSerializableExtra(ProfileModel.MY_PROFILE_RES_MODEL);

        mChatTabPagerAdapter = new ChatTabPagerAdapter(getSupportFragmentManager(), mMyProfileModel);
        mChatViewPager.setAdapter(mChatTabPagerAdapter);

        mChatTabLayout.setupWithViewPager(mChatViewPager);

        mChatTabLayout.addOnTabSelectedListener(this);

        final TabLayout.Tab mSingleChatTab = mChatTabLayout.getTabAt(0);
        if (mSingleChatTab != null) {
            mSingleChatTab.setIcon(R.drawable.single_chat_icon);
        }

        TabLayout.Tab mGroupChatTab = mChatTabLayout.getTabAt(1);
        if (mGroupChatTab != null) {
            mGroupChatTab.setIcon(R.drawable.group_chat_icon);
        }

    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.create_chat})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.create_chat:
                if(mChatTabLayout.getSelectedTabPosition() == 0) {
                    ((BaseFragment) mChatTabPagerAdapter.getItem(mChatTabLayout.getSelectedTabPosition())).launchSingleChatAddActivity();
                } else {
                    ((BaseFragment) mChatTabPagerAdapter.getItem(mChatTabLayout.getSelectedTabPosition())).launchCreateGroupChatActivity();
                }
                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        switch (tab.getPosition()) {
            case 0:
                mCreateChatBtn.setImageResource(R.drawable.add_person_icon);
                break;
            case 1:
                mCreateChatBtn.setImageResource(R.drawable.add_group_icon);
                break;
        }

        ((BaseFragment) mChatTabPagerAdapter.getItem(mChatTabLayout.getSelectedTabPosition())).callGetChatRoom(mMyProfileModel);

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NEW_SINGLE_CHAT) {

            if(resultCode == RESULT_OK) {

                ProfileResModel mSelectedProfile = (ProfileResModel) data.getExtras().get(ProfileModel.OTHERS_PROFILE_RES_MODEL);

                SingleChatRoomModel mSingleChatRoomModel = new SingleChatRoomModel();

                List<SingleChatRoomResModel> mSingleChatRoomResModelList = new ArrayList<>();

                SingleChatRoomResModel mSingleChatRoomResModel = new SingleChatRoomResModel();
                mSingleChatRoomResModel.setFromProfileID(mMyProfileModel.getID());
                assert mSelectedProfile != null;
                mSingleChatRoomResModel.setToProfileID(mSelectedProfile.getID());
                String mFromToRelation = mMyProfileModel.getID() + "_" + mSelectedProfile.getID();
                mSingleChatRoomResModel.setFromToRelation(mFromToRelation);

                mSingleChatRoomResModelList.add(mSingleChatRoomResModel);

                mSingleChatRoomResModel = new SingleChatRoomResModel();
                mSingleChatRoomResModel.setFromProfileID(mSelectedProfile.getID());
                mSingleChatRoomResModel.setToProfileID(mMyProfileModel.getID());
                mFromToRelation = mSelectedProfile.getID() + "_" + mMyProfileModel.getID();
                mSingleChatRoomResModel.setFromToRelation(mFromToRelation);

                mSingleChatRoomResModelList.add(mSingleChatRoomResModel);

                mSingleChatRoomModel.setResource(mSingleChatRoomResModelList);

                RetrofitClient.getRetrofitInstance().callCreateSingleChatRoom(this, mSingleChatRoomModel, RetrofitClient.CREATE_SINGLE_CHAT_ROOM);

            }

        } else if(requestCode == ADD_NEW_GROUP_CHAT) {

            if(resultCode == RESULT_OK) {

                GroupChatRoomResModel mGroupChatRoomResModel = (GroupChatRoomResModel) data.getExtras().get(GroupChatRoomModel.GRP_CHAT_ROOM_RES_MODEL);
                ((BaseFragment) mChatTabPagerAdapter.getItem(mChatTabLayout.getSelectedTabPosition())).callUpdateGroupChatRoom(mGroupChatRoomResModel);

            }

        }

    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        if (responseObj instanceof SingleChatRoomModel) {

            SingleChatRoomModel mSingleChatRoomModel = (SingleChatRoomModel) responseObj;

            switch (responseType) {

                case RetrofitClient.GET_SINGLE_CHAT_ROOM:

                    ((BaseFragment) mChatTabPagerAdapter.getItem(mChatTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);

                    break;

                case RetrofitClient.CREATE_SINGLE_CHAT_ROOM:

                    if (mSingleChatRoomModel.getResource() != null && mSingleChatRoomModel.getResource().size() > 0) {

                        ((BaseFragment) mChatTabPagerAdapter.getItem(mChatTabLayout.getSelectedTabPosition())).callUpdateSingleChatRoom(mSingleChatRoomModel.getResource().get(0));

                    }

                    break;

            }

        } else if (responseObj instanceof GroupChatRoomModel) {

            switch (responseType) {

                case RetrofitClient.GET_GRP_CHAT_ROOM_RESPONSE:

                    ((BaseFragment) mChatTabPagerAdapter.getItem(mChatTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);

                    break;

            }

        } else if (responseObj instanceof SessionModel) {

            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }

            ((BaseFragment) mChatTabPagerAdapter.getItem(0)).setRefresh(true);
            ((BaseFragment) mChatTabPagerAdapter.getItem(1)).setRefresh(true);
            ((BaseFragment) mChatTabPagerAdapter.getItem(mChatTabLayout.getSelectedTabPosition())).callGetChatRoom(mMyProfileModel);

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
