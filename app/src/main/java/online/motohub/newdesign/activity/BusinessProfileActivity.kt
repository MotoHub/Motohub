package online.motohub.newdesign.activity

import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import android.text.TextUtils
import android.view.View
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_business_profile.*
import kotlinx.android.synthetic.main.common_header.*
import online.motohub.R
import online.motohub.activity.BaseActivity
import online.motohub.activity.business.BusinessWritePostActivity
import online.motohub.adapter.business.BusinessPostAdapter
import online.motohub.adapter.business.BusinessViewPagerAdapter
import online.motohub.fragment.BaseFragment
import online.motohub.fragment.Business.BusinessContactFragment
import online.motohub.fragment.Business.BusinessSubScribedUserFragment
import online.motohub.fragment.dialog.AppDialogFragment
import online.motohub.fragment.promoter.PromoterVideosFragment
import online.motohub.model.*
import online.motohub.model.promoter_club_news_media.PromotersResModel
import online.motohub.newdesign.constants.AppConstants
import online.motohub.newdesign.constants.BundleConstants
import online.motohub.newdesign.fragment.BusinessEventFragment
import online.motohub.newdesign.fragment.BusinessNewsFeedFragment
import online.motohub.newdesign.fragment.BusinessPhotosFragment
import online.motohub.newdesign.fragment.BusinessVideosFragment
import online.motohub.newdesign.viewmodel.BaseViewModelFactory
import online.motohub.newdesign.viewmodel.BusinessProfileViewModel
import online.motohub.retrofit.RetrofitClient
import online.motohub.util.PreferenceUtils
import online.motohub.util.UrlUtils
import org.greenrobot.eventbus.EventBus
import java.util.*

class BusinessProfileActivity : BaseActivity(), TabLayout.OnTabSelectedListener, BusinessPostAdapter.TotalRetrofitPostsResultCount {


    private val fragmentList = ArrayList<androidx.fragment.app.Fragment>()

    private var viewPagerAdapter: BusinessViewPagerAdapter? = null

    private var model: BusinessProfileViewModel? = null

