package online.motohub.activity.performance_shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
import online.motohub.activity.UpgradeProfileActivity;
import online.motohub.activity.WritePostActivity;
import online.motohub.adapter.EventsFindAdapter;
import online.motohub.adapter.PerfProfileTabPagerAdapter;
import online.motohub.adapter.club.ClubPostsAdapter;
import online.motohub.fragment.BaseFragment;
import online.motohub.fragment.Performance_Shop.PerfVehiclesFragment;
import online.motohub.fragment.Performance_Shop.PerfVideosFragment;
import online.motohub.fragment.dialog.AppDialogFragment;
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
import online.motohub.model.PromoterSubsResModel;
import online.motohub.model.PromotersFollowers1;
import online.motohub.model.RacingModel;
import online.motohub.model.SessionModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerResModel;
import online.motohub.model.promoter_club_news_media.PromoterSubs;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.constants.AppConstants;
import online.motohub.util.CommonAPI;
import online.motohub.util.DialogManager;
import online.motohub.util.PreferenceUtils;
import online.motohub.util.UrlUtils;

public class PerformanceShopProfileActivity extends BaseActivity implements
        TabLayout.OnTabSelectedListener, ClubPostsAdapter.TotalRetrofitPostsResultCount {

    private final int SUBSCRIPTION_PURCHASED = 1;
    private final int UNSUBSCRIPTION_STATUS = 0;

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
    TextView mShopNameTv;
    @BindView(R.id.followBtn)
    TextView mFollowBtn;
    @BindView(R.id.followers_count_tv)
    TextView mFollowersCountTv;
    @BindView(R.id.view_pager_tab_layout)
    TabLayout mTabLayout;
    @BindString(R.string.internet_failure)
    String mInternetFailed;
    @BindString(R.string.no_performance_shop_err)
    String mNoNewsAndMediaFoundErr;
    @BindView(R.id.subscribeBtn)
    TextView mSubscribeBtn;
    @BindView(R.id.writePostBtn)
    Button mWritePostBtn;
    @BindView(R.id.writePostSeparator)
    View mWritePostSeparator;

    PerfProfileTabPagerAdapter mPerfProfileTabPageAdapter;

    private PromotersResModel mPromotersResModel;
    private ProfileResModel mMyProfileResModel;
    private PromotersFollowers1.Meta meta = new PromotersFollowers1.Meta();
    private int followCount;

    private String mSubscriptionId = "";
    private int mDeleteSubscribeID;

    private int mPostsRvOffset = 0, mPostsRvTotalCount = 0;
    private PromotersFollowers1.Resource mPromoterFollowers1;
    private String mSubscriptionType;
    private boolean isUpgradeToSubscription = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_profile);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Bundle mBundle = getIntent().getExtras();
        /*if (mBundle != null) {
            mPromotersResModel = (PromotersResModel) mBundle.get(PromotersModel.PROMOTERS_RES_MODEL);
            mMyProfileResModel = (ProfileResModel) getIntent().getExtras().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
            callGetPerformanceShop();
        }*/
        /*mMyProfileResModel = MotoHub.getApplicationInstance().getmProfileResModel();
        mPromotersResModel = MotoHub.getApplicationInstance().getmPromoterResModel();*/
        mMyProfileResModel = EventBus.getDefault().getStickyEvent(ProfileResModel.class);
        mPromotersResModel = EventBus.getDefault().getStickyEvent(PromotersResModel.class);
        callGetPerformanceShop();
        setUpPurchseSuccessUI();
        if (mPromotersResModel != null)
            setUpPurchseUI(mPromotersResModel.getSubscription_fee(), mMyProfileResModel.getID());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void callGetPerformanceShop() {
        if (mPromotersResModel != null && mPromotersResModel.getUserId() != 0) {
            String mFilter = "PromoterUserID=" + mPromotersResModel.getUserId();
            RetrofitClient.getRetrofitInstance().callGetPromotersFollowers(this, mFilter, RetrofitClient.GET_PROMOTERS_RESPONSE);
        }
    }

    private void setProfile() {
        try {
            mPerfProfileTabPageAdapter = new PerfProfileTabPagerAdapter(this, getSupportFragmentManager(), mMyProfileResModel, mPromotersResModel);
            mViewPager.setAdapter(mPerfProfileTabPageAdapter);
            mTabLayout.setupWithViewPager(mViewPager);
            mTabLayout.addOnTabSelectedListener(this);
            mViewPager.setOffscreenPageLimit(5);
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
                            .apply(new RequestOptions()
                                    .error(R.drawable.default_cover_img)
                                    .centerCrop()
                                    .dontAnimate())
                            .into(mCoverImg);
                }
                setToolbar(mToolbar, mPromotersResModel.getName());
                mShopNameTv.setText(mPromotersResModel.getName());
                List<PromoterFollowerResModel> mPromoterFollowerResModelList = mPromotersResModel.getPromoterFollowerByPromoterUserID();
                PromoterFollowerResModel mPromoterFollowerResModel = new PromoterFollowerResModel();
                mPromoterFollowerResModel.setProfileID(mMyProfileResModel.getID());

                isAlreadyFollowed();
                callGetShopSubscription();
                if (meta != null) {
                    followCount = meta.getCount();
                    mFollowersCountTv.setText(String.valueOf(followCount));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // getNewsFeedPosts();
    }

    private void callGetShopSubscription() {
        String mFilter = "(PromoterID=" + mPromotersResModel.getUserId() + ") AND (" + "ProfileID=" + mMyProfileResModel.getID() + ")";
        RetrofitClient.getRetrofitInstance().GetPromoterSubs(this, mFilter, RetrofitClient.CALL_GET_PROMOTER_SUBS);
    }

    private void isAlreadyFollowed() {
        String mFollowRelation = mMyProfileResModel.getID() + "_" + mPromotersResModel.getUserId();
        RetrofitClient.getRetrofitInstance().callGetIsAlreadyFollowedPromoter(this, mFollowRelation, RetrofitClient.PROMOTER_IS_ALREADY_FOLLOWED);

    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.followBtn, R.id.subscribeBtn, R.id.followers_box, R.id.cover_photo_img_view, R.id.writePostBtn})
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
                String mSubsStr = mSubscribeBtn.getText().toString();
                if (mPurchaseDialog != null && mSubsStr.equals(getString(R.string.Subscribe))) {
                    if (mPromotersResModel.getSubscriptionStatus() == 0 || mPromotersResModel.getSubscription_planID().isEmpty())
                        showToast(this, getString(R.string.subscribe_err_shop));
                    else if (mPromotersResModel.getSubscriptionStatus() == 1 && mPromotersResModel.getSubscription_fee() == 0) {
                        mSubscriptionType = AppConstants.FREE_SUBSCRIPTION;
                        apiCallForSubscription("");
                    } else
                        mPurchaseDialog.show();
                } else if (mSubsStr.equals(getString(R.string.un_subscribe))) {
                    apiCallForUnSubscription();
                } else if (mSubsStr.equals(getString(R.string.upgrade))) {
                    mPurchaseDialog.show();
                }
                break;
            case R.id.cover_photo_img_view:
                if (!mPromotersResModel.getCoverImage().trim().isEmpty()) {
                    moveLoadImageScreen(this, UrlUtils.FILE_URL + mPromotersResModel.getCoverImage());
                }

                break;
            case R.id.writePostBtn:
                /*Gson mGson = new Gson();
                String mProfile = mGson.toJson(mMyProfileResModel);*/
                //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                EventBus.getDefault().postSticky(mMyProfileResModel);
                startActivityForResult(new Intent(PerformanceShopProfileActivity.this, WritePostActivity.class)
                        /*.putExtra(AppConstants.MY_PROFILE_OBJ, mProfile)*/
                        .putExtra(AppConstants.IS_NEWSFEED_POST, false)
                        .putExtra(AppConstants.USER_TYPE, "shop_user").putExtra(AppConstants.TO_SUBSCRIBED_USER_ID, mPromotersResModel.getUserId()), AppConstants.WRITE_POST_REQUEST);
                break;
        }
    }

    private void deleteSubsDataInTable() {
        JsonObject mInputObj = new JsonObject();

        mInputObj.addProperty(PromoterSubs.SUBS_STATUS, UNSUBSCRIPTION_STATUS);
        mInputObj.addProperty(PromoterSubs.ID_, mDeleteSubscribeID);
        JsonArray mArray = new JsonArray();
        mArray.add(mInputObj);
        RetrofitClient.getRetrofitInstance().callUpdateUnSubscription(this, mArray, RetrofitClient.CALL_REMOVE_PROMOTER_SUBS);
    }

    private void apiCallForUnSubscription() {
        RetrofitClient.getRetrofitInstance().postUnSubScribeRequestToClub(this, mSubscriptionId, "UNSUBSCRIBE", mPromotersResModel.getAccessToken(), RetrofitClient.PROMOTER_PAYMENT_UNSUBSCRIPTION);
    }

    private void manageSubscription(boolean isSubscribed) {
        if (isSubscribed) {
            if (isUpgradeToSubscription) {
                mSubscribeBtn.setText(getString(R.string.upgrade));
            } else {
                mSubscribeBtn.setText(getString(R.string.un_subscribe));
            }
            mWritePostBtn.setVisibility(View.VISIBLE);
            mWritePostSeparator.setVisibility(View.VISIBLE);
        } else {
            mSubscribeBtn.setText(getString(R.string.Subscribe));
            mWritePostBtn.setVisibility(View.GONE);
            mWritePostSeparator.setVisibility(View.GONE);
        }
    }


    private void subscriptionCall(Object responseObj) {
        PaymentModel mResponse = (PaymentModel) responseObj;
        if (mResponse.getStatus() != null) {
            /*if (mPromotersResModel.getSubscription_fee() == 0)
                apiCallToUpdatePromoterSubs(mResponse.getID(), mResponse.getCustomer(), UNSUBSCRIPTION_STATUS);
            else*/
            apiCallToUpdatePromoterSubs(mResponse.getID(), mResponse.getCustomer(), SUBSCRIPTION_PURCHASED);
        } else {
            String mErrorMsg = "Your card was declined.";
            if (mResponse.getMessage() != null) {
                mErrorMsg = mResponse.getMessage();
            }
            mErrorMsg = mErrorMsg + " " + getString(R.string.try_again);
            showToast(this, mErrorMsg);
        }
    }

    private void apiCallToUpdatePromoterSubs(String subs_id, String customer_id, int subStatus) {
        JsonObject mInputObj = new JsonObject();
        mInputObj.addProperty(PromoterSubs.PROMOTER_ID, mPromotersResModel.getUserId());
        mInputObj.addProperty(PromoterSubs.SUBS_STATUS, subStatus);
        mInputObj.addProperty(PromoterSubs.SUBS_ID, subs_id);
        mInputObj.addProperty(PromoterSubs.PROFILE_ID, mMyProfileResModel.getID());
        mInputObj.addProperty(PromoterSubs.USER_ID, mMyProfileResModel.getUserID());
        mInputObj.addProperty(PromoterSubs.PLAN_ID, mPromotersResModel.getSubscription_planID());
        mInputObj.addProperty(PromoterSubs.PROMOTER_TYPE, mPromotersResModel.getUserType());
        mInputObj.addProperty(PromoterSubs.CUSTOMER_ID, customer_id);
        JsonArray mArray = new JsonArray();
        mArray.add(mInputObj);
        RetrofitClient.getRetrofitInstance().callUpdatePromoterSubs(this, mArray, RetrofitClient.UPDATE_SUBSCRIPTION);
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

    private void apiCallForSubscription(String token) {
        RetrofitClient.getRetrofitInstance().postSubScribeRequestToClub(this, mPromotersResModel.getSubscription_planID(),
                PreferenceUtils.getInstance(this).getStrData(PreferenceUtils.USER_EMAIL), token,
                "SUBSCRIBE", mPromotersResModel.getStripeUserId(), mPromotersResModel.getSubscription_fee(), RetrofitClient.PROMOTER_PAYMENT_SUBSCRIPTION);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RetrofitClient.UPDATE_FEED_COUNT:
                    mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition()).onActivityResult(requestCode, resultCode, data);
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
                case EventsFindAdapter.EVENT_PAYMENT_REQ_CODE:
                    String mToken = data.getStringExtra("TOKEN");
                    apiCallForSubscription(mToken);
                    break;
                case AppConstants.POST_COMMENT_REQUEST:
                case AppConstants.POST_UPDATE_SUCCESS:
                case AppConstants.REPORT_POST_SUCCESS:
                case AppConstants.WRITE_POST_REQUEST:
                    mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition()).onActivityResult(requestCode, resultCode, data);
                    break;
                default:
                    mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition()).onActivityResult(requestCode, resultCode, data);
                    break;
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
                    if (mPromoterFollowerModel.getResource() != null && mPromoterFollowerModel.getResource().size() > 0) {
                        mPromotersResModel.setIsFollowing(true);
                      /*  ArrayList<PromoterFollowerResModel> mPromoterFollowerResModelList = mPromotersResModel.getPromoterFollowerByPromoterUserID();
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
                    if ((mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible() && mPerfProfileTabPageAdapter.getItem(0) != null) {
                        ((BaseFragment) mPerfProfileTabPageAdapter.getItem(0)).retrofitOnResponse(responseObj, responseType);
                    }
                    break;
                case RetrofitClient.SHARED_POST_RESPONSE:
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        ((BaseFragment) mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                        showSnackBar(mCoordinatorLayout, getResources().getString(R.string.post_shared));
                    }
                    break;
                case RetrofitClient.FEED_VIDEO_COUNT:
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        ((BaseFragment) mPerfProfileTabPageAdapter
                                .getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                    }
                    break;
                case RetrofitClient.ADD_FEED_COUNT:
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        ((BaseFragment) mPerfProfileTabPageAdapter
                                .getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                    }
                    break;
                case RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE:
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        ((BaseFragment) mPerfProfileTabPageAdapter
                                .getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                        showSnackBar(mCoordinatorLayout, getResources().getString(R.string.post_delete));
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
            // getNewsFeedPosts();
        } else if (responseObj instanceof NotificationBlockedUsersModel) {
            ((BaseFragment) mPerfProfileTabPageAdapter
                    .getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof FeedShareModel) {
            ((BaseFragment) mPerfProfileTabPageAdapter
                    .getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof FeedLikesModel) {
            ((BaseFragment) mPerfProfileTabPageAdapter
                    .getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof EventsModel) {
            if (mPerfProfileTabPageAdapter.getItem(1).isVisible()) {
                ((BaseFragment) mPerfProfileTabPageAdapter
                        .getItem(1)).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof EventsWhoIsGoingModel) {
            if ((mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mPerfProfileTabPageAdapter
                        .getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof PaymentModel) {
            if (responseType == RetrofitClient.PROMOTER_PAYMENT_SUBSCRIPTION)
                subscriptionCall(responseObj);
            else {
                PaymentModel mResponse = (PaymentModel) responseObj;
                if (mResponse.getStatus() != null && mResponse.getStatus().equals("succeeded")) {
                    if ((mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                        ((BaseFragment) mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                    }
                } else {
                    String mErrorMsg = "Your card was declined.";
                    if (mResponse.getMessage() != null) {
                        mErrorMsg = mResponse.getMessage();
                    }
                    mErrorMsg = mErrorMsg + " " + getString(R.string.try_again);
                    showToast(this, mErrorMsg);
                }
            }

        } else if (responseObj instanceof RacingModel) {
            showSnackBar(mCoordinatorLayout, "Successfully booked an event.");
        } else if (responseObj instanceof EventAnswersModel) {
            if ((mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof SessionModel) {
            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }
            if (mPerfProfileTabPageAdapter != null) {
                if ((mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                    ((BaseFragment) mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                }
            }
        } else if (responseObj instanceof GalleryImgModel) {
            ((BaseFragment) mPerfProfileTabPageAdapter.getItem(2)).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof GalleryVideoModel) {
            ((BaseFragment) mPerfProfileTabPageAdapter.getItem(3)).retrofitOnResponse(responseObj, responseType);
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
        } else if (responseObj instanceof PromoterSubsResModel) {
            PromoterSubsResModel mPromoterSub = (PromoterSubsResModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_SHOP_VEHICLES:
                    ((BaseFragment) mPerfProfileTabPageAdapter.getItem(4)).retrofitOnResponse(responseObj, responseType);
                    break;
                case RetrofitClient.CALL_GET_PROMOTER_SUBS:
                    if (mPromoterSub.getResource().size() > 0) {
                        mSubscriptionId = mPromoterSub.getResource().get(0).getSubscriptionID();
                        mDeleteSubscribeID = mPromoterSub.getResource().get(0).getID();
                        if (mPromoterSub.getResource().get(0).getFailedSubscription_by_CustomerID() != null) {
                            if (getCurrentDate().equals(mPromoterSub.getResource().get(0).getFailedSubscription_by_CustomerID().getSubscriptionExpiry()))
                                isUpgradeToSubscription = mPromoterSub.getResource().get(0).getFailedSubscription_by_CustomerID().getStatus() == AppConstants.FAILED_SUBSCRIPTION_STATUS;
                        }
                        manageSubscription(true);
                    } else {
                        manageSubscription(false);
                    }
                    break;
                case RetrofitClient.UPDATE_SUBSCRIPTION:
                    mSubscriptionId = mPromoterSub.getResource().get(0).getSubscriptionID();
                    mDeleteSubscribeID = mPromoterSub.getResource().get(0).getID();
                    manageSubscription(true);
                    ((PerfVehiclesFragment) mPerfProfileTabPageAdapter.getItem(4)).updateVehicles();
                    if (mSubscriptionType.equals(AppConstants.FREE_SUBSCRIPTION))
                        showToast(this, "Success");
                    else {
                        if (mPurchaseSuccessDialog != null)
                            mPurchaseSuccessDialog.show();
                    }
                    break;
                case RetrofitClient.CALL_REMOVE_PROMOTER_SUBS:
                    manageSubscription(false);
                    ((PerfVehiclesFragment) mPerfProfileTabPageAdapter.getItem(4)).updateVehicles();
                    showToast(this, getString(R.string.un_subscribe_success));
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
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else if (code == RetrofitClient.GET_FEED_POSTS_RESPONSE) {
            mPostsRvTotalCount = 0;
        } else if (code == RetrofitClient.GET_VIDEO_FILE_RESPONSE) {
            ((PerfVideosFragment) mPerfProfileTabPageAdapter.getItem(3)).retrofitOnError(code, message);
        } else if (code == RetrofitClient.GET_SHOP_VEHICLES) {
            ((PerfVehiclesFragment) mPerfProfileTabPageAdapter.getItem(4)).retrofitOnError(code, message);
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
                ((BaseFragment) mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).alertDialogPositiveBtnClick(dialogType, position);
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
                ((BaseFragment) mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).alertDialogPositiveBtnClick(dialogType, position);
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
        return ((BaseFragment) mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).getTotalPostsResultCount();
    }

}