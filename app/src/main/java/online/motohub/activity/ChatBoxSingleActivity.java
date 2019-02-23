package online.motohub.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
import online.motohub.adapter.ChatBoxSingleAdapter;
import online.motohub.application.MotoHub;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.ImageModel;
import online.motohub.model.NotificationModel1;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.model.SingleChatMsgModel;
import online.motohub.model.SingleChatMsgResModel;
import online.motohub.model.SingleChatRoomModel;
import online.motohub.model.SingleChatRoomResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.CommonAPI;
import online.motohub.util.DialogManager;
import online.motohub.util.NotificationUtils1;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.Utility;

public class ChatBoxSingleActivity extends BaseActivity implements ChatBoxSingleAdapter.TotalRetrofitMsgResultCount {

    public static final String NOTIFY_MSG_READ = "online.motohub.NOTIFY_MSG_READ";

    private static final int mDataLimit = 15;
    @BindView(R.id.chat_box_layout)
    RelativeLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mSingleChatMsgRv;
    @BindView(R.id.writeChatEt)
    EditText mMessageEt;

    @BindView(R.id.imageConstraintLay)
    ConstraintLayout mImageConstraintLay;
    @BindView(R.id.iv_post_image)
    ImageView mIvPostImage;
    @BindView(R.id.fileAttachImgBtn)
    ImageView mfileAttachImgBtn;

