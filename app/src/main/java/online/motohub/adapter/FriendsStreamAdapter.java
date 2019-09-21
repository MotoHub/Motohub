package online.motohub.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.ViewLiveVideoViewScreen2;
import online.motohub.application.MotoHub;
import online.motohub.model.LiveStreamEntity;
import online.motohub.model.ProfileResModel;
import online.motohub.newdesign.constants.AppConstants;


public class FriendsStreamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private Context mContext;
    private ArrayList<LiveStreamEntity> mStreamUserList;
    private int selPos;
    private LayoutInflater mInflater;
    private int mCurrentProfileID;


    public FriendsStreamAdapter(Context context, int currentProfileID, ArrayList<LiveStreamEntity> streamUserList) {
        mContext = context;
        mStreamUserList = streamUserList;
        mInflater = LayoutInflater.from(mContext);
        mCurrentProfileID = currentProfileID;
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
                    LiveStreamEntity mEntity = mStreamUserList.get(pos);
                    String imgStr = mEntity.getProfiles_by_StreamProfileID().getProfilePicture();
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
                            Bundle mBundle = new Bundle();
                            ProfileResModel mOthersProfileModel = new ProfileResModel();
                            mOthersProfileModel.setID(mStreamUserList.get(selPos).getStreamProfileID());
                            //mBundle.putSerializable(ProfileModel.OTHERS_PROFILE_RES_MODEL, mOthersProfileModel);
                            MotoHub.getApplicationInstance().setmOthersProfileResModel(mOthersProfileModel);
                            ProfileResModel mMyProfileModel = new ProfileResModel();
                            mMyProfileModel.setID(mCurrentProfileID);
                            //mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileModel);
                            //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileModel);
                            EventBus.getDefault().postSticky(mMyProfileModel);
                        }
                    });
                    mHolder.mViewStreamBtn.setTag(pos);
                    mHolder.mViewStreamBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selPos = (int) v.getTag();
                            int receiverProfileID = mStreamUserList.get(selPos).getStreamProfileID();
                            Intent mGoWatchActivity = new Intent(mContext, ViewLiveVideoViewScreen2.class);
                            mGoWatchActivity.putExtra(AppConstants.PROFILE_ID, receiverProfileID);
                            mContext.startActivity(mGoWatchActivity);

                        }
                    });
                    if (mEntity.getProfiles_by_StreamProfileID().getProfileType() == 5) {
                        mHolder.mUserNameTxt.setText(mEntity.getProfiles_by_StreamProfileID().getSpectatorName());
                    } else {
                        mHolder.mUserNameTxt.setText(mEntity.getProfiles_by_StreamProfileID().getDriver());
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

    public void filterList(ArrayList<LiveStreamEntity> filteredNames) {
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
