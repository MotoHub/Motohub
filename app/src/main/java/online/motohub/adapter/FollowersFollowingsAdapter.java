package online.motohub.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.dialog.DialogManager;
import online.motohub.interfaces.CommonReturnInterface;
import online.motohub.interfaces.RetrofitResInterface;
import online.motohub.model.BlockedUserModel;
import online.motohub.model.FollowProfileEntity;
import online.motohub.model.FollowProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.CommonAPI;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.Utility;

import static android.app.Activity.RESULT_OK;


public class FollowersFollowingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<ProfileResModel> mFollowersFollowingsList;
    private LayoutInflater mInflater;
    private ProfileResModel mMyProfileResModel;

    private int mAdapterPos;
    private int mMyProfileID = 0;
    private boolean isFollowers;
    private boolean isBlockAPI;
    RetrofitResInterface mRetrofitResInterface = new RetrofitResInterface() {
        @Override
        public void retrofitOnResponse(Object responseObj, int responseType) {
            ((BaseActivity) mContext).sysOut(responseObj.toString());
            if (responseObj instanceof FollowProfileModel) {
                FollowProfileModel mResponse = (FollowProfileModel) responseObj;
                if (mResponse.getResource().size() > 0) {
                    switch (responseType) {
                        case RetrofitClient.FOLLOW_PROFILE_RESPONSE:
                            updateFollowProfile(mResponse.getResource().get(0));
                            break;
                        case RetrofitClient.UN_FOLLOW_PROFILE_RESPONSE:
                            updateUnFollowProfile(mResponse.getResource().get(0));
                            break;
                        case RetrofitClient.UN_FOLLOW_MY_PROFILE_RESPONSE:
                            updateUnFollowMyProfile(mResponse.getResource().get(0));
                            break;
                        case RetrofitClient.UN_FOLLOW_BOTH_PROFILE_RESPONSE:
                            if (mResponse.getResource().size() > 1) {
                                FollowProfileEntity mMyFollowingEntity, mMyFollowerEntity;
                                if (mResponse.getResource().get(0).getProfileID() == mMyProfileID) {
                                    mMyFollowingEntity = mResponse.getResource().get(0);
                                    mMyFollowerEntity = mResponse.getResource().get(1);
                                } else {
                                    mMyFollowerEntity = mResponse.getResource().get(0);
                                    mMyFollowingEntity = mResponse.getResource().get(1);
                                }
                                updateUnFollowBothProfile(mMyFollowingEntity, mMyFollowerEntity);
                            } else {
                                updateUnFollowMyProfile(mResponse.getResource().get(0));
                            }
                            break;
                        default:
                            break;
                    }

                    setResult();
                } else {
                    ((BaseActivity) mContext).showToast(mContext, mContext.getString(R.string.something_wrong));
                }
            } else if (responseObj instanceof BlockedUserModel) {
                BlockedUserModel mResponse = (BlockedUserModel) responseObj;
                if (mResponse.getResource().size() > 0) {
                    switch (responseType) {
                        case RetrofitClient.CALL_BLOCK_USER_PROFILE:
                            updateBlockProfile();
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
            if (message.equals("Unauthorized") || code == 401) {
                RetrofitClient.getRetrofitInstance().callUpdateSession(mContext, mRetrofitResInterface, RetrofitClient.UPDATE_SESSION_RESPONSE);
            } else {
                ((BaseActivity) mContext).showToast(mContext, mContext.getString(R.string.internet_err));
            }
        }

        @Override
        public void retrofitOnFailure() {
            ((BaseActivity) mContext).showToast(mContext, mContext.getString(R.string.internet_err));
        }
    };
    CommonReturnInterface mUnFollowBlockCallback = new CommonReturnInterface() {
        @Override
        public void onSuccess(int type) {
            ProfileResModel mEntity = mFollowersFollowingsList.get(mAdapterPos);
            if (type == 1) {
                CommonAPI.getInstance().callUnFollowProfile(mContext, mRetrofitResInterface, Utility.getInstance().getUnFollowRowID(
                        mEntity.getFollowprofile_by_FollowProfileID(), mMyProfileID, mEntity.getID()));
            } else {
                isBlockAPI = true;
                int mOtherProfileID = mEntity.getID();
                int rowID = Utility.getInstance().getUnFollowRowID(mEntity.getFollowprofile_by_FollowProfileID(),
                        mMyProfileID, mOtherProfileID);
                boolean isFollowed = Utility.getInstance().isAlreadyFollowed(mEntity.getFollowprofile_by_FollowProfileID(), mMyProfileID);
                boolean isFollowedMe = Utility.getInstance().isAlreadyFollowedMe(mMyProfileResModel.getFollowprofile_by_FollowProfileID(), mOtherProfileID);
                if (isFollowed && isFollowedMe) {
                    CommonAPI.getInstance().callUnFollowBothProfile(mContext, mRetrofitResInterface, rowID, mMyProfileID, mOtherProfileID);
                } else if (isFollowed) {
                    CommonAPI.getInstance().callUnFollowProfile(mContext, mRetrofitResInterface,
                            Utility.getInstance().getUnFollowRowID(mEntity.getFollowprofile_by_FollowProfileID(),
                                    mMyProfileID, mOtherProfileID));
                } else if (isFollowedMe) {
                    CommonAPI.getInstance().callUnFollowMyProfile(mContext, mRetrofitResInterface, mMyProfileID, mOtherProfileID);
                } else {
                    CommonAPI.getInstance().callBlockProfile(mContext, mRetrofitResInterface,
                            mMyProfileID, mOtherProfileID);
                }
            }
        }
    };

    public FollowersFollowingsAdapter(Context context, ArrayList<ProfileResModel> followersFollowingsList, boolean isfollowers, ProfileResModel myProfileResModel) {
        mContext = context;
        mFollowersFollowingsList = followersFollowingsList;
        isFollowers = isfollowers;
        mInflater = LayoutInflater.from(mContext);
        mMyProfileResModel = myProfileResModel;
        mMyProfileID = mMyProfileResModel.getID();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = mInflater.inflate(R.layout.adap_followers_followings, parent, false);
        return new Holder(mView);
    }

    public void filterList(ArrayList<ProfileResModel> filteredNames) {
        this.mFollowersFollowingsList = filteredNames;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int pos) {
        try {
            final Holder mHolder = (Holder) holder;
            ProfileResModel mEntity = mFollowersFollowingsList.get(pos);

            String imgStr = mEntity.getProfilePicture();
            if (!imgStr.isEmpty()) {
                ((BaseActivity) mContext).setImageWithGlide(mHolder.mUserImg, imgStr, R.drawable.default_profile_icon);
            } else {
                mHolder.mUserImg.setImageResource(R.drawable.default_profile_icon);
            }
            mHolder.mUserNameTxt.setText(Utility.getInstance().getUserName(mEntity));

            mFollowersFollowingsList.get(pos).setIsFollowing(Utility.getInstance().
                    isAlreadyFollowed(mFollowersFollowingsList.get(pos).getFollowprofile_by_FollowProfileID(), mMyProfileID));

            if (mFollowersFollowingsList.get(pos).getIsFollowing()) {
                mHolder.mFollowBtn.setText(mContext.getString(R.string.un_follow));
            } else {
                mHolder.mFollowBtn.setText(mContext.getString(R.string.follow));
            }
            mHolder.mUserImg.setTag(pos);
            mHolder.mUserImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapterPos = (int) v.getTag();
                    ((BaseActivity) mContext).moveOtherProfileScreenWithResult(mContext, mMyProfileResModel.getID(),
                            mFollowersFollowingsList.get(mAdapterPos).getID(), AppConstants.FOLLOWERS_FOLLOWING_RESULT);

                }
            });
            mHolder.mFollowBtn.setTag(pos);
            mHolder.mFollowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapterPos = (int) v.getTag();
                    ProfileResModel mEntity = mFollowersFollowingsList.get(mAdapterPos);
                    if (mFollowersFollowingsList.get(mAdapterPos).getIsFollowing()) {
                        //TODO show alert then call Unfollow API
                        DialogManager.showUnFollowBlockDialogWithCallback(mContext, mUnFollowBlockCallback, mEntity);

                    } else {
                        //TODO call Follow API
                        CommonAPI.getInstance().callFollowProfile(mContext, mRetrofitResInterface,
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

    private void updateBlockProfile() {
        mFollowersFollowingsList.remove(mAdapterPos);
        notifyDataSetChanged();
    }

    private void updateFollowProfile(FollowProfileEntity mEntity) {
        ArrayList<FollowProfileEntity> mList = mFollowersFollowingsList.get(mAdapterPos).getFollowprofile_by_FollowProfileID();
        mList.add(mEntity);
        mFollowersFollowingsList.get(mAdapterPos).setFollowprofile_by_FollowProfileID(mList);
        notifyDataSetChanged();
    }

    private void updateUnFollowProfile(FollowProfileEntity mEntity) {
        ArrayList<FollowProfileEntity> mList = mFollowersFollowingsList.get(mAdapterPos).getFollowprofile_by_FollowProfileID();
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getID() == mEntity.getID()) {
                mList.remove(i);
                break;
            }
        }
        mFollowersFollowingsList.get(mAdapterPos).setFollowprofile_by_FollowProfileID(mList);
        notifyDataSetChanged();
        if (isBlockAPI) {
            isBlockAPI = false;
            CommonAPI.getInstance().callBlockProfile(mContext, mRetrofitResInterface,
                    mMyProfileID, mEntity.getFollowProfileID());
        }
    }

    private void updateUnFollowMyProfile(FollowProfileEntity mEntity) {
        ArrayList<FollowProfileEntity> mOtherFollowingsList = mFollowersFollowingsList.get(mAdapterPos).getFollowprofile_by_ProfileID();
        ArrayList<FollowProfileEntity> mMyFollowersList = mMyProfileResModel.getFollowprofile_by_FollowProfileID();
        for (int i = 0; i < mOtherFollowingsList.size(); i++) {
            if (mOtherFollowingsList.get(i).getID() == mEntity.getID()) {
                mOtherFollowingsList.remove(i);
                break;
            }
        }
        for (int i = 0; i < mMyFollowersList.size(); i++) {
            if (mMyFollowersList.get(i).getID() == mEntity.getID()) {
                mMyFollowersList.remove(i);
                break;
            }
        }
        mFollowersFollowingsList.get(mAdapterPos).setFollowprofile_by_ProfileID(mOtherFollowingsList);
        mMyProfileResModel.setFollowprofile_by_FollowProfileID(mMyFollowersList);
        notifyDataSetChanged();
        if (isBlockAPI) {
            isBlockAPI = false;
            CommonAPI.getInstance().callBlockProfile(mContext, mRetrofitResInterface,
                    mMyProfileID, mEntity.getProfileID());
        }
    }

    private void updateUnFollowBothProfile(FollowProfileEntity mMyFollowingEntity, FollowProfileEntity mMyFollowerEntity) {
        ArrayList<FollowProfileEntity> mOtherFollowersList = mFollowersFollowingsList.get(mAdapterPos).getFollowprofile_by_FollowProfileID();
        ArrayList<FollowProfileEntity> mMyFollowingsList = mMyProfileResModel.getFollowprofile_by_ProfileID();
        for (int i = 0; i < mOtherFollowersList.size(); i++) {
            if (mOtherFollowersList.get(i).getID() == mMyFollowingEntity.getID()) {
                mOtherFollowersList.remove(i);
                break;
            }
        }
        for (int i = 0; i < mMyFollowingsList.size(); i++) {
            if (mMyFollowingsList.get(i).getID() == mMyFollowingEntity.getID()) {
                mMyFollowingsList.remove(i);
                break;
            }
        }
        mFollowersFollowingsList.get(mAdapterPos).setFollowprofile_by_FollowProfileID(mOtherFollowersList);
        mMyProfileResModel.setFollowprofile_by_ProfileID(mMyFollowingsList);

        updateUnFollowMyProfile(mMyFollowerEntity);
    }

    @Override
    public int getItemCount() {
        return mFollowersFollowingsList.size();
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
        TextView mFollowBtn;

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