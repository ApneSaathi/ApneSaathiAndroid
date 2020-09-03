package com.nitiaayog.apnesaathi.ui.adminandstaffmember.password

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PasswordViewModel(
    private val dataManager: DataManager, private val userId: String, private val phoneNo: String,
    private val role: String
) : BaseViewModel() {

    companion object {
        @Synchronized
        fun getInstance(dataManager: DataManager, userId: String, phoneNo: String, role: String):
                PasswordViewModel {
            return synchronized(this) { PasswordViewModel(dataManager, userId, phoneNo, role) }
        }
    }

    fun getNetworkStateObservable(): LiveData<NetworkRequestState> = loaderObservable

    /**
     * Validate Password with Server
     * */
    @WorkerThread
    fun validatePassword(context: Context, password: String) {
        if (checkNetworkAvailability(context, ApiProvider.ApiVerifyPassword)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.AdminId, userId)
            params.addProperty(ApiConstants.Password, password)
            viewModelScope.launch(Dispatchers.IO) {
                dataManager.verifyPassword(params).doOnSubscribe {
                    updateNetworkState(NetworkRequestState.LoadingData(ApiProvider.ApiVerifyPassword))
                }.subscribe({
                    if (it.status == "0") {
                        dataManager.setUserId(userId)
                        dataManager.setPhoneNumber(phoneNo)
                        dataManager.setRole(role)
                        updateNetworkState(
                            NetworkRequestState.SuccessResponse(ApiProvider.ApiVerifyPassword, "")
                        )
                    } else updateNetworkState(NetworkRequestState.Error(ApiProvider.ApiVerifyPassword))
                }, {
                    updateNetworkState(
                        NetworkRequestState.ErrorResponse(ApiProvider.ApiVerifyPassword, it)
                    )
                }).autoDispose(disposables)
            }
        }
    }
}