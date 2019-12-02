package online.motohub.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.model.ProfileResModel;
import online.motohub.util.Utility;

public class TaggedProfilesListAdapter extends ArrayAdapter<ProfileResModel> {

    private Context mContext;
    private List<ProfileResModel> mTaggedProfilesListData;

    public TaggedProfilesListAdapter(@NonNull Context context, List<ProfileResModel> taggedProfilesListData) {
        super(context, R.layout.row_list_view_item, taggedProfilesListData);
        this.mContext = context;
        this.mTaggedProfilesListData = taggedProfilesListData;
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
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        ProfileResModel mProfileResModel = mTaggedProfilesListData.get(position);

        String mUsernameStr = Utility.getInstance().getUserName(mProfileResModel);

        ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);

        mViewHolder.mUsernameTv.setText(mUsernameStr);

        return convertView;

    }

    private static class ViewHolder {
        CircleImageView mUserImgView;
        TextView mUsernameTv;
    }

}
