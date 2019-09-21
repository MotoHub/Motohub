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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.crypto.NoSuchPaddingException;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.EventsModel;
import online.motohub.model.PaymentCardDetailsModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.util.CryptLib;
import online.motohub.dialog.DialogManager;
import online.motohub.util.PreferenceUtils;

public class CreateNewPaymentActivity extends BaseActivity {

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

    @BindView(R.id.newCardLayout)
    LinearLayout mNewCardLayout;

    String mCardNameTxt, mCardNumberTxt, mCardCvcTxt, mUserID, mProfileID;

    int mCardExpMonthVal, mCardExpYearVal, mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        mAmount = getIntent().getIntExtra(EventsModel.EVENT_AMOUNT, 0);
        mProfileID = String.valueOf(getIntent().getIntExtra(AppConstants.PROFILE_ID, 0));
        mUserID = String.valueOf(PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID));
        try {
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initView() throws Exception {

        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        float mResAmount = (float) mAmount / 100;

        String mPayAmount;
        mPayAmount = "Pay" + " $" + mResAmount;

        mPayBtn.setText(mPayAmount);

        mCardNumber.addTextChangedListener(new CreditCardNumberFormattingTextWatcher());

        mCardExpDate.addTextChangedListener(new DateEntryWatcher());
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
        finish();
    }

    @OnClick({R.id.pay_btn, R.id.toolbar_back_img_btn, R.id.txt_card_management})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                hideSoftKeyboard();
                break;
            case R.id.pay_btn:
                checkValues();
                break;
            case R.id.txt_card_management:
                startActivity(new Intent(this, PaymentActivity.class).putExtra(AppConstants.CARD_SETTINGS, true));
                break;
        }

    }


    private void checkValues() {

        if (mCardName.getText().toString().trim().equals("") && mCardNumber.getText().toString().trim().equals("") && mCardExpDate.getText().toString().trim().equals("") &&
                mCardCvc.getText().toString().trim().equals("")) {
            showToast(CreateNewPaymentActivity.this, "Please fill all details");
        } else if (mCardName.getText().toString().trim().equals("")) {
            showToast(CreateNewPaymentActivity.this, "Please specify the card holder name");
        } else if (mCardNumber.getText().toString().trim().equals("")) {
            showToast(CreateNewPaymentActivity.this, "Please specify the card number");
        } else if (mCardNumber.getText().toString().trim().length() < 16) {
            showToast(this, "The Card number should contains 16 digits");
        } else if (mCardExpDate.getText().toString().trim().equals("")) {
            showToast(CreateNewPaymentActivity.this, "Please specify the card valid date");
        } else if (mCardExpDate.getText().toString().length() < 5) {
            showToast(this, "Please enter valid expiry date");
        } else if (mCardCvc.getText().toString().trim().equals("")) {
            showToast(CreateNewPaymentActivity.this, "Please enter the CVV number");
        } else if (mCardCvc.getText().toString().length() < 3) {
            showToast(this, "The CVV number should contains 3 digits");
        } else {
            //  showAppDialog(AppDialogFragment.PROGRESS_DIALOG,null);
            mCardNameTxt = mCardName.getText().toString();
            mCardNumberTxt = mCardNumber.getText().toString().trim();
            if (mCardExpDate.getText().toString().contains("/")) {
                String[] date = mCardExpDate.getText().toString().split("/");
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

    private void callAddPaymentCardDetails() {

        String mEncryptCardNumber = "";
        CryptLib cryptLib = null;
        try {
            cryptLib = new CryptLib();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            mEncryptCardNumber = cryptLib.encryptPlainTextWithRandomIV(mCardNumberTxt, AppConstants.ENCRYPT_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject mJsonObject = new JsonObject();
        String mRelation = mUserID + "_" + mEncryptCardNumber;
        mJsonObject.addProperty("UserID", mUserID);
        mJsonObject.addProperty("ProfileID", mProfileID);
        mJsonObject.addProperty("CardNumber", mEncryptCardNumber);
        mJsonObject.addProperty("CardName", mCardNameTxt);
        mJsonObject.addProperty("CardExpiryMonth", mCardExpMonthVal);
        mJsonObject.addProperty("CardExpiryYear", mCardExpYearVal);
        mJsonObject.addProperty("CardRelation", mRelation);
        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mJsonObject);

        RetrofitClient.getRetrofitInstance().callAddPaymentCardDetails(this, mJsonArray, RetrofitClient.CALL_ADD_PAYMENT_CARD_DETAILS);

    }

    public void hideSoftKeyboard() {
        try {
            InputMethodManager mInputMethodManager = (InputMethodManager) CreateNewPaymentActivity.this.getSystemService(INPUT_METHOD_SERVICE);
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
            PaymentCardDetailsModel mPaymentCardDetailsModel = (PaymentCardDetailsModel) responseObj;
            if (mPaymentCardDetailsModel.getResource().size() > 0) {
                finish();
            }
        } else if (responseObj instanceof SessionModel) {

            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }


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
                    Toast.makeText(CreateNewPaymentActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
                    callAddPaymentCardDetails();
                }
            });
        } else {
            showToast(this, "The given card is not valid");
        }

    }

    private void finishDialog() {
        DialogManager.hideProgress();

    }

    @Override
    public void retrofitOnFailure(int code, String message) {
        super.retrofitOnFailure(code, message);
    }

    @Override
    public void retrofitOnError(int code, String message, int responseType) {
        super.retrofitOnError(code, message, responseType);
        if (responseType == RetrofitClient.CALL_ADD_PAYMENT_CARD_DETAILS) {
            showSnackBar(mNewCardLayout, getString(R.string.new_card_err));
        }
        if (message.equals("Unauthorized") || code == 401) {
            if (isNetworkConnected(this))
                RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
            else
                showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mNewCardLayout, mErrorMsg);
        }
    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
    }

    private class DateEntryWatcher implements TextWatcher {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String working = s.toString();
            boolean isValid = true;
            if (working.length() == 2 && before == 0) {
                if (Integer.parseInt(working) < 1 || Integer.parseInt(working) > 12) {
                    isValid = false;
                } else {
                    working += "/";
                    mCardExpDate.setText(working);
                    mCardExpDate.setSelection(working.length());
                }
            } else if (working.length() == 7 && before == 0) {
                String enteredYear = working.substring(3);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                if (Integer.parseInt(enteredYear) < currentYear) {
                    isValid = false;
                }
            } else if (working.length() != 7) {
                isValid = false;
            }

            if (!isValid) {
                mCardExpDate.setError("Enter a valid date: MM/YYYY");
            } else {
                mCardExpDate.setError(null);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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
            String[] eachBlock = mCardExpDate.getText().toString().split("/");
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
}
