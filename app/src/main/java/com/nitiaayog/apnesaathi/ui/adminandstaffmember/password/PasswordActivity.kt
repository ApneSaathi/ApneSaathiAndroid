package com.nitiaayog.apnesaathi.ui.adminandstaffmember.password

import android.os.Bundle
import androidx.lifecycle.Observer
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.ProgressDialog
import com.nitiaayog.apnesaathi.base.extensions.getTargetIntent
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.onTextChanges
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.ui.adminandstaffmember.dashboard.DashboardActivity
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.utility.BaseUtility
import kotlinx.android.synthetic.main.activity_password.*

class PasswordActivity : BaseActivity<PasswordViewModel>() {

    private val progressDialog: ProgressDialog.Builder by lazy {
        ProgressDialog.Builder(this).setTitle(R.string.verification)
            .setMessage(R.string.check_details)
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

        ivBack.throttleClick().subscribe { finish() }.autoDispose(disposables)

        tietPassword.onTextChanges {
            if (it.isNotEmpty()) tilPassword.error = ""
        }

        btnLogin.throttleClick().subscribe {
            if (validateFields()) viewModel.validatePassword(this, tietPassword.text.toString())
        }.autoDispose(disposables)
    }

    override fun provideViewModel(): PasswordViewModel = getViewModel {
        PasswordViewModel.getInstance(dataManager, phoneNumber, role)
    }

    override fun provideLayoutResource(): Int = R.layout.activity_password

    private fun validateFields(): Boolean {
        if (tietPassword.text.toString().isEmpty()) {
            tilPassword.error = getString(R.string.validate_password)
            return false
        } else tilPassword.error = ""
        return true
    }

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
                progressDialog.dismiss()
                BaseUtility.showAlertMessage(
                    this, R.string.error, R.string.something_went_wrong
                )
            }
            is NetworkRequestState.SuccessResponse<*> -> {
                progressDialog.dismiss()
                startActivity(getTargetIntent(DashboardActivity::class.java))
                finishAffinity()
            }
        }
    })
}