package com.nitiaayog.apnesaathi.preferences



/**
 * [PreferenceRequest] interface for request to store data in shared preferences
 */
interface PreferenceRequest {

    fun isLogin(): Boolean

    fun getUserId(): String
    fun setUserId(userId: String)

    fun setGender(gender: String)
    fun getGender(): String

    fun setSelectedDistrictId(id: String)
    fun getSelectedDistrictId(): String

    fun setSrCitizenGender(gender: String)
    fun getSrCitizenGender(): String

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

    fun getDistrict(): String
    fun setDistrict(district: String)

    fun getState(): String
    fun setState(state: String)

    fun setLastSelectedId(callId: String)
    fun getLastSelectedId(): String

    fun setRole(role: String)
    fun getRole(): String

    fun clearPreferences()
}