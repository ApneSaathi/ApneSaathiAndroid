package com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenfeedbackform

import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_senior_citizen_feedback_form.*

class SeniorCitizenFeedbackFormActivity : BaseActivity<SeniorCitizenFeedbackViewModel>() {

    private var selectedState: String = ""
    private var selectedDistrict: String = ""
    private var callConnectionStatus: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val explodeTransition = Explode()
            explodeTransition.interpolator = AccelerateDecelerateInterpolator()
            explodeTransition.duration = 1000
            window?.apply {
                this.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
                this.exitTransition = explodeTransition
            }
        }*/

        super.onCreate(savedInstanceState)

        initViews()
    }

    override fun provideViewModel(): SeniorCitizenFeedbackViewModel =
        getViewModel { SeniorCitizenFeedbackViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.activity_senior_citizen_feedback_form

    private fun initViews() {
        val statesList = resources.getStringArray(R.array.states_array)
        val statesAdapter = ArrayAdapter(this, R.layout.item_layout_dropdown_menu, statesList)
        actStates.setAdapter(statesAdapter)
        actStates.setOnItemClickListener { _, _, position, _ ->
            selectedState = statesList[position]
        }

        val districtsList = resources.getStringArray(R.array.districts_array)
        val districtsAdapter = ArrayAdapter(this, R.layout.item_layout_dropdown_menu, districtsList)
        actDistricts.setAdapter(districtsAdapter)
        actDistricts.setOnItemClickListener { _, _, position, _ ->
            selectedDistrict = districtsList[position]
        }

        val callStatusList = resources.getStringArray(R.array.call_status)
        val callStatusAdapter =
            ArrayAdapter(this, R.layout.item_layout_dropdown_menu, callStatusList)
        actCallStatus.setAdapter(callStatusAdapter)
        actCallStatus.setOnItemClickListener { _, _, position, _ ->
            callConnectionStatus = callStatusList[position]
        }

        ivBack.throttleClick().subscribe { finish() }.autoDispose(disposables)

        btnSubmit.throttleClick().subscribe {
            if (validateField()) {

            }
        }.autoDispose(disposables)
    }

    private fun validateField(): Boolean {
        when {
            tietVolunteerName.text.toString().isEmpty() -> {
                tietVolunteerName.error = getString(R.string.validate_volunteer_name)
                tilVolunteerName.isErrorEnabled = true
                return false
            }
            tietVolunteerPhoneNumber.text.toString().isEmpty() -> {
                tietVolunteerPhoneNumber.error = getString(R.string.validate_volunteer_phone_number)
                tilVolunteerPhoneNumber.isErrorEnabled = true
                return false
            }
            tietPersonName.text.toString().isEmpty() -> {
                tietPersonName.error = getString(R.string.validate_person_name)
                tilPersonName.isErrorEnabled = true
                return false
            }
            tietPersonPhoneNumber.text.toString().isEmpty() -> {
                tietPersonPhoneNumber.error = getString(R.string.validate_person_phone)
                tilPersonPhoneNumber.isErrorEnabled = true
                return false
            }
            selectedState.isEmpty() -> {
                Snackbar.make(
                    coordinatorLayout, getString(R.string.validate_person_state),
                    Snackbar.LENGTH_LONG
                ).show()
                return false
            }
            selectedDistrict.isEmpty() -> {
                Snackbar.make(
                    coordinatorLayout, getString(R.string.validate_person_district),
                    Snackbar.LENGTH_LONG
                ).show()
                return false
            }
            callConnectionStatus.isEmpty() -> {
                Snackbar.make(
                    coordinatorLayout, getString(R.string.validate_call_connection),
                    Snackbar.LENGTH_LONG
                ).show()
                return false
            }
            tietBlockName.text.toString().isEmpty() -> {
                tietBlockName.error = getString(R.string.validate_block_name)
                tilBlockName.isErrorEnabled = true
                return false
            }
            else -> {
                tilVolunteerName.isErrorEnabled = false
                tilVolunteerPhoneNumber.isErrorEnabled = false
                tilPersonName.isErrorEnabled = false
                tilPersonPhoneNumber.isErrorEnabled = false
                tilPersonState.isErrorEnabled = false
                tilBlockName.isErrorEnabled = false
            }
        }
        return true
    }
}