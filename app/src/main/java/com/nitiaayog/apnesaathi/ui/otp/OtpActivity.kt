package com.nitiaayog.apnesaathi.ui.otp

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
                if (editOtpfourthchar.length() == 0) {
                    editOtpthirdchar.requestFocus();
                } else {

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        btnVerify.throttleClick().subscribe() {
            hideKeyboard()
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
                            if (EditFirstChar.text.toString()
                                    .trim() + editOtpsecondchar.text.toString()
                                    .trim() + editOtpthirdchar.text.toString()
                                    .trim() + editOtpfourthchar.text.toString().trim() == "1122"
                            ) {
                                callnextActivity();
                            } else {
                                CallSnackbar(
                                    mainRootRelativeLayout,
                                    resources.getString(R.string.invalidOTP)
                                )
                            }
                        }
                    }
                }
            }
        }.autoDispose(disposables)
        txttimer.throttleClick().subscribe() {
            Toast.makeText(applicationContext, "Coming soon", Toast.LENGTH_LONG).show()
        }.autoDispose(disposables)

        val timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                txttimer.setText(resources.getString(R.string.otpverification) + millisUntilFinished / 1000)
            }

            override fun onFinish() {
                txttimer.text = resources.getString(R.string.resendOTP)
                btnVerify.setBackgroundColor(resources.getColor(R.color.color_dark_grey_txt))
                btnVerify.isClickable = false
            }
        }
        timer.start()
        TxtChangeNumber.paintFlags=Paint.UNDERLINE_TEXT_FLAG

        if (!intent.getStringExtra("PhoneNo").isNullOrEmpty()) {
            TxtMobileNumber.setText(intent.getStringExtra("PhoneNo"))

        } else {
            TxtMobileNumber.setText("1234")
        }
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

    fun AppCompatActivity.hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }
}