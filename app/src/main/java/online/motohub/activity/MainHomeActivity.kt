package online.motohub.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import kotlinx.android.synthetic.main.activity_main_home.*
import kotlinx.android.synthetic.main.widget_toolbar.*
import online.motohub.R
import online.motohub.fragment.*


class MainHomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_home)
        initView()
    }

    private fun initView() {
        val bundle = intent.extras
        val navigationID = bundle.getInt("ID")
        navigationAction(navigationID)
        changeFooterMenu(navigationID)
        navigationView.setOnNavigationItemSelectedListener { item ->
            navigationAction(item.itemId)
            return@setOnNavigationItemSelectedListener true
        }
        navigationView.setOnNavigationItemReselectedListener { item ->
            //TODO do nothing if needed will use in future
        }
    }

    private fun navigationAction(itemId: Int) {
        when (itemId) {
            R.id.newsFeedView -> {
                setTitleTxt(getString(R.string.news_feed))
                replaceFragment(NewsFeedFragment())
            }
            R.id.onDemandView -> {
                setTitleTxt(getString(R.string.on_demand))
                replaceFragment(OnDemandFragment())
            }
            R.id.eventVideosView -> {
                setTitleTxt(getString(R.string.event_videos))
                replaceFragment(EventVideosFragment())
            }
            R.id.findEventView -> {
                setTitleTxt(getString(R.string.find_event))
                replaceFragment(FindEventFragment())
            }
            R.id.myProfileView -> {
                setTitleTxt(getString(R.string.profile_my))
                replaceFragment(MyProfileFragment())
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

    private fun replaceFragment(mFragment: Fragment?) {
        if (mFragment != null) {
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.contentFrame, mFragment)
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

