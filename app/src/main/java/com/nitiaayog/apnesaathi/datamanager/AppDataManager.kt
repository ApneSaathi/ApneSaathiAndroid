package com.nitiaayog.apnesaathi.datamanager

import android.app.Application
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.database.ApneSathiDatabase
import com.nitiaayog.apnesaathi.database.dao.CallDataDao
import com.nitiaayog.apnesaathi.database.dao.GrievanceTrackingDao
import com.nitiaayog.apnesaathi.database.dao.GrievancesDao
import com.nitiaayog.apnesaathi.database.dao.SyncSrCitizenGrievancesDao
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.GrievanceData
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.model.SyncSrCitizenGrievance
import com.nitiaayog.apnesaathi.networkadapter.api.apimanager.ApiManager
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.ApiRequest
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.BaseRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.HomeRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.grievancedata.GrievanceRespData
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.loginresponse.Login_Response
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.volunteerdata.VolunteerDataResponse
import com.nitiaayog.apnesaathi.networkadapter.retrofit.RetrofitClient
import com.nitiaayog.apnesaathi.preferences.PreferenceManager
import com.nitiaayog.apnesaathi.preferences.PreferenceRequest
import com.nitiaayog.apnesaathi.utility.BaseUtility
import io.reactivex.Single
import java.util.*

