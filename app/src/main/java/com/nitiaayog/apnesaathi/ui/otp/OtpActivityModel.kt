package com.nitiaayog.apnesaathi.ui.otp

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class OtpActivityModel private constructor(dataManager: DataManager) : BaseViewModel() {

    companion object {
        @Volatile
        private var instance: OtpActivityModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): OtpActivityModel =
            instance ?: synchronized(this) {
                instance ?: OtpActivityModel(dataManager).also { instance = it }
            }
    }

}