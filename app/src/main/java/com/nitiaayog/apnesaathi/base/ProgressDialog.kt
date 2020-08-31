package com.nitiaayog.apnesaathi.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.StringRes
import com.nitiaayog.apnesaathi.ApneSaathiApplication
import com.nitiaayog.apnesaathi.R
import kotlinx.android.synthetic.main.progress_dialog_layout.*

/**
 * Class for showing progress dialogues throughout the app.
 * [Dialog] is the default class from android library.
 */
class ProgressDialog private constructor(context: Context) : Dialog(context) {

    interface DialogEventListener {
        fun onPositiveClick() {}
        fun onCancelClick() {}
        fun onDialogDismiss() {}
    }

    internal class Builder(private val context: Context) {

        private var isCancellable: Boolean = true

        private var title: String = ""
        private var message: String = context.getString(R.string.app_name)

        private var positiveTitle: String = context.getString(android.R.string.ok)
        private var cancelText: String = context.getString(android.R.string.cancel)

        private var onPositiveClick: DialogEventListener? = null
        private var onCancelClick: DialogEventListener? = null
        private var onDialogDismiss: DialogEventListener? = null

        private val dialog by lazy { ProgressDialog(context) }

        fun setCancellable(isCancellable: Boolean): Builder {
            this.isCancellable = isCancellable
            return this
        }

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }

        fun setTitle(@StringRes titleId: Int): Builder {
            this.title = context.getString(titleId)
            return this
        }

        fun setMessage(@StringRes messageId: Int): Builder {
            this.message = context.getString(messageId)
            return this
        }

        fun setPositiveText(title: String): Builder {
            positiveTitle = title
            return this
        }

        fun addNegativeText(title: String): Builder {
            cancelText = title
            return this
        }

        fun addPositiveButton(title: String, onPositiveClick: DialogEventListener): Builder {
            positiveTitle = title
            this.onPositiveClick = onPositiveClick
            return this
        }

        fun addPositiveButton(
            @StringRes titleId: Int, onPositiveClickListener: DialogEventListener
        ): Builder {
            positiveTitle = context.getString(titleId)
            this.onPositiveClick = onPositiveClickListener
            return this
        }

        fun addCancelButton(title: String, onCancelClick: DialogEventListener): Builder {
            cancelText = title
            this.onCancelClick = onCancelClick
            return this
        }

        fun addCancelButton(@StringRes cancelTextId: Int, onCancelClick: DialogEventListener):
                Builder {
            cancelText = context.getString(cancelTextId)
            this.onCancelClick = onCancelClick
            return this
        }

        fun addOnDialogDismissListener(onDialogDismiss: DialogEventListener): Builder {
            this.onDialogDismiss = onDialogDismiss
            return this
        }

        fun show() = create().show()

        fun dismiss() = dialog.dismiss()

        private fun create(): ProgressDialog {
            dialog.setCancelable(isCancellable)

            dialog.setTitle(title)
            dialog.setMessage(message)

            dialog.setPositiveText(positiveTitle)
            onPositiveClick?.run { dialog.addPositiveButton(positiveTitle, this) }

            dialog.setCancelText(cancelText)
            onCancelClick?.run { dialog.addCancelButton(cancelText, this) }

            onDialogDismiss?.run { dialog.addOnDialogDismissListener(this) }

            return dialog
        }
    }

    private val screenSize: IntArray = intArrayOf(0, 0)

    private var title: String = ""
    private var message: String = ""

    private var positiveTitle: String = context.getString(android.R.string.ok)
    private var cancelText: String = context.getString(android.R.string.cancel)

    private var onPositiveClick: DialogEventListener? = null
    private var onCancelClick: DialogEventListener? = null
    private var onDialogDismiss: DialogEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.progress_dialog_layout)

        window?.apply {
            this.setLayout(
                (ApneSaathiApplication.screenSize[0] * 0.95).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }

        if (title.isNotEmpty()) {
            tvDialogTitle.text = title
            tvDialogTitle.visibility = View.VISIBLE
        }

        tvDialogMessage.text = message
        tvDialogMessage.isSelected = true

        onPositiveClick?.run {
            btnDialogAction.text = positiveTitle
            btnDialogAction.setOnClickListener { this.onPositiveClick() }
        }

        onCancelClick?.run {
            btnDialogCancel.text = cancelText
            btnDialogCancel.setOnClickListener { this.onCancelClick() }
        }
    }

    override fun dismiss() {
        onDialogDismiss?.run { this.onDialogDismiss() }
        super.dismiss()
    }

    private fun setTitle(title: String): ProgressDialog {
        this.title = title
        return this
    }

    private fun setMessage(message: String): ProgressDialog {
        this.message = message
        return this
    }

    private fun setPositiveText(title: String): ProgressDialog {
        this.positiveTitle = title
        return this
    }

    private fun setCancelText(title: String): ProgressDialog {
        this.cancelText = title
        return this
    }

    private fun addPositiveButton(title: String, onPositiveClick: DialogEventListener):
            ProgressDialog {
        this.positiveTitle = title
        this.onPositiveClick = onPositiveClick
        return this
    }

    private fun addCancelButton(title: String, onCancelClick: DialogEventListener):
            ProgressDialog {
        this.cancelText = title
        this.onCancelClick = onCancelClick
        return this
    }

    private fun addOnDialogDismissListener(onDialogDismiss: DialogEventListener): ProgressDialog {
        this.onDialogDismiss = onDialogDismiss
        return this
    }
}