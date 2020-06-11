package com.nitiaayog.apnesaathi.preferences

interface PreferenceRequest {

    fun isLogin(): Boolean

    fun getUserId(): String
    fun setUserId(userId: String)

    fun getUserName(): String
    fun setUserName(userName: String)

    fun getProfileImage(): String
    fun setProfileImage(profileImage: String)

    fun getPhoneNumber(): String
    fun setPhoneNumber(phoneNumber: String)

    fun getSelectedLaungage(): String
    fun setSelectedLaungage(selectedlanguage: String)
}