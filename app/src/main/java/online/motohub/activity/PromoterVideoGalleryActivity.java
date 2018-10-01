package online.motohub.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.promoter.PromoterVideoPostAdapter;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.OnLoadMoreListener;
import online.motohub.model.PostsModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PromoterVideoModel;
import online.motohub.model.SessionModel;
import online.motohub.model.VideoCommentsModel;
import online.motohub.model.VideoLikesModel;
import online.motohub.model.VideoShareModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.ProfileUploadService;
import online.motohub.util.Utility;

public class PromoterVideoGalleryActivity extends BaseActivity {

    public static final String EXTRA_RESULT_DATA = "activity_video_picker_uri";
    @BindView(R.id.video_gallery_parent_view)
    FrameLayout mParentView;
    @BindView(R.id.video_gallery_recycler_view)
    RecyclerView mVideoListView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.gallery_video_view_pager_lay)
    RelativeLayout mVideoContainer;
    @BindView(R.id.gallery_video_view)
    VideoView mVideoView;
    @BindView(R.id.search_edt)
    EditText mSearchEdt;
    @BindView(R.id.search_lay)
    LinearLayout mSearchLLay;
    @BindView(R.id.video_gallery_fab)
    FloatingActionButton mUploadVideoBtn;
    private int mCurrentProfileID = 0, mEventId = 0;
    private String mNewSharedID;
    private VideoShareModel mSharedFeed;
    private int mCurrentPostPosition;
    private ProfileResModel mMyProfileResModel;
    private PromoterVideoPostAdapter mAdapter;
    private ArrayList<PromoterVideoModel.Resource> mPromoterVideoList;
    private String mVideoPathUri;
    private String mEventType = "UserID";
    private String mFilter = "(EventFinishDate<" + getCurrentDate() + ") AND (UserType != user) AND (" + mEventType + "= " + mEventId + ")";

    private LinearLayoutManager mListLayoutManager;
    private int mOffset = 0;
    private boolean mIsLoadMore = false;
    BroadcastReceiver broadCastUploadStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String status = intent.getStringExtra("status");
                if (status.equalsIgnoreCase("File Uploaded")) {
                    mIsLoadMore = false;
                    mPromoterVideoList.clear();
                    mOffset = 0;
                    getVideoDataFromAPi(mFilter);
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
    private boolean isLoading;
    private int mFeedTotalCount = 0;
    private File videoFile;
    private String mSearchStr = "";
    private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            mIsLoadMore = true;
            mPromoterVideoList.add(null);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyItemInserted(mPromoterVideoList.size() - 1);
                }
            });
            mOffset = mPromoterVideoList.size() - 1;
            //Show loading item
            if (mSearchStr.isEmpty()) {
                getVideoDataFromAPi(mFilter);
            } else {
                getSearchDataFromAPi(mFilter);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_gallery);
        ButterKnife.bind(this);
        initView();
        setupUI(mParentView);
    }

    private void initView() {
        mOffset = 0;
        setToolbar(mToolbar, getString(R.string.on_demand));
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        mListLayoutManager = new LinearLayoutManager(PromoterVideoGalleryActivity.this);
        mListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mVideoListView.setLayoutManager(mListLayoutManager);

        mPromoterVideoList = new ArrayList<>();
        //mSearchLLay.setVisibility(View.VISIBLE);
        initVideoPlayer();

        mVideoListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int mVisibleItemCount = mListLayoutManager.getChildCount();
                int mTotalItemCount = mListLayoutManager.getItemCount();
                int mFirstVisibleItemPosition = mListLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (mOffset < mFeedTotalCount)) {
                    if ((mVisibleItemCount + mFirstVisibleItemPosition) >= mTotalItemCount
                            && mFirstVisibleItemPosition >= 0) {
                        isLoading = true;
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }

                    }
                }
            }
        });
        if (getIntent().getExtras().getBoolean(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY)) {
            try {
                JSONObject mJsonObjectEntry = new JSONObject(getIntent().getExtras().getString(MyFireBaseMessagingService.ENTRY_JSON_OBJ));
                JSONObject mDetailsObj = mJsonObjectEntry.getJSONObject("Details");
                mFilter = "(" + mFilter + ") AND (ID = " + mDetailsObj.getInt("VideoID") + ")";
                mCurrentProfileID = Integer.parseInt(mDetailsObj.getString("ProfileID"));
                mUploadVideoBtn.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //mFilter = "(EventFinishDate<" + getCurrentDate() + ") AND " + "(UserType != user)" +;
            try {
                Bundle mBundle = getIntent().getExtras();
                if (mBundle != null) {
                    mCurrentProfileID = mBundle.getInt(AppConstants.PROFILE_ID, 0);
                    mEventId = mBundle.getInt("ID", 0);
                    mEventType = mBundle.getString("TYPE", null);
                }
                //mFilter = "(EventFinishDate<" + getCurrentDate() + ") AND " + "(UserType != user) AND (UserID =" + ProfileID + ")";
                if (mEventType.equals("EventID")) {
                    mFilter = mEventType + " = " + mEventId;
                } /*else {
                    mFilter = "(EventFinishDate<" + getCurrentDate() + ") AND (UserType != user) AND (" + mEventType + "= " + mEventId + ")";
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mUploadVideoBtn.setVisibility(View.GONE);
        getMyProfiles();
        mSearchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchOnDemand(s.toString());
            }
        });
    }

    private void searchOnDemand(String searchStr) {
        mOffset = 0;
        mFeedTotalCount = 0;
        mIsLoadMore = false;
        mPromoterVideoList.clear();
        setAdapter();
        mSearchStr = searchStr;
        if (mSearchStr.isEmpty()) {
            //mFilter = "(EventFinishDate<" + getCurrentDate() + ") AND " + "(UserType != user)";
            getSearchDataFromAPi(mFilter);
        } else {
            mPromoterVideoList.add(null);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyItemInserted(mPromoterVideoList.size() - 1);
                }
            });
            mFilter = "(EventFinishDate<" + getCurrentDate() + ") AND " + "(UserType != user) AND (Caption LIKE '%" + mSearchStr + "%')";
            getSearchDataFromAPi(mFilter);
        }
    }

    private void setAdapter() {
        isLoading = false;
        if (mAdapter == null) {
            mAdapter = new PromoterVideoPostAdapter(mPromoterVideoList, mMyProfileResModel, PromoterVideoGalleryActivity.this, mEventId);
            mVideoListView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void getMyProfiles() {
        String mFilter = "ID=" + mCurrentProfileID;
        if (isNetworkConnected())
            RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);
        else
            showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
    }

    private void getVideoDataFromAPi(String mFilter) {
        RetrofitClient.getRetrofitInstance()
                .callGetPromotersGallery(PromoterVideoGalleryActivity.this, mFilter, RetrofitClient.GET_VIDEO_FILE_RESPONSE, mOffset, mIsLoadMore);
    }

    private void getSearchDataFromAPi(String mFilter) {
        RetrofitClient.getRetrofitInstance()
                .callGetPromotersGallery(PromoterVideoGalleryActivity.this, mFilter, RetrofitClient.GET_SEARCH_VIDEO_FILE_RESPONSE, mOffset);
    }

    private void initVideoPlayer() {
        MediaController mMediaController = new MediaController(PromoterVideoGalleryActivity.this);
        mMediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mMediaController);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(this.broadCastUploadStatus, new IntentFilter("UPLOAD_STATUS"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(this.broadCastUploadStatus);
        if (mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.gallery_video_cancel_btn, R.id.video_gallery_fab})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.gallery_video_cancel_btn:
                mVideoContainer.setVisibility(View.GONE);
                if (mVideoView != null && mVideoView.isPlaying()) {
                    mVideoView.suspend();
                }
                break;
            case R.id.video_gallery_fab:
                mVideoContainer.setVisibility(View.GONE);
                if (mVideoView != null && mVideoView.isActivated()) {
                    mVideoView.suspend();
                }
                galleryIntentVideo1();
                break;
        }
    }

    private String getSelectedVideoPath() {

        String mPath = "";
        try {
            videoFile = copiedVideoFile(Uri.parse(mVideoPathUri),
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
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mVideoPathUri, MediaStore.Images.Thumbnails.MINI_KIND);
            File imageFile = compressedImgFromBitmap(thumb);
            Intent service_intent = new Intent(this, ProfileUploadService.class);
            service_intent.putExtra(AppConstants.VIDEO_PATH, mCompressedVideoPath);
            service_intent.putExtra(AppConstants.IMAGE_PATH, String.valueOf(imageFile));
            String destFilePath = Environment.getExternalStorageDirectory().getPath() + getString(R.string.util_app_folder_root_path);
            service_intent.putExtra(AppConstants.PROFILE_ID, mMyProfileResModel.getID());
            service_intent.putExtra(AppConstants.DEST_PATH, destFilePath);
            service_intent.putExtra(AppConstants.USER_TYPE, AppConstants.ONDEMAND);
            service_intent.setAction("ProfileUploadService");
            startService(service_intent);
            mCompressedVideoPath = "";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        switch (responseType) {
            case RetrofitClient.GET_PROFILE_RESPONSE:
                if (responseObj instanceof ProfileModel) {
                    ProfileModel mProfileModel = (ProfileModel) responseObj;
                    if (mProfileModel.getResource().size() > 0) {
                        mMyProfileResModel = mProfileModel.getResource().get(0);
                        getVideoDataFromAPi(mFilter);
                    } else {
                        showSnackBar(mParentView, mNoProfileErr);
                    }
                }
                break;
            case RetrofitClient.UPDATE_SESSION_RESPONSE:
                SessionModel mSessionModel = (SessionModel) responseObj;
                if (mSessionModel.getSessionToken() == null) {
                    PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
                } else {
                    PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
                }
                getMyProfiles();
                break;
            case RetrofitClient.GET_VIDEO_FILE_RESPONSE:
                PromoterVideoModel mResponse = (PromoterVideoModel) responseObj;
                if (mIsLoadMore) {
                    hideLoading();
                    if (mResponse != null && mResponse.getResource().size() > 0) {
                        mPromoterVideoList.addAll(mResponse.getResource());
                    }
                } else {
                    mPromoterVideoList.clear();
                    mFeedTotalCount = mResponse.getMeta().getCount();
                    if (mResponse != null && mResponse.getResource().size() > 0) {
                        mPromoterVideoList.addAll(mResponse.getResource());
                    } else {
                        showSnackBar(mParentView, getString(R.string.video_not_found));
                    }
                }
                mOffset = mPromoterVideoList.size();
                setAdapter();
                break;
            case RetrofitClient.GET_SEARCH_VIDEO_FILE_RESPONSE:
                PromoterVideoModel mResponse1 = (PromoterVideoModel) responseObj;
                if (mIsLoadMore) {
                    hideLoading();
                    if (mResponse1 != null && mResponse1.getResource().size() > 0) {
                        mPromoterVideoList.addAll(mResponse1.getResource());
                    }
                } else {
                    mPromoterVideoList.clear();
                    mFeedTotalCount = mResponse1.getMeta().getCount();
                    if (mResponse1 != null && mResponse1.getResource().size() > 0) {
                        mPromoterVideoList.addAll(mResponse1.getResource());
                    } else {
                        showSnackBar(mParentView, getString(R.string.video_not_found));
                    }
                }
                mOffset = mPromoterVideoList.size();
                setAdapter();
                break;

            case RetrofitClient.VIDEO_LIKES:
                VideoLikesModel mFeedLikesList = (VideoLikesModel) responseObj;
                ArrayList<VideoLikesModel> mNewFeedLike = mFeedLikesList.getResource();
                mAdapter.resetLikeAdapter(mNewFeedLike.get(0));
                break;
            case RetrofitClient.VIDEO_UNLIKE:
                VideoLikesModel mFeedLikesList1 = (VideoLikesModel) responseObj;
                ArrayList<VideoLikesModel> mNewFeedLike1 = mFeedLikesList1.getResource();
                mAdapter.resetDisLike(mNewFeedLike1.get(0));
                break;

            case RetrofitClient.VIDEO_SHARES:
                VideoShareModel mFeedShareList = (VideoShareModel) responseObj;
                ArrayList<VideoShareModel> mNewFeedShare = mFeedShareList.getResource();
                mSharedFeed = mNewFeedShare.get(0);
                callAddVideoSharedPost();
                break;
            case RetrofitClient.DELETE_SHARED_POST_RESPONSE:
                break;

            case RetrofitClient.SHARED_POST_RESPONSE:
                PostsModel mPostsModel = (PostsModel) responseObj;
                if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                    mAdapter.resetShareCount(mSharedFeed);
                    showSnackBar(mParentView, getResources().getString(R.string.post_shared));
                }
                break;


        }
    }

    private void hideLoading() {
        mIsLoadMore = false;
        mPromoterVideoList.remove(mPromoterVideoList.size() - 1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemRemoved(mPromoterVideoList.size());
            }
        });
        isLoading = false;
    }

    private void callAddVideoSharedPost() {
        JsonObject mJsonObject = new JsonObject();

        mJsonObject.addProperty(PostsModel.PROFILE_ID, String.valueOf(mMyProfileResModel.getID()));
        mJsonObject.addProperty(PostsModel.OLD_POST_ID, mPromoterVideoList.get(mCurrentPostPosition).getID());
        mJsonObject.addProperty(PostsModel.WHO_POSTED_PROFILE_ID, mPromoterVideoList.get(mCurrentPostPosition).getUserID());
        mJsonObject.addProperty(PostsModel.WHO_POSTED_USER_ID, mPromoterVideoList.get(mCurrentPostPosition).getUserID());
        mJsonObject.addProperty(PostsModel.POST_TEXT, mPromoterVideoList.get(mCurrentPostPosition).getCaption());
        mJsonObject.addProperty(PostsModel.CREATED_AT, mPromoterVideoList.get(mCurrentPostPosition).getCreatedAt());
        mJsonObject.addProperty(PostsModel.IS_NEWS_FEED_POST, false);
        mJsonObject.addProperty(PostsModel.SHARED_PROFILE_ID, Utility.getInstance().
                getMyFollowersFollowingsID(mMyProfileResModel.getFollowprofile_by_ProfileID(), false));
        mJsonObject.addProperty(PostsModel.WHO_SHARED_PROFILE_ID, mMyProfileResModel.getID());
        mJsonObject.addProperty(PostsModel.NEW_SHARED_POST_ID, mNewSharedID);
        mJsonObject.addProperty(PostsModel.PostVideoThumbnailurl, mPromoterVideoList.get(mCurrentPostPosition).getThumbnail());
        mJsonObject.addProperty(PostsModel.PostVideoURL, mPromoterVideoList.get(mCurrentPostPosition).getVideoUrl());
        if (mPromoterVideoList.get(mCurrentPostPosition).getUserType().trim().equals(AppConstants.ONDEMAND))
            mJsonObject.addProperty(PostsModel.USER_TYPE, AppConstants.USER_VIDEO_SHARED_POST);
        else
            mJsonObject.addProperty(PostsModel.USER_TYPE, AppConstants.VIDEO_SHARED_POST);
        mJsonObject.addProperty(PostsModel.USER_TYPE, AppConstants.VIDEO_SHARED_POST);

        final JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mJsonObject);

        RetrofitClient.getRetrofitInstance().callCreateProfilePosts(this, mJsonArray, RetrofitClient.SHARED_POST_RESPONSE);


    }

    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String dialogType, StringBuilder profileTypesStr, ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);
        switch (dialogType) {
            case AppDialogFragment.BOTTOM_SHARE_DIALOG:
                mCurrentPostPosition = position;
                callPostShare(position);
                break;

        }
    }

    private void callPostShare(int position) {

        mCurrentPostPosition = position;

        JsonObject mJsonObject = new JsonObject();
        mNewSharedID = mPromoterVideoList.get(position).getID() + "_" + mMyProfileResModel.getID();
        mJsonObject.addProperty("VideoID", mNewSharedID);
        mJsonObject.addProperty("ProfileID", (mMyProfileResModel.getID()));
        if (mPromoterVideoList.get(mCurrentPostPosition).getUserType().trim().equals(AppConstants.ONDEMAND))
            mJsonObject.addProperty(PostsModel.USER_TYPE, AppConstants.USER_VIDEO_SHARED_POST);
        else
            mJsonObject.addProperty(PostsModel.USER_TYPE, AppConstants.VIDEO_SHARED_POST);
        mJsonObject.addProperty("VideoOwnerID", String.valueOf(mPromoterVideoList.get(position).getProfileID()));
        mJsonObject.addProperty("SharedAt", mPromoterVideoList.get(position).getCreatedAt());
        mJsonObject.addProperty("OriginalVideoID", mPromoterVideoList.get(position).getID());

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mJsonObject);
        JsonObject mResJsonObject = new JsonObject();
        mResJsonObject.add("resource", mJsonArray);
        RetrofitClient.getRetrofitInstance().callPostVideoShares(this, mResJsonObject, RetrofitClient.VIDEO_SHARES);
    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        if (mIsLoadMore) {
            hideLoading();
        } else {
            showSnackBar(mParentView, getString(R.string.internet_failure));
        }
        RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);

    }

    @Override
    public void retrofitOnError(int code, String message, int responseType) {
        super.retrofitOnError(code, message, responseType);
        if (mIsLoadMore) {
            hideLoading();
        } else if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else if (responseType == 102) {
            showSnackBar(mParentView, getResources().getString(R.string.all_ready_post_shared));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_VIDEO_REQ:
                    mVideoPathUri = data.getStringExtra(EXTRA_RESULT_DATA);
                    if (mVideoPathUri != null) {
                        new VideoCompressor().execute(getSelectedVideoPath(), getCompressedVideoPath());
                    } else {
                        showSnackBar(mParentView, getString(R.string.file_not_found));
                    }
                    break;
                case AppConstants.VIDEO_COMMENT_REQUEST:
                    assert data.getExtras() != null;
                    ArrayList<VideoCommentsModel> mFeedCommentModel = (ArrayList<VideoCommentsModel>) data.getExtras().getSerializable(PostsModel.COMMENTS_BY_POSTID);
                    mAdapter.refreshCommentList(mFeedCommentModel);
                    break;
            }
        }
    }

    public String getPathVideo(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    @SuppressLint("StaticFieldLeak")
    class VideoCompressor extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogManager.showProgress(PromoterVideoGalleryActivity.this);
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
                //showToast(PromoterVideoGalleryActivity.this, getString(R.string.uploading_video));
                //uploadVideoFile();
                //Intent intent = new Intent(PromoterVideoGalleryActivity.this, VideoStoryPreviewActivity.class);
                Intent intent = new Intent(PromoterVideoGalleryActivity.this, VideoPreviewOnDemandActivity.class);
                intent.putExtra("file_uri", Uri.fromFile(videoFile));
                intent.putExtra("mVideoPathUri", mVideoPathUri);
                intent.putExtra("bundle_data", mMyProfileResModel);
                startActivity(intent);
            }
        }
    }

}
