package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.CallDetails
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
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
            params.addProperty(ApiConstants.VolunteerId, dataManager.getUserId().toInt())
            dataManager.getCallDetails(params).doOnSubscribe {
                updateNetworkState(NetworkRequestState.LoadingData(ApiProvider.ApiLoadDashboard))
            }.subscribe({
                try {
                    if (it.status == "0") {
                        viewModelScope.launch {
                            io {
                                val data = it.getData()
                                manageCallsData(data)
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
}