package com.nitiaayog.apnesaathi.ui.fragments.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.adapter.GrievancesAdapter
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.io
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.Grievances
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class HomeViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {
        @Synchronized
        fun getInstance(dataManager: DataManager): HomeViewModel =
            synchronized(this) { HomeViewModel(dataManager) }
    }

    private val TAG: String = "TAG -- ${HomeViewModel::class.java.simpleName} -->"

    private val followupCallsList: MutableList<User> = mutableListOf()
    private val attendedCallsList: MutableList<User> = mutableListOf()
    private val allCallsList: MutableList<User> = mutableListOf()
    private val grievances: MutableList<Grievances> = mutableListOf()

    private val callsList: LiveData<MutableList<CallData>> = dataManager.getAllCallsList()
    fun getUniqueGrievanceList(id: Int): LiveData<MutableList<SrCitizenGrievance>> = dataManager.getAllUniqueGrievances(id)
    private val grievancesList: LiveData<MutableList<SrCitizenGrievance>> =
        dataManager.getGrievances()

    private var grievancesFromCall: MutableList<SrCitizenGrievance> = mutableListOf()

    init {
        prepareFollowupData()
        prepareAttendedData()
        prepareGrievancesData()
    }

    private fun prepareFollowupData() {
        followupCallsList.add(
            User(
                "5", "Sucheta S.", "53", "TCG 1005", "Pune",
                "Maharashtra", "F", "9650650808"
            )
        )
        followupCallsList.add(
            User(
                "6", "Dushyant Datta", "57", "Shree Hari Nagar", "Kota",
                "Rajasthan", "M", "8076982318"
            )
        )
        allCallsList.addAll(followupCallsList)
    }

    private fun prepareAttendedData() {
        attendedCallsList.add(
            User(
                "6", "RajShankar Khanal", "67", "Rang Baugh Society", "Kota",
                "Haridwar", "M", "8076982318"
            )
        )
        attendedCallsList.add(
            User(
                "7", "Sr. Narendra Modi", "70", "Rashtrapati Bhavan", "Vadnager",
                "Gujarat", "M", "9650650808"
            )
        )
        attendedCallsList.add(
            User(
                "8", "Akshay Kumar", "71", "Bandra", "Mumbai",
                "Maharashtra", "M", "9016903906"
            )
        )
        attendedCallsList.add(
            User(
                "9", "Amitabh Bachchan", "52", "Phase 2, Hinjewadi", "Pune",
                "Maharashtra", "M", "8893089872"
            )
        )
        allCallsList.addAll(attendedCallsList)
    }

    private fun prepareGrievancesData() {
        grievances.add(
            Grievances(
                "1", "Having food related issues", GrievancesAdapter.GRIEVANCE_PENDING
            )
        )
        grievances.add(
            Grievances(
                "2", "Not receiving pension", GrievancesAdapter.GRIEVANCE_RESOLVED
            )
        )
        grievances.add(
            Grievances(
                "3", "Having COVID-19 symptoms", GrievancesAdapter.GRIEVANCE_PENDING
            )
        )
        grievances.add(
            Grievances(
                "4", "Medical checkup not possible", GrievancesAdapter.GRIEVANCE_RESOLVED
            )
        )
        grievances.add(
            Grievances("5", "Physically unfit", GrievancesAdapter.GRIEVANCE_PENDING)
        )
        grievances.add(
            Grievances(
                "6", "Diabetic and Pressure related issues", GrievancesAdapter.GRIEVANCE_RESOLVED
            )
        )
    }

    fun getFewGrievancesList() = if (grievances.size > 3) grievances.subList(0, 3) else grievances

    fun getGrievances() = grievances

    fun getFewFollowupCalls(): MutableList<User> =
        if (followupCallsList.size > 3) followupCallsList.subList(0, 3) else followupCallsList

    fun getFollowupCalls(): MutableList<User> = followupCallsList

    fun getFewAttendedCalls(): MutableList<User> =
        if (attendedCallsList.size > 3) attendedCallsList.subList(0, 3) else attendedCallsList

    fun getAttendedCalls(): MutableList<User> = attendedCallsList

    fun getAllCalls(): MutableList<User> = allCallsList

    private fun prepareGrievances(grievance: List<CallData>): List<SrCitizenGrievance> {
        val callData = grievance.filter {
            (it.medicalGrievance != null && it.medicalGrievance!!.size > 0)
        }
        val grievances: MutableList<SrCitizenGrievance> = mutableListOf()
        callData.forEach { grievances.addAll(it.medicalGrievance!!) }
        return grievances
    }

    fun getDataStream(): LiveData<NetworkRequestState> = loaderObservable

    fun getCallsList(): LiveData<MutableList<CallData>> = callsList

    fun getGrievancesList(): LiveData<MutableList<SrCitizenGrievance>> = grievancesList

    fun getCallDetails(context: Context) {
        if (checkNetworkAvailability(context, ApiProvider.ApiLoadDashboard)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.VolunteerId, 1234 /*dataManager.getUserId()*/)
            dataManager.getCallDetails(params).doOnSubscribe {
                loaderObservable.value =
                    NetworkRequestState.LoadingData(ApiProvider.ApiLoadDashboard)
            }.subscribe({
                try {
                    if (it.status == "0") {
                        viewModelScope.launch {
                            io {
                                val data = it.getData()
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
}