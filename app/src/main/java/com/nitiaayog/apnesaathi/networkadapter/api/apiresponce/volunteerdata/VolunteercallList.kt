package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.volunteerdata

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VolunteercallList {
    @SerializedName("callid")
    @Expose
    var callid: Int? = null

    @SerializedName("idvolunteer")
    @Expose
    var idvolunteer: Int? = null

    @SerializedName("namesrcitizen")
    @Expose
    var namesrcitizen: String? = null

    @SerializedName("phonenosrcitizen")
    @Expose
    var phonenosrcitizen: String? = null

    @SerializedName("agesrcitizen")
    @Expose
    var agesrcitizen: Int? = null

    @SerializedName("gendersrcitizen")
    @Expose
    var gendersrcitizen: String? = null

    @SerializedName("addresssrcitizen")
    @Expose
    var addresssrcitizen: String? = null

    @SerializedName("emailsrcitizen")
    @Expose
    var emailsrcitizen: String? = null

    @SerializedName("statesrcitizen")
    @Expose
    var statesrcitizen: String? = null

    @SerializedName("districtsrcitizen")
    @Expose
    var districtsrcitizen: String? = null

    @SerializedName("blocknamesrcitizen")
    @Expose
    var blocknamesrcitizen: String? = null

    @SerializedName("villagesrcitizen")
    @Expose
    var villagesrcitizen: String? = null

    @SerializedName("callstatusCode")
    @Expose
    var callstatusCode: Int? = null

    @SerializedName("callstatussubCode")
    @Expose
    var callstatussubCode: Int? = null

    @SerializedName("talkedwith")
    @Expose
    var talkedwith: String? = null

    @SerializedName("assignedbyMember")
    @Expose
    var assignedbyMember: Any? = null

    @SerializedName("remarks")
    @Expose
    var remarks: Any? = null

    @SerializedName("loggeddateTime")
    @Expose
    var loggeddateTime: String? = null

    @SerializedName("medicalandgreivance")
    @Expose
    var medicalandgreivance: List<Medicalandgreivance>? = null

}