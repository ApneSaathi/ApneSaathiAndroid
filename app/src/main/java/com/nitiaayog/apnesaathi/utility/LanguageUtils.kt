package com.nitiaayog.apnesaathi.utility

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.util.DisplayMetrics
import com.nitiaayog.apnesaathi.datamanager.DataManager
import java.util.*

object LanguageUtils {
    val LANGUAGE_HINDI = "Hindi"
    val LANGUAGE_ENGLISH = "ENGLISH"
    val LANGUAGE_MARATHI = "MARATHI"
    val LANGUAGE_GUJRATI = "GUJRATI"

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


//            if (Build.VERSION.SDK_INT >= 25) {
//                config.setLocale(Locale("hi".toLowerCase()));
//                applicationContext.createConfigurationContext(config);
//            } else {
//                config.locale = Locale("hi".toLowerCase())
//                resources.updateConfiguration(config, dm)
//            }

        } else if (dataManager.getSelectedLanguage() == LANGUAGE_GUJRATI) {
            config.setLocale(Locale("gu".toLowerCase()))
            resources.updateConfiguration(config, dm)


        } else if (dataManager.getSelectedLanguage() == LANGUAGE_MARATHI) {
            config.setLocale(Locale("mr".toLowerCase()))
            resources.updateConfiguration(config, dm)
//            wrap(applicationContext, Locale("mr".toLowerCase()))

        }
    }

    fun wrap(context: Context, newLocale: Locale): Context {
        val res = context.getResources()
        val configuration = res.getConfiguration()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(newLocale)
            val localeList = LocaleList(newLocale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
            context.createConfigurationContext(configuration)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(newLocale)
            context.createConfigurationContext(configuration)
        } else {
            configuration.locale = newLocale
            res.updateConfiguration(configuration, res.getDisplayMetrics())
        }
        return context
    }
}