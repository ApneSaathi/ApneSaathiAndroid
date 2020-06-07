package com.nitiaayog.apnesaathi.networkadapter.api.apirequest

import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.AssessmentRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.BaseRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.LoginRepo
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {

    @GET(ApiProvider.ApiLoginUser)
    fun loginUser(phoneNumber: String): Single<LoginRepo>

    @POST(ApiProvider.ApiGetAssessmentQuestions)
    fun getAssessmentQuestions(): Single<AssessmentRepo>

    @POST(ApiProvider.ApiSyncAppDataWithServer)
    fun syncAppDataWithServer(): Single<BaseRepo>
}