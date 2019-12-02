package online.motohub.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
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

public class GrpChatMemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ProfileResModel> mGrpChatMemListData;
    private ProfileResModel mMyProfileResModel;

    public GrpChatMemAdapter(List<ProfileResModel> grpChatProfilesList, Context ctx, ProfileResModel myProfileResModel) {
        this.mGrpChatMemListData = grpChatProfilesList;
        this.mContext = ctx;
        this.mMyProfileResModel = myProfileResModel;
    }

    @Override
    public int getItemCount() {
        return mGrpChatMemListData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.row_list_view_item, parent, false);
        return new ViewHolderGrpChatProfiles(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {

            final ViewHolderGrpChatProfiles mViewHolderGrpChatProfiles = (ViewHolderGrpChatProfiles) holder;

            ProfileResModel mProfileResModel = mGrpChatMemListData.get(position);

            String mUsernameStr;
            mUsernameStr = Utility.getInstance().getUserName(mProfileResModel);

            ((BaseActivity) mContext).setImageWithGlide(mViewHolderGrpChatProfiles.mUserImgView, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);

            mViewHolderGrpChatProfiles.mUsernameTv.setText(mUsernameStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class ViewHolderGrpChatProfiles extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircleImageView mUserImgView;
        private TextView mUsernameTv;

        ViewHolderGrpChatProfiles(View view) {
            super(view);
            mUserImgView = view.findViewById(R.id.circular_img_view);
            mUsernameTv = view.findViewById(R.id.top_tv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mGrpChatMemListData.get(getLayoutPosition()).getID() == mMyProfileResModel.getID()) {
                ((BaseActivity) mContext).moveMyProfileScreen(mContext, 0);
            } else {
                ((BaseActivity) mContext).moveOtherProfileScreen(mContext, mMyProfileResModel.getID(),
                        mGrpChatMemListData.get(getLayoutPosition()).getID());
            }
        }

    }

}
