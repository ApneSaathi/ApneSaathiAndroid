package com.nitiaayog.apnesaathi.networkadapter.api.apirequest

import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.BaseRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.HomeRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.LoginRepo
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST(ApiProvider.ApiLoginUser)
    fun loginUser(@Body phoneNumber: JsonObject): Single<LoginRepo>

    @POST(ApiProvider.ApiLoadDashboard)
    fun getCallDetails(@Body details: JsonObject): Single<HomeRepo>

    @POST(ApiProvider.ApiSaveSeniorCitizenFeedbackForm)
    fun saveSrCitizenFeedback(@Body srCitizenFeedback: JsonObject): Single<BaseRepo>

    @POST(ApiProvider.ApiRegisterSeniorCitizen)
    fun registerSeniorCitizen(@Body srDetails: JsonObject): Single<BaseRepo>
}