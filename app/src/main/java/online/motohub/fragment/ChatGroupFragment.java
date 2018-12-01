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
import online.motohub.activity.ChatCreateGroupActivity;
import online.motohub.activity.ChatHomeActivity;
import online.motohub.adapter.GroupChatRoomAdapter;
import online.motohub.application.MotoHub;
import online.motohub.model.GroupChatRoomModel;
import online.motohub.model.GroupChatRoomResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.retrofit.RetrofitClient;

public class ChatGroupFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView mGroupChatRoomRecyclerView;

    private Unbinder mUnBinder;

    private boolean mRefresh = true;

    private List<GroupChatRoomResModel> mGroupChatRoomList;
    private GroupChatRoomAdapter mGroupChatRoomAdapter;
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

        //this.mMyProfileResModel = (ProfileResModel) getArguments().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        //mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mGroupChatRoomRecyclerView.setLayoutManager(mLinearLayoutManager);

        mGroupChatRoomList = new ArrayList<>();
        mGroupChatRoomAdapter = new GroupChatRoomAdapter(mGroupChatRoomList, getActivity(), mMyProfileResModel);
        mGroupChatRoomRecyclerView.setAdapter(mGroupChatRoomAdapter);

    }

    @Override
    public void callGetChatRoom(ProfileResModel myProfileModel) {
        super.callGetChatRoom(myProfileModel);

        this.mMyProfileResModel = myProfileModel;

        if (mRefresh) {
            getGrpChatRoomList();
        }

    }

    @Override
    public void setRefresh(boolean refresh) {
        super.setRefresh(refresh);

        this.mRefresh = refresh;

    }

    @Override
    public void launchCreateGroupChatActivity() {
        super.launchCreateGroupChatActivity();
        //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
        EventBus.getDefault().postSticky(mMyProfileResModel);
        getActivity().startActivityForResult(new Intent(getActivity(), ChatCreateGroupActivity.class)
                /*.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel)*/, ChatHomeActivity.ADD_NEW_GROUP_CHAT);

    }

    private void getGrpChatRoomList() {

        String mFilter = "(CreatedByProfileID=" + mMyProfileResModel.getID() + ") OR " +
                "(GroupMemProfileID LIKE '%," + mMyProfileResModel.getID() + ",%')";

        RetrofitClient.getRetrofitInstance().callGetGrpChatRoom((BaseActivity) getActivity(), mFilter, RetrofitClient.GET_GRP_CHAT_ROOM_RESPONSE);

    }

    @Override
    public void callUpdateGroupChatRoom(GroupChatRoomResModel groupChatRoomResModel) {
        super.callUpdateGroupChatRoom(groupChatRoomResModel);

        mGroupChatRoomList.add(0, groupChatRoomResModel);
        mGroupChatRoomAdapter.notifyDataSetChanged();

    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        if (responseObj instanceof GroupChatRoomModel) {

            GroupChatRoomModel mGroupChatRoomModel = (GroupChatRoomModel) responseObj;

            switch (responseType) {

                case RetrofitClient.GET_GRP_CHAT_ROOM_RESPONSE:

                    mRefresh = false;

                    mGroupChatRoomList.clear();

                    if (mGroupChatRoomModel.getResource() != null && mGroupChatRoomModel.getResource().size() > 0) {

                        mGroupChatRoomList.addAll(mGroupChatRoomModel.getResource());

                    }

                    mGroupChatRoomAdapter.notifyDataSetChanged();

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
