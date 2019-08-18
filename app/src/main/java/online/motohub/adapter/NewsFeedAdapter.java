package online.motohub.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.text.TextUtils;
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

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.TaggedProfilesListActivity;
import online.motohub.constants.AppConstants;
import online.motohub.constants.ModelConstants;
import online.motohub.fragment.BaseFragment;
import online.motohub.interfaces.AdapterClickCallBack;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.NotificationBlockedUsersModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.VideoShareModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.tags.AdapterTag;
import online.motohub.util.UrlUtils;
import online.motohub.util.Utility;

public class NewsFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_POSTS = 1;
    private final ArrayList<PostsResModel> feedsList;
    private Context context;
    private ProfileResModel myProfile;
    private int adapPos;
    private RecyclerView mFeedLikesListView;

    private AdapterClickCallBack callBack;
    private BaseFragment fragment;

    public NewsFeedAdapter(Context ctx, ArrayList<PostsResModel> postsList, ProfileResModel myProfile,
                           AdapterClickCallBack callBack, BaseFragment fragment) {
        this.context = ctx;
        this.feedsList = postsList;
        this.myProfile = myProfile;
        this.callBack = callBack;
        this.fragment = fragment;

    }

    @Override
    public int getItemCount() {
        return feedsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= feedsList.size()) {
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
                return new Holder(mView);
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        switch (getItemViewType(position)) {

            case VIEW_TYPE_POSTS:
                try {
                    Holder holder = (Holder) viewHolder;
                    holder.bindView(feedsList.get(position));

                    String mUsernameStr;
                    String mPostOwnerName = "";

                    holder.postTimeTxt.setText(feedsList.get(position).getDateCreatedAt());

                    if (feedsList.get(position).getUserType() == null
                            || feedsList.get(position).getUserType().isEmpty()
                            || feedsList.get(position).getUserType().trim().equals("user")
                            || feedsList.get(position).getUserType().trim().equals("club_user")
                            || feedsList.get(position).getUserType().trim().equals("shop_user")
                            || feedsList.get(position).getUserType().trim().equals(AppConstants.ONDEMAND)) {

                        ProfileResModel mProfileResModel = feedsList.get(position).getProfilesByWhoPostedProfileID();
                        mUsernameStr = Utility.getInstance().getUserName(mProfileResModel);
                        mPostOwnerName = mUsernameStr;
                        if (feedsList.get(position).getTaggedProfileID() != null
                                && !feedsList.get(position).getTaggedProfileID().isEmpty()) {
                            setUserName(holder, position, mUsernameStr);
                        } else {
                            holder.profileNameTxt.setText(mUsernameStr);
                        }
                        if (mProfileResModel.getProfilePicture() != null)
                            ((BaseActivity) context).setImageWithGlide(holder.userImg, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);
                    } else if (feedsList.get(position).getUserType().trim().equals(AppConstants.USER_VIDEO_SHARED_POST)) {
                        if (feedsList.get(position).getVideoSharesByNewSharedPostID() != null) {
                            ProfileResModel mProfileResModel = feedsList.get(position).getProfilesByWhoPostedProfileID();
                            mUsernameStr = Utility.getInstance().getUserName(mProfileResModel);
                            mPostOwnerName = mUsernameStr;
                            if (feedsList.get(position).getTaggedProfileID() != null && !feedsList.get(position)
                                    .getTaggedProfileID().isEmpty()) {
                                setUserName(holder, position, mUsernameStr);
                            } else {
                                holder.profileNameTxt.setText(mUsernameStr);
                            }
                            ((BaseActivity) context).setImageWithGlide(holder.userImg, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);
                        }
                    } else if (feedsList.get(position).getUserType().trim().equals(AppConstants.SHARED_POST)
                            || feedsList.get(position).getUserType().trim().equals(AppConstants.VIDEO_SHARED_POST)
                        /*|| feedsList.get(position).getUserType().trim().equals(AppConstants.SHARED_VIDEO)*/) {

                        if (feedsList.get(position).getPromoterByWhoPostedProfileID() != null) {
                            PromotersResModel mPromotersResModel = feedsList.get(position).getPromoterByWhoPostedProfileID();
                            mUsernameStr = (mPromotersResModel.getName());
                            mPostOwnerName = mUsernameStr;
                            holder.profileNameTxt.setText(mUsernameStr);
                            ((BaseActivity) context).setImageWithGlide(holder.userImg,
                                    mPromotersResModel.getProfileImage(), R.drawable.default_profile_icon);
                        }

                    } else if (feedsList.get(position).getUserType().trim().equals(AppConstants.PROMOTER)
                            || feedsList.get(position).getUserType().trim().equals(AppConstants.CLUB)
                            || feedsList.get(position).getUserType().trim().equals(AppConstants.NEWS_MEDIA)
                            || feedsList.get(position).getUserType().trim().equals(AppConstants.TRACK)
                            || feedsList.get(position).getUserType().trim().equals(AppConstants.SHOP)) {
                        PromotersResModel mPromotersProfilesModel = feedsList.get(position).getPromoterByProfileID();
                        mUsernameStr = mPromotersProfilesModel.getName();
                        mPostOwnerName = mUsernameStr;
                        holder.profileNameTxt.setText(mUsernameStr);
                        ((BaseActivity) context).setImageWithGlide(holder.userImg, mPromotersProfilesModel.getProfileImage(), R.drawable.default_profile_icon);
                    }
                    final String[] mVideoArray = Utility.getInstance().getImgVideoList(feedsList.get(position).getPostVideoThumbnailURL());
                    final String[] mImgArray = Utility.getInstance().getImgVideoList(feedsList.get(position).getPostPicture());

                    if ((mVideoArray != null && mVideoArray.length >= 1) && (mImgArray != null && mImgArray.length >= 1)) {
                        String[] mergedList = mergeArrayList(mVideoArray, mImgArray);
                        setImageView(holder, mergedList);
                        holder.imgVideoView.setVisibility(View.VISIBLE);
                    } else if (mVideoArray != null && mVideoArray.length >= 1) {
                        setImageView(holder, mVideoArray);
                        holder.imgVideoView.setVisibility(View.VISIBLE);
                    } else if (mImgArray != null && mImgArray.length >= 1) {
                        setImageView(holder, mImgArray);
                        holder.imgVideoView.setVisibility(View.VISIBLE);
                    }
                    if (feedsList.get(position).getPostText().isEmpty()) {
                        holder.postContentTxt.setVisibility(View.GONE);
                    } else {
                        try {
                            if (!feedsList.get(position).getPostText().contains(" ")) {
                                holder.postContentTxt.setText(URLDecoder.decode(feedsList.get(position).getPostText(), "UTF-8"));
                            } else {
                                holder.postContentTxt.setText(feedsList.get(position).getPostText());
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        holder.postContentTxt.setVisibility(View.VISIBLE);
                    }


                    //Count View


                    //share
                    if (TextUtils.isEmpty(feedsList.get(position).getNewSharedPostID().trim())) {
                        HideShareLayout(holder);
                    } else {
                        showShareLayout(holder, position, mPostOwnerName);
                    }
                    ArrayList<FeedCommentModel> mFeedCommentModel = feedsList.get(position).getPostComments();
                    if (mFeedCommentModel.size() > 0) {
                        setPostComments(holder, position);
                        showPostCommentsLay(holder, mFeedCommentModel, position);
                    }


                    holder.likeCountTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                ArrayList<FeedLikesModel> mLikeModel;
                                if (feedsList.get(position).getPostLikes() != null) {
                                    mLikeModel = feedsList.get(position).getPostLikes();
                                } else {
                                    mLikeModel = new ArrayList<>();
                                }
                                showLikeListPopup(context.getString(R.string.likes));
                                setFeedLikeAdapter(mLikeModel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    holder.shareCountTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                ArrayList<FeedShareModel> mShareModel;
                                if (feedsList.get(position).getPostShares() != null) {
                                    mShareModel = feedsList.get(position).getPostShares();
                                } else {
                                    mShareModel = new ArrayList<>();
                                }
                                showLikeListPopup(context.getString(R.string.shares));
                                setFeedShareAdapter(mShareModel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

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
        AdapterTag tag = (AdapterTag) view.getTag();
        callBack.onClick(view, tag);
    }

    private void setImageView(Holder holder, String[] mArray) {
        int length = mArray.length;
        switch (length) {
            case 1:
                holder.mTopLay.setVisibility(View.VISIBLE);
                holder.mImgLay1.setVisibility(View.VISIBLE);

                holder.playicon1.setVisibility(!mArray[0].contains("Video") ? View.GONE : View.VISIBLE);

                setGlideImage(holder.mImg1, mArray[0], R.drawable.default_cover_img);

                break;
            case 2:
                holder.mTopLay.setVisibility(View.VISIBLE);
                holder.mImgLay1.setVisibility(View.VISIBLE);
                holder.mImgLay2.setVisibility(View.VISIBLE);

                holder.playicon1.setVisibility(!mArray[0].contains("Video") ? View.GONE : View.VISIBLE);
                holder.playicon2.setVisibility(!mArray[1].contains("Video") ? View.GONE : View.VISIBLE);

                setGlideImage(holder.mImg1, mArray[0], R.drawable.default_cover_img);
                setGlideImage(holder.mImg2, mArray[1], R.drawable.default_cover_img);

                break;
            case 3:
                holder.mTopLay.setVisibility(View.VISIBLE);
                holder.mImgLay1.setVisibility(View.VISIBLE);
                holder.mImgLay2.setVisibility(View.VISIBLE);
                holder.mBottomLay.setVisibility(View.VISIBLE);
                holder.mImgLay3.setVisibility(View.VISIBLE);

                holder.playicon1.setVisibility(!mArray[0].contains("Video") ? View.GONE : View.VISIBLE);
                holder.playicon2.setVisibility(!mArray[1].contains("Video") ? View.GONE : View.VISIBLE);
                holder.playicon3.setVisibility(!mArray[2].contains("Video") ? View.GONE : View.VISIBLE);

                setGlideImage(holder.mImg3, mArray[2], R.drawable.default_cover_img);
                setGlideImage(holder.mImg1, mArray[0], R.drawable.default_cover_img);
                setGlideImage(holder.mImg2, mArray[1], R.drawable.default_cover_img);

                break;
            case 4:
                holder.mTopLay.setVisibility(View.VISIBLE);
                holder.mImgLay1.setVisibility(View.VISIBLE);
                holder.mImgLay2.setVisibility(View.VISIBLE);
                holder.mBottomLay.setVisibility(View.VISIBLE);
                holder.mImgLay3.setVisibility(View.VISIBLE);
                holder.mImgLay4.setVisibility(View.VISIBLE);

                holder.playicon1.setVisibility(!mArray[0].contains("Video") ? View.GONE : View.VISIBLE);
                holder.playicon2.setVisibility(!mArray[1].contains("Video") ? View.GONE : View.VISIBLE);
                holder.playicon3.setVisibility(!mArray[2].contains("Video") ? View.GONE : View.VISIBLE);
                holder.playicon4.setVisibility(!mArray[3].contains("Video") ? View.GONE : View.VISIBLE);

                setGlideImage(holder.mImg1, mArray[0], R.drawable.default_cover_img);
                setGlideImage(holder.mImg2, mArray[1], R.drawable.default_cover_img);
                setGlideImage(holder.mImg3, mArray[2], R.drawable.default_cover_img);
                setGlideImage(holder.mImg4, mArray[3], R.drawable.default_cover_img);

                break;
            default:
                holder.mTopLay.setVisibility(View.VISIBLE);
                holder.mImgLay1.setVisibility(View.VISIBLE);
                holder.mImgLay2.setVisibility(View.VISIBLE);
                holder.mBottomLay.setVisibility(View.VISIBLE);
                holder.mImgLay3.setVisibility(View.VISIBLE);
                holder.mImgLay4.setVisibility(View.VISIBLE);
                holder.mMoreLay.setVisibility(View.VISIBLE);

                String mCnt = "+" + (mArray.length - 4) + " More";
                holder.mTotalImgCountTv.setText(mCnt);

                holder.playicon1.setVisibility(!mArray[0].contains("Video") ? View.GONE : View.VISIBLE);
                holder.playicon2.setVisibility(!mArray[1].contains("Video") ? View.GONE : View.VISIBLE);
                holder.playicon3.setVisibility(!mArray[2].contains("Video") ? View.GONE : View.VISIBLE);
                holder.playicon4.setVisibility(!mArray[3].contains("Video") ? View.GONE : View.VISIBLE);

                setGlideImage(holder.mImg1, mArray[0], R.drawable.default_cover_img);
                setGlideImage(holder.mImg2, mArray[1], R.drawable.default_cover_img);
                setGlideImage(holder.mImg3, mArray[2], R.drawable.default_cover_img);
                setGlideImage(holder.mImg4, mArray[3], R.drawable.default_cover_img);
                break;
        }
    }

    private void setUserName(final Holder holder, final int position, String mUsernameStr) {

        String[] mTaggedProfilesID = feedsList.get(position).getTaggedProfileID().split(",");
        int mTaggedProfilesIDCount = mTaggedProfilesID.length;

        String mTagStr = "with " + mTaggedProfilesIDCount + " other(s)";

        mUsernameStr = mUsernameStr + " " + mTagStr;

        SpannableString mSpannableString = new SpannableString(mUsernameStr);

        mSpannableString.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);

                ds.setColor(ContextCompat.getColor(context, R.color.colorOrange));
                ds.setUnderlineText(false);

            }

            @Override
            public void onClick(View view) {
                //MotoHub.getApplicationInstance().setmProfileResModel(myProfile);
                EventBus.getDefault().postSticky(myProfile);
                Intent mIntent = new Intent(context, TaggedProfilesListActivity.class);
                mIntent.putExtra(TaggedProfilesListActivity.TAGGED_PROFILES_ID, feedsList.get(position).getTaggedProfileID());
                /*mIntent.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, myProfile);*/
                context.startActivity(mIntent);

            }

        }, mUsernameStr.length() - mTagStr.length(), mUsernameStr.length(), 0);

        holder.profileNameTxt.setText(mSpannableString);
        holder.profileNameTxt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void showPostCommentsLay(Holder holder, ArrayList<FeedCommentModel> mFeedCommentModel, int position) {

        holder.commentView.setVisibility(View.VISIBLE);

        int latestPostion = mFeedCommentModel.size() - 1;

        String mCommentText = mFeedCommentModel.get(latestPostion).getmComment().trim();
        String mCommentImgUrl = mFeedCommentModel.get(latestPostion).getCommentImages().trim();

        if (!mCommentImgUrl.isEmpty()) {
            holder.commentContentTxt.setVisibility(View.VISIBLE);
            holder.commentContentTxt.setText("Commented to this post.");
        } else if (!mCommentText.isEmpty()) {
            try {
                if (!mCommentText.contains(" ")) {
                    holder.commentContentTxt.setText(((BaseActivity) context)
                            .setTextEdt(context, URLDecoder.decode(mCommentText, "UTF-8"),
                                    mFeedCommentModel.get(latestPostion).getCommentTaggedUserNames(),
                                    mFeedCommentModel.get(latestPostion).getCommentTaggedUserID(), myProfile.getID()));
                } else {
                    holder.commentContentTxt.setText(((BaseActivity) context)
                            .setTextEdt(context, mCommentText,
                                    mFeedCommentModel.get(latestPostion).getCommentTaggedUserNames(),
                                    mFeedCommentModel.get(latestPostion).getCommentTaggedUserID(), myProfile.getID()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.commentContentTxt.setMovementMethod(LinkMovementMethod.getInstance());
            holder.commentContentTxt.setVisibility(View.VISIBLE);
        } else {
            holder.commentContentTxt.setVisibility(View.GONE);
        }
        holder.commentUserNameTxt.setText(Utility.getInstance().getUserName(mFeedCommentModel.get(latestPostion).getProfiles_by_ProfileID()));

        ((BaseActivity) context).setImageWithGlide(holder.commentUserImg, mFeedCommentModel.get(latestPostion).getProfiles_by_ProfileID().getProfilePicture(), R.drawable.default_profile_icon);
    }

    private String getCount(int count, String type) {
        String countStr = BaseActivity.convertToSuffix(Long.parseLong(String.valueOf(count)));
        if (count > 1) {
            countStr = countStr + type + "s";
        } else if (count == 1) {
            countStr = countStr + type;
        } else {
            countStr = "";
        }
        return countStr;
    }

    private boolean isLiked(ArrayList<FeedLikesModel> likedList) {
        for (final FeedLikesModel data : likedList) {
            if (myProfile.getID() == data.getProfileID()) {
                return true;
            }
        }
        return false;
    }

    private boolean isBlocked(ArrayList<NotificationBlockedUsersModel> blockedList) {
        for (final NotificationBlockedUsersModel data : blockedList) {
            if (myProfile.getID() == data.getmProfileID()) {
                return true;
            }
        }
        return false;
    }

    private void showShareLayout(final Holder holder, final int position, String mPostOwnerName) {
        holder.sharedProfileLay.setVisibility(View.VISIBLE);
        holder.downIcon.setVisibility(View.GONE);
        holder.notifyIcon.setVisibility(View.GONE);

        String sharedContent = feedsList.get(position).getSharedTxt().trim();

        if (TextUtils.isEmpty(sharedContent)) {
            holder.sharedPostContentTxt.setVisibility(View.GONE);
        } else {
            holder.sharedPostContentTxt.setVisibility(View.VISIBLE);
            try {
                if (!sharedContent.contains(" ")) {
                    holder.sharedPostContentTxt.setText(URLDecoder.decode(sharedContent, "UTF-8"));
                } else {
                    holder.sharedPostContentTxt.setText(sharedContent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (feedsList.get(position).getUserType().trim().equals(AppConstants.VIDEO_SHARED_POST) || feedsList.get(position).getUserType().trim().equals(AppConstants.USER_VIDEO_SHARED_POST)) {
            if (feedsList.get(position).getVideoSharesByNewSharedPostID() != null) {
                VideoShareModel mFollowingsShareList = feedsList.get(position).getVideoSharesByNewSharedPostID();
                holder.postTimeTxt.setText(Utility.getInstance().findTime(mFollowingsShareList.getSharedAt()));
            }
        } else {
            if (feedsList.get(position).getNewSharedPost() != null) {
                FeedShareModel mFollowingsShareList = feedsList.get(position).getNewSharedPost();
                holder.postTimeTxt.setText(Utility.getInstance().findTime(mFollowingsShareList.getSharedAt()));
            }
        }

        String mShareTxt;
        int mFirstSpanWordLength;

        if (feedsList.get(position).getProfilesByProfileID().getProfileType() == Integer.parseInt(BaseActivity.SPECTATOR)) {
            mShareTxt = feedsList.get(position).getProfilesByProfileID().getSpectatorName() + " shared " + mPostOwnerName + " post";
            mFirstSpanWordLength = feedsList.get(position).getProfilesByProfileID().getSpectatorName().length();
        } else {
            mShareTxt = feedsList.get(position).getProfilesByProfileID().getDriver() + " shared " + mPostOwnerName + " post";
            mFirstSpanWordLength = feedsList.get(position).getProfilesByProfileID().getDriver().length();
        }

        ((BaseActivity) context).setImageWithGlide(holder.sharedUserImg, feedsList.get(position).getProfilesByProfileID().getProfilePicture(), R.drawable.default_profile_icon);

        Spannable mWordToSpan = new SpannableString(mShareTxt);
        int mSecondSpanWordLength = mShareTxt.length();
        mWordToSpan.setSpan(new ForegroundColorSpan(Color.GRAY), mFirstSpanWordLength, mFirstSpanWordLength + 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mWordToSpan.setSpan(new ForegroundColorSpan(Color.GRAY), mSecondSpanWordLength - 5, mSecondSpanWordLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mWordToSpan.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(context, R.color.colorBlack));
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View view) {
                fragment.moveProfileScreen(position, true);
            }
        }, 0, mFirstSpanWordLength, 0);
        mWordToSpan.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(context, R.color.colorBlack));
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View view) {
                fragment.moveProfileScreen(position, false);
            }
        }, mFirstSpanWordLength + 8, mSecondSpanWordLength - 5, 0);

        holder.sharedProfileNameTxt.setText(mWordToSpan);
        holder.sharedProfileNameTxt.setMovementMethod(LinkMovementMethod.getInstance());

        holder.sharedPostTimeTxt.setText(feedsList.get(position).getDateCreatedAt());

        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) holder.profileLay.getLayoutParams();

        //relativeParams.height = context.getResources().getDimensionPixelSize(R.dimen.size80);

        relativeParams.height = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
        relativeParams.width = ViewGroup.MarginLayoutParams.MATCH_PARENT;

        relativeParams.setMargins(context.getResources().getDimensionPixelSize(R.dimen.size24),
                context.getResources().getDimensionPixelSize(R.dimen.size5),
                context.getResources().getDimensionPixelSize(R.dimen.size24),
                context.getResources().getDimensionPixelSize(R.dimen.size0));

        holder.profileLay.setLayoutParams(relativeParams);
        holder.profileLay.requestLayout();

        ViewGroup.MarginLayoutParams relativeParams1 = (ViewGroup.MarginLayoutParams) holder.imgVideoLay.getLayoutParams();

        relativeParams1.setMargins(context.getResources().getDimensionPixelSize(R.dimen.size20),
                context.getResources().getDimensionPixelSize(R.dimen.size10),
                context.getResources().getDimensionPixelSize(R.dimen.size20),
                context.getResources().getDimensionPixelSize(R.dimen.size0));

        relativeParams1.height = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
        relativeParams1.width = ViewGroup.MarginLayoutParams.MATCH_PARENT;

        holder.imgVideoLay.setLayoutParams(relativeParams1);
        holder.imgVideoLay.requestLayout();


    }

    private void HideShareLayout(Holder holder) {

        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) holder.profileLay.getLayoutParams();

//        relativeParams.height = context.getResources().getDimensionPixelSize(R.dimen.size80);
        relativeParams.height = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
        relativeParams.width = ViewGroup.MarginLayoutParams.MATCH_PARENT;

        relativeParams.setMargins(context.getResources().getDimensionPixelSize(R.dimen.size10),
                context.getResources().getDimensionPixelSize(R.dimen.size0),
                context.getResources().getDimensionPixelSize(R.dimen.size10),
                context.getResources().getDimensionPixelSize(R.dimen.size0));

        holder.profileLay.setLayoutParams(relativeParams);
        holder.profileLay.requestLayout();

        ViewGroup.MarginLayoutParams relativeParams1 = (ViewGroup.MarginLayoutParams) holder.imgVideoLay.getLayoutParams();

        relativeParams1.setMargins(context.getResources().getDimensionPixelSize(R.dimen.size0),
                context.getResources().getDimensionPixelSize(R.dimen.size10),
                context.getResources().getDimensionPixelSize(R.dimen.size0),
                context.getResources().getDimensionPixelSize(R.dimen.size0));

        relativeParams1.height = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
        relativeParams1.width = ViewGroup.MarginLayoutParams.MATCH_PARENT;

        holder.imgVideoLay.setLayoutParams(relativeParams1);
        holder.imgVideoLay.requestLayout();

        holder.sharedProfileLay.setVisibility(View.GONE);
    }

    private void setPostComments(Holder holder, int position) {

        String resComments;

        if (feedsList.get(position).getPostComments().size() == 1) {
            resComments = feedsList.get(position).getPostComments().size() + " Comment ";
        } else {
            resComments = feedsList.get(position).getPostComments().size() + " Comments ";
        }

        holder.commentsCountTxt.setText(resComments);

    }


    public void resetCommentAdapter(FeedCommentModel feedCommentModel) {

        ArrayList<FeedCommentModel> mCommentList = feedsList.get(adapPos).getPostComments();
        mCommentList.add(feedCommentModel);
        feedsList.get(adapPos).setFeedComments(mCommentList);
        //notifyDataSetChanged();
        notifyItemChanged(adapPos);

    }

    public void resetUnBlock(int pos) {
        ArrayList<NotificationBlockedUsersModel> mNotifylist = feedsList.get(pos).getmNotificationBlockedUsersID();
        Iterator<NotificationBlockedUsersModel> iterator = mNotifylist.iterator();
        while (iterator.hasNext()) {
            NotificationBlockedUsersModel next = iterator.next();
            if (next.getmProfileID() == myProfile.getID()) {
                iterator.remove();
                mNotifylist.remove(next);
            }
        }
        feedsList.get(pos).setmNotificationBlockedUsersID(mNotifylist);
        notifyItemChanged(pos);
    }

    public void resetBlock(int pos, NotificationBlockedUsersModel mNotify) {
        ArrayList<NotificationBlockedUsersModel> mNotifylist = feedsList.get(pos).getmNotificationBlockedUsersID();
        mNotifylist.add(mNotify);
        feedsList.get(pos).setmNotificationBlockedUsersID(mNotifylist);
        notifyItemChanged(pos);
    }

    public void resetLikeAdapter(int pos, FeedLikesModel feedLikesModel) {
        ArrayList<FeedLikesModel> mFeedLikesList = feedsList.get(pos).getPostLikes();
        mFeedLikesList.add(feedLikesModel);
        feedsList.get(pos).setPostLikes(mFeedLikesList);
        notifyItemChanged(pos);
    }

    public void resetDisLike(int pos) {
        ArrayList<FeedLikesModel> mFeedLikesList = feedsList.get(pos).getPostLikes();
        Iterator<FeedLikesModel> iterator = mFeedLikesList.iterator();
        while (iterator.hasNext()) {
            FeedLikesModel next = iterator.next();
            if (next.getProfileID() == myProfile.getID()) {
                iterator.remove();
                mFeedLikesList.remove(next);
            }
        }
        feedsList.get(pos).setPostLikes(mFeedLikesList);
        notifyItemChanged(pos);
    }

    public void resetShareAdapter(FeedShareModel feedShareModel) {
        ArrayList<FeedShareModel> mShareList = feedsList.get(adapPos + 1).getPostShares();
        mShareList.add(feedShareModel);
        feedsList.get(adapPos + 1).setPostShares(mShareList);
        //notifyDataSetChanged();
        notifyItemChanged(adapPos);
    }

    private void setFeedLikeAdapter(ArrayList<FeedLikesModel> mFeedLikeList) {
        FeedLikesAdapter mFeedLikesAdapter = new FeedLikesAdapter(context, mFeedLikeList, myProfile);
        mFeedLikesListView.setAdapter(mFeedLikesAdapter);
        mFeedLikesListView.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setFeedShareAdapter(ArrayList<FeedShareModel> mFeedShareList) {
        FeedSharesAdapter mFeedShareAdapter = new FeedSharesAdapter(context, mFeedShareList, myProfile);
        mFeedLikesListView.setAdapter(mFeedShareAdapter);
        mFeedLikesListView.setLayoutManager(new LinearLayoutManager(context));
    }

    private void showLikeListPopup(String title) {
//        if (mCommentListPopup != null && mCommentListPopup.isShowing()) {
//            mCommentListPopup.dismiss();
//        }
        // Create custom dialog object
        Dialog mCommentListPopup = new Dialog(this.context, R.style.MyDialogBottomSheet);
        mCommentListPopup.setContentView(R.layout.popup_feed_like_list);
        ImageView mCloseIcon = mCommentListPopup.findViewById(R.id.close_btn);
        TextView mTitleTxt = mCommentListPopup.findViewById(R.id.title_txt);
        mFeedLikesListView = mCommentListPopup.findViewById(R.id.feeds_likes_list_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
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
        ArrayList<FeedShareModel> mResFeedShareList = feedsList.get(adapPos).getPostShares();
        mResFeedShareList.add(mSharedFeed);
        feedsList.get(adapPos).setPostShares(mResFeedShareList);
        //notifyDataSetChanged();
        notifyItemChanged(adapPos);
    }

    private void setGlideImage(ImageView imgView, String imgUrl, int drawable) {
        GlideUrl glideUrl = new GlideUrl(UrlUtils.AWS_FILE_URL + imgUrl, new LazyHeaders.Builder()
                .addHeader("X-DreamFactory-Api-Key", context.getString(R.string.dream_factory_api_key))
                .build());

        Glide.with(context)
                .load(glideUrl)
                .apply(new RequestOptions()
                        .error(drawable)
                        .dontAnimate())
                .into(imgView);
    }

    public void refreshCommentList(ArrayList<FeedCommentModel> mCommentList) {
        Collections.reverse(mCommentList);
        feedsList.get(adapPos).setFeedComments(mCommentList);
        //notifyDataSetChanged();
        notifyItemChanged(adapPos);
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

    class Holder extends RecyclerView.ViewHolder {

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

        @BindView(R.id.sharedProfileLay)
        RelativeLayout sharedProfileLay;
        @BindView(R.id.sharedUserImg)
        CircleImageView sharedUserImg;
        @BindView(R.id.sharedProfileNameTxt)
        TextView sharedProfileNameTxt;
        @BindView(R.id.sharedPostTimeTxt)
        TextView sharedPostTimeTxt;
        @BindView(R.id.sharedPostContentTxt)
        TextView sharedPostContentTxt;
        @BindView(R.id.sharedNotifyIcon)
        ImageView sharedNotifyIcon;
        @BindView(R.id.sharedDownIcon)
        ImageView sharedDownIcon;

        @BindView(R.id.profileLay)
        RelativeLayout profileLay;
        @BindView(R.id.userImg)
        CircleImageView userImg;
        @BindView(R.id.profileNameTxt)
        TextView profileNameTxt;
        @BindView(R.id.postTimeTxt)
        TextView postTimeTxt;
        @BindView(R.id.postContentTxt)
        TextView postContentTxt;
        @BindView(R.id.notifyIcon)
        ImageView notifyIcon;
        @BindView(R.id.downIcon)
        ImageView downIcon;

        @BindView(R.id.commentView)
        RelativeLayout commentView;
        @BindView(R.id.commentUserImg)
        ImageView commentUserImg;
        @BindView(R.id.commentUserNameTxt)
        TextView commentUserNameTxt;
        @BindView(R.id.commentContentTxt)
        TextView commentContentTxt;

        @BindView(R.id.countView)
        ConstraintLayout countView;
        @BindView(R.id.shareCountTxt)
        TextView shareCountTxt;
        @BindView(R.id.likeCountTxt)
        TextView likeCountTxt;
        @BindView(R.id.viewCountTxt)
        TextView viewCountTxt;
        @BindView(R.id.commentsCountTxt)
        TextView commentsCountTxt;

        @BindView(R.id.likeBtn)
        ImageView likeBtn;
        @BindView(R.id.commentBtn)
        ImageView commentBtn;
        @BindView(R.id.shareBtn)
        ImageView shareBtn;


        Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindView(PostsResModel postsResModel) {
            setClickListener();
            setVisibility(postsResModel);
            adapPos = getLayoutPosition();

            likeCountTxt.setText(getCount(postsResModel.getPostLikes().size(), ModelConstants.LIKE));
            shareCountTxt.setText(getCount(postsResModel.getPostShares().size(), ModelConstants.SHARE));
            commentsCountTxt.setText(getCount(postsResModel.getPostComments().size(), ModelConstants.COMMENT));
            viewCountTxt.setText(getCount(postsResModel.getViewCount(), ModelConstants.VIEW));

            if (isLiked(postsResModel.getPostLikes())) {
                likeBtn.setImageResource(R.drawable.liked_icon);
            } else {
                likeBtn.setImageResource(R.drawable.like_icon);
            }
            if (isBlocked(postsResModel.getmNotificationBlockedUsersID())) {
                sharedNotifyIcon.setImageResource(R.drawable.notify_inactive_icon);
                notifyIcon.setImageResource(R.drawable.notify_inactive_icon);
            } else {
                sharedNotifyIcon.setImageResource(R.drawable.notify_active_icon);
                notifyIcon.setImageResource(R.drawable.notify_active_icon);
            }

        }

        void setVisibility(PostsResModel postsResModel) {
            //Set Visibility
            countView.setVisibility(View.VISIBLE);
            downIcon.setVisibility(View.VISIBLE);
            notifyIcon.setVisibility(View.VISIBLE);

            commentView.setVisibility(View.GONE);
            countView.setVisibility(View.GONE);

            imgVideoView.setVisibility(View.GONE);
            mTopLay.setVisibility(View.GONE);
            mImgLay1.setVisibility(View.GONE);
            mImgLay2.setVisibility(View.GONE);
            mBottomLay.setVisibility(View.GONE);
            mImgLay3.setVisibility(View.GONE);
            mImgLay4.setVisibility(View.GONE);
            mMoreLay.setVisibility(View.GONE);

            playicon1.setVisibility(View.GONE);
            playicon2.setVisibility(View.GONE);
            playicon3.setVisibility(View.GONE);
            playicon4.setVisibility(View.GONE);

            if (postsResModel.getPostLikes().size() > 0
                    || postsResModel.getPostComments().size() > 0
                    || postsResModel.getPostShares().size() > 0
                    || postsResModel.getViewCount() > 0) {
                countView.setVisibility(View.VISIBLE);
            }
        }

        void setClickListener() {
            //Set Click Listener
            AdapterTag tag = new AdapterTag();
            tag.setPos(getLayoutPosition());
            tag.setBlocked(isBlocked(feedsList.get(getLayoutPosition()).getmNotificationBlockedUsersID()));
            tag.setLiked(isLiked(feedsList.get(getLayoutPosition()).getPostLikes()));
            sharedUserImg.setTag(tag);
            sharedUserImg.setOnClickListener(NewsFeedAdapter.this);
            sharedProfileNameTxt.setTag(tag);
            sharedProfileNameTxt.setOnClickListener(NewsFeedAdapter.this);
            sharedPostTimeTxt.setTag(tag);
            sharedPostTimeTxt.setOnClickListener(NewsFeedAdapter.this);
            sharedNotifyIcon.setTag(tag);
            sharedNotifyIcon.setOnClickListener(NewsFeedAdapter.this);
            sharedDownIcon.setTag(tag);
            sharedDownIcon.setOnClickListener(NewsFeedAdapter.this);

            userImg.setTag(tag);
            userImg.setOnClickListener(NewsFeedAdapter.this);
            profileNameTxt.setTag(tag);
            profileNameTxt.setOnClickListener(NewsFeedAdapter.this);
            postTimeTxt.setTag(tag);
            postTimeTxt.setOnClickListener(NewsFeedAdapter.this);
            notifyIcon.setTag(tag);
            notifyIcon.setOnClickListener(NewsFeedAdapter.this);
            downIcon.setTag(tag);
            downIcon.setOnClickListener(NewsFeedAdapter.this);

            imgVideoView.setTag(tag);
            imgVideoView.setOnClickListener(NewsFeedAdapter.this);

            commentView.setTag(tag);
            commentView.setOnClickListener(NewsFeedAdapter.this);

//            likeCountTxt.setTag(tag);
//            likeCountTxt.setOnClickListener(NewsFeedAdapter.this);
//            viewCountTxt.setTag(tag);
//            viewCountTxt.setOnClickListener(NewsFeedAdapter.this);
//            shareCountTxt.setTag(tag);
//            shareCountTxt.setOnClickListener(NewsFeedAdapter.this);

            commentsCountTxt.setTag(tag);
            commentsCountTxt.setOnClickListener(NewsFeedAdapter.this);

            likeBtn.setTag(tag);
            likeBtn.setOnClickListener(NewsFeedAdapter.this);
            commentBtn.setTag(tag);
            commentBtn.setOnClickListener(NewsFeedAdapter.this);
            shareBtn.setTag(tag);
            shareBtn.setOnClickListener(NewsFeedAdapter.this);
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