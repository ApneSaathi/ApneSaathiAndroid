package com.nitiaayog.apnesaathi.ui.fragments.details

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.DateItem
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class SeniorCitizenDetailsViewModel private constructor(private val dataManager: DataManager) :
    BaseViewModel() {
    private val dataList: MutableList<DateItem> = mutableListOf()
    fun prepareData(): MutableList<DateItem> {
        dataList.clear()
        dataList.add(
            DateItem(
                "01", "Jan", "Attended"
            )
        )
        dataList.add(
            DateItem(
                "02", "Jan", "Attended"
            )
        )
        dataList.add(
            DateItem(
                "08", "Jan", "Attended"
            )
        )
        dataList.add(
            DateItem(
                "09", "Feb", "Unattended"
            )
        )
        return dataList
    }

    fun getDataObserver(): LiveData<NetworkRequestState> = loaderObservable
    fun getSeniorCitizenDetails(context: Context) {
        if (checkNetworkAvailability(context, ApiProvider.ApiSeniorCitizenDetails)) {
            val params = JsonObject()
            params.addProperty("callid", 11)
            dataManager.getSeniorCitizenDetails(params).doOnSubscribe {
                loaderObservable.value =
                    NetworkRequestState.LoadingData(ApiProvider.ApiSeniorCitizenDetails)
            }.doOnSuccess {
                loaderObservable.value =
                    NetworkRequestState.SuccessResponse(ApiProvider.ApiSeniorCitizenDetails, it)
            }.doOnError {
                loaderObservable.value =
                    NetworkRequestState.ErrorResponse(ApiProvider.ApiSeniorCitizenDetails, it)
            }.subscribe().autoDispose(disposables)
        }
    }

    fun getDataList(): MutableList<DateItem> {
        return dataList
    }

    companion object {
        @Volatile
        private var instance: SeniorCitizenDetailsViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): SeniorCitizenDetailsViewModel =
            instance
                ?: synchronized(this) {
                    instance
                        ?: SeniorCitizenDetailsViewModel(
                            dataManager
                        )
                            .also { instance = it }
                }
    }
}