package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce

import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

class SeniorCitizenRepo : ApiStatus() {

    @SerializedName(ApiConstants.SeniorCitizensList)
    private val srCitizensList: MutableList<CallData>? = mutableListOf()

    fun getSeniorCitizens(): MutableList<CallData> {
        return srCitizensList ?: mutableListOf()
    }
}