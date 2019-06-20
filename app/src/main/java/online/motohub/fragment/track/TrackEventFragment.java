package online.motohub.fragment.track;

import android.app.Activity;
import android.content.Context;
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

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.CreateProfileActivity;
import online.motohub.adapter.EventsFindAdapter;
import online.motohub.application.MotoHub;
import online.motohub.fragment.BaseFragment;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.EventsInterface;
import online.motohub.model.EventAddOnModel;
import online.motohub.model.EventAnswersModel;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsResModel;
import online.motohub.model.EventsWhoIsGoingModel;
import online.motohub.model.PaymentModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PurchasedAddOnModel;
import online.motohub.model.RacingModel;
import online.motohub.model.SessionModel;
import online.motohub.model.TrackResModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.util.PreferenceUtils;

import static android.app.Activity.RESULT_OK;

public class TrackEventFragment extends BaseFragment {

    private static final String TAG = TrackEventFragment.class.getName();

    @BindView(R.id.events_list_view)
    RecyclerView mRecyclerView;

    @BindString(R.string.no_events_err)
    String mNoEventsErr;

    private Activity mActivity;
    private Unbinder mUnBinder;
    private ArrayList<EventsResModel> mEventsFindListData = new ArrayList<>();
    private EventsFindAdapter mEventsFindAdapter;
    private boolean mRefresh = true;
    private ProfileResModel mMyProfileResModel;
    private PromotersResModel mPromoterResModel;
    private PaymentModel mTempPaymentModel;
    private int mFailureResponseType;
    private TrackResModel mTrackResModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mRefresh = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnBinder = ButterKnife.bind(this, view);
        initView();
    }

    private void initView() {
        assert getArguments() != null;
        /*mMyProfileResModel = (ProfileResModel) getArguments().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        mPromoterResModel = (PromotersResModel) getArguments().getSerializable(PromotersModel.PROMOTERS_RES_MODEL);*/
        /*mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        mPromoterResModel = MotoHub.getApplicationInstance().getmPromoterResModel();*/
        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
        mPromoterResModel = EventBus.getDefault().getStickyEvent(PromotersResModel.class);
        ((BaseActivity) getActivity()).setUpPurchseSuccessUI();
    }

    private void setAdapter() {
        if (mEventsFindAdapter == null) {
            if (mMyProfileResModel != null && mMyProfileResModel.getID() != 0) {
                mEventsFindAdapter = new EventsFindAdapter(getContext(), mEventsFindListData, mMyProfileResModel, null, false);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(mEventsFindAdapter);
            }
        } else {
            mEventsFindAdapter.notifyDataSetChanged();
        }
    }

    public void callGetEvents(int userid) {
        super.callGetEvents();
        if (mRefresh) {
            callGetTrackEvents(userid);
        }
    }

    private void callGetTrackEvents(int userid) {
        int status = 2;
        String mDateFilter = "((( Date >= " + ((BaseActivity) mActivity).getCurrentDate() + " ) OR ( Finish >= " + ((BaseActivity) mActivity).getCurrentDate()
                + " )) AND ( EventStatus=" + status + ")) AND (UserID = " + userid + ")";
        RetrofitClient.getRetrofitInstance().callGetAllEventsFromTrack(((BaseActivity) mActivity), mDateFilter, RetrofitClient.GET_ALL_EVENTS_FROM_TRACK_RESPONSE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case EventsFindAdapter.EVENT_PAYMENT_REQ_CODE:
                    mEventsFindAdapter.onActivityResult(requestCode, resultCode, data);
                    break;
                case EventsFindAdapter.EVENT_QUESTIONS_REQ_CODE:
                    mEventsFindAdapter.onActivityResult(requestCode, resultCode, data);
                    break;
                case EventsFindAdapter.EVENT_LIVE_PAYMENT_REQ_CODE:
                    mEventsFindAdapter.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

    @Override
    public void alertDialogPositiveBtnClick(String dialogType, int position) {
        super.alertDialogPositiveBtnClick(dialogType, position);
        switch (dialogType) {
            case AppDialogFragment.ALERT_API_FAILURE_DIALOG:
                switch (mFailureResponseType) {
                    case RetrofitClient.EVENTS_BOOKING_RESPONSE:
                        mEventsFindAdapter.bookAnEventRequest(mTempPaymentModel);
                        break;
                    case RetrofitClient.RACING_RESPONSE:
                        mEventsFindAdapter.callPostRacingData();
                        break;
                }
                break;
            case AppDialogFragment.ALERT_SPECTATOR_UPDATE_DIALOG:
                startActivity(new Intent(mActivity, CreateProfileActivity.class).putExtra(BaseActivity.CREATE_PROF_AFTER_REG, true)
                        .putExtra(AppConstants.TAG, TAG));
                ((BaseActivity) mActivity).dismissAppDialog();
                break;
            default:
                mEventsFindAdapter.alertDialogPositiveBtnClick(dialogType);
                ((BaseActivity) mActivity).dismissAppDialog();
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof EventsWhoIsGoingModel) {
            EventsWhoIsGoingModel mEventsWhoIsGoingModel = (EventsWhoIsGoingModel) responseObj;
            if (mEventsWhoIsGoingModel.getResource() != null && mEventsWhoIsGoingModel.getResource().size() > 0) {
                EventsInterface mEventsInterface = mEventsFindAdapter;
                mEventsInterface.bookAnEventSuccess(mEventsWhoIsGoingModel.getResource().get(0));
                mEventsFindAdapter.callPostRacingData();
            }
        } else if (responseObj instanceof PaymentModel) {
            PaymentModel mPaymentModel = (PaymentModel) responseObj;
            mTempPaymentModel = mPaymentModel;
            mEventsFindAdapter.bookAnEventRequest(mPaymentModel);
        } else if (responseObj instanceof SessionModel) {
            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(getActivity()).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(getActivity()).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            mEventsFindListData.clear();
        } else if (responseObj instanceof RacingModel) {
            ArrayList<EventAddOnModel> mSelectedEventAddOn = MotoHub.getApplicationInstance().getPurchasedAddOn();
            if (mSelectedEventAddOn.size() > 0)
                mEventsFindAdapter.callPostSelectedAddOns(mSelectedEventAddOn);
            else {
                if (mEventsFindAdapter.mEventType.equals(AppConstants.FREE_EVENT)) {
                    ((BaseActivity) getActivity()).showToast(getActivity(), getString(R.string.event_booked_successfully));
                } else {
                    if (((BaseActivity) getActivity()).mPurchaseSuccessDialog != null)
                        ((BaseActivity) getActivity()).mPurchaseSuccessDialog.show();
                }
            }
        } else if (responseObj instanceof PurchasedAddOnModel) {
            if (mEventsFindAdapter.mEventType.equals(AppConstants.FREE_EVENT)) {
                showToast(getActivity(), "Successfully booked an event.");
            } else {
                if (((BaseActivity) getActivity()).mPurchaseSuccessDialog != null)
                    ((BaseActivity) getActivity()).mPurchaseSuccessDialog.show();
            }
        } else if (responseObj instanceof EventsModel) {
            EventsModel model = (EventsModel) responseObj;
            if (model.getResource() != null && model.getResource().size() > 0) {
                mEventsFindListData.clear();
                mEventsFindListData.addAll(model.getResource());
            } else {
                ((BaseActivity) mActivity).showToast(mActivity, mNoEventsErr);
            }
            setAdapter();
        } else if (responseObj instanceof EventAnswersModel) {
            EventAnswersModel mEventAnswerList = (EventAnswersModel) responseObj;
            ArrayList<EventAnswersModel> mResEventAnswerModel = mEventAnswerList.getResource();
            mEventsFindAdapter.callUpdateEventAnswerScreen(mResEventAnswerModel);
        }
    }

    @Override
    public void retrofitOnFailure(int responseType) {
        super.retrofitOnFailure(responseType);
        mFailureResponseType = responseType;
        ((BaseActivity) getActivity()).showAppDialog(AppDialogFragment.ALERT_API_FAILURE_DIALOG, null);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mEventsFindListData.size() == 0)
            /*if (mTrackResModel != null)*/
            callGetEvents(mPromoterResModel.getUserId());
    }

}
