package com.nitiaayog.apnesaathi.ui.fragments

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class SeniorCitizenUpdateFormViewModel private constructor(private val dataManager: DataManager) :
    BaseViewModel() {

    companion object {
        @Volatile
        private var instance: SeniorCitizenUpdateFormViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): SeniorCitizenUpdateFormViewModel =
            instance ?: synchronized(this) {
                instance ?: SeniorCitizenUpdateFormViewModel(dataManager).also { instance = it }
            }
    }
}