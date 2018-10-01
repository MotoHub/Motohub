package online.motohub.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.track.TrackProfileActivity;
import online.motohub.activity.promoter.PromotersListActivity;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;


public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.Holder> {

    private Context mContext;
    private List<PromotersResModel> mTrackResModelsList;
    private ProfileResModel mMyProfileResModel;

    public TrackAdapter(Context mContext, List<PromotersResModel> mTrackResModelsList, ProfileResModel mProfileResModel) {
        this.mContext = mContext;
        this.mTrackResModelsList = mTrackResModelsList;
        this.mMyProfileResModel = mProfileResModel;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.row_list_view_item, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        PromotersResModel model = mTrackResModelsList.get(position);

        if (model!=null) {
            if(model.getProfileImage()!=null) {
                if(!model.getProfileImage().trim().equals(""))
                ((BaseActivity)mContext).setImageWithGlide(holder.mPromotersImgView,model.getProfileImage().trim(),R.drawable.default_profile_icon);
                else
                    holder.mPromotersImgView.setImageResource(R.drawable.default_profile_icon);
            }
            else {
                holder.mPromotersImgView.setImageResource(R.drawable.default_profile_icon);
            }

        } else {
            holder.mPromotersImgView.setImageResource(R.drawable.default_profile_icon);
        }

        if(model.getName()==null){
            holder.mPromotersName.setVisibility(View.GONE);
        } else {
            holder.mPromotersName.setText(model.getName());
        }

    }

    @Override
    public int getItemCount() {
        return mTrackResModelsList.size();
    }


    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CircleImageView mPromotersImgView;
        private TextView mPromotersName;

        Holder(View view) {
            super(view);
            mPromotersImgView = view.findViewById(R.id.circular_img_view);
            mPromotersName = view.findViewById(R.id.top_tv);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, mTrackResModelsList.get(getLayoutPosition()));
            mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);

            ((BaseActivity)mContext).startActivityForResult(
                    new Intent(mContext, TrackProfileActivity.class).putExtras(mBundle),
                    PromotersListActivity.PROMOTER_FOLLOW_RESPONSE);
        }
    }
}
