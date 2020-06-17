package com.nitiaayog.apnesaathi.model

import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

data class User(

    @SerializedName(ApiConstants.UserId)
    private val mUserId: String? = "",

    @SerializedName(ApiConstants.UserName)
    private val mUserName: String? = "",

    @SerializedName(ApiConstants.Block)
    private val mBlock: String? = "",

    @SerializedName(ApiConstants.District)
    private val mDistrict: String? = "",

    @SerializedName(ApiConstants.State)
    private val mState: String? = "",

    @SerializedName(ApiConstants.Gender)
    private val mGender: String? = "",

    @SerializedName(ApiConstants.PhoneNumber)
    private val mPhoneNumber: String? = ""
) {
    val userId: String = mUserId ?: ""
    val userName: String = mUserName ?: ""
    val state: String = mState ?: ""
    val district: String = mDistrict ?: ""
    val block: String = mBlock ?: ""
    val gender: String = mGender ?: ""
    val phoneNumber: String = mPhoneNumber ?: ""
}