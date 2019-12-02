package online.motohub.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import org.greenrobot.eventbus.EventBus;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.VideoCommentReplyActivity;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.VideoCommentLikeModel;
import online.motohub.model.VideoCommentReplyModel;
import online.motohub.model.VideoCommentsModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.UrlUtils;


public class VideoCommentsAdapter extends RecyclerView.Adapter<VideoCommentsAdapter.Holder> {

    private Context mContext;
    private List<VideoCommentsModel> mFeedCommentList;
    private ProfileResModel mMyProfileResModel;
    private Dialog mCommentListPopup;
    private Activity mActivity;
    private int mTempPosition;
    private RecyclerView mFeedCommentsListView;
    private int mDeleteLikeID;

    public VideoCommentsAdapter(Context context, List<VideoCommentsModel> commentsList, ProfileResModel myProfileResModel) {
        this.mContext = context;
        this.mFeedCommentList = commentsList;
        this.mMyProfileResModel = myProfileResModel;
        this.mActivity = (Activity) context;
    }


    public void resetCommentList(List<VideoCommentsModel> commentList) {
        mFeedCommentList.clear();
        mFeedCommentList.addAll(commentList);
        notifyDataSetChanged();
    }

    public void resetCommentReplyList(ArrayList<VideoCommentReplyModel> mReplyList) {
        Collections.reverse(mReplyList);
        mFeedCommentList.get(mTempPosition).setFeedCommentReplyModel(mReplyList);
        notifyDataSetChanged();
    }

    public void resetCommentsLikeList(VideoCommentLikeModel mCommentLikeModel) {

        ArrayList<VideoCommentLikeModel> mCommentLikeEntityList = mFeedCommentList.get(mTempPosition).getFeedCommentLikeModel();

        mCommentLikeEntityList.add(mCommentLikeModel);

        mFeedCommentList.get(mTempPosition).setFeedCommentLikeModel(mCommentLikeEntityList);
        notifyDataSetChanged();

    }

    public void resetCommentUnLikeList(VideoCommentLikeModel mCommentLikeModel) {

        ArrayList<VideoCommentLikeModel> mCommentLikeEntityList = mFeedCommentList.get(mTempPosition).getFeedCommentLikeModel();

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
            final VideoCommentsModel mEntity = mFeedCommentList.get(pos);

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
                    //int selPos = (int) v.getTag();
                    //TODO View Profile Screen
                    profileClick(mFeedCommentList.get(pos));

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


            if (mEntity.getProfiles_by_ProfileID().getProfileType() == Integer.parseInt(BaseActivity.SPECTATOR)) {
                mHolder.mUserNameTxt.setText(mEntity.getProfiles_by_ProfileID().getSpectatorName());
            } else {
                mHolder.mUserNameTxt.setText(mEntity.getProfiles_by_ProfileID().getDriver());
            }
            if (!mEntity.getmComment().trim().isEmpty()) {
                try {
                    if (mEntity.getCommentTaggedUserNames().isEmpty())
                        mHolder.mCommentTxt.setText(URLDecoder.decode(mEntity.getmComment(), "UTF-8"));
                    else {
                        //mHolder.mCommentTxt.setText(((BaseActivity) mContext).setTextEdt(mContext, URLDecoder.decode(mEntity.getmComment(), "UTF-8"), mEntity.getCommentTaggedUserNames(), mEntity.getCommentTaggedUserID(), mMyProfileResModel.getID()));
                        String comment_text = ((BaseActivity) mContext).setTextEdt(mContext, URLDecoder.decode(mEntity.getmComment(), "UTF-8"), mEntity.getCommentTaggedUserNames(), mEntity.getCommentTaggedUserID(), mMyProfileResModel.getID()).toString();
                        mHolder.mCommentTxt.setText(URLDecoder.decode(comment_text, "UTF-8"));
                    }
                    mHolder.mCommentTxt.setVisibility(View.VISIBLE);
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
                    //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                    EventBus.getDefault().postSticky(mMyProfileResModel);
                    Intent mCommentReplyActivity = new Intent(mContext, VideoCommentReplyActivity.class).putExtra(FeedCommentModel.COMMENT_LIST, mFeedCommentList.get(pos))/*.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel)*/;
                    ((Activity) mContext).startActivityForResult(mCommentReplyActivity, AppConstants.VIDEO_COMMENT_REPLY_REQUEST);
                }
            });

            mHolder.mLikeCountTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<VideoCommentLikeModel> mCommentLikeEntity;
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

                                ArrayList<VideoCommentLikeModel> mFeedLikeList = (mFeedCommentList.get(mTempPosition).getFeedCommentLikeModel());

                                if (mFeedLikeList.size() > 0) {

                                    for (VideoCommentLikeModel likesEntity : mFeedLikeList) {
                                        if (likesEntity.getProfileID() == mMyProfileResModel.getID() && likesEntity.getCommentID() == mFeedCommentList.get(mTempPosition).getId()) {
                                            mDeleteLikeID = likesEntity.getID();
                                            break;
                                        }

                                    }
                                    callUnLikePost(mDeleteLikeID);
                                }

                                break;

                            case "like":

                                callLikePost(mFeedCommentList.get(mTempPosition).getId(), mMyProfileResModel.getID());

