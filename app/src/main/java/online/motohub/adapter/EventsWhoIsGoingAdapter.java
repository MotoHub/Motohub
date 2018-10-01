package online.motohub.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.model.EventsWhoIsGoingResModel;
import online.motohub.model.ProfileResModel;
import online.motohub.util.Utility;

public class EventsWhoIsGoingAdapter extends ArrayAdapter<EventsWhoIsGoingResModel> {

    private Context mContext;
    private List<EventsWhoIsGoingResModel> mWhoIsGoingListData;
    private List<EventsWhoIsGoingResModel> mOriginalWhoIsGoingList;

    public EventsWhoIsGoingAdapter(@NonNull Context context, List<EventsWhoIsGoingResModel> whoIsGoingListData) {
        super(context, R.layout.row_list_view_item, whoIsGoingListData);
        this.mContext = context;
        this.mWhoIsGoingListData = whoIsGoingListData;
        this.mOriginalWhoIsGoingList = whoIsGoingListData;
    }

    private static class ViewHolder {
        CircleImageView mUserImgView;
        TextView mUsernameTv;
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


        ProfileResModel mProfileResModel = mWhoIsGoingListData.get(position).getProfileByProfileID();

        String mUsernameStr;

        mUsernameStr = Utility.getInstance().getUserName(mProfileResModel);

        ((BaseActivity) mContext).setImageWithGlide(mViewHolder.mUserImgView, mProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);

        mViewHolder.mUsernameTv.setText(mUsernameStr);


        return convertView;

    }

    @NonNull
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults mResults = new FilterResults(); // Holds the results of a filtering operation in values
                List<EventsWhoIsGoingResModel> mFilteredArrList = new ArrayList<>();

                if (mOriginalWhoIsGoingList == null) {
                    mOriginalWhoIsGoingList = new ArrayList<>(mOriginalWhoIsGoingList); // saves the original data in mOriginalValues
                }

                //If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                //else does the Filtering and returns FilteredArrList(Filtered)
                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return
                    mResults.count = mOriginalWhoIsGoingList.size();
                    mResults.values = mOriginalWhoIsGoingList;
                } else {
                    constraint = constraint.toString().toLowerCase().trim();
                    for (int i = 0; i < mOriginalWhoIsGoingList.size(); i++) {

                        String mName;

                        if (mOriginalWhoIsGoingList.get(i).getProfileByProfileID().getDriver() != null && !mOriginalWhoIsGoingList.get(i).getProfileByProfileID().getDriver().isEmpty()) {
                            mName = mOriginalWhoIsGoingList.get(i).getProfileByProfileID().getDriver().toLowerCase().trim();
                        } else {
                            mName = mOriginalWhoIsGoingList.get(i).getProfileByProfileID().getSpectatorName().toLowerCase().trim();
                        }

                        if (mName.startsWith(constraint.toString())) {
                            mFilteredArrList.add(mOriginalWhoIsGoingList.get(i));
                        } else {
                            String mProfileType = ((BaseActivity) mContext).getProfileTypeStr(String.valueOf(mOriginalWhoIsGoingList.get(i).getProfileByProfileID().getProfileType()));
                            if (constraint.toString().toLowerCase().startsWith(mProfileType.toLowerCase())) {
                                mFilteredArrList.add(mOriginalWhoIsGoingList.get(i));
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

                mWhoIsGoingListData = (List<EventsWhoIsGoingResModel>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values

            }
        };
    }

    @Override
    public int getCount() {
        return mWhoIsGoingListData.size();
    }

    @Nullable
    @Override
    public EventsWhoIsGoingResModel getItem(int position) {
        return mWhoIsGoingListData.get(position);
    }

}
