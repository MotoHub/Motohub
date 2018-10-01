package online.motohub.adapter;

import android.content.Context;
import android.content.Intent;
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
import online.motohub.activity.MyMotoFileActivity;
import online.motohub.model.ProfileResModel;
import online.motohub.model.ReplyLikeModel;
import online.motohub.util.Utility;


public class CommentReplyLikeAdapter  extends RecyclerView.Adapter<CommentReplyLikeAdapter.Holder> {

    private Context mContext;
    private List<ReplyLikeModel> mReplyLikesList;
    private ProfileResModel mMyProfileResModel;

    public CommentReplyLikeAdapter(Context mContext, ArrayList<ReplyLikeModel> replyLikeList, ProfileResModel myProfileResModel) {

        this.mContext = mContext;
        this.mReplyLikesList = replyLikeList;
        this.mMyProfileResModel = myProfileResModel;
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
    public void onBindViewHolder(final Holder mHolder, int pos) {
        ReplyLikeModel mEntity = mReplyLikesList.get(pos);
        String imgStr = mEntity.getProfilesByProfileID().getProfilePicture();
        if (!imgStr.isEmpty()) {
            ((BaseActivity)mContext).setImageWithGlide(mHolder.mUserImg,imgStr, R.drawable.default_profile_icon);
        } else {
            mHolder.mUserImg.setImageResource(R.drawable.default_profile_icon);
        }
        mHolder.mCommentImgLay.setTag(pos);
        mHolder.mCommentImgLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileClick(mReplyLikesList.get(mHolder.getLayoutPosition()));
            }
        });

        mHolder.mUserNameTxt.setText(Utility.getInstance().getUserName(mEntity.getProfilesByProfileID()));
    }

    @Override
    public int getItemCount() {
        return mReplyLikesList.size();
    }

    private void profileClick(ReplyLikeModel feedLikeModel) {

        if(mMyProfileResModel.getID() == feedLikeModel.getProfileID()){
            ((BaseActivity) mContext).moveMyProfileScreen(mContext,0);
        } else{
            ((BaseActivity) mContext).moveOtherProfileScreen(mContext, mMyProfileResModel.getID(),
                    feedLikeModel.getProfilesByProfileID().getID());
        }
    }

}