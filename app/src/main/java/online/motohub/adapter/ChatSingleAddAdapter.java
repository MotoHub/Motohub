package online.motohub.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.model.ProfileResModel;
import online.motohub.util.Utility;

public class ChatSingleAddAdapter extends ArrayAdapter<ProfileResModel> {

    private Context mContext;
    private List<ProfileResModel> mFollowingListData;

    public ChatSingleAddAdapter(@NonNull Context context, List<ProfileResModel> followingListData) {
        super(context, R.layout.row_list_view_item, followingListData);
        this.mContext = context;
        this.mFollowingListData = followingListData;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder mViewHolder;

        if (convertView == null) {
            mViewHolder = new ViewHolder();
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.row_list_view_item, parent, false);
            mViewHolder.mUserImgView = convertView.findViewById(R.id.circular_img_view);
            mViewHolder.mUsernameTv = convertView.findViewById(R.id.top_tv);
            mViewHolder.mRightArrow = convertView.findViewById(R.id.right_arrow);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        ProfileResModel mProfileResModel = mFollowingListData.get(position);

        ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);

        mViewHolder.mUsernameTv.setText(Utility.getInstance().getUserName(mProfileResModel));

        if (mProfileResModel.getIsSelected()) {
            convertView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGrey));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        }

        mViewHolder.mRightArrow.setVisibility(View.GONE);
        return convertView;

    }

    private static class ViewHolder {
        CircleImageView mUserImgView;
        TextView mUsernameTv;
        ImageView mRightArrow;
    }

}
