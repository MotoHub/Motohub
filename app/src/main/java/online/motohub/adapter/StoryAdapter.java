package online.motohub.adapter;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.StorySettingActivity;
import online.motohub.model.ProfileResModel;
import online.motohub.util.Utility;

/**
 * Created by Pickzy-05 on 3/8/2018.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryHolder> {

    private ArrayList<ProfileResModel> mProfileList;
    private Context mContext;
    private ArrayList<String> mSelectedIds;

    public StoryAdapter(Context context, ArrayList<ProfileResModel> profileList, ArrayList<String> selectedIds) {
        mContext = context;
        mProfileList = profileList;
        mSelectedIds = selectedIds;
    }

    @Override
    public StoryAdapter.StoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StoryAdapter.StoryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_story, null));
    }

    @Override
    public void onBindViewHolder(StoryAdapter.StoryHolder holder, int position) {
        try {
            ProfileResModel mModel = mProfileList.get(position);
            ((BaseActivity) mContext).setImageWithGlide(holder.mUserImage, mModel.getProfilePicture(), R.drawable.default_profile_icon);
            holder.mUserName.setText(Utility.getInstance().getUserName(mModel));
            if (mModel.isSelected())
                ((BaseActivity) mContext).setImageWithGlide(holder.mCheckBox, android.R.drawable.checkbox_on_background);
            else
                ((BaseActivity) mContext).setImageWithGlide(holder.mCheckBox, android.R.drawable.checkbox_off_background);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mProfileList.size();
    }

    class StoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_user)
        CircleImageView mUserImage;
        @BindView(R.id.tv_user)
        TextView mUserName;
        @BindView(R.id.iv_check)
        ImageView mCheckBox;
        @BindView(R.id.card)
        CardView mCardView;

        public StoryHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mCheckBox.setOnClickListener(this);
            mCardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String mId = String.valueOf(mProfileList.get(getLayoutPosition()).getUserID());
            if (mProfileList.get(getLayoutPosition()).isSelected()) {
                for (int i = 0; i < mSelectedIds.size(); i++) {
                    if (mId.equalsIgnoreCase(mSelectedIds.get(i))) {
                        mProfileList.get(getLayoutPosition()).setSelected(false);
                        mSelectedIds.remove(i);
                    }
                }
            } else {
                mProfileList.get(getLayoutPosition()).setSelected(true);
                mSelectedIds.add(mId);
            }
            notifyDataSetChanged();
            StorySettingActivity mActivity = (StorySettingActivity) mContext;
            mActivity.submitBtnVisiblity();
        }
    }
}
