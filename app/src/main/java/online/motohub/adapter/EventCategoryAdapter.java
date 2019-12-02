package online.motohub.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import online.motohub.R;
import online.motohub.model.EventCategoryModel;


public class EventCategoryAdapter extends RecyclerView.Adapter<EventCategoryAdapter.Holder> {

    ArrayList<EventCategoryModel> mEventCategoryList = new ArrayList<>();
    private Context mContext;
    private ArrayList<EventCategoryModel> mSelectedEventCategory = new ArrayList<>();


    public EventCategoryAdapter(Context mContext, ArrayList<EventCategoryModel> mEventCategoryList) {

        this.mContext = mContext;
        this.mEventCategoryList = mEventCategoryList;

    }

    @Override
    public EventCategoryAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_event_category, parent, false);
        return new EventCategoryAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(EventCategoryAdapter.Holder mHolder, final int pos) {
        try {
            mHolder.mCkEventCategory.setText(mEventCategoryList.get(pos).getGroupName());
            mHolder.mCkEventCategory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mSelectedEventCategory.add(mEventCategoryList.get(pos));
                    } else {
                        mSelectedEventCategory.remove(mEventCategoryList.get(pos));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<EventCategoryModel> getSelectedEventCategory() {
        return mSelectedEventCategory;
    }

    @Override
    public int getItemCount() {
        return mEventCategoryList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.ckEventCategory)
        CheckBox mCkEventCategory;

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

}