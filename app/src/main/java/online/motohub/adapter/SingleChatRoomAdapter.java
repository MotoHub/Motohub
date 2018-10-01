package online.motohub.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.ChatBoxSingleActivity;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SingleChatRoomModel;
import online.motohub.model.SingleChatRoomResModel;
import online.motohub.util.Utility;

public class SingleChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private Context mContext;
    private List<SingleChatRoomResModel> mSingleChatRoomList;
    private ProfileResModel mMyProfileResModel;

    public SingleChatRoomAdapter(List<SingleChatRoomResModel> singleChatRoomList, Context ctx, ProfileResModel myProfileResModel) {
        this.mSingleChatRoomList = singleChatRoomList;
        this.mMyProfileResModel = myProfileResModel;
        this.mContext = ctx;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_view_item, parent, false);
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chatuserlist, parent, false);
        return new ViewHolderSingleChatRoom(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        /*final ViewHolderSingleChatRoom mViewHolderSingleChatRoom = (ViewHolderSingleChatRoom) holder;

        ProfileResModel mProfileResModel = mSingleChatRoomList.get(position).getProfilesByToProfileID();

        String mUsernameStr= Utility.getInstance().getUserName(mProfileResModel);

        ((BaseActivity) mContext).setImageWithGlide(mViewHolderSingleChatRoom.mUserImgView, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);

        mViewHolderSingleChatRoom.mUsernameTv.setText(mUsernameStr);

        mViewHolderSingleChatRoom.mParentLay.setOnClickListener(this);
        mViewHolderSingleChatRoom.mParentLay.setOnLongClickListener(this);
        mViewHolderSingleChatRoom.mParentLay.setTag(position);*/

        final ViewHolderSingleChatRoom mViewHolderSingleChatRoom = (ViewHolderSingleChatRoom) holder;
        ProfileResModel mProfileResModel = mSingleChatRoomList.get(position).getProfilesByToProfileID();
        String mUsernameStr= Utility.getInstance().getUserName(mProfileResModel);
        ((BaseActivity) mContext).setImageWithGlide(mViewHolderSingleChatRoom.img_profile, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);
        mViewHolderSingleChatRoom.name.setText(mUsernameStr);

        mViewHolderSingleChatRoom.mParentLay.setOnClickListener(this);
        mViewHolderSingleChatRoom.mParentLay.setOnLongClickListener(this);
        mViewHolderSingleChatRoom.mParentLay.setTag(position);


    }

    @Override
    public int getItemCount() {
        return mSingleChatRoomList.size();
    }

    @Override
    public void onClick(View v) {
        Bundle mBundle = new Bundle();
        int pos = Integer.parseInt(v.getTag().toString());
        mBundle.putSerializable(SingleChatRoomModel.SINGLE_CHAT_ROOM_RES_MODEL, mSingleChatRoomList.get(pos));
        mBundle.putBoolean(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY, false);
        mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
        mContext.startActivity(new Intent(mContext, ChatBoxSingleActivity.class).putExtras(mBundle));
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

        //TODO new added for change the design
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
