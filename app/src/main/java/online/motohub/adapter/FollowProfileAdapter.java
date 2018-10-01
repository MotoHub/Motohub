package online.motohub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.ViewProfileActivity;
import online.motohub.interfaces.RetrofitResInterface;
import online.motohub.model.FollowProfileEntity;
import online.motohub.model.FollowProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.CommonAPI;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.Utility;

public class FollowProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        View.OnClickListener {

    private ArrayList<ProfileResModel> mUnFollowedProfileListData;
    private ProfileResModel mCurrentProfileObj;
    private Context mContext;
    private int mAdapterPos;
    private int mMyProfileID = 0;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_USER = 1;

    public interface TotalRetrofitResultCount {
        int getTotalUnFollowedResultCount();
    }

    public FollowProfileAdapter(ArrayList<ProfileResModel> unFollowedProfileListData, ProfileResModel currentProfileObj, Context ctx) {
        this.mUnFollowedProfileListData = unFollowedProfileListData;
        this.mCurrentProfileObj = currentProfileObj;
        this.mContext = ctx;
    }

    @Override
    public int getItemCount() {
        return mUnFollowedProfileListData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        if (position >= mUnFollowedProfileListData.size()) {
            return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_USER;
        }

    }

    @Override
    public long getItemId(int position) {
        return (getItemViewType(position) == VIEW_TYPE_USER) ? position : -1;
    }

    private class ViewHolderUser extends RecyclerView.ViewHolder {

        private CircleImageView mProfileImg;
        private TextView mUsername;
        private TextView mFollow;

        ViewHolderUser(View view) {
            super(view);
            mProfileImg = view.findViewById(R.id.profile_img);
            mUsername = view.findViewById(R.id.name_of_driver_tv);
            mFollow = view.findViewById(R.id.follow_tv);
        }

    }

    private class ViewHolderLoader extends RecyclerView.ViewHolder {

        ProgressBar mProgressBar;

        ViewHolderLoader(View view) {
            super(view);
            mProgressBar = view.findViewById(R.id.smallProgressBar);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mView;

        switch (viewType) {

            case VIEW_TYPE_USER:

                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_follow_profile_item, parent, false);
                return new ViewHolderUser(mView);

            case VIEW_TYPE_LOADING:

                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_small_progress_bar, parent, false);
                RecyclerView.LayoutParams mLayoutParams = (RecyclerView.LayoutParams) mView.getLayoutParams();
                mLayoutParams.bottomMargin = 30;
                mLayoutParams.width = RecyclerView.LayoutParams.WRAP_CONTENT;
                mLayoutParams.height = RecyclerView.LayoutParams.MATCH_PARENT;
                mView.setLayoutParams(mLayoutParams);
                return new ViewHolderLoader(mView);

            default:
                return null;

        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_USER:
                final ViewHolderUser mViewHolderUserProfile = (ViewHolderUser) holder;
                mViewHolderUserProfile.mProfileImg.setTag(position);
                mViewHolderUserProfile.mUsername.setTag(position);
                mViewHolderUserProfile.mFollow.setTag(position);

                ((BaseActivity) mContext).setImageWithGlide(mViewHolderUserProfile.mProfileImg, mUnFollowedProfileListData.get(position).getProfilePicture(), R.drawable.default_profile_icon);
                mViewHolderUserProfile.mUsername.setText(Utility.getInstance().getUserName(mUnFollowedProfileListData.get(position)));
                mMyProfileID = mCurrentProfileObj.getID();
                mUnFollowedProfileListData.get(position).setIsFollowing(Utility.getInstance().
                        isAlreadyFollowed(mUnFollowedProfileListData.get(position).getFollowprofile_by_FollowProfileID(), mMyProfileID));
                if (mUnFollowedProfileListData.get(position).getIsFollowing()) {
                    mViewHolderUserProfile.mFollow.setText(mContext.getResources().getString(R.string.UnFollow));
                } else {
                    mViewHolderUserProfile.mFollow.setText(mContext.getResources().getString(R.string.follow));
                }
                mViewHolderUserProfile.mProfileImg.setOnClickListener(this);
                mViewHolderUserProfile.mUsername.setOnClickListener(this);
                mViewHolderUserProfile.mFollow.setOnClickListener(this);
                break;
            case VIEW_TYPE_LOADING:
                final ViewHolderLoader mViewHolderLoader = (ViewHolderLoader) holder;
                if (mUnFollowedProfileListData.size() != ((TotalRetrofitResultCount) mContext).getTotalUnFollowedResultCount()) {
                    mViewHolderLoader.mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mViewHolderLoader.mProgressBar.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_img:
            case R.id.name_of_driver_tv:
                mAdapterPos = (int) view.getTag();
                ((BaseActivity) mContext).moveOtherProfileScreenWithResult(mContext, mCurrentProfileObj.getID(),
                        mUnFollowedProfileListData.get(mAdapterPos).getID(), AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                break;
            case R.id.follow_tv:
                mAdapterPos = (int) view.getTag();
                if (!mUnFollowedProfileListData.get(mAdapterPos).getIsFollowing()) {
                    CommonAPI.getInstance().callFollowProfile(mContext, mRetrofitResInterface,
                            mMyProfileID, mUnFollowedProfileListData.get(mAdapterPos).getID());
                } else {
                    CommonAPI.getInstance().callUnFollowProfile(mContext, mRetrofitResInterface, Utility.getInstance().getUnFollowRowID(
                            mUnFollowedProfileListData.get(mAdapterPos).getFollowprofile_by_FollowProfileID(), mMyProfileID,
                            mUnFollowedProfileListData.get(mAdapterPos).getID()));
                }
                break;
        }
    }

