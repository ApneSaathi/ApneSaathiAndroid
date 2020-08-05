package com.nitiaayog.apnesaathi.paging.volunteer

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.nitiaayog.apnesaathi.model.Volunteer
import com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.home.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VolunteerSourceFactory(context: Context, viewModel: HomeViewModel) :
    DataSource.Factory<Int, Volunteer>() {

    private val callsDataSource = MutableLiveData<VolunteersKeyedSource>()
    private val itemKeyedSource = VolunteersKeyedSource()

    override fun create(): DataSource<Int, Volunteer> {
        CoroutineScope(Dispatchers.Main).launch { callsDataSource.postValue(itemKeyedSource) }
        return itemKeyedSource
    }

    fun invalidateSource() = itemKeyedSource.invalidate()
    fun getKey(): Int = itemKeyedSource.getLastKey()
}