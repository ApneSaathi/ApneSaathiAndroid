package com.nitiaayog.apnesaathi.paging.volunteer

import androidx.paging.ItemKeyedDataSource
import com.nitiaayog.apnesaathi.model.Volunteer

class VolunteersKeyedSource : ItemKeyedDataSource<Int, Volunteer>() {

    companion object {
        private val TAG: String = "TAG -- ${VolunteersKeyedSource::class.java.simpleName} -->"
    }

    private var lastItemKey: Int = 0

    override fun loadInitial(
        params: LoadInitialParams<Int>, callback: LoadInitialCallback<Volunteer>
    ) {
        TODO("Not yet implemented")
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Volunteer>) {
        TODO("Not yet implemented")
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Volunteer>) {
    }

    override fun getKey(item: Volunteer): Int {
        return item.id!!
    }

    fun getLastKey(): Int {
        return lastItemKey
    }
}