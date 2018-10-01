package online.motohub.adapter.promoter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.promoter.PromotersImgListActivity;
import online.motohub.adapter.FeedLikesAdapter;
import online.motohub.adapter.FeedSharesAdapter;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.UrlUtils;
import online.motohub.util.Utility;

public class PromoterPostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_POSTS = 1;
    private final List<PostsResModel> mPostsList;
    private Context mContext;
    private Activity mActivity;

    private Dialog mCommentListPopup;
    private int mAdapPos;
    private int mDeleteLikeID;
    private RecyclerView mFeedLikesListView;
    private ProfileResModel mCurrentProfileResModel;
    private ViewHolderPosts mViewHolderPost;
    private int mTempPosition;
    private String[] finalArr = null;

    public PromoterPostsAdapter(List<PostsResModel> postsList, ProfileResModel mMyProfileResModel, Context ctx) {
        this.mPostsList = postsList;
        this.mContext = ctx;
        this.mActivity = (Activity) ctx;
        this.mCurrentProfileResModel = mMyProfileResModel;
    }

    public void resetShareCount(FeedShareModel mSharedFeed) {
        ArrayList<FeedShareModel> mShareModel = mPostsList.get(mAdapPos).getPostShares();
        mShareModel.add(mSharedFeed);
        mPostsList.get(mAdapPos).setPostShares(mShareModel);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mPostsList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= mPostsList.size()) {
            return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_POSTS;
        }
    }

    @Override
    public long getItemId(int position) {
        return (getItemViewType(position) == VIEW_TYPE_POSTS) ? position : -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView;
        switch (viewType) {
            case VIEW_TYPE_POSTS:
                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_profile_post_item, parent, false);
                return new ViewHolderPosts(mView);
            case VIEW_TYPE_LOADING:
                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_progress_bar, parent, false);
                RecyclerView.LayoutParams mLayoutParams = (RecyclerView.LayoutParams) mView.getLayoutParams();
                mLayoutParams.width = RecyclerView.LayoutParams.MATCH_PARENT;
                mLayoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT;
                mView.setLayoutParams(mLayoutParams);
                return new ViewHolderLoader(mView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_POSTS:
                mViewHolderPost = (ViewHolderPosts) holder;
                mViewHolderPost.mPostPic.setVisibility(View.GONE);
                mViewHolderPost.playicon.setVisibility(View.GONE);
                PromotersResModel mPromotersResModel = mPostsList.get(position).getPromoterByProfileID();
                ((BaseActivity) mContext).setImageWithGlide(mViewHolderPost.mProfileImg, mPromotersResModel.getProfileImage(), R.drawable.default_profile_icon);
                mViewHolderPost.mBottomArrowImgView.setVisibility(View.GONE);
                mViewHolderPost.mUsername.setText(mPromotersResModel.getName());
                mViewHolderPost.mPostDate.setText(((BaseActivity) mContext).findTime(mPostsList.get(position).getDateCreatedAt()));
                if (mPostsList.get(position).getPostText().isEmpty()) {
                    mViewHolderPost.mPostText.setVisibility(View.GONE);
                } else {
                    mViewHolderPost.mPostText.setText(mPostsList.get(position).getPostText());
                    mViewHolderPost.mPostText.setVisibility(View.VISIBLE);
                }
                RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mViewHolderPost.mProfileDetailedLay.getLayoutParams();
                relativeParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.size65);
                relativeParams.width = ViewGroup.MarginLayoutParams.MATCH_PARENT;
                relativeParams.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.size10),
                        mContext.getResources().getDimensionPixelSize(R.dimen.size0),
                        mContext.getResources().getDimensionPixelSize(R.dimen.size10),
                        mContext.getResources().getDimensionPixelSize(R.dimen.size0));
                mViewHolderPost.mProfileDetailedLay.setLayoutParams(relativeParams);
                mViewHolderPost.mProfileDetailedLay.requestLayout();
                mViewHolderPost.mCommentViewLay.setVisibility(View.GONE);
                mViewHolderPost.mCountLay.setVisibility(View.GONE);

                /*final String[] mVideoArray = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(position).getPostVideoThumbnailURL());

                if (mVideoArray == null) {
                    mViewHolderPost.mPostImageVideoBox.setVisibility(View.GONE);
                    mViewHolderPost.playicon.setVisibility(View.GONE);
                } else if (mVideoArray.length == 1 && mVideoArray[0].trim().equals("")) {
                    mViewHolderPost.mPostImageVideoBox.setVisibility(View.GONE);
                    mViewHolderPost.playicon.setVisibility(View.GONE);
                } else if (mVideoArray.length == 1) {
                    mViewHolderPost.mPostImageVideoBox.setVisibility(View.VISIBLE);
                    mViewHolderPost.mPostPic.setVisibility(View.VISIBLE);
                    mViewHolderPost.mPostPicProgressBar.setVisibility(View.VISIBLE);
                    showVideoFile(mViewHolderPost, mVideoArray[0]);
                }

                String[] mImgArray = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(position).getPostPicture());
                if (mImgArray == null) {
                    mViewHolderPost.mMultiImgLay.setVisibility(View.GONE);
                } else if (mImgArray.length == 1 && mImgArray[0].trim().equals("")) {
                    mViewHolderPost.mMultiImgLay.setVisibility(View.GONE);
                } else if (mImgArray.length == 1) {
                    mViewHolderPost.mMultiImgLay.setVisibility(View.GONE);
                    setPostPicture(mViewHolderPost, mImgArray[0]);
                } else {
                    mViewHolderPost.mMultiImgLay.setVisibility(View.VISIBLE);
                    setImageView(mViewHolderPost, mImgArray);
                }*/


                final String[] mVideoArray = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(position).getPostVideoThumbnailURL());


                /*if (mVideoArray == null) {
                    mViewHolderPost.mPostImageVideoBox.setVisibility(View.GONE);
                    mViewHolderPost.playicon.setVisibility(View.GONE);
                } else if (mVideoArray.length == 1 && mVideoArray[0].trim().equals("")) {
                    mViewHolderPost.mPostImageVideoBox.setVisibility(View.GONE);
                    mViewHolderPost.playicon.setVisibility(View.GONE);
                } else if (mVideoArray.length == 1) {
                    mViewHolderPost.mPostImageVideoBox.setVisibility(View.VISIBLE);
                    mViewHolderPost.mPostPic.setVisibility(View.VISIBLE);
                    mViewHolderPost.mPostPicProgressBar.setVisibility(View.VISIBLE);
                    showVideoFile(mViewHolderPost, mVideoArray[0]);
                }*/


                final String[] mImgArray = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(position).getPostPicture());


                if ((mVideoArray != null && mVideoArray.length >= 1) && (mImgArray != null && mImgArray.length >= 1)) {
                    String[] c = concat(mVideoArray, mImgArray);
                    mViewHolderPost.mMultiImgLay.setVisibility(View.VISIBLE);
                    setImageView(mViewHolderPost, c);
                } else {
                    if (mVideoArray == null) {
                        mViewHolderPost.mMultiImgLay.setVisibility(View.GONE);
                        mViewHolderPost.playicon.setVisibility(View.GONE);
                    } else if (mVideoArray.length == 1 && mVideoArray[0].trim().equals("")) {
                        mViewHolderPost.mMultiImgLay.setVisibility(View.GONE);
                        mViewHolderPost.playicon.setVisibility(View.GONE);
                    } else if (mVideoArray.length == 1) {
                        mViewHolderPost.mPostImageVideoBox.setVisibility(View.VISIBLE);
                        mViewHolderPost.mPostPic.setVisibility(View.VISIBLE);
                        mViewHolderPost.mPostPicProgressBar.setVisibility(View.VISIBLE);
                        showVideoFile(mViewHolderPost, mVideoArray[0]);
                    }

                    if (mImgArray == null) {
                        mViewHolderPost.mMultiImgLay.setVisibility(View.GONE);
                    } else if (mImgArray.length == 1 && mImgArray[0].trim().equals("")) {
                        mViewHolderPost.mMultiImgLay.setVisibility(View.GONE);
                    } else if (mImgArray.length == 1) {
                        mViewHolderPost.mMultiImgLay.setVisibility(View.GONE);
                        setPostPicture(mViewHolderPost, mImgArray[0]);
                    } else {
                        mViewHolderPost.mMultiImgLay.setVisibility(View.VISIBLE);
                        setImageView(mViewHolderPost, mImgArray);
                    }
                }

                mViewHolderPost.multi_img_layout.setTag(position);
                mViewHolderPost.multi_img_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*int selPos = (int) view.getTag();
                        final String[] mImgArray = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(selPos).getPostPicture());
                        if (mImgArray != null) {
                            Intent intent = new Intent(mContext, PromotersImgListActivity.class);
                            intent.putExtra("img", mImgArray);
                            mContext.startActivity(intent);
                        }*/
                        final String[] mImgArray = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(position).getPostPicture());
                        final String[] mVideoArray = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(position).getPostVideoThumbnailURL());
                        final String[] mVideoUrls = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(position).getPostVideoURL());

                        if (mImgArray != null && mVideoArray != null) {
                            finalArr = concat(mVideoArray, mImgArray);
                        } else if (mImgArray == null && mVideoArray != null) {
                            finalArr = Arrays.copyOf(mVideoArray, mVideoArray.length);
                        } else if (mImgArray != null && mVideoArray == null) {
                            finalArr = Arrays.copyOf(mImgArray, mImgArray.length);
                        }

                        Intent intent = new Intent(mContext, PromotersImgListActivity.class);
                        intent.putExtra("img", finalArr);
                        mContext.startActivity(intent);
                    }
                });

                mViewHolderPost.mPostImageVideoBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mVideosList[] = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(position).getPostVideoURL());
                        String mImgList[] = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(position).getPostPicture());
                        if (mVideosList != null && mVideosList.length > 0) {
                            ((BaseActivity) mContext).moveLoadVideoScreen(mContext, mVideosList[0]);
                        } else if (mImgList != null && mImgList.length > 0) {
                            ((BaseActivity) mContext).moveLoadImageScreen(mContext, mImgList[0]);
                        }
                    }
                });

                if (mPostsList.get(position).getPostLikes() != null) {

                    if (mPostsList.get(position).getPostLikes().size() > 0) {

                        ArrayList<FeedLikesModel> mFeedLikes = mPostsList.get(position).getPostLikes();

                        for (final FeedLikesModel likesEntity : mFeedLikes) {

                            if ((likesEntity.getOwnerID() == mCurrentProfileResModel.getID())) {
                                mViewHolderPost.mLikeBtn.setImageResource(R.drawable.like_click_to_like_bg);
                                mViewHolderPost.mLikeBtn.setTag("unlike");
                                break;
                            } else {
                                mViewHolderPost.mLikeBtn.setImageResource(R.drawable.like_to_like_click_bg);
                                mViewHolderPost.mLikeBtn.setTag("like");
                            }
                        }
                    } else {
                        mViewHolderPost.mLikeBtn.setImageResource(R.drawable.like_to_like_click_bg);
                        mViewHolderPost.mLikeBtn.setTag("like");
                    }
                } else {
                    mViewHolderPost.mLikeBtn.setImageResource(R.drawable.like_to_like_click_bg);
                    mViewHolderPost.mLikeBtn.setTag("like");
                }

                if (mPostsList.get(position).getPostLikes() != null && mPostsList.get(position).getPostComments() != null && mPostsList.get(position).getPostShares() != null) {

                    if (mPostsList.get(position).getPostLikes().size() == 0 && mPostsList.get(position).getPostComments().size() == 0 && mPostsList.get(position).getPostShares().size() == 0) {
                        mViewHolderPost.mCountLay.setVisibility(View.GONE);
                    } else {
                        mViewHolderPost.mCountLay.setVisibility(View.VISIBLE);
                    }
                } else {
                    mViewHolderPost.mCountLay.setVisibility(View.VISIBLE);
                }

                if (mPostsList.get(position).getPostLikes() != null) {

                    if (mPostsList.get(position).getPostLikes().size() > 0) {

                        String resLikes;

                        if (mPostsList.get(position).getPostLikes().size() == 1) {

                            resLikes = String.valueOf(mPostsList.get(position).getPostLikes().size()) + " Like";
                        } else {
                            resLikes = String.valueOf(mPostsList.get(position).getPostLikes().size()) + " Likes";

                        }

                        mViewHolderPost.mLikeCountText.setText(resLikes);
                    } else {
                        mViewHolderPost.mLikeCountText.setText(" ");
                    }
                } else {
                    mViewHolderPost.mLikeCountText.setText(" ");
                }

                if (mPostsList.get(position).getPostComments() != null) {

                    if (mPostsList.get(position).getPostComments().size() > 0) {

                        String resComments;

                        if (mPostsList.get(position).getPostComments().size() == 1) {
                            resComments = mPostsList.get(position).getPostComments().size() + " Comment ";

                        } else {
                            resComments = mPostsList.get(position).getPostComments().size() + " Comments ";
                        }

                        mViewHolderPost.mCommentCountTxt.setText(resComments);
                    } else {
                        mViewHolderPost.mCommentCountTxt.setText(" ");
                    }
                } else {
                    mViewHolderPost.mCommentCountTxt.setText(" ");
                }

                String numberOfShares;


                if (mPostsList.get(position).getPostShares() != null) {

                    if (mPostsList.get(position).getPostShares().size() == 0) {
                        mViewHolderPost.mShareCountTxt.setVisibility(View.GONE);

                    } else if (mPostsList.get(position).getPostShares().size() == 1) {
                        numberOfShares = mPostsList.get(position).getPostShares().size() + " Share";
                        mViewHolderPost.mShareCountTxt.setText(numberOfShares);
                        mViewHolderPost.mShareCountTxt.setVisibility(View.VISIBLE);

                    } else {
                        numberOfShares = mPostsList.get(position).getPostShares().size() + " Shares";
                        mViewHolderPost.mShareCountTxt.setText(numberOfShares);
                        mViewHolderPost.mShareCountTxt.setVisibility(View.VISIBLE);
                    }

                }

                ArrayList<FeedCommentModel> mFeedCommentModel = mPostsList.get(position).getPostComments();

                if (mFeedCommentModel != null) {

                    showPostCommentsLay(mViewHolderPost, mFeedCommentModel, position);
                }

                mViewHolderPost.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!((BaseActivity) mContext).isMultiClicked()) {
                            mAdapPos = position;

                            switch (view.getTag().toString()) {

                                case "unlike":

                                    ArrayList<FeedLikesModel> mFeedLikeList = mPostsList.get(position).getPostLikes();

                                    if (mFeedLikeList.size() > 0) {

                                        for (FeedLikesModel likesEntity : mFeedLikeList) {
                                            if (likesEntity.getOwnerID() == mCurrentProfileResModel.getID() && likesEntity.getFeedID() == mPostsList.get(position).getID()) {
                                                mDeleteLikeID = likesEntity.getId();
                                                break;
                                            }

                                        }
                                        String mFilter = "ID=" + mDeleteLikeID;
                                        callUnLikePost(mFilter);
                                    }

                                    break;

                                case "like":
                                    callLikePost(mPostsList.get(position).getID(), mCurrentProfileResModel.getID());
                                    break;
                            }
                        }
                    }
                });

                mViewHolderPost.mCommentViewLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mTempPosition = position;
                        ((BaseActivity) mContext).movePostCommentScreen(mContext, mPostsList.get(mTempPosition).getID(), mCurrentProfileResModel);
                    }
                });

                mViewHolderPost.mCommentCountTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mTempPosition = position;
                        ((BaseActivity) mContext).movePostCommentScreen(mContext, mPostsList.get(mTempPosition).getID(), mCurrentProfileResModel);
                    }
                });

                mViewHolderPost.mCommentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mTempPosition = position;
                        ((BaseActivity) mContext).movePostCommentScreen(mContext, mPostsList.get(mTempPosition).getID(), mCurrentProfileResModel);
                    }
                });

                mViewHolderPost.mLikeCountText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<FeedLikesModel> mLikeModel;
                        if (mPostsList.get(position).getPostLikes() != null) {
                            mLikeModel = mPostsList.get(position).getPostLikes();
                        } else {
                            mLikeModel = new ArrayList<>();
                        }
                        showLikeListPopup(mContext.getString(R.string.likes));
                        setFeedLikeAdapter(mLikeModel);

                    }
                });

                mViewHolderPost.mShareCountTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ArrayList<FeedShareModel> mShareModel;
                        if (mPostsList.get(position).getPostShares() != null) {
                            mShareModel = mPostsList.get(position).getPostShares();
                        } else {
                            mShareModel = new ArrayList<>();
                        }
                        showLikeListPopup(mContext.getString(R.string.shares));
                        setFeedShareAdapter(mShareModel);

                    }
                });

                mViewHolderPost.mShareBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String content = mPostsList.get(position).getPostText();

                        String[] mImgList = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(position).getPostPicture());

                        boolean mIsOtherMotoProfile;

                        mIsOtherMotoProfile = mPostsList.get(position).getProfileID() != mCurrentProfileResModel.getID();
                        String mVideosList[] = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(position).getPostVideoURL());
                        if (mVideosList != null && mVideosList.length > 0) {

                            ((BaseActivity) mContext).showFBShareDialog(AppDialogFragment.BOTTOM_SHARE_DIALOG, content, null, mVideosList, position, mIsOtherMotoProfile);

                        } else if (mImgList != null) {

                            ArrayList<Bitmap> mBitmapList = ((BaseActivity) mContext).getBitmapImageGlide(mImgList);

                            if (mBitmapList != null) {
                                ((BaseActivity) mContext).showFBShareDialog(AppDialogFragment.BOTTOM_SHARE_DIALOG, content, mBitmapList, null, position, mIsOtherMotoProfile);
                            }

                        } else {

                            ((BaseActivity) mContext).showFBShareDialog(AppDialogFragment.BOTTOM_SHARE_DIALOG, content, null, null, position, mIsOtherMotoProfile);
                        }
                    }
                });

                break;
            case VIEW_TYPE_LOADING:
                final ViewHolderLoader mViewHolderLoader = (ViewHolderLoader) holder;
                if (mPostsList.size() != ((TotalRetrofitPostsResultCount) mContext).getTotalPostsResultCount()) {
                    mViewHolderLoader.mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mViewHolderLoader.mProgressBar.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    public void refreshCommentList(ArrayList<FeedCommentModel> mCommentList) {
        Collections.reverse(mCommentList);
        mPostsList.get(mTempPosition).setFeedComments(mCommentList);
        notifyDataSetChanged();
    }

    private void setPostPicture(final ViewHolderPosts mViewHolderPost, String
            imgUrl) {

        mViewHolderPost.mPostImageVideoBox.setVisibility(View.VISIBLE);
        mViewHolderPost.playicon.setVisibility(View.GONE);

        mViewHolderPost.mPostPic.setVisibility(View.VISIBLE);
        mViewHolderPost.mPostPicProgressBar.setVisibility(View.VISIBLE);

        GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + imgUrl, new LazyHeaders.Builder()
                .addHeader("X-DreamFactory-Api-Key", mContext.getString(R.string.dream_factory_api_key))
                .build());

        Glide.with(mContext)
                .load(glideUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        mViewHolderPost.mPostPicProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mViewHolderPost.mPostPicProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .apply(new RequestOptions()
                        .dontAnimate()
                        .dontAnimate())
                .into(mViewHolderPost.mPostPic);

    }

    private void showVideoFile(final ViewHolderPosts mViewHolderPost, String videoThumbnail) {

        mViewHolderPost.mPostImageVideoBox.setVisibility(View.VISIBLE);
        mViewHolderPost.mPostPic.setVisibility(View.VISIBLE);
        mViewHolderPost.mPostPicProgressBar.setVisibility(View.VISIBLE);

        GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + videoThumbnail, new LazyHeaders
                .Builder()
                .addHeader("X-DreamFactory-Api-Key", mContext.getString(R.string.dream_factory_api_key))
                .build());

        Glide.with(mContext)
                .load(glideUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        mViewHolderPost.mPostPicProgressBar.setVisibility(View.GONE);
                        mViewHolderPost.playicon.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mViewHolderPost.mPostPicProgressBar.setVisibility(View.GONE);
                        mViewHolderPost.playicon.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .apply(new RequestOptions()
                        .dontAnimate())
                .into(mViewHolderPost.mPostPic);
    }


    /*private void setImageView(ViewHolderPosts mHolder, String[] mArray) {
        int length = mArray.length;
        switch (length) {
            case 1:
                mHolder.mTopLay.setVisibility(View.VISIBLE);
                mHolder.mImgLay1.setVisibility(View.VISIBLE);
                mHolder.mImgLay2.setVisibility(View.GONE);
                mHolder.mBottomLay.setVisibility(View.GONE);
                setGlideImage(mHolder.mImg1, mArray[0], R.drawable.default_cover_img);
                break;
            case 2:
                mHolder.mTopLay.setVisibility(View.VISIBLE);
                mHolder.mImgLay1.setVisibility(View.VISIBLE);
                mHolder.mImgLay2.setVisibility(View.VISIBLE);
                mHolder.mBottomLay.setVisibility(View.GONE);
                setGlideImage(mHolder.mImg1, mArray[0], R.drawable.default_cover_img);
                setGlideImage(mHolder.mImg2, mArray[1], R.drawable.default_cover_img);
                break;
            case 3:
                mHolder.mTopLay.setVisibility(View.VISIBLE);
                mHolder.mImgLay1.setVisibility(View.VISIBLE);
                mHolder.mImgLay2.setVisibility(View.VISIBLE);
                mHolder.mBottomLay.setVisibility(View.VISIBLE);
                mHolder.mImgLay3.setVisibility(View.VISIBLE);
                mHolder.mImgLay4.setVisibility(View.GONE);
                setGlideImage(mHolder.mImg1, mArray[0], R.drawable.default_cover_img);
                setGlideImage(mHolder.mImg2, mArray[1], R.drawable.default_cover_img);
                setGlideImage(mHolder.mImg3, mArray[2], R.drawable.default_cover_img);
                break;
            case 4:
                mHolder.mTopLay.setVisibility(View.VISIBLE);
                mHolder.mImgLay1.setVisibility(View.VISIBLE);
                mHolder.mImgLay2.setVisibility(View.VISIBLE);
                mHolder.mBottomLay.setVisibility(View.VISIBLE);
                mHolder.mImgLay3.setVisibility(View.VISIBLE);
                mHolder.mImgLay4.setVisibility(View.VISIBLE);
                mHolder.mMoreLay.setVisibility(View.GONE);
                setGlideImage(mHolder.mImg1, mArray[0], R.drawable.default_cover_img);
                setGlideImage(mHolder.mImg2, mArray[1], R.drawable.default_cover_img);
                setGlideImage(mHolder.mImg3, mArray[2], R.drawable.default_cover_img);
                setGlideImage(mHolder.mImg4, mArray[3], R.drawable.default_cover_img);
                break;
            default:
                mHolder.mTopLay.setVisibility(View.VISIBLE);
                mHolder.mImgLay1.setVisibility(View.VISIBLE);
                mHolder.mImgLay2.setVisibility(View.VISIBLE);
                mHolder.mBottomLay.setVisibility(View.VISIBLE);
                mHolder.mImgLay3.setVisibility(View.VISIBLE);
                mHolder.mImgLay4.setVisibility(View.VISIBLE);
                mHolder.mMoreLay.setVisibility(View.VISIBLE);
                String mCnt = "+" + (mArray.length - 4) + " More";
                mHolder.mTotalImgCountTv.setText(mCnt);
                setGlideImage(mHolder.mImg1, mArray[0], R.drawable.default_cover_img);
                setGlideImage(mHolder.mImg2, mArray[1], R.drawable.default_cover_img);
                setGlideImage(mHolder.mImg3, mArray[2], R.drawable.default_cover_img);
                setGlideImage(mHolder.mImg4, mArray[3], R.drawable.default_cover_img);
                break;
        }
    }*/
    private void setImageView(ViewHolderPosts mHolder, String[] mArray) {
        int length = mArray.length;
        switch (length) {
            case 1:

                mHolder.mTopLay.setVisibility(View.VISIBLE);
                mHolder.mImgLay1.setVisibility(View.VISIBLE);
                mHolder.mImgLay2.setVisibility(View.GONE);
                mHolder.mBottomLay.setVisibility(View.GONE);
                mHolder.playicon1.setVisibility(View.GONE);
                /*setGlideImage(mHolder.mImg1, mArray[0], R.drawable.default_cover_img);*/
                if (!mArray[0].contains("Video")) {
                    mHolder.playicon1.setVisibility(View.GONE);
                    setGlideImage(mHolder.mImg1, mArray[0], R.drawable.default_cover_img);
                } else {
                    mHolder.playicon1.setVisibility(View.VISIBLE);
                    setGlideImage(mHolder.mImg1, mArray[0], R.drawable.default_cover_img);
                }

                break;
            case 2:

                mHolder.mTopLay.setVisibility(View.VISIBLE);
                mHolder.mImgLay1.setVisibility(View.VISIBLE);
                mHolder.mImgLay2.setVisibility(View.VISIBLE);
                mHolder.playicon2.setVisibility(View.GONE);
                mHolder.mBottomLay.setVisibility(View.GONE);
                /*setGlideImage(mHolder.mImg1, mArray[0], R.drawable.default_cover_img);
                setGlideImage(mHolder.mImg2, mArray[1], R.drawable.default_cover_img);*/
                if (!mArray[0].contains("Video")) {
                    mHolder.playicon1.setVisibility(View.GONE);
                    setGlideImage(mHolder.mImg1, mArray[0], R.drawable.default_cover_img);
                } else {
                    mHolder.playicon1.setVisibility(View.VISIBLE);
                    setGlideImage(mHolder.mImg1, mArray[0], R.drawable.default_cover_img);
                }

                if (!mArray[1].contains("Video")) {
                    mHolder.playicon2.setVisibility(View.GONE);
                    setGlideImage(mHolder.mImg2, mArray[1], R.drawable.default_cover_img);
                } else {
                    mHolder.playicon2.setVisibility(View.VISIBLE);
                    setGlideImage(mHolder.mImg2, mArray[1], R.drawable.default_cover_img);
                }

                break;
            case 3:

                mHolder.mTopLay.setVisibility(View.VISIBLE);
                mHolder.mImgLay1.setVisibility(View.VISIBLE);
                mHolder.mImgLay2.setVisibility(View.VISIBLE);
                mHolder.mBottomLay.setVisibility(View.VISIBLE);
                mHolder.mImgLay3.setVisibility(View.VISIBLE);
                mHolder.playicon3.setVisibility(View.GONE);
                mHolder.mImgLay4.setVisibility(View.GONE);
                /*setGlideImage(mHolder.mImg1, mArray[0], R.drawable.default_cover_img);
                setGlideImage(mHolder.mImg2, mArray[1], R.drawable.default_cover_img);
                setGlideImage(mHolder.mImg3, mArray[2], R.drawable.default_cover_img);*/
                if (!mArray[0].contains("Video")) {
                    mHolder.playicon1.setVisibility(View.GONE);
                    setGlideImage(mHolder.mImg1, mArray[0], R.drawable.default_cover_img);
                } else {
                    mHolder.playicon1.setVisibility(View.VISIBLE);
                    setGlideImage(mHolder.mImg1, mArray[0], R.drawable.default_cover_img);
                }

                if (!mArray[1].contains("Video")) {
                    mHolder.playicon2.setVisibility(View.GONE);
                    setGlideImage(mHolder.mImg2, mArray[1], R.drawable.default_cover_img);
                } else {
                    mHolder.playicon2.setVisibility(View.VISIBLE);
                    setGlideImage(mHolder.mImg2, mArray[1], R.drawable.default_cover_img);
                }

                if (!mArray[2].contains("Video")) {
                    mHolder.playicon3.setVisibility(View.GONE);
                    setGlideImage(mHolder.mImg3, mArray[2], R.drawable.default_cover_img);
                } else {
                    mHolder.playicon3.setVisibility(View.VISIBLE);
                    setGlideImage(mHolder.mImg3, mArray[2], R.drawable.default_cover_img);
                }

                break;
            case 4:
                mHolder.mTopLay.setVisibility(View.VISIBLE);
                mHolder.mImgLay1.setVisibility(View.VISIBLE);
                mHolder.mImgLay2.setVisibility(View.VISIBLE);
                mHolder.mBottomLay.setVisibility(View.VISIBLE);
                mHolder.mImgLay3.setVisibility(View.VISIBLE);
                mHolder.mImgLay4.setVisibility(View.VISIBLE);
                mHolder.mMoreLay.setVisibility(View.GONE);
                if (!mArray[0].contains("Video")) {
                    mHolder.playicon1.setVisibility(View.GONE);
                    setGlideImage(mHolder.mImg1, mArray[0], R.drawable.default_cover_img);
                } else {
                    mHolder.playicon1.setVisibility(View.VISIBLE);
                    setGlideImage(mHolder.mImg1, mArray[0], R.drawable.default_cover_img);
                }

                if (!mArray[1].contains("Video")) {
                    mHolder.playicon2.setVisibility(View.GONE);
                    setGlideImage(mHolder.mImg2, mArray[1], R.drawable.default_cover_img);
                } else {
                    mHolder.playicon2.setVisibility(View.VISIBLE);
                    setGlideImage(mHolder.mImg2, mArray[1], R.drawable.default_cover_img);
                }

                if (!mArray[2].contains("Video")) {
                    mHolder.playicon3.setVisibility(View.GONE);
                    setGlideImage(mHolder.mImg3, mArray[2], R.drawable.default_cover_img);
                } else {
                    mHolder.playicon3.setVisibility(View.VISIBLE);
                    setGlideImage(mHolder.mImg3, mArray[2], R.drawable.default_cover_img);
                }

                if (!mArray[3].contains("Video")) {
                    mHolder.playicon4.setVisibility(View.GONE);
                    setGlideImage(mHolder.mImg4, mArray[3], R.drawable.default_cover_img);
                } else {
                    mHolder.playicon4.setVisibility(View.VISIBLE);
                    setGlideImage(mHolder.mImg4, mArray[3], R.drawable.default_cover_img);
                }

                break;
            default:
                mHolder.mTopLay.setVisibility(View.VISIBLE);
                mHolder.mImgLay1.setVisibility(View.VISIBLE);
                mHolder.mImgLay2.setVisibility(View.VISIBLE);
                mHolder.mBottomLay.setVisibility(View.VISIBLE);
                mHolder.mImgLay3.setVisibility(View.VISIBLE);
                mHolder.mImgLay4.setVisibility(View.VISIBLE);
                mHolder.mMoreLay.setVisibility(View.VISIBLE);
                String mCnt = "+" + (mArray.length - 4) + " More";
                mHolder.mTotalImgCountTv.setText(mCnt);
                setGlideImage(mHolder.mImg1, mArray[0], R.drawable.default_cover_img);
                setGlideImage(mHolder.mImg2, mArray[1], R.drawable.default_cover_img);
                setGlideImage(mHolder.mImg3, mArray[2], R.drawable.default_cover_img);
                setGlideImage(mHolder.mImg4, mArray[3], R.drawable.default_cover_img);
                break;
        }
    }

    private void setGlideImage(ImageView imgView, String imgUrl, int drawable) {
        ((BaseActivity) mContext).setImageWithGlide(imgView, imgUrl, drawable);
    }

    public void resetCommentAdapter(FeedCommentModel feedCommentModel) {

        ArrayList<FeedCommentModel> mCommentList = mPostsList.get(mAdapPos).getPostComments();
        mCommentList.add(feedCommentModel);
        mPostsList.get(mAdapPos).setFeedComments(mCommentList);
        notifyDataSetChanged();

    }

    public void resetLikeAdapter(FeedLikesModel feedLikesModel) {

        ArrayList<FeedLikesModel> mFeedLikesList = mPostsList.get(mAdapPos).getPostLikes();
        mFeedLikesList.add(feedLikesModel);
        mPostsList.get(mAdapPos).setPostLikes(mFeedLikesList);
        notifyDataSetChanged();

    }

    public void resetDisLike(FeedLikesModel feedLikesModel) {
        ArrayList<FeedLikesModel> mFeedLikesList = mPostsList.get(mAdapPos).getPostLikes();
        Iterator<FeedLikesModel> iterator = mFeedLikesList.iterator();
        while (iterator.hasNext()) {
            FeedLikesModel next = iterator.next();
            if (next.getId() == (feedLikesModel.getId())) {
                iterator.remove();
                mFeedLikesList.remove(next);
            }
        }

        mPostsList.get(mAdapPos).setPostLikes(mFeedLikesList);
        notifyDataSetChanged();
    }

    private void callUnLikePost(String mFilter) {

        RetrofitClient.getRetrofitInstance().callUnLikeForPosts(((BaseActivity) mActivity), mFilter, RetrofitClient.POST_UNLIKE);

    }

    private void callLikePost(int postId, int profileId) {

        JsonObject mJsonObject = new JsonObject();
        JsonObject mItem = new JsonObject();

        mItem.addProperty("PostID", postId);
        mItem.addProperty("ProfileID", profileId);

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mItem);
        mJsonObject.add("resource", mJsonArray);
        RetrofitClient.getRetrofitInstance().postLikesForPosts(((BaseActivity) mActivity), mJsonObject, RetrofitClient.POST_LIKES);
    }

    private void setFeedLikeAdapter(ArrayList<FeedLikesModel> mFeedLikeList) {
        FeedLikesAdapter mFeedLikesAdapter = new FeedLikesAdapter(mContext, mFeedLikeList, mCurrentProfileResModel);
        mFeedLikesListView.setAdapter(mFeedLikesAdapter);
        mFeedLikesListView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void setFeedShareAdapter(ArrayList<FeedShareModel> mFeedShareList) {
        FeedSharesAdapter mFeedShareAdapter = new FeedSharesAdapter(mContext, mFeedShareList, mCurrentProfileResModel);
        mFeedLikesListView.setAdapter(mFeedShareAdapter);
        mFeedLikesListView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void showLikeListPopup(String title) {

        if (mCommentListPopup != null && mCommentListPopup.isShowing()) {
            mCommentListPopup.dismiss();
        }

        // Create custom dialog object
        mCommentListPopup = new Dialog(this.mContext, R.style.MyDialogBottomSheet);
        mCommentListPopup.setContentView(R.layout.popup_feed_like_list);

        ImageView mCloseIcon = mCommentListPopup.findViewById(R.id.close_btn);
        TextView mTitleTxt = mCommentListPopup.findViewById(R.id.title_txt);
        mFeedLikesListView = mCommentListPopup.findViewById(R.id.feeds_likes_list_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mFeedLikesListView.setLayoutManager(mLayoutManager);
        mFeedLikesListView.setItemAnimator(new DefaultItemAnimator());

        mTitleTxt.setText(title);


        mCloseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentListPopup.dismiss();
            }
        });

        mCommentListPopup.show();

    }

    private void showPostCommentsLay(ViewHolderPosts mViewHolderPost, ArrayList<FeedCommentModel> mFeedCommentModel, int position) {
        if (mFeedCommentModel.size() > 0) {

            mViewHolderPost.mCommentViewLay.setVisibility(View.VISIBLE);

            int latestPostion = mFeedCommentModel.size() - 1;

            String mCommentText = mFeedCommentModel.get(latestPostion).getmComment().trim();
            String mCommentImgUrl = mFeedCommentModel.get(latestPostion).getCommentImages().trim();

            if (!mCommentImgUrl.isEmpty()) {
                mViewHolderPost.mCommentTxt.setVisibility(View.VISIBLE);
                mViewHolderPost.mCommentTxt.setText("Commented to this post.");
            } else if (!mCommentText.isEmpty()) {
                try {
                    mViewHolderPost.mCommentTxt.setText(URLDecoder.decode(mCommentText, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                mViewHolderPost.mCommentTxt.setVisibility(View.VISIBLE);
            } else {
                mViewHolderPost.mCommentTxt.setVisibility(View.GONE);
            }
            mViewHolderPost.mCommentUserName.setText(Utility.getInstance().getUserName(mFeedCommentModel.get(latestPostion).getProfiles_by_ProfileID()));

            ((BaseActivity) mContext).setImageWithGlide(mViewHolderPost.mCommentImg, mFeedCommentModel.get(latestPostion).getProfiles_by_ProfileID().getProfilePicture(), R.drawable.default_profile_icon);

            mViewHolderPost.mCommentImg.setTag(position);
        } else {
            mViewHolderPost.mCommentViewLay.setVisibility(View.GONE);
        }
    }


    public interface TotalRetrofitPostsResultCount {
        int getTotalPostsResultCount();
    }

    class ViewHolderPosts extends RecyclerView.ViewHolder {

        @BindView(R.id.top_lay)
        LinearLayout mTopLay;
        @BindView(R.id.bottom_lay)
        LinearLayout mBottomLay;
        @BindView(R.id.multi_img)
        LinearLayout multi_img_layout;
        @BindView(R.id.more_lay)
        LinearLayout mMoreLay;
        @BindView(R.id.img1_lay)
        CardView mImgLay1;
        @BindView(R.id.img2_lay)
        CardView mImgLay2;
        @BindView(R.id.img3_lay)
        CardView mImgLay3;
        @BindView(R.id.img4_lay)
        CardView mImgLay4;
        @BindView(R.id.img1)
        ImageView mImg1;
        @BindView(R.id.img2)
        ImageView mImg2;
        @BindView(R.id.img3)
        ImageView mImg3;
        @BindView(R.id.img4)
        ImageView mImg4;

        @BindView(R.id.playicon1)
        ImageView playicon1;
        @BindView(R.id.playicon2)
        ImageView playicon2;
        @BindView(R.id.playicon3)
        ImageView playicon3;
        @BindView(R.id.playicon4)
        ImageView playicon4;

        private CircleImageView mProfileImg;
        private TextView mUsername;
        private TextView mPostDate;
        private TextView mPostText;
        private ImageView mPostPic;
        private ImageView mBottomArrowImgView;
        private RelativeLayout mProfileDetailedLay;
        private RelativeLayout mMultiImgLay;
        private TextView mTotalImgCountTv;
        private ImageView mLikeBtn;
        private ImageView mCommentBtn;
        private ImageView mShareBtn;
        private ImageView mCommentImg;
        private TextView mCommentUserName;
        private TextView mCommentTxt;
        private RelativeLayout mCommentViewLay;
        private TextView mShareCountTxt;
        private RelativeLayout mCountLay;
        private TextView mLikeCountText;
        private TextView mCommentCountTxt;
        private RelativeLayout mPostImageVideoBox;
        private ImageView playicon;
        private ProgressBar mPostPicProgressBar;

        ViewHolderPosts(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mProfileImg = view.findViewById(R.id.circular_img_view);
            mUsername = view.findViewById(R.id.top_tv);
            mPostDate = view.findViewById(R.id.bottom_tv);
            mPostText = view.findViewById(R.id.post_text_tv);
            mPostPic = view.findViewById(R.id.post_picture_img_view);
            mBottomArrowImgView = view.findViewById(R.id.down_arrow);
            mProfileDetailedLay = view.findViewById(R.id.profile_details_box);
            mCommentViewLay = view.findViewById(R.id.comment_view_lay);
            mCountLay = view.findViewById(R.id.rl_count);
            mMultiImgLay = view.findViewById(R.id.multi_img_lay);
            mTotalImgCountTv = view.findViewById(R.id.img_count_txt);

            mCommentImg = view.findViewById(R.id.comment_img);
            mCommentUserName = view.findViewById(R.id.comment_user_name_txt);
            mCommentTxt = view.findViewById(R.id.comment_txt);
            mLikeCountText = view.findViewById(R.id.like_count_txt);
            mCommentViewLay = view.findViewById(R.id.comment_view_lay);
            mCountLay = view.findViewById(R.id.rl_count);
            mCommentCountTxt = view.findViewById(R.id.comments_comment_txt);
            mShareCountTxt = view.findViewById(R.id.share_count_txt);
            mProfileDetailedLay = view.findViewById(R.id.profile_details_box);
            mLikeBtn = view.findViewById(R.id.likeBtn);
            mCommentBtn = view.findViewById(R.id.commentBtn);
            mShareBtn = view.findViewById(R.id.shareBtn);

            mPostImageVideoBox = view.findViewById(R.id.postImageVideoBox);
            playicon = view.findViewById(R.id.playicon);
            mPostPicProgressBar = view.findViewById(R.id.smallProgressBar);
        }

    }

    private class ViewHolderLoader extends RecyclerView.ViewHolder {

        ProgressBar mProgressBar;

        ViewHolderLoader(View view) {
            super(view);
            mProgressBar = view.findViewById(R.id.myProgressBar);
        }

    }

    public String[] concat(String[]... arrays) {
        int length = 0;
        for (String[] array : arrays) {
            length += array.length;
        }
        String[] result = new String[length];
        int destPos = 0;
        for (String[] array : arrays) {
            System.arraycopy(array, 0, result, destPos, array.length);
            destPos += array.length;
        }
        return result;
    }

}
