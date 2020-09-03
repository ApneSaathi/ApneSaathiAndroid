package com.nitiaayog.apnesaathi.networkadapter.api.apimanager

import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.subscribeAndObserve
import com.nitiaayog.apnesaathi.base.extensions.rx.subscribeAndObserveWithDelaySubscription
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.ApiInterface
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.ApiRequest
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.BaseRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.HomeRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.SeniorCitizenRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.VolunteerRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.emergencycontact.EmergencyContactResponse
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.grievancedata.GrievanceRespData
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.loginresponse.LoginResponse
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.profileupdate.ProfileUpdateResponse
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.volunteerdata.VolunteerDataResponse
import com.nitiaayog.apnesaathi.networkadapter.retrofit.RetrofitClient
import io.reactivex.Single

/**
 * [ApiManager] important class
 * [apiClient] this interface available the web complete service functions with parameter
 */
class ApiManager private constructor(private val apiClient: ApiInterface) : ApiRequest {

    companion object {
        @Volatile
        private var instance: ApiManager? = null

        @Synchronized
        fun getApiRequest(apiClient: ApiInterface): ApiManager =
            instance ?: synchronized(this) { ApiManager(apiClient).also { instance = it } }
    }


    /**
     * Method for user login service
     * [param] bind all the parameter for login api
     */
    override fun loginUser(param: JsonObject): Single<LoginResponse> {
        return apiClient.loginUser(param).subscribeAndObserveWithDelaySubscription()
    }

    /**
     * Method for user verifyPassword service
     * [param] bind all the parameter for verifyPassword api
     */
    override suspend fun verifyPassword(params: JsonObject): Single<BaseRepo> {
        return apiClient.verifyPassword(params).subscribeAndObserveWithDelaySubscription()
    }

    /**
     * Method for getVolunteers service
     * [param] bind all the parameter for getVolunteers api
     */
    override suspend fun getVolunteers(params: JsonObject): Single<VolunteerRepo> {
        return apiClient.getVolunteers(params).subscribeAndObserveWithDelaySubscription()
    }

    /**
     * Method for volunteer details service
     * [param] bind all the parameter for volunteerData api
     */
    override fun volunteerData(param: JsonObject): Single<VolunteerDataResponse> {
        return apiClient.getVolunteerData(param).subscribeAndObserveWithDelaySubscription()
    }

    /**
     * Method for volunteer update details data service
     * [param] bind all the parameter for updatevolunteerData api
     */
    override fun updatevolunteerData(param: JsonObject): Single<ProfileUpdateResponse> {
        return apiClient.getUpdateVolunteerData(param)
            .subscribeAndObserveWithDelaySubscription()
    }

    /**
     * Method for getCallDetails service
     * [details] bind all the parameter for getCallDetails api
     */
    override fun getCallDetails(details: JsonObject): Single<HomeRepo> {
        return apiClient.getCallDetails(details).subscribeAndObserveWithDelaySubscription()
    }

    override fun getGrievanceTrackingDetails(details: JsonObject): Single<GrievanceRespData> {
        return apiClient.getGrievanceTrackingDetails(details)
            .subscribeAndObserveWithDelaySubscription()
    }

    /**
     * Method for saveSrCitizenFeedback service
     * [srCitizenFeedback] bind all the parameter for getCallDetails api
     */
    override fun saveSrCitizenFeedback(srCitizenFeedback: JsonObject): Single<BaseRepo> {
        return apiClient.saveSrCitizenFeedback(srCitizenFeedback)
            .subscribeAndObserveWithDelaySubscription()
    }

    /**
     * Method for registerSeniorCitizen service
     * [srDetails] bind all the parameter for registerSeniorCitizen api
     */
    override fun registerSeniorCitizen(srDetails: JsonObject): Single<BaseRepo> {
        return apiClient.registerSeniorCitizen(srDetails).subscribeAndObserveWithDelaySubscription()
    }

    /**
     * Method for getSeniorCitizenDetails service
     * [srDetails] bind all the parameter for getSeniorCitizenDetails api
     */
    override fun getSeniorCitizenDetails(srDetails: JsonObject): Single<SeniorCitizenRepo> {
        return apiClient.getSeniorCitizenDetails(srDetails)
            .subscribeAndObserveWithDelaySubscription()
    }

    /**
     * Method for updateGrievanceDetails service
     * [grDetails] bind all the parameter for updateGrievanceDetails api
     */
    override fun updateGrievanceDetails(grDetails: JsonObject): Single<BaseRepo> {
        return apiClient.updateGrievanceDetails(grDetails).subscribeAndObserve()
    }

    /**
     * Method for updateVolunteerRatings service
     * [params] bind all the parameter for updateVolunteerRatings api
     */
    override fun updateVolunteerRatings(params: JsonObject): Single<BaseRepo> {
        return apiClient.updateVolunteerRatings(params).subscribeAndObserveWithDelaySubscription()
    }

    /**
     * Method for getEmergencyContact service
     * [params] bind all the parameter for getEmergencyContact api
     */
    override fun getEmergencyContact(params: JsonObject): Single<EmergencyContactResponse> {
        return apiClient.getEmergencyContact(params).subscribeAndObserve()
    }
}