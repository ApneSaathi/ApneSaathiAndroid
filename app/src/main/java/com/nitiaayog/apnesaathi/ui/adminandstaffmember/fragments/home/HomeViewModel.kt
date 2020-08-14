package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.AdminCallDetails
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.CallDetails
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import com.nitiaayog.apnesaathi.utility.ROLE_STAFF_MEMBER
import com.nitiaayog.apnesaathi.utility.ROLE_VOLUNTEER
import kotlinx.coroutines.launch

class HomeViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {

        private val TAG: String = "TAG -- ${HomeViewModel::class.java.simpleName} -->"

        @Volatile
        private var instance: HomeViewModel? = null

        @Synchronized
        fun getInstance(context: Context, dataManager: DataManager): HomeViewModel {
            return instance ?: synchronized(this) {
                HomeViewModel(dataManager).also { instance = it }
            }
        }
    }

    private val pendingCalls: LiveData<MutableList<CallData>> = dataManager.getPendingCallsList()
    private val followUpCalls: LiveData<MutableList<CallData>> = dataManager.getFollowupCallsList()
    private val completedCalls: LiveData<MutableList<CallData>> =
        dataManager.getCompletedCallsList()
    private val invalidCalls: LiveData<MutableList<CallData>> = dataManager.getInvalidCallsList()

    override fun onCleared() {
        instance?.let { instance = null }
        super.onCleared()
    }

    private fun prepareGrievances(grievance: List<CallData>): List<SrCitizenGrievance> {
        val callData = grievance.filter {
            (it.medicalGrievance != null && it.medicalGrievance!!.size > 0)
        }
        val grievances: MutableList<SrCitizenGrievance> = mutableListOf()
        callData.forEach { data ->
            data.medicalGrievance?.forEach {
                it.srCitizenName = data.srCitizenName
                it.gender = data.gender
            }
            grievances.addAll(data.medicalGrievance!!)
        }
        return grievances
    }

    private fun manageCallsData(data: CallDetails) {
        dataManager.clearPreviousData()
        dataManager.insertCallData(data.callsList)

        val grievances: List<SrCitizenGrievance> = prepareGrievances(data.callsList)
        dataManager.insertGrievances(grievances)
    }

    private fun manageAdminData(data: AdminCallDetails) {
        dataManager.clearPreviousData()
        dataManager.insertCallData(data.adminCallsList)

        dataManager.insertDistrictData(data.adminDistrictList)

        val grievances: List<SrCitizenGrievance> = prepareGrievances(data.adminCallsList)
        dataManager.insertGrievances(grievances)
    }

    fun getNetworkStream(): LiveData<NetworkRequestState> {
        return loaderObservable
    }

    fun getPendingCalls(): LiveData<MutableList<CallData>> {
        return pendingCalls
    }

    fun getFollowupCalls(): LiveData<MutableList<CallData>> {
        return followUpCalls
    }

    fun getCompletedCalls(): LiveData<MutableList<CallData>> {
        return completedCalls
    }

    fun getInvalidCalls(): LiveData<MutableList<CallData>> {
        return invalidCalls
    }

    suspend fun getCallDetails(context: Context) {
        if (checkNetworkAvailability(context, ApiProvider.ApiLoadDashboard)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.Id, dataManager.getUserId().toInt())
            params.addProperty(ApiConstants.FilterBy, dataManager.getRole())
            dataManager.getCallDetails(params).doOnSubscribe {
                updateNetworkState(NetworkRequestState.LoadingData(ApiProvider.ApiLoadDashboard))
            }.subscribe({
                try {
                    if (it.status == "0") {
                        viewModelScope.launch {
                            io {

                                if (dataManager.getRole() == ROLE_VOLUNTEER) {
                                    val data = it.getData()
                                    manageCallsData(data)
                                } else {
                                    val data = it.getAdminData()
                                    manageAdminData(data)
                                }
                            }
                            updateNetworkState(
                                NetworkRequestState.SuccessResponse(
                                    ApiProvider.ApiLoadDashboard, it
                                )
                            )
                        }
                    } else updateNetworkState(NetworkRequestState.ErrorResponse(ApiProvider.ApiLoadDashboard))
                } catch (e: Exception) {
                    println("$TAG ${e.message}")
                }
            }, {
                updateNetworkState(
                    NetworkRequestState.ErrorResponse(ApiProvider.ApiLoadDashboard, it)
                )
            }).autoDispose(disposables)
        }
    }

    suspend fun getGrievanceTrackingList(context: Context) {
        if (checkNetworkAvailability(context, ApiProvider.ApiGrievanceTracking)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.Id, dataManager.getUserId().toInt())
            if (dataManager.getRole() == ROLE_VOLUNTEER || dataManager.getRole() == ROLE_STAFF_MEMBER) {
                params.addProperty(ApiConstants.FilterBy, dataManager.getRole())
            } else {
                var id = 4 //todo replace with assigned district
                if (dataManager.getSelectedDistrictId().isNotEmpty()) {
                    id = dataManager.getSelectedDistrictId().toInt()
                }
                params.addProperty(ApiConstants.DistrictId, id)
            }
            //params.addProperty(ApiConstants.Role, dataManager.getRole())
            //params.addProperty(ApiConstants.LastId, 0)// id - last id we got in list
            //params.addProperty(ApiConstants.RequestedData, 0)// Count - No of data we need in oone page
            dataManager.getGrievanceTrackingDetails(params).doOnSubscribe {
                updateNetworkState(NetworkRequestState.LoadingData(ApiProvider.ApiGrievanceTracking))
            }.subscribe({
                try {
                    if (it.getStatus() == "0") {
                        viewModelScope.launch {
                            io {
                                dataManager.clearPreviousTrackingData()
                                dataManager.insertGrievanceTrackingList(it.getTrackingList())
                            }
                            loaderObservable.value = NetworkRequestState.SuccessResponse(
                                ApiProvider.ApiGrievanceTracking, it
                            )
                        }
                    } else updateNetworkState(NetworkRequestState.ErrorResponse(ApiProvider.ApiGrievanceTracking,responseCode = it.getStatus()))
                } catch (e: Exception) {
                    println("$TAG ${e.message}")
                }
            }, {
                updateNetworkState(
                    NetworkRequestState.ErrorResponse(
                        ApiProvider.ApiGrievanceTracking,
                        it
                    )
                )
            }).autoDispose(disposables)
        }
    }
}