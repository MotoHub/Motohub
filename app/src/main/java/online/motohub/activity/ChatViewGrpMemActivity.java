package online.motohub.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.adapter.GrpChatMemAdapter;
import online.motohub.dialog.DialogManager;
import online.motohub.model.GroupChatRoomModel;
import online.motohub.model.GroupChatRoomResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;

public class ChatViewGrpMemActivity extends BaseActivity {

    @BindView(R.id.grpChatMemLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.grp_chat_img)
    CircleImageView mGrpChatImg;

    @BindView(R.id.grp_name_tv)
    TextView mGrpNameTv;

    @BindView(R.id.grp_members_rv)
    RecyclerView mGrpChatMemRv;

    private GroupChatRoomResModel mGroupChatRoomResModel;
    private int mGrpChatRoomID;

    private List<ProfileResModel> mGrpMemListData;
    private GrpChatMemAdapter mGrpChatMemAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_grp_view_mem);

        ButterKnife.bind(this);

        initViews();

    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initViews() {

        //ProfileResModel mMyProfileResModel = (ProfileResModel) getIntent().getExtras().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        //ProfileResModel mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        ProfileResModel mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);

        String mToolbarTitle = getIntent().getExtras().getString(EventsWhoIsGoingActivity.TOOLBAR_TITLE);

        mGroupChatRoomResModel = (GroupChatRoomResModel) getIntent().getExtras().getSerializable(GroupChatRoomModel.GRP_CHAT_ROOM_RES_MODEL);

        mGrpMemListData = new ArrayList<>();

        mGrpChatMemAdapter = new GrpChatMemAdapter(mGrpMemListData, this, mMyProfileResModel);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mGrpChatMemRv.setLayoutManager(mLinearLayoutManager);

        mGrpChatMemRv.setAdapter(mGrpChatMemAdapter);

        if (mGroupChatRoomResModel == null) {

            mGrpChatRoomID = getIntent().getExtras().getInt(GroupChatRoomModel.GRP_CHAT_ROOM_ID);

            getGroupChatRoom();

        } else {

            mGrpChatRoomID = mGroupChatRoomResModel.getID();

            setImageWithGlide(mGrpChatImg, mGroupChatRoomResModel.getGroupPicture(), R.drawable.default_group_icon);

            mGrpNameTv.setText(mGroupChatRoomResModel.getGroupName());

            getGroupMembers();

        }

        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

    }

    private void getGroupChatRoom() {

        String mFilter = "id=" + mGrpChatRoomID;

        RetrofitClient.getRetrofitInstance().callGetGrpChatRoom(this, mFilter, RetrofitClient.GET_GRP_CHAT_ROOM_RESPONSE);

    }

    private void getGroupMembers() {

        String mFilter = "id IN (" + mGroupChatRoomResModel.getGroupMemProfileID() + ")";

        RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);

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
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        if (responseObj instanceof GroupChatRoomModel) {

            GroupChatRoomModel mGroupChatRoomModel = (GroupChatRoomModel) responseObj;

            switch (responseType) {

                case RetrofitClient.GET_GRP_CHAT_ROOM_RESPONSE:

                    if (mGroupChatRoomModel.getResource() != null && mGroupChatRoomModel.getResource().size() > 0) {

                        mGroupChatRoomResModel = mGroupChatRoomModel.getResource().get(0);

                        setImageWithGlide(mGrpChatImg, mGroupChatRoomResModel.getGroupPicture(), R.drawable.default_group_icon);

                        mGrpNameTv.setText(mGroupChatRoomResModel.getGroupName());

                        getGroupMembers();

                    }

                    break;

            }

        }
        if (responseObj instanceof ProfileModel) {

            ProfileModel mProfileModel = (ProfileModel) responseObj;

            switch (responseType) {

                case RetrofitClient.GET_PROFILE_RESPONSE:

                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        mGrpMemListData.clear();
                        mGrpMemListData.addAll(mProfileModel.getResource());
                        mGrpChatMemAdapter.notifyDataSetChanged();
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

            getGroupChatRoom();

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
