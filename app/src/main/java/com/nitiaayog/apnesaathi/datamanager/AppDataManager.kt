package com.nitiaayog.apnesaathi.datamanager

import android.app.Application
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.networkadapter.api.apimanager.ApiManager
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.ApiRequest
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.BaseRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.LoginRepo
import com.nitiaayog.apnesaathi.networkadapter.retrofit.RetrofitClient
import com.nitiaayog.apnesaathi.preferences.PreferenceManager
import com.nitiaayog.apnesaathi.preferences.PreferenceRequest
import io.reactivex.Single

class AppDataManager private constructor(
    private val apiRequest: ApiRequest, private val preferences: PreferenceRequest
) : DataManager {

    companion object {
        @Volatile
        private var instance: AppDataManager? = null

        @Synchronized
        fun getDataManager(application: Application): AppDataManager =
            instance ?: synchronized(this) {
                instance ?: AppDataManager(
                    ApiManager.getApiRequest(RetrofitClient.createApiClient(application)),
                    PreferenceManager.getPreferenceRequest(application)
                ).also {
                    instance = it
                }
            }
    }

    // ApiRequests
    override fun loginUser(phoneNumber: JsonObject): Single<LoginRepo> =
        apiRequest.loginUser(phoneNumber)

    override fun registerSeniorCitizen(srDetails: JsonObject): Single<BaseRepo> =
        apiRequest.registerSeniorCitizen(srDetails)

    override fun saveSrCitizenFeedback(srCitizenFeedback: JsonObject): Single<BaseRepo> =
        apiRequest.saveSrCitizenFeedback(srCitizenFeedback)

    override fun getSeniorCitizenDetails(srDetails: JsonObject): Single<BaseRepo> =
        apiRequest.getSeniorCitizenDetails(srDetails)

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
    override fun getSelectedLanguage(): String {
        return preferences.getSelectedLanguage()
    }

    override fun setSelectedLanguage(language: String) {
        preferences.setSelectedLanguage(language)
    }
}