package com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenfeedbackform

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
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

    companion object {
        private const val YES: String = "1"
        private const val NO: String = "2"

        private const val TalkedWithSrCitizen: String = "1"
        private const val TalkedWithFamilyMember: String = "2"
        private const val TalkedWithCommunityMember: String = "3"

        private const val TalkedAboutPrevention: String = "1"
        private const val TalkedAboutAccess: String = "2"
        private const val TalkedAboutDetection: String = "3"

        private const val BehaviorChangeMayBe: String = "3"
        private const val BehaviorChangeNotApplicable: String = "4"
    }

    private var selectedSymptomsAwareness: MaterialButton? = null
    private var selectedKnowledgeOfCovidPrevention: MaterialButton? = null
    private var selectedLackOfEssentialServices: MaterialButton? = null
    private var selectedNeedOfEmergencyServices: MaterialButton? = null
    private var selectedState: String = ""

    private val selectedColor: Int by lazy { ContextCompat.getColor(this, R.color.color_orange) }
    private val normalColor: Int by lazy {
        ContextCompat.getColor(this, R.color.color_dark_grey_txt)
    }

    private val progressDialog: ProgressDialog.Builder by lazy {
        ProgressDialog.Builder(this).setMessage(R.string.wait_saving_data)
    }

    private var callId: String = ""

    private val syncData by lazy { SyncSrCitizenGrievance() }

    /**
     * popupMedicalHistoryList, popupMedicalHistorySrCitizenAdapter, rvMedicalHistorySrCitizenAdapter are attached with
     * @see(rvMedicalHistorySrCitizen) i.e. when popup (@see showPopupWindow(anchorView: View))
     * is shown for rvMedicalHistorySrCitizen then popupMedicalHistorySrCitizenAdapter and
     * popupMedicalHistoryList are used as Adapter to the popup view.
     * */
    private val popupMedicalHistoryList: MutableList<FormElements> = mutableListOf()
    private val popupMedicalHistorySrCitizenAdapter: PopupAdapter by lazy {
        PopupAdapter(popupMedicalHistoryList).apply {
            this.setOnItemClickListener(object : OnItemClickListener<FormElements> {
                override fun onItemClick(position: Int, data: FormElements) {
                    if (data.isSelected) {
                        rvMedicalHistorySrCitizenAdapter.addItem(data.name)
                        viewModel.addMedicalHistory(data.name)
                    } else {
                        rvMedicalHistorySrCitizenAdapter.removeItem(data.name)
                        viewModel.removeMedicalHistory(data.name)
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
            this.setOnItemClickListener(object : OnItemClickListener<String> {
                override fun onItemClick(position: Int, data: String) {
                    popupMedicalHistorySrCitizenAdapter.updateItem(data)
                    viewModel.removeMedicalHistory(data)
                    rvMedicalHistorySrCitizen.visibility =
                        if (rvMedicalHistorySrCitizenAdapter.itemCount > 0) View.VISIBLE
                        else View.GONE
                }

                override fun onMoreInfoClick(position: Int, data: String) =
                    showPopupWindow(llMedicalHistorySrCitizen)
            })
        }
    }

    /**
     * popupCategoryList, popupCategoryAdapter, rvCategoryAdapter are attached with
     * @see(rvCategory) i.e. when popup
     * @see(showPopupWindow(anchorView: View)) is shown for rvCategory then popupCategoryAdapter
     * and popupCategoryList are used as Adapter to the popup view.
     * */
    private val popupCategoryList: MutableList<FormElements> = mutableListOf()
    private val popupCategoryAdapter: PopupAdapter by lazy {
        PopupAdapter(popupCategoryList).apply {
            this.setOnItemClickListener(
                object : OnItemClickListener<FormElements> {
                    override fun onItemClick(position: Int, data: FormElements) {
                        if (data.isSelected) {
                            rvCategoryAdapter.addItem(data.name)
                            viewModel.addComplaint(data.name)
                        } else {
                            rvCategoryAdapter.removeItem(data.name)
                            viewModel.removeComplaint(data.name)
                        }

                        rvCategory.visibility = if (rvCategoryAdapter.itemCount > 0) View.VISIBLE
                        else View.GONE
                    }
                })
        }
    }
    private val rvCategoryAdapter: SimpleBaseAdapter by lazy {
        SimpleBaseAdapter().apply {
            this.setOnItemClickListener(object : OnItemClickListener<String> {
                override fun onItemClick(position: Int, data: String) {
                    popupCategoryAdapter.updateItem(data)
                    viewModel.removeComplaint(data)
                    rvCategory.visibility = if (rvCategoryAdapter.itemCount > 0) View.VISIBLE
                    else View.GONE
                }

                override fun onMoreInfoClick(position: Int, data: String) =
                    showPopupWindow(flCategory)
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

        /** Converting array to list*/
        resources.getStringArray(R.array.medical_problems).forEach {
            popupMedicalHistoryList.add(FormElements(it))
        }

        /** Converting array to list*/
        resources.getStringArray(R.array.grievance_array).forEach {
            popupCategoryList.add(FormElements(it))
        }

        observeData()
        /**
         * Once UI is loaded we will set the data, hence delay required.
         * */
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

    /**
     * Retrieve data provided via intent
     **/
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

//                        setCallStatus(callData.callStatusCode!!)
//                        setTalkedWith(callData.talkedWith!!, callData.callStatusCode!!)

                        if (callData.callStatusCode == "10") {
                            grievances?.run {
                                setCheckForMedicalHistoryData(this)
//                                setRelatedInfoTalkedAboutData(this.relatedInfoTalkedAbout!!)
//                                setBehavioralChangesData(this.behavioralChangesNoticed!!)
//                                setCovidData(this)
//                                setComplaintData(this)
//                                etOtherDescription.setText(this.impRemarkInfo)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkData(dataString: String, element: String, compareString: String): Boolean =
        (dataString.toLowerCase(Locale.getDefault()) == YES) && (element == compareString)

    /** set the call status and hide/show Ui accordingly */
    private fun setCallStatus(callStatus: String) = when (callStatus) {
        this.getString(R.string.connected) -> {
            viewModel.setCallStatus("10")
            tvTalkWith.visibility = View.VISIBLE
            actTalkWith.visibility = View.VISIBLE
        }
        this.getString(R.string.not_picked_single_line) -> viewModel.setCallStatus("2")
        this.getString(R.string.not_reachable_single_line) -> viewModel.setCallStatus("3")
        this.getString(R.string.number_busy) -> viewModel.setCallStatus("4")
        this.getString(R.string.call_later) -> viewModel.setCallStatus("5")
        this.getString(R.string.call_dropped) -> viewModel.setCallStatus("6")
        this.getString(R.string.wrong_number) -> viewModel.setCallStatus("7")
        this.getString(R.string.number_not_existing) -> viewModel.setCallStatus("8")
        this.getString(R.string.dis_connected) -> {
            viewModel.setCallStatus("9")
            edt_call_status_comment.visibility = View.VISIBLE
        }
        else -> {
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
//            rvMedicalHistorySrCitizen.visibility = View.VISIBLE
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

        val callStatusList = resources.getStringArray(R.array.call_status)
        val callStatusAdapter =
            BaseArrayAdapter(this, R.layout.item_layout_dropdown_menu, callStatusList)
        actCallStatus.threshold = 0
        actCallStatus.setAdapter(callStatusAdapter)
        actCallStatus.setOnKeyListener(null)
        actCallStatus.setOnItemClickListener { _, _, position, _ ->
            resetForm()
            edt_call_status_comment.visibility = View.GONE
            setCallStatus(callStatusList[position])
        }

        val stateList = resources.getStringArray(R.array.states_array)
        val stateAdapter = BaseArrayAdapter(this, R.layout.item_layout_dropdown_menu, stateList)
        actState.threshold = 0
        actState.setAdapter(stateAdapter)
        actState.setOnKeyListener(null)
        actState.setOnItemClickListener { _, _, position, _ ->
            viewModel.setState(stateList[position])
            selectedState = stateList[position]
            if (tvStateError.visibility == View.VISIBLE) tvStateError.visibility = View.GONE
            setDistrictAdapter(position)
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
        /**
         * This values should be passed in Api Parameters and while retrieving from api we will get
         * same values as below
         * SeniorCitizen = "1";
         * FamilyMemberOfSrCitizen = "2";
         * CommunityMember = "3";
         **/
        actTalkWith.setOnItemClickListener { _, _, position, _ ->
            if (viewModel.getTalkedWith() == TalkedWithCommunityMember) {
                resetRegisterNewSrCitizenLayout()
                resetAllFormElements()
            }
            viewModel.setTalkedWith(
                when (talkedWithList[position]) {
                    getString(R.string.sr_citizen) -> TalkedWithSrCitizen
                    getString(R.string.family_member_of_sr_citizen) -> TalkedWithFamilyMember
                    getString(R.string.community_member) -> TalkedWithCommunityMember
                    else -> ""
                }
            )
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
        /**
         * This values should be passed in Api Parameters and while retrieving from api we will get
         * same values as below
         * Yes = "1",
         * No = "2",
         * May Be = "3",
         * Not Applicable = "4"
         **/
        actBehaviorChange.setOnItemClickListener { _, _, position, _ ->
            viewModel.setBehaviorChange(
                when (behaviorChangesList[position]) {
                    getString(R.string.yes) -> YES
                    getString(R.string.no) -> NO
                    getString(R.string.may_be) -> BehaviorChangeMayBe
                    getString(R.string.not_applicable) -> BehaviorChangeNotApplicable
                    else -> ""
                }
            )
            if ((behaviorChangesList[position] == getString(R.string.no)) ||
                (behaviorChangesList[position] == getString(R.string.may_be))
            ) {
                tvElaboratePracticeFollowed.visibility = View.VISIBLE
                etElaboratePracticeFollowed.visibility = View.VISIBLE
            } else {
                viewModel.setPracticeNotAllowed("")
                tvElaboratePracticeFollowed.visibility = View.GONE
                etElaboratePracticeFollowed.visibility = View.GONE
                etElaboratePracticeFollowed.text.clear()
            }
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

    private fun setDistrictAdapter(position: Int) {
        var districtsList = resources.getStringArray(R.array.districts_array)
        if (position != -1) {
            when (position) {
                0 -> districtsList = resources.getStringArray(R.array.district0)
                1 -> districtsList = resources.getStringArray(R.array.district1)
                2 -> districtsList = resources.getStringArray(R.array.district2)
                3 -> districtsList = resources.getStringArray(R.array.district3)
                4 -> districtsList = resources.getStringArray(R.array.district4)
                5 -> districtsList = resources.getStringArray(R.array.district5)
                6 -> districtsList = resources.getStringArray(R.array.district6)
                7 -> districtsList = resources.getStringArray(R.array.district7)
            }
        }
        val districtsAdapter =
            BaseArrayAdapter(this, R.layout.item_layout_dropdown_menu, districtsList)
        actDistrict.threshold = 0
        actDistrict.setAdapter(districtsAdapter)
        actDistrict.setOnKeyListener(null)
        actDistrict.setOnItemClickListener { _, _, position, _ ->
            viewModel.setDistrict(districtsList[position])
            if (tvDistrictError.visibility == View.VISIBLE) tvDistrictError.visibility = View.GONE
        }
    }

    private fun initClicks() {
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
        actCallStatus.throttleClick().subscribe { actCallStatus.showDropDown() }
            .autoDispose(disposables)
        actDistrict.throttleClick().subscribe {
            if (selectedState.isNotEmpty()) {
                actDistrict.showDropDown()
            } else {
                Toast.makeText(this, getString(R.string.select_a_state), Toast.LENGTH_SHORT).show()
            }
        }
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

        etElaboratePracticeFollowed.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) =
                viewModel.setPracticeNotAllowed(text?.toString() ?: "")
        })

        // Had discussion about Prevention/Access/Detection
        btnPrevention.throttleClick().subscribe { changeButtonSelectionWithIcon(btnPrevention) }
            .autoDispose(disposables)
        btnAccess.throttleClick().subscribe { changeButtonSelectionWithIcon(btnAccess) }
            .autoDispose(disposables)
        btnDetection.throttleClick().subscribe { changeButtonSelectionWithIcon(btnDetection) }
            .autoDispose(disposables)

        btnIsCovidAwareYes.throttleClick().subscribe {
            //viewModel.setAwareOfCovid19("y")
            viewModel.setAwareOfCovid19(YES)
            selectedSymptomsAwareness =
                toggleButtonSelection(btnIsCovidAwareYes, selectedSymptomsAwareness)
            if (tvIsCovidPreventionTaken.isVisible) updateCovidAwarenessVisibility(View.GONE)
        }.autoDispose(disposables)
        btnIsCovidAwareNo.throttleClick().subscribe {
            //viewModel.setAwareOfCovid19("n")
            viewModel.setAwareOfCovid19(NO)
            selectedSymptomsAwareness =
                toggleButtonSelection(btnIsCovidAwareNo, selectedSymptomsAwareness)
            if (!tvIsCovidPreventionTaken.isVisible) updateCovidAwarenessVisibility(View.VISIBLE)
        }.autoDispose(disposables)
        btnIsCovidPreventionYes.throttleClick().subscribe {
            //viewModel.setSymptomsPreventionDiscussed("y")
            viewModel.setSymptomsPreventionDiscussed(YES)
            selectedKnowledgeOfCovidPrevention =
                toggleButtonSelection(btnIsCovidPreventionYes, selectedKnowledgeOfCovidPrevention)
        }.autoDispose(disposables)
        btnIsCovidPreventionNo.throttleClick().subscribe {
            viewModel.setSymptomsPreventionDiscussed(NO)
            selectedKnowledgeOfCovidPrevention =
                toggleButtonSelection(btnIsCovidPreventionNo, selectedKnowledgeOfCovidPrevention)
        }.autoDispose(disposables)

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
            viewModel.setLackOfEssential(YES)
            cg_complaint.visibility = View.VISIBLE
            selectedLackOfEssentialServices =
                toggleButtonSelection(
                    btnLackOfEssentialServicesYes, selectedLackOfEssentialServices
                )
        }.autoDispose(disposables)
        btnLackOfEssentialServicesNo.throttleClick().subscribe {
            viewModel.setLackOfEssential(NO)
            cg_complaint.visibility = View.GONE
            selectedLackOfEssentialServices =
                toggleButtonSelection(btnLackOfEssentialServicesNo, selectedLackOfEssentialServices)
        }.autoDispose(disposables)

        // is Sr. citizen is in any Emergency ?
        btnEmergencyEscalationYes.throttleClick().subscribe {
            //viewModel.isEmergencyEscalation("Y")
            viewModel.isEmergencyEscalation(YES)
            selectedNeedOfEmergencyServices =
                toggleButtonSelection(btnEmergencyEscalationYes, selectedNeedOfEmergencyServices)
        }.autoDispose(disposables)
        btnEmergencyEscalationNo.throttleClick().subscribe {
            //viewModel.isEmergencyEscalation("N")
            viewModel.isEmergencyEscalation(NO)
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
            if (viewModel.getTalkedWith() == TalkedWithCommunityMember) {
                if (validateSrCitizenRegistrationForm()) viewModel.registerNewSeniorCitizen(this)
            } else {
                if (validateFields()) {
                    preparePostParams()
                    //viewModel.saveSrCitizenFeedback(this, preparePostParams(), syncData)
                }
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

    private fun changeButtonSelectionWithIcon(button: MaterialButton) = button.apply {
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

    private fun updateCovidAwarenessVisibility(visibility: Int) {
        tvIsCovidPreventionTaken.visibility = visibility
        btnIsCovidPreventionYes.visibility = visibility
        btnIsCovidPreventionNo.visibility = visibility
    }

    /**
     * Reset layout when user selects @see{TalkedWithCommunityMember} also when resetting the whole
     * layout
     * */
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

        selectedSymptomsAwareness = null
        selectedKnowledgeOfCovidPrevention = null
        selectedLackOfEssentialServices = null
        selectedNeedOfEmergencyServices = null
    }

    private fun resetForm() {
        tvTalkWith.visibility = View.GONE
        actTalkWith.visibility = View.GONE

        if (viewModel.getTalkedWith() == TalkedWithCommunityMember) {
            resetRegisterNewSrCitizenLayout()
            return
        }

        if (btnIsCovidAwareYes.isSelected) updateButtonState(btnIsCovidAwareYes)
        if (btnIsCovidAwareNo.isSelected) updateButtonState(btnIsCovidAwareNo)

        if (btnIsCovidPreventionYes.isSelected) updateButtonState(btnIsCovidPreventionYes)
        updateCovidAwarenessVisibility(View.GONE)

        if (btnIsCovidPreventionNo.isSelected) updateButtonState(btnIsCovidPreventionNo)
        updateCovidAwarenessVisibility(View.GONE)

        if (viewModel.getPracticeNotAllowed().isNotEmpty()) {
            viewModel.setPracticeNotAllowed("")
            tvElaboratePracticeFollowed.visibility = View.GONE
            etElaboratePracticeFollowed.visibility = View.GONE
            etElaboratePracticeFollowed.text.clear()
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

    /**
     * Reset Talked About Layout
     * @see hsvInfoYouTalkedAbout view with related values in ViewModel
     * */
    private fun resetTalkedRelatedToTopic() {
        viewModel.resetTalkedAbout()
        if (btnPrevention.isSelected) changeButtonSelectionWithIcon(btnPrevention)
        if (btnAccess.isSelected) changeButtonSelectionWithIcon(btnAccess)
        if (btnDetection.isSelected) changeButtonSelectionWithIcon(btnDetection)
    }

    /** Reset Given Button*/
    private fun resetButton(button: MaterialButton) {
        button.isSelected = false
        button.setTextColor(normalColor)
        button.strokeColor = ColorStateList.valueOf(normalColor)
        button.icon = null
    }

    /** Reset
     * @see popupMedicalHistorySrCitizenAdapter,
     * @see rvCategoryAdapter
     * @see rvCategoryAdapter
     * @see rvMedicalHistorySrCitizenAdapter
     * all the adapters with related values in ViewModel
     * */
    private fun resetAdapters() {
        viewModel.resetMedicalHistory()
        popupMedicalHistorySrCitizenAdapter.resetAdapter()
        rvMedicalHistorySrCitizenAdapter.resetAdapter()

        viewModel.resetComplaints()
        popupCategoryAdapter.resetAdapter()
        rvCategoryAdapter.resetAdapter()
    }

    /**
     * Reset Register New Senior Citizen layout
     * @see registerNewSrCitizen view with related values in ViewModel
     * */
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

    /** Reset
     * @see hsvCovidSymptoms view with related values in ViewModel
     * */
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

    /**
     * Reset all the AutoCompleteTextViewes useed in Layout with related values in ViewModel
     * */
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

    /**
     * Manage visibility of
     * @see cgMedicalDetails view with the data including resetting views and Data of Talked With
     * */
    private fun manageViewVisibility() {
        if (viewModel.getTalkedWith() == TalkedWithCommunityMember) {
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
        updateOnTalkedAbout(btnPrevention, TalkedAboutPrevention) // getString(R.string.prevention)
        updateOnTalkedAbout(btnAccess, TalkedAboutAccess) // getString(R.string.access)
        updateOnTalkedAbout(btnDetection, TalkedAboutDetection) // getString(R.string.detection)
    }

    private fun updateOnTalkedAbout(button: MaterialButton, tag: String) {
        if (button.isSelected) viewModel.addTalkedAbout(tag)
        else viewModel.removeTalkedAbout(tag)
    }

    /**
     * Manage visibility of
     * @see cgComplaintDetails view with the data including resetting views with related values
     * in ViewModel
     * */
    private fun manageOtherMedicalRelatedViews() {
        when (viewModel.getOtherMedicalProblem()) {
            getString(R.string.covid_19_symptoms) -> {
                cgComplaintDetails.visibility = View.VISIBLE

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
                cgComplaintDetails.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Show popup when user taps on
     * @see rvMedicalHistorySrCitizen,
     * @see rvCategory to provide selection of items
     * */
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

    /** Observe change in data and initiate appropriate action*/
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

    /**
     * If talked with is Sr. Citizen/Family Member then we will first validate all the fields
     * just to verify and pass proper values
     **/
    private fun validateFields(): Boolean {
        if (viewModel.getCallStatus().isEmpty()) {
            BaseUtility.showAlertMessage(this, R.string.error, R.string.validate_call_status)
            return false
        }
        if (viewModel.getCallStatus() == "9" && edt_call_status_comment.text.toString().isEmpty()) {
            BaseUtility.showAlertMessage(this, R.string.error, R.string.provide_remarks)
            return false
        }
        if (viewModel.getCallStatus() == "10") {
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
            } else if (viewModel.isAwareOfCovid19() == "") {
                BaseUtility.showAlertMessage(
                    this, R.string.error, R.string.validate_is_aware_of_covid_symptoms
                )
                return false
            } else if ((viewModel.isAwareOfCovid19() == NO) && (viewModel.isSymptomsPreventionDiscussed() == "")) {
                BaseUtility.showAlertMessage(
                    this, R.string.error, R.string.validate_shared_knowledge_of_covid_symptoms
                )
                return false
            } else if (viewModel.getBehaviorChange().isEmpty()) {
                BaseUtility.showAlertMessage(
                    this, R.string.error, R.string.validate_behavioural_changes
                )
                return false
            } else if (((viewModel.getBehaviorChange() == NO) ||
                        (viewModel.getBehaviorChange() == BehaviorChangeMayBe)) &&
                viewModel.getPracticeNotAllowed().isEmpty()
            ) {
                BaseUtility.showAlertMessage(
                    this, R.string.error, R.string.validate_practices_not_followed
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
            } else if ((viewModel.getComplaints().isEmpty()) &&
                (viewModel.isLackOfEssential() == YES)
            ) {
                BaseUtility.showAlertMessage(
                    this, R.string.error, R.string.validate_essential_services
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

    /**
     * If talked with is Community Member then from here it self user can register new Sr. Citizen
     * and before calling api here we you validate all the fields so that we can pass valid values
     * in API
     **/
    private fun validateSrCitizenRegistrationForm(): Boolean = when {
        etName.text.toString().isEmpty() -> {
            tvNameError.visibility = View.VISIBLE
            false
        }
        etAge.text.toString().isEmpty() -> {
            tvAgeError.visibility = View.VISIBLE
            false
        }
        etAge.text.toString().toInt() < 60 -> {
            tvAgeError.visibility = View.VISIBLE
            false
        }
        etAge.text.toString().toInt() > 110 -> {
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

    /** Prepare Parameters to be passed in API to save the details*/
    private fun preparePostParams(): JsonObject {
        val params = JsonObject()
        params.addProperty(ApiConstants.CallId, callId.toInt())
        syncData.callId = callId.toInt()

        params.addProperty(ApiConstants.VolunteerId, dataManager.getUserId())
        syncData.volunteerId = dataManager.getUserId()

        //val callStatus = if (viewModel.getCallStatus() == "5") "2" else "1"
        syncData.callStatusSubCode = viewModel.getCallStatus()
        params.addProperty(ApiConstants.SrCitizenCallStatusCode, viewModel.getCallStatus())

        syncData.talkedWith = viewModel.getTalkedWith()
        params.addProperty(ApiConstants.SrCitizenTalkedWith, syncData.talkedWith)

        params.addProperty(ApiConstants.SrCitizenName, dataManager.getUserName())
        syncData.srCitizenName = dataManager.getUserName()

        params.addProperty(ApiConstants.SrCitizenGender, dataManager.getSrCitizenGender())
        syncData.gender = dataManager.getSrCitizenGender()

        if (viewModel.getCallStatus() == "9") {
            syncData.impRemarkInfo = edt_call_status_comment.text.toString()
            params.addProperty(ApiConstants.ImpRemarkInfo, edt_call_status_comment.text.toString())
        }

        if (viewModel.getCallStatus() != "10") return params

        syncData.impRemarkInfo = etOtherDescription.text.toString()
        params.addProperty(ApiConstants.ImpRemarkInfo, etOtherDescription.text.toString())
        val arraySubParams = JsonObject()
        arraySubParams.addProperty(ApiConstants.CallId, callId.toInt())
        arraySubParams.addProperty(ApiConstants.VolunteerId, dataManager.getUserId())

        var dataList = viewModel.getMedicalHistory()
        if (dataList.any { it == getString(R.string.no_problems) }) {
            arraySubParams.addProperty(ApiConstants.IsDiabetic, NO)
            syncData.hasDiabetic = NO

            arraySubParams.addProperty(ApiConstants.IsBloodPressure, NO)
            syncData.hasBloodPressure = NO

            arraySubParams.addProperty(ApiConstants.LungAilment, NO)
            syncData.hasLungAilment = NO

            arraySubParams.addProperty(ApiConstants.CancerOrMajorSurgery, NO)
            syncData.cancerOrMajorSurgery = NO
        } else {
            syncData.hasDiabetic = if (dataList.any { it == getString(R.string.diabetes) }) YES
            else NO
            arraySubParams.addProperty(ApiConstants.IsDiabetic, syncData.hasDiabetic)

            syncData.hasBloodPressure =
                if (dataList.any { it == getString(R.string.blood_pressure) }) YES else NO
            arraySubParams.addProperty(ApiConstants.IsBloodPressure, syncData.hasBloodPressure)

            syncData.hasLungAilment =
                if (dataList.any { it == getString(R.string.lung_ailment) }) YES else NO
            arraySubParams.addProperty(ApiConstants.LungAilment, syncData.hasLungAilment)

            syncData.cancerOrMajorSurgery =
                if (dataList.any { it == getString(R.string.cancer_major_surgery) }) YES else NO
            arraySubParams.addProperty(
                ApiConstants.CancerOrMajorSurgery, syncData.cancerOrMajorSurgery
            )
        }

        arraySubParams.addProperty(ApiConstants.OtherAilments, NO)
        syncData.otherAilments = NO

        arraySubParams.addProperty(ApiConstants.RemarkOnMedicalHistory, "")
        syncData.remarksMedicalHistory = ""

        syncData.relatedInfoTalkedAbout = if (viewModel.getTalkedAbout().isEmpty()) ""
        else TextUtils.join(",", viewModel.getTalkedAbout())
        arraySubParams.addProperty(ApiConstants.InfoTalkAbout, syncData.relatedInfoTalkedAbout)

        syncData.hasSrCitizenAwareOfCovid19 = viewModel.isAwareOfCovid19()
            .toUpperCase(Locale.ENGLISH)
        arraySubParams.addProperty(
            ApiConstants.IsSrCitizenAwareOfCovid19, syncData.hasSrCitizenAwareOfCovid19
        )

        /*syncData.hasSymptomsPreventionDiscussed = if (syncData.hasSrCitizenAwareOfCovid19 != YES)
            viewModel.isSymptomsPreventionDiscussed().toUpperCase(Locale.ENGLISH) else ""
        arraySubParams.addProperty(
            ApiConstants.IsSymptomsPreventionDiscussed, syncData.hasSymptomsPreventionDiscussed
        )*/
        syncData.hasSymptomsPreventionDiscussed = if (syncData.hasSrCitizenAwareOfCovid19 != YES)
            viewModel.isSymptomsPreventionDiscussed() else ""
        arraySubParams.addProperty(
            ApiConstants.IsSymptomsPreventionDiscussed, syncData.hasSymptomsPreventionDiscussed
        )

        syncData.behavioralChangesNoticed = viewModel.getBehaviorChange()
        arraySubParams.addProperty(
            ApiConstants.NoticedBehaviouralChange, syncData.behavioralChangesNoticed
        )

        syncData.whichPracticesNotFollowed = viewModel.getPracticeNotAllowed()
        arraySubParams.addProperty(
            ApiConstants.WhichPracticeNotFollowed, syncData.whichPracticesNotFollowed
        )

        syncData.hasCovidSymptoms = if (viewModel.isCovideSymptoms()) YES else NO
        arraySubParams.addProperty(ApiConstants.HasCovidSymptoms, syncData.hasCovidSymptoms)

        syncData.hasCough = if (viewModel.isCoughSymptoms()) YES else NO
        arraySubParams.addProperty(ApiConstants.HasCough, syncData.hasCough)

        syncData.hasFever = if (viewModel.isFeverSymptom()) YES else NO
        arraySubParams.addProperty(ApiConstants.HasFever, syncData.hasFever)

        syncData.hasShortnessOfBreath = if (viewModel.isShortBreathSymptoms()) YES else NO
        arraySubParams.addProperty(ApiConstants.HasShortnessOfBreath, syncData.hasShortnessOfBreath)

        syncData.hasSoreThroat = if (viewModel.isSoreThroatSymptom()) YES else NO
        arraySubParams.addProperty(ApiConstants.HasSoreThroat, syncData.hasSoreThroat)

        syncData.quarantineStatus = viewModel.getQuarantineStatus()
        arraySubParams.addProperty(ApiConstants.QuarantineStatus, syncData.quarantineStatus!!)

        dataList = viewModel.getComplaints()
        /*if (dataList.any { it == getString(R.string.no_problems) }) {
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
        } else {*/
        syncData.foodShortage =
            if (dataList.any { it == getString(R.string.lack_of_food) }) "1" else "4"
        arraySubParams.addProperty(ApiConstants.FoodShortage, syncData.foodShortage!!.toInt())

        syncData.medicineShortage =
            if (dataList.any { it == getString(R.string.lack_of_medicine) }) "1" else "4"
        arraySubParams.addProperty(
            ApiConstants.MedicineShortage, syncData.medicineShortage!!.toInt()
        )

        syncData.accessToBankingIssue =
            if (dataList.any { it == getString(R.string.lack_of_banking_service) }) "1" else "4"
        arraySubParams.addProperty(
            ApiConstants.AccessToBankingIssue, syncData.accessToBankingIssue!!.toInt()
        )

        syncData.utilitySupplyIssue =
            if (dataList.any { it == getString(R.string.lack_of_utilities) }) "1" else "4"
        arraySubParams.addProperty(ApiConstants.UtilityIssue, syncData.utilitySupplyIssue!!.toInt())

        syncData.hygieneIssue =
            if (dataList.any { it == getString(R.string.lack_of_hygine) }) "1" else "4"
        arraySubParams.addProperty(ApiConstants.HygieneIssue, syncData.hygieneIssue!!.toInt())

        syncData.safetyIssue =
            if (dataList.any { it == getString(R.string.lack_of_safety) }) "1" else "4"
        arraySubParams.addProperty(ApiConstants.SafetyIssue, syncData.safetyIssue!!.toInt())

        syncData.emergencyServiceIssue =
            if (dataList.any { it == getString(R.string.lack_of_access_emergency) }) "1" else "4"
        arraySubParams.addProperty(
            ApiConstants.EmergencyServiceIssue, syncData.emergencyServiceIssue!!.toInt()
        )

        syncData.phoneAndInternetIssue =
            if (dataList.any { it == getString(R.string.phone_and_service) }) "1" else "4"
        arraySubParams.addProperty(
            ApiConstants.PhoneInternetIssue, syncData.phoneAndInternetIssue!!.toInt()
        )

        syncData.description = etDescription.text.toString()
        arraySubParams.addProperty(ApiConstants.Description, syncData.description)
        //}

        syncData.emergencyServiceRequired = viewModel.isEmergencyEscalation()
        arraySubParams.addProperty(
            ApiConstants.IsEmergencyServicesRequired, syncData.emergencyServiceRequired
        )
        syncData.lackOfEssentialServices = viewModel.isLackOfEssential()
        arraySubParams.addProperty(
            ApiConstants.LackOfEssentialServices, syncData.lackOfEssentialServices
        )

        val jsonArray = JsonArray()
        jsonArray.add(arraySubParams)

        params.add(ApiConstants.MedicalGrievances, jsonArray)

        println("TAG -- MyData --> $params")

        return params
    }
}