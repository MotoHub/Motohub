package online.motohub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.RetrofitResInterface;
import online.motohub.model.LiveStreamRequestEntity;
import online.motohub.model.LiveStreamRequestResponse;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;


public class StreamRequestedUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<LiveStreamRequestEntity> mStreamUserList;
    private int mCurrentProfileID, mCurrentUserID;
    private int selPos;
    private LayoutInflater mInflater;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private boolean isAcceptAPI = false;

    public StreamRequestedUserAdapter(Context context, int currentProfileID, ArrayList<LiveStreamRequestEntity> streamUserList) {
        mContext = context;
        mCurrentProfileID = currentProfileID;
        mCurrentUserID = PreferenceUtils.getInstance(mContext).getIntData(PreferenceUtils.USER_ID);
        mStreamUserList = streamUserList;
        mInflater = LayoutInflater.from(mContext);
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_img)
        CircleImageView mUserImg;
        @BindView(R.id.user_name_txt)
        TextView mUserNameTxt;
        @BindView(R.id.accept_btn)
        TextView mAcceptBtn;
        @BindView(R.id.decline_btn)
        TextView mDeclineBtn;

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
                    LiveStreamRequestEntity mEntity = mStreamUserList.get(pos);
                    String imgStr = mEntity.getProfiles_by_RequestedProfileID().getProfilePicture();
                    if (!imgStr.isEmpty()) {
                        ((BaseActivity) mContext).setImageWithGlide(mHolder.mUserImg, imgStr, R.drawable.default_profile_icon);
                    } else {
                        mHolder.mUserImg.setImageResource(R.drawable.default_profile_icon);
                    }
                    if (mEntity.getStatus() == 1) {
                        // Status =1 --> Accepted Start Stream Directly
                        mHolder.mAcceptBtn.setText(mContext.getString(R.string.accepted));
                    } else {
                        // Status =0 --> User should call accept API
                        mHolder.mAcceptBtn.setText(mContext.getString(R.string.accept));
                    }
                    mHolder.mUserImg.setTag(pos);
                    mHolder.mUserImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int selPos = (int) v.getTag();

                        }
                    });
                    mHolder.mAcceptBtn.setTag(pos);
                    mHolder.mAcceptBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selPos = (int) v.getTag();
                            if (mHolder.mAcceptBtn.getText().toString().equals(mContext.getString(R.string.accept))) {
                                callAcceptRequestAPI(selPos);
                            } else {
                                ((BaseActivity) mContext).showToast(mContext, mContext.getString(R.string.request_accepted_already));
                            }
                        }
                    });
                    mHolder.mDeclineBtn.setTag(pos);
                    mHolder.mDeclineBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selPos = (int) v.getTag();
                            callDeclineRequestAPI(selPos);
                        }
                    });
                    if (mEntity.getProfiles_by_RequestedProfileID().getProfileType() == 5) {
                        mHolder.mUserNameTxt.setText(mEntity.getProfiles_by_RequestedProfileID().getSpectatorName());
                    } else {
                        mHolder.mUserNameTxt.setText(mEntity.getProfiles_by_RequestedProfileID().getDriver());
                    }
                }catch (Exception e){
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

    private void callAcceptRequestAPI(int pos) {

        try {
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(APIConstants.ID, mStreamUserList.get(pos).getID());
            mJsonObject.addProperty(APIConstants.RequestedUserID, mStreamUserList.get(pos).getRequestedUserID());
            mJsonObject.addProperty(APIConstants.RequestedProfileID, mStreamUserList.get(pos).getRequestedProfileID());
            mJsonObject.addProperty(APIConstants.ReceiverProfileID, mStreamUserList.get(pos).getReceiverProfileID());
            mJsonObject.addProperty(APIConstants.Status, 1);
            JsonArray mJsonArray = new JsonArray();
            mJsonArray.add(mJsonObject);
            isAcceptAPI = true;
            RetrofitClient.getRetrofitInstance().callAcceptStreamRequest(mContext, mApiInterface, mJsonArray);
        } catch (Exception e) {
            ((BaseActivity) mContext).sysOut("" + e.getMessage());
        }
    }

    private void callDeclineRequestAPI(int pos) {
        try {
            String mFilter = APIConstants.ID + "=" + mStreamUserList.get(pos).getID();
            isAcceptAPI = false;
            RetrofitClient.getRetrofitInstance().callDeclineStreamRequest(mContext, mApiInterface, mFilter);
        } catch (Exception e) {
            ((BaseActivity) mContext).sysOut("" + e.getMessage());
        }
    }

    RetrofitResInterface mApiInterface = new RetrofitResInterface() {
        @Override
        public void retrofitOnResponse(Object responseObj, int responseType) {
            if (responseObj instanceof LiveStreamRequestResponse) {
                LiveStreamRequestResponse mStreamReqResponse = (LiveStreamRequestResponse) responseObj;
                if (isAcceptAPI) {
                    updateAcceptStatus();
                } else {
                    updateDeclineStatus();
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

    private void updateAcceptStatus() {
        mStreamUserList.get(selPos).setStatus(1);
        notifyDataSetChanged();
    }

    private void updateDeclineStatus() {
        mStreamUserList.remove(selPos);
        notifyDataSetChanged();
    }

    public void filterList(ArrayList<LiveStreamRequestEntity> filteredNames) {
        this.mStreamUserList = filteredNames;
        notifyDataSetChanged();
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

}
