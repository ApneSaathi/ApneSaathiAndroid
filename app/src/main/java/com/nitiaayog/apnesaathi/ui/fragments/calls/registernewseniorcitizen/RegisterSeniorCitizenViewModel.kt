package com.nitiaayog.apnesaathi.ui.fragments.calls.registernewseniorcitizen

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class RegisterSeniorCitizenViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {
        @Volatile
        private var instance: RegisterSeniorCitizenViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): RegisterSeniorCitizenViewModel =
            instance ?: synchronized(this) {
                instance ?: RegisterSeniorCitizenViewModel(dataManager).also { instance = it }
            }
    }

    fun getDataObserver(): LiveData<NetworkRequestState> = loaderObservable

    fun registerNewSeniorCitizen(
        context: Context, name: String, age: String, gender: String, contactNumber: String,
        district: String, state: String, address: String
    ) {
        if (checkNetworkAvailability(context)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.FirstName, name)
            params.addProperty(ApiConstants.Age, age.toInt())
            params.addProperty(ApiConstants.Gender, gender)
            params.addProperty(ApiConstants.PhoneNumber, contactNumber)
            params.addProperty(ApiConstants.District, district)
            params.addProperty(ApiConstants.State, state)
            params.addProperty(ApiConstants.Address, address)
            //params.addProperty(ApiConstants.VolunteerId, dataManager.getUserId())
            dataManager.registerSeniorCitizen(params).doOnSubscribe {
                loaderObservable.value = NetworkRequestState.LoadingData
            }.doOnSuccess {
                loaderObservable.value = NetworkRequestState.SuccessResponse(it)
            }.doOnError {
                loaderObservable.value = NetworkRequestState.ErrorResponse(it)
            }.subscribe().autoDispose(disposables)
        }
    }
}