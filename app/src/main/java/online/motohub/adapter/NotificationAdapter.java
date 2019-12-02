package online.motohub.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.ChatBoxEventGrpActivity;
import online.motohub.activity.ChatBoxGroupActivity;
import online.motohub.activity.ChatBoxSingleActivity;
import online.motohub.activity.CommentReplyActivity;
import online.motohub.activity.EventLiveActivity;
import online.motohub.activity.NotificationActivity;
import online.motohub.activity.NotificationEventCreatedActivity;
import online.motohub.activity.NotificationUpdateProfileActivity;
import online.motohub.activity.OthersMotoFileActivity;
import online.motohub.activity.PostCommentLikeViewActivity;
import online.motohub.activity.PostViewActivity;
import online.motohub.activity.PromoterVideoGalleryActivity;
import online.motohub.activity.VideoCommentReplyActivity;
import online.motohub.activity.VideoCommentsActivity;
import online.motohub.activity.ViewLiveVideoViewScreen2;
import online.motohub.activity.ViewRequestUsersActivity;
import online.motohub.activity.ViewStreamUsersActivity;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.model.NotificationResModel;
import online.motohub.model.PostsModel;
import online.motohub.model.ProfileResModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.APIConstants;
import online.motohub.util.Utility;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_POSTS = 1;
    private Context mContext;
    private ArrayList<NotificationResModel> mNotificationListData;

    public NotificationAdapter(@NonNull Context context, ArrayList<NotificationResModel> notificationListData) {
        //super(context, R.layout.row_list_view_item, notificationListData);
        this.mContext = context;
        this.mNotificationListData = notificationListData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView;
        switch (viewType) {
            case VIEW_TYPE_POSTS:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_view_item, parent, false);
                return new ViewHolder(view);
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
    public int getItemViewType(int position) {
        if (position >= mNotificationListData.size()) {
            return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_POSTS;
        }
    }

    @Override
    public int getItemCount() {
        return mNotificationListData.size();
    }

    @Override
    public long getItemId(int position) {
        return (getItemViewType(position) == VIEW_TYPE_POSTS) ? position : -1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_POSTS:
                try {
                    ViewHolder mViewHolder = (ViewHolder) holder;
                    JSONObject mDataObj = new JSONObject(mNotificationListData.get(position).getData());
                    JSONObject mDetailsObj = mDataObj.getJSONObject("Details");
                    ProfileResModel mProfileResModel = new ProfileResModel();
                    String mMsg = mDataObj.getString("Msg");
                    String mUserName = "MotoHUB";
                    mViewHolder.mNotificationTypeTv.setVisibility(View.VISIBLE);
                    mViewHolder.mNotificationTypeTv.setText(mMsg);
                    Glide.with(mContext).clear(mViewHolder.mUserImgView);
                    switch (mNotificationListData.get(position).getType()) {
                        case "LIVE_REQUEST":
                            mUserName = (mDetailsObj.getString("ReceiverName"));
                            break;
                        case "LIVE_ACCEPT":
                            mUserName = (mDetailsObj.getString("SenderName"));
                            break;
                        case "FOLLOW":
                            StringTokenizer st = new StringTokenizer(mMsg, " ");
                            if (mDetailsObj.has(AppConstants.NOTIFICATION_SENDER_NAME)) {
                                mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            } else if (st.hasMoreTokens()) {
                                mUserName = st.nextToken();
                            }
                            break;
                        case "FOLLOWER_POST":
                            if (mDetailsObj.has(AppConstants.NOTIFICATION_SENDER_NAME)) {
                                mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            } else if (mDetailsObj.getString(AppConstants.USER_TYPE).trim().equals(AppConstants.USER)) {
                                mProfileResModel = new Gson().fromJson(mDetailsObj.getJSONObject(PostsModel.PROFILES_BY_WHO_POSTED_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = (mProfileResModel.getDriver() == null || mProfileResModel.getDriver().equals("") ? mProfileResModel.getSpectatorName() : mProfileResModel.getDriver());
                            } else {
                                mUserName = mMsg.substring(0, (mMsg.length() - 15));
                            }
                            break;
                        case "TAGGED":
                            if (mDetailsObj.has(AppConstants.NOTIFICATION_SENDER_NAME)) {
                                mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            } else {
                                mProfileResModel = new Gson().fromJson(mDetailsObj.getJSONObject(PostsModel.PROFILES_BY_WHO_POSTED_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = (mProfileResModel.getDriver() == null || mProfileResModel.getDriver().equals("") ? mProfileResModel.getSpectatorName() : mProfileResModel.getDriver());
                            }
                            break;
                        case "VIDEO_COMMENT_REPLY_LIKE":
                            if (mDetailsObj.has(AppConstants.NOTIFICATION_SENDER_NAME)) {
                                mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            } else if (mDetailsObj.has(APIConstants.PROFILES_BY_PROFILE_ID)) {
                                mProfileResModel = new Gson().fromJson(mDetailsObj.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            } else if (mDetailsObj.has("postshares_by_NewSharedPostID")) {
                                JSONObject mFeedSharesModel = mDetailsObj.getJSONObject("postshares_by_NewSharedPostID");
                                mProfileResModel = new Gson().fromJson(mFeedSharesModel.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            }
                            break;
                        case "VIDEO_COMMENT_LIKE":
                            if (mDetailsObj.has(AppConstants.NOTIFICATION_SENDER_NAME)) {
                                mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            } else if (mDetailsObj.has(APIConstants.PROFILES_BY_PROFILE_ID)) {
                                mProfileResModel = new Gson().fromJson(mDetailsObj.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            } else if (mDetailsObj.has("postshares_by_NewSharedPostID")) {
                                JSONObject mFeedSharesModel = mDetailsObj.getJSONObject("postshares_by_NewSharedPostID");
                                mProfileResModel = new Gson().fromJson(mFeedSharesModel.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            }
                            break;
                        case "TAGGED_POST_COMMENTS":
                            mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                            ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            break;
                        case "TAGGED_POST_VIDEO_COMMENTS":
                            mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                            ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            break;
                        case "TAGGED_VIDEO_COMMENT_REPLY":
                            mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                            ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            break;
                        case "VIDEO_COMMENTS":
                            if (mDetailsObj.has(AppConstants.NOTIFICATION_SENDER_NAME)) {
                                mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            } else if (mDetailsObj.has(APIConstants.PROFILES_BY_PROFILE_ID)) {
                                mProfileResModel = new Gson().fromJson(mDetailsObj.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            } else if (mDetailsObj.has("postshares_by_NewSharedPostID")) {
                                JSONObject mFeedSharesModel = mDetailsObj.getJSONObject("postshares_by_NewSharedPostID");
                                mProfileResModel = new Gson().fromJson(mFeedSharesModel.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            }
                            break;
                        case "VIDEO_LIKES":
                            if (mDetailsObj.has(AppConstants.NOTIFICATION_SENDER_NAME)) {
                                mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            } else if (mDetailsObj.has(APIConstants.PROFILES_BY_PROFILE_ID)) {
                                mProfileResModel = new Gson().fromJson(mDetailsObj.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            } else if (mDetailsObj.has("postshares_by_NewSharedPostID")) {
                                JSONObject mFeedSharesModel = mDetailsObj.getJSONObject("postshares_by_NewSharedPostID");
                                mProfileResModel = new Gson().fromJson(mFeedSharesModel.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            }
                            break;
                        case "TAGGED_COMMENT_REPLY":
                            mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                            ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            break;
                        case "VIDEO_COMMENT_REPLY":
                            if (mDetailsObj.has(AppConstants.NOTIFICATION_SENDER_NAME)) {
                                mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            } else if (mDetailsObj.has(APIConstants.PROFILES_BY_PROFILE_ID)) {
                                mProfileResModel = new Gson().fromJson(mDetailsObj.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            } else if (mDetailsObj.has("postshares_by_NewSharedPostID")) {
                                JSONObject mFeedSharesModel = mDetailsObj.getJSONObject("postshares_by_NewSharedPostID");
                                mProfileResModel = new Gson().fromJson(mFeedSharesModel.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            }
                            break;
                        case "COMMENT_REPLY":
                            if (mDetailsObj.has(AppConstants.NOTIFICATION_SENDER_NAME)) {
                                mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            } else if (mDetailsObj.has(APIConstants.PROFILES_BY_PROFILE_ID)) {
                                mProfileResModel = new Gson().fromJson(mDetailsObj.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            } else if (mDetailsObj.has("postshares_by_NewSharedPostID")) {
                                JSONObject mFeedSharesModel = mDetailsObj.getJSONObject("postshares_by_NewSharedPostID");
                                mProfileResModel = new Gson().fromJson(mFeedSharesModel.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            }
                            break;
                        case "POST_COMMENTS":
                            if (mDetailsObj.has(AppConstants.NOTIFICATION_SENDER_NAME)) {
                                mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            } else if (mDetailsObj.has(APIConstants.PROFILES_BY_PROFILE_ID)) {
                                mProfileResModel = new Gson().fromJson(mDetailsObj.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            } else if (mDetailsObj.has("postshares_by_NewSharedPostID")) {
                                JSONObject mFeedSharesModel = mDetailsObj.getJSONObject("postshares_by_NewSharedPostID");
                                mProfileResModel = new Gson().fromJson(mFeedSharesModel.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            }
                            break;
                        case "POST_LIKES":
                            if (mDetailsObj.has(AppConstants.NOTIFICATION_SENDER_NAME)) {
                                mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            } else if (mDetailsObj.has(APIConstants.PROFILES_BY_PROFILE_ID)) {
                                mProfileResModel = new Gson().fromJson(mDetailsObj.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            } else if (mDetailsObj.has("postshares_by_NewSharedPostID")) {
                                JSONObject mFeedSharesModel = mDetailsObj.getJSONObject("postshares_by_NewSharedPostID");
                                mProfileResModel = new Gson().fromJson(mFeedSharesModel.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            }
                            break;
                        case "COMMENT_LIKE":
                            if (mDetailsObj.has(AppConstants.NOTIFICATION_SENDER_NAME)) {
                                mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            } else if (mDetailsObj.has(APIConstants.PROFILES_BY_PROFILE_ID)) {
                                mProfileResModel = new Gson().fromJson(mDetailsObj.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            } else if (mDetailsObj.has("postshares_by_NewSharedPostID")) {
                                JSONObject mFeedSharesModel = mDetailsObj.getJSONObject("postshares_by_NewSharedPostID");
                                mProfileResModel = new Gson().fromJson(mFeedSharesModel.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            }
                            break;
                        case "COMMENT_REPLY_LIKE":
                            if (mDetailsObj.has(AppConstants.NOTIFICATION_SENDER_NAME)) {
                                mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            } else if (mDetailsObj.has(APIConstants.PROFILES_BY_PROFILE_ID)) {
                                mProfileResModel = new Gson().fromJson(mDetailsObj.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            } else if (mDetailsObj.has("postshares_by_NewSharedPostID")) {
                                JSONObject mFeedSharesModel = mDetailsObj.getJSONObject("postshares_by_NewSharedPostID");
                                mProfileResModel = new Gson().fromJson(mFeedSharesModel.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            }
                            break;
                        case "VIDEO_SHARE":
                            if (mDetailsObj.has(AppConstants.NOTIFICATION_SENDER_NAME)) {
                                mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            } else if (mDetailsObj.has(APIConstants.PROFILES_BY_PROFILE_ID)) {
                                mProfileResModel = new Gson().fromJson(mDetailsObj.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            } else if (mDetailsObj.has("postshares_by_NewSharedPostID")) {
                                JSONObject mFeedSharesModel = mDetailsObj.getJSONObject("postshares_by_NewSharedPostID");
                                mProfileResModel = new Gson().fromJson(mFeedSharesModel.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            }
                            break;
                        case "POST_SHARE":
                            if (mDetailsObj.has(AppConstants.NOTIFICATION_SENDER_NAME)) {
                                mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            } else if (mDetailsObj.has(APIConstants.PROFILES_BY_PROFILE_ID)) {
                                mProfileResModel = new Gson().fromJson(mDetailsObj.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            } else if (mDetailsObj.has("postshares_by_NewSharedPostID")) {
                                JSONObject mFeedSharesModel = mDetailsObj.getJSONObject("postshares_by_NewSharedPostID");
                                mProfileResModel = new Gson().fromJson(mFeedSharesModel.getJSONObject(APIConstants.PROFILES_BY_PROFILE_ID).toString(), ProfileResModel.class);
                                mUserName = Utility.getInstance().getUserName(mProfileResModel);
                            }
                            break;
                        case "VEHICLE_LIKE":
                            StringTokenizer st_vehicle = new StringTokenizer(mMsg, " ");
                            if (mDetailsObj.has(AppConstants.NOTIFICATION_SENDER_NAME)) {
                                mUserName = (mDetailsObj.getString(AppConstants.NOTIFICATION_SENDER_NAME));
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mDetailsObj.getString(AppConstants.NOTIFICATION_PROFILE_PICTURE), R.drawable.default_profile_icon);
                            } else if (st_vehicle.hasMoreTokens()) {
                                mUserName = st_vehicle.nextToken();
                            }
                            break;
                        case "EVENT_LIVE_CHAT":
                            mProfileResModel.setProfilePicture(mDetailsObj.getString(MyFireBaseMessagingService.PROFILE_PICTURE));
                            mUserName = mDetailsObj.getString(MyFireBaseMessagingService.EVENT_GRP_CHAT_SENDER_NAME);
                            if (!mProfileResModel.getProfilePicture().isEmpty()) {
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);
                            }
                            break;
                        case "EVENT_CREATION":
                            String[] st_event = mMsg.split("-");
                            if (st_event.length > 1)
                                mUserName = st_event[1];
                            break;
                        case "EVENT_CHAT":
                            mProfileResModel.setProfilePicture(mDetailsObj.getString(MyFireBaseMessagingService.PROFILE_PICTURE));
                            mUserName = "Event: " + mDetailsObj.getString(MyFireBaseMessagingService.EVENT_GRP_CHAT_SENDER_NAME);
                            if (!mProfileResModel.getProfilePicture().isEmpty()) {
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);
                            }
                            break;
                        case "GROUP_CHAT_MSG":
                            JSONObject GrpChatObj = mDetailsObj.getJSONObject(MyFireBaseMessagingService.GRP_CHAT);
                            mProfileResModel.setProfilePicture(GrpChatObj.getString(MyFireBaseMessagingService.GROUP_SENDER_PIC));
                            mUserName = "Group: " + GrpChatObj.getString(MyFireBaseMessagingService.GRP_NAME);
                            if (!mProfileResModel.getProfilePicture().isEmpty()) {
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);
                            }
                            break;
                        case "SINGLE_CHAT":
                            mProfileResModel.setProfilePicture(mDetailsObj.getString(MyFireBaseMessagingService.PROFILE_PICTURE));
                            mUserName = "From: " + (mDetailsObj.getString(MyFireBaseMessagingService.SENDER_NAME));
                            if (!mProfileResModel.getProfilePicture().isEmpty()) {
                                ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);
                            }
                            break;
                    }

                    mViewHolder.mUsernameTv.setText(mUserName);
                    mViewHolder.mParentLayout.setOnClickListener(this);
                    mViewHolder.mParentLayout.setTag(position);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case VIEW_TYPE_LOADING:
                final ViewHolderLoader mViewHolderLoader = (ViewHolderLoader) holder;
                if (mNotificationListData.size() != ((TotalNotificationResultCount) mContext).getTotalNotificationResultCount()) {
                    mViewHolderLoader.mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mViewHolderLoader.mProgressBar.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int position = Integer.parseInt(v.getTag().toString());
        try {
            JSONObject mDataObj = new JSONObject(mNotificationListData.get(position).getData());
            JSONObject mDetailsObj = mDataObj.getJSONObject("Details");
            switch (mNotificationListData.get(position).getType()) {
                case "EVENT_CREATION":
                    Intent mEventIntent = new Intent(mContext, NotificationEventCreatedActivity.class);
                    mEventIntent.putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mNotificationListData.get(position).getData());
                    mEventIntent.putExtra(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY, true);
                    mContext.startActivity(mEventIntent);
                    break;
                case "LIVE_STREAM":
                    mContext.startActivity(new Intent(mContext, ViewLiveVideoViewScreen2.class)
                            .putExtra(AppConstants.PROFILE_ID, mDetailsObj.getInt(APIConstants.StreamProfileID)));
                    break;
                case "LIVE_REQUEST":
                    mContext.startActivity(new Intent(mContext, ViewRequestUsersActivity.class)
                            .putExtra(AppConstants.PROFILE_ID, mDetailsObj.getInt(APIConstants.ReceiverProfileID)));
                    break;
                case "LIVE_ACCEPT":
                    mContext.startActivity(new Intent(mContext, ViewStreamUsersActivity.class)
                            .putExtra(AppConstants.IS_FROM_NOTIFICATION_TRAY, true).
                                    putExtra(AppConstants.PROFILE_ID, mDetailsObj.getInt(APIConstants.RequestedProfileID)));
                    break;
                case "FOLLOW":
                    Intent mViewImageActivity = new Intent(mContext, OthersMotoFileActivity.class).
                            putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mNotificationListData.get(position).getData()).putExtra(AppConstants.IS_FROM_NOTIFICATION_TRAY, true);
                    ((NotificationActivity) mContext).startActivityForResult(mViewImageActivity, AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                    break;
                case "FOLLOWER_POST":
                case "TAGGED":
                case "TAGGED_POST_COMMENTS":
                case "POST_COMMENTS":
                case "POST_LIKES":
                case "VIDEO_SHARE":
                case "POST_SHARE":
                    Intent mTagIntent = new Intent(mContext, PostViewActivity.class);
                    mTagIntent.putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mNotificationListData.get(position).getData()).putExtra(AppConstants.IS_FROM_NOTIFICATION_TRAY, true);
                    mContext.startActivity(mTagIntent);
                    break;
                case "VIDEO_COMMENT_LIKE":
                    Intent mVideoCommentLikeIntent = new Intent(mContext, VideoCommentsActivity.class);
                    mVideoCommentLikeIntent.putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mNotificationListData.get(position).getData());
                    mVideoCommentLikeIntent.putExtra(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY, true);
                    mContext.startActivity(mVideoCommentLikeIntent);
                    break;
                case "VIDEO_COMMENT_REPLY_LIKE":
                case "TAGGED_VIDEO_COMMENT_REPLY":
                case "VIDEO_COMMENT_REPLY":
                    Intent mVideoCommentReplyIntent = new Intent(mContext, VideoCommentReplyActivity.class);
                    mVideoCommentReplyIntent.putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mNotificationListData.get(position).getData());
                    mVideoCommentReplyIntent.putExtra(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY, true);
                    mContext.startActivity(mVideoCommentReplyIntent);
                    break;
                case "VIDEO_LIKES":
                case "TAGGED_POST_VIDEO_COMMENTS":
                case "VIDEO_COMMENTS":
                    Intent mPromoterVideoGalleryIntent = new Intent(mContext, PromoterVideoGalleryActivity.class);
                    mPromoterVideoGalleryIntent.putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mNotificationListData.get(position).getData());
                    mPromoterVideoGalleryIntent.putExtra(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY, true);
                    mContext.startActivity(mPromoterVideoGalleryIntent);
                    break;
                case "COMMENT_REPLY_LIKE":
                case "TAGGED_COMMENT_REPLY":
                case "COMMENT_REPLY":
                    Intent mCommentReplyIntent = new Intent(mContext, CommentReplyActivity.class);
                    mCommentReplyIntent.putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mNotificationListData.get(position).getData());
                    mCommentReplyIntent.putExtra(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY, true);
                    mContext.startActivity(mCommentReplyIntent);
                    break;
                case "COMMENT_LIKE":
                    Intent mCommentLikeIntent = new Intent(mContext, PostCommentLikeViewActivity.class);
                    mCommentLikeIntent.putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mNotificationListData.get(position).getData());
                    mCommentLikeIntent.putExtra(AppConstants.IS_FROM_NOTIFICATION_TRAY, true);
                    mContext.startActivity(mCommentLikeIntent);
                    break;
                case "EVENT_LIVE_CHAT":
                    Intent mEventLiveChatIntent = new Intent(mContext, EventLiveActivity.class);
                    mEventLiveChatIntent.putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mNotificationListData.get(position).getData());
                    mEventLiveChatIntent.putExtra(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY, true);
                    mContext.startActivity(mEventLiveChatIntent);
                    break;
                case "EVENT_CHAT":
                    Intent mEventChatIntent = new Intent(mContext, ChatBoxEventGrpActivity.class);
                    mEventChatIntent.putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mNotificationListData.get(position).getData());
                    mEventChatIntent.putExtra(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY, true);
                    mContext.startActivity(mEventChatIntent);
                    break;
                case "GROUP_CHAT_MSG":
                    Intent mGrpChatIntent = new Intent(mContext, ChatBoxGroupActivity.class);
                    mGrpChatIntent.putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mNotificationListData.get(position).getData());
                    mGrpChatIntent.putExtra(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY, true);
                    mContext.startActivity(mGrpChatIntent);
                    break;
                case "SINGLE_CHAT":
                    Intent mSingleChatIntent = new Intent(mContext, ChatBoxSingleActivity.class);
                    mSingleChatIntent.putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mNotificationListData.get(position).getData());
                    mSingleChatIntent.putExtra(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY, true);
                    mContext.startActivity(mSingleChatIntent);
                    break;
                case "VEHICLE_LIKE":
                    Intent mVehicleInfoLikeIntent = new Intent(mContext, NotificationUpdateProfileActivity.class);
                    mVehicleInfoLikeIntent.putExtra(MyFireBaseMessagingService.ENTRY_JSON_OBJ, mNotificationListData.get(position).getData());
                    mVehicleInfoLikeIntent.putExtra(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY, true);
                    mContext.startActivity(mVehicleInfoLikeIntent);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface TotalNotificationResultCount {
        int getTotalNotificationResultCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mUserImgView;
        TextView mUsernameTv;
        RelativeLayout mParentLayout;
        TextView mNotificationTypeTv;

        public ViewHolder(View itemView) {
            super(itemView);
            mParentLayout = itemView.findViewById(R.id.list_item);
            mUserImgView = itemView.findViewById(R.id.circular_img_view);
            mUsernameTv = itemView.findViewById(R.id.top_tv);
            mNotificationTypeTv = itemView.findViewById(R.id.bottom_tv);
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