package online.motohub.newdesign.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.ChatBoxEventGrpActivity;
import online.motohub.activity.EventLiveActivity;
import online.motohub.activity.EventQuestionAnswerActivity;
import online.motohub.activity.EventsAddOnActivity;
import online.motohub.activity.EventsWhoIsGoingActivity;
import online.motohub.activity.PaymentActivity;
import online.motohub.activity.TimeTableActivity;
import online.motohub.activity.UpdateEventQuestionAnswerActivity;
import online.motohub.application.MotoHub;
import online.motohub.dialog.DialogManager;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.CommonInterface;
import online.motohub.interfaces.EventsInterface;
import online.motohub.interfaces.RetrofitResInterface;
import online.motohub.model.EventAddOnModel;
import online.motohub.model.EventAnswersModel;
import online.motohub.model.EventCategoryModel;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsResModel;
import online.motohub.model.EventsWhoIsGoingModel;
import online.motohub.model.EventsWhoIsGoingResModel;
import online.motohub.model.LiveStreamPaymentEntity;
import online.motohub.model.LiveStreamPaymentResponse;
import online.motohub.model.PaymentModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PurchasedAddOnModel;
import online.motohub.model.RacingModel;
import online.motohub.model.SessionModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerResModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.UrlUtils;
import online.motohub.util.Utility;

import static android.app.Activity.RESULT_OK;

public class FindEventsAdapter extends RecyclerView.Adapter<FindEventsAdapter.Holder> implements View.OnClickListener, EventsInterface {

