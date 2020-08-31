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
import com.nitiaayog.apnesaathi.ui.adminandstaffmember.password.PasswordActivity
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.dashboard.DashBoardActivity
import com.nitiaayog.apnesaathi.ui.login.LoginActivity
import com.nitiaayog.apnesaathi.ui.login.LoginViewModel
import com.nitiaayog.apnesaathi.utility.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_login_otpverify.*
import java.util.concurrent.TimeUnit

/**
 * [OtpActivity] Activity for validation the Login User OTP
 * [BaseActivity] is the base activity with functions that are common in all the Activity
 * [OtpActivityModel] is the view model for performing fetching data from API.
 */
class OtpActivity : BaseActivity<OtpActivityModel>() {

    /**
     * Roles Details,
     * ROLE_VOLUNTEER = "1",
     * ROLE_STAFF_MEMBER = "2",
     * ROLE_DISTRICT_ADMIN = "3" [Access to Features : Only See Grievances of only it's own assigned district],
     * ROLE_MASTER_ADMIN = "4",
     * */

    var wrongAttempCount: Int = 0
    lateinit var mContext: Context
    val otpEt = arrayOfNulls<EditText>(4)

    private val progressDialog: ProgressDialog.Builder by lazy {
        ProgressDialog.Builder(mContext).setMessage("Please wait.")
    }

    private val userId: String by lazy {
        val isUserId: Boolean = intent?.hasExtra(ApiConstants.UserId) ?: false
        if (isUserId) intent.getStringExtra(ApiConstants.UserId) else ""
    }
    private val role: String by lazy {
        val isRole: Boolean = intent?.hasExtra(ApiConstants.Role) ?: false
        if (isRole) intent.getStringExtra(ApiConstants.Role) else ""
    }
    private val phoneNumber: String by lazy {
        val isPhoneNo: Boolean = intent?.hasExtra(ApiConstants.PhoneNumber) ?: false
        if (isPhoneNo) intent.getStringExtra(ApiConstants.PhoneNumber) else ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = this

        otpEt[0] = findViewById(R.id.edOtp1)
        otpEt[1] = findViewById(R.id.edOtp2)
        otpEt[2] = findViewById(R.id.edOtp3)
        otpEt[3] = findViewById(R.id.edOtp4)
        setOtpEditTextHandler()

        observeStates()

        tvOtpMessage.text = getString(R.string.wehavesendverificaiton_code, phoneNumber)

        ivBack.throttleClick().subscribe { finish() }.autoDispose(disposables)
        btnVerify.throttleClick().subscribe {
            hideKeyboard()
            if (btnVerify.text.toString() == resources.getString(R.string.getnewOTP)) {
                onBackPressed()
            } else {
                if (TextUtils.isEmpty(edOtp1.text.toString().trim())) {
                    CallSnackbar(mainRootRelativeLayout, resources.getString(R.string.enterOtp))
                } else {
                    edOtp1.error = null
                    if (TextUtils.isEmpty(edOtp2.text.toString().trim())) {
                        CallSnackbar(mainRootRelativeLayout, resources.getString(R.string.enterOtp))
                    } else {
                        edOtp2.error = null
                        if (TextUtils.isEmpty(edOtp3.text.toString().trim())) {
                            CallSnackbar(
                                mainRootRelativeLayout,
                                resources.getString(R.string.enterOtp)
                            )

                        } else {
                            edOtp4.error = null
                            if (TextUtils.isEmpty(edOtp4.text.toString().trim())) {
                                CallSnackbar(
                                    mainRootRelativeLayout,
                                    resources.getString(R.string.enterOtp)
                                )
                            } else {
                                edOtp4.error = null
                                if (edOtp1.text.toString()
                                        .trim() + edOtp2.text.toString()
                                        .trim() + edOtp3.text.toString()
                                        .trim() + edOtp4.text.toString().trim() == "1122"
                                ) {
                                    if (wrongAttempCount <= 3) {
                                        callnextActivity()
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
                                    wrongAttempCount += 1
                                }
                            }
                        }
                    }
                }
            }
        }.autoDispose(disposables)
        txttimer.throttleClick().subscribe {

            if (txttimer.text == resources.getString(R.string.resendOTP)) {
                txttimer.isClickable = true
                txttimer.paintFlags = 0
                if (phoneNumber.isNotEmpty()) {//!intent.getStringExtra("PhoneNo").isNullOrEmpty()
                    Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe {
                            viewModel.callLogin(
                                mContext,
                                phoneNumber//intent.getStringExtra("PhoneNo")
                            )
                        }.autoDispose(disposables)

                } /*else {
                    TxtMobileNumber.text = "1234"
                }*/
            } else {
                txttimer.isClickable = false
                txttimer.paintFlags = 0
            }


        }.autoDispose(disposables)

        TxtChangeNumber.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        TxtChangeNumber.throttleClick().subscribe { onBackPressed() }.autoDispose(disposables)

        callTimer()
    }

    /**
     *  Method for set time OTP.
     */
    private fun callTimer() {
        val timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                txttimer.text =
                    resources.getString(R.string.otpverification) + millisUntilFinished / 1000
            }

            override fun onFinish() {
                txttimer.text = resources.getString(R.string.resendOTP)
                txttimer.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            }
        }
        timer.start()
    }

    /**
     *  Method for handel the OTP Edit Text Next or Back Functionality
     */
    private fun setOtpEditTextHandler() {

        for (i in 0..3) {
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
                    } else if (otpEt[i]!!.text.toString().isNotEmpty()) {
                        otpEt.get(i + 1)!!
                            .requestFocus()
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
                    }
                    if (keyCode == KeyEvent.KEYCODE_DEL && otpEt[i]!!.text.toString()
                            .isEmpty() && i != 0
                    ) {
             otpEt.get(i - 1)!!.requestFocus() //and sets the focus on previous digit
                    }
                    return false
                }
            })
        }
    }

    /**
     *  Method for Navigating the next activity.
     */
    private fun callnextActivity() {
        val targetNavigation = when (role) {
            ROLE_STAFF_MEMBER, ROLE_DISTRICT_ADMIN, ROLE_MASTER_ADMIN -> {
                PasswordActivity::class.java
            }
            ROLE_VOLUNTEER -> {
                DashBoardActivity::class.java
            }
            else -> null
        }
        targetNavigation?.run {
            /*dataManager.setPhoneNumber(phoneNumber) //intent.getStringExtra("PhoneNo")
            dataManager.setRole(role)*/
            val navigationIntent = getTargetIntent(this)
            when (role) {
                ROLE_VOLUNTEER -> {
                    viewModel.setData(userId, phoneNumber, role)
                    finishAffinity()
                }
                else -> {
                    navigationIntent.putExtra(ApiConstants.UserId, userId)
                    navigationIntent.putExtra(ApiConstants.PhoneNumber, phoneNumber)
                    navigationIntent.putExtra(ApiConstants.Role, role)
                    finish()
                }
            }
            startActivity(navigationIntent)
        }
    }

    override fun provideViewModel(): OtpActivityModel = getViewModel {
        OtpActivityModel.getInstance(dataManager)
    }

    override fun provideLayoutResource(): Int = R.layout.activity_login_otpverify

    /**
     *  Method for hiding the keypad.
     */
    fun AppCompatActivity.hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    /**
     *  Method for fetching the login API Response.
     */
    private fun observeStates() {
        viewModel.getDataObserver().removeObservers(this)
        viewModel.getDataObserver().observe(this, Observer {
            when (it) {
                is NetworkRequestState.NetworkNotAvailable -> {
                    BaseUtility.showAlertMessage(
                        mContext,
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