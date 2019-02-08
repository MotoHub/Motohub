package online.motohub.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.ChatSingleAddActivity;
import online.motohub.adapter.SingleChatRoomAdapter;
import online.motohub.application.MotoHub;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SingleChatCountModel;
import online.motohub.model.SingleChatMsg;
import online.motohub.model.SingleChatRoomModel;
import online.motohub.model.SingleChatRoomResModel;
import online.motohub.model.SingleChatUnreadCountModel;
import online.motohub.model.SingleChatUnreadMsgModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.Utility;

import static android.app.Activity.RESULT_OK;
import static online.motohub.activity.ChatHomeActivity.ADD_NEW_SINGLE_CHAT;

public class ChatSingleFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView mSingleChatRoomRecyclerView;
    private Unbinder mUnBinder;
    private boolean mRefresh = true;
    private List<SingleChatRoomResModel> mSingleChatRoomList;
    private List<SingleChatUnreadCountModel> mSingleChatMsgCount;
    private List<SingleChatMsg> mSingleChatUnreadMsg;
    private ProfileResModel mMyProfileResModel;
    private SingleChatRoomAdapter mSingleChatRoomAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.widget_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnBinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        //this.mMyProfileResModel = (ProfileResModel) getArguments().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        //mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSingleChatRoomRecyclerView.setLayoutManager(mLinearLayoutManager);
        mSingleChatRoomList = new ArrayList<>();
        mSingleChatMsgCount = new ArrayList<>();
        mSingleChatUnreadMsg = new ArrayList<>();
        mSingleChatRoomAdapter = new SingleChatRoomAdapter(mSingleChatRoomList, getActivity(),
                mMyProfileResModel, mSingleChatMsgCount, mSingleChatUnreadMsg);
        mSingleChatRoomRecyclerView.setAdapter(mSingleChatRoomAdapter);
        callGetChatRoom(mMyProfileResModel);
    }

    @Override
    public void callGetChatRoom(ProfileResModel myProfileModel) {
        super.callGetChatRoom(myProfileModel);
        this.mMyProfileResModel = myProfileModel;
        if (mRefresh) {
            getSingleChatRoomList();
        }
    }

    @Override
    public void getaddedChat(ProfileResModel myProfileResModel, boolean addedchat) {
        super.getaddedChat(myProfileResModel, addedchat);
        this.mMyProfileResModel = myProfileResModel;
        if (addedchat)
            getSingleChatRoomList();
    }

    @Override
    public void setRefresh(boolean refresh) {
        super.setRefresh(refresh);
        this.mRefresh = refresh;
    }

    @Override
    public void launchSingleChatAddActivity() {
        super.launchSingleChatAddActivity();
        StringBuilder mSingleChatProfileIDs = new StringBuilder();
        for (SingleChatRoomResModel mSingleChatRoomResModel : mSingleChatRoomList) {
            mSingleChatProfileIDs.append(mSingleChatRoomResModel.getToProfileID()).append(",");
        }
        if (mSingleChatProfileIDs.length() > 1) {
            mSingleChatProfileIDs.deleteCharAt(mSingleChatProfileIDs.length() - 1);
        }
        //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
        EventBus.getDefault().postSticky(mMyProfileResModel);
        getActivity().startActivityForResult(new Intent(getActivity(), ChatSingleAddActivity.class)
                /*.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel)*/
                .putExtra(SingleChatRoomModel.SINGLE_CHAT_ROOM_ID, mSingleChatProfileIDs.toString()), ADD_NEW_SINGLE_CHAT);
    }

    private void getSingleChatRoomList() {
        String mBlockedUsers = Utility.getInstance().getMyBlockedUsersID(mMyProfileResModel.getBlockedUserProfilesByProfileID(),
                mMyProfileResModel.getBlockeduserprofiles_by_BlockedProfileID());
        String mFilter;
        if (mBlockedUsers.trim().isEmpty()) {
            mFilter = "(FromProfileID = " + mMyProfileResModel.getID() + ")";
        } else {
            mFilter = "(FromProfileID = " + mMyProfileResModel.getID() + ") AND (ID NOT IN (" + mBlockedUsers + "))";
        }
        if (this.isVisible() && mSingleChatRoomList != null) {
            mSingleChatRoomList.clear();
            mSingleChatRoomAdapter.notifyDataSetChanged();
            RetrofitClient.getRetrofitInstance()
                    .callGetSingleChatRoom((BaseActivity) getActivity(), mFilter, RetrofitClient.GET_SINGLE_CHAT_ROOM);
        }
    }

    public void getUnreadMsgCount() {
        int myID = mMyProfileResModel.getID();
        String mFilter = "(messagestatus=false)AND(ToProfileID=" + myID + ")";
        RetrofitClient.getRetrofitInstance()
                .CallGetSingleChatCount(((BaseActivity) getActivity()), mFilter, RetrofitClient.GET_SINGLE_CHAT_COUNT);
    }

    public void getLastMsg() {
        int myID = mMyProfileResModel.getID();
        String mFilter = "((FromProfileID=" + myID + ")OR(ToProfileID=" + myID + "))";
        RetrofitClient.getRetrofitInstance()
                .CallGetSingleChatUnreadMsg(((BaseActivity) getActivity()), mFilter, RetrofitClient.GET_SINGLE_CHAT_LAST_MSG);
    }

    @Override
    public void callUpdateSingleChatRoom(SingleChatRoomResModel singleChatRoomResModel) {
        super.callUpdateSingleChatRoom(singleChatRoomResModel);
        mSingleChatRoomList.add(0, singleChatRoomResModel);
        mSingleChatRoomAdapter.notifyDataSetChanged();
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof SingleChatRoomModel) {
            SingleChatRoomModel mSingleChatRoomModel = (SingleChatRoomModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_SINGLE_CHAT_ROOM:
                    mRefresh = false;
                    if (mSingleChatRoomList != null)
                        mSingleChatRoomList.clear();
                    if (mSingleChatRoomModel.getResource() != null && mSingleChatRoomModel.getResource().size() > 0)
                        mSingleChatRoomList.addAll(mSingleChatRoomModel.getResource());
                    getUnreadMsgCount();
                    break;
            }
        } else if (responseObj instanceof SingleChatCountModel) {
            SingleChatCountModel mSingleChatCountModel = (SingleChatCountModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_SINGLE_CHAT_COUNT:
                    mSingleChatMsgCount.clear();
                    if (mSingleChatCountModel.getResource() != null && mSingleChatCountModel.getResource().size() > 0) {
                        mSingleChatMsgCount.addAll(mSingleChatCountModel.getResource());
                    }
                    getLastMsg();
                    break;
            }
        } else if (responseObj instanceof SingleChatUnreadMsgModel) {
            SingleChatUnreadMsgModel mSingleChatUnreadMsgModel = (SingleChatUnreadMsgModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_SINGLE_CHAT_LAST_MSG:
                    mSingleChatUnreadMsg.clear();
                    if (mSingleChatUnreadMsgModel.getResource() != null && mSingleChatUnreadMsgModel.getResource().size() > 0) {
                        mSingleChatUnreadMsg.addAll(mSingleChatUnreadMsgModel.getResource());
                    }
                    mSingleChatRoomAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RetrofitClient.GET_SINGLE_CHAT_COUNT:
                    getSingleChatRoomList();
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
    }

}