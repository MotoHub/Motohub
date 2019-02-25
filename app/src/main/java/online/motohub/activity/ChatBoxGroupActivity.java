package online.motohub.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import online.motohub.R;
import online.motohub.adapter.ChatBoxGroupMsgAdapter;
import online.motohub.application.MotoHub;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.GroupChatMsgModel;
import online.motohub.model.GroupChatMsgResModel;
import online.motohub.model.GroupChatRoomModel;
import online.motohub.model.GroupChatRoomResModel;
import online.motohub.model.ImageModel;
import online.motohub.model.NotificationModel1;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.DialogManager;
import online.motohub.util.NotificationUtils1;
import online.motohub.util.PreferenceUtils;

public class ChatBoxGroupActivity extends BaseActivity implements ChatBoxGroupMsgAdapter.TotalRetrofitMsgResultCount {

    private static final int mDataLimit = 15;

    @BindView(R.id.chat_box_layout)
    RelativeLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler_view)
    RecyclerView mGrpChatMsgRv;

    @BindView(R.id.writeChatEt)
    EditText mMessageEt;

    @BindView(R.id.imageConstraintLay)
    ConstraintLayout mImageConstraintLay;

    @BindView(R.id.iv_post_image)
    ImageView mIvPostImage;

    @BindView(R.id.fileAttachImgBtn)
    ImageView mfileAttachImgBtn;

    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<GroupChatMsgResModel> mGrpChatMsgList = new ArrayList<>();
    private ChatBoxGroupMsgAdapter mChatBoxGroupMsgAdapter;
    private GroupChatRoomResModel mGroupChatRoomResModel;
    private ProfileResModel mMyProfileResModel;
    private GroupChatMsgResModel mGrpChatMsgResModel;
    private int mGroupChatRoomID, mSenderProfileID = 0;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras().getBoolean(AppConstants.IS_FROM_GROUP_CHAT)) {
                updatePushMsg(intent);

            }
        }
    };
    private String mPostImgUri = null;
    private String imgUrl = null;
    private String message;
    private String mGroupName;
    private int mMsgRvOffset = 0, mMsgRvTotalCount = -1;
    private boolean mIsMsgRvLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        ButterKnife.bind(this);
        initViews();
        setupUI(mCoordinatorLayout);
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initViews() {
        if (getIntent().getExtras().getBoolean(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY)) {
            try {
                assert getIntent().getExtras() != null;
                JSONObject mJsonObject = new JSONObject(getIntent().getExtras().getString(MyFireBaseMessagingService.ENTRY_JSON_OBJ));
                JSONObject mDetailsObj = (mJsonObject.getJSONObject("Details"));
                mGroupChatRoomID = Integer.parseInt(mDetailsObj.get(GroupChatRoomModel.GRP_CHAT_ROOM_ID).toString());
                mGroupName = (mDetailsObj.getJSONObject("GroupChat").get(MyFireBaseMessagingService.GRP_NAME).toString());
                getMotoProfiles();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            /*mMyProfileResModel = (ProfileResModel) getIntent().getExtras().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
            mGroupChatRoomResModel = (GroupChatRoomResModel) getIntent().getExtras().getSerializable(GroupChatRoomModel.GRP_CHAT_ROOM_RES_MODEL);*/
            //mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
            mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
            mGroupChatRoomResModel = (GroupChatRoomResModel) getIntent().getExtras().getSerializable(GroupChatRoomModel.GRP_CHAT_ROOM_RES_MODEL);
            assert mGroupChatRoomResModel != null;
            mGroupChatRoomID = mGroupChatRoomResModel.getID();
            mGroupName = mGroupChatRoomResModel.getGroupName();
            assert mMyProfileResModel != null;
            mSenderProfileID = mMyProfileResModel.getID();
            setViews();
        }
    }

    public void setViews() {
        setToolbar(mToolbar, mGroupName);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager.setReverseLayout(true);
        mGrpChatMsgRv.setLayoutManager(mLinearLayoutManager);
        mGrpChatMsgRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int mVisibleItemCount = mLinearLayoutManager.getChildCount();
                int mTotalItemCount = mLinearLayoutManager.getItemCount();
                int mFirstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (!mIsMsgRvLoading && !(mMsgRvOffset >= mMsgRvTotalCount)) {
                    if ((mVisibleItemCount + mFirstVisibleItemPosition) >= mTotalItemCount
                            && mFirstVisibleItemPosition >= 0) {
                        mIsMsgRvLoading = true;
                        getGrpChatMsg();
                    }
                }
            }
        });
        mChatBoxGroupMsgAdapter = new ChatBoxGroupMsgAdapter(mMyProfileResModel, mGrpChatMsgList, this);
        mGrpChatMsgRv.setAdapter(mChatBoxGroupMsgAdapter);
        getGrpChatMsg();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, new IntentFilter(MyFireBaseMessagingService.PUSH_MSG_RECEIVER_ACTION));
        MotoHub.getApplicationInstance().grpChatOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
        MotoHub.getApplicationInstance().grpChatOnPause();
    }

    private void getMotoProfiles() {
        int mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        String mFilter = "UserID = " + mUserID;
        RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);
    }

    private void updatePushMsg(Intent intent) {
        if (Integer.parseInt(intent.getStringExtra(GroupChatRoomModel.GRP_CHAT_ROOM_ID)) == mGroupChatRoomID) {
            try {
                JSONObject mJsonObject = new JSONObject(intent.getStringExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ));
                JSONObject mDetailsObj = mJsonObject.getJSONObject("Details");
                JSONObject mGroupChatObj = mDetailsObj.getJSONObject("GroupChat");
                mGrpChatMsgResModel = new GroupChatMsgResModel();
                mGrpChatMsgResModel.setID(Integer.parseInt(mDetailsObj.getString(MyFireBaseMessagingService.ID)));
                mGrpChatMsgResModel.setGroupChatRoomID(Integer.parseInt(mDetailsObj.getString(GroupChatRoomModel.GRP_CHAT_ROOM_ID)));
                mGrpChatMsgResModel.setSenderUserID(Integer.parseInt(mDetailsObj.getString(MyFireBaseMessagingService.GRP_CHAT_SENDER_USER_ID)));
                mGrpChatMsgResModel.setSenderProfileID(Integer.parseInt(mDetailsObj.getString(MyFireBaseMessagingService.GRP_CHAT_SENDER_PROFILE_ID)));
                mGrpChatMsgResModel.setMessage(mDetailsObj.getString(MyFireBaseMessagingService.MESSAGE));
                mGrpChatMsgResModel.setCreatedAt(mDetailsObj.getString(MyFireBaseMessagingService.CREATED_AT));
                mGrpChatMsgResModel.setMsgType(mDetailsObj.getString(MyFireBaseMessagingService.MSG_TYPE));
                mGrpChatMsgResModel.setPhotoMessage(mDetailsObj.getString(MyFireBaseMessagingService.PHOTO_MESSAGE));
                ProfileResModel mProfileResModel = new ProfileResModel();
                mProfileResModel.setSpectatorName(mGroupChatObj.getString(MyFireBaseMessagingService.GROUP_SENDER_NAME));
                mProfileResModel.setProfilePicture(mGroupChatObj.getString(MyFireBaseMessagingService.GROUP_SENDER_PIC));
                mGrpChatMsgResModel.setProfilesBySenderProfileID(mProfileResModel);
                mMsgRvTotalCount = mMsgRvTotalCount + 1;
                mGrpChatMsgList.add(0, mGrpChatMsgResModel);
                mChatBoxGroupMsgAdapter.notifyDataSetChanged();
                mGrpChatMsgRv.scrollToPosition(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                NotificationModel1 model1 = new NotificationModel1();
                model1.setMainObj(new JSONObject(intent.getStringExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ)));
                model1.setForceNotification(true);
                new NotificationUtils1(this, model1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void getGrpChatMsg() {
        String mFilter = "GroupChatRoomID=" + mGroupChatRoomID;
        RetrofitClient.getRetrofitInstance().callGetGrpChatMsg(this, mFilter, RetrofitClient.GET_GRP_CHAT_MSG, mDataLimit, mMsgRvOffset);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.toolbar_title, R.id.send_btn, R.id.fileAttachImgBtn, R.id.iv_remove_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.toolbar_title:
                if (mMyProfileResModel == null) {
                    return;
                }
                Bundle mBundle = new Bundle();
                //mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
                //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                EventBus.getDefault().postSticky(mMyProfileResModel);
                mBundle.putString(EventsWhoIsGoingActivity.TOOLBAR_TITLE, mGroupName + " Member(s)");
                if (mGroupChatRoomResModel == null) {
                    mBundle.putInt(GroupChatRoomModel.GRP_CHAT_ROOM_ID, mGroupChatRoomID);
                } else {
                    mBundle.putSerializable(GroupChatRoomModel.GRP_CHAT_ROOM_RES_MODEL, mGroupChatRoomResModel);
                }
                startActivity(new Intent(this, ChatViewGrpMemActivity.class).putExtras(mBundle));
                break;
            case R.id.send_btn:
                sendMessage();
                break;
            case R.id.fileAttachImgBtn:
                showAppDialog(AppDialogFragment.BOTTOM_ADD_IMG_DIALOG, null);
                break;
            case R.id.iv_remove_image:
                clearFields();
                break;
        }
    }

    private void sendMessage() {
        try {
            String mMessage = mMessageEt.getText().toString().trim();
            if (mSenderProfileID == 0) {
                return;
            }
            if (mPostImgUri != null) {
                uploadPicture(mPostImgUri);
            } else {
                if (mMessage.isEmpty()) {
                    showToast(getApplicationContext(), getString(R.string.write_something));
                    return;
                }
                GroupChatMsgModel mGrpChatMsgModel = new GroupChatMsgModel();
                mGrpChatMsgResModel = new GroupChatMsgResModel();
                mGrpChatMsgResModel.setGroupChatRoomID(mGroupChatRoomID);
                mGrpChatMsgResModel.setMessage(URLEncoder.encode(mMessageEt.getText().toString(), "UTF-8"));
                mGrpChatMsgResModel.setSenderProfileID(mSenderProfileID);
                mGrpChatMsgResModel.setSenderUserID(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID));
                mGrpChatMsgResModel.setMsgType(imgUrl != null ? "media" : "text");
                mGrpChatMsgResModel.setPhotoMessage(imgUrl != null ? imgUrl : "");
                List<GroupChatMsgResModel> mGrpChatMsgResModelList = new ArrayList<>();
                mGrpChatMsgResModelList.add(mGrpChatMsgResModel);
                mGrpChatMsgModel.setResource(mGrpChatMsgResModelList);
                String data = new Gson().toJson(mGrpChatMsgModel);
                RetrofitClient.getRetrofitInstance().callSendGrpChatMsg(this, mGrpChatMsgModel, RetrofitClient.SEND_GRP_CHAT_MSG);
                clearFields();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void uploadPicture(String mCommentImgUri) {
        File mFile = new File(Uri.parse(mCommentImgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", "GroupChat_" + mFile.getName(), requestBody);
        RetrofitClient.getRetrofitInstance().callUploadChatImg(this, filePart, RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof ProfileModel) {
            ProfileModel mProfileModel = (ProfileModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_PROFILE_RESPONSE:
                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        ArrayList<ProfileResModel> mFullMPList = new ArrayList<>();
                        mFullMPList.addAll(mProfileModel.getResource());
                        mMyProfileResModel = mFullMPList.get(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS));
                        mSenderProfileID = mMyProfileResModel.getID();
                        setViews();
                    }
                    break;
            }
        } else if (responseObj instanceof GroupChatMsgModel) {
            GroupChatMsgModel mGrpChatMsgModel = (GroupChatMsgModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_GRP_CHAT_MSG:
                    if (mGrpChatMsgModel.getResource() != null && mGrpChatMsgModel.getResource().size() > 0) {
                        mMsgRvTotalCount = mGrpChatMsgModel.getMeta().getCount();
                        mIsMsgRvLoading = false;
                        if (mMsgRvOffset == 0) {
                            mGrpChatMsgList.clear();
                        }
                        mGrpChatMsgList.addAll(mGrpChatMsgModel.getResource());
                        mChatBoxGroupMsgAdapter.notifyDataSetChanged();
                        if (mMsgRvOffset == 0) {
                            mGrpChatMsgRv.scrollToPosition(0);
                        }
                        mMsgRvOffset = mMsgRvOffset + mDataLimit;
                    } else {
                        if (mMsgRvOffset == 0) {
                            mMsgRvTotalCount = 0;
                            mChatBoxGroupMsgAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case RetrofitClient.SEND_GRP_CHAT_MSG:
                    mChatBoxGroupMsgAdapter.notifyDataSetChanged();
                    mMsgRvTotalCount = mMsgRvTotalCount + 1;
                    mMessageEt.setText("");
                   /* mMsgRvTotalCount = mMsgRvTotalCount + 1;
                    mGrpChatMsgList.add(0, mGrpChatMsgResModel);
                    mChatBoxGroupMsgAdapter.notifyDataSetChanged();
                    mGrpChatMsgRv.scrollToPosition(0);*/
                    break;
            }
        } else if (responseObj instanceof SessionModel) {
            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            getGrpChatMsg();
        } else if (responseObj instanceof ImageModel) {
            ImageModel mImageModel = (ImageModel) responseObj;
            switch (responseType) {
                case RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE:
                    //update the record in database/tablet
                    imgUrl = getHttpFilePath(mImageModel.getmModels().get(0).getPath());
                    try {
                        String mMessage = mMessageEt.getText().toString().trim();
                        GroupChatMsgModel mGrpChatMsgModel = new GroupChatMsgModel();
                        mGrpChatMsgResModel = new GroupChatMsgResModel();
                        mGrpChatMsgResModel.setGroupChatRoomID(mGroupChatRoomID);
                        mGrpChatMsgResModel.setMessage(URLEncoder.encode(mMessage, "UTF-8"));
                        mGrpChatMsgResModel.setSenderProfileID(mSenderProfileID);
                        mGrpChatMsgResModel.setSenderUserID(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID));
                        mGrpChatMsgResModel.setMsgType(imgUrl != null ? "media" : "text");
                        mGrpChatMsgResModel.setPhotoMessage(imgUrl != null ? imgUrl : "");
                        List<GroupChatMsgResModel> mGrpChatMsgResModelList = new ArrayList<>();
                        mGrpChatMsgResModelList.add(mGrpChatMsgResModel);
                        mGrpChatMsgModel.setResource(mGrpChatMsgResModelList);
                        String data = new Gson().toJson(mGrpChatMsgModel);
                        RetrofitClient.getRetrofitInstance().callSendGrpChatMsg(this, mGrpChatMsgModel, RetrofitClient.SEND_GRP_CHAT_MSG);
                        clearFields();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void clearFields() {
        mImageConstraintLay.setVisibility(View.GONE);
        mMessageEt.setText("");
        mfileAttachImgBtn.setVisibility(View.VISIBLE);
        mPostImgUri = null;
        imgUrl = null;
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

    @Override
    public int getTotalMsgResultCount() {
        return mMsgRvTotalCount;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_CAPTURE_REQ:
                    Uri mCameraPicUri = getImgResultUri(data);
                    if (mCameraPicUri != null) {
                        try {
                            File mPostImgFile = compressedImgFile(mCameraPicUri, POST_IMAGE_NAME_TYPE, "");
                            mPostImgUri = Uri.fromFile(mPostImgFile).toString();
                            mImageConstraintLay.setVisibility(View.VISIBLE);
                            setImageWithGlide(mIvPostImage, Uri.parse(mPostImgUri), R.drawable.default_cover_img);
                            mfileAttachImgBtn.setVisibility(View.GONE);

                        } catch (Exception e) {
                            e.printStackTrace();
                            showSnackBar(mCoordinatorLayout, e.getMessage());
                        }
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
                case GALLERY_PIC_REQ:
                    assert data.getExtras() != null;
                    Uri mSelectedImgFileUri = (Uri) data.getExtras().get(PickerImageActivity.EXTRA_RESULT_DATA);
                    if (mSelectedImgFileUri != null) {
                        try {
                            String mProfileID = String.valueOf(mMyProfileResModel.getID());
                            File mPostImgFile = compressedImgFile(mSelectedImgFileUri,
                                    POST_IMAGE_NAME_TYPE, mProfileID);
                            mPostImgUri = Uri.fromFile(mPostImgFile).toString();
                            mImageConstraintLay.setVisibility(View.VISIBLE);
                            setImageWithGlide(mIvPostImage, Uri.parse(mPostImgUri), R.drawable.default_cover_img);
                            mfileAttachImgBtn.setVisibility(View.GONE);

                        } catch (Exception e) {
                            e.printStackTrace();
                            showSnackBar(mCoordinatorLayout, e.getMessage());
                        }
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
            }
        }
    }

}
