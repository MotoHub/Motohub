package online.motohub.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.CommentReplyActivity;
import online.motohub.model.FeedCommentLikeModel;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedCommentReplyModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.util.UrlUtils;
import online.motohub.util.Utility;

public class FeedCommentsAdapter extends RecyclerView.Adapter<FeedCommentsAdapter.Holder> {

    private Context mContext;
    private List<FeedCommentModel> mFeedCommentList;
    private ProfileResModel mMyProfileResModel;
    private Dialog mCommentListPopup;
    private Activity mActivity;
    private int mTempPosition;
    private RecyclerView mFeedCommentsListView;
    private int mDeleteLikeID;

    public FeedCommentsAdapter(Context context, List<FeedCommentModel> commentsList, ProfileResModel myProfileResModel) {
        this.mContext = context;
        this.mFeedCommentList = commentsList;
        this.mMyProfileResModel = myProfileResModel;
        this.mActivity = (Activity) context;
    }

    public void resetCommentList(List<FeedCommentModel> commentList) {
        mFeedCommentList.clear();
        mFeedCommentList.addAll(commentList);
        notifyDataSetChanged();
    }

    public void resetCommentReplyList(ArrayList<FeedCommentReplyModel> mReplyList) {
        Collections.reverse(mReplyList);
        mFeedCommentList.get(mTempPosition).setFeedCommentReplyModel(mReplyList);
        notifyDataSetChanged();
    }

    public void resetCommentsLikeList(FeedCommentLikeModel mCommentLikeModel) {

        ArrayList<FeedCommentLikeModel> mCommentLikeEntityList = mFeedCommentList.get(mTempPosition).getFeedCommentLikeModel();

        mCommentLikeEntityList.add(mCommentLikeModel);

        mFeedCommentList.get(mTempPosition).setFeedCommentLikeModel(mCommentLikeEntityList);
        notifyDataSetChanged();

    }

