package online.motohub.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.ViewProfileActivity;
import online.motohub.model.FollowProfileEntity;
import online.motohub.model.ProfileResModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.util.CommonAPI;
import online.motohub.util.Utility;

public class FollowPhoneEmailProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        View.OnClickListener {

    private static final int VIEW_TYPE_USER = 1;
    private ArrayList<ProfileResModel> mUnFollowedProfileListData;
    private ProfileResModel mCurrentProfileObj;
    private Context mContext;
    private int mAdapterPos;
    private int mMyProfileID = 0;

    public FollowPhoneEmailProfileAdapter(ArrayList<ProfileResModel> unFollowedProfileListData, ProfileResModel currentProfileObj, Context ctx) {
        this.mUnFollowedProfileListData = unFollowedProfileListData;
        this.mCurrentProfileObj = currentProfileObj;
        this.mContext = ctx;
    }

    @Override
    public int getItemCount() {
        return mUnFollowedProfileListData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_follow_profile_item, parent, false);
        return new ViewHolderUser(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolderUser mViewHolderUserProfile = (ViewHolderUser) holder;
        mUnFollowedProfileListData.get(position).setIsFollowing(Utility.getInstance().
                isAlreadyFollowed(mUnFollowedProfileListData.get(position).getFollowprofile_by_FollowProfileID(), mMyProfileID));
        if (mUnFollowedProfileListData.get(position).getIsFollowing()) {
            mViewHolderUserProfile.mFollow.setText(mContext.getResources().getString(R.string.UnFollow));
        } else {
            mViewHolderUserProfile.mFollow.setText(mContext.getResources().getString(R.string.follow));
        }
        mViewHolderUserProfile.mProfileImg.setTag(position);
        mViewHolderUserProfile.mUsername.setTag(position);
        mViewHolderUserProfile.mFollow.setTag(position);

        ((BaseActivity) mContext).setImageWithGlide(mViewHolderUserProfile.mProfileImg, mUnFollowedProfileListData.get(position).getProfilePicture(), R.drawable.default_profile_icon);
        mViewHolderUserProfile.mUsername.setText(Utility.getInstance().getUserName(mUnFollowedProfileListData.get(position)));
        mMyProfileID = mCurrentProfileObj.getID();

        mViewHolderUserProfile.mProfileImg.setOnClickListener(this);
        mViewHolderUserProfile.mUsername.setOnClickListener(this);
        mViewHolderUserProfile.mFollow.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_img:
            case R.id.name_of_driver_tv:
                mAdapterPos = (int) view.getTag();
                String mPos = Integer.valueOf(mAdapterPos).toString();
                if (!((BaseActivity) mContext).isMultiClicked()) {
                    try {
                        ((BaseActivity) mContext).moveOtherProfileScreenWithResult(mContext, mCurrentProfileObj.getID(),
                                mUnFollowedProfileListData.get(mAdapterPos).getID(), AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.follow_tv:
                mAdapterPos = (int) view.getTag();
                if (!mUnFollowedProfileListData.get(mAdapterPos).getIsFollowing()) {
                    CommonAPI.getInstance().callFollowProfile(mContext,
                            mMyProfileID, mUnFollowedProfileListData.get(mAdapterPos).getID());
                } else {
                    CommonAPI.getInstance().callUnFollowProfile(mContext, Utility.getInstance().getUnFollowRowID(
                            mUnFollowedProfileListData.get(mAdapterPos).getFollowprofile_by_FollowProfileID(), mMyProfileID,
                            mUnFollowedProfileListData.get(mAdapterPos).getID()));
                }
                break;
        }
    }

    public void updateFollowProfile(FollowProfileEntity mEntity) {

        int mSelectedPos = -1;

        for (int i = 0; i < mUnFollowedProfileListData.size(); i++) {
            if (mUnFollowedProfileListData.get(i).getID() == mEntity.getFollowProfileID()) {
                mSelectedPos = i;
                break;
            }
        }


        if (mSelectedPos != -1) {
            if (mUnFollowedProfileListData.size() > 0) {
                ArrayList<FollowProfileEntity> mList = mUnFollowedProfileListData.get(mAdapterPos).getFollowprofile_by_FollowProfileID();
                ArrayList<FollowProfileEntity> mMyFollowingsList = mCurrentProfileObj.getFollowprofile_by_ProfileID();
                mList.add(mEntity);
                mMyFollowingsList.add(mEntity);
                mUnFollowedProfileListData.get(mAdapterPos).setFollowprofile_by_FollowProfileID(mList);
                mCurrentProfileObj.setFollowprofile_by_ProfileID(mMyFollowingsList);
                mUnFollowedProfileListData.remove(mAdapterPos);
                ((ViewProfileActivity) mContext).refreshFeeds(mCurrentProfileObj);
                notifyDataSetChanged();
            }
        }

    }

    public void updateUnFollowProfile(FollowProfileEntity mEntity) {
        int mSelectedPos = -1;

        for (int i = 0; i < mUnFollowedProfileListData.size(); i++) {
            if (mUnFollowedProfileListData.get(i).getID() == mEntity.getFollowProfileID()) {
                mSelectedPos = i;
                break;
            }
        }

        if (mSelectedPos != -1) {

            if (mUnFollowedProfileListData.size() > 0) {
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
                mUnFollowedProfileListData.remove(mAdapterPos);
                mCurrentProfileObj.setFollowprofile_by_ProfileID(mMyFollowingsList);
                ((ViewProfileActivity) mContext).refreshFeeds(mCurrentProfileObj);
                notifyDataSetChanged();
            }
        }
        if (mUnFollowedProfileListData.size() == 0) {
            if (mContext instanceof ViewProfileActivity)
                ((ViewProfileActivity) mContext).hidePhoneEmailLayout();
        }
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

}