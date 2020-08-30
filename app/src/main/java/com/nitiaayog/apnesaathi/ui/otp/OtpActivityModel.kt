package com.nitiaayog.apnesaathi.ui.otp

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
import com.nitiaayog.apnesaathi.ui.login.LoginViewModel
import kotlinx.coroutines.launch


/**
 * [OtpActivityModel] for calling the login API
 * [dataManager] is used to store all the data that is required in the app.
 */
class OtpActivityModel private constructor(dataManager: DataManager) : BaseViewModel() {

    val dataManager: DataManager = dataManager

    companion object {

        @Synchronized
        fun getInstance(dataManager: DataManager): OtpActivityModel = synchronized(this) {
            OtpActivityModel(dataManager)
        }
    }

    fun getDataObserver(): LiveData<NetworkRequestState> = loaderObservable

    /**
     * Method getting the emergency contact from API.
     * [userId] this is successfully logged userID and store in shared preference
     * [phoneNumber] this is successfully logged phoneNumber and store in shared preference
     * [role] this is successfully logged user role and store in shared preference
     */
    fun setData(userId:String, phoneNumber:String, role: String) {
        dataManager.setUserId(userId)
        dataManager.setRole(role)
        dataManager.setPhoneNumber(phoneNumber)
    }

    /**
     * Method getting the emergency contact from API.
     * [mContext] is the current activity context
     * [phone] is the phone number for user using the login.
     */
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
                            /*dataManager.setUserId(
                                when {
                                    it.getId() == null -> "1001"
                                    it.getId()!!.isEmpty() -> "1001"
                                    else -> it.getId()!!
                                }
                            )*/
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