package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.volunteers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.Volunteer
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class VolunteersViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {

        private val TAG: String = "TAG -- ${VolunteersViewModel::class.java.simpleName} -->"

        @Volatile
        private var instance: VolunteersViewModel? = null

        @Synchronized
        fun getInstance(dataManager: DataManager): VolunteersViewModel {
            return instance ?: synchronized(this) {
                VolunteersViewModel(dataManager).also { instance = it }
            }
        }
    }

    private val volunteers: LiveData<PagedList<Volunteer>> by lazy {
        LivePagedListBuilder(/*factory*/dataManager.getVolunteersList(), config).build()
    }

    override fun onCleared() {
        //factory.invalidateSource()
        instance?.let { instance = null }
        super.onCleared()
    }

    private suspend fun insertVolunteers(volunteers: MutableList<Volunteer>) {
        try {
            dataManager.deleteVolunteers()
            dataManager.insertVolunteers(volunteers)
        } catch (e: Exception) {
            println("$TAG ${e.message}")
        }
    }

    private suspend fun getVolunteers() {
        val params = JsonObject()
        params.addProperty(ApiConstants.AdminId, dataManager.getUserId().toInt())
        dataManager.getVolunteers(params).doOnSubscribe {
            updateNetworkState(NetworkRequestState.LoadingData(ApiProvider.ApiGetVolunteers))
        }.subscribe({
            viewModelScope.launch {
                if (it.status == "0") {
                    io {
                        val volunteersList = it.getVolunteers()
                        insertVolunteers(volunteersList)
                        volunteers.value!!.dataSource.invalidate()
                    }
                    updateNetworkState(
                        NetworkRequestState.SuccessResponse(
                            ApiProvider.ApiGetVolunteers, it.getVolunteers()
                        )
                    )
                } else updateNetworkState(
                    NetworkRequestState.Error(ApiProvider.ApiGetVolunteers)
                )
            }
        }, {
            updateNetworkState(
                NetworkRequestState.ErrorResponse(ApiProvider.ApiGetVolunteers, it)
            )
        }).autoDispose(disposables)
    }

    suspend fun getVolunteersList(context: Context) {
        viewModelScope.launch {
            if (checkNetworkAvailability(context, ApiProvider.ApiGetVolunteers)) {
                getVolunteers()
            }
        }
    }

    suspend fun getVolunteer(id: Int): Volunteer? {
        return dataManager.getVolunteer(id)
    }

    fun getNetworkStream(): LiveData<NetworkRequestState> {
        return loaderObservable
    }

    fun getVolunteersStream(): LiveData<PagedList<Volunteer>> {
        return volunteers
    }
}