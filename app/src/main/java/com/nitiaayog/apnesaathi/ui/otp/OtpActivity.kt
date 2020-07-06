package com.nitiaayog.apnesaathi.ui.otp

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.CallSnackbar
import com.nitiaayog.apnesaathi.base.extensions.getTargetIntent
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.dashboard.DashBoardActivity
import kotlinx.android.synthetic.main.activity_login_otpverify.*


class OtpActivity : BaseActivity<OtpActivityModel>() {
       override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EditFirstChar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (EditFirstChar.length() == 1) {
                    editOtpsecondchar.requestFocus();
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        editOtpsecondchar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (editOtpsecondchar.length() == 1) {
                    editOtpthirdchar.requestFocus();
                } else {
                    EditFirstChar.requestFocus();
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        editOtpthirdchar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (editOtpthirdchar.length() == 1) {
                    editOtpfourthchar.requestFocus();
                } else {
                    editOtpsecondchar.requestFocus();
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        editOtpfourthchar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (editOtpthirdchar.length() == 1) {

                } else {
                    editOtpthirdchar.requestFocus();
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        btnVerify.throttleClick().subscribe() {
            if (TextUtils.isEmpty(EditFirstChar.text.toString().trim())) {
                CallSnackbar(mainRootRelativeLayout, resources.getString(R.string.enterOtp))
            } else {
                EditFirstChar.setError(null)
                if (TextUtils.isEmpty(editOtpsecondchar.text.toString().trim())) {
                    CallSnackbar(mainRootRelativeLayout, resources.getString(R.string.enterOtp))
                } else {
                    editOtpsecondchar.setError(null)
                    if (TextUtils.isEmpty(editOtpthirdchar.text.toString().trim())) {
                        CallSnackbar(mainRootRelativeLayout, resources.getString(R.string.enterOtp))
                    } else {
                        editOtpfourthchar.setError(null)
                        if (TextUtils.isEmpty(editOtpfourthchar.text.toString().trim())) {
                            CallSnackbar(
                                mainRootRelativeLayout,
                                resources.getString(R.string.enterOtp)
                            )
                        } else {
                            editOtpfourthchar.setError(null)

                            callnextActivity();
                        }
                    }
                }
            }
        }.autoDispose(disposables)
    }

    private fun callnextActivity() {

        val targetIntent = getTargetIntent(DashBoardActivity::class.java)
        startActivity(targetIntent)
        dataManager.setPhoneNumber(intent.getStringExtra("PhoneNo"))
        finishAffinity()

    }

    override fun provideViewModel(): OtpActivityModel = getViewModel {
        OtpActivityModel.getInstance(dataManager)
    }

    override fun provideLayoutResource(): Int = R.layout.activity_login_otpverify
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}