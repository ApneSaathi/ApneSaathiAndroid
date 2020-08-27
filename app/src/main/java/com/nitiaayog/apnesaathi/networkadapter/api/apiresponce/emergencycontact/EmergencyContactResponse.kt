package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.emergencycontact

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EmergencyContactResponse {
    @SerializedName("emergencyContactsList")
    @Expose
    var emergencyContactsList: List<EmergencyContactsList>? = null

    @SerializedName("statusCode")
    @Expose
    var statusCode: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

}