package com.nitiaayog.apnesaathi.ui.localization

import android.content.Context
import android.os.Bundle
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getTargetIntent
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.dashboard.DashBoardActivity
import com.nitiaayog.apnesaathi.ui.login.LoginActivity
import com.nitiaayog.apnesaathi.utility.LanguageUtils
import kotlinx.android.synthetic.main.activity_launguage_selection.*

class LanguageSelectionActivity : BaseActivity<LanguageSelectionModel>() {
    lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext
        if (dataManager.getSelectedLanguage().isEmpty()) {
            radiobtnEnglish.isChecked = true
            dataManager.setSelectedLanguage(LanguageUtils.LANGUAGE_ENGLISH)
        } else {
            if (dataManager.getSelectedLanguage().equals(LanguageUtils.LANGUAGE_ENGLISH)) {
                radiobtnEnglish.isChecked = true
            } else {
                if (dataManager.getSelectedLanguage().equals(LanguageUtils.LANGUAGE_MARATHI)) {
                    radiobtnMarathi.isChecked = true
                } else {
                    if (dataManager.getSelectedLanguage().equals(LanguageUtils.LANGUAGE_GUJRATI)) {
                        radiobtnGujrati.isChecked = true
                    } else {
                        radiobtnHindi.isChecked = true
                    }
                }
            }
        }

        radiogroup.setOnCheckedChangeListener { radioGroup, i ->
            when (radiogroup.checkedRadioButtonId) {
                R.id.radiobtnEnglish -> {
                    dataManager.setSelectedLanguage(LanguageUtils.LANGUAGE_ENGLISH)
                }
                R.id.radiobtnMarathi -> {
                    dataManager.setSelectedLanguage(LanguageUtils.LANGUAGE_MARATHI)
                }
                R.id.radiobtnHindi -> {
                    dataManager.setSelectedLanguage(LanguageUtils.LANGUAGE_HINDI)
                }
                R.id.radiobtnGujrati -> {
                    dataManager.setSelectedLanguage(LanguageUtils.LANGUAGE_GUJRATI)
                }
                else -> dataManager.setSelectedLanguage(LanguageUtils.LANGUAGE_ENGLISH)
            }
            recreate()
        }

        btnSubmit.throttleClick().subscribe() {

            val targetIntent = getTargetIntent(

                if (dataManager.isLogin() == false)
                    LoginActivity::class.java
                else DashBoardActivity::class.java
            )
            startActivity(targetIntent)
            finish()


        }.autoDispose(disposables)
    }

    override fun provideViewModel(): LanguageSelectionModel =
        LanguageSelectionModel.getInstance(dataManager)

    override fun provideLayoutResource(): Int = R.layout.activity_launguage_selection
}