    public void resetCommentUnLikeList(FeedCommentLikeModel mCommentLikeModel) {

        ArrayList<FeedCommentLikeModel> mCommentLikeEntityList = mFeedCommentList.get(mTempPosition).getFeedCommentLikeModel();

        for (int i = 0; i < mCommentLikeEntityList.size(); i++) {
            if (mCommentLikeEntityList.get(i).getProfileID() == mMyProfileResModel.getID())
                mCommentLikeEntityList.remove(i);
        }

        mFeedCommentList.get(mTempPosition).setFeedCommentLikeModel(mCommentLikeEntityList);
        notifyDataSetChanged();

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_feed_comments, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder mHolder, final int pos) {
        try {
            final FeedCommentModel mEntity = mFeedCommentList.get(pos);

            if (pos == mFeedCommentList.size() - 1) {
                mHolder.mBottomView.setVisibility(View.VISIBLE);
            }

            String imgStr = mEntity.getProfiles_by_ProfileID().getProfilePicture();
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
                    profileClick(mFeedCommentList.get(selPos));

                }
            });

            if (!mEntity.getCommentImages().trim().isEmpty()) {
                mHolder.mIvCommentImg.setVisibility(View.VISIBLE);
                mHolder.mPostPicProgressBar.setVisibility(View.VISIBLE);
                //  ((BaseActivity) mContext).setImageWithGlide(mHolder.mIvCommentImg, mEntity.getCommentImages().trim(), R.drawable.default_cover_img);
                GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + mEntity.getCommentImages().trim(), new LazyHeaders.Builder()
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

            mHolder.mUserNameTxt.setText(Utility.getInstance().getUserName(mEntity.getProfiles_by_ProfileID()));
            if (!mEntity.getmComment().trim().isEmpty()) {
                try {
                    mHolder.mCommentTxt.setVisibility(View.VISIBLE);
                    mHolder.mCommentTxt.setText(((BaseActivity) mContext).setTextEdt(mContext, URLDecoder.decode(mEntity.getmComment(), "UTF-8"), mEntity.getCommentTaggedUserNames(), mEntity.getCommentTaggedUserID(), mMyProfileResModel.getID()));
                    mHolder.mCommentTxt.setMovementMethod(LinkMovementMethod.getInstance());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mHolder.mCommentTxt.setVisibility(View.GONE);
            }
            mHolder.mCommentPostTime.setText(((BaseActivity) mContext).findTime(mEntity.getmCreateTime()));

            if (mFeedCommentList.get(pos).getFeedCommentLikeModel() != null) {
                setLikeUnLikeForPost(mHolder, pos);
            } else {
                mHolder.mLikeBtn.setImageResource(R.drawable.like_icon);
                mHolder.mLikeBtn.setTag("like");
                mHolder.mLikeCountTxt.setText("like");
            }

            if (mEntity.getFeedCommentReplyModel() != null && mEntity.getFeedCommentReplyModel().size() > 0) {
                showReplyLay(mHolder, mFeedCommentList.get(pos), pos);
            } else {
                mHolder.mReplyLinearLay.setVisibility(View.GONE);
            }

            mHolder.mIvCommentImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BaseActivity) mContext).moveLoadImageScreen(mContext, UrlUtils.FILE_URL + mEntity.getCommentImages());

                }
            });
            mHolder.mCommentReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTempPosition = pos;
                    Intent mCommentReplyActivity = new Intent(mContext, CommentReplyActivity.class).putExtra(FeedCommentModel.COMMENT_LIST, mFeedCommentList.get(pos)).putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
                    ((Activity) mContext).startActivityForResult(mCommentReplyActivity, AppConstants.POST_COMMENT_REPLY_REQUEST);
                }
            });

            mHolder.mLikeCountTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<FeedCommentLikeModel> mCommentLikeEntity;
                    mTempPosition = pos;
                    if (mEntity.getFeedCommentReplyModel() != null) {
                        mCommentLikeEntity = mEntity.getFeedCommentLikeModel();
                    } else {
                        mCommentLikeEntity = new ArrayList<>();
                    }
                    if (mCommentLikeEntity.size() > 0) {
                        showCommentLikeListPopup(mContext.getString(R.string.comment_likes));
                        setFeedCommentLikeAdapter(mCommentLikeEntity);
                    }
                }
            });
            mHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTempPosition = pos;
                    if (!((BaseActivity) mContext).isMultiClicked()) {
                        switch (v.getTag().toString()) {
                            case "unlike":

                                ArrayList<FeedCommentLikeModel> mFeedLikeList = (mFeedCommentList.get(pos).getFeedCommentLikeModel());

                                if (mFeedLikeList.size() > 0) {

                                    for (FeedCommentLikeModel likesEntity : mFeedLikeList) {
                                        if (likesEntity.getProfileID() == mMyProfileResModel.getID() && likesEntity.getCommentID() == mFeedCommentList.get(pos).getId()) {
                                            mDeleteLikeID = likesEntity.getID();
                                            break;
                                        }

                                    }
                                    callUnLikePost(mDeleteLikeID);
                                }

                                break;

                            case "like":

                                callLikePost(mFeedCommentList.get(pos).getId(), mMyProfileResModel.getID());

                                break;
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showReplyLay(final Holder mHolder, FeedCommentModel mEntity, final int position) {

        final ArrayList<FeedCommentReplyModel> mCommentReplyEntity = mEntity.getFeedCommentReplyModel();
        final int mLastCount = (mCommentReplyEntity.size()) - 1;
        if (mLastCount > 0) {
            String mCountTxt = "View " + mLastCount + " More Replies";
            mHolder.mReplyCountTxt.setText(mCountTxt);
            mHolder.mReplyCountTxt.setVisibility(View.VISIBLE);
        } else {
            mHolder.mReplyCountTxt.setVisibility(View.GONE);
        }


        if (mCommentReplyEntity.get(mLastCount).getUserType().trim().isEmpty() || mCommentReplyEntity.get(mLastCount).getUserType().trim().equals(AppConstants.USER)) {
            ((BaseActivity) mContext).setImageWithGlide(mHolder.mReplyUserImg, mCommentReplyEntity.get(mLastCount).getProfilesByProfileID().getProfilePicture(), R.drawable.default_profile_icon);

            mHolder.mReplyUserNameTxt.setText(Utility.getInstance().getUserName(mCommentReplyEntity.get(mLastCount).getProfilesByProfileID()));
        } else {
            ((BaseActivity) mContext).setImageWithGlide(mHolder.mReplyUserImg, mCommentReplyEntity.get(mLastCount).getPromoterByProfileID().getProfileImage(), R.drawable.default_profile_icon);

            mHolder.mReplyUserNameTxt.setText((mCommentReplyEntity.get(mLastCount).getPromoterByProfileID().getName()));
        }

        if (!mCommentReplyEntity.get(mLastCount).getReplyImages().trim().isEmpty()) {
            mHolder.mReplyTxt.setVisibility(View.VISIBLE);
            mHolder.mReplyTxt.setText("Replied to this comment.");
        } else if (!mCommentReplyEntity.get(mLastCount).getReplyText().trim().isEmpty()) {
            try {
                mHolder.mReplyTxt.setVisibility(View.VISIBLE);
                mHolder.mReplyTxt.setText(((BaseActivity) mContext).setTextEdt(mContext, URLDecoder.decode(mCommentReplyEntity.get(mLastCount).getReplyText(), "UTF-8"), mCommentReplyEntity.get(mLastCount).getReplyTaggedUserNames(), mCommentReplyEntity.get(mLastCount).getReplyTaggedUserIDs(), mMyProfileResModel.getID()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            mHolder.mReplyTxt.setVisibility(View.GONE);
        }

        mHolder.mReplyLinearLay.setVisibility(View.VISIBLE);
        mHolder.mReplyLinearLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTempPosition = position;
                Intent mCommentReplyActivity = new Intent(mContext, CommentReplyActivity.class).putExtra(FeedCommentModel.COMMENT_LIST, mFeedCommentList.get(position)).putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
                ((Activity) mContext).startActivityForResult(mCommentReplyActivity, AppConstants.POST_COMMENT_REPLY_REQUEST);
            }
        });

        mHolder.mReplyCountTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTempPosition = position;
                Intent mCommentReplyActivity = new Intent(mContext, CommentReplyActivity.class).putExtra(FeedCommentModel.COMMENT_LIST, mFeedCommentList.get(position)).putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
                ((Activity) mContext).startActivityForResult(mCommentReplyActivity, AppConstants.POST_COMMENT_REPLY_REQUEST);
            }
        });
    }

    private void profileClick(FeedCommentModel feedCommentModel) {
        if (mMyProfileResModel.getID() == feedCommentModel.getmProfileId()) {
            ((BaseActivity) mContext).moveMyProfileScreen(mContext, 0);
        } else {
            ((BaseActivity) mContext).moveOtherProfileScreen(mContext, mMyProfileResModel.getID(),
                    feedCommentModel.getProfiles_by_ProfileID().getID());
        }
    }

    private void setFeedCommentLikeAdapter(ArrayList<FeedCommentLikeModel> mCommentLikeEntity) {
        FeedCommentLikeAdapter mFeedLikesAdapter = new FeedCommentLikeAdapter(mContext, mCommentLikeEntity, mMyProfileResModel);
        mFeedCommentsListView.setAdapter(mFeedLikesAdapter);
        mFeedCommentsListView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void showCommentLikeListPopup(String title) {


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

    private void callUnLikePost(Integer profileId) {

        RetrofitClient.getRetrofitInstance().callUnLikeForComments(((BaseActivity) mActivity), profileId, RetrofitClient.COMMENT_UNLIKE);

    }

    private void callLikePost(int commentId, int profileId) {

        JsonObject mJsonObject = new JsonObject();
        JsonObject mItem = new JsonObject();

        mItem.addProperty("CommentID", commentId);
        mItem.addProperty("ProfileID", profileId);

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mItem);
        mJsonObject.add("resource", mJsonArray);
        RetrofitClient.getRetrofitInstance().postLikesForComments(((BaseActivity) mActivity), mJsonObject, RetrofitClient.COMMENT_LIKES);

    }

    @Override
    public int getItemCount() {
        return mFeedCommentList.size();
    }

    private void setLikeUnLikeForPost(Holder mViewHolder, int position) {

        if (mFeedCommentList.get(position).getFeedCommentLikeModel().size() > 0) {

            ArrayList<FeedCommentLikeModel> mFeedLikes = (mFeedCommentList.get(position).getFeedCommentLikeModel());

            String resLikes;

            if (mFeedCommentList.get(position).getFeedCommentLikeModel().size() == 1) {

                resLikes = mFeedCommentList.get(position).getFeedCommentLikeModel().size() + " like";
            } else {
                resLikes = mFeedCommentList.get(position).getFeedCommentLikeModel().size() + " likes";

            }
            mViewHolder.mLikeCountTxt.setText(resLikes);

            for (final FeedCommentLikeModel likesEntity : mFeedLikes) {

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

        @BindView(R.id.comment_user_img)
        CircleImageView mUserImg;

        @BindView(R.id.comment_user_img_lay)
        RelativeLayout mCommentImgLay;

        @BindView(R.id.comment_user_name_txt)
        TextView mUserNameTxt;

        @BindView(R.id.comment_txt)
        TextView mCommentTxt;

        @BindView(R.id.commentPostTimeTxt)
        TextView mCommentPostTime;

        @BindView(R.id.replyTxt)
        TextView mCommentReply;

        @BindView(R.id.replyCommentsLay)
        LinearLayout mReplyLinearLay;

        @BindView(R.id.replyCount)
        TextView mReplyCountTxt;

        @BindView(R.id.imgReplyUser)
        CircleImageView mReplyUserImg;

        @BindView(R.id.txtReplyText)
        TextView mReplyTxt;

        @BindView(R.id.txtReplyUserName)
        TextView mReplyUserNameTxt;

        @BindView(R.id.like_count_txt)
        TextView mLikeCountTxt;

        @BindView(R.id.likeBtn)
        ImageView mLikeBtn;

        @BindView(R.id.bottomView)
        View mBottomView;

        @BindView(R.id.ivCommentImg)
        ImageView mIvCommentImg;

        @BindView(R.id.smallProgressBar)
        ProgressBar mPostPicProgressBar;

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }


}
