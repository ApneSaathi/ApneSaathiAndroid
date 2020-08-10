package com.nitiaayog.apnesaathi.ui.emergency_contact

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class EmergencyContact_ViewModel private constructor(dataManager: DataManager) : BaseViewModel() {

    val dataManager: DataManager = dataManager

    companion object {

        @Synchronized
        fun getInstance(dataManager: DataManager): EmergencyContact_ViewModel = synchronized(this) {
            EmergencyContact_ViewModel(dataManager)
        }
    }


}