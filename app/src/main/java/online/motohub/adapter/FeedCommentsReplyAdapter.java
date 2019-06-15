package online.motohub.adapter;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.club.ClubProfileActivity;
import online.motohub.activity.news_and_media.NewsAndMediaProfileActivity;
import online.motohub.activity.performance_shop.PerformanceShopProfileActivity;
import online.motohub.activity.promoter.PromoterProfileActivity;
import online.motohub.activity.track.TrackProfileActivity;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedCommentReplyModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.ReplyLikeModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.util.UrlUtils;
import online.motohub.util.Utility;

public class FeedCommentsReplyAdapter extends RecyclerView.Adapter<FeedCommentsReplyAdapter.Holder> {

    private Context mContext;
    private List<FeedCommentReplyModel> mFeedReplyList;
    private ProfileResModel mMyProfileResModel;
    private int mTempPosition;
    private int mDeleteLikeID;
    private FeedCommentModel mFeedCommentModel;
    private Dialog mCommentListPopup;
    private RecyclerView mFeedCommentsListView;

    public FeedCommentsReplyAdapter(Context context, List<FeedCommentReplyModel> replyList, ProfileResModel myProfileResModel, FeedCommentModel feedCommentModel) {
        this.mContext = context;
        this.mFeedReplyList = replyList;
        this.mMyProfileResModel = myProfileResModel;
        this.mFeedCommentModel = feedCommentModel;


    }

    public void resetCommentReplyList(ArrayList<FeedCommentReplyModel> mCommentReplyEntity) {
        mFeedReplyList.clear();
        mFeedReplyList.addAll(mCommentReplyEntity);
        notifyDataSetChanged();
    }


    public void resetReplyLikeList(ReplyLikeModel mReplyLikeModel) {

        ArrayList<ReplyLikeModel> mReplyLikeList = mFeedReplyList.get(mTempPosition).getReplyLikeByReplyID();
        mReplyLikeList.add(mReplyLikeModel);
        mFeedReplyList.get(mTempPosition).setReplyLikeByReplyID(mReplyLikeList);
        notifyDataSetChanged();
    }

    public void resetReplyUnLikeList(ReplyLikeModel replyLikeModel) {
        ArrayList<ReplyLikeModel> mReplyLikeList = mFeedReplyList.get(mTempPosition).getReplyLikeByReplyID();

        for (int i = 0; i < mReplyLikeList.size(); i++) {
            if (mReplyLikeList.get(i).getProfileID() == mMyProfileResModel.getID())
                mReplyLikeList.remove(i);
        }

        mFeedReplyList.get(mTempPosition).setReplyLikeByReplyID(mReplyLikeList);
        notifyDataSetChanged();
    }

