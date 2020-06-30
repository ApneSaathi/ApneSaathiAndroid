package com.nitiaayog.apnesaathi.networkadapter.api.apirequest

import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.AssessmentRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.BaseRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.LoginRepo
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiRequest {

    // Get Login User Data
    @FormUrlEncoded
    @POST(ApiProvider.ApiLoginUser+ApiProvider.GET_LOGIN_USER)
    fun loginUser(@Field("phoneNo") phoneNo:String ): Single<LoginRepo>

    // Get Assessment Questions List
    fun getAssessmentQuestions(): Single<AssessmentRepo>

    // Sync App Data With Server
    fun syncAppDataWithServer(): Single<BaseRepo>
}