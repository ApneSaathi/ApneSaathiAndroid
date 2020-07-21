package com.nitiaayog.apnesaathi.ui.fragments.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.io
import com.nitiaayog.apnesaathi.database.dao.GrievancesDao_Impl
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.GrievanceData
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class HomeViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {
        @Volatile
        private var instance: HomeViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): HomeViewModel =
            instance ?: synchronized(this) {
                instance ?: HomeViewModel(dataManager).also { instance = it }
            }
    }

    private val TAG: String = "TAG -- ${HomeViewModel::class.java.simpleName} -->"

    private val pendingCallsList: LiveData<MutableList<CallData>> =
        dataManager.getPendingCallsList()
    private val followUpCallsList: LiveData<MutableList<CallData>> =
        dataManager.getFollowupCallsList()
    private val completedCallsList: LiveData<MutableList<CallData>> =
        dataManager.getCompletedCallsList()
    private val callsList: LiveData<MutableList<CallData>> = dataManager.getAllCallsList()

    private val pendingGrievance :LiveData<MutableList<GrievanceData>> = dataManager.getPendingGrievances()
    private val inProgressGrievance :LiveData<MutableList<GrievanceData>> = dataManager.getInProgressGrievances()
    private val resolvedGrievance :LiveData<MutableList<GrievanceData>> = dataManager.getResolvedGrievances()

    private val grievancesList: LiveData<MutableList<SrCitizenGrievance>> =
        dataManager.getGrievances()

    private val grievancesTrackingList: LiveData<MutableList<GrievanceData>> =
        dataManager.getAllTrackingGrievances()

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

    fun getDataStream(): LiveData<NetworkRequestState> = loaderObservable

    fun getCallsList(): LiveData<MutableList<CallData>> = callsList

    fun getPendingCalls(): LiveData<MutableList<CallData>> = pendingCallsList

    fun getFollowupCalls(): LiveData<MutableList<CallData>> = followUpCallsList

    fun getCompletedCalls(): LiveData<MutableList<CallData>> = completedCallsList

    fun getPendingGrievances():LiveData<MutableList<GrievanceData>> = pendingGrievance
    fun getInProgressGrievances():LiveData<MutableList<GrievanceData>> = inProgressGrievance
    fun getResolvedGrievances():LiveData<MutableList<GrievanceData>> = resolvedGrievance

    fun getGrievancesList(): LiveData<MutableList<SrCitizenGrievance>> = grievancesList

    fun getGrievancesTrackingList(): LiveData<MutableList<GrievanceData>> = grievancesTrackingList

    fun getCallDetails(context: Context) {
        if (checkNetworkAvailability(context, ApiProvider.ApiLoadDashboard)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.VolunteerId, dataManager.getUserId().toInt())
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

    fun getGrievanceTrackingList(context: Context) {
        if (checkNetworkAvailability(context, ApiProvider.ApiGrievanceTracking)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.VolunteerId, dataManager.getUserId())
            dataManager.getGrievanceTrackingDetails(params).doOnSubscribe {
                loaderObservable.value =
                    NetworkRequestState.LoadingData(ApiProvider.ApiGrievanceTracking)
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
                    } else loaderObservable.value =
                        NetworkRequestState.ErrorResponse(ApiProvider.ApiGrievanceTracking)
                } catch (e: Exception) {
                    println("$TAG ${e.message}")
                }
            }, {
                loaderObservable.value =
                    NetworkRequestState.ErrorResponse(ApiProvider.ApiGrievanceTracking, it)
            }).autoDispose(disposables)
        }

    }
}