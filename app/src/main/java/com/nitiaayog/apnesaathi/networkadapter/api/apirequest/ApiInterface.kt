package com.nitiaayog.apnesaathi.networkadapter.api.apirequest

import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.BaseRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.HomeRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.VolunteerRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.grievancedata.GrievanceRespData
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.loginresponse.LoginResponse
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.profileupdate.ProfileUpdateResponse
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.volunteerdata.VolunteerDataResponse
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiInterface {

    @POST(ApiProvider.ApiLoginUser)
    fun loginUser(@Body phoneNumber: JsonObject): Single<LoginResponse>

    @POST(ApiProvider.ApiVerifyPassword)
    fun verifyPassword(@Body params: JsonObject): Single<BaseRepo>

    @POST(ApiProvider.ApiGetVolunteers)
    fun getVolunteers(@Body params: JsonObject): Single<VolunteerRepo>

    @POST(ApiProvider.ApiGetVolunteerData)
    fun getVolunteerData(@Body phoneNumber: JsonObject): Single<VolunteerDataResponse>

    @PUT(ApiProvider.ApiUpdateProfile)
    fun getUpdateVolunteerData(@Body phoneNumber: JsonObject): Single<ProfileUpdateResponse>

    @POST(ApiProvider.ApiLoadDashboard)
    fun getCallDetails(@Body details: JsonObject): Single<HomeRepo>

    @POST(ApiProvider.ApiGrievanceTracking)
    fun getGrievanceTrackingDetails(@Body details: JsonObject): Single<GrievanceRespData>

    @POST(ApiProvider.ApiSaveSeniorCitizenFeedbackForm)
    fun saveSrCitizenFeedback(@Body srCitizenFeedback: JsonObject): Single<BaseRepo>

    @POST(ApiProvider.ApiRegisterSeniorCitizen)
    fun registerSeniorCitizen(@Body srDetails: JsonObject): Single<BaseRepo>

    @POST(ApiProvider.ApiSeniorCitizenDetails)
    fun getSeniorCitizenDetails(@Body srDetails: JsonObject): Single<BaseRepo>

    @PUT(ApiProvider.ApiUpdateGrievanceDetails)
    fun updateGrievanceDetails(@Body srDetails: JsonObject): Single<BaseRepo>
}