package com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenfeedbackform

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.jakewharton.rxbinding3.widget.textChanges
import com.nitiaayog.apnesaathi.ApneSaathiApplication
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.BaseArrayAdapter
import com.nitiaayog.apnesaathi.adapter.PopupAdapter
import com.nitiaayog.apnesaathi.adapter.SimpleBaseAdapter
import com.nitiaayog.apnesaathi.base.ProgressDialog
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.base.ui
import com.nitiaayog.apnesaathi.model.FormElements
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.model.SyncSrCitizenGrievance
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.CALL_ID
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_senior_citizen_feedback_form.*
import kotlinx.android.synthetic.main.include_recyclerview.view.*
import kotlinx.android.synthetic.main.include_register_new_sr_citizen.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class SeniorCitizenFeedbackFormActivity : BaseActivity<SeniorCitizenFeedbackViewModel>() {

    private var selectedCallStatusButton: MaterialButton? = null
    private var selectedLackOfEssentialServices: MaterialButton? = null
    private var selectedNeedOfEmergencyServices: MaterialButton? = null

    private val selectedColor: Int by lazy { ContextCompat.getColor(this, R.color.color_orange) }
    private val normalColor: Int by lazy {
        ContextCompat.getColor(this, R.color.color_dark_grey_txt)
    }

    private val progressDialog: ProgressDialog.Builder by lazy {
        ProgressDialog.Builder(this).setMessage(R.string.wait_saving_data)
    }

    private var callId: String = ""

    private val syncData by lazy { SyncSrCitizenGrievance() }

    // List Medical History
    private val popupMedicalHistoryList: MutableList<FormElements> = mutableListOf()
    private val popupMedicalHistorySrCitizenAdapter: PopupAdapter by lazy {
        PopupAdapter(popupMedicalHistoryList).apply {
            this.setOnItemClickListener(
                object : PopupAdapter.OnItemClickListener {
                    override fun onItemClicked(position: Int, element: FormElements) {
                        if (element.isSelected) {
                            rvMedicalHistorySrCitizenAdapter.addItem(element.name)
                            viewModel.addMedicalHistory(element.name)
                        } else {
                            rvMedicalHistorySrCitizenAdapter.removeItem(element.name)
                            viewModel.removeMedicalHistory(element.name)
                        }

                        rvMedicalHistorySrCitizen.visibility =
                            if (rvMedicalHistorySrCitizenAdapter.itemCount > 0) View.VISIBLE
                            else View.GONE
                    }
                })
        }
    }
    private val rvMedicalHistorySrCitizenAdapter: SimpleBaseAdapter by lazy {
        SimpleBaseAdapter().apply {
            this.setOnItemClickListener(object : SimpleBaseAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, name: String) {
                    popupMedicalHistorySrCitizenAdapter.updateItem(name)
                    viewModel.removeMedicalHistory(name)
                    rvMedicalHistorySrCitizen.visibility =
                        if (rvMedicalHistorySrCitizenAdapter.itemCount > 0) View.VISIBLE
                        else View.GONE
                }

                override fun showPopup() = showPopupWindow(llMedicalHistorySrCitizen)
            })
        }
    }

    // List Category
    private val popupCategoryList: MutableList<FormElements> = mutableListOf()
    private val popupCategoryAdapter: PopupAdapter by lazy {
        PopupAdapter(popupCategoryList).apply {
            this.setOnItemClickListener(
                object : PopupAdapter.OnItemClickListener {
                    override fun onItemClicked(position: Int, element: FormElements) {
                        if (element.isSelected) {
                            rvCategoryAdapter.addItem(element.name)
                            viewModel.addComplaint(element.name)
                        } else {
                            rvCategoryAdapter.removeItem(element.name)
                            viewModel.removeComplaint(element.name)
                        }

                        rvCategory.visibility = if (rvCategoryAdapter.itemCount > 0) View.VISIBLE
                        else View.GONE
                    }
                })
        }
    }
    private val rvCategoryAdapter: SimpleBaseAdapter by lazy {
        SimpleBaseAdapter().apply {
            this.setOnItemClickListener(object : SimpleBaseAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, name: String) {
                    popupCategoryAdapter.updateItem(name)
                    viewModel.removeComplaint(name)
                    rvCategory.visibility = if (rvCategoryAdapter.itemCount > 0) View.VISIBLE
                    else View.GONE
                }

                override fun showPopup() {
                    showPopupWindow(flCategory)
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolBar)
        supportActionBar?.let {
            it.setTitle(R.string.sr_citizen_follow_up)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_back)
        }
        toolBar.setNavigationOnClickListener { finish() }

        ivCall.visibility = View.GONE
        tvCancel.visibility = View.GONE
        tvRegister.visibility = View.GONE

        resources.getStringArray(R.array.medical_problems).forEach {
            popupMedicalHistoryList.add(FormElements(it))
        }
        resources.getStringArray(R.array.grievance_array).forEach {
            popupCategoryList.add(FormElements(it))
        }

        observeData()

        Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                setData()
                initAutoCompleteTextViews()
                initClicks()
            }.autoDispose(disposables)
    }

    override fun provideViewModel(): SeniorCitizenFeedbackViewModel =
        getViewModel { SeniorCitizenFeedbackViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.activity_senior_citizen_feedback_form

    private fun setData() {
        intent?.let { intent ->
            val callId: Int = intent.getIntExtra(CALL_ID, -1)
            this.callId = callId.toString()
            if (callId != -1) {
                CoroutineScope(Dispatchers.IO).launch {
                    val callData = viewModel.getCallDetailFromId(callId)
                    val grievances = viewModel.getGrievance(callId)
                    syncData.id = grievances?.id
                    ui {
                        civGender.setImageResource(
                            if (callData.gender == "M") R.drawable.ic_male_user
                            else R.drawable.ic_female_user
                        )

                        val address = getString(R.string.address).plus(" : ")
                        val dataString = address.plus(callData.block).plus(", ")
                            .plus(callData.district).plus(", ").plus(callData.state)
                        val spanAddress = SpannableString(dataString)
                        spanAddress.setSpan(StyleSpan(Typeface.BOLD), 0, address.length, 0)
                        spanAddress.setSpan(StyleSpan(Typeface.ITALIC), 0, address.length, 0)

                        tvSrCitizenName.text = callData.srCitizenName.plus("(").plus(callData.age)
                            .plus(" Yrs)")
                        tvSrCitizenPhoneNumber.text = callData.contactNumber
                        tvSrCitizenAddress.text = spanAddress

                        setCallStatus(callData.callStatusSubCode!!)
                        setTalkedWith(callData.talkedWith!!)

                        if (callData.callStatusSubCode == "5") {
                            grievances?.run {
                                setCheckForMedicalHistoryData(this)
                                setRelatedInfoTalkedAboutData(this.relatedInfoTalkedAbout!!)
                                setBehavioralChangesData(this.behavioralChangesNoticed!!)
                                setCovidData(this)
                                setComplaintData(this)
                                etOtherDescription.setText(this.impRemarkInfo)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkData(dataString: String, element: String, compareString: String): Boolean =
        (dataString.toLowerCase(Locale.getDefault()) == "y") && (element == compareString)

    private fun checkComplainData(dataString: String, element: String, compareString: String):
            Boolean = (dataString == "0") && (element == compareString)

    private fun setCallStatus(callStatus: String) = when (callStatus) {
        "1" -> {
            changeButtonSelection(btnNoResponse)
            viewModel.setCallStatus("1")
        }
        "2" -> {
            changeButtonSelection(btnNotPicked)
            viewModel.setCallStatus("2")
        }
        "3" -> {
            changeButtonSelection(btnNotReachable)
            viewModel.setCallStatus("3")
        }
        "4" -> {
            changeButtonSelection(btnDisConnected)
            viewModel.setCallStatus("4")
        }
        "5" -> {
            changeButtonSelection(btnConnected)
            viewModel.setCallStatus("5")
            tvTalkWith.visibility = View.VISIBLE
            actTalkWith.visibility = View.VISIBLE
        }
        else -> {
        }
    }

    private fun setTalkedWith(talkedWith: String) {
        if ((talkedWith == getString(R.string.sr_citizen)) || (talkedWith.toLowerCase(Locale.getDefault()) == "s")) {
            actTalkWith.setText(R.string.sr_citizen)
            viewModel.setTalkedWith(getString(R.string.sr_citizen))
            cgMedicalDetails.visibility = View.VISIBLE
        } else if ((talkedWith == getString(R.string.family_member_of_sr_citizen)) ||
            (talkedWith.toLowerCase(Locale.getDefault()) == "f")
        ) {
            actTalkWith.setText(R.string.family_member_of_sr_citizen)
            viewModel.setTalkedWith(getString(R.string.family_member_of_sr_citizen))
            cgMedicalDetails.visibility = View.VISIBLE
        } else if ((talkedWith == getString(R.string.sr_citizen)) || (talkedWith.toLowerCase(Locale.getDefault()) == "c")) {
            actTalkWith.setText(R.string.community_member)
            viewModel.setTalkedWith(getString(R.string.community_member))
            tvAnySrCitizenInHome.visibility = View.VISIBLE
            btnAnySrCitizenInHomeYes.visibility = View.VISIBLE
            btnAnySrCitizenInHomeNo.visibility = View.VISIBLE
        }
    }

    private fun setCheckForMedicalHistoryData(grievance: SrCitizenGrievance) {
        popupMedicalHistoryList.forEach { element ->
            if (checkData(grievance.hasDiabetic!!, element.name, getString(R.string.diabetes))) {
                element.isSelected = true
                rvMedicalHistorySrCitizenAdapter.addItem(element.name)
            }

            if (checkData(
                    grievance.hasBloodPressure!!, element.name, getString(R.string.blood_pressure)
                )
            ) {
                element.isSelected = true
                rvMedicalHistorySrCitizenAdapter.addItem(element.name)
            }

            if (checkData(
                    grievance.hasLungAilment!!, element.name, getString(R.string.lung_ailment)
                )
            ) {
                element.isSelected = true
                rvMedicalHistorySrCitizenAdapter.addItem(element.name)
            }

            if (checkData(
                    grievance.cancerOrMajorSurgery!!, element.name,
                    getString(R.string.cancer_major_surgery)
                )
            ) {
                element.isSelected = true
                rvMedicalHistorySrCitizenAdapter.addItem(element.name)
            }
        }
        val selectedItems = popupMedicalHistoryList.filter { it.isSelected }.map {
            viewModel.addMedicalHistory(it.name)
        }
        if (selectedItems.isNotEmpty()) {
            //if (cgMedicalDetails.visibility == View.GONE) cgMedicalDetails.visibility = View.VISIBLE
            rvMedicalHistorySrCitizenAdapter.notifyDataSetChanged()
            rvMedicalHistorySrCitizen.visibility = View.VISIBLE
        }
    }

    private fun setRelatedInfoTalkedAboutData(relatedInfo: String) {
        if (relatedInfo.toLowerCase(Locale.getDefault())
                .contains(getString(R.string.prevention).toLowerCase(Locale.getDefault()))
        ) changeButtonSelectionWithIcon(btnPrevention)

        if (relatedInfo.toLowerCase(Locale.getDefault())
                .contains(getString(R.string.access).toLowerCase(Locale.getDefault()))
        ) changeButtonSelectionWithIcon(btnAccess)

        if (relatedInfo.toLowerCase(Locale.getDefault())
                .contains(getString(R.string.detection).toLowerCase(Locale.getDefault()))
        ) changeButtonSelectionWithIcon(btnDetection)

        checkTalkedAbout()
        //if (cgMedicalDetails.visibility == View.GONE) cgMedicalDetails.visibility = View.VISIBLE
    }

    private fun setBehavioralChangesData(behavioralChange: String) {
        viewModel.setBehaviorChange(
            when (behavioralChange.toLowerCase(Locale.getDefault())) {
                getString(R.string.yes).toLowerCase(Locale.getDefault()), "y" ->
                    getString(R.string.yes)
                getString(R.string.no).toLowerCase(Locale.getDefault()), "n" ->
                    getString(R.string.no)
                getString(R.string.may_be).toLowerCase(Locale.getDefault()), "m" ->
                    getString(R.string.may_be)
                else -> getString(R.string.not_applicable)
            }
        )
        actBehaviorChange.setText(viewModel.getBehaviorChange())
        //if (cgMedicalDetails.visibility == View.GONE) cgMedicalDetails.visibility = View.VISIBLE
    }

    private fun setCovidData(grievance: SrCitizenGrievance) {
        if (grievance.hasCough!!.toLowerCase(Locale.getDefault()) == "y") {
            changeCovidSymptomsSelection(ivCough, tvCough, ivDoneCough)
            viewModel.isCoughSymptoms(true)
        }
        if (grievance.hasFever!!.toLowerCase(Locale.getDefault()) == "y") {
            changeCovidSymptomsSelection(ivFever, tvFever, ivDoneFever)
            viewModel.isFeverSymptom(true)
        }
        if (grievance.hasShortnessOfBreath!!.toLowerCase(Locale.getDefault()) == "y") {
            changeCovidSymptomsSelection(
                ivShortnessOfBreath, tvShortnessOfBreath, ivDoneShortnessOfBreath
            )
            viewModel.isShortBreathSymptoms(true)
        }
        if (grievance.hasSoreThroat!!.toLowerCase(Locale.getDefault()) == "y") {
            changeCovidSymptomsSelection(ivSoreThroat, tvSoreThroat, ivDoneSoreThroat)
            viewModel.isSoreThroatSymptom(true)
        }

        if ((grievance.hasCough!!.toLowerCase(Locale.getDefault()) == "y") ||
            (grievance.hasFever!!.toLowerCase(Locale.getDefault()) == "y") ||
            (grievance.hasShortnessOfBreath!!.toLowerCase(Locale.getDefault()) == "y") ||
            (grievance.hasSoreThroat!!.toLowerCase(Locale.getDefault()) == "y")
        ) {
            viewModel.setOtherMedicalProblem(getString(R.string.covid_19_symptoms))
            viewModel.isCovideSymptoms(true)

            actOtherMedicalProblems.listSelection = 0
            actOtherMedicalProblems.setText(viewModel.getOtherMedicalProblem())
            actOtherMedicalProblems.visibility = View.VISIBLE

            tvCovidSymptoms.visibility = View.VISIBLE
            hsvCovidSymptoms.visibility = View.VISIBLE

            viewModel.setQuarantineStatus(grievance.quarantineStatus!!)
            actQuarantineHospitalizationStatus.setText(
                when (viewModel.getQuarantineStatus()) {
                    "1" -> R.string.home_quarantine
                    "2" -> R.string.government_quarantine
                    "3" -> R.string.hospitalized
                    "0" -> R.string.not_quarantined
                    else -> R.string.status
                }
            )
        }
        //if (cgMedicalDetails.visibility == View.GONE) cgMedicalDetails.visibility = View.VISIBLE
    }

    private fun setComplaintData(grievance: SrCitizenGrievance) {
        selectedLackOfEssentialServices =
            if (grievance.emergencyServiceRequired!!.toLowerCase(Locale.getDefault()) == "y") {
                toggleButtonSelection(
                    btnLackOfEssentialServicesYes, selectedLackOfEssentialServices
                )
            } else toggleButtonSelection(
                btnLackOfEssentialServicesNo, selectedLackOfEssentialServices
            )
        viewModel.isEmergencyEscalation(grievance.emergencyServiceRequired!!.toUpperCase(Locale.getDefault()))

        popupCategoryList.forEach {
            if (checkComplainData(
                    grievance.foodShortage!!, it.name, getString(R.string.lack_of_food)
                )
            ) {
                it.isSelected = true
                rvCategoryAdapter.addItem(it.name)
            }
            if (checkComplainData(
                    grievance.medicineShortage!!, it.name, getString(R.string.lack_of_medicine)
                )
            ) {
                it.isSelected = true
                rvCategoryAdapter.addItem(it.name)
            }
            if (checkComplainData(
                    grievance.accessToBankingIssue!!, it.name,
                    getString(R.string.lack_of_banking_service)
                )
            ) {
                it.isSelected = true
                rvCategoryAdapter.addItem(it.name)
            }
            if (checkComplainData(
                    grievance.utilitySupplyIssue!!, it.name, getString(R.string.lack_of_utilities)
                )
            ) {
                it.isSelected = true
                rvCategoryAdapter.addItem(it.name)
            }
            if (checkComplainData(
                    grievance.hygieneIssue!!, it.name, getString(R.string.lack_of_hygine)
                )
            ) {
                it.isSelected = true
                rvCategoryAdapter.addItem(it.name)
            }
            if (checkComplainData(
                    grievance.safetyIssue!!, it.name, getString(R.string.lack_of_safety)
                )
            ) {
                it.isSelected = true
                rvCategoryAdapter.addItem(it.name)
            }
            if (checkComplainData(
                    grievance.emergencyServiceIssue!!, it.name,
                    getString(R.string.lack_of_access_emergency)
                )
            ) {
                it.isSelected = true
                rvCategoryAdapter.addItem(it.name)
            }
            if (checkComplainData(
                    grievance.phoneAndInternetIssue!!, it.name,
                    getString(R.string.phone_and_service)
                )
            ) {
                it.isSelected = true
                rvCategoryAdapter.addItem(it.name)
            }
        }

        selectedNeedOfEmergencyServices = if (grievance.emergencyServiceRequired == "Y") {
            toggleButtonSelection(btnEmergencyEscalationYes, selectedNeedOfEmergencyServices)
        } else toggleButtonSelection(btnEmergencyEscalationNo, selectedNeedOfEmergencyServices)
        viewModel.isEmergencyEscalation(grievance.emergencyServiceRequired!!)

        val selectedItems = popupCategoryList.filter { it.isSelected }.map {
            viewModel.addComplaint(it.name)
        }
        if (selectedItems.isNotEmpty() || (grievance.emergencyServiceIssue!! == "0") ||
            (grievance.emergencyServiceRequired!! == "Y")
        ) {
            actOtherMedicalProblems.setText(getString(R.string.non_covid_symptoms))
            viewModel.setOtherMedicalProblem(getString(R.string.non_covid_symptoms))
            viewModel.isCovideSymptoms(false)

            cgMedicalDetails.visibility = View.VISIBLE

            if (cgComplaintDetails.visibility == View.GONE)
                cgComplaintDetails.visibility = View.VISIBLE
            rvCategoryAdapter.notifyDataSetChanged()
            rvCategory.visibility = View.VISIBLE
        }
    }

    private fun initAutoCompleteTextViews() {
        val genderList = resources.getStringArray(R.array.gender_array)
        val gendersAdapter = BaseArrayAdapter(this, R.layout.item_layout_dropdown_menu, genderList)
        actGender.threshold = 0
        actGender.setAdapter(gendersAdapter)
        actGender.setOnKeyListener(null)
        actGender.setOnItemClickListener { _, _, position, _ ->
            viewModel.setGender(
                when (genderList[position]) {
                    getString(R.string.gender_male) -> "M"
                    getString(R.string.gender_female) -> "F"
                    getString(R.string.gender_others) -> "O"
                    else -> ""
                }
            )
            if (tvGenderError.visibility == View.VISIBLE) tvGenderError.visibility = View.GONE
        }

        val districtsList = resources.getStringArray(R.array.districts_array)
        val districtsAdapter =
            BaseArrayAdapter(this, R.layout.item_layout_dropdown_menu, districtsList)
        actDistrict.threshold = 0
        actDistrict.setAdapter(districtsAdapter)
        actDistrict.setOnKeyListener(null)
        actDistrict.setOnItemClickListener { _, _, position, _ ->
            viewModel.setDistrict(districtsList[position])
            if (tvDistrictError.visibility == View.VISIBLE) tvDistrictError.visibility = View.GONE
        }

        val stateList = resources.getStringArray(R.array.states_array)
        val stateAdapter = BaseArrayAdapter(this, R.layout.item_layout_dropdown_menu, stateList)
        actState.threshold = 0
        actState.setAdapter(stateAdapter)
        actState.setOnKeyListener(null)
        actState.setOnItemClickListener { _, _, position, _ ->
            viewModel.setState(stateList[position])
            if (tvStateError.visibility == View.VISIBLE) tvStateError.visibility = View.GONE
        }

        val rvMedicalHistorySrCitizen = rvMedicalHistorySrCitizen as RecyclerView
        (rvMedicalHistorySrCitizen.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.HORIZONTAL
        rvMedicalHistorySrCitizen.adapter = rvMedicalHistorySrCitizenAdapter

        val rvCategory = rvCategory as RecyclerView
        (rvCategory.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.HORIZONTAL
        rvCategory.adapter = rvCategoryAdapter

        val talkedWithList = resources.getStringArray(R.array.talked_with)
        val statesAdapter =
            BaseArrayAdapter(this, R.layout.item_layout_dropdown_menu, talkedWithList)
        actTalkWith.threshold = 0
        actTalkWith.setAdapter(statesAdapter)
        actTalkWith.setOnKeyListener(null)
        actTalkWith.setOnDismissListener {
            updateDropDownIndicator(actTalkWith, R.drawable.ic_arrow_down)
        }
        actTalkWith.setOnItemClickListener { _, _, position, _ ->
            if (viewModel.getTalkedWith() == getString(R.string.community_member)) {
                resetRegisterNewSrCitizenLayout()
                resetAllFormElements()
            }
            viewModel.setTalkedWith(talkedWithList[position])
            manageViewVisibility()
        }

        val behaviorChangesList = resources.getStringArray(R.array.behavior_changes)
        val behaviorChangesAdapter =
            BaseArrayAdapter(this, R.layout.item_layout_dropdown_menu, behaviorChangesList)
        actBehaviorChange.threshold = 0
        actBehaviorChange.setAdapter(behaviorChangesAdapter)
        actBehaviorChange.setOnKeyListener(null)
        actBehaviorChange.setOnDismissListener {
            updateDropDownIndicator(actBehaviorChange, R.drawable.ic_arrow_down)
        }
        actBehaviorChange.setOnItemClickListener { _, _, position, _ ->
            viewModel.setBehaviorChange(behaviorChangesList[position])
        }

        val otherMedicalProblemsList = resources.getStringArray(R.array.other_medical_problems_list)
        val otherMedicalProblemsAdapter =
            BaseArrayAdapter(this, R.layout.item_layout_dropdown_menu, otherMedicalProblemsList)
        actOtherMedicalProblems.threshold = 0
        actOtherMedicalProblems.setAdapter(otherMedicalProblemsAdapter)
        actOtherMedicalProblems.setOnKeyListener(null)
        actOtherMedicalProblems.setOnDismissListener {
            updateDropDownIndicator(actOtherMedicalProblems, R.drawable.ic_arrow_down)
        }
        actOtherMedicalProblems.setOnItemClickListener { _, _, position, _ ->
            viewModel.setOtherMedicalProblem(otherMedicalProblemsList[position])
            viewModel.isCovideSymptoms(otherMedicalProblemsList[position] == getString(R.string.covid_19_symptoms))
            manageOtherMedicalRelatedViews()
        }

        val quarantineStatusList = resources.getStringArray(R.array.quarantine_status_array)
        val quarantineStatusAdapter =
            BaseArrayAdapter(this, R.layout.item_layout_dropdown_menu, quarantineStatusList)
        actQuarantineHospitalizationStatus.threshold = 0
        actQuarantineHospitalizationStatus.setAdapter(quarantineStatusAdapter)
        actQuarantineHospitalizationStatus.setOnKeyListener(null)
        actQuarantineHospitalizationStatus.setOnDismissListener {
            updateDropDownIndicator(actQuarantineHospitalizationStatus, R.drawable.ic_arrow_down)
        }
        actQuarantineHospitalizationStatus.setOnItemClickListener { _, _, position, _ ->
            viewModel.setQuarantineStatus(
                if (position == (quarantineStatusAdapter.count - 1)) "0" else (position + 1).toString()
            )
        }
    }

    private fun initClicks() {
        // Call Status Button Clicks
        btnNoResponse.throttleClick().subscribe {
            manageResetForm(btnNoResponse)
            viewModel.setCallStatus("3")
        }.autoDispose(disposables)
        btnNotPicked.throttleClick().subscribe {
            manageResetForm(btnNotPicked)
            viewModel.setCallStatus("3")
        }.autoDispose(disposables)
        btnNotReachable.throttleClick().subscribe {
            manageResetForm(btnNotReachable)
            viewModel.setCallStatus("3")
        }.autoDispose(disposables)
        btnDisConnected.throttleClick().subscribe {
            manageResetForm(btnDisConnected)
            viewModel.setCallStatus("4")
        }.autoDispose(disposables)
        btnConnected.throttleClick().subscribe {
            changeButtonSelection(btnConnected)
            viewModel.setCallStatus("5")
            tvTalkWith.visibility = View.VISIBLE
            actTalkWith.visibility = View.VISIBLE
        }.autoDispose(disposables)

        // Is Sr citizen at home
        btnAnySrCitizenInHomeYes.throttleClick().subscribe {
            viewModel.isSeniorCitizenAtHome(true)
            toggleSelectionForSrCitizenAtHome(btnAnySrCitizenInHomeYes, btnAnySrCitizenInHomeNo)
            tvNewSrCitizenPersonalDetails.visibility = View.VISIBLE
            registerNewSrCitizen.visibility = View.VISIBLE
        }.autoDispose(disposables)
        btnAnySrCitizenInHomeNo.throttleClick().subscribe {
            viewModel.isSeniorCitizenAtHome(false)
            toggleSelectionForSrCitizenAtHome(btnAnySrCitizenInHomeNo, btnAnySrCitizenInHomeYes)
            tvNewSrCitizenPersonalDetails.visibility = View.GONE
            registerNewSrCitizen.visibility = View.GONE
        }.autoDispose(disposables)

        btnLessMoreDetails.throttleClick().subscribe {
            flGrievances.visibility = if (flGrievances.visibility == View.VISIBLE) {
                tvGrievances.visibility = View.GONE
                btnLessMoreDetails.icon =
                    ContextCompat.getDrawable(this, R.drawable.ic_downward_arrow)
                View.GONE
            } else {
                tvGrievances.visibility = View.VISIBLE
                btnLessMoreDetails.icon =
                    ContextCompat.getDrawable(this, R.drawable.ic_upward_arrow)
                View.VISIBLE
            }
        }.autoDispose(disposables)

        llMedicalHistorySrCitizen.throttleClick().subscribe {
            showPopupWindow(llMedicalHistorySrCitizen)
        }.autoDispose(disposables)
        flCategory.throttleClick().subscribe { showPopupWindow(flCategory) }
            .autoDispose(disposables)

        // All AutoCompleteTextView clicks
        actGender.throttleClick().subscribe { actGender.showDropDown() }.autoDispose(disposables)
        actDistrict.throttleClick().subscribe { actDistrict.showDropDown() }
            .autoDispose(disposables)
        actState.throttleClick().subscribe { actState.showDropDown() }.autoDispose(disposables)
        actTalkWith.throttleClick().subscribe {
            actTalkWith.showDropDown()
            updateDropDownIndicator(actTalkWith, R.drawable.ic_arrow_up)
        }.autoDispose(disposables)
        actOtherMedicalProblems.throttleClick().subscribe {
            actOtherMedicalProblems.showDropDown()
            updateDropDownIndicator(actOtherMedicalProblems, R.drawable.ic_arrow_up)
        }.autoDispose(disposables)
        actBehaviorChange.throttleClick().subscribe {
            actBehaviorChange.showDropDown()
            updateDropDownIndicator(actBehaviorChange, R.drawable.ic_arrow_up)
        }.autoDispose(disposables)
        actQuarantineHospitalizationStatus.throttleClick().subscribe {
            actQuarantineHospitalizationStatus.showDropDown()
            updateDropDownIndicator(actQuarantineHospitalizationStatus, R.drawable.ic_arrow_up)
        }.autoDispose(disposables)

        // Had discussion about Prevention/Access/Detection
        btnPrevention.throttleClick().subscribe { changeButtonSelectionWithIcon(btnPrevention) }
            .autoDispose(disposables)
        btnAccess.throttleClick().subscribe { changeButtonSelectionWithIcon(btnAccess) }
            .autoDispose(disposables)
        btnDetection.throttleClick().subscribe { changeButtonSelectionWithIcon(btnDetection) }
            .autoDispose(disposables)

        // Which COVID-19 symptopms Sr. citizen is having
        rlCough.throttleClick().subscribe {
            changeCovidSymptomsSelection(ivCough, tvCough, ivDoneCough)
        }.autoDispose(disposables)
        rlFever.throttleClick().subscribe {
            changeCovidSymptomsSelection(ivFever, tvFever, ivDoneFever)
        }.autoDispose(disposables)
        rlShortOfBreath.throttleClick().subscribe {
            changeCovidSymptomsSelection(
                ivShortnessOfBreath, tvShortnessOfBreath, ivDoneShortnessOfBreath
            )
        }.autoDispose(disposables)
        rlSoreThroat.throttleClick().subscribe {
            changeCovidSymptomsSelection(ivSoreThroat, tvSoreThroat, ivDoneSoreThroat)
        }.autoDispose(disposables)

        // Lack of essential services
        btnLackOfEssentialServicesYes.throttleClick().subscribe {
            selectedLackOfEssentialServices =
                toggleButtonSelection(
                    btnLackOfEssentialServicesYes, selectedLackOfEssentialServices
                )
        }.autoDispose(disposables)
        btnLackOfEssentialServicesNo.throttleClick().subscribe {
            selectedLackOfEssentialServices =
                toggleButtonSelection(btnLackOfEssentialServicesNo, selectedLackOfEssentialServices)
        }.autoDispose(disposables)

        // is Sr. citizen is in any Emergency ?
        btnEmergencyEscalationYes.throttleClick().subscribe {
            viewModel.isEmergencyEscalation("Y")
            selectedNeedOfEmergencyServices =
                toggleButtonSelection(btnEmergencyEscalationYes, selectedNeedOfEmergencyServices)
        }.autoDispose(disposables)
        btnEmergencyEscalationNo.throttleClick().subscribe {
            viewModel.isEmergencyEscalation("N")
            selectedNeedOfEmergencyServices =
                toggleButtonSelection(btnEmergencyEscalationNo, selectedNeedOfEmergencyServices)
        }.autoDispose(disposables)

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

        // Button to save the data or go back
        btnSave.throttleClick().subscribe {
            if (viewModel.getTalkedWith() == getString(R.string.community_member)) {
                if (validateSrCitizenRegistrationForm()) viewModel.registerNewSeniorCitizen(this)
            } else {
                if (validateFields())
                    viewModel.saveSrCitizenFeedback(this, preparePostParams(), syncData)
            }
        }.autoDispose(disposables)
        btnCancelMe.throttleClick().subscribe {
            BaseUtility.showAlertMessage(this, R.string.alert, R.string.data_not_saved,
                onPositiveBtnClick = { dialog, _ ->
                    dialog.dismiss()
                    finish()
                },
                onNegativeBtnClick = { dialog, _ -> dialog.dismiss() }
            )
        }.autoDispose(disposables)
    }

    private fun changeButtonSelection(button: MaterialButton) {
        if ((selectedCallStatusButton != null) && (selectedCallStatusButton == button)) return

        button.apply {
            updateButtonState(this)
        }

        selectedCallStatusButton?.apply {
            this.isSelected = false
            this.strokeColor = ColorStateList.valueOf(normalColor)
            this.setTextColor(normalColor)
        }

        selectedCallStatusButton = button
    }

    private fun changeButtonSelectionWithIcon(button: MaterialButton) =
        button.apply {
            updateButtonState(this, true)
            checkTalkedAbout()
        }

    private fun changeCovidSymptomsSelection(
        imageView: ImageView, textView: TextView, ivDone: ImageView
    ) {
        imageView.isSelected = !imageView.isSelected
        textView.setTextColor(
            if (imageView.isSelected) {
                ivDone.visibility = View.VISIBLE
                selectedColor
            } else {
                ivDone.visibility = View.INVISIBLE
                normalColor
            }
        )
        when (imageView.id) {
            R.id.ivCough -> viewModel.isCoughSymptoms(imageView.isSelected)
            R.id.ivFever -> viewModel.isFeverSymptom(imageView.isSelected)
            R.id.ivShortnessOfBreath -> viewModel.isShortBreathSymptoms(imageView.isSelected)
            R.id.ivSoreThroat -> viewModel.isSoreThroatSymptom(imageView.isSelected)
        }
    }

    private fun toggleButtonSelection(button: MaterialButton, lastSelectedButton: MaterialButton?):
            MaterialButton {
        if ((lastSelectedButton != null) && (lastSelectedButton == button))
            return button

        button.apply { updateButtonState(this) }

        lastSelectedButton?.apply {
            this.isSelected = false
            this.strokeColor = ColorStateList.valueOf(normalColor)
            this.setTextColor(normalColor)
        }

        return button
    }

    private fun toggleSelectionForSrCitizenAtHome(
        buttonSelected: MaterialButton, buttonNormal: MaterialButton
    ) {
        buttonSelected.apply {
            this.strokeColor = ColorStateList.valueOf(selectedColor)
            this.setTextColor(selectedColor)
        }
        buttonNormal.apply {
            this.strokeColor = ColorStateList.valueOf(normalColor)
            this.setTextColor(normalColor)
        }
    }

    private fun updateButtonState(button: MaterialButton, isShowIcon: Boolean = false) {
        button.isSelected = !button.isSelected
        if (button.isSelected) {
            button.setTextColor(selectedColor)
            button.strokeColor = ColorStateList.valueOf(selectedColor)
            if (isShowIcon)
                button.icon = ContextCompat.getDrawable(this, R.drawable.ic_checked_circle)
        } else {
            button.setTextColor(normalColor)
            button.strokeColor = ColorStateList.valueOf(normalColor)
            if (isShowIcon) button.icon = null
        }
    }

    private fun manageResetForm(button: MaterialButton) {
        changeButtonSelection(button)
        resetCallStatus()
        resetForm()
    }

    private fun resetAllFormElements() {
        viewModel.clearData()

        resetAdapters()
        resetAutoCompleteTextView()
        resetCovidSymptomsViews()
        resetTalkedRelatedToTopic()
        resetComplaintsLayout()

        etOtherDescription.text.clear()
        etOtherDescription.clearFocus()
        viewModel.setImpDescription("")

        selectedLackOfEssentialServices = null
        selectedNeedOfEmergencyServices = null
    }

    private fun resetForm() {
        tvTalkWith.visibility = View.GONE
        actTalkWith.visibility = View.GONE

        if (viewModel.getTalkedWith() == getString(R.string.community_member)) {
            resetRegisterNewSrCitizenLayout()
            return
        }

        if (viewModel.getOtherMedicalProblem() == getString(R.string.non_covid_symptoms)) {
            tvQuarantineHospitalizationStatus.visibility = View.GONE
            actQuarantineHospitalizationStatus.visibility = View.GONE
        }

        actTalkWith.setText("")
        viewModel.setTalkedWith("")
        updateDropDownIndicator(actTalkWith, R.drawable.ic_arrow_down)

        viewModel.isEmergencyEscalation("")

        resetAllFormElements()

        cgMedicalDetails.visibility = View.GONE
        cgComplaintDetails.visibility = View.GONE
        resetComplaintsLayout()

        viewModel.isSeniorCitizenAtHome(false)
        if (btnAnySrCitizenInHomeYes.isSelected)
            toggleSelectionForSrCitizenAtHome(btnAnySrCitizenInHomeYes, btnAnySrCitizenInHomeNo)
        if (btnAnySrCitizenInHomeNo.isSelected)
            toggleSelectionForSrCitizenAtHome(btnAnySrCitizenInHomeNo, btnAnySrCitizenInHomeYes)
    }

    private fun resetComplaintsLayout() {
        if (btnLackOfEssentialServicesYes.isSelected)
            toggleButtonSelection(btnLackOfEssentialServicesYes, null)
        if (btnLackOfEssentialServicesNo.isSelected)
            toggleButtonSelection(btnLackOfEssentialServicesNo, null)

        viewModel.resetComplaints()
        rvCategoryAdapter.resetAdapter()
        popupCategoryAdapter.resetAdapter()

        etDescription.text.clear()
        etDescription.clearFocus()

        selectedLackOfEssentialServices = null

        if (btnEmergencyEscalationYes.isSelected)
            toggleButtonSelection(btnEmergencyEscalationYes, null)
        if (btnEmergencyEscalationNo.isSelected)
            toggleButtonSelection(btnEmergencyEscalationNo, null)
    }

    private fun resetTalkedRelatedToTopic() {
        viewModel.resetTalkedAbout()
        if (btnPrevention.isSelected) changeButtonSelectionWithIcon(btnPrevention)
        if (btnAccess.isSelected) changeButtonSelectionWithIcon(btnAccess)
        if (btnDetection.isSelected) changeButtonSelectionWithIcon(btnDetection)
    }

    private fun resetCallStatus() {
        viewModel.setCallStatus("")
        if ((selectedCallStatusButton?.id != R.id.btnNoResponse) && btnNoResponse.isSelected)
            resetButton(btnNoResponse)
        if ((selectedCallStatusButton?.id != R.id.btnNotPicked) && btnNotPicked.isSelected)
            resetButton(btnNotPicked)
        if ((selectedCallStatusButton?.id != R.id.btnNotReachable) && btnNotReachable.isSelected)
            resetButton(btnNotReachable)
        if ((selectedCallStatusButton?.id != R.id.btnDisConnected) && btnDisConnected.isSelected)
            resetButton(btnDisConnected)
        if ((selectedCallStatusButton?.id != R.id.btnConnected) && btnConnected.isSelected)
            resetButton(btnConnected)
    }

    private fun resetButton(button: MaterialButton) {
        button.isSelected = false
        button.setTextColor(normalColor)
        button.strokeColor = ColorStateList.valueOf(normalColor)
        button.icon = null
    }

    private fun resetAdapters() {
        viewModel.resetMedicalHistory()
        popupMedicalHistorySrCitizenAdapter.resetAdapter()
        rvMedicalHistorySrCitizenAdapter.resetAdapter()

        viewModel.resetComplaints()
        popupCategoryAdapter.resetAdapter()
        rvCategoryAdapter.resetAdapter()
    }

    private fun resetRegisterNewSrCitizenLayout() {
        tvAnySrCitizenInHome.visibility = View.GONE
        btnAnySrCitizenInHomeYes.visibility = View.GONE
        btnAnySrCitizenInHomeNo.visibility = View.GONE

        if (registerNewSrCitizen.visibility == View.VISIBLE) {
            tvNewSrCitizenPersonalDetails.visibility = View.GONE
            registerNewSrCitizen.visibility = View.GONE
        }

        resetButton(btnAnySrCitizenInHomeYes)
        resetButton(btnAnySrCitizenInHomeNo)

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

    private fun resetCovidSymptomsViews() {
        if (hsvCovidSymptoms.visibility == View.VISIBLE) {
            tvCovidSymptoms.visibility = View.GONE
            hsvCovidSymptoms.visibility = View.GONE
            if (ivCough.isSelected) changeCovidSymptomsSelection(ivCough, tvCough, ivDoneCough)
            if (ivFever.isSelected) changeCovidSymptomsSelection(ivFever, tvFever, ivDoneFever)
            if (ivShortnessOfBreath.isSelected)
                changeCovidSymptomsSelection(
                    ivShortnessOfBreath, tvShortnessOfBreath, ivDoneShortnessOfBreath
                )
            if (ivSoreThroat.isSelected)
                changeCovidSymptomsSelection(ivSoreThroat, tvSoreThroat, ivDoneSoreThroat)
            tvQuarantineHospitalizationStatus.visibility = View.GONE
            actQuarantineHospitalizationStatus.visibility = View.GONE
        }
    }

    private fun updateDropDownIndicator(autoCompleteTextView: AutoCompleteTextView, icon: Int) =
        autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0)

    private fun resetAutoCompleteTextView() {
        actGender.setText("")
        viewModel.setGender("")

        actDistrict.setText("")
        viewModel.setDistrict("")

        actState.setText("")
        viewModel.setState("")

        actBehaviorChange.setText("")
        viewModel.setBehaviorChange("")
        updateDropDownIndicator(actBehaviorChange, R.drawable.ic_arrow_down)

        actOtherMedicalProblems.setText("")
        viewModel.setOtherMedicalProblem("")
        updateDropDownIndicator(actOtherMedicalProblems, R.drawable.ic_arrow_down)

        actQuarantineHospitalizationStatus.setText("")
        viewModel.setQuarantineStatus("")
        updateDropDownIndicator(actQuarantineHospitalizationStatus, R.drawable.ic_arrow_down)
    }

    private fun manageViewVisibility() {
        if (viewModel.getTalkedWith() == getString(R.string.community_member)) {
            if (cgMedicalDetails.visibility == View.VISIBLE) {
                resetCovidSymptomsViews()
                cgMedicalDetails.visibility = View.GONE
            }
            if (cgComplaintDetails.visibility == View.VISIBLE)
                cgComplaintDetails.visibility = View.GONE

            tvAnySrCitizenInHome.visibility = View.VISIBLE
            btnAnySrCitizenInHomeYes.visibility = View.VISIBLE
            btnAnySrCitizenInHomeNo.visibility = View.VISIBLE
        } else {
            tvAnySrCitizenInHome.visibility = View.GONE
            btnAnySrCitizenInHomeYes.visibility = View.GONE
            btnAnySrCitizenInHomeNo.visibility = View.GONE
            if (registerNewSrCitizen.visibility == View.VISIBLE) {
                tvNewSrCitizenPersonalDetails.visibility = View.GONE
                registerNewSrCitizen.visibility = View.GONE
            }

            cgMedicalDetails.visibility = View.VISIBLE
        }
    }

    private fun checkTalkedAbout() {
        updateOnTalkedAbout(btnPrevention, getString(R.string.prevention))
        updateOnTalkedAbout(btnAccess, getString(R.string.access))
        updateOnTalkedAbout(btnDetection, getString(R.string.detection))
    }

    private fun updateOnTalkedAbout(button: MaterialButton, tag: String) {
        if (button.isSelected) viewModel.addTalkedAbout(tag)
        else viewModel.removeTalkedAbout(tag)
    }

    private fun manageOtherMedicalRelatedViews() {
        when (viewModel.getOtherMedicalProblem()) {
            getString(R.string.covid_19_symptoms) -> {
                cgComplaintDetails.visibility = View.GONE

                tvCovidSymptoms.visibility = View.VISIBLE
                hsvCovidSymptoms.visibility = View.VISIBLE
                tvQuarantineHospitalizationStatus.visibility = View.VISIBLE
                actQuarantineHospitalizationStatus.visibility = View.VISIBLE

                resetComplaintsLayout()
            }
            getString(R.string.non_covid_symptoms) -> {
                cgComplaintDetails.visibility = View.VISIBLE
                resetCovidSymptomsViews()
            }
            else -> {
                resetCovidSymptomsViews()
                cgComplaintDetails.visibility = View.GONE
            }
        }
    }

    private fun showPopupWindow(anchorView: View) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.include_recyclerview, null)

        view.rvList.adapter =
            if (anchorView == llMedicalHistorySrCitizen) popupMedicalHistorySrCitizenAdapter
            else popupCategoryAdapter

        PopupWindow(
            view, llMedicalHistorySrCitizen.width,
            (ApneSaathiApplication.screenSize[1] * 0.35).toInt()
        ).apply {
            this.isOutsideTouchable = true
            this.isFocusable = true
            this.elevation = resources.getDimensionPixelSize(R.dimen.dimen_10).toFloat()
            this.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            this.showAsDropDown(anchorView)
        }
    }

    private fun observeData() = viewModel.getDataObserver().observe(this, Observer {
        when (it) {
            is NetworkRequestState.NetworkNotAvailable -> when (it.apiName) {
                ApiProvider.ApiSaveSeniorCitizenFeedbackForm ->
                    BaseUtility.showAlertMessage(
                        this, R.string.alert, R.string.no_internet_save_data_in_app,
                        R.string.thanks_go_back
                    ) { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }
                ApiProvider.ApiRegisterSeniorCitizen ->
                    BaseUtility.showAlertMessage(this, R.string.alert, R.string.check_internet)
            }
            is NetworkRequestState.LoadingData -> progressDialog.show()
            is NetworkRequestState.ErrorResponse -> {
                progressDialog.dismiss()
                BaseUtility.showAlertMessage(
                    this, R.string.alert, R.string.can_not_connect_to_server
                )
            }
            is NetworkRequestState.SuccessResponse<*> -> {
                progressDialog.dismiss()
                when (it.apiName) {
                    ApiProvider.ApiSaveSeniorCitizenFeedbackForm -> BaseUtility.showAlertMessage(
                        this, R.string.success, R.string.details_saved_successfully,
                        R.string.thanks_go_back
                    ) { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }
                    ApiProvider.ApiRegisterSeniorCitizen -> {
                        resetRegisterNewSrCitizenLayout()
                        BaseUtility.showAlertMessage(
                            this, R.string.success, R.string.sr_citizen_registered_successfully
                        )
                    }
                }
            }
        }
    })

    private fun validateFields(): Boolean {
        if (viewModel.getCallStatus().isEmpty()) {
            BaseUtility.showAlertMessage(this, R.string.error, R.string.validate_call_status)
            return false
        }
        if (viewModel.getCallStatus() == "5") {
            if (viewModel.getTalkedWith().isEmpty()) {
                BaseUtility.showAlertMessage(this, R.string.error, R.string.validate_talked_with)
                return false
            } else if (viewModel.getMedicalHistory().size == 0) {
                BaseUtility.showAlertMessage(
                    this, R.string.error, R.string.validate_medical_history
                )
                return false
            } else if (viewModel.getTalkedAbout().isEmpty()) {
                BaseUtility.showAlertMessage(this, R.string.error, R.string.validate_talked_about)
                return false
            } else if (viewModel.getBehaviorChange().isEmpty()) {
                BaseUtility.showAlertMessage(
                    this, R.string.error, R.string.validate_behavioural_changes
                )
                return false
            } else if (actOtherMedicalProblems.text.toString().isEmpty()) {
                BaseUtility.showAlertMessage(
                    this, R.string.error, R.string.validate_other_medical_details
                )
                return false
            } else if (viewModel.isCovideSymptoms() && !viewModel.isCoughSymptoms() &&
                !viewModel.isFeverSymptom() && !viewModel.isShortBreathSymptoms() &&
                !viewModel.isSoreThroatSymptom()
            ) {
                BaseUtility.showAlertMessage(this, R.string.error, R.string.validate_covid_symptoms)
                return false
            } else if (viewModel.isCovideSymptoms() && viewModel.getQuarantineStatus().isEmpty()) {
                BaseUtility.showAlertMessage(
                    this, R.string.error, R.string.validate_quarantine_status
                )
                return false
            } else if (!viewModel.isCovideSymptoms() && (viewModel.getComplaints().size == 0) &&
                (viewModel.getOtherMedicalProblem() == getString(R.string.non_covid_symptoms))
            ) {
                BaseUtility.showAlertMessage(
                    this, R.string.error, R.string.validate_complaint_category
                )
                return false
            } else if (!viewModel.isCovideSymptoms() && viewModel.isEmergencyEscalation().isEmpty()
                && (viewModel.getOtherMedicalProblem() != getString(R.string.no_problems))
            ) {
                BaseUtility.showAlertMessage(
                    this, R.string.error, R.string.validate_emergency_required
                )
                return false
            }
        } else return true
        return true
    }

    private fun validateSrCitizenRegistrationForm(): Boolean = when {
        etName.text.toString().isEmpty() -> {
            tvNameError.visibility = View.VISIBLE
            false
        }
        etAge.text.toString().isEmpty() -> {
            tvAgeError.visibility = View.VISIBLE
            false
        }
        viewModel.getGender().isEmpty() -> {
            tvGenderError.visibility = View.VISIBLE
            false
        }
        etContactNumber.text.toString().isEmpty() -> {
            tvContactNumberError.setText(R.string.validate_contact_number)
            tvContactNumberError.visibility = View.VISIBLE
            false
        }
        ((etContactNumber.text.toString().length < 7) ||
                !BaseUtility.validatePhoneNumber(etContactNumber.text.toString())) -> {
            tvContactNumberError.setText(R.string.valid_contact_number)
            tvContactNumberError.visibility = View.VISIBLE
            false
        }
        viewModel.getState().isEmpty() -> {
            tvStateError.visibility = View.VISIBLE
            false
        }
        viewModel.getDistrict().isEmpty() -> {
            tvDistrictError.visibility = View.VISIBLE
            false
        }
        etAddress.text.toString().isEmpty() -> {
            tvAddressError.visibility = View.VISIBLE
            false
        }
        else -> {
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

            true
        }
    }

    private fun preparePostParams(): JsonObject {
        val params = JsonObject()
        params.addProperty(ApiConstants.CallId, callId.toInt())
        syncData.callId = callId.toInt()

        params.addProperty(ApiConstants.VolunteerId, 1234/*dataManager.getUserId()*/)
        syncData.volunteerId = "1234"/*dataManager.getUserId()*/

        params.addProperty(ApiConstants.SrCitizenCallStatusSubCode, viewModel.getCallStatus())
        syncData.callStatusSubCode = viewModel.getCallStatus()

        val callStatus = if (viewModel.getCallStatus() == "5") "2" else "1"
        params.addProperty(ApiConstants.SrCitizenCallStatusCode, callStatus)

        syncData.talkedWith = viewModel.getTalkedWith()
        params.addProperty(ApiConstants.SrCitizenTalkedWith, syncData.talkedWith)

        if (callStatus == "1") return params

        val arraySubParams = JsonObject()
        arraySubParams.addProperty(ApiConstants.CallId, callId.toInt())
        arraySubParams.addProperty(ApiConstants.VolunteerId, 1234/*dataManager.getUserId()*/)

        var dataList = viewModel.getMedicalHistory()
        if (dataList.any { it == getString(R.string.no_problems) }) {
            arraySubParams.addProperty(ApiConstants.IsDiabetic, "N")
            syncData.hasDiabetic = "N"

            arraySubParams.addProperty(ApiConstants.IsBloodPressure, "N")
            syncData.hasBloodPressure = "N"

            arraySubParams.addProperty(ApiConstants.LungAilment, "N")
            syncData.hasLungAilment = "N"

            arraySubParams.addProperty(ApiConstants.CancerOrMajorSurgery, "N")
            syncData.cancerOrMajorSurgery = "N"
        } else {
            syncData.hasDiabetic = if (dataList.any { it == getString(R.string.diabetes) }) "Y"
            else "N"
            arraySubParams.addProperty(ApiConstants.IsDiabetic, syncData.hasDiabetic)

            syncData.hasBloodPressure =
                if (dataList.any { it == getString(R.string.blood_pressure) }) "Y" else "N"
            arraySubParams.addProperty(ApiConstants.IsBloodPressure, syncData.hasBloodPressure)

            syncData.hasLungAilment =
                if (dataList.any { it == getString(R.string.lung_ailment) }) "Y" else "N"
            arraySubParams.addProperty(ApiConstants.LungAilment, syncData.hasLungAilment)

            syncData.cancerOrMajorSurgery =
                if (dataList.any { it == getString(R.string.cancer_major_surgery) }) "Y" else "N"
            arraySubParams.addProperty(
                ApiConstants.CancerOrMajorSurgery, syncData.cancerOrMajorSurgery
            )
        }

        arraySubParams.addProperty(ApiConstants.OtherAilments, "N")
        syncData.otherAilments = "N"

        arraySubParams.addProperty(ApiConstants.RemarkOnMedicalHistory, "")
        syncData.remarksMedicalHistory = ""

        syncData.relatedInfoTalkedAbout = if (viewModel.getTalkedAbout().isEmpty()) ""
        else TextUtils.join(",", viewModel.getTalkedAbout())
        arraySubParams.addProperty(ApiConstants.InfoTalkAbout, syncData.relatedInfoTalkedAbout)

        syncData.behavioralChangesNoticed = viewModel.getBehaviorChange()
        arraySubParams.addProperty(
            ApiConstants.NoticedBehaviouralChange, syncData.behavioralChangesNoticed
        )

        syncData.hasCovidSymptoms = if (viewModel.isCovideSymptoms()) "Y" else "N"
        arraySubParams.addProperty(ApiConstants.HasCovidSymptoms, syncData.hasCovidSymptoms)

        syncData.hasCough = if (viewModel.isCoughSymptoms()) "Y" else "N"
        arraySubParams.addProperty(ApiConstants.HasCough, syncData.hasCough)

        syncData.hasFever = if (viewModel.isFeverSymptom()) "Y" else "N"
        arraySubParams.addProperty(ApiConstants.HasFever, syncData.hasFever)

        syncData.hasShortnessOfBreath = if (viewModel.isShortBreathSymptoms()) "Y" else "N"
        arraySubParams.addProperty(ApiConstants.HasShortnessOfBreath, syncData.hasShortnessOfBreath)

        syncData.hasSoreThroat = if (viewModel.isSoreThroatSymptom()) "Y" else "N"
        arraySubParams.addProperty(ApiConstants.HasSoreThroat, syncData.hasSoreThroat)

        syncData.quarantineStatus = viewModel.getQuarantineStatus()
        arraySubParams.addProperty(ApiConstants.QuarantineStatus, syncData.quarantineStatus!!)

        dataList = viewModel.getComplaints()
        if (dataList.any { it == getString(R.string.no_problems) }) {
            syncData.foodShortage = "4"
            arraySubParams.addProperty(ApiConstants.FoodShortage, 4)

            syncData.medicineShortage = "4"
            arraySubParams.addProperty(ApiConstants.MedicineShortage, 4)

            syncData.accessToBankingIssue = "4"
            arraySubParams.addProperty(ApiConstants.AccessToBankingIssue, 4)

            syncData.utilitySupplyIssue = "4"
            arraySubParams.addProperty(ApiConstants.UtilityIssue, 4)

            syncData.hygieneIssue = "4"
            arraySubParams.addProperty(ApiConstants.HygieneIssue, 4)

            syncData.safetyIssue = "4"
            arraySubParams.addProperty(ApiConstants.SafetyIssue, 4)

            syncData.phoneAndInternetIssue = "4"
            arraySubParams.addProperty(ApiConstants.PhoneInternetIssue, 4)
        } else {
            syncData.foodShortage = if (dataList.any { it == getString(R.string.lack_of_food) }) "0"
            else "4"
            arraySubParams.addProperty(ApiConstants.FoodShortage, syncData.foodShortage!!.toInt())

            syncData.medicineShortage =
                if (dataList.any { it == getString(R.string.lack_of_medicine) }) "0" else "4"
            arraySubParams.addProperty(
                ApiConstants.MedicineShortage, syncData.medicineShortage!!.toInt()
            )

            syncData.accessToBankingIssue =
                if (dataList.any { it == getString(R.string.lack_of_banking_service) }) "0" else "4"
            arraySubParams.addProperty(
                ApiConstants.AccessToBankingIssue, syncData.accessToBankingIssue!!.toInt()
            )

            syncData.utilitySupplyIssue =
                if (dataList.any { it == getString(R.string.lack_of_utilities) }) "0" else "4"
            arraySubParams.addProperty(
                ApiConstants.UtilityIssue, syncData.utilitySupplyIssue!!.toInt()
            )

            syncData.hygieneIssue =
                if (dataList.any { it == getString(R.string.lack_of_hygine) }) "0" else "4"
            arraySubParams.addProperty(
                ApiConstants.HygieneIssue, syncData.hygieneIssue!!.toInt()
            )

            syncData.safetyIssue =
                if (dataList.any { it == getString(R.string.lack_of_safety) }) "0" else "4"
            arraySubParams.addProperty(ApiConstants.SafetyIssue, syncData.safetyIssue!!.toInt())

            syncData.emergencyServiceIssue =
                if (dataList.any { it == getString(R.string.lack_of_access_emergency) }) "0" else "4"
            arraySubParams.addProperty(
                ApiConstants.EmergencyServiceIssue, syncData.emergencyServiceIssue!!.toInt()
            )

            syncData.phoneAndInternetIssue =
                if (dataList.any { it == getString(R.string.phone_and_service) }) "0" else "4"
            arraySubParams.addProperty(
                ApiConstants.PhoneInternetIssue, syncData.phoneAndInternetIssue!!.toInt()
            )
        }
        syncData.emergencyServiceRequired = viewModel.isEmergencyEscalation()
        arraySubParams.addProperty(
            ApiConstants.IsEmergencyServicesRequired, syncData.emergencyServiceRequired
        )

        val jsonArray = JsonArray()
        jsonArray.add(arraySubParams)

        params.add(ApiConstants.MedicalGrievances, jsonArray)

        println("TAG -- MyData --> $params")

        return params
    }
}