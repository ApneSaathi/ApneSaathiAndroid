package com.nitiaayog.apnesaathi.networkadapter.api.apirequest

import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.BaseRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.HomeRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.loginresponse.Login_Response
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.volunteerdata.VolunteerDataResponse
import io.reactivex.Single

interface ApiRequest {

    // Get Login User Data
    fun loginUser(phoneNumber: JsonObject): Single<Login_Response>

    //    Get Volunteer Data
    fun volunteerData(phoneNumber: JsonObject): Single<VolunteerDataResponse>


    // Get Calls list for Home
    fun getCallDetails(details: JsonObject): Single<HomeRepo>

    // Save Sr citizen feedback over a call
    fun saveSrCitizenFeedback(srCitizenFeedback: JsonObject): Single<BaseRepo>

    // Register new Sr. Citizen
    fun registerSeniorCitizen(srDetails: JsonObject): Single<BaseRepo>

    // Get senior citizen details
    fun getSeniorCitizenDetails(srDetails: JsonObject): Single<BaseRepo>

}