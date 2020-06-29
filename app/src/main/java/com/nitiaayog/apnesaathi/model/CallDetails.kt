package com.nitiaayog.apnesaathi.model

import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.ApiStatus

class CallDetails : ApiStatus() {

    private val firstName: String? = ""
    private val lastName: String? = ""
    private val phoneNo: String? = ""
    private val email: String? = ""
    private val assignedtoFellow: String? = ""
    private val assignedtoFellowContact: String? = ""
    private val gender: String? = ""
    private val volunteercallList: MutableList<CallData>? = mutableListOf()

    val volunteerContactNo: String = phoneNo ?: ""
    val fullName: String = (firstName ?: "").plus(" ").plus(lastName ?: "")
    val emailId: String = email ?: ""
    val volunteerGender: String = gender ?: ""
    val assignedToFellow: String = assignedtoFellow ?: ""
    val assignedToFellowContact: String = assignedtoFellowContact ?: ""
    val callsList: MutableList<CallData>
        get() = volunteercallList ?: mutableListOf()
}