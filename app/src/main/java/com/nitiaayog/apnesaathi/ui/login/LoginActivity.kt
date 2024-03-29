package com.nitiaayog.apnesaathi.ui.login

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.CallSnackbar
import com.nitiaayog.apnesaathi.base.extensions.getTargetIntent
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.loginresponse.LoginResponse
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.grievances.GrievanceDetailsViewModel
import com.nitiaayog.apnesaathi.ui.otp.OtpActivity
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit


/**
 * [LoginActivity] Activity for multiple login
 * [BaseActivity] is the base activity with functions that are common in all the Activity
 * [LoginViewModel] is the view model for performing fetching data from API.
 */
class LoginActivity : BaseActivity<LoginViewModel>() {

    /**
     * Staff Mem      : 9611571555  ----  A@x@ffrt6781
     * Staff Mem      : 9246920600  ----  pwd@123
     * Master Admin   : 9248602051  ----  pwd@988
     * District Admin : 9247299503  ----  pwd@485
     * Volunteer      : 44444
     */

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


    /**
     *  Method for handing the login API Response.
     */
    private fun observeStates() {
        viewModel.getDataObserver().removeObservers(this)
        viewModel.getDataObserver().observe(this, Observer {
            when (it) {
                is NetworkRequestState.NetworkNotAvailable -> {
                    BaseUtility.showAlertMessage(
                        mContext!!,
                        R.string.alert, R.string.check_internet
                    )
                }
                is NetworkRequestState.LoadingData -> {
                    progressBarlogin.visibility = VISIBLE
                }
                is NetworkRequestState.Error, is NetworkRequestState.ErrorResponse -> {
                    progressBarlogin.visibility = GONE
                    EditMobileNumber.isFocusableInTouchMode = true
                    CallSnackbar(rootRelativeLayout, getString(R.string.validate_movbile_number))
                }
                is NetworkRequestState.SuccessResponse<*> -> {
                    EditMobileNumber.isFocusableInTouchMode = true
                    progressBarlogin.visibility = GONE

                    val loginResponse = it.data as LoginResponse
                    dataManager.setSelectedDistrictId(loginResponse.getDistrictId() ?: "-1")
                    val intent = getTargetIntent(OtpActivity::class.java)
                    intent.putExtra(ApiConstants.UserId, loginResponse.getId().toString())
                    intent.putExtra(ApiConstants.PhoneNumber, EditMobileNumber.text.toString())
                    intent.putExtra(ApiConstants.Role, loginResponse.getRole())
                    startActivity(intent)
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

    /**
     *  Method for hiding the keypad
     */
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
