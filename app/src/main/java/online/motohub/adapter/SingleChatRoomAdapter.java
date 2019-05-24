package online.motohub.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.ChatBoxSingleActivity;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SingleChatMsg;
import online.motohub.model.SingleChatRoomModel;
import online.motohub.model.SingleChatRoomResModel;
import online.motohub.model.SingleChatUnreadCountModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.Utility;

public class SingleChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {

    private Context mContext;
    private List<SingleChatRoomResModel> mSingleChatRoomList;
    private ProfileResModel mMyProfileResModel;
    private List<SingleChatUnreadCountModel> mSingleChatMsgCount;
    private List<SingleChatMsg> mSingleChatLastMsg;
    private StringBuffer sb = new StringBuffer();

    public SingleChatRoomAdapter(List<SingleChatRoomResModel> singleChatRoomList, Context ctx, ProfileResModel myProfileResModel,
                                 List<SingleChatUnreadCountModel> singlechatmsgcount, List<SingleChatMsg> singlechatunreadmsg) {
        this.mSingleChatRoomList = singleChatRoomList;
        this.mMyProfileResModel = myProfileResModel;
        this.mContext = ctx;
        this.mSingleChatMsgCount = singlechatmsgcount;
        this.mSingleChatLastMsg = singlechatunreadmsg;
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

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }


