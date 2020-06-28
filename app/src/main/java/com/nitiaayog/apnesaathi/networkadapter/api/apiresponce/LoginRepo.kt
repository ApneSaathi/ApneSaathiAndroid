package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginRepo() {

    @SerializedName("statusCode")
    @Expose
    var statusCode: String = ""

    @SerializedName("message")
    @Expose
    var message: String = ""

}