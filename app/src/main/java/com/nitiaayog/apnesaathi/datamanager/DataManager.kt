package com.nitiaayog.apnesaathi.datamanager

import androidx.lifecycle.LiveData
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.ApiRequest
import com.nitiaayog.apnesaathi.preferences.PreferenceRequest

interface DataManager : ApiRequest, PreferenceRequest {
    fun updateUserPreference(loginUser: User)

    // TODO : Database Methods
    fun insertCallData(callData: List<CallData>)
    fun getAllCallsList(): LiveData<MutableList<CallData>>
    fun getCallDetailFromId(id: Int): CallData

    fun insertGrievances(grievances: List<SrCitizenGrievance>)
    fun getAllGrievances(): LiveData<MutableList<SrCitizenGrievance>>
}