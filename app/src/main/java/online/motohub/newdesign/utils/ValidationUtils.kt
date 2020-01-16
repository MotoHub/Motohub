package online.motohub.newdesign.utils

import android.text.TextUtils
import android.util.Patterns

class ValidationUtils {
    companion object {
        fun isValidEmail(email: String?): Boolean {
            return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())
        }

        fun isValidPassword(pwd: String?): Boolean {
            return (!isValidString(pwd) && pwd!!.length < 6)
        }

        fun isValidString(str: String?): Boolean {
            return (!TextUtils.isEmpty(str))
        }

    }
}