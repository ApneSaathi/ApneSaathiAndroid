package com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenfeedbackform

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.nitiaayog.apnesaathi.ApneSaathiApplication
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.PopupAdapter
import com.nitiaayog.apnesaathi.adapter.SimpleBaseAdapter
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.model.FormElements
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import com.nitiaayog.apnesaathi.utility.USER_DETAILS
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_senior_citizen_feedback_form.*
import kotlinx.android.synthetic.main.include_recyclerview.view.*
import kotlinx.android.synthetic.main.include_register_new_sr_citizen.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.util.concurrent.TimeUnit

class SeniorCitizenFeedbackFormActivity : BaseActivity<SeniorCitizenFeedbackViewModel>() {

    private var selectedTalkedWith: String = ""
    private var selectedMedicalProblem: String = ""
    private var selectedBehaviorChange: String = ""
    private var selectedOtherMedicalProblem: String = ""
    private var selectedQuarantineStatus: String = ""
    private var selectedComplaintCategory: String = ""
    private var selectedEssentialServices: String = ""

    private var selectedGender: String = ""
    private var selectedDistrict: String = ""
    private var selectedState: String = ""

    private var isSeniorCitizenAtHome: Boolean = false
    private var isLackOfEssentialServices: Boolean = false
    private var isAnyEmergency: Boolean = false

    private var selectedCallStatusButton: MaterialButton? = null
    private var selectedLackOfEssentialServices: MaterialButton? = null
    private var selectedNeedOfEmergencyServices: MaterialButton? = null

    private val selectedColor: Int by lazy { ContextCompat.getColor(this, R.color.color_orange) }
    private val normalColor: Int by lazy {
        ContextCompat.getColor(this, R.color.color_dark_grey_txt)
    }

    // List Medical History
    private val popupMedicalHistoryList: MutableList<FormElements> = mutableListOf()
    private val popupMedicalHistorySrCitizenAdapter: PopupAdapter by lazy {
        PopupAdapter(popupMedicalHistoryList).apply {
            this.setOnItemClickListener(
                object : PopupAdapter.OnItemClickListener {
                    override fun onItemClicked(position: Int, formElement: FormElements) {
                        if (formElement.isSelected)
                            rvMedicalHistorySrCitizenAdapter.addItem(formElement.name)
                        else rvMedicalHistorySrCitizenAdapter.removeItem(formElement.name)

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
                    rvMedicalHistorySrCitizen.visibility =
                        if (rvMedicalHistorySrCitizenAdapter.itemCount > 0) View.VISIBLE
                        else View.GONE
                }

                override fun showPopup() {
                    showPopupWindow(llMedicalHistorySrCitizen)
                }
            })
        }
    }

