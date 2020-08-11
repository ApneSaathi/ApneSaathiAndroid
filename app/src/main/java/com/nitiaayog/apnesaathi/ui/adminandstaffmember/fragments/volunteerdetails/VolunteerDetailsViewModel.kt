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
            return instance ?: synchronized(this) {
                VolunteerDetailsViewModel(dataManager, volunteerId).also { instance = it }
            }
        }
    }

    private val pendingCalls: MutableList<CallData> = mutableListOf()
    private val followupCalls: MutableList<CallData> = mutableListOf()
    private val completedCalls: MutableList<CallData> = mutableListOf()
    private val invalidCalls: MutableList<CallData> = mutableListOf()

    private fun differentiateCalls(data: MutableList<CallData>) {
        val pndngCalls: List<CallData> = data.filter {
            ((it.callStatusCode == "1") || (it.callStatusCode == "null") || (it.callStatusCode == ""))
        }
        pendingCalls.addAll(pndngCalls)

        val flupCalls: List<CallData> = data.filter {
            ((it.callStatusCode == "2") || (it.callStatusCode == "3") || (it.callStatusCode == "4")
                    || (it.callStatusCode == "5") || (it.callStatusCode == "6"))
        }
        followupCalls.addAll(flupCalls)

        val cmpltCalls: List<CallData> = data.filter {
            ((it.callStatusCode == "9") || (it.callStatusCode == "10"))
        }
        completedCalls.addAll(cmpltCalls)

        val invldCalls: List<CallData> = data.filter {
            ((it.callStatusCode == "7") || (it.callStatusCode == "8"))
        }
        invalidCalls.addAll(invldCalls)
    }

    @WorkerThread
    private suspend fun getCallDetails() {
        val params = JsonObject()
        params.addProperty(ApiConstants.VolunteerId, volunteerId)
        dataManager.getCallDetails(params).doOnSubscribe {
            updateNetworkState(NetworkRequestState.LoadingData(ApiProvider.ApiLoadDashboard))
        }.subscribe({
            try {
                if (it.status == "0") {
                    viewModelScope.launch {
                        io {
                            val data = it.getData()
                            differentiateCalls(data.callsList)
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
        val calls: MutableList<CallData> = mutableListOf()
        calls.addAll(pendingCalls)
        calls.addAll(followupCalls)
        calls.addAll(completedCalls)
        calls.addAll(invalidCalls)
        return calls
    }

    suspend fun getVolunteerDetails(context: Context) {
        if (checkNetworkAvailability(context, ApiProvider.ApiLoadDashboard)) {
            io { getCallDetails() }
        }
    }

    suspend fun updateRatings(context: Context, ratings: Float) {
        if (checkNetworkAvailability(context, ApiProvider.ApiUpdateVolunteerRatings)) {
            io { updateRatings(ratings.toString()) }
        }
    }
}