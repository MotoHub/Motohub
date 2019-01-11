package online.motohub.adapter.promoter;

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
import online.motohub.activity.promoter.PromoterProfileActivity;
import online.motohub.activity.promoter.PromotersListActivity;
import online.motohub.application.MotoHub;
import online.motohub.model.ProfileResModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class PromotersListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<PromotersResModel> mPromotersList;
    private ProfileResModel mMyProfileResModel;
    private int mAdapterPos;

    public PromotersListAdapter(List<PromotersResModel> promotersList, Context ctx, ProfileResModel myProfileResModel) {
        this.mPromotersList = promotersList;
        this.mMyProfileResModel = myProfileResModel;
        this.mContext = ctx;
    }

    public void updatePromoterFollowResponse(PromotersResModel promotersResModel) {
        mPromotersList.set(mAdapterPos, promotersResModel);
        notifyItemChanged(mAdapterPos);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_view_item, parent, false);
        return new ViewHolderPromoters(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewHolderPromoters mViewHolderPromoters = (ViewHolderPromoters) holder;
        try {

            PromotersResModel mPromotersResModel = mPromotersList.get(position);

            ((BaseActivity) mContext).setImageWithGlide(mViewHolderPromoters.mPromotersImgView, mPromotersResModel.getProfileImage(), R.drawable.default_profile_icon);

            mViewHolderPromoters.mPromotersName.setText(mPromotersResModel.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mPromotersList.size();
    }

    private class ViewHolderPromoters extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircleImageView mPromotersImgView;
        private TextView mPromotersName;

        ViewHolderPromoters(View view) {
            super(view);
            mPromotersImgView = view.findViewById(R.id.circular_img_view);
            mPromotersName = view.findViewById(R.id.top_tv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mAdapterPos = getLayoutPosition();
            /*Bundle mBundle = new Bundle();
            mBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, mPromotersList.get(getLayoutPosition()));
            mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
            ((BaseActivity)mContext).startActivityForResult(
                    new Intent(mContext, PromoterProfileActivity.class).putExtras(mBundle),
                    PromotersListActivity.PROMOTER_FOLLOW_RESPONSE);*/
            /*MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
            MotoHub.getApplicationInstance().setmPromoterResModel(mPromotersList.get(getLayoutPosition()));*/
            EventBus.getDefault().postSticky(mMyProfileResModel);
            EventBus.getDefault().postSticky(mPromotersList.get(getLayoutPosition()));
            ((BaseActivity) mContext).startActivityForResult(
                    new Intent(mContext, PromoterProfileActivity.class),
                    PromotersListActivity.PROMOTER_FOLLOW_RESPONSE);
        }

    }

}
