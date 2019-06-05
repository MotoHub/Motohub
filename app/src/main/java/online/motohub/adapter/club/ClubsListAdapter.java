package online.motohub.adapter.club;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.club.ClubProfileActivity;
import online.motohub.activity.promoter.PromotersListActivity;
import online.motohub.model.ProfileResModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class ClubsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<PromotersResModel> mClubsList;
    private ProfileResModel mMyProfileResModel;
    private int mAdapterPos;

    public ClubsListAdapter(List<PromotersResModel> clubsList, Context ctx, ProfileResModel myProfileResModel) {
        this.mClubsList = clubsList;
        this.mMyProfileResModel = myProfileResModel;
        this.mContext = ctx;
    }

    public void updatePromoterFollowResponse(PromotersResModel promotersResModel) {
        mClubsList.set(mAdapterPos, promotersResModel);
        notifyItemChanged(mAdapterPos);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_view_item, parent, false);
        return new ViewHolderClubs(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewHolderClubs mViewHolderClubsPromoters = (ViewHolderClubs) holder;
        try {

            PromotersResModel mPromotersResModel = mClubsList.get(position);

            ((BaseActivity) mContext).setImageWithGlide(mViewHolderClubsPromoters.mClubsImgView, mPromotersResModel.getProfileImage(), R.drawable.default_profile_icon);

            mViewHolderClubsPromoters.mClubsName.setText(mPromotersResModel.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mClubsList.size();
    }

    private class ViewHolderClubs extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircleImageView mClubsImgView;
        private TextView mClubsName;

        ViewHolderClubs(View view) {
            super(view);
            mClubsImgView = view.findViewById(R.id.circular_img_view);
            mClubsName = view.findViewById(R.id.top_tv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mAdapterPos = getLayoutPosition();
            /*Bundle mBundle = new Bundle();
            mBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, mClubsList.get(getLayoutPosition()));
            mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);

            ((BaseActivity)mContext).startActivityForResult(
                    new Intent(mContext, ClubProfileActivity.class).putExtras(mBundle),
                    PromotersListActivity.PROMOTER_FOLLOW_RESPONSE);*/
           /* MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
            MotoHub.getApplicationInstance().setmPromoterResModel(mClubsList.get(getLayoutPosition()));*/
            EventBus.getDefault().postSticky(mMyProfileResModel);
            EventBus.getDefault().postSticky(mClubsList.get(getLayoutPosition()));
            ((BaseActivity) mContext).startActivityForResult(
                    new Intent(mContext, ClubProfileActivity.class),
                    PromotersListActivity.PROMOTER_FOLLOW_RESPONSE);
        }

    }

}
