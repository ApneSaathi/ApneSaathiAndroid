package com.nitiaayog.apnesaathi.ui.fragments.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.DistrictDetails
import com.nitiaayog.apnesaathi.model.GrievanceData
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import com.nitiaayog.apnesaathi.utility.ROLE_MASTER_ADMIN
import com.nitiaayog.apnesaathi.utility.ROLE_STAFF_MEMBER
import com.nitiaayog.apnesaathi.utility.ROLE_VOLUNTEER
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * View model for handling all the actions related home page
 * [dataManager] is used to store all the data that is required in the app.
 */
class HomeViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {

        @Volatile
        private var instance: HomeViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): HomeViewModel = instance ?: synchronized(this) {
            HomeViewModel(dataManager)
        }
    }

    private val TAG: String = "TAG -- ${HomeViewModel::class.java.simpleName} -->"

    private val pendingCallsList: LiveData<MutableList<CallData>> =
        dataManager.getPendingCallsList()
    private val followUpCallsList: LiveData<MutableList<CallData>> =
        dataManager.getFollowupCallsList()
    private val completedCallsList: LiveData<MutableList<CallData>> =
        dataManager.getCompletedCallsList()
    private val invalidCallsList: LiveData<MutableList<CallData>> =
        dataManager.getInvalidCallsList()
    private val callsList: LiveData<MutableList<CallData>> = dataManager.getAllCallsList()
    private val districtList: LiveData<MutableList<DistrictDetails>> = dataManager.getDistrictList()

    private val pendingGrievance: LiveData<MutableList<GrievanceData>> =
        dataManager.getPendingGrievances()
    private val inProgressGrievance: LiveData<MutableList<GrievanceData>> =
        dataManager.getInProgressGrievances()
    private val resolvedGrievance: LiveData<MutableList<GrievanceData>> =
        dataManager.getResolvedGrievances()

    private val grievancesList: LiveData<MutableList<SrCitizenGrievance>> =
        dataManager.getGrievances()

    private val grievancesTrackingList: LiveData<MutableList<GrievanceData>> =
        dataManager.getAllTrackingGrievances()

    override fun onCleared() {
        instance?.run { instance = null }
        super.onCleared()
    }

    private fun prepareGrievances(grievance: List<CallData>): List<SrCitizenGrievance> {
        val callData = grievance.filter {
            (it.medicalGrievance != null && it.medicalGrievance!!.size > 0)
        }
        val grievances: MutableList<SrCitizenGrievance> = mutableListOf()
        callData.forEach { data ->
            grievances.addAll(data.medicalGrievance!!)
        }
        return grievances
    }

    fun getDataStream(): LiveData<NetworkRequestState> = loaderObservable

    fun getCallsList(): LiveData<MutableList<CallData>> = callsList
    fun getDistrictList(): LiveData<MutableList<DistrictDetails>> = districtList

    fun getPendingCalls(): LiveData<MutableList<CallData>> = pendingCallsList
    fun getFollowupCalls(): LiveData<MutableList<CallData>> = followUpCallsList
    fun getCompletedCalls(): LiveData<MutableList<CallData>> = completedCallsList
    fun getInvalidCallsList(): LiveData<MutableList<CallData>> = invalidCallsList

    fun getPendingGrievances(): LiveData<MutableList<GrievanceData>> = pendingGrievance
    fun getInProgressGrievances(): LiveData<MutableList<GrievanceData>> = inProgressGrievance
    fun getResolvedGrievances(): LiveData<MutableList<GrievanceData>> = resolvedGrievance

    fun getGrievancesList(): LiveData<MutableList<SrCitizenGrievance>> = grievancesList

    fun getGrievancesTrackingList(): LiveData<MutableList<GrievanceData>> = grievancesTrackingList

    fun setLastSelectedUser(callId: String) {
        dataManager.setLastSelectedId(callId)
    }

    /**
     * Method for fetching the call details from the API
     */
    fun getCallDetails(context: Context) {
        if (checkNetworkAvailability(context, ApiProvider.ApiLoadDashboard)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.Id, dataManager.getUserId().toInt())
            params.addProperty(ApiConstants.FilterBy, dataManager.getRole())
            dataManager.getCallDetails(params).doOnSubscribe {
                loaderObservable.value =
                    NetworkRequestState.LoadingData(ApiProvider.ApiLoadDashboard)
            }.subscribe({
                try {
                    if (it.status == "0") {
                        viewModelScope.launch {
                            io {
                                val data = it.getData()
                                dataManager.clearPreviousData()
                                dataManager.insertCallData(data.callsList)

                                val grievances: List<SrCitizenGrievance> =
                                    prepareGrievances(data.callsList)
                                dataManager.insertGrievances(grievances)
                            }
                            loaderObservable.value = NetworkRequestState.SuccessResponse(
                                ApiProvider.ApiLoadDashboard, it
                            )
                        }
                    } else loaderObservable.value =
                        NetworkRequestState.ErrorResponse(ApiProvider.ApiLoadDashboard)
                } catch (e: Exception) {
                    println("$TAG ${e.message}")
                }
            }, {
                loaderObservable.value =
                    NetworkRequestState.ErrorResponse(ApiProvider.ApiLoadDashboard, it)
            }).autoDispose(disposables)
        }
    }

    /**
     * Method for fetching the grievance list from the API
     */
    fun getGrievanceTrackingList(context: Context) {
        if (checkNetworkAvailability(context, ApiProvider.ApiGrievanceTracking)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.Id, dataManager.getUserId().toInt())
            if (dataManager.getRole() == ROLE_VOLUNTEER || dataManager.getRole() == ROLE_STAFF_MEMBER) {
                params.addProperty(ApiConstants.FilterBy, dataManager.getRole())
            } else {
                var id = -1
                if (dataManager.getSelectedDistrictId().isNotEmpty()) {
                    id = dataManager.getSelectedDistrictId().toInt()
                }
                if (id == -1) {
                    params.addProperty(ApiConstants.FilterBy, dataManager.getRole())
                }
                params.addProperty(ApiConstants.DistrictId, id)
            }
            //params.addProperty(ApiConstants.Role, dataManager.getRole())
            //params.addProperty(ApiConstants.LastId, 0)// id - last id we got in list
            //params.addProperty(ApiConstants.RequestedData, 0)// Count - No of data we need in oone page
            dataManager.getGrievanceTrackingDetails(params).doOnSubscribe {
                if (dataManager.getRole() == ROLE_MASTER_ADMIN) {
                    viewModelScope.launch {
                        io { dataManager.clearPreviousTrackingData() }
                    }
                }
                loaderObservable.value =
                    NetworkRequestState.LoadingData(ApiProvider.ApiGrievanceTracking)
            }.subscribe({
                try {
                    if (it.getStatus() == "0") {
                        viewModelScope.launch {
                            io {
                                if (dataManager.getRole() != ROLE_MASTER_ADMIN)
                                    dataManager.clearPreviousTrackingData()
                                dataManager.insertGrievanceTrackingList(it.getTrackingList())
                            }
                            loaderObservable.value = NetworkRequestState.SuccessResponse(
                                ApiProvider.ApiGrievanceTracking, it
                            )
                        }
                    } else loaderObservable.value =
                        NetworkRequestState.ErrorResponse(ApiProvider.ApiGrievanceTracking)
                } catch (e: Exception) {
                    println("$TAG ${e.message}")
                }
            }, {
                var errorCode = -1
                if (it is HttpException) {
                    errorCode = it.code()
                }
                loaderObservable.value =
                    NetworkRequestState.ErrorResponse(
                        ApiProvider.ApiGrievanceTracking,
                        it,
                        errorCode = errorCode
                    )
            }).autoDispose(disposables)
        }
    }
}