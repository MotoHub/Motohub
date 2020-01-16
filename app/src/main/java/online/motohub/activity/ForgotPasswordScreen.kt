package online.motohub.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.acitivity_forgot_pwd_screen.*
import kotlinx.android.synthetic.main.widget_toolbar.*
import online.motohub.R
import online.motohub.newdesign.utils.ValidationUtils


class ForgotPasswordScreen : BaseActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_forgot_pwd_screen)
        ButterKnife.bind(this)
        initView()
    }

    private fun initView() {
        setToolbar(toolbar, getString(R.string.forgot_pwd))
        showToolbarBtn(toolbar, R.id.toolbar_back_img_btn)
        setupUI(patentLay)
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.toolbar_back_img_btn -> onBackPressed()
            R.id.sendCodeBtn -> validateFields()
            R.id.alreadyHaveCodeBtn -> {
                startActivity(Intent(this, PasswordResetScreen::class.java))
                finish()
            }
        }
    }

    private fun validateFields() {
        val email = emailEdt.text.toString().trim()
        if (!ValidationUtils.isValidEmail(email)) {
            showToast(this, getString(R.string.valid_email_msg))
        } else {
            //TODO call API

        }
    }

    override fun onBackPressed() {
        finish()
    }
}