    private LinearLayoutManager mLinearLayoutManager;
    private SingleChatRoomResModel mSingleChatRoomResModel;
    private ProfileResModel mMyProfileResModel;
    private List<SingleChatMsgResModel> mSingleChatMsgList;
    private ChatBoxSingleAdapter mSingleChatMsgAdapter;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updatePushMsg(intent);
        }
    };
    private int mMsgRvOffset = 0, mMsgRvTotalCount = -1;
    private boolean mIsMsgRvLoading = true;
    private boolean mIsnewMSG = true;
    private String mChatRelation = "";
    private String mPostImgUri = null;
    private String imgUrl = null;
    private String message;

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
                JSONObject mJsonObjectEntry = new JSONObject(getIntent().getExtras().getString(MyFireBaseMessagingService.ENTRY_JSON_OBJ));
                JSONObject mDetailsObj = mJsonObjectEntry.getJSONObject("Details");
                mSingleChatRoomResModel = new SingleChatRoomResModel();
                mSingleChatRoomResModel.setFromProfileID(Integer.parseInt(mDetailsObj.get(MyFireBaseMessagingService.TO_PROFILE_ID).toString()));
                mSingleChatRoomResModel.setToProfileID(Integer.parseInt(mDetailsObj.get(MyFireBaseMessagingService.FROM_PROFILE_ID).toString()));
                ProfileResModel mProfileResModel = new ProfileResModel();
                mProfileResModel.setProfilePicture(mDetailsObj.getString(MyFireBaseMessagingService.PROFILE_PICTURE));
                mProfileResModel.setID(Integer.parseInt(mDetailsObj.get(MyFireBaseMessagingService.FROM_PROFILE_ID).toString()));
                mProfileResModel.setUserID(Integer.parseInt(mDetailsObj.get(MyFireBaseMessagingService.FROM_USER_ID).toString()));
                mProfileResModel.setProfileType(Integer.parseInt(BaseActivity.SPECTATOR));
                mProfileResModel.setSpectatorName(mDetailsObj.getString(MyFireBaseMessagingService.SENDER_NAME));
                mSingleChatRoomResModel.setProfilesByToProfileID(mProfileResModel);
                getProfiles(Integer.parseInt(mDetailsObj.get(MyFireBaseMessagingService.TO_PROFILE_ID).toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            /*
            this.mMyProfileResModel = (ProfileResModel) getIntent().getExtras().get(ProfileModel.MY_PROFILE_RES_MODEL);*/
            //mSingleChatRoomResModel = MotoHub.getApplicationInstance().getmSingleChatRoomResModel();
            this.mSingleChatRoomResModel = (SingleChatRoomResModel) getIntent().getExtras().get(SingleChatRoomModel.SINGLE_CHAT_ROOM_RES_MODEL);
            //mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
            mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
            setViews();
        }
    }

    private void setViews() {
        assert mSingleChatRoomResModel != null;
        assert mMyProfileResModel != null;
        String mToolbarTitle;
        mToolbarTitle = Utility.getInstance().getUserName(mSingleChatRoomResModel.getProfilesByToProfileID());
        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager.setReverseLayout(true);
        mSingleChatMsgRv.setItemAnimator(new DefaultItemAnimator());
        mSingleChatMsgRv.setLayoutManager(mLinearLayoutManager);
        mSingleChatMsgRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        getSingleChatMsg();
                    }
                }
            }
        });
        mSingleChatMsgList = new ArrayList<>();
        mSingleChatMsgAdapter = new ChatBoxSingleAdapter(mSingleChatMsgList, this, mSingleChatRoomResModel, mMyProfileResModel);
        mSingleChatMsgRv.setAdapter(mSingleChatMsgAdapter);
        mSingleChatMsgRv.smoothScrollToPosition(mSingleChatMsgAdapter.getItemCount());
        /*if (mSingleChatMsgAdapter.getItemCount() > 1) {
            mSingleChatMsgRv.getLayoutManager().smoothScrollToPosition(mSingleChatMsgRv, null, mSingleChatMsgRv.getAdapter().getItemCount() - 1);
        }*/
        getSingleChatMsg();
    }

    private void getProfiles(int profileID) {
        String mFilter = "ID = " + profileID;
        RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, new IntentFilter(MyFireBaseMessagingService.PUSH_MSG_RECEIVER_ACTION));
        MotoHub.getApplicationInstance().singleChatOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
        MotoHub.getApplicationInstance().singleChatOnPause();
    }

    private void updatePushMsg(Intent intent) {
        try {
            JSONObject mJsonObjectEntry = new JSONObject(intent.getStringExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ));
            JSONObject mDetailsObj = mJsonObjectEntry.getJSONObject("Details");
            if ((mSingleChatRoomResModel.getFromProfileID() != Integer.parseInt(mDetailsObj.get(MyFireBaseMessagingService.TO_PROFILE_ID).toString()))
                    && (mSingleChatRoomResModel.getToProfileID() != Integer.parseInt(mDetailsObj.get(MyFireBaseMessagingService.FROM_PROFILE_ID).toString()))) {
                NotificationModel1 model1 = new NotificationModel1();
                model1.setMainObj(mJsonObjectEntry);
                model1.setForceNotification(true);
                new NotificationUtils1(this, model1);
                return;
            }
            SingleChatMsgResModel mSingleChatMsgResModel = new SingleChatMsgResModel();
            mSingleChatMsgResModel.setToUserID(Integer.parseInt(mDetailsObj.get(MyFireBaseMessagingService.TO_USER_ID).toString()));
            mSingleChatMsgResModel.setCreatedAt(mDetailsObj.getString(MyFireBaseMessagingService.CREATED_AT));
            mSingleChatMsgResModel.setFromUserID(Integer.parseInt(mDetailsObj.get(MyFireBaseMessagingService.FROM_USER_ID).toString()));
            mSingleChatMsgResModel.setFromProfileID(Integer.parseInt(mDetailsObj.get(MyFireBaseMessagingService.FROM_PROFILE_ID).toString()));
            mSingleChatMsgResModel.setID(Integer.parseInt(mDetailsObj.get(MyFireBaseMessagingService.ID).toString()));
            mSingleChatMsgResModel.setMessage(mDetailsObj.getString(MyFireBaseMessagingService.MESSAGE));
            mSingleChatMsgResModel.setToProfileID(Integer.parseInt(mDetailsObj.get(MyFireBaseMessagingService.TO_PROFILE_ID).toString()));
            mSingleChatMsgResModel.setMsgType(mDetailsObj.getString(MyFireBaseMessagingService.MSG_TYPE));
            String photoMessage = "";
            if (mDetailsObj.has(MyFireBaseMessagingService.PHOTO_MESSAGE)) {
                photoMessage = mDetailsObj.getString(MyFireBaseMessagingService.PHOTO_MESSAGE);
            } else {
                photoMessage = "";
            }
            mSingleChatMsgResModel.setPhotoMessage(photoMessage);
            mMsgRvTotalCount = mMsgRvTotalCount + 1;
            mSingleChatMsgList.add(0, mSingleChatMsgResModel);
            mSingleChatMsgAdapter.notifyDataSetChanged();
            mSingleChatMsgRv.smoothScrollToPosition(0);
            /*if (mSingleChatMsgAdapter.getItemCount() > 1) {
                mSingleChatMsgRv.getLayoutManager().smoothScrollToPosition(mSingleChatMsgRv, null, mSingleChatMsgRv.getAdapter().getItemCount() - 1);
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getSingleChatMsg() {
        String mFilter = "((FromProfileID=" + mSingleChatRoomResModel.getFromProfileID()
                + ") AND (ToProfileID=" + mSingleChatRoomResModel.getToProfileID()
                + ")) OR ((FromProfileID=" + mSingleChatRoomResModel.getToProfileID()
                + ") AND (ToProfileID=" + mSingleChatRoomResModel.getFromProfileID() + "))";
        RetrofitClient.getRetrofitInstance()
                .callGetSingleChatMsg(this, mFilter, RetrofitClient.GET_SINGLE_CHAT_MSG, mDataLimit, mMsgRvOffset);
    }

    @Override
    public void onBackPressed() {
        Intent mintent = new Intent();
        mintent.putExtra(SingleChatRoomModel.SINGLE_CHAT_ROOM_RES_MODEL, mSingleChatRoomResModel.getToProfileID());
        setResult(RESULT_OK, mintent);
        finish();
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.send_btn, R.id.fileAttachImgBtn, R.id.iv_remove_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                Intent mintent = new Intent();
                mintent.putExtra(SingleChatRoomModel.SINGLE_CHAT_ROOM_RES_MODEL, mSingleChatRoomResModel.getToProfileID());
                setResult(RESULT_OK, mintent);
                finish();
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

    private void clearFields() {
        mImageConstraintLay.setVisibility(View.GONE);
        mMessageEt.setText("");
        mfileAttachImgBtn.setVisibility(View.VISIBLE);
        mPostImgUri = null;
        imgUrl = null;
    }

    private void sendMessage() {
        try {
            String mMessage = mMessageEt.getText().toString().trim();
            if (mPostImgUri != null) {
                uploadPicture(mPostImgUri);
            } else {
                if (mMessage.isEmpty()) {
                    showToast(getApplicationContext(), getString(R.string.write_something));
                    return;
                }
                message = URLEncoder.encode(mMessageEt.getText().toString(), "UTF-8");
                if (mChatRelation.equals("")) {
                    mChatRelation = mMyProfileResModel.getID() + "_" + mSingleChatRoomResModel.getToProfileID();
                }
                CommonAPI.getInstance().callSendSingleChatMsg(this,
                        mMyProfileResModel.getID(), mMyProfileResModel.getUserID(),
                        mSingleChatRoomResModel.getProfilesByToProfileID().getID(),
                        mSingleChatRoomResModel.getProfilesByToProfileID().getUserID(),
                        message, mChatRelation, imgUrl != null ? "media" : "text", imgUrl != null ? imgUrl : "");
                clearFields();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void uploadPicture(String mCommentImgUri) {
        File mFile = new File(Uri.parse(mCommentImgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", "Chat_" + mFile.getName(), requestBody);
        RetrofitClient.getRetrofitInstance().callUploadChatImg(this, filePart, RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE);
    }

    private void setMsgStatus() {
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonobj = new JsonObject();
        jsonobj.addProperty(SingleChatMsgResModel.MessageStatus, true);
        jsonArray.add(jsonobj);
        String mFilter = "((messagestatus=false)AND((FromProfileID=" + mSingleChatRoomResModel.getToProfileID()
                + ")and(ToProfileID=" + mMyProfileResModel.getID() + ")))";
        RetrofitClient.getRetrofitInstance().CallSetMsgStatus(this, mFilter, jsonArray, RetrofitClient.SET_MSG_STATUS);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof SingleChatMsgModel) {
            String data = new Gson().toJson(responseObj);
            SingleChatMsgModel mSingleChatMsgModel = (SingleChatMsgModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_SINGLE_CHAT_MSG:
                    if (mSingleChatMsgModel.getResource() != null && mSingleChatMsgModel.getResource().size() > 0) {
                        mIsnewMSG = false;
                        mMsgRvTotalCount = mSingleChatMsgModel.getMeta().getCount();
                        mIsMsgRvLoading = false;
                        if (mMsgRvOffset == 0) {
                            mSingleChatMsgList.clear();
                        }
                        mSingleChatMsgList.addAll(mSingleChatMsgModel.getResource());
                        mChatRelation = mSingleChatMsgList.get(0).getmChatRelation();
                        mSingleChatMsgAdapter.notifyDataSetChanged();
                        if (mMsgRvOffset == 0) {
                            mSingleChatMsgRv.smoothScrollToPosition(0);
                            /*if (mSingleChatMsgAdapter.getItemCount() > 1) {
                                mSingleChatMsgRv.getLayoutManager().smoothScrollToPosition(mSingleChatMsgRv, null, mSingleChatMsgRv.getAdapter().getItemCount() - 1);
                            }*/
                        }
                        mMsgRvOffset = mMsgRvOffset + mDataLimit;
                        setMsgStatus();
                    } else {
                        if (mMsgRvOffset == 0) {
                            mMsgRvTotalCount = 0;
                            mSingleChatMsgAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case RetrofitClient.SEND_SINGLE_CHAT_MSG:
                    if (mSingleChatMsgModel.getResource() != null && mSingleChatMsgModel.getResource().size() > 0) {
                        mSingleChatMsgList.add(0, mSingleChatMsgModel.getResource().get(0));
                        mSingleChatMsgAdapter.notifyDataSetChanged();
                        mSingleChatMsgRv.smoothScrollToPosition(0);
                        mMsgRvTotalCount = mMsgRvTotalCount + 1;
                        mMessageEt.setText("");
                    }
                    break;
                case RetrofitClient.SET_MSG_STATUS:
                    break;
            }
        } else if (responseObj instanceof SessionModel) {
            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            getSingleChatMsg();
        } else if (responseObj instanceof ProfileModel) {
            ProfileModel mProfileModel = (ProfileModel) responseObj;
            mMyProfileResModel = mProfileModel.getResource().get(0);
            setViews();
        } else if (responseObj instanceof ImageModel) {
            ImageModel mImageModel = (ImageModel) responseObj;
            switch (responseType) {
                case RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE:
                    //update the record in database/tablet
                    imgUrl = getHttpFilePath(mImageModel.getmModels().get(0).getPath());
                    try {
                        //callPostFeedComments(imgUrl, mPostID, mMyProfileResModel.getID());
                        String mMessage = mMessageEt.getText().toString().trim();
                        if (mChatRelation.equals("")) {
                            mChatRelation = mMyProfileResModel.getID() + "_" + mSingleChatRoomResModel.getToProfileID();
                        }
                        message = URLEncoder.encode(mMessage, "UTF-8");
                        CommonAPI.getInstance().callSendSingleChatMsg(this,
                                mMyProfileResModel.getID(), mMyProfileResModel.getUserID(),
                                mSingleChatRoomResModel.getProfilesByToProfileID().getID(),
                                mSingleChatRoomResModel.getProfilesByToProfileID().getUserID(),
                                message, mChatRelation, imgUrl != null ? "media" : "text", imgUrl != null ? imgUrl : "");
                        clearFields();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
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
