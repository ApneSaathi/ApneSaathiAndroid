package com.nitiaayog.apnesaathi.ui.fragments.home

import com.nitiaayog.apnesaathi.adapter.GrievancesAdapter
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.Grievances
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

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

    private val pendingCallsList: MutableList<User> = mutableListOf()
    private val followupCallsList: MutableList<User> = mutableListOf()
    private val attendedCallsList: MutableList<User> = mutableListOf()
    private val allCallsList: MutableList<User> = mutableListOf()

    private val grievancesList: MutableList<Grievances> = mutableListOf()

    init {
        preparePendingData()
        prepareFollowupData()
        prepareAttendedData()
        prepareGrievancesData()
    }

    private fun preparePendingData() {
        pendingCallsList.add(
            User(
                "1", "Sunil Sunny","78", "102/Shantinagar", "Pune",
                "Maharashtra", "M", "8893089872"
            )
        )
        pendingCallsList.add(
            User(
                "2", "Amol Khose", "65","Panghat Row House", "Pune",
                "Maharashtra", "M", "9673346489"
            )
        )
        pendingCallsList.add(
            User(
                "3", "Omi H Mehta","55", "803/Nakshatra View", "Surat",
                "Gujarat", "M", "9016903906"
            )
        )
        pendingCallsList.add(
            User(
                "4", "Tejeshwar Chaudhary", "60","Niti Aayog", "Pune",
                "Maharashtra", "M", "9650650808"
            )
        )
        pendingCallsList.add(
            User(
                "5", "Sucheta S.", "59","TCG 1005", "Pune",
                "Maharashtra", "F", "9650650808"
            )
        )
        pendingCallsList.add(
            User(
                "6", "Dushyant Datta","63", "Shree Hari Nagar", "Kota",
                "Rajasthan", "M", "8076982318"
            )
        )
        pendingCallsList.add(
            User(
                "4", "Tejeshwar Chaudhary","58", "Niti Aayog", "Pune",
                "Maharashtra", "M", "9650650808"
            )
        )
        pendingCallsList.add(
            User(
                "5", "Sucheta S.","75", "TCG 1005", "Pune",
                "Maharashtra", "F", "9650650808"
            )
        )
        pendingCallsList.add(
            User(
                "6", "Dushyant Datta", "63","Shree Hari Nagar", "Kota",
                "Rajasthan", "M", "8076982318"
            )
        )
        allCallsList.addAll(pendingCallsList)
    }

    private fun prepareFollowupData() {
        followupCallsList.add(
            User(
                "5", "Sucheta S.","53", "TCG 1005", "Pune",
                "Maharashtra", "F", "9650650808"
            )
        )
        followupCallsList.add(
            User(
                "6", "Dushyant Datta", "57","Shree Hari Nagar", "Kota",
                "Rajasthan", "M", "8076982318"
            )
        )
        allCallsList.addAll(followupCallsList)
    }

    private fun prepareAttendedData() {
        attendedCallsList.add(
            User(
                "6", "RajShankar Khanal", "67","Rang Baugh Society", "Kota",
                "Haridwar", "M", "8076982318"
            )
        )
        attendedCallsList.add(
            User(
                "7", "Sr. Narendra Modi","70", "Rashtrapati Bhavan", "Vadnager",
                "Gujarat", "M", "9650650808"
            )
        )
        attendedCallsList.add(
            User(
                "8", "Akshay Kumar", "71","Bandra", "Mumbai",
                "Maharashtra", "M", "9016903906"
            )
        )
        attendedCallsList.add(
            User(
                "9", "Amitabh Bachchan","52", "Phase 2, Hinjewadi", "Pune",
                "Maharashtra", "M", "8893089872"
            )
        )
        allCallsList.addAll(attendedCallsList)
    }

    private fun prepareGrievancesData() {
        grievancesList.add(
            Grievances(
                "1", "Having food related issues", GrievancesAdapter.GRIEVANCE_PENDING
            )
        )
        grievancesList.add(
            Grievances(
                "2", "Not receiving pension", GrievancesAdapter.GRIEVANCE_RESOLVED
            )
        )
        grievancesList.add(
            Grievances(
                "3", "Having COVID-19 symptoms", GrievancesAdapter.GRIEVANCE_PENDING
            )
        )
        grievancesList.add(
            Grievances(
                "4", "Medical checkup not possible", GrievancesAdapter.GRIEVANCE_RESOLVED
            )
        )
        grievancesList.add(
            Grievances(
                "5", "Physically unfit", GrievancesAdapter.GRIEVANCE_PENDING
            )
        )
        grievancesList.add(
            Grievances(
                "6", "Diabetic and Pressure related issues", GrievancesAdapter.GRIEVANCE_RESOLVED
            )
        )
    }

    fun getFewPendingCalls(): MutableList<User> =
        if (pendingCallsList.size > 3) pendingCallsList.subList(0, 3) else pendingCallsList

    fun getPendingCalls(): MutableList<User> = pendingCallsList

    fun getFewGrievancesList() =
        if (grievancesList.size > 3) grievancesList.subList(0, 3) else grievancesList

    fun getGrievancesList() = grievancesList

    fun getFewFollowupCalls(): MutableList<User> =
        if (followupCallsList.size > 3) followupCallsList.subList(0, 3) else followupCallsList

    fun getFollowupCalls(): MutableList<User> = followupCallsList

    fun getFewAttendedCalls(): MutableList<User> =
        if (attendedCallsList.size > 3) attendedCallsList.subList(0, 3) else attendedCallsList

    fun getAttendedCalls(): MutableList<User> = attendedCallsList

    fun getAllCalls(): MutableList<User> = allCallsList
}