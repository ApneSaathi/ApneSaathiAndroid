package com.nitiaayog.apnesaathi.networkadapter.api.apirequest

import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.BaseRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.HomeRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.VolunteerRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.grievancedata.GrievanceRespData
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.loginresponse.LoginResponse
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.profileupdate.ProfileUpdateResponse
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.volunteerdata.VolunteerDataResponse
import io.reactivex.Single

interface ApiRequest {

    // Get Login User Data
    fun loginUser(phoneNumber: JsonObject): Single<LoginResponse>

    suspend fun verifyPassword(params: JsonObject): Single<BaseRepo>

    // will provide list of volunteers
    @WorkerThread
    suspend fun getVolunteers(params: JsonObject): Single<VolunteerRepo>

    // Get Volunteer Data
    fun volunteerData(phoneNumber: JsonObject): Single<VolunteerDataResponse>

    // Get Update Volunteer Data
    fun updatevolunteerData(phoneNumber: JsonObject): Single<ProfileUpdateResponse>

    // Get Calls list for Home
    fun getCallDetails(details: JsonObject): Single<HomeRepo>

    // Get grievance tracking details
    @WorkerThread
    fun getGrievanceTrackingDetails(details: JsonObject): Single<GrievanceRespData>

    // Save Sr citizen feedback over a call
    fun saveSrCitizenFeedback(srCitizenFeedback: JsonObject): Single<BaseRepo>

    // Register new Sr. Citizen
    fun registerSeniorCitizen(srDetails: JsonObject): Single<BaseRepo>

    // Get senior citizen details
    fun getSeniorCitizenDetails(srDetails: JsonObject): Single<BaseRepo>

    fun updateGrievanceDetails(grDetails: JsonObject): Single<BaseRepo>

    @WorkerThread
    fun updateVolunteerRatings(params: JsonObject): Single<BaseRepo>
}