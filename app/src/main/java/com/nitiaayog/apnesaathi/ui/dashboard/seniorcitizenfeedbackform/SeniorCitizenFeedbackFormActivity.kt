package com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenfeedbackform

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.MultiAutoCompleteTextView.CommaTokenizer
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.utility.BaseUtility
import kotlinx.android.synthetic.main.include_register_new_sr_citizen.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.senior_citizen_feedback_form.*

class SeniorCitizenFeedbackFormActivity : BaseActivity<SeniorCitizenFeedbackViewModel>() {

    private var selectedTalkedWith: String = ""
    private var selectedMedicalProblem: String = ""
    private var selectedBehaviorChange: String = ""
    private var selectedOtherMedicalProblem: String = ""
    private var selectedQuarantineStatus: String = ""
    private var selectedComplaintCategory: String = ""
    private var selectedEssentialServices: String = ""

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_back)
        }
        toolBar.setNavigationOnClickListener { finish() }

        tvCancel.visibility = View.GONE
        tvRegister.visibility = View.GONE

        initAutoCompleteTextViews()
        initClicks()
    }

    override fun provideViewModel(): SeniorCitizenFeedbackViewModel =
        getViewModel { SeniorCitizenFeedbackViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.senior_citizen_feedback_form

    private fun initAutoCompleteTextViews() {
        val talkedWithList = resources.getStringArray(R.array.talked_with)
        val statesAdapter =
            ArrayAdapter(this, R.layout.item_layout_dropdown_menu, talkedWithList)
        actTalkWith.threshold = 0
        actTalkWith.setAdapter(statesAdapter)
        actTalkWith.setOnKeyListener(null)
        actTalkWith.setOnItemClickListener { _, _, position, _ ->
            selectedTalkedWith = talkedWithList[position]
            manageViewVisibility()
        }

        val medicalProblemsList = resources.getStringArray(R.array.medical_problems)
        val medicalListAdapter =
            ArrayAdapter(this, R.layout.item_layout_dropdown_menu, medicalProblemsList)
        actMedicalHistorySrCitizen.threshold = 0
        actMedicalHistorySrCitizen.setTokenizer(CommaTokenizer())
        actMedicalHistorySrCitizen.setAdapter(medicalListAdapter)
        actMedicalHistorySrCitizen.setOnKeyListener(null)
        actMedicalHistorySrCitizen.setOnItemClickListener { _, _, position, _ ->
            selectedMedicalProblem = medicalProblemsList[position]
        }

        val behaviorChangesList = resources.getStringArray(R.array.behavior_changes)
        val behaviorChangesAdapter =
            ArrayAdapter(this, R.layout.item_layout_dropdown_menu, behaviorChangesList)
        actBehaviorChange.threshold = 0
        actBehaviorChange.setAdapter(behaviorChangesAdapter)
        actBehaviorChange.setOnKeyListener(null)
        actBehaviorChange.setOnItemClickListener { _, _, position, _ ->
            selectedBehaviorChange = behaviorChangesList[position]
        }

        val otherMedicalProblemsList = resources.getStringArray(R.array.other_medical_problems_list)
        val otherMedicalProblemsAdapter =
            ArrayAdapter(this, R.layout.item_layout_dropdown_menu, otherMedicalProblemsList)
        actOtherMedicalProblems.threshold = 0
        actOtherMedicalProblems.setAdapter(otherMedicalProblemsAdapter)
        actOtherMedicalProblems.setOnKeyListener(null)
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
        actQuarantineHospitalizationStatus.setOnItemClickListener { _, _, position, _ ->
            selectedQuarantineStatus = behaviorChangesList[position]
        }

        val categoryList = resources.getStringArray(R.array.grievance_array)
        val categoryAdapter = ArrayAdapter(this, R.layout.item_layout_dropdown_menu, categoryList)
        actCategory.threshold = 0
        actCategory.setTokenizer(CommaTokenizer())
        actCategory.setAdapter(categoryAdapter)
        actCategory.setOnKeyListener(null)
        actCategory.setOnItemClickListener { _, _, position, _ ->
            selectedComplaintCategory = categoryList[position]
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

        // All AutoCompleteTextView clicks
        actTalkWith.throttleClick().subscribe { actTalkWith.showDropDown() }
            .autoDispose(disposables)
        actMedicalHistorySrCitizen.throttleClick()
            .subscribe { actMedicalHistorySrCitizen.showDropDown() }.autoDispose(disposables)
        actOtherMedicalProblems.throttleClick().subscribe { actOtherMedicalProblems.showDropDown() }
            .autoDispose(disposables)
        actBehaviorChange.throttleClick().subscribe { actBehaviorChange.showDropDown() }
            .autoDispose(disposables)
        actQuarantineHospitalizationStatus.throttleClick()
            .subscribe { actQuarantineHospitalizationStatus.showDropDown() }
            .autoDispose(disposables)
        actCategory.throttleClick().subscribe { actCategory.showDropDown() }
            .autoDispose(disposables)

        // Had discussion about Prevention/Access/Detection
        btnPrevention.throttleClick().subscribe { changeButtonSelectionWithIcon(btnPrevention) }
            .autoDispose(disposables)
        btnAccess.throttleClick().subscribe { changeButtonSelectionWithIcon(btnAccess) }
            .autoDispose(disposables)
        btnDetection.throttleClick().subscribe { changeButtonSelectionWithIcon(btnDetection) }
            .autoDispose(disposables)

        // Which COVID-19 symptopms Sr. citizen is having
        llCough.throttleClick().subscribe { changeCovidSymptomsSelection(ivCough, tvCough) }
            .autoDispose(disposables)
        llFever.throttleClick().subscribe { changeCovidSymptomsSelection(ivFever, tvFever) }
            .autoDispose(disposables)
        llShortOfBreath.throttleClick().subscribe {
            changeCovidSymptomsSelection(ivShortnessOfBreath, tvShortnessOfBreath)
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
        }.autoDispose(disposables)
        btnCancelMe.throttleClick().subscribe {
            showAlertMessage(R.string.alert, R.string.data_not_saved)
        }.autoDispose(disposables)
    }

    private fun changeButtonSelection(button: MaterialButton, isReset: Boolean = false) {
        if (!isReset && (selectedCallStatusButton != null) && (selectedCallStatusButton == button))
            return

        button.apply { updateButtonState(this) }

        selectedCallStatusButton?.apply {
            this.isSelected = false
            this.strokeColor = ColorStateList.valueOf(normalColor)
            this.setTextColor(normalColor)
        }

        selectedCallStatusButton = if (!isReset) button else null
    }

    private fun changeButtonSelectionWithIcon(button: MaterialButton) =
        button.apply { updateButtonState(this, true) }

    private fun changeCovidSymptomsSelection(imageView: ImageView, textView: TextView) {
        imageView.isSelected = !imageView.isSelected
        textView.setTextColor(if (imageView.isSelected) selectedColor else normalColor)
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
                button.icon = ContextCompat.getDrawable(
                    this@SeniorCitizenFeedbackFormActivity, R.drawable.ic_checked_circle
                )
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

        cgMedicalDetails.visibility = View.GONE
        cgComplaintDetails.visibility = View.GONE

        if (btnPrevention.isSelected) changeButtonSelectionWithIcon(btnPrevention)
        if (btnAccess.isSelected) changeButtonSelectionWithIcon(btnAccess)
        if (btnDetection.isSelected) changeButtonSelectionWithIcon(btnDetection)

        resetAutoCompleteTextView()
        resetCovidSymptomsViews()

        etDescription.text.clear()
        etDescription.clearFocus()

        etOtherDescription.text.clear()
        etOtherDescription.clearFocus()

        selectedCallStatusButton = null
        selectedLackOfEssentialServices = null
        selectedNeedOfEmergencyServices = null
    }

    private fun resetCallStatus() {
        if (btnDisConnected.isSelected) changeButtonSelection(btnDisConnected, true)
        if (btnConnected.isSelected) changeButtonSelection(btnConnected, true)
    }

    private fun resetRegisterNewSrCitizenLayout() {
        tvAnySrCitizenInHome.visibility = View.GONE
        btnAnySrCitizenInHomeYes.visibility = View.GONE
        btnAnySrCitizenInHomeNo.visibility = View.GONE

        if (registerNewSrCitizen.visibility == View.VISIBLE) {
            tvNewSrCitizenPersonalDetails.visibility = View.GONE
            registerNewSrCitizen.visibility = View.GONE
        }

        etName.text.clear()
        etAge.text.clear()
        etContactNumber.text.clear()
        etAddress.text.clear()
    }

    private fun resetCovidSymptomsViews() {
        if (hsvCovidSymptoms.visibility == View.VISIBLE) {
            tvCovidSymptoms.visibility = View.GONE
            hsvCovidSymptoms.visibility = View.GONE
            if (ivCough.isSelected) ivCough.isSelected = false
            if (ivFever.isSelected) ivFever.isSelected = false
            if (ivShortnessOfBreath.isSelected) ivShortnessOfBreath.isSelected = false
            tvQuarantineHospitalizationStatus.visibility = View.GONE
            actQuarantineHospitalizationStatus.visibility = View.GONE
        }
    }

    private fun resetAutoCompleteTextView() {
        actTalkWith.setText("")
        actMedicalHistorySrCitizen.setText("")
        actBehaviorChange.setText("")
        actOtherMedicalProblems.setText("")
        actQuarantineHospitalizationStatus.setText("")
        actCategory.setText("")
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

    private fun showAlertMessage(@StringRes title: Int, @StringRes message: Int) =
        AlertDialog.Builder(this, R.style.Theme_MaterialComponents_Light_Dialog).setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.go_back) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
}