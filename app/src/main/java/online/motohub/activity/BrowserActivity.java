package online.motohub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.util.DialogManager;
import online.motohub.util.UrlUtils;

public class BrowserActivity extends BaseActivity {

    @BindString(R.string.facebook_login)
    String mToolbarTitle;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.web_view)
    WebView mWebView;

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
        DialogManager.showProgress(this);

        mWebView.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                DialogManager.hideProgress();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent;
                Uri data = Uri.parse(url);
                String service = data.getQueryParameter("service");
                if (service != null && service.equals("facebook")) {
                    intent = new Intent(BrowserActivity.this, LoginActivity.class);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    finish();
                    return true;
                }
                DialogManager.showProgress(BrowserActivity.this);
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.loadUrl(UrlUtils.BASE_URL + UrlUtils.FACEBOOK_LOGIN);

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
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

}
