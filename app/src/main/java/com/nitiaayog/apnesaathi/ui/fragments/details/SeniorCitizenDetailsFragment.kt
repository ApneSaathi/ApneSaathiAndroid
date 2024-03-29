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
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.SeniorCitizenDateAdapter
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.DateItem
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenfeedbackform.SeniorCitizenFeedbackFormActivity
import com.nitiaayog.apnesaathi.utility.CALL_ID
import com.nitiaayog.apnesaathi.utility.REQUEST_CODE
import com.nitiaayog.apnesaathi.utility.ROLE_VOLUNTEER
import kotlinx.android.synthetic.main.fragment_senior_citizen_details.*
import org.threeten.bp.format.DateTimeFormatter

/**
 *  Fragment for showing the detailed view of senior citizen details. Which includes date wise filtering and a function to call them!.
 * [DateItem] is used for filtering the grievances based on date.
 * [BaseFragment] is the base fragment with functions that are common in all the fragments
 * [SeniorCitizenDetailsViewModel] is the view model for performing fetching data from API, caching it in data base and fetching the data back from database
 */
class SeniorCitizenDetailsFragment : BaseFragment<SeniorCitizenDetailsViewModel>(),
    OnItemClickListener<DateItem> {

    private var isFromHomeFragment: Boolean = false
    private var adapter: SeniorCitizenDateAdapter = SeniorCitizenDateAdapter()
    var callData: CallData? = null
    private var grievancesList: MutableList<SrCitizenGrievance> = mutableListOf()
    override fun provideViewModel(): SeniorCitizenDetailsViewModel =
        getViewModel {
            SeniorCitizenDetailsViewModel.getInstance(dataManager)
        }

    override fun provideLayoutResource(): Int = R.layout.fragment_senior_citizen_details

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.title = getString(R.string.app_bar_title)
        toolBar.setNavigationOnClickListener { activity?.onBackPressed() }

        getGrievanceData()
        initRecyclerView()
        initClicks()
    }

    /**
     * Method for fetching the grievance data from the database
     */
    private fun getGrievanceData() {
        if (isFromHomeFragment || dataManager.getRole() == ROLE_VOLUNTEER) {
            callData?.callId?.let {
                viewModel.getUniqueGrievanceList(it).removeObservers(viewLifecycleOwner)
            }
            callData?.callId?.let { it ->
                viewModel.getUniqueGrievanceList(it).observe(viewLifecycleOwner, {
                    grievancesList = it
                    bindData()
                })
            }
        } else {
            grievancesList = callData?.medicalGrievance ?: mutableListOf()
            bindData()
        }
    }

    /**
     * Method for binding the data that was fetched from the database
     */
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
            txt_status.text = "--"
            txt_address.text = spanAddress
        }
        viewModel.getDataList().observe(viewLifecycleOwner, {
            adapter.setData(it)
            adapter.setOnItemClickListener(this)
            rcl_call_dates.layoutManager?.scrollToPosition(adapter.selectedPos)
        })
        if (isFromHomeFragment || dataManager.getRole() == ROLE_VOLUNTEER) {
            img_call_button.visibility = View.VISIBLE
        } else {
            img_call_button.visibility = View.GONE
        }
        if (grievancesList.size > 0) {
            viewModel.prepareData(grievancesList)
            makeGrievanceContainerVisible()
            bindGrievanceData(grievancesList[grievancesList.size - 1])
        } else {
            viewModel.setCurrentDate()
            makeGrievanceContainerInvisible()
        }

    }

    /**
     * Method for making the view visible
     * [view] is the view to be made visible
     */
    private fun makeViewVisible(view: View) {
        view.visibility = View.VISIBLE
    }

    /**
     * Method for making the view invisible
     * [view] is the view to be made invisible
     */
    private fun makeViewInvisible(view: View) {
        view.visibility = View.GONE
    }

    /**
     * Method for binding the grievance data into the view
     * [srCitizenGrievance] is data that was selected by the user by clicking the date
     */
    private fun bindGrievanceData(srCitizenGrievance: SrCitizenGrievance) {
        var medicalHistory = ""
        if (srCitizenGrievance.hasDiabetic == "1") {
            medicalHistory += getString(R.string.diabetes).plus(", ")
        }
        if (srCitizenGrievance.hasBloodPressure == "1") {
            medicalHistory += getString(R.string.blood_pressure).plus(", ")
        }
        if (srCitizenGrievance.hasLungAilment == "1") {
            medicalHistory += getString(R.string.lung_ailment).plus(", ")
        }
        if (srCitizenGrievance.cancerOrMajorSurgery == "1") {
            medicalHistory += getString(R.string.cancer_major_surgery)
        }
        if (medicalHistory.isEmpty()) {
            txt_medical_history.text = getString(R.string.no_problems)
        } else {
            txt_medical_history.text = medicalHistory
        }
        callData?.talkedWith.let {
            when (it) {
                "1" -> {
                    txt_call_response.text = getString(R.string.sr_citizen)
                }
                "2" -> {
                    txt_call_response.text = getString(R.string.family_member_of_sr_citizen)
                }
                "3" -> {
                    txt_call_response.text = getString(R.string.community_member)
                }
            }
        }
        if (srCitizenGrievance.hasSrCitizenAwareOfCovid19 == "1") {
            tv_awareness.text = getString(R.string.yes)
            tv_reinforce_with_knowledge.visibility = View.GONE
            tv_measures_for_prevention.visibility = View.GONE
        } else {
            tv_awareness.text = getString(R.string.no)
            tv_reinforce_with_knowledge.visibility = View.VISIBLE
            tv_measures_for_prevention.visibility = View.VISIBLE
            if (srCitizenGrievance.hasSymptomsPreventionDiscussed == "1") {
                tv_measures_for_prevention.text = getString(R.string.yes)
            } else {
                tv_measures_for_prevention.text = getString(R.string.no)
            }
        }

        if (srCitizenGrievance.behavioralChangesNoticed == getString(R.string.no)
            || srCitizenGrievance.behavioralChangesNoticed == getString(R.string.may_be)
        ) {
            tv_title_behavior_change.visibility = View.VISIBLE
            tv_practices_not_followed.visibility = View.VISIBLE
            tv_practices_not_followed.text = srCitizenGrievance.whichPracticesNotFollowed ?: "--"
        } else {
            tv_title_behavior_change.visibility = View.GONE
            tv_practices_not_followed.visibility = View.GONE
        }
        var talkedAboutText = ""
        if (srCitizenGrievance.relatedInfoTalkedAbout != null) {
            val result =
                srCitizenGrievance.relatedInfoTalkedAbout!!.split(",").map { it.trim() }
            result.forEach {
                when (it) {
                    "1" -> {
                        talkedAboutText += getString(R.string.prevention).plus(",")
                    }
                    "2" -> {
                        talkedAboutText += getString(R.string.access).plus(",")
                    }
                    "3" -> {
                        talkedAboutText += getString(R.string.detection)
                    }
                }
            }
        }
        txt_related_info.text = talkedAboutText

        if (srCitizenGrievance.hasCovidSymptoms == "1") {
            txt_covid_symptoms.text = getString(R.string.yes)
            txt_covid.visibility = View.GONE
            cl_covid_symptoms.visibility = View.VISIBLE
            if (srCitizenGrievance.hasCough == "1") {
                makeViewVisible(txt_cough)
            } else {
                makeViewInvisible(txt_cough)
            }
            if (srCitizenGrievance.hasFever == "1") {
                makeViewVisible(txt_fever)
            } else {
                makeViewInvisible(txt_fever)
            }
            if (srCitizenGrievance.hasShortnessOfBreath == "1") {
                makeViewVisible(txt_shortness)
            } else {
                makeViewInvisible(txt_shortness)
            }
            if (srCitizenGrievance.hasSoreThroat == "1") {
                makeViewVisible(txt_sore_throat)
            } else {
                makeViewInvisible(txt_sore_throat)
            }
        } else {
            txt_covid_symptoms.text = getString(R.string.no)
            cl_covid_symptoms.visibility = View.GONE
            txt_covid.visibility = View.VISIBLE
        }
        if (srCitizenGrievance.otherAilments == "1") {
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
            "3" -> {
                tv_quarantine_status.text = getText(R.string.hospitalized)
            }
            else -> {
                tv_quarantine_status.text = getText(R.string.no_problems)
            }
        }

        if (srCitizenGrievance.emergencyServiceRequired == "2") {
            txt_grievance_priority.text = getString(R.string.no)
            txt_escalation.text = "--"
            txt_status.text = getString(R.string.regular)
        } else {
            txt_grievance_priority.text = getString(R.string.yes)
            txt_escalation.text = getString(R.string.yes)
            txt_status.text = getString(R.string.emergency)
        }
        if (srCitizenGrievance.lackOfEssentialServices == "1") {
            txt_issue_raised_date.text =
                srCitizenGrievance.createdDate?.let { getFormattedDate(it) }
            txt_grievance.text = getText(R.string.yes)
            var grievanceCategory = ""
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
            txt_issue_raised_date.text = "--"
        }
        txt_grievance_desc.text = srCitizenGrievance.description
        txt_other_problem.text = srCitizenGrievance.impRemarkInfo ?: "--"

    }

    /**
     * Method for getting the call status code
     * [status] is the string variable for which we require the status codde
     */
    private fun getCallStatusFromCode(status: String?): String {
        var callStatus = ""
        when (status) {
            "1" -> {
                callStatus = getString(R.string.pending)
            }
            "2" -> {
                callStatus = getString(R.string.not_picked_single_line)
            }
            "3" -> {
                callStatus = getString(R.string.not_reachable_single_line)
            }
            "4" -> {
                callStatus = getString(R.string.number_busy)
            }
            "5" -> {
                callStatus = getString(R.string.call_later)
            }
            "6" -> {
                callStatus = getString(R.string.call_dropped)
            }
            "7" -> {
                callStatus = getString(R.string.wrong_number)
            }
            "8" -> {
                callStatus = getString(R.string.number_not_existing)
            }
            "9" -> {
                callStatus = getString(R.string.dis_connected)
            }
            "10" -> {
                callStatus = getString(R.string.connected)
            }
        }
        return callStatus
    }

    /**
     * Method for getting the formatted date
     * [date] is a date which is required to be converted in the required format
     */
    private fun getFormattedDate(date: String): String {
        val input = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val output = DateTimeFormatter.ofPattern("dd-MMM-yyyy")
        val fa = input.parse(date)
        return output.format(fa)
    }

    /**
     * Method for making the grievance container invisible. This is required when there are no grievance to show.
     */
    private fun makeGrievanceContainerInvisible() {
        if (isFromHomeFragment || dataManager.getRole() == ROLE_VOLUNTEER)
            txt_edit.visibility = View.VISIBLE
        else
            txt_edit.visibility = View.GONE

        cl_uneditable_container.visibility = View.GONE
        ll_status_container.visibility = View.VISIBLE
        val status = getCallStatusFromCode(callData?.callStatusCode)
        if (status.isNotEmpty())
            tv_call_status_new.text = status
        else {
            tv_call_status_new.text = getString(R.string.yet_to_be_called)
        }
    }

    /**
     * Method for making the grievance container visible. This is required when there are grievances to show.
     */
    private fun makeGrievanceContainerVisible() {
        cl_uneditable_container.visibility = View.VISIBLE
        ll_status_container.visibility = View.GONE
        txt_edit.visibility = View.GONE
    }

    /**
     * Method for initializing the clicks in this page
     */
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
            dataManager.setUserName(callData?.srCitizenName ?: "")
            dataManager.setSrCitizenGender(callData?.gender ?: "")
            intent.putExtra(CALL_ID, callData?.callId)
            activity!!.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    /**
     * Method for initialing the recycler view
     */
    private fun initRecyclerView() {
        val layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rcl_call_dates.layoutManager = layoutManager
        rcl_call_dates.adapter = adapter
    }


    override fun onItemClick(position: Int, data: DateItem) {
        adapter.notifyDataSetChanged()
        if (grievancesList.size > 0) {
            bindGrievanceData(grievancesList[position])
            makeGrievanceContainerVisible()
        } else {
            makeGrievanceContainerInvisible()
        }
    }

    override fun onCallPermissionGranted() {
        callData?.let { placeCall(it) }// , R.id.fragment_edit_container
    }

    override fun onCallPermissionDenied() =
        Toast.makeText(context, R.string.not_handle_action, Toast.LENGTH_LONG).show()

    /**
     * Method for setting the selected senior citizen data
     * [callData] is the actual senior citizen data
     * [isFromHomeFragment] is a boolean for checking if the navigation happened from home fragment
     */
    fun setSelectedUser(
        callData: CallData, isFromHomeFragment: Boolean = false
    ) {
        this.callData = callData
        this.isFromHomeFragment = isFromHomeFragment
    }

    /**
     * Method for reloading the page
     * [callData] is the data to be reloaded.
     */
    fun reloadFragment(callData: CallData) {
        this.callData = callData
        getGrievanceData()
    }
}