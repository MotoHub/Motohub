package online.motohub.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.interfaces.RetrofitResInterface;
import online.motohub.model.BlockedUserModel;
import online.motohub.model.BlockedUserResModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.util.CommonAPI;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.Utility;

import static android.app.Activity.RESULT_OK;


public class BlockedProfilesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<ProfileResModel> mBlockedProfileList;
    private LayoutInflater mInflater;
    private ProfileResModel mMyProfileResModel;
    RetrofitResInterface mRetrofitResInterface = new RetrofitResInterface() {
        @Override
        public void retrofitOnResponse(Object responseObj, int responseType) {
            ((BaseActivity) mContext).sysOut(responseObj.toString());
            if (responseObj instanceof BlockedUserModel) {
                BlockedUserModel mResponse = (BlockedUserModel) responseObj;
                if (mResponse.getResource().size() > 0) {
                    switch (responseType) {
                        case RetrofitClient.CALL_BLOCK_USER_PROFILE:
                            updateBlockProfile(mResponse.getResource().get(0));
                            break;
                        case RetrofitClient.CALL_UN_BLOCK_USER_PROFILE:
                            updateUnBlockProfile(mResponse.getResource().get(0));
                            break;
                    }
                    setResult();
                } else {
                    ((BaseActivity) mContext).showToast(mContext, mContext.getString(R.string.something_wrong));
                }

            } else if (responseObj instanceof SessionModel) {
                SessionModel mSessionModel = (SessionModel) responseObj;
                if (mSessionModel.getSessionToken() == null) {
                    PreferenceUtils.getInstance(mContext).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
                } else {
                    PreferenceUtils.getInstance(mContext).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
                }
                ((BaseActivity) mContext).showToast(mContext, mContext.getString(R.string.session_updated));
            }
        }

        @Override
        public void retrofitOnError(int code, String message) {
            if (message.equals("Unauthorized") || code == 401) {
                RetrofitClient.getRetrofitInstance().callUpdateSession(mContext, mRetrofitResInterface, RetrofitClient.UPDATE_SESSION_RESPONSE);
            } else {
                ((BaseActivity) mContext).showToast(mContext, mContext.getString(R.string.internet_err));
            }

        }

        @Override
        public void retrofitOnSessionError(int code, String message) {
            retrofitOnSessionError(code, message);
        }

        @Override
        public void retrofitOnFailure() {
            ((BaseActivity) mContext).showToast(mContext, mContext.getString(R.string.internet_err));
        }
    };
    private int mAdapterPos;
    private int mMyProfileID = 0;

    public BlockedProfilesAdapter(Context context, ArrayList<ProfileResModel> blockedList, ProfileResModel myProfileResModel) {
        mContext = context;
        mBlockedProfileList = blockedList;
        mInflater = LayoutInflater.from(mContext);
        mMyProfileResModel = myProfileResModel;
        mMyProfileID = mMyProfileResModel.getID();
    }

    public void updateProfile(ProfileResModel myProfileResModel) {
        mMyProfileResModel = myProfileResModel;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = mInflater.inflate(R.layout.adap_followers_followings, parent, false);
        return new Holder(mView);
    }

    public void filterList(ArrayList<ProfileResModel> filteredNames) {
        this.mBlockedProfileList = filteredNames;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int pos) {
        try {
            final Holder mHolder = (Holder) holder;
            ProfileResModel mEntity = mBlockedProfileList.get(pos);
            String imgStr = mEntity.getProfilePicture();
            if (!imgStr.isEmpty()) {
                ((BaseActivity) mContext).setImageWithGlide(mHolder.mUserImg, imgStr, R.drawable.default_profile_icon);
            } else {
                mHolder.mUserImg.setImageResource(R.drawable.default_profile_icon);
            }
            mHolder.mUserNameTxt.setText(Utility.getInstance().getUserName(mEntity));

            if (Utility.getInstance().isAlreadyBlocked(mMyProfileResModel.getBlockedUserProfilesByProfileID(), mEntity.getID())) {
                mHolder.mBlockBtn.setText(mContext.getString(R.string.un_block));
            } else {
                mHolder.mBlockBtn.setText(mContext.getString(R.string.block));
            }
            mHolder.mUserImg.setTag(pos);
            mHolder.mUserImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapterPos = (int) v.getTag();
                    ((BaseActivity) mContext).moveOtherProfileScreenWithResult(mContext, mMyProfileResModel.getID(),
                            mBlockedProfileList.get(mAdapterPos).getID(), AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                }
            });
            mHolder.mBlockBtn.setTag(pos);
            mHolder.mBlockBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapterPos = (int) v.getTag();
                    ProfileResModel mEntity = mBlockedProfileList.get(mAdapterPos);
                    if (Utility.getInstance().isAlreadyBlocked(mMyProfileResModel.getBlockedUserProfilesByProfileID(), mEntity.getID())) {
                        CommonAPI.getInstance().callUnBlockProfile(mContext, mRetrofitResInterface,
                                Utility.getInstance().getUnBlockRowID(mMyProfileResModel.getBlockedUserProfilesByProfileID(),
                                        mMyProfileID, mEntity.getID()));
                    } else {
                        CommonAPI.getInstance().callBlockProfile(mContext, mRetrofitResInterface,
                                mMyProfileID, mEntity.getID());
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setResult() {
        ((BaseActivity) mContext).setResult(RESULT_OK, new Intent().putExtra(AppConstants.IS_FOLLOW_RESULT, true));
    }

    private void updateBlockProfile(BlockedUserResModel mEntity) {
        ArrayList<BlockedUserResModel> mBlockedUsersList = mMyProfileResModel.getBlockedUserProfilesByProfileID();
        mBlockedUsersList.add(mEntity);
        mMyProfileResModel.setBlockedUserProfilesByProfileID(mBlockedUsersList);
        notifyDataSetChanged();
    }

    private void updateUnBlockProfile(BlockedUserResModel mEntity) {
        ArrayList<BlockedUserResModel> mBlockedUsersList = mMyProfileResModel.getBlockedUserProfilesByProfileID();
        for (int i = 0; i < mBlockedUsersList.size(); i++) {
            if (mBlockedUsersList.get(i).getID() == mEntity.getID()) {
                mBlockedUsersList.remove(i);
            }
        }
        mMyProfileResModel.setBlockedUserProfilesByProfileID(mBlockedUsersList);
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return mBlockedProfileList.size();
    }

    @Override
    public int getItemViewType(int pos) {
        return pos;
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_img)
        CircleImageView mUserImg;
        @BindView(R.id.user_name_txt)
        TextView mUserNameTxt;
        @BindView(R.id.follow_btn)
        TextView mBlockBtn;

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
