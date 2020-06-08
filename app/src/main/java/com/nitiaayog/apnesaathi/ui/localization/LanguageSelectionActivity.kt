package com.nitiaayog.apnesaathi.ui.localization

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getTargetIntent
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_launguage_selection.*
import kotlinx.android.synthetic.main.custom_spinner_popup.*

class LanguageSelectionActivity : BaseActivity<LanguageSelectionModel>() {
    lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext
        country_selection_view.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_spinner_popup, null)
            val mBuilder = AlertDialog.Builder(this, R.style.CustomDialog)
                .setView(mDialogView)
            val mAlertDialog = mBuilder.show()
            if (confirmed_language.text.toString() == "English") {
                mAlertDialog.english_block.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_highlited
                    )
                )

            } else if (confirmed_language.text.toString() == "Hindi") {
                mAlertDialog.spanish_block.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_highlited
                    )
                )

            }
            mAlertDialog.english_block.setOnClickListener {
                confirmed_language.text = "English"
                mAlertDialog.dismiss()


            }

            mAlertDialog.spanish_block.setOnClickListener {
                confirmed_language.text = "Hindi"
                mAlertDialog.dismiss()


            }
        }
        BtnSubmit.setOnClickListener {
            val targetIntent = getTargetIntent(LoginActivity::class.java)
            startActivity(targetIntent)
            finish()
        }
    }

    override fun provideViewModel(): LanguageSelectionModel =
        LanguageSelectionModel.getInstance(dataManager)

    override fun provideLayoutResource(): Int = R.layout.activity_launguage_selection

}