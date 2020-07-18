package com.nitiaayog.apnesaathi.ui.login

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.CallSnackbar
import com.nitiaayog.apnesaathi.base.extensions.getTargetIntent
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.loginresponse.Login_Response
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.otp.OtpActivity
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit


class LoginActivity : BaseActivity<LoginViewModel>() {
    lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        observeStates()


        btnLogin.throttleClick().subscribe() {
            hideKeyboard()
            if (TextUtils.isEmpty(EditMobileNumber.text.toString().trim())) {
                EditMobileNumber.setError(resources.getString(R.string.txtenterMobilenumbe))
            } else {
                EditMobileNumber.setError(null)
                try {


                    Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe {
                            viewModel.callLogin(
                                mContext,
                                EditMobileNumber.text.toString()
                            )
                        }.autoDispose(disposables)

                } catch (e: Exception) {
                    println("TAG -- MyData --> ${e.message}")
                }
                EditMobileNumber.isFocusable = false

            }
        }.autoDispose(disposables)

    }


    private fun layoutGravitySet(
        rootRelativeLayout: RelativeLayout?,
        center: Int
    ) {
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT, 0
        )
        params.gravity = center
        rootRelativeLayout?.setLayoutParams(params)
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
                    progressBarlogin.visibility = VISIBLE
                }
                is NetworkRequestState.ErrorResponse -> {

                    progressBarlogin.visibility = GONE
                    EditMobileNumber.isFocusableInTouchMode = true
                    CallSnackbar(rootRelativeLayout, ApiConstants.VolunteerNotRegisterErrorMessage)
                }
                is NetworkRequestState.SuccessResponse<*> -> {
                    val loginres = it.data
                    EditMobileNumber.isFocusableInTouchMode = true
                    if (loginres is Login_Response)
                        dataManager.updateUserPreference(loginres)

                    progressBarlogin.visibility = GONE
                    val targetIntent = getTargetIntent(OtpActivity::class.java)
                    targetIntent.putExtra("PhoneNo", EditMobileNumber.text.toString())
                    startActivity(targetIntent)
                    EditMobileNumber.text.clear()

                }

            }
        })
    }


    override fun provideViewModel(): LoginViewModel = getViewModel {
        LoginViewModel.getInstance(dataManager)
    }

    override fun provideLayoutResource(): Int = R.layout.activity_login

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()

    }


    fun AppCompatActivity.hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        // else {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        // }
    }
}