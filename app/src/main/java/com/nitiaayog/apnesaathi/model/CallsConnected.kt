package com.nitiaayog.apnesaathi.model

import com.google.gson.annotations.SerializedName

data class CallsConnected(
    @SerializedName("")
    private val mId: String? = "0",

    @SerializedName("")
    private val mSrCitizenName: String? = "",

    @SerializedName("")
    private val mSrCitizenPhoneNumber: String? = "",

    @SerializedName("")
    private val mSrCitizenState: String? = "",

    @SerializedName("")
    private val mSrCitizenDistrict: String? = "",

    @SerializedName("")
    private val mSrCitizenAge: String? = "",

    @SerializedName("")
    private val mIssues: String? = ""
) {

    val id: String = mId ?: "0"
    val name: String = mSrCitizenName ?: ""
    val phoneNumber: String = mSrCitizenPhoneNumber ?: ""
    val age: String = mSrCitizenAge ?: ""
    val district: String = mSrCitizenDistrict ?: ""
    val state: String = mSrCitizenState ?: ""
    val issues: String = mIssues ?: ""
}