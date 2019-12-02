package online.motohub.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import online.motohub.R;
import online.motohub.activity.EventsAddOnActivity;
import online.motohub.model.EventAddOnModel;

public class EventAddOnAdapter extends RecyclerView.Adapter<EventAddOnAdapter.Holder> {

    private Context mContext;

    private ArrayList<EventAddOnModel> mEventAddOnList = new ArrayList<>();
    private ArrayList<EventAddOnModel> mSelectedEventAddOn = new ArrayList<>();
    private String mAmount;


    public EventAddOnAdapter(Context mContext, ArrayList<EventAddOnModel> mEventCategoryList) {

        this.mContext = mContext;
        this.mEventAddOnList = mEventCategoryList;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_on_list_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder mHolder, int pos) {
        try {
            mHolder.mAddOnDescTxt.setText(mEventAddOnList.get(pos).getAddOnDescription());

            mAmount = "+ " + "$ " + (mEventAddOnList.get(mHolder.getLayoutPosition()).getAddOnPrice() / 100);
            mHolder.mAddOnToggleBtn.setText(mAmount);
            mHolder.mAddOnToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mSelectedEventAddOn.add(mEventAddOnList.get(mHolder.getLayoutPosition()));
                        String mTempText = "- " + "$ " + (mEventAddOnList.get(mHolder.getLayoutPosition()).getAddOnPrice() / 100);
                        mHolder.mAddOnToggleBtn.setTextOn(mTempText);
                        ((EventsAddOnActivity) mContext).increaseTotalAmount(mEventAddOnList.get(mHolder.getLayoutPosition()).getAddOnPrice());
                    } else {
                        mSelectedEventAddOn.remove(mEventAddOnList.get(mHolder.getLayoutPosition()));
                        String mTempText = "+ " + "$ " + (mEventAddOnList.get(mHolder.getLayoutPosition()).getAddOnPrice() / 100);
                        mHolder.mAddOnToggleBtn.setTextOff(mTempText);
                        ((EventsAddOnActivity) mContext).decreaseTotalAmount(mEventAddOnList.get(mHolder.getLayoutPosition()).getAddOnPrice());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<EventAddOnModel> getSelectedEventAddOn() {
        return mSelectedEventAddOn;
    }

    @Override
    public int getItemCount() {
        return mEventAddOnList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.addOnDescTxt)
        TextView mAddOnDescTxt;

        @BindView(R.id.addOnToggleBtn)
        ToggleButton mAddOnToggleBtn;

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

}
