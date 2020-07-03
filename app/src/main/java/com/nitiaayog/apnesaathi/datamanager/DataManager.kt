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
    fun updateCallStatus(callStatus: String)
    fun updateCallData(callData: CallData): Long

    // Table : grievances
    fun insertGrievance(grievance: SrCitizenGrievance): Long
    fun insertGrievances(grievances: List<SrCitizenGrievance>)
    fun getGrievances(): LiveData<MutableList<SrCitizenGrievance>>
    fun getGrievance(callId: Int): SrCitizenGrievance?
    suspend fun updateGrievance(grievance: SrCitizenGrievance)

    // Table : sync_grievances_data
    fun getGrievancesToSync(): List<SyncSrCitizenGrievance>?
    suspend fun insert(syncData: SyncSrCitizenGrievance)
    fun delete(syncData: SyncSrCitizenGrievance)
}