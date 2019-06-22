package online.motohub.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import online.motohub.R;
import online.motohub.adapter.TaggedProfilesAdapter;
import online.motohub.database.DatabaseHandler;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.ImageModel;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.dialog.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.services.UpdateVideoFileService;
import online.motohub.util.UrlUtils;
import online.motohub.util.Utility;

public class PostEditActivity extends BaseActivity implements
        TaggedProfilesAdapter.TaggedProfilesSizeInterface {


    public static final String EXTRA_RESULT_DATA = "activity_video_picker_uri";
    @BindView(R.id.postEditCoLayout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.circular_img_view)
    CircleImageView mProfileImg;
    @BindView(R.id.top_tv)
    TextView mUsername;
    @BindView(R.id.bottom_tv)
    TextView mPostDate;
    @BindView(R.id.edit_post_et)
    EditText mEditPostEt;
    @BindView(R.id.close_layout)
    FrameLayout mRemovePostImgBtnLayout;
    @BindView(R.id.post_picture_img_view)
    ImageView mPostPicImgView;
    @BindView(R.id.tag_profiles_recycler_view)
    RecyclerView mTagProfilesRecyclerView;
    @BindView(R.id.playicon)
    ImageView mPlayIcon;
    @BindString(R.string.edit_post)
    String mToolbarTitle;
    @BindString(R.string.post_update_success)
    String mPostUpdateSuccess;
    @BindString(R.string.tag_err)
    String mTagErr;
    @BindString(R.string.camera_permission_denied)
    String mNoCameraPer;
    @BindString(R.string.storage_permission_denied)
    String mNoStoragePer;
    private ArrayList<ProfileResModel> mFollowingListData;
    private ArrayList<ProfileResModel> mTaggedProfilesList;
    private TaggedProfilesAdapter mTaggedProfilesAdapter;
    private ProfileResModel mMyProfileResModel;
    private PostsResModel mPostResModel;
    private String mPostImgUri;
    private String mVideoThumbnailUri;
    private String mCurrentImgPath = "", mCurrentVideoPath = "", mCurrentVideoThumbnailPath = "";
    private StringBuffer sb = new StringBuffer();

    public static String replacer(StringBuffer outBuffer) {

        String data = outBuffer.toString();
        try {
            StringBuffer tempBuffer = new StringBuffer();
            int incrementor = 0;
            int dataLength = data.length();
            while (incrementor < dataLength) {
                char charecterAt = data.charAt(incrementor);
                if (charecterAt == '%') {
                    tempBuffer.append("<percentage>");
                } else if (charecterAt == '+') {
                    tempBuffer.append("<plus>");
                } else {
                    tempBuffer.append(charecterAt);
                }
                incrementor++;
            }
            data = tempBuffer.toString();
            data = URLDecoder.decode(data, "utf-8");
            data = data.replaceAll("<percentage>", "%");
            data = data.replaceAll("<plus>", "+");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);
        ButterKnife.bind(this);
        initView();
    }

    /*@Override
    @SuppressWarnings("unchecked")
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mPostResModel = (PostsResModel) savedInstanceState.getSerializable(PostsModel.POST_MODEL);
        mMyProfileResModel = (ProfileResModel)
                savedInstanceState.getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        mTaggedProfilesList.clear();
        assert savedInstanceState.getSerializable(ProfileModel.FOLLOWING) != null;
        ArrayList<ProfileResModel> mTempTaggedProfileList = (ArrayList<ProfileResModel>)
                savedInstanceState.getSerializable(ProfileModel.FOLLOWING);
        assert mTempTaggedProfileList != null;
        mTaggedProfilesList.addAll(mTempTaggedProfileList);
        if (mTaggedProfilesAdapter != null)
            mTaggedProfilesAdapter.notifyDataSetChanged();
        if (mTaggedProfilesList.size() > 0) {
            mTagProfilesRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mTagProfilesRecyclerView.setVisibility(View.GONE);
        }
        changeAndSetProfilePost();
        mPostImgUri = savedInstanceState.getString(PostsModel.POST_PICTURE);
        if (mPostImgUri != null) {
            setImageWithGlide(mPostPicImgView, Uri.parse(mPostImgUri));
            mRemovePostImgBtnLayout.setVisibility(View.VISIBLE);
            mPostPicImgView.setVisibility(View.VISIBLE);
            mPlayIcon.setVisibility(View.GONE);
        }
        mVideoThumbnailUri = savedInstanceState.getString(PostsModel.PostVideoThumbnailurl);
        if (mVideoThumbnailUri != null) {
            mRemovePostImgBtnLayout.setVisibility(View.VISIBLE);
            setVideoPost();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(PostsModel.POST_MODEL, mPostResModel);
        outState.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
        outState.putSerializable(ProfileModel.FOLLOWING, mTaggedProfilesList);
        outState.putString(PostsModel.POST_PICTURE, mPostImgUri);
        outState.putString(PostsModel.PostVideoThumbnailurl, mVideoThumbnailUri);
        super.onSaveInstanceState(outState);
    }*/

    private void initView() {
        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        assert getIntent().getExtras() != null;
        /*mPostResModel = (PostsResModel) getIntent().getExtras().getSerializable(PostsModel.POST_MODEL);
        mMyProfileResModel = (ProfileResModel) getIntent().getExtras().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);*/
        //mPostResModel = MotoHub.getApplicationInstance().getmPostResModel();
        mPostResModel = (PostsResModel) getIntent().getExtras().getSerializable(PostsModel.POST_MODEL);
        //mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
        mFollowingListData = new ArrayList<>();
        mTaggedProfilesList = new ArrayList<>();
        FlexboxLayoutManager mFlexBoxLayoutManager = new FlexboxLayoutManager(this);
        mFlexBoxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        mFlexBoxLayoutManager.setFlexDirection(FlexDirection.ROW);
        mFlexBoxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        mTagProfilesRecyclerView.setLayoutManager(mFlexBoxLayoutManager);
        mTaggedProfilesAdapter = new TaggedProfilesAdapter(mTaggedProfilesList, this);
        mTagProfilesRecyclerView.setAdapter(mTaggedProfilesAdapter);
        getTaggedProfilesListData();
        changeAndSetProfilePost();
    }

    private void changeAndSetProfilePost() {
        setImageWithGlide(mProfileImg, mMyProfileResModel.getProfilePicture(), R.drawable.default_profile_icon);
        mUsername.setText(Utility.getInstance().getUserName(mMyProfileResModel));

        mPostDate.setText(findTime(mPostResModel.getDateCreatedAt()));
        if (mPostResModel.getPostText() != null && !mPostResModel.getPostText().isEmpty()) {
            try {
                if (mPostResModel.getPostText().contains(" ")) {
                    mEditPostEt.setText(mPostResModel.getPostText());
                } else {
                    mEditPostEt.setText(URLDecoder.decode(mPostResModel.getPostText(), "UTF-8"));
                }
                //mEditPostEt.setText(replacer(sb.append(mPostResModel.getPostText())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String[] mImgArray = getImgVideoList(mPostResModel.getPostPicture());

        if (mImgArray == null) {
            removePostImg();
        } else if (mImgArray.length == 1 && mImgArray[0].trim().isEmpty()) {
            removePostImg();
        } else {
            mPostImgUri = null;
            mCurrentImgPath = mImgArray[0];
            mPlayIcon.setVisibility(View.GONE);
            mRemovePostImgBtnLayout.setVisibility(View.VISIBLE);
            mPostPicImgView.setVisibility(View.VISIBLE);
          /*  GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + mCurrentImgPath, new LazyHeaders.Builder()
                    .addHeader("X-DreamFactory-Api-Key", getString(R.string.dream_factory_api_key))
                    .build());*/

            GlideUrl glideUrl = new GlideUrl(UrlUtils.AWS_FILE_URL + mCurrentImgPath, new LazyHeaders.Builder()
                    .addHeader("X-DreamFactory-Api-Key", getString(R.string.dream_factory_api_key))
                    .build());

            Glide.with(this)
                    .load(glideUrl)
                    .apply(new RequestOptions()
                            .dontAnimate())
                    .into(mPostPicImgView);
        }

        String[] mVideoArray = getImgVideoList(mPostResModel.getPostVideoThumbnailURL());
        String[] mVideoPathUri = getImgVideoList(mPostResModel.getPostVideoURL());

        if (mVideoArray == null) {
            mPlayIcon.setVisibility(View.GONE);
        } else if (mVideoArray.length == 1 && mVideoArray[0].trim().equals("")) {
            mPlayIcon.setVisibility(View.GONE);
        } else if (mVideoArray.length == 1) {
            mCurrentVideoPath = mVideoPathUri[0];
            mCurrentVideoThumbnailPath = mVideoArray[0];
            mVideoThumbnailUri = null;
            mRemovePostImgBtnLayout.setVisibility(View.VISIBLE);
            mPostPicImgView.setVisibility(View.VISIBLE);
            showVideoFile(mVideoArray[0]);
        }
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.add_post_video, R.id.update_post_btn, R.id.add_post_img, R.id.tag_profile_img, R.id.remove_post_img_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.update_post_btn:
                String mWritePostStr = mEditPostEt.getText().toString().trim();
                try {
                    if (mVideoThumbnailUri != null) {
                        File videoFile = copiedVideoFile(Uri.fromFile(new File(mVideoThumbnailUri)),
                                GALLERY_VIDEO_NAME_TYPE);
                        String mPath = String.valueOf(videoFile);
                        // new VideoCompressor().execute(mPath, getCompressedVideoPath());
                        startUploadVideoService();

                    } else if (mPostImgUri != null) {
                        uploadPicture(mPostImgUri);
                    } else if (!mWritePostStr.isEmpty()) {
                        profilePostContent(mCurrentImgPath, mCurrentVideoPath, mCurrentVideoThumbnailPath);
                    } else {
                        showToast(this, getString(R.string.post_edit_err));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.add_post_img:
                showAppDialog(AppDialogFragment.BOTTOM_ADD_IMG_DIALOG, null);
                break;
            case R.id.remove_post_img_btn:
                removePostImgVideo();
                break;
            case R.id.tag_profile_img:
                /*if (mFollowingListData.size() > 0) {
                    launchTagFollowersProfileDialog();
                } else {
                    getFollowingProfileList();
                }*/
                getFollowingProfileList();
                break;
            case R.id.add_post_video:
                showAppDialog(AppDialogFragment.BOTTOM_ADD_VIDEO_DIALOG, null);
                break;
        }
    }

    private void startUploadVideoService() {
        try {
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mVideoThumbnailUri,
                    MediaStore.Images.Thumbnails.MINI_KIND);
            File imageFile = null;
            try {
                imageFile = compressedImgFromBitmap(thumb);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String postText;
            if (mEditPostEt.getText().toString().isEmpty() || mEditPostEt.getText().toString().equals("")) {
                postText = "";
            } else {
                postText = mEditPostEt.getText().toString();
            }
            DatabaseHandler databaseHandler = new DatabaseHandler(this);
            int count = databaseHandler.getPendingCount();
            String destFilePath = Environment.getExternalStorageDirectory().getPath()
                    + getString(R.string.util_app_folder_root_path);
            Intent service_intent = new Intent(this, UpdateVideoFileService.class);
            service_intent.putExtra("videofile",
                    mCurrentVideoPath);
            service_intent.putExtra("imagefile", String.valueOf(imageFile));
            service_intent.putExtra("posttext", postText);
            service_intent.putExtra("profileid", mPostResModel.getProfileID());
            service_intent.putExtra("postid", mPostResModel.getID());
            service_intent.putExtra("dest_file", destFilePath);
            service_intent.putExtra("running", count + 1);
            service_intent.putExtra("flag", 1);
            service_intent.setAction("UploadService");
            if (mTaggedProfilesList.size() == 0) {
                service_intent.putExtra(PostsModel.TAGGED_PROFILE_ID, "");
            } else {
                StringBuilder mTaggedProfileID = new StringBuilder();
                for (ProfileResModel mProfileResModel : mTaggedProfilesList) {
                    mTaggedProfileID.append(mProfileResModel.getID()).append(",");
                }
                mTaggedProfileID.deleteCharAt(mTaggedProfileID.length() - 1);
                service_intent.putExtra(PostsModel.TAGGED_PROFILE_ID, mTaggedProfileID.toString());
            }
            startService(service_intent);
            //mCompressedVideoPath = "";
            mVideoThumbnailUri = null;
            mPostImgUri = null;
            mEditPostEt.setText("");
            mPostPicImgView.setImageDrawable(null);
            mPostPicImgView.setVisibility(View.GONE);
            mTaggedProfilesList.clear();
            mTaggedProfilesAdapter.notifyDataSetChanged();
            if (mTaggedProfilesList.size() > 0) {
                mTagProfilesRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mTagProfilesRecyclerView.setVisibility(View.GONE);
            }
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removePostImg() {
        mPostPicImgView.setImageDrawable(null);
        mRemovePostImgBtnLayout.setVisibility(View.GONE);
        mPostPicImgView.setVisibility(View.GONE);
    }

    private void removePostImgVideo() {
        mPostImgUri = null;
        mVideoThumbnailUri = null;
        mPostPicImgView.setImageDrawable(null);
        mRemovePostImgBtnLayout.setVisibility(View.GONE);
        mPostPicImgView.setVisibility(View.GONE);
        mPlayIcon.setVisibility(View.GONE);

    }

    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String mDialogType, StringBuilder profileTypesStr, ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, mDialogType, profileTypesStr, profileTypes, position);
        switch (mDialogType) {
            case AppDialogFragment.TAG_FOLLOWING_PROFILE_DIALOG:
                mTaggedProfilesList.clear();
                mTaggedProfilesAdapter.notifyDataSetChanged();
                for (ProfileResModel mFollowerModel : mFollowingListData) {
                    if (mFollowerModel.getIsProfileTagged()) {
                        mTaggedProfilesList.add(mFollowerModel);
                        mTaggedProfilesAdapter.notifyDataSetChanged();
                    }
                }
                if (mTaggedProfilesList.size() > 0) {
                    mTagProfilesRecyclerView.setVisibility(View.VISIBLE);
                    AppDialogFragment.getInstance().dismiss();
                } else {
                    mTagProfilesRecyclerView.setVisibility(View.GONE);
                    showToast(getApplicationContext(), "Please select at least one profile");

                }
                break;
        }
    }

    private void launchTagFollowersProfileDialog() {
        DialogFragment mDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(AppDialogFragment.TAG);
        if (mDialogFragment != null && mDialogFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(mDialogFragment).commit();
        }
        AppDialogFragment.newInstance(AppDialogFragment.TAG_FOLLOWING_PROFILE_DIALOG, mFollowingListData, null)
                .show(getSupportFragmentManager(), AppDialogFragment.TAG);
    }

    private void getTaggedProfilesListData() {
        if (mPostResModel.getTaggedProfileID() != null && !mPostResModel.getTaggedProfileID().isEmpty()) {
            String mFilter = "id IN (" + mPostResModel.getTaggedProfileID() + ")";
            RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);
        }
    }

    private void getFollowingProfileList() {
        String mMyFollowingsID = Utility.getInstance().getMyFollowersFollowingsID(mMyProfileResModel.getFollowprofile_by_ProfileID(), false);
        if (mMyFollowingsID.isEmpty()) {
            showSnackBar(mCoordinatorLayout, mTagErr);
            return;
        }
        String mBlockedUsers = Utility.getInstance().getMyBlockedUsersID(mMyProfileResModel.getBlockedUserProfilesByProfileID(),
                mMyProfileResModel.getBlockeduserprofiles_by_BlockedProfileID());
        String mFilter;
        if (mBlockedUsers.trim().isEmpty()) {
            mFilter = "id IN (" + mMyFollowingsID + ")";
        } else {
            mFilter = "id IN (" + mMyFollowingsID + ") AND ( id NOT IN (" + mBlockedUsers + "))";
        }
        RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_FOLLOWING_PROFILE_RESPONSE);
    }

    private void uploadPicture(String imgUri) {
        File mFile = new File(Uri.parse(imgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", mFile.getName(), requestBody);
        RetrofitClient.getRetrofitInstance().callUploadProfilePostImg(this, filePart, RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE);
    }

    private void showVideoFile(String videoThumbnail) {

      /*  GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + videoThumbnail,
                new LazyHeaders.Builder()
                        .addHeader("X-DreamFactory-Api-Key", getString(R.string.dream_factory_api_key))
                        .build());*/

        GlideUrl glideUrl = new GlideUrl(UrlUtils.AWS_FILE_URL + videoThumbnail,
                new LazyHeaders.Builder()
                        .build());

        Glide.with(this)
                .load(glideUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        mPlayIcon.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        mPlayIcon.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .apply(new RequestOptions()
                        .dontAnimate())
                .into(mPostPicImgView);
    }

    private void profilePostContent(String mCurrentImgPath, String mCurrentVideoPath, String mCurrentVideoThumbnailPath) {
        try {
            String mWritePostStr = mEditPostEt.getText().toString().trim();
            JsonObject mJsonObject = new JsonObject();
            mJsonObject.addProperty(PostsModel.POST_ID, mPostResModel.getID());
            mJsonObject.addProperty(PostsModel.POST_TEXT, URLEncoder.encode(mWritePostStr, "UTF-8"));
            mJsonObject.addProperty(PostsModel.POST_PICTURE, mCurrentImgPath);

            if (mTaggedProfilesList.size() == 0) {
                mJsonObject.addProperty(PostsModel.TAGGED_PROFILE_ID, "");
            } else {
                StringBuilder mTaggedProfileID = new StringBuilder();
                for (ProfileResModel mProfileResModel : mTaggedProfilesList) {
                    mTaggedProfileID.append(mProfileResModel.getID()).append(",");
                }
                mTaggedProfileID.deleteCharAt(mTaggedProfileID.length() - 1);
                mJsonObject.addProperty(PostsModel.TAGGED_PROFILE_ID, mTaggedProfileID.toString());
            }

            mJsonObject.addProperty(PostsModel.PostVideoURL, mCurrentVideoPath);
            mJsonObject.addProperty(PostsModel.PostVideoThumbnailurl, mCurrentVideoThumbnailPath);

            JsonArray mJsonArray = new JsonArray();
            mJsonArray.add(mJsonObject);
            RetrofitClient.getRetrofitInstance().callUpdateProfilePosts(this, mJsonArray, RetrofitClient.UPDATE_PROFILE_POSTS_RESPONSE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String mProfileID = String.valueOf(mMyProfileResModel.getID());
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_CAPTURE_REQ:
                    Uri mCameraPicUri = getImgResultUri(data);
                    mVideoThumbnailUri = null;
                    if (mCameraPicUri != null) {
                        try {
                            File mPostImgFile = compressedImgFile(mCameraPicUri,
                                    POST_IMAGE_NAME_TYPE, mProfileID);
                            mPostImgUri = Uri.fromFile(mPostImgFile).toString();
                            setImageWithGlide(mPostPicImgView, Uri.parse(mPostImgUri));
                            mRemovePostImgBtnLayout.setVisibility(View.VISIBLE);
                            mPostPicImgView.setVisibility(View.VISIBLE);
                            mPlayIcon.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showSnackBar(mCoordinatorLayout, e.getMessage());
                        }
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
                case GALLERY_PIC_REQ:
                    assert data.getExtras() != null;
                    mVideoThumbnailUri = null;
                    Uri mSelectedImgFileUri = (Uri) data.getExtras()
                            .get(PickerImageActivity.EXTRA_RESULT_DATA);
                    if (mSelectedImgFileUri != null) {
                        try {
                            File mPostImgFile = compressedImgFile(mSelectedImgFileUri,
                                    POST_IMAGE_NAME_TYPE, mProfileID);
                            mPostImgUri = Uri.fromFile(mPostImgFile).toString();
                            setImageWithGlide(mPostPicImgView, Uri.parse(mPostImgUri));
                            mRemovePostImgBtnLayout.setVisibility(View.VISIBLE);
                            mPostPicImgView.setVisibility(View.VISIBLE);
                            mPlayIcon.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showSnackBar(mCoordinatorLayout, e.getMessage());
                        }
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
                case ACTION_TAKE_VIDEO:
                    Uri videoUri = data.getData();
                    mPostImgUri = null;
                    if (videoUri != null) {
                        mVideoThumbnailUri = getRealPathFromURI(videoUri);
                        setVideoPost();
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
                case GALLERY_VIDEO_REQ:
                    mVideoThumbnailUri = data.getStringExtra(EXTRA_RESULT_DATA);
                    mPostImgUri = null;
                    if (mVideoThumbnailUri != null) {
                        setVideoPost();
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;

            }
        }
    }

    private void setVideoPost() {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(this.mVideoThumbnailUri,
                MediaStore.Images.Thumbnails.MINI_KIND);
        Drawable drawable = new BitmapDrawable(getResources(), thumb);
        mPostPicImgView.setImageDrawable(drawable);
        mPostPicImgView.setVisibility(View.VISIBLE);
        mRemovePostImgBtnLayout.setVisibility(View.VISIBLE);
        mPlayIcon.setVisibility(View.VISIBLE);

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
                        mTaggedProfilesAdapter.notifyDataSetChanged();

                        if (mTaggedProfilesList.size() > 0) {
                            mTagProfilesRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            mTagProfilesRecyclerView.setVisibility(View.GONE);
                        }

                    }

                    break;

                case RetrofitClient.GET_FOLLOWING_PROFILE_RESPONSE:

                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {

                        mFollowingListData.clear();
                        mFollowingListData.addAll(mProfileModel.getResource());

                        for (ProfileResModel mFollowingModel : mFollowingListData) {

                            for (ProfileResModel mTaggedProfileModel : mTaggedProfilesList) {

                                if (mFollowingModel.getID() == mTaggedProfileModel.getID()) {

                                    mFollowingModel.setIsProfileTagged(true);

                                }

                            }

                        }

                        launchTagFollowersProfileDialog();

                    } else {
                        showSnackBar(mCoordinatorLayout, mTagErr);
                    }

            }

        } else if (responseObj instanceof PostsModel) {

            PostsModel mPostsModel = (PostsModel) responseObj;

            switch (responseType) {

                case RetrofitClient.UPDATE_PROFILE_POSTS_RESPONSE:

                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {

                        setResult(RESULT_OK, new Intent().putExtra(PostsModel.POST_MODEL,
                                mPostsModel.getResource().get(0)));

                        showToast(this, mPostUpdateSuccess);

                        finish();

                    }

                    break;


            }

        } else if (responseObj instanceof ImageModel) {

            ImageModel mImageModel = (ImageModel) responseObj;

            switch (responseType) {

                case RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE:
                    //update the record in database/tablet
                    String imgUrl = getHttpFilePath(mImageModel.getmModels().get(0).getPath());
                    profilePostContent(imgUrl, "", "");
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
        RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        showSnackBar(mCoordinatorLayout, mInternetFailed);
    }

    @Override
    public void notifyEmptyTaggedProfilesList(ArrayList<ProfileResModel> mTaggedProfilesList1) {
        mTaggedProfilesList = mTaggedProfilesList1;
        if (mTaggedProfilesList.size() > 0) {
            mTagProfilesRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mTagProfilesRecyclerView.setVisibility(View.GONE);
        }
    }

}
