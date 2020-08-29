package com.nitiaayog.apnesaathi.paging.allcalls

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.paging.ItemKeyedDataSource
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.ui.fragments.calls.allcalls.AllCallsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CallsKeyedSource(private val context: Context, private val viewModel: AllCallsViewModel) :
    ItemKeyedDataSource<Int, CallData>() {

    companion object {
        private val TAG: String = "TAG -- ${CallsKeyedSource::class.java.simpleName} -->"
    }

    private var lastItemKey: Int = 0

    private val dataManager: DataManager by lazy { viewModel.getDataManager() }

    /**
     * When first we will load data this method will be called i.e. only first time when object
     * will be created
     * */
    override fun loadInitial(
        params: LoadInitialParams<Int>, callback: LoadInitialCallback<CallData>
    ) {
        println("$TAG loadInitial called")
        println("$TAG loadInitial --> Key = ${params.requestedInitialKey}, RequestedSize = ${params.requestedLoadSize}")
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            val dataList: List<CallData> = dataManager.getCalls(params.requestedLoadSize)
            callback.onResult(dataList, lastItemKey, dataList.size)
        }
    }

    /**
     * Except first time for loading more data on scroll to bottom this method will be called and
     * if key will be null then it will stop load more
     * */
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<CallData>) {
        println("$TAG loadAfter called")
        println("$TAG loadInitial --> Key = ${params.key}, RequestedSize = ${params.requestedLoadSize}")
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            val dataList: List<CallData> =
                dataManager.getCallsAfter(params.key, params.requestedLoadSize)
            if (dataList.isNotEmpty()) {
                //println("$TAG last_key -- loadAfter --> ${dataList[dataList.size - 1].callId} : ${dataList[dataList.size - 1].loggedDateTime}")
                callback.onResult(dataList)
            } else viewModel.getCallDetails(context, callback)
        }
    }

    /**
     * This method will provide item it that we will use as Key.
     * */
    override fun getKey(item: CallData): Int {
        lastItemKey = item.callId!!
        println("$TAG last_key : $lastItemKey")
        return lastItemKey
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<CallData>) {

    }

    fun getLastKey(): Int = lastItemKey
}