package com.nitiaayog.apnesaathi.ui.emergency_contact.hospital

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.DistrictDetails
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ContactDataViewModel private constructor(val dataManager: DataManager) : BaseViewModel() {

    private val districtList: LiveData<MutableList<DistrictDetails>> = dataManager.getDistrictList()

    companion object {

        @Synchronized
        fun getInstance(dataManager: DataManager): ContactDataViewModel = synchronized(this) {
            ContactDataViewModel(dataManager)
        }
    }

    fun getDistrictList(): LiveData<MutableList<DistrictDetails>> = districtList
    fun getDataObserver(): LiveData<NetworkRequestState> = loaderObservable
    fun callEmergencyDataApi(mContext: Context, districtID: String) {

        if (checkNetworkAvailability(mContext, ApiProvider.ApiEmergencyContact)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.districtIdForEmergency, districtID)
            dataManager.getEmergencyContact(params).doOnSubscribe {
                loaderObservable.value = NetworkRequestState.LoadingData(ApiProvider.ApiEmergencyContact)
            }.subscribe({
                try {
                    if (it.statusCode== "0") {
                        viewModelScope.launch {
                            loaderObservable.value =
                                NetworkRequestState.SuccessResponse(ApiProvider.ApiEmergencyContact, it)

                        }

                    } else loaderObservable.value =
                        NetworkRequestState.ErrorResponse(ApiProvider.ApiEmergencyContact)
                } catch (e: Exception) {
                    println(e.printStackTrace())
                }
            }, {
                loaderObservable.value =
                    NetworkRequestState.ErrorResponse(ApiProvider.ApiEmergencyContact, it)

            }).autoDispose(disposables)
        }

    }
}