package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.volunteerdetails

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class VolunteerDetailsViewModel(
    private val dataManager: DataManager, private val volunteerId: Int
) : BaseViewModel() {

    companion object {
        private val TAG: String = "TAG -- ${VolunteerDetailsViewModel::class.java.simpleName} -->"

        private var instance: VolunteerDetailsViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager, volunteerId: Int): VolunteerDetailsViewModel {
            return if (instance != null) {
                return if (instance!!.volunteerId == volunteerId) instance!! else synchronized(this) {
                    VolunteerDetailsViewModel(dataManager, volunteerId).also { instance = it }
                }
            } else synchronized(this) {
                VolunteerDetailsViewModel(dataManager, volunteerId).also { instance = it }
            }
        }
    }

    private val allCalls: MutableList<CallData> = mutableListOf()
    private val pendingCalls: MutableList<CallData> = mutableListOf()
    private val followupCalls: MutableList<CallData> = mutableListOf()
    private val completedCalls: MutableList<CallData> = mutableListOf()
    private val invalidCalls: MutableList<CallData> = mutableListOf()

    override fun onCleared() {
        instance?.let { instance = null }
        super.onCleared()
    }

    private fun addCallsTo(calls: MutableList<CallData>, callsToAdded: List<CallData>) {
        calls.apply {
            clear()
            addAll(callsToAdded)
        }
    }

    private fun differentiateCalls() {
        val pndngCalls: List<CallData> = allCalls.filter {
            ((it.callStatusCode == "1") || (it.callStatusCode == "null") || (it.callStatusCode == ""))
        }
        addCallsTo(pendingCalls, pndngCalls)

        val flupCalls: List<CallData> = allCalls.filter {
            ((it.callStatusCode == "2") || (it.callStatusCode == "3") || (it.callStatusCode == "4")
                    || (it.callStatusCode == "5") || (it.callStatusCode == "6"))
        }
        addCallsTo(followupCalls, flupCalls)

        val cmpltCalls: List<CallData> = allCalls.filter {
            ((it.callStatusCode == "9") || (it.callStatusCode == "10"))
        }
        addCallsTo(completedCalls, cmpltCalls)

        val invldCalls: List<CallData> = allCalls.filter {
            ((it.callStatusCode == "7") || (it.callStatusCode == "8"))
        }
        addCallsTo(invalidCalls, invldCalls)
    }

    @WorkerThread
    private suspend fun getCallDetails(date: String) {// date in format "yyyy-MM-dd"
        val params = JsonObject()
        params.addProperty(ApiConstants.VolunteerId, volunteerId)
        params.addProperty(ApiConstants.LoggedDateTime, date)
        println("$TAG $params")
        dataManager.getSeniorCitizenDetails(params).doOnSubscribe {
            updateNetworkState(NetworkRequestState.LoadingData(ApiProvider.ApiLoadDashboard))
        }.subscribe({
            try {
                if (it.status == "0") {
                    viewModelScope.launch {
                        io {
                            val data = it.getSeniorCitizens()
                            allCalls.apply {
                                clear()
                                addAll(data)
                            }
                            differentiateCalls()
                            updateNetworkState(
                                NetworkRequestState.SuccessResponse(
                                    ApiProvider.ApiLoadDashboard, data
                                )
                            )
                        }
                    }
                } else updateNetworkState(NetworkRequestState.ErrorResponse(ApiProvider.ApiLoadDashboard))
            } catch (e: Exception) {
                println("$TAG ${e.message}")
            }
        }, {
            updateNetworkState(NetworkRequestState.ErrorResponse(ApiProvider.ApiLoadDashboard, it))
        }).autoDispose(disposables)
    }

    @WorkerThread
    private suspend fun updateRatings(rating: String) {
        val params = JsonObject()
        params.addProperty(ApiConstants.Ratings, rating)
        params.addProperty(ApiConstants.VolunteerId, volunteerId)
        params.addProperty(ApiConstants.AdminId, dataManager.getUserId().toInt())
        dataManager.updateVolunteerRatings(params).doOnSubscribe {
            updateNetworkState(NetworkRequestState.LoadingData(ApiProvider.ApiUpdateVolunteerRatings))
        }.subscribe({
            try {
                viewModelScope.launch {
                    if (it.status == "0")
                        updateNetworkState(
                            NetworkRequestState.SuccessResponse(
                                ApiProvider.ApiUpdateVolunteerRatings, ""
                            )
                        )
                    else
                        updateNetworkState(NetworkRequestState.ErrorResponse(ApiProvider.ApiUpdateVolunteerRatings))
                }
            } catch (e: Exception) {
                println("$TAG ${e.message}")
            }
        }, {
            updateNetworkState(
                NetworkRequestState.ErrorResponse(ApiProvider.ApiUpdateVolunteerRatings, it)
            )
        }).autoDispose(disposables)
    }

    fun getNetworkStream(): LiveData<NetworkRequestState> {
        return loaderObservable
    }

    fun getPendingCalls(): List<CallData> {
        return pendingCalls
    }

    fun getFollowupCalls(): List<CallData> {
        return followupCalls
    }

    fun getCompletedCalls(): List<CallData> {
        return completedCalls
    }

    fun getInvalidCalls(): List<CallData> {
        return invalidCalls
    }

    fun getCalls(): List<CallData> {
        return allCalls
    }

    suspend fun getVolunteerDetails(context: Context, date: String) {
        if (checkNetworkAvailability(context, ApiProvider.ApiLoadDashboard)) {
            io { getCallDetails(date) }
        }
    }

    suspend fun updateRatings(context: Context, ratings: Float) {
        if (checkNetworkAvailability(context, ApiProvider.ApiUpdateVolunteerRatings)) {
            io { updateRatings(ratings.toString()) }
        }
    }
}