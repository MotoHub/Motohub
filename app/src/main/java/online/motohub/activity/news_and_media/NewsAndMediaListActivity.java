package online.motohub.activity.news_and_media;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

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
import online.motohub.model.ProfileResModel;
import online.motohub.model.PromotersFollowers1;
import online.motohub.model.SessionModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.dialog.DialogManager;
import online.motohub.util.PreferenceUtils;

public class NewsAndMediaListActivity extends BaseActivity {

    @BindView(R.id.promotersViewGroup)
    ConstraintLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler_view)
    RecyclerView mNewsAndMediaRv;

    @BindString(R.string.news_and_media)
    String mToolbarTitle;

    @BindString(R.string.internet_failure)
    String mInternetFailed;

    @BindString(R.string.no_news_media_err)
    String mPromotersFoundErr;

    private List<PromotersResModel> mNewsAndMediaList;
    private NewsAndMediaListAdapter mNewsAndMediaListAdapter;

    private PromotersFollowers1.Meta meta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promoters_list);
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

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mNewsAndMediaRv.setLayoutManager(mLinearLayoutManager);

        //assert getIntent().getExtras() != null;
        //ProfileResModel mProfileResModel = (ProfileResModel) getIntent().getExtras().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        //ProfileResModel mProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        ProfileResModel mProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);

        mNewsAndMediaList = new ArrayList<>();
        String usertype = "newsmedia";
        mNewsAndMediaListAdapter = new NewsAndMediaListAdapter(mNewsAndMediaList, this, mProfileResModel, usertype);
        mNewsAndMediaRv.setAdapter(mNewsAndMediaListAdapter);

        callGetNewsAndMedias();

    }

    private void callGetNewsAndMedias() {
        String mFilter = "(user_type=newsmedia) AND (Status=2)";
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
                    /*PromotersResModel mPromotersResModel = (PromotersResModel) data.getExtras().getSerializable(PromotersModel.PROMOTERS_RES_MODEL);
                    ProfileResModel mMyProfileResModel = (ProfileResModel) data.getExtras().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);*/
                    //TODO getting data
                    /*PromotersResModel mPromotersResModel = MotoHub.getApplicationInstance().getmPromoterResModel();
                    ProfileResModel mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();*/
                    PromotersResModel mPromotersResModel = EventBus.getDefault().getStickyEvent(PromotersResModel.class);
                    ProfileResModel mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
                    mNewsAndMediaListAdapter.updatePromoterFollowResponse(mPromotersResModel);

                    //TODO setting data
                    /*MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                    MotoHub.getApplicationInstance().setmPromoterResModel(mPromotersResModel);*/
                    EventBus.getDefault().postSticky(mMyProfileResModel);
                    EventBus.getDefault().postSticky(mPromotersResModel);
                    setResult(RESULT_OK, new Intent()
                            /*.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel)
                            .putExtra(PromotersModel.PROMOTERS_RES_MODEL, mPromotersResModel)*/);
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

            callGetNewsAndMedias();

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
