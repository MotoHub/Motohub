package online.motohub.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import online.motohub.R;
import online.motohub.activity.ondemand.EventVideosPlayingActivity;
import online.motohub.adapter.ChatBoxEventGrpAdapter;
import online.motohub.application.MotoHub;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.CommonInterface;
import online.motohub.interfaces.CommonReturnInterface;
import online.motohub.interfaces.RetrofitResInterface;
import online.motohub.model.EventGrpChatMsgModel;
import online.motohub.model.EventGrpChatMsgResModel;
import online.motohub.model.EventLiveGroupChatModel;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsResModel;
import online.motohub.model.EventsWhoIsGoingResModel;
import online.motohub.model.ImageModel;
import online.motohub.model.LiveStreamPaymentEntity;
import online.motohub.model.LiveStreamPaymentResponse;
import online.motohub.model.NotificationModel1;
import online.motohub.model.PaymentModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PromoterVideoModel;
import online.motohub.model.SessionModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerResModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.dialog.DialogManager;
import online.motohub.util.NotificationUtils1;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;


public class EventLiveActivity extends BaseActivity implements ChatBoxEventGrpAdapter.TotalRetrofitMsgResultCount, CommonReturnInterface {

    public static final String TAG = EventLiveActivity.class.getSimpleName();

