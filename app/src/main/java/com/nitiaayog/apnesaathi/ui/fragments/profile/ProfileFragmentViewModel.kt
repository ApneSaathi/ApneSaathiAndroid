package com.nitiaayog.apnesaathi.ui.fragments.profile

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.io
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ProfileFragmentViewModel private constructor(private val dataManager: DataManager) :
    BaseViewModel() {

//    companion object {
//        @Volatile
//        private var instance: ProfileFragmentViewModel? = null
//
//        @Synchronized
//        fun getInstance(dataManager: DataManager): ProfileFragmentViewModel =
//            instance ?: synchronized(this) {
//                instance
//                    ?: ProfileFragmentViewModel(
//                        dataManager
//                    )
//                        .also { instance = it }
//            }
//    }

companion object {

    @Synchronized
    fun getInstance(dataManager: DataManager): ProfileFragmentViewModel = synchronized(this) {
        ProfileFragmentViewModel(dataManager)
    }
}


    fun getDataObserver(): LiveData<NetworkRequestState> = loaderObservable

    fun getvolunteerData(mContext: Context, volunteerId: String) {
        if (checkNetworkAvailability(mContext, ApiProvider.Api_volunteer_Data)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.phoneNo, volunteerId)

            dataManager.volunteerData(params).doOnSubscribe {
                loaderObservable.value =
                    NetworkRequestState.LoadingData(ApiProvider.Api_volunteer_Data)
            }.subscribe({
                try {
                    if (it.getStatusCode() == "0") {
                        loaderObservable.value =
                            NetworkRequestState.SuccessResponse(ApiProvider.Api_volunteer_Data, it)
                        viewModelScope.launch {
                            io {
                            }
                            loaderObservable.value =
                                NetworkRequestState.SuccessResponse(
                                    ApiProvider.Api_volunteer_Data,
                                    it
                                )

                        }
                    } else loaderObservable.value =
                        NetworkRequestState.ErrorResponse(ApiProvider.Api_volunteer_Data)
                } catch (e: Exception) {
                    println(e.printStackTrace())
                }
            }, {
                loaderObservable.value =
                    NetworkRequestState.ErrorResponse(ApiProvider.Api_volunteer_Data, it)

            }).autoDispose(disposables)
        }


    }

    fun getUpdatedvolunteerData(
        mContext: Context,
        volunteerId: String,
        fullname: String,
        address: String,
        email: String
    ) {
        if (checkNetworkAvailability(mContext, ApiProvider.Api_UPDATEPROFILE)) {
            val params1 = JsonObject()
            params1.addProperty(ApiConstants.Profileidvolunteer, volunteerId)
            params1.addProperty(ApiConstants.ProfileFullName, fullname)
            params1.addProperty(ApiConstants.ProfileAddress, address)
            params1.addProperty(ApiConstants.ProfileEmail, email)

            dataManager.updatevolunteerData(params1).doOnSubscribe {
                loaderObservable.value =
                    NetworkRequestState.LoadingData(ApiProvider.Api_UPDATEPROFILE)
            }.subscribe({
                try {
                    if (it.statusCode == "0") {
                        loaderObservable.value =
                            NetworkRequestState.SuccessResponse(ApiProvider.Api_UPDATEPROFILE, it)
                        viewModelScope.launch {
                            io {
                            }
                            loaderObservable.value =
                                NetworkRequestState.SuccessResponse(ApiProvider.Api_UPDATEPROFILE, it)

                        }
                    } else loaderObservable.value =
                        NetworkRequestState.ErrorResponse(ApiProvider.Api_UPDATEPROFILE)
                } catch (e: Exception) {
                    println(e.printStackTrace())
                }
            }, {
                loaderObservable.value =
                    NetworkRequestState.ErrorResponse(ApiProvider.Api_UPDATEPROFILE, it)

            }).autoDispose(disposables)
        }


    }
}