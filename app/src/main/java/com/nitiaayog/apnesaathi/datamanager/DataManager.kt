package com.nitiaayog.apnesaathi.datamanager

import androidx.lifecycle.LiveData
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.model.SyncSrCitizenGrievance
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.ApiRequest
import com.nitiaayog.apnesaathi.preferences.PreferenceRequest

interface DataManager : ApiRequest, PreferenceRequest {
    fun updateUserPreference(loginUser: User)

    // TODO : Database Methods
    // Table : call_details
    fun insertCallData(callData: List<CallData>)
    fun getAllCallsList(): LiveData<MutableList<CallData>>
    fun getCallDetailFromId(id: Int): CallData

    // Table : grievances
    fun insertGrievances(grievances: List<SrCitizenGrievance>)
    fun getGrievances(): LiveData<MutableList<SrCitizenGrievance>>
    fun getGrievance(callId: Int): SrCitizenGrievance?
    fun update(grievance: SrCitizenGrievance)

    // Table : sync_grievances_data
    fun insert(syncData: SyncSrCitizenGrievance)
    fun delete(syncData: SyncSrCitizenGrievance)
}