    public static final int EVENT_PAYMENT_REQ_CODE = 500;
    public static final int EVENT_QUESTIONS_REQ_CODE = 501;
    public static final int EVENT_LIVE_PAYMENT_REQ_CODE = 505;
    public String mEventType = "";
    private Context mContext;
    private List<EventsResModel> mOriginalEventsFindListData;
    private List<EventsResModel> mEventsFindListData;
    private ProfileResModel mMyProfileResModel;
    private int mAdapterPos;
    private ArrayList<EventCategoryModel> mSelectedCategory = new ArrayList<>();
    private int mStreamAmount;
    CommonInterface mPaymentAlertInterface = new CommonInterface() {
        @Override
        public void onSuccess() {
            Intent paymentActivity = new Intent(mContext, PaymentActivity.class);
            paymentActivity.putExtra(EventsModel.EVENT_AMOUNT, mStreamAmount).putExtra(AppConstants.PROFILE_ID, mMyProfileResModel.getID());
            ((Activity) mContext).startActivityForResult(paymentActivity, EVENT_LIVE_PAYMENT_REQ_CODE);
        }
    };
    private String mToken = "";
    private boolean isUpdatePayment = false;
    private String mTransactionID = "";
    private ArrayList<PromoterFollowerResModel> mPromoterFollowerList = new ArrayList<>();
    RetrofitResInterface mRetrofitResInterface = new RetrofitResInterface() {
        @Override
        public void retrofitOnResponse(Object responseObj, int responseType) {

            if (responseObj instanceof PaymentModel) {
                ((BaseActivity) mContext).sysOut(responseObj.toString());
                PaymentModel mResponse = (PaymentModel) responseObj;
                if (mResponse.getStatus() != null && mResponse.getStatus().equals("succeeded")) {
                    mTransactionID = mResponse.getID();
                    callUpdateLiveStreamPayment();
                } else {
                    String mErrorMsg = "Your card was declined.";
                    if (mResponse.getMessage() != null) {
                        mErrorMsg = mResponse.getMessage();
                    }
                    mErrorMsg = mErrorMsg + " " + mContext.getString(R.string.try_again);
                    ((BaseActivity) mContext).showToast(mContext, mErrorMsg);
                }
            } else if (responseObj instanceof LiveStreamPaymentResponse) {
                LiveStreamPaymentResponse mResponse = (LiveStreamPaymentResponse) responseObj;
                if (mResponse.getResource().size() > 0) {
                    updateList(mResponse.getResource().get(0));
                    ((BaseActivity) mContext).showToast(mContext, "Payment Succeeded");
                } else {
                    DialogManager.showRetryAlertDialogWithCallback(mContext, mCommonInterface, mContext.getString(R.string.payment_must_update));
                }

            } else if (responseObj instanceof SessionModel) {
                SessionModel mSessionModel = (SessionModel) responseObj;
                if (mSessionModel.getSessionToken() == null) {
                    PreferenceUtils.getInstance(mContext).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
                } else {
                    PreferenceUtils.getInstance(mContext).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
                }
                if (isUpdatePayment) {
                    callUpdateLiveStreamPayment();
                } else {
                    callPayViewLiveStream();
                }
            } else if (responseObj instanceof PromoterFollowerModel) {
                PromoterFollowerModel mPromoterFollowerModel = (PromoterFollowerModel) responseObj;
                switch (responseType) {
                    case RetrofitClient.GET_PROMOTER_FOLLOW_RESPONSE:
                        mPromoterFollowerList.add(mPromoterFollowerModel.getResource().get(0));
                        mMyProfileResModel.setPromoterFollowerByProfileID(mPromoterFollowerList);
                        notifyDataSetChanged();
                        break;
                    case RetrofitClient.GET_PROMOTER_UN_FOLLOW_RESPONSE:
                        for (int i = 0; i < mPromoterFollowerList.size(); i++) {
                            if (mPromoterFollowerList.get(i).getID() == mPromoterFollowerModel.getResource().get(0).getID()) {
                                mPromoterFollowerList.remove(i);
                                break;
                            }
                        }
                        mMyProfileResModel.setPromoterFollowerByProfileID(mPromoterFollowerList);
                        notifyDataSetChanged();
                        break;
                }
                setResult();

            }
        }

        @Override
        public void retrofitOnError(int code, String message) {
            if (message.equals("Unauthorized") || code == 401) {
                RetrofitClient.getRetrofitInstance().callUpdateSession(mContext, mRetrofitResInterface, RetrofitClient.UPDATE_SESSION_RESPONSE);
            } else {
                String mErrorMsg;
                if (!isUpdatePayment) {
                    mErrorMsg = mContext.getString(R.string.internet_err);
                } else {
                    mErrorMsg = mContext.getString(R.string.payment_must_update);
                }
                ((BaseActivity) mContext).showToast(mContext, mContext.getString(R.string.internet_err));
//                DialogManager.showRetryAlertDialogWithCallback(mContext, mCommonInterface, mErrorMsg);
            }

        }

        @Override
        public void retrofitOnSessionError(int code, String message) {
            ((BaseActivity) mContext).retrofitOnSessionError(code, message);
        }

        @Override
        public void retrofitOnFailure() {
            ((BaseActivity) mContext).showToast(mContext, mContext.getString(R.string.internet_err));
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
    private boolean isFromEventList = false;

    public FindEventsAdapter( Context context, ArrayList<EventsResModel> eventsFindListData, ProfileResModel myProfileResModel,  boolean isFromEventList) {
        this.mContext = context;
        this.mOriginalEventsFindListData = eventsFindListData;
        this.mMyProfileResModel = myProfileResModel;
        this.mEventsFindListData = eventsFindListData;
        this.isFromEventList = isFromEventList;
    }

    private void callEventAnswer(int mProfileID, int mEventID) {
        String mFilter = "(EventID=" + mEventID + ") AND (ProfileID=" + mProfileID + ")";
        RetrofitClient.getRetrofitInstance().callGetAnswerForEventRegistrationQuestions((BaseActivity) mContext, mFilter, RetrofitClient.GET_EVENT_ANSWER);
    }

    public void alertDialogPositiveBtnClick(String dialogType) {

        switch (dialogType) {
            case AppDialogFragment.EVENT_CATEGORY_DIALOG:
                mSelectedCategory = MotoHub.getApplicationInstance().getSelectedEventCategoryList();
                MotoHub.getApplicationInstance().setSelectedEventCategoryList(null);
                if (mMyProfileResModel != null)
                    callEventAnswer(mMyProfileResModel.getID(), mEventsFindListData.get(mAdapterPos).getID());
                break;
        }
    }

    public void callUpdateEventAnswerScreen(ArrayList<EventAnswersModel> mResEventAnswerModel) {
        if (mResEventAnswerModel.size() > 0) {
            Intent updateEventQuestionAnswerActivity = new Intent(mContext, UpdateEventQuestionAnswerActivity.class);
            updateEventQuestionAnswerActivity.putExtra("AnswerList", mResEventAnswerModel);
            updateEventQuestionAnswerActivity.putExtra("QuestionList", mEventsFindListData.get(mAdapterPos).getEventRegistrationQuestion());
            ((Activity) mContext).startActivityForResult(updateEventQuestionAnswerActivity, EVENT_QUESTIONS_REQ_CODE);
        } else {
            Intent eventQuestionAnswerActivity = new Intent(mContext, EventQuestionAnswerActivity.class);
            eventQuestionAnswerActivity.putExtra("ProfileID", mMyProfileResModel.getID());
            eventQuestionAnswerActivity.putExtra("EventID", mEventsFindListData.get(mAdapterPos).getID());
            eventQuestionAnswerActivity.putExtra("QuestionList", mEventsFindListData.get(mAdapterPos).getEventRegistrationQuestion());
            ((Activity) mContext).startActivityForResult(eventQuestionAnswerActivity, EVENT_QUESTIONS_REQ_CODE);
        }
    }

    private void setEventCategory(ArrayList<EventCategoryModel> mCategoryNameList) {
        if (mCategoryNameList != null && !mCategoryNameList.isEmpty()) {
            AppDialogFragment.newInstance(AppDialogFragment.EVENT_CATEGORY_DIALOG, null, mCategoryNameList)
                    .show(((BaseActivity) mContext).getSupportFragmentManager(), AppDialogFragment.TAG);
        } else {
            Toast.makeText(mContext, "There are no categorie", Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    @Override
    public FindEventsAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_events_find_item, parent, false);
        return new FindEventsAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FindEventsAdapter.Holder mViewHolder, @SuppressLint("RecyclerView") final int position) {
        try {
            boolean isLive = ((BaseActivity) mContext).isLiveEvent(mEventsFindListData.get(position).getDate(), mEventsFindListData.get(position).getFinish());
            if (isLive) {
                final Animation animation = new AlphaAnimation(1, 0);
                animation.setDuration(500);
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatCount(Animation.INFINITE);
                mViewHolder.mLiveButton.startAnimation(animation);
                mViewHolder.mLiveButton.setVisibility(View.VISIBLE);
            } else {
                mViewHolder.mLiveButton.setVisibility(View.GONE);
            }
            mViewHolder.mStartInDaysTv.setText(((BaseActivity) mContext).getWhenEventStartingStr(mEventsFindListData.get(position).getDate(),
                    (mEventsFindListData.get(position).getFinish())));

            String mEventName = mEventsFindListData.get(position).getName() + " - " + ((BaseActivity) mContext).getFormattedDate(mEventsFindListData.get(position).getDate())
                    + " - " + mEventsFindListData.get(position).getMotorVehicle();

            mViewHolder.mEventNameTv.setText(mEventName);

            if (isAlreadyBooked(mEventsFindListData.get(position).getWhoIsGoingByEventID())) {
                mViewHolder.mBookNowBtn.setText(mContext.getResources().getString(R.string.booked));
            } else {
                mViewHolder.mBookNowBtn.setText(mContext.getResources().getString(R.string.book_now));
            }

            mViewHolder.mPromoterNameTxt.setText(mEventsFindListData.get(position).getPromoterByUserID().getName());
            ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mPromoterProfileImg, mEventsFindListData.get(position).getPromoterByUserID().getProfileImage(), R.drawable.default_profile_icon);
            if (!mEventsFindListData.get(position).getmEventImage().isEmpty())
                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mCoverImg, mEventsFindListData.get(position).getmEventImage(), R.drawable.default_cover_img);
            else
                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mCoverImg, mEventsFindListData.get(position).getPromoterByUserID().getProfileImage(), R.drawable.default_cover_img);

            mViewHolder.mCoverImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String imgUrl;

                    if (!mEventsFindListData.get(position).getmEventImage().isEmpty())
                        imgUrl = mEventsFindListData.get(position).getmEventImage();
                    else
                        imgUrl = mEventsFindListData.get(position).getPromoterByUserID().getProfileImage();
                    ((BaseActivity) mContext).moveLoadImageScreen(mContext, UrlUtils.FILE_URL + imgUrl);
                }
            });

            mViewHolder.mWhoIsGoingBtn.setTag(position);
            mViewHolder.mWhoIsGoingBtn.setOnClickListener(this);

            mViewHolder.mBookNowBtn.setOnClickListener(this);
            mViewHolder.mBookNowBtn.setTag(position);

            mViewHolder.mTimeTableBtn.setOnClickListener(this);
            mViewHolder.mTimeTableBtn.setTag(position);

            mViewHolder.mEventGrpChatBtn.setOnClickListener(this);
            mViewHolder.mEventGrpChatBtn.setTag(position);

            mViewHolder.mLiveButton.setTag(position);
            mViewHolder.mLiveButton.setOnClickListener(this);

            if (mMyProfileResModel == null) {
                mViewHolder.mBookNowBtn.setEnabled(false);
            }

            if (isFromEventList) {
                mPromoterFollowerList = mMyProfileResModel.getPromoterFollowerByProfileID();
                if (isAlreadyFollowed(position, mPromoterFollowerList)) {
                    mViewHolder.mPromoterFollowBtn.setText(mContext.getString(R.string.un_follow));
                } else {
                    mViewHolder.mPromoterFollowBtn.setText(mContext.getString(R.string.follow));
                }
                mViewHolder.mPromoterFollowBtn.setTag(position);
                mViewHolder.mPromoterFollowBtn.setOnClickListener(this);

            } else {
                mViewHolder.mPromoterFollowBtn.setVisibility(View.GONE);
                mViewHolder.mPromoterLay.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mEventsFindListData.size();
    }

    private boolean isAlreadyFollowed(int pos, ArrayList<PromoterFollowerResModel> mPromoterFollowerList) {
        boolean isAlreadyFollowed = false;
        for (PromoterFollowerResModel mPromoterFollowerResModel : mPromoterFollowerList) {
            if (mEventsFindListData.get(pos).getUserID() == mPromoterFollowerResModel.getPromoterUserID()) {
                isAlreadyFollowed = true;
                break;
            }
        }
        return isAlreadyFollowed;
    }

    private boolean isAlreadyPaid() {
        boolean isAlreadyPaid = false;
        int mUserId = PreferenceUtils.getInstance(mContext).getIntData(PreferenceUtils.USER_ID);
        EventsResModel mEventsResModel = mEventsFindListData.get(mAdapterPos);
        int mEventID = mEventsResModel.getID();
        for (LiveStreamPaymentEntity mPaymentEntity : mEventsResModel.getLivestreampayment_by_EventID()) {
            if (mEventID == mPaymentEntity.getEventID() && mPaymentEntity.getViewUserID() == mUserId) {
                isAlreadyPaid = true;
                break;
            }
        }
        return isAlreadyPaid;
    }

    @Override
    public void onClick(View view) {
        mAdapterPos = (int) view.getTag();
        switch (view.getId()) {
            case R.id.who_is_going_btn:

                if (mEventsFindListData.get(mAdapterPos).getWhoIsGoingByEventID().size() == 0) {
                    ((BaseActivity) mContext).showToast(mContext, mContext.getResources().getString(R.string.event_nobody_registered));
                    return;
                }

                //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                EventBus.getDefault().postSticky(mMyProfileResModel);
                Bundle mBundle = new Bundle();
                //mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
                mBundle.putInt(EventsModel.EVENT_ID, mEventsFindListData.get(mAdapterPos).getID());
                mBundle.putSerializable(EventsWhoIsGoingModel.WHO_IS_GOING_RES_MODEL, mEventsFindListData.get(mAdapterPos).getWhoIsGoingByEventID());
                mBundle.putString(EventsWhoIsGoingActivity.TOOLBAR_TITLE, mContext.getResources().getString(R.string.who_is_going));

                mContext.startActivity(new Intent(mContext, EventsWhoIsGoingActivity.class).putExtras(mBundle));

                break;

            case R.id.book_now_img_btn:

                if (!isAlreadyBooked(mEventsFindListData.get(mAdapterPos).getWhoIsGoingByEventID())) {
                    if (Utility.getInstance().isSpectator(mMyProfileResModel)) {
                        ((BaseActivity) mContext).showAppDialog(AppDialogFragment.ALERT_SPECTATOR_UPDATE_DIALOG, null);
                    } else {
                        ArrayList<EventCategoryModel> mEventCategoryList = mEventsFindListData.get(mAdapterPos).getEventGroupsByEventID();
                        ArrayList<String> mEventCategoryNameList = new ArrayList<>();
                        ArrayList<EventCategoryModel> mFinalCategoryList = new ArrayList<>();
                        for (int i = 0; i < mEventCategoryList.size(); i++) {
                            mEventCategoryNameList.add(mEventCategoryList.get(i).getGroupName());
                        }
                        HashSet<String> hs = new HashSet<>();
                        hs.addAll(mEventCategoryNameList);
                        mEventCategoryNameList.clear();
                        mEventCategoryNameList.addAll(hs);
                        for (int i = 0; i < mEventCategoryNameList.size(); i++) {
                            for (int j = 0; j < mEventCategoryList.size(); j++) {
                                if (mEventCategoryList.get(j).getGroupName().trim().equals(mEventCategoryNameList.get(i).trim())) {
                                    mFinalCategoryList.add(mEventCategoryList.get(j));
                                    break;
                                }
                            }
                        }
                        setEventCategory(mFinalCategoryList);
                    }
                } else {
                    Toast.makeText(mContext, "Your are already booked", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.time_table_btn:
                Intent mTimeTableIntent = new Intent(mContext, TimeTableActivity.class);
                mTimeTableIntent.putExtra(EventsModel.EVENT_ID, mEventsFindListData.get(mAdapterPos).getID());
                mContext.startActivity(mTimeTableIntent);
                break;

            case R.id.grp_chat_img_btn:
                if (mEventsFindListData.get(mAdapterPos).getWhoIsGoingByEventID().size() == 0) {
                    ((BaseActivity) mContext).showToast(mContext, mContext.getResources().getString(R.string.event_grp_nobody_registered));
                    return;
                }
                boolean isEligibleToChat = false;
                for (EventsWhoIsGoingResModel mEventsWhoIsGoingResModel : mEventsFindListData.get(mAdapterPos).getWhoIsGoingByEventID()) {
                    if (mEventsWhoIsGoingResModel.getProfileID() == mMyProfileResModel.getID()) {
                        isEligibleToChat = true;
                    }
                }
                if (isEligibleToChat) {
                    mBundle = new Bundle();
                    //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                    EventBus.getDefault().postSticky(mMyProfileResModel);
                    //mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
                    //  mBundle.putSerializable(EventsWhoIsGoingModel.WHO_IS_GOING_RES_MODEL, mEventsFindListData.get(mAdapterPos).getWhoIsGoingByEventID());
                    mBundle.putInt(EventsModel.EVENT_ID, mEventsFindListData.get(mAdapterPos).getID());
                    mContext.startActivity(new Intent(mContext, ChatBoxEventGrpActivity.class)
                            .putExtras(mBundle));
                } else {
                    ((BaseActivity) mContext).showToast(mContext, mContext.getResources().getString(R.string.event_grp_profile_not_registered));
                }
                break;
            case R.id.live_btn:
                /*view.clearAnimation();
                mStreamAmount = mEventsFindListData.get(mAdapterPos).getStream_amount();
                float mResAmount = (float) mStreamAmount / 100;
                if (mStreamAmount == 0 || isAlreadyPaid()) {
                    int eventID = mEventsFindListData.get(mAdapterPos).getID();
                    Intent mGoWatchActivity = new Intent(mContext, PromoterLiveStreamViewActivity.class);
                    mGoWatchActivity.putExtra(AppConstants.EVENT_ID, eventID);
                    mContext.startActivity(mGoWatchActivity);
                } else {
                    DialogManager.showAlertDialogWithCallback(mContext, mPaymentAlertInterface,
                            mContext.getString(R.string.alert_live_pay_amount) + "" + mResAmount);
                }*/
                mBundle = new Bundle();
                //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                EventBus.getDefault().postSticky(mMyProfileResModel);
                //mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
                mBundle.putInt(EventsModel.EVENT_ID, mEventsFindListData.get(mAdapterPos).getID());
                mContext.startActivity(new Intent(mContext, EventLiveActivity.class)
                        .putExtras(mBundle));
                break;

            case R.id.promoter_follow_btn:
                if (isAlreadyFollowed(mAdapterPos, mMyProfileResModel.getPromoterFollowerByProfileID())) {
                    callUnFollowPromoter();
                } else {
                    callFollowPromoter();
                }
                break;

        }

    }

    private void callUnFollowPromoter() {
        String mFilter = "FollowRelation=" + mMyProfileResModel.getID() + "_" + mEventsFindListData.get(mAdapterPos).getUserID();
        RetrofitClient.getRetrofitInstance().callUnFollowPromoter(mContext, mRetrofitResInterface, mFilter, RetrofitClient.GET_PROMOTER_UN_FOLLOW_RESPONSE);
    }

    private void callFollowPromoter() {

        String mFollowRelation = mMyProfileResModel.getID() + "_" + mEventsFindListData.get(mAdapterPos).getUserID();
        JsonArray mJsonArray = new JsonArray();
        JsonObject mJsonObject = new JsonObject();
        mJsonObject.addProperty(PromoterFollowerResModel.PROFILE_ID, mMyProfileResModel.getID());
        mJsonObject.addProperty(PromoterFollowerResModel.PROMOTER_USER_ID, mEventsFindListData.get(mAdapterPos).getUserID());
        mJsonObject.addProperty(PromoterFollowerResModel.FOLLOW_RELATION, mFollowRelation);

        mJsonArray.add(mJsonObject);

        RetrofitClient.getRetrofitInstance().callFollowPromoter(mContext, mRetrofitResInterface, mJsonArray, RetrofitClient.GET_PROMOTER_FOLLOW_RESPONSE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case EVENT_PAYMENT_REQ_CODE:
                if (resultCode == RESULT_OK) {
                    if (data.hasExtra("TOKEN")) {
                        String mToken = data.getStringExtra("TOKEN");
                        PromotersResModel mPromoterResModel = mEventsFindListData.get(mAdapterPos).getPromoterByUserID();
                        String mAcctNo = mPromoterResModel.getStripeUserId();
                        int mAmount = data.getIntExtra(EventsModel.EVENT_AMOUNT, 100);
                        if (mAcctNo.trim().equals("")) {
                            Toast.makeText(mContext, mContext.getString(R.string.promoter_account_err), Toast.LENGTH_SHORT).show();
                        } else {
                            RetrofitClient.getRetrofitInstance().postPaymentForEventBooking((BaseActivity) mContext, mToken, mAcctNo, mAmount, AppConstants.EVENT_PAYMENT,
                                    RetrofitClient.POST_DATA_FOR_PAYMENT);
                        }
                    }
                }
                break;
            case EVENT_QUESTIONS_REQ_CODE:
                if (mEventsFindListData.get(mAdapterPos).getPrice() == 0) {
                    mEventType = AppConstants.FREE_EVENT;
                    bookAnFreeEventRequest();
                } else {
                    Intent addOnActivity = new Intent(mContext, EventsAddOnActivity.class);
                    addOnActivity.putExtra(EventsModel.EVENT_AMOUNT, (mEventsFindListData.get(mAdapterPos).getPrice()));
                    addOnActivity.putExtra(EventsModel.EVENT_ID, mEventsFindListData.get(mAdapterPos).getID()).putExtra(AppConstants.PROFILE_ID, mMyProfileResModel.getID());
                    ((Activity) mContext).startActivityForResult(addOnActivity, EVENT_PAYMENT_REQ_CODE);
                }
                break;
            case EVENT_LIVE_PAYMENT_REQ_CODE:
                if (resultCode == RESULT_OK) {
                    if (data.hasExtra("TOKEN")) {
                        mToken = data.getStringExtra("TOKEN");
                        callPayViewLiveStream();
                    }
                }
                break;
        }

    }

    private boolean isAlreadyBooked(List<EventsWhoIsGoingResModel> eventsWhoIsGoingResModel) {
        boolean isAlreadyBooked = false;
        int mUserId = PreferenceUtils.getInstance(mContext).getIntData(PreferenceUtils.USER_ID);
        for (int i = 0; i < eventsWhoIsGoingResModel.size(); i++) {
            if (mUserId == eventsWhoIsGoingResModel.get(i).getUserID()) {
                isAlreadyBooked = true;
                break;
            }
        }
        return isAlreadyBooked;
    }

    private void callPayViewLiveStream() {
        isUpdatePayment = false;
        PromotersResModel mPromoterResModel = mEventsFindListData.get(mAdapterPos).getPromoterByUserID();
        String mAcctNo = mPromoterResModel.getStripeUserId();
        RetrofitClient.getRetrofitInstance().postPayForViewLiveStream(mContext, mRetrofitResInterface, mToken, mAcctNo, mStreamAmount, AppConstants.LIVE_STREAM_PAYMENT);
    }

    private void setResult() {
        //TODO
        ((BaseActivity) mContext).setResult(RESULT_OK, new Intent().putExtra(AppConstants.IS_FOLLOW_RESULT, true)
                .putExtra(AppConstants.MY_PROFILE_OBJ, mMyProfileResModel));
    }

    private void updateList(LiveStreamPaymentEntity mEntity) {
        ArrayList<LiveStreamPaymentEntity> mList = mEventsFindListData.get(mAdapterPos).getLivestreampayment_by_EventID();
        mList.add(mEntity);
        mEventsFindListData.get(mAdapterPos).setLivestreampayment_by_EventID(new ArrayList<>(mList));
        notifyDataSetChanged();
    }

    private void callUpdateLiveStreamPayment() {
        int mUserId = PreferenceUtils.getInstance(mContext).getIntData(PreferenceUtils.USER_ID);
        try {
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(APIConstants.PromoterID, mEventsFindListData.get(mAdapterPos).getUserID());
            mJsonObject.addProperty(APIConstants.ViewUserID, mUserId);
            mJsonObject.addProperty(APIConstants.EventID, mEventsFindListData.get(mAdapterPos).getID());
            mJsonObject.addProperty(APIConstants.TransactionID, mTransactionID);
            mJsonObject.addProperty(APIConstants.Amount, mStreamAmount);
            JsonArray mJsonArray = new JsonArray();
            mJsonArray.add(mJsonObject);
            isUpdatePayment = true;
            RetrofitClient.getRetrofitInstance().callUpdateLiveStreamPayment(mContext, mRetrofitResInterface, mJsonArray);
        } catch (Exception e) {
            ((BaseActivity) mContext).sysOut("" + e.getMessage());
        }
    }

    public void bookAnFreeEventRequest() {
        int mUserId = PreferenceUtils.getInstance(mContext).getIntData(PreferenceUtils.USER_ID);

        ArrayList<Integer> listCategory = new ArrayList<>();
        for (int k = 0; k < mSelectedCategory.size(); k++) {
            listCategory.add(mSelectedCategory.get(k).getID());
        }

        String list = Arrays.toString(listCategory.toArray()).replace("[", "").replace("]", "");

        JsonObject mItem = new JsonObject();
        mItem.addProperty("EventID", mEventsFindListData.get(mAdapterPos).getID());
        mItem.addProperty("UserID", mUserId);
        mItem.addProperty("ProfileID", mMyProfileResModel.getID());
        mItem.addProperty("Answers", String.valueOf(MotoHub.getApplicationInstance().getEventQuestionAnswerObject()));
        mItem.addProperty("transaction_token", "");
        mItem.addProperty("payment_details", mContext.getString(R.string.free_event));
        mItem.addProperty("addons", list);

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mItem);
        RetrofitClient.getRetrofitInstance().callBookNowEvent((BaseActivity) mContext, mJsonArray, RetrofitClient.EVENTS_FREE_BOOKING_RESPONSE);

        MotoHub.getApplicationInstance().setEventQuestionAnswerObject(null);
    }

    public void bookAnEventRequest(PaymentModel mPaymentModel) {

        int mUserId = PreferenceUtils.getInstance(mContext).getIntData(PreferenceUtils.USER_ID);

        ArrayList<Integer> listCategory = new ArrayList<>();
        for (int k = 0; k < mSelectedCategory.size(); k++) {
            listCategory.add(mSelectedCategory.get(k).getID());
        }

        String list = Arrays.toString(listCategory.toArray()).replace("[", "").replace("]", "");

        JsonObject mItem = new JsonObject();
        mItem.addProperty("EventID", mEventsFindListData.get(mAdapterPos).getID());
        mItem.addProperty("UserID", mUserId);
        mItem.addProperty("Payment Details", mPaymentModel.getOutcome().getSellerMessage());
        mItem.addProperty("Amount", mPaymentModel.getAmount());
        mItem.addProperty("To Account", mPaymentModel.getDestination());
        mItem.addProperty("PaymentStatus", mPaymentModel.getStatus());
        mItem.addProperty("ProfileID", mMyProfileResModel.getID());
        mItem.addProperty("Answers", String.valueOf(MotoHub.getApplicationInstance().getEventQuestionAnswerObject()));
        mItem.addProperty("transaction_token", mPaymentModel.getID());
        mItem.addProperty("payment_details", mPaymentModel.getMessage());
        mItem.addProperty("addons", list);


        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mItem);
        RetrofitClient.getRetrofitInstance().callBookNowEvent((BaseActivity) mContext, mJsonArray, RetrofitClient.EVENTS_BOOKING_RESPONSE);

        MotoHub.getApplicationInstance().setEventQuestionAnswerObject(null);

    }

    public void callPostRacingData() {

        JsonArray mJsonArray = new JsonArray();

        String mCarNumber;

        HashMap<Integer, ArrayList<RacingModel>> mEventCategoryRacingList = null;

        ArrayList<RacingModel> mEventRacingList = mEventsFindListData.get(mAdapterPos).getRacingList();


        if (mEventRacingList != null && !mEventRacingList.isEmpty()) {

            mEventCategoryRacingList = callGetDataByCategory(mEventRacingList);

        }


        for (int i = 0; i < mSelectedCategory.size(); i++) {

            if (mEventCategoryRacingList != null && !mEventCategoryRacingList.isEmpty()) {
                if (mEventCategoryRacingList.containsKey(mSelectedCategory.get(i).getGroupNo())) {
                    ArrayList<RacingModel> mRacingModelList = mEventCategoryRacingList.get(mSelectedCategory.get(i).getGroupNo());
                    int mLastIndex = mRacingModelList.size() - 1;
                    mCarNumber = String.valueOf(Integer.parseInt(mRacingModelList.get(mLastIndex).getCarNumber()) + 1);
                } else if (mEventsFindListData.get(i).getBookingNumberIndex().trim().equals("")) {
                    mCarNumber = "1";
                } else {
                    mCarNumber = (mEventsFindListData.get(mAdapterPos).getBookingNumberIndex()) + 1;
                }
            } else {
                if (mEventsFindListData.get(i).getBookingNumberIndex().trim().equals("")) {
                    mCarNumber = "1";
                } else {
                    mCarNumber = (mEventsFindListData.get(mAdapterPos).getBookingNumberIndex()) + 1;
                }
            }

            JsonObject mItem = new JsonObject();
            mItem.addProperty(RacingModel.EVENT_ID, mEventsFindListData.get(mAdapterPos).getID());
            mItem.addProperty(RacingModel.CATEGORY_ID, mSelectedCategory.get(i).getGroupNo());
            mItem.addProperty(RacingModel.PROFILE_ID, mMyProfileResModel.getID());
            mItem.addProperty(RacingModel.CAR_NUMBER, mCarNumber);
            mItem.addProperty(RacingModel.RACE_NUMBER, 0);
            mItem.addProperty(RacingModel.RACE_STATUS, 1);
            mItem.addProperty(RacingModel.RACE_NAME, 0);

            mJsonArray.add(mItem);
        }

        RetrofitClient.getRetrofitInstance().callPostRacingData((BaseActivity) mContext, mJsonArray, RetrofitClient.RACING_RESPONSE);
    }

    private HashMap<Integer, ArrayList<RacingModel>> callGetDataByCategory(ArrayList<RacingModel> mEventRacingList) {

        HashMap<Integer, ArrayList<RacingModel>> mCategoryEventDetails = new HashMap<>();
        ArrayList<RacingModel> mTempRacingModel = new ArrayList<>();
        int mKeyValue;

        for (int i = 0; i < mEventRacingList.size(); i++) {
            ArrayList<RacingModel> mRacingModel = new ArrayList<>();
            if (mTempRacingModel.contains(mEventRacingList.get(i))) {
                break;
            }
            mRacingModel.add(mEventRacingList.get(i));
            mKeyValue = mEventRacingList.get(i).getCategoryID();
            for (int j = i + 1; j < mEventRacingList.size(); j++) {
                if (mEventRacingList.get(i).getCategoryID() == (mEventRacingList.get(j).getCategoryID())) {
                    mRacingModel.add(mEventRacingList.get(j));
                }
            }
            mTempRacingModel.addAll(mRacingModel);
            mCategoryEventDetails.put(mKeyValue, mRacingModel);
        }

        return mCategoryEventDetails;

    }

    public void callPostSelectedAddOns(ArrayList<EventAddOnModel> mSelectedEventAddOn) {
        JsonArray mJsonArray = new JsonArray();

        for (int i = 0; i < mSelectedEventAddOn.size(); i++) {

            JsonObject mItem = new JsonObject();

            mItem.addProperty(PurchasedAddOnModel.ADDON_PRICE, mSelectedEventAddOn.get(i).getAddOnPrice());
            mItem.addProperty(PurchasedAddOnModel.EVENT_ID, mSelectedEventAddOn.get(i).getEventID());
            mItem.addProperty(PurchasedAddOnModel.ADDON_ID, mSelectedEventAddOn.get(i).getID());
            mItem.addProperty(PurchasedAddOnModel.ADDON_DESC, mSelectedEventAddOn.get(i).getAddOnDescription());
            mItem.addProperty(PurchasedAddOnModel.PROFILE_ID, mMyProfileResModel.getID());

            mJsonArray.add(mItem);

        }

        RetrofitClient.getRetrofitInstance().callPostPurchasedAddOn((BaseActivity) mContext, mJsonArray, RetrofitClient.POST_ADD_ON);
    }

    @Override
    public void bookAnEventSuccess(EventsWhoIsGoingResModel eventsWhoIsGoingResModel) {

        ArrayList<EventsWhoIsGoingResModel> mEventsWhoIsGoingResModels = mEventsFindListData.get(mAdapterPos).getWhoIsGoingByEventID();
        mEventsWhoIsGoingResModels.add(eventsWhoIsGoingResModel);
        mEventsFindListData.get(mAdapterPos).setWhoIsGoingByEventID(mEventsWhoIsGoingResModels);
        notifyDataSetChanged();


    }

    @NonNull
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults mResults = new FilterResults(); // Holds the results of a filtering operation in values
                List<EventsResModel> mFilteredArrList = new ArrayList<>();

                if (mOriginalEventsFindListData == null) {
                    mOriginalEventsFindListData = new ArrayList<>(mEventsFindListData); // saves the original data in mOriginalValues
                }

                //If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                //else does the Filtering and returns FilteredArrList(Filtered)
                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return
                    mResults.count = mOriginalEventsFindListData.size();
                    mResults.values = mOriginalEventsFindListData;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalEventsFindListData.size(); i++) {

                        if (constraint.toString().toLowerCase().equals("all")) {
                            mFilteredArrList.add(mOriginalEventsFindListData.get(i));
                        } else {
                            String mEventType = mOriginalEventsFindListData.get(i).getEventType().toLowerCase();
                            if (constraint.toString().toLowerCase().startsWith(mEventType)) {
                                mFilteredArrList.add(mOriginalEventsFindListData.get(i));
                            }
                        }

                    }
                    // set the Filtered result to return
                    mResults.count = mFilteredArrList.size();
                    mResults.values = mFilteredArrList;
                }

                return mResults;

            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                mEventsFindListData = (List<EventsResModel>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values

                if (mEventsFindListData.size() == 0) {
                    ((BaseActivity) mContext).showToast(mContext, mContext.getResources().getString(R.string.no_events_err));
                }

            }
        };
    }

    public static class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.starts_in_tv)
        TextView mStartInDaysTv;
        @BindView(R.id.moto_event_name_tv)
        TextView mEventNameTv;
        @BindView(R.id.who_is_going_btn)
        Button mWhoIsGoingBtn;
        @BindView(R.id.book_now_img_btn)
        Button mBookNowBtn;
        @BindView(R.id.time_table_btn)
        Button mTimeTableBtn;
        @BindView(R.id.grp_chat_img_btn)
        Button mEventGrpChatBtn;
        @BindView(R.id.live_btn)
        Button mLiveButton;
        @BindView(R.id.promoter_follow_btn)
        Button mPromoterFollowBtn;
        @BindView(R.id.promoter_profile_img)
        CircleImageView mPromoterProfileImg;
        @BindView(R.id.promoter_name_txt)
        TextView mPromoterNameTxt;
        @BindView(R.id.coverImg)
        ImageView mCoverImg;
        @BindView(R.id.promoter_lay)
        RelativeLayout mPromoterLay;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
