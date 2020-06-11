package com.nitiaayog.apnesaathi.ui.localization

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getTargetIntent
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_launguage_selection.*
import kotlinx.android.synthetic.main.custom_spinner_popup.*


class LanguageSelectionActivity : BaseActivity<LanguageSelectionModel>() {
    lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext
        if (dataManager.getSelectedLaungage().isEmpty()) {
            confirmed_language.text = resources.getString(R.string.english_text)
        } else {
//            confirmed_language.text = dataManager.getSelectedLaungage()
            if (dataManager.getSelectedLaungage().equals("English")) {
                confirmed_language.text = resources.getString(R.string.english_text)
            } else {
                confirmed_language.text = resources.getString(R.string.hindi_text)
            }

        }

        country_selection_view.throttleClick().subscribe() {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_spinner_popup, null)
            val mBuilder = AlertDialog.Builder(this, R.style.CustomDialog)
                .setView(mDialogView)
            val mAlertDialog = mBuilder.show()

            if (confirmed_language.text.toString() == resources.getString(R.string.english_text)) {
                mAlertDialog.english_block.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_highlited
                    )
                )

            } else if (confirmed_language.text.toString() == resources.getString(R.string.hindi_text)) {
                mAlertDialog.hindi_block.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_highlited
                    )
                )
            }
            mAlertDialog.english_block.throttleClick().subscribe() {
                mAlertDialog.dismiss()
                dataManager.setSelectedLaungage("English")
                recreate()
                confirmed_language.text = resources.getString(R.string.english_text)

            }.autoDispose(disposables)

            mAlertDialog.hindi_block.throttleClick().subscribe() {
                mAlertDialog.dismiss()
                dataManager.setSelectedLaungage("Hindi")
                recreate()
                confirmed_language.text = resources.getString(R.string.hindi_text)

            }.autoDispose(disposables)
        }.autoDispose(disposables)


        btnSubmit.throttleClick().subscribe() {
            val targetIntent = getTargetIntent(LoginActivity::class.java)
            startActivity(targetIntent)
            finish()
        }.autoDispose(disposables)
    }

    override fun provideViewModel(): LanguageSelectionModel =
        LanguageSelectionModel.getInstance(dataManager)

    override fun provideLayoutResource(): Int = R.layout.activity_launguage_selection


}