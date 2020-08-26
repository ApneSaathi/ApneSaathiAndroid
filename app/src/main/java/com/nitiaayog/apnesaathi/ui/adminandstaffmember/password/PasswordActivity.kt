package com.nitiaayog.apnesaathi.ui.adminandstaffmember.password

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ScrollView
import androidx.lifecycle.Observer
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.ProgressDialog
import com.nitiaayog.apnesaathi.base.extensions.getTargetIntent
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.ui.adminandstaffmember.dashboard.DashboardActivity
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.SEARCH_ELEMENT_DELAY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_password.*
import java.util.concurrent.TimeUnit

class PasswordActivity : BaseActivity<PasswordViewModel>() {

    private val progressDialog: ProgressDialog.Builder by lazy {
        ProgressDialog.Builder(this).setTitle(R.string.verification)
            .setMessage(R.string.check_details)
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

        getDataStreams()

        ivBackToLogin.throttleClick().subscribe { finish() }.autoDispose(disposables)

        /**
         * When keyboard pops up scroll to bottom to show login button above the keyboard.
         * */
        tietPassword.throttleClick().subscribe {
            Observable.timer(SEARCH_ELEMENT_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                }.autoDispose(disposables)
        }.autoDispose(disposables)

        tietPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                editable?.run {
                    if (this.toString().isNotEmpty() && tilPassword.isErrorEnabled)
                        tilPassword.isErrorEnabled = false
                }
            }

            override fun beforeTextChanged(
                text: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        btnLogin.throttleClick().subscribe {
            if (validateFields()) viewModel.validatePassword(this, tietPassword.text.toString())
        }.autoDispose(disposables)
    }

    override fun provideViewModel(): PasswordViewModel = getViewModel {
        PasswordViewModel.getInstance(dataManager, userId, phoneNumber, role)
    }

    override fun provideLayoutResource(): Int = R.layout.activity_password

    /**
     * Validation check before calling API
     * */
    private fun validateFields(): Boolean {
        if (tietPassword.text.toString().isEmpty()) {
            tilPassword.error = getString(R.string.validate_password)
            return false
        } else tilPassword.error = ""
        return true
    }

    /**
     * Observe API response and initiate proper action.
     * */
    private fun getDataStreams() = viewModel.getNetworkStateObservable().observe(this, Observer {
        when (it) {
            is NetworkRequestState.NetworkNotAvailable ->
                BaseUtility.showAlertMessage(this, R.string.error, R.string.check_internet)
            is NetworkRequestState.LoadingData -> progressDialog.show()
            is NetworkRequestState.ErrorResponse -> {
                progressDialog.dismiss()
                BaseUtility.showAlertMessage(
                    this, getString(R.string.error),
                    getString(R.string.cannt_connect_to_server_try_later), getString(R.string.okay)
                )
            }
            is NetworkRequestState.Error -> {
                tilPassword.error = getString(R.string.validate_password)
                tilPassword.isErrorEnabled = true
                progressDialog.dismiss()
            }
            is NetworkRequestState.SuccessResponse<*> -> {
                progressDialog.dismiss()
                startActivity(getTargetIntent(DashboardActivity::class.java))
                finishAffinity()
            }
        }
    })
}