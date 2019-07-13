package online.motohub.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main_home.*
import kotlinx.android.synthetic.main.widget_toolbar.*
import online.motohub.R


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
    }

    private fun navigationAction(itemId: Int) {
        when (itemId) {
            R.id.newsFeedView -> {
                setTitleTxt(getString(R.string.news_feed))
            }
            R.id.onDemandView -> {
                setTitleTxt(getString(R.string.on_demand))
            }
            R.id.eventVideosView -> {
                setTitleTxt(getString(R.string.event_videos))
            }
            R.id.findEventView -> {
                setTitleTxt(getString(R.string.find_event))
            }
            R.id.myProfileView -> {
                setTitleTxt(getString(R.string.profile_my))
            }
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

