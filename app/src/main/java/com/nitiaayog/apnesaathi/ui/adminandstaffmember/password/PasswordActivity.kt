package com.nitiaayog.apnesaathi.ui.adminandstaffmember.password

import android.os.Bundle
import androidx.lifecycle.Observer
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.onTextChanges
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_password.*

class PasswordActivity : BaseActivity<PasswordViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getObservableStreams()

        ivBack.throttleClick().subscribe { finish() }.autoDispose(disposables)

        tietPassword.onTextChanges {
            if (it.isNotEmpty()) tilPassword.error = ""
        }

        btnLogin.throttleClick().subscribe {
            if (validateFields()) viewModel.validatePassword(this, tietPassword.text.toString())
        }.autoDispose(disposables)
    }

    override fun provideViewModel(): PasswordViewModel = getViewModel {
        PasswordViewModel.getInstance(dataManager)
    }

    override fun provideLayoutResource(): Int = R.layout.activity_password

    private fun validateFields(): Boolean {
        if (tietPassword.text.toString().isEmpty()) {
            tilPassword.error = getString(R.string.validate_password)
            return false
        } else tilPassword.error = ""
        return true
    }

    private fun getObservableStreams() =
        viewModel.getNetworkStateObservable().observe(this, Observer {
            when (it) {
                is NetworkRequestState.NetworkNotAvailable -> {
                }
                is NetworkRequestState.LoadingData -> {
                }
                is NetworkRequestState.ErrorResponse -> {
                }
                is NetworkRequestState.Error -> {
                }
                is NetworkRequestState.SuccessResponse<*> -> {
                }
            }
        })
}