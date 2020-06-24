package com.nitiaayog.apnesaathi.ui.fragments.details

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_details_edit_page.*
import kotlinx.android.synthetic.main.include_toolbar.*

class SeniorCitizenEditFragment : BaseFragment<SeniorCitizenEditViewModel>() {
    override fun provideViewModel(): SeniorCitizenEditViewModel =
        getViewModel {
            SeniorCitizenEditViewModel.getInstance(
                dataManager
            )
        }

    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onSaveButton(status: String)
        fun onCancelButton()
    }

    var selectedGrievance: String = ""
    var selectedPriority: String = ""
    var selectedCallStatus: String = ""
    var selectedQuarantineStatus: String = ""
    var selectedOtherMedicalProblems: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar.title = getString(R.string.app_bar_title)
        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.setNavigationOnClickListener { activity?.onBackPressed() }

        initViews()
        initClicks()
    }

    private fun initViews() {
        rb_unattended.isChecked = true
        rb_no.isChecked = true

        val callStatusList = resources.getStringArray(R.array.call_status_array)
        val callCategoryAdapter =
            activity?.let { ArrayAdapter(it, R.layout.item_layout_dropdown_menu, callStatusList) }
        act_call_category.setAdapter(callCategoryAdapter)
        act_call_category.setOnItemClickListener { _, _, position, _ ->
            selectedCallStatus = callStatusList[position]
        }


        val otherMedicalProblemList = resources.getStringArray(R.array.medical_problems)
        val otherMedicalProblemsListAdapter =
            activity?.let {
                ArrayAdapter(
                    it,
                    R.layout.item_layout_dropdown_menu,
                    otherMedicalProblemList
                )
            }
        act_other_medical_problems.setAdapter(otherMedicalProblemsListAdapter)
        act_other_medical_problems.setOnItemClickListener { _, _, position, _ ->
            selectedOtherMedicalProblems = otherMedicalProblemList[position]
        }

        val quarantineStatusList = resources.getStringArray(R.array.quarantine_status_array)
        val quarantineStatusListAdapter =
            activity?.let {
                ArrayAdapter(
                    it,
                    R.layout.item_layout_dropdown_menu,
                    quarantineStatusList
                )
            }
        act_hospitalization_status.setAdapter(quarantineStatusListAdapter)
        act_hospitalization_status.setOnItemClickListener { _, _, position, _ ->
            selectedQuarantineStatus = quarantineStatusList[position]
        }

        val grievanceList = resources.getStringArray(R.array.grievance_array)
        val grievanceAdapter =
            activity?.let { ArrayAdapter(it, R.layout.item_layout_dropdown_menu, grievanceList) }
        act_grivence_category.setAdapter(grievanceAdapter)
        act_grivence_category.setOnItemClickListener { _, _, position, _ ->
            selectedGrievance = grievanceList[position]
        }

        val priorityList = resources.getStringArray(R.array.priority_array)
        val priorityAdapter =
            activity?.let { ArrayAdapter(it, R.layout.item_layout_dropdown_menu, priorityList) }
        act_priority.setAdapter(priorityAdapter)
        act_priority.setOnItemClickListener { _, _, position, _ ->
            selectedPriority = priorityList[position]
        }
    }

    private fun initClicks() {
        txt_cancel.setOnClickListener {
            if (::itemClickListener.isInitialized) itemClickListener.onCancelButton()
            activity?.supportFragmentManager?.popBackStackImmediate()
        }

        txt_save.setOnClickListener {
            var status = "Unattended"
            if (rb_attended.isChecked) {
                status = "Attended"
            }
            if (::itemClickListener.isInitialized) itemClickListener.onSaveButton(status)
            activity?.supportFragmentManager?.popBackStackImmediate()
        }
        rg_call_status.setOnCheckedChangeListener { _, optionId ->
            run {
                when (optionId) {
                    R.id.rb_attended -> {
                        ll_attended_container.visibility = View.VISIBLE
                        ti_call_category.visibility = View.GONE
                    }
                    R.id.rb_unattended -> {
                        ll_attended_container.visibility = View.GONE
                        ti_call_category.visibility = View.VISIBLE
                    }
                }
            }
        }
        rg_grivence.setOnCheckedChangeListener { _, optionId ->
            run {
                when (optionId) {
                    R.id.rb_yes -> {
                        ll_grivence_container.visibility = View.VISIBLE
                    }
                    R.id.rb_no -> {
                        ll_grivence_container.visibility = View.GONE
                    }
                }
            }
        }

        img_cough.setOnClickListener {
            val status = !img_cough.isSelected
            img_cough.isSelected = status
            txt_cough.isSelected = status
            view_border_cough.isSelected = status
        }
        img_fever.setOnClickListener {
            val status = !img_fever.isSelected
            img_fever.isSelected = status
            txt_fever.isSelected = status
            view_border_fever.isSelected = status
        }
        img_shortness.setOnClickListener {
            val status = !img_shortness.isSelected
            img_shortness.isSelected = status
            txt_shortness.isSelected = status
            view_border_shortness.isSelected = status
        }
    }

    override fun provideLayoutResource(): Int = R.layout.fragment_details_edit_page

    override fun onCallPermissionGranted() {
    }

    override fun onCallPermissionDenied() {
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
}