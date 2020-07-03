package com.nitiaayog.apnesaathi.service

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.datamanager.AppDataManager
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.SyncSrCitizenGrievance
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

    private val disposables: CompositeDisposable by lazy { CompositeDisposable() }
    private val dataManager: DataManager by lazy { AppDataManager.getDataManager(application) }

    override fun onStartJob(params: JobParameters?): Boolean {
        val calendar: Calendar = Calendar.getInstance()
        val dateTime: String = "${calendar.get(Calendar.HOUR_OF_DAY)} :" +
                " ${calendar.get(Calendar.MINUTE)} ${calendar.get(Calendar.AM_PM)}"
        if (NetworkProvider.isConnected(applicationContext)) {
            println("\n\nTAG -- JobService --> Internet Available - $dateTime")
            CoroutineScope(Dispatchers.IO).launch {
                val processData = dataManager.getGrievancesToSync()
                println("\nTAG -- JobService --> ${processData?.size} data will be synced")
                processData?.run {
                    if (processData.isEmpty()) stopSelf()
                    else {
                        processData.forEach { syncData(it) }
                    }
                }
            }
        } else println("\nTAG -- JobService --> No Internet - $dateTime")

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    private fun disposeAll(params: JobParameters?) {
        if ((disposables.size() > 0) && (!disposables.isDisposed)) {
            disposables.dispose()
            disposables.clear()
        }
        jobFinished(params, true)
    }

    private fun preparePostParams(grievance: SyncSrCitizenGrievance): JsonObject {
        val params = JsonObject()
        params.addProperty(ApiConstants.CallId, grievance.callId!!)
        params.addProperty(ApiConstants.VolunteerId, 1234/*dataManager.getUserId()*/)
        params.addProperty(ApiConstants.SrCitizenCallStatusSubCode, grievance.callStatusSubCode)

        val callStatus = if (grievance.callStatusSubCode == "5") "2" else "1"
        params.addProperty(ApiConstants.SrCitizenCallStatusCode, callStatus)

        params.addProperty(ApiConstants.SrCitizenTalkedWith, grievance.talkedWith)

        if (callStatus == "1") return params

        val arraySubParams = JsonObject()
        arraySubParams.addProperty(ApiConstants.CallId, grievance.callId)
        arraySubParams.addProperty(ApiConstants.VolunteerId, 1234/*dataManager.getUserId()*/)

        arraySubParams.addProperty(ApiConstants.IsDiabetic, grievance.hasDiabetic)
        arraySubParams.addProperty(ApiConstants.IsBloodPressure, grievance.hasBloodPressure)
        arraySubParams.addProperty(ApiConstants.LungAilment, grievance.hasLungAilment)
        arraySubParams.addProperty(
            ApiConstants.CancerOrMajorSurgery, grievance.cancerOrMajorSurgery
        )
        arraySubParams.addProperty(ApiConstants.OtherAilments, "N")

        arraySubParams.addProperty(ApiConstants.RemarkOnMedicalHistory, "")

        arraySubParams.addProperty(ApiConstants.InfoTalkAbout, grievance.relatedInfoTalkedAbout)
        arraySubParams.addProperty(
            ApiConstants.NoticedBehaviouralChange, grievance.behavioralChangesNoticed
        )

        arraySubParams.addProperty(ApiConstants.HasCovidSymptoms, grievance.hasCovidSymptoms)
        arraySubParams.addProperty(ApiConstants.HasCough, grievance.hasCough)
        arraySubParams.addProperty(ApiConstants.HasFever, grievance.hasFever)
        arraySubParams.addProperty(
            ApiConstants.HasShortnessOfBreath, grievance.hasShortnessOfBreath
        )
        arraySubParams.addProperty(ApiConstants.HasSoreThroat, grievance.hasCovidSymptoms)
        arraySubParams.addProperty(ApiConstants.HasSoreThroat, grievance.hasSoreThroat)
        arraySubParams.addProperty(ApiConstants.QuarantineStatus, grievance.quarantineStatus!!)

        arraySubParams.addProperty(ApiConstants.FoodShortage, grievance.foodShortage!!.toInt())
        arraySubParams.addProperty(
            ApiConstants.MedicineShortage, grievance.medicineShortage!!.toInt()
        )
        arraySubParams.addProperty(
            ApiConstants.AccessToBankingIssue, grievance.accessToBankingIssue!!.toInt()
        )
        arraySubParams.addProperty(
            ApiConstants.UtilityIssue, grievance.utilitySupplyIssue!!.toInt()
        )
        arraySubParams.addProperty(ApiConstants.HygieneIssue, grievance.hygieneIssue!!.toInt())
        arraySubParams.addProperty(ApiConstants.SafetyIssue, grievance.safetyIssue!!.toInt())
        arraySubParams.addProperty(
            ApiConstants.EmergencyServiceIssue, grievance.emergencyServiceIssue!!.toInt()
        )
        arraySubParams.addProperty(
            ApiConstants.PhoneInternetIssue, grievance.phoneAndInternetIssue!!.toInt()
        )

        arraySubParams.addProperty(
            ApiConstants.IsEmergencyServicesRequired, grievance.emergencyServiceRequired
        )

        val jsonArray = JsonArray()
        jsonArray.add(arraySubParams)

        params.add(ApiConstants.MedicalGrievances, jsonArray)

        return params
    }

    private suspend fun syncData(grievance: SyncSrCitizenGrievance) {
        val params = preparePostParams(grievance)
        val disposable = dataManager.saveSrCitizenFeedback(params).doOnSubscribe {
            println("TAG -- JobService -- params --> $params")
        }.subscribe({
            if (it.status == "0") {
                CoroutineScope(Dispatchers.IO).launch {
                    dataManager.update(grievance)
                    dataManager.delete(grievance)
                    println("TAG -- JobService -- sync --> success")
                }
            }
        }, {
            println("TAG -- JobService -- params --> Exception")
            println("TAG -- JobService -- params --> ${it?.message}")
        })
        disposables.add(disposable)
    }
}