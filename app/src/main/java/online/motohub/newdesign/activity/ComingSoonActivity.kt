package online.motohub.newdesign.activity

import android.os.Bundle
import android.view.View
import online.motohub.R
import online.motohub.activity.BaseActivity

class ComingSoonActivity : BaseActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comming_soon)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.backIcon -> {
                finish()
            }
        }
    }

}
