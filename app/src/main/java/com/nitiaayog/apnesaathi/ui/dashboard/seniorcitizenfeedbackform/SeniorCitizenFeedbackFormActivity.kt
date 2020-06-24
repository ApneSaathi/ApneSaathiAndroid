package com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenfeedbackform

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.MultiAutoCompleteTextView.CommaTokenizer
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.senior_citizen_feedback_form.*

class SeniorCitizenFeedbackFormActivity : BaseActivity<SeniorCitizenFeedbackViewModel>() {

    private var talkedWith: String = ""
    private var selectedMedicalProblem: String = ""
    private var selectedBehaviorChange: String = ""
    private var selectedOtherMedicalProblem: String = ""
    private var selectedQuarantineStatus: String = ""
    private var selectedComplaintCategory: String = ""

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
        actTalkWith.setOnItemClickListener { _, _, position, _ ->
            talkedWith = talkedWithList[position]
        }
        actTalkWith.setOnKeyListener(null)

        val medicalProblemsList = resources.getStringArray(R.array.medical_problems)
        val medicalListAdapter =
            ArrayAdapter(this, R.layout.item_layout_dropdown_menu, medicalProblemsList)
        actMedicalHistorySrCitizen.threshold = 0
        actMedicalHistorySrCitizen.setTokenizer(CommaTokenizer())
        actMedicalHistorySrCitizen.setAdapter(medicalListAdapter)
        actMedicalHistorySrCitizen.setOnItemClickListener { _, _, position, _ ->
            selectedMedicalProblem = medicalProblemsList[position]
        }
        actMedicalHistorySrCitizen.setOnKeyListener(null)

        val behaviorChangesList = resources.getStringArray(R.array.behavior_changes)
        val behaviorChangesAdapter =
            ArrayAdapter(this, R.layout.item_layout_dropdown_menu, behaviorChangesList)
        actBehaviorChange.threshold = 0
        actBehaviorChange.setAdapter(behaviorChangesAdapter)
        actBehaviorChange.setOnItemClickListener { _, _, position, _ ->
            selectedBehaviorChange = behaviorChangesList[position]
        }
        actBehaviorChange.setOnKeyListener(null)

        val otherMedicalProblemsList = resources.getStringArray(R.array.other_medical_problems_list)
        val otherMedicalProblemsAdapter =
            ArrayAdapter(this, R.layout.item_layout_dropdown_menu, otherMedicalProblemsList)
        actOtherMedicalProblems.threshold = 0
        actOtherMedicalProblems.setAdapter(otherMedicalProblemsAdapter)
        actOtherMedicalProblems.setOnItemClickListener { _, _, position, _ ->
            selectedOtherMedicalProblem = otherMedicalProblemsList[position]
            if (selectedOtherMedicalProblem == getString(R.string.covid_19_symptoms)) {
                tvCovidSymptoms.visibility = View.VISIBLE
                llCough.visibility = View.VISIBLE
                llFever.visibility = View.VISIBLE
                llShortOfBreath.visibility = View.VISIBLE
            } else {
                tvCovidSymptoms.visibility = View.GONE
                llCough.visibility = View.GONE
                llFever.visibility = View.GONE
                llShortOfBreath.visibility = View.GONE
                if (ivCough.isSelected) changeCovidSymptomsSelection(ivCough, tvCough)
                if (ivFever.isSelected) changeCovidSymptomsSelection(ivFever, tvFever)
                if (ivShortnessOfBreath.isSelected)
                    changeCovidSymptomsSelection(ivShortnessOfBreath, tvShortnessOfBreath)
            }
        }
        actOtherMedicalProblems.setOnKeyListener(null)

        val quarantineStatusList = resources.getStringArray(R.array.quarantine_status_array)
        val quarantineStatusAdapter =
            ArrayAdapter(this, R.layout.item_layout_dropdown_menu, quarantineStatusList)
        actQuarantineHospitalizationStatus.threshold = 0
        actQuarantineHospitalizationStatus.setAdapter(quarantineStatusAdapter)
        actQuarantineHospitalizationStatus.setOnItemClickListener { _, _, position, _ ->
            selectedQuarantineStatus = behaviorChangesList[position]
        }
        actQuarantineHospitalizationStatus.setOnKeyListener(null)

        val categoryList = resources.getStringArray(R.array.grievance_array)
        val categoryAdapter = ArrayAdapter(this, R.layout.item_layout_dropdown_menu, categoryList)
        actCategory.threshold = 0
        actCategory.setTokenizer(CommaTokenizer())
        actCategory.setAdapter(categoryAdapter)
        actCategory.setOnItemClickListener { _, _, position, _ ->
            selectedComplaintCategory = categoryList[position]
        }
        actCategory.setOnKeyListener(null)
    }

    private fun initClicks() {
        // Call Status Button Clicks
        btnNoResponse.throttleClick().subscribe { changeButtonSelection(btnNoResponse) }
            .autoDispose(disposables)
        btnNotPicked.throttleClick().subscribe { changeButtonSelection(btnNotPicked) }
            .autoDispose(disposables)
        btnNotReachable.throttleClick().subscribe { changeButtonSelection(btnNotReachable) }
            .autoDispose(disposables)
        btnDisConnected.throttleClick().subscribe { changeButtonSelection(btnDisConnected) }
            .autoDispose(disposables)
        btnConnected.throttleClick().subscribe { changeButtonSelection(btnConnected) }
            .autoDispose(disposables)

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

        btnPrevention.throttleClick().subscribe { changeButtonSelectionWithIcon(btnPrevention) }
            .autoDispose(disposables)
        btnAccess.throttleClick().subscribe { changeButtonSelectionWithIcon(btnAccess) }
            .autoDispose(disposables)
        btnDetection.throttleClick().subscribe { changeButtonSelectionWithIcon(btnDetection) }
            .autoDispose(disposables)

        llCough.throttleClick().subscribe { changeCovidSymptomsSelection(ivCough, tvCough) }
            .autoDispose(disposables)
        llFever.throttleClick().subscribe { changeCovidSymptomsSelection(ivFever, tvFever) }
            .autoDispose(disposables)
        llShortOfBreath.throttleClick().subscribe {
            changeCovidSymptomsSelection(ivShortnessOfBreath, tvShortnessOfBreath)
        }.autoDispose(disposables)

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

        btnEmergencyYes.throttleClick().subscribe {
            selectedNeedOfEmergencyServices =
                toggleButtonSelection(btnEmergencyYes, selectedNeedOfEmergencyServices)
        }.autoDispose(disposables)
        btnEmergencyNo.throttleClick().subscribe {
            selectedNeedOfEmergencyServices =
                toggleButtonSelection(btnEmergencyNo, selectedNeedOfEmergencyServices)
        }.autoDispose(disposables)
    }

    private fun changeButtonSelection(button: MaterialButton) {
        if ((selectedCallStatusButton != null) && (selectedCallStatusButton == button))
            return

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
}