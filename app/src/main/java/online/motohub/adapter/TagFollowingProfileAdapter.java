package online.motohub.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.model.ProfileResModel;
import online.motohub.util.Utility;

public class TagFollowingProfileAdapter extends ArrayAdapter<ProfileResModel> {

    private Context mContext;
    private List<ProfileResModel> mFollowingListData;
    private List<ProfileResModel> mOriginalFollowingListData;

    public TagFollowingProfileAdapter(@NonNull Context context, List<ProfileResModel> followingListData) {
        super(context, R.layout.row_tag_following_item, followingListData);
        this.mContext = context;
        this.mOriginalFollowingListData = followingListData;
        this.mFollowingListData = followingListData;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder mViewHolder;

        if (convertView == null) {
            mViewHolder = new ViewHolder();
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.row_tag_following_item, parent, false);
            mViewHolder.mUserImgView = convertView.findViewById(R.id.circular_img_view);
            mViewHolder.mNameTv = convertView.findViewById(R.id.name_tv);
            mViewHolder.mCheckBox = convertView.findViewById(R.id.tag_cb);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        ProfileResModel mProfileResModel = mFollowingListData.get(position);

        ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);

        mViewHolder.mNameTv.setText(Utility.getInstance().getUserName(mProfileResModel));

        mViewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mFollowingListData.get(position).setIsProfileTagged(isChecked);
            }
        });

        if (mProfileResModel.getIsProfileTagged()) {
            mViewHolder.mCheckBox.setChecked(true);
        } else {
            mViewHolder.mCheckBox.setChecked(false);
        }

        return convertView;

    }

    @NonNull
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults mResults = new FilterResults(); // Holds the results of a filtering operation in values
                List<ProfileResModel> mFilteredArrList = new ArrayList<>();

                if (mOriginalFollowingListData == null) {
                    mOriginalFollowingListData = new ArrayList<>(mFollowingListData); // saves the original data in mOriginalValues
                }

                //If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                //else does the Filtering and returns FilteredArrList(Filtered)
                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return
                    mResults.count = mOriginalFollowingListData.size();
                    mResults.values = mOriginalFollowingListData;
                } else {
                    constraint = constraint.toString().toLowerCase().trim();
                    for (int i = 0; i < mOriginalFollowingListData.size(); i++) {

                        String mName;

                        if (mOriginalFollowingListData.get(i).getDriver() != null && !mOriginalFollowingListData.get(i).getDriver().isEmpty()) {
                            mName = mOriginalFollowingListData.get(i).getDriver().toLowerCase().trim();
                        } else {
                            mName = mOriginalFollowingListData.get(i).getSpectatorName().toLowerCase().trim();
                        }

                        String mSearchStr = constraint.toString();

                        if (constraint.length() > 0 && constraint.charAt(0) == '@') {
                            if (constraint.length() == 1) {
                                mSearchStr = "";
                            } else {
                                mSearchStr = mSearchStr.substring(1, constraint.length() - 1);
                            }
                        }

                        if (mName.startsWith(mSearchStr)) {
                            mFilteredArrList.add(mOriginalFollowingListData.get(i));
                        } else {
                            String mProfileType = ((BaseActivity) mContext).getProfileTypeStr(String.valueOf(mOriginalFollowingListData.get(i).getProfileType()));
                            if (mSearchStr.toLowerCase().startsWith(mProfileType.toLowerCase())) {
                                mFilteredArrList.add(mOriginalFollowingListData.get(i));
                            }
                        }

                    }
                    // set the Filtered result to return
                    mResults.count = mFilteredArrList.size();
                    mResults.values = mFilteredArrList;
                }

                return mResults;

            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {

                mFollowingListData = (List<ProfileResModel>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values

            }
        };
    }

    @Override
    public int getCount() {
        return mFollowingListData.size();
    }

    @Nullable
    @Override
    public ProfileResModel getItem(int position) {
        return mFollowingListData.get(position);
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    private static class ViewHolder {
        CircleImageView mUserImgView;
        TextView mNameTv;
        CheckBox mCheckBox;
    }

}
