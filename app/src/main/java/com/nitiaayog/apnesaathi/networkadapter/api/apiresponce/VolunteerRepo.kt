package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce

import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.model.Volunteer
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

class VolunteerRepo : ApiStatus() {

    @SerializedName(ApiConstants.Volunteers)
    private var volunteerList: MutableList<Volunteer>? = mutableListOf()

    fun getVolunteers(): MutableList<Volunteer> {
        return if (volunteerList.isNullOrEmpty()) mutableListOf()
        else volunteerList!!
    }
}