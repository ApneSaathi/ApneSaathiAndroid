package com.nitiaayog.apnesaathi.networkadapter.api.apirequest

import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.AssessmentRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.BaseRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.LoginRepo
import io.reactivex.Single

interface ApiRequest {

    // Get Login User Data
    fun loginUser(phoneNumber: String): Single<LoginRepo>

    // Get Assessment Questions List
    fun getAssessmentQuestions(): Single<AssessmentRepo>

    // Sync App Data With Server
    fun syncAppDataWithServer(): Single<BaseRepo>
}