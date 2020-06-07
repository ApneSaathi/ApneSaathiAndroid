package com.nitiaayog.apnesaathi.datamanager

import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.ApiRequest
import com.nitiaayog.apnesaathi.preferences.PreferenceRequest

interface DataManager : ApiRequest, PreferenceRequest {
    fun updateUserPreference(loginUser: User)
}