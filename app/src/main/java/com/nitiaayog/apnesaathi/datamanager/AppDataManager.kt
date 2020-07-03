package com.nitiaayog.apnesaathi.datamanager

import android.app.Application
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.database.ApneSathiDatabase
import com.nitiaayog.apnesaathi.database.dao.CallDataDao
import com.nitiaayog.apnesaathi.database.dao.GrievancesDao
import com.nitiaayog.apnesaathi.database.dao.SyncSrCitizenGrievancesDao
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.model.SyncSrCitizenGrievance
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.networkadapter.api.apimanager.ApiManager
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.ApiRequest
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.BaseRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.HomeRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.LoginRepo
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
    private val syncGrievancesDao: SyncSrCitizenGrievancesDao by lazy {
        dbManager.provideSrCitizenGrievancesDao()
    }

    // ApiRequests
    override fun loginUser(phoneNumber: JsonObject): Single<LoginRepo> =
        apiRequest.loginUser(phoneNumber)

    override fun getCallDetails(details: JsonObject): Single<HomeRepo> =
        apiRequest.getCallDetails(details)

    override fun registerSeniorCitizen(srDetails: JsonObject): Single<BaseRepo> =
        apiRequest.registerSeniorCitizen(srDetails)

    override fun saveSrCitizenFeedback(srCitizenFeedback: JsonObject): Single<BaseRepo> =
        apiRequest.saveSrCitizenFeedback(srCitizenFeedback)

    override fun getSeniorCitizenDetails(srDetails: JsonObject): Single<BaseRepo> =
        apiRequest.getSeniorCitizenDetails(srDetails)

    // Database Access
    // => Table : call_details
    override fun insertCallData(callData: List<CallData>) = callsDataDao.insertOrUpdate(callData)
    override fun getAllCallsList(): LiveData<MutableList<CallData>> = callsDataDao.getAllCallsList()
    override fun getCallDetailFromId(id: Int): CallData = callsDataDao.getCallDetailFromId(id)
    override fun updateCallStatus(callStatus: String) = callsDataDao.update(callStatus)

    // => Table : grievances
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

    override suspend fun update(grievance: SrCitizenGrievance) =
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

    override suspend fun insert(syncData: SyncSrCitizenGrievance) =
        syncGrievancesDao.insertOrUpdate(syncData)

    override fun delete(syncData: SyncSrCitizenGrievance) =
        syncGrievancesDao.delete(syncData.id!!, syncData.callId!!, syncData.volunteerId!!)

    // PreferenceRequests
    override fun isLogin(): Boolean = preferences.isLogin()

    override fun updateUserPreference(loginUser: User) {
        setUserId(loginUser.userId)
        setUserName(loginUser.userName)
        //setProfileImage(loginUser.userProfileImage)
        setPhoneNumber(loginUser.phoneNumber)
    }

    override fun getUserId(): String = preferences.getUserId()

    override fun setUserId(userId: String) = preferences.setUserId(userId)

    override fun getUserName(): String = preferences.getUserName()

    override fun setUserName(userName: String) = preferences.setUserName(userName)

    override fun getProfileImage(): String = preferences.getProfileImage()

    override fun setProfileImage(profileImage: String) = preferences.setProfileImage(profileImage)

    override fun getPhoneNumber(): String = preferences.getPhoneNumber()

    override fun setPhoneNumber(phoneNumber: String) = preferences.setPhoneNumber(phoneNumber)

    override fun getSelectedLanguage(): String = preferences.getSelectedLanguage()

    override fun setSelectedLanguage(language: String) = preferences.setSelectedLanguage(language)
}