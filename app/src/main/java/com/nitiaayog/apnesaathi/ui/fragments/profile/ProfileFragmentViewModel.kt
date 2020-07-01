package com.nitiaayog.apnesaathi.ui.fragments.profile

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class ProfileFragmentViewModel private constructor(private val dataManager: DataManager) :
    BaseViewModel() {
    companion object {
        @Volatile
        private var instance: ProfileFragmentViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): ProfileFragmentViewModel =
            instance
                ?: synchronized(this) {
                    instance
                        ?: ProfileFragmentViewModel(
                            dataManager
                        )
                            .also { instance = it }
                }
    }
}