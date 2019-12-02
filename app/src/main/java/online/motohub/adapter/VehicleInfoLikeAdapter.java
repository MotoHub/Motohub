package online.motohub.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.model.ProfileResModel;
import online.motohub.model.VehicleInfoLikeModel;
import online.motohub.util.Utility;


public class VehicleInfoLikeAdapter extends RecyclerView.Adapter<VehicleInfoLikeAdapter.Holder> {

    private Context mContext;
    private List<VehicleInfoLikeModel> mVehicleInfoLikesList;
    private ProfileResModel mMyProfileResModel;

    public VehicleInfoLikeAdapter(Context mContext, ArrayList<VehicleInfoLikeModel> mVehicleInfoLikeList, ProfileResModel mMyProfileResModel) {

        this.mContext = mContext;
        this.mVehicleInfoLikesList = mVehicleInfoLikeList;
        this.mMyProfileResModel = mMyProfileResModel;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_feed_likes, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder mHolder, final int pos) {
        try {
            VehicleInfoLikeModel mEntity = mVehicleInfoLikesList.get(pos);
            String imgStr = mEntity.getProfilesByWhoLikedProfileID().getProfilePicture();
            if (!imgStr.isEmpty()) {
                ((BaseActivity) mContext).setImageWithGlide(mHolder.mUserImg, imgStr, R.drawable.default_profile_icon);
            } else {
                mHolder.mUserImg.setImageResource(R.drawable.default_profile_icon);
            }
            mHolder.mCommentImgLay.setTag(pos);
            mHolder.mCommentImgLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selPos = (int) v.getTag();
                    //TODO View Profile Screen
                    profileClick(mVehicleInfoLikesList.get(pos));
                }
            });
            mHolder.mUserNameTxt.setText(Utility.getInstance().getUserName(mEntity.getProfilesByWhoLikedProfileID()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mVehicleInfoLikesList.size();
    }

    private void profileClick(VehicleInfoLikeModel vehicleInfoLikeModel) {

        if (mMyProfileResModel.getID() == vehicleInfoLikeModel.getWhoLikedProfileID()) {
            ((BaseActivity) mContext).moveMyProfileScreen(mContext, 0);
        } else {
            ((BaseActivity) mContext).moveOtherProfileScreen(mContext, mMyProfileResModel.getID(),
                    vehicleInfoLikeModel.getProfilesByWhoLikedProfileID().getID());
        }
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.comment_user_img)
        CircleImageView mUserImg;

        @BindView(R.id.comment_user_img_lay)
        RelativeLayout mCommentImgLay;

        @BindView(R.id.comment_user_name_txt)
        TextView mUserNameTxt;

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

}
