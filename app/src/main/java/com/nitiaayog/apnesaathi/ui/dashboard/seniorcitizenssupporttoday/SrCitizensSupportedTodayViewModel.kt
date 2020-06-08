package com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenssupporttoday

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class SrCitizensSupportedTodayViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {
        @Volatile
        private var instance: SrCitizensSupportedTodayViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): SrCitizensSupportedTodayViewModel =
            instance ?: synchronized(this) {
                instance ?: SrCitizensSupportedTodayViewModel(dataManager).also { instance = it }
            }
    }
}