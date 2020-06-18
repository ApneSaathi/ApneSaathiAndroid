package com.nitiaayog.apnesaathi.model

data class DateItem(private val mDay: String? = "0", private val mMonth: String? = "0",private val mStatus:String?="") {
    val day: String = mDay ?: ""
    val month: String = mMonth ?: ""
    var status: String = mStatus?:""
}