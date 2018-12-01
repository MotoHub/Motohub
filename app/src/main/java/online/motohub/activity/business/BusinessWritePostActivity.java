package online.motohub.activity.business;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.PickerImageActivity;
import online.motohub.application.MotoHub;
import online.motohub.database.DatabaseHandler;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.GalleryImgModel;
import online.motohub.model.ImageModel;
import online.motohub.model.ImageResModel;
import online.motohub.model.PostsModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.UploadFileService;

public class BusinessWritePostActivity extends BaseActivity {

    public static final String EXTRA_RESULT_DATA = "activity_video_picker_uri";

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
    @BindView(R.id.close_layout)
    FrameLayout mPostImgVideoCloseBtnLayout;
    @BindView(R.id.imageframe)
    RelativeLayout mPostImgVideoLayout;
    @BindView(R.id.post_picture_img_view)
    ImageView mPostPicImgView;
    @BindView(R.id.play)
    ImageView mPlayIconImgView;

    @BindView(R.id.parent)
    LinearLayout mParent;
    @BindView(R.id.toolbar_back_img_btn)
    ImageButton toolBack;

    @BindString(R.string.write_new_post)
    String mToolbarTitle;

    private String mVideoPathUri, mPostImgUri;
    private PromotersResModel mPromoterResModel;
    private String usertype;
    private ImageResModel mImageResModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);
        ButterKnife.bind(this);
        setToolbar(mToolbar, mToolbarTitle);
        initView();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, mPromoterResModel);
        outState.putString(PostsModel.POST_PICTURE, mPostImgUri);
        outState.putString(PostsModel.PostVideoURL, mVideoPathUri);
        outState.putString(PostsModel.PostVideoURL, mVideoPathUri);
        super.onSaveInstanceState(outState);
    }


    @Override
    @SuppressWarnings("unchecked")
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mPostImgUri = savedInstanceState.getString(PostsModel.POST_PICTURE);
        if (mPostImgUri != null) {
            setImageWithGlide(mPostPicImgView, Uri.parse(mPostImgUri));
            mPostImgVideoCloseBtnLayout.setVisibility(View.VISIBLE);
            mPostPicImgView.setVisibility(View.VISIBLE);
            mPostImgVideoLayout.setVisibility(View.VISIBLE);
        }
        mVideoPathUri = savedInstanceState.getString(PostsModel.PostVideoURL);
        if (mVideoPathUri != null) {
            setVideoPost();
        }
        mPromoterResModel = (PromotersResModel) savedInstanceState.getSerializable(PromotersModel.PROMOTERS_RES_MODEL);

    }*/

    private void initView() {

        setupUI(mParent);
        toolBack.setVisibility(View.VISIBLE);
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
        if (mPromoterResModel != null & mPromoterResModel.getUserType() != null) {
            usertype = mPromoterResModel.getUserType();
            changeAndSetProfile();
        }
    }

    @OnClick({R.id.post_btn, R.id.add_post_img, R.id.add_post_video, R.id.imageframe, R.id.remove_post_img_btn, R.id.toolbar_back_img_btn})
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
                showAppDialog(AppDialogFragment.BOTTOM_ADD_IMG_DIALOG, null);
                break;
            case R.id.add_post_video:
                showAppDialog(AppDialogFragment.BOTTOM_ADD_VIDEO_DIALOG, null);
                break;

            case R.id.remove_post_img_btn:
                mPostImgUri = null;
                mVideoPathUri = null;
                mPostPicImgView.setImageDrawable(null);
                mPostImgVideoCloseBtnLayout.setVisibility(View.GONE);
                mPostImgVideoLayout.setVisibility(View.GONE);
                mPostPicImgView.setVisibility(View.GONE);
                break;
            case R.id.imageframe:
                if (mVideoPathUri != null) {
                    moveLoadVideoPreviewScreen(this, mVideoPathUri);
                }
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

    public int getProfileCurrentPos() {

        return PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS);
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
            //mCompressedVideoPath = "";
            mWritePostEt.setText("");
            mPostPicImgView.setImageDrawable(null);
            mPostPicImgView.setVisibility(View.GONE);
            mPostImgVideoLayout.setVisibility(View.GONE);
            mPostImgVideoCloseBtnLayout.setVisibility(View.GONE);

            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_CAPTURE_REQ:
                    Uri mCameraPicUri = getImgResultUri(data);
                    mVideoPathUri = null;
                    if (mCameraPicUri != null) {
                        try {
                            File mPostImgFile = compressedImgFile(mCameraPicUri,
                                    POST_IMAGE_NAME_TYPE, "");
                            mPostImgUri = Uri.fromFile(mPostImgFile).toString();
                            setImageWithGlide(mPostPicImgView, Uri.parse(mPostImgUri));

                            mPostImgVideoCloseBtnLayout.setVisibility(View.VISIBLE);
                            mPlayIconImgView.setVisibility(View.GONE);
                            mPostImgVideoLayout.setVisibility(View.VISIBLE);
                            mPostPicImgView.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showSnackBar(mParent, e.getMessage());
                        }
                    } else {
                        showSnackBar(mParent, getString(R.string.file_not_found));
                    }
                    break;
                case GALLERY_PIC_REQ:
                    assert data.getExtras() != null;
                    mVideoPathUri = null;
                    Uri mSelectedImgFileUri = (Uri) data.getExtras().get(PickerImageActivity.EXTRA_RESULT_DATA);
                    if (mSelectedImgFileUri != null) {
                        try {
                            String mProfileID = String.valueOf(getUserId());
                            File mPostImgFile = compressedImgFile(mSelectedImgFileUri,
                                    POST_IMAGE_NAME_TYPE, mProfileID);
                            mPostImgUri = Uri.fromFile(mPostImgFile).toString();
                            setImageWithGlide(mPostPicImgView, Uri.parse(mPostImgUri));
                            mPostImgVideoCloseBtnLayout.setVisibility(View.VISIBLE);
                            mPlayIconImgView.setVisibility(View.GONE);
                            mPostPicImgView.setVisibility(View.VISIBLE);
                            mPostImgVideoLayout.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showSnackBar(mParent, e.getMessage());
                        }
                    } else {
                        showSnackBar(mParent, getString(R.string.file_not_found));
                    }
                    break;
                case ACTION_TAKE_VIDEO:
                    Uri videoUri = data.getData();
                    mPostImgUri = null;
                    if (videoUri != null) {
                        mVideoPathUri = getRealPathFromURI(videoUri);
                        setVideoPost();
                    } else {
                        showSnackBar(mParent, getString(R.string.file_not_found));
                    }
                    break;
                case GALLERY_VIDEO_REQ:
                    mVideoPathUri = data.getStringExtra(EXTRA_RESULT_DATA);
                    mPostImgUri = null;
                    if (mVideoPathUri != null) {
                        setVideoPost();
                    } else {
                        showSnackBar(mParent, getString(R.string.file_not_found));
                    }
                    break;
            }
        }
    }

    private void setVideoPost() {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(this.mVideoPathUri,
                MediaStore.Images.Thumbnails.MINI_KIND);
        Drawable drawable = new BitmapDrawable(getResources(), thumb);
        mPostImgVideoCloseBtnLayout.setVisibility(View.VISIBLE);
        mPostPicImgView.setVisibility(View.VISIBLE);
        mPlayIconImgView.setVisibility(View.VISIBLE);
        mPostImgVideoLayout.setVisibility(View.VISIBLE);
        mPostPicImgView.setImageDrawable(drawable);
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

}

