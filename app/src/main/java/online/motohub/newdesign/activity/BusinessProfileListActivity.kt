package online.motohub.newdesign.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_business_profile_list.*
import kotlinx.android.synthetic.main.common_header.*
import online.motohub.R
import online.motohub.activity.BaseActivity
import online.motohub.newdesign.adapter.BusinessProfileListAdapter
import online.motohub.newdesign.constants.BundleConstants
import online.motohub.interfaces.AdapterClickCallBack
import online.motohub.model.promoter_club_news_media.PromotersResModel
import online.motohub.tags.AdapterTag
import online.motohub.newdesign.viewmodel.BaseViewModelFactory
import online.motohub.newdesign.viewmodel.BusinessProfileListViewModel
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
        setupUI(parentLay)
        val activeBundle = intent.extras
        val businessProfileType = activeBundle!!.getString(BundleConstants.BUSINESS_PROFILE_TYPE)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        listView.layoutManager = layoutManager
        setTitleTxt(businessProfileType!!)

        model = ViewModelProviders.of(this, BaseViewModelFactory(activity!!.application, activeBundle)).get(BusinessProfileListViewModel::class.java)
        registerModel(model)
        model!!.businessProfileListLiveData.observe(this, Observer {
            businessProfileList.clear()
            if (it != null)
                businessProfileList.addAll(it)
            setAdapter()
        })
        model!!.initialize()
        searchEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(c: CharSequence, i: Int, i1: Int, i2: Int) {
                model!!.searchProfile(c.toString())
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
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
        val bundle = Bundle()
        bundle.putString(BundleConstants.MY_PROFILE_OBJ, Gson().toJson(model!!.profileObj))
        bundle.putString(BundleConstants.BUSINESS_PROFILE_OBJ, Gson().toJson(businessProfileList[pos]))
        startActivity(Intent(activity, BusinessProfileActivity::class.java).putExtras(bundle))
    }

    private fun setTitleTxt(sTitle: String) {
        titleTxt.text = sTitle
    }

}
