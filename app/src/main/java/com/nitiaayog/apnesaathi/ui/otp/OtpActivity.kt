package com.nitiaayog.apnesaathi.ui.otp

import android.os.Bundle
import android.text.TextUtils
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getTargetIntent
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.dashboard.DashBoardActivity
import com.nitiaayog.apnesaathi.ui.login.LoginViewModel
import kotlinx.android.synthetic.main.activity_login_otpverify.*

class OtpActivity : BaseActivity<OtpActivityModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Btnverify.setOnClickListener {

            if (TextUtils.isEmpty(EditFirstChar.text.toString().trim())) {
                EditFirstChar.setError("Enter Otp")
            } else {
                EditFirstChar.setError(null)
                val targetIntent = getTargetIntent(DashBoardActivity::class.java)
                startActivity(targetIntent)
            }
        }
    }

    override fun provideViewModel(): OtpActivityModel = getViewModel {
        OtpActivityModel.getInstance(dataManager)
    }

    override fun provideLayoutResource(): Int = R.layout.activity_login_otpverify
}