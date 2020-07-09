package com.nitiaayog.apnesaathi.ui.fragments.calls.registernewseniorcitizen

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.Observer
import com.jakewharton.rxbinding3.widget.textChanges
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.ProgressDialog
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.utility.BaseUtility
import kotlinx.android.synthetic.main.include_register_new_sr_citizen.*
import kotlinx.android.synthetic.main.include_toolbar.*

class RegisterNewSeniorCitizenFragment : BaseFragment<RegisterSeniorCitizenViewModel>() {

    private var selectedGender: String = ""
    private var selectedDistrict: String = ""
    private var selectedState: String = ""

    private val progressDialog: ProgressDialog.Builder by lazy {
        ProgressDialog.Builder(context!!).setMessage(R.string.wait_saving_data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.title = getString(R.string.register_a_new_citizen)
        toolBar.setNavigationOnClickListener { fragmentManager?.popBackStack() }

        observeStates()
        initAutoCompleteTextView()
        initClicks()
        initTextWatcher()
    }

    override fun provideViewModel(): RegisterSeniorCitizenViewModel =
        getViewModel { RegisterSeniorCitizenViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.fragment_register_new_sr_citizen

    override fun onCallPermissionGranted() {

    }

    override fun onCallPermissionDenied() {

    }

    private fun initAutoCompleteTextView() {
        val genderList = resources.getStringArray(R.array.gender_array)
        val gendersAdapter =
            ArrayAdapter(activity!!, R.layout.item_layout_dropdown_menu, genderList)
        actGender.threshold = 0
        actGender.setAdapter(gendersAdapter)
        actGender.setOnKeyListener(null)
        actGender.setOnDismissListener {
            updateDropDownIndicator(actGender, R.drawable.ic_arrow_down)
        }
        actGender.setOnItemClickListener { _, _, position, _ ->
            selectedGender = genderList[position]
            viewModel.setGender(
                when (selectedGender) {
                    getString(R.string.gender_male) -> "M"
                    getString(R.string.gender_female) -> "F"
                    getString(R.string.gender_others) -> "O"
                    else -> ""
                }
            )
        }

        val stateList = resources.getStringArray(R.array.states_array)
        val stateAdapter = ArrayAdapter(activity!!, R.layout.item_layout_dropdown_menu, stateList)
        actState.threshold = 0
        actState.setAdapter(stateAdapter)
        actState.setOnKeyListener(null)
        actState.setOnDismissListener {
            updateDropDownIndicator(actState, R.drawable.ic_arrow_down)
        }
        actState.setOnItemClickListener { _, _, position, _ ->
            selectedState = stateList[position]
            viewModel.setState(selectedState)
        }

        val districtsList = resources.getStringArray(R.array.districts_array)
        val districtsAdapter =
            ArrayAdapter(activity!!, R.layout.item_layout_dropdown_menu, districtsList)
        actDistrict.threshold = 0
        actDistrict.setAdapter(districtsAdapter)
        actDistrict.setOnKeyListener(null)
        actDistrict.setOnDismissListener {
            updateDropDownIndicator(actDistrict, R.drawable.ic_arrow_down)
        }
        actDistrict.setOnItemClickListener { _, _, position, _ ->
            selectedDistrict = districtsList[position]
            viewModel.setDistrict(selectedDistrict)
        }
    }

    private fun initClicks() {

        etContactNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(10)))

        actGender.throttleClick().subscribe {
            actGender.showDropDown()
            updateDropDownIndicator(actGender, R.drawable.ic_arrow_up)
            if (tvGenderError.visibility == View.VISIBLE) tvGenderError.visibility = View.GONE
        }.autoDispose(disposables)

        actState.throttleClick().subscribe {
            actState.showDropDown()
            updateDropDownIndicator(actState, R.drawable.ic_arrow_up)
            if (tvStateError.visibility == View.VISIBLE) tvStateError.visibility = View.GONE
        }.autoDispose(disposables)

        actDistrict.throttleClick().subscribe {
            actDistrict.showDropDown()
            updateDropDownIndicator(actDistrict, R.drawable.ic_arrow_up)
            if (tvDistrictError.visibility == View.VISIBLE) tvDistrictError.visibility = View.GONE
        }.autoDispose(disposables)

        tvRegister.throttleClick().subscribe {
            if (validateFields()) viewModel.registerNewSeniorCitizen(context!!)
        }.autoDispose(disposables)
        tvCancel.throttleClick().subscribe {}.autoDispose(disposables)
    }

