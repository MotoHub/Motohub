package online.motohub.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import online.motohub.activity.CameraActivity;
import online.motohub.application.MotoHub;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.CommonInterface;
import online.motohub.interfaces.RetrofitResInterface;
import online.motohub.model.LiveStreamRequestEntity;
import online.motohub.model.LiveStreamRequestResponse;
import online.motohub.model.LiveStreamResponse;
import online.motohub.model.ProfileResModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.StringUtils;


public class StreamUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CommonInterface {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private Context mContext;
    private ArrayList<ProfileResModel> mStreamUserList;
    private int mCurrentProfileID, mCurrentUserID;
    private int selPos;
    private LayoutInflater mInflater;
    private boolean isDelete = false;
    private int liveStreamID = 0;
    private String mLiveStreamName = "";
    private boolean isAcceptAPI = false;
    RetrofitResInterface mApiInterface = new RetrofitResInterface() {
        @Override
        public void retrofitOnResponse(Object responseObj, int responseType) {
            if (responseObj instanceof LiveStreamRequestResponse) {
                LiveStreamRequestResponse mStreamReqResponse = (LiveStreamRequestResponse) responseObj;
                if (mStreamReqResponse.getResource().size() > 0) {
                    if (isAcceptAPI) {
                        updateRequestStatus(mStreamReqResponse.getResource().get(0));
                    } else {
                        updateDeclineStatus();
                    }

                }
            }
            if (responseObj instanceof LiveStreamResponse) {
                if (isDelete) {
                    isDelete = false;
                } else {
                    LiveStreamResponse mLiveStreamResponse = (LiveStreamResponse) responseObj;
                    if (mLiveStreamResponse.getResource().size() > 0) {
                        liveStreamID = mLiveStreamResponse.getResource().get(0).getID();
                        mLiveStreamName = mLiveStreamResponse.getResource().get(0).getStreamName();
                    }
                    MotoHub.getApplicationInstance().setLiveStreamName(mLiveStreamName);
                    Intent mCameraActivity = new Intent(mContext, CameraActivity.class);
                    mContext.startActivity(mCameraActivity);
                }
            }
        }

        @Override
        public void retrofitOnError(int code, String message) {
            if (message.equals("Unauthorized") || code == 401) {
                RetrofitClient.getRetrofitInstance().callUpdateSession(mContext, mApiInterface, RetrofitClient.UPDATE_SESSION_RESPONSE);
            } else {
                String mErrorMsg = code + " - " + message;
                ((BaseActivity) mContext).showToast(mContext, mErrorMsg);
            }
        }

        @Override
        public void retrofitOnSessionError(int code, String message) {
            String mErrorMsg = code + " - " + message;
            ((BaseActivity) mContext).showToast(mContext, mErrorMsg);
        }

        @Override
        public void retrofitOnFailure() {
            ((BaseActivity) mContext).showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
        }
    };

