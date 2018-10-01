package online.motohub.activity.performance_shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.promoter.PromotersListActivity;
import online.motohub.adapter.news_and_media.NewsAndMediaListAdapter;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;

public class PerformanceShopListActivity extends BaseActivity {

    @BindView(R.id.promotersViewGroup)
    ConstraintLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler_view)
    RecyclerView mNewsAndMediaRv;

    @BindString(R.string.shop)
    String mToolbarTitle;

    @BindString(R.string.internet_failure)
    String mInternetFailed;

    @BindString(R.string.no_performance_shop_err)
    String mPromotersFoundErr;

    private List<PromotersResModel> mNewsAndMediaList;
    private NewsAndMediaListAdapter mNewsAndMediaListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promoters_list);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mNewsAndMediaRv.setLayoutManager(mLinearLayoutManager);

        assert getIntent().getExtras() != null;
        ProfileResModel mProfileResModel = (ProfileResModel) getIntent().getExtras().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);

        mNewsAndMediaList = new ArrayList<>();
        String usertype = "shop";
        mNewsAndMediaListAdapter = new NewsAndMediaListAdapter(mNewsAndMediaList, this, mProfileResModel,usertype);
        mNewsAndMediaRv.setAdapter(mNewsAndMediaListAdapter);

        callGetPerformaceShop();

    }

    private void callGetPerformaceShop() {
        String mFilter = "(user_type = shop) AND (Status=2)";
        RetrofitClient.getRetrofitInstance().callGetPromoters(this, mFilter, RetrofitClient.GET_PROMOTERS_RESPONSE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PromotersListActivity.PROMOTER_FOLLOW_RESPONSE:
                    assert data.getExtras() != null;
                    PromotersResModel mPromotersResModel = (PromotersResModel) data.getExtras()
                            .getSerializable(PromotersModel.PROMOTERS_RES_MODEL);
                    mNewsAndMediaListAdapter.updatePromoterFollowResponse(mPromotersResModel);
                    ProfileResModel mMyProfileResModel = (ProfileResModel) data.getExtras()
                            .getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
                    setResult(RESULT_OK, new Intent()
                            .putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel)
                            .putExtra(PromotersModel.PROMOTERS_RES_MODEL, mPromotersResModel));
                    break;
            }
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        if (responseObj instanceof PromotersModel) {

            PromotersModel mPromotersModel = (PromotersModel) responseObj;

            switch (responseType) {

                case RetrofitClient.GET_PROMOTERS_RESPONSE:

                    if (mPromotersModel.getResource() != null && mPromotersModel.getResource().size() > 0) {

                        mNewsAndMediaList.addAll(mPromotersModel.getResource());
                        mNewsAndMediaListAdapter.notifyDataSetChanged();

                    } else {
                        showSnackBar(mCoordinatorLayout, mPromotersFoundErr);
                    }

                    break;

            }

        } else if (responseObj instanceof SessionModel) {

            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }

            callGetPerformaceShop();

        }
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mCoordinatorLayout, mErrorMsg);
        }
    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
        String mErrorMsg = code + " - " + message;
        showSnackBar(mCoordinatorLayout, mErrorMsg);
    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        showSnackBar(mCoordinatorLayout, mInternetFailed);
    }

}
