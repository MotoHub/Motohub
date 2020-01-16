package online.motohub.newdesign.activity

import android.os.Bundle
import android.view.View
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.widget_toolbar.*
import online.motohub.R
import online.motohub.activity.BaseActivity
import online.motohub.newdesign.utils.ValidationUtils

class SignUpActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        ButterKnife.bind(this)
        initView()
    }

    private fun initView() {
        setupUI(parentLay)
        setToolbar(toolbar, getString(R.string.register))
        setToolbarLeftBtn(toolbar)
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.toolbar_back_img_btn -> finish()
            R.id.submitBtn -> validateFields()
        }
    }

    private fun validateFields() {
        val firstName = firstNameEdt.text.toString().trim()
        val lastName = lastNameEdt.text.toString().trim()
        val email = emailEdt.text.toString().trim()
        val pwd = pwdEdt.text.toString().trim()
        val confPwd = confPwdEdt.text.toString().trim()
        //val phoneNumber = phoneNumberEdt.getText().toString().trim();
        if (!ValidationUtils.isValidString(firstName) && !ValidationUtils.isValidString(lastName) && !ValidationUtils.isValidEmail(email) &&
                !ValidationUtils.isValidPassword(pwd) && !ValidationUtils.isValidPassword(confPwd)) {
            showToast(this, getString(R.string.valid_all_fields_msg))
        } else if (!ValidationUtils.isValidString(firstName)) {
            showToast(this, getString(R.string.valid_first_name_msg))
        } else if (!ValidationUtils.isValidString(lastName)) {
            showToast(this, getString(R.string.valid_last_name_msg))
        } else if (!ValidationUtils.isValidEmail(email)) {
            showToast(this, getString(R.string.valid_email_msg))
        } else if (!ValidationUtils.isValidPassword(pwd)) {
            showToast(this, getString(R.string.valid_pwd_msg))
        } else if (!ValidationUtils.isValidPassword(confPwd)) {
            showToast(this, getString(R.string.valid_conf_pwd_msg))
        } else if (pwd != confPwd) {
            showToast(this, getString(R.string.valid_mismatch_pwd_msg))
        } else {
            showToast(this, "Success")
            //TODO call Register
        }/*if (mPhoneNumber)) {
            showToast(this, getString(R.string.enter_phone_number));
            return;
        }*/

    }

}
