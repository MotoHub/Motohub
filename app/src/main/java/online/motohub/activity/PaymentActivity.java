package online.motohub.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.EventsFindAdapter;
import online.motohub.adapter.PaymentCardDetailsAdapter;
import online.motohub.model.EventsModel;
import online.motohub.model.PaymentCardDetailsModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.util.CryptLib;
import online.motohub.dialog.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.SwipeHelper;

public class PaymentActivity extends BaseActivity {

    @BindView(R.id.parent)
    RelativeLayout mParent;

    @BindString(R.string.saved_cards)
    String mToolbarTitle;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;


    @BindView(R.id.pay_btn)
    Button mPayBtn;

    @BindView(R.id.savedCardRecyclerView)
    RecyclerView mSavedardRecyclerView;

    @BindView(R.id.newCardView)
    CardView mNewCardView;

    @BindView(R.id.payableAmountTxt)
    TextView mPayableAmountTxt;

    @BindView(R.id.payableCardView)
    CardView mPayableCardView;

    int mAmount, mUserID, mProfileID;
    String mCardNumberTxt, mCardCvcTxt;
    int mCardExpMonthVal, mCardExpYearVal;
    private ArrayList<PaymentCardDetailsModel> mPaymentCardDetailsList = new ArrayList<>();
    private boolean mIsFromCardManagement;
    private LinearLayoutManager mSavedCardsLayoutManager;
    private PaymentCardDetailsAdapter mPaymentCardDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);
        ButterKnife.bind(this);
        getData();
        initView();

    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void getData() {
        mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        if (getIntent().hasExtra(AppConstants.CARD_SETTINGS)) {
            //mIsFromCardManagement = true;
            mIsFromCardManagement = getIntent().getBooleanExtra(AppConstants.CARD_SETTINGS, true);
        } else {
            showToast(this, "Please keep the perfect internet connection while doing payment");
            mAmount = getIntent().getIntExtra(EventsModel.EVENT_AMOUNT, 0);
            mProfileID = getIntent().getIntExtra(AppConstants.PROFILE_ID, 0);
        }
    }

    private void initView() {

        setupUI(mParent);

        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        if (!mIsFromCardManagement) {

            float mResAmount = (float) mAmount / 100;

            String mPayAmount;
            mPayAmount = " $" + mResAmount;

            mPayableAmountTxt.setText(mPayAmount);
        } else {
            mPayBtn.setVisibility(View.GONE);
            mNewCardView.setVisibility(View.GONE);
            mPayableCardView.setVisibility(View.GONE);

        }
        mSavedCardsLayoutManager = new LinearLayoutManager(getApplicationContext());
        mSavedCardsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mSavedardRecyclerView.setLayoutManager(mSavedCardsLayoutManager);

        new SwipeHelper(this, mSavedardRecyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Delete",
                        0,
                        ContextCompat.getColor(PaymentActivity.this, R.color.colorWhite),
                        ContextCompat.getColor(PaymentActivity.this, R.color.colorRed),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: onDelete
                                String mFilter = "ID = " + mPaymentCardDetailsList.get(pos).getID();
                                callDeleteCardDetails(mFilter);
                                mPaymentCardDetailsList.remove(pos);
                                setPaymentCardAdapter();

                            }
                        }
                ));

            }
        };

        callGetCardDetails();

    }

    private void callDeleteCardDetails(String mFilter) {
        RetrofitClient.getRetrofitInstance().callDeletePaymentCardDetails(this, mFilter, RetrofitClient.DELETE_PAYMENT_CARD_DETAILS);
    }

    private void callGetCardDetails() {

        String mFilter = "UserID = " + mUserID;
        RetrofitClient.getRetrofitInstance().callGetPaymentCardDetails(this, mFilter, RetrofitClient.CALL_GET_PAYMENT_CARD_DETAILS);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @OnClick({R.id.pay_btn, R.id.toolbar_back_img_btn, R.id.newCardView})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                hideSoftKeyboard();
                break;
            case R.id.pay_btn:
                purchase();
                break;
            case R.id.newCardView:
                startActivityForResult(new Intent(this, CreateNewPaymentActivity.class).putExtra(AppConstants.PROFILE_ID, mProfileID).putExtra(EventsModel.EVENT_AMOUNT, mAmount), EventsFindAdapter.EVENT_PAYMENT_REQ_CODE);
                break;
        }

    }

    private void purchase() {
        Card card = new Card(mCardNumberTxt, mCardExpMonthVal, mCardExpYearVal, mCardCvcTxt);
        Stripe stripe = new Stripe(this, getString(R.string.stripe_publishable_key));

        if (card.validateCard()) {
            DialogManager.showProgress(this);
            stripe.createToken(card, new TokenCallback() {
                @Override
                public void onError(Exception error) {
                    Toast.makeText(PaymentActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    finishDialog();
                }

                @Override
                public void onSuccess(Token token) {
                    String mToken = token.getId();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("TOKEN", mToken);
                    resultIntent.putExtra(EventsModel.EVENT_AMOUNT, mAmount);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finishDialog();
                    finish();
                }
            });
        } else {
            showToast(this, "The given card is not valid");
        }

    }

    private void finishDialog() {
        DialogManager.hideProgress();

    }

    public void hideSoftKeyboard() {
        try {
            InputMethodManager mInputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null
                    && getCurrentFocus().getWindowToken() != null) {
                mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof PaymentCardDetailsModel) {
            switch (responseType) {
                case RetrofitClient.CALL_GET_PAYMENT_CARD_DETAILS:
                    if (((PaymentCardDetailsModel) responseObj).getResource().size() > 0) {
                        mPaymentCardDetailsList.addAll(((PaymentCardDetailsModel) responseObj).getResource());
                        setPaymentCardAdapter();
                    } else {
                        /*if(!mIsFromCardManagement) {
                            startActivityForResult(new Intent(this, CreateNewPaymentActivity.class).putExtra(AppConstants.PROFILE_ID, mProfileID).putExtra(EventsModel.EVENT_AMOUNT, mAmount), EventsFindAdapter.EVENT_PAYMENT_REQ_CODE);
                        } else{*/
                        showSnackBar(mParent, getString(R.string.no_saved_cards));
                        //  }
                    }
                    break;
                case RetrofitClient.DELETE_PAYMENT_CARD_DETAILS:
                    showColorSnackBar(mParent, getString(R.string.delete_card_msg));
                    break;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConstants.CREATE_NEW_PAYMENT_REQUEST) {
                PaymentCardDetailsModel mNewPaymentCardDetails = (PaymentCardDetailsModel) data.getSerializableExtra(AppConstants.NEW_PAYMENT_CARD_DETAILS);
                mPaymentCardDetailsList.add(mNewPaymentCardDetails);
                mPaymentCardDetailsAdapter.notifyDataSetChanged();
                if (mPaymentCardDetailsList.size() > 0) {
                    setPaymentCardAdapter();
                } else {
                    mSavedardRecyclerView.setVisibility(View.GONE);
                }
            } else if (requestCode == EventsFindAdapter.EVENT_PAYMENT_REQ_CODE) {
                String mToken = "";
                if (data.hasExtra("TOKEN")) {
                    mToken = data.getStringExtra("TOKEN");
                }
                Intent resultIntent = new Intent();
                resultIntent.putExtra("TOKEN", mToken);
                resultIntent.putExtra(EventsModel.EVENT_AMOUNT, mAmount);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        }
    }

    private void setPaymentCardAdapter() {

        if (mPaymentCardDetailsAdapter == null) {
            mPaymentCardDetailsAdapter = new PaymentCardDetailsAdapter(this, mPaymentCardDetailsList, mIsFromCardManagement);
            mSavedardRecyclerView.setAdapter(mPaymentCardDetailsAdapter);
        } else
            mPaymentCardDetailsAdapter.notifyDataSetChanged();

    }

    @Override
    public void retrofitOnFailure(int code, String message) {
        super.retrofitOnFailure(code, message);
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
    }

    public void setCardDetails(String cvv, String cardNumber, String cardExpMonthVal, String cardExpYearVal) {

        CryptLib cryptLib = null;
        try {
            cryptLib = new CryptLib();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        mCardCvcTxt = cvv;
        try {
            assert cryptLib != null;
            mCardNumberTxt = cryptLib.decryptCipherTextWithRandomIV(cardNumber, AppConstants.ENCRYPT_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCardExpMonthVal = Integer.parseInt(cardExpMonthVal);
        mCardExpYearVal = Integer.parseInt(cardExpYearVal);

        purchase();

    }
}
