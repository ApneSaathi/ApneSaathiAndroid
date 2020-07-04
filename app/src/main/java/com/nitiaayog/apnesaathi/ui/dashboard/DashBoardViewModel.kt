package com.nitiaayog.apnesaathi.ui.dashboard

import android.content.Context
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.service.SyncDataService
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class DashBoardViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {

        @Synchronized
        fun getInstance(dataManager: DataManager): DashBoardViewModel = synchronized(this) {
            DashBoardViewModel(dataManager)
        }
    }

    fun startSyncingData(context: Context) = SyncDataService.enqueueWork(context)
}