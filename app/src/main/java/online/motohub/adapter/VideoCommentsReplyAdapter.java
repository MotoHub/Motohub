package online.motohub.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.model.ProfileResModel;
import online.motohub.model.VideoCommentReplyModel;
import online.motohub.model.VideoCommentsModel;
import online.motohub.model.VideoReplyLikeModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.UrlUtils;

public class VideoCommentsReplyAdapter extends RecyclerView.Adapter<VideoCommentsReplyAdapter.Holder> {

    private Context mContext;
    private List<VideoCommentReplyModel> mFeedReplyList;
    private ProfileResModel mMyProfileResModel;
    private int mTempPosition;
    private int mDeleteLikeID;
    private VideoCommentsModel mFeedCommentModel;
    private Dialog mCommentListPopup;
    private RecyclerView mFeedCommentsListView;

    public VideoCommentsReplyAdapter(Context context, List<VideoCommentReplyModel> replyList, ProfileResModel myProfileResModel, VideoCommentsModel feedCommentModel) {
        this.mContext = context;
        this.mFeedReplyList = replyList;
        this.mMyProfileResModel = myProfileResModel;
        this.mFeedCommentModel = feedCommentModel;


    }

    public void resetCommentReplyList(ArrayList<VideoCommentReplyModel> mCommentReplyEntity) {
        mFeedReplyList.clear();
        mFeedReplyList.addAll(mCommentReplyEntity);
        notifyDataSetChanged();
    }


    public void resetReplyLikeList(VideoReplyLikeModel mReplyLikeModel) {

        ArrayList<VideoReplyLikeModel> mReplyLikeList = mFeedReplyList.get(mTempPosition).getReplyLikeByReplyID();
        mReplyLikeList.add(mReplyLikeModel);
        mFeedReplyList.get(mTempPosition).setReplyLikeByReplyID(mReplyLikeList);
        notifyDataSetChanged();
    }

