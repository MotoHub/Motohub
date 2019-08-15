package online.motohub.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

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
import online.motohub.constants.AppConstants;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.AdapterClickCallBack;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.NotificationBlockedUsersModel;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.VideoShareModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.UrlUtils;
import online.motohub.util.Utility;

public class NewsFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_POSTS = 1;
    private final ArrayList<PostsResModel> mPostsList;
    private ArrayList<NotificationBlockedUsersModel> notifications_blocked_users = new ArrayList<>();
    private Context mContext;
    private ProfileResModel mCurrentProfileObj;
    private Activity mActivity;
    private Dialog mCommentListPopup;
    private int mAdapterPosition;
    private RecyclerView mFeedLikesListView;
    private ViewHolderPosts mViewHolderPost;
    private int mDeleteLikeID;
    private boolean isFromMyProfile;
    private AdapterClickCallBack callBack;

    public NewsFeedAdapter(
            Context ctx, ArrayList<PostsResModel> postsList, ProfileResModel myProfileResModel, boolean isFromMyProfile, AdapterClickCallBack callBack) {
        this.mContext = ctx;
        this.mPostsList = postsList;
        this.mCurrentProfileObj = myProfileResModel;
        this.isFromMyProfile = isFromMyProfile;
        this.mActivity = (Activity) ctx;
        this.callBack = callBack;
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView;
        switch (viewType) {
            case VIEW_TYPE_POSTS:
                mView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.adap_news_feed, parent, false);
                return new ViewHolderPosts(mView);
            case VIEW_TYPE_LOADING:
                mView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.widget_progress_bar, parent, false);
                RecyclerView.LayoutParams mLayoutParams = (RecyclerView.LayoutParams) mView.getLayoutParams();
                mLayoutParams.width = RecyclerView.LayoutParams.MATCH_PARENT;
                mLayoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT;
                mView.setLayoutParams(mLayoutParams);
                return new ViewHolderLoader(mView);
            default:
                //noinspection ConstantConditions
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        switch (getItemViewType(position)) {

            case VIEW_TYPE_POSTS:
                try {
                    mViewHolderPost = (ViewHolderPosts) holder;
                    String mUsernameStr;
                    String mPostOwnerName = "";

                    mViewHolderPost.commentView.setVisibility(View.GONE);
                    mViewHolderPost.countView.setVisibility(View.GONE);

                    mViewHolderPost.imgVideoView.setVisibility(View.GONE);
                    mViewHolderPost.mTopLay.setVisibility(View.GONE);
                    mViewHolderPost.mImgLay1.setVisibility(View.GONE);
                    mViewHolderPost.mImgLay2.setVisibility(View.GONE);
                    mViewHolderPost.mBottomLay.setVisibility(View.GONE);
                    mViewHolderPost.mImgLay3.setVisibility(View.GONE);
                    mViewHolderPost.mImgLay4.setVisibility(View.GONE);
                    mViewHolderPost.mMoreLay.setVisibility(View.GONE);

                    if (mPostsList.get(position).getDateCreatedAt() != null)
                        mViewHolderPost.postTimeTxt.setText(Utility.getInstance().findTime(mPostsList.get(position).getDateCreatedAt()));

                    mViewHolderPost.downIcon.setVisibility(View.VISIBLE);
                    mViewHolderPost.downIcon.setOnClickListener(this);
                    mViewHolderPost.downIcon.setTag(position);
                    /*PostNotifications*/
                    if (mPostsList.get(position).getmNotificationBlockedUsersID().size() > 0) {
                        setPostNotifications(position, mViewHolderPost.notifyIcon);
                    } else {
                        postNotificationDefault(mViewHolderPost.notifyIcon);
                    }
                    mViewHolderPost.notifyIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAdapterPosition = position;
                            String img_tag = v.getTag().toString();
                            if (!Utility.getInstance().isMultiClicked()) {
                                if (img_tag.equals(mContext.getString(R.string.notification_blocked))) {
                                    unBlockNotification(position);
                                } else if (img_tag.equals(mContext.getString(R.string.notification_unblocked))) {
                                    blockNotification(position);
                                }
                            }
                        }
                    });
                    if (!isFromMyProfile) {
                        mViewHolderPost.userImg.setTag(position);
                        mViewHolderPost.userImg.setOnClickListener(this);
                        mViewHolderPost.profileNameTxt.setTag(position);
                        mViewHolderPost.profileNameTxt.setOnClickListener(this);
                        mViewHolderPost.sharedUserImg.setTag(position);
                        mViewHolderPost.sharedUserImg.setOnClickListener(this);
                    }
                    if (mPostsList.get(position).getUserType() == null
                            || mPostsList.get(position).getUserType().isEmpty()
                            || mPostsList.get(position).getUserType().trim().equals("user")
                            || mPostsList.get(position).getUserType().trim().equals("club_user")
                            || mPostsList.get(position).getUserType().trim().equals("shop_user")
                            || mPostsList.get(position).getUserType().trim().equals(AppConstants.ONDEMAND)) {

                        ProfileResModel mProfileResModel = mPostsList.get(position).getProfilesByWhoPostedProfileID();
                        mUsernameStr = Utility.getInstance().getUserName(mProfileResModel);
                        mPostOwnerName = mUsernameStr;
                        if (mPostsList.get(position).getTaggedProfileID() != null
                                && !mPostsList.get(position).getTaggedProfileID().isEmpty()) {
                            setUserName(mViewHolderPost, position, mUsernameStr);
                        } else {
                            mViewHolderPost.profileNameTxt.setText(mUsernameStr);
                        }
                        if (mProfileResModel.getProfilePicture() != null)
                            ((BaseActivity) mContext).setImageWithGlide(mViewHolderPost.userImg, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);
                    } else if (mPostsList.get(position).getUserType().trim().equals(AppConstants.USER_VIDEO_SHARED_POST)) {
                        if (mPostsList.get(position).getVideoSharesByNewSharedPostID() != null) {
                            ProfileResModel mProfileResModel = mPostsList.get(position).getProfilesByWhoPostedProfileID();
                            mUsernameStr = Utility.getInstance().getUserName(mProfileResModel);
                            mPostOwnerName = mUsernameStr;
                            if (mPostsList.get(position).getTaggedProfileID() != null && !mPostsList.get(position)
                                    .getTaggedProfileID().isEmpty()) {
                                setUserName(mViewHolderPost, position, mUsernameStr);
                            } else {
                                mViewHolderPost.profileNameTxt.setText(mUsernameStr);
                            }
                            ((BaseActivity) mContext).setImageWithGlide(mViewHolderPost.userImg, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);
                        }
                    } else if (mPostsList.get(position).getUserType().trim().equals(AppConstants.SHARED_POST)
                            || mPostsList.get(position).getUserType().trim().equals(AppConstants.VIDEO_SHARED_POST)
                        /*|| mPostsList.get(position).getUserType().trim().equals(AppConstants.SHARED_VIDEO)*/) {

                        if (mPostsList.get(position).getPromoterByWhoPostedProfileID() != null) {
                            PromotersResModel mPromotersResModel = mPostsList.get(position).getPromoterByWhoPostedProfileID();
                            mUsernameStr = (mPromotersResModel.getName());
                            mPostOwnerName = mUsernameStr;
                            mViewHolderPost.profileNameTxt.setText(mUsernameStr);
                            ((BaseActivity) mContext).setImageWithGlide(mViewHolderPost.userImg,
                                    mPromotersResModel.getProfileImage(), R.drawable.default_profile_icon);
                        }

                    } else if (mPostsList.get(position).getUserType().trim().equals(AppConstants.PROMOTER)
                            || mPostsList.get(position).getUserType().trim().equals(AppConstants.CLUB)
                            || mPostsList.get(position).getUserType().trim().equals(AppConstants.NEWS_MEDIA)
                            || mPostsList.get(position).getUserType().trim().equals(AppConstants.TRACK)
                            || mPostsList.get(position).getUserType().trim().equals(AppConstants.SHOP)) {
                        PromotersResModel mPromotersProfilesModel = mPostsList.get(position).getPromoterByProfileID();
                        mUsernameStr = mPromotersProfilesModel.getName();
                        mPostOwnerName = mUsernameStr;
                        mViewHolderPost.profileNameTxt.setText(mUsernameStr);
                        ((BaseActivity) mContext).setImageWithGlide(mViewHolderPost.userImg, mPromotersProfilesModel.getProfileImage(), R.drawable.default_profile_icon);
                    }
                    final String[] mVideoArray = Utility.getInstance().getImgVideoList(mPostsList.get(position).getPostVideoThumbnailURL());
                    final String[] mImgArray = Utility.getInstance().getImgVideoList(mPostsList.get(position).getPostPicture());

                    if ((mVideoArray != null && mVideoArray.length >= 1) && (mImgArray != null && mImgArray.length >= 1)) {
                        String[] c = mergeArrayList(mVideoArray, mImgArray);
                        setImageView(mViewHolderPost, c);
                        mViewHolderPost.imgVideoView.setVisibility(View.VISIBLE);
                    } else if (mVideoArray != null && mVideoArray.length >= 1) {
                        setImageView(mViewHolderPost, mVideoArray);
                        mViewHolderPost.imgVideoView.setVisibility(View.VISIBLE);
                    } else if (mImgArray != null && mImgArray.length >= 1) {
                        setImageView(mViewHolderPost, mImgArray);
                        mViewHolderPost.imgVideoView.setVisibility(View.VISIBLE);
                    }
                    if (mPostsList.get(position).getPostText().isEmpty()) {
                        mViewHolderPost.postContentTxt.setVisibility(View.GONE);
                    } else {
                        try {
                            if (!mPostsList.get(position).getPostText().contains(" ")) {
                                mViewHolderPost.postContentTxt.setText(URLDecoder.decode(mPostsList.get(position).getPostText(), "UTF-8"));
                            } else {
                                mViewHolderPost.postContentTxt.setText(mPostsList.get(position).getPostText());
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        mViewHolderPost.postContentTxt.setVisibility(View.VISIBLE);
                    }


                    //Count View
                    if (mPostsList.get(position).getPostLikes().size() > 0
                            || mPostsList.get(position).getPostComments().size() >= 0
                            || mPostsList.get(position).getPostShares().size() >= 0
                            || mPostsList.get(position).getmViewCount() >= 0) {
                        mViewHolderPost.countView.setVisibility(View.VISIBLE);
                    }
                    //Like
                    if (mPostsList.get(position).getPostLikes().size() > 0) {
                        setLikeUnLikeForPost(mViewHolderPost, position);
                    } else {
                        mViewHolderPost.likeBtn.setImageResource(R.drawable.like_icon);
                        mViewHolderPost.likeBtn.setTag("like");
                        mViewHolderPost.likeCountTxt.setText("");
                    }
                    //share
                    if (mPostsList.get(position).getNewSharedPostID().trim().isEmpty()) {
                        HideShareLayout(mViewHolderPost, position);
                    } else {
                        showShareLayout(mViewHolderPost, position, mPostOwnerName);
                    }
                    ArrayList<FeedCommentModel> mFeedCommentModel = mPostsList.get(position).getPostComments();
                    if (mFeedCommentModel.size() > 0) {
                        setPostComments(mViewHolderPost, position);
                        showPostCommentsLay(mViewHolderPost, mFeedCommentModel, position);
                    }
                    //View Count
                    if (mPostsList.get(position).getmViewCount() > 0) {
                        setViewCount(mViewHolderPost, position);
                    } else {
                        mViewHolderPost.viewCountTxt.setVisibility(View.GONE);
                    }
                    String numberOfShares;
                    if (mPostsList.get(position).getPostShares().size() == 0) {
                        mViewHolderPost.shareCountTxt.setVisibility(View.GONE);
                    } else if (mPostsList.get(position).getPostShares().size() == 1) {
                        numberOfShares = mPostsList.get(position).getPostShares().size() + " Share";
                        mViewHolderPost.shareCountTxt.setText(numberOfShares);
                        mViewHolderPost.shareCountTxt.setVisibility(View.VISIBLE);
                    } else {
                        numberOfShares = mPostsList.get(position).getPostShares().size() + " Shares";
                        mViewHolderPost.shareCountTxt.setText(numberOfShares);
                        mViewHolderPost.shareCountTxt.setVisibility(View.VISIBLE);
                    }

                    mViewHolderPost.imgVideoView.setTag(position);
                    mViewHolderPost.imgVideoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAdapterPosition = (int) view.getTag();
                            String[] finalArr = null;
                            final String[] mImgArray = Utility.getInstance().getImgVideoList(mPostsList.get(position).getPostPicture());
                            final String[] mVideoArray = Utility.getInstance().getImgVideoList(mPostsList.get(position).getPostVideoThumbnailURL());
                            if (mImgArray != null && mVideoArray != null) {
                                finalArr = mergeArrayList(mVideoArray, mImgArray);
                            } else if (mVideoArray != null && mVideoArray.length == 1) {
                                updateViewCount(mAdapterPosition);
                                ((BaseActivity) mContext).LoadVideoScreen(mContext, UrlUtils.AWS_S3_BASE_URL + mVideoArray[0],
                                        mAdapterPosition, RetrofitClient.UPDATE_FEED_COUNT);
                                return;
                            } else if (mVideoArray != null && mVideoArray.length > 1) {
                                finalArr = Arrays.copyOf(mVideoArray, mVideoArray.length);
                            } else if (mImgArray != null && mImgArray.length == 1) {
                                finalArr = Arrays.copyOf(mImgArray, mImgArray.length);
                                ((BaseActivity) mContext).moveLoadImageScreen(mContext, UrlUtils.AWS_S3_BASE_URL + mImgArray[0]);
                                return;
                            } else if (mImgArray != null && mImgArray.length > 1) {
                                finalArr = Arrays.copyOf(mImgArray, mImgArray.length);
                            }
                            Intent intent = new Intent(mContext, PromotersImgListActivity.class);
                            intent.putExtra("img", finalArr);
                            mContext.startActivity(intent);

                        }
                    });
                    mViewHolderPost.likeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!Utility.getInstance().isMultiClicked()) {
                                mAdapterPosition = position;
                                switch (view.getTag().toString()) {
                                    case "unlike":
                                        ArrayList<FeedLikesModel> mFeedLikeList = mPostsList.get(position).getPostLikes();
                                        if (mFeedLikeList.size() > 0) {
                                            for (FeedLikesModel likesEntity : mFeedLikeList) {
                                                if (likesEntity.getOwnerID() == mCurrentProfileObj.getID() && likesEntity.getFeedID() == mPostsList.get(position).getID()) {
                                                    mDeleteLikeID = likesEntity.getId();
                                                    break;
                                                }
                                            }
                                            String mFilter = "ID=" + mDeleteLikeID;
                                            // String mFilter = "(" + PostsModel.POST_ID +  "=" + mPostsList.get(position).getID() + ") AND ("+ PostsModel.PROFILE_ID + "=" + mCurrentProfileResModel.getID() + ")";
                                            callUnLikePost(mFilter);
                                        }
                                        break;
                                    case "like":
                                        callLikePost(mPostsList.get(position).getID(), mCurrentProfileObj.getID());
                                        break;
                                }
                            }
                        }
                    });

                    mViewHolderPost.commentView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAdapterPosition = position;
                            ((BaseActivity) mContext).movePostCommentScreen(mContext, mPostsList.get(mAdapterPosition).getID(), mCurrentProfileObj);
                        }
                    });

                    mViewHolderPost.commentsCountTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAdapterPosition = position;
                            ((BaseActivity) mContext).movePostCommentScreen(mContext, mPostsList.get(mAdapterPosition).getID(), mCurrentProfileObj);
                        }
                    });

                    mViewHolderPost.commentBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAdapterPosition = position;
                            ((BaseActivity) mContext).movePostCommentScreen(mContext, mPostsList.get(mAdapterPosition).getID(), mCurrentProfileObj);
                        }
                    });

                    mViewHolderPost.likeCountTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                ArrayList<FeedLikesModel> mLikeModel;
                                mAdapterPosition = position;
                                if (mPostsList.get(position).getPostLikes() != null) {
                                    mLikeModel = mPostsList.get(position).getPostLikes();
                                } else {
                                    mLikeModel = new ArrayList<>();
                                }
                                showLikeListPopup(mContext.getString(R.string.likes));
                                setFeedLikeAdapter(mLikeModel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    mViewHolderPost.shareCountTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                ArrayList<FeedShareModel> mShareModel;
                                mAdapterPosition = position;
                                if (mPostsList.get(position).getPostShares() != null) {
                                    mShareModel = mPostsList.get(position).getPostShares();
                                } else {
                                    mShareModel = new ArrayList<>();
                                }
                                showLikeListPopup(mContext.getString(R.string.shares));
                                setFeedShareAdapter(mShareModel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    //noinspection ConstantConditions
                    mViewHolderPost.shareBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String content = null;
                            mAdapterPosition = position;
                            try {
                                /*if (mPostsList.get(position).getPostText().contains(" "))
                                    content = mPostsList.get(position).getPostText();
                                else*/
                                content = URLDecoder.decode(mPostsList.get(position).getPostText(), "UTF-8");
                                //content = replacer(sb.append(mPostsList.get(position).getPostText()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            boolean isImgFile;
                            String[] mImgList = Utility.getInstance().getImgVideoList(mPostsList.get(position).getPostPicture());
                            if (mImgList == null) {
                                isImgFile = false;
                            } else
                                isImgFile = !(mImgList.length == 1 && mImgList[0].trim().equals(""));
                            boolean isVideoFile;
                            String[] mVideoList = Utility.getInstance().getImgVideoList(mPostsList.get(position).getPostVideoURL());
                            if (mVideoList == null) {
                                isVideoFile = false;
                            } else
                                isVideoFile = !(mVideoList.length == 1 && mVideoList[0].trim().equals(""));
                            boolean mIsOtherMotoProfile;
                            mIsOtherMotoProfile = mPostsList.get(position).getProfileID() != mCurrentProfileObj.getID();
                            //if (mIsOtherMotoProfile) {
                            if (isImgFile) {
                                ArrayList<Bitmap> mBitmapList = ((BaseActivity) mContext).getBitmapImageGlide(mImgList);
                                if (mBitmapList != null) {
                                    ((BaseActivity) mContext).showFBShareDialog(AppDialogFragment.BOTTOM_SHARE_DIALOG, content, mBitmapList, null, position, mIsOtherMotoProfile);
                                }
                            } else if (isVideoFile) {
                                String[] mVideosList = Utility.getInstance().getImgVideoList(mPostsList.get(position).getPostVideoURL());
                                ((BaseActivity) mContext).showFBShareDialog(AppDialogFragment.BOTTOM_SHARE_DIALOG, content, null, mVideosList, position, mIsOtherMotoProfile);

                            } else {
                                ((BaseActivity) mContext).showFBShareDialog(AppDialogFragment.BOTTOM_SHARE_DIALOG, content, null, null, position, mIsOtherMotoProfile);
                            }
                            /*} else {
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.alert_share), Toast.LENGTH_SHORT).show();
                            }*/

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.downIcon:
                int position = (int) view.getTag();
                ArrayList<String> mPos = new ArrayList<>();
                mPos.add(String.valueOf(position));
                if (mPostsList.get(position).getProfileID() == mCurrentProfileObj.getID()) {
                    ((BaseActivity) mContext).showAppDialog(AppDialogFragment.BOTTOM_POST_ACTION_DIALOG, mPos);
                } else {
                    ((BaseActivity) mContext).showAppDialog(AppDialogFragment.BOTTOM_REPORT_ACTION_DIALOG, mPos);
                }
                break;
            case R.id.userImg:
            case R.id.sharedPostTimeTxt:
            case R.id.profileNameTxt:
                postProfileClick(view);
                break;
            case R.id.sharedUserImg:
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
                //MotoHub.getApplicationInstance().setmProfileResModel(mCurrentProfileObj);
                EventBus.getDefault().postSticky(mCurrentProfileObj);
                Intent mIntent = new Intent(mContext, TaggedProfilesListActivity.class);
                mIntent.putExtra(TaggedProfilesListActivity.TAGGED_PROFILES_ID, mPostsList.get(position).getTaggedProfileID());
                /*mIntent.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mCurrentProfileObj);*/
                mContext.startActivity(mIntent);

            }

        }, mUsernameStr.length() - mTagStr.length(), mUsernameStr.length(), 0);

        mViewHolderPost.profileNameTxt.setText(mSpannableString);
        mViewHolderPost.profileNameTxt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void showPostCommentsLay(ViewHolderPosts mViewHolderPost, ArrayList<FeedCommentModel> mFeedCommentModel, int position) {

        mViewHolderPost.commentView.setVisibility(View.VISIBLE);

        int latestPostion = mFeedCommentModel.size() - 1;

        String mCommentText = mFeedCommentModel.get(latestPostion).getmComment().trim();
        String mCommentImgUrl = mFeedCommentModel.get(latestPostion).getCommentImages().trim();

        if (!mCommentImgUrl.isEmpty()) {
            mViewHolderPost.commentContentTxt.setVisibility(View.VISIBLE);
            mViewHolderPost.commentContentTxt.setText("Commented to this post.");
        } else if (!mCommentText.isEmpty()) {
            try {
                if (!mCommentText.contains(" ")) {
                    mViewHolderPost.commentContentTxt.setText(((BaseActivity) mContext)
                            .setTextEdt(mContext, URLDecoder.decode(mCommentText, "UTF-8"),
                                    mFeedCommentModel.get(latestPostion).getCommentTaggedUserNames(),
                                    mFeedCommentModel.get(latestPostion).getCommentTaggedUserID(), mCurrentProfileObj.getID()));
                } else {
                    mViewHolderPost.commentContentTxt.setText(((BaseActivity) mContext)
                            .setTextEdt(mContext, mCommentText,
                                    mFeedCommentModel.get(latestPostion).getCommentTaggedUserNames(),
                                    mFeedCommentModel.get(latestPostion).getCommentTaggedUserID(), mCurrentProfileObj.getID()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mViewHolderPost.commentContentTxt.setMovementMethod(LinkMovementMethod.getInstance());
            mViewHolderPost.commentContentTxt.setVisibility(View.VISIBLE);
        } else {
            mViewHolderPost.commentContentTxt.setVisibility(View.GONE);
        }

        mViewHolderPost.commentUserNameTxt.setText(Utility.getInstance().getUserName(mFeedCommentModel.get(latestPostion).getProfiles_by_ProfileID()));

        ((BaseActivity) mContext).setImageWithGlide(mViewHolderPost.commentUserImg, mFeedCommentModel.get(latestPostion).getProfiles_by_ProfileID().getProfilePicture(), R.drawable.default_profile_icon);

        mViewHolderPost.commentUserImg.setTag(position);

    }

    private void postNotificationDefault(ImageView img) {
        img.setVisibility(View.VISIBLE);
        img.setImageResource(R.drawable.notify_active_icon);
        img.setTag(mContext.getString(R.string.notification_unblocked));
    }

    private void setPostNotifications(int pos, ImageView img) {
        notifications_blocked_users = mPostsList.get(pos).getmNotificationBlockedUsersID();
        for (final NotificationBlockedUsersModel mNotifications_post : notifications_blocked_users) {
            if (mCurrentProfileObj.getID() == mNotifications_post.getmProfileID()) {
                img.setVisibility(View.VISIBLE);
                img.setImageResource(R.drawable.notify_inactive_icon);
                img.setTag(mContext.getString(R.string.notification_blocked));
                break;
            } else {
                img.setVisibility(View.VISIBLE);
                img.setImageResource(R.drawable.notify_active_icon);
                img.setTag(mContext.getString(R.string.notification_unblocked));
                break;
            }
        }
    }

    public void blockNotification(int pos) {
        int postID = mPostsList.get(pos).getID();
        int mUserID = mCurrentProfileObj.getUserID();
        int ProfileID = mCurrentProfileObj.getID();

        JsonArray mJsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(NotificationBlockedUsersModel.PostID, postID);
        jsonObject.addProperty(NotificationBlockedUsersModel.UserID, mUserID);
        jsonObject.addProperty(NotificationBlockedUsersModel.ProfileID, ProfileID);
        mJsonArray.add(jsonObject);

        JsonObject mJsonObj = new JsonObject();
        mJsonObj.add("resource", mJsonArray);

        RetrofitClient.getRetrofitInstance().blockNotifications(((BaseActivity) mActivity), mJsonObj, RetrofitClient.BLOCK_NOTIFY);
    }

    public void unBlockNotification(int pos) {
        int postID = mPostsList.get(pos).getID();
        int profileID = mCurrentProfileObj.getID();
        String mFilter = "((ProfileID=" + profileID + ")AND(PostID=" + postID + "))";

        RetrofitClient.getRetrofitInstance().unBlockNotifications(((BaseActivity) mActivity), mFilter, RetrofitClient.UNBLOCK_NOTIFY);
    }

    //OnBindView
    @SuppressLint("SetTextI18n")
    private void setViewCount(ViewHolderPosts mViewHolderPost, int position) {
        String view_count;
        int val = 11;
        /*view_count = String.valueOf(mPostsList.get(position).getmViewCount() * val);
        String count = BaseActivity.convertToSuffix(Long.parseLong(view_count));
        mViewHolderPost.viewCountTxt.setText(count + " Views");*/
        view_count = String.valueOf(mPostsList.get(position).getmViewCount()); /** val);*/
        String count = BaseActivity.convertToSuffix(Long.parseLong(view_count));
        mViewHolderPost.viewCountTxt.setText(count + " Views");
    }

    //OnActivityResult
    public void updateView(int pos) {
        //notifyItemChanged(pos);
        notifyDataSetChanged();
    }

    //OnClick-Video
    private void updateViewCount(int pos) {
        String mFilter = "ID = " + mPostsList.get(pos).getID().toString();
        RetrofitClient.getRetrofitInstance().getViewCount(((BaseActivity) mActivity), mFilter, RetrofitClient.FEED_VIDEO_COUNT);
    }

    public void addViewCount(int count) {
        try {
            int videocount = count + 1;

            JsonArray mJsonArray = new JsonArray();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(PostsModel.POST_ID, mPostsList.get(mAdapterPosition).getID());
            jsonObject.addProperty(PostsModel.viewCount, videocount);
            mJsonArray.add(jsonObject);

            JsonObject mJsonObj = new JsonObject();
            mJsonObj.add("resource", mJsonArray);

            RetrofitClient.getRetrofitInstance().setViewCount(((BaseActivity) mActivity), mJsonObj, RetrofitClient.ADD_FEED_COUNT);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void ViewCount(int Count) {
        mPostsList.get(mAdapterPosition).setmViewCount(Count);
    }

    private void showShareLayout(final ViewHolderPosts mViewHolderPost, final int position, String mPostOwnerName) {
        mViewHolderPost.sharedProfileLay.setVisibility(View.VISIBLE);
        mViewHolderPost.sharedDownIcon.setVisibility(View.VISIBLE);
        mViewHolderPost.sharedNotifyIcon.setVisibility(View.VISIBLE);
        if (mPostsList.get(position).getSharedTxt().trim().isEmpty()) {
            mViewHolderPost.sharedPostContentTxt.setVisibility(View.GONE);
        } else {
            mViewHolderPost.sharedPostContentTxt.setVisibility(View.VISIBLE);
            try {
                if (!mPostsList.get(position).getSharedTxt().contains(" ")) {
                    mViewHolderPost.sharedPostContentTxt.setText(URLDecoder.decode(mPostsList.get(position).getSharedTxt(), "UTF-8"));
                } else {
                    mViewHolderPost.sharedPostContentTxt.setText(mPostsList.get(position).getSharedTxt());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*PostNotifications*/
        if (mPostsList.get(position).getmNotificationBlockedUsersID().size() > 0) {
            setPostNotifications(position, mViewHolderPost.sharedNotifyIcon);
        } else {
            postNotificationDefault(mViewHolderPost.sharedNotifyIcon);
        }
        mViewHolderPost.sharedNotifyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapterPosition = position;
                String imgtag = v.getTag().toString();
                if (!Utility.getInstance().isMultiClicked()) {
                    if (imgtag.equals(mContext.getString(R.string.notification_blocked))) {
                        unBlockNotification(position);
                    } else if (imgtag.equals(mContext.getString(R.string.notification_unblocked))) {
                        blockNotification(position);
                    }
                }
            }
        });
        mViewHolderPost.sharedProfileNameTxt.setTag(position);
        mViewHolderPost.sharedProfileNameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postProfileClick(v);
            }
        });
        if (String.valueOf(mPostsList.get(position).getProfileID()).trim()
                .equals(String.valueOf(mCurrentProfileObj.getID()).trim())) {
            mViewHolderPost.sharedDownIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> mPos = new ArrayList<>();
                    mPos.add(String.valueOf(position));
                    ((BaseActivity) mContext).showAppDialog(AppDialogFragment.BOTTOM_SHARED_POST_ACTION_DIALOG, mPos);
                }
            });
        } else {
            mViewHolderPost.sharedDownIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> mPos = new ArrayList<>();
                    mPos.add(String.valueOf(position));
                    ((BaseActivity) mContext).showAppDialog(AppDialogFragment.BOTTOM_REPORT_ACTION_DIALOG, mPos);
                }
            });
        }

        if (mPostsList.get(position).getUserType().trim().equals(AppConstants.VIDEO_SHARED_POST) || mPostsList.get(position).getUserType().trim().equals(AppConstants.USER_VIDEO_SHARED_POST)) {
            if (mPostsList.get(position).getVideoSharesByNewSharedPostID() != null) {
                VideoShareModel mFollowingsShareList = mPostsList.get(position).getVideoSharesByNewSharedPostID();
                mViewHolderPost.postTimeTxt.setText(Utility.getInstance().findTime(mFollowingsShareList.getSharedAt()));
            }
        } else {
            if (mPostsList.get(position).getNewSharedPost() != null) {
                FeedShareModel mFollowingsShareList = mPostsList.get(position).getNewSharedPost();
                mViewHolderPost.postTimeTxt.setText(Utility.getInstance().findTime(mFollowingsShareList.getSharedAt()));
            }
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

        ((BaseActivity) mContext).setImageWithGlide(mViewHolderPost.sharedUserImg, mPostsList.get(position).getProfilesByProfileID().getProfilePicture(), R.drawable.default_profile_icon);

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

        mViewHolderPost.sharedProfileNameTxt.setText(mWordToSpan);
        mViewHolderPost.sharedProfileNameTxt.setTag(position);
        mViewHolderPost.sharedProfileNameTxt.setMovementMethod(LinkMovementMethod.getInstance());
        if (mPostsList.get(position).getDateCreatedAt() != null)
            mViewHolderPost.sharedPostTimeTxt.setText(Utility.getInstance().findTime(mPostsList.get(position).getDateCreatedAt()));
        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mViewHolderPost.profileLay.getLayoutParams();

        //relativeParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.size80);

        relativeParams.height = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
        relativeParams.width = ViewGroup.MarginLayoutParams.MATCH_PARENT;

        relativeParams.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.size24),
                mContext.getResources().getDimensionPixelSize(R.dimen.size5),
                mContext.getResources().getDimensionPixelSize(R.dimen.size24),
                mContext.getResources().getDimensionPixelSize(R.dimen.size0));

        mViewHolderPost.profileLay.setLayoutParams(relativeParams);
        mViewHolderPost.profileLay.requestLayout();

        ViewGroup.MarginLayoutParams relativeParams1 = (ViewGroup.MarginLayoutParams) mViewHolderPost.imgVideoLay.getLayoutParams();

        relativeParams1.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.size20),
                mContext.getResources().getDimensionPixelSize(R.dimen.size10),
                mContext.getResources().getDimensionPixelSize(R.dimen.size20),
                mContext.getResources().getDimensionPixelSize(R.dimen.size0));

        relativeParams1.height = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
        relativeParams1.width = ViewGroup.MarginLayoutParams.MATCH_PARENT;

        mViewHolderPost.imgVideoLay.setLayoutParams(relativeParams1);
        mViewHolderPost.imgVideoLay.requestLayout();

        mViewHolderPost.downIcon.setVisibility(View.GONE);
        mViewHolderPost.notifyIcon.setVisibility(View.GONE);

    }

    private void HideShareLayout(ViewHolderPosts mViewHolderPost, int position) {

        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mViewHolderPost.profileLay.getLayoutParams();

//        relativeParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.size80);
        relativeParams.height = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
        relativeParams.width = ViewGroup.MarginLayoutParams.MATCH_PARENT;

        relativeParams.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.size10),
                mContext.getResources().getDimensionPixelSize(R.dimen.size0),
                mContext.getResources().getDimensionPixelSize(R.dimen.size10),
                mContext.getResources().getDimensionPixelSize(R.dimen.size0));

        mViewHolderPost.profileLay.setLayoutParams(relativeParams);
        mViewHolderPost.profileLay.requestLayout();

        ViewGroup.MarginLayoutParams relativeParams1 = (ViewGroup.MarginLayoutParams) mViewHolderPost.imgVideoLay.getLayoutParams();

        relativeParams1.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.size0),
                mContext.getResources().getDimensionPixelSize(R.dimen.size10),
                mContext.getResources().getDimensionPixelSize(R.dimen.size0),
                mContext.getResources().getDimensionPixelSize(R.dimen.size0));

        relativeParams1.height = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
        relativeParams1.width = ViewGroup.MarginLayoutParams.MATCH_PARENT;

        mViewHolderPost.imgVideoLay.setLayoutParams(relativeParams1);
        mViewHolderPost.imgVideoLay.requestLayout();

        mViewHolderPost.sharedProfileLay.setVisibility(View.GONE);
        mViewHolderPost.shareCountTxt.setVisibility(View.GONE);

    }

    private void setPostComments(ViewHolderPosts mViewHolderPost, int position) {

        String resComments;

        if (mPostsList.get(position).getPostComments().size() == 1) {
            resComments = mPostsList.get(position).getPostComments().size() + " Comment ";
        } else {
            resComments = mPostsList.get(position).getPostComments().size() + " Comments ";
        }

        mViewHolderPost.commentsCountTxt.setText(resComments);

    }

    private void setLikeUnLikeForPost(ViewHolderPosts mViewHolderPost, int position) {
        ArrayList<FeedLikesModel> mFeedLikes = mPostsList.get(position).getPostLikes();
        String resLikes;
        if (mPostsList.get(position).getPostLikes().size() == 1) {
            resLikes = mPostsList.get(position).getPostLikes().size() + " Like";
        } else {
            resLikes = mPostsList.get(position).getPostLikes().size() + " Likes";
        }
        mViewHolderPost.likeCountTxt.setText(resLikes);
        for (final FeedLikesModel likesEntity : mFeedLikes) {
            if ((likesEntity.getOwnerID() == mCurrentProfileObj.getID())) {
                mViewHolderPost.likeBtn.setImageResource(R.drawable.liked_icon);
                mViewHolderPost.likeBtn.setTag("unlike");
                break;
            } else {
                mViewHolderPost.likeBtn.setImageResource(R.drawable.like_icon);
                mViewHolderPost.likeBtn.setTag("like");
            }
        }
    }

    private void postProfileClick(View view) {
        int selPos = (int) view.getTag();
        if (mCurrentProfileObj != null) {
            if (mPostsList.get(selPos).getNewSharedPostID() == null) {
                if (mPostsList.get(selPos).getProfileID() == mCurrentProfileObj.getID()) {
                    if (!(mContext instanceof MyMotoFileActivity)) {
                        ((BaseActivity) mContext).moveMyProfileScreenWithResult(mContext, 0, AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                    }
                    return;
                }
            } else {
                if (mPostsList.get(selPos).getWhoPostedProfileID() == mCurrentProfileObj.getID()) {
                    if (!(mContext instanceof MyMotoFileActivity)) {
                        ((BaseActivity) mContext).moveMyProfileScreenWithResult(mContext, 0, AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                    }
                    return;
                }
            }
        }

        String mPostUserType = mPostsList.get(selPos).getUserType().trim();
        if (mPostUserType.equals(PromotersModel.PROMOTER) || mPostUserType.equals(PromotersModel.NEWS_AND_MEDIA) ||
                mPostUserType.equals(PromotersModel.TRACK) || mPostUserType.equals(PromotersModel.CLUB)
                || mPostUserType.equals(PromotersModel.SHOP) || mPostUserType.equals(AppConstants.SHARED_POST) || mPostUserType.equals(AppConstants.VIDEO_SHARED_POST)) {
            Class mClassName;
            @SuppressWarnings("UnusedAssignment") String mUserType = "";
            if (mPostsList.get(selPos).getPromoterByWhoPostedProfileID() != null) {
                //mBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, mPostsList.get(selPos).getPromoterByWhoPostedProfileID());
                //MotoHub.getApplicationInstance().setmPromoterResModel(mPostsList.get(selPos).getPromoterByWhoPostedProfileID());
                EventBus.getDefault().postSticky(mPostsList.get(selPos).getPromoterByWhoPostedProfileID());
                mUserType = mPostsList.get(selPos)
                        .getPromoterByWhoPostedProfileID().getUserType();
            } else {
                //mBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, mPostsList.get(selPos).getPromoterByProfileID());
                //MotoHub.getApplicationInstance().setmPromoterResModel(mPostsList.get(selPos).getPromoterByProfileID());
                if (mPostsList.get(selPos).getPromoterByProfileID() != null) {
                    EventBus.getDefault().postSticky(mPostsList.get(selPos).getPromoterByProfileID());
                    mUserType = mPostsList.get(selPos)
                            .getPromoterByProfileID().getUserType();
                }
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

            /*mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mCurrentProfileObj);
            ((BaseActivity) mContext).startActivityForResult(new Intent(mContext, mClassName).putExtras(mBundle), AppConstants.FOLLOWERS_FOLLOWING_RESULT);*/
            //MotoHub.getApplicationInstance().setmProfileResModel(mCurrentProfileObj);
            EventBus.getDefault().postSticky(mCurrentProfileObj);
            ((BaseActivity) mContext).startActivityForResult(new Intent(mContext, mClassName), AppConstants.FOLLOWERS_FOLLOWING_RESULT);

        } else {
            ((BaseActivity) mContext).moveOtherProfileScreen(mContext, mCurrentProfileObj.getID(),
                    mPostsList.get(selPos).getProfilesByWhoPostedProfileID().getID());
        }
    }

    private void sharedPostProfileClick(View view) {
        int selPos = (int) view.getTag();
        int mSharedProfileID;
        if (Integer.parseInt(mPostsList.get(selPos).getWhoSharedProfileID())
                == mCurrentProfileObj.getID()) {
            if (!(mContext instanceof MyMotoFileActivity)) {
                ((BaseActivity) mContext).moveMyProfileScreenWithResult(mContext, 0, AppConstants.FOLLOWERS_FOLLOWING_RESULT);
            }
            return;
        }

        if (mPostsList.get(selPos).getUserType().trim().equals(AppConstants.VIDEO_SHARED_POST) || mPostsList.get(selPos).getUserType().trim().equals(AppConstants.USER_VIDEO_SHARED_POST)) {

            mSharedProfileID = mPostsList.get(selPos).getVideoSharesByNewSharedPostID().getProfiles_by_ProfileID().getID();
        } else {
            mSharedProfileID = mPostsList.get(selPos).getNewSharedPost().getProfiles_by_ProfileID().getID();

        }
        ((BaseActivity) mContext).moveOtherProfileScreen(mContext, mCurrentProfileObj.getID(),
                mSharedProfileID);

    }

    public void resetCommentAdapter(FeedCommentModel feedCommentModel) {

        ArrayList<FeedCommentModel> mCommentList = mPostsList.get(mAdapterPosition).getPostComments();
        mCommentList.add(feedCommentModel);
        mPostsList.get(mAdapterPosition).setFeedComments(mCommentList);
        //notifyDataSetChanged();
        notifyItemChanged(mAdapterPosition);

    }

    public void resetUnBlock(NotificationBlockedUsersModel mNotify) {
        ArrayList<NotificationBlockedUsersModel> mNotifylist = mPostsList.get(mAdapterPosition).getmNotificationBlockedUsersID();
        Iterator<NotificationBlockedUsersModel> iterator = mNotifylist.iterator();
        while (iterator.hasNext()) {
            NotificationBlockedUsersModel next = iterator.next();
            if ((next.getmProfileID() == mNotify.getmProfileID()) && (next.getmPostID() == mNotify.getmPostID())) {
                iterator.remove();
                mNotifylist.remove(next);
            }
        }
        mPostsList.get(mAdapterPosition).setmNotificationBlockedUsersID(mNotifylist);
        //notifyDataSetChanged();
        notifyItemChanged(mAdapterPosition);
    }

    public void resetBlock(NotificationBlockedUsersModel mNotify) {
        ArrayList<NotificationBlockedUsersModel> mNotifylist = mPostsList.get(mAdapterPosition).getmNotificationBlockedUsersID();
        mNotifylist.add(mNotify);
        mPostsList.get(mAdapterPosition).setmNotificationBlockedUsersID(mNotifylist);
        //notifyDataSetChanged();
        notifyItemChanged(mAdapterPosition);
    }

    public void resetLikeAdapter(FeedLikesModel feedLikesModel) {
        ArrayList<FeedLikesModel> mFeedLikesList = mPostsList.get(mAdapterPosition).getPostLikes();
        mFeedLikesList.add(feedLikesModel);
        mPostsList.get(mAdapterPosition).setPostLikes(mFeedLikesList);
        //notifyDataSetChanged();
        notifyItemChanged(mAdapterPosition);
    }

    public void resetDisLike(FeedLikesModel feedLikesModel) {
        ArrayList<FeedLikesModel> mFeedLikesList = mPostsList.get(mAdapterPosition).getPostLikes();
        Iterator<FeedLikesModel> iterator = mFeedLikesList.iterator();
        while (iterator.hasNext()) {
            FeedLikesModel next = iterator.next();
            if (next.getId() == (feedLikesModel.getId())) {
                iterator.remove();
                mFeedLikesList.remove(next);
            }
        }
        mPostsList.get(mAdapterPosition).setPostLikes(mFeedLikesList);
        //notifyDataSetChanged();
        notifyItemChanged(mAdapterPosition);
    }

    public void resetShareAdapter(FeedShareModel feedShareModel) {
        ArrayList<FeedShareModel> mShareList = mPostsList.get(mAdapterPosition + 1).getPostShares();
        mShareList.add(feedShareModel);
        mPostsList.get(mAdapterPosition + 1).setPostShares(mShareList);
        //notifyDataSetChanged();
        notifyItemChanged(mAdapterPosition);
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
        FeedLikesAdapter mFeedLikesAdapter = new FeedLikesAdapter(mContext, mFeedLikeList, mCurrentProfileObj);
        mFeedLikesListView.setAdapter(mFeedLikesAdapter);
        mFeedLikesListView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void setFeedShareAdapter(ArrayList<FeedShareModel> mFeedShareList) {
        FeedSharesAdapter mFeedShareAdapter = new FeedSharesAdapter(mContext, mFeedShareList, mCurrentProfileObj);
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
        ArrayList<FeedShareModel> mResFeedShareList = mPostsList.get(mAdapterPosition).getPostShares();
        mResFeedShareList.add(mSharedFeed);
        mPostsList.get(mAdapterPosition).setPostShares(mResFeedShareList);
        //notifyDataSetChanged();
        notifyItemChanged(mAdapterPosition);
    }

    private void setGlideImage(ImageView imgView, String imgUrl, int drawable) {
      /*  GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + imgUrl, new LazyHeaders.Builder()
                .addHeader("X-DreamFactory-Api-Key", mContext.getString(R.string.dream_factory_api_key))
                .build());  */
        GlideUrl glideUrl = new GlideUrl(UrlUtils.AWS_FILE_URL + imgUrl, new LazyHeaders.Builder()
                .addHeader("X-DreamFactory-Api-Key", mContext.getString(R.string.dream_factory_api_key))
                .build());

        Glide.with(mContext)
                .load(glideUrl)
                .apply(new RequestOptions()
                        .error(drawable)
                        .dontAnimate())
                .into(imgView);
    }

    public void refreshCommentList(ArrayList<FeedCommentModel> mCommentList) {
        Collections.reverse(mCommentList);
        mPostsList.get(mAdapterPosition).setFeedComments(mCommentList);
        //notifyDataSetChanged();
        notifyItemChanged(mAdapterPosition);
    }

    private String[] mergeArrayList(String[]... arrays) {
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

    class ViewHolderPosts extends RecyclerView.ViewHolder {
        @BindView(R.id.imgVideoLay)
        RelativeLayout imgVideoLay;
        @BindView(R.id.imgVideoView)
        RelativeLayout imgVideoView;

        @BindView(R.id.top_lay)
        LinearLayout mTopLay;
        @BindView(R.id.bottom_lay)
        LinearLayout mBottomLay;
        @BindView(R.id.more_lay)
        LinearLayout mMoreLay;
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
        @BindView(R.id.playicon1)
        ImageView playicon1;
        @BindView(R.id.playicon2)
        ImageView playicon2;
        @BindView(R.id.playicon3)
        ImageView playicon3;
        @BindView(R.id.playicon4)
        ImageView playicon4;


        private RelativeLayout sharedProfileLay;
        private CircleImageView sharedUserImg;
        private TextView sharedProfileNameTxt;
        private TextView sharedPostTimeTxt;
        private TextView sharedPostContentTxt;
        private ImageView sharedNotifyIcon;
        private ImageView sharedDownIcon;

        private RelativeLayout profileLay;
        private CircleImageView userImg;
        private TextView profileNameTxt;
        private TextView postTimeTxt;
        private TextView postContentTxt;
        private ImageView notifyIcon;
        private ImageView downIcon;

        private RelativeLayout commentView;
        private ImageView commentUserImg;
        private TextView commentUserNameTxt;
        private TextView commentContentTxt;

        private ConstraintLayout countView;
        private TextView shareCountTxt;
        private TextView likeCountTxt;
        private TextView viewCountTxt;
        private TextView commentsCountTxt;

        private ImageView likeBtn;
        private ImageView commentBtn;
        private ImageView shareBtn;


        ViewHolderPosts(View view) {
            super(view);
            ButterKnife.bind(this, view);

            mTotalImgCountTv = view.findViewById(R.id.img_count_txt);

            sharedProfileLay = view.findViewById(R.id.sharedProfileLay);
            sharedUserImg = view.findViewById(R.id.sharedUserImg);
            sharedProfileNameTxt = view.findViewById(R.id.sharedProfileNameTxt);
            sharedPostTimeTxt = view.findViewById(R.id.sharedPostTimeTxt);
            sharedPostContentTxt = view.findViewById(R.id.sharedPostContentTxt);
            sharedDownIcon = view.findViewById(R.id.sharedDownIcon);
            sharedNotifyIcon = view.findViewById(R.id.sharedNotifyIcon);

            profileLay = view.findViewById(R.id.profileLay);
            userImg = view.findViewById(R.id.userImg);
            profileNameTxt = view.findViewById(R.id.profileNameTxt);
            postTimeTxt = view.findViewById(R.id.postTimeTxt);
            postContentTxt = view.findViewById(R.id.postContentTxt);
            notifyIcon = view.findViewById(R.id.notifyIcon);
            downIcon = view.findViewById(R.id.downIcon);

            commentView = view.findViewById(R.id.commentView);
            commentUserImg = view.findViewById(R.id.commentUserImg);
            commentUserNameTxt = view.findViewById(R.id.commentUserNameTxt);
            commentContentTxt = view.findViewById(R.id.commentContentTxt);

            countView = view.findViewById(R.id.countView);
            likeCountTxt = view.findViewById(R.id.likeCountTxt);
            viewCountTxt = view.findViewById(R.id.viewCountTxt);
            commentsCountTxt = view.findViewById(R.id.commentsCountTxt);
            shareCountTxt = view.findViewById(R.id.shareCountTxt);

            likeBtn = view.findViewById(R.id.likeBtn);
            commentBtn = view.findViewById(R.id.commentBtn);
            shareBtn = view.findViewById(R.id.shareBtn);

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