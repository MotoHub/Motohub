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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
import online.motohub.dialog.DialogManager;
import online.motohub.interfaces.PermissionCallback;
import online.motohub.model.LocalFolderModel;
import online.motohub.model.LocalImgModel;


public class PickerPostVideoActivity extends BaseActivity {

    public static final String EXTRA_RESULT_DATA = "activity_video_picker_uri";
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
    @BindString(R.string.storage_permission_denied)
    String mNoStoragePer;
    private GalleryPickerAdapter mAdapter;
    private List<LocalImgModel> mVideoUriList = new ArrayList<>();
    private List<LocalImgModel> mFolderVideoUriList = new ArrayList<>();
    private List<LocalFolderModel> mFolderList = new ArrayList<>();
    private boolean isFolderState = true;
    GalleryPickerAdapter.OnItemClickListener mImageFileCallback = new GalleryPickerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(boolean isFolder, int position) {
            if (isFolder) {
                isFolderState = false;
                int folderId = mFolderList.get(position).getId();
                new GetVideoFiles(folderId).execute();
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

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initView() {

        setToolbar(mToolbar, getString(R.string.videos));
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        mLayoutManager = new GridLayoutManager(this, 1);
        mImgListView.setLayoutManager(mLayoutManager);
        mImgListView.setItemAnimator(new DefaultItemAnimator());

        mVideoUriList = new ArrayList<>();
        mFolderList = new ArrayList<>();

        mAdapter = new GalleryPickerAdapter(PickerPostVideoActivity.this, GalleryPickerAdapter.IMAGE_FOLDER_TYPE);
        mImgListView.setAdapter(mAdapter);

        mAdapter.setImgFolderList(mFolderList);
        mAdapter.setOnItemClickListener(mImageFileCallback);

        new GetVideoFolders().execute();
    }

    @OnClick({R.id.cancel_btn, R.id.select_btn, R.id.toolbar_back_img_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_btn:
                visibleViewPager(false);
                break;
            case R.id.select_btn:
                uploadVideoFile();
                break;
            case R.id.toolbar_back_img_btn:
                onBackClicked();
                break;
        }
    }

    private void uploadVideoFile() {
        int uriPosition = mViewPager.getCurrentItem();
        LocalImgModel localImgModel = mVideoUriList.get(uriPosition);
        Intent pickerIntent = getIntent();
        pickerIntent.putExtra(EXTRA_RESULT_DATA, localImgModel.getFileUri().toString());
        setResult(RESULT_OK, pickerIntent);
        finish();
    }

    private void setFolderAdapter() {
        mLayoutManager.setSpanCount(2);
        if (mAdapter == null) {
            mAdapter = new GalleryPickerAdapter(PickerPostVideoActivity.this, GalleryPickerAdapter.IMAGE_FOLDER_TYPE);
            mAdapter.setImgFolderList(mFolderList);
            mAdapter.setImgList(mFolderVideoUriList);
            mAdapter.setGalleryType(GalleryPickerAdapter.IMAGE_FOLDER_TYPE);
            mAdapter.setOnItemClickListener(mImageFileCallback);
            mImgListView.setAdapter(mAdapter);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setImgFolderList(mFolderList);
                    mAdapter.setImgList(mFolderVideoUriList);
                    mAdapter.setGalleryType(GalleryPickerAdapter.IMAGE_FOLDER_TYPE);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }


    }

    private void setImageAdapter() {
        mLayoutManager.setSpanCount(3);
        if (mAdapter == null) {
            mAdapter = new GalleryPickerAdapter(PickerPostVideoActivity.this, GalleryPickerAdapter.VIDEO_TYPE);
            mAdapter.setImgList(mVideoUriList);
            mAdapter.setGalleryType(GalleryPickerAdapter.VIDEO_TYPE);
            mAdapter.setOnItemClickListener(mImageFileCallback);
            mImgListView.setAdapter(mAdapter);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setImgList(mVideoUriList);
                    mAdapter.setGalleryType(GalleryPickerAdapter.VIDEO_TYPE);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void visibleViewPager(boolean isViewpager) {
        mViewPagerLay.setVisibility(isViewpager ? View.VISIBLE : View.GONE);
        mImgListView.setVisibility(isViewpager ? View.GONE : View.VISIBLE);
    }

    private List<LocalImgModel> getVideos(int folderId) {
        List<LocalImgModel> mImgModels = new ArrayList<>();
        Uri mExternalUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        Cursor mCursor = getContentResolver().query(mExternalUri, null, MediaStore.Video.Media.BUCKET_ID + "=?",
                new String[]{String.valueOf(folderId)}, MediaStore.Video.Media.DATE_MODIFIED + " DESC");

        if (mCursor != null && mCursor.moveToFirst()) {
            do {

                LocalImgModel model = new LocalImgModel();

                int id = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
                String fileName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                String folderName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                String data = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));

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

    private List<LocalImgModel> getVideosFolder(List<LocalFolderModel> folderList) {
        List<LocalImgModel> mImgModels = new ArrayList<>();
        Uri mExternalUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        for (int i = 0; i < folderList.size() - 1; i++) {

            Cursor mCursor = getContentResolver().query(mExternalUri, null, MediaStore.Video.Media.BUCKET_ID + "=?",
                    new String[]{String.valueOf(folderList.get(i).getId())}, MediaStore.Video.Media.DATE_MODIFIED + " DESC");

            if (mCursor != null && mCursor.moveToFirst()) {

                LocalImgModel model = new LocalImgModel();

                int id = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
                String fileName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                String folderName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                String data = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));

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

        Uri mExternalUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {"DISTINCT " + MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATA, MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.DATE_TAKEN};

        String orderBy = MediaStore.Video.Media.DATE_MODIFIED + " DESC";

        Cursor mCursor = getContentResolver().query(mExternalUri, projection, null, null, orderBy);

        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                LocalFolderModel model = new LocalFolderModel();

                String folderName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                int id = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID));
                String folderPath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));

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
                setFolderAdapter();
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

    private class GetVideoFolders extends AsyncTask<Void, List<LocalFolderModel>, List<LocalFolderModel>> {

        @Override
        protected void onPreExecute() {
            DialogManager.showProgress(PickerPostVideoActivity.this);
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
            new GetVideoFiles("folderimage").execute();
        }
    }

    private class GetVideoFiles extends AsyncTask<Void, List<LocalImgModel>, List<LocalImgModel>> {
        private int folderId;
        private String folderimage;

        GetVideoFiles(int folderId) {
            this.folderId = folderId;
        }

        GetVideoFiles(String folder) {
            this.folderimage = folder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (folderimage == null) {
                DialogManager.showProgress(PickerPostVideoActivity.this);
            }
        }

        @Override
        protected List<LocalImgModel> doInBackground(Void... params) {

            List<LocalImgModel> mUris = null;
            if (folderimage != null) {
                mUris = getVideosFolder(mFolderList);

                if (mUris == null) {
                    mUris = new ArrayList<>();
                }

            } else {
                mUris = getVideos(folderId);
                if (mUris == null) {
                    mUris = new ArrayList<>();
                }
            }
            return mUris;
        }

        @Override
        protected void onPostExecute(List<LocalImgModel> mUriList) {
            super.onPostExecute(mUriList);
            if (folderimage != null) {
                mFolderVideoUriList.clear();
                mFolderVideoUriList.addAll(mUriList);
                setFolderAdapter();
            } else {
                mVideoUriList.clear();
                mVideoUriList.addAll(mUriList);
                setImageAdapter();
                CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(PickerPostVideoActivity.this, mVideoUriList);
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
            View convertView = mInflater.inflate(R.layout.row_picker_img_pager_thumbnail1, parent, false);
            ImageView mImageView = convertView.findViewById(R.id.row_picker_img_pager_thumbnail_image_view);

            RelativeLayout mImgLay = convertView.findViewById(R.id.main_lay);
            ImageView mPlayIcon = convertView.findViewById(R.id.play_icon);
            mPlayIcon.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(mImgUriList.get(pos).getFileUri().getPath())
                    .apply(new RequestOptions().error(R.drawable.settings_icon))
                    .into(mImageView);

            mImgLay.setTag(pos);
            mImgLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int selPos = (int) view.getTag();
                    moveLoadVideoPreviewScreen(mContext, mImgUriList.get(selPos).getFileUri().toString());
                }
            });
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
