package online.motohub.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.dialog.DialogManager;

public class EventsResultActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.moto_event_name_tv)
    TextView mMotoEventNameTv;

    @BindView(R.id.winner_name_tv)
    TextView mWinnerNameTv;

    @BindView(R.id.runner_name_tv)
    TextView mRunnerNameTv;

    @BindView(R.id.third_name_tv)
    TextView mThirdNameTv;

    @BindString(R.string.event_results)
    String mToolbarTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_result);

        ButterKnife.bind(this);

        initView();

    }


    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initView() {

        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        String mEventNameStr = "21/3-Moto TT-Taupo";
        String mWinnerStr = "Mike Smith";
        String mRunnerStr = "Tom Hanks";
        String mThirdStr = "Steve Joe";

        mMotoEventNameTv.setText(mEventNameStr);
        mWinnerNameTv.setText(mWinnerStr);
        mRunnerNameTv.setText(mRunnerStr);
        mThirdNameTv.setText(mThirdStr);

    }

    @OnClick(R.id.toolbar_back_img_btn)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
