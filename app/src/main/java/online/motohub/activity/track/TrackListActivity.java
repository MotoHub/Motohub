package online.motohub.activity.track;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.adapter.TrackAdapter;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;

import static online.motohub.activity.promoter.PromotersListActivity.PROMOTER_FOLLOW_RESPONSE;

public class TrackListActivity extends BaseActivity  {

    @BindView(R.id.track_list_parent_view)
    LinearLayout mParentView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.track_list_rv)
    RecyclerView mTrackListRv;

    List<PromotersResModel> mTrackResModelsList;
    TrackAdapter mTrackAdapter;

    @BindString(R.string.internet_failure)
    String mInternetFailed;

    @BindString(R.string.now_track_error)
    String mTracksFoundErr;
    public static final String EXTRA_PROFILE_DATA = "extra_profile_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        ButterKnife.bind(this);

        initRv();

        getAllTrackList();

    }

    private void initRv() {

        setToolbar(mToolbar, getString(R.string.Tracks));
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        ProfileResModel mProfileResModel = (ProfileResModel) getIntent().getSerializableExtra(EXTRA_PROFILE_DATA);

        LinearLayoutManager layoutManager = new LinearLayoutManager(TrackListActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mTrackListRv.setLayoutManager(layoutManager);

        mTrackResModelsList = new ArrayList<>();
        mTrackAdapter = new TrackAdapter(TrackListActivity.this, mTrackResModelsList, mProfileResModel);
        mTrackListRv.setAdapter(mTrackAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case PROMOTER_FOLLOW_RESPONSE:
                    assert data.getExtras() != null;
                    PromotersResModel mPromotersResModel = (PromotersResModel) data.getExtras()
                            .getSerializable(PromotersModel.PROMOTERS_RES_MODEL);
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
    public void onBackPressed() {
        finish();
    }

    @OnClick({R.id.toolbar_back_img_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                break;
        }


    }
    //End of OnClick


    //Begin API call
    private void getAllTrackList() {
        String mFilter = "(user_type=track) AND (Status=2)";

        RetrofitClient.getRetrofitInstance().callGetPromoters(this, mFilter, RetrofitClient.GET_PROMOTERS_RESPONSE);
    }

    //end of API call

    //Begin API response
    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
       if (responseObj instanceof PromotersModel) {

            PromotersModel mPromotersModel = (PromotersModel) responseObj;

            switch (responseType) {

                case RetrofitClient.GET_PROMOTERS_RESPONSE:

                    if (mPromotersModel.getResource() != null && mPromotersModel.getResource().size() > 0) {

                        mTrackResModelsList.addAll(mPromotersModel.getResource());
                        mTrackAdapter.notifyDataSetChanged();

                    } else {
                        showSnackBar(mParentView, mTracksFoundErr);
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

           getAllTrackList();

       }

    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);

        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mParentView, mErrorMsg);
        }

    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);

        String mErrorMsg = code + " - " + message;
        showSnackBar(mParentView, mErrorMsg);

    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        showSnackBar(mParentView, mInternetFailed);
    }

}
