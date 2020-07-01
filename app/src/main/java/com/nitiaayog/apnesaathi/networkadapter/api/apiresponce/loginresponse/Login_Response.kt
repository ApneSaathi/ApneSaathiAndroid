package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.loginresponse

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Login_Response {
    @SerializedName("statusCode")
    @Expose
     var statusCode:String=""
    @SerializedName("message")
    @Expose
    var message:String = ""
}