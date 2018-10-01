package online.motohub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.ReportAdapter;
import online.motohub.model.PostReportModel;
import online.motohub.model.PostsModel;
import online.motohub.model.ProfileModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        initViews();
        getDataFromBundle();
    }

    private void getDataFromBundle() {

        if(getIntent().hasExtra(ProfileModel.PROFILE_ID))
            mProfileID = getIntent().getIntExtra(ProfileModel.PROFILE_ID,0);

        if(getIntent().hasExtra(ProfileModel.USER_ID))
            mUserID = getIntent().getIntExtra(ProfileModel.USER_ID,0);

        if(getIntent().hasExtra(PostsModel.POST_ID))
            mPostID = getIntent().getIntExtra(PostsModel.POST_ID,0);

    }

    private void initViews() {
        setToolbar(mToolbar,getString(R.string.report));
        setToolbarLeftBtn(mToolbar);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mReportRecyclerView.setLayoutManager(mLinearLayoutManager);

        ReportAdapter reportAdapter;
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

    public void setReportString(String reportString){
        mReportString = reportString;
    }

    private void reportPost() {
        CommonAPI.getInstance().callPostReportpost(this, mUserID, mProfileID, mPostID, mReportString, mStatus);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if(responseObj instanceof PostReportModel){
            updatePost();
        } else if(responseObj instanceof PostsModel){
            finish();
        }
    }

    private void updatePost() {
        JsonObject mJsonObject = new JsonObject();

        mJsonObject.addProperty(PostsModel.REPORT_STATUS, true);
        mJsonObject.addProperty(PostsModel.POST_ID, mPostID);

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mJsonObject);
        RetrofitClient.getRetrofitInstance().callUpdateProfilePosts(this, mJsonArray,RetrofitClient.UPDATE_PROFILE_POSTS_RESPONSE);
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
        if(resultCode == RESULT_OK){
            assert data.getExtras() != null;
            if(data.hasExtra(AppConstants.REPORT_STRING)){
                Intent resultIntent = new Intent();
                resultIntent.putExtra(AppConstants.REPORT_STRING,data.getStringExtra(AppConstants.REPORT_STRING));
                setResult(RESULT_OK, resultIntent);
                setReportString(data.getStringExtra(AppConstants.REPORT_STRING));
                reportPost();
            }

        }
    }
}
