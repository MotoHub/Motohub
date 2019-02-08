package online.motohub.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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

import org.greenrobot.eventbus.EventBus;

import java.net.URLDecoder;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.MyMotoFileActivity;
import online.motohub.activity.OthersMotoFileActivity;
import online.motohub.activity.club.ClubProfileActivity;
import online.motohub.activity.news_and_media.NewsAndMediaProfileActivity;
import online.motohub.activity.performance_shop.PerformanceShopProfileActivity;
import online.motohub.activity.promoter.PromoterProfileActivity;
import online.motohub.activity.track.TrackProfileActivity;
import online.motohub.application.MotoHub;
import online.motohub.model.EventGrpChatMsgResModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.util.AppConstants;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.UrlUtils;

public class ChatBoxEventGrpAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int SENDER = 1;
    private static final int OTHERS = 2;
    private int adapterpos = 0;
    private View mPreviousSelectedMsgView = null;
    private ImageView mPreviousForwardMsgIV = null;

    private List<EventGrpChatMsgResModel> mEventGrpChatMsgList;
    private Context mContext;
    private ProfileResModel mMyProfileResModel;
    private StringBuffer sb = new StringBuffer();

    public ChatBoxEventGrpAdapter(ProfileResModel mMyProfileResModel, List<EventGrpChatMsgResModel> eventGrpChatMsgList, Context ctx) {
        this.mEventGrpChatMsgList = eventGrpChatMsgList;
        this.mContext = ctx;
        this.mMyProfileResModel = mMyProfileResModel;
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
        return mEventGrpChatMsgList.size();
    }

    @Override
    public int getItemViewType(int position) {

        PreferenceUtils.getInstance(mContext).getIntData(PreferenceUtils.USER_ID);

        if (position >= mEventGrpChatMsgList.size()) {
            return VIEW_TYPE_LOADING;
        } else if (PreferenceUtils.getInstance(mContext).getIntData(PreferenceUtils.USER_ID) ==
                mEventGrpChatMsgList.get(position).getSenderUserID()) {
            if (mEventGrpChatMsgList.get(position).getUserType() == null) {
                return SENDER;
            } else if (mEventGrpChatMsgList.get(position).getUserType().isEmpty() || mEventGrpChatMsgList.get(position).getUserType().trim().equals("user")) {
                return SENDER;
            } else {
                return OTHERS;
            }
        } else if (PreferenceUtils.getInstance(mContext).getIntData(PreferenceUtils.USER_ID) != mEventGrpChatMsgList.get(position).getSenderUserID()) {
            return OTHERS;
        } else {
            return super.getItemViewType(position);
        }

    }

    @Override
    public long getItemId(int position) {
        return (getItemViewType(position) == SENDER || getItemViewType(position) == OTHERS) ? position : -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mView;

        switch (viewType) {

            case SENDER:

                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_sender, parent, false);
                return new ViewHolderSender(mView);

            case OTHERS:

                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_receiver, parent, false);
                return new ViewHolderOthers(mView);

            case VIEW_TYPE_LOADING:

                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_progress_bar, parent, false);
                return new ViewHolderLoader(mView);

            default:
                return null;

        }

    }

    void showHideReceiverMsgTime(ViewHolderOthers mHolder, int position) {
        EventGrpChatMsgResModel eventGrpChatMsgResModel = mEventGrpChatMsgList.get(position);

        if (eventGrpChatMsgResModel.getDateVisible()) {
            eventGrpChatMsgResModel.setDateVisible(false);
            mHolder.mOthersMsgTimeTv.setVisibility(View.INVISIBLE);
        } else {
            eventGrpChatMsgResModel.setDateVisible(true);
            mHolder.mOthersMsgTimeTv.setVisibility(View.VISIBLE);
        }
    }

    void showHideSenderMsgTime(ViewHolderSender mHolder, int position) {
        EventGrpChatMsgResModel eventGrpChatMsgResModel = mEventGrpChatMsgList.get(position);

        if (eventGrpChatMsgResModel.getDateVisible()) {
            eventGrpChatMsgResModel.setDateVisible(false);
            mHolder.mSenderMsgTimeTv.setVisibility(View.INVISIBLE);
        } else {
            eventGrpChatMsgResModel.setDateVisible(true);
            mHolder.mSenderMsgTimeTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        switch (getItemViewType(position)) {

            case SENDER:
                try {

                    final ViewHolderSender mViewHolderSender = (ViewHolderSender) holder;

                    mViewHolderSender.mSenderMsgTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showHideSenderMsgTime(mViewHolderSender, position);
                        }
                    });
/*

                mViewHolderSender.mForwardSenderMsgBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       if(mContext instanceof EventLiveActivity){
                           try {
                               ((EventLiveActivity) mContext).setReplyChatMsg(URLDecoder.decode(mEventGrpChatMsgList.get(position).getMainObj(), "UTF-8"), mEventGrpChatMsgList.get(position).getID(),mEventGrpChatMsgList.get(position).getSenderProfileID(), Utility.getInstance().getUserName(mEventGrpChatMsgList.get(position).getProfilesBySenderProfileID()), mEventGrpChatMsgList.get(position).getPhotoMessage());
                               mViewHolderSender.mForwardSenderMsgBtn.setVisibility(View.GONE);
                               mViewHolderSender.mSenderChatView.setVisibility(View.GONE);
                           } catch (UnsupportedEncodingException e) {
                               e.printStackTrace();
                           }
                       } else if( mContext instanceof ChatBoxEventGrpActivity){
                           try {
                               ((ChatBoxEventGrpActivity) mContext).setReplyChatMsg(URLDecoder.decode(mEventGrpChatMsgList.get(position).getMainObj(), "UTF-8"), mEventGrpChatMsgList.get(position).getID(),mEventGrpChatMsgList.get(position).getSenderProfileID(), Utility.getInstance().getUserName(mEventGrpChatMsgList.get(position).getProfilesBySenderProfileID()), mEventGrpChatMsgList.get(position).getPhotoMessage());
                               mViewHolderSender.mForwardSenderMsgBtn.setVisibility(View.GONE);
                               mViewHolderSender.mSenderChatView.setVisibility(View.GONE);
                           } catch (UnsupportedEncodingException e) {
                               e.printStackTrace();
                           }
                       }
                    }
                });


                mViewHolderSender.mSenderChatLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showSenderSelectedMsg(mViewHolderSender);
                        return false;
                    }
                });

                if(mEventGrpChatMsgList.get(position).getIsRepliedMsg()){
                    showReplyToSenderMsgLayout(mViewHolderSender, position);
                } else{
                    mViewHolderSender.mReplyToSenderChatLayout.setVisibility(View.GONE);
                }

*/

                    if (mEventGrpChatMsgList.get(position).getProfilesBySenderProfileID() != null) {

                        String mProfilePicUrl = "";

                        if (mEventGrpChatMsgList.get(position).getProfilesBySenderProfileID()
                                .getProfilePicture() != null) {
                            mProfilePicUrl = mEventGrpChatMsgList.get(position).getProfilesBySenderProfileID()
                                    .getProfilePicture();
                        }

                        ((BaseActivity) mContext).setImageWithGlide(mViewHolderSender.mSenderImgView, mProfilePicUrl, R.drawable.default_profile_icon);
                        if (mEventGrpChatMsgList.get(position).getProfilesBySenderProfileID().getDriver().isEmpty())
                            mViewHolderSender.mSenderNameTv.setText(mEventGrpChatMsgList.get(position).getProfilesBySenderProfileID().getSpectatorName());
                        else
                            mViewHolderSender.mSenderNameTv.setText(mEventGrpChatMsgList.get(position).getProfilesBySenderProfileID().getDriver());

                    }

                    if (mEventGrpChatMsgList.get(position).getMessage().trim().isEmpty()) {
                        mViewHolderSender.mSenderMsgTv.setVisibility(View.GONE);
                    } else {
                        mViewHolderSender.mSenderMsgTv.setVisibility(View.VISIBLE);
                        try {
                            mViewHolderSender.mSenderMsgTv.setText(URLDecoder.decode(mEventGrpChatMsgList.get(position).getMessage(), "UTF-8"));
                           // mViewHolderSender.mSenderMsgTv.setText(replacer(sb.append(mEventGrpChatMsgList.get(position).getMainObj())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    mViewHolderSender.mSenderMsgTimeTv.setText(((BaseActivity) mContext).getFormattedDate(mEventGrpChatMsgList.get(position).getCreatedAt()));
                    if (mEventGrpChatMsgList.get(position).getDateVisible()) {
                        mViewHolderSender.mSenderMsgTimeTv.setVisibility(View.VISIBLE);
                    } else {
                        mViewHolderSender.mSenderMsgTimeTv.setVisibility(View.INVISIBLE);
                    }

                    //This is for image
                    if (!mEventGrpChatMsgList.get(position).getPhotoMessage().trim().isEmpty()) {
                        mViewHolderSender.ivCommentImg.setVisibility(View.VISIBLE);
                        mViewHolderSender.smallProgressBar.setVisibility(View.VISIBLE);
                        GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + mEventGrpChatMsgList.get(position).getPhotoMessage().trim(), new LazyHeaders.Builder()
                                .addHeader("X-DreamFactory-Api-Key", mContext.getString(R.string.dream_factory_api_key))
                                .build());

                        Glide.with(mContext)
                                .load(glideUrl)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        mViewHolderSender.smallProgressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        mViewHolderSender.smallProgressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .apply(new RequestOptions()
                                        .dontAnimate()
                                        .centerCrop()
                                )
                                .into(mViewHolderSender.ivCommentImg);
                    } else {
                        mViewHolderSender.ivCommentImg.setVisibility(View.GONE);
                        mViewHolderSender.smallProgressBar.setVisibility(View.GONE);
                    }

                    mViewHolderSender.ivCommentImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((BaseActivity) mContext).moveLoadImageScreen(mContext, UrlUtils.FILE_URL + mEventGrpChatMsgList.get(position).getPhotoMessage());

                        }
                    });

                    mViewHolderSender.mSenderImgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            postProfileClick(mViewHolderSender.getAdapterPosition());
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case OTHERS:

                try {

                    final ViewHolderOthers mViewHolderOthers = (ViewHolderOthers) holder;

                    mViewHolderOthers.mOthersMsgTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showHideReceiverMsgTime(mViewHolderOthers, position);
                        }
                    });

               /* mViewHolderOthers.mForwardReceiverMsgBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mContext instanceof EventLiveActivity){
                            try {
                                ((EventLiveActivity) mContext).setReplyChatMsg(URLDecoder.decode(mEventGrpChatMsgList.get(position).getMainObj(),"UTF-8"), mEventGrpChatMsgList.get(position).getID(), mEventGrpChatMsgList.get(position).getSenderProfileID(),Utility.getInstance().getUserName(mEventGrpChatMsgList.get(position).getProfilesBySenderProfileID()), mEventGrpChatMsgList.get(position).getPhotoMessage());
                                mViewHolderOthers.mReceiverChatView.setVisibility(View.GONE);
                                mViewHolderOthers.mForwardReceiverMsgBtn.setVisibility(View.GONE);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else if( mContext instanceof ChatBoxEventGrpActivity){
                            try {
                                ((ChatBoxEventGrpActivity) mContext).setReplyChatMsg(URLDecoder.decode(mEventGrpChatMsgList.get(position).getMainObj(), "UTF-8"), mEventGrpChatMsgList.get(position).getID(),mEventGrpChatMsgList.get(position).getSenderProfileID(), Utility.getInstance().getUserName(mEventGrpChatMsgList.get(position).getProfilesBySenderProfileID()), mEventGrpChatMsgList.get(position).getPhotoMessage());
                                mViewHolderOthers.mForwardReceiverMsgBtn.setVisibility(View.GONE);
                                mViewHolderOthers.mReceiverChatView.setVisibility(View.GONE);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                mViewHolderOthers.mReceiverChatLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showReceiverSelectedMsg(mViewHolderOthers);
                        return false;
                    }
                });

                if(mEventGrpChatMsgList.get(position).getIsRepliedMsg()){
                    showReplyToReceiverMsgLayout(mViewHolderOthers,position);
                } else{
                    mViewHolderOthers.mReplyToReceiverMsgLayout.setVisibility(View.GONE);
                }

               */
                    if (mEventGrpChatMsgList.get(position).getUserType().equals(AppConstants.USER)) {
                        if (mEventGrpChatMsgList.get(position).getProfilesBySenderProfileID() != null) {
                            String mProfilePicUrl = "";
                            if (mEventGrpChatMsgList.get(position)
                                    .getProfilesBySenderProfileID().getProfilePicture() != null) {
                                mProfilePicUrl = mEventGrpChatMsgList.get(position).getProfilesBySenderProfileID().getProfilePicture();
                            }
                            ((BaseActivity) mContext).setImageWithGlide(mViewHolderOthers.mOthersImgView, mProfilePicUrl, R.drawable.default_profile_icon);

                            if (mEventGrpChatMsgList.get(position).getProfilesBySenderProfileID().getDriver().isEmpty())
                                mViewHolderOthers.mOthersNameTv.setText(mEventGrpChatMsgList.get(position).getProfilesBySenderProfileID().getSpectatorName());
                            else
                                mViewHolderOthers.mOthersNameTv.setText(mEventGrpChatMsgList.get(position).getProfilesBySenderProfileID().getDriver());
                        }
                    } else {
                        if (mEventGrpChatMsgList.get(position).getPromoterBySenderUserID() != null) {
                            PromotersResModel mPromotersResModel = mEventGrpChatMsgList.get(position)
                                    .getPromoterBySenderUserID();
                            String mProfilePicUrl = "";
                            if (mPromotersResModel.getProfileImage() != null) {
                                mProfilePicUrl = mPromotersResModel.getProfileImage();
                            }
                            ((BaseActivity) mContext).setImageWithGlide(mViewHolderOthers.mOthersImgView,
                                    mProfilePicUrl, R.drawable.default_profile_icon);
                            mViewHolderOthers.mOthersNameTv.setText(mPromotersResModel.getName());
                        }
                    }

                    if (mEventGrpChatMsgList.get(position).getMessage().trim().isEmpty()) {
                        mViewHolderOthers.mOthersMsgTv.setVisibility(View.GONE);
                    } else {
                        mViewHolderOthers.mOthersMsgTv.setVisibility(View.VISIBLE);
                        try {
                            mViewHolderOthers.mOthersMsgTv.setText(URLDecoder.decode(mEventGrpChatMsgList.get(position).getMessage(), "UTF-8"));
                            //mViewHolderOthers.mOthersMsgTv.setText(replacer(sb.append(mEventGrpChatMsgList.get(position).getMainObj())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    mViewHolderOthers.mOthersMsgTimeTv.setText(((BaseActivity) mContext).getFormattedDate(mEventGrpChatMsgList.get(position).getCreatedAt()));
                    if (mEventGrpChatMsgList.get(position).getDateVisible()) {
                        mViewHolderOthers.mOthersMsgTimeTv.setVisibility(View.VISIBLE);
                    } else {
                        mViewHolderOthers.mOthersMsgTimeTv.setVisibility(View.INVISIBLE);
                    }

                    //This is for receiver
                    if (!mEventGrpChatMsgList.get(position).getPhotoMessage().trim().isEmpty()) {
                        mViewHolderOthers.ivCommentImg.setVisibility(View.VISIBLE);
                        mViewHolderOthers.smallProgressBar.setVisibility(View.VISIBLE);
                        GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + mEventGrpChatMsgList.get(position).getPhotoMessage().trim(), new LazyHeaders.Builder()
                                .addHeader("X-DreamFactory-Api-Key", mContext.getString(R.string.dream_factory_api_key))
                                .build());

                        Glide.with(mContext)
                                .load(glideUrl)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        mViewHolderOthers.smallProgressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        mViewHolderOthers.smallProgressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .apply(new RequestOptions()
                                        .dontAnimate()
                                        .centerCrop()
                                )
                                .into(mViewHolderOthers.ivCommentImg);
                    } else {
                        mViewHolderOthers.ivCommentImg.setVisibility(View.GONE);
                        mViewHolderOthers.smallProgressBar.setVisibility(View.GONE);
                    }

                    mViewHolderOthers.ivCommentImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((BaseActivity) mContext).moveLoadImageScreen(mContext, UrlUtils.FILE_URL + mEventGrpChatMsgList.get(position).getPhotoMessage());

                        }
                    });

                    mViewHolderOthers.mOthersImgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            postProfileClick(mViewHolderOthers.getAdapterPosition());
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;

            case VIEW_TYPE_LOADING:

                final ViewHolderLoader mViewHolderLoader = (ViewHolderLoader) holder;

                if (mEventGrpChatMsgList.size() != ((TotalRetrofitMsgResultCount) mContext).getTotalMsgResultCount()) {
                    mViewHolderLoader.mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mViewHolderLoader.mProgressBar.setVisibility(View.GONE);
                }

                break;

            default:
                break;

        }
    }

    private void showReplyToSenderMsgLayout(ViewHolderSender mViewHolderSender, int position) {
        mViewHolderSender.mReplyToSenderChatLayout.setVisibility(View.VISIBLE);
        mViewHolderSender.mReplyChatUserName.setText(mEventGrpChatMsgList.get(position).getReplyUserName());
        mViewHolderSender.mReplyChatView.setLayoutParams(new RelativeLayout.LayoutParams(mViewHolderSender.mReplyChatLayout.getWidth(), mViewHolderSender.mReplyChatLayout.getHeight()));
        if (mEventGrpChatMsgList.get(position).getReplyMessage().trim().isEmpty()) {
            mViewHolderSender.mReplyChatMsg.setVisibility(View.GONE);
        } else {
            mViewHolderSender.mReplyChatMsg.setVisibility(View.VISIBLE);
            mViewHolderSender.mReplyChatMsg.setText(mEventGrpChatMsgList.get(position).getReplyMessage());
        }
        if (mEventGrpChatMsgList.get(position).getReplyImage().trim().isEmpty()) {
            mViewHolderSender.mReplyChatImageIv.setVisibility(View.GONE);
        } else {
            mViewHolderSender.mReplyChatImageIv.setVisibility(View.VISIBLE);
            ((BaseActivity) mContext).setImageWithGlide(mViewHolderSender.mReplyChatImageIv, mEventGrpChatMsgList.get(position).getReplyImage(), R.drawable.default_cover_img);
        }
    }

    private void showReplyToReceiverMsgLayout(ViewHolderOthers mViewHolderReceiver, int position) {
        mViewHolderReceiver.mReplyToReceiverMsgLayout.setVisibility(View.VISIBLE);
        mViewHolderReceiver.mReplyChatUserName.setText(mEventGrpChatMsgList.get(position).getReplyUserName());
        mViewHolderReceiver.mReplyChatView.setLayoutParams(new RelativeLayout.LayoutParams(mViewHolderReceiver.mReplyChatLayout.getWidth(), mViewHolderReceiver.mReplyChatLayout.getHeight()));
        if (mEventGrpChatMsgList.get(position).getReplyMessage().trim().isEmpty()) {
            mViewHolderReceiver.mReplyChatMsg.setVisibility(View.GONE);
        } else {
            mViewHolderReceiver.mReplyChatMsg.setVisibility(View.VISIBLE);
            mViewHolderReceiver.mReplyChatMsg.setText(mEventGrpChatMsgList.get(position).getReplyMessage());
        }
        if (mEventGrpChatMsgList.get(position).getReplyImage().trim().isEmpty()) {
            mViewHolderReceiver.mReplyChatImageIv.setVisibility(View.GONE);
        } else {
            mViewHolderReceiver.mReplyChatImageIv.setVisibility(View.VISIBLE);
            ((BaseActivity) mContext).setImageWithGlide(mViewHolderReceiver.mReplyChatImageIv, mEventGrpChatMsgList.get(position).getReplyImage(), R.drawable.default_cover_img);
        }

    }

    private void showSenderSelectedMsg(ViewHolderSender mViewHolderSender) {

        if (mPreviousSelectedMsgView != null) {
            mPreviousSelectedMsgView.setVisibility(View.GONE);
            mPreviousForwardMsgIV.setVisibility(View.GONE);
        }

        mViewHolderSender.mSenderChatView.setLayoutParams(new RelativeLayout.LayoutParams(mViewHolderSender.mSenderChatLayout.getWidth(), mViewHolderSender.mSenderChatLayout.getHeight()));
        mViewHolderSender.mSenderChatView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorLighterGrey));
        mViewHolderSender.mForwardSenderMsgBtn.setVisibility(View.VISIBLE);
        mViewHolderSender.mSenderChatView.setVisibility(View.VISIBLE);
        mPreviousSelectedMsgView = mViewHolderSender.mSenderChatView;
        mPreviousForwardMsgIV = mViewHolderSender.mForwardSenderMsgBtn;


    }

    private void showReceiverSelectedMsg(ViewHolderOthers mViewHolderOthers) {
        if (mPreviousSelectedMsgView != null) {
            mPreviousSelectedMsgView.setVisibility(View.GONE);
            mPreviousForwardMsgIV.setVisibility(View.GONE);
        }
        mViewHolderOthers.mReceiverChatView.setLayoutParams(new RelativeLayout.LayoutParams(mViewHolderOthers.mReceiverChatView.getWidth(), mViewHolderOthers.mReceiverChatView.getHeight()));
        mViewHolderOthers.mReceiverChatView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorLighterGrey));
        mViewHolderOthers.mForwardReceiverMsgBtn.setVisibility(View.VISIBLE);
        mViewHolderOthers.mReceiverChatView.setVisibility(View.VISIBLE);
        mPreviousSelectedMsgView = mViewHolderOthers.mReceiverChatView;
        mPreviousForwardMsgIV = mViewHolderOthers.mForwardReceiverMsgBtn;
    }

    //TODO new implementation for profile click

    private void postProfileClick(int selPos) {
        try {
            int mCurrentProfile = mMyProfileResModel.getID();
            if (mEventGrpChatMsgList.get(selPos).getProfilesBySenderProfileID() != null || mEventGrpChatMsgList.get(selPos).getProfilesByReplyUserProfileID() != null) {
                if (mEventGrpChatMsgList.get(selPos).getUserType().equals("user")) {
                    if (mCurrentProfile != mEventGrpChatMsgList.get(selPos).getSenderProfileID() /*|| mCurrentProfile != mEventGrpChatMsgList.get(selPos).getReplyUserProfileID()*/) {
                        Intent intent = new Intent(mContext, OthersMotoFileActivity.class);
                        intent.putExtra(AppConstants.MY_PROFILE_ID, mCurrentProfile);
                        intent.putExtra(AppConstants.OTHER_PROFILE_ID, mEventGrpChatMsgList.get(selPos).getSenderProfileID());
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, MyMotoFileActivity.class);
                        intent.putExtra(AppConstants.MY_PROFILE_ID, mCurrentProfile);
                        mContext.startActivity(intent);
                    }
                }

            } else if (mEventGrpChatMsgList.get(selPos).getPromoterBySenderUserID() != null) {
                Intent intent = null;
                String mUserType = mEventGrpChatMsgList.get(selPos).getUserType();
                switch (mUserType) {
                    case PromotersModel.PROMOTER:
                        intent = new Intent(mContext, PromoterProfileActivity.class);
                        break;
                    case PromotersModel.CLUB:
                        intent = new Intent(mContext, ClubProfileActivity.class);
                        break;
                    case PromotersModel.NEWS_AND_MEDIA:
                        intent = new Intent(mContext, NewsAndMediaProfileActivity.class);
                        break;
                    case PromotersModel.SHOP:
                        intent = new Intent(mContext, PerformanceShopProfileActivity.class);
                        break;
                    case PromotersModel.TRACK:
                        intent = new Intent(mContext, TrackProfileActivity.class);
                        break;
                    default:
                        intent = new Intent(mContext, PromoterProfileActivity.class);
                        break;
                }
                /*intent.putExtra(PromotersModel.PROMOTERS_RES_MODEL, mEventGrpChatMsgList.get(selPos).getPromoterBySenderUserID());
                intent.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);*/
                //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                //MotoHub.getApplicationInstance().setmPromoterResModel(mEventGrpChatMsgList.get(selPos).getPromoterBySenderUserID());
                EventBus.getDefault().postSticky(mMyProfileResModel);
                EventBus.getDefault().postSticky(mEventGrpChatMsgList.get(selPos).getPromoterBySenderUserID());
                mContext.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getUserId() {
        return PreferenceUtils.getInstance(mContext).getIntData(PreferenceUtils.USER_ID);
    }

    public int getProfileCurrentPos() {
        return PreferenceUtils.getInstance(mContext).getIntData(PreferenceUtils.CURRENT_PROFILE_POS);
    }

    public interface TotalRetrofitMsgResultCount {
        int getTotalMsgResultCount();
    }

    private class ViewHolderSender extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView mSenderImgView;
        TextView mSenderMsgTv, mSenderMsgTimeTv, mSenderNameTv;
        ImageView ivCommentImg;
        ProgressBar smallProgressBar;
        RelativeLayout mSenderChatLayout;
        View mSenderChatView;
        ImageView mForwardSenderMsgBtn;
        RelativeLayout mReplyToSenderChatLayout;
        TextView mReplyChatUserName;
        TextView mReplyChatMsg;
        ImageView mReplyChatImageIv;
        View mReplyChatView;
        RelativeLayout mReplyChatLayout;

        ViewHolderSender(View v) {
            super(v);
            mSenderMsgTv = v.findViewById(R.id.senderMsgTv);
            mSenderMsgTimeTv = v.findViewById(R.id.senderMsgTimeTv);
            mSenderNameTv = v.findViewById(R.id.senderNameTv);
            mSenderImgView = v.findViewById(R.id.profileImgView);
            mSenderMsgTv.setOnClickListener(this);
            ivCommentImg = v.findViewById(R.id.ivCommentImg);
            smallProgressBar = v.findViewById(R.id.smallProgressBar);
            mSenderChatLayout = v.findViewById(R.id.sender_chat_layout);
            mSenderChatView = v.findViewById(R.id.senderChatView);
            mForwardSenderMsgBtn = v.findViewById(R.id.forwardSenderMsgBtn);
            mReplyToSenderChatLayout = v.findViewById(R.id.senderReplyChatLayout);
            mReplyChatUserName = v.findViewById(R.id.replyChatUserNameTv);
            mReplyChatMsg = v.findViewById(R.id.replyChatMsgTv);
            mReplyChatImageIv = v.findViewById(R.id.replyChatImageIv);
            mReplyChatView = v.findViewById(R.id.replyChatView);
            mReplyChatLayout = v.findViewById(R.id.replyChatLayout);

        }

        @Override
        public void onClick(View v) {
            EventGrpChatMsgResModel groupChatMsgResModel = mEventGrpChatMsgList.get(getLayoutPosition());
            mSenderMsgTimeTv.setVisibility(View.VISIBLE);
            if (adapterpos == 0) {
                if (getLayoutPosition() == 0) {
                    boolean firstindex = groupChatMsgResModel.getDateVisible();
                    if (firstindex) {
                        groupChatMsgResModel.setDateVisible(false);
                    } else {
                        groupChatMsgResModel.setDateVisible(true);
                    }
                } else {
                    mEventGrpChatMsgList.get(0).setDateVisible(false);
                    groupChatMsgResModel.setDateVisible(true);
                }
                adapterpos = getLayoutPosition();
            } else {
                if (adapterpos == getLayoutPosition()) {
                    groupChatMsgResModel.setDateVisible(false);
                    adapterpos = 0;
                } else {
                    EventGrpChatMsgResModel old_eventChatMsgResModel = mEventGrpChatMsgList.get(adapterpos);
                    old_eventChatMsgResModel.setDateVisible(false);
                    groupChatMsgResModel.setDateVisible(true);
                    adapterpos = getLayoutPosition();
                }
            }
            notifyDataSetChanged();
        }

    }

    private class ViewHolderOthers extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView mOthersImgView;
        TextView mOthersMsgTv, mOthersMsgTimeTv, mOthersNameTv;
        ImageView ivCommentImg;
        ProgressBar smallProgressBar;
        View mReceiverChatView;
        ImageView mForwardReceiverMsgBtn;
        RelativeLayout mReplyToReceiverMsgLayout;
        TextView mReplyChatUserName;
        TextView mReplyChatMsg;
        ImageView mReplyChatImageIv;
        RelativeLayout mReceiverChatLayout;
        View mReplyChatView;
        RelativeLayout mReplyChatLayout;

        ViewHolderOthers(View v) {
            super(v);
            mOthersMsgTv = v.findViewById(R.id.receiverMsgTv);
            mOthersMsgTimeTv = v.findViewById(R.id.receiverMsgTimeTv);
            mOthersNameTv = v.findViewById(R.id.receiverNameTv);
            mOthersImgView = v.findViewById(R.id.profileImgView);
            mOthersMsgTv.setOnClickListener(this);
            ivCommentImg = v.findViewById(R.id.ivCommentImg);
            smallProgressBar = v.findViewById(R.id.smallProgressBar);
            mReceiverChatLayout = v.findViewById(R.id.receiverChatLayout);
            mReceiverChatView = v.findViewById(R.id.receiverChatView);
            mForwardReceiverMsgBtn = v.findViewById(R.id.forwardReceiverMsgBtn);
            mReplyToReceiverMsgLayout = v.findViewById(R.id.receiverReplyChatLayout);
            mReplyChatUserName = v.findViewById(R.id.replyChatUserNameTv);
            mReplyChatMsg = v.findViewById(R.id.replyChatMsgTv);
            mReplyChatImageIv = v.findViewById(R.id.replyChatImageIv);
            mReplyChatView = v.findViewById(R.id.replyChatView);
            mReplyChatLayout = v.findViewById(R.id.replyChatLayout);
        }

        @Override
        public void onClick(View v) {
            EventGrpChatMsgResModel groupChatMsgResModel = mEventGrpChatMsgList.get(getLayoutPosition());
            mOthersMsgTv.setVisibility(View.VISIBLE);
            if (adapterpos == 0) {
                if (getLayoutPosition() == 0) {
                    boolean firstindex = groupChatMsgResModel.getDateVisible();
                    if (firstindex) {
                        groupChatMsgResModel.setDateVisible(false);
                    } else {
                        groupChatMsgResModel.setDateVisible(true);
                    }
                } else {
                    mEventGrpChatMsgList.get(0).setDateVisible(false);
                    groupChatMsgResModel.setDateVisible(true);
                }
                adapterpos = getLayoutPosition();
            } else {
                if (adapterpos == getLayoutPosition()) {
                    groupChatMsgResModel.setDateVisible(false);
                    adapterpos = 0;
                } else {
                    EventGrpChatMsgResModel old_eventChatMsgResModel = mEventGrpChatMsgList.get(adapterpos);
                    old_eventChatMsgResModel.setDateVisible(false);
                    groupChatMsgResModel.setDateVisible(true);
                    adapterpos = getLayoutPosition();
                }
            }
            notifyDataSetChanged();
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
