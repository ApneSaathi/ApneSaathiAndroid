package com.nitiaayog.apnesaathi.paging

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.paging.allcalls.CallsKeyedSource
import com.nitiaayog.apnesaathi.ui.fragments.calls.allcalls.AllCallsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CallsDataSourceFactory(context: Context, viewModel: AllCallsViewModel) :
    DataSource.Factory<Int, CallData>() {

    private val callsDataSource = MutableLiveData<CallsKeyedSource>()
    private val itemKeyedSource = CallsKeyedSource(context, viewModel)

    override fun create(): DataSource<Int, CallData> {
        CoroutineScope(Dispatchers.Main).launch { callsDataSource.value = itemKeyedSource }
        return itemKeyedSource
    }

    fun invalidateSource() = itemKeyedSource.invalidate()
    fun getKey(): Int = itemKeyedSource.getLastKey()
}