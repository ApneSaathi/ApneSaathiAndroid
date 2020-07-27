package com.nitiaayog.apnesaathi.preferences

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getString
import com.nitiaayog.apnesaathi.base.extensions.putString

open class PreferenceManager private constructor(application: Application) : PreferenceRequest {

    companion object {
        @Volatile
        private var instance: PreferenceManager? = null

        @Synchronized
        fun getPreferenceRequest(application: Application): PreferenceManager =
            instance ?: synchronized(this) {
                PreferenceManager(application).also { instance = it }
            }
    }

    private val preferences: SharedPreferences by lazy {
        application.getSharedPreferences(
            application.getString(R.string.app_name), Context.MODE_PRIVATE
        )
    }

    override fun isLogin(): Boolean = getPhoneNumber() != ""

    override fun getUserId(): String = preferences.getString(PreferenceConstants.UserId)!!
    override fun setUserId(userId: String) =
        preferences.putString(PreferenceConstants.UserId, userId)

    override fun setGender(gender: String) =
        preferences.putString(PreferenceConstants.Gender, gender)

    override fun getGender(): String = preferences.getString(PreferenceConstants.Gender)!!

    override fun getUserName(): String = preferences.getString(PreferenceConstants.UserName)!!
    override fun setUserName(userName: String) =
        preferences.putString(PreferenceConstants.UserName, userName)

    override fun getProfileImage(): String =
        preferences.getString(PreferenceConstants.ProfileImage)!!

    override fun setProfileImage(profileImage: String) =
        preferences.putString(PreferenceConstants.ProfileImage, profileImage)

    override fun getPhoneNumber(): String = preferences.getString(PreferenceConstants.PhoneNumber)!!
    override fun setPhoneNumber(phoneNumber: String) =
        preferences.putString(PreferenceConstants.PhoneNumber, phoneNumber)

    override fun getSelectedLanguage(): String =
        preferences.getString(PreferenceConstants.SelectedLanguage)!!

    override fun setSelectedLanguage(language: String) =
        preferences.putString(PreferenceConstants.SelectedLanguage, language)

    override fun getFirstName(): String = preferences.getString(PreferenceConstants.FirstName)!!
    override fun setFirstName(firstName: String) =
        preferences.putString(PreferenceConstants.FirstName, firstName)

    override fun getLastName(): String = preferences.getString(PreferenceConstants.LastName)!!
    override fun setLastName(lastName: String) =
        preferences.putString(PreferenceConstants.LastName, lastName)

    override fun getEmail(): String = preferences.getString(PreferenceConstants.Email)!!
    override fun setEmail(email: String) = preferences.putString(PreferenceConstants.Email, email)

    override fun getAddress(): String = preferences.getString(PreferenceConstants.Address)!!
    override fun setAddress(address: String) =
        preferences.putString(PreferenceConstants.Address, address)

    override fun setLastSelectedId(callId: String) =
        preferences.putString(PreferenceConstants.LastSelectedId, callId)

    override fun getLastSelectedId(): String =
        preferences.getString(PreferenceConstants.LastSelectedId)!!

    override fun clearPreferences() = preferences.edit().clear().apply()
}