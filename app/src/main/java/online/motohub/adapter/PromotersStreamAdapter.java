package online.motohub.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.PaymentActivity;
import online.motohub.activity.PromoterLiveStreamViewActivity;
import online.motohub.activity.club.ClubProfileActivity;
import online.motohub.activity.news_and_media.NewsAndMediaProfileActivity;
import online.motohub.activity.promoter.PromoterProfileActivity;
import online.motohub.activity.track.TrackProfileActivity;
import online.motohub.interfaces.CommonInterface;
import online.motohub.interfaces.RetrofitResInterface;
import online.motohub.model.LiveStreamPaymentEntity;
import online.motohub.model.LiveStreamPaymentResponse;
import online.motohub.model.PaymentModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.dialog.DialogManager;
import online.motohub.util.PreferenceUtils;

import static android.app.Activity.RESULT_OK;


public class PromotersStreamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int LIVE_PAYMENT_REQ_CODE = 500;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private Context mContext;
    private ArrayList<PromotersResModel> mStreamUserList;
    private LayoutInflater mInflater;
    private ProfileResModel mMyProfileResModel;
    private int mAdapterPos;
    //TODO Live payment cost $4.99 * 100(cent)
    private int mAmount = 499;
    CommonInterface mPaymentAlertInterface = new CommonInterface() {
        @Override
        public void onSuccess() {
            Intent paymentActivity = new Intent(mContext, PaymentActivity.class);
            paymentActivity.putExtra("PaymentAmount", mAmount).putExtra(AppConstants.PROFILE_ID, mMyProfileResModel.getID());
            ((Activity) mContext).startActivityForResult(paymentActivity, LIVE_PAYMENT_REQ_CODE);
        }
    };
    private boolean isUpdatePayment = false;
    private String mToken = "";
    private int mMyUserID = 0;
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
    RetrofitResInterface mRetrofitResInterface = new RetrofitResInterface() {
        @Override
        public void retrofitOnResponse(Object responseObj, int responseType) {

            if (responseObj instanceof PaymentModel) {
                ((BaseActivity) mContext).sysOut(responseObj.toString());
                PaymentModel mResponse = (PaymentModel) responseObj;
                if (mResponse.getStatus() != null && mResponse.getStatus().equals("succeeded")) {
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
                DialogManager.showRetryAlertDialogWithCallback(mContext, mCommonInterface, mErrorMsg);
            }

        }

        @Override
        public void retrofitOnSessionError(int code, String message) {
            ((BaseActivity) mContext).retrofitOnSessionError(code, message);
        }

        @Override
        public void retrofitOnFailure() {
            DialogManager.showRetryAlertDialogWithCallback(mContext, mCommonInterface, mContext.getString(R.string.internet_err));
        }
    };

    public PromotersStreamAdapter(Context context, ArrayList<PromotersResModel> streamUserList, ProfileResModel myProfileResModel) {
        mContext = context;
        mStreamUserList = streamUserList;
        mInflater = LayoutInflater.from(mContext);
        mMyProfileResModel = myProfileResModel;
        mMyUserID = mMyProfileResModel.getUserID();
    }

    public void updatePromoterFollowResponse(PromotersResModel promotersResModel) {
        mStreamUserList.set(mAdapterPos, promotersResModel);
        notifyItemChanged(mAdapterPos);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView;
        switch (viewType) {
            case VIEW_TYPE_ITEM:
                mView = mInflater.inflate(R.layout.adap_stream_requested_users, parent, false);
                return new Holder(mView);
            case VIEW_TYPE_LOADING:
                mView = mInflater.inflate(R.layout.adap_loading_item, parent, false);
                return new ViewHolderLoader(mView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int pos) {
        switch (getItemViewType(pos)) {
            case VIEW_TYPE_ITEM:
                try {
                    final Holder mHolder = (Holder) holder;
                    PromotersResModel mEntity = mStreamUserList.get(pos);
                    String imgStr = mEntity.getProfileImage();
                    if (!imgStr.isEmpty()) {
                        ((BaseActivity) mContext).setImageWithGlide(mHolder.mUserImg, imgStr, R.drawable.default_profile_icon);
                    } else {
                        mHolder.mUserImg.setImageResource(R.drawable.default_profile_icon);
                    }
                    mHolder.mStartStreamBtn.setVisibility(View.GONE);
                    mHolder.mViewStreamBtn.setText(mContext.getString(R.string.view_stream));
                    mHolder.mUserImg.setTag(pos);
                    mHolder.mUserImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int selPos = (int) v.getTag();
                            mAdapterPos = selPos;
                            Class mClassName;
                            String mUserType = mStreamUserList.get(selPos).getUserType();
                            switch (mUserType) {
                                case PromotersModel.NEWS_AND_MEDIA:
                                    mClassName = NewsAndMediaProfileActivity.class;
                                    break;
                                case PromotersModel.CLUB:
                                    mClassName = ClubProfileActivity.class;
                                    break;
                                case PromotersModel.PROMOTER:
                                    mClassName = PromoterProfileActivity.class;
                                    break;
                                case PromotersModel.TRACK:
                                    mClassName = TrackProfileActivity.class;
                                    break;
                                default:
                                    mClassName = PromoterProfileActivity.class;
                                    break;
                            }
                            /*Bundle mBundle = new Bundle();
                            mBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, mStreamUserList.get(selPos));
                            mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
                            ((BaseActivity) mContext).startActivityForResult(
                                    new Intent(mContext, mClassName).putExtras(mBundle),
                                    AppConstants.FOLLOWERS_FOLLOWING_RESULT);*/
                            /*MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                            MotoHub.getApplicationInstance().setmPromoterResModel(mStreamUserList.get(selPos));*/
                            EventBus.getDefault().postSticky(mMyProfileResModel);
                            EventBus.getDefault().postSticky(mStreamUserList.get(selPos));
                            ((BaseActivity) mContext).startActivityForResult(
                                    new Intent(mContext, mClassName),
                                    AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                        }
                    });
                    mHolder.mViewStreamBtn.setTag(pos);
                    mHolder.mViewStreamBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAdapterPos = (int) v.getTag();
                            if (isAlreadyPaid()) {
                                int receiverProfileID = mStreamUserList.get(mAdapterPos).getUserId();
                                Intent mGoWatchActivity = new Intent(mContext, PromoterLiveStreamViewActivity.class);
                                mGoWatchActivity.putExtra(AppConstants.PROFILE_ID, receiverProfileID);
                                mContext.startActivity(mGoWatchActivity);
                            } else {
                                DialogManager.showAlertDialogWithCallback(mContext, mPaymentAlertInterface,
                                        mContext.getString(R.string.alert_live_pay_amount));
                            }

                        }
                    });
                    mHolder.mUserNameTxt.setText(mEntity.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case VIEW_TYPE_LOADING:
                ViewHolderLoader mViewHolderLoader = (ViewHolderLoader) holder;
                mViewHolderLoader.mProgressBar.setIndeterminate(true);
                break;
            default:
                break;

        }
    }

    private boolean isAlreadyPaid() {
        boolean isAlreadyPaid = false;
        for (PromotersResModel mPromotersResModel : mStreamUserList) {
            if (isAlreadyPaid)
                break;
            for (LiveStreamPaymentEntity mPaymentEntity : mPromotersResModel.getLivestreampayment_by_PromoterID()) {
                if (mPaymentEntity.getViewUserID() == mMyUserID) {
                    isAlreadyPaid = true;
                    break;
                }
            }
        }
        return isAlreadyPaid;
    }


    @Override
    public int getItemCount() {
        return mStreamUserList.size();
    }

    @Override
    public int getItemViewType(int pos) {
        if (mStreamUserList.get(pos) == null) {
            return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case LIVE_PAYMENT_REQ_CODE:
                if (resultCode == RESULT_OK) {
                    if (data.hasExtra("TOKEN")) {
                        mToken = data.getStringExtra("TOKEN");
                        callPayViewLiveStream();
                    }
                }
                break;


        }

    }

    private void callPayViewLiveStream() {
        isUpdatePayment = false;
        RetrofitClient.getRetrofitInstance().postPayForViewLiveStream(mContext, mRetrofitResInterface, mToken, mStreamUserList.get(mAdapterPos).getStripeUserId(), mAmount, AppConstants.LIVE_STREAM_PAYMENT);
    }

    private void callUpdateLiveStreamPayment() {
        try {
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(APIConstants.PromoterID, mStreamUserList.get(mAdapterPos).getUserId());
            mJsonObject.addProperty(APIConstants.ViewUserID, mMyUserID);
            JsonArray mJsonArray = new JsonArray();
            mJsonArray.add(mJsonObject);
            isUpdatePayment = true;
            RetrofitClient.getRetrofitInstance().callUpdateLiveStreamPayment(mContext, mRetrofitResInterface, mJsonArray);
        } catch (Exception e) {
            ((BaseActivity) mContext).sysOut("" + e.getMessage());
        }
    }

    private void updateList(LiveStreamPaymentEntity mEntity) {
        ArrayList<LiveStreamPaymentEntity> mList = mStreamUserList.get(mAdapterPos).getLivestreampayment_by_PromoterID();
        mList.add(mEntity);
        mStreamUserList.get(mAdapterPos).setLivestreampayment_by_PromoterID(new ArrayList<>(mList));
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_img)
        CircleImageView mUserImg;
        @BindView(R.id.user_name_txt)
        TextView mUserNameTxt;
        @BindView(R.id.accept_btn)
        TextView mStartStreamBtn;
        @BindView(R.id.decline_btn)
        TextView mViewStreamBtn;

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class ViewHolderLoader extends RecyclerView.ViewHolder {

        @BindView(R.id.progress_bar)
        ProgressBar mProgressBar;

        public ViewHolderLoader(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
