package com.nitiaayog.apnesaathi.ui.fragments.userDetails

import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import com.nitiaayog.apnesaathi.model.DateItem

class SeniorCitizenDetailsViewModel private constructor(private val dataManager: DataManager) :
    BaseViewModel() {
    private val dataList: MutableList<DateItem> = mutableListOf()
    fun prepareData(): MutableList<DateItem> {
        dataList.clear()
        dataList.add(
            DateItem(
                "01", "Jan","Attended"
            )
        )
        dataList.add(
            DateItem(
                "02", "Jan","Attended"
            )
        )
        dataList.add(
            DateItem(
                "08", "Jan","Attended"
            )
        )
        dataList.add(
            DateItem(
                "09", "Feb","Unattended"
            )
        )
        return  dataList
    }

    companion object {
        @Volatile
        private var instance: SeniorCitizenDetailsViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): SeniorCitizenDetailsViewModel =
            instance
                ?: synchronized(this) {
                instance
                    ?: SeniorCitizenDetailsViewModel(
                        dataManager
                    )
                        .also { instance = it }
            }
    }
}