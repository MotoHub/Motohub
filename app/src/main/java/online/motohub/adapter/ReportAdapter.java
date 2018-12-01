package online.motohub.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.ReportActivity;


public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {


    private String[] reportContent;
    private int lastSelectedPosition = -1;

    private Context context;

    class ViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        @BindView(R.id.textViewReportContent)
        TextView mContent;
        @BindView(R.id.radioButtonReport)
        RadioButton mRadioButtonReport;

        @BindView(R.id.viewRowSeparator)
        View mRowSeparator;

        @BindView(R.id.reportRowLinearLayout)
        LinearLayout mReportRowLinearLayout;

        public ViewHolder(View reportItemView) {
            super(reportItemView);
            ButterKnife.bind(this, reportItemView);
        }

    }


    public ReportAdapter(Context context) {
        this.context = context;
        reportContent = context.getResources().getStringArray(R.array.report_array);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View reportItemvView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_report, parent, false);
        return new ViewHolder(reportItemvView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.mContent.setText(reportContent[position]);
            holder.mRadioButtonReport.setChecked(lastSelectedPosition == position);

            if (position == reportContent.length - 1) {
                holder.mRowSeparator.setVisibility(View.GONE);
            } else {
                holder.mRowSeparator.setVisibility(View.VISIBLE);
            }

            holder.mRadioButtonReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastSelectedPosition = position;
                    setReportOption(position, holder);
                }
            });
            holder.mReportRowLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastSelectedPosition = position;
                    setReportOption(position, holder);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setReportOption(int position, ViewHolder holder) {
        notifyDataSetChanged();
        if(context instanceof ReportActivity) {
            if (position == (reportContent.length - 1)) {
                holder.mRadioButtonReport.setChecked(false);
                ((BaseActivity) context).moveToReportWritePostScreen(context);
            } else {
                ((ReportActivity) context).setReportString(reportContent[position]);
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return reportContent.length;
    }

}
