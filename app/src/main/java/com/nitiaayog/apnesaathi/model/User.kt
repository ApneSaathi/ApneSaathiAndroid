package com.nitiaayog.apnesaathi.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(

    @SerializedName(ApiConstants.UserId)
    private val mUserId: String? = "",

    @SerializedName(ApiConstants.UserName)
    private val mUserName: String? = "",

    @SerializedName(ApiConstants.Age)
    private val mAge: String? = "",

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
) : Parcelable {
    @IgnoredOnParcel
    val userId: String = mUserId ?: ""

    @IgnoredOnParcel
    val userName: String = mUserName ?: ""

    @IgnoredOnParcel
    val age: String = mAge ?: ""

    @IgnoredOnParcel
    val state: String = mState ?: ""

    @IgnoredOnParcel
    val district: String = mDistrict ?: ""

    @IgnoredOnParcel
    val block: String = mBlock ?: ""

    @IgnoredOnParcel
    val gender: String = mGender ?: ""

    @IgnoredOnParcel
    val phoneNumber: String = mPhoneNumber ?: ""
}