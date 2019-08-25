package online.motohub.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.common_header.*

import online.motohub.R
import online.motohub.constants.BundleConstants

class BusinessProfileListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_profile_list)
        initView()
    }

    private fun initView() {
        val bundle = intent.extras
        val businessProfileType = bundle!!.getString(BundleConstants.BUSINESS_PROFILE_TYPE)

        setTitleTxt(businessProfileType!!)
    }

    private fun setTitleTxt(sTitle: String) {
        titleTxt.text = sTitle
    }

}
