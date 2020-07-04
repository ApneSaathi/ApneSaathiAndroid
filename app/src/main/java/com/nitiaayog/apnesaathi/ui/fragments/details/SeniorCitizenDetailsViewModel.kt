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

class SeniorCitizenDetailsViewModel private constructor(private val dataManager: DataManager) :
    BaseViewModel() {
    private val dateList: MutableList<DateItem> = ArrayList()
    private val dataList = MutableLiveData<List<DateItem>>()
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

    private fun getMonthFromDate(date: String?): String {
        val input = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val output = DateTimeFormatter.ofPattern("MMM")
        val fa = input.parse(date)
        return output.format(fa)
    }

    private fun getDayFromDate(date: String?): String {
        val input = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val output = DateTimeFormatter.ofPattern("dd")
        val fa = input.parse(date)
        return output.format(fa)
    }

    fun getDataObserver(): LiveData<NetworkRequestState> = loaderObservable

    //    fun getSeniorCitizenDetails(context: Context) {
//        if (checkNetworkAvailability(context)) {
//            val params = JsonObject()
//            params.addProperty("callid", 11)
//            dataManager.getSeniorCitizenDetails(params).doOnSubscribe {
//                loaderObservable.value = NetworkRequestState.LoadingData
//            }.doOnSuccess { loaderObservable.value = NetworkRequestState.SuccessResponse(it) }
//                .doOnError {
//                    loaderObservable.value =
//                        NetworkRequestState.ErrorResponse(ApiConstants.STATUS_EXCEPTION, it)
//                }.subscribe().autoDispose(disposables)
//        }
//    }
    fun getUniqueGrievanceList(id: Int): LiveData<MutableList<SrCitizenGrievance>> =
        dataManager.getAllUniqueGrievances(id)

    fun getDataList(): MutableLiveData<List<DateItem>> {
        return dataList
    }

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