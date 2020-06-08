package com.nitiaayog.apnesaathi.ui.fragments

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.CallsConnected
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class TodaysCallsViewModel private constructor(private val dataManager: DataManager) :
    BaseViewModel() {

    companion object {
        @Volatile
        private var instance: TodaysCallsViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): TodaysCallsViewModel =
            instance ?: synchronized(this) {
                instance ?: TodaysCallsViewModel(dataManager).also { instance = it }
            }
    }

    private val dataList: MutableList<CallsConnected> = mutableListOf()

    fun prepareData():MutableList<CallsConnected> {
        dataList.add(
            CallsConnected(
                "1", "Omi H Mehta", "9016903906", "Pune",
                "Maharashra", "60", "not mentioned"
            )
        )
        dataList.add(
            CallsConnected(
                "2", "Sunil Sunny", "9016903906", "Behraich",
                "Bihar", "62", "not mentioned"
            )
        )
        dataList.add(
            CallsConnected(
                "3", "Amol", "9016903906", "Pune",
                "Maharashra", "65", "not mentioned"
            )
        )
        dataList.add(
            CallsConnected(
                "4", "Sucheta", "9016903906", "Pune",
                "Maharashra", "63", "not mentioned"
            )
        )
        dataList.add(
            CallsConnected(
                "5", "Omi H Mehta", "9016903906", "Pune",
                "Maharashra", "68", "not mentioned"
            )
        )
        return dataList
    }
}