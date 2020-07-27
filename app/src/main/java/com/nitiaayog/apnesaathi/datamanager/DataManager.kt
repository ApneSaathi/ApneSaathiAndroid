package com.nitiaayog.apnesaathi.datamanager

import androidx.lifecycle.LiveData
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.GrievanceData
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.model.SyncSrCitizenGrievance
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.ApiRequest
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.loginresponse.Login_Response
import com.nitiaayog.apnesaathi.preferences.PreferenceRequest

interface DataManager : ApiRequest, PreferenceRequest {
    fun updateUserPreference(loginUser: Login_Response)

    // TODO : Database Access
    // Table : call_details
    fun getPendingCallsList(): LiveData<MutableList<CallData>>
    fun getFollowupCallsList(): LiveData<MutableList<CallData>>
    fun getCompletedCallsList(): LiveData<MutableList<CallData>>
    fun getAllCallsList(): LiveData<MutableList<CallData>>
    fun getInvalidCallsList(): LiveData<MutableList<CallData>>
    fun getCalls(requestedItems: Int): List<CallData>
    fun getCallsAfter(itemKey: Int, requestedItems: Int): List<CallData>

    //fun getMinId(): Int
    fun insertCallData(callData: List<CallData>)
    fun insertGrievanceTrackingList(grievanceTracking: List<GrievanceData>)
    fun getCallDetailFromId(id: Int): CallData
    fun updateCallStatus(callStatus: String)
    fun updateCallData(callData: CallData): Long

    // Table : grievances
    fun insertGrievance(grievance: SrCitizenGrievance): Long
    fun insertGrievances(grievances: List<SrCitizenGrievance>)
    fun getGrievances(): LiveData<MutableList<SrCitizenGrievance>>
    fun getGrievance(callId: Int): SrCitizenGrievance?
    fun isDataExist(id: Int, callId: Int): SrCitizenGrievance?
    fun deleteGrievance(grievance: SrCitizenGrievance)
    suspend fun updateGrievance(grievance: SrCitizenGrievance)

    // Table : grievance_Tracking
    fun getAllTrackingGrievances(): LiveData<MutableList<GrievanceData>>
    fun getPendingGrievances(): LiveData<MutableList<GrievanceData>>
    fun getInProgressGrievances(): LiveData<MutableList<GrievanceData>>
    fun getResolvedGrievances(): LiveData<MutableList<GrievanceData>>
    fun clearPreviousTrackingData()

    // Table : sync_grievances_data
    fun getGrievancesToSync(): List<SyncSrCitizenGrievance>?
    suspend fun insertSyncGrievance(syncData: SyncSrCitizenGrievance)
    fun delete(syncData: SyncSrCitizenGrievance)
    fun getAllUniqueGrievances(callId: Int): LiveData<MutableList<SrCitizenGrievance>>
    fun clearPreviousData()
}