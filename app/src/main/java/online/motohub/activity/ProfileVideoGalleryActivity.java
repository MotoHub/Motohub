package online.motohub.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.GalleryVideoAdapter;
import online.motohub.model.DeleteProfileImagesResponse;
import online.motohub.model.GalleryVideoModel;
import online.motohub.model.GalleryVideoResModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.ProfileUploadService;
import online.motohub.util.RecyclerClick_Listener;
import online.motohub.util.RecyclerTouchListener;
import online.motohub.util.ToolbarActionModeCallbackVideos;
import online.motohub.util.UrlUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static online.motohub.retrofit.RetrofitClient.DELETE_MY_PROFILE_IMAGE;

public class ProfileVideoGalleryActivity extends BaseActivity {

    public static final String EXTRA_PROFILE = "extra_profile_data";
    public static final String EXTRA_RESULT_DATA = "activity_video_picker_uri";
    private static final String TAG = ProfileVideoGalleryActivity.class.getName();
    @BindView(R.id.video_gallery_parent_view)
    FrameLayout mParentView;
    @BindView(R.id.video_gallery_recycler_view)
    RecyclerView mRv;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.gallery_video_view_pager_lay)
    RelativeLayout mVideoContainer;
    @BindView(R.id.gallery_video_view)
    VideoView mVideoView;
    @BindView(R.id.shimmer_ondemand_events)
    ShimmerFrameLayout mShimmer_myvideos;
    /*final GalleryVideoAdapter.OnItemClickListener onItemClickListener = new GalleryVideoAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(GalleryVideoResModel model, int position) {
            if (model != null) {
                moveLoadVideoScreen(ProfileVideoGalleryActivity.this, model.getVideoUrl());
            }
        }
    };*/
    @BindView(R.id.video_gallery_fab)
    FloatingActionButton mUpdateFAB;

    private int mProfileID;
    BroadcastReceiver broadCastUploadStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String status = intent.getStringExtra("status");
                if (status.equalsIgnoreCase("File Uploaded")) {
                    getVideoDataFromAPi();
                    showToast(getBaseContext(), "Video uploaded successfully");
                } else {
                    showSnackBar(mParentView, status);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.print(e.getMessage());
            }
        }
    };
    private ArrayList<GalleryVideoResModel> videoResModels;
    private GalleryVideoAdapter mAdapter;
    private String mVideoPathUri;
    private ActionMode mActionMode;
    private ArrayList<Integer> deleteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_gallery);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setToolbar(mToolbar, getString(R.string.videos));
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        deleteList = new ArrayList<>();
        mShimmer_myvideos.startShimmerAnimation();
        mRv.setVisibility(View.GONE);
        try {
            mProfileID = getIntent().getIntExtra(EXTRA_PROFILE, 0);
            // mProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        } catch (Exception e) {
            e.printStackTrace();
            // mProfileResModel = null;
        }
        GridLayoutManager layoutManager = new GridLayoutManager(ProfileVideoGalleryActivity.this, 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv.setLayoutManager(layoutManager);
        videoResModels = new ArrayList<>();
        mAdapter = new GalleryVideoAdapter(ProfileVideoGalleryActivity.this, videoResModels);
        mRv.setAdapter(mAdapter);
        //mAdapter.setOnItemClickListener(onItemClickListener);
        if (getIntent().getBooleanExtra(AppConstants.FROM_OTHER_PROFILE, false)) {
            mUpdateFAB.setVisibility(View.GONE);
        }
        initVideoPlayer();
        getVideoDataFromAPi();
        mRv.addOnItemTouchListener(new RecyclerTouchListener(this, mRv, new RecyclerClick_Listener() {
            @Override
            public void onClick(View view, int position) {
                //If ActionMode not null select item
                if (mActionMode != null) {
                    onListItemSelect(position);
                } else {
                    GalleryVideoResModel model = videoResModels.get(position);
                    if (model != null) {
                        moveLoadVideoScreen(ProfileVideoGalleryActivity.this, UrlUtils.AWS_S3_BASE_URL + model.getVideoUrl());
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                //Select item on long click
                onListItemSelect(position);
            }
        }));
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initVideoPlayer() {
        MediaController mMediaController = new MediaController(ProfileVideoGalleryActivity.this);
        mMediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mMediaController);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(this.broadCastUploadStatus);
        if (mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }

    private void getVideoDataFromAPi() {
        String filter = "ProfileID = " + mProfileID;
        RetrofitClient.getRetrofitInstance()
                .callGetVideoGallery(ProfileVideoGalleryActivity.this,
                        filter, RetrofitClient.GET_VIDEO_FILE_RESPONSE);
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.video_gallery_fab, R.id.gallery_video_cancel_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.video_gallery_fab:
                mVideoContainer.setVisibility(View.GONE);
                if (mVideoView != null && mVideoView.isActivated()) {
                    mVideoView.suspend();
                }
                galleryIntentVideo();
                break;
            case R.id.gallery_video_cancel_btn:
                mVideoContainer.setVisibility(View.GONE);
                if (mVideoView != null && mVideoView.isPlaying()) {
                    mVideoView.suspend();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_VIDEO_REQ:
                    mVideoPathUri = data.getStringExtra(EXTRA_RESULT_DATA);
                    if (mVideoPathUri != null) {
                        uploadVideoFile();
                        //   new VideoCompressor().execute(getSelectedVideoPath(), getCompressedVideoPath());
                    } else {
                        showSnackBar(mParentView, getString(R.string.file_not_found));
                    }
                    break;
               /* case AppConstants.ONDEMAND_REQUEST:
                    videoResModels.clear();
                    ArrayList<GalleryVideoResModel> mTempPromoterVideoList = (ArrayList<GalleryVideoResModel>) data.getSerializableExtra(AppConstants.VIDEO_LIST);
                    videoResModels.addAll(mTempPromoterVideoList);
                    break;*/
            }
        }
    }

    private String getSelectedVideoPath() {
        String mPath = "";
        try {
            File videoFile = copiedVideoFile(Uri.parse(mVideoPathUri),
                    GALLERY_VIDEO_NAME_TYPE);
            mPath = String.valueOf(videoFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            mPath = "";
        }
        return mPath;
    }

    private void uploadVideoFile() {
        try {
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mVideoPathUri,
                    MediaStore.Images.Thumbnails.MINI_KIND);
            File imageFile = compressedImgFromBitmap(thumb);
            Intent service_intent = new Intent(this, ProfileUploadService.class);
            service_intent.putExtra(AppConstants.VIDEO_PATH, mVideoPathUri);
            service_intent.putExtra(AppConstants.IMAGE_PATH, String.valueOf(imageFile));
            String destFilePath = Environment.getExternalStorageDirectory().getPath() + getString(R.string.util_app_folder_root_path);
            service_intent.putExtra(AppConstants.PROFILE_ID, mProfileID);
            service_intent.putExtra(AppConstants.DEST_PATH, destFilePath);
            service_intent.putExtra(AppConstants.USER_TYPE, AppConstants.USER);
            service_intent.setAction("ProfileUploadService");
            startService(service_intent);
            //mCompressedVideoPath = "";
            mVideoPathUri = "";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        switch (responseType) {
            case RetrofitClient.GET_VIDEO_FILE_RESPONSE:
                try {
                    GalleryVideoModel videoModel = (GalleryVideoModel) responseObj;
                    if (videoModel != null && videoModel.getResModelList().size() > 0) {
                        videoResModels.clear();
                        videoResModels.addAll(videoModel.getResModelList());
                        mAdapter.notifyDataSetChanged();
                        mRv.setVisibility(View.VISIBLE);
                    } else {
                        showSnackBar(mParentView, getString(R.string.video_not_found));
                        mRv.setVisibility(View.GONE);
                    }
                    mShimmer_myvideos.stopShimmerAnimation();
                    mShimmer_myvideos.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case RetrofitClient.UPDATE_SESSION_RESPONSE:
                SessionModel mSessionModel = (SessionModel) responseObj;
                if (mSessionModel.getSessionToken() == null) {
                    PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
                } else {
                    PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
                }
                getVideoDataFromAPi();
                break;
        }

    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        showSnackBar(mParentView, getString(R.string.internet_failure));
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
        showSnackBar(mParentView, message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(this.broadCastUploadStatus, new IntentFilter("UPLOAD_STATUS"));
    }

    //List item select method
    private void onListItemSelect(int position) {
        mAdapter.toggleSelection(position);//Toggle the selection

        boolean hasCheckedItems = mAdapter.getSelectedCount() > 0;//Check if any items are already selected or not

        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = this.startSupportActionMode(new ToolbarActionModeCallbackVideos(this, mAdapter, videoResModels));
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();

        if (mActionMode != null)
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(mAdapter.getSelectedCount()) + " selected");
    }

    //Set action mode null after use
    public void setNullToActionMode() {
        if (mActionMode != null) {
            mActionMode = null;
        }
    }

    //Delete selected rows
    public void deleteRows() {
        SparseBooleanArray selected = mAdapter.getSelectedIds();//Get selected ids

        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                //If current id is selected remove the item via key
                deleteList.add(videoResModels.get(selected.keyAt(i)).getId());
                videoResModels.remove(selected.keyAt(i));
                mAdapter.notifyDataSetChanged();//notify adapter
            }
        }
        String sd = new Gson().toJson(deleteList);
        JsonArray jsonArray = new JsonParser().parse(sd).getAsJsonArray();
        //JSONArray jsonArray = new JSONArray(deleteList);
        JsonObject finalObject = new JsonObject();
        finalObject.add("ids", jsonArray);
        mActionMode.finish();//Finish action mode after use
        deleteList.clear();
        callDeleteVideosMyProfile(finalObject, DELETE_MY_PROFILE_IMAGE);
    }

    private void callDeleteVideosMyProfile(JsonObject object, final int responseType) {
        DialogManager.showProgress(this);
        // String filter = "UserID=" + userId;
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callDeleteVideoGallery(object)
                .enqueue(new Callback<DeleteProfileImagesResponse>() {
                    @Override
                    public void onResponse(Call<DeleteProfileImagesResponse> call, Response<DeleteProfileImagesResponse> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            Object mResponseObj = response.body();
                            retrofitOnResponse(mResponseObj, responseType);
                        } else {
                            retrofitOnError(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<DeleteProfileImagesResponse> call, Throwable t) {
                        DialogManager.hideProgress();
                        retrofitOnFailure();
                    }
                });
    }

    class VideoCompressor extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogManager.showProgress(ProfileVideoGalleryActivity.this);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return com.yovenny.videocompress.MediaController.getInstance().convertVideo(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            DialogManager.hideProgress();
            if (compressed) {
                showToast(ProfileVideoGalleryActivity.this, getString(R.string.uploading_video));
                uploadVideoFile();
            }
        }
    }

}