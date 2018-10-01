package online.motohub.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.model.EventsModel;
import online.motohub.util.DialogManager;

public class PaymentActivity extends BaseActivity {

    @BindView(R.id.et_card_name)
    EditText mCardName;

    @BindView(R.id.et_card_number)
    EditText mCardNumber;

    @BindView(R.id.et_card_exp_date)
    EditText mCardExpDate;

    @BindView(R.id.et_cvc)
    EditText mCardCvc;

    @BindString(R.string.payment)
    String mToolbarTitle;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.pay_btn)
    Button mPayBtn;

    String mCardNameTxt, mCardNumberTxt, mCardCvcTxt;

    int mCardExpMonthVal, mCardExpYearVal;

    int mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);

        showToast(this, "Please keep the perfect internet connection while doing payment");
        mAmount = getIntent().getIntExtra(EventsModel.EVENT_AMOUNT, 0);
        initView();

    }

    private void initView() {

        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        float mResAmount = (float) mAmount / 100;

        String mPayAmount;
        mPayAmount = "Pay" + " $" + mResAmount;

        mPayBtn.setText(mPayAmount);

        mCardNumber.addTextChangedListener(new CreditCardNumberFormattingTextWatcher());

        mCardExpDate.addTextChangedListener(new CreditCardExpiryDateFormattingTextWatcher());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
    }

    @OnClick({R.id.pay_btn, R.id.toolbar_back_img_btn})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                hideSoftKeyboard();
                break;
            case R.id.pay_btn:

                checkValues();
                break;
        }

    }

    private void purchase() {
        Card card = new Card(mCardNumberTxt, mCardExpMonthVal, mCardExpYearVal, mCardCvcTxt);
        Stripe stripe = new Stripe(PaymentActivity.this, getString(R.string.stripe_publishable_key));

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
            showToast(PaymentActivity.this, "The given card is not valid");
        }

    }

    private void finishDialog() {
         DialogManager.hideProgress();

    }

    private void checkValues() {

        if (mCardName.getText().toString().trim().equals("") && mCardNumber.getText().toString().trim().equals("") && mCardExpDate.getText().toString().trim().equals("") &&
                mCardCvc.getText().toString().trim().equals("")) {
            showToast(PaymentActivity.this, "Please fill all details");
        } else if (mCardName.getText().toString().trim().equals("")) {
            showToast(PaymentActivity.this, "Please specify the card holder name");
        } else if (mCardNumber.getText().toString().trim().equals("")) {
            showToast(PaymentActivity.this, "Please specify the card number");
        } else if (mCardNumber.getText().toString().trim().length() < 16) {
            showToast(this, "The Card number should contains 16 digits");
        } else if (mCardExpDate.getText().toString().trim().equals("")) {
            showToast(PaymentActivity.this, "Please specify the card valid date");
        } else if (mCardExpDate.getText().toString().length() < 5) {
            showToast(this, "Please enter valid expiry date");
        } else if (mCardCvc.getText().toString().trim().equals("")) {
            showToast(PaymentActivity.this, "Please enter the CVV number");
        } else if (mCardCvc.getText().toString().length() < 3) {
            showToast(this, "The CVV number should contains 3 digits");
        } else {
            //  showAppDialog(AppDialogFragment.PROGRESS_DIALOG,null);
            mCardNameTxt = mCardName.getText().toString();
            mCardNumberTxt = mCardNumber.getText().toString().trim();
            if (mCardExpDate.getText().toString().contains("/")) {
                String date[] = mCardExpDate.getText().toString().split("/");
                mCardExpMonthVal = Integer.parseInt(date[0]);
                mCardExpYearVal = Integer.parseInt(date[1]);
            } else {
                showToast(this, "Please enter valid expiry date");
            }
            mCardCvcTxt = mCardCvc.getText().toString().trim();

            DateFormat df = new SimpleDateFormat("yy");
            int mCurrentYear = Integer.parseInt(df.format(Calendar.getInstance().getTime()));

            if (mCardExpMonthVal > 12) {
                showToast(this, "Expiry month should not be greater than 12");
            } else if (mCardExpYearVal < mCurrentYear) {
                showToast(this, "Expiry year should not below the current year");
            } else {
                purchase();
            }
        }
    }

    private class CreditCardNumberFormattingTextWatcher implements TextWatcher {
        String temp;
        int keyDel;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            boolean flag = true;

            String eachBlock = mCardNumber.getText().toString().trim();
            if (!eachBlock.isEmpty()) {
                if (eachBlock.length() % 5 == 0) {
                    flag = false;
                }
            }
            if (flag) {

                mCardNumber.setOnKeyListener(new View.OnKeyListener() {

                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if (keyCode == KeyEvent.KEYCODE_DEL)
                            keyDel = 1;
                        return false;
                    }
                });

                if (keyDel == 0) {

                    if (((mCardNumber.getText().length() + 1) % 5) == 0) {

                        if (mCardNumber.getText().toString().split(" ").length <= 3) {
                            String mCardNum = mCardNumber.getText() + " ";
                            mCardNumber.setText(mCardNum);
                            mCardNumber.setSelection(mCardNumber.getText().length());
                        }
                    }
                    temp = mCardNumber.getText().toString();
                } else {
                    temp = mCardNumber.getText().toString();
                    keyDel = 0;
                }

            } else {
                mCardNumber.setText(temp);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

    }

    private class CreditCardExpiryDateFormattingTextWatcher implements TextWatcher {
        String temp;
        int keyDel;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            boolean flag = true;
            String eachBlock[] = mCardExpDate.getText().toString().split("/");
            for (String anEachBlock : eachBlock) {
                if (anEachBlock.length() > 2) {
                    flag = false;
                }
            }
            if (flag) {

                mCardExpDate.setOnKeyListener(new View.OnKeyListener() {

                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if (keyCode == KeyEvent.KEYCODE_DEL)
                            keyDel = 1;
                        return false;
                    }
                });

                if (keyDel == 0) {

                    if (((mCardExpDate.getText().length() + 1) % 3) == 0) {

                        if (mCardExpDate.getText().toString().split("/").length <= 1) {
                            String mCardNum = mCardExpDate.getText() + "/";
                            mCardExpDate.setText(mCardNum);
                            mCardExpDate.setSelection(mCardExpDate.getText().length());
                        }
                    }
                    temp = mCardExpDate.getText().toString();
                } else {
                    temp = mCardExpDate.getText().toString();
                    keyDel = 0;
                }

            } else {
                mCardExpDate.setText(temp);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    public void hideSoftKeyboard() {
        try {
            InputMethodManager mInputMethodManager = (InputMethodManager) PaymentActivity.this.getSystemService(INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null
                    && getCurrentFocus().getWindowToken() != null) {
                mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
