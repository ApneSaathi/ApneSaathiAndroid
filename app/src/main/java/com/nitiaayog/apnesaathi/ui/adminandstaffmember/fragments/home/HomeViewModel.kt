package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.*
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.paging.volunteer.VolunteerSourceFactory
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {

        private val TAG: String = "TAG -- ${HomeViewModel::class.java.simpleName} -->"

        @Volatile
        private var instance: HomeViewModel? = null

        private lateinit var factory: VolunteerSourceFactory

        @Synchronized
        fun getInstance(context: Context, dataManager: DataManager): HomeViewModel {
            if (instance == null) synchronized(this) {
                HomeViewModel(dataManager).also { instance = it }
            }
            factory = VolunteerSourceFactory(context, instance!!)
            return instance!!
        }
    }

    private val pendingCalls: LiveData<MutableList<CallData>> = dataManager.getPendingCallsList()
    private val followUpCalls: LiveData<MutableList<CallData>> = dataManager.getFollowupCallsList()
    private val completedCalls: LiveData<MutableList<CallData>> =
        dataManager.getCompletedCallsList()
    private val invalidCalls: LiveData<MutableList<CallData>> = dataManager.getInvalidCallsList()

    private val grievancesTrackingList: LiveData<MutableList<GrievanceData>> =
        dataManager.getAllTrackingGrievances()

    private val volunteerData: LiveData<PagedList<Volunteer>> by lazy {
        LivePagedListBuilder(factory, config).build()
    }

    override fun onCleared() {
        factory.invalidateSource()
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

    fun getVolunteersStream(): LiveData<PagedList<Volunteer>> {
        return volunteerData
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

    fun getVolunteers(context: Context) {
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (checkNetworkAvailability(context, ApiProvider.ApiGetVolunteers)) {
                val params = JsonObject()
                params.addProperty(ApiConstants.UserId, dataManager.getUserId().toInt())
                params.addProperty(ApiConstants.LastId, factory.getKey())
                params.addProperty(ApiConstants.RequestedData, config.pageSize)
                dataManager.getVolunteers(params).doOnSubscribe {
                    updateNetworkState(NetworkRequestState.LoadingData(ApiProvider.ApiGetVolunteers))
                }.subscribe({
                    viewModelScope.launch {
                        if (it.status == "0") {
                            io {
                                if (factory.getKey() == 0)
                                    dataManager.deleteVolunteers()
                                dataManager.insertVolunteers(it.volunteerList)
                            }
                            updateNetworkState(
                                NetworkRequestState.SuccessResponse(
                                    ApiProvider.ApiGetVolunteers, it.volunteerList
                                )
                            )
                        } else updateNetworkState(
                            NetworkRequestState.Error(ApiProvider.ApiGetVolunteers)
                        )
                    }
                }, {
                    updateNetworkState(
                        NetworkRequestState.ErrorResponse(ApiProvider.ApiGetVolunteers, it)
                    )
                })
            }
        }
    }

    fun getCallDetails(context: Context) {
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

    fun getGrievanceTrackingList(context: Context) {
        if (checkNetworkAvailability(context, ApiProvider.ApiGrievanceTracking)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.VolunteerId, dataManager.getUserId())
            params.addProperty(ApiConstants.Role, dataManager.getRole())
            params.addProperty(ApiConstants.LastId, factory.getKey())// id - last id we got in list
            // Count - No of data we need in one page
            params.addProperty(ApiConstants.RequestedData, config.pageSize)
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
                            updateNetworkState(
                                NetworkRequestState.SuccessResponse(
                                    ApiProvider.ApiGrievanceTracking, it
                                )
                            )
                        }
                    } else updateNetworkState(
                        NetworkRequestState.ErrorResponse(ApiProvider.ApiGrievanceTracking)
                    )
                } catch (e: Exception) {
                    println("$TAG ${e.message}")
                }
            }, {
                updateNetworkState(
                    NetworkRequestState.ErrorResponse(ApiProvider.ApiGrievanceTracking, it)
                )
            }).autoDispose(disposables)
        }
    }
}