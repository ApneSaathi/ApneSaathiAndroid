package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.volunteerdata

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.admindata.Admin

class VolunteerDataResponse {
    @SerializedName("statusCode")
    @Expose
    var statusCode: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("volunteerId")
    @Expose
    var volunteerId: Any? = null

    @SerializedName("idgrevance")
    @Expose
    var idgrevance: Any? = null

    @SerializedName("volunteer")
    @Expose
    var volunteer: Volunteer? =
        null

    @SerializedName("admin")
    @Expose
    val admin: Admin? = null

    @SerializedName("volunteerassignment")
    @Expose
    var volunteerassignment: Any? = null

    @SerializedName("medicalandgreivance")
    @Expose
    var medicalandgreivance: Any? = null

    @SerializedName("loginOTP")
    @Expose
    var loginOTP: Any? = null

}