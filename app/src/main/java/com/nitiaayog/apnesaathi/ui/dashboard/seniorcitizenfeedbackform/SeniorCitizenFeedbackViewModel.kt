package com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenfeedbackform

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class SeniorCitizenFeedbackViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {
        @Synchronized
        fun getInstance(dataManager: DataManager): SeniorCitizenFeedbackViewModel =
            synchronized(this) { SeniorCitizenFeedbackViewModel(dataManager) }
    }

    fun getCallDetailFromId(id: Int): CallData = dataManager.getCallDetailFromId(id)

    fun getDataObserver(): LiveData<NetworkRequestState> = loaderObservable

    fun saveSeniorCitizenFeedback(context: Context) {
        if (checkNetworkAvailability(context)) {
            val params = JsonObject()
            dataManager.saveSrCitizenFeedback(params).doOnSubscribe {
                loaderObservable.value = NetworkRequestState.LoadingData
                // Insert data in room database
            }.doOnSuccess {
                // Update "is_synced" flag as true in room database
                loaderObservable.value = NetworkRequestState.SuccessResponse(it)
            }.doOnError {
                loaderObservable.value =
                    NetworkRequestState.ErrorResponse(ApiConstants.STATUS_EXCEPTION, it)
            }.subscribe().autoDispose(disposables)
        }
    }

    fun registerNewSeniorCitizen(context: Context) {
        if (checkNetworkAvailability(context)) {
            val params = JsonObject()
            dataManager.registerSeniorCitizen(params).doOnSubscribe {
                loaderObservable.value = NetworkRequestState.LoadingData
            }.doOnSuccess { loaderObservable.value = NetworkRequestState.SuccessResponse(it) }
                .doOnError {
                    loaderObservable.value =
                        NetworkRequestState.ErrorResponse(ApiConstants.STATUS_EXCEPTION, it)
                }.subscribe().autoDispose(disposables)
        }
    }
}