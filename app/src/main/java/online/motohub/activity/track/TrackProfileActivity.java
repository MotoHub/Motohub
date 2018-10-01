package online.motohub.activity.track;

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
import online.motohub.adapter.TrackProfileTabPagerAdapter;
import online.motohub.adapter.promoter.PromoterPostsAdapter;
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
import online.motohub.model.PurchasedAddOnModel;
import online.motohub.model.RacingModel;
import online.motohub.model.SessionModel;
import online.motohub.model.TrackResModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerResModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.CommonAPI;
import online.motohub.util.PreferenceUtils;

public class TrackProfileActivity extends BaseActivity implements
        TabLayout.OnTabSelectedListener, PromoterPostsAdapter.TotalRetrofitPostsResultCount {

    @BindView(R.id.track_profile_parent_view)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.track_profile_cover_iv)
    ImageView mCoverPic;

    @BindView(R.id.profileName)
    TextView mProfileName;

    @BindView(R.id.track_profile_description_tv)
    TextView mTrackDescriptionTv;

    @BindView(R.id.profile_img)
    CircleImageView mProfileImg;

    @BindView(R.id.track_profile_pager_tab)
    TabLayout mTabLayout;

    @BindView(R.id.track_profile_view_pager)
    ViewPager mViewPager;

    @BindView(R.id.followBtn)
    TextView mFollowBtn;

    @BindView(R.id.followers_count_tv)
    TextView mFollowersCountTv;

    @BindString(R.string.internet_failure)
    String mInternetFailed;

    TrackProfileTabPagerAdapter mTrackFeedTabAdapter;

    private TrackResModel mTrackResModel;
    private ProfileResModel mProfileResModel;
    private PromotersResModel mPromoterResModel;
    private PromotersFollowers1.Resource mPromoterFollowers1;
    private PromotersFollowers1.Meta meta;
    private int followCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_profile);
        ButterKnife.bind(this);
        getData();

    }

    private void getData() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mProfileResModel = (ProfileResModel) getIntent().getSerializableExtra(ProfileModel.MY_PROFILE_RES_MODEL);
            mPromoterResModel = (PromotersResModel) mBundle.get(PromotersModel.PROMOTERS_RES_MODEL);
            mTrackResModel = mPromoterResModel.getTrackByUserID();
            callGetTracks();
        }
    }

    private void callGetTracks() {

        String mFilter = "PromoterUserID=" + mPromoterResModel.getUserId();
        RetrofitClient.getRetrofitInstance().callGetPromotersFollowers(this, mFilter, RetrofitClient.GET_PROMOTERS_RESPONSE);
    }

    @Override
    public void alertDialogNegativeBtnClick() {
        super.alertDialogNegativeBtnClick();

        dismissAppDialog();

    }

    private void callCheckFollow() {
        String mFilter = "FollowRelation=" + mProfileResModel.getID() + "_" + mPromoterResModel.getUserId();
        RetrofitClient.getRetrofitInstance().callCheckFollowers(this, mFilter, RetrofitClient.CHECK_FOLLOWER_STATUS);
    }


    private void setProfileReady() {

        String mToolbarTitle;
        if (mTrackResModel != null) {

            mToolbarTitle = mTrackResModel.getTrackName();

            if (mTrackResModel.getTrackDescription() == null) {
                mTrackDescriptionTv.setVisibility(View.GONE);
            } else {
                mTrackDescriptionTv.setText(mTrackResModel.getTrackDescription());
            }
            setCoverImageWithGlide(mCoverPic, mPromoterResModel.getCoverImage(), R.drawable.default_cover_img);
        } else {
            mToolbarTitle = "TRACK PROFILE";
            mTrackDescriptionTv.setVisibility(View.GONE);
            mProfileName.setText(mPromoterResModel.getName());

        }
        mProfileName.setText(mPromoterResModel.getName());
        setImageWithGlide(mProfileImg, mPromoterResModel.getProfileImage(), R.drawable.default_profile_icon);
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        mTrackFeedTabAdapter = new TrackProfileTabPagerAdapter(this, getSupportFragmentManager(), mProfileResModel, mPromoterResModel);
        mViewPager.setAdapter(mTrackFeedTabAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(this);
        mViewPager.setOffscreenPageLimit(3);

        setToolbar(mToolbar, mToolbarTitle);
        List<PromoterFollowerResModel> mPromoterFollowerResModelList =
                mPromoterResModel.getPromoterFollowerByPromoterUserID();
        PromoterFollowerResModel mPromoterFollowerResModel = new PromoterFollowerResModel();
        mPromoterFollowerResModel.setProfileID(mProfileResModel.getID());

        /*if (isAlreadyFollowed()) {
            mPromoterResModel.setIsFollowing(true);
        }
        if (mPromoterResModel.getIsFollowing()) {
            mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
            mFollowBtn.setText(R.string.following);
        }
        mFollowersCountTv.setText(String.valueOf(mPromoterFollowerResModelList.size()));*/

        if (meta != null) {
            followCount = meta.getCount();
            mFollowersCountTv.setText(String.valueOf(followCount));
        }

        callCheckFollow();

    }

    //Begin Listener
    @OnClick({R.id.toolbar_back_img_btn, R.id.followBtn, R.id.followers_box, R.id.track_profile_cover_iv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.followBtn:
                if (!mPromoterResModel.getIsFollowing()) {
                    CommonAPI.getInstance().callFollowPromoter(this, mPromoterResModel.getUserId(), mProfileResModel.getID());
                } else {
                    showProfileViewDialog();
                }
                break;
            case R.id.track_profile_cover_iv:
                if (!mTrackResModel.getCoverImg().trim().isEmpty()) {
                    moveLoadImageScreen(this, mPromoterResModel.getCoverImage());
                }
                break;
        }
    }


    private boolean isAlreadyFollowed() {
        String mFollowRelation = mProfileResModel.getID() + "_" + mPromoterResModel.getUserId();
        ArrayList<PromoterFollowerResModel> mPromoterFollowerResModelList = mPromoterResModel.getPromoterFollowerByPromoterUserID();
        for (int i = 0; i < mPromoterFollowerResModelList.size(); i++) {
            if (mPromoterFollowerResModelList.get(i).getFollowRelation().trim().equals(mFollowRelation)) {
                return true;
            }
        }
        return false;
    }


    private void showProfileViewDialog() {
        ArrayList<String> mArrayList = new ArrayList<>();
        mArrayList.add(ProfileModel.FOLLOWING);
        mArrayList.add(String.valueOf(mPromoterResModel.getID()));
        mArrayList.add(mPromoterResModel.getName());
        mArrayList.add(mPromoterResModel.getProfileImage());
        showAppDialog(AppDialogFragment.DIALOG_PROMOTER_PROFILE_VIEW, mArrayList);
    }

    private void callUnFollowPromoterRequest() {
        String mFilter =
                "FollowRelation=" + mProfileResModel.getID() + "_" + mPromoterResModel.getUserId();
        RetrofitClient.getRetrofitInstance().callUnFollowPromoter(this,
                mFilter, RetrofitClient.GET_PROMOTER_UN_FOLLOW_RESPONSE);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    //Begin API response
    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof PromotersModel) {
            PromotersModel mPromotersModel = (PromotersModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_PROMOTERS_RESPONSE:
                    if (mPromotersModel.getResource() != null && mPromotersModel.getResource().size() > 0) {
                        mPromoterResModel = mPromotersModel.getResource().get(0);
                        setProfileReady();
                    } else {
                        showSnackBar(mCoordinatorLayout, "No Tracks");
                    }
                    break;
            }
        } else if (responseObj instanceof EventsWhoIsGoingModel) {
            if ((mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof PaymentModel) {
            PaymentModel mResponse = (PaymentModel) responseObj;
            if (mResponse.getStatus() != null && mResponse.getStatus().equals("succeeded")) {
                if ((mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                    ((BaseFragment) mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
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
            if ((mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof PurchasedAddOnModel) {
            if ((mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof EventAnswersModel) {
            if ((mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof PostsModel) {
            switch (responseType) {
                case RetrofitClient.SHARED_POST_RESPONSE:
                    PostsModel mPostsModel = (PostsModel) responseObj;
                    if (mPostsModel.getResource() != null && mPostsModel.getResource().size() > 0) {
                        showSnackBar(mCoordinatorLayout, getResources().getString(R.string.post_shared));
                    }
                    break;
                default:
                    if ((mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                        ((BaseFragment) mTrackFeedTabAdapter.getItem(0)).retrofitOnResponse(responseObj, responseType);
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
            if (mTrackFeedTabAdapter != null) {
                if ((mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                    ((BaseFragment) mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);
                }
            }
        } else if (responseObj instanceof FeedShareModel) {

            ((BaseFragment) mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);

        } else if (responseObj instanceof FeedLikesModel) {

            ((BaseFragment) mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).retrofitOnResponse(responseObj, responseType);

        } else if (responseObj instanceof PromoterFollowerModel) {
            PromoterFollowerModel mPromoterFollowerModel = (PromoterFollowerModel) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_PROMOTER_FOLLOW_RESPONSE:
                    if (mPromoterFollowerModel.getResource() != null
                            && mPromoterFollowerModel.getResource().size() > 0) {
                        mPromoterResModel.setIsFollowing(true);
                        ArrayList<PromoterFollowerResModel> mPromoterFollowerResModelList =
                                mPromoterResModel.getPromoterFollowerByPromoterUserID();
                        mPromoterFollowerResModelList
                                .add(mPromoterFollowerModel.getResource().get(0));
                        mPromoterResModel
                                .setPromoterFollowerByPromoterUserID(mPromoterFollowerResModelList);
                        mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                        mFollowBtn.setText(R.string.following);
                        followCount = followCount + 1;
                        mFollowersCountTv
                                .setText(String.valueOf(followCount));
                        showSnackBar(mCoordinatorLayout, getString(R.string.follow_success));
                        mPromoterResModel.setIsFollowing(true);

                    }
                    break;
                case RetrofitClient.GET_PROMOTER_UN_FOLLOW_RESPONSE:
                    if (mPromoterFollowerModel.getResource() != null
                            && mPromoterFollowerModel.getResource().size() > 0) {
                        mPromoterResModel.setIsFollowing(false);
                        ArrayList<PromoterFollowerResModel> mPromoterFollowerResModelList =
                                mPromoterResModel.getPromoterFollowerByPromoterUserID();
                        mPromoterFollowerResModelList.remove(mPromoterFollowerModel.getResource().get(0));

                        for (int i = 0; i < mPromoterFollowerResModelList.size(); i++) {
                            if (mPromoterFollowerResModelList.get(i).getFollowRelation().trim().equals(mPromoterFollowerModel.getResource().get(0).getFollowRelation().trim())) {
                                mPromoterFollowerResModelList.remove(i);
                            }
                        }
                        mPromoterResModel
                                .setPromoterFollowerByPromoterUserID(mPromoterFollowerResModelList);
                        mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                        mFollowBtn.setText(R.string.follow);
                        followCount = followCount - 1;
                        mFollowersCountTv
                                .setText(String.valueOf(followCount));
                        showSnackBar(mCoordinatorLayout, getString(R.string.un_follow_success));
                        mPromoterResModel.setIsFollowing(false);
                    }
                    break;
            }
            Intent mIntent = new Intent();
            mIntent.putExtra(ProfileModel.MY_PROFILE_RES_MODEL, mProfileResModel);
            mIntent.putExtra(PromotersModel.PROMOTERS_RES_MODEL, mPromoterResModel);
            setResult(RESULT_OK, mIntent);
        } else if (responseObj instanceof EventsModel) {
            if ((mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).isVisible()) {
                ((BaseFragment) mTrackFeedTabAdapter.getItem(1)).retrofitOnResponse(responseObj, responseType);
            }
        } else if (responseObj instanceof PromotersFollowers1) {
            PromotersFollowers1 promotersFollowers1 = (PromotersFollowers1) responseObj;
            switch (responseType) {
                case RetrofitClient.GET_PROMOTERS_RESPONSE:
                    if (promotersFollowers1.getResource() != null && promotersFollowers1.getResource().size() > 0) {
                        mPromoterFollowers1 = promotersFollowers1.getResource().get(0);
                        meta = promotersFollowers1.getMeta();
                        setProfileReady();
                    } else {
                        showSnackBar(mCoordinatorLayout, "No Tracks");
                    }
                    break;
                case RetrofitClient.CHECK_FOLLOWER_STATUS:
                    if (promotersFollowers1.getResource() != null && promotersFollowers1.getResource().size() > 0) {
                        mPromoterResModel.setIsFollowing(true);
                        mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                        mFollowBtn.setText(R.string.following);
                    } else {
                        mFollowBtn.setBackgroundResource(R.drawable.black_orange_btn_bg);
                        mFollowBtn.setText(R.string.follow);
                        mPromoterResModel.setIsFollowing(false);
                    }
                    break;
            }
        }  else if (responseObj instanceof GalleryImgModel) {
            ((BaseFragment) mTrackFeedTabAdapter.getItem(2)).retrofitOnResponse(responseObj, responseType);
        } else if (responseObj instanceof GalleryVideoModel) {
            ((BaseFragment) mTrackFeedTabAdapter.getItem(3)).retrofitOnResponse(responseObj, responseType);
        }
    }

    @Override
    public void alertDialogPositiveBtnClick(BaseActivity activity, String dialogType, StringBuilder profileTypesStr, ArrayList<String> profileTypes, int position) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position);
        switch (dialogType) {
            case AppDialogFragment.ALERT_SPECTATOR_UPDATE_DIALOG:
                String myProfileObj = new Gson().toJson(mProfileResModel);
                startActivity(new Intent(this, UpgradeProfileActivity.class).putExtra(AppConstants.MY_PROFILE_OBJ, myProfileObj));
                dismissAppDialog();
                break;

            case AppDialogFragment.BOTTOM_SHARE_DIALOG:
                ((BaseFragment) mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).alertDialogPositiveBtnClick(dialogType, position);
                break;

            case AppDialogFragment.DIALOG_PROMOTER_PROFILE_VIEW:
                callUnFollowPromoterRequest();
                dismissAppDialog();
                break;

            default:
                ((BaseFragment) (mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition()))).alertDialogPositiveBtnClick(dialogType, position);
                break;
        }
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);

        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else if (code == RetrofitClient.GET_FEED_POSTS_RESPONSE) {
            ((BaseFragment) (mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition()))).retrofitOnError(code, message);
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
    public void retrofitOnError(int code, String message, int responseType) {
        super.retrofitOnError(code, message, responseType);
        switch (responseType) {
            case RetrofitClient.POST_SHARES:
                showSnackBar(mCoordinatorLayout, "Already shared this post");
                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public int getTotalPostsResultCount() {
        return ((BaseFragment) mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition())).getTotalPostsResultCount();
    }
    //End of API Response

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        (mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition()))
                .onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.FOLLOWERS_FOLLOWING_RESULT:
                    assert data.getExtras() != null;
                    ProfileResModel mMyProfileResModel = (ProfileResModel) data.getExtras()
                            .get(ProfileModel.MY_PROFILE_RES_MODEL);
                    assert mMyProfileResModel != null;
                    this.mProfileResModel = mMyProfileResModel;
                    setResult(RESULT_OK, new Intent()
                            .putExtra(ProfileModel.MY_PROFILE_RES_MODEL, this.mProfileResModel)
                            .putExtra(PromotersModel.PROMOTERS_RES_MODEL, mPromoterResModel));
                    break;
                case AppConstants.POST_COMMENT_REQUEST:
                    mTrackFeedTabAdapter.getItem(mTabLayout.getSelectedTabPosition()).onActivityResult(requestCode, resultCode, data);
                    break;

            }
        }
    }

}
