package com.nitiaayog.apnesaathi.utility

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.util.DisplayMetrics
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.login.LoginActivity
import com.nitiaayog.apnesaathi.ui.login.LoginViewModel
import java.util.*


/**
 * [LanguageUtils] for helping the load the UI in selected language
 */
object LanguageUtils {
    val LANGUAGE_HINDI = "Hindi"
    val LANGUAGE_ENGLISH = "ENGLISH"
    val LANGUAGE_MARATHI = "MARATHI"
    val LANGUAGE_GUJRATI = "GUJRATI"


    /**
     *  for helping the load the UI in selected language
     *  [dataManager]is used to store all the data that is required in the app.
     *  [applicationContext] is the current activity context
     *  [resources] to load the selected language code.
     */
    fun changeLanguage(
        dataManager: DataManager,
        applicationContext: Context,
        resources: Resources
    ) {

        val resources: Resources = resources
        val dm: DisplayMetrics = resources.getDisplayMetrics()
        val config: Configuration = resources.getConfiguration()



        if (dataManager.getSelectedLanguage() == LANGUAGE_ENGLISH) {

            config.setLocale(Locale("en".toLowerCase()))
            resources.updateConfiguration(config, dm)

        } else if (dataManager.getSelectedLanguage() == LANGUAGE_HINDI) {
            config.setLocale(Locale("hi".toLowerCase()))
            resources.updateConfiguration(config, dm)

        } else if (dataManager.getSelectedLanguage() == LANGUAGE_GUJRATI) {
            config.setLocale(Locale("gu".toLowerCase()))
            resources.updateConfiguration(config, dm)


        } else if (dataManager.getSelectedLanguage() == LANGUAGE_MARATHI) {
            config.setLocale(Locale("mr".toLowerCase()))
            resources.updateConfiguration(config, dm)

        }
    }


}