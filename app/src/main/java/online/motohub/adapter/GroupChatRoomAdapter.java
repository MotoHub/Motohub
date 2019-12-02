package online.motohub.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.ChatBoxGroupActivity;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.model.GroupChatRoomModel;
import online.motohub.model.GroupChatRoomResModel;
import online.motohub.model.ProfileResModel;

public class GroupChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<GroupChatRoomResModel> mGroupChatRoomList;
    private ProfileResModel mMyProfileResModel;

    public GroupChatRoomAdapter(List<GroupChatRoomResModel> groupChatRoomList, Context ctx, ProfileResModel myProfileResModel) {
        this.mGroupChatRoomList = groupChatRoomList;
        this.mMyProfileResModel = myProfileResModel;
        this.mContext = ctx;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_view_item, parent, false);
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chatuserlist, parent, false);
        return new ViewHolderGroupChatRoom(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ViewHolderGroupChatRoom mViewHolderGroupChatRoom = (ViewHolderGroupChatRoom) holder;
        GroupChatRoomResModel mGroupChatRoomResModel = mGroupChatRoomList.get(position);
        /*((BaseActivity) mContext).setImageWithGlide(mViewHolderGroupChatRoom.mGroupImgView, mGroupChatRoomResModel.getGroupPicture(), R.drawable.default_group_icon);
        mViewHolderGroupChatRoom.mGroupNameTv.setText(mGroupChatRoomResModel.getGroupName());*/
        ((BaseActivity) mContext).setImageWithGlide(mViewHolderGroupChatRoom.img_profile, mGroupChatRoomResModel.getGroupPicture(), R.drawable.default_group_icon);
        mViewHolderGroupChatRoom.name.setText(mGroupChatRoomResModel.getGroupName());

    }

    @Override
    public int getItemCount() {
        return mGroupChatRoomList.size();
    }

    private class ViewHolderGroupChatRoom extends RecyclerView.ViewHolder implements View.OnClickListener {

        /*private CircleImageView mGroupImgView;
        private TextView mGroupNameTv;*/
        private CircleImageView img_profile;
        private TextView name, message, timestamp, count;
        private LinearLayout mParentLay;

        ViewHolderGroupChatRoom(View view) {
            super(view);
            /*mGroupImgView = view.findViewById(R.id.circular_img_view);
            mGroupNameTv = view.findViewById(R.id.top_tv);
            view.setOnClickListener(this);*/
            img_profile = view.findViewById(R.id.img_profile);
            name = view.findViewById(R.id.name);
            message = view.findViewById(R.id.message);
            timestamp = view.findViewById(R.id.timestamp);
            count = view.findViewById(R.id.count);
            mParentLay = view.findViewById(R.id.list_item);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            /*Bundle mBundle = new Bundle();
            mBundle.putSerializable(GroupChatRoomModel.GRP_CHAT_ROOM_RES_MODEL, mGroupChatRoomList.get(getLayoutPosition()));
            mBundle.putBoolean(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY, false);
            mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);

            mContext.startActivity(new Intent(mContext, ChatBoxGroupActivity.class)
                    .putExtras(mBundle));*/

            //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
            EventBus.getDefault().postSticky(mMyProfileResModel);
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(GroupChatRoomModel.GRP_CHAT_ROOM_RES_MODEL, mGroupChatRoomList.get(getLayoutPosition()));
            mBundle.putBoolean(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY, false);
            mContext.startActivity(new Intent(mContext, ChatBoxGroupActivity.class)
                    .putExtras(mBundle));

        }

    }

}
