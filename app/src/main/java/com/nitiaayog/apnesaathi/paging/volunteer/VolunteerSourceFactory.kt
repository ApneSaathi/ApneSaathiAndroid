package com.nitiaayog.apnesaathi.paging.volunteer

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.Volunteer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VolunteerSourceFactory(dataManager: DataManager) :
    DataSource.Factory<Int, Volunteer>() {

    private val volunteerSource = MutableLiveData<VolunteersKeyedSource>()
    private val itemKeyedSource = VolunteersKeyedSource(dataManager)

    override fun create(): DataSource<Int, Volunteer> {
        CoroutineScope(Dispatchers.Main).launch { volunteerSource.postValue(itemKeyedSource) }
        return itemKeyedSource
    }

    fun invalidateSource() = itemKeyedSource.invalidate()
}