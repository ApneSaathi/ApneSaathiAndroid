package com.nitiaayog.apnesaathi.ui.fragments

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.DateItem
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel

class SeniorCitizenDetailsViewModel private constructor(private val dataManager: DataManager) :
    BaseViewModel() {
    private val dataList: MutableList<DateItem> = mutableListOf()
    fun prepareData(): MutableList<DateItem> {
        dataList.add(
            DateItem(
                "01", "Jan"
            )
        )
        dataList.add(
            DateItem(
                "02", "Jan"
            )
        )
        dataList.add(
            DateItem(
                "08", "Jan"
            )
        )
        dataList.add(
            DateItem(
                "09", "Feb"
            )
        )
        return  dataList
    }

    companion object {
        @Volatile
        private var instance: SeniorCitizenDetailsViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): SeniorCitizenDetailsViewModel =
            instance ?: synchronized(this) {
                instance ?: SeniorCitizenDetailsViewModel(dataManager).also { instance = it }
            }
    }
}