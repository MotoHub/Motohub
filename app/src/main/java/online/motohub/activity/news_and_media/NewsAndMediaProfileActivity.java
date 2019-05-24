package online.motohub.activity.news_and_media;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

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
import online.motohub.adapter.NewsandMediaProfileTabPagerAdapter;
import online.motohub.adapter.news_and_media.NewsAndMediaPostsAdapter;
import online.motohub.fragment.BaseFragment;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.fragment.newsandmedia.NewsandMediaVideosFragment;
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

public class NewsAndMediaProfileActivity extends BaseActivity implements
        TabLayout.OnTabSelectedListener, NewsAndMediaPostsAdapter.TotalRetrofitPostsResultCount {

    @BindView(R.id.promoterCoLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.cover_photo_img_view)
    ImageView mCoverImg;

    @BindView(R.id.profile_img)
    CircleImageView mProfileImg;

    @BindView(R.id.promoterName)
    TextView mNewsAndMediaNameTv;

    @BindView(R.id.followBtn)
    TextView mFollowBtn;

    @BindView(R.id.followers_count_tv)
    TextView mFollowersCountTv;

    @BindString(R.string.internet_failure)
    String mInternetFailed;

    @BindView(R.id.perf_profile_pager_tab)
    TabLayout mTabLayout;

    @BindView(R.id.perf_profile_view_pager)
    ViewPager mViewPager;

    @BindString(R.string.no_news_media_err)
    String mNoNewsAndMediaFoundErr;

    NewsandMediaProfileTabPagerAdapter mNewsandMediaProfileTabPagerAdapter;

    private PromotersResModel mPromotersResModel;
    private PromotersFollowers1.Resource mPromoterFollowers1;
    private PromotersFollowers1.Meta meta;
    private ProfileResModel mMyProfileResModel;
    private int followCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_and_media_profile);
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
            mMyProfileResModel = (ProfileResModel) getIntent().getExtras().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
            callGetNewsAndMedias();
        }*/
        /*mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        mPromotersResModel = MotoHub.getApplicationInstance().getmPromoterResModel();*/
        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
        mPromotersResModel = EventBus.getDefault().getStickyEvent(PromotersResModel.class);
        callGetNewsAndMedias();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void callGetNewsAndMedias() {
        if (mPromotersResModel != null && mPromotersResModel.getUserId() != 0) {
            String mFilter = "";
            try {
                mFilter = "PromoterUserID=" + mPromotersResModel.getUserId();
            } catch (Exception e) {
                e.printStackTrace();
            }
            RetrofitClient.getRetrofitInstance().callGetPromotersFollowers(this, mFilter, RetrofitClient.GET_PROMOTERS_RESPONSE);
        }
    }

    private void setProfile() {
        try {
            mNewsandMediaProfileTabPagerAdapter = new NewsandMediaProfileTabPagerAdapter(this, getSupportFragmentManager(), mMyProfileResModel, mPromotersResModel);
            mViewPager.setAdapter(mNewsandMediaProfileTabPagerAdapter);
            mTabLayout.setupWithViewPager(mViewPager);
            mTabLayout.addOnTabSelectedListener(this);
            mViewPager.setOffscreenPageLimit(4);
            if (mPromotersResModel != null) {
                setToolbar(mToolbar, mPromotersResModel.getName());
                showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
                if (mPromotersResModel.getProfileImage() != null && !mPromotersResModel.getProfileImage().isEmpty()) {
                    setImageWithGlide(mProfileImg, mPromotersResModel.getProfileImage(), R.drawable.default_profile_icon);
                }
                if (mPromotersResModel.getCoverImage() != null && !mPromotersResModel.getCoverImage().isEmpty()) {
                    GlideUrl mGlideUrl = new GlideUrl(UrlUtils.FILE_URL + mPromotersResModel.getCoverImage(), new LazyHeaders.Builder()
                            .addHeader("X-DreamFactory-Api-Key", getString(R.string.dream_factory_api_key))
                            .build());

                    Glide.with(this)
                            .load(mGlideUrl)
                            .into(mCoverImg);
                }
                setToolbar(mToolbar, mPromotersResModel.getName());
                mNewsAndMediaNameTv.setText(mPromotersResModel.getName());
                List<PromoterFollowerResModel> mPromoterFollowerResModelList =
                        mPromotersResModel.getPromoterFollowerByPromoterUserID();
                PromoterFollowerResModel mPromoterFollowerResModel = new PromoterFollowerResModel();
                mPromoterFollowerResModel.setProfileID(mMyProfileResModel.getID());
                isAlreadyFollowed();

          /*  if (isAlreadyFollowed()) {
                mPromotersResModel.setIsFollowing(true);
            }
            if (mPromotersResModel.getIsFollowing()) {
                mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                mFollowBtn.setText(R.string.following);
            }
          */
                // mFollowersCountTv.setText(String.valueOf(mPromoterFollowerResModelList.size()));
                if (meta != null) {
                    followCount = meta.getCount();
                    mFollowersCountTv.setText(String.valueOf(followCount));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //callCheckFollow();
        // getNewsFeedPosts();
    }

/*    private void callCheckFollow() {
        String mFilter = "FollowRelation=" + mMyProfileResModel.getID() + "_" + mPromotersResModel.getUserId();
        RetrofitClient.getRetrofitInstance().callCheckFollowers(this, mFilter, RetrofitClient.CHECK_FOLLOWER_STATUS);
    }*/

   /* private void getNewsFeedPosts() {
        String mFilter = "(ProfileID=" + mPromotersResModel.getUserId() + ") AND (user_type=newsmedia)";
        RetrofitClient.getRetrofitInstance().callGetProfilePosts(this, mFilter, RetrofitClient.GET_FEED_POSTS_RESPONSE, mDataLimit, mPostsRvOffset);
    }*/

    @OnClick({R.id.toolbar_back_img_btn, R.id.followBtn, R.id.followers_box, R.id.cover_photo_img_view})
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
            case R.id.cover_photo_img_view:
                if (!mPromotersResModel.getCoverImage().trim().isEmpty()) {
                    moveLoadImageScreen(this, UrlUtils.FILE_URL + mPromotersResModel.getCoverImage());
                }
                break;
        }
    }

    private void callUnFollowPromoterRequest() {
        String mFilter =
                "FollowRelation=" + mMyProfileResModel.getID() + "_" + mPromotersResModel.getUserId();
        RetrofitClient.getRetrofitInstance().callUnFollowPromoter(this,
                mFilter, RetrofitClient.GET_PROMOTER_UN_FOLLOW_RESPONSE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.FOLLOWERS_FOLLOWING_RESULT:
                    //ProfileResModel mMyProfileResModel = (ProfileResModel) data.getExtras().get(ProfileModel.MY_PROFILE_RES_MODEL);
                    //ProfileResModel mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
                    ProfileResModel mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
                    assert mMyProfileResModel != null;
                    this.mMyProfileResModel = mMyProfileResModel;
                   /* MotoHub.getApplicationInstance().setmProfileResModel(this.mMyProfileResModel);
                    MotoHub.getApplicationInstance().setmPromoterResModel(mPromotersResModel);*/
                    EventBus.getDefault().postSticky(this.mMyProfileResModel);
                    EventBus.getDefault().postSticky(mPromotersResModel);
                    setResult(RESULT_OK, new Intent()
                            /*.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, this.mMyProfileResModel)
                            .putExtra(PromotersModel.PROMOTERS_RES_MODEL, mPromotersResModel)*/);
                    break;
                case RetrofitClient.UPDATE_FEED_COUNT:
                    mNewsandMediaProfileTabPagerAdapter.getItem(mTabLayout.getSelectedTabPosition()).onActivityResult(requestCode, resultCode, data);
                    break;
                case AppConstants.POST_COMMENT_REQUEST:
                    mNewsandMediaProfileTabPagerAdapter.getItem(mTabLayout.getSelectedTabPosition()).onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

    private void isAlreadyFollowed() {
        String mFollowRelation = mMyProfileResModel.getID() + "_" + mPromotersResModel.getUserId();
        RetrofitClient.getRetrofitInstance().callGetIsAlreadyFollowedPromoter(this, mFollowRelation, RetrofitClient.PROMOTER_IS_ALREADY_FOLLOWED);

    /*    ArrayList<PromoterFollowerResModel> mPromoterFollowerResModelList = mPromotersResModel.getPromoterFollowerByPromoterUserID();
        for (int i = 0; i < mPromoterFollowerResModelList.size(); i++) {
            if (mPromoterFollowerResModelList.get(i).getFollowRelation().trim().equals(mFollowRelation)) {
                return true;
            }
        }
        return false;*/
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
                        setProfile();
                    } else {
                        showSnackBar(mCoordinatorLayout, mNoNewsAndMediaFoundErr);
                    }
                    break;
            }
        } else if (responseObj instanceof PromoterFollowerModel) {
            PromoterFollowerModel mPromoterFollowerModel = (PromoterFollowerModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_PROMOTER_FOLLOW_RESPONSE:
                    if (mPromoterFollowerModel.getResource() != null && mPromoterFollowerModel.getResource().size() > 0) {
                        mPromotersResModel.setIsFollowing(true);
                       /* ArrayList<PromoterFollowerResModel> mPromoterFollowerResModelList = mPromotersResModel.getPromoterFollowerByPromoterUserID();
                        mPromoterFollowerResModelList.add(mPromoterFollowerModel.getResource().get(0));
                        mPromotersResModel.setPromoterFollowerByPromoterUserID(mPromoterFollowerResModelList);*/
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
                     /*   ArrayList<PromoterFollowerResModel> mPromoterFollowerResModelList =
                                mPromotersResModel.getPromoterFollowerByPromoterUserID();
                        for (int i = 0; i < mPromoterFollowerResModelList.size(); i++) {
                            if (mPromoterFollowerResModelList.get(i).getFollowRelation().trim().equals(mPromoterFollowerModel.getResource().get(0).getFollowRelation().trim())) {
                                mPromoterFollowerResModelList.remove(i);
                            }
                        }
                        mPromotersResModel.setPromoterFollowerByPromoterUserID(mPromoterFollowerResModelList);*/
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
                    if ((mNewsandMediaProfileTabPagerAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                        ((BaseFragment) mNewsandMediaProfileTabPagerAdapter.getItem(0)).retrofitOnResponse(responseObj, responseType);
                    }
                    break;
                case RetrofitClient.SHARED_POST_RESPONSE:
                    PostsModel mnPostsModel = (PostsModel) responseObj;
                    if (mPostsModel.getResource() != null && mnPostsModel.getResource().size() > 0) {
                        ((BaseFragment) mNewsandMediaProfileTabPagerAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                        showSnackBar(mCoordinatorLayout, getResources().getString(R.string.post_shared));
                    }
                    break;
                case RetrofitClient.FEED_VIDEO_COUNT:
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        ((BaseFragment) mNewsandMediaProfileTabPagerAdapter
                                .getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                    }
                    break;
                case RetrofitClient.ADD_FEED_COUNT:
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        ((BaseFragment) mNewsandMediaProfileTabPagerAdapter
                                .getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                    }
                    break;
                case RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE:
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        ((BaseFragment) mNewsandMediaProfileTabPagerAdapter
                                .getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                        showSnackBar(mCoordinatorLayout, getResources().getString(R.string.post_delete));
                    }
                    break;
            }
        } else if (responseObj instanceof NotificationBlockedUsersModel) {
            ((BaseFragment) mNewsandMediaProfileTabPagerAdapter
                    .getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof FeedShareModel) {
            ((BaseFragment) mNewsandMediaProfileTabPagerAdapter
                    .getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof FeedLikesModel) {
            ((BaseFragment) mNewsandMediaProfileTabPagerAdapter
                    .getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof EventsModel) {
            if (mNewsandMediaProfileTabPagerAdapter.getItem(1).isVisible()) {
                ((BaseFragment) mNewsandMediaProfileTabPagerAdapter
                        .getItem(1)).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof EventsWhoIsGoingModel) {
            if ((mNewsandMediaProfileTabPagerAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mNewsandMediaProfileTabPagerAdapter
                        .getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof PaymentModel) {
            PaymentModel mResponse = (PaymentModel) responseObj;
            if (mResponse.getStatus() != null && mResponse.getStatus().equals("succeeded")) {
                if ((mNewsandMediaProfileTabPagerAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                    ((BaseFragment) mNewsandMediaProfileTabPagerAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
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
            if ((mNewsandMediaProfileTabPagerAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mNewsandMediaProfileTabPagerAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof SessionModel) {
            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            if (mNewsandMediaProfileTabPagerAdapter != null) {
                if ((mNewsandMediaProfileTabPagerAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                    ((BaseFragment) mNewsandMediaProfileTabPagerAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                }
            }
        } else if (responseObj instanceof GalleryImgModel) {
            ((BaseFragment) mNewsandMediaProfileTabPagerAdapter.getItem(2)).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof GalleryVideoModel) {
            ((BaseFragment) mNewsandMediaProfileTabPagerAdapter.getItem(3)).retrofitOnResponse(responseObj, responseType);
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
        } else if (code == RetrofitClient.GET_VIDEO_FILE_RESPONSE) {
            ((NewsandMediaVideosFragment) mNewsandMediaProfileTabPagerAdapter.getItem(3)).retrofitOnError(code, message);
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
    public void alertDialogPositiveBtnClick(BaseActivity activity, String dialogType, StringBuilder profileTypesStr, ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);
        switch (dialogType) {
            case AppDialogFragment.BOTTOM_SHARE_DIALOG:
                ((BaseFragment) mNewsandMediaProfileTabPagerAdapter.getItem(mTabLayout.getSelectedTabPosition())).alertDialogPositiveBtnClick(dialogType, position);
                break;
            case AppDialogFragment.DIALOG_PROMOTER_PROFILE_VIEW:
                callUnFollowPromoterRequest();
                dismissAppDialog();
                break;
        }
    }

    @Override
    public void alertDialogNegativeBtnClick() {
        super.alertDialogNegativeBtnClick();
        dismissAppDialog();
    }


    @Override
    public void retrofitOnFailure(int responseType, String message) {
        super.retrofitOnFailure(responseType, message);
        if (responseType == RetrofitClient.POST_DATA_FOR_PAYMENT) {
            showSnackBar(mCoordinatorLayout, message);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public int getTotalPostsResultCount() {
        return ((BaseFragment) mNewsandMediaProfileTabPagerAdapter.getItem(mTabLayout.getSelectedTabPosition())).getTotalPostsResultCount();
    }
}
