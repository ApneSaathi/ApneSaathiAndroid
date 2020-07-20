package com.nitiaayog.apnesaathi.ui.otp

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.nitiaayog.apnesaathi.R
import kotlinx.android.synthetic.main.activity_login_otpverify.view.*

class OtpTextWatcher internal constructor(view:View): TextWatcher {
    private val view: View
    init{
        this.view = view
    }
    override fun beforeTextChanged(charSequence:CharSequence, i:Int, i1:Int, i2:Int) {
        val text = charSequence.toString()
        when (view.getId()) {
            R.id.edOtp1 -> {
                if (text.length == 1)
                    view.edOtp2.requestFocus()
                view.edOtp2?.text?.length?.let { view.edOtp2.setSelection(it) }
                run { view.edOtp1.requestFocus() }
            }
            R.id.edOtp2 -> if (text.isEmpty())
            {
                view.edOtp1.requestFocus()
                view.edOtp1.text?.length?.let { view.edOtp1.setSelection(it) }
            }
            R.id.edOtp3 -> if (text.isEmpty())
            {
                view.edOtp2.requestFocus()
                view.edOtp2.text?.length?.let { view.edOtp2.setSelection(it) }
            }
            R.id.edOtp4 -> if (text.isEmpty())
            {
                view.edOtp3.requestFocus()
                view.edOtp3.text?.length?.let { view.edOtp3.setSelection(it) }
            }
            else -> {}
        }
    }
    override fun onTextChanged(charSequence:CharSequence, i:Int, i1:Int, i2:Int) {}
    override fun afterTextChanged(editable: Editable) {
        val text = editable.toString()
        when (view.getId()) {
            R.id.edOtp1 -> if (text.length == 1)
                view.edOtp2.requestFocus()
            else
                view.edOtp1.text?.length?.let { view.edOtp1.setSelection(it) }
            R.id.edOtp2 -> if (text.length == 1)
                view.edOtp3.requestFocus()
            else if (text.isEmpty())
            {
                view.edOtp1.requestFocus()
                view.edOtp1.getText()?.length?.let { view.edOtp1.setSelection(it) }
            }
            R.id.edOtp3 -> if (text.length == 1)
                view.edOtp4.requestFocus()
            else if (text.isEmpty())
            {
                view.edOtp2.requestFocus()
                view.edOtp2.text?.length?.let { view.edOtp2.setSelection(it) }
            }
            R.id.edOtp4 -> if (text.isEmpty())
            {
                view.edOtp3.requestFocus()
                view.edOtp3.text?.length?.let { view.edOtp3.setSelection(it) }
            }
            else -> {}
        }
    }
}