package online.motohub.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.model.ProfileResModel;
import online.motohub.util.Utility;

public class CreateGrpSelectProfilesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final CreateGrpSelectProfilesInterface mCreateGrpSelectProfilesInterface;
    private Context mContext;
    private List<ProfileResModel> mGrpSelectProfilesList;

    public CreateGrpSelectProfilesAdapter(List<ProfileResModel> grpSelectProfilesList, Context ctx) {
        this.mCreateGrpSelectProfilesInterface = (CreateGrpSelectProfilesInterface) ctx;
        this.mGrpSelectProfilesList = grpSelectProfilesList;
        this.mContext = ctx;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_view_item, parent, false);
        return new ViewHolderGrpSelectProfiles(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {

            final ViewHolderGrpSelectProfiles mViewHolderGrpSelectProfiles = (ViewHolderGrpSelectProfiles) holder;

            ProfileResModel mProfileResModel = mGrpSelectProfilesList.get(position);

            String mUsernameStr = Utility.getInstance().getUserName(mProfileResModel);

            ((BaseActivity) mContext).setImageWithGlide(mViewHolderGrpSelectProfiles.mUserImgView, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);

            mViewHolderGrpSelectProfiles.mUsernameTv.setText(mUsernameStr);

            if (mProfileResModel.getIsSelected()) {
                mViewHolderGrpSelectProfiles.mRelativeLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGrey));
            } else {
                mViewHolderGrpSelectProfiles.mRelativeLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            }

            mViewHolderGrpSelectProfiles.mRightArrow.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mGrpSelectProfilesList.size();
    }

    public interface CreateGrpSelectProfilesInterface {
        void grpSelectProfiles(int adapterPosition);
    }

    private class ViewHolderGrpSelectProfiles extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircleImageView mUserImgView;
        private TextView mUsernameTv;
        private RelativeLayout mRelativeLayout;
        private ImageView mRightArrow;

        ViewHolderGrpSelectProfiles(View view) {
            super(view);
            mRelativeLayout = view.findViewById(R.id.list_item);
            mUserImgView = view.findViewById(R.id.circular_img_view);
            mUsernameTv = view.findViewById(R.id.top_tv);
            mRightArrow = view.findViewById(R.id.right_arrow);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mCreateGrpSelectProfilesInterface.grpSelectProfiles(getLayoutPosition());
        }

    }

}