    public StreamUserAdapter(Context context, int currentProfileID, ArrayList<ProfileResModel> streamUserList) {
        AppConstants.LIVE_STREAM_CALL_BACK = this;
        mContext = context;
        mCurrentProfileID = currentProfileID;
        mCurrentUserID = PreferenceUtils.getInstance(mContext).getIntData(PreferenceUtils.USER_ID);
        mStreamUserList = streamUserList;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView;
        switch (viewType) {
            case VIEW_TYPE_ITEM:
                mView = mInflater.inflate(R.layout.adap_stream_users, parent, false);
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
                    ProfileResModel mProfileEntity = mStreamUserList.get(pos);
                    String imgStr = mProfileEntity.getProfilePicture();
                    if (!imgStr.isEmpty()) {
                        ((BaseActivity) mContext).setImageWithGlide(mHolder.mUserImg, imgStr, R.drawable.default_profile_icon);
                    } else {
                        mHolder.mUserImg.setImageResource(R.drawable.default_profile_icon);
                    }
                    setRequestStatus(mHolder.mSendRequestBtn, mProfileEntity.getLivestreamrequest_by_ReceiverProfileID());

                    mHolder.mUserImg.setTag(pos);
                    mHolder.mUserImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int selPos = (int) v.getTag();
                            Bundle mBundle = new Bundle();
                            ProfileResModel mOthersProfileModel = new ProfileResModel();
                            mOthersProfileModel.setID(mStreamUserList.get(selPos).getID());
                            //mBundle.putSerializable(ProfileModel.OTHERS_PROFILE_RES_MODEL, mOthersProfileModel);
                            MotoHub.getApplicationInstance().setmOthersProfileResModel(mOthersProfileModel);
                            ProfileResModel mMyProfileModel = new ProfileResModel();
                            mMyProfileModel.setID(mCurrentProfileID);
                            //mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileModel);
                            //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileModel);
                            EventBus.getDefault().postSticky(mMyProfileModel);
                        }
                    });
                    mHolder.mSendRequestBtn.setTag(pos);
                    mHolder.mSendRequestBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selPos = (int) v.getTag();
                            int receiverProfileID = mStreamUserList.get(selPos).getID();
                            if (mHolder.mSendRequestBtn.getText().toString().equals(mContext.getString(R.string.send_stream_request))) {
                                callSendRequestAPI(receiverProfileID);
                            } else if (mHolder.mSendRequestBtn.getText().toString().equals(mContext.getString(R.string.cancel_request))) {
                                callDeclineRequestAPI();
                            } else {
                                ((BaseActivity) mContext).showToast(mContext, mContext.getString(R.string.accepted));
                            }
                        }
                    });
                    if (mProfileEntity.getProfileType() == 5) {
                        mHolder.mUserNameTxt.setText(mProfileEntity.getSpectatorName());
                    } else {
                        mHolder.mUserNameTxt.setText(mProfileEntity.getDriver());
                    }

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

    private void setRequestStatus(Button mSendRequestBtn, ArrayList<LiveStreamRequestEntity> mRequestedList) {
        mSendRequestBtn.setText(mContext.getString(R.string.send_stream_request));
        for (int i = 0; i < mRequestedList.size(); i++) {
            LiveStreamRequestEntity mEntity = mRequestedList.get(i);
            if (mEntity.getRequestedUserID() == mCurrentUserID && mEntity.getRequestedProfileID() == mCurrentProfileID) {
                if (mEntity.getStatus() == 1) {
                    // Status =1 --> Accepted Start Stream Directly
                    mSendRequestBtn.setText(mContext.getString(R.string.accepted));
                } else {
                    // Status =0 --> Request Sent
                    mSendRequestBtn.setText(mContext.getString(R.string.cancel_request));
                }
                break;
            }
        }
    }

    private void callSendRequestAPI(int receiverProfileID) {

        try {
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(APIConstants.RequestedUserID, mCurrentUserID);
            mJsonObject.addProperty(APIConstants.RequestedProfileID, mCurrentProfileID);
            mJsonObject.addProperty(APIConstants.ReceiverProfileID, receiverProfileID);
            mJsonObject.addProperty(APIConstants.Status, 0);
            JsonArray mJsonArray = new JsonArray();
            mJsonArray.add(mJsonObject);
            isAcceptAPI = true;
            RetrofitClient.getRetrofitInstance().callSendStreamRequest(mContext, mApiInterface, mJsonArray);
        } catch (Exception e) {
            ((BaseActivity) mContext).sysOut("" + e.getMessage());
        }
    }

    private void callDeclineRequestAPI() {
        try {
            String mFilter = APIConstants.ID + "=" + getDeclineStreamRequestID(mStreamUserList.get(selPos).getLivestreamrequest_by_ReceiverProfileID());
            isAcceptAPI = false;
            RetrofitClient.getRetrofitInstance().callDeclineStreamRequest(mContext, mApiInterface, mFilter);
        } catch (Exception e) {
            ((BaseActivity) mContext).sysOut("" + e.getMessage());
        }
    }

    private int getDeclineStreamRequestID(ArrayList<LiveStreamRequestEntity> mRequestedList) {
        int mID = 0;
        for (int i = 0; i < mRequestedList.size(); i++) {
            LiveStreamRequestEntity mEntity = mRequestedList.get(i);
            if (mEntity.getRequestedUserID() == mCurrentUserID && mEntity.getRequestedProfileID() == mCurrentProfileID) {
                mID = mEntity.getID();
                break;
            }
        }
        return mID;
    }

    private void updateDeclineStatus() {
        ArrayList<LiveStreamRequestEntity> mReqList = mStreamUserList.get(selPos).getLivestreamrequest_by_ReceiverProfileID();
        for (int i = 0; i < mReqList.size(); i++) {
            LiveStreamRequestEntity mEntity = mReqList.get(i);
            if (mEntity.getRequestedUserID() == mCurrentUserID && mEntity.getRequestedProfileID() == mCurrentProfileID) {
                mReqList.remove(i);
                break;
            }
        }
        mStreamUserList.get(selPos).setLivestreamrequest_by_ReceiverProfileID(new ArrayList<>(mReqList));
        notifyDataSetChanged();
    }

    private void updateRequestStatus(LiveStreamRequestEntity mReqEntity) {
        ArrayList<LiveStreamRequestEntity> mReqList = mStreamUserList.get(selPos).getLivestreamrequest_by_ReceiverProfileID();
        mReqList.add(mReqEntity);
        mStreamUserList.get(selPos).setLivestreamrequest_by_ReceiverProfileID(new ArrayList<>(mReqList));
        notifyDataSetChanged();
    }

    public void filterList(ArrayList<ProfileResModel> filteredNames) {
        this.mStreamUserList = filteredNames;
        notifyDataSetChanged();
    }

    private void startSingleStream(int mStreamProfileID) {
        try {
            String mStreamName = StringUtils.genRandomStreamName(mContext);
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(APIConstants.StreamName, mStreamName);
            mJsonObject.addProperty(APIConstants.CreatedProfileID, mCurrentProfileID);
            mJsonObject.addProperty(APIConstants.StreamProfileID, mStreamProfileID);
            JsonArray mJsonArray = new JsonArray();
            mJsonArray.add(mJsonObject);
            RetrofitClient.getRetrofitInstance().callPostLiveStream(mContext, mApiInterface, mJsonArray);
        } catch (Exception e) {
            ((BaseActivity) mContext).sysOut("" + e.getMessage());
        }

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

    @Override
    public void onSuccess() {
        isDelete = true;
        String mLiveStreamName = "ID=" + liveStreamID;
        RetrofitClient.getRetrofitInstance().callDeleteLiveStream(mContext, mApiInterface, mLiveStreamName);
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_img)
        CircleImageView mUserImg;
        @BindView(R.id.user_name_txt)
        TextView mUserNameTxt;
        @BindView(R.id.send_request_btn)
        Button mSendRequestBtn;

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
