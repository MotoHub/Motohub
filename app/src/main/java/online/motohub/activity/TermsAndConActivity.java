package online.motohub.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.dialog.DialogManager;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.util.UrlUtils;

public class TermsAndConActivity extends BaseActivity {

    @BindString(R.string.terms_conditions)
    String mToolbarTitle;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.web_view)
    WebView mWebView;

    @BindView(R.id.bottom_lay)
    LinearLayout mBottomLay;

    @BindView(R.id.terms_conditions_check_box)
    CheckBox mTermsAndConChkBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

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

        boolean isTerms = getIntent().getBooleanExtra(AppConstants.IS_FROM_TERMS, false);
        DialogManager.showProgress(TermsAndConActivity.this);

        if (isTerms)
            mBottomLay.setVisibility(View.VISIBLE);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                DialogManager.hideProgress();
            }
        });

        mWebView.loadUrl(UrlUtils.TERMS_AND_CON);

    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.submit_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.submit_btn:
                if (mTermsAndConChkBox.isChecked()) {
                    showToast(this, getString(R.string.under_dev));
//                    startActivity(new Intent(this, ScannerActivity.class));
                } else {
                    showToast(this, getString(R.string.accept_terms_and_con));
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

}
