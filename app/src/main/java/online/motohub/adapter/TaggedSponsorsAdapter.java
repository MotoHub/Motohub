package online.motohub.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import online.motohub.R;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class TaggedSponsorsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final TaggedSponsorsInterface mTaggedSponsorsInterface;

    private final List<PromotersResModel> mTaggedSponsorsList;

    public TaggedSponsorsAdapter(AppDialogFragment appDialogFragment, List<PromotersResModel> taggedSponsorsList) {
        this.mTaggedSponsorsInterface = appDialogFragment;
        this.mTaggedSponsorsList = taggedSponsorsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tag_item, parent, false);
        return new TaggedSponsorsAdapter.viewHolderTaggedSponsors(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {

            final TaggedSponsorsAdapter.viewHolderTaggedSponsors mViewHolderTaggedSponsors = (TaggedSponsorsAdapter.viewHolderTaggedSponsors) holder;

            mViewHolderTaggedSponsors.mFollowerNameTv.setText(mTaggedSponsorsList.get(position).getName());
            mViewHolderTaggedSponsors.mCloseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTaggedSponsorsInterface.deselectTagSponsorItemFromTagSponsorsList(mViewHolderTaggedSponsors.getLayoutPosition());
                    mTaggedSponsorsList.remove(mViewHolderTaggedSponsors.getLayoutPosition());
                    notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mTaggedSponsorsList.size();
    }

    public interface TaggedSponsorsInterface {
        void deselectTagSponsorItemFromTagSponsorsList(int adapterPosition);
    }

    private class viewHolderTaggedSponsors extends RecyclerView.ViewHolder {

        private TextView mFollowerNameTv;
        private ImageView mCloseBtn;

        viewHolderTaggedSponsors(View view) {
            super(view);
            mFollowerNameTv = view.findViewById(R.id.name_of_tag);
            mCloseBtn = view.findViewById(R.id.close_btn);
        }

    }


}
