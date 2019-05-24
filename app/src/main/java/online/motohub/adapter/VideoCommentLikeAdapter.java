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
import online.motohub.model.VideoCommentLikeModel;

import static online.motohub.activity.BaseActivity.SPECTATOR;

/**
 * Created by pyd10 on 3/9/2018.
 */

public class VideoCommentLikeAdapter extends RecyclerView.Adapter<VideoCommentLikeAdapter.Holder> {

    private Context mContext;
    private List<VideoCommentLikeModel> mFeedLikesList;
    private ProfileResModel mMyProfileResModel;

    public VideoCommentLikeAdapter(Context mContext, ArrayList<VideoCommentLikeModel> mFeedLikeList, ProfileResModel mMyProfileResModel) {

        this.mContext = mContext;
        this.mFeedLikesList = mFeedLikeList;
        this.mMyProfileResModel = mMyProfileResModel;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_feed_likes, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder mHolder, final int pos) {
        try {
            VideoCommentLikeModel mEntity = mFeedLikesList.get(pos);
            String imgStr = mEntity.getProfileByProfileID().getProfilePicture();
            if (!imgStr.isEmpty()) {
                ((BaseActivity) mContext).setImageWithGlide(mHolder.mUserImg, imgStr, R.drawable.default_profile_icon);
            } else {
                mHolder.mUserImg.setImageResource(R.drawable.default_profile_icon);
            }
            mHolder.mCommentImgLay.setTag(pos);
            mHolder.mCommentImgLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selPos = (int) v.getTag();
                    //TODO View Profile Screen
                    profileClick(mFeedLikesList.get(pos));
                }
            });

            if (mEntity.getProfileByProfileID().getProfileType() == Integer.parseInt(SPECTATOR)) {
                mHolder.mUserNameTxt.setText(mEntity.getProfileByProfileID().getSpectatorName());
            } else {
                mHolder.mUserNameTxt.setText(mEntity.getProfileByProfileID().getDriver());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mFeedLikesList.size();
    }

    private void profileClick(VideoCommentLikeModel feedCommentLikeModel) {
        if (mMyProfileResModel.getID() == feedCommentLikeModel.getProfileID()) {
            ((BaseActivity) mContext).moveMyProfileScreen(mContext, mMyProfileResModel.getID());
        } else {
            ((BaseActivity) mContext).moveOtherProfileScreen(mContext, mMyProfileResModel.getID(),
                    feedCommentLikeModel.getProfileByProfileID().getID());

        }
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

}
