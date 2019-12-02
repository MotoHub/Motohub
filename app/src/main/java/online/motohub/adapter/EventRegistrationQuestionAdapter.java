package online.motohub.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import online.motohub.R;
import online.motohub.model.EventAnswersModel;
import online.motohub.model.EventRegistrationQuestionModel;


public class EventRegistrationQuestionAdapter extends RecyclerView.Adapter<EventRegistrationQuestionAdapter.Holder> {

    public String[] mDataset = new String[25];
    ArrayList<EventRegistrationQuestionModel> mEventQuestionList = new ArrayList<>();
    ArrayList<EventAnswersModel> mEventAnswerList = new ArrayList<>();
    private Context mContext;


    public EventRegistrationQuestionAdapter(Context mContext, ArrayList<EventRegistrationQuestionModel> eventQuestionList, ArrayList<EventAnswersModel> mEventAnswerList) {

        this.mContext = mContext;
        this.mEventQuestionList = eventQuestionList;
        this.mEventAnswerList = mEventAnswerList;
    }

    @Override
    public EventRegistrationQuestionAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_event_question_list, parent, false);
        return new EventRegistrationQuestionAdapter.Holder(view, new MyCustomEditTextListener());
    }

    @Override
    public void onBindViewHolder(EventRegistrationQuestionAdapter.Holder mHolder, final int pos) {
        try {
            String mQtnNo = (pos + 1) + ".";
            mHolder.mQuestionNoTv.setText(mQtnNo);

            mHolder.myCustomEditTextListener.updatePosition(mHolder.getLayoutPosition());
            mHolder.mQuestionTv.setText(mEventQuestionList.get(pos).getQuestion());
            if (mEventAnswerList != null && !mEventAnswerList.isEmpty())
                mHolder.mAnswerEt.setText(mEventAnswerList.get(pos).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getAnswers() {
        return this.mDataset;
    }

    @Override
    public int getItemCount() {
        return mEventQuestionList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        public MyCustomEditTextListener myCustomEditTextListener;
        @BindView(R.id.qtn_number_tv)
        TextView mQuestionNoTv;
        @BindView(R.id.qtn_tv)
        TextView mQuestionTv;
        @BindView(R.id.answer_et)
        EditText mAnswerEt;

        public Holder(View view, MyCustomEditTextListener myCustomEditTextListener) {
            super(view);
            ButterKnife.bind(this, view);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.mAnswerEt.addTextChangedListener(myCustomEditTextListener);
        }
    }

    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            mDataset[position] = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

}