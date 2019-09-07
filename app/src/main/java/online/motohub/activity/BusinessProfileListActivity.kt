package online.motohub.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_business_profile_list.*
import kotlinx.android.synthetic.main.common_header.*
import online.motohub.R
import online.motohub.activity.business.BusinessProfileActivity
import online.motohub.adapter.BusinessProfileListAdapter
import online.motohub.constants.BundleConstants
import online.motohub.interfaces.AdapterClickCallBack
import online.motohub.model.promoter_club_news_media.PromotersResModel
import online.motohub.tags.AdapterTag
import online.motohub.viewmodel.BaseViewModelFactory
import online.motohub.viewmodel.BusinessProfileListViewModel
import org.greenrobot.eventbus.EventBus
import java.util.*

class BusinessProfileListActivity : BaseActivity(), View.OnClickListener, AdapterClickCallBack {


    private var model: BusinessProfileListViewModel? = null
    private var businessProfileList = ArrayList<PromotersResModel>()
    private var businessProfileListAdapter: BusinessProfileListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_profile_list)
        initView()
    }

    private fun initView() {
        val activeBundle = intent.extras
        val businessProfileType = activeBundle!!.getString(BundleConstants.BUSINESS_PROFILE_TYPE)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        listView.layoutManager = layoutManager
        setTitleTxt(businessProfileType!!)

        model = ViewModelProviders.of(this, BaseViewModelFactory(activity!!.application, activeBundle)).get(BusinessProfileListViewModel::class.java)
        registerModel(model)
        model!!.businessProfileListLiveData.observe(this, Observer {
            if (it != null)
                businessProfileList.addAll(it)
            setAdapter()
        })
        model!!.initialize()
    }

    private fun setAdapter() {
        if (businessProfileListAdapter == null) {
            businessProfileListAdapter = BusinessProfileListAdapter(activity, businessProfileList, this)
            listView.adapter = businessProfileListAdapter
        } else {
            businessProfileListAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.backIcon -> {
                finish()
            }
        }
    }

    override fun onClick(view: View?, tag: AdapterTag?) {
        val pos = tag!!.pos
        EventBus.getDefault().postSticky(model!!.profileObj)
        EventBus.getDefault().postSticky(pos)
        startActivity(Intent(activity, BusinessProfileActivity::class.java))
    }

    private fun setTitleTxt(sTitle: String) {
        titleTxt.text = sTitle
    }

}
