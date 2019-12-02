package online.motohub.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import online.motohub.R;
import online.motohub.model.ProfileResModel;
import online.motohub.util.Utility;

public class TaggedProfilesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<ProfileResModel> mTaggedProfilesList;
    private TaggedProfilesSizeInterface mTaggedProfilesSizeInterface;
    private Context mContext;

    public TaggedProfilesAdapter(ArrayList<ProfileResModel> taggedProfilesList, Context ctx) {
        this.mTaggedProfilesList = taggedProfilesList;
        this.mTaggedProfilesSizeInterface = (TaggedProfilesSizeInterface) ctx;
        this.mContext = ctx;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tag_item, parent, false);
        return new TaggedProfilesAdapter.viewHolderTaggedProfiles(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {

            final TaggedProfilesAdapter.viewHolderTaggedProfiles mViewHolderTaggedProfiles = (TaggedProfilesAdapter.viewHolderTaggedProfiles) holder;
            mViewHolderTaggedProfiles.mFollowerNameTv.setText
                    (mContext.getString(R.string.tagged_profile_name, Utility.getInstance().getUserName(mTaggedProfilesList.get(position))));

            mViewHolderTaggedProfiles.mCloseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTaggedProfilesList.remove(mViewHolderTaggedProfiles.getLayoutPosition());
                    notifyDataSetChanged();
                    mTaggedProfilesSizeInterface.notifyEmptyTaggedProfilesList(mTaggedProfilesList);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mTaggedProfilesList.size();
    }

    public interface TaggedProfilesSizeInterface {
        void notifyEmptyTaggedProfilesList(ArrayList<ProfileResModel> mTaggedProfilesList);
    }

    private class viewHolderTaggedProfiles extends RecyclerView.ViewHolder {

        private TextView mFollowerNameTv;
        private ImageView mCloseBtn;

        viewHolderTaggedProfiles(View view) {
            super(view);
            mFollowerNameTv = view.findViewById(R.id.name_of_tag);
            mCloseBtn = view.findViewById(R.id.close_btn);
        }

    }

}
