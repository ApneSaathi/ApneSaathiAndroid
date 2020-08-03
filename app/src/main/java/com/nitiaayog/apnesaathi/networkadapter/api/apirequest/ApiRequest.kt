package com.nitiaayog.apnesaathi.networkadapter.api.apirequest

import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.BaseRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.HomeRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.grievancedata.GrievanceRespData
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.loginresponse.LoginResponse
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.profileupdate.ProfileUpdateResponse
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.volunteerdata.VolunteerDataResponse
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

interface ApiRequest {

    // Get Login User Data
    fun loginUser(phoneNumber: JsonObject): Single<LoginResponse>

    suspend fun verifyPassword(params: JsonObject):Single<BaseRepo>

    //    Get Volunteer Data
    fun volunteerData(phoneNumber: JsonObject): Single<VolunteerDataResponse>

    //    Get Update Volunteer Data
    fun updatevolunteerData(phoneNumber: JsonObject): Single<ProfileUpdateResponse>

    // Get Calls list for Home
    fun getCallDetails(details: JsonObject): Single<HomeRepo>

    // Get grievance tracking details
    fun getGrievanceTrackingDetails(details: JsonObject): Single<GrievanceRespData>

    // Save Sr citizen feedback over a call
    fun saveSrCitizenFeedback(srCitizenFeedback: JsonObject): Single<BaseRepo>

    // Register new Sr. Citizen
    fun registerSeniorCitizen(srDetails: JsonObject): Single<BaseRepo>

    // Get senior citizen details
    fun getSeniorCitizenDetails(srDetails: JsonObject): Single<BaseRepo>

    fun updateGrievanceDetails(grDetails: JsonObject): Single<BaseRepo>
}