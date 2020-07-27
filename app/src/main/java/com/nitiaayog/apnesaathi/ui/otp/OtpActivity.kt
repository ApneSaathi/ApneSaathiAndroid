package com.nitiaayog.apnesaathi.ui.otp

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.ProgressDialog
import com.nitiaayog.apnesaathi.base.extensions.CallSnackbar
import com.nitiaayog.apnesaathi.base.extensions.getTargetIntent
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.dashboard.DashBoardActivity
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_login_otpverify.*
import java.util.concurrent.TimeUnit


class OtpActivity : BaseActivity<OtpActivityModel>() {
    var wrongAttempCount: Int = 0
    lateinit var mContext: Context
    val otpEt = arrayOfNulls<EditText>(4)

    private val progressDialog: ProgressDialog.Builder by lazy {
        ProgressDialog.Builder(mContext!!).setMessage("Please wait.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = this

        otpEt[0] = findViewById(R.id.edOtp1);
        otpEt[1] = findViewById(R.id.edOtp2);
        otpEt[2] = findViewById(R.id.edOtp3);
        otpEt[3] = findViewById(R.id.edOtp4);
        setOtpEditTextHandler()

        observeStates()




        btnVerify.throttleClick().subscribe() {
            hideKeyboard()
            if (btnVerify.text.toString() == resources.getString(R.string.getnewOTP)) {
                onBackPressed()
            } else {
                if (TextUtils.isEmpty(edOtp1.text.toString().trim())) {
                    CallSnackbar(mainRootRelativeLayout, resources.getString(R.string.enterOtp))
                } else {
                    edOtp1.setError(null)
                    if (TextUtils.isEmpty(edOtp2.text.toString().trim())) {
                        CallSnackbar(mainRootRelativeLayout, resources.getString(R.string.enterOtp))
                    } else {
                        edOtp2.setError(null)
                        if (TextUtils.isEmpty(edOtp3.text.toString().trim())) {
                            CallSnackbar(
                                mainRootRelativeLayout,
                                resources.getString(R.string.enterOtp)
                            )

                        } else {
                            edOtp4.setError(null)
                            if (TextUtils.isEmpty(edOtp4.text.toString().trim())) {
                                CallSnackbar(
                                    mainRootRelativeLayout,
                                    resources.getString(R.string.enterOtp)
                                )
                            } else {
                                edOtp4.setError(null)
                                if (edOtp1.text.toString()
                                        .trim() + edOtp2.text.toString()
                                        .trim() + edOtp3.text.toString()
                                        .trim() + edOtp4.text.toString().trim() == "1122"
                                ) {
                                    if (wrongAttempCount <= 3) {
                                        callnextActivity();
                                    } else {
                                        CallSnackbar(
                                            mainRootRelativeLayout,
                                            resources.getString(R.string.limitecros)
                                        )
                                    }
                                } else {
                                    if (wrongAttempCount >= 3) {
                                        CallSnackbar(
                                            mainRootRelativeLayout,
                                            resources.getString(R.string.limitecros)
                                        )
                                        btnVerify.text = resources.getString(R.string.getnewOTP)
                                        btnVerify.setBackgroundColor(resources.getColor(R.color.color_dark_grey_txt))
                                    } else {
                                        CallSnackbar(
                                            mainRootRelativeLayout,
                                            resources.getString(R.string.invalidOTP)
                                        )
                                    }
                                    wrongAttempCount = wrongAttempCount + 1
                                }


                            }
                        }
                    }
                }
            }
        }.autoDispose(disposables)
        txttimer.throttleClick().subscribe() {

            if (txttimer.text == resources.getString(R.string.resendOTP)) {
                txttimer.isClickable = true
                txttimer.paintFlags = 0
                if (!intent.getStringExtra("PhoneNo").isNullOrEmpty()) {
                    Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe {
                            viewModel.callLogin(
                                mContext,
                                intent.getStringExtra("PhoneNo")
                            )
                        }.autoDispose(disposables)

                } else {
                    TxtMobileNumber.setText("1234")
                }
            } else {
                txttimer.isClickable = false
                txttimer.paintFlags = 0
            }


        }.autoDispose(disposables)


        TxtChangeNumber.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        TxtChangeNumber.throttleClick().subscribe() {
            onBackPressed()
        }.autoDispose(disposables)

        callTimer()
        if (!intent.getStringExtra("PhoneNo").isNullOrEmpty()) {
            TxtMobileNumber.setText(intent.getStringExtra("PhoneNo"))

        } else {
            TxtMobileNumber.setText("1234")
        }
    }

    private fun callTimer() {
        val timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                txttimer.setText(resources.getString(R.string.otpverification) + millisUntilFinished / 1000)

            }

            override fun onFinish() {
                txttimer.text = resources.getString(R.string.resendOTP)
                txttimer.paintFlags = Paint.UNDERLINE_TEXT_FLAG

//                btnVerify.setBackgroundColor(resources.getColor(R.color.color_dark_grey_txt))
//                if (btnVerify.text.toString() == resources.getString(R.string.getnewOTP)) {
//                } else {
//                    btnVerify.isClickable = false
//                }
            }
        }
        timer.start()
    }

    private fun setOtpEditTextHandler() {

        for (i in 0..3) { //Its designed for 6 digit OTP
            otpEt.get(i)!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable) {
                    if (i == 3 && otpEt[i]!!.text.toString().isNotEmpty()) {
//                        otpEt[i]!!
//                            .clearFocus() //Clears focus when you have entered the last digit of the OTP.
                    } else if (otpEt[i]!!.text.toString().isNotEmpty()) {
                        otpEt.get(i + 1)!!
                            .requestFocus() //focuses on the next edittext after a digit is entered.
                    }
                }
            })
            otpEt.get(i)!!.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(
                    v: View?,
                    keyCode: Int,
                    event: KeyEvent
                ): Boolean {
                    if (event.action != KeyEvent.ACTION_DOWN) {
                        return false
                        //Dont get confused by this, it is because onKeyListener is called twice and this condition is to avoid it.
                    }
                    if (keyCode == KeyEvent.KEYCODE_DEL && otpEt[i]!!.text.toString()
                            .isEmpty() && i != 0
                    ) {
//                        otpEt.get(i - 1)!!.setText("") //Deletes the digit of OTP
                        otpEt.get(i - 1)!!.requestFocus() //and sets the focus on previous digit
                    }
                    return false
                }
            })
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

    private fun observeStates() {
        viewModel.getDataObserver().removeObservers(this)
        viewModel.getDataObserver().observe(this, Observer {
            when (it) {
                is NetworkRequestState.NetworkNotAvailable -> {
                    BaseUtility.showAlertMessage(
                        mContext!!,
                        R.string.alert,
                        R.string.check_internet
                    )
                }


                is NetworkRequestState.LoadingData -> {
                    progressDialog.show()
                }
                is NetworkRequestState.ErrorResponse -> {
                    progressDialog.dismiss()
                    CallSnackbar(
                        mainRootRelativeLayout,
                        ApiConstants.VolunteerNotRegisterErrorMessage
                    )
                }
                is NetworkRequestState.SuccessResponse<*> -> {
                    progressDialog.dismiss()
                    CallSnackbar(
                        mainRootRelativeLayout,
                        resources.getString(R.string.otpsendsuccesfully)
                    )
                    callTimer()
                }

            }
        })
    }
}