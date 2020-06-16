package com.nitiaayog.apnesaathi

import android.app.Application
import com.nitiaayog.apnesaathi.datamanager.AppDataManager
import com.nitiaayog.apnesaathi.datamanager.DataManager

class ApneSaathiApplication : Application() {

    private val dataManager: DataManager by lazy { AppDataManager.getDataManager(this) }

    companion object {
        private lateinit var instance: ApneSaathiApplication
        fun getApiClient(): DataManager = instance.dataManager
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}