package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.emergencycontact

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EmergencyContactsList {
    @SerializedName("emergencyId")
    @Expose
    var emergencyId: Int? = null

    @SerializedName("districtId")
    @Expose
    var districtId: Int? = null

    @SerializedName("hospital")
    @Expose
    var hospital: String? = null

    @SerializedName("police")
    @Expose
    var police: String? = null

    @SerializedName("ambulance")
    @Expose
    var ambulance: String? = null

    @SerializedName("covidCtrlRoom")
    @Expose
    var covidCtrlRoom: String? = null

    @SerializedName("apnisathiContact")
    @Expose
    var apnisathiContact: String? = null

    @SerializedName("consultantName")
    @Expose
    var consultantName: String? = null

    @SerializedName("customeContact")
    @Expose
    var customeContact: String? = null

    @SerializedName("hospitalName")
    @Expose
    var hospitalName: String? = null

    @SerializedName("policeRegion")
    @Expose
    var policeRegion: String? = null

    @SerializedName("ctrlRoomRegion")
    @Expose
    var ctrlRoomRegion: String? = null

    @SerializedName("contactName")
    @Expose
    var contactName: String? = null
}