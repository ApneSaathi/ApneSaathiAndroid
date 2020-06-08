package com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenfeedbackform

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class SeniorCitizenFeedbackViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {
        @Volatile
        private var instance: SeniorCitizenFeedbackViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): SeniorCitizenFeedbackViewModel =
            instance ?: synchronized(this) {
                instance ?: SeniorCitizenFeedbackViewModel(dataManager).also { instance = it }
            }
    }
}