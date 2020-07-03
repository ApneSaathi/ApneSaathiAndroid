package com.nitiaayog.apnesaathi.networkadapter.api.apirequest

import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.BaseRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.HomeRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.LoginRepo
import io.reactivex.Single

interface ApiRequest {

    // Get Login User Data
    fun loginUser(phoneNumber: JsonObject): Single<LoginRepo>

    // Get Calls list for Home
    fun getCallDetails(details: JsonObject): Single<HomeRepo>

    // Save Sr citizen feedback over a call
    fun saveSrCitizenFeedback(srCitizenFeedback: JsonObject): Single<BaseRepo>

    // Register new Sr. Citizen
    fun registerSeniorCitizen(srDetails: JsonObject): Single<BaseRepo>

    // Get senior citizen details
    fun getSeniorCitizenDetails(srDetails: JsonObject): Single<BaseRepo>

}