package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce

import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.model.AdminCallDetails
import com.nitiaayog.apnesaathi.model.CallDetails
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

class HomeRepo(

    @SerializedName(ApiConstants.Volunteer)
    private val volunteerDetails: CallDetails? = CallDetails(),

    @SerializedName(ApiConstants.Admin)
    private val adminDetails: AdminCallDetails? = AdminCallDetails()

) : ApiStatus() {

    fun getData(): CallDetails = volunteerDetails ?: CallDetails()
    fun getAdminData(): AdminCallDetails = adminDetails ?: AdminCallDetails()
}