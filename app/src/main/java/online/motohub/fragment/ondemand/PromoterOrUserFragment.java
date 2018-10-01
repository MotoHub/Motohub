package online.motohub.fragment.ondemand;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.processors.PublishProcessor;
import online.motohub.R;
import online.motohub.activity.PickerPostVideoForOnDemandActivity;
import online.motohub.activity.VideoPreviewOnDemandActivity;
import online.motohub.adapter.ondemand.PromoterVideoPostAdapter1;
import online.motohub.application.MotoHub;
import online.motohub.fcm.MyFireBaseMessagingService;
import online.motohub.fragment.BaseFragment;
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
import online.motohub.util.UrlUtils;
import online.motohub.util.Utility;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by pickzy01 on 30/05/2018.
 */

public class PromoterOrUserFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String EXTRA_RESULT_DATA = "activity_video_picker_uri";
    protected static final int GALLERY_VIDEO_REQ = 96;
    private final int VISIBLE_THRESHOLD = 1;
    public ShareDialog shareFBDialog;
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
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    //Static
    private int mCurrentProfileID = 0, ProfileID = 0;
    private String mFilterProfile = "ID = " + mCurrentProfileID;
    private String mNewSharedID;
    private VideoShareModel mSharedFeed;
    private int mCurrentPostPosition;
    private ProfileResModel mMyProfileResModel;
    private PromoterVideoPostAdapter1 mAdapter;
    private ArrayList<PromoterVideoModel.Resource> mPromoterVideoList;
    private String mVideoPathUri, mShareContent;
    private String mFilter = "(UserType != usereventvideos) AND (UserType != user)";
    //private String mFilter = "(EventFinishDate<" + getCurrentDate() + ") AND (UserType != user)";

    private boolean mIsLoadMore = false;
    private int pageNumber = 1;
    private boolean isLoading;
    private int mFeedTotalCount = 0;
    private File videoFile;
    private String mSearchStr = "";
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
    private Uri mVideoFileUri;
    private int lastVisibleItem, totalItemCount;
    private LinearLayoutManager layoutManager;
    private boolean loading = false;
    private String[] mShareVideoUrl;
    private PublishProcessor<Integer> paginator = PublishProcessor.create();
    private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            mIsLoadMore = true;
            mPromoterVideoList.add(null);
            getActivity().runOnUiThread(new Runnable() {
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

    private void initView() {

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
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
        if (getActivity().getIntent().getExtras().getBoolean(MyFireBaseMessagingService.IS_FROM_NOTIFICATION_TRAY)) {
            try {
                JSONObject mJsonObjectEntry = new JSONObject(getActivity().getIntent().getExtras().getString(MyFireBaseMessagingService.ENTRY_JSON_OBJ));
                JSONObject mDetailsObj = mJsonObjectEntry.getJSONObject("Details");
                mFilter = "(" + mFilter + ") AND (ID = " + mDetailsObj.getInt("VideoID") + ")";
                mCurrentProfileID = Integer.parseInt(mDetailsObj.getString("ProfileID"));
                videoGalleryFab.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //mFilter = "(EventFinishDate<" + getCurrentDate() + ") AND " + "(UserType != user)" +;
           /* try {
                Bundle mBundle = getActivity().getIntent().getExtras();
                if (mBundle != null) {
                    mCurrentProfileID = mBundle.getInt(AppConstants.PROFILE_ID, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
        getMyProfiles();
        search_edt.addTextChangedListener(new TextWatcher() {
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

        search_edt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
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
        if (mSearchStr.isEmpty() || mSearchStr.length() == 0) {
            //mFilter = "(EventFinishDate<" + getCurrentDate() + ") AND " + "(UserType != user)";
            mFilter = "(UserType != usereventvideos) AND (UserType != user)";
            getSearchDataFromAPi(mFilter);
        } else {
            mPromoterVideoList.add(null);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyItemInserted(mPromoterVideoList.size() - 1);
                }
            });
            //mFilter = "(EventFinishDate<" + getCurrentDate() + ") AND " + "(UserType != user) AND (Caption LIKE '%" + mSearchStr + "%')";
            mFilter = "(UserType != usereventvideos) AND (UserType != user) AND (Caption LIKE '%" + mSearchStr + "%')";
            getSearchDataFromAPi(mFilter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        compositeDisposable.dispose();
    }

    private void getMyProfiles() {
        String mFilter = "ID=" + mCurrentProfileID;
        if (isNetworkConnected())
            RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);
        else {
            showSnackBar(patent, getActivity().getResources().getString(R.string.internet_err));
        }

    }

    private void getVideoDataFromAPi(String mFilter) {
        RetrofitClient.getRetrofitInstance()
                .callGetPromotersGallery(this, mFilter, RetrofitClient.GET_VIDEO_FILE_RESPONSE, mOffset, mIsLoadMore);
    }

    private void getSearchDataFromAPi(String mFilter) {
        RetrofitClient.getRetrofitInstance().callGetPromotersGallery(this, mFilter, RetrofitClient.GET_SEARCH_VIDEO_FILE_RESPONSE, mOffset);
    }

    @Override
    public void onRefresh() {
        mOffset = 0;
        mPromoterVideoList.clear();
        recyclerView.getRecycledViewPool().clear();
        setAdapter();
        getMyProfiles();
    }

    @OnClick({R.id.video_gallery_fab})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_gallery_fab:
                galleryIntentVideo1();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(this.broadCastUploadStatus, new IntentFilter("UPLOAD_STATUS"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.broadCastUploadStatus);
    }

    private void setAdapter() {
        isLoading = false;
        if (mAdapter == null) {
            mAdapter = new PromoterVideoPostAdapter1(mPromoterVideoList, mMyProfileResModel, getContext(), ProfileID, PromoterOrUserFragment.this);
            recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void galleryIntentVideo1() {
        Intent mGalleryIntent = new Intent(getActivity(), PickerPostVideoForOnDemandActivity.class);
        startActivityForResult(mGalleryIntent, GALLERY_VIDEO_REQ);
        getActivity().overridePendingTransition(R.anim.anim_bottom_up, R.anim.anim_bottom_down);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_VIDEO_REQ:
                    mVideoPathUri = data.getStringExtra(EXTRA_RESULT_DATA);
                    if (mVideoPathUri != null) {
                        new VideoCompressor().execute(getSelectedVideoPath(), getCompressedVideoPath());
                    } else {
                        showSnackBar(patent, getString(R.string.file_not_found));
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
                        showSnackBar(patent, getString(R.string.video_not_found));
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
                mSharedFeed = mNewFeedShare.get(0);
                callAddVideoSharedPost();
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
        mIsLoadMore = false;
        mPromoterVideoList.remove(mPromoterVideoList.size() - 1);
        getActivity().runOnUiThread(new Runnable() {
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

    /*@Override
    public void alertDialogPositiveBtnClick(BaseFragment activity, String dialogType, StringBuilder profileTypesStr, ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);
        switch (dialogType) {

            case AppDialogFragment.BOTTOM_SHARE_DIALOG:
                mCurrentPostPosition = position;
                callPostShare(position);
                break;

        }
    }*/

    public void showBottomSheetDialog(final String shareContent, final ArrayList<Bitmap> shareImg, final String[] videoUrl, final int mPos, final boolean mIsFromOtherMotoProfile) {
        //LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = getLayoutInflater().inflate(R.layout.share_layout, null);
        LinearLayout lFriends = view.findViewById(R.id.Friends_lay);
        LinearLayout lFacebook = view.findViewById(R.id.facebook_lay);
        TextView txtvwCancel = view.findViewById(R.id.txtvwCancel);
        final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(view);
        dialog.show();
        lFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPostShare(mPos);
                dialog.dismiss();
            }
        });

        lFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFBShareDialog1(shareContent, shareImg, videoUrl);
                dialog.dismiss();
            }
        });

        txtvwCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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

    private void showFBShareDialog1(String content, final ArrayList<Bitmap> mShareImage, final String[] mVideoUrl) {

        shareFBDialog = new ShareDialog(this);

        mShareContent = content;

        if (mVideoUrl != null) {

            if (ShareDialog.canShow(ShareVideoContent.class)) {

                getUriVideo(mVideoUrl);
                //getUriVideo(mVideoUrl);
            } else {
                Toast.makeText(getContext(), "Please install the Facebook app for sharing photos.", Toast.LENGTH_SHORT).show();
            }

        } else if (mShareImage != null) {

            if (ShareDialog.canShow(SharePhotoContent.class)) {
                SharePhoto[] photo = new SharePhoto[mShareImage.size()];
                ArrayList<SharePhoto> mSharePhotoList = new ArrayList<>();
                SharePhotoContent shareContent;

                for (int i = 0; i < mShareImage.size(); i++) {
                    photo[i] = new SharePhoto.Builder()
                            .setBitmap(mShareImage.get(i))
                            .build();
                }

                mSharePhotoList.addAll(Arrays.asList(photo).subList(0, mShareImage.size()));
                if (content != null) {
                    content = content.replace(" ", "#");
                    String mShareTxt = "#" + content;
                    shareContent = new SharePhotoContent.Builder()
                            .addPhotos(mSharePhotoList)
                            .setShareHashtag(new ShareHashtag.Builder()
                                    .setHashtag(mShareTxt)
                                    .build())
                            .build();
                } else {
                    shareContent = new SharePhotoContent.Builder()
                            .addPhotos(mSharePhotoList)
                            .build();
                }

                shareFBDialog.show(shareContent, ShareDialog.Mode.AUTOMATIC);


            } else {
                Toast.makeText(getContext(), "Please install the Facebook app for sharing photos.", Toast.LENGTH_SHORT).show();
            }

        } else {

            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setQuote(content)
                    .setContentUrl(Uri.parse("www.pickzy.com"))
                    .build();
            shareFBDialog.show(linkContent);

        }

    }

    @SuppressLint("StaticFieldLeak")
    public void getUriVideo(final String[] videoUrl) {
        mShareVideoUrl = videoUrl;


        final String uri = (UrlUtils.FILE_URL + videoUrl[0] + "?api_key="
                + getResources().getString(R.string.dream_factory_api_key)
                + "&session_token="
                + PreferenceUtils.getInstance(getActivity()).getStrData(PreferenceUtils
                .SESSION_TOKEN) + "&download=" + true);


        new AsyncTask<Void, Void, Void>() {
            File apkStorage = null;
            File outputFile = null;

            @Override
            protected void onPreExecute() {
                DialogManager.showProgress(getActivity());
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... Void) {
                String[] mUrl = videoUrl[0].split("/");

                try {
                    URL url = new URL(uri);//Create Download URl
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                    c.setRequestMethod("GET");//Set Request Method to "GET" since we are getting data
                    c.connect();//connect the URL Connection

                    //If Connection response is not OK then show Logs
                    if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    }

                    //Get File if SD card is present
                    if (isSDCardPresent()) {

                        apkStorage = new File(
                                Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES)
                                        + getString(R.string.util_app_folder_root_path));
                    } else
                        Toast.makeText(getActivity(), "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

                    //If File is not present create directory
                    if (!apkStorage.exists()) {
                        apkStorage.mkdir();
                    }

                    outputFile = new File(apkStorage, mUrl[1]);//Create Output file in Main File

                    //Create New File if not present
                    if (!outputFile.exists()) {
                        outputFile.createNewFile();
                    } else {
                        return null;
                    }

                    FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                    InputStream is = c.getInputStream();//Get InputStream for connection

                    byte[] buffer = new byte[1024];//Set buffer type
                    int len1 = 0;//init length
                    while ((len1 = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len1);//Write new file
                    }

                    //Close all connection after doing task
                    fos.close();
                    is.close();
                    mVideoFileUri = Uri.fromFile(outputFile);

                } catch (Exception e) {
                    //Read exception if something went wrong
                    e.printStackTrace();
                    outputFile = null;
                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                DialogManager.hideProgress();
                if (outputFile != null) {
                    mVideoFileUri = Uri.fromFile(outputFile);
                   /* sendVideoUrl = new AppDialogFragment();
                    sendVideoUrl.SendData(mVideoFileUri, BaseActivity.this);*/
                    showFBVideoShareDialog(mVideoFileUri, getActivity());
                    // AppDialogFragment.getInstance().showFBVideoShareDialog(mVideoFileUri);
                } else {
                    showToast(getApplicationContext(), "Something went wrong!! Video could not been shared.");
                }
                super.onPostExecute(aVoid);

            }
        }.execute();
    }

    public boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public void showFBVideoShareDialog(Uri mVideoUri, Context context) {
        //Uri ur = Uri.parse(mVideoUri.toString().replace("/video","/Kiran.mp4"));
        //if (isAdded()) {

        ShareVideo video = new ShareVideo.Builder()
                .setLocalUrl(mVideoUri)
                .build();

        ShareVideoContent videoContent;
        shareFBDialog = new ShareDialog(getActivity());

        if (mShareContent != null) {

            mShareContent = mShareContent.replace(" ", "#");
            String mShareTxt = "#" + mShareContent;
            videoContent = new ShareVideoContent.Builder()
                    .setVideo(video)
                    .setShareHashtag(new ShareHashtag.Builder()
                            .setHashtag(mShareTxt)
                            .build())
                    .setContentTitle(mShareContent)
                    .build();
        } else {
            videoContent = new ShareVideoContent.Builder()
                    .setVideo(video)
                    .build();
        }

        shareFBDialog.show(videoContent, ShareDialog.Mode.AUTOMATIC);

    }

    @SuppressLint("StaticFieldLeak")
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
    }
}
