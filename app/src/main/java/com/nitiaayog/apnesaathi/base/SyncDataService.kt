package com.nitiaayog.apnesaathi.base

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Handler
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.AppDataManager
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.utility.NetworkProvider
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SyncDataService : JobService() {

    companion object {
        private const val JOB_ID: Int = 1959
        fun enqueueWork(context: Context) {
            val jobScheduler =
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler?
            jobScheduler?.run {
                val componentName = ComponentName(context, SyncDataService::class.java)
                val jobInfo = JobInfo.Builder(JOB_ID, componentName)
                    .setPersisted(true).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build()
                this.schedule(jobInfo)
            }
        }
    }

    private val handler: Handler by lazy { Handler() }
    private val disposables: CompositeDisposable by lazy { CompositeDisposable() }
    private val dataManager: DataManager by lazy { AppDataManager.getDataManager(application) }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        val calendar: Calendar = Calendar.getInstance()
        val dateTime: String = "${calendar.get(Calendar.HOUR_OF_DAY)} :" +
                " ${calendar.get(Calendar.MINUTE)} ${calendar.get(Calendar.AM_PM)}"
        if (NetworkProvider.isConnected(applicationContext)) {
            printLogs("\n\nTAG -- MyData --> Internet Available - $dateTime")
            CoroutineScope(Dispatchers.IO).launch {
                val apiParams = JsonObject()
                apiParams.addProperty(ApiConstants.VolunteerId, 1234)
                dataManager.getCallDetails(apiParams).subscribe(
                    {
                        printLogs("\nTAG -- MyData --> Api Success - $dateTime")
                        disposeAll(params)
                    },
                    {
                        printLogs("\nTAG -- MyData --> Api Error - $dateTime")
                        disposeAll(params)
                    }).autoDispose(disposables)
            }
        } else printLogs("\nTAG -- MyData --> No Internet - $dateTime")

        return true
    }

    private fun printLogs(logText: String) {
        println(logText)
        //logsGenerator(logText)
    }

    private fun disposeAll(params: JobParameters?) {
        if ((disposables.size() > 0) && (!disposables.isDisposed)) {
            disposables.dispose()
            disposables.clear()
        }
        jobFinished(params, true)
    }
}