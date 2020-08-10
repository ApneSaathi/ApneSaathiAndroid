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
    private val dataManager: DataManager, private val phoneNo: String, private val role: String
) : BaseViewModel() {

    companion object {

        @Volatile
        private var instanceFactory: PasswordViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager, phoneNo: String, role: String):
                PasswordViewModel {
            if (instanceFactory == null) synchronized(this) {
                PasswordViewModel(dataManager, phoneNo, role).also { instanceFactory = it }
            }
            return instanceFactory!!
        }
    }

    fun getNetworkStateObservable(): LiveData<NetworkRequestState> = loaderObservable

    @WorkerThread
    fun validatePassword(context: Context, password: String) {
        if (checkNetworkAvailability(context, ApiProvider.ApiVerifyPassword)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.AdminId, dataManager.getUserId())
            params.addProperty(ApiConstants.Password, password)
            viewModelScope.launch(Dispatchers.IO) {
                dataManager.verifyPassword(params).doOnSubscribe {
                    updateNetworkState(NetworkRequestState.LoadingData(ApiProvider.ApiVerifyPassword))
                }.subscribe({
                    if (it.status == "0") {
                        dataManager.setPhoneNumber(phoneNo)
                        dataManager.setRole("3")
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