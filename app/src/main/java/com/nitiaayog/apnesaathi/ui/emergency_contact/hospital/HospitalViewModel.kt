package com.nitiaayog.apnesaathi.ui.emergency_contact.hospital

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class HospitalViewModel private constructor(dataManager: DataManager) : BaseViewModel() {

    val dataManager: DataManager = dataManager

    companion object {

        @Synchronized
        fun getInstance(dataManager: DataManager): HospitalViewModel = synchronized(this) {
            HospitalViewModel(dataManager)
        }
    }


}