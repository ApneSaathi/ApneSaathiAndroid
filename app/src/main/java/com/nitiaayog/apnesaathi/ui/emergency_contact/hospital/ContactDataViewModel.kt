package com.nitiaayog.apnesaathi.ui.emergency_contact.hospital

import androidx.lifecycle.LiveData
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.DistrictDetails
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class ContactDataViewModel private constructor(val dataManager: DataManager) : BaseViewModel() {

    private val districtList: LiveData<MutableList<DistrictDetails>> = dataManager.getDistrictList()

    companion object {

        @Synchronized
        fun getInstance(dataManager: DataManager): ContactDataViewModel = synchronized(this) {
            ContactDataViewModel(dataManager)
        }
    }

    fun getDistrictList(): LiveData<MutableList<DistrictDetails>> = districtList

}