    public void resetReplyUnLikeList(VideoReplyLikeModel replyLikeModel) {
        ArrayList<VideoReplyLikeModel> mReplyLikeList = mFeedReplyList.get(mTempPosition).getReplyLikeByReplyID();

        for (int i = 0; i < mReplyLikeList.size(); i++) {
            if (mReplyLikeList.get(i).getProfileID() == mMyProfileResModel.getID())
                mReplyLikeList.remove(i);
        }

        mFeedReplyList.get(mTempPosition).setReplyLikeByReplyID(mReplyLikeList);
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_feed_comments_reply, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder mHolder, final int pos) {
        try {
            final VideoCommentReplyModel mEntity = mFeedReplyList.get(pos);

            mHolder.mCommentReplyImgLay.setTag(pos);

            mHolder.mReplyPostTime.setText(((BaseActivity) mContext).findTime(mEntity.getCreateTime()));
            if (mFeedReplyList.get(pos).getReplyLikeByReplyID().size() > 0) {
                setLikeUnLikeForPost(mHolder, pos);
            } else {
                mHolder.mLikeBtn.setImageResource(R.drawable.like_icon);
                mHolder.mLikeBtn.setTag("like");
                mHolder.mLikeCountTxt.setText("like");
            }

            mHolder.mCommentReplyImgLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //int selPos = (int) v.getTag();
                    //TODO View Profile Screen
                    profileClick(mFeedReplyList.get(pos));
                }
            });

            if (mEntity.getUserType().isEmpty() || mEntity.getUserType().trim().equals(AppConstants.USER)) {
                String imgStr = mEntity.getProfilesByProfileID().getProfilePicture();

                ((BaseActivity) mContext).setImageWithGlide(mHolder.mUserImg, imgStr, R.drawable.default_profile_icon);

                if (mEntity.getProfilesByProfileID().getProfileType() == Integer.parseInt(BaseActivity.SPECTATOR)) {
                    mHolder.mUserNameTxt.setText(mEntity.getProfilesByProfileID().getSpectatorName());
                } else {
                    mHolder.mUserNameTxt.setText(mEntity.getProfilesByProfileID().getDriver());
                }
            } else {
                String imgStr = mEntity.getPromoterByProfileID().getProfileImage();

                ((BaseActivity) mContext).setImageWithGlide(mHolder.mUserImg, imgStr, R.drawable.default_profile_icon);

                mHolder.mUserNameTxt.setText(mEntity.getPromoterByProfileID().getName());

            }
            if (!mEntity.getReplyText().trim().isEmpty()) {
                try {
                    mHolder.mCommentReplyTxt.setVisibility(View.VISIBLE);
                    mHolder.mCommentReplyTxt.setText(((BaseActivity) mContext).setTextEdt(mContext, URLDecoder.decode(mEntity.getReplyText(), "UTF-8"), mEntity.getReplyTaggedUserNames(), mEntity.getReplyTaggedUserIDs(), mMyProfileResModel.getID()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                mHolder.mCommentReplyTxt.setVisibility(View.GONE);
            }
            if (!mEntity.getReplyImages().trim().isEmpty()) {
                mHolder.mIvCommentImg.setVisibility(View.VISIBLE);
                mHolder.mPostPicProgressBar.setVisibility(View.VISIBLE);
                // ((BaseActivity)mContext).setImageWithGlide(mHolder.mIvCommentImg,mEntity.getReplyImages(),R.drawable.default_cover_img);
                GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + mEntity.getReplyImages().trim(), new LazyHeaders.Builder()
                        .addHeader("X-DreamFactory-Api-Key", mContext.getString(R.string.dream_factory_api_key))
                        .build());

                Glide.with(mContext)
                        .load(glideUrl)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                mHolder.mPostPicProgressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                mHolder.mPostPicProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .apply(new RequestOptions()
                                .dontAnimate()
                        )
                        .into(mHolder.mIvCommentImg);
            } else {
                mHolder.mIvCommentImg.setVisibility(View.GONE);
            }

            mHolder.mIvCommentImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BaseActivity) mContext).moveLoadImageScreen(mContext, UrlUtils.FILE_URL + mEntity.getReplyImages());
                }
            });
            mHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTempPosition = pos;

                    if (!((BaseActivity) mContext).isMultiClicked()) {

                        switch (v.getTag().toString()) {
                            case "unlike":

                                ArrayList<VideoReplyLikeModel> mReplyLikeList = mFeedReplyList.get(mTempPosition).getReplyLikeByReplyID();

                                if (mReplyLikeList.size() > 0) {

                                    for (VideoReplyLikeModel likesEntity : mReplyLikeList) {
                                        if (likesEntity.getProfileID() == mMyProfileResModel.getID() && likesEntity.getReplyID() == mFeedReplyList.get(mTempPosition).getID()) {
                                            mDeleteLikeID = likesEntity.getID();
                                            break;
                                        }

                                    }
                                    callUnLikeReply(mDeleteLikeID);
                                }

                                break;

                            case "like":

                                callLikeReply(mFeedCommentModel.getId(), mFeedReplyList.get(mTempPosition).getID(), mMyProfileResModel.getID());

                                break;
                        }
                    }
                }
            });
            mHolder.mLikeCountTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTempPosition = pos;
                    ArrayList<VideoReplyLikeModel> mReplyLikeList = mFeedReplyList.get(mTempPosition).getReplyLikeByReplyID();
                    if (mReplyLikeList.size() > 0) {
                        showCommentReplyListPopup(mContext.getString(R.string.reply_likes));
                        setFeedReplyLikeAdapter(mReplyLikeList);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setFeedReplyLikeAdapter(ArrayList<VideoReplyLikeModel> mReplyLikeList) {
        VideoReplyLikeAdapter mLikesReplyAdapter = new VideoReplyLikeAdapter(mContext, mReplyLikeList, mMyProfileResModel);
        mFeedCommentsListView.setAdapter(mLikesReplyAdapter);
        mFeedCommentsListView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void showCommentReplyListPopup(String title) {

        if (mCommentListPopup != null && mCommentListPopup.isShowing()) {
            mCommentListPopup.dismiss();
        }

        // Create custom dialog object
        mCommentListPopup = new Dialog(this.mContext, R.style.MyDialogBottomSheet);
        mCommentListPopup.setContentView(R.layout.popup_feed_like_list);

        ImageView mCloseIcon = mCommentListPopup.findViewById(R.id.close_btn);
        TextView mTitleTxt = mCommentListPopup.findViewById(R.id.title_txt);
        mFeedCommentsListView = mCommentListPopup.findViewById(R.id.feeds_likes_list_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mFeedCommentsListView.setLayoutManager(mLayoutManager);
        mFeedCommentsListView.setItemAnimator(new DefaultItemAnimator());

        mTitleTxt.setText(title);


        mCloseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentListPopup.dismiss();
            }
        });

        mCommentListPopup.show();
    }

    private void callLikeReply(int commentID, int replyID, int profileId) {

        JsonObject mItem = new JsonObject();

        mItem.addProperty("VideoCommentID", commentID);
        mItem.addProperty("ReplyID", replyID);
        mItem.addProperty("ProfileID", profileId);

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mItem);

        RetrofitClient.getRetrofitInstance().postLikesForVideoReply(((BaseActivity) mContext), mJsonArray, RetrofitClient.VIDEO_REPLY_LIKE);

    }

    private void callUnLikeReply(int mDeleteLikeID) {

        RetrofitClient.getRetrofitInstance().callUnLikeVideoReply(((BaseActivity) mContext), mDeleteLikeID, RetrofitClient.VIDEO_REPLY_UNLIKE);
    }

    @Override
    public int getItemCount() {
        return mFeedReplyList.size();
    }

    private void profileClick(VideoCommentReplyModel feedCommentReplyModel) {
        if (mMyProfileResModel.getID() == feedCommentReplyModel.getProfileID()) {
            ((BaseActivity) mContext).moveMyProfileScreen(mContext, mMyProfileResModel.getID());
        } else {

            ((BaseActivity) mContext).moveOtherProfileScreen(mContext, mMyProfileResModel.getID(),
                    feedCommentReplyModel.getProfilesByProfileID().getID());

        }
    }

    private void setLikeUnLikeForPost(Holder mViewHolder, int position) {

        if (mFeedReplyList.get(position).getReplyLikeByReplyID().size() > 0) {

            ArrayList<VideoReplyLikeModel> mReplyLikes = mFeedReplyList.get(position).getReplyLikeByReplyID();

            String resLikes;

            if (mFeedReplyList.get(position).getReplyLikeByReplyID().size() == 1) {

                resLikes = mFeedReplyList.get(position).getReplyLikeByReplyID().size() + " like";
            } else {
                resLikes = mFeedReplyList.get(position).getReplyLikeByReplyID().size() + " likes";

            }
            mViewHolder.mLikeCountTxt.setText(resLikes);

            for (final VideoReplyLikeModel likesEntity : mReplyLikes) {

                if ((likesEntity.getProfileID() == mMyProfileResModel.getID())) {
                    mViewHolder.mLikeBtn.setImageResource(R.drawable.liked_icon);
                    mViewHolder.mLikeBtn.setTag("unlike");
                    break;
                } else {
                    mViewHolder.mLikeBtn.setImageResource(R.drawable.like_icon);
                    mViewHolder.mLikeBtn.setTag("like");
                }
            }
        } else {
            mViewHolder.mLikeBtn.setImageResource(R.drawable.like_icon);
            mViewHolder.mLikeBtn.setTag("like");
            mViewHolder.mLikeCountTxt.setText("like");
        }
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.reply_user_img)
        CircleImageView mUserImg;

        @BindView(R.id.reply_user_img_lay)
        RelativeLayout mCommentReplyImgLay;

        @BindView(R.id.reply_user_name_txt)
        TextView mUserNameTxt;

        @BindView(R.id.reply_txt)
        TextView mCommentReplyTxt;

        @BindView(R.id.like_count_txt)
        TextView mLikeCountTxt;

        @BindView(R.id.likeBtn)
        ImageView mLikeBtn;

        @BindView(R.id.ivCommentImg)
        ImageView mIvCommentImg;

        @BindView(R.id.replyPostTimeTxt)
        TextView mReplyPostTime;

        @BindView(R.id.smallProgressBar)
        ProgressBar mPostPicProgressBar;


        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
