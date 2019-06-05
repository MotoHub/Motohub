package online.motohub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.model.ProfileResModel;
import online.motohub.util.Utility;

public class CreateGrpSelectedProfilesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ProfileResModel> mGrpSelectedProfilesList;
    private Context mContext;

    public CreateGrpSelectedProfilesAdapter(List<ProfileResModel> grpSelectedProfilesList, Context ctx) {
        this.mGrpSelectedProfilesList = grpSelectedProfilesList;
        this.mContext = ctx;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_selected_profile_item, parent, false);
        return new ViewHolderUserProfile(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {

            final ViewHolderUserProfile mViewHolderUserProfile = (ViewHolderUserProfile) holder;

            mViewHolderUserProfile.mProfileImg.setTag(mViewHolderUserProfile.getLayoutPosition());
            mViewHolderUserProfile.mUsername.setTag(mViewHolderUserProfile.getLayoutPosition());

            ((BaseActivity) mContext).setImageWithGlide(mViewHolderUserProfile.mProfileImg, mGrpSelectedProfilesList.get(position).getProfilePicture(), R.drawable.default_profile_icon);


            mViewHolderUserProfile.mUsername.setText(Utility.getInstance().getUserName(mGrpSelectedProfilesList.get(position)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mGrpSelectedProfilesList.size();
    }

    private class ViewHolderUserProfile extends RecyclerView.ViewHolder {

        private CircleImageView mProfileImg;
        private TextView mUsername;

        ViewHolderUserProfile(View view) {
            super(view);
            mProfileImg = view.findViewById(R.id.profile_img);
            mUsername = view.findViewById(R.id.name_of_driver_tv);
        }

    }

}
