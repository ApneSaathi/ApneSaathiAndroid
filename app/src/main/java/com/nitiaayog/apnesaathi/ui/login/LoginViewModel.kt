package com.nitiaayog.apnesaathi.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel private constructor(dataManager: DataManager) : BaseViewModel() {

    val dataManager: DataManager = dataManager

    companion object {

        @Synchronized
        fun getInstance(dataManager: DataManager): LoginViewModel = synchronized(this) {
            LoginViewModel(dataManager)
        }
    }

    fun getDataObserver(): LiveData<NetworkRequestState> = loaderObservable

    fun callLogin(mContext: Context, phone: String) {

        if (checkNetworkAvailability(mContext, ApiProvider.ApiLoginUser)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.phoneNo, phone)


            dataManager.loginUser(params).doOnSubscribe {
                loaderObservable.value = NetworkRequestState.LoadingData(ApiProvider.ApiLoginUser)
            }.subscribe({
                try {
                    if (it.getStatusCode() == "0") {
                        viewModelScope.launch {
                            loaderObservable.value =
                                NetworkRequestState.SuccessResponse(ApiProvider.ApiLoginUser, it)
                            dataManager.setUserId(
                                when {
                                    it.getVolunteerId() == null -> "1001"
                                    it.getVolunteerId()!!.isEmpty() -> "1001"
                                    else -> it.getVolunteerId()!!
                                }
                            )

                        }

                    } else loaderObservable.value =
                        NetworkRequestState.ErrorResponse(ApiProvider.ApiLoginUser)
                } catch (e: Exception) {
                    println(e.printStackTrace())
                }
            }, {
                loaderObservable.value =
                    NetworkRequestState.ErrorResponse(ApiProvider.ApiLoginUser, it)

            }).autoDispose(disposables)
        }

    }
}