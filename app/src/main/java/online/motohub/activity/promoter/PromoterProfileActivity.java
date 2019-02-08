package online.motohub.activity.promoter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.ComingSoonActivity;
import online.motohub.activity.UpgradeProfileActivity;
import online.motohub.adapter.promoter.PromoterPostsAdapter;
import online.motohub.adapter.promoter.PromoterViewPagerAdapter;
import online.motohub.application.MotoHub;
import online.motohub.fragment.BaseFragment;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.fragment.promoter.PromoterVideosFragment;
import online.motohub.model.EventAnswersModel;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsWhoIsGoingModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.GalleryImgModel;
import online.motohub.model.GalleryVideoModel;
import online.motohub.model.NotificationBlockedUsersModel;
import online.motohub.model.PaymentModel;
import online.motohub.model.PostsModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PromotersFollowers1;
import online.motohub.model.RacingModel;
import online.motohub.model.SessionModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerResModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.CommonAPI;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.UrlUtils;

public class PromoterProfileActivity extends BaseActivity implements
        TabLayout.OnTabSelectedListener,
        PromoterPostsAdapter.TotalRetrofitPostsResultCount {

    @BindView(R.id.promoterCoLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.cover_photo_img_view)
    ImageView mCoverImg;

    @BindView(R.id.profile_img)
    CircleImageView mProfileImg;

    @BindView(R.id.promoterName)
    TextView mPromoterNameTv;

    @BindView(R.id.followBtn)
    TextView mFollowBtn;

    @BindView(R.id.followers_count_tv)
    TextView mFollowersCountTv;

    @BindView(R.id.view_pager_tab_layout)
    TabLayout mViewPagerTabLayout;

    @BindString(R.string.internet_failure)
    String mInternetFailed;

    @BindString(R.string.no_promoters_err)
    String mNoPromotersFoundErr;

    private PromoterViewPagerAdapter mViewPagerAdapter;
    private ProfileResModel mMyProfileResModel;
    private PromotersResModel mPromotersResModel;
    private PromotersFollowers1.Resource mPromoterFollowers1;
    private PromotersFollowers1.Meta meta = new PromotersFollowers1.Meta();
    private int followCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promoters_profile);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initView() {
        Bundle mBundle = getIntent().getExtras();
        /*if (mBundle != null) {
            mPromotersResModel = (PromotersResModel) mBundle.get(PromotersModel.PROMOTERS_RES_MODEL);
            if(getIntent().hasExtra(ProfileModel.MY_PROFILE_RES_MODEL))
                 mMyProfileResModel = (ProfileResModel) getIntent().getExtras().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
            callGetPromoters();
        }*/
        /*mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        mPromotersResModel = MotoHub.getApplicationInstance().getmPromoterResModel();*/
        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
        mPromotersResModel = EventBus.getDefault().getStickyEvent(PromotersResModel.class);
        callGetPromoters();
    }

    private void callGetPromoters() {
        String mFilter = "PromoterUserID=" + mPromotersResModel.getUserId();
        RetrofitClient.getRetrofitInstance().callGetPromotersFollowers(this, mFilter, RetrofitClient.GET_PROMOTERS_RESPONSE);
    }

    private void setProfile() {
        try {
            mViewPagerAdapter = new PromoterViewPagerAdapter(getSupportFragmentManager(), this, mPromotersResModel, mMyProfileResModel);
            mViewPager.setAdapter(mViewPagerAdapter);
            mViewPagerTabLayout.setupWithViewPager(mViewPager);
            mViewPager.setOffscreenPageLimit(4);
            mViewPagerTabLayout.addOnTabSelectedListener(this);
            if (mPromotersResModel != null) {
                setToolbar(mToolbar, mPromotersResModel.getName());
                showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
                if (mPromotersResModel.getProfileImage() != null && !mPromotersResModel.getProfileImage().isEmpty()) {
                    setImageWithGlide(mProfileImg, mPromotersResModel.getProfileImage(), R.drawable.default_profile_icon);
                }
                if (mPromotersResModel.getCoverImage() != null && !mPromotersResModel.getCoverImage().isEmpty()) {
                    setCoverImageWithGlide(mCoverImg, mPromotersResModel.getCoverImage(), R.drawable.default_cover_img);
                }
                mPromoterNameTv.setText(mPromotersResModel.getName());
                List<PromoterFollowerResModel> mPromoterFollowerResModelList =
                        mPromotersResModel.getPromoterFollowerByPromoterUserID();
                PromoterFollowerResModel mPromoterFollowerResModel = new PromoterFollowerResModel();
                mPromoterFollowerResModel.setProfileID(mMyProfileResModel.getID());
                isAlreadyFollowed();

                if (meta != null) {
                    followCount = meta.getCount();
                    mFollowersCountTv.setText(String.valueOf(followCount));
                }
                callCheckFollow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callCheckFollow() {
        String mFilter = "FollowRelation=" + mMyProfileResModel.getID() + "_" + mPromotersResModel.getUserId();
        RetrofitClient.getRetrofitInstance().callCheckFollowers(this, mFilter, RetrofitClient.CHECK_FOLLOWER_STATUS);
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.followBtn, R.id.followers_box, R.id.subscribeBtn, R.id.cover_photo_img_view})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                hideSoftKeyboard(this);
                finish();
                break;
            case R.id.followBtn:
                try {
                    if (!mPromotersResModel.getIsFollowing()) {
                        CommonAPI.getInstance().callFollowPromoter(this, mPromotersResModel.getUserId(), mMyProfileResModel.getID());
                    } else {
                        showProfileViewDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.subscribeBtn:
                startActivity(new Intent(this, ComingSoonActivity.class));
                break;
            case R.id.followers_box:
                break;
            case R.id.cover_photo_img_view:
                if (!mPromotersResModel.getCoverImage().trim().isEmpty()) {
                    moveLoadImageScreen(this, UrlUtils.FILE_URL + mPromotersResModel.getCoverImage().trim());
                }
                break;
        }
    }

    private void isAlreadyFollowed() {
        String mFollowRelation = mMyProfileResModel.getID() + "_" + mPromotersResModel.getUserId();
        RetrofitClient.getRetrofitInstance().callGetIsAlreadyFollowedPromoter(this, mFollowRelation, RetrofitClient.PROMOTER_IS_ALREADY_FOLLOWED);
    }

    private void callUnFollowPromoterRequest() {
        String mFilter =
                "FollowRelation=" + mMyProfileResModel.getID() + "_" + mPromotersResModel.getUserId();
        RetrofitClient.getRetrofitInstance().callUnFollowPromoter(this,
                mFilter, RetrofitClient.GET_PROMOTER_UN_FOLLOW_RESPONSE);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void showProfileViewDialog() {
        ArrayList<String> mArrayList = new ArrayList<>();
        mArrayList.add(ProfileModel.FOLLOWING);
        mArrayList.add(String.valueOf(mPromotersResModel.getID()));
        mArrayList.add(mPromotersResModel.getName());
        mArrayList.add(mPromotersResModel.getProfileImage());
        showAppDialog(AppDialogFragment.DIALOG_PROMOTER_PROFILE_VIEW, mArrayList);
    }

    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String dialogType, StringBuilder profileTypesStr, ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);
        switch (dialogType) {
            case AppDialogFragment.BOTTOM_SHARE_DIALOG:
                ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).alertDialogPositiveBtnClick(dialogType, position);
                break;
            case AppDialogFragment.DIALOG_PROMOTER_PROFILE_VIEW:
                callUnFollowPromoterRequest();
                dismissAppDialog();
                break;
            case AppDialogFragment.ALERT_SPECTATOR_UPDATE_DIALOG:
                String myProfileObj = new Gson().toJson(mMyProfileResModel);
                startActivity(new Intent(this, UpgradeProfileActivity.class).putExtra(AppConstants.MY_PROFILE_OBJ, myProfileObj));
                dismissAppDialog();
                break;
            default:
                ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).alertDialogPositiveBtnClick(dialogType, position);
                break;
        }
    }

    @Override
    public void alertDialogNegativeBtnClick() {
        super.alertDialogNegativeBtnClick();
        dismissAppDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        (mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition()))
                .onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RetrofitClient.UPDATE_FEED_COUNT:
                    mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition()).onActivityResult(requestCode, resultCode, data);
                    break;
                case AppConstants.FOLLOWERS_FOLLOWING_RESULT:
                    //ProfileResModel mMyProfileResModel = (ProfileResModel) data.getExtras().get(ProfileModel.MY_PROFILE_RES_MODEL);
                    //ProfileResModel mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
                    ProfileResModel mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
                    assert mMyProfileResModel != null;
                    this.mMyProfileResModel = mMyProfileResModel;
                    /*MotoHub.getApplicationInstance().setmProfileResModel(this.mMyProfileResModel);
                    MotoHub.getApplicationInstance().setmPromoterResModel(mPromotersResModel);*/
                    EventBus.getDefault().postSticky(this.mMyProfileResModel);
                    EventBus.getDefault().postSticky(mPromotersResModel);
                    setResult(RESULT_OK, new Intent()
                            /*.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, this.mMyProfileResModel)
                            .putExtra(PromotersModel.PROMOTERS_RES_MODEL, mPromotersResModel)*/);
                    break;
                case AppConstants.POST_COMMENT_REQUEST:
                case AppConstants.REPORT_POST_SUCCESS:
                case AppConstants.POST_UPDATE_SUCCESS:
                case AppConstants.WRITE_POST_REQUEST:
                    try {
                        mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition()).onActivityResult(requestCode, resultCode, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    try {
                        mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition()).onActivityResult(requestCode, resultCode, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof PromoterFollowerModel) {
            PromoterFollowerModel mPromoterFollowerModel = (PromoterFollowerModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_PROMOTER_FOLLOW_RESPONSE:
                    if (mPromoterFollowerModel.getResource() != null
                            && mPromoterFollowerModel.getResource().size() > 0) {
                        mPromotersResModel.setIsFollowing(true);
                        mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                        mFollowBtn.setText(R.string.following);
                        followCount = followCount + 1;
                        mFollowersCountTv.setText(String.valueOf(followCount));
                        showSnackBar(mCoordinatorLayout, getString(R.string.follow_success));
                        mPromotersResModel.setIsFollowing(true);
                        Intent mIntent = new Intent();
                        /*mIntent.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
                        mIntent.putExtra(PromotersModel.PROMOTERS_RES_MODEL, mPromotersResModel);*/
                        /*MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                        MotoHub.getApplicationInstance().setmPromoterResModel(mPromotersResModel);*/
                        EventBus.getDefault().postSticky(mMyProfileResModel);
                        EventBus.getDefault().postSticky(mPromotersResModel);
                        setResult(RESULT_OK, mIntent);
                    }
                    break;
                case RetrofitClient.GET_PROMOTER_UN_FOLLOW_RESPONSE:
                    if (mPromoterFollowerModel.getResource() != null
                            && mPromoterFollowerModel.getResource().size() > 0) {
                        mPromotersResModel.setIsFollowing(false);
                        mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                        mFollowBtn.setText(R.string.follow);
                        followCount = followCount - 1;
                        mFollowersCountTv.setText(String.valueOf(followCount));
                        showSnackBar(mCoordinatorLayout, getString(R.string.un_follow_success));
                        mPromotersResModel.setIsFollowing(false);
                        Intent mIntent = new Intent();
                        /*mIntent.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
                        mIntent.putExtra(PromotersModel.PROMOTERS_RES_MODEL, mPromotersResModel);*/
                        /*MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                        MotoHub.getApplicationInstance().setmPromoterResModel(mPromotersResModel);*/
                        EventBus.getDefault().postSticky(mMyProfileResModel);
                        EventBus.getDefault().postSticky(mPromotersResModel);
                        setResult(RESULT_OK, mIntent);
                    }
                    break;
                case RetrofitClient.PROMOTER_IS_ALREADY_FOLLOWED:
                    if (mPromoterFollowerModel.getResource() != null && mPromoterFollowerModel.getResource().size() > 0) {
                        mPromotersResModel.setIsFollowing(true);
                        mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                        mFollowBtn.setText(R.string.following);
                    } else {
                        mPromotersResModel.setIsFollowing(false);
                        mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                        mFollowBtn.setText(R.string.follow);
                    }
                    break;
            }

        } else if (responseObj instanceof PostsModel) {
            PostsModel mPostsModel = (PostsModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_FEED_POSTS_RESPONSE:
                    if ((mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).isVisible()) {
                        ((BaseFragment) mViewPagerAdapter.getItem(0)).retrofitOnResponse(responseObj, responseType);
                    }
                    break;
                case RetrofitClient.SHARED_POST_RESPONSE:
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                        showSnackBar(mCoordinatorLayout, getResources().getString(R.string.post_shared));
                    }
                    break;
                case RetrofitClient.FEED_VIDEO_COUNT:
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        ((BaseFragment) mViewPagerAdapter
                                .getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                    }
                    break;
                case RetrofitClient.ADD_FEED_COUNT:
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        ((BaseFragment) mViewPagerAdapter
                                .getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                    }
                    break;
                case RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE:
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        ((BaseFragment) mViewPagerAdapter
                                .getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                        showSnackBar(mCoordinatorLayout, getResources().getString(R.string.post_delete));
                    }
                    break;
            }
        } else if (responseObj instanceof EventsModel) {
            if (mViewPagerAdapter.getItem(1).isVisible()) {
                ((BaseFragment) mViewPagerAdapter.getItem(1)).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof EventsWhoIsGoingModel) {
            if ((mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof PaymentModel) {
            PaymentModel mResponse = (PaymentModel) responseObj;
            if (mResponse.getStatus() != null && mResponse.getStatus().equals("succeeded")) {
                if ((mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).isVisible()) {
                    ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                }
            } else {
                String mErrorMsg = "Your card was declined.";
                if (mResponse.getMessage() != null) {
                    mErrorMsg = mResponse.getMessage();
                }
                mErrorMsg = mErrorMsg + " " + getString(R.string.try_again);
                showToast(this, mErrorMsg);
            }

        } else if (responseObj instanceof RacingModel) {
            showSnackBar(mCoordinatorLayout, "Successfully booked an event.");
        } else if (responseObj instanceof EventAnswersModel) {
            if ((mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof SessionModel) {
            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            if (mViewPagerAdapter != null) {
                if ((mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).isVisible()) {
                    ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                }
            }
        } else if (responseObj instanceof NotificationBlockedUsersModel) {

            ((BaseFragment) mViewPagerAdapter
                    .getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);

        } else if (responseObj instanceof FeedShareModel) {

            ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);

        } else if (responseObj instanceof FeedLikesModel) {

            ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);

        } else if (responseObj instanceof GalleryImgModel) {
            ((BaseFragment) mViewPagerAdapter.getItem(2)).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof GalleryVideoModel) {
            ((BaseFragment) mViewPagerAdapter.getItem(3)).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof PromotersFollowers1) {
            PromotersFollowers1 promotersFollowers1 = (PromotersFollowers1) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_PROMOTERS_RESPONSE:
                    if (promotersFollowers1.getResource() != null && promotersFollowers1.getResource().size() > 0) {
                        mPromoterFollowers1 = promotersFollowers1.getResource().get(0);
                        meta = promotersFollowers1.getMeta();
                    } else {
                        meta.setCount(0);
                    }
                    setProfile();
                    break;
                case RetrofitClient.CHECK_FOLLOWER_STATUS:
                    if (promotersFollowers1.getResource() != null && promotersFollowers1.getResource().size() > 0) {
                        mPromotersResModel.setIsFollowing(true);
                        mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                        mFollowBtn.setText(R.string.following);
                    } else {
                        mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                        mFollowBtn.setText(R.string.follow);
                        mPromotersResModel.setIsFollowing(false);
                    }
                    break;
            }
        }

    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else if (code == RetrofitClient.GET_FEED_POSTS_RESPONSE) {
            ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnError(code, message);
        } else if (code == RetrofitClient.GET_VIDEO_FILE_RESPONSE) {
            ((PromoterVideosFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnError(code, message);
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

    @Override
    public void retrofitOnFailure(final int responseType) {
        super.retrofitOnFailure(responseType);
        if ((mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).isVisible()) {
            ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).retrofitOnFailure(responseType);
        }
        showSnackBar(mCoordinatorLayout, mInternetFailed);
    }

    @Override
    public void retrofitOnError(int code, String message, int responseType) {
        super.retrofitOnError(code, message, responseType);
        switch (responseType) {
            case RetrofitClient.POST_SHARES:
                showSnackBar(mCoordinatorLayout, "Already shared this post");
                break;
        }
    }

    @Override
    public int getTotalPostsResultCount() {
        return ((BaseFragment) mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).getTotalPostsResultCount();
    }

    @Override
    public void retrofitOnFailure(int responseType, String message) {
        super.retrofitOnFailure(responseType, message);
        if (responseType == RetrofitClient.POST_DATA_FOR_PAYMENT) {
            showSnackBar(mCoordinatorLayout, message);
        }
    }

}
