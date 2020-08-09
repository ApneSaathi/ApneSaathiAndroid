package com.nitiaayog.apnesaathi.ui.fragments.calls.allcalls

import android.content.Context
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ItemKeyedDataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.io
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.paging.allcalls.CallsDataSourceFactory
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class AllCallsViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {

        private val TAG: String = "TAG -- ${AllCallsViewModel::class.java.simpleName} -->"

        @Volatile
        private var instance: AllCallsViewModel? = null

        private lateinit var factory: CallsDataSourceFactory

        @Synchronized
        fun getInstance(context: Context, dataManager: DataManager): AllCallsViewModel {
            if (instance == null) synchronized(this) {
                AllCallsViewModel(dataManager).also { instance = it }
            }
            factory = CallsDataSourceFactory(context, instance!!)
            return instance!!
        }
    }

    private var isAllDataLoaded: Boolean = false

    private val callsData: LiveData<PagedList<CallData>> by lazy {
        LivePagedListBuilder(factory, config).build()
    }

    override fun onCleared() {
        factory.invalidateSource()
        instance = null
        super.onCleared()
    }

    @WorkerThread
    private suspend fun storeData(dataList: MutableList<CallData>) = io {
        dataManager.insertCallData(dataList)

        val grievances: List<SrCitizenGrievance> = prepareGrievances(dataList)
        dataManager.insertGrievances(grievances)
    }

    private fun prepareGrievances(grievance: List<CallData>): List<SrCitizenGrievance> {
        val callData = grievance.filter {
            (it.medicalGrievance != null && it.medicalGrievance!!.size > 0)
        }
        val grievances: MutableList<SrCitizenGrievance> = mutableListOf()
        callData.forEach { data ->
            data.medicalGrievance?.forEach {
                it.srCitizenName = data.srCitizenName
                it.gender = data.gender
            }
            grievances.addAll(data.medicalGrievance!!)
        }
        return grievances
    }

    fun getDataManager(): DataManager = dataManager
    fun getDataStream(): LiveData<NetworkRequestState> = loaderObservable
    fun getCallsStream(): LiveData<PagedList<CallData>> = callsData

    @WorkerThread
    suspend fun getCallDetails(
        context: Context, callback: ItemKeyedDataSource.LoadCallback<CallData>? = null
    ) {
        if (isAllDataLoaded) {
            callback?.onResult(mutableListOf())
            return
        }
        if (checkNetworkAvailability(context, ApiProvider.ApiLoadDashboard)) {
            val params = JsonObject()
            params.addProperty(ApiConstants.VolunteerId, dataManager.getUserId().toInt())
            params.addProperty(ApiConstants.LastId, factory.getKey())
            params.addProperty(ApiConstants.RequestedData, config.pageSize)
            dataManager.getCallDetails(params).doOnSubscribe {
                updateNetworkState(NetworkRequestState.LoadingData(ApiProvider.ApiLoadDashboard))
            }.subscribe({
                try {
                    factory.create()
                    if (it.status == "0") {
                        viewModelScope.launch {
                            val data = it.getData()
                            //dataManager.clearPreviousData()
                            val dataList = data.callsList
                            if (dataList.size < config.pageSize) isAllDataLoaded = true
                            storeData(dataList)
                            //callback?.onResult(dataList)

                            updateNetworkState(
                                NetworkRequestState.SuccessResponse(
                                    ApiProvider.ApiLoadDashboard, it
                                )
                            )
                        }
                    } else updateNetworkState(NetworkRequestState.ErrorResponse(ApiProvider.ApiLoadDashboard))
                } catch (e: Exception) {
                    println("$TAG ${e.message}")
                }
            }, {
                updateNetworkState(
                    NetworkRequestState.ErrorResponse(ApiProvider.ApiLoadDashboard, it)
                )
            }).autoDispose(disposables)
        }
    }
}