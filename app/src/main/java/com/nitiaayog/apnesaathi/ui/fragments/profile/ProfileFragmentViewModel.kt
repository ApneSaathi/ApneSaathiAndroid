package com.nitiaayog.apnesaathi.ui.fragments.profile

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
import com.nitiaayog.apnesaathi.utility.ROLE_VOLUNTEER
import kotlinx.coroutines.launch

class ProfileFragmentViewModel private constructor(private val dataManager: DataManager) :
    BaseViewModel() {

    companion object {

        @Synchronized
        fun getInstance(dataManager: DataManager): ProfileFragmentViewModel = synchronized(this) {
            ProfileFragmentViewModel(dataManager)
        }
    }

    suspend fun getCountOfDataRemainingToSync(): Int {
        return dataManager.getCount()
    }

    fun clearData() {
        dataManager.clearData()
    }

    fun getDataObserver(): LiveData<NetworkRequestState> = loaderObservable

    fun getvolunteerData(mContext: Context, volunteerId: String) {
        if (checkNetworkAvailability(mContext, ApiProvider.ApiGetVolunteerData)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.phoneNo, volunteerId)

            dataManager.volunteerData(params).doOnSubscribe {
                loaderObservable.value =
                    NetworkRequestState.LoadingData(ApiProvider.ApiGetVolunteerData)
            }.subscribe({
                try {
                    if (it.statusCode == "0") {
                        loaderObservable.value =
                            NetworkRequestState.SuccessResponse(ApiProvider.ApiGetVolunteerData, it)
                        viewModelScope.launch {
                            io {
                            }
                            loaderObservable.value =
                                NetworkRequestState.SuccessResponse(
                                    ApiProvider.ApiGetVolunteerData,
                                    it
                                )

                        }
                    } else loaderObservable.value =
                        NetworkRequestState.ErrorResponse(ApiProvider.ApiGetVolunteerData)
                } catch (e: Exception) {
                    println(e.printStackTrace())
                }
            }, {
                loaderObservable.value =
                    NetworkRequestState.ErrorResponse(ApiProvider.ApiGetVolunteerData, it)

            }).autoDispose(disposables)
        }


    }

    fun getUpdatedvolunteerData(
        mContext: Context,
        volunteerId: String,
        firstname: String,
        lastname: String,
        address: String,
        email: String
    ) {
        if (checkNetworkAvailability(mContext, ApiProvider.ApiUpdateProfile)) {
            val params1 = JsonObject()
            if (dataManager.getRole() == ROLE_VOLUNTEER)
                params1.addProperty(ApiConstants.Profileidvolunteer, volunteerId)
            else
                params1.addProperty(ApiConstants.AdminId, volunteerId)
            params1.addProperty(ApiConstants.ProfileFirstName, firstname)
            params1.addProperty(ApiConstants.ProfileLstname, lastname)
            params1.addProperty(ApiConstants.ProfileAddress, address)
            params1.addProperty(ApiConstants.ProfileEmail, email)

            dataManager.updatevolunteerData(params1).doOnSubscribe {
                loaderObservable.value =
                    NetworkRequestState.LoadingData(ApiProvider.ApiUpdateProfile)
            }.subscribe({
                try {
                    if (it.statusCode == "0") {
                        loaderObservable.value =
                            NetworkRequestState.SuccessResponse(ApiProvider.ApiUpdateProfile, it)
                        viewModelScope.launch {
                            io {
                            }
                            loaderObservable.value =
                                NetworkRequestState.SuccessResponse(
                                    ApiProvider.ApiUpdateProfile,
                                    it
                                )

                        }
                    } else loaderObservable.value =
                        NetworkRequestState.ErrorResponse(ApiProvider.ApiUpdateProfile)
                } catch (e: Exception) {
                    println(e.printStackTrace())
                }
            }, {
                loaderObservable.value =
                    NetworkRequestState.ErrorResponse(ApiProvider.ApiUpdateProfile, it)

            }).autoDispose(disposables)
        }
    }
}