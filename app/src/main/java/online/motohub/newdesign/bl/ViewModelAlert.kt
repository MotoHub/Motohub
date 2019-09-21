package online.motohub.newdesign.bl

import android.content.Context
import android.text.TextUtils

import online.motohub.interfaces.AlertOnClickListener


class ViewModelAlert(var name: String) {
    private var titleId: Int = 0
    private var cancelable: Boolean = false
    private var title: CharSequence = ""
    private var messageId: Int = 0
    private var message: CharSequence = ""
    private var positiveTextId: Int = 0
    private var positiveListener: AlertOnClickListener? = null
    private var positiveMessage: CharSequence = ""
    private var negativeTextId: Int = 0
    private var negativeListener: AlertOnClickListener? = null
    private var negativeMessage: CharSequence = ""

    fun setTitle(titleId: Int): ViewModelAlert {
        this.titleId = titleId
        return this
    }

    fun setTitle(title: CharSequence): ViewModelAlert {
        this.title = title
        return this
    }


    fun setMessage(messageId: Int): ViewModelAlert {
        this.messageId = messageId
        return this
    }

    fun setMessage(message: CharSequence): ViewModelAlert {
        this.message = message
        return this
    }

    fun setPositiveButton(textId: Int, listener: AlertOnClickListener): ViewModelAlert {
        this.positiveTextId = textId
        this.positiveListener = listener
        return this
    }

    fun setPositiveButton(text: CharSequence, listener: AlertOnClickListener): ViewModelAlert {
        this.positiveMessage = text
        this.positiveListener = listener
        return this
    }

    fun setNegativeButton(textId: Int, listener: AlertOnClickListener): ViewModelAlert {
        this.negativeTextId = textId
        this.negativeListener = listener
        return this
    }

    fun setNegativeButton(text: CharSequence, listener: AlertOnClickListener): ViewModelAlert {
        this.negativeMessage = text
        this.negativeListener = listener
        return this
    }


    fun setCancelable(cancelable: Boolean): ViewModelAlert {
        this.cancelable = cancelable
        return this
    }

    fun getTitle(context: Context): String {
        if (!TextUtils.isEmpty(title)) {
            return title.toString()
        }
        return if (titleId != 0) {
            context.getString(titleId)
        } else ""
    }

    fun getContent(context: Context): String {
        if (!TextUtils.isEmpty(message)) {
            return message.toString()
        }
        return if (messageId != 0) {
            context.getString(messageId)
        } else ""
    }

    fun getLeftButtonText(context: Context): String {
        if (!TextUtils.isEmpty(negativeMessage)) {
            return negativeMessage.toString()
        }
        return if (negativeTextId != 0) {
            context.getString(negativeTextId)
        } else ""
    }

    fun getRightButtonText(context: Context): String {
        if (!TextUtils.isEmpty(positiveMessage)) {
            return positiveMessage.toString()
        }
        return if (positiveTextId != 0) {
            context.getString(positiveTextId)
        } else ""
    }
}


