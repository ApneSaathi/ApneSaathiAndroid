package com.nitiaayog.apnesaathi.ui.dashboard

import android.app.job.JobScheduler
import android.content.Context
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.service.SyncDataService
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class DashBoardViewModel : BaseViewModel() {

    companion object {
        @Synchronized
        fun getInstance(): DashBoardViewModel = synchronized(this) { DashBoardViewModel() }
    }

    /**
     * This method is used only when admin/staff member will be logged in.
     * */
    fun getOffScreenPageLimit(dataManager: DataManager): Int {
        return if (dataManager.getRole() == "3") 2 else 4
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