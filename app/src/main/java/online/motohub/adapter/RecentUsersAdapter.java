package online.motohub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.interfaces.CommonReturnInterface;
import online.motohub.model.ProfileResModel;

public class RecentUsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        View.OnClickListener {

    private ArrayList<ProfileResModel> mRecentUsersList;
    private Context mContext;
    CommonReturnInterface mCommonReturnInterface;
    public RecentUsersAdapter(Context ctx, ArrayList<ProfileResModel> recentUsersList, CommonReturnInterface mCommonReturnInterface) {
        this.mRecentUsersList = recentUsersList;
        this.mContext = ctx;
        this.mCommonReturnInterface=mCommonReturnInterface;
    }

    @Override
    public int getItemCount() {
        return mRecentUsersList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_recent_user, parent, false);
        return new ViewHolderUser(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            final ViewHolderUser mViewHolderUserProfile = (ViewHolderUser) holder;
            mViewHolderUserProfile.mProfileImg.setTag(position);
            mViewHolderUserProfile.mUsername.setTag(position);

            ((BaseActivity) mContext).setImageWithGlide(mViewHolderUserProfile.mProfileImg, mRecentUsersList.get(position).getProfilePicture(), R.drawable.default_profile_icon);
            mViewHolderUserProfile.mUsername.setText(mRecentUsersList.get(position).getUserName());

            mViewHolderUserProfile.mProfileImg.setOnClickListener(this);
            mViewHolderUserProfile.mUsername.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        int selPos=(int)view.getTag();
        switch (view.getId()) {
            case R.id.profile_img:
            case R.id.name_of_driver_tv:
                mCommonReturnInterface.onSuccess(selPos);
                break;

        }
    }

    private class ViewHolderUser extends RecyclerView.ViewHolder {

        private CircleImageView mProfileImg;
        private TextView mUsername;

        ViewHolderUser(View view) {
            super(view);
            mProfileImg = view.findViewById(R.id.profile_img);
            mUsername = view.findViewById(R.id.name_of_driver_tv);
        }

    }

}
