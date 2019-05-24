package online.motohub.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.EventsResultActivity;
import online.motohub.model.EventsResModel;

public class EventsResultAdapter extends ArrayAdapter<EventsResModel> implements View.OnClickListener {

    private Context mContext;
    private List<EventsResModel> mOriginalEventsResultListData;
    private List<EventsResModel> mEventsResultListData;

    public EventsResultAdapter(@NonNull Context context, List<EventsResModel> eventsResultListData) {
        super(context, R.layout.row_events_result_item, eventsResultListData);
        this.mContext = context;
        this.mOriginalEventsResultListData = eventsResultListData;
        this.mEventsResultListData = eventsResultListData;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder mViewHolder;

        if (convertView == null) {
            mViewHolder = new ViewHolder();
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.row_events_result_item, parent, false);
            mViewHolder.mEndedOnTv = convertView.findViewById(R.id.ended_on_tv);
            mViewHolder.mEventNameTv = convertView.findViewById(R.id.moto_event_name_tv);
            mViewHolder.mViewResultsBtn = convertView.findViewById(R.id.view_event_result_btn);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.mEndedOnTv.setText(((BaseActivity) mContext).getFinishedEventDateStr(mEventsResultListData.get(position).getFinish()));

        String mEventName = mEventsResultListData.get(position).getName() + " - " + ((BaseActivity) mContext).getFormattedDate(mEventsResultListData.get(position).getDate())
                + " - " + mEventsResultListData.get(position).getMotorVehicle();

        mViewHolder.mEventNameTv.setText(mEventName);

        mViewHolder.mViewResultsBtn.setOnClickListener(this);

        return convertView;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_event_result_btn:
                mContext.startActivity(new Intent(mContext, EventsResultActivity.class));
                break;
        }
    }

    @NonNull
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults mResults = new FilterResults(); // Holds the results of a filtering operation in values
                List<EventsResModel> mFilteredArrList = new ArrayList<>();

                if (mOriginalEventsResultListData == null) {
                    mOriginalEventsResultListData = new ArrayList<>(mEventsResultListData); // saves the original data in mOriginalValues
                }

                //If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                //else does the Filtering and returns FilteredArrList(Filtered)
                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return
                    mResults.count = mOriginalEventsResultListData.size();
                    mResults.values = mOriginalEventsResultListData;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalEventsResultListData.size(); i++) {

                        if (constraint.toString().toLowerCase().equals("all")) {
                            mFilteredArrList.add(mOriginalEventsResultListData.get(i));
                        } else {
                            String mEventType = mOriginalEventsResultListData.get(i).getEventType().toLowerCase();
                            if (constraint.toString().toLowerCase().startsWith(mEventType)) {
                                mFilteredArrList.add(mOriginalEventsResultListData.get(i));
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

                mEventsResultListData = (List<EventsResModel>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values

                if (mEventsResultListData.size() == 0) {
                    ((BaseActivity) mContext).showToast(mContext, mContext.getResources().getString(R.string.no_events_err));
                }

            }
        };
    }

    @Override
    public int getCount() {
        return mEventsResultListData.size();
    }

    @Nullable
    @Override
    public EventsResModel getItem(int position) {
        return mEventsResultListData.get(position);
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    private static class ViewHolder {
        TextView mEndedOnTv;
        TextView mEventNameTv;
        Button mViewResultsBtn;
    }

}
