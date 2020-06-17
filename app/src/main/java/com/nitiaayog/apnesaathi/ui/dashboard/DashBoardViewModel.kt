package com.nitiaayog.apnesaathi.ui.dashboard

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class DashBoardViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {
        @Volatile
        private var instance: DashBoardViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager):DashBoardViewModel =
            instance ?: synchronized(this) {
                instance ?: DashBoardViewModel(dataManager).also { instance = it }
            }
    }
}