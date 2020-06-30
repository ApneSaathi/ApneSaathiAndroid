package com.nitiaayog.apnesaathi.ui.login

import android.content.Context
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
    fun registerNewSeniorCitizen(
        context: Context, name: String, age: String, gender: String, contactNumber: String,
        district: String, state: String, address: String
    ) {

    }
}