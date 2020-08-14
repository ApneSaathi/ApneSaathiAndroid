package com.nitiaayog.apnesaathi.ui.fragments.calls.registernewseniorcitizen

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class RegisterSeniorCitizenViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {

        @Synchronized
        fun getInstance(dataManager: DataManager): RegisterSeniorCitizenViewModel =
            synchronized(this) { RegisterSeniorCitizenViewModel(dataManager) }
    }

    private var name: String = ""
    private var age: String = ""
    private var gender: String = ""
    private var state: String = ""
    private var district: String = ""
    private var contactNumber: String = ""
    private var address: String = ""

    fun getDataObserver(): LiveData<NetworkRequestState> = loaderObservable

    fun setName(name: String) {
        this.name = name
    }

    fun setAge(age: String) {
        this.age = age
    }

    fun setGender(gender: String) {
        this.gender = gender
    }

    fun getGender(): String = gender

    fun setContactNumber(contactNumber: String) {
        this.contactNumber = contactNumber
    }

    fun setState(state: String) {
        this.state = state
    }

    fun getState(): String = state

    fun setDistrict(district: String) {
        this.district = district
    }

    fun getDistrict(): String = district

    fun setAddress(address: String) {
        this.address = address
    }

    fun registerNewSeniorCitizen(context: Context) {
        if (checkNetworkAvailability(context, ApiProvider.ApiRegisterSeniorCitizen)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.Id, dataManager.getUserId().toInt())
            params.addProperty(ApiConstants.SrCitizenName, name)
            params.addProperty(ApiConstants.SrCitizenAge, age.toInt())
            params.addProperty(ApiConstants.SrCitizenGender, gender)
            params.addProperty(ApiConstants.SrCitizenContactNumber, contactNumber)
            params.addProperty(ApiConstants.SrCitizenDistrict, district)
            params.addProperty(ApiConstants.SrCitizenState, state)
            params.addProperty(ApiConstants.SrCitizenBlock, address)
            params.addProperty(ApiConstants.Role, dataManager.getRole())
            params.addProperty(ApiConstants.SrCitizenCallStatusCode, 1)
            dataManager.registerSeniorCitizen(params).doOnSubscribe {
                loaderObservable.value =
                    NetworkRequestState.LoadingData(ApiProvider.ApiRegisterSeniorCitizen)
            }.subscribe({
                loaderObservable.value = if (it.status == "0")
                    NetworkRequestState.SuccessResponse(ApiProvider.ApiRegisterSeniorCitizen, it)
                else NetworkRequestState.Error(ApiProvider.ApiRegisterSeniorCitizen)
            }, {
                loaderObservable.value =
                    NetworkRequestState.ErrorResponse(ApiProvider.ApiRegisterSeniorCitizen, it)
            }).autoDispose(disposables)
        }
    }
}