class AppDataManager private constructor(
    private val apiRequest: ApiRequest, private val preferences: PreferenceRequest,
    private val dbManager: ApneSathiDatabase
) : DataManager {

    companion object {
        @Volatile
        private var instance: AppDataManager? = null

        @Synchronized
        fun getDataManager(application: Application): AppDataManager =
            instance ?: synchronized(this) {
                instance ?: AppDataManager(
                    ApiManager.getApiRequest(RetrofitClient.createApiClient(application)),
                    PreferenceManager.getPreferenceRequest(application),
                    ApneSathiDatabase.getDatabase(application)
                ).also {
                    instance = it
                }
            }
    }

    private val callsDataDao: CallDataDao by lazy { dbManager.provideCallDataDao() }
    private val grievancesDao: GrievancesDao by lazy { dbManager.provideGrievancesDao() }
    private val grievancesTrackingDao: GrievanceTrackingDao by lazy { dbManager.provideGrievancesTrackingDao() }
    private val syncGrievancesDao: SyncSrCitizenGrievancesDao by lazy {
        dbManager.provideSrCitizenGrievancesDao()
    }

    // ApiRequests
    override fun loginUser(phoneNumber: JsonObject): Single<Login_Response> =
        apiRequest.loginUser(phoneNumber)

    override fun volunteerData(phoneNumber: JsonObject): Single<VolunteerDataResponse> {
        return apiRequest.volunteerData(phoneNumber)
    }

    override fun getCallDetails(details: JsonObject): Single<HomeRepo> =
        apiRequest.getCallDetails(details)

    override fun getGrievanceTrackingDetails(details: JsonObject): Single<GrievanceRespData> =
        apiRequest.getGrievanceTrackingDetails(details)

    override fun registerSeniorCitizen(srDetails: JsonObject): Single<BaseRepo> =
        apiRequest.registerSeniorCitizen(srDetails)

    override fun saveSrCitizenFeedback(srCitizenFeedback: JsonObject): Single<BaseRepo> =
        apiRequest.saveSrCitizenFeedback(srCitizenFeedback)

    override fun getSeniorCitizenDetails(srDetails: JsonObject): Single<BaseRepo> =
        apiRequest.getSeniorCitizenDetails(srDetails)

    override fun updateGrievanceDetails(grDetails: JsonObject): Single<BaseRepo> =
        apiRequest.updateGrievanceDetails(grDetails)

    // Database Access
    // => Table : call_details
    override fun getPendingCallsList(): LiveData<MutableList<CallData>> =
        callsDataDao.getAllCallsList(arrayOf("1","null", "")) //Null and empty should be removed

    override fun getFollowupCallsList(): LiveData<MutableList<CallData>> =
        callsDataDao.getAllCallsList(arrayOf("2", "3", "4", "5", "6"))

    override fun getCompletedCallsList(): LiveData<MutableList<CallData>> =
        callsDataDao.getAllCallsList(arrayOf("10", "9"))

    override fun getAllCallsList(): LiveData<MutableList<CallData>> =
        callsDataDao.getAllCallsList(arrayOf("1", "2", "3", "4", "5", "6", "9", "10","null", ""))  //Null and empty should be removed

    override fun insertCallData(callData: List<CallData>) = callsDataDao.insertOrUpdate(callData)
    override fun getCallDetailFromId(id: Int): CallData = callsDataDao.getCallDetailFromId(id)
    override fun updateCallStatus(callStatus: String) = callsDataDao.update(callStatus)
    override fun updateCallData(callData: CallData): Long = callsDataDao.update(callData)

    override fun insertGrievanceTrackingList(grievanceTracking: List<GrievanceData>) =
        grievancesTrackingDao.insertAll(grievanceTracking)

    // => Table : grievances
    override fun insertGrievance(grievance: SrCitizenGrievance): Long =
        grievancesDao.insert(grievance)

    override fun insertGrievances(grievances: List<SrCitizenGrievance>) =
        grievancesDao.insertAll(grievances)

    override fun getGrievances(): LiveData<MutableList<SrCitizenGrievance>> =
        grievancesDao.getGrievances()

    override fun getGrievance(callId: Int): SrCitizenGrievance? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val date = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-" +
                "${calendar.get(Calendar.DAY_OF_MONTH)}"
        return grievancesDao.getGrievance(
            callId, BaseUtility.format(
                "${date}T00:00:00", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss"
            ), BaseUtility.format(
                "${date}T23:59:59", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss"
            )
        )
    }

    override fun isDataExist(id: Int, callId: Int): SrCitizenGrievance? =
        grievancesDao.isDataExist(id, callId)

    override fun deleteGrievance(grievance: SrCitizenGrievance) =
        grievancesDao.deleteGrievance(grievance)

    override suspend fun updateGrievance(grievance: SrCitizenGrievance) =
        grievancesDao.update(
            grievance.id!!, grievance.callId!!, grievance.hasDiabetic!!,
            grievance.hasBloodPressure!!, grievance.hasLungAilment!!,
            grievance.cancerOrMajorSurgery!!, grievance.otherAilments!!,
            grievance.remarksMedicalHistory!!, grievance.relatedInfoTalkedAbout!!,
            grievance.behavioralChangesNoticed!!, grievance.hasCovidSymptoms!!,
            grievance.hasCough!!, grievance.hasFever!!, grievance.hasShortnessOfBreath!!,
            grievance.hasSoreThroat!!, grievance.quarantineStatus!!,
            grievance.lackOfEssentialServices!!, grievance.foodShortage!!,
            grievance.medicineShortage!!, grievance.accessToBankingIssue!!,
            grievance.utilitySupplyIssue!!, grievance.hygieneIssue!!, grievance.safetyIssue!!,
            grievance.emergencyServiceIssue!!, grievance.phoneAndInternetIssue!!,
            grievance.emergencyServiceRequired!!, grievance.impRemarkInfo!!
        )

    // => Table : sync_grievances_data
    override fun getGrievancesToSync(): List<SyncSrCitizenGrievance>? =
        syncGrievancesDao.getGrievances()

    override fun getAllUniqueGrievances(callId: Int): LiveData<MutableList<SrCitizenGrievance>> =
        grievancesDao.getAllUniqueGrievances(callId)

    override fun clearPreviousData() = grievancesDao.deletePreviousData()

    override fun getAllTrackingGrievances(): LiveData<MutableList<GrievanceData>> =
        grievancesTrackingDao.getAllGrievances()

    override fun getInProgressGrievances(): LiveData<MutableList<GrievanceData>> =
        grievancesTrackingDao.getGrievancesWithStatus("UNDER REVIEW")

    override fun getPendingGrievances(): LiveData<MutableList<GrievanceData>> =
        grievancesTrackingDao.getGrievancesWithStatus("RAISED")

    override fun getResolvedGrievances(): LiveData<MutableList<GrievanceData>> =
        grievancesTrackingDao.getGrievancesWithStatus("RESOLVED")

    override fun clearPreviousTrackingData() =  grievancesTrackingDao.deletePreviousGrievanceData()

    override suspend fun insertSyncGrievance(syncData: SyncSrCitizenGrievance) =
        syncGrievancesDao.insertOrUpdate(syncData)

    override fun delete(syncData: SyncSrCitizenGrievance) =
        syncGrievancesDao.delete(syncData.id!!, syncData.callId!!, syncData.volunteerId!!)

    // PreferenceRequests
    override fun isLogin(): Boolean = preferences.isLogin()

    override fun updateUserPreference(loginUser: Login_Response) {
        setUserId(loginUser.getVolunteerId()!!)
//        setUserName(loginUser.userName)
//        //setProfileImage(loginUser.userProfileImage)
//        setPhoneNumber(loginUser.phoneNumber)

    }

    override fun getUserId(): String = preferences.getUserId()

    override fun setUserId(userId: String) = preferences.setUserId(userId)

    override fun getUserName(): String = preferences.getUserName()

    override fun setUserName(userName: String) = preferences.setUserName(userName)

    override fun getGender() = preferences.getGender()

    override fun setGender(gender: String) = preferences.setGender(gender)

    override fun getProfileImage(): String = preferences.getProfileImage()

    override fun setProfileImage(profileImage: String) = preferences.setProfileImage(profileImage)

    override fun getPhoneNumber(): String = preferences.getPhoneNumber()

    override fun setPhoneNumber(phoneNumber: String) = preferences.setPhoneNumber(phoneNumber)

    override fun getSelectedLanguage(): String = preferences.getSelectedLanguage()

    override fun setSelectedLanguage(language: String) = preferences.setSelectedLanguage(language)
    override fun getFirstname(): String = preferences.getFirstname()

    override fun setFirstName(fname: String) = preferences.setFirstName(fname)


    override fun getLastname(): String = preferences.getLastname()

    override fun setLastname(lastname: String) = preferences.setLastname(lastname)


    override fun getEmail(): String = preferences.getEmail()

    override fun setEmail(email: String) = preferences.setEmail(email)


    override fun getAddress(): String = preferences.getAddress()

    override fun setAddress(address: String) = preferences.setAddress(address)

}