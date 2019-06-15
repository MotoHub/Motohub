package online.motohub.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import online.motohub.R;
import online.motohub.adapter.StoryAdapter;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsResModel;
import online.motohub.model.ImageModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SpectatorLiveModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.util.DialogManager;

public class StorySettingActivity extends BaseActivity {

    @BindView(R.id.civ_media)
    CircleImageView mCiv;
    @BindView(R.id.iv_user)
    CircleImageView mUserCiv;
    @BindView(R.id.edit_story)
    EditText mEditStory;

    @BindView(R.id.rv)
    RecyclerView mRV;
    @BindView(R.id.btn_submit)
    Button mSubmit;
    @BindView(R.id.iv_play)
    ImageView mIVPlay;
    @BindView(R.id.toolbar_back_img_btn)
    ImageButton mBackBtn;
    @BindView(R.id.iv_check)
    ImageView mMyStroryChecked;

    @BindView(R.id.image_frame)
    FrameLayout mMediaFrame;

    @BindView(R.id.parent)
    RelativeLayout mParent;
    ArrayList<ProfileResModel> mProfileList;
    private Uri mFileURI;
    private String mFileType;
    private StoryAdapter mStoryAdapter;
    private ArrayList<String> mSelectedIds;

    private boolean isMyStoryChecked = false;
    private EventsResModel mEventResModel;
    private ProfileResModel mMyProfileResModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_setting);
        ButterKnife.bind(this);
        setupUI(mParent);
        initialize();
        setPlayIconVisiblity(mFileType);
        setImageWithGlide(mCiv, mFileURI);
        setImageWithGlide(mUserCiv, mMyProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);
        setUpRecyclerView();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initialize() {
        mBackBtn.setVisibility(View.VISIBLE);
        mFileURI = getIntent().getParcelableExtra("file_uri");
        mFileType = getIntent().getStringExtra("file_type");
        mEventResModel = (EventsResModel) getIntent().getBundleExtra("bundle_data").getSerializable(EventsModel.EVENTS_RES_MODEL);
        //mMyProfileResModel = (ProfileResModel) getIntent().getBundleExtra("bundle_data").getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        //mMyProfileResModel= MotoHub.getApplicationInstance().getmProfileResModel();
        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
    }

    private void setPlayIconVisiblity(String mFileType) {
        if (mFileType.equalsIgnoreCase("image"))
            mIVPlay.setVisibility(View.GONE);
        else
            mIVPlay.setVisibility(View.VISIBLE);
    }

    private void setUpRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(StorySettingActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRV.setLayoutManager(mLayoutManager);
        mProfileList = new ArrayList<>();
        mSelectedIds = new ArrayList<>();
        apiCallToGetFollowings();
    }

    private void apiCallToGetFollowings() {
        String mFilter = APIConstants.ID + " in (1,2,3,4)";
        if (isNetworkConnected(this))
            RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_FOLLOWERS_FOLLOWING_PROFILE_RESPONSE);
        else
            showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
    }

    private void setAdapter() {
        mStoryAdapter = new StoryAdapter(this, mProfileList, mSelectedIds);
        mRV.setAdapter(mStoryAdapter);
        mStoryAdapter.notifyDataSetChanged();
    }

    public void submitBtnVisiblity() {
        if (mSelectedIds.size() == 0 && !isMyStoryChecked)
            mSubmit.setVisibility(View.GONE);
        else
            mSubmit.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.btn_submit, R.id.cb_layout, R.id.image_frame})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cb_layout:
                isMyStoryChecked = !isMyStoryChecked;
                changeCheckBoxState();
                submitBtnVisiblity();
                break;
            case R.id.btn_submit:
                uploadProfilePicture();
                break;
            case R.id.image_frame:

                break;
            default:
                super.onBackPressed();
        }
    }

    private void apiCallToPostSpectatorData(String mFileURL) {
        JsonArray mJsonArray = new JsonArray();
        for (int i = 0; i < mSelectedIds.size(); i++) {
            JsonObject mObject = new JsonObject();
            mObject.addProperty(SpectatorLiveModel.PROFILE_ID, mMyProfileResModel.getID());
            mObject.addProperty(SpectatorLiveModel.CAPTION, mEditStory.getText().toString());
            mObject.addProperty(SpectatorLiveModel.FILEURL, mFileURL);
            mObject.addProperty(SpectatorLiveModel.OTHERPROFILEID, mSelectedIds.get(i));
            mObject.addProperty(SpectatorLiveModel.EVENTID, mEventResModel.getID());
            mObject.addProperty(SpectatorLiveModel.FILETYPE, mFileType.equalsIgnoreCase("image") ? AppConstants.IMAGE : AppConstants.VIDEO);
            mJsonArray.add(mObject);
        }
        //  RetrofitClient.getRetrofitInstance().callToShareSpectatorData(this, mJsonArray, RetrofitClient.SHARE_STORY_RESPONSE_IN_SPECTATOR_LIVE);
    }

    private void uploadProfilePicture() {
        File mFile = new File(mFileURI.getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart =
                MultipartBody.Part.createFormData("files", mFile.getName(), requestBody);
        /*RetrofitClient.getRetrofitInstance().callUploadSpectatorLive(
                this,
                filePart,
                RetrofitClient.UPLOAD_PROFILE_IMAGE_FILE_RESPONSE);*/
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        switch (responseType) {
            case RetrofitClient.GET_FOLLOWERS_FOLLOWING_PROFILE_RESPONSE:
                ProfileModel mProfileModel = (ProfileModel) responseObj;
                mProfileList.addAll(mProfileModel.getResource());
                setAdapter();
                break;
            case RetrofitClient.UPLOAD_PROFILE_IMAGE_FILE_RESPONSE:
                ImageModel mImageModel = (ImageModel) responseObj;
                apiCallToPostSpectatorData(mImageModel.getmModels().get(0).getPath());
                break;
           /* case RetrofitClient.SHARE_STORY_RESPONSE_IN_SPECTATOR_LIVE:
                SpectatorLiveModel mSpectatorLiveStory = (SpectatorLiveModel) responseObj;
                showToast(this, "Shared successfully");
                break;*/
        }
    }

    private void changeCheckBoxState() {
        if (isMyStoryChecked)
            mMyStroryChecked.setImageResource(android.R.drawable.checkbox_on_background);
        else
            mMyStroryChecked.setImageResource(android.R.drawable.checkbox_off_background);
    }
}
