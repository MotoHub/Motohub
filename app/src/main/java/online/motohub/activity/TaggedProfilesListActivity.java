package online.motohub.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import online.motohub.R;
import online.motohub.adapter.TaggedProfilesListAdapter;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;

public class TaggedProfilesListActivity extends BaseActivity {

    public static final String TAGGED_PROFILES_ID = "TaggedProfilesID";
    @BindView(R.id.list_view_co_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.widget_list_view)
    ListView mTaggedProfilesListView;
    @BindString(R.string.tagged_profile)
    String mToolbarTitle;
    private ArrayList<ProfileResModel> mTaggedProfilesList;
    private TaggedProfilesListAdapter mTaggedProfilesListAdapter;
    private String mTaggedProfilesIDStr;
    private ProfileResModel mMyProfileResModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.widget_list_view);

        ButterKnife.bind(this);

        initView();

    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initView() {

        //mMyProfileResModel = (ProfileResModel) getIntent().getExtras().get(ProfileModel.MY_PROFILE_RES_MODEL);
        //mMyProfileResModel= MotoHub.getApplicationInstance().getmProfileResModel();
        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);

        mTaggedProfilesIDStr = getIntent().getStringExtra(TAGGED_PROFILES_ID);

        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        mTaggedProfilesList = new ArrayList<>();
        mTaggedProfilesListAdapter = new TaggedProfilesListAdapter(this, mTaggedProfilesList);
        mTaggedProfilesListView.setAdapter(mTaggedProfilesListAdapter);

        getTaggedProfilesListData();

    }

    private void getTaggedProfilesListData() {

        String mFilter = "id IN (" + mTaggedProfilesIDStr + ")";

        RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);

    }

    @OnClick(R.id.toolbar_back_img_btn)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
        }
    }

    @OnItemClick(R.id.widget_list_view)
    public void onItemClick(int position) {
        if (mTaggedProfilesList.get(position).getUserID() == mMyProfileResModel.getUserID()) {
            moveMyProfileScreen(this, mMyProfileResModel.getID());
            return;
        }
        moveOtherProfileScreenWithResult(this, mMyProfileResModel.getID(),
                mTaggedProfilesList.get(position).getID(), AppConstants.FOLLOWERS_FOLLOWING_RESULT);

    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        if (responseObj instanceof ProfileModel) {

            ProfileModel mProfileModel = (ProfileModel) responseObj;

            switch (responseType) {

                case RetrofitClient.GET_PROFILE_RESPONSE:

                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        mTaggedProfilesList.clear();
                        mTaggedProfilesList.addAll(mProfileModel.getResource());
                        mTaggedProfilesListAdapter.notifyDataSetChanged();
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

            getTaggedProfilesListData();

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
