package com.nitiaayog.apnesaathi.service

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.io
import com.nitiaayog.apnesaathi.datamanager.AppDataManager
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
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

        private const val TAG: String = "TAG -- JobService -->"
        const val JOB_ID: Int = 1959

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
            println("\n\n$TAG Internet Available - $dateTime")
            CoroutineScope(Dispatchers.IO).launch {
                val processData = dataManager.getGrievancesToSync()
                println("\n$TAG ${processData?.size} data will be synced")
                processData?.run {
                    if (processData.isEmpty()) jobFinished(params, true)
                    else {
                        io {
                            startSyncing(processData)
                        }
                    }
                    getFetchData()
                }
            }
        } else println("\n$TAG No Internet - $dateTime")

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        println("\n$TAG Job Completed")
        disposeAll(params)
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
        params.addProperty(ApiConstants.VolunteerId, dataManager.getUserId())
        params.addProperty(ApiConstants.SrCitizenCallStatusSubCode, grievance.callStatusSubCode)

        val callStatus = if (grievance.callStatusSubCode == "5") "2" else "1"
        params.addProperty(ApiConstants.SrCitizenCallStatusCode, callStatus)

        params.addProperty(ApiConstants.SrCitizenTalkedWith, grievance.talkedWith)
        params.addProperty(ApiConstants.SrCitizenName, grievance.srCitizenName)
        params.addProperty(ApiConstants.SrCitizenGender, grievance.gender)

        if (callStatus == "1") return params

        val arraySubParams = JsonObject()
        arraySubParams.addProperty(ApiConstants.CallId, grievance.callId)
        arraySubParams.addProperty(ApiConstants.VolunteerId, dataManager.getUserId())

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

    private fun prepareGrievances(grievance: List<CallData>): List<SrCitizenGrievance> {
        val callData = grievance.filter {
            (it.medicalGrievance != null && it.medicalGrievance!!.size > 0)
        }
        val grievances: MutableList<SrCitizenGrievance> = mutableListOf()
        callData.forEach { data -> grievances.addAll(data.medicalGrievance!!)
        }
        return grievances
    }

    private fun startSyncing(processData: List<SyncSrCitizenGrievance>) =
        processData.forEach { syncData(it) }

    private fun syncData(grievance: SyncSrCitizenGrievance) {
        val params = preparePostParams(grievance)
        val disposable = dataManager.saveSrCitizenFeedback(params).doOnSubscribe {
            println("$TAG -- params --> $params")
        }.subscribe({
            if (it.status == "0") {
                CoroutineScope(Dispatchers.IO).launch {
                    // (it.grievanceId != "-1") grievance is not added if added then
                    // that id will returned
                    //dataManager.updateCallStatus(grievance.callStatusSubCode)
                    dataManager.delete(grievance)
                    if (it.grievanceId.isNotEmpty() && (it.grievanceId != "-1")) {
                        try {
                            if (dataManager.isDataExist(grievance.id!!, grievance.callId!!)
                                == null
                            ) {
                                grievance.id = it.grievanceId.toInt()
                                dataManager.insertGrievance(grievance)
                            } else
                                dataManager.updateGrievance(grievance)
                        } catch (e: Exception) {
                            println("TAG -- MyData --> ${e.message}")
                        }
                    }
                    println("$TAG -- sync --> success")
                }
            }
        }, {
            println("$TAG params --> Exception")
            println("$TAG params --> ${it?.message}")
        })
        disposables.add(disposable)
    }

    private fun getFetchData() {
        val params = JsonObject()
        params.addProperty(ApiConstants.VolunteerId, dataManager.getUserId())
        dataManager.getCallDetails(params).doOnSubscribe {
            println("\n\n$TAG -- Start fetching data")
        }.subscribe({
            try {
                if (it.status == "0") {
                    CoroutineScope(Dispatchers.IO).launch {
                        io {
                            val data = it.getData()
                            dataManager.insertCallData(data.callsList)

                            val grievances: List<SrCitizenGrievance> =
                                prepareGrievances(data.callsList)
                            dataManager.insertGrievances(grievances)
                            println("$TAG -- Fetched data successfully")
                        }
                    }
                }
            } catch (e: Exception) {
                println("$TAG ${e.message}")
            }
        }, {
            println("$TAG -- Error -- data fetching")
            println("$TAG -- Error -- ${it?.message}")
        }).autoDispose(disposables)
//        dataManager.getGrievanceTrackingDetails(params).doOnSubscribe {
//            println("\n\n$TAG -- Start fetching grievances data")
//        }.subscribe({
//            try {
//                if (it.getStatus() == "0") {
//                    CoroutineScope(Dispatchers.IO).launch {
//                        io {
//                            val data = it.getTrackingList()
//                            dataManager.insertGrievanceTrackingList(it.getTrackingList())
//                            println("$TAG -- Fetched data successfully")
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                println("$TAG ${e.message}")
//            }
//        }, {
//            println("$TAG -- Error -- data fetching")
//            println("$TAG -- Error -- ${it?.message}")
//        }).autoDispose(disposables)
    }
}