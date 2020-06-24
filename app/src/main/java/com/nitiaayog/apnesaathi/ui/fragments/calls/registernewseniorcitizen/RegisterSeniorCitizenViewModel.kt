package com.nitiaayog.apnesaathi.ui.fragments.calls.registernewseniorcitizen

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class RegisterSeniorCitizenViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {
        @Volatile
        private var instance: RegisterSeniorCitizenViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): RegisterSeniorCitizenViewModel =
            instance ?: synchronized(this) {
                instance ?: RegisterSeniorCitizenViewModel(dataManager).also { instance = it }
            }
    }
}