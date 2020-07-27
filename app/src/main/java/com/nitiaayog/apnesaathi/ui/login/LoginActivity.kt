package com.nitiaayog.apnesaathi.ui.login

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
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

    companion object {
        private const val CONST_PHONE_NUMBER_PICKER: Int = 0xa12e
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeStates()

        var disposable = EditMobileNumber.throttleClick().subscribe { requestHint() }

        tvUseOtherNo.throttleClick().subscribe {
            if (tvUseOtherNo.text.toString() == getString(R.string.use_other_number)) {
                EditMobileNumber.text.clear()
                EditMobileNumber.isFocusable = true
                EditMobileNumber.isFocusableInTouchMode = true
                if (!disposable.isDisposed) disposable.dispose()

                tvUseOtherNo.setText(R.string.select_mobile_number)

                EditMobileNumber.requestFocus()
                popupKeyboard()
            } else {
                hideKeyboard()

                EditMobileNumber.isFocusable = false
                EditMobileNumber.isFocusableInTouchMode = false
                EditMobileNumber.clearFocus()

                disposable = EditMobileNumber.throttleClick().subscribe { requestHint() }

                requestHint()

                tvUseOtherNo.setText(R.string.use_other_number)
            }
        }.autoDispose(disposables)

        btnLogin.throttleClick().subscribe {
            hideKeyboard()
            if (TextUtils.isEmpty(EditMobileNumber.text.toString().trim())) {
                EditMobileNumber.error = resources.getString(R.string.txtenterMobilenumbe)
            } else {
                EditMobileNumber.error = null
                try {
                    Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe {
                            viewModel.callLogin(this, EditMobileNumber.text.toString())
                        }.autoDispose(disposables)
                } catch (e: Exception) {
                    println("TAG -- MyData --> ${e.message}")
                }
                EditMobileNumber.isFocusable = false

            }
        }.autoDispose(disposables)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((resultCode == Activity.RESULT_OK) && (requestCode == CONST_PHONE_NUMBER_PICKER)) {
            val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)
            val phoneNumber = credential?.id
            println("TAG -- dual-sim --> $phoneNumber")
            phoneNumber?.let { EditMobileNumber.setText(it.replace("+91", "")) }
        }
    }

    private fun observeStates() {
        viewModel.getDataObserver().removeObservers(this)
        viewModel.getDataObserver().observe(this, Observer {
            when (it) {
                is NetworkRequestState.NetworkNotAvailable -> {
                    BaseUtility.showAlertMessage(this, R.string.alert, R.string.check_internet)
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

    private fun requestHint() {
        val hintRequest: HintRequest = HintRequest.Builder().setPhoneNumberIdentifierSupported(true)
            .build()
        val intent: PendingIntent = Credentials.getClient(this)
            .getHintPickerIntent(hintRequest)
        startIntentSenderForResult(
            intent.intentSender, CONST_PHONE_NUMBER_PICKER, null, 0, 0, 0, null
        )
    }

    private fun popupKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.toggleSoftInputFromWindow(
            EditMobileNumber.windowToken, InputMethodManager.SHOW_IMPLICIT,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    private fun AppCompatActivity.hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(EditMobileNumber.windowToken, 0)
        }
        // else {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        // }
    }
}