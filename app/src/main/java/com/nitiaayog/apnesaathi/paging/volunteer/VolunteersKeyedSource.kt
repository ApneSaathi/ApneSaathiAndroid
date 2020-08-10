package com.nitiaayog.apnesaathi.paging.volunteer

import androidx.paging.ItemKeyedDataSource
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.Volunteer

class VolunteersKeyedSource(private val dataManager: DataManager) :
    ItemKeyedDataSource<Int, Volunteer>() {

    companion object {
        private val TAG: String = "TAG -- ${VolunteersKeyedSource::class.java.simpleName} -->"
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>, callback: LoadInitialCallback<Volunteer>
    ) {
        println("$TAG loadInitial called")
        println("$TAG loadInitial --> Key = ${params.requestedInitialKey}, RequestedSize = ${params.requestedLoadSize}")
        val dataList: List<Volunteer> = dataManager.getVolunteers(0, 2)
        callback.onResult(dataList, 0, dataList.size)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Volunteer>) {
        println("$TAG loadAfter called")
        println("$TAG loadAfter --> Key = ${params.key}, RequestedSize = ${params.requestedLoadSize}")
        val dataList: List<Volunteer> =
            dataManager.getVolunteers(params.key, 20)
        callback.onResult(dataList)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Volunteer>) {
    }

    override fun getKey(item: Volunteer): Int {
        return item.id!!
    }
}