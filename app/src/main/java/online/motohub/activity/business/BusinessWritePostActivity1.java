package online.motohub.activity.business;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pavlospt.rxfile.RxFile;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.listener.OnCheckedListener;
import com.zhihu.matisse.listener.OnSelectedListener;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.VideoStoryPreviewActivity;
import online.motohub.application.MotoHub;
import online.motohub.database.DatabaseHandler;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.CustomGallery;
import online.motohub.model.GalleryImgModel;
import online.motohub.model.ImageModel;
import online.motohub.model.ImageResModel;
import online.motohub.model.PostsModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.DialogManager;
import online.motohub.util.GifSizeFilter;
import online.motohub.util.Glide4Engine;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.UploadFileService;
import online.motohub.util.UrlUtils;
import online.motohub.util.Utils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class BusinessWritePostActivity1 extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.profile_img)
    CircleImageView mMPImg;
    @BindView(R.id.name_of_moto_tv)
    TextView mNameOfMotoTv;
    @BindView(R.id.name_of_driver_tv)
    TextView mNameOfDriverTv;
    @BindView(R.id.write_post_et)
    EditText mWritePostEt;
    /*@BindView(R.id.close_layout)
    FrameLayout mPostImgVideoCloseBtnLayout;
    @BindView(R.id.imageframe)
    RelativeLayout mPostImgVideoLayout;
    @BindView(R.id.post_picture_img_view)
    ImageView mPostPicImgView;
    @BindView(R.id.play)
    ImageView mPlayIconImgView;*/

    @BindView(R.id.parent)
    LinearLayout mParent;
    @BindView(R.id.toolbar_back_img_btn)
    ImageButton toolBack;

    @BindString(R.string.write_new_post)
    String mToolbarTitle;

    private static final int REQUEST_CODE_CHOOSE = 23;

    private UriAdapter mAdapter;

    private String mVideoPathUri, mPostImgUri;
    private PromotersResModel mPromoterResModel;
    private String usertype;
    private ImageResModel mImageResModel = null;

    AmazonS3Client s3;
    BasicAWSCredentials credentials;
    TransferUtility transferUtility;
    TransferObserver observerVideo, observerImage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.write_post_business);
        ButterKnife.bind(this);
        setToolbar(mToolbar, mToolbarTitle);

        init();
    }


    private void init() {
        setupUI(mParent);
        toolBack.setVisibility(View.VISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter = new UriAdapter());
        String mProfiles = "";

        /*if (getIntent().getExtras() != null) {
            Gson gson = new Gson();
            if (getIntent().hasExtra(PromotersModel.PROMOTERS_RES_MODEL))
                mProfiles = getIntent().getStringExtra(PromotersModel.PROMOTERS_RES_MODEL);
            mPromoterResModel = gson.fromJson(mProfiles, PromotersResModel.class);
            usertype = mPromoterResModel.getUserType();

        }*/
        //mPromoterResModel = MotoHub.getApplicationInstance().getmPromoterResModel();
        mPromoterResModel = EventBus.getDefault().getStickyEvent(PromotersResModel.class);
        usertype = mPromoterResModel.getUserType();

        changeAndSetProfile();
        RxFile.setLoggingEnabled(true);

    }

    private void changeAndSetProfile() {
        if (mPromoterResModel != null) {
            if (!mPromoterResModel.getProfileImage().isEmpty()) {
                setImageWithGlide(mMPImg, mPromoterResModel.getProfileImage(), R.drawable.default_profile_icon);
            } else {
                mMPImg.setImageResource(R.drawable.default_profile_icon);
            }

            mNameOfMotoTv.setText(mPromoterResModel.getName());
            mNameOfDriverTv.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.post_btn, R.id.add_post_img, R.id.toolbar_back_img_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_btn:
                setResult(RESULT_OK);
                if (mVideoPathUri != null) {
                    startUploadVideoService();
                } else if (mPostImgUri != null) {
                    uploadPicture(mPostImgUri);
                } else {
                    profilePostContent("");
                }
                break;
            case R.id.add_post_img:
                callGallery(v);
                break;
            case R.id.profile_img:
            case R.id.name_of_moto_tv:
            case R.id.toolbar_back_img_btn:
                finish();
                break;
        }
    }

    private void uploadPicture(String mPostImgUri) {
        File mFile = new File(Uri.parse(mPostImgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", mFile.getName(), requestBody);
        RetrofitClient.getRetrofitInstance().callUploadProfilePostImg(this, filePart, RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE);
    }

    private void startUploadVideoService() {
        try {
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mVideoPathUri, MediaStore.Images.Thumbnails.MINI_KIND);
            File imageFile = compressedImgFromBitmap(thumb);
            String postText, mPostText = "";
            if (mWritePostEt.getText().toString().isEmpty() || mWritePostEt.getText().toString().equals("")) {
                postText = "";
            } else {
                postText = mWritePostEt.getText().toString();
                mPostText = URLEncoder.encode(postText, "UTF-8");
            }
            DatabaseHandler databaseHandler = new DatabaseHandler(this);
            int count = databaseHandler.getPendingCount();
            String destFilePath = Environment.getExternalStorageDirectory().getPath()
                    + getString(R.string.util_app_folder_root_path);
            //String mProfileId = Integer.parseInt(getUserId());
            Intent service_intent = new Intent(this, UploadFileService.class);
            service_intent.putExtra("videofile", mVideoPathUri);
            service_intent.putExtra("imagefile", String.valueOf(imageFile));
            service_intent.putExtra("posttext", mPostText);
            service_intent.putExtra("profileid", Integer.parseInt(getUserId()));
            service_intent.putExtra("dest_file", destFilePath);
            service_intent.putExtra("usertype", usertype);
            service_intent.putExtra("running", count + 1);
            service_intent.putExtra("flag", 1);
            service_intent.setAction("UploadService");
            service_intent.putExtra(PostsModel.TAGGED_PROFILE_ID, "");
            startService(service_intent);
            mVideoPathUri = null;
            mPostImgUri = null;
            mWritePostEt.setText("");
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This is called when any media files (image)
     *
     * @param postImgFilePath
     */
    private void profilePostContent(String postImgFilePath) {
        try {
            String mPostPic;
            String mWritePostStr = mWritePostEt.getText().toString().trim();
            if (TextUtils.isEmpty(postImgFilePath) && TextUtils.isEmpty(mWritePostStr)) {
                return;
            }
            int mUserId = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(PostsModel.POST_TEXT, URLEncoder.encode(mWritePostStr, "UTF-8"));

            if (!postImgFilePath.trim().isEmpty())
                mPostPic = "[\"" + postImgFilePath + "\"]";
            else
                mPostPic = "";

            mJsonObject.addProperty(PostsModel.POST_PICTURE, mPostPic);
            mJsonObject.addProperty(PostsModel.PROFILE_ID, mUserId);
            //mJsonObject.addProperty(PostsModel.PostVideoURL,"");
            //mJsonObject.addProperty(PostsModel.PostVideoThumbnailurl,"");

            mJsonObject.addProperty(PostsModel.WHO_POSTED_USER_ID, mUserId);
            mJsonObject.addProperty(PostsModel.WHO_POSTED_PROFILE_ID, mUserId);
            mJsonObject.addProperty(PostsModel.IS_NEWS_FEED_POST, true);
            mJsonObject.addProperty(PostsModel.USER_TYPE, usertype);


            JsonArray mJsonArray = new JsonArray();
            mJsonArray.add(mJsonObject);
            RetrofitClient.getRetrofitInstance().callCreateProfilePosts(this, mJsonArray, RetrofitClient.CREATE_PROFILE_POSTS_RESPONSE);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callGallery(final View v) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            Matisse.from(BusinessWritePostActivity1.this)
                                    .choose(MimeType.ofAll(), false)
                                    .countable(true)
                                    .capture(true)
                                    .captureStrategy(
                                            new CaptureStrategy(true, getResources().getString(R.string.file_provider_authority)))
                                    .maxSelectable(199)
                                    .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                    .gridExpectedSize(
                                            getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                    .thumbnailScale(0.85f)
//                                            .imageEngine(new GlideEngine())  // for glide-V3
                                    .imageEngine(new Glide4Engine())    // for glide-V4
                                    .setOnSelectedListener(new OnSelectedListener() {
                                        @Override
                                        public void onSelected(
                                                @NonNull List<Uri> uriList, @NonNull List<String> pathList) {
                                            // DO SOMETHING IMMEDIATELY HERE
                                            Log.e("onSelected", "onSelected: pathList=" + pathList);

                                        }
                                    })
                                    .originalEnable(true)
                                    .maxOriginalSize(200)
                                    .setOnCheckedListener(new OnCheckedListener() {
                                        @Override
                                        public void onCheck(boolean isChecked) {
                                            // DO SOMETHING IMMEDIATELY HERE
                                            Log.e("isChecked", "onCheck: isChecked=" + isChecked);
                                        }
                                    })
                                    .forResult(REQUEST_CODE_CHOOSE);


                            mAdapter.setData(null, null, BusinessWritePostActivity1.this);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(BusinessWritePostActivity1.this, R.string.permission_request_denied, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mAdapter.setData(Matisse.obtainResult(data), Matisse.obtainPathResult(data), this);
            if (Matisse.obtainResult(data).size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                fetchFiles(data.getClipData());
            } else {
                recyclerView.setVisibility(View.GONE);
            }
            //Log.e("OnActivityResult ", String.valueOf(Matisse.obtainOriginalState(data)));
        }
    }


    private void fetchFiles(ClipData clipData) {
        RxFile.createFilesFromClipData(this, clipData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<File>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<File> files) {
                        for (File f : files) {
                            if (!isImageFile(f.getAbsolutePath())) {
                                //PostVideos
                                //PostVideosThumb
                                File imageFile = null;
                                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
                                try {
                                    imageFile = compressedImgFromBitmap(thumb);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                amazoneUpload(imageFile, UrlUtils.FILE_WRITE_POST_VIDEO_THUBILINE, 1);
                                amazoneUpload(f, UrlUtils.FILE_WRITE_POST, 1);
                            } else {
                                amazoneUpload(f, UrlUtils.FILE_WRITE_POST_IMAGE, 0);
                            }
                        }
                    }
                });
    }


    private io.reactivex.Observable<ImageModel> uploadImage(File file) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", file.getName(), requestBody);
        return RetrofitClient.getRetrofitInstance().getRetrofitApiInterface()
                .postProfilePostImgFileRxjava(filePart)
                .toObservable()
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread());
    }

    private void callAddGalleryPhoto() {

        if (mImageResModel != null) {

            JsonObject obj = new JsonObject();
            obj.addProperty(GalleryImgModel.USER_ID, getUserId());
            obj.addProperty(GalleryImgModel.MOTO_ID, getUserId());
            obj.addProperty(GalleryImgModel.GALLERY_IMG, getHttpFilePath(mImageResModel.getPath()));

            JsonArray jsonElements = new JsonArray();
            jsonElements.add(obj);

            RetrofitClient.getRetrofitInstance().postImgToGallery(this,
                    jsonElements, RetrofitClient.POST_GALLERY_DATA_RESPONSE);

            mImageResModel = null;

        } else {

            showSnackBar(mParent, getString(R.string.select_file_to_upload));

        }

    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    public int getProfileCurrentPos() {
        return PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS);
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }


    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof ImageModel) {
            ImageModel mImageModel = (ImageModel) responseObj;
            switch (responseType) {
                case RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE:
                    //update the record in database/tablet
                    String imgUrl = getHttpFilePath(mImageModel.getmModels().get(0).getPath());
                    mImageResModel = mImageModel.getmModels().get(0);
                    profilePostContent(imgUrl);
                    break;
            }
        } else if (responseObj instanceof PostsModel) {
            PostsModel mPostsModel = (PostsModel) responseObj;
            if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                if (!mPostsModel.getResource().get(0).getPostPicture().trim().isEmpty()) {
                    callAddGalleryPhoto();
                } else {
                    finish();
                }
            }
        } else if (responseObj instanceof GalleryImgModel) {
            finish();
        }
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);

        if (message.equals("Unauthorized") || code == 401) {
            if (isNetworkConnected(this))
                RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
            else
                showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mParent, mErrorMsg);
        }

    }

    @Override
    public void retrofitOnError(int code, String message, int responseType) {
        super.retrofitOnError(code, message, responseType);
        if (message.equals("Unauthorized") || code == 401) {
            if (isNetworkConnected(this))
                RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
            else
                showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
        } else {
            switch (responseType) {
                case RetrofitClient.POST_SHARES:
                    showSnackBar(mParent, "Already shared this post");
                    break;
            }
        }
    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
        String mErrorMsg = code + " - " + message;
        showSnackBar(mParent, mErrorMsg);

    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        showAppDialog(AppDialogFragment.ALERT_INTERNET_FAILURE_DIALOG, null);
    }

    private static class UriAdapter extends RecyclerView.Adapter<UriAdapter.UriViewHolder> {

        private List<Uri> mUris;
        private List<String> mPaths;
        private Context mContext;

        void setData(List<Uri> uris, List<String> paths, Context context) {
            mUris = uris;
            mPaths = paths;
            mContext = context;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public UriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UriViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.uri_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull UriViewHolder holder, int position) {
            holder.mUri.setText(mUris.get(position).toString());
            holder.mPath.setText(mPaths.get(position));

            holder.mUri.setAlpha(position % 2 == 0 ? 1.0f : 0.54f);
            holder.mPath.setAlpha(position % 2 == 0 ? 1.0f : 0.54f);

            Uri photoUri = Uri.fromFile(new File(mPaths.get(position)));

            if (mPaths.get(position).contains(".mp4")) {
                holder.play.setVisibility(View.VISIBLE);
            } else {
                holder.play.setVisibility(View.GONE);
            }

            Glide.with(mContext)
                    .load(photoUri) // Uri of the picture
                    .into(holder.imgQueue);
        }

        @Override
        public int getItemCount() {
            return mUris == null ? 0 : mUris.size();
        }

        static class UriViewHolder extends RecyclerView.ViewHolder {

            private TextView mUri;
            private TextView mPath;
            private ImageView imgQueue;
            private ImageView play;

            UriViewHolder(View contentView) {
                super(contentView);
                mUri = (TextView) contentView.findViewById(R.id.uri);
                mPath = (TextView) contentView.findViewById(R.id.path);
                imgQueue = (ImageView) contentView.findViewById(R.id.imgQueue);
                play = (ImageView) contentView.findViewById(R.id.play);
            }
        }
    }


    private void amazoneUpload(final File file, final String urlPath, final int type) {

        /*pDialog.setMessage("Please wait video is uploading...");
        showDialog();*/

        final ArrayList<String> postImage = new ArrayList<>();
        final ArrayList<String> postVideoImage = new ArrayList<>();
        final ArrayList<String> postVidoe = new ArrayList<>();

        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setMaxErrorRetry(3);
        configuration.setConnectionTimeout(501000);
        configuration.setSocketTimeout(501000);
        configuration.setProtocol(Protocol.HTTP);

        credentials = new BasicAWSCredentials(AppConstants.KEY, AppConstants.SECRET);
        s3 = new AmazonS3Client(credentials, configuration);
        s3.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));
        transferUtility = new TransferUtility(s3, BusinessWritePostActivity1.this);


        observerVideo = transferUtility.upload(AppConstants.BUCKET_NAME, urlPath + file.getName(), file);

        observerVideo.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (state.COMPLETED.equals(observerVideo.getState())) {

                    if (type == 0) {
                        postImage.add(urlPath + file.getName());
                    } else {
                        if (file.getName().contains(".mp4")) {
                            postVidoe.add(urlPath + file.getName());
                        } else {
                            postVideoImage.add(urlPath + file.getName());
                        }
                    }

                    //hideDialog();

                    /*String imageFileName = image.getName().substring(image.getName().lastIndexOf("/") + 1);
                    String videoFileName = video.getName().substring(video.getName().lastIndexOf("/") + 1);*/

                    //apiCallToUploadSpecLiveStreamVideo(UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + videoFileName, UrlUtils.FILE_UPLOAD_SPECTOCTORLIVE + imageFileName);

                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                long _bytesCurrent = bytesCurrent;
                long _bytesTotal = bytesTotal;

                float percentage = ((float) _bytesCurrent / (float) _bytesTotal * 100);
                Log.d("percentage", "" + percentage);
                //pDialog.setProgress((int) percentage);

            }

            @Override
            public void onError(int id, Exception ex) {
                //hideDialog();
                //Toast.makeText(VideoStoryPreviewActivity.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
