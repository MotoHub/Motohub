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
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import online.motohub.adapter.ChatBoxEventGrpAdapter;
import online.motohub.application.MotoHub;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.EventGrpChatMsgModel;
import online.motohub.model.EventGrpChatMsgResModel;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsWhoIsGoingModel;
import online.motohub.model.EventsWhoIsGoingResModel;
import online.motohub.model.ImageModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.Utility;

import static android.view.View.GONE;

public class ChatBoxEventGrpActivity extends BaseActivity implements ChatBoxEventGrpAdapter.TotalRetrofitMsgResultCount {

    private static final int mDataLimit = 15;

    @BindView(R.id.chat_box_layout)
    RelativeLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler_view)
    RecyclerView mEventGrpChatMsgRv;

    @BindView(R.id.writeChatEt)
    EditText mMessageEt;

    @BindView(R.id.imageConstraintLay)
    ConstraintLayout mImageConstraintLay;
    @BindView(R.id.iv_post_image)
    ImageView mIvPostImage;
    @BindView(R.id.fileAttachImgBtn)
    ImageView mfileAttachImgBtn;

    @BindView(R.id.replyChatLayout)
    RelativeLayout mReplyChatLayout;
    @BindView(R.id.replyChatMsgTv)
    TextView mReplyChatMsgTv;
    @BindView(R.id.replyChatUserNameTv)
    TextView mReplyChatUserNameTv;
    @BindView(R.id.replyChatCloseIv)
    ImageView mReplyChatCloseIv;
    @BindView(R.id.replyChatImageIv)
    ImageView mReplyChatImageIv;
    @BindView(R.id.replyChatView)
    View mReplyChatView;


    private LinearLayoutManager mLinearLayoutManager;
    private List<EventsWhoIsGoingResModel> mEventsWhoIsGoingResModels;
    private ProfileResModel mMyProfileResModel;
    private List<EventGrpChatMsgResModel> mEventGrpChatMsgList;
    private ChatBoxEventGrpAdapter mChatBoxEventGrpAdapter;
    private int mEventID, mSenderProfileID = 0;

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!getIntent().getExtras().getBoolean(AppConstants.IS_FROM_LIVE_EVENT_CHAT))
                 updatePushMsg(intent);
        }
    };

    private String mEventName;
    private int mMsgRvOffset = 0, mMsgRvTotalCount = -1;
    private boolean mIsMsgRvLoading = true;

    private String mPostImgUri = null;
    private String imgUrl = "";
    private boolean isRepliedMsg = false;
    private int mReplyMsgID;
    private String mReplyMsg = "", mReplyUserName, mReplyImg = "";
    private int mReplyUserProfileID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        ButterKnife.bind(this);

        initViews();

    }

    @SuppressWarnings("unchecked")
    private void initViews() {

        if (getIntent().getExtras().getBoolean(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY)) {
            try {
                assert getIntent().getExtras() != null;
                JSONObject mJsonObject = new JSONObject(getIntent().getExtras().getString(MyFireBaseMessagingService.ENTRY_JSON_OBJ));

                    mEventID = Integer.parseInt(mJsonObject.getJSONObject("Details").get(EventsModel.EVENT_ID).toString());
                    mEventName = (mJsonObject.getJSONObject("Details").get(MyFireBaseMessagingService.EVENT_GRP_CHAT_EVENT_NAME).toString());

                getMotoProfiles();

                setView();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;

        } else {

            mMyProfileResModel = (ProfileResModel) getIntent().getExtras().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);

            mEventID = getIntent().getExtras().getInt(EventsModel.EVENT_ID);

            mSenderProfileID = mMyProfileResModel.getID();

            getEvent();

        }

    }


    private void setView(){
        setToolbar(mToolbar, mEventName);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager.setReverseLayout(true);

        mEventGrpChatMsgRv.setLayoutManager(mLinearLayoutManager);

        mEventGrpChatMsgRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        getEventGrpChatMsg();
                    }
                }

            }
        });

        mEventGrpChatMsgList = new ArrayList<>();
        mChatBoxEventGrpAdapter = new ChatBoxEventGrpAdapter(mEventGrpChatMsgList, this);
        mEventGrpChatMsgRv.setAdapter(mChatBoxEventGrpAdapter);
    }
    private void getEvent() {
        String mFilter = "ID=" + mEventID;
        RetrofitClient.getRetrofitInstance().callGetEvents(this,mFilter,RetrofitClient.GET_EVENTS_RESPONSE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, new IntentFilter(MyFireBaseMessagingService.PUSH_MSG_RECEIVER_ACTION));
        MotoHub.getApplicationInstance().eventGrpChatOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
        MotoHub.getApplicationInstance().eventGrpChatOnPause();
    }

    private void updatePushMsg(Intent intent) {

        if (Integer.parseInt(intent.getStringExtra(EventsModel.EVENT_ID)) == mEventID) {

            try {

                JSONObject mJsonObject = new JSONObject(intent.getStringExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ));
                JSONObject mDetailsObj = mJsonObject.getJSONObject("Details");

                EventGrpChatMsgResModel mEventGrpChatMsgResModel = new EventGrpChatMsgResModel();
                mEventGrpChatMsgResModel.setID(Integer.parseInt(mDetailsObj.getString(MyFireBaseMessagingService.ID)));
                mEventGrpChatMsgResModel.setEventID(Integer.parseInt(mDetailsObj.getString(EventsModel.EVENT_ID)));
                mEventGrpChatMsgResModel.setSenderUserID(Integer.parseInt(mDetailsObj.getString(MyFireBaseMessagingService.GRP_CHAT_SENDER_USER_ID)));
                if (!mDetailsObj.getString(MyFireBaseMessagingService.GRP_CHAT_SENDER_PROFILE_ID).isEmpty()) {
                    mEventGrpChatMsgResModel.setSenderProfileID(Integer.parseInt(mDetailsObj.getString(MyFireBaseMessagingService.GRP_CHAT_SENDER_PROFILE_ID)));
                }
                mEventGrpChatMsgResModel.setMessage(mDetailsObj.getString(MyFireBaseMessagingService.MESSAGE));
                mEventGrpChatMsgResModel.setUserType(mDetailsObj.getString(MyFireBaseMessagingService.EVENT_USER_TYPE));
                mEventGrpChatMsgResModel.setCreatedAt(mDetailsObj.getString(MyFireBaseMessagingService.CREATED_AT));
                mEventGrpChatMsgResModel.setMsgType(mDetailsObj.getString(MyFireBaseMessagingService.MSG_TYPE));
                mEventGrpChatMsgResModel.setPhotoMessage(mDetailsObj.getString(MyFireBaseMessagingService.PHOTO_MESSAGE));

                if (mDetailsObj.getString(MyFireBaseMessagingService.EVENT_USER_TYPE).isEmpty()) {
                    PromotersResModel mPromotersResModel = new PromotersResModel();
                    mPromotersResModel.setName(mDetailsObj.getString(MyFireBaseMessagingService.EVENT_GRP_CHAT_SENDER_NAME));
                    mPromotersResModel.setProfileImage(mDetailsObj.getString(MyFireBaseMessagingService.PROFILE_PICTURE));
                    mEventGrpChatMsgResModel.setPromoterBySenderUserID(mPromotersResModel);
                } else {
                    ProfileResModel mProfileResModel = new ProfileResModel();
                    mProfileResModel.setSpectatorName(mDetailsObj.getString(MyFireBaseMessagingService.EVENT_GRP_CHAT_SENDER_NAME));
                    mProfileResModel.setProfilePicture(mDetailsObj.getString(MyFireBaseMessagingService.PROFILE_PICTURE));
                    mEventGrpChatMsgResModel.setProfilesBySenderProfileID(mProfileResModel);
                }

              /*  if (mDetailsObj.getBoolean(AppConstants.IS_REPLY_CHAT_MSG)) {
                    mEventGrpChatMsgResModel.setRepliedMsgID(mDetailsObj.getInt(AppConstants.REPLY_CHAT_MSG_ID));
                    mEventGrpChatMsgResModel.setReplyMessage(URLDecoder.decode(mDetailsObj.getString(AppConstants.REPLY_CHAT_MSG),"UTF-8"));
                    mEventGrpChatMsgResModel.setReplyUserProfileID(mDetailsObj.getInt(AppConstants.REPLY_CHAT_USER_PROFILE_ID));
                    mEventGrpChatMsgResModel.setReplyUserName(mDetailsObj.getString(AppConstants.REPLY_CHAT_USER_NAME));
                    mEventGrpChatMsgResModel.setIsRepliedMsg(mDetailsObj.getBoolean(AppConstants.IS_REPLY_CHAT_MSG));
                    mEventGrpChatMsgResModel.setReplyImage(mDetailsObj.getString(AppConstants.REPLY_IMAGE));
                    mReplyChatLayout.setVisibility(GONE);
                }*/
                mMsgRvTotalCount = mMsgRvTotalCount + 1;

                mEventGrpChatMsgList.add(0, mEventGrpChatMsgResModel);
                mChatBoxEventGrpAdapter.notifyDataSetChanged();
                mEventGrpChatMsgRv.scrollToPosition(0);

                if (Integer.parseInt(mDetailsObj.getString(MyFireBaseMessagingService.GRP_CHAT_SENDER_USER_ID)) == PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID)) {
                    mMessageEt.setText("");
                }
            } catch(JSONException e){
                e.printStackTrace();
            }

        } else {

            try {

                JSONObject mNotificationJsonObject = new JSONObject();
                JSONObject mJsonObject = new JSONObject(intent.getStringExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ));
                mNotificationJsonObject.put(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mJsonObject);
                JSONObject mDetailsObj = mJsonObject.getJSONObject("Details");
                String mEventChatID = "EVENT_CHAT"+(mDetailsObj.get("EventID").toString());
                int mNotificationID = Integer.parseInt((mDetailsObj.get("EventID").toString()));
                String mContentTitle = "EVENT :" + (mDetailsObj.get(MyFireBaseMessagingService.EVENT_GRP_CHAT_EVENT_NAME).toString());
                MyFireBaseMessagingService.composeChatNotification(mNotificationJsonObject, this, mNotificationID,
                        mEventChatID,mContentTitle,ChatBoxEventGrpActivity.class);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void getMotoProfiles() {

        int mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        String mFilter = "UserID = " + mUserID;
        RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);

    }

    private void clearFields() {
        mImageConstraintLay.setVisibility(View.GONE);
        mMessageEt.setText("");
        mfileAttachImgBtn.setVisibility(View.VISIBLE);
        mPostImgUri = null;
        imgUrl = "";
    }

    private void getEventGrpChatMsg() {

        String mFilter = "EventID=" + mEventID;

        RetrofitClient.getRetrofitInstance().callGetEventGrpChatMsg(this, mFilter, RetrofitClient.GET_EVENT_GRP_CHAT_MSG, mDataLimit, mMsgRvOffset);

    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.toolbar_title, R.id.send_btn,R.id.fileAttachImgBtn,R.id.iv_remove_image,R.id.replyChatCloseIv})
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
                mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
                mBundle.putString(EventsWhoIsGoingActivity.TOOLBAR_TITLE, mEventName + " Member(s)");

                if (mEventsWhoIsGoingResModels == null) {
                    mBundle.putInt(EventsModel.EVENT_ID, mEventID);
                } else {
                    mBundle.putSerializable(EventsWhoIsGoingModel.WHO_IS_GOING_RES_MODEL, (Serializable) mEventsWhoIsGoingResModels);
                }

                startActivity(new Intent(this, EventsWhoIsGoingActivity.class).putExtras(mBundle));

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
            case R.id.replyChatCloseIv:
                mReplyChatLayout.setVisibility(GONE);
                mReplyUserName = null;
                mReplyMsg = null;
                isRepliedMsg = false;
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
                    return;
                }

                EventGrpChatMsgModel mEventGrpChatMsgModel = new EventGrpChatMsgModel();

                EventGrpChatMsgResModel mEventGrpChatMsgResModel = new EventGrpChatMsgResModel();
                mEventGrpChatMsgResModel.setEventID(mEventID);
                mEventGrpChatMsgResModel.setSenderName(Utility.getInstance().getUserName(mMyProfileResModel));

                mEventGrpChatMsgResModel.setMessage(URLEncoder.encode(mMessage, "UTF-8"));
                mEventGrpChatMsgResModel.setMsgType(imgUrl != null ? "media" : "text");
                mEventGrpChatMsgResModel.setPhotoMessage(imgUrl != null ? imgUrl : "");

                mEventGrpChatMsgResModel.setSenderProfileID(mSenderProfileID);
                mEventGrpChatMsgResModel.setSenderUserID(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID));

                List<EventGrpChatMsgResModel> mEventGrpChatMsgResModelList = new ArrayList<>();
                mEventGrpChatMsgResModelList.add(mEventGrpChatMsgResModel);

                mEventGrpChatMsgModel.setResource(mEventGrpChatMsgResModelList);

                RetrofitClient.getRetrofitInstance().callSendEventGrpChatMsg(this, mEventGrpChatMsgModel, RetrofitClient.SEND_EVENT_GRP_CHAT_MSG);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void uploadPicture(String mCommentImgUri) {
        File mFile = new File(Uri.parse(mCommentImgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", "EventChat_" + mFile.getName(), requestBody);
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

                        getEventGrpChatMsg();

                    }

                    break;

            }

        } else if (responseObj instanceof EventGrpChatMsgModel) {

            EventGrpChatMsgModel mEventGrpChatMsgModel = (EventGrpChatMsgModel) responseObj;

            switch (responseType) {

                case RetrofitClient.GET_EVENT_GRP_CHAT_MSG:

                    if (mEventGrpChatMsgModel.getResource() != null && mEventGrpChatMsgModel.getResource().size() > 0) {

                        mMsgRvTotalCount = mEventGrpChatMsgModel.getMeta().getCount();

                        mIsMsgRvLoading = false;

                        if (mMsgRvOffset == 0) {
                            mEventGrpChatMsgList.clear();
                        }

                        mEventGrpChatMsgList.addAll(mEventGrpChatMsgModel.getResource());

                        mChatBoxEventGrpAdapter.notifyDataSetChanged();

                        if (mMsgRvOffset == 0) {
                            mEventGrpChatMsgRv.scrollToPosition(0);
                        }

                        mMsgRvOffset = mMsgRvOffset + mDataLimit;

                    } else {
                        if (mMsgRvOffset == 0) {
                            mMsgRvTotalCount = 0;
                            mChatBoxEventGrpAdapter.notifyDataSetChanged();
                        }
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

            getEventGrpChatMsg();

        } else if ( responseObj instanceof EventsModel){

            EventsModel mEventModel = (EventsModel)responseObj;

            mEventName = mEventModel.getResource().get(0).getName();

            mEventsWhoIsGoingResModels = mEventModel.getResource().get(0).getWhoIsGoingByEventID();

            setView();

            getEventGrpChatMsg();

        } else if (responseObj instanceof ImageModel) {

            ImageModel mImageModel = (ImageModel) responseObj;

            switch (responseType) {

                case RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE:
                    //update the record in database/tablet
                    imgUrl = getHttpFilePath(mImageModel.getmModels().get(0).getPath());
                    try {
                        //String mMessage = mMessageEt.getText().toString().trim();
                        EventGrpChatMsgModel mEventGrpChatMsgModel = new EventGrpChatMsgModel();

                        EventGrpChatMsgResModel mEventGrpChatMsgResModel = new EventGrpChatMsgResModel();
                        mEventGrpChatMsgResModel.setEventID(mEventID);
                        mEventGrpChatMsgResModel.setSenderName(Utility.getInstance().getUserName(mMyProfileResModel));

                        mEventGrpChatMsgResModel.setMessage("");
                        mEventGrpChatMsgResModel.setMsgType(imgUrl != null ? "media" : "text");
                        mEventGrpChatMsgResModel.setPhotoMessage(imgUrl != null ? imgUrl : "");

                        mEventGrpChatMsgResModel.setSenderProfileID(mSenderProfileID);
                        mEventGrpChatMsgResModel.setSenderUserID(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID));

                        List<EventGrpChatMsgResModel> mEventGrpChatMsgResModelList = new ArrayList<>();
                        mEventGrpChatMsgResModelList.add(mEventGrpChatMsgResModel);

                        mEventGrpChatMsgModel.setResource(mEventGrpChatMsgResModelList);

                        RetrofitClient.getRetrofitInstance().callSendEventGrpChatMsg(this, mEventGrpChatMsgModel, RetrofitClient.SEND_EVENT_GRP_CHAT_MSG);

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

    public void setReplyChatMsg(String replyMsg, int replyMsgID, int replyUserProfileID, String replyMsgUserName, String mImgUrl){
        isRepliedMsg = true;
        mReplyUserProfileID  = replyUserProfileID;
        mReplyMsgID = replyMsgID;
        mReplyMsg = replyMsg;
        mReplyImg = mImgUrl;
        mReplyUserName = replyMsgUserName;
        mReplyChatUserNameTv.setText(replyMsgUserName);
        mReplyChatLayout.setVisibility(View.VISIBLE);
        mReplyChatCloseIv.setVisibility(View.VISIBLE);
        mReplyChatView.setLayoutParams(new RelativeLayout.LayoutParams(3,mReplyChatLayout.getHeight()));
        if(replyMsg.trim().isEmpty()){
            mReplyChatMsgTv.setVisibility(GONE);
        } else {
            mReplyChatMsgTv.setVisibility(View.VISIBLE);
            mReplyChatMsgTv.setText(replyMsg);
        }
        if(mImgUrl.trim().isEmpty()){
            mReplyChatImageIv.setVisibility(GONE);
        } else {
            mReplyChatImageIv.setVisibility(View.VISIBLE);
            setImageWithGlide(mReplyChatImageIv, mImgUrl, R.drawable.default_cover_img);
        }

    }

}
