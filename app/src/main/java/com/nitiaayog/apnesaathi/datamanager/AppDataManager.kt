package com.nitiaayog.apnesaathi.datamanager

import android.app.Application
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.database.ApneSathiDatabase
import com.nitiaayog.apnesaathi.database.dao.CallDataDao
import com.nitiaayog.apnesaathi.database.dao.GrievancesDao
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.networkadapter.api.apimanager.ApiManager
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.ApiRequest
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.BaseRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.HomeRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.LoginRepo
import com.nitiaayog.apnesaathi.networkadapter.retrofit.RetrofitClient
import com.nitiaayog.apnesaathi.preferences.PreferenceManager
import com.nitiaayog.apnesaathi.preferences.PreferenceRequest
import io.reactivex.Single

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

    // ApiRequests
    override fun loginUser(phoneNumber: JsonObject): Single<LoginRepo> =
        apiRequest.loginUser(phoneNumber)

    override fun getCallDetails(details: JsonObject): Single<HomeRepo> =
        apiRequest.getCallDetails(details)

    override fun registerSeniorCitizen(srDetails: JsonObject): Single<BaseRepo> =
        apiRequest.registerSeniorCitizen(srDetails)

    override fun saveSrCitizenFeedback(srCitizenFeedback: JsonObject): Single<BaseRepo> =
        apiRequest.saveSrCitizenFeedback(srCitizenFeedback)

    // Database Access
    override fun insertCallData(callData: List<CallData>) = callsDataDao.insertAll(callData)

    override fun getAllCallsList(): LiveData<MutableList<CallData>> = callsDataDao.getAllCallsList()

    override fun insertGrievances(grievances: List<SrCitizenGrievance>) =
        grievancesDao.insertAll(grievances)

    override fun getAllGrievances(): LiveData<MutableList<SrCitizenGrievance>> =
        grievancesDao.getAllGrievances()

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