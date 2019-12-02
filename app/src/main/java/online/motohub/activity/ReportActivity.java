package online.motohub.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.ReportAdapter;
import online.motohub.dialog.DialogManager;
import online.motohub.model.PostReportModel;
import online.motohub.model.PostsModel;
import online.motohub.model.ProfileModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.CommonAPI;

public class ReportActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mReportRecyclerView;
    private int mProfileID;
    private int mUserID;
    private int mPostID;
    private String mReportString = "";
    private String mStatus = "";
    private String REPORT_STRING = "";
    private ReportAdapter reportAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        initViews();
        getDataFromBundle();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void getDataFromBundle() {

        if (getIntent().hasExtra(ProfileModel.PROFILE_ID))
            mProfileID = getIntent().getIntExtra(ProfileModel.PROFILE_ID, 0);

        if (getIntent().hasExtra(ProfileModel.USER_ID))
            mUserID = getIntent().getIntExtra(ProfileModel.USER_ID, 0);

        if (getIntent().hasExtra(PostsModel.POST_ID))
            mPostID = getIntent().getIntExtra(PostsModel.POST_ID, 0);

        if (getIntent().hasExtra(AppConstants.REPORT))
            REPORT_STRING = getIntent().getStringExtra(AppConstants.REPORT);

    }

    private void initViews() {
        setToolbar(mToolbar, getString(R.string.report));
        setToolbarLeftBtn(mToolbar);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mReportRecyclerView.setLayoutManager(mLinearLayoutManager);

        reportAdapter = new ReportAdapter(this);
        mReportRecyclerView.setAdapter(reportAdapter);
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.reportBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.reportBtn:
                reportPost();
                break;
        }
    }

    public void setReportString(String reportString) {
        mReportString = reportString;
    }

    private void reportPost() {
        if (mReportString.trim().isEmpty())
            showToast(this, getString(R.string.report_err));
        else
            CommonAPI.getInstance().callPostReportpost(this, mUserID, mProfileID, mPostID, mReportString, mStatus);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof PostReportModel) {
            if (REPORT_STRING.trim().equals(AppConstants.REPORT_POST))
                updatePost();
            else if (REPORT_STRING.trim().equals(AppConstants.REPORT_VIDEO))
                updateVideoPost();
        } else if (responseObj instanceof PostsModel) {
            setResult(RESULT_OK);
            finish();
        } else if (responseType == RetrofitClient.UPDATE_VIDEO_POST_RESPONSE) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void updateVideoPost() {
        JsonObject mJsonObject = new JsonObject();

        mJsonObject.addProperty(PostsModel.REPORT_STATUS, true);
        mJsonObject.addProperty(PostsModel.POST_ID, mPostID);

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mJsonObject);
        RetrofitClient.getRetrofitInstance().callUpdateOnDemandProfilePosts(this, mJsonArray, RetrofitClient.UPDATE_VIDEO_POST_RESPONSE);
    }

    private void updatePost() {
        JsonObject mJsonObject = new JsonObject();

        mJsonObject.addProperty(PostsModel.REPORT_STATUS, true);
        mJsonObject.addProperty(PostsModel.POST_ID, mPostID);

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mJsonObject);
        RetrofitClient.getRetrofitInstance().callUpdateProfilePosts(this, mJsonArray, RetrofitClient.UPDATE_PROFILE_POSTS_RESPONSE);
    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
    }

    @Override
    public void retrofitOnError(int code, String message, int responseType) {
        super.retrofitOnError(code, message, responseType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            assert data.getExtras() != null;
            if (data.hasExtra(AppConstants.REPORT_STRING)) {
                setReportString(data.getStringExtra(AppConstants.REPORT_STRING));
                reportPost();
            }
        }
    }
}
