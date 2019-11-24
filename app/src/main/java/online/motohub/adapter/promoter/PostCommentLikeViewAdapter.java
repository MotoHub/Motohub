package online.motohub.adapter.promoter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
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
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.MyMotoFileActivity;
import online.motohub.activity.TaggedProfilesListActivity;
import online.motohub.activity.club.ClubProfileActivity;
import online.motohub.activity.news_and_media.NewsAndMediaProfileActivity;
import online.motohub.activity.performance_shop.PerformanceShopProfileActivity;
import online.motohub.activity.promoter.PromoterProfileActivity;
import online.motohub.activity.promoter.PromotersImgListActivity;
import online.motohub.activity.track.TrackProfileActivity;
import online.motohub.adapter.FeedLikesAdapter;
import online.motohub.adapter.FeedSharesAdapter;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.VideoShareModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.UrlUtils;
import online.motohub.util.Utility;


public class PostCommentLikeViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_POSTS = 1;
    private final List<PostsResModel> mPostsList;
    private ArrayList<ProfileResModel> mMyFullMPList;
    private Context mContext;
    private boolean mIsFromOthersMotoProfile;
    private ArrayList<ProfileResModel> mMyProfileResModel;
    private Activity mActivity;
    private Dialog mCommentListPopup;
    private int mTempPosition;
    private int mDeleteLikeID;
    private RecyclerView mFeedLikesListView;
    private ProfileResModel mCurrentProfileResModel;
    private StringBuffer sb = new StringBuffer();

    public PostCommentLikeViewAdapter(List<PostsResModel> postsList, ArrayList<ProfileResModel> myfullMPList, ArrayList<ProfileResModel> myProfileResModel, Context ctx, boolean isFromOthersMotoProfile) {
        this.mPostsList = postsList;
        this.mMyFullMPList = myfullMPList;
        this.mIsFromOthersMotoProfile = isFromOthersMotoProfile;
        this.mMyProfileResModel = myProfileResModel;
        this.mContext = ctx;
        this.mActivity = (Activity) ctx;
    }

    public static String replacer(StringBuffer outBuffer) {

        String data = outBuffer.toString();
        try {
            StringBuffer tempBuffer = new StringBuffer();
            int incrementor = 0;
            int dataLength = data.length();
            while (incrementor < dataLength) {
                char charecterAt = data.charAt(incrementor);
                if (charecterAt == '%') {
                    tempBuffer.append("<percentage>");
                } else if (charecterAt == '+') {
                    tempBuffer.append("<plus>");
                } else {
                    tempBuffer.append(charecterAt);
                }
                incrementor++;
            }
            data = tempBuffer.toString();
            data = URLDecoder.decode(data, "utf-8");
            data = data.replaceAll("<percentage>", "%");
            data = data.replaceAll("<plus>", "+");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        switch (getItemViewType(position)) {

            case VIEW_TYPE_POSTS:

                final ViewHolderPosts mViewHolderPost = (ViewHolderPosts) holder;
                try {

                    String mUsernameStr;

                    String mPostOwnerName = "";

                    mViewHolderPost.playicon.setVisibility(View.GONE);
                    mViewHolderPost.mPostPic.setVisibility(View.GONE);
                    mViewHolderPost.mPostPicProgressBar.setVisibility(View.GONE);
                    mViewHolderPost.mPostImageVideoBox.setVisibility(View.GONE);
                    mViewHolderPost.mCommentViewLay.setVisibility(View.GONE);
                    mViewHolderPost.mBottomArrowImgView.setVisibility(View.GONE);
                    mViewHolderPost.mSharedBottomArrow.setVisibility(View.GONE);


                    mViewHolderPost.mPostDate.setText(((BaseActivity) mContext).findTime(mPostsList.get(position).getDateCreatedAt()));


                    if (mIsFromOthersMotoProfile) {
                        mCurrentProfileResModel = mMyProfileResModel.get(0);
                    } else {
                        mCurrentProfileResModel = mMyFullMPList.get(((BaseActivity) mContext).getProfileCurrentPos());
                    }

                    if (mPostsList.get(position).getUserType() == null
                            || mPostsList.get(position).getUserType().isEmpty()
                            || mPostsList.get(position).getUserType().trim().equals("user") || mPostsList.get(position).getUserType().trim().equals("club_user")) {
                        ProfileResModel mProfileResModel = mPostsList.get(position).getProfilesByWhoPostedProfileID();

                        mUsernameStr = Utility.getInstance().getUserName(mProfileResModel);
                        mPostOwnerName = mUsernameStr;

                        if (mPostsList.get(position).getTaggedProfileID() != null && !mPostsList.get(position).getTaggedProfileID().isEmpty()) {
                            setUserName(mViewHolderPost, position, mUsernameStr);
                        } else {
                            mViewHolderPost.mUsername.setText(mUsernameStr);
                        }

                        ((BaseActivity) mContext).setImageWithGlide(mViewHolderPost.mProfileImg, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);

                    } else if (mPostsList.get(position).getUserType().trim().equals(AppConstants.SHARED_POST)
                            || mPostsList.get(position).getUserType().trim().equals(AppConstants.VIDEO_SHARED_POST)
                        /*|| mPostsList.get(position).getUserType().trim().equals(AppConstants.SHARED_VIDEO)*/) {

                        if (mPostsList.get(position).getPromoterByWhoPostedProfileID() != null) {
                            PromotersResModel mPromotersResModel = mPostsList.get(position).getPromoterByWhoPostedProfileID();

                            mUsernameStr = (mPromotersResModel.getName());
                            mPostOwnerName = mUsernameStr;
                            mViewHolderPost.mUsername.setText(mUsernameStr);
                            ((BaseActivity) mContext).setImageWithGlide(mViewHolderPost.mProfileImg, mPromotersResModel.getProfileImage(), R.drawable.default_profile_icon);
                        } else {
                            ProfileResModel mProfileResModel = mPostsList.get(position).getProfilesByWhoPostedProfileID();

                            mUsernameStr = Utility.getInstance().getUserName(mProfileResModel);
                            mPostOwnerName = mUsernameStr;
                            if (mPostsList.get(position).getTaggedProfileID() != null && !mPostsList.get(position).getTaggedProfileID().isEmpty()) {
                                setUserName(mViewHolderPost, position, mUsernameStr);
                            } else {
                                mViewHolderPost.mUsername.setText(mUsernameStr);
                            }

                            ((BaseActivity) mContext).setImageWithGlide(mViewHolderPost.mProfileImg, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);
                        }

                    } else if (mPostsList.get(position).getUserType().trim().equals(AppConstants.PROMOTER) || mPostsList.get(position).getUserType().trim().equals(AppConstants.CLUB) || mPostsList.get(position).getUserType().trim().equals(AppConstants.NEWS_MEDIA) || mPostsList.get(position).getUserType().trim().equals(AppConstants.TRACK)) {

                        PromotersResModel mPromotersProfilesModel = mPostsList.get(position).getPromoterByProfileID();

                        mUsernameStr = mPromotersProfilesModel.getName();
                        mPostOwnerName = mUsernameStr;
                        mViewHolderPost.mUsername.setText(mUsernameStr);
                        ((BaseActivity) mContext).setImageWithGlide(mViewHolderPost.mProfileImg, mPromotersProfilesModel.getProfileImage(), R.drawable.default_profile_icon);
                    }

                    mViewHolderPost.mProfileImg.setTag(position);
                    mViewHolderPost.mProfileImg.setOnClickListener(this);
                    mViewHolderPost.mUsername.setTag(position);
                    mViewHolderPost.mUsername.setOnClickListener(this);

                    final String[] mVideoArray = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(position).getPostVideoThumbnailURL());

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

                    final String[] mImgArray = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(position).getPostPicture());

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

                    mViewHolderPost.multi_img_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mImgArray != null) {
                                Intent intent = new Intent(mContext, PromotersImgListActivity.class);
                                intent.putExtra("img", mImgArray);
                                mContext.startActivity(intent);
                            }
                        }
                    });

                    if (mPostsList.get(position).getPostText().isEmpty()) {
                        mViewHolderPost.mPostText.setVisibility(View.GONE);
                    } else {

                        try {
                            mViewHolderPost.mPostText.setText(URLDecoder.decode(mPostsList.get(position).getPostText(), "UTF-8"));
                            //mViewHolderPost.mPostText.setText(replacer(sb.append(mPostsList.get(position).getPostText())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mViewHolderPost.mPostText.setVisibility(View.VISIBLE);

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
                        setLikeUnLikeForPost(mViewHolderPost, position);
                    } else {
                        mViewHolderPost.mLikeBtn.setImageResource(R.drawable.like_icon);
                        mViewHolderPost.mLikeBtn.setTag("like");
                        mViewHolderPost.mLikeCountText.setText("");
                    }

                    if (mPostsList.get(position).getPostComments() != null) {
                        setPostComments(mViewHolderPost, position);
                    } else {
                        mViewHolderPost.mCommentCountTxt.setText(" ");
                    }

                    //share
                    if (mPostsList.get(position).getNewSharedPostID().trim().isEmpty()) {
                        HideShareLayout(mViewHolderPost);
                    } else {
                        showShareLayout(mViewHolderPost, position, mPostOwnerName);
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
                    } else {
                        mViewHolderPost.mShareCountTxt.setVisibility(View.GONE);
                    }


                    mViewHolderPost.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!((BaseActivity) mContext).isMultiClicked()) {
                                mTempPosition = position;

                                switch (view.getTag().toString()) {
                                    case "unlike":

                                        ArrayList<FeedLikesModel> mFeedLikeList = mPostsList.get(position).getPostLikes();

                                        if (mFeedLikeList.size() > 0) {

                                            for (FeedLikesModel likesEntity : mFeedLikeList) {
                                                if (likesEntity.getProfileID() == mCurrentProfileResModel.getID() && likesEntity.getFeedID() == mPostsList.get(position).getID()) {
                                                    mDeleteLikeID = likesEntity.getId();
                                                    break;
                                                }

                                            }
                                            String mFilter = "ID=" + mDeleteLikeID;
                                            callUnLikePost(mFilter);
                                        }
                                        String mFilter = "(" + PostsModel.POST_ID + "=" + mPostsList.get(position).getID() + ") AND (" + PostsModel.PROFILE_ID + "=" + mCurrentProfileResModel.getID() + ")";
                                        callUnLikePost(mFilter);
                                        break;

                                    case "like":

                                        callLikePost(mPostsList.get(position).getID(), mCurrentProfileResModel.getID());

                                        break;
                                }
                            }
                        }
                    });


                    mViewHolderPost.mLikeCountText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ArrayList<FeedLikesModel> mLikeModel;
                            mTempPosition = position;
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
                            mTempPosition = position;
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

                            mTempPosition = position;

                            String content = null;
                            try {
                                content = URLDecoder.decode(mPostsList.get(position).getPostText(), "UTF-8");
                                //content = replacer(sb.append(mPostsList.get(position).getPostText()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            boolean isImgFile = false;

                            String[] mImgList = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(position).getPostPicture());

                            if (mImgList != null) {
                                isImgFile = true;
                            }

                            boolean isVideoFile = false;

                            String[] mVideoList = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(position).getPostVideoURL());

                            if (mVideoList != null) {
                                isVideoFile = true;
                            }

                            boolean mIsOtherMotoProfile;

                            mIsOtherMotoProfile = mPostsList.get(position).getProfileID() != mCurrentProfileResModel.getID();

                            if (isImgFile) {

                                ArrayList<Bitmap> mBitmapList = ((BaseActivity) mContext).getBitmapImageGlide(mImgList);

                                if (mBitmapList != null) {
                                    ((BaseActivity) mContext).showFBShareDialog(AppDialogFragment.BOTTOM_SHARE_DIALOG, content, mBitmapList, null, position, mIsOtherMotoProfile);
                                }

                            } else if (isVideoFile) {

                                String[] mVideosList = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(mViewHolderPost
                                        .getLayoutPosition()).getPostVideoURL());

                                ((BaseActivity) mContext).showFBShareDialog(AppDialogFragment.BOTTOM_SHARE_DIALOG, content, null, mVideosList, position, mIsOtherMotoProfile);

                            } else {

                                ((BaseActivity) mContext).showFBShareDialog(AppDialogFragment.BOTTOM_SHARE_DIALOG, content, null, null, position, mIsOtherMotoProfile);
                            }

                        }
                    });

                    mViewHolderPost.mPostImageVideoBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String[] mVideosList = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(position).getPostVideoURL());
                            String[] mImgList = ((BaseActivity) mContext).getImgVideoList(mPostsList.get(position).getPostPicture());
                            if (mVideosList != null && mVideosList.length > 0) {
                                ((BaseActivity) mContext).moveLoadVideoScreen(mContext, UrlUtils.AWS_S3_BASE_URL + mVideosList[0]);
                            } else if (mImgList != null && mImgList.length > 0) {
                                ((BaseActivity) mContext).moveLoadImageScreen(mContext, UrlUtils.AWS_S3_BASE_URL + mImgList[0]);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.down_arrow:
                ArrayList<String> mPos = new ArrayList<>();
                mPos.add(String.valueOf(view.getTag()));
                ((BaseActivity) mContext).showAppDialog(AppDialogFragment.BOTTOM_POST_ACTION_DIALOG, mPos);
                break;
            case R.id.circular_img_view:
            case R.id.top_tv:

                postProfileClick(view);
                break;

            case R.id.circular_img_view_share:
                sharedPostProfileClick(view);
                break;
        }

    }

    private void setImageView(ViewHolderPosts mHolder, String[] mArray) {
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
    }


    private void setUserName(final ViewHolderPosts mViewHolderPost, final int position, String mUsernameStr) {

        String[] mTaggedProfilesID = mPostsList.get(position).getTaggedProfileID().split(",");
        int mTaggedProfilesIDCount = mTaggedProfilesID.length;

        String mTagStr = "with " + mTaggedProfilesIDCount + " other(s)";

        mUsernameStr = mUsernameStr + " " + mTagStr;

        SpannableString mSpannableString = new SpannableString(mUsernameStr);

        mSpannableString.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);

                ds.setColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                ds.setUnderlineText(false);

            }

            @Override
            public void onClick(View view) {

                Intent mIntent = new Intent(mContext, TaggedProfilesListActivity.class);
                mIntent.putExtra(TaggedProfilesListActivity.TAGGED_PROFILES_ID, mPostsList.get(position).getTaggedProfileID());

                if (mIsFromOthersMotoProfile) {
                    //mIntent.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel.get(0));
                    //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel.get(0));
                    EventBus.getDefault().postSticky(mMyProfileResModel.get(0));
                } else {
                    //mIntent.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mCurrentProfileResModel);
                    //MotoHub.getApplicationInstance().setmProfileResModel(mCurrentProfileResModel);
                    EventBus.getDefault().postSticky(mCurrentProfileResModel);
                }

                mContext.startActivity(mIntent);

            }

        }, mUsernameStr.length() - mTagStr.length(), mUsernameStr.length(), 0);

        mViewHolderPost.mUsername.setText(mSpannableString);
        mViewHolderPost.mUsername.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void showShareLayout(final ViewHolderPosts mViewHolderPost, final int position, String mPostOwnerName) {

        mViewHolderPost.mShareViewLay.setVisibility(View.VISIBLE);
        if (String.valueOf(mPostsList.get(position).getProfileID()).trim().equals(String.valueOf(mCurrentProfileResModel.getID()).trim())) {
            mViewHolderPost.mSharedBottomArrow.setVisibility(View.VISIBLE);
            mViewHolderPost.mSharedBottomArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> mPos = new ArrayList<>();
                    mPos.add(String.valueOf(position));
                    ((BaseActivity) mContext).showAppDialog(AppDialogFragment.BOTTOM_SHARED_POST_ACTION_DIALOG, mPos);
                }
            });
        } else {
            mViewHolderPost.mSharedBottomArrow.setVisibility(View.GONE);
        }

        if (mPostsList.get(position).getUserType().trim().equals(AppConstants.VIDEO_SHARED_POST)) {
            VideoShareModel mFollowingsShareList = mPostsList.get(position).getVideoSharesByNewSharedPostID();
            mViewHolderPost.mPostDate.setText(((BaseActivity) mContext).findTime(mFollowingsShareList.getSharedAt()));
        } else {
            FeedShareModel mFollowingsShareList = mPostsList.get(position).getNewSharedPost();
            mViewHolderPost.mPostDate.setText(((BaseActivity) mContext).findTime(mFollowingsShareList.getSharedAt()));
        }

        String mShareTxt;

        int mFirstSpanWordLength;

        if (mPostsList.get(position).getProfilesByProfileID().getProfileType() == Integer.parseInt(BaseActivity.SPECTATOR)) {

            mShareTxt = mPostsList.get(position).getProfilesByProfileID().getSpectatorName() + " shared " + mPostOwnerName + " post";

            mFirstSpanWordLength = mPostsList.get(position).getProfilesByProfileID().getSpectatorName().length();

        } else {

            mShareTxt = mPostsList.get(position).getProfilesByProfileID().getDriver() + " shared " + mPostOwnerName + " post";

            mFirstSpanWordLength = mPostsList.get(position).getProfilesByProfileID().getDriver().length();

        }

        ((BaseActivity) mContext).setImageWithGlide(mViewHolderPost.mShareProfileImg, mPostsList.get(position).getProfilesByProfileID().getProfilePicture(), R.drawable.default_profile_icon);


        mViewHolderPost.mShareProfileImg.setTag(position);
        mViewHolderPost.mShareProfileImg.setOnClickListener(this);

        Spannable mWordToSpan = new SpannableString(mShareTxt);

        int mSecondSpanWordLength = mShareTxt.length();

        mWordToSpan.setSpan(new ForegroundColorSpan(Color.GRAY), mFirstSpanWordLength, mFirstSpanWordLength + 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mWordToSpan.setSpan(new ForegroundColorSpan(Color.GRAY), mSecondSpanWordLength - 5, mSecondSpanWordLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mWordToSpan.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);

                ds.setColor(ContextCompat.getColor(mContext, R.color.colorBlack));
                ds.setUnderlineText(false);

            }

            @Override
            public void onClick(View view) {

                sharedPostProfileClick(view);

            }

        }, 0, mFirstSpanWordLength, 0);

        mWordToSpan.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);

                ds.setColor(ContextCompat.getColor(mContext, R.color.colorBlack));
                ds.setUnderlineText(false);

            }

            @Override
            public void onClick(View view) {

                postProfileClick(view);

            }

        }, mFirstSpanWordLength + 8, mSecondSpanWordLength - 5, 0);

        mViewHolderPost.mShareTxtName.setText(mWordToSpan);
        mViewHolderPost.mShareTxtName.setTag(position);
        mViewHolderPost.mShareTxtName.setMovementMethod(LinkMovementMethod.getInstance());

        mViewHolderPost.mShareTxtTime.setText(((BaseActivity) mContext).findTime(mPostsList.get(position).getDateCreatedAt()));

        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mViewHolderPost.mProfileDetailedLay.getLayoutParams();

        //relativeParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.size80);

        relativeParams.height = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
        relativeParams.width = ViewGroup.MarginLayoutParams.MATCH_PARENT;

        relativeParams.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.size24),
                mContext.getResources().getDimensionPixelSize(R.dimen.size5),
                mContext.getResources().getDimensionPixelSize(R.dimen.size24),
                mContext.getResources().getDimensionPixelSize(R.dimen.size0));

        mViewHolderPost.mProfileDetailedLay.setLayoutParams(relativeParams);
        mViewHolderPost.mProfileDetailedLay.requestLayout();

        ViewGroup.MarginLayoutParams relativeParams1 = (ViewGroup.MarginLayoutParams) mViewHolderPost.mImageLay.getLayoutParams();

        relativeParams1.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.size20),
                mContext.getResources().getDimensionPixelSize(R.dimen.size10),
                mContext.getResources().getDimensionPixelSize(R.dimen.size20),
                mContext.getResources().getDimensionPixelSize(R.dimen.size0));

        relativeParams1.height = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
        relativeParams1.width = ViewGroup.MarginLayoutParams.MATCH_PARENT;

        mViewHolderPost.mImageLay.setLayoutParams(relativeParams1);
        mViewHolderPost.mImageLay.requestLayout();


        mViewHolderPost.mBottomArrowImgView.setVisibility(View.GONE);

    }

    private void HideShareLayout(ViewHolderPosts mViewHolderPost) {

        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mViewHolderPost.mProfileDetailedLay.getLayoutParams();

//        relativeParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.size80);
        relativeParams.height = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
        relativeParams.width = ViewGroup.MarginLayoutParams.MATCH_PARENT;

        relativeParams.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.size10),
                mContext.getResources().getDimensionPixelSize(R.dimen.size0),
                mContext.getResources().getDimensionPixelSize(R.dimen.size10),
                mContext.getResources().getDimensionPixelSize(R.dimen.size0));

        mViewHolderPost.mProfileDetailedLay.setLayoutParams(relativeParams);
        mViewHolderPost.mProfileDetailedLay.requestLayout();

        ViewGroup.MarginLayoutParams relativeParams1 = (ViewGroup.MarginLayoutParams) mViewHolderPost.mImageLay.getLayoutParams();

        relativeParams1.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.size0),
                mContext.getResources().getDimensionPixelSize(R.dimen.size10),
                mContext.getResources().getDimensionPixelSize(R.dimen.size0),
                mContext.getResources().getDimensionPixelSize(R.dimen.size0));

        relativeParams1.height = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
        relativeParams1.width = ViewGroup.MarginLayoutParams.MATCH_PARENT;

        mViewHolderPost.mImageLay.setLayoutParams(relativeParams1);
        mViewHolderPost.mImageLay.requestLayout();

        mViewHolderPost.mShareViewLay.setVisibility(View.GONE);
        mViewHolderPost.mShareCountTxt.setVisibility(View.GONE);

    }

    private void setPostComments(ViewHolderPosts mViewHolderPost, int position) {
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
    }

    private void setLikeUnLikeForPost(ViewHolderPosts mViewHolderPost, int position) {
        if (mPostsList.get(position).getPostLikes().size() > 0) {

            ArrayList<FeedLikesModel> mFeedLikes = mPostsList.get(position).getPostLikes();

            String resLikes;

            if (mPostsList.get(position).getPostLikes().size() == 1) {

                resLikes = mPostsList.get(position).getPostLikes().size() + " Like";
            } else {
                resLikes = mPostsList.get(position).getPostLikes().size() + " Likes";

            }
            mViewHolderPost.mLikeCountText.setText(resLikes);
            for (final FeedLikesModel likesEntity : mFeedLikes) {

                if ((likesEntity.getProfileID() == mCurrentProfileResModel.getID())) {
                    mViewHolderPost.mLikeBtn.setImageResource(R.drawable.liked_icon);
                    mViewHolderPost.mLikeBtn.setTag("unlike");
                    break;
                } else {
                    mViewHolderPost.mLikeBtn.setImageResource(R.drawable.like_icon);
                    mViewHolderPost.mLikeBtn.setTag("like");
                }
            }
        } else {
            mViewHolderPost.mLikeBtn.setImageResource(R.drawable.like_icon);
            mViewHolderPost.mLikeBtn.setTag("like");
            mViewHolderPost.mLikeCountText.setText("");
        }
    }

    private void setPostPicture(final ViewHolderPosts mViewHolderPost, String
            imgUrl) {

        mViewHolderPost.mPostImageVideoBox.setVisibility(View.VISIBLE);
        mViewHolderPost.playicon.setVisibility(View.GONE);

        mViewHolderPost.mPostPic.setVisibility(View.VISIBLE);
        mViewHolderPost.mPostPicProgressBar.setVisibility(View.VISIBLE);

        /*GlideUrl glideUrl = new GlideUrl(UrlUtils.AWS_FILE_URL + imgUrl, new LazyHeaders.Builder()
                .addHeader("X-DreamFactory-Api-Key", mContext.getString(R.string.dream_factory_api_key))
                .build());*/

        Glide.with(mContext)
                .load(UrlUtils.AWS_S3_BASE_URL + imgUrl)
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

        /*GlideUrl glideUrl = new GlideUrl(UrlUtils.AWS_FILE_URL + videoThumbnail,
                new LazyHeaders.Builder()
                        .addHeader("X-DreamFactory-Api-Key", mContext.getString(R.string.dream_factory_api_key))
                        .build());*/


        Glide.with(mContext)
                .load(UrlUtils.AWS_S3_BASE_URL + videoThumbnail)
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


    private void postProfileClick(View view) {
        int selPos = (int) view.getTag();
        ProfileResModel mProfileModel = null;
        if (mIsFromOthersMotoProfile) {
            mProfileModel = mMyProfileResModel.get(0);
        } else if (mCurrentProfileResModel != null) {
            mProfileModel = mCurrentProfileResModel;
        }
        if (mProfileModel != null) {
            if (mPostsList.get(selPos).getNewSharedPostID() == null) {
                if (mPostsList.get(selPos).getProfileID() == mProfileModel.getID()) {
                    if (!(mContext instanceof MyMotoFileActivity)) {
                        ((BaseActivity) mContext).moveMyProfileScreenWithResult(mContext, 0, AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                    }
                    return;
                }
            } else {
                if (mPostsList.get(selPos).getWhoPostedProfileID() == mProfileModel.getID()) {
                    if (!(mContext instanceof MyMotoFileActivity)) {
                        ((BaseActivity) mContext).moveMyProfileScreenWithResult(mContext, 0, AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                    }
                    return;
                }
            }

        }

        Bundle mBundle = new Bundle();
        String mPostUserType = mPostsList.get(selPos).getUserType().trim();
        if (mPostUserType.equals(PromotersModel.PROMOTER) || mPostUserType.equals(PromotersModel.NEWS_AND_MEDIA) ||
                mPostUserType.equals(PromotersModel.TRACK) || mPostUserType.equals(PromotersModel.CLUB)
                || mPostUserType.equals(PromotersModel.SHOP)) {
            Class mClassName;
            String mUserType = "";
            if (mPostsList.get(selPos).getPromoterByWhoPostedProfileID() != null) {
                //mBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, mPostsList.get(selPos).getPromoterByWhoPostedProfileID());
                //MotoHub.getApplicationInstance().setmPromoterResModel(mPostsList.get(selPos).getPromoterByWhoPostedProfileID());
                EventBus.getDefault().postSticky(mPostsList.get(selPos).getPromoterByWhoPostedProfileID());
                mUserType = mPostsList.get(selPos)
                        .getPromoterByWhoPostedProfileID().getUserType();
            } else {
                //mBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, mPostsList.get(selPos).getPromoterByProfileID());
                //MotoHub.getApplicationInstance().setmPromoterResModel(mPostsList.get(selPos).getPromoterByProfileID());
                EventBus.getDefault().postSticky(mPostsList.get(selPos).getPromoterByProfileID());
                mUserType = mPostsList.get(selPos)
                        .getPromoterByProfileID().getUserType();
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

            /*mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mProfileModel);
            ((BaseActivity) mContext).startActivityForResult(new Intent(mContext, mClassName).putExtras(mBundle), AppConstants.FOLLOWERS_FOLLOWING_RESULT);*/
            //MotoHub.getApplicationInstance().setmProfileResModel(mProfileModel);
            EventBus.getDefault().postSticky(mProfileModel);
            ((BaseActivity) mContext).startActivityForResult(new Intent(mContext, mClassName), AppConstants.FOLLOWERS_FOLLOWING_RESULT);

        } else {
            ((BaseActivity) mContext).moveOtherProfileScreen(mContext, mProfileModel.getID(),
                    mPostsList.get(selPos).getProfilesByWhoPostedProfileID().getID());
        }
    }

    private void sharedPostProfileClick(View view) {
        int selPos = (int) view.getTag();
        ProfileResModel mProfileModel;
        if (mIsFromOthersMotoProfile) {
            mProfileModel = mMyProfileResModel.get(0);
        } else {
            mProfileModel = mCurrentProfileResModel;
        }

        if (Integer.parseInt(mPostsList.get(selPos).getWhoSharedProfileID())
                == mProfileModel.getID()) {
            if (!(mContext instanceof MyMotoFileActivity)) {
                ((BaseActivity) mContext).moveMyProfileScreen(mContext, 0);
            }
            return;
        }
        FeedShareModel mSharedPostModel = mPostsList.get(selPos).getNewSharedPost();
        ((BaseActivity) mContext).moveOtherProfileScreen(mContext, mProfileModel.getID(),
                mSharedPostModel.getProfiles_by_ProfileID().getID());

    }


    public void resetLikeAdapter(FeedLikesModel feedLikesModel) {

        ArrayList<FeedLikesModel> mFeedLikesList = mPostsList.get(mTempPosition).getPostLikes();

        mFeedLikesList.add(feedLikesModel);

        mPostsList.get(mTempPosition).setPostLikes(mFeedLikesList);
        notifyDataSetChanged();

    }

    public void resetDisLike(FeedLikesModel feedLikesModel) {
        ArrayList<FeedLikesModel> mFeedLikesList = mPostsList.get(mTempPosition).getPostLikes();

        Iterator<FeedLikesModel> iterator = mFeedLikesList.iterator();
        while (iterator.hasNext()) {
            FeedLikesModel next = iterator.next();
            if (next.getId() == (feedLikesModel.getId())) {
                iterator.remove();
                mFeedLikesList.remove(next);
            }
        }

        mPostsList.get(mTempPosition).setPostLikes(mFeedLikesList);
        notifyDataSetChanged();
    }

    public void resetShareAdapter(FeedShareModel feedShareModel) {
        ArrayList<FeedShareModel> mShareList = mPostsList.get(mTempPosition + 1).getPostShares();

        mShareList.add(feedShareModel);

        mPostsList.get(mTempPosition + 1).setPostShares(mShareList);
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


    public void resetShareCount(FeedShareModel mSharedFeed) {
        ArrayList<FeedShareModel> mShareModel = mPostsList.get(mTempPosition).getPostShares();
        mShareModel.add(mSharedFeed);

        mPostsList.get(mTempPosition).setPostShares(mShareModel);
        notifyDataSetChanged();
    }

    private void setGlideImage(ImageView imgView, String imgUrl, int drawable) {
        GlideUrl glideUrl = new GlideUrl(UrlUtils.AWS_FILE_URL + imgUrl, new LazyHeaders.Builder()
                .addHeader("X-DreamFactory-Api-Key", mContext.getString(R.string.dream_factory_api_key))
                .build());

        Glide.with(mContext)
                .load(glideUrl)
                .apply(new RequestOptions()
                        .dontAnimate()
                        .error(drawable)
                        .dontAnimate())
                .into(imgView);
    }


    public interface TotalRetrofitPostsResultCount {
        int getTotalPostsResultCount();
    }

    public class ViewHolderPosts extends RecyclerView.ViewHolder {

        @BindView(R.id.top_lay)
        LinearLayout mTopLay;
        @BindView(R.id.bottom_lay)
        LinearLayout mBottomLay;
        @BindView(R.id.more_lay)
        LinearLayout mMoreLay;
        @BindView(R.id.multi_img)
        LinearLayout multi_img_layout;
        @BindView(R.id.img_count_txt)
        TextView mTotalImgCountTv;
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
        private RelativeLayout mPostImageVideoBox;
        private CircleImageView mProfileImg;
        private TextView mUsername;
        private TextView mPostDate;
        private TextView mPostText;
        private ImageView mPostPic;
        private ImageView mBottomArrowImgView;
        private RelativeLayout mCommentViewLay;
        private TextView mShareCountTxt;
        private RelativeLayout mCountLay;
        private TextView mLikeCountText;
        private TextView mCommentCountTxt;
        private RelativeLayout mShareViewLay;
        private CircleImageView mShareProfileImg;
        private TextView mShareTxtName;
        private TextView mShareTxtTime;
        private RelativeLayout mProfileDetailedLay;
        private RelativeLayout mImageLay;
        private ImageView mLikeBtn;
        private ImageView mShareBtn;
        private ImageView playicon;
        private ProgressBar mPostPicProgressBar;
        private RelativeLayout mMultiImgLay;
        private ImageView mSharedBottomArrow;

        ViewHolderPosts(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mProfileImg = view.findViewById(R.id.circular_img_view);
            mUsername = view.findViewById(R.id.top_tv);
            playicon = view.findViewById(R.id.playicon);
            mPostDate = view.findViewById(R.id.bottom_tv);
            mPostText = view.findViewById(R.id.post_text_tv);
            mPostPic = view.findViewById(R.id.post_picture_img_view);
            mPostPicProgressBar = view.findViewById(R.id.smallProgressBar);
            mBottomArrowImgView = view.findViewById(R.id.down_arrow);
            mLikeCountText = view.findViewById(R.id.like_count_txt);
            mCommentViewLay = view.findViewById(R.id.comment_view_lay);
            mCountLay = view.findViewById(R.id.rl_count);
            mCommentCountTxt = view.findViewById(R.id.comments_comment_txt);
            mShareCountTxt = view.findViewById(R.id.share_count_txt);
            mShareViewLay = view.findViewById(R.id.profile_shared_box);
            mShareProfileImg = view.findViewById(R.id.circular_img_view_share);
            mShareTxtName = view.findViewById(R.id.top_tv_share);
            mShareTxtTime = view.findViewById(R.id.bottom_tv_share);
            mProfileDetailedLay = view.findViewById(R.id.profile_details_box);
            mImageLay = view.findViewById(R.id.img_lay);
            mLikeBtn = view.findViewById(R.id.likeBtn);
            mShareBtn = view.findViewById(R.id.shareBtn);
            mPostImageVideoBox = view.findViewById(R.id.postImageVideoBox);
            mMultiImgLay = view.findViewById(R.id.multi_img_lay);
            mTotalImgCountTv = view.findViewById(R.id.img_count_txt);
            mSharedBottomArrow = view.findViewById(R.id.share_down_arrow);
        }

    }

    private class ViewHolderLoader extends RecyclerView.ViewHolder {

        ProgressBar mProgressBar;

        ViewHolderLoader(View view) {
            super(view);
            mProgressBar = view.findViewById(R.id.myProgressBar);
        }

    }

}
