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
     * Role wise Access to Features :-
     *
     * 1). Staff Members and Master Admin :-
     *      - Can See Sr. Citizens list assigned to it,
     *      - Call Sr. Citizens and get feedback from them about problems they are facing,
     *      - Calling to Volunteers,
     *      - Can see Volunteers and their details,
     *      - Can See/Update Grievances status.
     *
     * 2). District Admin :-
     *      - Can see Grievances and status of only district assigned to it(Mostly 1 district).
     *
     * 3). Volunteers :-
     *      - Can See Sr. Citizens list assigned to it,
     *      - Call Sr. Citizens and get feedback from them about problems they are facing,
     *      - Can See/Update Grievances status.
     *
     * */
    fun getOffScreenPageLimit(dataManager: DataManager): Int {
        return if (dataManager.getRole() == "3") 2 else 4
    }

    /**
     * This api is only syncing data of feedback form i.e. Only data that were collected over a
     * call by logged in user with Senior Citizen
     *
     * This method is used to Sync data which were stored when logged in person tried to update
     * the data and Network was not available.
     *
     * Process is very simple that when user will tap on call icon call will place and after 6/7
     * seconds
     * @see com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenfeedbackform.SeniorCitizenFeedbackFormActivity
     * will be launched to provide the status of call and to update the issues of Senior Citizens.
     *
     * Hence, in parallel with a call user can save data to server as per provided by Sr. Citizen
     * it may possible cellular data may not be available. So that will be saved in local database
     * and later on when cellular data will available, data will be synced with server using
     * @see com.nitiaayog.apnesaathi.service.SyncDataService service.
     *
     * Important : This method will check that service is working in background or not.If not then
     * this method will inform system about the service that will only execute when
     * cellular data/network is available
     **/
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