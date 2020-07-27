package com.nitiaayog.apnesaathi.preferences

interface PreferenceRequest {

    fun isLogin(): Boolean

    fun getUserId(): String
    fun setUserId(userId: String)

    fun setGender(gender: String)
    fun getGender(): String

    fun getUserName(): String
    fun setUserName(userName: String)

    fun getProfileImage(): String
    fun setProfileImage(profileImage: String)

    fun getPhoneNumber(): String
    fun setPhoneNumber(phoneNumber: String)

    fun getSelectedLanguage(): String
    fun setSelectedLanguage(language: String)

    fun getFirstName(): String
    fun setFirstName(firstName: String)

    fun getLastName(): String
    fun setLastName(lastName: String)

    fun getEmail(): String
    fun setEmail(email: String)

    fun getAddress(): String
    fun setAddress(address: String)

    fun setLastSelectedId(callId: String)
    fun getLastSelectedId(): String
}