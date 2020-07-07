package com.nitiaayog.apnesaathi.ui.dashboard

import android.app.job.JobScheduler
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

    fun startSyncingData(context: Context) {
        val jobSchedular = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler?
        jobSchedular?.run {
            var isJobScheduled: Boolean = false
            val jobList = this.allPendingJobs
            jobList.forEach {
                if (it.id == SyncDataService.JOB_ID) isJobScheduled = true
            }
            if (!isJobScheduled)
                SyncDataService.enqueueWork(context)
        }
    }
}