package online.motohub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.model.ProfileResModel;
import online.motohub.model.VideoShareModel;

/**
 * Created by pyd10 on 3/8/2018.
 */

public class VideoSharesAdapter extends RecyclerView.Adapter<VideoSharesAdapter.Holder> {

    private Context mContext;
    private List<VideoShareModel> mFeedSharesList;
    private ProfileResModel mMyProfileResModel;

    public VideoSharesAdapter(Context mContext, ArrayList<VideoShareModel> mFeedShareList, ProfileResModel mMyProfileResModel) {

        this.mContext = mContext;
        this.mFeedSharesList = mFeedShareList;
        this.mMyProfileResModel = mMyProfileResModel;
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.comment_user_img)
        CircleImageView mUserImg;

        @BindView(R.id.comment_user_img_lay)
        RelativeLayout mCommentImgLay;

        @BindView(R.id.comment_user_name_txt)
        TextView mUserNameTxt;

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_feed_likes, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder mHolder, final int pos) {
        VideoShareModel mEntity = mFeedSharesList.get(pos);
        String imgStr = mEntity.getProfiles_by_ProfileID().getProfilePicture();
        if (!imgStr.isEmpty()) {
            ((BaseActivity)mContext).setImageWithGlide(mHolder.mUserImg,imgStr, R.drawable.default_profile_icon);
        } else {
            mHolder.mUserImg.setImageResource(R.drawable.default_profile_icon);
        }
        mHolder.mCommentImgLay.setTag(pos);
        mHolder.mCommentImgLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selPos = (int) v.getTag();
                //TODO View Profile Screen
                profileClick(mFeedSharesList.get(pos));
            }
        });

        if (mEntity.getProfiles_by_ProfileID().getProfileType() == Integer.parseInt(BaseActivity.SPECTATOR)) {
            mHolder.mUserNameTxt.setText(mEntity.getProfiles_by_ProfileID().getSpectatorName());
        }
        else {
            mHolder.mUserNameTxt.setText(mEntity.getProfiles_by_ProfileID().getDriver());
        }
    }


    private void profileClick(VideoShareModel feedShareModel) {

        if (mMyProfileResModel.getID() == feedShareModel.getProfileID()) {
            ((BaseActivity)mContext).moveMyProfileScreen(mContext,mMyProfileResModel.getID());
        } else {
            ((BaseActivity)mContext).moveOtherProfileScreen(mContext,mMyProfileResModel.getID(),
                    feedShareModel.getProfiles_by_ProfileID().getID());

        }
    }
    @Override
    public int getItemCount() {
        return mFeedSharesList.size();
    }

}