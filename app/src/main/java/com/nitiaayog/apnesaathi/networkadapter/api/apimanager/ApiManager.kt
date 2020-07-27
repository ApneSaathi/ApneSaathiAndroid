package com.nitiaayog.apnesaathi.networkadapter.api.apimanager

import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.subscribeAndObserve
import com.nitiaayog.apnesaathi.base.extensions.rx.subscribeAndObserveWithDelaySubscription
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.ApiInterface
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.ApiRequest
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.BaseRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.HomeRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.grievancedata.GrievanceRespData
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.loginresponse.Login_Response
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.profileupdate.ProfileUpdateResponse
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.volunteerdata.VolunteerDataResponse
import io.reactivex.Single

class ApiManager private constructor(private val apiClient: ApiInterface) : ApiRequest {

    companion object {
        @Volatile
        private var instance: ApiManager? = null

        @Synchronized
        fun getApiRequest(apiClient: ApiInterface): ApiManager =
            instance ?: synchronized(this) { ApiManager(apiClient).also { instance = it } }
    }

    override fun loginUser(phoneNumber: JsonObject): Single<Login_Response> =
        apiClient.loginUser(phoneNumber).subscribeAndObserveWithDelaySubscription()

    override fun volunteerData(phoneNumber: JsonObject): Single<VolunteerDataResponse> {
        return apiClient.getVolunteerData(phoneNumber).subscribeAndObserveWithDelaySubscription()
    }

    override fun updatevolunteerData(phoneNumber: JsonObject): Single<ProfileUpdateResponse> {
        return apiClient.getUpdateVolunteerData(phoneNumber).subscribeAndObserveWithDelaySubscription()
    }

    override fun getCallDetails(details: JsonObject): Single<HomeRepo> =
        apiClient.getCallDetails(details).subscribeAndObserveWithDelaySubscription()

    override fun getGrievanceTrackingDetails(details: JsonObject): Single<GrievanceRespData> =
        apiClient.getGrievanceTrackingDetails(details).subscribeAndObserveWithDelaySubscription()

    override fun saveSrCitizenFeedback(srCitizenFeedback: JsonObject): Single<BaseRepo> =
        apiClient.saveSrCitizenFeedback(srCitizenFeedback)
            .subscribeAndObserveWithDelaySubscription()

    override fun registerSeniorCitizen(srDetails: JsonObject): Single<BaseRepo> =
        apiClient.registerSeniorCitizen(srDetails).subscribeAndObserveWithDelaySubscription()

    override fun getSeniorCitizenDetails(srDetails: JsonObject): Single<BaseRepo> =
        apiClient.getSeniorCitizenDetails(srDetails).subscribeAndObserveWithDelaySubscription()

    override fun updateGrievanceDetails(grDetails: JsonObject): Single<BaseRepo> =
        apiClient.updateGrievanceDetails(grDetails).subscribeAndObserve()
}