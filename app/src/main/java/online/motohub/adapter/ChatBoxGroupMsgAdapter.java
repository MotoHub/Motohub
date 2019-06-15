package online.motohub.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
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

import java.net.URLDecoder;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.MyMotoFileActivity;
import online.motohub.activity.OthersMotoFileActivity;
import online.motohub.model.GroupChatMsgResModel;
import online.motohub.model.ProfileResModel;
import online.motohub.constants.AppConstants;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.UrlUtils;

public class ChatBoxGroupMsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int SENDER = 1;
    private static final int OTHERS = 2;
    private final ArrayList<GroupChatMsgResModel> mGrpChatMsgList;
    private final Context mContext;
    private Integer adapterpos = 0;
    private ProfileResModel mMyProfileResModel;
    private StringBuffer sb = new StringBuffer();

    public ChatBoxGroupMsgAdapter(ProfileResModel mMyProfileResModel, ArrayList<GroupChatMsgResModel> grpChatMsgList, Context ctx) {
        this.mGrpChatMsgList = grpChatMsgList;
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

    void showHideOtherMsgTime(ViewHolderOthers mHolder, int position) {

        if (mGrpChatMsgList.get(position).getDateVisible()) {
            mGrpChatMsgList.get(position).setDateVisible(false);
            mHolder.mOthersMsgTimeTv.setVisibility(View.INVISIBLE);
        } else {
            mGrpChatMsgList.get(position).setDateVisible(true);
            mHolder.mOthersMsgTimeTv.setVisibility(View.VISIBLE);
        }
    }

    void showHideSenderMsgTime(ViewHolderSender mHolder, int position) {
        if (mGrpChatMsgList.get(position).getDateVisible()) {
            mGrpChatMsgList.get(position).setDateVisible(false);
            mHolder.mSenderMsgTimeTv.setVisibility(View.INVISIBLE);
        } else {
            mGrpChatMsgList.get(position).setDateVisible(true);
            mHolder.mSenderMsgTimeTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mGrpChatMsgList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        PreferenceUtils.getInstance(mContext).getIntData(PreferenceUtils.USER_ID);

        if (position >= mGrpChatMsgList.size()) {
            return VIEW_TYPE_LOADING;
        } else if (PreferenceUtils.getInstance(mContext).getIntData(PreferenceUtils.USER_ID) == mGrpChatMsgList.get(position).getSenderUserID()) {
            return SENDER;
        } else if (PreferenceUtils.getInstance(mContext).getIntData(PreferenceUtils.USER_ID) != mGrpChatMsgList.get(position).getSenderUserID()) {
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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        switch (getItemViewType(position)) {

            case SENDER:
                try {

                    final ViewHolderSender mViewHolderSender = (ViewHolderSender) viewHolder;

                    String mProfilePicUrl = mGrpChatMsgList.get(position).getProfilesBySenderProfileID().getProfilePicture();
                    if (!mProfilePicUrl.isEmpty()) {
                        ((BaseActivity) mContext).setImageWithGlide(mViewHolderSender.mSenderImgView, mProfilePicUrl,
                                R.drawable.default_profile_icon);
                    } else {
                        mViewHolderSender.mSenderImgView.setImageResource(R.drawable.default_profile_icon);
                    }

                    if (mGrpChatMsgList.get(position).getMessage().trim().isEmpty()) {
                        mViewHolderSender.mSenderMsgTv.setVisibility(View.GONE);
                    } else {
                        mViewHolderSender.mSenderMsgTv.setVisibility(View.VISIBLE);
                        try {
                            mViewHolderSender.mSenderMsgTv.setText(URLDecoder.decode(mGrpChatMsgList.get(position).getMessage(), "UTF-8"));
                            //mViewHolderSender.mSenderMsgTv.setText(replacer(sb.append(mGrpChatMsgList.get(position).getMainObj())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (mGrpChatMsgList.get(position).getProfilesBySenderProfileID().getDriver().isEmpty())
                        mViewHolderSender.mSenderNameTv.setText(mGrpChatMsgList.get(position).getProfilesBySenderProfileID().getSpectatorName());
                    else
                        mViewHolderSender.mSenderNameTv.setText(mGrpChatMsgList.get(position).getProfilesBySenderProfileID().getDriver());

                    mViewHolderSender.mSenderMsgTimeTv.setText(((BaseActivity) mContext).getFormattedDate(mGrpChatMsgList.get(position).getCreatedAt()));
                    if (mGrpChatMsgList.get(position).getDateVisible()) {
                        mViewHolderSender.mSenderMsgTimeTv.setVisibility(View.VISIBLE);
                    } else {
                        mViewHolderSender.mSenderMsgTimeTv.setVisibility(View.INVISIBLE);
                    }

                    //This is for image
                    if (!mGrpChatMsgList.get(position).getPhotoMessage().trim().isEmpty()) {
                        mViewHolderSender.ivCommentImg.setVisibility(View.VISIBLE);
                        mViewHolderSender.smallProgressBar.setVisibility(View.VISIBLE);
                        GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + mGrpChatMsgList.get(position).getPhotoMessage().trim(), new LazyHeaders.Builder()
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
                            ((BaseActivity) mContext).moveLoadImageScreen(mContext, UrlUtils.FILE_URL + mGrpChatMsgList.get(position).getPhotoMessage());

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

                    final ViewHolderOthers mViewHolderOthers = (ViewHolderOthers) viewHolder;

                    String mProfilePicOthersUrl = mGrpChatMsgList.get(position).getProfilesBySenderProfileID().getProfilePicture();
                    if (!mProfilePicOthersUrl.isEmpty()) {
                        ((BaseActivity) mContext).setImageWithGlide(mViewHolderOthers.mOthersImgView, mProfilePicOthersUrl,
                                R.drawable.default_profile_icon);
                    } else {
                        mViewHolderOthers.mOthersImgView.setImageResource(R.drawable.default_profile_icon);
                    }

                    if (mGrpChatMsgList.get(position).getProfilesBySenderProfileID().getDriver().isEmpty())
                        mViewHolderOthers.mOthersNameTv.setText(mGrpChatMsgList.get(position).getProfilesBySenderProfileID().getSpectatorName());
                    else
                        mViewHolderOthers.mOthersNameTv.setText(mGrpChatMsgList.get(position).getProfilesBySenderProfileID().getDriver());

                    if (mGrpChatMsgList.get(position).getMessage().trim().isEmpty()) {
                        mViewHolderOthers.mOthersMsgTv.setVisibility(View.GONE);
                    } else {
                        mViewHolderOthers.mOthersMsgTv.setVisibility(View.VISIBLE);
                        try {
                            mViewHolderOthers.mOthersMsgTv.setText(URLDecoder.decode(mGrpChatMsgList.get(position).getMessage(), "UTF-8"));
                            //mViewHolderOthers.mOthersMsgTv.setText(replacer(sb.append(mGrpChatMsgList.get(position).getMainObj())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    mViewHolderOthers.mOthersMsgTimeTv.setText(((BaseActivity) mContext).getFormattedDate(mGrpChatMsgList.get(position).getCreatedAt()));
                    if (mGrpChatMsgList.get(position).getDateVisible()) {
                        mViewHolderOthers.mOthersMsgTimeTv.setVisibility(View.VISIBLE);
                    } else {
                        mViewHolderOthers.mOthersMsgTimeTv.setVisibility(View.INVISIBLE);
                    }

                    //This is for receiver
                    if (!mGrpChatMsgList.get(position).getPhotoMessage().trim().isEmpty()) {
                        mViewHolderOthers.ivCommentImg.setVisibility(View.VISIBLE);
                        mViewHolderOthers.smallProgressBar.setVisibility(View.VISIBLE);
                        GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + mGrpChatMsgList.get(position).getPhotoMessage().trim(), new LazyHeaders.Builder()
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
                            ((BaseActivity) mContext).moveLoadImageScreen(mContext, UrlUtils.FILE_URL + mGrpChatMsgList.get(position).getPhotoMessage());

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

                final ViewHolderLoader mViewHolderLoader = (ViewHolderLoader) viewHolder;

                if (mGrpChatMsgList.size() != ((TotalRetrofitMsgResultCount) mContext).getTotalMsgResultCount()) {
                    mViewHolderLoader.mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mViewHolderLoader.mProgressBar.setVisibility(View.GONE);
                }

                break;

            default:
                break;
        }
    }

    private void postProfileClick(int selPos) {
        try {
            int mCurrentProfile = mMyProfileResModel.getID();
            if (mGrpChatMsgList.get(selPos).getProfilesBySenderProfileID() != null) {
                if (mCurrentProfile != mGrpChatMsgList.get(selPos).getSenderProfileID()) {
                    Intent intent = new Intent(mContext, OthersMotoFileActivity.class);
                    intent.putExtra(AppConstants.MY_PROFILE_ID, mCurrentProfile);
                    intent.putExtra(AppConstants.OTHER_PROFILE_ID, mGrpChatMsgList.get(selPos).getSenderProfileID());
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, MyMotoFileActivity.class);
                    intent.putExtra(AppConstants.MY_PROFILE_ID, mCurrentProfile);
                    mContext.startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        RelativeLayout mReplyToSenderMsgLayout;


        ViewHolderSender(View v) {
            super(v);
            mSenderMsgTv = v.findViewById(R.id.senderMsgTv);
            mSenderMsgTimeTv = v.findViewById(R.id.senderMsgTimeTv);
            mSenderNameTv = v.findViewById(R.id.senderNameTv);
            mSenderImgView = v.findViewById(R.id.profileImgView);
            // mSenderMsgTv.setOnClickListener(this);
            ivCommentImg = v.findViewById(R.id.ivCommentImg);
            smallProgressBar = v.findViewById(R.id.smallProgressBar);
            mReplyToSenderMsgLayout = v.findViewById(R.id.senderReplyChatLayout);
            mReplyToSenderMsgLayout.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            GroupChatMsgResModel groupChatMsgResModel = mGrpChatMsgList.get(getLayoutPosition());
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
                    mGrpChatMsgList.get(0).setDateVisible(false);
                    groupChatMsgResModel.setDateVisible(true);
                }
                adapterpos = getLayoutPosition();
            } else {
                if (adapterpos == getLayoutPosition()) {
                    groupChatMsgResModel.setDateVisible(false);
                    adapterpos = 0;
                } else {
                    GroupChatMsgResModel old_singleChatMsgResModel = mGrpChatMsgList.get(adapterpos);
                    old_singleChatMsgResModel.setDateVisible(false);
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
        RelativeLayout mReplyToReceiverMsgLayout;

        ViewHolderOthers(View v) {
            super(v);
            mOthersMsgTv = v.findViewById(R.id.receiverMsgTv);
            mOthersMsgTimeTv = v.findViewById(R.id.receiverMsgTimeTv);
            mOthersNameTv = v.findViewById(R.id.receiverNameTv);
            mOthersImgView = v.findViewById(R.id.profileImgView);
            mOthersMsgTv.setOnClickListener(this);
            ivCommentImg = v.findViewById(R.id.ivCommentImg);
            smallProgressBar = v.findViewById(R.id.smallProgressBar);
            mReplyToReceiverMsgLayout = v.findViewById(R.id.receiverReplyChatLayout);
            mReplyToReceiverMsgLayout.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            GroupChatMsgResModel groupChatMsgResModel = mGrpChatMsgList.get(getLayoutPosition());
            mOthersMsgTimeTv.setVisibility(View.VISIBLE);
            if (adapterpos == 0) {
                if (getLayoutPosition() == 0) {
                    boolean firstindex = groupChatMsgResModel.getDateVisible();
                    if (firstindex) {
                        groupChatMsgResModel.setDateVisible(false);
                    } else {
                        groupChatMsgResModel.setDateVisible(true);
                    }
                } else {
                    mGrpChatMsgList.get(0).setDateVisible(false);
                    groupChatMsgResModel.setDateVisible(true);
                }
                adapterpos = getLayoutPosition();
            } else {
                if (adapterpos == getLayoutPosition()) {
                    groupChatMsgResModel.setDateVisible(false);
                    adapterpos = 0;
                } else {
                    GroupChatMsgResModel old_singleChatMsgResModel = mGrpChatMsgList.get(adapterpos);
                    old_singleChatMsgResModel.setDateVisible(false);
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
