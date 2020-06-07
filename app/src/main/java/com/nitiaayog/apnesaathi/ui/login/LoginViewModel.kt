package com.nitiaayog.apnesaathi.ui.login

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class LoginViewModel private constructor(dataManager: DataManager) : BaseViewModel() {

    companion object {
        @Volatile
        private var instance: LoginViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): LoginViewModel =
            instance ?: synchronized(this) {
                instance ?: LoginViewModel(dataManager).also { instance = it }
            }
    }
}