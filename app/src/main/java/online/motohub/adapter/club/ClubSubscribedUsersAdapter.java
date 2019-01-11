package online.motohub.adapter.club;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.FitCenter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.fragment.Performance_Shop.PerfVehiclesFragment;
import online.motohub.fragment.club.ClubSubscribedUsersFragment;
import online.motohub.model.ProfileResModel;
import online.motohub.model.promoter_club_news_media.PromoterSubs;
import online.motohub.util.Utility;

public class ClubSubscribedUsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<PromoterSubs> mClubUsersList;
    private String TAG_Name;

    public ClubSubscribedUsersAdapter(ArrayList<PromoterSubs> mClubUsers, Context context, String TAG) {
        this.mClubUsersList = mClubUsers;
        this.mContext = context;
        this.TAG_Name = TAG;
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.club_sub_users)
        CircleImageView mUserImg;
        @BindView(R.id.club_sub_user_name)
        TextView mUserNameTxt;
        @BindView(R.id.vehicle_model)
        TextView mVehiclename;
        @BindView(R.id.shop_user_name)
        TextView mshopUsername;

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.club_users, parent, false);
        return new Holder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final Holder mHolder = (Holder) holder;

        if(mClubUsersList.get(position).getMprofiles_by_ProfileID()!=null) {
            String imgstr = mClubUsersList.get(position).getMprofiles_by_ProfileID().getProfilePicture();
            ((BaseActivity) mContext).setImageWithGlide(mHolder.mUserImg, imgstr, R.drawable.default_profile_icon);
            if (TAG_Name.equals(ClubSubscribedUsersFragment.class.getName())) {
                mHolder.mUserNameTxt.setText(Utility.getInstance()
                        .getUserName(mClubUsersList.get(position).getMprofiles_by_ProfileID()));
            } else if (TAG_Name.equals(PerfVehiclesFragment.class.getName())) {
                mHolder.mUserNameTxt.setVisibility(View.INVISIBLE);
                mHolder.mVehiclename.setVisibility(View.VISIBLE);
                mHolder.mshopUsername.setVisibility(View.VISIBLE);
                mHolder.mshopUsername.setText(Utility.getInstance()
                        .getUserName(mClubUsersList.get(position).getMprofiles_by_ProfileID()));

                mHolder.mVehiclename.setText(mClubUsersList.get(position).getMprofiles_by_ProfileID().getModel());
            }
        }
    }

    @Override
    public int getItemCount() {
        return mClubUsersList.size();
    }

    @Override
    public int getItemViewType(int pos) {
        return pos;
    }

}