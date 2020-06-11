package com.nitiaayog.apnesaathi.utility

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import com.nitiaayog.apnesaathi.datamanager.DataManager
import java.util.*

object LanguageUtils {
    fun changeLanguage(
        dataManager: DataManager,
        applicationContext: Context,
        resources: Resources
    ) {

        val LANGUAGE_HINDI = ""

        if (dataManager.getSelectedLaungage() == "English") {
            val resources: Resources = resources
            val dm: DisplayMetrics = resources.getDisplayMetrics()
            val config: Configuration = resources.getConfiguration()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(Locale("en".toLowerCase()))
            } else {
                config.locale = Locale("en".toLowerCase())
            }
            resources.updateConfiguration(config, dm)

        } else if (dataManager.getSelectedLaungage() == "Hindi") {
            val resources: Resources = resources
            val dm: DisplayMetrics = resources.getDisplayMetrics()
            val config: Configuration = resources.getConfiguration()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(Locale("hi".toLowerCase()))
            } else {
                config.locale = Locale("hi".toLowerCase())
            }
            resources.updateConfiguration(config, dm)


        }
    }
}