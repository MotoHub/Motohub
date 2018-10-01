package online.motohub.activity.performance_shop;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.adapter.PerfProfileTabPagerAdapter;
import online.motohub.adapter.news_and_media.NewsAndMediaPostsAdapter;
import online.motohub.fragment.BaseFragment;
import online.motohub.fragment.dialog.AppDialogFragment;
import online.motohub.model.EventAnswersModel;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsWhoIsGoingModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.GalleryImgModel;
import online.motohub.model.GalleryVideoModel;
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
import online.motohub.util.PreferenceUtils;
import online.motohub.util.UrlUtils;

public class PerformanceShopProfileActivity extends BaseActivity implements
        TabLayout.OnTabSelectedListener, NewsAndMediaPostsAdapter.TotalRetrofitPostsResultCount{

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

    @BindString(R.string.no_performance_shop_err)
    String mNoNewsAndMediaFoundErr;

    PerfProfileTabPagerAdapter mPerfProfileTabPageAdapter;

    private PromotersResModel mPromotersResModel;
    private ProfileResModel mMyProfileResModel;
    private PromotersFollowers1.Meta meta;
    private int followCount;
    private static final int mDataLimit = 15;
    private int mPostsRvOffset = 0, mPostsRvTotalCount = 0;
    private PromotersFollowers1.Resource mPromoterFollowers1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_and_media_profile);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mPromotersResModel = (PromotersResModel) mBundle.get(PromotersModel.PROMOTERS_RES_MODEL);
            mMyProfileResModel = (ProfileResModel) getIntent().getExtras().getSerializable(ProfileModel.MY_PROFILE_RES_MODEL);
            callGetPerformanceShop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void callGetPerformanceShop() {
        String mFilter = "id=" + mPromotersResModel.getID();
        RetrofitClient.getRetrofitInstance().callGetPromoters(this, mFilter, RetrofitClient.GET_PROMOTERS_RESPONSE);
    }

    private void setProfile() {
        mPerfProfileTabPageAdapter = new PerfProfileTabPagerAdapter(this, getSupportFragmentManager(), mMyProfileResModel, mPromotersResModel);
        mViewPager.setAdapter(mPerfProfileTabPageAdapter);
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

                /*Glide.with(this)
                        .load(mGlideUrl)
                        .apply(new RequestOptions().override(600, 100).placeholder(R.drawable.default_cover_img).error(R.drawable.default_cover_img))
                        .into(mCoverImg);*/

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

            if (isAlreadyFollowed()) {
                mPromotersResModel.setIsFollowing(true);
            }
            if (mPromotersResModel.getIsFollowing()) {
                mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                mFollowBtn.setText(R.string.following);
            }
            mFollowersCountTv.setText(String.valueOf(mPromoterFollowerResModelList.size()));
            if (meta != null) {
                followCount = meta.getCount();
                mFollowersCountTv.setText(String.valueOf(followCount));
            }
        }
        getNewsFeedPosts();
    }

    private boolean isAlreadyFollowed() {
        String mFollowRelation = mMyProfileResModel.getID() + "_" + mPromotersResModel.getUserId();
        ArrayList<PromoterFollowerResModel> mPromoterFollowerResModelList = mPromotersResModel.getPromoterFollowerByPromoterUserID();
        for (int i = 0; i < mPromoterFollowerResModelList.size(); i++) {
            if (mPromoterFollowerResModelList.get(i).getFollowRelation().trim().equals(mFollowRelation)) {
                return true;
            }
        }
        return false;
    }

    private void getNewsFeedPosts() {
        String mFilter;
        mFilter = "(ProfileID=" + mPromotersResModel.getUserId() + ") AND (user_type=shop)";
        RetrofitClient.getRetrofitInstance().callGetProfilePosts(this, mFilter, RetrofitClient.GET_FEED_POSTS_RESPONSE, mDataLimit, mPostsRvOffset);
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.followBtn, R.id.followers_box, R.id.cover_photo_img_view})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.followBtn:
                if (!mPromotersResModel.getIsFollowing()) {
                    CommonAPI.getInstance().callFollowPromoter(this, mPromotersResModel.getUserId(), mMyProfileResModel.getID());
                } else {
                    showProfileViewDialog();
                }
                break;
            case R.id.cover_photo_img_view:
                if (!mPromotersResModel.getCoverImage().trim().isEmpty()) {
                    moveLoadImageScreen(this, mPromotersResModel.getCoverImage());
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
                    ProfileResModel mMyProfileResModel = (ProfileResModel) data.getExtras()
                            .get(ProfileModel.MY_PROFILE_RES_MODEL);
                    assert mMyProfileResModel != null;
                    this.mMyProfileResModel = mMyProfileResModel;
                    setResult(RESULT_OK, new Intent()
                            .putExtra(ProfileModel.MY_PROFILE_RES_MODEL, this.mMyProfileResModel)
                            .putExtra(PromotersModel.PROMOTERS_RES_MODEL, mPromotersResModel));
                    break;
                case AppConstants.POST_COMMENT_REQUEST:
                    mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition()).onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
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
                        ArrayList<PromoterFollowerResModel> mPromoterFollowerResModelList = mPromotersResModel.getPromoterFollowerByPromoterUserID();
                        mPromoterFollowerResModelList.add(mPromoterFollowerModel.getResource().get(0));
                        mPromotersResModel.setPromoterFollowerByPromoterUserID(mPromoterFollowerResModelList);
                        mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                        mFollowBtn.setText(R.string.following);
                        followCount = followCount + 1;
                        mFollowersCountTv.setText(String.valueOf(mPromoterFollowerResModelList.size()));
                        showSnackBar(mCoordinatorLayout, getString(R.string.follow_success));
                        mPromotersResModel.setIsFollowing(true);
                    }
                    break;
                case RetrofitClient.GET_PROMOTER_UN_FOLLOW_RESPONSE:
                    if (mPromoterFollowerModel.getResource() != null
                            && mPromoterFollowerModel.getResource().size() > 0) {
                        mPromotersResModel.setIsFollowing(false);
                        ArrayList<PromoterFollowerResModel> mPromoterFollowerResModelList =
                                mPromotersResModel.getPromoterFollowerByPromoterUserID();
                        for (int i = 0; i < mPromoterFollowerResModelList.size(); i++) {
                            if (mPromoterFollowerResModelList.get(i).getFollowRelation().trim().equals(mPromoterFollowerModel.getResource().get(0).getFollowRelation().trim())) {
                                mPromoterFollowerResModelList.remove(i);
                            }
                        }
                        mPromotersResModel.setPromoterFollowerByPromoterUserID(mPromoterFollowerResModelList);
                        mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                        mFollowBtn.setText(R.string.follow);
                        followCount = followCount - 1;
                        mFollowersCountTv.setText(String.valueOf(mPromoterFollowerResModelList.size()));
                        showSnackBar(mCoordinatorLayout, getString(R.string.un_follow_success));
                        mPromotersResModel.setIsFollowing(false);
                    }
                    break;
            }
            Intent mIntent = new Intent();
            mIntent.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
            mIntent.putExtra(PromotersModel.PROMOTERS_RES_MODEL, mPromotersResModel);
            setResult(RESULT_OK, mIntent);
        } else if (responseObj instanceof PostsModel) {
            switch (responseType) {
                case RetrofitClient.GET_FEED_POSTS_RESPONSE:
                    if ((mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                        ((BaseFragment) mPerfProfileTabPageAdapter.getItem(0)).retrofitOnResponse(responseObj, responseType);
                    }
                    break;
                case RetrofitClient.SHARED_POST_RESPONSE:
                    PostsModel mPostsModel = (PostsModel) responseObj;
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        ((BaseFragment) mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                        showSnackBar(mCoordinatorLayout, getResources().getString(R.string.post_shared));
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
            getNewsFeedPosts();
        } else if (responseObj instanceof FeedShareModel) {
            ((BaseFragment) mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof FeedLikesModel) {
            ((BaseFragment) mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof EventsModel) {
            if (mPerfProfileTabPageAdapter.getItem(1).isVisible()) {
                ((BaseFragment) mPerfProfileTabPageAdapter.getItem(1)).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof EventsWhoIsGoingModel) {
            if ((mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mPerfProfileTabPageAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof PaymentModel) {
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
        }  else if (responseObj instanceof GalleryImgModel) {
            ((BaseFragment) mPerfProfileTabPageAdapter.getItem(2)).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof GalleryVideoModel) {
            ((BaseFragment) mPerfProfileTabPageAdapter.getItem(3)).retrofitOnResponse(responseObj, responseType);
        }else if (responseObj instanceof PromotersFollowers1) {
            PromotersFollowers1 promotersFollowers1 = (PromotersFollowers1) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_PROMOTERS_RESPONSE:
                    if (promotersFollowers1.getResource() != null && promotersFollowers1.getResource().size() > 0) {
                        mPromoterFollowers1 = promotersFollowers1.getResource().get(0);
                        meta = promotersFollowers1.getMeta();
                        setProfile();
                    } else {
                        showSnackBar(mCoordinatorLayout, "No Tracks");
                    }
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
            mPostsRvTotalCount = 0;
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
       /*     case AppDialogFragment.BOTTOM_SHARE_DIALOG:
                mCurrentPostPosition = position;
                CommonAPI.getInstance().callPostShare(this, mNewsFeedList.get(mCurrentPostPosition), mMyProfileResModel.getID());
                dismissAppDialog();
                break;*/
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

/*    private void scaleImage(ImageView view) throws NoSuchElementException {
        // Get bitmap from the the ImageView.
        Bitmap bitmap = null;

        try {
            Drawable drawing = view.getDrawable();
            bitmap = ((BitmapDrawable) drawing).getBitmap();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("No drawable on given view");
        } catch (ClassCastException e) {
            // Check bitmap is Ion drawable
            // bitmap = Ion.with(view).getBitmap();
        }

        // Get current dimensions AND the desired bounding box
        int width = 0;

        try {
            width = bitmap.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }

        int height = bitmap.getHeight();
        int bounding = dpToPx(250);
        Log.i("Test", "original width = " + Integer.toString(width));
        Log.i("Test", "original height = " + Integer.toString(height));
        Log.i("Test", "bounding = " + Integer.toString(bounding));

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        Log.i("Test", "xScale = " + Float.toString(xScale));
        Log.i("Test", "yScale = " + Float.toString(yScale));
        Log.i("Test", "scale = " + Float.toString(scale));

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        Log.i("Test", "scaled width = " + Integer.toString(width));
        Log.i("Test", "scaled height = " + Integer.toString(height));

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);

        Log.i("Test", "done");
    }

    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }*/

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
