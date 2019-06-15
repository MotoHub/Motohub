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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.MyMotoFileActivity;
import online.motohub.activity.OthersMotoFileActivity;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SingleChatMsgResModel;
import online.motohub.model.SingleChatRoomResModel;
import online.motohub.constants.AppConstants;
import online.motohub.util.UrlUtils;
import online.motohub.util.Utility;

public class ChatBoxSingleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int SENDER = 1;
    private static final int RECEIVER = 2;
    private List<SingleChatMsgResModel> mSingleChatMsgList;
    private SingleChatRoomResModel mSingleChatRoomResModel;
    private Context mContext;
    private ProfileResModel mMyProfileResModel;
    private StringBuffer sb = new StringBuffer();

    public ChatBoxSingleAdapter(List<SingleChatMsgResModel> singleChatMsgList, Context ctx, SingleChatRoomResModel singleChatRoomResModel, ProfileResModel myProfileResModel) {
        this.mSingleChatMsgList = singleChatMsgList;
        this.mSingleChatRoomResModel = singleChatRoomResModel;
        this.mMyProfileResModel = myProfileResModel;
        this.mContext = ctx;
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
        return mSingleChatMsgList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        if (position >= mSingleChatMsgList.size()) {
            return VIEW_TYPE_LOADING;
        } else if (mSingleChatMsgList.get(position).getFromProfileID() == (mSingleChatRoomResModel.getFromProfileID())) {
            return SENDER;
        } else if (mSingleChatMsgList.get(position).getFromProfileID() == (mSingleChatRoomResModel.getToProfileID())) {
            return RECEIVER;
        } else {
            return super.getItemViewType(position);
        }

    }

    void showHideReceiverMsgTime(ViewHolderReceiver mHolder, int position) {
        SingleChatMsgResModel singleChatMsgResModel = mSingleChatMsgList.get(position);

        if (singleChatMsgResModel.getIsDateVisible()) {
            singleChatMsgResModel.setIsDateVisible(false);
            mHolder.mReceiverMsgTimeTv.setVisibility(View.INVISIBLE);
        } else {
            singleChatMsgResModel.setIsDateVisible(true);
            mHolder.mReceiverMsgTimeTv.setVisibility(View.VISIBLE);
        }
    }

    void showHideSenderMsgTime(ViewHolderSender mHolder, int position) {
        SingleChatMsgResModel singleChatMsgResModel = mSingleChatMsgList.get(position);

        if (singleChatMsgResModel.getIsDateVisible()) {
            singleChatMsgResModel.setIsDateVisible(false);
            mHolder.mSenderMsgTimeTv.setVisibility(View.INVISIBLE);
        } else {
            singleChatMsgResModel.setIsDateVisible(true);
            mHolder.mSenderMsgTimeTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public long getItemId(int position) {
        return (getItemViewType(position) == SENDER || getItemViewType(position) == RECEIVER) ? position : -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mView;

        switch (viewType) {

            case SENDER:
                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_sender, parent, false);
                return new ViewHolderSender(mView);
            case RECEIVER:
                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_receiver, parent, false);
                return new ViewHolderReceiver(mView);
            case VIEW_TYPE_LOADING:
                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_progress_bar, parent, false);
                return new ViewHolderLoader(mView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

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

                    String mProfilePicUrl = mMyProfileResModel.getProfilePicture();
                    //((BaseActivity) mContext).setImageWithGlide(mViewHolderSender.mSenderImgView, mProfilePicUrl, R.drawable.default_profile_icon);

                    if (!mProfilePicUrl.isEmpty()) {
                        ((BaseActivity) mContext).setImageWithGlide(mViewHolderSender.mSenderImgView, mProfilePicUrl, R.drawable.default_profile_icon);
                    } else {
                        mViewHolderSender.mSenderImgView.setImageResource(R.drawable.default_profile_icon);
                    }

                    mViewHolderSender.mSenderNameTv.setText(Utility.getInstance().getUserName(mMyProfileResModel));

                    mViewHolderSender.mSenderMsgTimeTv.setText(((BaseActivity) mContext).getFormattedDate(mSingleChatMsgList.get(position).getCreatedAt()));
                    if (mSingleChatMsgList.get(position).getIsDateVisible()) {
                        mViewHolderSender.mSenderMsgTimeTv.setVisibility(View.VISIBLE);
                    } else {
                        mViewHolderSender.mSenderMsgTimeTv.setVisibility(View.INVISIBLE);
                    }

                    //This is for image
                    if (!mSingleChatMsgList.get(position).getPhotoMessage().trim().isEmpty()) {
                        mViewHolderSender.ivCommentImg.setVisibility(View.VISIBLE);
                        mViewHolderSender.smallProgressBar.setVisibility(View.VISIBLE);
                        GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + mSingleChatMsgList.get(position).getPhotoMessage().trim(), new LazyHeaders.Builder()
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

                    if (mSingleChatMsgList.get(position).getMessage().trim().isEmpty()) {
                        mViewHolderSender.mSenderMsgTv.setVisibility(View.GONE);
                    } else {
                        mViewHolderSender.mSenderMsgTv.setVisibility(View.VISIBLE);
                        try {
                            mViewHolderSender.mSenderMsgTv.setText(URLDecoder.decode(mSingleChatMsgList.get(position).getMessage(), "UTF-8"));
                            //mViewHolderSender.mSenderMsgTv.setText(replacer(sb.append(mSingleChatMsgList.get(position).getMainObj())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    mViewHolderSender.ivCommentImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((BaseActivity) mContext).moveLoadImageScreen(mContext, UrlUtils.FILE_URL + mSingleChatMsgList.get(position).getPhotoMessage());

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

            case RECEIVER:
                try {
                    final ViewHolderReceiver mViewHolderReceiver = (ViewHolderReceiver) holder;
                    mViewHolderReceiver.mReceiverMsgTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showHideReceiverMsgTime(mViewHolderReceiver, position);
                        }
                    });

                    String mProfilePicOthersUrl = mSingleChatRoomResModel.getProfilesByToProfileID().getProfilePicture();
                    if (!mProfilePicOthersUrl.isEmpty()) {
                        ((BaseActivity) mContext).setImageWithGlide(mViewHolderReceiver.mReceiverImgView, mProfilePicOthersUrl,
                                R.drawable.default_profile_icon);
                    } else {
                        mViewHolderReceiver.mReceiverImgView.setImageResource(R.drawable.default_profile_icon);
                    }

                    mViewHolderReceiver.mReceiverNameTv.setText(Utility.getInstance().getUserName(mSingleChatRoomResModel.getProfilesByToProfileID()));

                    if (!mSingleChatMsgList.get(position).getMessage().trim().isEmpty()) {
                        mViewHolderReceiver.mReceiverMsgTv.setVisibility(View.VISIBLE);
                        try {
                            mViewHolderReceiver.mReceiverMsgTv.setText(URLDecoder.decode(mSingleChatMsgList.get(position).getMessage(), "UTF-8"));
                            //mViewHolderReceiver.mReceiverMsgTv.setText(replacer(sb.append(mSingleChatMsgList.get(position).getMainObj())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        mViewHolderReceiver.mReceiverMsgTv.setVisibility(View.GONE);
                    }

                    mViewHolderReceiver.mReceiverMsgTimeTv.setText(((BaseActivity) mContext).getFormattedDate(mSingleChatMsgList.get(position).getCreatedAt()));
                    if (mSingleChatMsgList.get(position).getIsDateVisible()) {
                        mViewHolderReceiver.mReceiverMsgTimeTv.setVisibility(View.VISIBLE);
                    } else {
                        mViewHolderReceiver.mReceiverMsgTimeTv.setVisibility(View.INVISIBLE);
                    }

                    //This is for receiver
                    if (!mSingleChatMsgList.get(position).getPhotoMessage().trim().isEmpty()) {
                        mViewHolderReceiver.ivCommentImg.setVisibility(View.VISIBLE);
                        mViewHolderReceiver.smallProgressBar.setVisibility(View.VISIBLE);
                        GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + mSingleChatMsgList.get(position).getPhotoMessage().trim(), new LazyHeaders.Builder()
                                .addHeader("X-DreamFactory-Api-Key", mContext.getString(R.string.dream_factory_api_key))
                                .build());

                        Glide.with(mContext)
                                .load(glideUrl)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        mViewHolderReceiver.smallProgressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        mViewHolderReceiver.smallProgressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .apply(new RequestOptions()
                                        .dontAnimate()
                                        .centerCrop()
                                )
                                .into(mViewHolderReceiver.ivCommentImg);
                    } else {
                        mViewHolderReceiver.ivCommentImg.setVisibility(View.GONE);
                        mViewHolderReceiver.smallProgressBar.setVisibility(View.GONE);
                    }

                    mViewHolderReceiver.ivCommentImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((BaseActivity) mContext).moveLoadImageScreen(mContext, UrlUtils.FILE_URL + mSingleChatMsgList.get(position).getPhotoMessage());
                        }
                    });

                    mViewHolderReceiver.mReceiverImgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            postProfileClick(mViewHolderReceiver.getAdapterPosition());
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case VIEW_TYPE_LOADING:

                final ViewHolderLoader mViewHolderLoader = (ViewHolderLoader) holder;

                if (mSingleChatMsgList.size() != ((TotalRetrofitMsgResultCount) mContext).getTotalMsgResultCount()) {
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

            if (mCurrentProfile == mSingleChatMsgList.get(selPos).getFromProfileID()) {
                Intent intent = new Intent(mContext, MyMotoFileActivity.class);
                intent.putExtra(AppConstants.MY_PROFILE_ID, mCurrentProfile);
                mContext.startActivity(intent);
            } else {
                Intent intent = new Intent(mContext, OthersMotoFileActivity.class);
                intent.putExtra(AppConstants.MY_PROFILE_ID, mCurrentProfile);
                intent.putExtra(AppConstants.OTHER_PROFILE_ID, mSingleChatMsgList.get(selPos).getFromProfileID());
                mContext.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface TotalRetrofitMsgResultCount {
        int getTotalMsgResultCount();
    }

    private class ViewHolderSender extends RecyclerView.ViewHolder {

        CircleImageView mSenderImgView;
        TextView mSenderMsgTv, mSenderMsgTimeTv, mSenderNameTv;
        ImageView ivCommentImg;
        ProgressBar smallProgressBar;

        ViewHolderSender(View v) {
            super(v);
            mSenderMsgTv = v.findViewById(R.id.senderMsgTv);
            mSenderMsgTimeTv = v.findViewById(R.id.senderMsgTimeTv);
            mSenderNameTv = v.findViewById(R.id.senderNameTv);
            mSenderImgView = v.findViewById(R.id.profileImgView);
            // mSenderMsgTv.setOnClickListener(this);
            ivCommentImg = v.findViewById(R.id.ivCommentImg);
            smallProgressBar = v.findViewById(R.id.smallProgressBar);
        }

       /* @Override
        public void onClick(View v) {
            SingleChatMsgResModel singleChatMsgResModel = mSingleChatMsgList.get(getLayoutPosition());
            mSenderMsgTimeTv.setVisibility(View.VISIBLE);
            if (adapterpos == 0) {
                if (getLayoutPosition() == 0) {
                    boolean firstindex = singleChatMsgResModel.getIsDateVisible();
                    if (firstindex) {
                        singleChatMsgResModel.setIsDateVisible(false);
                    } else {
                        singleChatMsgResModel.setIsDateVisible(true);
                    }
                } else {
                    mSingleChatMsgList.get(0).setIsDateVisible(false);
                    singleChatMsgResModel.setIsDateVisible(true);
                }
                adapterpos = getLayoutPosition();
            } else {
                if (adapterpos == getLayoutPosition()) {
                    singleChatMsgResModel.setIsDateVisible(false);
                    adapterpos = 0;
                } else {
                    SingleChatMsgResModel old_singleChatMsgResModel = mSingleChatMsgList.get(adapterpos);
                    old_singleChatMsgResModel.setIsDateVisible(false);
                    singleChatMsgResModel.setIsDateVisible(true);
                    adapterpos = getLayoutPosition();
                }
            }
            notifyDataSetChanged();

    }*/
    }

    private class ViewHolderReceiver extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView mReceiverImgView;
        TextView mReceiverMsgTv, mReceiverMsgTimeTv, mReceiverNameTv;
        ImageView ivCommentImg;
        ProgressBar smallProgressBar;

        ViewHolderReceiver(View v) {
            super(v);
            mReceiverMsgTv = v.findViewById(R.id.receiverMsgTv);
            mReceiverMsgTimeTv = v.findViewById(R.id.receiverMsgTimeTv);
            mReceiverNameTv = v.findViewById(R.id.receiverNameTv);
            mReceiverImgView = v.findViewById(R.id.profileImgView);
            ivCommentImg = v.findViewById(R.id.ivCommentImg);
            smallProgressBar = v.findViewById(R.id.smallProgressBar);
            // mReceiverMsgTv.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

          /*  if (adapterpos == 0) {
                if (getLayoutPosition() == 0) {
                    boolean firstindex = singleChatMsgResModel.getIsDateVisible();
                    if (firstindex) {
                        singleChatMsgResModel.setIsDateVisible(false);
                    } else {
                        singleChatMsgResModel.setIsDateVisible(true);
                    }
                } else {
                    mSingleChatMsgList.get(0).setIsDateVisible(false);
                    singleChatMsgResModel.setIsDateVisible(true);
                }
                adapterpos = getLayoutPosition();
            } else {
                if (adapterpos == getLayoutPosition()) {
                    singleChatMsgResModel.setIsDateVisible(false);
                    mReceiverMsgTimeTv.setVisibility(View.INVISIBLE);
                    adapterpos = 0;
                } else {
                    SingleChatMsgResModel old_singleChatMsgResModel = mSingleChatMsgList.get(adapterpos);
                    old_singleChatMsgResModel.setIsDateVisible(false);
                    singleChatMsgResModel.setIsDateVisible(true);
                    adapterpos = getLayoutPosition();
                }
            }*/
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
