package online.motohub.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import online.motohub.R;
import online.motohub.model.EventCategoryModel;
import online.motohub.util.DateUtil;

public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.Holder> {

    private static final String TAG = TimeTableAdapter.class.getName();
    private Context mContext;
    private List<Date> mIndicators;
    private Map<Date, List<EventCategoryModel>> mModels;

    public TimeTableAdapter(Context mContext, List<Date> mIndicators, Map<Date, List<EventCategoryModel>> mModels) {

        this.mContext = mContext;
        this.mIndicators = mIndicators;
        this.mModels = mModels;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = mInflater.inflate(R.layout.row_event_time_table, parent, false);
        return new Holder(v);

    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        Date sessionIndexDate = mIndicators.get(position);

        try {

            String indicator = DateUtil.getDate(sessionIndexDate, DateUtil.FORMAT_H_AP);
            holder.mSessionIndicatorTv.setText(indicator);

            List<EventCategoryModel> models = mModels.get(sessionIndexDate);
            if (models != null && models.size() > 0) {

                bindView(holder.mContainer, models);

            } else {

                holder.mContainer.removeAllViews();

            }

        } catch (Exception e) {

            Log.e(TAG, "onBindViewHolder: " + e.getMessage());
        }

    }

    private void bindView(LinearLayout container, List<EventCategoryModel> models) {

        container.removeAllViews();
        for (EventCategoryModel model : models) {

            LayoutInflater mInflater = LayoutInflater.from(mContext);

            View v = mInflater.inflate(R.layout.row_event_session_layout, null);

            TextView mSessionNameTv = v.findViewById(R.id.session_name_text_view);
            TextView mSessionStartTimeTv = v.findViewById(R.id.session_start_time_text_view);
            TextView mSessionEndTimeTv = v.findViewById(R.id.session_end_time_text_view);

            mSessionNameTv.setText(model.getSection());

            try {


                Date mStartDate = DateUtil.getDateTime(model.getStartTime(), DateUtil.FORMAT_DMY_HMS);
                Date mEndDate = DateUtil.getDateTime(model.getEndTime(),DateUtil.FORMAT_DMY_HMS);

                mSessionStartTimeTv.setText(DateUtil.getDate(mStartDate, DateUtil.FORMAT_HM_AP));
                mSessionEndTimeTv.setText(DateUtil.getDate(mEndDate, DateUtil.FORMAT_HM_AP));

            } catch (ParseException e) {
                e.printStackTrace();
                mSessionStartTimeTv.setText(model.getStartTime());
                mSessionEndTimeTv.setText(model.getEndTime());

            }

            container.addView(v);

        }
    }

    @Override
    public int getItemCount() {
        return mIndicators.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView mSessionIndicatorTv;
        LinearLayout mContainer;
        HorizontalScrollView mContainerParent;

        Holder(View v) {
            super(v);
            mContainer = v.findViewById(R.id.session_view_container);
            mContainerParent = v.findViewById(R.id.session_view_container_parent);
            mSessionIndicatorTv = v.findViewById(R.id.session_indication_time_text_view);

        }

    }

}