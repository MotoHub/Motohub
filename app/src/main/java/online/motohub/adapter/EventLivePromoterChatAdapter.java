package online.motohub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.model.EventGrpChatMsgResModel;


public class EventLivePromoterChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<EventGrpChatMsgResModel> mMsgList;
    private LayoutInflater mInflater;


    public EventLivePromoterChatAdapter(Context context, ArrayList<EventGrpChatMsgResModel> msgList) {
        mContext = context;
        mMsgList = msgList;
        mInflater = LayoutInflater.from(mContext);
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.msg_txt)
        TextView mMsgTxt;
        @BindView(R.id.time_txt)
        TextView mSenderMsgTimeTv;

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mMsgTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSenderMsgTimeTv.getVisibility() == View.VISIBLE) {
                        mSenderMsgTimeTv.setVisibility(View.GONE);
                    } else {
                        mSenderMsgTimeTv.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView;
        mView = mInflater.inflate(R.layout.adap_promoter_msg, parent, false);
        return new Holder(mView);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int pos) {
        final Holder mHolder = (Holder) holder;
        try {
            mHolder.mMsgTxt.setText(URLDecoder.decode(mMsgList.get(pos).getMessage(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mHolder.mSenderMsgTimeTv.setText(((BaseActivity) mContext).getFormattedDate(mMsgList.get(pos).getCreatedAt()));

    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }

}
