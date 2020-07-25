package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.profileupdate

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProfileUpdateResponse {
    @SerializedName("statusCode")
    @Expose
    var statusCode:String?=null
    @SerializedName("message")
    @Expose
    var message:String?=null
    @SerializedName("volunteerId")
    @Expose
    var volunteerId:Any?=null
    @SerializedName("idgrevance")
    @Expose
    var idgrevance:Any?=null
    @SerializedName("volunteer")
    @Expose
    var volunteer:Any?=null
    @SerializedName("volunteerassignment")
    @Expose
    var volunteerassignment:Any?=null
    @SerializedName("medicalandgreivance")
    @Expose
    var medicalandgreivance:Any?=null
    @SerializedName("greivanceTrackingList")
    @Expose
    var greivanceTrackingList:Any?=null
    @SerializedName("loginOTP")
    @Expose
    var loginOTP:Any?=null
}