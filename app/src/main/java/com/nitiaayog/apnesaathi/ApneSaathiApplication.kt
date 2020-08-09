package com.nitiaayog.apnesaathi

import android.app.Application
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.jakewharton.threetenabp.AndroidThreeTen
import com.nitiaayog.apnesaathi.datamanager.AppDataManager
import com.nitiaayog.apnesaathi.datamanager.DataManager

class ApneSaathiApplication : Application() {

    private val dataManager: DataManager by lazy { AppDataManager.getDataManager(this) }

    companion object {
        val screenSize = intArrayOf(0, 0)

        private lateinit var instance: ApneSaathiApplication
        fun getDataClient(): DataManager = instance.dataManager
    }

    override fun onCreate() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate()

        //println("TAG -- ApiHost --> ${BuildConfig.ApiHost}")

        AndroidThreeTen.init(this)
        getScreenSize(this)

        instance = this
    }

    private fun getScreenSize(context: Context) {
        val wManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        screenSize[0] = metrics.widthPixels
        screenSize[1] = metrics.heightPixels
    }
}