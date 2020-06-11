package com.nitiaayog.apnesaathi.networkadapter.api.apimanager

import com.nitiaayog.apnesaathi.base.extensions.rx.subscribeAndObserve
import com.nitiaayog.apnesaathi.base.extensions.rx.subscribeAndObserveWithDelaySubscription
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.ApiInterface
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.ApiRequest
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.AssessmentRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.BaseRepo
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.LoginRepo
import io.reactivex.Single

class ApiManager private constructor(private val apiClient: ApiInterface) : ApiRequest {

    companion object {
        @Volatile
        private var instance: ApiManager? = null

        @Synchronized
        fun getApiRequest(apiClient: ApiInterface): ApiManager =
            instance ?: synchronized(this) {
                instance ?: ApiManager(apiClient).also { instance = it }
            }
    }

    override fun loginUser(phoneNumber: String): Single<LoginRepo> =
        apiClient.loginUser(phoneNumber).subscribeAndObserveWithDelaySubscription()

    override fun getAssessmentQuestions(): Single<AssessmentRepo> =
        apiClient.getAssessmentQuestions().subscribeAndObserveWithDelaySubscription()

    override fun syncAppDataWithServer(): Single<BaseRepo> =
        apiClient.syncAppDataWithServer().subscribeAndObserve()
}