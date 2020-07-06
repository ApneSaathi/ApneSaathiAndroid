package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce

import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

open class BaseRepo : ApiStatus() {

    @SerializedName(ApiConstants.GrievanceId)
    private val id: String? = ""

    val grievanceId: String = id ?: ""
}