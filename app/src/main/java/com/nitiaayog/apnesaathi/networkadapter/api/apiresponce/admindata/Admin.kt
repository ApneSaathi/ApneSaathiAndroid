package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.admindata

import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

class Admin {
    @SerializedName(ApiConstants.AdminId)
    var adminId: Int? = -1
        get() = field ?: -1
        set(value) {
            field = value ?: -1
        }

    @SerializedName(ApiConstants.MobileNo)
    var phoneNo: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @SerializedName(ApiConstants.FirstName)
    var firstName: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @SerializedName(ApiConstants.LastName)
    var lastName: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @SerializedName(ApiConstants.ProfileEmail)
    var email: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @SerializedName(ApiConstants.Role)
    var role: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @SerializedName(ApiConstants.District)
    var district: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @SerializedName(ApiConstants.State)
    var state: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }
}