package online.motohub.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private StringBuffer sb = new StringBuffer();

    public EventLivePromoterChatAdapter(Context context, ArrayList<EventGrpChatMsgResModel> msgList) {
        mContext = context;
        mMsgList = msgList;
        mInflater = LayoutInflater.from(mContext);
    }

    public static String replacer(StringBuffer outBuffer) {

        String data = outBuffer.toString();
        try {
            StringBuffer tempBuffer = new StringBuffer();
            int incrementor = 0;
            int dataLength = data.length();
            while (incrementor < dataLength) {
                char charecterAt = data.charAt(incrementor);
                if (charecterAt == '%') {
                    tempBuffer.append("<percentage>");
                } else if (charecterAt == '+') {
                    tempBuffer.append("<plus>");
                } else {
                    tempBuffer.append(charecterAt);
                }
                incrementor++;
            }
            data = tempBuffer.toString();
            data = URLDecoder.decode(data, "utf-8");
            data = data.replaceAll("<percentage>", "%");
            data = data.replaceAll("<plus>", "+");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView;
        mView = mInflater.inflate(R.layout.adap_promoter_msg, parent, false);
        return new Holder(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int pos) {
        try {
            final Holder mHolder = (Holder) holder;
            try {
                //mHolder.mMsgTxt.setText(URLDecoder.decode(mMsgList.get(pos).getMainObj(), "UTF-8"));
                mHolder.mMsgTxt.setText(sb.append(mMsgList.get(pos).getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHolder.mSenderMsgTimeTv.setText(((BaseActivity) mContext).getFormattedDate(mMsgList.get(pos).getCreatedAt()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
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

}
