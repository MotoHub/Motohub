package online.motohub.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.GalleryPickerAdapter;
import online.motohub.database.DatabaseHandler;
import online.motohub.interfaces.PermissionCallback;
import online.motohub.model.LocalFolderModel;
import online.motohub.model.LocalImgModel;
import online.motohub.model.VideoUploadModel;
import online.motohub.util.DialogManager;
import online.motohub.util.ProfileImageService;
import online.motohub.util.ZoomImageView;


public class PickerImageActivity extends BaseActivity {

    public static final String EXTRA_RESULT_DATA = "activity_image_picker_uri";
    @BindView(R.id.pick_img_parent_view)
    FrameLayout mParentView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.img_files_list_view)
    RecyclerView mImgListView;
    @BindView(R.id.pager)
    ViewPager mViewPager;
    @BindView(R.id.view_pager_lay)
    RelativeLayout mViewPagerLay;
    @BindView(R.id.select_btn)
    Button mSelectViewPagerBtn;
    @BindString(R.string.storage_permission_denied)
    String mNoStoragePer;
    Integer ProfileID;
    private GalleryPickerAdapter mAdapter;
    private List<LocalImgModel> mImgUriList = new ArrayList<>();
    private List<LocalFolderModel> mFolderList = new ArrayList<>();
    private boolean isFolderState = true;
    GalleryPickerAdapter.OnItemClickListener mImageFileCallback = new GalleryPickerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(boolean isFolder, int position) {
            if (isFolder) {
                isFolderState = false;
                int folderId = mFolderList.get(position).getId();
                new GetImageFiles(folderId).execute();
            } else {
                visibleViewPager(true);
                mViewPager.setCurrentItem(position);
            }
        }
    };
    private GridLayoutManager mLayoutManager;
    PermissionCallback mPermissionCallback = new PermissionCallback() {
        @Override
        public void permissionOkClick() {
            initView();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        ButterKnife.bind(this);

        if (isPermissionAdded())
            initView();
    }

    private void initView() {
        setToolbar(mToolbar, getString(R.string.photos));
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        mLayoutManager = new GridLayoutManager(this, 1);
        mImgListView.setLayoutManager(mLayoutManager);
        mImgListView.setItemAnimator(new DefaultItemAnimator());
        if (getIntent() != null) {
            mSelectViewPagerBtn.setText(getString(R.string.upload));
            ProfileID = getIntent().getIntExtra("profileid", 0);
        }
        mImgUriList = new ArrayList<>();
        mFolderList = new ArrayList<>();
        new GetImageFolders().execute();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    @OnClick({R.id.cancel_btn, R.id.select_btn, R.id.toolbar_back_img_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_btn:
                visibleViewPager(false);
                break;
            case R.id.select_btn:
                visibleViewPager(false);
                int uriPosition = mViewPager.getCurrentItem();
                LocalImgModel selectedUri = mImgUriList.get(uriPosition);
                DatabaseHandler databaseHandler = new DatabaseHandler(this);
                Intent service_intent = new Intent(this, ProfileImageService.class);
                int count = databaseHandler.getPendingCount();
                int notificationcount = count + 1;
                VideoUploadModel videoUploadModel = new VideoUploadModel();
                // File imgfile=new File(selectedUri.getFileName());
                String s = String.valueOf(selectedUri.getFileUri());
                videoUploadModel.setThumbnailURl(s.substring(s.lastIndexOf("/") + 1, s.length()));
                videoUploadModel.setProfileID(ProfileID);
                videoUploadModel.setNotificationflag(notificationcount);
                databaseHandler.addVideoDetails(videoUploadModel);
                service_intent.putExtra("imagefile", String.valueOf(selectedUri.getFileUri()));
                service_intent.putExtra("profileid", ProfileID);
                service_intent.putExtra("notification", notificationcount);
                startService(service_intent);
                Toast.makeText(this, "Your Image Started Uploading", Toast.LENGTH_SHORT).show();
                finish();
                //   showSnackBar(mCoordinatorLayout, getString(R.string.post_video_uploading));
                break;
            case R.id.toolbar_back_img_btn:
                onBackClicked();
                break;
        }
    }

    private void visibleViewPager(boolean isViewpager) {
        mViewPagerLay.setVisibility(isViewpager ? View.VISIBLE : View.GONE);
        mImgListView.setVisibility(isViewpager ? View.GONE : View.VISIBLE);
    }

    private List<LocalImgModel> getImages(int folderId) {
        List<LocalImgModel> mImgModels = new ArrayList<>();
        Uri mExternalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Cursor mCursor = getContentResolver().query(mExternalUri, null, MediaStore.Images.Media.BUCKET_ID + "=?",
                new String[]{String.valueOf(folderId)}, MediaStore.MediaColumns.DATE_MODIFIED + " DESC");

        if (mCursor != null && mCursor.moveToFirst()) {
            do {

                LocalImgModel model = new LocalImgModel();

                int id = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media._ID));
                String fileName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                String folderName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String data = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

                model.setId(id);
                model.setFileName(fileName);
                model.setFolderName(folderName);
                model.setFolderId(folderId);
                model.setFileUri(Uri.parse(data));
                mImgModels.add(model);
            } while (mCursor.moveToNext());

            mCursor.close();
        }
        return mImgModels;

    }

    private List<LocalImgModel> getImagesFolder(List<LocalFolderModel> folderList) {
        List<LocalImgModel> mImgModels = new ArrayList<>();
        Uri mExternalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        for (int i = 0; i < folderList.size() - 1; i++) {

            Cursor mCursor = getContentResolver().query(mExternalUri, null, MediaStore.Images.Media.BUCKET_ID + "=?",
                    new String[]{String.valueOf(folderList.get(i).getId())}, MediaStore.Images.Media.DATE_MODIFIED + " DESC");

            if (mCursor != null && mCursor.moveToFirst()) {

                LocalImgModel model = new LocalImgModel();

                int id = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media._ID));
                String fileName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                String folderName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String data = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

                model.setId(id);
                model.setFileName(fileName);
                model.setFolderName(folderName);
                model.setFolderId(folderList.get(i).getId());
                model.setFileUri(Uri.parse(data));
                mImgModels.add(model);


                mCursor.close();
            }
        }
        return mImgModels;

    }

    private List<LocalFolderModel> getFolders() {
        List<LocalFolderModel> mFolders = new ArrayList<>();

        Uri mExternalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String projection[] = {"DISTINCT " + MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.DATE_TAKEN};

        String orderBy = MediaStore.Images.Media.DATE_MODIFIED + " DESC";

        Cursor mCursor = getContentResolver().query(mExternalUri, projection, null, null, orderBy);

        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                LocalFolderModel model = new LocalFolderModel();

                String folderName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                int id = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                String folderPath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

                model.setDisplayName(folderName);
                model.setPath(folderPath);
                model.setId(id);

                boolean isExists = false;
                for (int i = 0; i < mFolders.size(); i++) {
                    LocalFolderModel folderModel = mFolders.get(i);
                    if (folderModel.getId() == model.getId()) {
                        isExists = true;
                        break;
                    }
                }

                if (!isExists) {
                    mFolders.add(model);
                }

            } while (mCursor.moveToNext());
            mCursor.close();

        }

        return mFolders;
    }

    @Override
    public void onBackPressed() {
        onBackClicked();
    }

    private void onBackClicked() {
        if (mViewPagerLay.getVisibility() == View.VISIBLE) {
            visibleViewPager(false);
            isFolderState = false;
        } else {
            if (isFolderState) {
                finish();
            } else {
                isFolderState = true;
                new GetImageFolders().execute();
            }
        }

    }

    public boolean isPermissionAdded() {
        boolean addPermission = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int readStoragePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int storagePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            List<String> listPermissionsNeeded = new ArrayList<>();
            if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                addPermission = isPermission(mPermissionCallback, listPermissionsNeeded);
            }
        }
        return addPermission;
    }

    private class GetImageFolders extends AsyncTask<Void, List<LocalFolderModel>, List<LocalFolderModel>> {

        @Override
        protected void onPreExecute() {
            DialogManager.showProgress(PickerImageActivity.this);
        }

        @Override
        protected List<LocalFolderModel> doInBackground(Void... params) {
            List<LocalFolderModel> mUris = getFolders();
            if (mUris == null) {
                mUris = new ArrayList<>();
            }
            return mUris;
        }

        @Override
        protected void onPostExecute(List<LocalFolderModel> mUriList) {
            super.onPostExecute(mUriList);
            mFolderList.clear();
            mFolderList.addAll(mUriList);
            if (mImgUriList.size() != 0) {
                mImgUriList.clear();
            }
            mLayoutManager.setSpanCount(2);
            new GetImageFiles("folderimage").execute();
        }
    }

    private class GetImageFiles extends AsyncTask<Void, List<LocalImgModel>, List<LocalImgModel>> {
        private int folderId;
        private String folderimage;

        GetImageFiles(int folderId) {
            this.folderId = folderId;
        }

        GetImageFiles(String folder) {
            this.folderimage = folder;
        }

        @Override
        protected List<LocalImgModel> doInBackground(Void... params) {
            List<LocalImgModel> mUris = null;

            if (folderimage != null) {
                mUris = getImagesFolder(mFolderList);

                if (mUris == null) {
                    mUris = new ArrayList<>();
                }

            } else {
                mUris = getImages(folderId);
                if (mUris == null) {
                    mUris = new ArrayList<>();
                }
            }
            return mUris;
        }

        @Override
        protected void onPostExecute(List<LocalImgModel> mUriList) {
            super.onPostExecute(mUriList);
            mImgUriList.clear();
            mImgUriList.addAll(mUriList);
            if (folderimage != null) {
                mAdapter = new GalleryPickerAdapter(PickerImageActivity.this, GalleryPickerAdapter.IMAGE_FOLDER_TYPE);
                mAdapter.setImgFolderList(mFolderList);
                mAdapter.setImgList(mImgUriList);
                mAdapter.setGalleryType(GalleryPickerAdapter.IMAGE_FOLDER_TYPE);
                mAdapter.setOnItemClickListener(mImageFileCallback);
                mImgListView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            } else {
                mLayoutManager.setSpanCount(3);
                mAdapter.setImgList(mImgUriList);
                mAdapter.setGalleryType(GalleryPickerAdapter.IMAGE_TYPE);
                mAdapter.notifyDataSetChanged();
                CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(PickerImageActivity.this, mImgUriList);
                mViewPager.setAdapter(mCustomPagerAdapter);
                visibleViewPager(false);
            }
            DialogManager.hideProgress();
        }
    }

    private class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;
        private List<LocalImgModel> mImgUriList;
        private LayoutInflater mInflater;


        CustomPagerAdapter(Context context, List<LocalImgModel> imgUriList) {
            mContext = context;
            mImgUriList = imgUriList;

            mInflater = LayoutInflater.from(mContext);

        }

        @Override
        public Object instantiateItem(ViewGroup parent, int pos) {
            View convertView = mInflater.inflate(R.layout.row_picker_img_pager_thumbnail, parent, false);
            ZoomImageView mImageView = convertView.findViewById(R.id.row_picker_img_pager_thumbnail_image_view);
            Glide.with(mContext)
                    .load(mImgUriList.get(pos).getFileUri().getPath())
                    .apply(new RequestOptions().error(R.drawable.settings_icon))
                    .into(mImageView);
            parent.addView(convertView);
            return convertView;
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
}
