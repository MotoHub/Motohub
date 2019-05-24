package online.motohub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import online.motohub.R;
import online.motohub.adapter.CreateGrpSelectProfilesAdapter;
import online.motohub.adapter.CreateGrpSelectedProfilesAdapter;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.GroupChatRoomModel;
import online.motohub.model.GroupChatRoomResModel;
import online.motohub.model.ImageModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.Utility;

public class ChatCreateGroupActivity extends BaseActivity implements
        CreateGrpSelectProfilesAdapter.CreateGrpSelectProfilesInterface {

    @BindView(R.id.create_grp_co_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.grp_chat_img)
    CircleImageView mGrpChatImgView;

    @BindView(R.id.grp_name_et)
    EditText mEditGrpName;

    @BindView(R.id.selected_profiles_tv)
    TextView mSelectedProfilesTv;

    @BindView(R.id.grp_selected_profiles_rv)
    RecyclerView mGrpSelectedProfilesRv;

    @BindView(R.id.grp_select_profiles_rv)
    RecyclerView mGrpSelectProfilesRv;

    @BindString(R.string.create_new_group)
    String mToolbarTitle;

    @BindString(R.string.no_following_found_err)
    String mNoFollowingErr;

    @BindString(R.string.camera_permission_denied)
    String mNoCameraPer;

    @BindString(R.string.storage_permission_denied)
    String mNoStoragePer;

    private ProfileResModel mMyProfileResModel;
    private CreateGrpSelectProfilesAdapter mGrpSelectProfilesAdapter;
    private List<ProfileResModel> mGrpSelectProfilesList;
    private List<ProfileResModel> mGrpSelectedProfilesList;
    private CreateGrpSelectedProfilesAdapter mGrpSelectedProfilesAdapter;
    String mGrpChatImgUri = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_create_group);
        ButterKnife.bind(this);
        setupUI(mCoordinatorLayout);
        initViews();
        if (savedInstanceState == null) {
            getFollowingProfileList();
        }
    }
    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }
    /*@Override
    @SuppressWarnings("unchecked")
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mMyProfileResModel = (ProfileResModel)
                savedInstanceState.getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
        ArrayList<ProfileResModel> mTempGrpSelectProfilesList = (ArrayList<ProfileResModel>)
                savedInstanceState.getSerializable("GROUP_SELECT_PROFILE_LIST");
        if (mTempGrpSelectProfilesList != null) {
            mGrpSelectProfilesList.clear();
            mGrpSelectProfilesList.addAll(mTempGrpSelectProfilesList);
            mGrpSelectProfilesAdapter.notifyDataSetChanged();
        }
        ArrayList<ProfileResModel> mTempGrpSelectedProfilesList = (ArrayList<ProfileResModel>)
                savedInstanceState.getSerializable("GROUP_SELECTED_PROFILE_LIST");
        if (mTempGrpSelectedProfilesList != null) {
            mGrpSelectedProfilesList.clear();
            mGrpSelectedProfilesList.addAll(mTempGrpSelectedProfilesList);
            mGrpSelectedProfilesAdapter.notifyDataSetChanged();
        }
        mGrpChatImgUri = savedInstanceState.getString(GroupChatRoomModel.GRP_PICTURE);
        if (mGrpChatImgUri != null) {
            setImageWithGlide(
                    mGrpChatImgView,
                    Uri.parse(mGrpChatImgUri),
                    R.drawable.add_img_icon);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
        outState.putSerializable("GROUP_SELECT_PROFILE_LIST", (Serializable) mGrpSelectProfilesList);
        outState.putSerializable("GROUP_SELECTED_PROFILE_LIST",
                (Serializable) mGrpSelectedProfilesList);
        outState.putString(GroupChatRoomModel.GRP_PICTURE, mGrpChatImgUri);
        super.onSaveInstanceState(outState);
    }*/

    private void initViews() {
        setToolbar(mToolbar, mToolbarTitle);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        //mMyProfileResModel = (ProfileResModel) getIntent().getSerializableExtra(ProfileModel.MY_PROFILE_RES_MODEL);
        //mMyProfileResModel= MotoHub.getApplicationInstance().getmProfileResModel();
        mMyProfileResModel= EventBus.getDefault().getStickyEvent(ProfileResModel.class);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mGrpSelectedProfilesRv.setLayoutManager(mLinearLayoutManager);
        mGrpSelectedProfilesList = new ArrayList<>();
        mGrpSelectedProfilesAdapter = new CreateGrpSelectedProfilesAdapter(mGrpSelectedProfilesList, this);
        mGrpSelectedProfilesRv.setAdapter(mGrpSelectedProfilesAdapter);
        LinearLayoutManager mLinearLayoutManagerTwo = new LinearLayoutManager(this);
        mLinearLayoutManagerTwo.setOrientation(LinearLayoutManager.VERTICAL);
        mGrpSelectProfilesRv.setLayoutManager(mLinearLayoutManagerTwo);
        mGrpSelectProfilesList = new ArrayList<>();
        mGrpSelectProfilesAdapter = new CreateGrpSelectProfilesAdapter(mGrpSelectProfilesList, this);
        mGrpSelectProfilesRv.setAdapter(mGrpSelectProfilesAdapter);
    }

    private void getFollowingProfileList() {
        String mMyFollowingsID = Utility.getInstance().getMyFollowersFollowingsID(mMyProfileResModel.getFollowprofile_by_ProfileID(), false);
        if (!mMyFollowingsID.isEmpty()) {
            String mFilter = "id IN (" + mMyFollowingsID + ")";
            RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_FOLLOWING_PROFILE_RESPONSE);
        } else {
            showSnackBar(mCoordinatorLayout, mNoFollowingErr);
        }
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.done_floating_btn, R.id.grp_chat_img})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.grp_chat_img:
                showAppDialog(AppDialogFragment.BOTTOM_ADD_IMG_DIALOG, null);
                break;
            case R.id.done_floating_btn:
                if (mEditGrpName.getText().toString().isEmpty()) {
                    showSnackBar(mCoordinatorLayout, getString(R.string.grp_name_err));
                } else if (mGrpSelectedProfilesList.size() == 0) {
                showSnackBar(mCoordinatorLayout, getString(R.string.select_profile_err));
                } else if (mGrpChatImgUri != null) {
                    uploadGrpChatImg(mGrpChatImgUri);
                } else {
                    showSnackBar(mCoordinatorLayout, getString(R.string.grp_img_err));
                }
                break;
        }
    }

    private void uploadGrpChatImg(String imgUri) {
        File mFile = new File(Uri.parse(imgUri).getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", mFile.getName(), requestBody);
        RetrofitClient.getRetrofitInstance().callUploadGrpChatImg(this, filePart, RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE);
    }

    private void createNewGroup(String grpChatImgUrl) {
        String mGrpNameStr = mEditGrpName.getText().toString();
        StringBuilder mGrpMemUserID = new StringBuilder(), mGrpMemProfileID = new StringBuilder();
        mGrpMemUserID.append(mMyProfileResModel.getUserID());
        mGrpMemProfileID.append(mMyProfileResModel.getID());
        if (mGrpSelectedProfilesList.size() > 0) {
            mGrpMemUserID.append(",");
            mGrpMemProfileID.append(",");
        }
        for (int i = 0; i < mGrpSelectedProfilesList.size(); i++) {
            mGrpMemUserID.append(mGrpSelectedProfilesList.get(i).getUserID());
            mGrpMemProfileID.append(mGrpSelectedProfilesList.get(i).getID());
            if (i == (mGrpSelectedProfilesList.size() - 1)) {
                continue;
            }
            mGrpMemUserID.append(",");
            mGrpMemProfileID.append(",");
        }
        GroupChatRoomResModel mGroupChatRoomResModel = new GroupChatRoomResModel();
        mGroupChatRoomResModel.setCreatedByProfileID(mMyProfileResModel.getID());
        mGroupChatRoomResModel.setGroupName(mGrpNameStr);
        mGroupChatRoomResModel.setGroupPicture(grpChatImgUrl);
        mGroupChatRoomResModel.setGroupMemUserID(mGrpMemUserID.toString());
        mGroupChatRoomResModel.setGroupMemProfileID(mGrpMemProfileID.toString());
        List<GroupChatRoomResModel> mGroupChatRoomResModelList = new ArrayList<>();
        mGroupChatRoomResModelList.add(mGroupChatRoomResModel);
        GroupChatRoomModel mGroupChatRoomModel = new GroupChatRoomModel();
        mGroupChatRoomModel.setResource(mGroupChatRoomResModelList);
        RetrofitClient.getRetrofitInstance().callCreateGrpChatRoom(this, mGroupChatRoomModel, RetrofitClient.CREATE_GRP_CHAT_ROOM_RESPONSE);
    }

    @Override
    public void grpSelectProfiles(int adapterPosition) {
        if (mGrpSelectProfilesList.get(adapterPosition).getIsSelected()) {
            mGrpSelectedProfilesList.remove(mGrpSelectProfilesList.get(adapterPosition));
            mGrpSelectProfilesList.get(adapterPosition).setIsSelected(false);
        } else {
            mGrpSelectedProfilesList.add(mGrpSelectProfilesList.get(adapterPosition));
            mGrpSelectProfilesList.get(adapterPosition).setIsSelected(true);
        }
        if (mGrpSelectedProfilesList.size() > 0) {
            mSelectedProfilesTv.setVisibility(View.VISIBLE);
        } else {
            mSelectedProfilesTv.setVisibility(View.GONE);
        }
        mGrpSelectProfilesAdapter.notifyDataSetChanged();
        mGrpSelectedProfilesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_CAPTURE_REQ:
                    Uri mCameraPicUri = getImgResultUri(data);
                    if (mCameraPicUri != null) {
                        try {
                            File mGrpChatImgFile = compressedImgFile(mCameraPicUri,
                                    GROUP_IMAGE_NAME_TYPE, String.valueOf(mMyProfileResModel.getID()));
                            mGrpChatImgUri = Uri.fromFile(mGrpChatImgFile).toString();
                            setImageWithGlide(
                                    mGrpChatImgView,
                                    Uri.parse(mGrpChatImgUri),
                                    R.drawable.add_img_icon);
                        } catch (Exception e) {
                            e.printStackTrace();
                            mGrpChatImgUri = null;
                            showSnackBar(mCoordinatorLayout, e.getMessage());
                        }
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
                case GALLERY_PIC_REQ:
                    assert data.getExtras() != null;
                    Uri mSelectedImgFileUri = (Uri) data.getExtras().get(PickerImageActivity.EXTRA_RESULT_DATA);
                    if (mSelectedImgFileUri != null) {
                        try {
                            File mGrpChatImgFile = compressedImgFile(mSelectedImgFileUri,
                                    GROUP_IMAGE_NAME_TYPE, String.valueOf(mMyProfileResModel.getID()));
                            mGrpChatImgUri = Uri.fromFile(mGrpChatImgFile).toString();
                            setImageWithGlide(
                                    mGrpChatImgView,
                                    Uri.parse(mGrpChatImgUri),
                                    R.drawable.default_cover_img);
                        } catch (Exception e) {
                            e.printStackTrace();
                            mGrpChatImgUri = null;
                            showSnackBar(mCoordinatorLayout, e.getMessage());
                        }
                    } else {
                        showSnackBar(mCoordinatorLayout, getString(R.string.file_not_found));
                    }
                    break;
            }
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof ProfileModel) {
            ProfileModel mProfileModel = (ProfileModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_FOLLOWING_PROFILE_RESPONSE:
                    if (mProfileModel.getResource() != null && mProfileModel.getResource().size() > 0) {
                        mGrpSelectProfilesList.clear();
                        mGrpSelectProfilesList.addAll(mProfileModel.getResource());
                        mGrpSelectProfilesAdapter.notifyDataSetChanged();
                    } else {
                        showSnackBar(mCoordinatorLayout, mNoFollowingErr);
                    }
                    break;
            }
        } else if (responseObj instanceof ImageModel) {
            ImageModel mImageModel = (ImageModel) responseObj;
            switch (responseType) {
                case RetrofitClient.UPLOAD_IMAGE_FILE_RESPONSE:
                    String mGrpChatImgUrl = getHttpFilePath(mImageModel.getmModels().get(0).getPath());
                    createNewGroup(mGrpChatImgUrl);
                    break;
            }
        } else if (responseObj instanceof GroupChatRoomModel) {
            GroupChatRoomModel mGroupChatRoomModel = (GroupChatRoomModel) responseObj;
            switch (responseType) {
                case RetrofitClient.CREATE_GRP_CHAT_ROOM_RESPONSE:
                    if (mGroupChatRoomModel.getResource() != null && mGroupChatRoomModel.getResource().size() > 0) {
                        setResult(RESULT_OK, new Intent().putExtra(GroupChatRoomModel.GRP_CHAT_ROOM_RES_MODEL, mGroupChatRoomModel.getResource().get(0)));
                        finish();
                    }
                    break;
            }
        } else if (responseObj instanceof SessionModel) {
            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            getFollowingProfileList();
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
        showSnackBar(mCoordinatorLayout, mInternetFailed);
    }

}
