package com.nitiaayog.apnesaathi.ui.fragments.calls.registernewseniorcitizen

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import kotlinx.android.synthetic.main.include_register_new_sr_citizen.*
import kotlinx.android.synthetic.main.include_toolbar.*

class RegisterNewSeniorCitizenFragment : BaseFragment<RegisterSeniorCitizenViewModel>() {

    private var selectedGender: String = ""
    private var selectedDistrict: String = ""
    private var selectedState: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.title = getString(R.string.register_a_new_citizen)
        toolBar.setNavigationOnClickListener { fragmentManager?.popBackStack() }

        initAutoCompleteTextView()
        initClicks()
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
        }
    }

    private fun initClicks() {
        actGender.throttleClick().subscribe {
            actGender.showDropDown()
            updateDropDownIndicator(actGender, R.drawable.ic_arrow_up)
        }.autoDispose(disposables)

        actState.throttleClick().subscribe {
            actState.showDropDown()
            updateDropDownIndicator(actState, R.drawable.ic_arrow_up)
        }.autoDispose(disposables)

        actDistrict.throttleClick().subscribe {
            actDistrict.showDropDown()
            updateDropDownIndicator(actDistrict, R.drawable.ic_arrow_up)
        }.autoDispose(disposables)

        tvRegister.throttleClick().subscribe {
            if (validateFields())
                viewModel.registerNewSeniorCitizen(
                    context!!, etName.text.toString(), etAge.text.toString(), selectedGender,
                    etContactNumber.text.toString(), selectedDistrict, selectedState,
                    etAddress.text.toString()
                )
        }.autoDispose(disposables)
        tvCancel.throttleClick().subscribe {}.autoDispose(disposables)
    }

    private fun updateDropDownIndicator(autoCompleteTextView: AutoCompleteTextView, icon: Int) =
        autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0)

    private fun validateFields(): Boolean {
        when {
            etName.text.isEmpty() -> {
                tvNameError.visibility = View.VISIBLE
                return false
            }
            etAge.text.isEmpty() -> {
                tvAgeError.visibility = View.VISIBLE
                return false
            }
            selectedGender.isEmpty() -> {
                tvGenderError.visibility = View.VISIBLE
                return false
            }
            etContactNumber.text.isEmpty() -> {
                tvContactNumberError.visibility = View.VISIBLE
                return false
            }
            selectedState.isEmpty() -> {
                tvStateError.visibility = View.VISIBLE
                return false
            }
            selectedDistrict.isEmpty() -> {
                tvDistrictError.visibility = View.VISIBLE
                return false
            }
            etAddress.text.isEmpty() -> {
                tvAddressError.visibility = View.VISIBLE
                return false
            }
            else -> {
                tvNameError.visibility = View.GONE
                tvAgeError.visibility = View.GONE
                tvGenderError.visibility = View.GONE
                tvContactNumberError.visibility = View.GONE
                tvStateError.visibility = View.GONE
                tvDistrictError.visibility = View.GONE
                tvAddressError.visibility = View.GONE
            }
        }
        return true
    }
}