package online.motohub.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import online.motohub.R;
import online.motohub.adapter.GalleryImgAdapter;
import online.motohub.application.MotoHub;
import online.motohub.model.DeleteProfileImagesResponse;
import online.motohub.model.GalleryImgModel;
import online.motohub.model.GalleryImgResModel;
import online.motohub.model.ImageModel;
import online.motohub.model.ImageResModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.RecyclerClick_Listener;
import online.motohub.util.RecyclerTouchListener;
import online.motohub.util.ToolbarActionModeCallback;
import online.motohub.util.UrlUtils;
import online.motohub.util.ZoomImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static online.motohub.retrofit.RetrofitClient.DELETE_MY_PROFILE_IMAGE;

public class ProfileImgGalleryActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.parent_view)
    LinearLayout mParentLayout;

    @BindView(R.id.profile_gallery_rv)
    RecyclerView mGalleryRv;

    @BindView(R.id.profile_gallery_pager)
    ViewPager mViewPager;

    @BindView(R.id.view_pager_lay)
    RelativeLayout mViewPagerLay;

    @BindView(R.id.profile_gallery_upload_image_view)
    FloatingActionButton mUpdateFAB;

    private GalleryImgAdapter mAdapter;
    private List<GalleryImgResModel> mGalleryResModels;
    private ProfileImgGalleryActivity.CustomPagerAdapter mCustomPagerAdapter;

    public static final String EXTRA_PROFILE = "extra_profile_data";

    private int mProfileID;
    private ActionMode mActionMode;
    private ArrayList<Integer> deleteList;

    private enum UpdateTask {
        NONE, FILE_UPLOAD, DATA_ENTRY_UPLOAD
    }

    private UpdateTask mUpdateTask = UpdateTask.NONE;


    BroadcastReceiver broadCastImageUploadStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                String status = intent.getStringExtra("status");
                if (status.equalsIgnoreCase("File Uploaded")) {
                    getGalleryImages();
                    showToast(getBaseContext(), "File Uploaded Successfully");
                } else {
                    showSnackBar(mParentLayout, "File Upload Failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.print(e.getMessage());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_gallery);
        ButterKnife.bind(this);
        initRV();
        getGalleryImages();

    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initRV() {

        setToolbar(mToolbar, getString(R.string.photos));
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        deleteList = new ArrayList<>();

        try {
            mProfileID = getIntent().getIntExtra(EXTRA_PROFILE, 0);
            // mProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        } catch (NullPointerException e) {
            e.printStackTrace();
            // mProfileResModel = null;
        }

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mGalleryRv.setLayoutManager(layoutManager);

        mGalleryResModels = new ArrayList<>();
        mAdapter = new GalleryImgAdapter(ProfileImgGalleryActivity.this, mGalleryResModels);
        mGalleryRv.setAdapter(mAdapter);
        //mAdapter.setOnItemClickListener(mOnItemClickListener);

        mCustomPagerAdapter = new CustomPagerAdapter(ProfileImgGalleryActivity.this, mGalleryResModels);
        mViewPager.setAdapter(mCustomPagerAdapter);

        if (getIntent().getBooleanExtra(AppConstants.FROM_OTHER_PROFILE, false)) {
            mUpdateFAB.setVisibility(View.GONE);
        }

        mGalleryRv.addOnItemTouchListener(new RecyclerTouchListener(this, mGalleryRv, new RecyclerClick_Listener() {
            @Override
            public void onClick(View view, int position) {
                //If ActionMode not null select item
                if (mActionMode != null) {
                    onListItemSelect(position);
                } else {
                    mViewPager.setCurrentItem(position);
                    visibleViewPager(true);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                //Select item on long click
                onListItemSelect(position);
            }
        }));
    }

    /*private final GalleryImgAdapter.OnItemClickListener mOnItemClickListener = new GalleryImgAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            mViewPager.setCurrentItem(position);
            visibleViewPager(true);
        }
    };*/

    private void visibleViewPager(boolean isViewpager) {
        mViewPagerLay.setVisibility(isViewpager ? View.VISIBLE : View.GONE);
        mGalleryRv.setVisibility(isViewpager ? View.GONE : View.VISIBLE);
    }

    private static final int IMAGE_PICKER_REQUEST_CODE = 121;

    @OnClick({R.id.profile_gallery_upload_image_view, R.id.toolbar_back_img_btn, R.id.cancel_btn})
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.profile_gallery_upload_image_view:
                Intent pickerIntent = new Intent(ProfileImgGalleryActivity.this, PickerImageActivity.class);
                pickerIntent.putExtra("profileid", mProfileID);
                startActivity(pickerIntent);
                break;
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                break;
            case R.id.cancel_btn:
                visibleViewPager(false);
                break;
        }

    }

    private void getGalleryImages() {

        mUpdateTask = UpdateTask.NONE;
        RetrofitClient.getRetrofitInstance().callGetImageGallery(ProfileImgGalleryActivity.this, "MotoID = " + String.valueOf(mProfileID),
                RetrofitClient.GET_GALLERY_DATA_RESPONSE);

    }

    private void uploadImageToServer() {

        if (mSelectedFile != null && mSelectedFile.isFile()) {

            mUpdateTask = UpdateTask.FILE_UPLOAD;
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mSelectedFile);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", mSelectedFile.getName(), requestBody);
            RetrofitClient.getRetrofitInstance().uploadImageFileToServer(this, filePart, RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE);

        } else {
            showSnackBar(mParentLayout, getString(R.string.select_file_to_upload));
        }

    }

    private void uploadPicture() {

        if (mImageResModel != null) {

            mUpdateTask = UpdateTask.DATA_ENTRY_UPLOAD;
            JsonObject obj = new JsonObject();
            obj.addProperty(GalleryImgModel.USER_ID, getUserId());
            obj.addProperty(GalleryImgModel.MOTO_ID, mProfileID);
            obj.addProperty(GalleryImgModel.GALLERY_IMG, getHttpFilePath(mImageResModel.getPath()));

            JsonArray jsonElements = new JsonArray();
            jsonElements.add(obj);

            RetrofitClient.getRetrofitInstance().postImgToGallery(ProfileImgGalleryActivity.this,
                    jsonElements, RetrofitClient.POST_GALLERY_DATA_RESPONSE);
        } else {
            showSnackBar(mParentLayout, getString(R.string.select_file_to_upload));
        }
    }

    private File mSelectedFile;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {

                case IMAGE_PICKER_REQUEST_CODE:
                    Uri selectedUri = (Uri) data.getExtras().get(PickerImageActivity.EXTRA_RESULT_DATA);
                    if (selectedUri != null) {
                        try {
                            mSelectedFile = compressedImgFile(selectedUri, GALLERY_IMAGE_NAME_TYPE, "");
                            uploadImageToServer();
                        } catch (Exception e) {
                            showSnackBar(mParentLayout, e.getMessage());
                        }
                    } else {
                        showSnackBar(mParentLayout, getString(R.string.picture_not_found));
                    }
                    break;
            }
        }
    }

    private ImageResModel mImageResModel = null;

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);

        switch (responseType) {
            case RetrofitClient.GET_GALLERY_DATA_RESPONSE:
                GalleryImgModel model = (GalleryImgModel) responseObj;
                if (model.getGalleryResModelList() != null && model.getGalleryResModelList().size() > 0) {
                    try {
                        mGalleryResModels.clear();
                        mGalleryResModels.addAll(model.getGalleryResModelList());
                        mAdapter.notifyDataSetChanged();
                        visibleViewPager(false);
                        mCustomPagerAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    showSnackBar(mParentLayout, getString(R.string.picture_not_found));
                }
                break;
            case RetrofitClient.POST_GALLERY_DATA_RESPONSE:
                model = (GalleryImgModel) responseObj;
                if (model.getGalleryResModelList() != null && model.getGalleryResModelList().size() > 0) {
                    getGalleryImages();
                }
                break;
            case RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE:
                ImageModel imageModel = (ImageModel) responseObj;
                if (imageModel.getmModels() != null) {
                    if (mSelectedFile != null) {
                        mSelectedFile.deleteOnExit();
                    }
                    mImageResModel = imageModel.getmModels().get(0);
                    uploadPicture();
                } else {
                    showSnackBar(mParentLayout, getString(R.string.try_again));
                }
                break;
            case RetrofitClient.UPDATE_SESSION_RESPONSE:
                SessionModel mSessionModel = (SessionModel) responseObj;
                if (mSessionModel.getSessionToken() == null) {
                    PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
                } else {
                    PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
                }

                switch (mUpdateTask) {
                    case NONE:
                        getGalleryImages();
                        break;
                    case FILE_UPLOAD:
                        uploadImageToServer();
                        break;
                    case DATA_ENTRY_UPLOAD:
                        uploadPicture();
                        break;
                }
                break;
            case RetrofitClient.DELETE_MY_PROFILE_IMAGE:
                DeleteProfileImagesResponse deleteProfileImagesResponse = (DeleteProfileImagesResponse) responseObj;
                if (deleteProfileImagesResponse.getResource() != null) {
                    showSnackBar(mParentLayout, getString(R.string.img_delete));
                } else {
                    showSnackBar(mParentLayout, getString(R.string.try_again));
                }
                break;
        }
    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
        showSnackBar(mParentLayout, getString(R.string.internet_failure));
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mParentLayout, mErrorMsg);
        }
    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
        showSnackBar(mParentLayout, message);
    }


    private class CustomPagerAdapter extends PagerAdapter {

        private final Context mContext;
        private final List<GalleryImgResModel> mImgUriList;
        private final LayoutInflater mInflater;


        CustomPagerAdapter(Context context, List<GalleryImgResModel> imgUriList) {
            mContext = context;
            mImgUriList = imgUriList;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public Object instantiateItem(ViewGroup parent, int pos) {
            View convertView = mInflater.inflate(R.layout.row_picker_img_pager_thumbnail, parent, false);
            ZoomImageView mImageView = convertView.findViewById(R.id.row_picker_img_pager_thumbnail_image_view);

            Glide.with(mContext)
                    .load(UrlUtils.AWS_S3_BASE_URL + mImgUriList.get(pos).getGalleryImage())
                    .apply(new RequestOptions()
                            .error(R.drawable.img_place_holder))
                    .into(mImageView);

            parent.addView(convertView);
            return convertView;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return mImgUriList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(this.broadCastImageUploadStatus, new IntentFilter("UPLOAD_IMAGE_STATUS"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(this.broadCastImageUploadStatus);
    }

    @Override
    public void onBackPressed() {
        if (mViewPagerLay.getVisibility() == View.VISIBLE) {
            visibleViewPager(false);
        } else {
            finish();
        }
    }


    //List item select method
    private void onListItemSelect(int position) {
        mAdapter.toggleSelection(position);//Toggle the selection

        boolean hasCheckedItems = mAdapter.getSelectedCount() > 0;//Check if any items are already selected or not

        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = this.startSupportActionMode(new ToolbarActionModeCallback(this, mAdapter, mGalleryResModels));
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
        try {
            SparseBooleanArray selected = mAdapter.getSelectedIds();//Get selected ids
            JSONObject jsonObjectIds = new JSONObject();

            //Loop all selected ids
            for (int i = (selected.size() - 1); i >= 0; i--) {
                if (selected.valueAt(i)) {
                    //If current id is selected remove the item via key
                    deleteList.add(mGalleryResModels.get(i).getId());
                    //jsonObjectIds.put(GalleryImgModel.USER_ID, String.valueOf(mGalleryResModels.get(selected.keyAt(i)).getId()));
                    mGalleryResModels.remove(selected.keyAt(i));
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
            callDeleteImagesMyProfile(finalObject, DELETE_MY_PROFILE_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callDeleteImagesMyProfile(JsonObject object, final int responseType) {
        DialogManager.showProgress(this);
        // String filter = "UserID=" + userId;
        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callDeleteImageGallery(object)
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
}
