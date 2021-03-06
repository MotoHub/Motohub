package online.motohub.adapter.promoter;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.OnDemanAudoVideoView;
import online.motohub.activity.PromoterVideoGalleryActivity;
import online.motohub.activity.ReportActivity;
import online.motohub.activity.club.ClubProfileActivity;
import online.motohub.activity.news_and_media.NewsAndMediaProfileActivity;
import online.motohub.activity.performance_shop.PerformanceShopProfileActivity;
import online.motohub.activity.promoter.PromoterProfileActivity;
import online.motohub.activity.promoter.PromotersListActivity;
import online.motohub.activity.track.TrackProfileActivity;
import online.motohub.adapter.VideoLikesAdapter;
import online.motohub.adapter.VideoSharesAdapter;
import online.motohub.application.MotoHub;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.CommonReturnInterface;
import online.motohub.interfaces.RetrofitResInterface;
import online.motohub.model.GalleryImgModel;
import online.motohub.model.PostsModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PromoterVideoModel;
import online.motohub.model.VideoCommentsModel;
import online.motohub.model.VideoLikesModel;
import online.motohub.model.VideoShareModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.util.DialogManager;
import online.motohub.util.UrlUtils;
import online.motohub.util.Utility;

public class PromoterVideoPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_POSTS = 1;
    private ArrayList<PromoterVideoModel.Resource> mPostsList;
    // ArrayList<HashMap<String, String>> mResMap;
    private Context mContext;
    // private ArrayList<HashMap<String, String>> mVideoList;
    RetrofitResInterface mApiInterface = new RetrofitResInterface() {
        @Override
        public void retrofitOnResponse(Object responseObj, int responseType) {
            if (responseObj instanceof GalleryImgModel) {
                ((BaseActivity) mContext).showToast(mContext, "Video saved successfully into your videos");
            }
        }

        @Override
        public void retrofitOnError(int code, String message) {
            ((BaseActivity) mContext).showToast(mContext, message);
        }

        @Override
        public void retrofitOnSessionError(int code, String message) {
            ((BaseActivity) mContext).showToast(mContext, message);
        }

        @Override
        public void retrofitOnFailure() {
            ((BaseActivity) mContext).showToast(mContext, mContext.getString(R.string.internet_err));
        }
    };
    private int ProfileID;
    private Activity mActivity;
    private Dialog mCommentListPopup;
    private int mDeleteLikeID;
    private RecyclerView mFeedLikesListView;
    private ProfileResModel mCurrentProfileResModel;
    private ViewHolderPosts mViewHolderPost;
    private int mAdapterPosition;
    CommonReturnInterface mSaveMyVideoCallback = new CommonReturnInterface() {
        @Override
        public void onSuccess(int type) {
            if (type == 2) {
                //TODO call API
                JsonArray jsonElements = new JsonArray();
                JsonObject obj = new JsonObject();
                obj.addProperty(GalleryImgModel.USER_ID, mCurrentProfileResModel.getUserID());
                obj.addProperty(GalleryImgModel.PROFILE_ID, mCurrentProfileResModel.getID());
                obj.addProperty(GalleryImgModel.VIDEO_URL, mPostsList.get(mAdapterPosition).getVideoUrl());
                obj.addProperty(GalleryImgModel.THUMBNAIL, mPostsList.get(mAdapterPosition).getThumbnail());
                obj.addProperty(GalleryImgModel.USER_TYPE, AppConstants.USER);
                obj.addProperty(GalleryImgModel.CAPTION, mPostsList.get(mAdapterPosition).getCaption());
                jsonElements.add(obj);
                RetrofitClient.getRetrofitInstance().postVideoToGallery(mContext, mApiInterface, jsonElements, 1);
            } else {
                mActivity.startActivityForResult(
                        new Intent(mActivity, ReportActivity.class)
                                .putExtra(PostsModel.POST_ID, mPostsList.get(mAdapterPosition).getID())
                                .putExtra(ProfileModel.PROFILE_ID, mCurrentProfileResModel.getID())
                                .putExtra(ProfileModel.USER_ID, mCurrentProfileResModel.getUserID())
                                .putExtra(AppConstants.REPORT, AppConstants.REPORT_VIDEO),
                        AppConstants.REPORT_POST_SUCCESS);
            }
        }
    };

    public PromoterVideoPostAdapter(List<PromoterVideoModel.Resource> postsList, ProfileResModel mMyProfileResModel, Context ctx, int ProfileID) {
        this.mPostsList = (ArrayList<PromoterVideoModel.Resource>) postsList;
        this.mContext = ctx;
        this.mActivity = (Activity) ctx;
        this.mCurrentProfileResModel = mMyProfileResModel;
        this.ProfileID = ProfileID;
    }

    public void resetShareCount(VideoShareModel mSharedFeed) {
        ArrayList<VideoShareModel> mShareModel = mPostsList.get(mAdapterPosition).getVideoshares_by_OriginalVideoID();
        mShareModel.add(mSharedFeed);
        mPostsList.get(mAdapterPosition).setVideoshares_by_OriginalVideoID(mShareModel);
        //notifyDataSetChanged();
        notifyItemChanged(mAdapterPosition);
    }

    @Override
    public int getItemCount() {
        return mPostsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mPostsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_POSTS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView;
        switch (viewType) {
            case VIEW_TYPE_POSTS:
                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_promoter_video_item, parent, false);
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_POSTS:
                final PromoterVideoModel.Resource mModel = mPostsList.get(position);
                try {
                    mViewHolderPost = (ViewHolderPosts) holder;
                    mViewHolderPost.mPostPic.setVisibility(View.GONE);
                    mViewHolderPost.playicon.setVisibility(View.VISIBLE);
                    mViewHolderPost.mBottomArrowImgView.setVisibility(View.VISIBLE);
                    mViewHolderPost.mCommentViewLay.setVisibility(View.GONE);
                    mViewHolderPost.mCountLay.setVisibility(View.GONE);
                    mViewHolderPost.mProfileImg.setTag(position);
                    Glide.with(mContext).clear(mViewHolderPost.mProfileImg);
                    if (mModel.getUserType().trim().equals(AppConstants.ONDEMAND) || mModel.getUserType().trim().equals(AppConstants.USER) || mModel.getUserType().trim().equals(AppConstants.USER_EVENT_VIDEOS)) {
                        ((BaseActivity) mContext).setImageWithGlide(mViewHolderPost.mProfileImg, mModel.profiles_by_ProfileID.getProfilePicture(), R.drawable.default_profile_icon);
                        mViewHolderPost.mUsername.setText(Utility.getInstance().getUserName(mModel.profiles_by_ProfileID));
                    } else if (mModel.getPromoter_by_UserID() != null) {
                        ((BaseActivity) mContext).setImageWithGlide(mViewHolderPost.mProfileImg, mModel.promoter_by_UserID.getProfileImage(), R.drawable.default_profile_icon);
                        mViewHolderPost.mUsername.setText(mModel.promoter_by_UserID.getName());
                    }
                    if (mModel.getEvent_by_EventID().getID() != null && !mModel.getUserID().equals(mModel.getProfileID()))
                        mViewHolderPost.mUsername.setText(Utility.getInstance().getUserName(mModel.profiles_by_ProfileID) + " " + "IS " + "@ " + mModel.getPromoter_by_UserID().getName() + " " + mModel.getEvent_by_EventID().getName());
                    if (mModel.getCaption() == null || mModel.getCaption().isEmpty()) {
                        mViewHolderPost.mPostText.setVisibility(View.GONE);
                    } else {
                        try {
                            if (mModel.getCaption().contains(" ")) {
                                mViewHolderPost.mPostText.setText(mModel.getCaption());
                            } else {
                                mViewHolderPost.mPostText.setText(URLDecoder.decode(mModel.getCaption(), "UTF-8"));
                            }
                            //mViewHolderPost.mPostText.setText(sb.append(mModel.getCaption()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mViewHolderPost.mPostText.setVisibility(View.VISIBLE);
                    }
/*                    RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mViewHolderPost.mProfileDetailedLay.getLayoutParams();
                    relativeParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.size65);
                    relativeParams.width = ViewGroup.MarginLayoutParams.MATCH_PARENT;
                    relativeParams.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.size10), mContext.getResources().getDimensionPixelSize(R.dimen.size0), mContext.getResources().getDimensionPixelSize(R.dimen.size10), mContext.getResources().getDimensionPixelSize(R.dimen.size0));*/
                    // mViewHolderPost.mProfileDetailedLay.setLayoutParams(relativeParams);
                    mViewHolderPost.mProfileDetailedLay.requestLayout();
                    mViewHolderPost.mPostDate.setText(((BaseActivity) mContext).findTime(mPostsList.get(position).getCreatedAt()));
                    final String[] mVideoArray = getImgVideoList(mModel.getThumbnail());
                    if (mVideoArray == null) {
                        mViewHolderPost.mPostImageVideoBox.setVisibility(View.GONE);
                        mViewHolderPost.playicon.setVisibility(View.GONE);
                    } else {
                        mViewHolderPost.mPostImageVideoBox.setVisibility(View.VISIBLE);
                        mViewHolderPost.mPostPic.setVisibility(View.VISIBLE);
                        mViewHolderPost.mPostPicProgressBar.setVisibility(View.VISIBLE);
                        showVideoFile(mViewHolderPost, mVideoArray[0]);
                    }
                    if (mPostsList.get(position).getVideolikes_by_VideoID().size() > 0) {
                        setLikeUnLikeForPost(mViewHolderPost, position);
                    } else {
                        mViewHolderPost.mLikeBtn.setImageResource(R.drawable.like_icon);
                        mViewHolderPost.mLikeBtn.setTag("like");
                        mViewHolderPost.mLikeCountText.setText("");
                    }
                    if (mPostsList.get(position).getVideoComments_by_VideoID().size() > 0) {
                        setVideoComments(mViewHolderPost, position);
                    } else {
                        mViewHolderPost.mCommentCountTxt.setText(" ");
                    }
                    if (mPostsList.get(position).getVideolikes_by_VideoID().size() == 0
                            && mPostsList.get(position).getVideoComments_by_VideoID().size() == 0
                            && mPostsList.get(position).getVideoshares_by_OriginalVideoID().size() == 0
                            && mPostsList.get(position).getViewCount() == 0) {
                        mViewHolderPost.mCountLay.setVisibility(View.GONE);
                    } else {
                        mViewHolderPost.mCountLay.setVisibility(View.VISIBLE);
                    }
                    //View Count
                    if (mPostsList.get(position).getViewCount() > 0) {
                        setViewCount(mViewHolderPost, position);
                    }/* else {
                        mViewHolderPost.mViewCount.setVisibility(View.GONE);
                    }*/

                    ArrayList<VideoCommentsModel> mFeedCommentModel = mPostsList.get(position).getVideoComments_by_VideoID();

                    if (mFeedCommentModel.size() > 0) {
                        showPostCommentsLay(mViewHolderPost, mFeedCommentModel, position);
                    } else {
                        mViewHolderPost.mCommentViewLay.setVisibility(View.GONE);
                    }
                    String numberOfShares;
                    if (mPostsList.get(position).getVideoshares_by_OriginalVideoID().size() == 0) {
                        mViewHolderPost.mShareCountTxt.setVisibility(View.GONE);
                    } else if (mPostsList.get(position).getVideoshares_by_OriginalVideoID().size() == 1) {
                        numberOfShares = mPostsList.get(position).getVideoshares_by_OriginalVideoID().size() + " Share";
                        mViewHolderPost.mShareCountTxt.setText(numberOfShares);
                        mViewHolderPost.mShareCountTxt.setVisibility(View.VISIBLE);
                    } else {
                        numberOfShares = mPostsList.get(position).getVideoshares_by_OriginalVideoID().size() + " Shares";
                        mViewHolderPost.mShareCountTxt.setText(numberOfShares);
                        mViewHolderPost.mShareCountTxt.setVisibility(View.VISIBLE);
                    }
                    mViewHolderPost.mProfileImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCurrentProfileResModel != null && mPostsList.size() > 0 && mPostsList != null) {
                                try {
                                    if (mPostsList.get(position).getUserType().equals(AppConstants.ONDEMAND) || mPostsList.get(position).getUserType().equals(AppConstants.USER_EVENT_VIDEOS) || mPostsList.get(position).getUserType().equals(AppConstants.USER)) {
                                        if (Integer.parseInt(mPostsList.get(position).getProfileID()) == mCurrentProfileResModel.getID()) {
                                            ((BaseActivity) mContext).moveMyProfileScreenWithResult(mContext,
                                                    mCurrentProfileResModel.getID(), AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                                        } else {
                                            ((BaseActivity) mContext).moveOtherProfileScreen(mContext, mCurrentProfileResModel.getID(),
                                                    Integer.parseInt(mPostsList.get(position).getProfileID()));
                                        }
                                    } else {
                                        navigationToProfileActivity(position);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    mViewHolderPost.mPostImageVideoBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (mPostsList != null && mModel.getVideoUrl() != null && mCurrentProfileResModel != null) {
                                    Intent intent = new Intent(mContext, OnDemanAudoVideoView.class);
                                    intent.putExtra(AppConstants.POSITION, position);
                                    intent.putExtra("Filter", "EventID =" + ProfileID);
                                    String profile = new Gson().toJson(mCurrentProfileResModel);
                                    MotoHub.getApplicationInstance().setmPostsList(mPostsList);
                                    intent.putExtra("profile", profile);
                                    ((PromoterVideoGalleryActivity) mContext).startActivityForResult(intent, AppConstants.ONDEMAND_REQUEST);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    mViewHolderPost.mBottomArrowImgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAdapterPosition = position;
                            DialogManager.showSaveToMyVideos(mContext, mSaveMyVideoCallback, mCurrentProfileResModel.getUserID(), Integer.parseInt(mModel.getUserID()));
                        }
                    });
                    mViewHolderPost.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!((BaseActivity) mContext).isMultiClicked()) {
                                mAdapterPosition = position;
                                switch (view.getTag().toString()) {
                                    case "unlike":
                                        ArrayList<VideoLikesModel> mFeedLikeList = mPostsList.get(position).getVideolikes_by_VideoID();

                                        if (mFeedLikeList.size() > 0) {

                                            for (VideoLikesModel likesEntity : mFeedLikeList) {
                                                if (likesEntity.getOwnerID() == mCurrentProfileResModel.getID() && likesEntity.getFeedID() == mPostsList.get(position).getID()) {
                                                    mDeleteLikeID = likesEntity.getId();
                                                    break;
                                                }
                                            }
                                            callUnLikePost(mDeleteLikeID);
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
                            mAdapterPosition = position;
                            ((BaseActivity) mContext).moveVideoCommentScreen(mContext, mPostsList.get(mAdapterPosition).getID(), mCurrentProfileResModel);
                        }
                    });
                    mViewHolderPost.mCommentCountTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAdapterPosition = position;
                            ((BaseActivity) mContext).moveVideoCommentScreen(mContext, mPostsList.get(mAdapterPosition).getID(), mCurrentProfileResModel);
                        }
                    });
                    mViewHolderPost.mCommentBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAdapterPosition = position;
                            ((BaseActivity) mContext).moveVideoCommentScreen(mContext, mPostsList.get(mAdapterPosition).getID(), mCurrentProfileResModel);
                        }
                    });
                    mViewHolderPost.mLikeCountText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ArrayList<VideoLikesModel> mLikeModel = mPostsList.get(position).getVideolikes_by_VideoID();
                            mAdapterPosition = position;
                            showLikeListPopup(mContext.getString(R.string.likes));
                            setFeedLikeAdapter(mLikeModel);

                        }
                    });
                    mViewHolderPost.mShareCountTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ArrayList<VideoShareModel> mShareModel;
                            mAdapterPosition = position;
                            if (mPostsList.get(position).getVideoshares_by_OriginalVideoID() != null) {
                                mShareModel = mPostsList.get(position).getVideoshares_by_OriginalVideoID();
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
                            String content = null;
                            mAdapterPosition = position;
                            String mMyFollowingsID = Utility.getInstance().getMyFollowersFollowingsID(mCurrentProfileResModel.getFollowprofile_by_ProfileID(), false);
                            if (mMyFollowingsID != null) {
                                try {
                                    /*if (mPostsList.get(position).getCaption().contains(" "))
                                        content = mPostsList.get(position).getCaption();
                                    else*/
                                    content = URLDecoder.decode(mPostsList.get(position).getCaption(), "UTF-8");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                boolean isVideoFile;
                                String[] mVideoList = getImgVideoList(mPostsList.get(position).getVideoUrl());
                                if (mVideoList == null) {
                                    isVideoFile = false;
                                } else {
                                    isVideoFile = !(mVideoList.length == 1 && mVideoList[0].trim().equals(""));
                                }
                                if (isVideoFile) {
                                    String[] mVideosList = getImgVideoList(mPostsList.get(position).getVideoUrl());
                                    ((BaseActivity) mContext).showFBShareDialog(AppDialogFragment.BOTTOM_SHARE_DIALOG, content, null, mVideosList, position, true);
                                } else {
                                    ((BaseActivity) mContext).showFBShareDialog(AppDialogFragment.BOTTOM_SHARE_DIALOG, content, null, null, position, true);
                                }
                            } else {
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.check_follower), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case VIEW_TYPE_LOADING:
                final ViewHolderLoader mViewHolderLoader = (ViewHolderLoader) holder;
                mViewHolderLoader.mProgressBar.setIndeterminate(true);
                break;
            default:
                break;
        }
    }

    //OnBindView
    private void setViewCount(ViewHolderPosts mViewHolderPost, int position) {
        String view_count;
        int val = 11;
        /*view_count = String.valueOf(mPostsList.get(position).getViewCount() * val) + " Views";
        mViewHolderPost.mViewCount.setText(view_count);*/
        view_count = String.valueOf(mPostsList.get(position).getViewCount() * val);
        String count = BaseActivity.convertToSuffix(Long.parseLong(view_count));
        mViewHolderPost.mViewCount.setText(count + " Views");
    }

    private void showPostCommentsLay(ViewHolderPosts mViewHolderPost, ArrayList<VideoCommentsModel> mFeedCommentModel, int position) {
        int latestPostion = 0;
        if (mFeedCommentModel != null && mFeedCommentModel.size() > 0) {
            latestPostion = mFeedCommentModel.size() - 1;
            mViewHolderPost.mCommentViewLay.setVisibility(View.VISIBLE);
            String mCommentText = mFeedCommentModel.get(latestPostion).getmComment().trim();
            String mCommentImgUrl = mFeedCommentModel.get(latestPostion).getCommentImages().trim();
            if (!mCommentImgUrl.isEmpty()) {
                mViewHolderPost.mCommentTxt.setVisibility(View.VISIBLE);
                mViewHolderPost.mCommentTxt.setText("Commented to this post.");
            } else if (!mCommentText.isEmpty()) {
                mViewHolderPost.mCommentTxt.setVisibility(View.VISIBLE);
                String mTemp = "";
                try {
                    if (mCommentText.contains(" ")) {
                        mTemp = mCommentText;
                    } else {
                        mTemp = URLDecoder.decode(mCommentText, "UTF-8");
                    }
                    //mTemp = replacer(sb.append(mCommentText));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mViewHolderPost.mCommentTxt.setText(((BaseActivity) mContext).setTextEdt(mContext, mTemp, mFeedCommentModel.get(latestPostion).getCommentTaggedUserNames(), mFeedCommentModel.get(latestPostion).getCommentTaggedUserID(), mCurrentProfileResModel.getID()));
            } else {
                mViewHolderPost.mCommentTxt.setVisibility(View.GONE);
            }
            if (mFeedCommentModel.get(latestPostion).getProfiles_by_ProfileID() != null)
                mViewHolderPost.mCommentUserName.setText(Utility.getInstance().getUserName(mFeedCommentModel.get(latestPostion).getProfiles_by_ProfileID()));
            if (mFeedCommentModel.get(latestPostion).getProfiles_by_ProfileID() != null && mFeedCommentModel.get(latestPostion).getProfiles_by_ProfileID().getProfilePicture() != null)
                ((BaseActivity) mContext).setImageWithGlide(mViewHolderPost.mCommentImg, mFeedCommentModel.get(latestPostion).getProfiles_by_ProfileID().getProfilePicture(), R.drawable.default_profile_icon);
            mViewHolderPost.mCommentImg.setTag(position);
        }
    }


    private void setVideoComments(ViewHolderPosts mViewHolderPost, int position) {
        String resComments;
        if (mPostsList.get(position).getVideoComments_by_VideoID().size() == 1) {
            resComments = mPostsList.get(position).getVideoComments_by_VideoID().size() + " Comment ";
        } else {
            resComments = mPostsList.get(position).getVideoComments_by_VideoID().size() + " Comments ";
        }
        mViewHolderPost.mCommentCountTxt.setText(resComments);
    }

    private void setLikeUnLikeForPost(ViewHolderPosts mViewHolderPost, int position) {
        ArrayList<VideoLikesModel> mFeedLikes = mPostsList.get(position).getVideolikes_by_VideoID();
        String resLikes;
        if (mPostsList.get(position).getVideolikes_by_VideoID().size() == 1) {
            resLikes = mPostsList.get(position).getVideolikes_by_VideoID().size() + " Like";
        } else {
            resLikes = mPostsList.get(position).getVideolikes_by_VideoID().size() + " Likes";
        }
        mViewHolderPost.mLikeCountText.setText(resLikes);
        for (final VideoLikesModel likesEntity : mFeedLikes) {
            try {
                if ((likesEntity.getOwnerID() == mCurrentProfileResModel.getID())) {
                    mViewHolderPost.mLikeBtn.setImageResource(R.drawable.liked_icon);
                    mViewHolderPost.mLikeBtn.setTag("unlike");
                    break;
                } else {
                    mViewHolderPost.mLikeBtn.setImageResource(R.drawable.like_icon);
                    mViewHolderPost.mLikeBtn.setTag("like");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void refreshCommentList(ArrayList<VideoCommentsModel> mCommentList) {
        Collections.reverse(mCommentList);
        mPostsList.get(mAdapterPosition).setVideoComments_by_VideoID(mCommentList);
        //notifyDataSetChanged();
        notifyItemChanged(mAdapterPosition);
    }

    private void showVideoFile(final ViewHolderPosts mViewHolderPost, String videoThumbnail) {
        mViewHolderPost.mPostImageVideoBox.setVisibility(View.VISIBLE);
        mViewHolderPost.mPostPic.setVisibility(View.VISIBLE);
        mViewHolderPost.mPostPicProgressBar.setVisibility(View.VISIBLE);

        GlideUrl glideUrl = new GlideUrl(UrlUtils.AWS_S3_BASE_URL + videoThumbnail, new LazyHeaders
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
                .apply(new RequestOptions().dontAnimate().error(R.drawable.video_place_holder).placeholder(R.drawable.video_place_holder))
                .into(mViewHolderPost.mPostPic);
    }

    private String[] getImgVideoList(String str) {
        String[] mArray = null;
        if (str != null && !str.isEmpty()) {
            str = str.replace("]", "")
                    .replace("[", "")
                    .replace("\n", "")
                    .replace("\"", "");
            mArray = str.split(",");
        }
        if (mArray.length == 1) {
            if (mArray[0].trim().isEmpty())
                mArray = null;
        }
        return mArray;
    }

    public void resetCommentAdapter(VideoCommentsModel feedCommentModel) {
        ArrayList<VideoCommentsModel> mCommentList = mPostsList.get(mAdapterPosition).getVideoComments_by_VideoID();
        mCommentList.add(feedCommentModel);
        mPostsList.get(mAdapterPosition).setVideoComments_by_VideoID(mCommentList);
        //notifyDataSetChanged();
        notifyItemChanged(mAdapterPosition);
    }

    public void resetLikeAdapter(VideoLikesModel feedLikesModel) {
        ArrayList<VideoLikesModel> mFeedLikesList = mPostsList.get(mAdapterPosition).getVideolikes_by_VideoID();
        mFeedLikesList.add(feedLikesModel);
        mPostsList.get(mAdapterPosition).setVideolikes_by_VideoID(mFeedLikesList);
        //notifyDataSetChanged();
        notifyItemChanged(mAdapterPosition);
    }

    public void resetDisLike(VideoLikesModel feedLikesModel) {
        ArrayList<VideoLikesModel> mFeedLikesList = mPostsList.get(mAdapterPosition).getVideolikes_by_VideoID();
        Iterator<VideoLikesModel> iterator = mFeedLikesList.iterator();
        while (iterator.hasNext()) {
            VideoLikesModel next = iterator.next();
            if (next.getId() == (feedLikesModel.getId())) {
                iterator.remove();
                mFeedLikesList.remove(next);
            }
        }
        mPostsList.get(mAdapterPosition).setVideolikes_by_VideoID(mFeedLikesList);
        //notifyDataSetChanged();
        notifyItemChanged(mAdapterPosition);
    }

    private void callUnLikePost(Integer profileId) {
        RetrofitClient.getRetrofitInstance().callUnLikeForVideos(((BaseActivity) mActivity), profileId, RetrofitClient.VIDEO_UNLIKE);
    }

    private void callLikePost(int postId, int profileId) {
        JsonObject mJsonObject = new JsonObject();
        JsonObject mItem = new JsonObject();
        mItem.addProperty("VideoID", postId);
        mItem.addProperty("ProfileID", profileId);
        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mItem);
        mJsonObject.add("resource", mJsonArray);
        RetrofitClient.getRetrofitInstance().postLikesForVideos(((BaseActivity) mActivity), mJsonObject, RetrofitClient.VIDEO_LIKES);
    }

    private void setFeedLikeAdapter(ArrayList<VideoLikesModel> mFeedLikeList) {
        VideoLikesAdapter mFeedLikesAdapter = new VideoLikesAdapter(mContext, mFeedLikeList, mCurrentProfileResModel);
        mFeedLikesListView.setAdapter(mFeedLikesAdapter);
        mFeedLikesListView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void setFeedShareAdapter(ArrayList<VideoShareModel> mFeedShareList) {
        VideoSharesAdapter mFeedShareAdapter = new VideoSharesAdapter(mContext, mFeedShareList, mCurrentProfileResModel);
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

    private ArrayList<HashMap<String, String>> convertModelToList(List<PromoterVideoModel.Resource> mPostsList) {
        ArrayList<HashMap<String, String>> mVideoList = new ArrayList<>();
        try {
            for (PromoterVideoModel.Resource mResource : mPostsList) {
                HashMap<String, String> mMapData = new HashMap<>();
                mMapData.put(AppConstants.VIDEO_PATH, mResource.getVideoUrl());
                mMapData.put(AppConstants.CAPTION, mResource.getCaption());
                mVideoList.add(mMapData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mVideoList;
    }

    private void navigationToProfileActivity(int layoutPosition) {
        Bundle mBundle = new Bundle();
        String mUserType = mPostsList.get(layoutPosition).getUserType().trim();
        if (mPostsList.get(layoutPosition)
                .getPromoter_by_UserID() != null) {
            Class mClassName;
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
            /*mBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, mPostsList.get(layoutPosition)
                    .getPromoter_by_UserID());
            mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mCurrentProfileResModel);
            ((BaseActivity) mContext).startActivityForResult(
                    new Intent(mContext, mClassName).putExtras(mBundle),
                    PromotersListActivity.PROMOTER_FOLLOW_RESPONSE);*/
            /*MotoHub.getApplicationInstance().setmProfileResModel(mCurrentProfileResModel);
            MotoHub.getApplicationInstance().setmPromoterResModel(mPostsList.get(layoutPosition)
                    .getPromoter_by_UserID());*/
            EventBus.getDefault().postSticky(mCurrentProfileResModel);
            EventBus.getDefault().postSticky(mPostsList.get(layoutPosition)
                    .getPromoter_by_UserID());

            ((BaseActivity) mContext).startActivityForResult(
                    new Intent(mContext, mClassName),
                    PromotersListActivity.PROMOTER_FOLLOW_RESPONSE);
        }
    }

    public void updateList(ArrayList<PromoterVideoModel.Resource> list) {
        mPostsList = list;
        this.notifyDataSetChanged();
    }

    public interface TotalRetrofitPostsResultCount {
        int getTotalPostsResultCount();
    }

    class ViewHolderPosts extends RecyclerView.ViewHolder {

        @BindView(R.id.circular_img_view)
        CircleImageView mProfileImg;

        @BindView(R.id.top_tv)
        TextView mUsername;

        @BindView(R.id.bottom_tv)
        TextView mPostDate;

        @BindView(R.id.post_text_tv)
        TextView mPostText;

        @BindView(R.id.post_picture_img_view)
        ImageView mPostPic;

        @BindView(R.id.down_arrow)
        ImageView mBottomArrowImgView;

        @BindView(R.id.profile_details_box)
        RelativeLayout mProfileDetailedLay;


        @BindView(R.id.likeBtn)
        ImageView mLikeBtn;

        @BindView(R.id.commentBtn)
        ImageView mCommentBtn;

        @BindView(R.id.shareBtn)
        ImageView mShareBtn;

        @BindView(R.id.comment_view_lay)
        RelativeLayout mCommentViewLay;

        @BindView(R.id.view_count_txt)
        TextView mViewCount;

        @BindView(R.id.share_count_txt)
        TextView mShareCountTxt;

        @BindView(R.id.rl_count)
        RelativeLayout mCountLay;

        @BindView(R.id.like_count_txt)
        TextView mLikeCountText;

        @BindView(R.id.comments_comment_txt)
        TextView mCommentCountTxt;

        @BindView(R.id.postImageVideoBox)
        RelativeLayout mPostImageVideoBox;

        @BindView(R.id.playicon)
        ImageView playicon;

        @BindView(R.id.smallProgressBar)
        ProgressBar mPostPicProgressBar;

        @BindView(R.id.comment_txt)
        TextView mCommentTxt;

        @BindView(R.id.comment_img)
        ImageView mCommentImg;

        @BindView(R.id.comment_user_name_txt)
        TextView mCommentUserName;

        ViewHolderPosts(View view) {
            super(view);
            ButterKnife.bind(this, view);
            // mProfileImg.setOnClickListener(this);
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