    @Override
    public FeedCommentsReplyAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_feed_comments_reply, parent, false);
        return new FeedCommentsReplyAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(final FeedCommentsReplyAdapter.Holder mHolder, final int pos) {
        try {
            final FeedCommentReplyModel mEntity = mFeedReplyList.get(pos);

            mHolder.mReplyPostTime.setText(((BaseActivity) mContext).findTime(mEntity.getCreateTime()));
            if (mFeedReplyList.get(pos).getReplyLikeByReplyID() != null) {
                setLikeUnLikeForPost(mHolder, pos);
            } else {
                mHolder.mLikeBtn.setImageResource(R.drawable.like_icon);
                mHolder.mLikeBtn.setTag("like");
                mHolder.mLikeCountTxt.setText("like");
            }

            mHolder.mCommentReplyImgLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //   int selPos = (int) v.getTag();
                    mTempPosition = pos;
                    profileClick(mFeedReplyList.get(pos));
                }
            });

            if (mEntity.getUserType().isEmpty() || mEntity.getUserType().trim().equals(AppConstants.USER)) {
                String imgStr = mEntity.getProfilesByProfileID().getProfilePicture();

                ((BaseActivity) mContext).setImageWithGlide(mHolder.mUserImg, imgStr, R.drawable.default_profile_icon);

                mHolder.mUserNameTxt.setText(Utility.getInstance().getUserName(mEntity.getProfilesByProfileID()));
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

                                ArrayList<ReplyLikeModel> mReplyLikeList = mFeedReplyList.get(pos).getReplyLikeByReplyID();

                                if (mReplyLikeList.size() > 0) {

                                    for (ReplyLikeModel likesEntity : mReplyLikeList) {
                                        if (likesEntity.getProfileID() == mMyProfileResModel.getID() && likesEntity.getReplyID() == mFeedReplyList.get(pos).getID()) {
                                            mDeleteLikeID = likesEntity.getID();
                                            break;
                                        }

                                    }
                                    callUnLikeReply(mDeleteLikeID);
                                }

                                break;

                            case "like":

                                callLikeReply(mFeedCommentModel.getId(), mFeedReplyList.get(pos).getID(), mMyProfileResModel.getID());

                                break;
                        }
                    }
                }
            });
            mHolder.mLikeCountTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTempPosition = pos;
                    ArrayList<ReplyLikeModel> mReplyLikeList = mFeedReplyList.get(mTempPosition).getReplyLikeByReplyID();
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

    private void setFeedReplyLikeAdapter(ArrayList<ReplyLikeModel> mReplyLikeList) {
        CommentReplyLikeAdapter mLikesReplyAdapter = new CommentReplyLikeAdapter(mContext, mReplyLikeList, mMyProfileResModel);
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

        mItem.addProperty("PostCommentID", commentID);
        mItem.addProperty("ReplyID", replyID);
        mItem.addProperty("ProfileID", profileId);

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mItem);

        RetrofitClient.getRetrofitInstance().postLikesForReply(((BaseActivity) mContext), mJsonArray, RetrofitClient.REPLY_LIKE);

    }

    private void callUnLikeReply(int mDeleteLikeID) {

        RetrofitClient.getRetrofitInstance().callUnLikeReply(((BaseActivity) mContext), mDeleteLikeID, RetrofitClient.REPLY_UNLIKE);
    }

    @Override
    public int getItemCount() {
        return mFeedReplyList.size();
    }

    private void profileClick(FeedCommentReplyModel feedCommentReplyModel) {

        if (feedCommentReplyModel.getUserType().equals(AppConstants.USER) || feedCommentReplyModel.getUserType().equals("") || feedCommentReplyModel.getUserType().equals(AppConstants.ONDEMAND) || feedCommentReplyModel.getUserType().equals(AppConstants.USER_VIDEO_SHARED_POST) || feedCommentReplyModel.getUserType().equals(AppConstants.USER_EVENT_VIDEOS) || feedCommentReplyModel.getUserType().equals(AppConstants.CLUB_USER)) {
            if (mMyProfileResModel.getID() == feedCommentReplyModel.getProfileID()) {
                ((BaseActivity) mContext).moveMyProfileScreen(mContext, 0);
            } else {
                ((BaseActivity) mContext).moveOtherProfileScreen(mContext, mMyProfileResModel.getID(),
                        feedCommentReplyModel.getProfilesByProfileID().getID());
            }

            return;
        }

        Bundle mBundle = new Bundle();
        String mPostUserType = mFeedReplyList.get(mTempPosition).getUserType().trim();
        if (mPostUserType.equals(PromotersModel.PROMOTER) || mPostUserType.equals(PromotersModel.NEWS_AND_MEDIA) ||
                mPostUserType.equals(PromotersModel.TRACK) || mPostUserType.equals(PromotersModel.CLUB)
                || mPostUserType.equals(PromotersModel.SHOP) || mPostUserType.equals(AppConstants.SHARED_POST) || mPostUserType.equals(AppConstants.VIDEO_SHARED_POST)) {
            Class mClassName;
            String mUserType = "";
            if (mFeedReplyList.get(mTempPosition).getPromoterByProfileID() != null) {
                //mBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, mFeedReplyList.get(mTempPosition).getPromoterByProfileID());
                //MotoHub.getApplicationInstance().setmPromoterResModel(mFeedReplyList.get(mTempPosition).getPromoterByProfileID());
                EventBus.getDefault().postSticky(mFeedReplyList.get(mTempPosition).getPromoterByProfileID());
                mUserType = mFeedReplyList.get(mTempPosition).getUserType();
            }

            switch (mUserType) {
                case PromotersModel.NEWS_AND_MEDIA:
                    mClassName = NewsAndMediaProfileActivity.class;
                    break;
                case PromotersModel.CLUB:
                    mClassName = ClubProfileActivity.class;
                    break;
                case PromotersModel.PROMOTER:
                    mClassName = PromoterProfileActivity.class;
                    break;
                case PromotersModel.TRACK:
                    mClassName = TrackProfileActivity.class;
                    break;
                case PromotersModel.SHOP:
                    mClassName = PerformanceShopProfileActivity.class;
                    break;
                default:
                    mClassName = PromoterProfileActivity.class;
                    break;
            }

            //mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
            //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
            EventBus.getDefault().postSticky(mMyProfileResModel);
            /*((BaseActivity) mContext).startActivityForResult(new Intent(mContext, mClassName).putExtras(mBundle), AppConstants.FOLLOWERS_FOLLOWING_RESULT);*/
            ((BaseActivity) mContext).startActivityForResult(new Intent(mContext, mClassName), AppConstants.FOLLOWERS_FOLLOWING_RESULT);

        } else {
            ((BaseActivity) mContext).moveOtherProfileScreen(mContext, mMyProfileResModel.getID(),
                    mFeedReplyList.get(mTempPosition).getPromoterByProfileID().getID());
        }


    }

    private void setLikeUnLikeForPost(Holder mViewHolder, int position) {

        if (mFeedReplyList.get(position).getReplyLikeByReplyID().size() > 0) {

            ArrayList<ReplyLikeModel> mReplyLikes = mFeedReplyList.get(position).getReplyLikeByReplyID();

            String resLikes;

            if (mFeedReplyList.get(position).getReplyLikeByReplyID().size() == 1) {

                resLikes = mFeedReplyList.get(position).getReplyLikeByReplyID().size() + " like";
            } else {
                resLikes = mFeedReplyList.get(position).getReplyLikeByReplyID().size() + " likes";

            }
            mViewHolder.mLikeCountTxt.setText(resLikes);

            for (final ReplyLikeModel likesEntity : mReplyLikes) {

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
