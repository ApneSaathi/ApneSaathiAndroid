package com.nitiaayog.apnesaathi.ui.fragments.details

import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.SeniorCitizenDateAdapter
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.DateItem
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenfeedbackform.SeniorCitizenFeedbackFormActivity
import com.nitiaayog.apnesaathi.utility.CALL_ID
import kotlinx.android.synthetic.main.fragment_senior_citizen_details.*
import org.threeten.bp.format.DateTimeFormatter

class SeniorCitizenDetailsFragment : BaseFragment<SeniorCitizenDetailsViewModel>(),
    SeniorCitizenDateAdapter.OnItemClickListener {

    private var adapter: SeniorCitizenDateAdapter? = null
    var callData: CallData? = null
    lateinit var grievancesList: MutableList<SrCitizenGrievance>
    override fun provideViewModel(): SeniorCitizenDetailsViewModel =
        getViewModel {
            SeniorCitizenDetailsViewModel.getInstance(dataManager)
        }

    override fun provideLayoutResource(): Int = R.layout.fragment_senior_citizen_details

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.setNavigationOnClickListener { activity?.onBackPressed() }

        initClicks()
        initRecyclerView()
        bindData()

    }

    private fun bindData() {
        if (callData?.gender == "M") {
            img_user_icon.background =
                activity?.let { ContextCompat.getDrawable(it, R.drawable.ic_male_user) }
        } else {
            img_user_icon.background =
                activity?.let { ContextCompat.getDrawable(it, R.drawable.ic_female_user) }
        }

        callData?.let {
            val address = getString(R.string.address).plus(" : ")
            val dataString = address.plus(it.block).plus(", ").plus(it.district)
                .plus(", ").plus(it.state)
            val spanAddress = SpannableString(dataString)
            spanAddress.setSpan(StyleSpan(Typeface.BOLD), 0, address.length, 0)
            spanAddress.setSpan(StyleSpan(Typeface.ITALIC), 0, address.length, 0)

            txt_user_name.text = it.srCitizenName.plus("(").plus(it.age).plus(" Yrs)")
            txt_user_phone_number.text = it.contactNumber

            txt_address.text = spanAddress
        }
        viewModel.getDataList().observe(viewLifecycleOwner, Observer {
            initAdapter()
        })
        if (grievancesList.size > 0) {
            grievancesList.let { viewModel.prepareData(it) }
            makeGrievanceContainerVisible()
            bindGrievanceData(grievancesList[grievancesList.size - 1])
        } else {
            viewModel.setCurrentDate()
            makeGrievanceContainerInvisible()
        }

    }

    private fun makeViewVisible(view: View) {
        view.visibility = View.VISIBLE
    }

    private fun makeViewInvisible(view: View) {
        view.visibility = View.GONE
    }

    private fun bindGrievanceData(srCitizenGrievance: SrCitizenGrievance) {
        var medicalHistory = ""
        if (srCitizenGrievance.hasDiabetic == "Y") {
            medicalHistory = getString(R.string.diabetes).plus(", ")
        }
        if (srCitizenGrievance.hasBloodPressure == "Y") {
            medicalHistory = getString(R.string.blood_pressure).plus(", ")
        }
        if (srCitizenGrievance.hasLungAilment == "Y") {
            medicalHistory = getString(R.string.lung_ailment).plus(", ")
        }
        if (srCitizenGrievance.cancerOrMajorSurgery == "Y") {
            medicalHistory = getString(R.string.cancer_major_surgery).plus(", ")
        }
        if (medicalHistory.isEmpty()) {
            txt_medical_history.text = getString(R.string.no_problems)
        } else {
            txt_medical_history.text = medicalHistory
        }
        txt_issue_raised_date.text = srCitizenGrievance.createdDate?.let { getFormattedDate(it) }
        txt_call_response.text = callData?.talkedWith

        txt_related_info.text = srCitizenGrievance.relatedInfoTalkedAbout?:"--"

        if (srCitizenGrievance.hasCovidSymptoms == "Y") {
            txt_covid_symptoms.text = getString(R.string.yes)
            txt_covid.visibility = View.GONE
            cl_covid_symptoms.visibility = View.VISIBLE
            if (srCitizenGrievance.hasCough == "Y") {
                makeViewVisible(txt_cough)
            } else {
                makeViewInvisible(txt_cough)
            }
            if (srCitizenGrievance.hasFever == "Y") {
                makeViewVisible(txt_fever)
            } else {
                makeViewInvisible(txt_fever)
            }
            if (srCitizenGrievance.hasShortnessOfBreath == "Y") {
                makeViewVisible(txt_shortness)
            } else {
                makeViewInvisible(txt_shortness)
            }
            if (srCitizenGrievance.hasSoreThroat == "Y") {
                makeViewVisible(txt_sore_throat)
            } else {
                makeViewInvisible(txt_sore_throat)
            }
        } else {
            txt_covid_symptoms.text = getString(R.string.no)
            cl_covid_symptoms.visibility = View.GONE
            txt_covid.visibility = View.VISIBLE
        }
        if (srCitizenGrievance.otherAilments == "Y") {
            txt_other_medical_problems.text = getString(R.string.yes)
        } else {
            txt_other_medical_problems.text = getString(R.string.no)
        }
        when (srCitizenGrievance.quarantineStatus) {
            "0" -> {
                tv_quarantine_status.text = getText(R.string.not_quarantined)
            }
            "1" -> {
                tv_quarantine_status.text = getText(R.string.home_quarantine)
            }
            "2" -> {
                tv_quarantine_status.text = getText(R.string.govt_quarantine)
            }
            else -> {
                tv_quarantine_status.text = getText(R.string.hospitalized)
            }
        }

        if (srCitizenGrievance.emergencyServiceRequired == "N") {
            txt_grievance_priority.text = getString(R.string.no)
            txt_escalation.text = getString(R.string.no)
        } else {
            txt_grievance_priority.text = getString(R.string.yes)
            txt_escalation.text = getString(R.string.yes)
        }
        if (srCitizenGrievance.lackOfEssentialServices == "Yes") {
            txt_grievance.text = getText(R.string.yes)
            var grievanceCategory: String = ""
            if (srCitizenGrievance.foodShortage != "4") {
                grievanceCategory = getString(R.string.lack_of_food).plus(", ")
            }
            if (srCitizenGrievance.medicineShortage != "4") {
                grievanceCategory =
                    grievanceCategory.plus(getString(R.string.lack_of_medicine)).plus(", ")
            }
            if (srCitizenGrievance.accessToBankingIssue != "4") {
                grievanceCategory =
                    grievanceCategory.plus(getString(R.string.lack_of_banking_service)).plus(", ")
            }
            if (srCitizenGrievance.utilitySupplyIssue != "4") {
                grievanceCategory =
                    grievanceCategory.plus(getString(R.string.lack_of_utilities)).plus(", ")
            }
            if (srCitizenGrievance.hygieneIssue != "4") {
                grievanceCategory =
                    grievanceCategory.plus(getString(R.string.lack_of_hygine)).plus(", ")
            }
            if (srCitizenGrievance.safetyIssue != "4") {
                grievanceCategory =
                    grievanceCategory.plus(getString(R.string.lack_of_safety)).plus(", ")
            }
            if (srCitizenGrievance.emergencyServiceIssue != "4") {
                grievanceCategory =
                    grievanceCategory.plus(getString(R.string.lack_of_access_emergency)).plus(", ")
            }
            if (srCitizenGrievance.phoneAndInternetIssue != "4") {
                grievanceCategory =
                    grievanceCategory.plus(getString(R.string.phone_and_service)).plus(", ")
            }
            txt_grievance_category.text = grievanceCategory
            txt_issue_raised.text = grievanceCategory
        } else {
            txt_grievance.text = getString(R.string.no)
            txt_grievance_category.text = getString(R.string.not_applicable)
            txt_issue_raised.text = getString(R.string.no_issues)
        }
        txt_grievance_desc.text = "--"
        txt_other_problem.text = srCitizenGrievance.impRemarkInfo ?: "--"

    }

    private fun getFormattedDate(date: String): String {
        val input = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val output = DateTimeFormatter.ofPattern("dd-MMM-yyyy")
        val fa = input.parse(date)
        return output.format(fa)
    }

    private fun makeGrievanceContainerInvisible() {
        txt_edit.visibility = View.VISIBLE
        cl_uneditable_container.visibility = View.GONE
        ll_status_container.visibility = View.VISIBLE
    }

    private fun makeGrievanceContainerVisible() {
        cl_uneditable_container.visibility = View.VISIBLE
        ll_status_container.visibility = View.GONE
        txt_edit.visibility = View.GONE
    }

    private fun initClicks() {
        txt_more_details.setOnClickListener {
            if (cg_more_details_group.isVisible) {
                cg_more_details_group.visibility = View.GONE
                txt_more_details.text = getString(R.string.more_details)
                txt_more_details.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    activity?.let { it1 ->
                        ContextCompat.getDrawable(
                            it1,
                            R.drawable.ic_downward_arrow
                        )
                    },
                    null
                )
                txt_more_details.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            } else {
                cg_more_details_group.visibility = View.VISIBLE
                txt_more_details.text = getString(R.string.less_details)
                txt_more_details.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    activity?.let { it1 ->
                        ContextCompat.getDrawable(
                            it1,
                            R.drawable.ic_upward_arrow
                        )
                    },
                    null
                )
                txt_more_details.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            }
        }
        img_call_button.setOnClickListener { prepareToCallPerson() }
        txt_edit.setOnClickListener {
            val intent = Intent(activity, SeniorCitizenFeedbackFormActivity::class.java)
            intent.putExtra(CALL_ID, callData?.callId)
            startActivity(intent)
        }
    }

    private fun initRecyclerView() {
        val layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rcl_call_dates.layoutManager = layoutManager
    }

    private fun initAdapter() {
        if (adapter == null) {
            adapter = SeniorCitizenDateAdapter(viewModel.getDataList().value)
            adapter?.setOnItemClickListener(this)
            rcl_call_dates.adapter = adapter
        } else {
            adapter?.notifyDataSetChanged()
        }

    }

    override fun onItemClick(position: Int, dateItem: DateItem) {
        adapter?.notifyDataSetChanged()
        if (grievancesList.size > 0) {
            bindGrievanceData(grievancesList[position])
            makeGrievanceContainerVisible()
        } else {
            makeGrievanceContainerInvisible()
        }
    }

    override fun onCallPermissionGranted() {
        callData?.let { placeCall(it, R.id.fragment_edit_container) }
    }

    override fun onCallPermissionDenied() =
        Toast.makeText(context, R.string.not_handle_action, Toast.LENGTH_LONG).show()

    private fun observeData() = viewModel.getDataObserver().observe(this, Observer {
        when (it) {
            is NetworkRequestState.NetworkNotAvailable -> {

            }
            is NetworkRequestState.LoadingData -> {

            }
            is NetworkRequestState.ErrorResponse -> {

            }
            is NetworkRequestState.SuccessResponse<*> -> {

            }
        }
    })

    fun setSelectedUser(
        callData: CallData,
        grievancesList: MutableList<SrCitizenGrievance>
    ) {
        this.grievancesList = grievancesList
        this.callData = callData
    }
}
