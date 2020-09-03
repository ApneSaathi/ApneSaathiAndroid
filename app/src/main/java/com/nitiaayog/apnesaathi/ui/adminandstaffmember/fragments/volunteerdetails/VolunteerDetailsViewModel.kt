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

/**
 * This ViewModel class is used in
 * @see com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.about.AboutVolunteerFragment and
 * @see com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.reviewrating.FragmentRatingReviews
 * to get the details of Volunteer and to rate the volunteer
 * */
class VolunteerDetailsViewModel(
    private val dataManager: DataManager, private val volunteerId: Int
) : BaseViewModel() {

    companion object {
        private val TAG: String = "TAG -- ${VolunteerDetailsViewModel::class.java.simpleName} -->"

        @Synchronized
        fun getInstance(dataManager: DataManager, volunteerId: Int): VolunteerDetailsViewModel {
            return synchronized(this) { VolunteerDetailsViewModel(dataManager, volunteerId) }
        }
    }

    private val allCalls: MutableList<CallData> = mutableListOf()
    private val pendingCalls: MutableList<CallData> = mutableListOf()
    private val followupCalls: MutableList<CallData> = mutableListOf()
    private val completedCalls: MutableList<CallData> = mutableListOf()
    private val invalidCalls: MutableList<CallData> = mutableListOf()

    private fun addCallsTo(calls: MutableList<CallData>, callsToAdded: List<CallData>) {
        calls.apply {
            clear()
            addAll(callsToAdded)
        }
    }

    /**
     * Differentiate various types of calls to show them in a chart.Go through the Ui of
     * @see com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.about.AboutVolunteerFragment
     * */
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

    /**
     * Get assigned Sr. Citizens and call progress details as per the selected date
     **/
    @WorkerThread
    private suspend fun getAssignedSrCitizens(date: String) {// date in format "yyyy-MM-dd"
        val params = JsonObject()
        params.addProperty(ApiConstants.VolunteerId, volunteerId)
        params.addProperty(ApiConstants.LoggedDateTime, date)
        println("$TAG $params")
        dataManager.getSeniorCitizenDetails(params).doOnSubscribe {
            updateNetworkState(NetworkRequestState.LoadingData(ApiProvider.ApiSeniorCitizenDetails))
        }.subscribe({
            try {
                if (it.status == "0") {
                    viewModelScope.launch {
                        io {
                            val data = it.getSeniorCitizens()
                            if (data.isNotEmpty()) {
                                allCalls.apply {
                                    clear()
                                    addAll(data)
                                }
                                differentiateCalls()
                            }
                            updateNetworkState(
                                NetworkRequestState.SuccessResponse(
                                    ApiProvider.ApiSeniorCitizenDetails, data
                                )
                            )
                        }
                    }
                } else updateNetworkState(
                    NetworkRequestState.SuccessResponse(
                        ApiProvider.ApiSeniorCitizenDetails, mutableListOf<CallData>()
                    )
                )
            } catch (e: Exception) {
                println("$TAG ${e.message}")
            }
        }, {
            updateNetworkState(
                NetworkRequestState.ErrorResponse(ApiProvider.ApiSeniorCitizenDetails, it)
            )
        }).autoDispose(disposables)
    }

    /**
     * Update the Ratings for the particular volunteer
     **/
    @WorkerThread
    private fun rateVolunteer(rating: Float, volunteerName: String) {
        val params = JsonObject()
        params.addProperty(ApiConstants.Ratings, rating)
        params.addProperty(ApiConstants.VolunteerId, volunteerId)
        params.addProperty(ApiConstants.VolunteerName, volunteerName)
        params.addProperty(ApiConstants.AdminId, dataManager.getUserId().toInt())
        params.addProperty(ApiConstants.AdminName, dataManager.getFirstName())

        println("$TAG $params")

        dataManager.updateVolunteerRatings(params).doOnSubscribe {
            updateNetworkState(NetworkRequestState.LoadingData(ApiProvider.ApiRateVolunteer))
        }.subscribe({
            try {
                viewModelScope.launch {
                    if (it.status == "0")
                        updateNetworkState(
                            NetworkRequestState.SuccessResponse(ApiProvider.ApiRateVolunteer, "")
                        )
                    else
                        updateNetworkState(NetworkRequestState.ErrorResponse(ApiProvider.ApiRateVolunteer))
                }
            } catch (e: Exception) {
                println("$TAG ${e.message}")
            }
        }, {
            updateNetworkState(
                NetworkRequestState.ErrorResponse(ApiProvider.ApiRateVolunteer, it)
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

    suspend fun getSeniorCitizens(context: Context, date: String) {
        if (checkNetworkAvailability(context, ApiProvider.ApiLoadDashboard)) {
            io { getAssignedSrCitizens(date) }
        }
    }

    suspend fun rateVolunteer(context: Context, ratings: Float, volunteerName: String) {
        if (checkNetworkAvailability(context, ApiProvider.ApiRateVolunteer)) {
            io { rateVolunteer(ratings, volunteerName) }
        }
    }
}