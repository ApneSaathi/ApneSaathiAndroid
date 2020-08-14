package com.nitiaayog.apnesaathi.model

import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.ApiStatus

class AdminCallDetails : ApiStatus() {

    private val firstName: String? = ""
    private val lastName: String? = ""
    private val phoneNo: String? = ""
    private val email: String? = ""
    private val assignedtoFellow: String? = ""
    private val assignedtoFellowContact: String? = ""
    private val gender: String? = ""
    private val adminCallList: MutableList<CallData>? = mutableListOf()
    private val districtList: MutableList<DistrictDetails>? = mutableListOf()

    val volunteerContactNo: String = phoneNo ?: ""
    val fullName: String = (firstName ?: "").plus(" ").plus(lastName ?: "")
    val emailId: String = email ?: ""
    val volunteerGender: String = gender ?: ""
    val assignedToFellow: String = assignedtoFellow ?: ""
    val assignedToFellowContact: String = assignedtoFellowContact ?: ""
    val adminCallsList: MutableList<CallData>
        get() = adminCallList ?: mutableListOf()

    val adminDistrictList: MutableList<DistrictDetails>
        get() = districtList ?: mutableListOf()
}