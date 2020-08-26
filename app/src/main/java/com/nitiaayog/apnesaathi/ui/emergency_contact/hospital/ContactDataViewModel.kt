package com.nitiaayog.apnesaathi.ui.emergency_contact.hospital

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class ContactDataViewModel private constructor(dataManager: DataManager) : BaseViewModel() {

    val dataManager: DataManager = dataManager

    companion object {

        @Synchronized
        fun getInstance(dataManager: DataManager): ContactDataViewModel = synchronized(this) {
            ContactDataViewModel(dataManager)
        }
    }


}