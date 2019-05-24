package online.motohub.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class TagSponsorsHorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_NO_SPONSOR_FOUND = 2;
    private final TagSponsorsInterface mTagSponsorsInterface;
    private List<PromotersResModel> mTagSponsorsList;
    private Context mContext;
    private boolean mShowProgressBar = false;
    private boolean mShowNoSponsorsFoundText = false;

    public TagSponsorsHorAdapter(@NonNull AppDialogFragment appDialogFragment, @NonNull List<PromotersResModel> tagSponsorsList) {
        this.mTagSponsorsInterface = appDialogFragment;
        this.mContext = appDialogFragment.getContext();
        this.mTagSponsorsList = tagSponsorsList;
    }

    public void setShowProgressBar(boolean showProgressBar) {
        this.mShowProgressBar = showProgressBar;
    }

    public void setShowNoSponsorsFoundText(boolean showNoSponsorsFoundText) {
        this.mShowNoSponsorsFoundText = showNoSponsorsFoundText;
    }

    @Override
    public int getItemCount() {
        return mTagSponsorsList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        if (mShowNoSponsorsFoundText) {
            return VIEW_TYPE_NO_SPONSOR_FOUND;
        } else if (position >= mTagSponsorsList.size()) {
            return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_USER;
        }

    }

    @Override
    public long getItemId(int position) {
        return (getItemViewType(position) == VIEW_TYPE_USER) ? position : -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mView;

        switch (viewType) {

            case VIEW_TYPE_USER:

                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_sponsor, parent, false);
                return new ViewHolderTagSponsors(mView);

            case VIEW_TYPE_LOADING:

                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_small_progress_bar, parent, false);
                RecyclerView.LayoutParams mLayoutParams = (RecyclerView.LayoutParams) mView.getLayoutParams();
                mLayoutParams.bottomMargin = 30;
                mLayoutParams.width = RecyclerView.LayoutParams.WRAP_CONTENT;
                mLayoutParams.height = RecyclerView.LayoutParams.MATCH_PARENT;
                mView.setLayoutParams(mLayoutParams);
                return new ViewHolderLoader(mView);

            case VIEW_TYPE_NO_SPONSOR_FOUND:

                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_text_view, parent, false);
                return new ViewHolderNoSponsorFoundText(mView);

            default:
                return null;

        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {

            case VIEW_TYPE_USER:
                try {

                    final ViewHolderTagSponsors mViewHolderTagSponsors = (ViewHolderTagSponsors) holder;

                    PromotersResModel mPromotersResModel = mTagSponsorsList.get(position);

                    ((BaseActivity) mContext).setImageWithGlide(mViewHolderTagSponsors.mUserImgView, mPromotersResModel.getProfileImage(), R.drawable.default_profile_icon);

                    mViewHolderTagSponsors.mPromoterNameTv.setText(mPromotersResModel.getName());

                    if (mPromotersResModel.getIsSelected()) {
                        mViewHolderTagSponsors.mAddOrRemoveImgBtn.setImageResource(R.drawable.cancel_circle_icon);
                        mViewHolderTagSponsors.mRelativeLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorLighterGrey));
                    } else {
                        mViewHolderTagSponsors.mAddOrRemoveImgBtn.setImageResource(R.drawable.add_circle_icon);
                        mViewHolderTagSponsors.mRelativeLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case VIEW_TYPE_LOADING:

                final ViewHolderLoader mViewHolderLoader = (ViewHolderLoader) holder;

                if (mShowProgressBar) {
                    mViewHolderLoader.mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mViewHolderLoader.mProgressBar.setVisibility(View.GONE);
                }

                break;

            case VIEW_TYPE_NO_SPONSOR_FOUND:

                final ViewHolderNoSponsorFoundText mViewHolderNoSponsorFoundText = (ViewHolderNoSponsorFoundText) holder;
                mViewHolderNoSponsorFoundText.mTextView.setText(mContext.getString(R.string.no_sponsor_found_err));

                break;

            default:
                break;

        }

    }

    public interface TagSponsorsInterface {
        void addSelectedSponsorsToTaggedList(int adapterPosition);

        void removeCancelledSponsorsFromTaggedList(int adapterPosition);
    }

    private class ViewHolderTagSponsors extends RecyclerView.ViewHolder implements View.OnClickListener {

        RelativeLayout mRelativeLayout;
        CircleImageView mUserImgView;
        TextView mPromoterNameTv;
        ImageButton mAddOrRemoveImgBtn;

        ViewHolderTagSponsors(View view) {

            super(view);

            mRelativeLayout = view.findViewById(R.id.rowAddSponsorLayout);
            mUserImgView = view.findViewById(R.id.profile_img);
            mPromoterNameTv = view.findViewById(R.id.promoter_name_tv);
            mAddOrRemoveImgBtn = view.findViewById(R.id.add_remove_img_btn);

            mRelativeLayout.setOnClickListener(this);
            mUserImgView.setOnClickListener(this);
            mPromoterNameTv.setOnClickListener(this);
            mAddOrRemoveImgBtn.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            if (mTagSponsorsList.get(getLayoutPosition()).getIsSelected()) {
                mTagSponsorsList.get(getLayoutPosition()).setIsSelected(false);
                mTagSponsorsInterface.removeCancelledSponsorsFromTaggedList(getLayoutPosition());
                notifyDataSetChanged();
            } else {
                mTagSponsorsList.get(getLayoutPosition()).setIsSelected(true);
                mTagSponsorsInterface.addSelectedSponsorsToTaggedList(getLayoutPosition());
                notifyDataSetChanged();
            }

        }

    }

    private class ViewHolderLoader extends RecyclerView.ViewHolder {

        ProgressBar mProgressBar;

        ViewHolderLoader(View view) {
            super(view);
            mProgressBar = view.findViewById(R.id.smallProgressBar);
        }

    }

    private class ViewHolderNoSponsorFoundText extends RecyclerView.ViewHolder {

        TextView mTextView;

        ViewHolderNoSponsorFoundText(View view) {
            super(view);
            mTextView = view.findViewById(R.id.textViewErr);
        }

    }

}