    private var businessProfileObj: PromotersResModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_profile)
        initView()
    }

    private fun initView() {
        val activeBundle = intent.extras
        model = ViewModelProviders.of(this, BaseViewModelFactory(activity!!.application, activeBundle)).get(BusinessProfileViewModel::class.java)
        registerModel(model)
        model!!.followersCount.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                followersLay.visibility = if (it > 0) View.VISIBLE else View.GONE
                followersTxt.text = if (it > 1) getString(R.string.followers) else getString(R.string.follower)
                followersCountTxt.text = it.toString()
            }

        })
        model!!.initialize()

        businessProfileObj = model!!.businessProfileObj

        setProfile()
    }

    private fun setProfile() {
        setTitleTxt(businessProfileObj!!.name)
        val profileImage = businessProfileObj!!.profileImage.trim()
        val coverImage = businessProfileObj!!.coverImage.trim()
        val userType = businessProfileObj!!.userType
        if (!TextUtils.isEmpty(profileImage)) {
            setImageWithGlide(profileImg, profileImage, R.drawable.default_profile_icon)
        }
        if (!TextUtils.isEmpty(coverImage)) {
            setCoverImageWithGlide(coverImg, coverImage, R.drawable.default_cover_img)
        }
        profileName.text = businessProfileObj!!.name

        val mBundle = Bundle()
        mBundle.putString(BundleConstants.MY_PROFILE_OBJ, Gson().toJson(model!!.profileObj))
        mBundle.putString(BundleConstants.BUSINESS_PROFILE_OBJ, Gson().toJson(businessProfileObj!!))
        //MotoHub.getApplicationInstance().setmPromoterResModel(mPromotersResModel);
        EventBus.getDefault().postSticky(model!!.profileObj)
        EventBus.getDefault().postSticky(businessProfileObj!!)
        val mBusinessHomeFragment = BusinessNewsFeedFragment()
        mBusinessHomeFragment.arguments = mBundle
        this.fragmentList.add(mBusinessHomeFragment)
        val mBusinessEventsFragment = BusinessEventFragment()
        mBusinessEventsFragment.arguments = mBundle
        this.fragmentList.add(mBusinessEventsFragment)
        val mBusinessPhotosFragment = BusinessPhotosFragment()
        mBusinessPhotosFragment.arguments = mBundle
        this.fragmentList.add(mBusinessPhotosFragment)
        val mBusinessVideosFrgment = BusinessVideosFragment()
        mBusinessVideosFrgment.arguments = mBundle
        this.fragmentList.add(mBusinessVideosFrgment)



        when (userType) {
            AppConstants.CLUB, AppConstants.SHOP -> {
                val mBusinessSubScribedUserFragment = BusinessSubScribedUserFragment()
                //mBusinessSubScribedUserFragment.setArguments(mBundle);
                this.fragmentList.add(mBusinessSubScribedUserFragment)
                viewPager.offscreenPageLimit = 5
            }
            AppConstants.TRACK -> {
                val mBusinessContactFragment = BusinessContactFragment()
                //mBusinessContactFragment.setArguments(mBundle);
                this.fragmentList.add(mBusinessContactFragment)
                viewPager.offscreenPageLimit = 5
            }
            else -> viewPager.offscreenPageLimit = 4
        }


        viewPagerAdapter = BusinessViewPagerAdapter(supportFragmentManager, this, fragmentList, userType)
        viewPager.adapter = viewPagerAdapter
        viewPagerTabLay.setupWithViewPager(viewPager)
        viewPagerTabLay.addOnTabSelectedListener(this)


    }


    fun onClick(v: View) {
        when (v.id) {
            R.id.backIcon -> {
                hideSoftKeyboard(this)
                finish()
            }
            R.id.cover_photo_img_view -> {
                val coverImage = businessProfileObj!!.coverImage.trim()
                if (!TextUtils.isEmpty(coverImage)) {
                    moveLoadImageScreen(this, UrlUtils.FILE_URL + coverImage)
                }
            }
//            R.id.toolbar_settings_img_btn -> showAppDialog(AppDialogFragment.LOG_OUT_DIALOG, null)
            R.id.writePostBtn -> {
                EventBus.getDefault().postSticky(businessProfileObj)
                startActivityForResult(Intent(this@BusinessProfileActivity, BusinessWritePostActivity::class.java), AppConstants.WRITE_POST_REQUEST)/*.putExtra(PromotersModel.PROMOTERS_RES_MODEL, mProfile)*/
            }
        }

    }

    override fun alertDialogPositiveBtnClick(activity: BaseActivity, dialogType: String, profileTypesStr: StringBuilder,
                                             profileTypes: ArrayList<String>, position: Int) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position)
        if (dialogType == AppDialogFragment.LOG_OUT_DIALOG) {
            logout()
        }
    }

    private fun logout() {
        val mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID)
        val mFilter = "UserID=$mUserID"
        RetrofitClient.getRetrofitInstance().callDeletePushToken(this, mFilter, RetrofitClient.FACEBOOK_LOGOUT)

    }


    override fun onTabSelected(tab: TabLayout.Tab) {
        viewPager.currentItem = tab.position
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {}

    override fun onTabReselected(tab: TabLayout.Tab) {}

    override fun retrofitOnResponse(responseObj: Any, responseType: Int) {
        super.retrofitOnResponse(responseObj, responseType)
        if (responseObj is PostsModel) {
            when (responseType) {
                RetrofitClient.GET_FEED_POSTS_RESPONSE -> if (viewPagerAdapter!!.getItem(viewPagerTabLay.selectedTabPosition).isVisible) {
                    (viewPagerAdapter!!.getItem(0) as BaseFragment).retrofitOnResponse(responseObj, responseType)
                }
            }
        } else if (responseObj is EventsModel) {
            if (viewPagerAdapter!!.getItem(1).isVisible) {
                (viewPagerAdapter!!.getItem(1) as BaseFragment).retrofitOnResponse(responseObj, responseType)
            }
        } else if (responseObj is EventsWhoIsGoingModel) {
            if (viewPagerAdapter!!.getItem(viewPagerTabLay.selectedTabPosition).isVisible) {
                (viewPagerAdapter!!.getItem(viewPagerTabLay.selectedTabPosition) as BaseFragment).retrofitOnResponse(responseObj, responseType)
            }
        } else if (responseObj is SessionModel) {
            if (responseObj.sessionToken == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, responseObj.sessionId)
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, responseObj.sessionToken)
            }
            if (viewPagerAdapter != null) {
                if (viewPagerAdapter!!.getItem(viewPagerTabLay.selectedTabPosition).isVisible) {
                    (viewPagerAdapter!!.getItem(viewPagerTabLay.selectedTabPosition) as BaseFragment).retrofitOnResponse(responseObj, responseType)
                }
            }
        } else if (responseObj is GalleryImgModel) {
            (viewPagerAdapter!!.getItem(2) as BaseFragment).retrofitOnResponse(responseObj, responseType)
        } else if (responseObj is GalleryVideoModel) {
            (viewPagerAdapter!!.getItem(3) as BaseFragment).retrofitOnResponse(responseObj, responseType)
        } else if (responseObj is PushTokenModel) {
            clearBeforeLogout()
            val loginActivity = Intent(this, LoginActivity::class.java)
            loginActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(loginActivity)
            finish()
        }

    }

    override fun retrofitOnError(code: Int, message: String) {
        super.retrofitOnError(code, message)
        if (message == "Unauthorized" || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE)
        } else if (code == RetrofitClient.GET_FEED_POSTS_RESPONSE) {
            (viewPagerAdapter!!.getItem(viewPagerTabLay.selectedTabPosition) as BaseFragment).retrofitOnError(code, message)
        } else if (code == RetrofitClient.GET_VIDEO_FILE_RESPONSE) {
            (viewPagerAdapter!!.getItem(viewPagerTabLay.selectedTabPosition) as PromoterVideosFragment).retrofitOnError(code, message)
        } else {
            val mErrorMsg = "$code - $message"
            showSnackBar(parentLay, mErrorMsg)
        }
    }

    override fun retrofitOnSessionError(code: Int, message: String) {
        super.retrofitOnSessionError(code, message)
        val mErrorMsg = "$code - $message"
        showSnackBar(parentLay, mErrorMsg)
    }

    override fun retrofitOnFailure() {
        super.retrofitOnFailure()
        showSnackBar(parentLay, getString(R.string.internet_err))
    }

    override fun retrofitOnFailure(responseType: Int) {
        super.retrofitOnFailure(responseType)
        if (viewPagerAdapter!!.getItem(viewPagerTabLay.selectedTabPosition).isVisible) {
            (viewPagerAdapter!!.getItem(viewPagerTabLay.selectedTabPosition) as BaseFragment).retrofitOnFailure(responseType)
        }
        showSnackBar(parentLay, getString(R.string.internet_err))
    }

    override fun getTotalPostsResultCount(): Int {
        return (viewPagerAdapter!!.getItem(viewPagerTabLay.selectedTabPosition) as BaseFragment).totalPostsResultCount
    }

    override fun retrofitOnFailure(responseType: Int, message: String) {
        super.retrofitOnFailure(responseType, message)
        if (responseType == RetrofitClient.POST_DATA_FOR_PAYMENT) {
            showSnackBar(parentLay, message)
        }
    }

    private fun setTitleTxt(sTitle: String) {
        titleTxt.text = sTitle
    }
}
