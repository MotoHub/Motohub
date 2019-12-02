package online.motohub.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
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
import online.motohub.model.VideoReplyLikeModel;

import static online.motohub.activity.BaseActivity.SPECTATOR;

/**
 * Created by pyd10 on 3/9/2018.
 */

public class VideoReplyLikeAdapter extends RecyclerView.Adapter<VideoReplyLikeAdapter.Holder> {

    private Context mContext;
    private List<VideoReplyLikeModel> mReplyLikesList;
    private ProfileResModel mMyProfileResModel;

    public VideoReplyLikeAdapter(Context mContext, ArrayList<VideoReplyLikeModel> replyLikeList, ProfileResModel myProfileResModel) {

        this.mContext = mContext;
        this.mReplyLikesList = replyLikeList;
        this.mMyProfileResModel = myProfileResModel;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_feed_likes, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder mHolder, final int pos) {
        try {
            VideoReplyLikeModel mEntity = mReplyLikesList.get(pos);
            String imgStr = mEntity.getProfilesByProfileID().getProfilePicture();
            if (!imgStr.isEmpty()) {
                ((BaseActivity) mContext).setImageWithGlide(mHolder.mUserImg, imgStr, R.drawable.default_profile_icon);
            } else {
                mHolder.mUserImg.setImageResource(R.drawable.default_profile_icon);
            }
            mHolder.mCommentImgLay.setTag(pos);
            mHolder.mCommentImgLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profileClick(mReplyLikesList.get(pos));
                }
            });

            if (mEntity.getProfilesByProfileID().getProfileType() == Integer.parseInt(SPECTATOR)) {
                mHolder.mUserNameTxt.setText(mEntity.getProfilesByProfileID().getSpectatorName());
            } else {
                mHolder.mUserNameTxt.setText(mEntity.getProfilesByProfileID().getDriver());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mReplyLikesList.size();
    }

    private void profileClick(VideoReplyLikeModel feedLikeModel) {
        if (mMyProfileResModel.getID() == feedLikeModel.getProfileID()) {
            ((BaseActivity) mContext).moveMyProfileScreen(mContext, mMyProfileResModel.getID());
        } else {
            ((BaseActivity) mContext).moveOtherProfileScreen(mContext, mMyProfileResModel.getID(),
                    feedLikeModel.getProfilesByProfileID().getID());

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