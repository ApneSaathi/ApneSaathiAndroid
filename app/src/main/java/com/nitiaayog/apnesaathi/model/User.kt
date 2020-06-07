package com.nitiaayog.apnesaathi.model

import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

data class User(

    @SerializedName(ApiConstants.UserId)
    private val mUserId: String? = "",

    @SerializedName(ApiConstants.UserName)
    private val mUserName: String? = "",

    @SerializedName(ApiConstants.ImageUrl)
    private val mProfileImage: String? = "",

    @SerializedName(ApiConstants.PhoneNumber)
    private val mPhoneNumber: String? = ""
) {
    val userId: String = mUserId ?: ""
    val userName: String = mUserName ?: ""
    val userProfileImage: String = mProfileImage ?: ""
    val phoneNumber: String = mPhoneNumber ?: ""
}