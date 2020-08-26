package com.nitiaayog.apnesaathi.ui.emergency_contact

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class MainEmergencyContact_ViewModel private constructor(dataManager: DataManager) : BaseViewModel() {

    val dataManager: DataManager = dataManager

    companion object {

        @Synchronized
        fun getInstance(dataManager: DataManager): MainEmergencyContact_ViewModel = synchronized(this) {
            MainEmergencyContact_ViewModel(dataManager)
        }
    }


}