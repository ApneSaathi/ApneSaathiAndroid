package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce

import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

abstract class ApiStatus {

    @SerializedName(ApiConstants.StatusCode)
    var status: String? = ""
        get() = field ?: "0"
        set(value) {
            field = value ?: ""
        }

    var message: String? = ""
        get() = field ?: "0"
        set(value) {
            field = value ?: ""
        }
}