    public static final int EVENT_LIVE_PAYMENT_REQ_CODE = 505;
    private static final int mDataLimit = 15;
    private final ArrayList<PromoterVideoModel.PromoterVideoResModel> mPostsList = new ArrayList<>();
    @BindView(R.id.chat_box_layout)
    RelativeLayout mParentLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mEventGrpChatMsgRv;
    @BindView(R.id.writeChatEt)
    EditText mMessageEt;
    @BindView(R.id.event_start_time_txt)
    TextView mEventStartTimeTxt;
    @BindView(R.id.event_end_time_txt)
    TextView mEventEndTimeTxt;
    @BindView(R.id.receiverNameTv)
    TextView mPromoterNameTv;
    @BindView(R.id.profileImgView)
    CircleImageView mPromoterImgView;
    @BindView(R.id.receiverMsgTv)
    TextView mPromoterMsgTv;
    @BindView(R.id.promoterCardView)
    CardView mPromoterCardView;
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
    @BindView(R.id.imageConstraintLay)
    ConstraintLayout mImageConstraintLay;
    @BindView(R.id.iv_post_image)
    ImageView mIvPostImage;
    @BindView(R.id.fileAttachImgBtn)
    ImageView mfileAttachImgBtn;
    @BindView(R.id.btnLiveFeed)
    Button mLiveFeedBtn;
    @BindView(R.id.btnSpectatorLive)
    Button mSpectatorLiveBtn;
    @BindView(R.id.replyChatView)
    View mReplyChatView;
    private LinearLayoutManager mLinearLayoutManager;
    private EventsResModel mEventResModel;
    private ProfileResModel mMyProfileResModel;
    private ArrayList<EventGrpChatMsgResModel> mEventGrpChatMsgList;
    private ChatBoxEventGrpAdapter mChatBoxEventGrpAdapter;
    private int mEventID, mSenderProfileID = 0, mUserID;
    private JSONObject mDetailsObj;
    private ArrayList<EventGrpChatMsgResModel> mEventGrpChatPromoterList = new ArrayList<>();
    private ArrayList<EventGrpChatMsgResModel> mPromoterEventGrpChatList = new ArrayList<>();
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras().getBoolean((AppConstants.IS_FROM_LIVE_EVENT_CHAT))) {
                updatePushMsg(intent);
            }
        }
    };
    private ArrayList<EventsWhoIsGoingResModel> mEventWhoIsGoingList = new ArrayList<>();
    private int mMsgRvOffset = 0, mMsgRvTotalCount = -1;
    private boolean mIsMsgRvLoading = true;
    private int mStreamAmount;
    CommonInterface mPaymentAlertInterface = new CommonInterface() {
        @Override
        public void onSuccess() {
            Intent paymentActivity = new Intent(EventLiveActivity.this, PaymentActivity.class);
            paymentActivity.putExtra(EventsModel.EVENT_AMOUNT, mStreamAmount).putExtra(AppConstants.PROFILE_ID, mMyProfileResModel.getID());
            startActivityForResult(paymentActivity, EVENT_LIVE_PAYMENT_REQ_CODE);
        }
    };
    private String mPostImgUri = null;
    private String imgUrl = null;
    private String mToken = "";
    private boolean isUpdatePayment = false;
    private String mTransactionID = "";
    RetrofitResInterface mRetrofitResInterface = new RetrofitResInterface() {
        @Override
        public void retrofitOnResponse(Object responseObj, int responseType) {
            if (responseObj instanceof PaymentModel) {
                PaymentModel mResponse = (PaymentModel) responseObj;
                if (mResponse.getStatus() != null && mResponse.getStatus().equals("succeeded")) {
                    mTransactionID = mResponse.getID();
                    callUpdateLiveStreamPayment();
                } else {
                    String mErrorMsg = "Your card was declined.";
                    if (mResponse.getMessage() != null) {
                        mErrorMsg = mResponse.getMessage();
                    }
                    mErrorMsg = mErrorMsg + " " + getString(R.string.try_again);
                    showToast(EventLiveActivity.this, mErrorMsg);
                }
            } else if (responseObj instanceof LiveStreamPaymentResponse) {
                LiveStreamPaymentResponse mResponse = (LiveStreamPaymentResponse) responseObj;
                if (mResponse.getResource().size() > 0) {
                    updateList(mResponse.getResource().get(0));
                    showToast(EventLiveActivity.this, "Payment Succeeded");
                } else {
                    DialogManager.showRetryAlertDialogWithCallback(EventLiveActivity.this, mCommonInterface, getString(R.string.payment_must_update));
                }

            } else if (responseObj instanceof SessionModel) {
                SessionModel mSessionModel = (SessionModel) responseObj;
                if (mSessionModel.getSessionToken() == null) {
                    PreferenceUtils.getInstance(EventLiveActivity.this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
                } else {
                    PreferenceUtils.getInstance(EventLiveActivity.this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
                }
                if (isUpdatePayment) {
                    callUpdateLiveStreamPayment();
                } else {
                    callPayViewLiveStream();
                }
            }
        }

        @Override
        public void retrofitOnError(int code, String message) {
            if (message.equals("Unauthorized") || code == 401) {
                RetrofitClient.getRetrofitInstance().callUpdateSession(EventLiveActivity.this, mRetrofitResInterface, RetrofitClient.UPDATE_SESSION_RESPONSE);
            } else {
                String mErrorMsg;
                if (!isUpdatePayment) {
                    mErrorMsg = getString(R.string.internet_err);
                } else {
                    mErrorMsg = getString(R.string.payment_must_update);
                }
                showToast(EventLiveActivity.this, mErrorMsg);
            }

        }

        @Override
        public void retrofitOnSessionError(int code, String message) {
            retrofitOnSessionError(code, message);
        }

        @Override
        public void retrofitOnFailure() {
            showToast(EventLiveActivity.this, getString(R.string.internet_err));
//            DialogManager.showRetryAlertDialogWithCallback(mContext, mCommonInterface, mContext.getString(R.string.internet_err));
        }
    };
    CommonInterface mCommonInterface = new CommonInterface() {
        @Override
        public void onSuccess() {
            if (isUpdatePayment) {
                callUpdateLiveStreamPayment();
            } else {
                callPayViewLiveStream();
            }
        }
    };
    private boolean isRepliedMsg = false;
    private int mReplyMsgID;
    private String mReplyMsg = "", mReplyUserName, mReplyImg = "";
    private int mReplyUserProfileID;
    private ArrayList<PromoterFollowerResModel> mPromoterFollowerList = new ArrayList<>();

/*
    private void AddMembersIntoGroupChat() {
        JsonObject mJsonObject = new JsonObject();
        mJsonObject.addProperty(EventLiveGroupChatModel.USER_ID, mUserID);
        mJsonObject.addProperty(EventLiveGroupChatModel.PROFILE_ID, mMyProfileResModel.getID());
        mJsonObject.addProperty(EventLiveGroupChatModel.EVENT_ID, mEventID);
        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mJsonObject);
        ApiClient.getRetrofitInstance().callPostEventLiveGroupChatMember(this, mJsonArray, ApiClient.POST_EVENT_LIVE_GROUP_CHAT_MEMBER);
    }
*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_live);
        ButterKnife.bind(this);
        getData();

    }

    /*private void RemoveMembersFromGroupChat() {
        String mFilter = "(UserID=" + mUserID + ") AND (EventID=" + mEventID + ")";
        ApiClient.getRetrofitInstance().callDeleteEventLiveGroupChatMember(this, mFilter, ApiClient.DELETE_EVENT_LIVE_GROUP_CHAT_MEMBER);
    }*/

    private void getData() {
        mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        if (getIntent().getExtras().getBoolean(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY)) {
            try {
                JSONObject mJsonObject = new JSONObject(getIntent().getExtras().getString(MyFireBaseMessagingService.ENTRY_JSON_OBJ));
                mDetailsObj = mJsonObject.getJSONObject("Details");
                mEventID = (mDetailsObj.getInt(EventsModel.EVENT_ID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getMotoProfiles();

        } else {
            //mMyProfileResModel = (ProfileResModel) getIntent().getSerializableExtra(ProfileModel.MY_PROFILE_RES_MODEL);
            //mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
            mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
            mEventID = getIntent().getExtras().getInt(EventsModel.EVENT_ID);
            getEvent();
        }
    }

    private void getMotoProfiles() {
        int mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        String mFilter = "UserID = " + mUserID;
        RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);

    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
        //  RemoveMembersFromGroupChat();
    }

    private void initView() {

        try {
            if (mMyProfileResModel != null && mMyProfileResModel.getID() != 0)
                mSenderProfileID = mMyProfileResModel.getID();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String mEventName = mEventResModel.getName();

        setToolbar(mToolbar, mEventName);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        mEventStartTimeTxt.setText(mEventResModel.getDate());
        mEventEndTimeTxt.setText(mEventResModel.getFinish());

        if (mEventResModel.getLivestream_by_EventID().size() == 0) {
            mLiveFeedBtn.setVisibility(GONE);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    4
            );
            mSpectatorLiveBtn.setLayoutParams(param);
        }
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
        mChatBoxEventGrpAdapter = new ChatBoxEventGrpAdapter(mMyProfileResModel, mEventGrpChatMsgList, this);
        mEventGrpChatMsgRv.setAdapter(mChatBoxEventGrpAdapter);

        //  AddMembersIntoGroupChat();

    }

    private void getEvent() {
        String mFilter = "ID=" + mEventID;
        RetrofitClient.getRetrofitInstance().callGetEvents(this, mFilter, RetrofitClient.GET_EVENTS_RESPONSE);
    }

    private void getEventGrpChatMsg() {
        String mFilter = "EventID=" + mEventID;
        RetrofitClient.getRetrofitInstance().callGetLiveEventGrpChatMsg(this, mFilter, RetrofitClient.GET_LIVE_EVENT_GRP_CHAT_MSG, mDataLimit, mMsgRvOffset);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        //   RemoveMembersFromGroupChat();
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.send_btn, R.id.timetable_view_lay, R.id.btnSpectatorLive, R.id.btnLiveFeed, R.id.promoterLay, R.id.fileAttachImgBtn, R.id.iv_remove_image, R.id.replyChatCloseIv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                break;
            case R.id.send_btn:
                if (isAlreadyBooked(mEventWhoIsGoingList)) {
                    sendMessage();
                } else {
                    showToast(this, getString(R.string.event_grp_profile_not_registered));
                }
                break;
            case R.id.timetable_view_lay:
                if (mEventResModel != null) {
                    Intent mTimeTableIntent = new Intent(this, TimeTableActivity.class);
                    mTimeTableIntent.putExtra(EventsModel.EVENT_ID, mEventResModel.getID());
                    startActivity(mTimeTableIntent);
                }
                break;
            case R.id.btnSpectatorLive:
                DialogManager.showMultiLiveOptionPopup(this, this, getString(R.string.go_live), getString(R.string.view_live));
                break;
            case R.id.btnLiveFeed:
                try {
                    if (mEventResModel != null && mEventResModel.getStream_amount() != null) {
                        mStreamAmount = mEventResModel.getStream_amount();
                        float mResAmount = (float) mStreamAmount / 100;
                        if (mStreamAmount == 0 || isAlreadyPaid()) {
                            int eventID = mEventResModel.getID();
                            Intent mGoWatchActivity = new Intent(this, PromoterLiveStreamViewActivity.class);
                            mGoWatchActivity.putExtra(AppConstants.EVENT_ID, eventID);
                            startActivity(mGoWatchActivity);
                        } else {
                            DialogManager.showAlertDialogWithCallback(this, mPaymentAlertInterface,
                                    getString(R.string.alert_live_pay_amount) + "" + mResAmount);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.promoterLay:
                if (mEventResModel != null) {
                    DialogManager.showPromoterChatPopup(this, mPromoterEventGrpChatList, mEventResModel.getPromoterByUserID().getName());
                }
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

    private boolean isAlreadyBooked(ArrayList<EventsWhoIsGoingResModel> eventsWhoIsGoingResModel) {
        boolean isAlreadyBooked = false;
        int mUserId = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        for (int i = 0; i < eventsWhoIsGoingResModel.size(); i++) {
            if (mUserId == eventsWhoIsGoingResModel.get(i).getUserID()) {
                isAlreadyBooked = true;
                break;
            }
        }
        return isAlreadyBooked;
    }

    private boolean isAlreadyPaid() {
        boolean isAlreadyPaid = false;
        int mEventID = mEventResModel.getID();
        for (LiveStreamPaymentEntity mPaymentEntity : mEventResModel.getLivestreampayment_by_EventID()) {
            if (mEventID == mPaymentEntity.getEventID() && mPaymentEntity.getViewUserID() == mUserID) {
                isAlreadyPaid = true;
                break;
            }
        }
        return isAlreadyPaid;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, new IntentFilter(MyFireBaseMessagingService.PUSH_MSG_RECEIVER_ACTION));
        MotoHub.getApplicationInstance().liveEventGrpChatOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
        MotoHub.getApplicationInstance().liveEventGrpChatOnPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case EVENT_LIVE_PAYMENT_REQ_CODE:
                    if (resultCode == RESULT_OK) {
                        if (data.hasExtra("TOKEN")) {
                            mToken = data.getStringExtra("TOKEN");
                            callPayViewLiveStream();
                        }
                    }
                    break;
                case CAMERA_CAPTURE_REQ:
                    Uri mCameraPicUri = getImgResultUri(data);
                    if (mCameraPicUri != null) {
                        try {
                            File mPostImgFile = compressedImgFile(mCameraPicUri, POST_IMAGE_NAME_TYPE, "");
                            mPostImgUri = Uri.fromFile(mPostImgFile).toString();
                            mImageConstraintLay.setVisibility(View.VISIBLE);
                            setImageWithGlide(mIvPostImage, Uri.parse(mPostImgUri), R.drawable.default_cover_img);
                            mfileAttachImgBtn.setVisibility(GONE);

                        } catch (Exception e) {
                            e.printStackTrace();
                            showSnackBar(mParentLayout, e.getMessage());
                        }
                    } else {
                        showSnackBar(mParentLayout, getString(R.string.file_not_found));
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
                            mfileAttachImgBtn.setVisibility(GONE);

                        } catch (Exception e) {
                            e.printStackTrace();
                            showSnackBar(mParentLayout, e.getMessage());
                        }
                    } else {
                        showSnackBar(mParentLayout, getString(R.string.file_not_found));
                    }
                    break;
            }
        }
    }

    private void callPayViewLiveStream() {
        isUpdatePayment = false;
        PromotersResModel mPromoterResModel = mEventResModel.getPromoterByUserID();
        String mAcctNo = mPromoterResModel.getStripeUserId();
        RetrofitClient.getRetrofitInstance().postPayForViewLiveStream(this, mRetrofitResInterface, mToken, mAcctNo, mStreamAmount, AppConstants.LIVE_STREAM_PAYMENT);
    }

    private void updateList(LiveStreamPaymentEntity mEntity) {
        ArrayList<LiveStreamPaymentEntity> mList = mEventResModel.getLivestreampayment_by_EventID();
        mList.add(mEntity);
        mEventResModel.setLivestreampayment_by_EventID(new ArrayList<>(mList));
    }

    private void callUpdateLiveStreamPayment() {
        try {
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(APIConstants.PromoterID, mEventResModel.getUserID());
            mJsonObject.addProperty(APIConstants.ViewUserID, mUserID);
            mJsonObject.addProperty(APIConstants.EventID, mEventResModel.getID());
            mJsonObject.addProperty(APIConstants.TransactionID, mTransactionID);
            mJsonObject.addProperty(APIConstants.Amount, mStreamAmount);
            JsonArray mJsonArray = new JsonArray();
            mJsonArray.add(mJsonObject);
            isUpdatePayment = true;
            RetrofitClient.getRetrofitInstance().callUpdateLiveStreamPayment(EventLiveActivity.this, mRetrofitResInterface, mJsonArray);
        } catch (Exception e) {
            sysOut("" + e.getMessage());
        }
    }
/*    private void updateMsg(EventGrpChatMsgResModel mEventGrpChatMsgResModel) {

        mMsgRvTotalCount = mMsgRvTotalCount + 1;
        mEventGrpChatMsgList.add(0, mEventGrpChatMsgResModel);
        mChatBoxEventGrpAdapter.notifyDataSetChanged();
        mEventGrpChatMsgRv.scrollToPosition(0);
        mMessageEt.setText("");

    }*/

    private void updatePushMsg(Intent intent) {

        if (Integer.parseInt(intent.getStringExtra(EventsModel.EVENT_ID)) == mEventID) {
            try {
                JSONObject mJsonObject = new JSONObject(intent.getStringExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ));
                mDetailsObj = mJsonObject.getJSONObject("Details");

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

                if (mDetailsObj.getString(MyFireBaseMessagingService.EVENT_USER_TYPE).equals(AppConstants.USER)) {
                    ProfileResModel mProfileResModel = new ProfileResModel();
                    mProfileResModel.setSpectatorName(mDetailsObj.getString(AppConstants.GROUP_CHAT_SENDER_NAME));
                    mProfileResModel.setProfilePicture(mDetailsObj.getString(AppConstants.PROFILE_PICTURE));
                    mEventGrpChatMsgResModel.setProfilesBySenderProfileID(mProfileResModel);
                } else {
                    PromotersResModel mPromotersResModel = new PromotersResModel();
                    mPromotersResModel.setName(mDetailsObj.getString(AppConstants.GROUP_CHAT_SENDER_NAME));
                    mPromotersResModel.setProfileImage(mDetailsObj.getString(AppConstants.PROFILE_PICTURE));
                    mEventGrpChatMsgResModel.setPromoterBySenderUserID(mPromotersResModel);
                    mEventGrpChatPromoterList.add(mEventGrpChatMsgResModel);
                    setPromoterLay(mEventGrpChatPromoterList);
                }

             /*   if (mDetailsObj.getString(AppConstants.IS_REPLY_CHAT_MSG).equals("1")) {
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

    private void sendMessage() {

        try {

            String mMessage = mMessageEt.getText().toString().trim();

            if (mSenderProfileID == 0) {
                return;
            }

            if (mPostImgUri != null) {
                uploadPicture(mPostImgUri);
                return;
            } else if (mMessage.isEmpty()) {
                showToast(getApplicationContext(), getString(R.string.write_something));
                return;
            }
            EventGrpChatMsgModel mEventGrpChatMsgModel = new EventGrpChatMsgModel();

            EventGrpChatMsgResModel mEventGrpChatMsgResModel = new EventGrpChatMsgResModel();
            mEventGrpChatMsgResModel.setEventID(mEventID);
            mEventGrpChatMsgResModel.setSenderName(Utility.getInstance().getUserName(mMyProfileResModel));
            mEventGrpChatMsgResModel.setMessage(URLEncoder.encode(mMessage, "UTF-8"));
            mEventGrpChatMsgResModel.setSenderProfileID(mSenderProfileID);
            mEventGrpChatMsgResModel.setSenderUserID(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID));
            mEventGrpChatMsgResModel.setSenderUserID(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID));
            mEventGrpChatMsgResModel.setMsgType(imgUrl != null ? "media" : "text");
            mEventGrpChatMsgResModel.setPhotoMessage(imgUrl != null ? imgUrl : "");

         /*   if (isRepliedMsg) {
                mEventGrpChatMsgResModel.setRepliedMsgID(mReplyMsgID);
                mEventGrpChatMsgResModel.setReplyMessage(URLEncoder.encode(mReplyMsg,"UTF-8"));
                mEventGrpChatMsgResModel.setReplyUserProfileID(mReplyUserProfileID);
                mEventGrpChatMsgResModel.setReplyUserName(mReplyUserName);
                mEventGrpChatMsgResModel.setReplyImage(mReplyImg);
                mEventGrpChatMsgResModel.setIsRepliedMsg(true);
                mReplyChatLayout.setVisibility(GONE);
            }*/

            List<EventGrpChatMsgResModel> mEventGrpChatMsgResModelList = new ArrayList<>();
            mEventGrpChatMsgResModelList.add(mEventGrpChatMsgResModel);

            mEventGrpChatMsgModel.setResource(mEventGrpChatMsgResModelList);

            RetrofitClient.getRetrofitInstance().callSendLiveEventGrpChatMsg(this, mEventGrpChatMsgModel, RetrofitClient.SEND_LIVE_EVENT_GRP_CHAT_MSG);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

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
                        //mMyProfileResModel = mFullMPList.get(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS));
                        //MotoHub.getApplicationInstance().setmProfileResModel(mFullMPList.get(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));
                        EventBus.getDefault().postSticky(mFullMPList.get(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS)));
                        //mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
                        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
                        getEvent();
                    }
                    break;
            }

        } else if (responseObj instanceof EventGrpChatMsgModel) {

            EventGrpChatMsgModel mEventGrpChatMsgModel = (EventGrpChatMsgModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_LIVE_EVENT_GRP_CHAT_MSG:
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

                case RetrofitClient.SEND_LIVE_EVENT_GRP_CHAT_MSG:
                    //  updateMsg(mEventGrpChatMsgModel.getResource().get(0));
                    break;

                case RetrofitClient.CALL_GET_EVENT_LIVE_PROMOTER_GRP_CHAT_MSG:
                    if (mEventGrpChatMsgModel.getResource().size() > 0) {
                        mEventGrpChatPromoterList.addAll(mEventGrpChatMsgModel.getResource());
                        setPromoterLay(mEventGrpChatMsgModel.getResource());
                    }
                    getEventGrpChatMsg();
                    break;

            }

        } else if (responseObj instanceof SessionModel) {
            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }

        } else if (responseObj instanceof EventLiveGroupChatModel) {
            switch (responseType) {
                case RetrofitClient.POST_EVENT_LIVE_GROUP_CHAT_MEMBER:
                    break;
                case RetrofitClient.DELETE_EVENT_LIVE_GROUP_CHAT_MEMBER:
                    finish();
                    break;
            }
        } else if (responseObj instanceof EventsModel) {
            EventsModel mEventModel = (EventsModel) responseObj;
            if (mEventModel.getResource().size() > 0) {
                mEventResModel = mEventModel.getResource().get(0);
                mEventWhoIsGoingList = mEventResModel.getWhoIsGoingByEventID();
                initView();
                getPromoterChatMsg();
            }
        } else if (responseObj instanceof ImageModel) {

            ImageModel mImageModel = (ImageModel) responseObj;

            switch (responseType) {

                case RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE:
                    //update the record in database/tablet
                    imgUrl = getHttpFilePath(mImageModel.getmModels().get(0).getPath());
                    try {
                        //callPostFeedComments(imgUrl, mPostID, mMyProfileResModel.getID());
                        String mMessage = mMessageEt.getText().toString().trim();
                        EventGrpChatMsgModel mEventGrpChatMsgModel = new EventGrpChatMsgModel();
                        EventGrpChatMsgResModel mEventGrpChatMsgResModel = new EventGrpChatMsgResModel();
                        mEventGrpChatMsgResModel.setEventID(mEventID);
                        mEventGrpChatMsgResModel.setSenderName(Utility.getInstance().getUserName(mMyProfileResModel));
                        mEventGrpChatMsgResModel.setMessage(URLEncoder.encode(mMessage, "UTF-8"));
                        mEventGrpChatMsgResModel.setSenderProfileID(mSenderProfileID);
                        mEventGrpChatMsgResModel.setSenderUserID(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID));
                        mEventGrpChatMsgResModel.setSenderUserID(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID));
                        mEventGrpChatMsgResModel.setMsgType(imgUrl != null ? "media" : "text");
                        mEventGrpChatMsgResModel.setPhotoMessage(imgUrl != null ? imgUrl : "");

                        List<EventGrpChatMsgResModel> mEventGrpChatMsgResModelList = new ArrayList<>();
                        mEventGrpChatMsgResModelList.add(mEventGrpChatMsgResModel);

                        mEventGrpChatMsgModel.setResource(mEventGrpChatMsgResModelList);

                        RetrofitClient.getRetrofitInstance().callSendLiveEventGrpChatMsg(this, mEventGrpChatMsgModel, RetrofitClient.SEND_LIVE_EVENT_GRP_CHAT_MSG);

                        clearFields();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void clearFields() {
        mImageConstraintLay.setVisibility(GONE);
        mMessageEt.setText("");
        mfileAttachImgBtn.setVisibility(View.VISIBLE);
        mPostImgUri = null;
        imgUrl = null;
    }


    private void uploadPicture(String mCommentImgUri) {
        File mFile = new File(Uri.parse(mCommentImgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", "EventChat_" + mFile.getName(), requestBody);
        RetrofitClient.getRetrofitInstance().callUploadChatImg(this, filePart, RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE);
    }

    private void setPromoterLay(List<EventGrpChatMsgResModel> mPromoterChatList) {
        mPromoterCardView.setVisibility(View.VISIBLE);
        mPromoterNameTv.setText(mPromoterChatList.get(0).getPromoterBySenderUserID().getName());
        setImageWithGlide(mPromoterImgView, mPromoterChatList.get(0).getPromoterBySenderUserID().getProfileImage());
        mPromoterMsgTv.setText(mPromoterChatList.get(0).getMessage());

        for (int i = 0; i < mPromoterChatList.size(); i++) {
            mPromoterEventGrpChatList.add(mPromoterChatList.get(i));
        }
    }


    private void getPromoterChatMsg() {
        String mFilter = "( EventID = " + mEventResModel.getID() + ") AND ( SenderUserID = " + mEventResModel.getUserID() + ")";
        RetrofitClient.getRetrofitInstance().callGetLiveEventPromoterGrpChatMsg(this, mFilter, RetrofitClient.CALL_GET_EVENT_LIVE_PROMOTER_GRP_CHAT_MSG);
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);

        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mParentLayout, mErrorMsg);
        }

    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
        String mErrorMsg = code + " - " + message;
        showSnackBar(mParentLayout, mErrorMsg);
    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        showSnackBar(mParentLayout, mInternetFailed);
    }

    @Override
    public int getTotalMsgResultCount() {
        return mMsgRvTotalCount;
    }

    @Override
    public void onSuccess(int type) {
        Bundle mBundle = new Bundle();
        if (type == 1) {
            //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
            if (mMyProfileResModel != null)
                EventBus.getDefault().postSticky(mMyProfileResModel);
            //mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
            mBundle.putSerializable(EventsModel.EVENTS_RES_MODEL, mEventResModel);
            startActivity(new Intent(this, CameraStoryActivity.class).putExtras(mBundle));
        } else {
            if (mEventResModel != null) {
               /* mBundle.putSerializable(EventsModel.EVENTS_RES_MODEL, mEventResModel);
                mBundle.putString(AppConstants.TAG, TAG);
                startActivity(new Intent(this, ViewSpecLiveActivity.class).putExtras(mBundle));*/
                String mFilter = "EventID" + " = " + mEventResModel.getID();
                callGetPromotersGalleryAdapter(EventLiveActivity.this, mFilter, 0);
            }
        }
    }

    public void setReplyChatMsg(String replyMsg, int replyMsgID, int replyUserProfileID, String replyMsgUserName, String mImgUrl) {
        isRepliedMsg = true;
        mReplyUserProfileID = replyUserProfileID;
        mReplyMsgID = replyMsgID;
        mReplyMsg = replyMsg;
        mReplyImg = mImgUrl;
        mReplyUserName = replyMsgUserName;
        mReplyChatUserNameTv.setText(replyMsgUserName);
        mReplyChatLayout.setVisibility(View.VISIBLE);
        mReplyChatCloseIv.setVisibility(View.VISIBLE);
        mReplyChatView.setLayoutParams(new RelativeLayout.LayoutParams(3, mReplyChatLayout.getHeight()));
        if (replyMsg.trim().isEmpty()) {
            mReplyChatMsgTv.setVisibility(GONE);
        } else {
            mReplyChatMsgTv.setVisibility(View.VISIBLE);
            mReplyChatMsgTv.setText(replyMsg);
        }
        if (mImgUrl.trim().isEmpty()) {
            mReplyChatImageIv.setVisibility(GONE);
        } else {
            mReplyChatImageIv.setVisibility(View.VISIBLE);
            setImageWithGlide(mReplyChatImageIv, mImgUrl, R.drawable.default_cover_img);
        }

    }

    public void hideReplyChatMsg() {
        mReplyChatLayout.setVisibility(GONE);
    }

    private void callGetPromotersGalleryAdapter(final Context context, final String filter, int mOffset) {
        String fields = "*";
        DialogManager.showProgress(context);
        int mLimit = 10;
        String mOrderBy = "CreatedAt ASC";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetPromotersVideosForAdapter(fields, filter, APIConstants.mPromoterGalleryRelated, mOrderBy, mLimit, mOffset, true)
                .enqueue(new Callback<PromoterVideoModel>() {
                    @Override
                    public void onResponse(Call<PromoterVideoModel> call, Response<PromoterVideoModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            PromoterVideoModel mResponse = response.body();
                            if (mResponse != null && mResponse.getResource().size() > 0) {
                                mPostsList.addAll(mResponse.getResource());
                                if (mPostsList.size() > 0) {
                                    Bundle mBundle = new Bundle();
                                    mBundle.putSerializable(AppConstants.ONDEMAND_DATA, mPostsList);
                                    EventBus.getDefault().postSticky(mMyProfileResModel);
                                    mBundle.putString("Filter", filter);
                                    startActivityForResult(new Intent(EventLiveActivity.this, EventVideosPlayingActivity.class).putExtras(mBundle), AppConstants.ONDEMAND_REQUEST);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PromoterVideoModel> call, Throwable t) {
                        DialogManager.hideProgress();
                    }
                });
    }
}
