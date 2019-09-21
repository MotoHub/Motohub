package online.motohub.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import online.motohub.R;
import online.motohub.newdesign.bl.ViewModelAlert;
import online.motohub.dialog.DialogManager;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.interfaces.PermissionViewModelCallback;
import online.motohub.interfaces.ViewModelCallback;
import online.motohub.model.GroupChatRoomResModel;
import online.motohub.model.OndemandNewResponse;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SingleChatRoomResModel;
import online.motohub.util.PreferenceUtils;
import online.motohub.newdesign.viewmodel.BaseViewModel;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class BaseFragment extends Fragment implements ViewModelCallback, PermissionViewModelCallback {

    protected static final String GALLERY_IMAGE_NAME_TYPE = "gallery_img";
    protected static final String GALLERY_VIDEO_NAME_TYPE = "gallery_vid";
    protected static final String PROFILE_IMAGE_NAME_TYPE = "profile";
    protected static final String COVER_IMAGE_NAME_TYPE = "cover";
    protected static final String POST_IMAGE_NAME_TYPE = "post";
    protected static final String GROUP_IMAGE_NAME_TYPE = "grp_chat_img";
    public String mCompressedVideoPath = "";
    private String COMPRESSED_VIDEO_FOLDER = "MotoHub";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void showSnackBar(View view, String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

//    public void callGetEvents(ArrayList<ProfileResModel> fullMPList) {}

    public void callFeedPost(ProfileResModel mProfileResModel) {
    }

    public void callGetEvents() {
    }

    public void setRefresh(boolean refresh) {
    }

    public void callGetChatRoom(ProfileResModel myProfileModel) {
    }

    public void getaddedChat(ProfileResModel myProfileModel, boolean addedchat) {
    }

    public void launchSingleChatAddActivity() {
    }

    public void launchCreateGroupChatActivity() {
    }

    public void callUpdateSingleChatRoom(SingleChatRoomResModel singleChatRoomResModel) {
    }

    public void callUpdateGroupChatRoom(GroupChatRoomResModel groupChatRoomResModel) {
    }

    public void alertDialogPositiveBtnClick(String dialogType, int position) {
    }

    public void retrofitOnFailure(int responseType) {
    }

    public void retrofitOnFailure() {
    }

    public void retrofitOnError(int responseType, String errorMessage) {
    }

    public void retrofitOnError(int responseType, String errorMessage, int code) {
    }

    public void retrofitOnError() {
    }

    public void retrofitOnSessionError(int type, String code) {
    }

    public void retrofitOnResponse(Object responseObj, int responseType) {
    }

    public void retrofitOnResponse(ArrayList<OndemandNewResponse> list, int responseType) {

    }

    public int getTotalPostsResultCount() {
        return 0;
    }

    public void alertDialogPositiveBtnClick(BaseFragment activity, String dialogType, StringBuilder profileTypesStr, ArrayList<String> profileTypes,
                                            int position) {

    }

    public int getProfileCurrentPos() {

        return PreferenceUtils.getInstance(getActivity()).getIntData(PreferenceUtils.CURRENT_PROFILE_POS);
    }

    public boolean isNetworkConnected() {
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        assert conMgr != null;
        if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            return true;
        } else
            return conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.DISCONNECTED
                    && conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.DISCONNECTED;
    }

    public String getCurrentDate() {
        DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        return mDateFormat.format(new Date());
    }

    public void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
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

        String userId = String.valueOf(PreferenceUtils.getInstance(getActivity())
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

    public String getCompressedVideoPath() {
        CreateCompressedVideoPath();
        mCompressedVideoPath = Environment.getExternalStorageDirectory()
                + File.separator
                + COMPRESSED_VIDEO_FOLDER + System.currentTimeMillis() + "COMPRESSED_VIDEO.mp4";
        return mCompressedVideoPath;

    }

    public void CreateCompressedVideoPath() {
        File f = new File(Environment.getExternalStorageDirectory(), File.separator + COMPRESSED_VIDEO_FOLDER);
        if (!f.exists())
            f.mkdirs();
    }

    protected void setupUI(View view) {

        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
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

    public void hideSoftKeyboard() {
        try {
            InputMethodManager mInputMethodManager = (InputMethodManager) getActivity()
                    .getSystemService(INPUT_METHOD_SERVICE);
            if (getActivity().getCurrentFocus() != null
                    && getActivity().getCurrentFocus().getWindowToken() != null) {
                mInputMethodManager.hideSoftInputFromWindow(getActivity()
                        .getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideSoftInputFromWindow(View view) {
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) view.getContext()
                    .getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showFBShareDialog(final String dialogType, final String shareContent, final ArrayList<Bitmap> shareImg, final String[] videoUrl, final int mPos, final boolean mIsFromOtherMotoProfile) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                DialogFragment mDialogFragment = (DialogFragment) getActivity().getSupportFragmentManager().findFragmentByTag(AppDialogFragment.TAG);
                if (mDialogFragment != null && mDialogFragment.isAdded()) {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(mDialogFragment).commit();
                }
                AppDialogFragment.newInstance(dialogType, shareContent, shareImg, videoUrl, mPos, mIsFromOtherMotoProfile).show(getActivity().getSupportFragmentManager(), AppDialogFragment.TAG);
            }
        });
    }

    private WeakReference<BaseViewModel> registeredModel = null;

    public void registerModel(BaseViewModel model) {
        if (getActivity() instanceof ViewModelCallback) {
            ViewModelCallback callback = (ViewModelCallback) getActivity();
            model.setCallback(callback);
        }
        model.setNavCallback(this);
        this.registeredModel = new WeakReference<>(model);
    }

    @Override
    public void requestPermission(List<String> strings, int code) {
        requestPermissions(strings.toArray(new String[strings.size()]), code);
    }

    @Override
    public void showProgress() {
        DialogManager.showProgress(getActivity());
    }

    @Override
    public void hideProgress() {
        DialogManager.hideProgress();
    }

    @Override
    public void showMessage(@NotNull String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAlert(@NotNull ViewModelAlert viewModelAlert) {

    }

    @Override
    public int getProgressCount() {
        return 0;
    }

    @Override
    public void setProgressCount(int progressCount) {

    }

    public void moveProfileScreen(int pos, boolean isSharedProfile) {
    }
}
