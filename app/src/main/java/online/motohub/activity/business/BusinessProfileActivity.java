package online.motohub.activity.business;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.LoginActivity;
import online.motohub.adapter.business.BusinessPostAdapter;
import online.motohub.adapter.business.BusinessViewPagerAdapter;
import online.motohub.fragment.BaseFragment;
import online.motohub.fragment.Business.BusinessContactFragment;
import online.motohub.fragment.Business.BusinessEventFragment;
import online.motohub.fragment.Business.BusinessHomeFragment;
import online.motohub.fragment.Business.BusinessPhotosFragment;
import online.motohub.fragment.Business.BusinessSubScribedUserFragment;
import online.motohub.fragment.Business.BusinessVideosFragment;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.fragment.promoter.PromoterVideosFragment;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsWhoIsGoingModel;
import online.motohub.model.GalleryImgModel;
import online.motohub.model.GalleryVideoModel;
import online.motohub.model.PostsModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PromotersFollowers1;
import online.motohub.model.PushTokenModel;
import online.motohub.model.SessionModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.UrlUtils;

public class BusinessProfileActivity extends BaseActivity implements
        TabLayout.OnTabSelectedListener,
        BusinessPostAdapter.TotalRetrofitPostsResultCount {

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

    @BindView(R.id.followers_count_tv)
    TextView mFollowersCountTv;

    @BindView(R.id.view_pager_tab_layout)
    TabLayout mViewPagerTabLayout;

    @BindString(R.string.internet_failure)
    String mInternetFailed;

    @BindString(R.string.no_promoters_err)
    String mNoPromotersFoundErr;

    private ArrayList<Fragment> mFragments = new ArrayList<>();

    private BusinessViewPagerAdapter mViewPagerAdapter;
    private PromotersResModel mPromotersResModel;
    private ProfileResModel mMyProfileResModel;
    private PromotersFollowers1.Meta meta;
    private int mPromoterUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_profile);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mPromoterUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        callGetPromoters();
    }

    private void callGetPromoters() {
        String mFilter = "user_id=" + mPromoterUserID;
        RetrofitClient.getRetrofitInstance().callGetPromoters(this, mFilter, RetrofitClient.GET_PROMOTERS_RESPONSE);
    }

    private void callGetPromoterFollowers() {
        String mFilter = "PromoterUserID=" + mPromoterUserID;
        RetrofitClient.getRetrofitInstance().callGetPromotersFollowers(this, mFilter, RetrofitClient.GET_PROMOTERS_FOLLOWER_RESPONSE);
    }


    private void setProfile() {
        /*Bundle mBundle = new Bundle();
        mBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, mPromotersResModel);*/
        //MotoHub.getApplicationInstance().setmPromoterResModel(mPromotersResModel);
        EventBus.getDefault().postSticky(mPromotersResModel);
        BusinessHomeFragment mBusinessHomeFragment = new BusinessHomeFragment();
        //mBusinessHomeFragment.setArguments(mBundle);
        this.mFragments.add(mBusinessHomeFragment);
        BusinessEventFragment mBusinessEventsFragment = new BusinessEventFragment();
        //mBusinessEventsFragment.setArguments(mBundle);
        this.mFragments.add(mBusinessEventsFragment);
        BusinessPhotosFragment mBusinessPhotosFragment = new BusinessPhotosFragment();
        //mBusinessPhotosFragment.setArguments(mBundle);
        this.mFragments.add(mBusinessPhotosFragment);
        BusinessVideosFragment mBusinessVideosFrgment = new BusinessVideosFragment();
        //mBusinessVideosFrgment.setArguments(mBundle);
        this.mFragments.add(mBusinessVideosFrgment);

        String mUserType = mPromotersResModel.getUserType();

        switch (mUserType) {
            case AppConstants.CLUB:
            case AppConstants.SHOP:
                BusinessSubScribedUserFragment mBusinessSubScribedUserFragment = new BusinessSubScribedUserFragment();
                //mBusinessSubScribedUserFragment.setArguments(mBundle);
                this.mFragments.add(mBusinessSubScribedUserFragment);
                mViewPager.setOffscreenPageLimit(5);
                break;
            case AppConstants.TRACK:
                BusinessContactFragment mBusinessContactFragment = new BusinessContactFragment();
                //mBusinessContactFragment.setArguments(mBundle);
                this.mFragments.add(mBusinessContactFragment);
                mViewPager.setOffscreenPageLimit(5);
                break;
            default:
                mViewPager.setOffscreenPageLimit(4);
                break;
        }


        mViewPagerAdapter = new BusinessViewPagerAdapter(getSupportFragmentManager(), this, mFragments, mPromotersResModel.getUserType());
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPagerTabLayout.setupWithViewPager(mViewPager);
        mViewPagerTabLayout.addOnTabSelectedListener(this);
        if (mPromotersResModel != null) {
            setToolbar(mToolbar, mPromotersResModel.getName());
            //showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
            showToolbarBtn(mToolbar, R.id.toolbar_settings_img_btn);
            if (mPromotersResModel.getProfileImage() != null && !mPromotersResModel.getProfileImage().isEmpty()) {
                setImageWithGlide(mProfileImg, mPromotersResModel.getProfileImage(), R.drawable.default_profile_icon);
            }
            if (mPromotersResModel.getCoverImage() != null && !mPromotersResModel.getCoverImage().isEmpty()) {
                setCoverImageWithGlide(mCoverImg, mPromotersResModel.getCoverImage(), R.drawable.default_cover_img);
            }
            mPromoterNameTv.setText(mPromotersResModel.getName());

        }


        if (meta != null) {
            int followCount = meta.getCount();
            mFollowersCountTv.setText(String.valueOf(followCount));
        }

    }


    @OnClick({R.id.followers_box, R.id.cover_photo_img_view, R.id.toolbar_settings_img_btn, R.id.writePostBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                hideSoftKeyboard(this);
                finish();
                break;
            case R.id.followers_box:
                break;
            case R.id.cover_photo_img_view:
                if (mPromotersResModel.getCoverImage() != null && !mPromotersResModel.getCoverImage().trim().isEmpty()) {
                    moveLoadImageScreen(this, UrlUtils.FILE_URL + mPromotersResModel.getCoverImage().trim());
                }
                break;
            case R.id.toolbar_settings_img_btn:
                showAppDialog(AppDialogFragment.LOG_OUT_DIALOG, null);
                break;
            case R.id.writePostBtn:
                Gson mGson = new Gson();
                String mProfile = mGson.toJson(mPromotersResModel);
                if (mPromotersResModel != null && mPromotersResModel.getID() != 0) {
                    //MotoHub.getApplicationInstance().setmPromoterResModel(mPromotersResModel);
                    EventBus.getDefault().postSticky(mPromotersResModel);
                    startActivityForResult(new Intent(BusinessProfileActivity.this, BusinessWritePostActivity.class)
                            /*.putExtra(PromotersModel.PROMOTERS_RES_MODEL, mProfile)*/, AppConstants.WRITE_POST_REQUEST);
                }
                break;
        }

    }

    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String dialogType, StringBuilder profileTypesStr,
                                            ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);
        if (dialogType.equals(AppDialogFragment.LOG_OUT_DIALOG)) {
            logout();
        }
    }

    private void logout() {
        int mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID);
        String mFilter = "UserID=" + mUserID;
        RetrofitClient.getRetrofitInstance().callDeletePushToken(this, mFilter, RetrofitClient.FACEBOOK_LOGOUT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.WRITE_POST_REQUEST:
                    mViewPagerAdapter.getItem(0).onActivityResult(requestCode, resultCode, data);
                    break;
                default:
                    (mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
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

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof PromotersModel) {
            PromotersModel mPromotersModel = (PromotersModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_PROMOTERS_RESPONSE:
                    if (mPromotersModel.getResource() != null && mPromotersModel.getResource().size() > 0) {
                        mPromotersResModel = mPromotersModel.getResource().get(0);
                        callGetPromoterFollowers();
                    } else {
                        showSnackBar(mCoordinatorLayout, mNoPromotersFoundErr);
                    }
                    break;
            }
        } else if (responseObj instanceof PostsModel) {
            switch (responseType) {
                case RetrofitClient.GET_FEED_POSTS_RESPONSE:
                    if ((mViewPagerAdapter.getItem(mViewPagerTabLayout.getSelectedTabPosition())).isVisible()) {
                        ((BaseFragment) mViewPagerAdapter.getItem(0)).retrofitOnResponse(responseObj, responseType);
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
        } else if (responseObj instanceof GalleryImgModel) {
            ((BaseFragment) mViewPagerAdapter.getItem(2)).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof GalleryVideoModel) {
            ((BaseFragment) mViewPagerAdapter.getItem(3)).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof PromotersFollowers1) {
            PromotersFollowers1 promotersFollowers1 = (PromotersFollowers1) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_PROMOTERS_FOLLOWER_RESPONSE:
                    if (promotersFollowers1.getResource() != null && promotersFollowers1.getResource().size() > 0) {
                        meta = promotersFollowers1.getMeta();
                        setProfile();
                    } else {
                        showSnackBar(mCoordinatorLayout, getResources().getString(R.string.no_news_media_err));
                    }
                    break;

            }
        } else if (responseObj instanceof PushTokenModel) {
            clearBeforeLogout();
            Intent loginActivity = new Intent(this, LoginActivity.class);
            loginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginActivity);
            finish();
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
