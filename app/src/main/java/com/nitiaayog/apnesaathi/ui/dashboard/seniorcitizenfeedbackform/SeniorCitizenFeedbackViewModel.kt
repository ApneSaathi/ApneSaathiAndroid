package com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenfeedbackform

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.model.SyncSrCitizenGrievance
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import com.nitiaayog.apnesaathi.utility.BaseUtility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SeniorCitizenFeedbackViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {
        @Synchronized
        fun getInstance(dataManager: DataManager): SeniorCitizenFeedbackViewModel =
            synchronized(this) { SeniorCitizenFeedbackViewModel(dataManager) }
    }

    // For updating Sr. Citizen data / Feedback form over a call
    private var callStatus: String = ""
    private var talkedWith: String = ""
    private var behaviorChange: String = ""
    private var isAwareOfCovid19: String = ""
    private var whichPracticeNotAllowed: String = ""
    private var isSymptomsPreventionDiscussed: String = ""
    private var otherMedicalProblem: String = ""
    private var quarantineStatus: String = ""
    private var essentialServices: String = ""
    private var impDescription: String = ""
    private var emergencyEscalation: String = "" // isemergencyservicerequired
    private var isLackOfEssential: String = "" // isemergencyservicerequired

    private var covideSymptoms: Boolean = false
    private var symptomsCough: Boolean = false
    private var symptomsFever: Boolean = false
    private var symptomsShortBreath: Boolean = false
    private var symptomSoreThroat: Boolean = false

    private var seniorCitizenAtHome: Boolean = false

    private val medicalHistory: MutableList<String> = mutableListOf()
    private val talkedAbout: MutableList<String> = mutableListOf()
    private val complaints: MutableList<String> = mutableListOf()

    private lateinit var callData: CallData
    private var grievance: SrCitizenGrievance? = null

    // For Registering new Sr. Citizen
    private var name: String = ""
    private var age: String = ""
    private var gender: String = ""
    private var state: String = ""
    private var district: String = ""
    private var contactNumber: String = ""
    private var address: String = ""

    /*private fun insertInSyncTable(details: JsonObject): SyncSrCitizenGrievance {
        val syncData = SyncSrCitizenGrievance().apply {
            this.id = details.get(ApiConstants.CallId) as Int
            this.callId = details.callId
            this.volunteerId = details.volunteerId
            this.hasDiabetic = details.hasDiabetic
            this.hasBloodPressure = details.hasBloodPressure
            this.hasLungAilment = details.hasLungAilment
            this.cancerOrMajorSurgery = details.cancerOrMajorSurgery
            this.otherAilments = details.otherAilments
            this.remarksMedicalHistory = details.remarksMedicalHistory
            this.relatedInfoTalkedAbout = details.relatedInfoTalkedAbout
            this.behavioralChangesNoticed = details.behavioralChangesNoticed
            this.hasCovidSymptoms = details.hasCovidSymptoms
            this.hasCough = details.hasCough
            this.hasFever = details.hasFever
            this.hasShortnessOfBreath = details.hasShortnessOfBreath
            this.hasSoreThroat = details.hasSoreThroat
            this.quarantineStatus = details.quarantineStatus
            this.foodShortage = details.foodShortage
            this.medicineShortage = details.medicineShortage
            this.accessToBankingIssue = details.accessToBankingIssue
            this.utilitySupplyIssue = details.utilitySupplyIssue
            this.hygieneIssue = details.hygieneIssue
            this.safetyIssue = details.safetyIssue
            this.emergencyServiceIssue = details.emergencyServiceIssue
            this.phoneAndInternetIssue = details.phoneAndInternetIssue
            this.emergencyServiceRequired = details.emergencyServiceRequired
            this.impRemarkInfo = details.impRemarkInfo
        }
        println("TAG -- MyData --> $syncData")
        dataManager.insert(syncData)
        return syncData
    }

    private fun preparePostParams(medicalHistory: Array<String>): JsonObject {
        val params = JsonObject()
        params.addProperty(ApiConstants.CallId, callData.callId)
        params.addProperty(ApiConstants.VolunteerId, dataManager.getUserId())
        params.addProperty(ApiConstants.SrCitizenCallStatusSubCode, callStatus)
        params.addProperty(
            ApiConstants.SrCitizenTalkedWith, talkedWith.toUpperCase(Locale.getDefault())
        )

        val arraySubParams = JsonObject()
        params.addProperty(ApiConstants.CallId, callData.callId)
        params.addProperty(ApiConstants.VolunteerId, dataManager.getUserId())
        medicalHistory.forEach {

        }
        params.addProperty(
            ApiConstants.IsDiabetic,
            if (medicalHistory.any { it == isDiabetic }) "Y" else "N"
        )
        params.addProperty(
            ApiConstants.IsBloodPressure,
            if (medicalHistory.any { it == bloodPressure }) "Y" else "N"
        )
        params.addProperty(
            ApiConstants.LungAilment,
            if (medicalHistory.any { it == bloodPressure }) "Y" else "N"
        )


        val jsonArray = JsonArray()
        jsonArray[0] = arraySubParams

        println("TAG -- MyData --> ${params.toString()}")

        return params
    }*/

    fun getCallDetailFromId(id: Int): CallData {
        callData = dataManager.getCallDetailFromId(id)
        return callData
    }

    fun getGrievance(callId: Int): SrCitizenGrievance? {
        grievance = dataManager.getGrievance(callId)
        return grievance
    }

    fun getDataObserver(): LiveData<NetworkRequestState> = loaderObservable

    fun getCallStatus(): String = callStatus
    fun setCallStatus(callStatus: String) {
        this.callStatus = callStatus
    }

    fun getTalkedWith(): String = talkedWith
    fun setTalkedWith(talkedWith: String) {
        this.talkedWith = talkedWith
    }

    fun getBehaviorChange(): String = behaviorChange
    fun setBehaviorChange(behaviorChange: String) {
        this.behaviorChange = behaviorChange
    }

    fun getTalkedAbout(): List<String> = talkedAbout
    fun addTalkedAbout(talkedAbout: String) {
        val filterList = this.talkedAbout.filter { it == talkedAbout }
        if (filterList.isEmpty()) this.talkedAbout.add(talkedAbout)
    }

    fun removeTalkedAbout(talkedAbout: String) {
        val filterList = this.talkedAbout.filter { it == talkedAbout }
        if (filterList.isNotEmpty()) this.talkedAbout.removeAt(this.talkedAbout.indexOf(talkedAbout))
    }

    fun resetTalkedAbout() = talkedAbout.clear()

    fun isAwareOfCovid19(): String = isAwareOfCovid19
    fun setAwareOfCovid19(isAwareOfCovid19: String) {
        this.isAwareOfCovid19 = isAwareOfCovid19
        if (isAwareOfCovid19.toLowerCase(Locale.ENGLISH) == "y")
            setSymptomsPreventionDiscussed("")
    }

    fun getPracticeNotAllowed(): String = whichPracticeNotAllowed
    fun setPracticeNotAllowed(whichPracticeNotAllowed: String) {
        this.whichPracticeNotAllowed = whichPracticeNotAllowed
    }

    fun isSymptomsPreventionDiscussed(): String = isSymptomsPreventionDiscussed
    fun setSymptomsPreventionDiscussed(isSymptomsPreventionDiscussed: String) {
        this.isSymptomsPreventionDiscussed = isSymptomsPreventionDiscussed
    }

    fun isCovideSymptoms(): Boolean = covideSymptoms
    fun isCovideSymptoms(covideSymptoms: Boolean) {
        this.covideSymptoms = covideSymptoms
        if (!this.covideSymptoms) {
            symptomsCough = false
            symptomsFever = false
            symptomsShortBreath = false
            symptomSoreThroat = false
        }
    }

    fun isCoughSymptoms(): Boolean = symptomsCough
    fun isCoughSymptoms(symptomsCough: Boolean) {
        this.symptomsCough = symptomsCough
    }

    fun isFeverSymptom(): Boolean = symptomsFever
    fun isFeverSymptom(symptomsFever: Boolean) {
        this.symptomsFever = symptomsFever
    }

    fun isShortBreathSymptoms(): Boolean = symptomsShortBreath
    fun isShortBreathSymptoms(symptomsShortBreath: Boolean) {
        this.symptomsShortBreath = symptomsShortBreath
    }

    fun isSoreThroatSymptom(): Boolean = symptomSoreThroat
    fun isSoreThroatSymptom(symptomSoreThroat: Boolean) {
        this.symptomSoreThroat = symptomSoreThroat
    }

    fun getOtherMedicalProblem(): String = otherMedicalProblem
    fun setOtherMedicalProblem(otherMedicalProblem: String) {
        this.otherMedicalProblem = otherMedicalProblem
    }

    fun getQuarantineStatus(): String = quarantineStatus
    fun setQuarantineStatus(quarantineStatus: String) {
        this.quarantineStatus = quarantineStatus
    }

    /*fun getEssentialServices(): String = essentialServices
    fun setEssentialServices(essentialServices: String) {
        this.essentialServices = essentialServices
    }*/

    fun getImpDescription(): String = impDescription
    fun setImpDescription(impDescription: String) {
        this.impDescription = impDescription
    }

    fun isSeniorCitizenAtHome(): Boolean = seniorCitizenAtHome
    fun isSeniorCitizenAtHome(seniorCitizenAtHome: Boolean) {
        this.seniorCitizenAtHome = seniorCitizenAtHome
    }

    fun isEmergencyEscalation(): String = emergencyEscalation
    fun isEmergencyEscalation(emergencyEscalation: String) {
        this.emergencyEscalation = emergencyEscalation
    }

    fun isLackOfEssential(): String = isLackOfEssential
    fun setLackOfEssential(isLackOfEssential: String) {
        this.isLackOfEssential = isLackOfEssential
    }

    fun getMedicalHistory() = medicalHistory

    fun setName(name: String) {
        this.name = name
    }

    fun setAge(age: String) {
        this.age = age
    }

    fun setGender(gender: String) {
        this.gender = gender
    }

    fun getGender(): String = gender

    fun setContactNumber(contactNumber: String) {
        this.contactNumber = contactNumber
    }

    fun setState(state: String) {
        this.state = state
    }

    fun getState(): String = state

    fun setDistrict(district: String) {
        this.district = district
    }

    fun getDistrict(): String = district

    fun setAddress(address: String) {
        this.address = address
    }

    fun addMedicalHistory(history: String) {
        val filterList = medicalHistory.filter { it == history }
        if (filterList.isEmpty()) medicalHistory.add(history)
    }

    fun removeMedicalHistory(history: String) {
        val filterList = medicalHistory.filter { it == history }
        if (filterList.isNotEmpty()) medicalHistory.removeAt(medicalHistory.indexOf(history))
    }

    fun resetMedicalHistory() = medicalHistory.clear()

    fun getComplaints() = complaints

    fun addComplaint(complaint: String) {
        val filterList = complaints.filter { it == complaint }
        if (filterList.isEmpty()) complaints.add(complaint)
    }

    fun removeComplaint(complaint: String) {
        val filterList = complaints.filter { it == complaint }
        if (filterList.isNotEmpty()) complaints.removeAt(complaints.indexOf(complaint))
    }

    fun resetComplaints() = complaints.clear()

    fun clearData() {
        talkedWith = ""
        behaviorChange = ""
        otherMedicalProblem = ""
        quarantineStatus = ""
        essentialServices = ""
        emergencyEscalation = ""
        isAwareOfCovid19 = ""
        isSymptomsPreventionDiscussed = ""

        seniorCitizenAtHome = false
    }

    fun saveSrCitizenFeedback(
        context: Context, params: JsonObject, syncData: SyncSrCitizenGrievance
    ) {
        viewModelScope.launch {
            io {
                if (::callData.isInitialized && callData.talkedWith != talkedWith) {
                    callData.callStatusCode = callStatus
                    callData.talkedWith = syncData.talkedWith
                    dataManager.updateCallData(callData)
                }
                if (callStatus == "10") {
                    val updateData: SrCitizenGrievance = syncData
                    if (syncData.id == -1) {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = System.currentTimeMillis()
                        val date: String =
                            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-" +
                                    "${calendar.get(Calendar.DAY_OF_MONTH)} ${calendar.get(Calendar.HOUR_OF_DAY)}" +
                                    ":${calendar.get(Calendar.MINUTE)}:${calendar.get(Calendar.SECOND)}"
                        val createdDate = BaseUtility.format(
                            date, BaseUtility.FORMAT_LOCAL_DATE_TIME,
                            BaseUtility.FORMAT_SERVER_DATE_TIME
                        )
                        syncData.id = BaseUtility.getRandomNumber(8).toInt()
                        syncData.createdDate = createdDate

                        updateData.id = syncData.id
                        updateData.createdDate = syncData.createdDate
                        updateData.deleteAfterSync = 1
                        //dataManager.updateCallStatus(callStatus)
                        dataManager.insertGrievance(updateData)
                    } else dataManager.updateGrievance(updateData)
                }

                val data = dataManager.getGrievance(callData.callId!!)
                data?.run {
                    println("TAG -- MyData --> ${this.hasDiabetic}")
                    println("TAG -- MyData --> ${this.hasLungAilment}")
                    println("TAG -- MyData --> ${this.relatedInfoTalkedAbout}")
                }

                val mSyncData = dataManager.getGrievancesToSync()
                println("TAG -- MyData --> ${mSyncData?.size}")
            }
            if (checkNetworkAvailability(context, ApiProvider.ApiSaveSeniorCitizenFeedbackForm)) {
                /**
                 * This api will save and create new issue.
                 * */
                params.addProperty(ApiConstants.Role, dataManager.getRole())
                dataManager.saveSrCitizenFeedback(params).doOnSubscribe {
                    loaderObservable.value =
                        NetworkRequestState.LoadingData(ApiProvider.ApiSaveSeniorCitizenFeedbackForm)
                }.subscribe({
                    if (it.status == "0") {
                        viewModelScope.launch {
                            io {
                                // If synced successfully with server then just remove it from
                                // SyncSrCitizenGrievance Table
                                if (it.grievanceId.isNotEmpty() && (it.grievanceId != "-1") &&
                                    ::callData.isInitialized
                                ) {
                                    try {
                                        dataManager.updateCallStatus(callStatus)
                                        if (dataManager.isDataExist(
                                                syncData.id!!, syncData.callId!!
                                            ) == null
                                        ) {
                                            dataManager.delete(syncData)
                                            syncData.id = it.grievanceId.toInt()
                                            syncData.deleteAfterSync = 1
                                            dataManager.insertGrievance(syncData)
                                        } /*else
                                            dataManager.updateGrievance(syncData)*/
                                    } catch (e: Exception) {
                                        println("TAG -- MyData --> ${e.message}")
                                    }
                                }
                                dataManager.delete(syncData)
                            }
                        }
                        loaderObservable.value = NetworkRequestState.SuccessResponse(
                            ApiProvider.ApiSaveSeniorCitizenFeedbackForm, it
                        )
                    } else {
                        loaderObservable.value =
                            NetworkRequestState.Error(ApiProvider.ApiSaveSeniorCitizenFeedbackForm)
                        CoroutineScope(Dispatchers.IO).launch {
                            dataManager.insertSyncGrievance(syncData)
                        }
                    }
                }, {
                    try {
                        loaderObservable.value = NetworkRequestState.ErrorResponse(
                            ApiProvider.ApiSaveSeniorCitizenFeedbackForm
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            dataManager.insertSyncGrievance(syncData)
                        }
                    } catch (e: Exception) {
                        println("TAG -- MyData --> ${e.message}")
                    }
                }).autoDispose(disposables)
            } else {
                CoroutineScope(Dispatchers.IO).launch { dataManager.insertSyncGrievance(syncData) }
            }
        }
    }

    fun registerNewSeniorCitizen(context: Context) {
        if (checkNetworkAvailability(context, ApiProvider.ApiRegisterSeniorCitizen)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.Id, dataManager.getUserId().toInt())
            params.addProperty(ApiConstants.SrCitizenName, name)
            params.addProperty(ApiConstants.SrCitizenAge, age.toInt())
            params.addProperty(ApiConstants.SrCitizenGender, gender)
            params.addProperty(ApiConstants.SrCitizenContactNumber, contactNumber)
            params.addProperty(ApiConstants.SrCitizenState, state)
            params.addProperty(ApiConstants.SrCitizenDistrict, district)
            params.addProperty(ApiConstants.SrCitizenBlock, address)
            params.addProperty(ApiConstants.Role, dataManager.getRole())
            params.addProperty(ApiConstants.SrCitizenCallStatusCode, 1)
            dataManager.registerSeniorCitizen(params).doOnSubscribe {
                loaderObservable.value =
                    NetworkRequestState.LoadingData(ApiProvider.ApiRegisterSeniorCitizen)
            }.subscribe({
                if (it.status == "0")
                    loaderObservable.value = NetworkRequestState.SuccessResponse(
                        ApiProvider.ApiRegisterSeniorCitizen, it
                    )
                else loaderObservable.value =
                    NetworkRequestState.Error(ApiProvider.ApiRegisterSeniorCitizen)
            }, {
                loaderObservable.value =
                    NetworkRequestState.ErrorResponse(ApiProvider.ApiRegisterSeniorCitizen, it)
            }).autoDispose(disposables)
        }
    }
}