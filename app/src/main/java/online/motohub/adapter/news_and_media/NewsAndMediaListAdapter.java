package online.motohub.adapter.news_and_media;

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
import online.motohub.activity.news_and_media.NewsAndMediaProfileActivity;
import online.motohub.activity.performance_shop.PerformanceShopProfileActivity;
import online.motohub.activity.promoter.PromotersListActivity;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class NewsAndMediaListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<PromotersResModel> mNewsAndMediaList;
    private ProfileResModel mMyProfileResModel;
    private int mAdapterPos;
    String utype;

    public NewsAndMediaListAdapter(List<PromotersResModel> newsAndMediaList, Context ctx, ProfileResModel myProfileResModel, String utype) {
        this.mNewsAndMediaList = newsAndMediaList;
        this.mMyProfileResModel = myProfileResModel;
        this.mContext = ctx;
        this.utype = utype;
    }

    public void updatePromoterFollowResponse(PromotersResModel promotersResModel) {
        mNewsAndMediaList.set(mAdapterPos, promotersResModel);
        notifyItemChanged(mAdapterPos);
    }

    private class ViewHolderNewsAndMedia extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircleImageView mNewsAndMediaImgView;
        private TextView mNewsAndMediaName;

        ViewHolderNewsAndMedia(View view) {
            super(view);
            mNewsAndMediaImgView = view.findViewById(R.id.circular_img_view);
            mNewsAndMediaName = view.findViewById(R.id.top_tv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mAdapterPos = getLayoutPosition();
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, mNewsAndMediaList.get(getLayoutPosition()));
            mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
            if(utype=="shop"){
                ((BaseActivity)mContext).startActivityForResult(
                        new Intent(mContext, PerformanceShopProfileActivity.class).putExtras(mBundle),
                        PromotersListActivity.PROMOTER_FOLLOW_RESPONSE);
            }else if(utype=="newsmedia"){
                ((BaseActivity)mContext).startActivityForResult(
                        new Intent(mContext, NewsAndMediaProfileActivity.class).putExtras(mBundle),
                        PromotersListActivity.PROMOTER_FOLLOW_RESPONSE);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_view_item, parent, false);
        return new ViewHolderNewsAndMedia(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolderNewsAndMedia mViewHolderNewsAndMedia = (ViewHolderNewsAndMedia) holder;
        PromotersResModel mPromotersResModel = mNewsAndMediaList.get(position);
        ((BaseActivity) mContext).setImageWithGlide(mViewHolderNewsAndMedia.mNewsAndMediaImgView, mPromotersResModel.getProfileImage(), R.drawable.default_profile_icon);
        mViewHolderNewsAndMedia.mNewsAndMediaName.setText(mPromotersResModel.getName());
    }

    @Override
    public int getItemCount() {
        return mNewsAndMediaList.size();
    }

}
