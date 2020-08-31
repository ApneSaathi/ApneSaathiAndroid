package com.nitiaayog.apnesaathi.ui.localization

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import com.nitiaayog.apnesaathi.ui.login.LoginViewModel


/**
 * [DataManager] is used to store all the data that is required in the app.
 */
class LanguageSelectionModel private constructor(dataManager: DataManager) : BaseViewModel() {

    companion object {
        @Volatile
        private var instance: LanguageSelectionModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): LanguageSelectionModel =
            instance ?: synchronized(this) {
                instance ?: LanguageSelectionModel(dataManager).also { instance = it }
            }
    }
}