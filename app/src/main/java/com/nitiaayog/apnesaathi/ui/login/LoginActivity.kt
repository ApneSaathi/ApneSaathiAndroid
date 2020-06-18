package com.nitiaayog.apnesaathi.ui.login

import android.os.Bundle
import android.text.TextUtils
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.CallSnackbar
import com.nitiaayog.apnesaathi.base.extensions.getTargetIntent
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.otp.OtpActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity<LoginViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        btnLogin.throttleClick().subscribe() {
            if (TextUtils.isEmpty(EditMobileNumber.text.toString().trim())) {
                CallSnackbar(rootRelativeLayout, resources.getString(R.string.txtenterMobilenumbe))
            } else {
                EditMobileNumber.setError(null)
                if (EditMobileNumber.text.toString().trim().length < 10) {
                    CallSnackbar(
                        rootRelativeLayout,
                        resources.getString(R.string.txtValidmobilenumber))

                } else {
                    val targetIntent = getTargetIntent(OtpActivity::class.java)
                    startActivity(targetIntent)
                }
            }
        }.autoDispose(disposables)
    }
    override fun provideViewModel(): LoginViewModel = getViewModel {
        LoginViewModel.getInstance(dataManager)
    }
    override fun provideLayoutResource(): Int = R.layout.activity_login
}