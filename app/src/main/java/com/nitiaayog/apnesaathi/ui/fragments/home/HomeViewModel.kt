package com.nitiaayog.apnesaathi.ui.fragments.home

import com.nitiaayog.apnesaathi.datamanager.DataManager
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

    init {
        preparePendingData()
        prepareFollowupData()
        prepareAttendedData()
    }

    private fun preparePendingData() {
        pendingCallsList.add(
            User(
                "1", "Sunil Sunny", "102/Shantinagar", "Pune",
                "Maharashtra", "M", "8893089872"
            )
        )
        pendingCallsList.add(
            User(
                "2", "Amol Khose", "Panghat Row House", "Pune",
                "Maharashtra", "M", "9673346489"
            )
        )
        pendingCallsList.add(
            User(
                "3", "Omi H Mehta", "803/Nakshatra View", "Surat",
                "Gujarat", "M", "9016903906"
            )
        )
        pendingCallsList.add(
            User(
                "4", "Tejeshwar Chaudhary", "Niti Aayog", "Pune",
                "Maharashtra", "M", "9650650808"
            )
        )
        pendingCallsList.add(
            User(
                "5", "Sucheta S.", "TCG 1005", "Pune",
                "Maharashtra", "F", "9650650808"
            )
        )
        pendingCallsList.add(
            User(
                "6", "Dushyant Datta", "Shree Hari Nagar", "Kota",
                "Rajasthan", "M", "8076982318"
            )
        )
        pendingCallsList.add(
            User(
                "4", "Tejeshwar Chaudhary", "Niti Aayog", "Pune",
                "Maharashtra", "M", "9650650808"
            )
        )
        pendingCallsList.add(
            User(
                "5", "Sucheta S.", "TCG 1005", "Pune",
                "Maharashtra", "F", "9650650808"
            )
        )
        pendingCallsList.add(
            User(
                "6", "Dushyant Datta", "Shree Hari Nagar", "Kota",
                "Rajasthan", "M", "8076982318"
            )
        )
        allCallsList.addAll(pendingCallsList)
    }

    private fun prepareFollowupData() {
        followupCallsList.add(
            User(
                "5", "Sucheta S.", "TCG 1005", "Pune",
                "Maharashtra", "F", "9650650808"
            )
        )
        followupCallsList.add(
            User(
                "6", "Dushyant Datta", "Shree Hari Nagar", "Kota",
                "Rajasthan", "M", "8076982318"
            )
        )
        allCallsList.addAll(followupCallsList)
    }

    private fun prepareAttendedData() {
        attendedCallsList.add(
            User(
                "6", "RajShankar Khanal", "Rang Baugh Society", "Kota",
                "Haridwar", "M", "8076982318"
            )
        )
        attendedCallsList.add(
            User(
                "7", "Sr. Narendra Modi", "Rashtrapati Bhavan", "Vadnager",
                "Gujarat", "M", "9650650808"
            )
        )
        attendedCallsList.add(
            User(
                "8", "Akshay Kumar", "Bandra", "Mumbai",
                "Maharashtra", "M", "9016903906"
            )
        )
        attendedCallsList.add(
            User(
                "9", "Amitabh Bachchan", "Phase 2, Hinjewadi", "Pune",
                "Maharashtra", "M", "8893089872"
            )
        )
        allCallsList.addAll(attendedCallsList)
    }

    fun getFewPendingCalls(): MutableList<User> =
        if (pendingCallsList.size > 3) pendingCallsList.subList(0, 3) else pendingCallsList

    fun getPendingCalls(): MutableList<User> = pendingCallsList

    fun getFewFollowupCalls(): MutableList<User> =
        if (followupCallsList.size > 3) followupCallsList.subList(0, 3) else followupCallsList

    fun getFollowupCalls(): MutableList<User> = followupCallsList

    fun getFewAttendedCalls(): MutableList<User> =
        if (attendedCallsList.size > 3) attendedCallsList.subList(0, 3) else attendedCallsList

    fun getAttendedCalls(): MutableList<User> = attendedCallsList

    fun getAllCalls(): MutableList<User> = allCallsList
}