                                break;
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showReplyLay(final Holder mHolder, VideoCommentsModel mEntity, final int position) {

        final ArrayList<VideoCommentReplyModel> mCommentReplyEntity = mEntity.getFeedCommentReplyModel();
        final int mLastCount = (mCommentReplyEntity.size()) - 1;
        if (mLastCount > 0) {
            String mCountTxt = "View " + mLastCount + " More Replies";
            mHolder.mReplyCountTxt.setText(mCountTxt);
            mHolder.mReplyCountTxt.setVisibility(View.VISIBLE);
        } else {
            mHolder.mReplyCountTxt.setVisibility(View.GONE);
        }

        if (mCommentReplyEntity.get(mLastCount).getUserType().trim().equals(AppConstants.USER) || mCommentReplyEntity.get(mLastCount).getUserType().isEmpty()) {
            ((BaseActivity) mContext).setImageWithGlide(mHolder.mReplyUserImg, mCommentReplyEntity.get(mLastCount).getProfilesByProfileID().getProfilePicture(), R.drawable.default_profile_icon);

            String replyUserName;
            if (mCommentReplyEntity.get(mLastCount).getProfilesByProfileID().getProfileType() == Integer.parseInt(BaseActivity.SPECTATOR)) {
                replyUserName = mCommentReplyEntity.get(mLastCount).getProfilesByProfileID().getSpectatorName();
            } else {
                replyUserName = mCommentReplyEntity.get(mLastCount).getProfilesByProfileID().getDriver();
            }
            mHolder.mReplyUserNameTxt.setText(replyUserName);


        } else {
            ((BaseActivity) mContext).setImageWithGlide(mHolder.mReplyUserImg, mCommentReplyEntity.get(mLastCount).getPromoterByProfileID().getProfileImage(), R.drawable.default_profile_icon);

            mHolder.mReplyUserNameTxt.setText(mCommentReplyEntity.get(mLastCount).getPromoterByProfileID().getName());
        }

        if (!mCommentReplyEntity.get(mLastCount).getReplyImages().trim().isEmpty()) {
            mHolder.mReplyTxt.setVisibility(View.VISIBLE);
            mHolder.mReplyTxt.setText("Replied to this comment.");
        } else if (!mCommentReplyEntity.get(mLastCount).getReplyText().trim().isEmpty()) {
            try {
                mHolder.mReplyTxt.setVisibility(View.VISIBLE);
                mHolder.mReplyTxt.setText(URLDecoder.decode(mCommentReplyEntity.get(mLastCount).getReplyText(), "UTF-8"));
                //mHolder.mReplyTxt.setText(replacer(sb.append(mCommentReplyEntity.get(mLastCount).getReplyText())));
            } catch (Exception e) {
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
                //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                EventBus.getDefault().postSticky(mMyProfileResModel);
                Intent mVideoCommentReplyActivity = new Intent(mContext, VideoCommentReplyActivity.class).putExtra(FeedCommentModel.COMMENT_LIST, mFeedCommentList.get(position))/*.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel)*/;
                ((Activity) mContext).startActivityForResult(mVideoCommentReplyActivity, AppConstants.VIDEO_COMMENT_REPLY_REQUEST);
            }
        });

        mHolder.mReplyCountTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTempPosition = position;
                //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                EventBus.getDefault().postSticky(mMyProfileResModel);
                Intent mVideoCommentReplyActivity = new Intent(mContext, VideoCommentReplyActivity.class).putExtra(FeedCommentModel.COMMENT_LIST, mFeedCommentList.get(position))/*.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel)*/;
                ((Activity) mContext).startActivityForResult(mVideoCommentReplyActivity, AppConstants.VIDEO_COMMENT_REPLY_REQUEST);
            }
        });
    }

    private void profileClick(VideoCommentsModel feedCommentModel) {

        if (mMyProfileResModel.getID() == feedCommentModel.getmProfileId()) {
            ((BaseActivity) mContext).moveMyProfileScreen(mContext, mMyProfileResModel.getID());
        } else {
            ((BaseActivity) mContext).moveOtherProfileScreen(mContext, mMyProfileResModel.getID(),
                    feedCommentModel.getProfiles_by_ProfileID().getID());

        }

    }

    private void setFeedCommentLikeAdapter(ArrayList<VideoCommentLikeModel> mCommentLikeEntity) {
        VideoCommentLikeAdapter mFeedLikesAdapter = new VideoCommentLikeAdapter(mContext, mCommentLikeEntity, mMyProfileResModel);
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

        RetrofitClient.getRetrofitInstance().callUnLikeForVideoComments(((BaseActivity) mActivity), profileId, RetrofitClient.VIDEO_COMMENTS_UNLIKE);

    }

    private void callLikePost(int commentId, int profileId) {

        JsonObject mJsonObject = new JsonObject();
        JsonObject mItem = new JsonObject();

        mItem.addProperty("CommentID", commentId);
        mItem.addProperty("ProfileID", profileId);

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mItem);
        mJsonObject.add("resource", mJsonArray);
        RetrofitClient.getRetrofitInstance().postLikesForVideoComments(((BaseActivity) mActivity), mJsonObject, RetrofitClient.VIDEO_COMMENTS_LIKE);

    }

    @Override
    public int getItemCount() {
        return mFeedCommentList.size();
    }

    private void setLikeUnLikeForPost(Holder mViewHolder, int position) {

        if (mFeedCommentList.get(position).getFeedCommentLikeModel().size() > 0) {

            ArrayList<VideoCommentLikeModel> mFeedLikes = (mFeedCommentList.get(position).getFeedCommentLikeModel());

            String resLikes;

            if (mFeedCommentList.get(position).getFeedCommentLikeModel().size() == 1) {

                resLikes = mFeedCommentList.get(position).getFeedCommentLikeModel().size() + " like";
            } else {
                resLikes = mFeedCommentList.get(position).getFeedCommentLikeModel().size() + " likes";

            }
            mViewHolder.mLikeCountTxt.setText(resLikes);

            for (final VideoCommentLikeModel likesEntity : mFeedLikes) {

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
