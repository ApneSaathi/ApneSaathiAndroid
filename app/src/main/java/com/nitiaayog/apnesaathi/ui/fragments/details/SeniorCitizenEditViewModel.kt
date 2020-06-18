package com.nitiaayog.apnesaathi.ui.fragments.details

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class SeniorCitizenEditViewModel private constructor(private val dataManager: DataManager) :
    BaseViewModel() {
    companion object {
        @Volatile
        private var instance: SeniorCitizenEditViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): SeniorCitizenEditViewModel =
            instance
                ?: synchronized(this) {
                    instance
                        ?: SeniorCitizenEditViewModel(
                            dataManager
                        )
                            .also { instance = it }
                }
    }
}