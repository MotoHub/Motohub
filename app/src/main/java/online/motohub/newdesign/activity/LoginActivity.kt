package online.motohub.newdesign.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import online.motohub.R
import online.motohub.activity.BaseActivity
import online.motohub.activity.ForgotPasswordScreen
import online.motohub.newdesign.viewmodel.BaseViewModelFactory
import online.motohub.newdesign.viewmodel.ProfileViewModel

class LoginActivity : BaseActivity() {

    var model: ProfileViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
    }

    private fun initView() {
        val bundle = intent.extras

        model = ViewModelProviders.of(this, BaseViewModelFactory(activity!!.application, bundle)).get(ProfileViewModel::class.java)
        registerModel(model)
    }
    fun onClick(v: View) {
        when (v.id) {
            R.id.loginBtn -> {
            }
            R.id.fbLoginBtn -> {
            }
            R.id.registerBtn -> {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
            R.id.forgotPwdBtn -> startActivity(Intent(this, ForgotPasswordScreen::class.java))
        }
    }
}