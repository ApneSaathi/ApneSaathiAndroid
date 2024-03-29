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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Class holds the data of Volunteers assigned to Master Admin and Staff Member
 * */
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

    /**
     * Every time when before inserting new volunteers list we should delete the previous list
     * so that if there is any addition/deletion in list of volunteers, can be visible to Ui
     * */
    private suspend fun insertVolunteers(volunteers: MutableList<Volunteer>) {
        try {
            dataManager.deleteVolunteers()
            dataManager.insertVolunteers(volunteers)
        } catch (e: Exception) {
            println("$TAG ${e.message}")
        }
    }

    /**
     * Fetch the data of Volunteers currently assigned to Master Admin/Staff Members
     * */
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

    /**
     * Will return Volunteer object from the id.
     * */
    suspend fun getVolunteer(id: Int): Volunteer? {
        return dataManager.getVolunteer(id)
    }

    fun getNetworkStream(): LiveData<NetworkRequestState> {
        return loaderObservable
    }

    fun getVolunteersStream(): LiveData<PagedList<Volunteer>> {
        return volunteers
    }

    /**
     * Update volunteer ratings
     * */
    fun updateVolunteerRating(ratings: String, id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dataManager.updateVolunteerRatings(ratings, id)
            } catch (e: Exception) {
                println("$TAG ${e.message}")
            }
        }
    }
}