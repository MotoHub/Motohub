package online.motohub.fragment.ondemand;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jakewharton.rxbinding.widget.RxTextView;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.VideoPreviewOnDemandActivity;
import online.motohub.adapter.ondemand.PromoterOrUserAdapter;
import online.motohub.application.MotoHub;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.BaseFragment;
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
import online.motohub.util.PreferenceUtils;
import online.motohub.util.Utility;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * Created by pickzy01 on 30/05/2018.
 */

public class PromoterOrUserFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String EXTRA_RESULT_DATA = "activity_video_picker_uri";
    // TAG for logging;
    private static final String TAG = "PromoterOrUserFragment";
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;
    Unbinder unbinder;
    @BindView(R.id.video_gallery_fab)
    FloatingActionButton videoGalleryFab;
    @BindView(R.id.patent)
    RelativeLayout patent;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.search_edt)
    EditText search_edt;
    @BindView(R.id.shimmer_ondemand_promoters)
    ShimmerFrameLayout mShimmer_ondemand_promoters;
    private int mCurrentProfileID = 0, ProfileID = 0;
    private String mNewSharedID;
    private VideoShareModel mSharedFeed;
    private int mCurrentPostPosition;
    private ProfileResModel mMyProfileResModel;
    private PromoterOrUserAdapter mAdapter;
    private ArrayList<PromoterVideoModel.Resource> mPromoterVideoList;
    private String mVideoPathUri;
    private String mFilter = "(UserType != usereventvideos) AND (ReportStatus == 0)";
    private boolean mIsLoadMore = false;
    private boolean isLoading;
    private int mFeedTotalCount = 0;
    private File videoFile;
    private String mSearchStr = "";
    private Timer timer;

    private rx.Subscription subscription;

    private int mOffset = 0;
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
                    showToast(getActivity(), "Video uploaded successfully");
                } else {
                    showSnackBar(patent, status);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.print(e.getMessage());
            }
        }
    };

    private LinearLayoutManager layoutManager;

    private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            mIsLoadMore = true;
            mPromoterVideoList.add(null);
            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View obj = inflater.inflate(R.layout.promoter_or_user_fragment, container, false);
        unbinder = ButterKnife.bind(this, obj);
        initView();
        setupUI(patent);
        return obj;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Get the data from any transfer's that have already happened,
        Objects.requireNonNull(getActivity()).registerReceiver(this.broadCastUploadStatus, new IntentFilter("UPLOAD_STATUS"));

    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(getActivity()).unregisterReceiver(this.broadCastUploadStatus);
    }

    private void initView() {

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        mShimmer_ondemand_promoters.startShimmerAnimation();

        mCurrentProfileID = MotoHub.getApplicationInstance().getProfileId();
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(R.color.colorOrange,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
            }
        });
        mPromoterVideoList = new ArrayList<>();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int mVisibleItemCount = layoutManager.getChildCount();
                int mTotalItemCount = layoutManager.getItemCount();
                int mFirstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (mOffset < mFeedTotalCount)) {
                    if ((mVisibleItemCount + mFirstVisibleItemPosition) >= mTotalItemCount - 2
                            && mFirstVisibleItemPosition >= 0) {
                        isLoading = true;
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }

                    }
                }
            }
        });
        if (getActivity().getIntent().getExtras().getBoolean(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY)) {
            try {
                JSONObject mJsonObjectEntry = new JSONObject(getActivity().getIntent().getExtras().getString(MyFireBaseMessagingService.ENTRY_JSON_OBJ));
                JSONObject mDetailsObj = mJsonObjectEntry.getJSONObject("Details");
                mFilter = "(" + mFilter + ") AND (ID = " + mDetailsObj.getInt("VideoID") + ") AND (ReportStatus == 0)";
                mCurrentProfileID = Integer.parseInt(mDetailsObj.getString("ProfileID"));
                videoGalleryFab.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        subscription = RxTextView.textChanges(search_edt)
                .skip(1)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.computation())
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        SystemClock.sleep(1000); // Simulate the heavy stuff.
                        return true;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CharSequence>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "Error.", e);
                    }

                    @Override
                    public void onNext(CharSequence charSequence) {
                        String mSearchStrTemp = charSequence.toString();
                        searchOnDemand(mSearchStrTemp);
                    }
                });

        getMyProfiles();
    }

    private void searchOnDemand(String searchStr) {
        try {
            mOffset = 0;
            mFeedTotalCount = 0;
            mIsLoadMore = false;
            mPromoterVideoList.clear();
            String text = null;
            try {
                text = URLEncoder.encode(searchStr, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mSearchStr = text;
            setAdapter();
            if (mSearchStr.isEmpty()) {
                mPromoterVideoList.add(null);
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyItemInserted(mPromoterVideoList.size() - 1);
                    }
                });
                mFilter = "(UserType != usereventvideos) AND (ReportStatus == 0)";
                getSearchDataFromAPi(mFilter);
            } else {
                mPromoterVideoList.add(null);
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyItemInserted(mPromoterVideoList.size() - 1);
                    }
                });
                mFilter = "(UserType != usereventvideos) AND (ReportStatus == 0) AND (Caption LIKE '%" + mSearchStr + "%')";
                getSearchDataFromAPi(mFilter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getMyProfiles() {
        try {
            String mFilter = "ID=" + mCurrentProfileID;
            if (isNetworkConnected())
                RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);
            else {
                showSnackBar(patent, getActivity().getResources().getString(R.string.internet_err));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getVideoDataFromAPi(String mFilter) {
        RetrofitClient.getRetrofitInstance().callGetPromotersGallery(this, mFilter, RetrofitClient.GET_VIDEO_FILE_RESPONSE, mOffset, mIsLoadMore);
    }

    private void getSearchDataFromAPi(String mFilter) {
        RetrofitClient.getRetrofitInstance().callGetPromotersGallery(this, mFilter, RetrofitClient.GET_SEARCH_VIDEO_FILE_RESPONSE, mOffset);
    }

    @Override
    public void onRefresh() {
        mOffset = 0;
        mPromoterVideoList.clear();
        recyclerView.getRecycledViewPool().clear();
        search_edt.setText(null);
        getMyProfiles();
    }

    @OnClick({R.id.video_gallery_fab})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_gallery_fab:
                assert getActivity() != null;
                ((BaseActivity) getActivity()).showAppDialog(AppDialogFragment.BOTTOM_ADD_VIDEO_DIALOG, null);
                break;
        }
    }


    private void setAdapter() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Do whatever you want
                    try {
                        isLoading = false;
                        if (mAdapter == null && mMyProfileResModel.getID() != 0) {
                            mAdapter = new PromoterOrUserAdapter(mPromoterVideoList, mMyProfileResModel, getActivity(), ProfileID, PromoterOrUserFragment.this);
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case BaseActivity.ACTION_TAKE_VIDEO:
                    Uri videoUri = data.getData();
                    // mVideoPathUri = data.getStringExtra(EXTRA_RESULT_DATA);
                    if (videoUri != null) {
                        assert getActivity() != null;
                        mVideoPathUri = ((BaseActivity) getActivity()).getRealPathFromURI(videoUri);
                        getSelectedVideoPath();
                        startVideoPreviewOnDemandActivity();
                    } else {
                        showSnackBar(patent, getString(R.string.file_not_found));
                    }
                    break;

                case BaseActivity.GALLERY_VIDEO_REQ:
                    mVideoPathUri = data.getStringExtra(EXTRA_RESULT_DATA);
                    if (mVideoPathUri != null) {
                        //new VideoCompressor().execute(getSelectedVideoPath(), getCompressedVideoPath());
                        getSelectedVideoPath();
                        startVideoPreviewOnDemandActivity();
                    } else {
                        showSnackBar(patent, getString(R.string.file_not_found));
                    }

                    break;
                case AppConstants.VIDEO_COMMENT_REQUEST:
                    assert data.getExtras() != null;
                    ArrayList<VideoCommentsModel> mFeedCommentModel = (ArrayList<VideoCommentsModel>) data.getExtras().getSerializable(PostsModel.COMMENTS_BY_POSTID);
                    if (mFeedCommentModel.size() > 0)
                        mAdapter.refreshCommentList(mFeedCommentModel);
                    break;

                case AppConstants.ONDEMAND_REQUEST:
                    assert data.getExtras() != null;
                    if (mPromoterVideoList != null) {
                        mPromoterVideoList.clear();
                        ArrayList<PromoterVideoModel.Resource> mTempPromoterVideoList = (ArrayList<PromoterVideoModel.Resource>) data.getExtras().getSerializable(AppConstants.VIDEO_LIST);
                        if (mTempPromoterVideoList.size() > 0) {
                            mPromoterVideoList.addAll(mTempPromoterVideoList);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case AppConstants.REPORT_POST_SUCCESS:
                    //TODO remove the reported post
                    getMyProfiles();
                    break;
            }
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

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        switch (responseType) {
            case RetrofitClient.GET_PROFILE_RESPONSE:
                if (responseObj instanceof ProfileModel) {
                    swipeContainer.setRefreshing(false);
                    ProfileModel mProfileModel = (ProfileModel) responseObj;
                    if (mProfileModel.getResource().size() > 0) {
                        mMyProfileResModel = mProfileModel.getResource().get(0);
                        if (mMyProfileResModel.getID() != 0)
                            getVideoDataFromAPi(mFilter);
                    } else {
                        showSnackBar(patent, getActivity().getResources().getString(R.string.no_profile_found_err));
                    }
                }
                break;
            case RetrofitClient.UPDATE_SESSION_RESPONSE:
                SessionModel mSessionModel = (SessionModel) responseObj;
                if (mSessionModel.getSessionToken() == null) {
                    PreferenceUtils.getInstance(getActivity()).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
                } else {
                    PreferenceUtils.getInstance(getActivity()).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
                }
                getMyProfiles();
                break;
            case RetrofitClient.GET_VIDEO_FILE_RESPONSE:
                try {
                    PromoterVideoModel mResponse = (PromoterVideoModel) responseObj;
                    String data = new Gson().toJson(mResponse);
                    if (mIsLoadMore) {
                        hideLoading();
                        if (mResponse != null && mResponse.getResource().size() > 0) {
                            mPromoterVideoList.addAll(mResponse.getResource());
                            mOffset = mPromoterVideoList.size();
                            setAdapter();
                            mOffset = mPromoterVideoList.size();
                            mShimmer_ondemand_promoters.stopShimmerAnimation();
                            mShimmer_ondemand_promoters.setVisibility(View.GONE);
                        }
                    } else {
                        mPromoterVideoList.clear();
                        mFeedTotalCount = mResponse.getMeta().getCount();
                        if (mResponse.getResource().size() > 0) {
                            mPromoterVideoList.addAll(mResponse.getResource());
                        } else {
                            showSnackBar(patent, getString(R.string.video_not_found));
                        }
                        mOffset = mPromoterVideoList.size();
                        try {
                            mShimmer_ondemand_promoters.stopShimmerAnimation();
                            mShimmer_ondemand_promoters.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        setAdapter();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case RetrofitClient.GET_SEARCH_VIDEO_FILE_RESPONSE:
                PromoterVideoModel mResponse1 = (PromoterVideoModel) responseObj;
                if (mIsLoadMore) {
                    hideLoading();
                    if (mResponse1 != null && mResponse1.getResource().size() > 0) {
                        mPromoterVideoList.addAll(mResponse1.getResource());
                        mOffset = mPromoterVideoList.size();
                        setAdapter();
                    }
                } else {
                    mPromoterVideoList.clear();
                    //recyclerView.getRecycledViewPool().clear();
                    mFeedTotalCount = mResponse1.getMeta().getCount();
                    if (mResponse1 != null && mResponse1.getResource().size() > 0) {
                        mPromoterVideoList.addAll(mResponse1.getResource());
                        mOffset = mPromoterVideoList.size();
                        setAdapter();
                    } else {
                        showSnackBar(patent, getString(R.string.video_not_found));
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
                if (mNewFeedShare.size() > 0) {
                    mSharedFeed = mNewFeedShare.get(0);
                    callAddVideoSharedPost();
                }
                break;
            case RetrofitClient.DELETE_SHARED_POST_RESPONSE:
                break;

            case RetrofitClient.SHARED_POST_RESPONSE:
                PostsModel mPostsModel = (PostsModel) responseObj;
                if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                    mAdapter.resetShareCount(mSharedFeed);
                    showSnackBar(patent, getResources().getString(R.string.post_shared));
                }
                break;
        }
    }

    private void hideLoading() {
        try {
            mIsLoadMore = false;
            mPromoterVideoList.remove(mPromoterVideoList.size() - 1);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyItemRemoved(mPromoterVideoList.size());
                }
            });
            isLoading = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callAddVideoSharedPost() {
        JsonObject mJsonObject = new JsonObject();

        mJsonObject.addProperty(PostsModel.PROFILE_ID, String.valueOf(mMyProfileResModel.getID()));
        mJsonObject.addProperty(PostsModel.OLD_POST_ID, mPromoterVideoList.get(mCurrentPostPosition).getID());
        mJsonObject.addProperty(PostsModel.WHO_POSTED_PROFILE_ID, mPromoterVideoList.get(mCurrentPostPosition).getProfileID());
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
        if (mPromoterVideoList.get(mCurrentPostPosition).getUserType().trim().equals(AppConstants.ONDEMAND) || mPromoterVideoList.get(mCurrentPostPosition).getUserType().trim().equals(AppConstants.USER_EVENT_VIDEOS) || mPromoterVideoList.get(mCurrentPostPosition).getUserType().trim().equals(AppConstants.USER))
            mJsonObject.addProperty(PostsModel.USER_TYPE, AppConstants.USER_VIDEO_SHARED_POST);
        else
            mJsonObject.addProperty(PostsModel.USER_TYPE, AppConstants.VIDEO_SHARED_POST);
        final JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mJsonObject);

        RetrofitClient.getRetrofitInstance().callCreateProfilePosts(this, mJsonArray, RetrofitClient.SHARED_POST_RESPONSE);
    }

    @Override
    public void alertDialogPositiveBtnClick(String dialogType, int position) {
        super.alertDialogPositiveBtnClick(dialogType, position);
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
            showSnackBar(patent, getString(R.string.internet_failure));
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
            showSnackBar(patent, getResources().getString(R.string.all_ready_post_shared));
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(patent, mErrorMsg);
        }
    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
        showSnackBar(patent, message);
    }

    void startVideoPreviewOnDemandActivity() {
        Intent intent = new Intent(getActivity(), VideoPreviewOnDemandActivity.class);
        intent.putExtra("file_uri", Uri.fromFile(videoFile));
        intent.putExtra("mVideoPathUri", mVideoPathUri);
        intent.putExtra("bundle_data", mMyProfileResModel);
        startActivity(intent);
    }

    /*@SuppressLint("StaticFieldLeak")
    class VideoCompressor extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogManager.showProgress(getActivity());
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
                Intent intent = new Intent(getActivity(), VideoPreviewOnDemandActivity.class);
                intent.putExtra("file_uri", Uri.fromFile(videoFile));
                intent.putExtra("mVideoPathUri", mVideoPathUri);
                intent.putExtra("bundle_data", mMyProfileResModel);
                startActivity(intent);
            }
        }
    }*/
}