    private fun updateDropDownIndicator(autoCompleteTextView: AutoCompleteTextView, icon: Int) =
        autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0)

    private fun validateFields(): Boolean {
        if (etName.text.isEmpty()) {
            tvNameError.visibility = View.VISIBLE
            return false
        } else if (etAge.text.isEmpty()) {
            tvAgeError.visibility = View.VISIBLE
            return false
        } else if (etAge.text.toString().toInt() < 60) {
            tvAgeError.visibility = View.VISIBLE
            return false
        } else if (etAge.text.toString().toInt() > 110) {
            tvAgeError.visibility = View.VISIBLE
            return false
        } else if (selectedGender.isEmpty()) {
            tvGenderError.visibility = View.VISIBLE
            return false
        } else if (etContactNumber.text.isEmpty()) {
            tvContactNumberError.setText(R.string.validate_contact_number)
            tvContactNumberError.visibility = View.VISIBLE
            return false
        } else if (!BaseUtility.validatePhoneNumber(etContactNumber.text.toString()) ||
            (etContactNumber.text.length < 7)
        ) {
            tvContactNumberError.setText(R.string.valid_contact_number)
            tvContactNumberError.visibility = View.VISIBLE
            return false
        } else if (selectedState.isEmpty()) {
            tvStateError.visibility = View.VISIBLE
            return false
        } else if (selectedDistrict.isEmpty()) {
            tvDistrictError.visibility = View.VISIBLE
            return false
        } else if (etAddress.text.isEmpty()) {
            tvAddressError.visibility = View.VISIBLE
            return false
        } else {
            tvNameError.visibility = View.GONE
            viewModel.setName(etName.text.toString())

            tvAgeError.visibility = View.GONE
            viewModel.setAge(etAge.text.toString())

            tvGenderError.visibility = View.GONE

            tvContactNumberError.visibility = View.GONE
            viewModel.setContactNumber(etContactNumber.text.toString())

            tvStateError.visibility = View.GONE
            tvDistrictError.visibility = View.GONE

            tvAddressError.visibility = View.GONE
            viewModel.setAddress(etAddress.text.toString())
        }
        return true
    }

    private fun resetRegisterNewSrCitizenLayout() {

        etName.text.clear()
        viewModel.setName("")

        etAge.text.clear()
        viewModel.setAge("")

        actGender.setText("")
        viewModel.setGender("")

        etContactNumber.text.clear()
        viewModel.setContactNumber("")

        actDistrict.setText("")
        viewModel.setDistrict("")

        actState.setText("")
        viewModel.setState("")

        etAddress.text.clear()
        viewModel.setAddress("")
    }

    private fun observeStates() {
        viewModel.getDataObserver().removeObservers(viewLifecycleOwner)
        viewModel.getDataObserver().observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkRequestState.NetworkNotAvailable ->
                    BaseUtility.showAlertMessage(context!!, R.string.alert, R.string.check_internet)
                is NetworkRequestState.LoadingData -> progressDialog.show()
                is NetworkRequestState.ErrorResponse, is NetworkRequestState.Error -> {
                    progressDialog.dismiss()
                    BaseUtility.showAlertMessage(
                        context!!, R.string.alert, R.string.can_not_connect_to_server
                    )
                }
                is NetworkRequestState.SuccessResponse<*> -> {
                    progressDialog.dismiss()

                    resetRegisterNewSrCitizenLayout()
                    BaseUtility.showAlertMessage(
                        activity!!, R.string.success, R.string.sr_citizen_registered_successfully,
                        R.string.okay
                    ) { dialog, _ ->
                        dialog.dismiss()
                        fragmentManager?.popBackStack()
                    }

//                    BaseUtility.showAlertMessage(
//                        context!!, R.string.success, R.string.sr_citizen_registered_successfully
//                    )
                }
            }
        })
    }

    private fun initTextWatcher() {
        etName.textChanges().subscribe {
            it?.run { if (this.toString().isNotEmpty()) tvNameError.visibility = View.GONE }
        }.autoDispose(disposables)

        etAge.textChanges().subscribe {
            it?.run { if (this.toString().isNotEmpty()) tvAgeError.visibility = View.GONE }
        }.autoDispose(disposables)

        etContactNumber.textChanges().subscribe {
            it?.run {
                if (this.toString().isNotEmpty()) tvContactNumberError.visibility = View.GONE
            }
        }.autoDispose(disposables)

        etAddress.textChanges().subscribe {
            it?.run { if (this.toString().isNotEmpty()) tvAddressError.visibility = View.GONE }
        }.autoDispose(disposables)
    }
}