package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce

import com.nitiaayog.apnesaathi.model.Volunteer

class VolunteerRepo : ApiStatus() {

    private var _volunteerList: MutableList<Volunteer>? = mutableListOf()

    val volunteerList: MutableList<Volunteer> = _volunteerList ?: mutableListOf()
}