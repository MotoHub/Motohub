package online.motohub.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.yalantis.contextmenu.lib.MenuObject;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.ButterKnife;
import online.motohub.R;
import online.motohub.adapter.EventsFindAdapter;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.CommonInterface;
import online.motohub.interfaces.PermissionCallback;
import online.motohub.interfaces.SendVideoUrl;
import online.motohub.model.ErrorMessage;
import online.motohub.model.EventsModel;
import online.motohub.model.ImageModel;
import online.motohub.model.OndemandNewResponse;
import online.motohub.model.PostsModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SingleChatRoomResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.dialog.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.UrlUtils;
import online.motohub.util.ZoomImageView;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    public static final int PERMISSION_IMAGE_CAMERA = 1;
    public static final int PERMISSION_IMAGE_GALLERY = 2;
    public static final int PERMISSION_VIDEO_CAMERA = 3;
    public static final int PERMISSION_VIDEO_GALLERY = 4;
    public static final int PERMISSION_VIDEO_GALLERY1 = 6;
    public static final int PERMISSION_SHARING_WRITE_ACCESS = 5;
    public static final String BIKE = "1";
    public static final String BOAT = "2";
    public static final String CAR = "3";
    public static final String KART = "4";
    public static final String SPECTATOR = "5";
    public static final String BIKE_STR = "Bike";
    public static final String BOAT_STR = "Boat";
    public static final String CAR_STR = "Car";
    public static final String KART_STR = "Kart";
    public static final int CAMERA_PERMISSION_REQ_CODE = 1, STORAGE_PERMISSION_REQ_CODE = 2, CAMERA_CAPTURE_REQ = 1, GALLERY_PIC_REQ = 2, IN_APP_PURCHASE = 15551,
            ACTION_TAKE_VIDEO = 95, GALLERY_VIDEO_REQ = 96;
    public static final String CREATE_PROF_AFTER_REG = "create_profile_after_registration";
    //Profile Types
    protected static final String PROFILE_TYPE = "profile_type";
    protected static final String PROFILE_PURCHASED = "0";
    protected static final String SPECTATOR_STR = "Spectator";
    //In-App Billing SKU/Product_ID
    protected static final String SKU_BIKE_MONTHLY = "bike_monthly_profile";
    protected static final String SKU_BIKE_YEARLY = "bike_yearly_profile";
    protected static final String SKU_BOAT_MONTHLY = "boat_monthly_profile";
    protected static final String SKU_BOAT_YEARLY = "boat_yearly_profile";
    protected static final String SKU_CAR_MONTHLY = "car_monthly_profile";
    protected static final String SKU_CAR_YEARLY = "car_yearly_profile";
    protected static final String SKU_KART_MONTHLY = "kart_monthly_profile";
    protected static final String SKU_KART_YEARLY = "kart_yearly_profile";
    protected static final String MONTHLY = "monthly";
    protected static final String YEARLY = "yearly";
    //file name type
    protected static final String GALLERY_IMAGE_NAME_TYPE = "gallery_img";
    protected static final String GALLERY_VIDEO_NAME_TYPE = "gallery_vid";
    protected static final String PROFILE_IMAGE_NAME_TYPE = "profile";
    protected static final String COVER_IMAGE_NAME_TYPE = "cover";
    protected static final String POST_IMAGE_NAME_TYPE = "post";
    protected static final String GROUP_IMAGE_NAME_TYPE = "grp_chat_img";
    public static int PERMISSION_ACTION_TYPE = 0;
    public SendVideoUrl sendVideoUrl;
    public Dialog mPurchaseDialog, mPurchaseSuccessDialog;
    public List<String> listPermissionsNeeded;

    public int settingsReqCode = 143;
    @BindString(R.string.no_profile_found_err)
    protected String mNoProfileErr;
    @BindString(R.string.internet_failure)
    String mInternetFailed;
    @BindString(R.string.session_updated)
    String mSessionUpdated;
    CommonInterface mConfirmCallback = new CommonInterface() {
        @Override
        public void onSuccess() {
            Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
            myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
            myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(myAppSettings, settingsReqCode);
//            mActivity.finish();
        }

    };
    private AppCompatActivity mActivity;
    private long mLastClickTime = 0;
    private String[] mShareVideoUrl;
    private Uri mVideoFileUri;
    private String COMPRESSED_VIDEO_FOLDER = "MotoHub";
    private String mPhoneNumbers = "", mEmailIDs = "";
    private int permsRequestCode = 200;
    private PermissionCallback mPermissionCallback = null;
    PermissionCallback permissionCallback = new PermissionCallback() {
        @Override
        public void permissionOkClick() {

            switch (PERMISSION_ACTION_TYPE) {
                case PERMISSION_IMAGE_CAMERA:
                    cameraIntent();
                    break;
                case PERMISSION_IMAGE_GALLERY:
                    galleryIntent();
                    break;
                case PERMISSION_VIDEO_CAMERA:
                    cameraIntentVideo();
                    break;
                case PERMISSION_VIDEO_GALLERY:
                    galleryIntentVideo();
                    break;
                case PERMISSION_VIDEO_GALLERY1:
                    galleryIntentVideo1();
                    break;
            }
        }
    };
    CommonInterface mAlertCallback = new CommonInterface() {
        @Override
        public void onSuccess() {
            isPermission(mPermissionCallback, listPermissionsNeeded);
        }


    };

    private static String getRealPathFromURI(Context mContext, Uri contentURI) {
        Cursor cursor = mContext.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    @SuppressLint("DefaultLocale")
    public static String convertToSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f%c",
                count / Math.pow(1000, exp),
                "kmgtpe".charAt(exp - 1));
    }

    public String getProfileTypeStr(String profileType) {
        switch (profileType) {
            case BIKE:
                return BIKE_STR;
            case BOAT:
                return BOAT_STR;
            case CAR:
                return CAR_STR;
            case KART:
                return KART_STR;
            case SPECTATOR:
                return SPECTATOR_STR;
            default:
                return BIKE_STR;
        }
    }

    /**
     * Don't use/change this String "mCompressedVideoPath" value anywhere in the app.
     */
    //public String mCompressedVideoPath = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActivity = this;
    }

    public BaseActivity getActivity() {
        return this;
    }

    protected void setToolbar(Toolbar toolbar, String toolbarTitle) {
        if (toolbar != null) {
            toolbar.setTitle("");
            toolbar.setContentInsetsAbsolute(0, 0);
            TextView mToolbarTitle = ButterKnife.findById(toolbar, R.id.toolbar_title);
            mToolbarTitle.setText(toolbarTitle);
            setSupportActionBar(toolbar);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void movePostCommentScreen(Context mContext, int postID, ProfileResModel mMyProfileModel) {
        //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileModel);
        EventBus.getDefault().postSticky(mMyProfileModel);
        Intent mPostCommentsActivity = new Intent(mContext, PostCommentsActivity.class)
                .putExtra(PostsModel.POST_ID, postID)
                /*.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileModel)*/;
        ((BaseActivity) mContext).startActivityForResult(mPostCommentsActivity, AppConstants.POST_COMMENT_REQUEST);
    }

    public void moveVideoCommentScreen(Context mContext, int postID, ProfileResModel mMyProfileModel) {
        //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileModel);
        EventBus.getDefault().postSticky(mMyProfileModel);
        Intent mPostCommentsActivity = new Intent(mContext, VideoCommentsActivity.class).putExtra(AppConstants.VIDEO_ID, postID)
                /*.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileModel)*/;
        ((BaseActivity) mContext).startActivityForResult(mPostCommentsActivity, AppConstants.VIDEO_COMMENT_REQUEST);
    }

    public void moveLoadVideoScreen(Context mContext, String mVideoPath) {
        /**
         * Exo Player
         */
        Intent mLoadVideoFromServerActivity = new Intent(mContext, LoadVideoFromServerActivity.class).
                putExtra(AppConstants.VIDEO_PATH, mVideoPath);
        mContext.startActivity(mLoadVideoFromServerActivity);
    }

    public void LoadVideoScreen(Context mContext, String mVideoPath, int pos, int requestcode) {
        /**
         * Exo Player
         */
        Intent mLoadVideoFromServerActivity = new Intent(mContext, LoadVideoFromServerActivity.class)
                .putExtra(AppConstants.VIDEO_PATH, mVideoPath)
                .putExtra(AppConstants.POSITION, pos);
        this.startActivityForResult(mLoadVideoFromServerActivity, requestcode);
    }

    public void moveLoadVideoPreviewScreen(Context mContext, String mVideoPath) {
        /**
         * Video View
         */
        Intent mViewImageActivity = new Intent(mContext, VideoPreviewScreen.class).
                putExtra(AppConstants.VIDEO_PATH, mVideoPath);
        mContext.startActivity(mViewImageActivity);
    }

    public void moveLoadImageScreen(Context mContext, String mImgPath) {
        Intent mViewImageActivity = new Intent(mContext, ViewImageActivity.class).
                putExtra(ImageModel.POST_IMAGE, mImgPath);
        mContext.startActivity(mViewImageActivity);
    }

    public void moveOtherProfileScreen(Context mContext, int mMyProfileID, int mOtherProfileID) {
        Intent mViewImageActivity = new Intent(mContext, OthersMotoFileActivity.class).
                putExtra(AppConstants.MY_PROFILE_ID, mMyProfileID).
                putExtra(AppConstants.OTHER_PROFILE_ID, mOtherProfileID);
        mContext.startActivity(mViewImageActivity);
    }

    public void moveOtherProfileScreenWithResult(Context mContext, int mMyProfileID, int mOtherProfileID, int mRequestCode) {
        Intent mViewImageActivity = new Intent(mContext, OthersMotoFileActivity.class).
                putExtra(AppConstants.MY_PROFILE_ID, mMyProfileID).
                putExtra(AppConstants.OTHER_PROFILE_ID, mOtherProfileID);
        this.startActivityForResult(mViewImageActivity, mRequestCode);
    }

    public void moveMyProfileScreen(Context mContext, int mMyProfileID) {
        Intent mViewImageActivity = new Intent(mContext, MyMotoFileActivity.class).
                putExtra(AppConstants.MY_PROFILE_ID, mMyProfileID);
        mContext.startActivity(mViewImageActivity);
    }

    public void moveMyProfileScreenWithResult(Context mContext, int mMyProfileID, int mRequestCode) {
        Intent mViewImageActivity = new Intent(mContext, MyMotoFileActivity.class).
                putExtra(AppConstants.MY_PROFILE_ID, mMyProfileID);
        this.startActivityForResult(mViewImageActivity, mRequestCode);
    }

    public boolean isMultiClicked() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
            return true;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return false;
    }

    public String[] getImgVideoList(String str) {
        String[] mArray = null;
        if (str != null && !str.isEmpty()) {
            str = str.replace("]", "")
                    .replace("[", "")
                    .replace("\n", "")
                    .replace("\"", "")
                    .replace("\\", "")
                    .replace(" ", "");
            if (str.isEmpty())
                return mArray;

            mArray = str.split(",");
        }
        return mArray;
    }

    protected void setToolbarLeftBtn(Toolbar toolbar) {
        if (toolbar != null) {
            ImageButton mToolbarLeftBtn = ButterKnife.findById(toolbar, R.id.toolbar_back_img_btn);
            mToolbarLeftBtn.setVisibility(View.VISIBLE);
        }
    }

    protected void showToolbarBtn(Toolbar toolbar, int resourceID) {
        if (toolbar != null) {
            ImageView mToolbarRightImgBtn = ButterKnife.findById(toolbar, resourceID);
            mToolbarRightImgBtn.setVisibility(View.VISIBLE);
        }
    }

    public ArrayList<Bitmap> getBitmapImageGlide(String[] imgUrl) {
        final ArrayList<Bitmap> mBitmap = new ArrayList<>();
        try {
            for (String anImgUrl : imgUrl) {

                GlideUrl glideUrl = new GlideUrl(UrlUtils.AWS_FILE_URL + anImgUrl, new LazyHeaders.Builder()
                        .addHeader("X-DreamFactory-Api-Key", getString(R.string.dream_factory_api_key))
                        .build());

                RequestBuilder<Bitmap> mRequestBuilder = Glide
                        .with(this)
                        .asBitmap();
                mRequestBuilder
                        .load(glideUrl)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                mBitmap.add(resource);
                            }
                        });
            }
            return mBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return mBitmap;
        }


    }

    protected void showPopupMenu(View v) {
        PopupMenu mPopup = new PopupMenu(this, v);
        mPopup.inflate(R.menu.settings_menu);
        mPopup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        mPopup.show();
    }

    protected void showLogoutMenu(View v) {
        PopupMenu mPopup = new PopupMenu(this, v);
        mPopup.inflate(R.menu.logout_menu);
        mPopup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        mPopup.show();
    }

    public void showSnackBar(View view, String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.text_color));

        snackbar.show();
    }

    public void showColorSnackBar(View view, String msg) {

        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorOrange));
        snackbar.show();
    }

    protected void logResult(String result) {
    }

    public void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public void showAppDialog(final String dialogType, final ArrayList<String> mFollowProfileTypes) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    DialogFragment mDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(AppDialogFragment.TAG);
                    if (mDialogFragment != null && mDialogFragment.isAdded()) {
                        getSupportFragmentManager().beginTransaction().remove(mDialogFragment).commitAllowingStateLoss();
                    }
                    AppDialogFragment.newInstance(dialogType, mFollowProfileTypes).show
                            (getSupportFragmentManager(), AppDialogFragment.TAG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void dismissAppDialog() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                DialogFragment mDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(AppDialogFragment.TAG);
                if (mDialogFragment != null && mDialogFragment.isAdded() && mDialogFragment.isVisible()) {
                    mDialogFragment.dismissAllowingStateLoss();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    public boolean isNetworkConnected(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert conMgr != null;
        if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            return true;
        } else
            return conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.DISCONNECTED
                    && conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.DISCONNECTED;
    }

    public void alertDialogPositiveBtnClick(BaseActivity activity, String dialogType, StringBuilder profileTypesStr, ArrayList<String> profileTypes,
                                            int position) {
    }

    public void alertDialogNegativeBtnClick() {
        dismissAppDialog();
    }

    public String getCurrentDate() {
        DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm:ss", Locale.getDefault());
//        mDateFormat.setTimeZone(TimeZone.getTimeZone(AppConstants.NZ_TIME_ZONE));
        return mDateFormat.format(new Date());
    }

    public String getFormattedDate(String whenEventDate) {

        DateFormat mDateParseStrFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        // TimeZone mTimeZone = TimeZone.getTimeZone("New Zealand");
        // mDateParseStrFormat.setTimeZone(mTimeZone);
        DateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy @ hh:mm a", Locale.getDefault());
        try {
            Date mDate = mDateParseStrFormat.parse(whenEventDate);
            return mDateFormat.format(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getWhenEventStartingStr(String whenEventDate, String finishEventDate) {

        DateFormat mDateParseStrFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        // TimeZone mTimeZone = TimeZone.getTimeZone("New Zealand");
        // mDateParseStrFormat.setTimeZone(mTimeZone);

        try {

            Date mWhenEventDate = mDateParseStrFormat.parse(whenEventDate);
            Date mFinishEventDate = mDateParseStrFormat.parse(finishEventDate);

            Date mCurrentDate = new Date();

            if (mCurrentDate.compareTo(mWhenEventDate) < 0) {

                long mMilliSecTimeDiff = mWhenEventDate.getTime() - mCurrentDate.getTime();
                long mNoOfDays = TimeUnit.DAYS.convert(mMilliSecTimeDiff, TimeUnit.MILLISECONDS);
                long diff = mMilliSecTimeDiff;

                long mNoOfYears = mNoOfDays / 365;
                long mNoOfMonths = (mNoOfDays % 365) / 30;
                long mNoOfDay = (mNoOfDays % 365) % 30;


                // Calculate difference in minutes
                long diffMinutes = diff / (60 * 1000);

                // Calculate difference in hours
                long diffHours = diff / (60 * 60 * 1000);

                // Calculate difference in days
                long diffDays = (diff / (24 * 60 * 60 * 1000));

                if (diffHours > 24) {
                    diffDays += 1;
                }


                StringBuilder mStringBuilder = new StringBuilder();
                mStringBuilder.append("Starts in ");

                if (mNoOfYears > 0) {
                    mStringBuilder.append(mNoOfYears).append(mNoOfYears > 1 ? " years " : " year ");
                    return mStringBuilder.toString();
                } else if (mNoOfMonths > 0) {
                    String mTime = mNoOfMonths + (mNoOfMonths > 1 ? " months " : " month ");
                    if (mNoOfDay > 0)
                        mTime = mTime + mNoOfDay + (mNoOfDay > 1 ? " days" : " day");
                    mStringBuilder.append(mTime);
                    return mStringBuilder.toString();
                } else if (diffDays > 0) {
                    mStringBuilder.append(diffDays).append(diffDays > 1 ? " days" : " day");
                    return mStringBuilder.toString();
                } else if (diffHours > 0) {
                    mStringBuilder.append(diffHours).append(diffHours > 1 ? " hours" : " hour");
                    return mStringBuilder.toString();
                } else if (diffMinutes > 0) {
                    mStringBuilder.append(diffHours).append(diffMinutes > 1 ? " minutes" : " minute");
                    return mStringBuilder.toString();
                }


            } else if (mCurrentDate.compareTo(mWhenEventDate) >= 0 && mCurrentDate.compareTo(mFinishEventDate) <= 0) {
                return "Available in LIVE";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "Available in LIVE";

    }

    @SuppressLint("StaticFieldLeak")
    public void getUriVideo(final String[] videoUrl) {
        mShareVideoUrl = videoUrl;
        PERMISSION_ACTION_TYPE = PERMISSION_SHARING_WRITE_ACCESS;
        if (isPermissionAdded()) {

            final String uri = (UrlUtils.AWS_FILE_URL + videoUrl[0] + "?api_key="
                    + getResources().getString(R.string.dream_factory_api_key)
                    + "&session_token="
                    + PreferenceUtils.getInstance(this).getStrData(PreferenceUtils
                    .SESSION_TOKEN) + "&download=" + true);


            new AsyncTask<Void, Void, Void>() {
                File apkStorage = null;
                File outputFile = null;

                @Override
                protected void onPreExecute() {
                    DialogManager.showProgress(mActivity);
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
                            Toast.makeText(BaseActivity.this, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

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
                        sendVideoUrl = new AppDialogFragment();
                        //sendVideoUrl.SendData(mVideoFileUri, BaseActivity.this);
                        AppDialogFragment.getInstance().showFBVideoShareDialog(mVideoFileUri, BaseActivity.this);
                    } else {
                        showToast(getApplicationContext(), "Something went wrong!! Video could not been shared.");
                    }
                    super.onPostExecute(aVoid);

                }
            }.execute();
        }
    }

    public boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(

                Environment.MEDIA_MOUNTED);
    }

    public boolean isLiveEvent(String startDate, String endDate) {
        boolean isLive = false;
//        DateFormat mServerFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        mServerFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        DateFormat mLocalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        long current = Calendar.getInstance().getTimeInMillis();
        String currentDate = mLocalFormat.format(current);

        Date mStartDate = null, mEndDate = null, mCurrentDate = null;
        try {
            mStartDate = mLocalFormat.parse(startDate);
            mEndDate = mLocalFormat.parse(endDate);
            mCurrentDate = mLocalFormat.parse(currentDate);
            if (mCurrentDate.equals(mStartDate)) {
                isLive = true;
            } else if (mCurrentDate.equals(mEndDate)) {
                isLive = true;
            } else if (mCurrentDate.after(mStartDate) && mCurrentDate.before(mEndDate)) {
                isLive = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isLive;
    }

    public String findTime(String createDate) {

        DateFormat mServerFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        mServerFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        DateFormat mLocalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        long current = Calendar.getInstance().getTimeInMillis();
        String currentDate = mLocalFormat.format(current);

        Date mCreateDate = null, mCurrentDate = null;
        String diffTime = "";
        try {
            mCreateDate = mServerFormat.parse(createDate);
            mCurrentDate = mLocalFormat.parse(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert mCurrentDate != null;
        long diff = mCurrentDate.getTime() - mCreateDate.getTime();
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        int diffInDays = (int) (diff / (60 * 60 * 1000 * 24));
        int diffInMonths = diffInDays / 30;
        int diffInYear = diffInMonths / 12;
        if (diffInYear > 0) {
            if (diffInYear == 1) {
                diffTime = diffInYear + " Year ago";
            } else {
                diffTime = diffInYear + " Years ago";
            }
        } else if (diffInMonths > 0) {
            if (diffInMonths == 1) {
                diffTime = diffInMonths + " Month ago";
            } else {
                diffTime = diffInMonths + " Months ago";
            }
        } else if (diffInDays > 0) {
            if (diffInDays == 1) {
                diffTime = diffInDays + " Day ago";
            } else if (diffInDays >= 7) {
                if (diffInDays / 7 == 1) {
                    diffTime = diffInDays / 7 + " Week ago";
                } else {
                    diffTime = diffInDays / 7 + " Weeks ago";
                }
            } else {
                diffTime = diffInDays + " Days ago";
            }
        } else if (diffHours > 0) {
            if (diffHours == 1) {
                diffTime = diffHours + " Hour ago";
            } else {
                diffTime = diffHours + " Hours ago";
            }
        } else if (diffMinutes > 0) {
            if (diffMinutes == 1) {
                diffTime = diffMinutes + " Minute ago";
            } else {
                diffTime = diffMinutes + " Minutes ago";
            }
        } else {
            diffTime = "Just Now";
        }
        return diffTime;
    }

    public String getFinishedEventDateStr(String finishEventDate) {

        DateFormat mDateParseStrFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        TimeZone mTimeZone = TimeZone.getTimeZone("UTC");
        // mDateParseStrFormat.setTimeZone(mTimeZone);
        DateFormat mDateFormat = new SimpleDateFormat(" EEE MMM yyyy", Locale.getDefault());
        DateFormat mDayDateFormat = new SimpleDateFormat("d", Locale.getDefault());

        try {
            Date mDate = mDateParseStrFormat.parse(finishEventDate);
            String mTempStr = mDayDateFormat.format(mDate);
            if (mTempStr.endsWith("1")) {
                mTempStr = mTempStr + "st";
            } else if (mTempStr.endsWith("2")) {
                mTempStr = mTempStr + "nd";
            } else if (mTempStr.endsWith("3")) {
                mTempStr = mTempStr + "rd";
            } else {
                mTempStr = mTempStr + "th";
            }
            return "Ended on " + mTempStr + mDateFormat.format(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";

    }

    /**
     * Checks Camera Permission of this app.
     *
     * @return true if Camera Permission is given, returns false otherwise.
     */
    protected boolean checkCameraPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int mCamera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
            return mCamera == PackageManager.PERMISSION_GRANTED;
        }

        return true;

    }

    protected boolean checkPhoneContactsPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int mCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
            return mCamera == PackageManager.PERMISSION_GRANTED;
        }

        return true;

    }

    /**
     * Checks Read Storage Permission of this app.
     *
     * @return true if Read Storage Permission is given, returns false otherwise.
     */
    protected boolean checkStoragePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int mStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return mStorage == PackageManager.PERMISSION_GRANTED;
        }

        return true;

    }

    protected void requestContactPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQ_CODE);
    }

    /**
     * Requests user to give Camera Permission for this app for taking pictures using Camera.
     */
    protected void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQ_CODE);
    }

    /**
     * Requests user to give Read Storage Permission for this app for getting stored pictures from gallery.
     */
    protected void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQ_CODE);
    }

    /**
     * Creates Camera Intent that launches System Default Camera App.
     */
    public void cameraIntent() {
        PERMISSION_ACTION_TYPE = PERMISSION_IMAGE_CAMERA;
        if (isPermissionAdded()) {
            Uri mOutputFileUri = getImgUri();
            Intent mCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (mOutputFileUri != null) {
                mCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
            }
            startActivityForResult(mCameraIntent, CAMERA_CAPTURE_REQ);
            overridePendingTransition(R.anim.anim_bottom_up, R.anim.anim_bottom_down);
        }

    }

    public void cameraIntentVideo() {
        PERMISSION_ACTION_TYPE = PERMISSION_VIDEO_CAMERA;
        if (isPermissionAdded()) {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);

            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
            }
            overridePendingTransition(R.anim.anim_bottom_up, R.anim.anim_bottom_down);
        }
    }

    /**
     * Creates Gallery Intent that user to select pictures from the gallery.
     */
    public void galleryIntent() {
        PERMISSION_ACTION_TYPE = PERMISSION_IMAGE_GALLERY;
        if (isPermissionAdded()) {
            Intent mGalleryIntent = new Intent(BaseActivity.this, PickerPostImageActivity.class);
            startActivityForResult(mGalleryIntent, GALLERY_PIC_REQ);
            overridePendingTransition(R.anim.anim_bottom_up, R.anim.anim_bottom_down);
        }
    }

    public void galleryIntentMultiple() {
        PERMISSION_ACTION_TYPE = PERMISSION_IMAGE_GALLERY;
        if (isPermissionAdded()) {
            Intent mGalleryIntent = new Intent(AppConstants.ACTION_MULTIPLE_PICK);
            startActivityForResult(mGalleryIntent, 200);
            overridePendingTransition(R.anim.anim_bottom_up, R.anim.anim_bottom_down);
        }
    }

    public void galleryIntentVideo() {
        PERMISSION_ACTION_TYPE = PERMISSION_VIDEO_GALLERY;
        if (isPermissionAdded()) {
            Intent mGalleryIntent = new Intent(BaseActivity.this, PickerPostVideoActivity.class);
            startActivityForResult(mGalleryIntent, GALLERY_VIDEO_REQ);
            overridePendingTransition(R.anim.anim_bottom_up, R.anim.anim_bottom_down);
        }
    }

    public void galleryIntentVideo1() {
        PERMISSION_ACTION_TYPE = PERMISSION_VIDEO_GALLERY1;
        if (isPermissionAdded()) {
            Intent mGalleryIntent = new Intent(BaseActivity.this, PickerPostVideoForOnDemandActivity.class);
            startActivityForResult(mGalleryIntent, GALLERY_VIDEO_REQ);
            overridePendingTransition(R.anim.anim_bottom_up, R.anim.anim_bottom_down);
        }
    }

    public boolean isPermissionAdded() {
        boolean addPermission = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int permissionCamera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
            int readStoragePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int storagePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            List<String> listPermissionsNeeded = new ArrayList<>();
            if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
            }
            if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!listPermissionsNeeded.isEmpty()) {
                addPermission = isPermission(permissionCallback, listPermissionsNeeded);
            }
        }
        return addPermission;
    }

    public List<MenuObject> getMenuObjects() {

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setBgColor(ContextCompat.getColor(this, R.color.colorBlack));
        close.setResource(R.drawable.orange_close_icon);


        MenuObject newprofile = new MenuObject(getString(R.string.create_new_profile));
        newprofile.setBgColor(ContextCompat.getColor(this, R.color.colorBlack));
        newprofile.setResource(R.drawable.ic_new_profile);

        MenuObject otherprofile = new MenuObject(getString(R.string.other_profiles));
        otherprofile.setBgColor(ContextCompat.getColor(this, R.color.colorBlack));
        otherprofile.setResource(R.drawable.ic_other_profile);

        MenuObject notification = new MenuObject(getString(R.string.notifications));
        notification.setBgColor(ContextCompat.getColor(this, R.color.colorBlack));
        notification.setResource(R.drawable.ic_notification);

        MenuObject blockeduser = new MenuObject(getString(R.string.blocked_users));
        blockeduser.setBgColor(ContextCompat.getColor(this, R.color.colorBlack));
        blockeduser.setResource(R.drawable.ic_blocked_user);

        MenuObject cardmanagement = new MenuObject(getString(R.string.card_management));
        cardmanagement.setBgColor(ContextCompat.getColor(this, R.color.colorBlack));
        cardmanagement.setResource(R.drawable.ic_card_management);

        MenuObject logout = new MenuObject(getString(R.string.logout));
        logout.setBgColor(ContextCompat.getColor(this, R.color.colorBlack));
        logout.setResource(R.drawable.ic_logout);

        menuObjects.add(close);
        menuObjects.add(newprofile);
        menuObjects.add(otherprofile);
        menuObjects.add(notification);
        menuObjects.add(blockeduser);
        menuObjects.add(cardmanagement);
        menuObjects.add(logout);
        return menuObjects;
    }

    protected Uri getImgUri() {
        File mGetImage = getExternalCacheDir();
        if (mGetImage != null) {
            return Uri.fromFile(new File(mGetImage.getPath(), "moto_hub.png"));
        }
        return null;
    }

    protected Uri getImgResultUri(Intent data) {
        boolean mIsCamera = true;
        if (data != null) {
            String mAction = data.getAction();
            mIsCamera = mAction != null && mAction.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return mIsCamera ? getImgUri() : data.getData();
    }

    /**
     * Converts Base64 Image String to Bitmap Image.
     *
     * @param base64ImgStr Base64 Image String.
     * @return Bitmap of Base64 Image.
     */
    public Bitmap getBitmapImg(String base64ImgStr) {
        byte[] mByte = Base64.decode(base64ImgStr, 0);
        return BitmapFactory.decodeByteArray(mByte, 0, mByte.length);
    }

    public void setImageWithGlideshop(ImageView imgView, String imgUrl, int drawable) {

        /*GlideUrl glideUrl = new GlideUrl(UrlUtils.AWS_FILE_URL + imgUrl, new LazyHeaders.Builder()
                .addHeader("X-DreamFactory-Api-Key", getString(R.string.dream_factory_api_key))
                .build());*/
        Glide.with(getApplicationContext())
                .load(UrlUtils.AWS_S3_BASE_URL + imgUrl)
                .apply(new RequestOptions().placeholder(drawable)
                        .override(800, 400)
                        .dontAnimate()
                        .error(drawable)
                )
                .into(imgView);
    }

    public void setImageWithGlide(ImageView imgView, String imgUrl, int drawable) {

        GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + imgUrl, new LazyHeaders.Builder()
                .addHeader("X-DreamFactory-Api-Key", getString(R.string.dream_factory_api_key))
                .build());
        Glide.with(getApplicationContext())
                .load(glideUrl)
                .apply(new RequestOptions().placeholder(drawable)
                        .dontAnimate()
                )
                .into(imgView);
    }

    public void setCoverImageWithGlide(ImageView imgView, String imgUrl, int drawable) {

        GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + imgUrl, new LazyHeaders.Builder()
                .addHeader("X-DreamFactory-Api-Key", getString(R.string.dream_factory_api_key))
                .build());

        Glide.with(getApplicationContext())
                .load(glideUrl)
                .apply(new RequestOptions()
                        .placeholder(drawable)
                        .dontAnimate().centerCrop())
                .into(imgView);

    }

    public void setImageWithGlide(ImageView imgView, String imgUrl) {
        GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + imgUrl, new LazyHeaders.Builder()
                .addHeader("X-DreamFactory-Api-Key", getString(R.string.dream_factory_api_key))
                .build());

        Glide.with(getApplicationContext())
                .load(glideUrl)
                .apply(new RequestOptions().dontAnimate())
                .into(imgView);
    }

    public void setImageWithGlide(ImageView imgView, int imgUrl) {
        Glide.with(getApplicationContext())
                .load(imgUrl)
                .apply(new RequestOptions()
                        .dontAnimate()
                )
                .into(imgView);
    }

    protected void setImageWithGlide(ImageView imgView, Uri imgUrl) {

        Glide.with(getApplicationContext())
                .load(imgUrl)
                .apply(new RequestOptions()
                        .dontAnimate()
                )
                .into(imgView);
    }

    protected void setImageWithGlide(ZoomImageView imgView, Uri imgUrl) {

        Glide.with(getApplicationContext())
                .load(imgUrl)
                .apply(new RequestOptions()
                        .dontAnimate()
                )
                .into(imgView);
    }

    public void setImageWithGlide(ZoomImageView imgView, String imgUrl) {
        /*GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + imgUrl, new LazyHeaders.Builder()
                .addHeader("X-DreamFactory-Api-Key", getString(R.string.dream_factory_api_key))
                .build());*/
        GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + imgUrl);

        Glide.with(getApplicationContext())
                .load(glideUrl)
                .apply(new RequestOptions()
                        .dontAnimate())
                .into(imgView);
    }

    protected void setImageWithGlide(ImageView imgView, Uri imgUrl, int drawable) {
        Glide.with(getApplicationContext())
                .load(imgUrl)
                .apply(new RequestOptions()
                        .dontAnimate()
                        .placeholder(drawable)
                )
                .into(imgView);

    }

    protected File compressedImgFile(Uri imageUri, String fileNameType, String profileID) {
        File srcFile = new File(imageUri.getPath());
        File destinationFile = getNewFile(fileNameType, profileID);
        boolean mStatus = false;
        if (destinationFile.exists()) {
            mStatus = destinationFile.delete();
        }
        if (mStatus) {
            destinationFile = getNewFile(fileNameType, profileID);
        }
        Uri srcUri = Uri.fromFile(srcFile);
        Uri destinationUri = Uri.fromFile(destinationFile);
        compressImage(srcUri, destinationUri);
        return destinationFile;
    }

    protected File compressedImgFromBitmap(Bitmap bitmap) throws IOException {

        File pictureFile = getNewFile(GALLERY_IMAGE_NAME_TYPE, "");

        boolean mStatus = false;
        if (pictureFile.exists()) {
            mStatus = pictureFile.delete();
        }

        if (mStatus) {
            pictureFile = getNewFile(GALLERY_IMAGE_NAME_TYPE, "");
        }

        FileOutputStream fos = new FileOutputStream(pictureFile, false);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.close();

        return pictureFile;
    }

    private File getNewFile(String fileType, String profileType) {
        File mMainFile = new File(Environment.getExternalStorageDirectory().toString() + getString(R.string.util_app_folder_root_path));
        if (!mMainFile.exists()) mMainFile.mkdir();

        switch (fileType) {
            case COVER_IMAGE_NAME_TYPE:
                return new File(mMainFile.getPath(), getNewFileName(fileType, profileType));
            case PROFILE_IMAGE_NAME_TYPE:
                return new File(mMainFile.getPath(), getNewFileName(fileType, profileType));
            case GROUP_IMAGE_NAME_TYPE:
                return new File(mMainFile.getPath(), getNewFileName(fileType, profileType));
            case GALLERY_VIDEO_NAME_TYPE:
                return new File(mMainFile.getPath(), getNewFileName(fileType, profileType));
            default:
                return new File(mMainFile.getPath(), getNewFileName(fileType, profileType));
        }

    }

    protected String getNewFileName(String fileType, String profileType) {

        String userId = String.valueOf(PreferenceUtils.getInstance(BaseActivity.this)
                .getIntData(PreferenceUtils.USER_ID));

        switch (fileType) {
            case COVER_IMAGE_NAME_TYPE:
                return COVER_IMAGE_NAME_TYPE + "_" + userId + "_" + profileType + System.currentTimeMillis() + ".jpg";
            case PROFILE_IMAGE_NAME_TYPE:
                return PROFILE_IMAGE_NAME_TYPE + "_" + userId + "_" + profileType + System.currentTimeMillis() + ".jpg";
            case GROUP_IMAGE_NAME_TYPE:
                return GROUP_IMAGE_NAME_TYPE + "_" + userId + "_" + profileType + System.currentTimeMillis() + ".jpg";
            case GALLERY_VIDEO_NAME_TYPE:
                return fileType + "_" + userId + System.currentTimeMillis() + ".mp4";
            default:
                return fileType + "_" + userId + System.currentTimeMillis() + ".jpg";
        }

    }

    protected String getHttpFilePath(String filePath) {
        return filePath;
    }

    /**
     * compress image size
     */
    public Uri compressImage(Uri srcImgUri, Uri destinationImgUri) {
        String srcFilePath = getRealPathFromURI(this, srcImgUri);
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcFilePath, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        // Max Height and width values of the compressed image is taken as 816x612
        float maxHeight = 2560.0f;
        float maxWidth = 1600.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;
        // Width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap scaledBitmap = BitmapFactory.decodeFile(srcFilePath, options);
        // Check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(srcFilePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(destinationImgUri.getPath(), false);
            // Write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return destinationImgUri;
    }

    /**
     * compress image size - old one
     */
    public Uri compresssImage(Uri srcImgUri, Uri destinationImgUri) {

        String srcFilePath = getRealPathFromURI(this, srcImgUri);
        Bitmap scaledBitmap;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels
//      are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(srcFilePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inBitmap = bmp;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(srcFilePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
            return null;
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
            return null;
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(srcFilePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        FileOutputStream out;

        try {
            out = new FileOutputStream(destinationImgUri.getPath(), false);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return destinationImgUri;

    }

    /**
     * calculating the image size for compression
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    protected File copiedVideoFile(Uri imageUri, String fileType) throws IOException {

        File srcFile = new File(imageUri.getPath());
        File destinationFile = getNewFile(fileType, "");

        boolean mStatus = false;
        if (destinationFile.exists()) {
            mStatus = destinationFile.delete();
        }

        if (mStatus) {
            destinationFile = getNewFile(fileType, "");
        }

        FileChannel srcFileChannel = new FileInputStream(srcFile).getChannel();
        FileChannel destFileChannel = new FileOutputStream(destinationFile).getChannel();

        if (srcFileChannel != null) {

            destFileChannel.transferFrom(srcFileChannel, 0, srcFileChannel.size());
            srcFileChannel.close();

        }

        destFileChannel.close();

        return destinationFile;

    }

    protected String getNewSavedFilePath(String imageUri, String fileType) throws IOException {

        File srcFile = new File(imageUri);
        File destinationFile = getNewFile(fileType, "");

        boolean mStatus = false;
        if (destinationFile.exists()) {
            mStatus = destinationFile.delete();
        }

        if (mStatus) {
            destinationFile = getNewFile(fileType, "");
        }

        FileChannel srcFileChannel = new FileInputStream(srcFile).getChannel();
        FileChannel destFileChannel = new FileOutputStream(destinationFile).getChannel();

        if (srcFileChannel != null) {

            destFileChannel.transferFrom(srcFileChannel, 0, srcFileChannel.size());
            srcFileChannel.close();

        }

        destFileChannel.close();

        return String.valueOf(destinationFile);

    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public boolean createAppFolder() {

        String rootPath = Environment.getExternalStorageDirectory().getPath();

        File mMainFile = new File(rootPath + getString(R.string.util_app_folder_root_path));

        boolean isCreated = mMainFile.exists();

        if (!isCreated) {
            isCreated = mMainFile.mkdir();
        }

        checkStoragePermission();

        return isCreated;

    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void deleteFilesFromAppFolder() {

        try {

            File mMainFile = new File(Environment.getExternalStorageDirectory().getPath()
                    + getString(R.string.util_app_folder_root_path));

            if (mMainFile.exists() && mMainFile.isDirectory()) {

                String[] children = mMainFile.list();
                for (String aChildren : children) new File(mMainFile, aChildren).deleteOnExit();

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    protected void clearProfileTypePreferences() {
        PreferenceUtils mPreferenceUtils = PreferenceUtils.getInstance(this);
        mPreferenceUtils.saveBooleanData(BIKE, false);
        mPreferenceUtils.saveBooleanData(BOAT, false);
        mPreferenceUtils.saveBooleanData(CAR, false);
        mPreferenceUtils.saveBooleanData(KART, false);
        mPreferenceUtils.saveBooleanData(SPECTATOR, false);
    }

    protected void saveInAppPurchasesLocally(Integer id, String profileType) {
        switch (profileType) {
            case BIKE:
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.BIKE_IN_APP, String.valueOf(id));
                break;
            case BOAT:
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.BOAT_IN_APP, String.valueOf(id));
                break;
            case CAR:
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.CAR_IN_APP, String.valueOf(id));
                break;
            case KART:
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.KART_IN_APP, String.valueOf(id));
                break;
        }
    }

    public void retrofitOnResponse(Object responseObj, int responseType) {

    }

    public void retrofitOnResponse(ArrayList<OndemandNewResponse> list, int responseType) {

    }

    public void retrofitOnFailure() {

    }

    public void retrofitOnFailure(int code, String message) {

    }

    public void retrofitOnError(int code, String message) {
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showToast(this, mErrorMsg);
        }

    }

    public void onRequestError(ErrorMessage mErrObj) {
        try {
            String err = new Gson().toJson(mErrObj);
            sysOut("API-ERROR-OUTPUT: " + err);
        } catch (Exception e) {
            sysOut("" + e.getMessage());
        }
    }

    public void retrofitOnSessionError(int code, String message) {
        if (message.equals("Unauthorized") || code == 401) {
            clearBeforeLogout();
            String mErrorMsg = code + " - " + message;
            showToast(getApplicationContext(), mErrorMsg);
            startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }

    public void clearBeforeLogout() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        PreferenceUtils.getInstance(this).clearData();
        PreferenceUtils mPreferenceUtils = PreferenceUtils.getInstance(this);
        mPreferenceUtils.saveBooleanData(PreferenceUtils.IS_NOT_FIRST_LAUNCH, true);
    }

    protected String getUserId() {
        return String.valueOf(PreferenceUtils.getInstance(BaseActivity.this).getIntData(PreferenceUtils.USER_ID));
    }

    public void showFBShareDialog(final String dialogType, final String shareContent, final ArrayList<Bitmap> shareImg, final String[] videoUrl, final int mPos, final boolean mIsFromOtherMotoProfile) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                DialogFragment mDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(AppDialogFragment.TAG);
                if (mDialogFragment != null && mDialogFragment.isAdded()) {
                    getSupportFragmentManager().beginTransaction().remove(mDialogFragment).commit();
                }
                AppDialogFragment.newInstance(dialogType, shareContent, shareImg, videoUrl, mPos, mIsFromOtherMotoProfile).show(getSupportFragmentManager(), AppDialogFragment.TAG);
            }
        });
    }

    public void showTagSponsorsDialog(final String dialogType, final String mCurrentTagSponsorId) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                DialogFragment mDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(AppDialogFragment.TAG);
                if (mDialogFragment != null && mDialogFragment.isAdded()) {
                    getSupportFragmentManager().beginTransaction().remove(mDialogFragment).commit();
                }
                AppDialogFragment.newInstanceForTagSponsors(dialogType, mCurrentTagSponsorId).show(getSupportFragmentManager(), AppDialogFragment.TAG);
            }
        });
    }

    public void showBottomChatEdit(final String dialogType, final SingleChatRoomResModel singleChatMsgResModel) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                DialogFragment mDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(AppDialogFragment.TAG);
                if (mDialogFragment != null && mDialogFragment.isAdded()) {
                    getSupportFragmentManager().beginTransaction().remove(mDialogFragment).commit();
                }
                AppDialogFragment.newInstanceForChatDelete(dialogType, singleChatMsgResModel).show(getSupportFragmentManager(), AppDialogFragment.TAG);
            }
        });
    }

    public void retrofitOnError(int code, String message, int responseType) {
        if (code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            //showToast(this, mErrorMsg);
        }
    }

    public void retrofitOnFailure(int responseType) {
    }

    public void sysOut(String msg) {
        /*System.out.println(getString(R.string.app_name) + " " + msg);
        Log.e(getString(R.string.app_name), " " + msg);*/
    }

    /**
     * Touch and Hide keyboard
     *
     * @param view
     */
    public void setupUI(View view) {

        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(mActivity);
                    return false;
                }
            });
        }
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View mInnerView = ((ViewGroup) view).getChildAt(i);
                setupUI(mInnerView);
            }
        }
    }

    public void hideSoftKeyboard(AppCompatActivity mActivity) {
        try {
            if (mActivity != null && !mActivity.isFinishing()) {
                InputMethodManager mInputMethodManager = (InputMethodManager) mActivity
                        .getSystemService(INPUT_METHOD_SERVICE);

                if (mActivity.getCurrentFocus() != null
                        && mActivity.getCurrentFocus().getWindowToken() != null) {
                    mInputMethodManager.hideSoftInputFromWindow(mActivity
                            .getCurrentFocus().getWindowToken(), 0);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            boolean isShowing = imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            if (!isShowing)
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    public boolean isPermission(PermissionCallback permissionCallback, List<String> camera) {
        mPermissionCallback = permissionCallback;
        listPermissionsNeeded = new ArrayList<>();
        listPermissionsNeeded.addAll(camera);

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), permsRequestCode);
            return false;
        }

        return true;
    }

   /* public String getCompressedVideoPath() {
        CreateCompressedVideoPath();
        mCompressedVideoPath = Environment.getExternalStorageDirectory()
                + File.separator
                + COMPRESSED_VIDEO_FOLDER + System.currentTimeMillis() + "COMPRESSED_VIDEO.mp4";
        return mCompressedVideoPath;

    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200: {
                Map<String, Integer> perms = new HashMap<>();
                if (listPermissionsNeeded != null && listPermissionsNeeded.size() > 0) {
                    for (int i = 0; i < listPermissionsNeeded.size(); i++) {
                        perms.put(listPermissionsNeeded.get(i), PackageManager.PERMISSION_GRANTED);
                    }
                    if (grantResults.length > 0) {
                        for (int i = 0; i < permissions.length; i++)
                            perms.put(permissions[i], grantResults[i]);
                        for (int j = 0; j < listPermissionsNeeded.size(); j++) {
                            if (perms.get(listPermissionsNeeded.get(j)) == PackageManager.PERMISSION_GRANTED) {

                                if (j == listPermissionsNeeded.size() - 1) {
                                    if (mPermissionCallback != null) {
                                        mPermissionCallback.permissionOkClick();
                                    }
                                }
                            } else {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(this, listPermissionsNeeded.get(j))) {

                                    //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                                    DialogManager.showAlertDialogWithCallback(this, mAlertCallback,
                                            "Permission must be required for this app");

                                    break;
                                } else {
                                    //permission is denied (and never ask again is  checked)
                                    //shouldShowRequestPermissionRationale will return false
                                    if (perms.get(listPermissionsNeeded.get(j)) == PackageManager.PERMISSION_DENIED) {

                                        DialogManager.showAlertDialogWithCallback(this, mConfirmCallback,
                                                getString(R.string.alert_settings));

                                        break;
                                    } else {

                                        if (j == listPermissionsNeeded.size() - 1) {
                                            if (mPermissionCallback != null) {
                                                mPermissionCallback.permissionOkClick();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void CreateCompressedVideoPath() {
        File f = new File(Environment.getExternalStorageDirectory(), File.separator + COMPRESSED_VIDEO_FOLDER);
        if (!f.exists())
            f.mkdirs();
    }

    public int getProfileCurrentPos() {
        return PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.CURRENT_PROFILE_POS);
    }

    public void targetVersion() {
        if (Build.VERSION.SDK_INT == 21) {
        }
    }

    public void setUpPurchseUI(final int price, final int mProfileID) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.widget_purchase_dialog, null);
        mPurchaseDialog = new Dialog(mActivity);
        mPurchaseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mPurchaseDialog.setContentView(view);
        ((TextView) view.findViewById(R.id.tv_price)).setText("$ ".concat(String.valueOf(price / 100).concat(getString(R.string.month))));
        view.findViewById(R.id.btn_subscribe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPurchaseDialog.dismiss();
                moveToPaymentScreen(price, mProfileID);
            }
        });
    }

    public void setUpPurchseSuccessUI() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.widget_purchase_success_dialog, null);
        mPurchaseSuccessDialog = new Dialog(mActivity);
        mPurchaseSuccessDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mPurchaseSuccessDialog.setContentView(view);
        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPurchaseSuccessDialog.dismiss();
            }
        });
    }

    private void moveToPaymentScreen(int price, int mProfileID) {
        Intent paymentActivity = new Intent(mActivity, PaymentActivity.class);
        paymentActivity.putExtra(EventsModel.EVENT_AMOUNT, price).putExtra(AppConstants.PROFILE_ID, mProfileID);
        mActivity.startActivityForResult(paymentActivity, EventsFindAdapter.EVENT_PAYMENT_REQ_CODE);
    }

    public void moveToReportWritePostScreen(Context mContext) {
        Intent mReportWritePostActivity = new Intent(mContext, ReportWritePostActivity.class);
        ((BaseActivity) mContext).startActivityForResult(mReportWritePostActivity, AppConstants.REPORT_WRITE_POST_RESPONSE);
    }

    private int getNumberOfSubStr(String str, String subStr) {
        if (subStr.length() > 0)
            return (str.length() - str.replace(subStr, "").length()) / subStr.length();
        else
            return 0;

    }

    public SpannableString setTextEdt(final Context mContext, String mCommentTagString, String mTagList, final String mTaggedUserIds, final int mMyProfileID) {
        SpannableString mWordToSpan = new SpannableString(mCommentTagString);
        if (mTagList.trim().isEmpty())
            return mWordToSpan;

        String[] mCommentTagArray = mTagList.split(",");
        final String[] mCommentTagUserIDs = mTaggedUserIds.split(",");

        for (int i = 0; i < mCommentTagArray.length; i++) {
            final int mCurrentPos = i;
            if (mCommentTagString.contains(mCommentTagArray[i])) {
                int subStrLength = mCommentTagArray[i].length();
                int subStrIndex = mCommentTagString.indexOf(mCommentTagArray[i]);
                int noOfSubStr = getNumberOfSubStr(mCommentTagString, mCommentTagArray[i]);
                if (noOfSubStr == 1) {
                    //   mWordToSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorOrange)), subStrIndex, subStrIndex + subStrLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mWordToSpan.setSpan(new ClickableSpan() {

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorOrange));
                            ds.setUnderlineText(false);

                        }

                        @Override
                        public void onClick(View view) {

                            // showToast(getApplicationContext(),"TAG");

                            //  int mOtherProfileID = Integer.parseInt(mCommentTagUserIDs[mCurrentPos]);

                            //moveOtherProfileScreen(mContext, mMyProfileID, mOtherProfileID);

                            // sharedPostProfileClick(view);

                        }

                    }, subStrIndex, subStrIndex + subStrLength, 0);
                } else {

                    for (int j = 0; j < noOfSubStr; j++) {

                        mWordToSpan.setSpan(new ClickableSpan() {

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorOrange));
                                ds.setUnderlineText(false);

                            }

                            @Override
                            public void onClick(View view) {

                                // sharedPostProfileClick(view);

                            }

                        }, subStrIndex, subStrIndex + subStrLength, 0);
                        //  mWordToSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorOrange)), subStrIndex, subStrIndex + subStrLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        subStrIndex = mCommentTagString.indexOf(mCommentTagArray[mCurrentPos], subStrIndex + subStrLength);
                    }
                }

            }
        }
       /* mWordToSpan.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View view) {

            }

        }, 0, mCommentTagString.length(), 0);*/
        return mWordToSpan;
    }

    public String setPaymentCardNumber(String cardNumber) {
        cardNumber = cardNumber.replace(" ", "");
        String mFirstSubStr = cardNumber.substring(0, 4);
        String mLastSubStr = cardNumber.substring(12, 16);
        String resStrTxt = mFirstSubStr + "-xxxx-xxxx-" + mLastSubStr;
        return resStrTxt;
    }

    // https://stackoverflow.com/questions/9769554/how-to-convert-number-into-k-thousands-m-million-and-b-billion-suffix-in-jsp
    // Converts the number to K, M suffix
    // Ex: 5500 will be displayed as 5.5k

    public SpannableString setCommentTagText(final Context mContext, String mCommentTagString, String mTagList, final String mTaggedUserIds) {
        SpannableString mWordToSpan = new SpannableString(mCommentTagString);
        if (mTagList.trim().isEmpty())
            return mWordToSpan;

        String[] mCommentTagArray = mTagList.split(",");
        final String[] mCommentTagUserIDs = mTaggedUserIds.split(",");

        for (int i = 0; i < mCommentTagArray.length; i++) {
            final int mCurrentPos = i;
            if (mCommentTagString.contains(mCommentTagArray[i])) {
                int subStrLength = mCommentTagArray[i].length();
                int subStrIndex = mCommentTagString.indexOf(mCommentTagArray[i]);
                int noOfSubStr = getNumberOfSubStr(mCommentTagString, mCommentTagArray[i]);
                if (noOfSubStr == 1) {
                    //   mWordToSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorOrange)), subStrIndex, subStrIndex + subStrLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mWordToSpan.setSpan(new ClickableSpan() {

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorOrange));
                            ds.setUnderlineText(false);

                        }

                        @Override
                        public void onClick(View view) {

                            // showToast(getApplicationContext(),"TAG");

                            //  int mOtherProfileID = Integer.parseInt(mCommentTagUserIDs[mCurrentPos]);

                            //moveOtherProfileScreen(mContext, mMyProfileID, mOtherProfileID);

                            // sharedPostProfileClick(view);

                        }

                    }, subStrIndex, subStrIndex + subStrLength, 0);
                } else {

                    for (int j = 0; j < noOfSubStr; j++) {

                        mWordToSpan.setSpan(new ClickableSpan() {

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorOrange));
                                ds.setUnderlineText(false);

                            }

                            @Override
                            public void onClick(View view) {

                                // sharedPostProfileClick(view);

                            }

                        }, subStrIndex, subStrIndex + subStrLength, 0);
                        //  mWordToSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorOrange)), subStrIndex, subStrIndex + subStrLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        subStrIndex = mCommentTagString.indexOf(mCommentTagArray[mCurrentPos], subStrIndex + subStrLength);
                    }
                }

            }
        }
       /* mWordToSpan.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View view) {

            }

        }, 0, mCommentTagString.length(), 0);*/
        return mWordToSpan;
    }
}
