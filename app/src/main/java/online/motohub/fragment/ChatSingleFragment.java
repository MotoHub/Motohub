package online.motohub.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.ChatSingleAddActivity;
import online.motohub.adapter.SingleChatRoomAdapter;
import online.motohub.model.BlockedUserResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SingleChatRoomModel;
import online.motohub.model.SingleChatRoomResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.Utility;

import static online.motohub.activity.ChatHomeActivity.ADD_NEW_SINGLE_CHAT;

public class ChatSingleFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView mSingleChatRoomRecyclerView;

    private Unbinder mUnBinder;

    private boolean mRefresh = true;

    private List<SingleChatRoomResModel> mSingleChatRoomList;
    private SingleChatRoomAdapter mSingleChatRoomAdapter;
    private ProfileResModel mMyProfileResModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.widget_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUnBinder = ButterKnife.bind(this, view);

        initView();

    }

    private void initView() {

        this.mMyProfileResModel = (ProfileResModel) getArguments().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mSingleChatRoomRecyclerView.setLayoutManager(mLinearLayoutManager);

        mSingleChatRoomList = new ArrayList<>();
        mSingleChatRoomAdapter = new SingleChatRoomAdapter(mSingleChatRoomList, getActivity(), mMyProfileResModel);
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
    public void setRefresh(boolean refresh) {
        super.setRefresh(refresh);

        this.mRefresh = refresh;

    }

    @Override
    public void launchSingleChatAddActivity() {
        super.launchSingleChatAddActivity();

        StringBuilder mSingleChatProfileIDs = new StringBuilder();

        for(SingleChatRoomResModel mSingleChatRoomResModel : mSingleChatRoomList) {
            mSingleChatProfileIDs.append(mSingleChatRoomResModel.getToProfileID()).append(",");
        }

        if(mSingleChatProfileIDs.length() > 1) {
            mSingleChatProfileIDs.deleteCharAt(mSingleChatProfileIDs.length() - 1);
        }

        getActivity().startActivityForResult(new Intent(getActivity(), ChatSingleAddActivity.class)
                .putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel)
                .putExtra(SingleChatRoomModel.SINGLE_CHAT_ROOM_ID, mSingleChatProfileIDs.toString()), ADD_NEW_SINGLE_CHAT);

    }

    private void getSingleChatRoomList() {
        String mBlockedUsers = Utility.getInstance().getMyBlockedUsersID(mMyProfileResModel.getBlockedUserProfilesByProfileID(),
                mMyProfileResModel.getBlockeduserprofiles_by_BlockedProfileID());

        String mFilter;

        if(mBlockedUsers.trim().isEmpty()) {
            mFilter = "(FromProfileID = " + mMyProfileResModel.getID() + ")";
        } else {
            mFilter = "(FromProfileID = " + mMyProfileResModel.getID() + ") AND (ID NOT IN (" + mBlockedUsers + "))";
        }

        if (this.isVisible() && mSingleChatRoomList != null) {
            mSingleChatRoomList.clear();
            mSingleChatRoomAdapter.notifyDataSetChanged();
            RetrofitClient.getRetrofitInstance().callGetSingleChatRoom((BaseActivity) getActivity(), mFilter, RetrofitClient.GET_SINGLE_CHAT_ROOM);
        }

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

                    mSingleChatRoomList.clear();

                    if (mSingleChatRoomModel.getResource() != null && mSingleChatRoomModel.getResource().size() > 0) {

                        mSingleChatRoomList.addAll(mSingleChatRoomModel.getResource());

                    }

                    mSingleChatRoomAdapter.notifyDataSetChanged();

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
