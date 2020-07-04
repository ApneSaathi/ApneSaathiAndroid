package com.nitiaayog.apnesaathi.ui.login

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View.GONE
import android.view.View.VISIBLE
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
        btnLogin.throttleClick().subscribe() {
            if (TextUtils.isEmpty(EditMobileNumber.text.toString().trim())) {
                CallSnackbar(rootRelativeLayout, resources.getString(R.string.txtenterMobilenumbe))
            } else {
                EditMobileNumber.setError(null)
//                if (EditMobileNumber.text.toString().trim().length <10) {
//                    CallSnackbar(
//                        rootRelativeLayout,
//                        resources.getString(R.string.txtValidmobilenumber)
//                    )
//
//                } else {

                try {
                    observeStates()

                    Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe {
                            viewModel.callLogin(
                                applicationContext,
                                EditMobileNumber.text.toString()
                            )
                        }
                        .autoDispose(disposables)
                } catch (e: Exception) {
                    println("TAG -- MyData --> ${e.message}")
                }



                EditMobileNumber.isFocusable = false

//                }
            }
        }.autoDispose(disposables)

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
                    CallSnackbar(
                        rootRelativeLayout, ApiConstants.VolunteerNotRegisterErrorMessage
                    )
                }
                is NetworkRequestState.SuccessResponse<*> -> {
                    val loginres = it.data

                    if (loginres is Login_Response) {
                        dataManager.updateUserPreference(loginres)
                    }

                    progressBarlogin.visibility = GONE
                    val targetIntent = getTargetIntent(OtpActivity::class.java)
                    targetIntent.putExtra("PhoneNo", EditMobileNumber.text.toString())
                    startActivity(targetIntent)
                    finish()

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
}