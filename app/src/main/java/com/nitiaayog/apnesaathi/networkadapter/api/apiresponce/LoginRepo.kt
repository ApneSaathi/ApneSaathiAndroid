package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce

import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

class LoginRepo(

    @SerializedName(ApiConstants.UserInfo)
    private val mUserInfo: User? = null

) : BaseRepo() {
    val userInfo: User = User()
}