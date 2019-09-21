package online.motohub.newdesign.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import com.google.gson.Gson
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment
import com.yalantis.contextmenu.lib.MenuParams
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener
import kotlinx.android.synthetic.main.activity_main_home.*
import kotlinx.android.synthetic.main.main_home_header.*
import online.motohub.R
import online.motohub.activity.*
import online.motohub.newdesign.bl.MotoHubApp
import online.motohub.newdesign.constants.AppConstants
import online.motohub.newdesign.constants.BundleConstants
import online.motohub.fragment.dialog.AppDialogFragment
import online.motohub.model.PushTokenModel
import online.motohub.newdesign.fragment.*
import online.motohub.retrofit.RetrofitClient
import online.motohub.util.PreferenceUtils
import online.motohub.newdesign.viewmodel.BaseViewModelFactory
import online.motohub.newdesign.viewmodel.HomeViewModel
import org.greenrobot.eventbus.EventBus
import java.util.*


class MainHomeActivity : BaseActivity(), View.OnClickListener, OnMenuItemClickListener {


    var model: HomeViewModel? = null
    var navigationID: Int = 0
    private var fragmentManager: FragmentManager? = null
    private var settingsMenuFragment: ContextMenuDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_home)
        initView()
    }

    private fun initView() {
        initMenuFragment()
        val bundle = intent.extras
        navigationID = bundle!!.getInt(BundleConstants.FOOTER_NAVIGATION_ID)

        navigationView.setOnNavigationItemSelectedListener { item ->
            navigationAction(item.itemId)
            return@setOnNavigationItemSelectedListener true
        }
        navigationView.setOnNavigationItemReselectedListener {
            //TODO do nothing if needed will use in future
        }

        model = ViewModelProviders.of(this, BaseViewModelFactory(activity!!.application, bundle)).get(HomeViewModel::class.java)
        registerModel(model)
        model!!.profileListLiveData.observe(this, Observer {
            if (it != null) {
                navigationAction(navigationID)
                changeFooterMenu(navigationID)
            }
        })
        model!!.initialize()
    }

    private fun initMenuFragment() {
        fragmentManager = supportFragmentManager
        try {
            val menuParams = MenuParams()
            menuParams.actionBarSize = resources.getDimension(R.dimen.tool_bar_height).toInt()
            menuParams.menuObjects = menuObjects
            menuParams.isClosableOutside = true
            menuParams.animationDuration = 100
            settingsMenuFragment = ContextMenuDialogFragment.newInstance(menuParams)
            settingsMenuFragment!!.setItemClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onMenuItemClick(v: View?, pos: Int) {
        try {
            when (pos) {
                1 -> {
                    val intent = Intent(this, CreateProfileActivity::class.java)
                    intent.putExtra(CREATE_PROF_AFTER_REG, true)
                    intent.putExtra(AppConstants.TAG, componentName)
                    startActivityForResult(intent, AppConstants.CREATE_PROFILE_RES)
                }
                2 -> {
                    showAppDialog(AppDialogFragment.ALERT_OTHER_PROFILE_DIALOG, model!!.profileSpinnerList)
                }
                3 -> {
                    startActivity(Intent(this, NotificationSettingsActivity::class.java))
                }
                4 -> {
                    if (model!!.profileObj!!.blockedUserProfilesByProfileID.size > 0) {
                        EventBus.getDefault().postSticky(model!!.profileObj)
                        startActivityForResult(Intent(this, BlockedUsersActivity::class.java),
                                AppConstants.FOLLOWERS_FOLLOWING_RESULT)
                    } else {
                        showMessage(getString(R.string.no_blocked_users_found_err))
                    }
                }
                5 -> {
                    startActivity(Intent(this, PaymentActivity::class.java).putExtra(AppConstants.CARD_SETTINGS, false))
                }
                6 -> {
                    showAppDialog(AppDialogFragment.LOG_OUT_DIALOG, null)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun alertDialogPositiveBtnClick(activity: BaseActivity?, dialogType: String?, profileTypesStr: StringBuilder?, profileTypes: ArrayList<String>?, position: Int) {
        super.alertDialogPositiveBtnClick(activity, dialogType, profileTypesStr, profileTypes, position)

        when (dialogType) {
            AppDialogFragment.ALERT_OTHER_PROFILE_DIALOG -> {
                model!!.changeProfile(position)
            }
            AppDialogFragment.LOG_OUT_DIALOG -> {
                logout()
            }
        }

    }

    private fun logout() {
        val mUserID = PreferenceUtils.getInstance(this).getIntData(PreferenceUtils.USER_ID)
        val mFilter = "UserID=$mUserID"
        RetrofitClient.getRetrofitInstance().callDeletePushToken(this, mFilter, RetrofitClient.LOGOUT)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.backIcon -> {
                finish()
            }
            R.id.notificationIcon -> {
                EventBus.getDefault().postSticky(model!!.profileObj)
                startActivity(Intent(this, NotificationActivity::class.java))
            }
            R.id.messageIcon -> {
                EventBus.getDefault().postSticky(model!!.profileObj)
                startActivity(Intent(this, ChatHomeActivity::class.java))
            }
            R.id.settingsIcon -> {
                if (!settingsMenuFragment!!.isVisible)
                    settingsMenuFragment!!.show(fragmentManager, ContextMenuDialogFragment.TAG)
            }
        }

    }

    private fun navigationAction(itemId: Int) {
        val bundle = Bundle()
        bundle.putString(BundleConstants.MY_PROFILE_OBJ, Gson().toJson(model!!.profileObj))
        when (itemId) {
            R.id.eventVideosView -> {
                navigationID = R.id.eventVideosView
                setTitleTxt(getString(R.string.event_videos))
                replaceFragment(EventVideosFragment(), bundle)
            }
            R.id.onDemandView -> {
                navigationID = R.id.onDemandView
                setTitleTxt(getString(R.string.on_demand))
                replaceFragment(OnDemandFragment(), bundle)
            }
            R.id.newsFeedView -> {
                navigationID = R.id.newsFeedView
                setTitleTxt(getString(R.string.news_feed))
                bundle.putString(BundleConstants.MY_PROFILE_OBJ, Gson().toJson(model!!.profileObj))
                replaceFragment(NewsFeedFragment(), bundle)
            }
            R.id.findEventView -> {
                navigationID = R.id.findEventView
                setTitleTxt(getString(R.string.find_event))
                replaceFragment(FindEventFragment(), bundle)

            }
            R.id.myProfileView -> {
                navigationID = R.id.myProfileView
                setTitleTxt(getString(R.string.profile_my))
                replaceFragment(MyProfileFragment(), bundle)
            }

        }
    }

    override fun retrofitOnResponse(responseObj: Any?, responseType: Int) {
        super.retrofitOnResponse(responseObj, responseType)
        if (responseObj is PushTokenModel) run {
            clearBeforeLogout()
            //TODO Clear File Cache
            MotoHubApp.getInstance().onUserLogout()
            val loginActivity = Intent(this, LoginActivity::class.java)
            loginActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(loginActivity)
            finish()
        }
    }

    fun addFragment(mFragment: Fragment?) {
        if (mFragment != null) {
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().addToBackStack(mFragment.javaClass.simpleName).add(R.id.contentFrame, mFragment)
                    .commit()
        } else {
            sysOut("Error in creating fragment")
        }
    }

    private fun replaceFragment(fragment: Fragment?, bundle: Bundle) {
        if (fragment != null) {
            fragment.arguments = bundle
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.contentFrame, fragment)
                    .commit()
        } else {
            sysOut("Error in creating fragment")
        }
    }

    private fun setTitleTxt(sTitle: String) {
        titleTxt.text = sTitle
    }

    /**
     * If needed remove private call from fragment and change the selection
     */
    private fun changeFooterMenu(id: Int) {
        navigationView.selectedItemId = id
    }

}

