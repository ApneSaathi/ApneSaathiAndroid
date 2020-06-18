package com.nitiaayog.apnesaathi.model

data class Grievances(
    private val mId: String = "0",
    private val mComplaint: String = "",
    private val mStatus: String = ""
) {
    val id: String = mId
    val complaint: String = mComplaint
    val status: String = mStatus
}