    RetrofitResInterface mRetrofitResInterface = new RetrofitResInterface() {
        @Override
        public void retrofitOnResponse(Object responseObj, int responseType) {

            if (responseObj instanceof FollowProfileModel) {
                ((BaseActivity) mContext).sysOut(responseObj.toString());
                FollowProfileModel mResponse = (FollowProfileModel) responseObj;
                if (mResponse.getResource().size() > 0) {
                    if (responseType == RetrofitClient.FOLLOW_PROFILE_RESPONSE) {
                        updateFollowProfile(mResponse.getResource().get(0));
                    } else {
                        updateUnFollowProfile(mResponse.getResource().get(0));
                    }

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
            ((BaseActivity) mContext).retrofitOnSessionError(code, message);
        }

        @Override
        public void retrofitOnFailure() {
            ((BaseActivity) mContext).showToast(mContext, mContext.getString(R.string.internet_err));
        }
    };

    private void updateFollowProfile(FollowProfileEntity mEntity) {
        ArrayList<FollowProfileEntity> mList = mUnFollowedProfileListData.get(mAdapterPos).getFollowprofile_by_FollowProfileID();
        ArrayList<FollowProfileEntity> mMyFollowingsList = mCurrentProfileObj.getFollowprofile_by_ProfileID();
        mList.add(mEntity);
        mMyFollowingsList.add(mEntity);
        mUnFollowedProfileListData.get(mAdapterPos).setFollowprofile_by_FollowProfileID(mList);
        mCurrentProfileObj.setFollowprofile_by_ProfileID(mMyFollowingsList);
        ((ViewProfileActivity) mContext).refreshFeeds(mCurrentProfileObj);
        notifyDataSetChanged();

    }

    private void updateUnFollowProfile(FollowProfileEntity mEntity) {
        ArrayList<FollowProfileEntity> mList = mUnFollowedProfileListData.get(mAdapterPos).getFollowprofile_by_FollowProfileID();
        ArrayList<FollowProfileEntity> mMyFollowingsList = mCurrentProfileObj.getFollowprofile_by_ProfileID();
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getID() == mEntity.getID()) {
                mList.remove(i);
                break;
            }
        }
        for (int i = 0; i < mMyFollowingsList.size(); i++) {
            if (mMyFollowingsList.get(i).getID() == mEntity.getID()) {
                mMyFollowingsList.remove(i);
                break;
            }
        }
        mUnFollowedProfileListData.get(mAdapterPos).setFollowprofile_by_FollowProfileID(mList);
        mCurrentProfileObj.setFollowprofile_by_ProfileID(mMyFollowingsList);
        ((ViewProfileActivity) mContext).refreshFeeds(mCurrentProfileObj);
        notifyDataSetChanged();
    }

    private String getFollowerFollowing(StringBuilder userFollowerFollowing) {

        String[] mFollowingID = userFollowerFollowing.toString().split(",");
        Set<String> temp = new HashSet<>(Arrays.asList(mFollowingID));
        mFollowingID = temp.toArray(new String[temp.size()]);

        StringBuilder mNewStrBuilder = new StringBuilder("");
        for (String mID : mFollowingID) {
            if (!mID.isEmpty()) {
                mNewStrBuilder.append(mID);
                mNewStrBuilder.append(",");
            }
        }

        if (!mNewStrBuilder.toString().equals("") && mNewStrBuilder.charAt(mNewStrBuilder.length() - 1) == ',') {
            mNewStrBuilder.deleteCharAt(mNewStrBuilder.length() - 1);
        }

        return mNewStrBuilder.toString();

    }


}
