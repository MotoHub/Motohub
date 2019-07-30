package online.motohub.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main_home.*
import kotlinx.android.synthetic.main.widget_toolbar.*
import online.motohub.R
import online.motohub.constants.BundleConstants
import online.motohub.fragment.*
import online.motohub.viewmodel.BaseViewModelFactory
import online.motohub.viewmodel.HomeViewModel


class MainHomeActivity : BaseActivity() {

    var model: HomeViewModel? = null
    var navigationID: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_home)
        initView()
    }

    private fun initView() {
        val bundle = intent.extras
        navigationID = bundle.getInt("ID")

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


    private fun navigationAction(itemId: Int) {
        var bundle = Bundle()
        when (itemId) {
            R.id.eventVideosView -> {
                setTitleTxt(getString(R.string.event_videos))
                replaceFragment(EventVideosFragment(), bundle)
            }
            R.id.onDemandView -> {
                setTitleTxt(getString(R.string.on_demand))
                replaceFragment(OnDemandFragment(), bundle)
            }
            R.id.newsFeedView -> {
                setTitleTxt(getString(R.string.news_feed))
                bundle.putString(BundleConstants.MY_PROFILE_OBJ,Gson().toJson(model!!.profileObj))
                replaceFragment(NewsFeedFragment(), bundle)
            }
            R.id.findEventView -> {
                setTitleTxt(getString(R.string.find_event))
                replaceFragment(FindEventFragment(), bundle)
            }
            R.id.myProfileView -> {
                setTitleTxt(getString(R.string.profile_my))
                replaceFragment(MyProfileFragment(), bundle)
            }

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
        toolbar_title.text = sTitle
    }

    /**
     * If needed remove private call from fragment and change the selection
     */
    private fun changeFooterMenu(id: Int) {
        navigationView.selectedItemId = id
    }

}

