package com.nitiaayog.apnesaathi

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.nitiaayog.apnesaathi.datamanager.AppDataManager
import com.nitiaayog.apnesaathi.datamanager.DataManager

class ApneSaathiApplication : Application() {

    private val dataManager: DataManager by lazy { AppDataManager.getDataManager(this) }

    companion object {
        private lateinit var instance: ApneSaathiApplication
        fun getApiClient(): DataManager = instance.dataManager
    }

    override fun onCreate() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate()
        instance = this
    }
}