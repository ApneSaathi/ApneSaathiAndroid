package com.nitiaayog.apnesaathi.ui.emergencycontact

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel


/**
 * [dataManager] is used to store all the data that is required in the app.
 */
class MainEmergencyContact_ViewModel private constructor(dataManager: DataManager) : BaseViewModel() {

    val dataManager: DataManager = dataManager

    companion object {

        @Synchronized
        fun getInstance(dataManager: DataManager): MainEmergencyContact_ViewModel = synchronized(this) {
            MainEmergencyContact_ViewModel(dataManager)
        }
    }


}