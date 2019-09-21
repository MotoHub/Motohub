package online.motohub.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.PaymentActivity;
import online.motohub.model.PaymentCardDetailsModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.util.CryptLib;

public class PaymentCardDetailsAdapter extends RecyclerView.Adapter<PaymentCardDetailsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<PaymentCardDetailsModel> mPaymentCardDetailsList = new ArrayList<>();
    private int oldPosition = -1;
    private boolean mIsFromCardManagement;


    public PaymentCardDetailsAdapter(Context context, ArrayList<PaymentCardDetailsModel> paymentCardDetailsList, boolean isFromCardManagement) {
        mContext = context;
        mPaymentCardDetailsList = paymentCardDetailsList;
        mIsFromCardManagement = isFromCardManagement;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_payment_card_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        CryptLib cryptLib = null;
        String mDecryptCardNumber = "";
        try {
            cryptLib = new CryptLib();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            assert cryptLib != null;
            mDecryptCardNumber = cryptLib.decryptCipherTextWithRandomIV(mPaymentCardDetailsList.get(position).getCardNumber(), AppConstants.ENCRYPT_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.mPaymentCardNumber.setText(((BaseActivity) mContext).setPaymentCardNumber(mDecryptCardNumber));
        holder.mPaymentCardName.setText(mPaymentCardDetailsList.get(position).getCardName());
        if (!mIsFromCardManagement) {
            holder.mCardDetailsConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.mCardCvvRelativeLayout.getVisibility() == View.VISIBLE)
                        holder.mCardCvvRelativeLayout.setVisibility(View.GONE);
                    else
                        holder.mCardCvvRelativeLayout.setVisibility(View.VISIBLE);
                }
            });
            holder.mIvSelectedPayCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.mCardCvvRelativeLayout.setVisibility(View.GONE);
                    if (mContext instanceof PaymentActivity)
                        ((PaymentActivity) mContext).setCardDetails(holder.mCvvEdtTxt.getText().toString(), mPaymentCardDetailsList.get(position).getCardNumber(), mPaymentCardDetailsList.get(position).getCardExpiryMonth(), mPaymentCardDetailsList.get(position).getmCardExpiryYear());

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mPaymentCardDetailsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.paymentCardNumber)
        TextView mPaymentCardNumber;
        @BindView(R.id.paymentCardName)
        TextView mPaymentCardName;
        @BindView(R.id.paymentCardImage)
        CircleImageView mPaymentImage;
        @BindView(R.id.rootCard)
        CardView root;
        @BindView(R.id.cardCvvRelativeLayout)
        RelativeLayout mCardCvvRelativeLayout;
        @BindView(R.id.cardDetailsConstraintLayout)
        ConstraintLayout mCardDetailsConstraintLayout;
        @BindView(R.id.ivSelectedPayCard)
        CircleImageView mIvSelectedPayCard;
        @BindView(R.id.cvvEdtTxt)
        EditText mCvvEdtTxt;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
