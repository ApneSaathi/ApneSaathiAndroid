package com.nitiaayog.apnesaathi.ui.citizen_update

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class SeniorCitizenUpdateViewModel(private val dataManager: DataManager) : BaseViewModel() {
    companion object {
        @Volatile
        private var instance: SeniorCitizenUpdateViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): SeniorCitizenUpdateViewModel =
            instance ?: synchronized(this) {
                instance ?: SeniorCitizenUpdateViewModel(dataManager).also { instance = it }
            }
    }
}