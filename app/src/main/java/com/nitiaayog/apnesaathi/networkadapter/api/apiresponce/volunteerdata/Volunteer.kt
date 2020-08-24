package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.volunteerdata

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Volunteer {
    @SerializedName("idvolunteer")
    @Expose
    var idvolunteer: Int? = null

    @SerializedName("phoneNo")
    @Expose
    var phoneNo: String? = null

    @SerializedName("firstName")
    @Expose
    var firstName: String? = null

    @SerializedName("lastName")
    @Expose
    var lastName: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @SerializedName("assignedtoFellow")
    @Expose
    var assignedtoFellow: String? = null

    @SerializedName("assignedtoFellowContact")
    @Expose
    var assignedtoFellowContact: String? = null

    @SerializedName("volunteercallList")
    @Expose
    var volunteercallList: List<VolunteercallList>? = null

    @SerializedName("district")
    @Expose
    var district: String? = null

    @SerializedName("block")
    @Expose
    var block: String? = null

    @SerializedName("village")
    @Expose
    var village: Any? = null

    @SerializedName("state")
    @Expose
    var state: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

}