package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.home

import android.content.Context
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import com.nitiaayog.apnesaathi.utility.REQUEST_DATA_COUNT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {
        @Volatile
        private var instance: HomeViewModel? = null

        fun getInstance(dataManager: DataManager): HomeViewModel = instance ?: synchronized(this) {
            HomeViewModel(dataManager)
        }
    }

    override fun onCleared() {
        instance?.let { instance = null }
        super.onCleared()
    }

    @UiThread
    private fun updateNetworkState(state: NetworkRequestState) {
        loaderObservable.postValue(state)
    }

    fun getNetworkStream(): LiveData<NetworkRequestState> {
        return loaderObservable
    }

    @WorkerThread
    fun getVolunteers(context: Context) =
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (checkNetworkAvailability(context, ApiProvider.ApiGetVolunteers)) {
                val params = JsonObject()
                params.addProperty(ApiConstants.UserId, dataManager.getUserId().toInt())
                params.addProperty(ApiConstants.LastId, 0)
                params.addProperty(ApiConstants.RequestedData, REQUEST_DATA_COUNT)
                dataManager.getVolunteers(params).doOnSubscribe {
                    updateNetworkState(NetworkRequestState.LoadingData(ApiProvider.ApiGetVolunteers))
                }.subscribe({

                }, {
                    updateNetworkState(
                        NetworkRequestState.ErrorResponse(ApiProvider.ApiGetVolunteers, it)
                    )
                })
            }
        }
}