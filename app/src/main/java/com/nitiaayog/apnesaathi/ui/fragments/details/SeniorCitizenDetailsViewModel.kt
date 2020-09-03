package com.nitiaayog.apnesaathi.ui.fragments.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.DateItem
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

/**
 * View model for handling all the actions related with senior citizen details page
 * [dataManager] is used to store all the data that is required in the app.
 */
class SeniorCitizenDetailsViewModel private constructor(private val dataManager: DataManager) :
    BaseViewModel() {
    private val dateList: MutableList<DateItem> = ArrayList()
    private val dataList = MutableLiveData<List<DateItem>>()

    /**
     * Method for extracting the date from senior citizen data
     * [data] is the data from which dates are extracted
     */
    fun prepareData(data: MutableList<SrCitizenGrievance>?) {
        dateList.clear()
        if (data != null) {
            for (grievanceData: SrCitizenGrievance in data) run {
                val dateItem = DateItem(
                    grievanceData.createdDate?.let { getDayFromDate(it) },
                    grievanceData.createdDate?.let { getMonthFromDate(it) },
                    grievanceData.status
                )
                dateList.add(dateItem)
                dataList.postValue(dateList)
            }
        }
    }

    /**
     * Method for getting the formatted month
     * [date] is a date which is required to be converted in the required format
     */
    private fun getMonthFromDate(date: String?): String {
        val input = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val output = DateTimeFormatter.ofPattern("MMM")
        val fa = input.parse(date)
        return output.format(fa)
    }

    /**
     * Method for getting the formatted day
     * [date] is a date which is required to be converted in the required format
     */
    private fun getDayFromDate(date: String?): String {
        val input = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val output = DateTimeFormatter.ofPattern("dd")
        val fa = input.parse(date)
        return output.format(fa)
    }

    /**
     * Method for observing the network state
     */
    fun getDataObserver(): LiveData<NetworkRequestState> = loaderObservable

    /**
     * Method for getting senior citizen data from the database
     */
    fun getUniqueGrievanceList(id: Int): LiveData<MutableList<SrCitizenGrievance>> =
        dataManager.getAllUniqueGrievances(id)

    /**
     * Method for getting the date details
     */
    fun getDataList(): MutableLiveData<List<DateItem>> {
        return dataList
    }

    /**
     * Method for setting the current date
     */
    fun setCurrentDate() {
        dateList.clear()
        val date = Calendar.getInstance().time
        val formatterMonth = SimpleDateFormat("MMM", Locale.ROOT) //or use getDateInstance()
        val formatterDay = SimpleDateFormat("dd", Locale.ROOT) //or use getDateInstance()
        val formattedMonth = formatterMonth.format(date)
        val formattedDay = formatterDay.format(date)
        val dateItem = DateItem(
            formattedDay,
            formattedMonth,
            "Not Connected"
        )
        dateList.add(dateItem)
        dataList.postValue(dateList)

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