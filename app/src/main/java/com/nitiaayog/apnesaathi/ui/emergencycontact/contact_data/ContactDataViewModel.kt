package com.nitiaayog.apnesaathi.ui.emergencycontact.contact_data

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class ContactDataViewModel private constructor(val dataManager: DataManager) : BaseViewModel() {

    companion object {

        @Synchronized
        fun getInstance(dataManager: DataManager): ContactDataViewModel = synchronized(this) {
            ContactDataViewModel(dataManager)
        }
    }

    fun getDataObserver(): LiveData<NetworkRequestState> = loaderObservable
    fun callEmergencyDataApi(mContext: Context, districtName: String) {

        if (checkNetworkAvailability(mContext, ApiProvider.ApiEmergencyContact)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.DistrictName, districtName)

            dataManager.getEmergencyContact(params).doOnSubscribe {
                loaderObservable.value =
                    NetworkRequestState.LoadingData(ApiProvider.ApiEmergencyContact)
            }.subscribe({
                try {
                    if (it.statusCode == "0") {
                        updateNetworkState(
                            NetworkRequestState.SuccessResponse(
                                ApiProvider.ApiEmergencyContact,
                                it
                            )
                        )
                    } else updateNetworkState(NetworkRequestState.Error(ApiProvider.ApiEmergencyContact))
                } catch (e: Exception) {
                    println(e.printStackTrace())
                }
            }, {
                updateNetworkState(
                    NetworkRequestState.ErrorResponse(ApiProvider.ApiEmergencyContact, it)
                )
            }).autoDispose(disposables)
        }

    }
}