    public static String getTimeAgo(Date date, Context ctx) {
        if (date == null) {
            return null;
        }
        long time = date.getTime();
        Date curDate = currentDate();
        long now = curDate.getTime();
        if (time > now || time <= 0) {
            return null;
        }
        int dim = getTimeDistanceInMinutes(time);
        String timeAgo = null;
        if (dim == 0) {
            timeAgo = ctx.getResources().getString(R.string.date_util_term_less) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim == 1) {
            return "1 " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim >= 2 && dim <= 44) {
            timeAgo = dim + " " + ctx.getResources().getString(R.string.date_util_unit_minutes);
        } else if (dim >= 45 && dim <= 89) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_an) + " " + ctx.getResources().getString(R.string.date_util_unit_hour);
        } else if (dim >= 90 && dim <= 1439) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 60)) + " " + ctx.getResources().getString(R.string.date_util_unit_hours);
        } else if (dim >= 1440 && dim <= 2519) {
            timeAgo = "1 " + ctx.getResources().getString(R.string.date_util_unit_day);
        } else if (dim >= 2520 && dim <= 43199) {
            timeAgo = (Math.round(dim / 1440)) + " " + ctx.getResources().getString(R.string.date_util_unit_days);
        } else if (dim >= 43200 && dim <= 86399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_month);
        } else if (dim >= 86400 && dim <= 525599) {
            timeAgo = (Math.round(dim / 43200)) + " " + ctx.getResources().getString(R.string.date_util_unit_months);
        } else if (dim >= 525600 && dim <= 655199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 655200 && dim <= 914399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_over) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 914400 && dim <= 1051199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_almost) + " 2 " + ctx.getResources().getString(R.string.date_util_unit_years);
        } else {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 525600)) + " " + ctx.getResources().getString(R.string.date_util_unit_years);
        }
        return timeAgo + " " + ctx.getResources().getString(R.string.date_util_suffix);
    }

    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chatuserlist, parent, false);
        return new ViewHolderSingleChatRoom(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            final ViewHolderSingleChatRoom mViewHolderSingleChatRoom = (ViewHolderSingleChatRoom) holder;
            ProfileResModel mProfileResModel = mSingleChatRoomList.get(position).getProfilesByToProfileID();
            String mUsernameStr = Utility.getInstance().getUserName(mProfileResModel);
            ((BaseActivity) mContext).setImageWithGlide(mViewHolderSingleChatRoom.img_profile, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);
            mViewHolderSingleChatRoom.name.setText(mUsernameStr);
            for (int i = 0; i < mSingleChatMsgCount.size(); i++) {
                SingleChatUnreadCountModel mSingleChatUser = mSingleChatMsgCount.get(i);
                if (mSingleChatUser.getmFromProfileID() == mProfileResModel.getID()) {
                    String mSg_count = String.valueOf(mSingleChatUser.getmCount());
                    mViewHolderSingleChatRoom.count.setBackgroundResource(R.drawable.bg_circle);
                    mViewHolderSingleChatRoom.count.setText(mSg_count);
                    break;
                }
            }
            for (int j = 0; j < mSingleChatLastMsg.size(); j++) {
                SingleChatMsg mSingleChatMsg = mSingleChatLastMsg.get(j);
                if (mSingleChatMsg.getMsinglechatmsg_by_Order().getmFromProfileID() == mProfileResModel.getID()
                        || mSingleChatMsg.getMsinglechatmsg_by_Order().getmToProfileID() == mProfileResModel.getID()) {
                    String last_msg = mSingleChatMsg.getMsinglechatmsg_by_Order().getmMessage();
                    String enc_last_msg = URLDecoder.decode(last_msg, "UTF-8");
                    //String enc_last_msg = replacer(sb.append(last_msg));
                    String createdAt = mSingleChatMsg.getMsinglechatmsg_by_Order().getmCreatedAt();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date dateformat = simpleDateFormat.parse(createdAt);
                    String timeunits = getTimeAgo(dateformat, mContext);

                    String[] mcreated = createdAt.split(" ");
                    int len = mcreated.length;
                    String msg_time = mcreated[len - 1];
                    mViewHolderSingleChatRoom.message.setText(enc_last_msg);
                    mViewHolderSingleChatRoom.timestamp.setText(timeunits);
                    break;
                }
            }
            mViewHolderSingleChatRoom.mParentLay.setOnClickListener(this);
            mViewHolderSingleChatRoom.mParentLay.setOnLongClickListener(this);
            mViewHolderSingleChatRoom.mParentLay.setTag(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mSingleChatRoomList.size();
    }

    @Override
    public void onClick(View v) {
        Bundle mBundle = new Bundle();
        int pos = Integer.parseInt(v.getTag().toString());

        for (int i = 0; i < mSingleChatMsgCount.size(); i++) {
            SingleChatUnreadCountModel mSingleChatUser = mSingleChatMsgCount.get(i);
            if (mSingleChatUser.getmFromProfileID() == mSingleChatRoomList.get(pos).getToProfileID()) {
                mSingleChatMsgCount.remove(i);
            }
        }
        //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
        EventBus.getDefault().postSticky(mMyProfileResModel);
        mBundle.putSerializable(SingleChatRoomModel.SINGLE_CHAT_ROOM_RES_MODEL, mSingleChatRoomList.get(pos));
        mBundle.putBoolean(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY, false);
        //mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
//        mContext.startActivity(new Intent(mContext, ChatBoxSingleActivity.class).putExtras(mBundle));
        ((BaseActivity) mContext).startActivityForResult(new Intent(mContext, ChatBoxSingleActivity.class)
                .putExtras(mBundle), RetrofitClient.GET_SINGLE_CHAT_COUNT);
    }

    @Override
    public boolean onLongClick(View v) {
        int pos = Integer.parseInt(v.getTag().toString());
        SingleChatRoomResModel singleChatRoomResModel = mSingleChatRoomList.get(pos);
        ((BaseActivity) mContext).showBottomChatEdit(AppDialogFragment.BOTTOM_CHAT_EDIT, singleChatRoomResModel);
        return true;
    }

    private class ViewHolderSingleChatRoom extends RecyclerView.ViewHolder {
        /*private CircleImageView mUserImgView;
        private TextView mUsernameTv;
        private ImageView mRightArrow;
        private RelativeLayout mParentLay;*/
        /* TODO new added for change the design */
        private CircleImageView img_profile;
        private TextView name, message, timestamp, count;
        private LinearLayout mParentLay;
        /*private ImageView ivCommentImg;
        private ProgressBar smallProgressBar;*/

        ViewHolderSingleChatRoom(View view) {
            super(view);
            /*mUserImgView = view.findViewById(R.id.circular_img_view);
            mUsernameTv = view.findViewById(R.id.top_tv);
            mRightArrow = view.findViewById(R.id.right_arrow);
            mParentLay = view.findViewById(R.id.list_item);*/
            img_profile = view.findViewById(R.id.img_profile);
            name = view.findViewById(R.id.name);
            message = view.findViewById(R.id.message);
            timestamp = view.findViewById(R.id.timestamp);
            count = view.findViewById(R.id.count);
            mParentLay = view.findViewById(R.id.list_item);
            /*ivCommentImg = view.findViewById(R.id.ivCommentImg);
            smallProgressBar = view.findViewById(R.id.smallProgressBar);*/
        }
    }

}