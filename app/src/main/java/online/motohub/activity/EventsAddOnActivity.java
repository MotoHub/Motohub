package online.motohub.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.EventAddOnAdapter;
import online.motohub.adapter.EventsFindAdapter;
import online.motohub.application.MotoHub;
import online.motohub.model.EventAddOnModel;
import online.motohub.model.EventsModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.dialog.DialogManager;

public class EventsAddOnActivity extends BaseActivity {

    @BindView(R.id.rvAddOn)
    RecyclerView mRvAddOn;

    @BindView(R.id.doneBtn)
    Button mTotalPriceAmountBtn;

    @BindString(R.string.event_add_on)
    String mToolbarTitle;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.noAddOnTxt)
    TextView mNoAddOnTxt;


    String mResEventAmount;

    private ArrayList<EventAddOnModel> mEventAddOnList = new ArrayList<>();
    private EventAddOnAdapter mEventAddOnAdapter;
    private int mEventID, mTotalAmount, mProfileID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_add_on);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    @OnClick({R.id.doneBtn, R.id.toolbar_back_img_btn})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                break;
            case R.id.doneBtn:
                MotoHub.getApplicationInstance().setPurchasedAddOn(mEventAddOnAdapter.getSelectedEventAddOn());
                Intent paymentActivity = new Intent(this, PaymentActivity.class);
                paymentActivity.putExtra(EventsModel.EVENT_AMOUNT, mTotalAmount).putExtra(AppConstants.PROFILE_ID, mProfileID);
                startActivityForResult(paymentActivity, EventsFindAdapter.EVENT_PAYMENT_REQ_CODE);
                break;
        }

    }


    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
    }

    private void initView() {

        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        mEventID = getIntent().getIntExtra(EventsModel.EVENT_ID, 0);
        mTotalAmount = getIntent().getIntExtra(EventsModel.EVENT_AMOUNT, 0);
        mProfileID = getIntent().getIntExtra(AppConstants.PROFILE_ID, 0);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRvAddOn.setLayoutManager(mLayoutManager);
        mRvAddOn.setItemAnimator(new DefaultItemAnimator());

        mEventAddOnAdapter = new EventAddOnAdapter(this, mEventAddOnList);
        mRvAddOn.setAdapter(mEventAddOnAdapter);

        mResEventAmount = "Event Amount $ " + (mTotalAmount / 100);
        mTotalPriceAmountBtn.setText(mResEventAmount);


        callGetEventAddOn();
    }

    private void callGetEventAddOn() {
        String mFilter = "event_id=" + mEventID;
        RetrofitClient.getRetrofitInstance().callGetEventAddOn(this, mFilter, RetrofitClient.CALL_GET_ADD_ON_RESPONSE);
    }

    public void increaseTotalAmount(int mAmount) {
        mTotalAmount += mAmount;
        mResEventAmount = "Event Amount $ " + (mTotalAmount / 100);
        mTotalPriceAmountBtn.setText(mResEventAmount);
    }

    public void decreaseTotalAmount(int mAmount) {
        mTotalAmount -= mAmount;
        mResEventAmount = "Event Amount $ " + (mTotalAmount / 100);
        mTotalPriceAmountBtn.setText(mResEventAmount);

    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof EventAddOnModel) {
            EventAddOnModel mEventAddOnModel = (EventAddOnModel) responseObj;
            if (mEventAddOnModel.getResource().size() == 0) {
                mNoAddOnTxt.setVisibility(View.VISIBLE);

            }
            mEventAddOnList.clear();
            mEventAddOnList.addAll(mEventAddOnModel.getResource());
            mEventAddOnAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showToast(this, mErrorMsg);
        }
    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
    }

    @Override
    public void retrofitOnError(int code, String message, int responseType) {
        super.retrofitOnError(code, message, responseType);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showToast(this, mErrorMsg);
        }
    }

    @Override
    public void retrofitOnFailure(int responseType) {
        super.retrofitOnFailure(responseType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EventsFindAdapter.EVENT_PAYMENT_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                resultIntent.putExtra("TOKEN", data.getStringExtra("TOKEN"));
                resultIntent.putExtra(EventsModel.EVENT_AMOUNT, mTotalAmount);
                this.finish();
            } else if (resultCode == RESULT_CANCELED) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
            }
        }
    }
}
