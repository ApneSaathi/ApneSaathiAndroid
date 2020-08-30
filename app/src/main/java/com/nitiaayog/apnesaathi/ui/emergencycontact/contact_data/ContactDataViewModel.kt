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

/**
 * [ContactDataViewModel] for calling the emergency contact API
 * [dataManager] is used to store all the data that is required in the app.
 */
class ContactDataViewModel private constructor(val dataManager: DataManager) : BaseViewModel() {

    companion object {

        @Synchronized
        fun getInstance(dataManager: DataManager): ContactDataViewModel = synchronized(this) {
            ContactDataViewModel(dataManager)
        }
    }

    fun getDataObserver(): LiveData<NetworkRequestState> = loaderObservable

    /**
     * Method getting the emergency contact from API.
     * [mContext] is the current activity context
     * [districtName] is the selected district name from drop-dawn.
     */
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