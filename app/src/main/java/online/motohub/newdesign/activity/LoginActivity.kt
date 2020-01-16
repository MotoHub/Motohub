package online.motohub.newdesign.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_login.*
import online.motohub.R
import online.motohub.activity.BaseActivity
import online.motohub.activity.ForgotPasswordScreen
import online.motohub.newdesign.constants.APIConstants
import online.motohub.newdesign.utils.ValidationUtils
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
                validateFields()
            }
            R.id.fbLoginBtn -> {
               showToast(this,"Under Development")
            }
            R.id.registerBtn -> {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
            R.id.forgotPwdBtn -> startActivity(Intent(this, ForgotPasswordScreen::class.java))
        }
    }

    private fun validateFields(){
        val email = emailEdt.text.toString().trim()
        val pwd = pwdEdt.text.toString().trim()
        if (!ValidationUtils.isValidEmail(email) && !ValidationUtils.isValidPassword(pwd)) {
            showToast(this, getString(R.string.valid_all_fields_msg))
        }else  if(!ValidationUtils.isValidEmail(email)){
            showToast(this,getString(R.string.valid_email_msg))
        }else if(!ValidationUtils.isValidPassword(pwd)){
            showToast(this,getString(R.string.valid_pwd_msg))
        }else if (checkboxTC.isChecked){
            showToast(this,getString(R.string.valid_terms_and_con_msg))
        }else{
            model!!.callLogin(email,pwd, APIConstants.TYPE_EMAIL)
        }

    }
}