    // List Category
    private val popupCategoryList: MutableList<FormElements> = mutableListOf()
    private val popupCategoryAdapter: PopupAdapter by lazy {
        PopupAdapter(popupCategoryList).apply {
            this.setOnItemClickListener(
                object : PopupAdapter.OnItemClickListener {
                    override fun onItemClicked(position: Int, formElement: FormElements) {
                        if (formElement.isSelected) rvCategoryAdapter.addItem(formElement.name)
                        else rvCategoryAdapter.removeItem(formElement.name)

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
            val user: User? = intent.getParcelableExtra(USER_DETAILS)
            user?.let {
                val address = getString(R.string.address).plus(" : ")
                val dataString = address.plus(it.block).plus(", ").plus(it.district)
                    .plus(", ").plus(it.state)
                val spanAddress = SpannableString(dataString)
                spanAddress.setSpan(StyleSpan(Typeface.BOLD), 0, address.length, 0)
                spanAddress.setSpan(StyleSpan(Typeface.ITALIC), 0, address.length, 0)

                tvSrCitizenName.text = it.userName.plus("(").plus(it.age).plus(" Yrs)")
                tvSrCitizenPhoneNumber.text = it.phoneNumber
                tvSrCitizenAddress.text = spanAddress
            }
        }
    }

    private fun initAutoCompleteTextViews() {
        val genderList = resources.getStringArray(R.array.gender_array)
        val gendersAdapter = ArrayAdapter(this, R.layout.item_layout_dropdown_menu, genderList)
        actGender.threshold = 0
        actGender.setAdapter(gendersAdapter)
        actGender.setOnKeyListener(null)
        actGender.setOnItemClickListener { _, _, position, _ ->
            selectedGender = genderList[position]
        }

        val districtsList = resources.getStringArray(R.array.districts_array)
        val districtsAdapter = ArrayAdapter(this, R.layout.item_layout_dropdown_menu, districtsList)
        actDistrict.threshold = 0
        actDistrict.setAdapter(districtsAdapter)
        actDistrict.setOnKeyListener(null)
        actDistrict.setOnItemClickListener { _, _, position, _ ->
            selectedDistrict = districtsList[position]
        }

        val stateList = resources.getStringArray(R.array.states_array)
        val stateAdapter = ArrayAdapter(this, R.layout.item_layout_dropdown_menu, stateList)
        actState.threshold = 0
        actState.setAdapter(stateAdapter)
        actState.setOnKeyListener(null)
        actState.setOnItemClickListener { _, _, position, _ ->
            selectedState = stateList[position]
        }

        val rvMedicalHistorySrCitizen = rvMedicalHistorySrCitizen as RecyclerView
        rvMedicalHistorySrCitizen.adapter = rvMedicalHistorySrCitizenAdapter

        val rvCategory = rvCategory as RecyclerView
        rvCategory.adapter = rvCategoryAdapter

        val talkedWithList = resources.getStringArray(R.array.talked_with)
        val statesAdapter = ArrayAdapter(this, R.layout.item_layout_dropdown_menu, talkedWithList)
        actTalkWith.threshold = 0
        actTalkWith.setAdapter(statesAdapter)
        actTalkWith.setOnKeyListener(null)
        actTalkWith.setOnDismissListener {
            updateDropDownIndicator(actTalkWith, R.drawable.ic_arrow_down)
        }
        actTalkWith.setOnItemClickListener { _, _, position, _ ->
            if (selectedTalkedWith == getString(R.string.community_member)) {
                resetRegisterNewSrCitizenLayout()
                resetAllFormElements()
            }
            selectedTalkedWith = talkedWithList[position]
            manageViewVisibility()
        }

        val behaviorChangesList = resources.getStringArray(R.array.behavior_changes)
        val behaviorChangesAdapter =
            ArrayAdapter(this, R.layout.item_layout_dropdown_menu, behaviorChangesList)
        actBehaviorChange.threshold = 0
        actBehaviorChange.setOnDismissListener { }
        actBehaviorChange.setAdapter(behaviorChangesAdapter)
        actBehaviorChange.setOnKeyListener(null)
        actBehaviorChange.setOnDismissListener {
            updateDropDownIndicator(actBehaviorChange, R.drawable.ic_arrow_down)
        }
        actBehaviorChange.setOnItemClickListener { _, _, position, _ ->
            selectedBehaviorChange = behaviorChangesList[position]
        }

        val otherMedicalProblemsList = resources.getStringArray(R.array.other_medical_problems_list)
        val otherMedicalProblemsAdapter =
            ArrayAdapter(this, R.layout.item_layout_dropdown_menu, otherMedicalProblemsList)
        actOtherMedicalProblems.threshold = 0
        actOtherMedicalProblems.setAdapter(otherMedicalProblemsAdapter)
        actOtherMedicalProblems.setOnKeyListener(null)
        actOtherMedicalProblems.setOnDismissListener {
            updateDropDownIndicator(actOtherMedicalProblems, R.drawable.ic_arrow_down)
        }
        actOtherMedicalProblems.setOnItemClickListener { _, _, position, _ ->
            selectedOtherMedicalProblem = otherMedicalProblemsList[position]
            manageOtherMedicalRelatedViews()
        }

        val quarantineStatusList = resources.getStringArray(R.array.quarantine_status_array)
        val quarantineStatusAdapter =
            ArrayAdapter(this, R.layout.item_layout_dropdown_menu, quarantineStatusList)
        actQuarantineHospitalizationStatus.threshold = 0
        actQuarantineHospitalizationStatus.setAdapter(quarantineStatusAdapter)
        actQuarantineHospitalizationStatus.setOnKeyListener(null)
        actQuarantineHospitalizationStatus.setOnDismissListener {
            updateDropDownIndicator(actQuarantineHospitalizationStatus, R.drawable.ic_arrow_down)
        }
        actQuarantineHospitalizationStatus.setOnItemClickListener { _, _, position, _ ->
            selectedQuarantineStatus = behaviorChangesList[position]
        }
    }

    private fun initClicks() {
        // Call Status Button Clicks
        btnNoResponse.throttleClick().subscribe { manageResetForm(btnNoResponse) }
            .autoDispose(disposables)
        btnNotPicked.throttleClick().subscribe { manageResetForm(btnNotPicked) }
            .autoDispose(disposables)
        btnNotReachable.throttleClick().subscribe { manageResetForm(btnNotReachable) }
            .autoDispose(disposables)
        btnDisConnected.throttleClick().subscribe { manageResetForm(btnDisConnected) }
            .autoDispose(disposables)
        btnConnected.throttleClick().subscribe {
            changeButtonSelection(btnConnected)
            tvTalkWith.visibility = View.VISIBLE
            actTalkWith.visibility = View.VISIBLE
        }.autoDispose(disposables)

        // Is Sr citizen at home
        btnAnySrCitizenInHomeYes.throttleClick().subscribe {
            isSeniorCitizenAtHome = true
            toggleSelectionForSrCitizenAtHome(btnAnySrCitizenInHomeYes, btnAnySrCitizenInHomeNo)
            tvNewSrCitizenPersonalDetails.visibility = View.VISIBLE
            registerNewSrCitizen.visibility = View.VISIBLE
        }.autoDispose(disposables)
        btnAnySrCitizenInHomeNo.throttleClick().subscribe {
            isSeniorCitizenAtHome = false
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
            isLackOfEssentialServices = true
            selectedLackOfEssentialServices =
                toggleButtonSelection(
                    btnLackOfEssentialServicesYes, selectedLackOfEssentialServices
                )
        }.autoDispose(disposables)
        btnLackOfEssentialServicesNo.throttleClick().subscribe {
            isLackOfEssentialServices = false
            selectedLackOfEssentialServices =
                toggleButtonSelection(btnLackOfEssentialServicesNo, selectedLackOfEssentialServices)
        }.autoDispose(disposables)

        // is Sr. citizen is in any Emergency ?
        btnEmergencyYes.throttleClick().subscribe {
            isAnyEmergency = true
            selectedNeedOfEmergencyServices =
                toggleButtonSelection(btnEmergencyYes, selectedNeedOfEmergencyServices)
        }.autoDispose(disposables)
        btnEmergencyNo.throttleClick().subscribe {
            isAnyEmergency = false
            selectedNeedOfEmergencyServices =
                toggleButtonSelection(btnEmergencyNo, selectedNeedOfEmergencyServices)
        }.autoDispose(disposables)

        // Button to save the data or go back
        btnSave.throttleClick().subscribe {
            BaseUtility.showAlertMessage(this, R.string.success, R.string.sr_feedback_saved)
            /*if (selectedTalkedWith == getString(R.string.community_member))
                viewModel.saveSeniorCitizenFeedback(this)
            else viewModel.saveSeniorCitizenFeedback(this)*/
        }.autoDispose(disposables)
        btnCancelMe.throttleClick().subscribe {
            BaseUtility.showAlertMessage(this, R.string.alert, R.string.data_not_saved,
                onPositiveBtnClick = { dialog, _ -> },
                onNegativeBtnClick = { dialog, _ -> dialog.dismiss() }
            )
        }.autoDispose(disposables)
    }

    private fun changeButtonSelection(button: MaterialButton) {
        if ((selectedCallStatusButton != null) && (selectedCallStatusButton == button)) return

        button.apply { updateButtonState(this) }

        selectedCallStatusButton?.apply {
            this.isSelected = false
            this.strokeColor = ColorStateList.valueOf(normalColor)
            this.setTextColor(normalColor)
        }

        selectedCallStatusButton = button
    }

    private fun changeButtonSelectionWithIcon(button: MaterialButton) =
        button.apply { updateButtonState(this, true) }

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
        selectedTalkedWith = ""
        selectedMedicalProblem = ""
        selectedBehaviorChange = ""
        selectedOtherMedicalProblem = ""
        selectedQuarantineStatus = ""
        selectedComplaintCategory = ""
        selectedEssentialServices = ""

        isSeniorCitizenAtHome = false
        isLackOfEssentialServices = false
        isAnyEmergency = false

        resetAdapters()
        resetAutoCompleteTextView()
        resetCovidSymptomsViews()
        resetTalkedRelatedToTopic()

        etDescription.text.clear()
        etDescription.clearFocus()

        etOtherDescription.text.clear()
        etOtherDescription.clearFocus()

        if (btnLackOfEssentialServicesYes.isSelected)
            toggleButtonSelection(btnLackOfEssentialServicesYes, null)
        if (btnLackOfEssentialServicesNo.isSelected)
            toggleButtonSelection(btnLackOfEssentialServicesNo, null)

        if (btnEmergencyYes.isSelected) toggleButtonSelection(btnEmergencyYes, null)
        if (btnEmergencyNo.isSelected) toggleButtonSelection(btnEmergencyNo, null)

        selectedLackOfEssentialServices = null
        selectedNeedOfEmergencyServices = null
    }

    private fun resetForm() {
        tvTalkWith.visibility = View.GONE
        actTalkWith.visibility = View.GONE

        if (selectedTalkedWith == getString(R.string.community_member)) {
            resetRegisterNewSrCitizenLayout()
            return
        }

        if (selectedOtherMedicalProblem == getString(R.string.non_covid_symptoms)) {
            tvQuarantineHospitalizationStatus.visibility = View.GONE
            actQuarantineHospitalizationStatus.visibility = View.GONE
        }

        actTalkWith.setText("")
        updateDropDownIndicator(actTalkWith, R.drawable.ic_arrow_down)

        /*selectedTalkedWith = ""
        selectedMedicalProblem = ""
        selectedBehaviorChange = ""
        selectedOtherMedicalProblem = ""
        selectedQuarantineStatus = ""
        selectedComplaintCategory = ""
        selectedEssentialServices = ""

        isSeniorCitizenAtHome = false
        isLackOfEssentialServices = false
        isAnyEmergency = false*/

        //resetAdapters()

        resetAllFormElements()

        cgMedicalDetails.visibility = View.GONE
        cgComplaintDetails.visibility = View.GONE

        if (btnAnySrCitizenInHomeYes.isSelected)
            toggleSelectionForSrCitizenAtHome(btnAnySrCitizenInHomeYes, btnAnySrCitizenInHomeNo)
        if (btnAnySrCitizenInHomeNo.isSelected)
            toggleSelectionForSrCitizenAtHome(btnAnySrCitizenInHomeNo, btnAnySrCitizenInHomeYes)

        //resetTalkedRelatedToTopic()

        /*if (btnLackOfEssentialServicesYes.isSelected)
            toggleButtonSelection(btnLackOfEssentialServicesYes, null)
        if (btnLackOfEssentialServicesNo.isSelected)
            toggleButtonSelection(btnLackOfEssentialServicesNo, null)*/

        /*if (btnEmergencyYes.isSelected) toggleButtonSelection(btnEmergencyYes, null)
        if (btnEmergencyNo.isSelected) toggleButtonSelection(btnEmergencyNo, null)*/

        /*resetAutoCompleteTextView()
        resetCovidSymptomsViews()

        etDescription.text.clear()
        etDescription.clearFocus()

        etOtherDescription.text.clear()
        etOtherDescription.clearFocus()

        selectedLackOfEssentialServices = null
        selectedNeedOfEmergencyServices = null*/
    }

    private fun resetTalkedRelatedToTopic() {
        if (btnPrevention.isSelected) changeButtonSelectionWithIcon(btnPrevention)
        if (btnAccess.isSelected) changeButtonSelectionWithIcon(btnAccess)
        if (btnDetection.isSelected) changeButtonSelectionWithIcon(btnDetection)
    }

    private fun resetCallStatus() {
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
        popupMedicalHistorySrCitizenAdapter.resetAdapter()
        rvMedicalHistorySrCitizenAdapter.resetAdapter()
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

        actGender.setText("")
        actDistrict.setText("")
        actState.setText("")

        etName.text.clear()
        etAge.text.clear()
        etContactNumber.text.clear()
        etAddress.text.clear()

        selectedGender = ""
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
        actDistrict.setText("")
        actState.setText("")

        actBehaviorChange.setText("")
        updateDropDownIndicator(actBehaviorChange, R.drawable.ic_arrow_down)

        actOtherMedicalProblems.setText("")
        updateDropDownIndicator(actOtherMedicalProblems, R.drawable.ic_arrow_down)

        actQuarantineHospitalizationStatus.setText("")
        updateDropDownIndicator(actQuarantineHospitalizationStatus, R.drawable.ic_arrow_down)
    }

    private fun manageViewVisibility() {
        if (selectedTalkedWith == getString(R.string.community_member)) {
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

    private fun manageOtherMedicalRelatedViews() {
        when (selectedOtherMedicalProblem) {
            getString(R.string.covid_19_symptoms) -> {
                cgComplaintDetails.visibility = View.GONE

                tvCovidSymptoms.visibility = View.VISIBLE
                hsvCovidSymptoms.visibility = View.VISIBLE
                tvQuarantineHospitalizationStatus.visibility = View.VISIBLE
                actQuarantineHospitalizationStatus.visibility = View.VISIBLE
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
            (ApneSaathiApplication.screenSize[1] * 0.50).toInt()
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
}