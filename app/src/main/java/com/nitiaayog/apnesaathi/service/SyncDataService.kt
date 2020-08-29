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
import com.nitiaayog.apnesaathi.model.*
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.utility.NetworkProvider
import com.nitiaayog.apnesaathi.utility.ROLE_VOLUNTEER
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * This Class is the most important class for syncing of SR. Citizen Feedback Form when internet
 * is not available and stored in room DB.
 *
 * Whenever network will available Android system will automatically trigger this class and
 * start data syncing and fetch the data.
 * */
class SyncDataService : JobService() {

    companion object {

        private const val TAG: String = "TAG -- JobService -- syncData --> "
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
            println("\n\n$TAG onStartJob -- Internet Available - $dateTime")
            CoroutineScope(Dispatchers.IO).launch {
                val processData = dataManager.getGrievancesToSync()
                println("\n$TAG onStartJob -- ${processData?.size} data will be synced")
                processData?.run {
                    if (processData.isEmpty()) jobFinished(params, true)
                    else {
                        io {
                            startSyncing(processData)
                        }
                    }
                    if (processData.isNotEmpty()) {
                        getFetchData()
                    }
                }
            }
        } else println("\n$TAG onStartJob -- No Internet - $dateTime")

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        println("\n$TAG onStopJob -- Job Completed")
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

        params.addProperty(ApiConstants.SrCitizenCallStatusCode, grievance.callStatusSubCode)

        params.addProperty(ApiConstants.SrCitizenTalkedWith, grievance.talkedWith)
        params.addProperty(ApiConstants.SrCitizenName, grievance.srCitizenName)
        params.addProperty(ApiConstants.SrCitizenGender, grievance.gender)

        params.addProperty(ApiConstants.ImpRemarkInfo, grievance.impRemarkInfo)
        params.addProperty(ApiConstants.Role, dataManager.getRole())

        if (grievance.callStatusSubCode != "10") return params

        val arraySubParams = JsonObject()
        arraySubParams.addProperty(ApiConstants.CallId, grievance.callId)
        arraySubParams.addProperty(ApiConstants.VolunteerId, dataManager.getUserId())

        arraySubParams.addProperty(ApiConstants.IsDiabetic, grievance.hasDiabetic)
        arraySubParams.addProperty(ApiConstants.IsBloodPressure, grievance.hasBloodPressure)
        arraySubParams.addProperty(ApiConstants.LungAilment, grievance.hasLungAilment)
        arraySubParams.addProperty(
            ApiConstants.CancerOrMajorSurgery, grievance.cancerOrMajorSurgery
        )
        arraySubParams.addProperty(ApiConstants.OtherAilments, grievance.otherAilments)

        arraySubParams.addProperty(ApiConstants.RemarkOnMedicalHistory, "")

        arraySubParams.addProperty(ApiConstants.InfoTalkAbout, grievance.relatedInfoTalkedAbout)

        arraySubParams.addProperty(
            ApiConstants.IsSrCitizenAwareOfCovid19, grievance.hasSrCitizenAwareOfCovid19
        )
        arraySubParams.addProperty(
            ApiConstants.IsSymptomsPreventionDiscussed, grievance.hasSymptomsPreventionDiscussed
        )

        arraySubParams.addProperty(
            ApiConstants.NoticedBehaviouralChange, grievance.behavioralChangesNoticed
        )
        arraySubParams.addProperty(
            ApiConstants.WhichPracticeNotFollowed, grievance.whichPracticesNotFollowed
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
            ApiConstants.Description, grievance.description
        )
        arraySubParams.addProperty(
            ApiConstants.IsEmergencyServicesRequired, grievance.emergencyServiceRequired
        )
        arraySubParams.addProperty(
            ApiConstants.LackOfEssentialServices, grievance.lackOfEssentialServices
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
        callData.forEach { data ->
            grievances.addAll(data.medicalGrievance!!)
        }
        return grievances
    }

    private fun manageCallsData(data: CallDetails) {
        dataManager.clearPreviousData()
        dataManager.insertCallData(data.callsList)

        val grievances: List<SrCitizenGrievance> = prepareGrievances(data.callsList)
        dataManager.insertGrievances(grievances)
    }

    private fun manageAdminData(data: AdminCallDetails) {
        dataManager.clearDistricts()
        dataManager.clearPreviousData()

        dataManager.insertCallData(data.adminCallsList)
        dataManager.insertDistrictData(data.adminDistrictList)

        val grievances: List<SrCitizenGrievance> = prepareGrievances(data.adminCallsList)
        dataManager.insertGrievances(grievances)
    }

    private suspend fun startSyncing(processData: List<SyncSrCitizenGrievance>) =
        processData.forEach { syncData(it) }

    private suspend fun syncData(grievance: SyncSrCitizenGrievance) {
        val params = preparePostParams(grievance)
        val disposable = dataManager.saveSrCitizenFeedback(params).doOnSubscribe {
            println("$TAG syncData -- params --> $params")
        }.subscribe({
            if (it.status == "0") {
                println("$TAG Successful -- ${grievance.callId} -- ${grievance.id}")
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
                                grievance.deleteAfterSync = 1
                                dataManager.insertGrievance(grievance)
                            } else
                                dataManager.updateGrievance(grievance)
                        } catch (e: Exception) {
                            println("$TAG syncData -- catch --> ${e.message}")
                        }
                    }
                    println("$TAG syncData -- sync --> success")
                }
            } else println("$TAG Failed -- ${grievance.callId} -- ${grievance.id}")
        }, {
            println("$TAG Failed -- ${grievance.callId} -- ${grievance.id}")
            println("$TAG Failed --> Exception")
            println("$TAG Failed --> ${it?.message}")
        })
        disposables.add(disposable)
    }

    private suspend fun getFetchData() {
        val params = JsonObject()
        params.addProperty(ApiConstants.Id, dataManager.getUserId().toInt())
        params.addProperty(ApiConstants.FilterBy, dataManager.getRole())
        dataManager.getCallDetails(params).doOnSubscribe {
            println("\n\n$TAG getFetchData -- Start fetching data")
        }.subscribe({
            try {
                if (it.status == "0") {
                    println("\n\n$TAG getFetchData -- Fetch Successful")
                    CoroutineScope(Dispatchers.IO).launch {
                        io {
                            if (dataManager.getRole() == ROLE_VOLUNTEER) {
                                val data = it.getData()
                                manageCallsData(data)
                            } else {
                                val data = it.getAdminData()
                                manageAdminData(data)
                            }
                            println("$TAG getFetchData -- Fetched data successfully")
                        }
                    }
                }
            } catch (e: Exception) {
                println("$TAG getFetchData -- ${e.message}")
            }
        }, {
            println("$TAG getFetchData -- Error -- data fetching")
            println("$TAG getFetchData -- Error -- ${it?.message}")
        }).autoDispose(disposables)
    }
}