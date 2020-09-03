package com.nitiaayog.apnesaathi.preferences

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getString
import com.nitiaayog.apnesaathi.base.extensions.putString

/**
 * [PreferenceManager] class for storing data in shared preferences
 */
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

    /**
     * Method for initialize the sharedPreference
     */
    private val preferences: SharedPreferences by lazy {
        application.getSharedPreferences(
            application.getString(R.string.app_name), Context.MODE_PRIVATE
        )
    }

    /**
     * Method for initialize the sharedPreference
     */
    override fun isLogin(): Boolean =
        ((getUserId() != "") && (getPhoneNumber() != "") && (getRole() != ""))

    /**
     * Methods for storing and getting userId in shared preference
     */
    override fun getUserId(): String = preferences.getString(PreferenceConstants.UserId)!!
    override fun setUserId(userId: String) =
        preferences.putString(PreferenceConstants.UserId, userId)

    /**
     * Methods for storing and getting user gender in shared preference
     */
    override fun setGender(gender: String) =
        preferences.putString(PreferenceConstants.Gender, gender)

    override fun getGender(): String = preferences.getString(PreferenceConstants.Gender)!!

    /**
     * Methods for storing and getting Selected DistrictId in shared preference
     */
    override fun setSelectedDistrictId(id: String) =
        preferences.putString(PreferenceConstants.DistrictId, id)

    override fun getSelectedDistrictId(): String =
        preferences.getString(PreferenceConstants.DistrictId)!!

    /**
     * Methods for storing and getting SrCitizenGender gender in shared preference
     */
    override fun setSrCitizenGender(gender: String) =
        preferences.putString(PreferenceConstants.SrCitizenGender, gender)

    override fun getSrCitizenGender(): String =
        preferences.getString(PreferenceConstants.SrCitizenGender)!!

    /**
     * Methods for storing and getting users UserName in shared preference
     */
    override fun getUserName(): String = preferences.getString(PreferenceConstants.UserName)!!
    override fun setUserName(userName: String) =
        preferences.putString(PreferenceConstants.UserName, userName)

    /**
     * Methods for storing and getting users ProfileImage in shared preference
     * but right now not user this method
     */
    override fun getProfileImage(): String =
        preferences.getString(PreferenceConstants.ProfileImage)!!

    override fun setProfileImage(profileImage: String) =
        preferences.putString(PreferenceConstants.ProfileImage, profileImage)

    /**
     * Methods for storing and getting PhoneNumber of user in shared preference
     */
    override fun getPhoneNumber(): String = preferences.getString(PreferenceConstants.PhoneNumber)!!
    override fun setPhoneNumber(phoneNumber: String) =
        preferences.putString(PreferenceConstants.PhoneNumber, phoneNumber)

    /**
     * Methods for storing and getting SelectedLanguage of user in shared preference
     */
    override fun getSelectedLanguage(): String =
        preferences.getString(PreferenceConstants.SelectedLanguage)!!

    override fun setSelectedLanguage(language: String) =
        preferences.putString(PreferenceConstants.SelectedLanguage, language)

    /**
     * Methods for storing and getting FirstName of user in shared preference
     */
    override fun getFirstName(): String = preferences.getString(PreferenceConstants.FirstName)!!
    override fun setFirstName(firstName: String) =
        preferences.putString(PreferenceConstants.FirstName, firstName)

    /**
     * Methods for storing and getting LastName of user in shared preference
     */
    override fun getLastName(): String = preferences.getString(PreferenceConstants.LastName)!!
    override fun setLastName(lastName: String) =
        preferences.putString(PreferenceConstants.LastName, lastName)

    /**
     * Methods for storing and getting Emailid of user in shared preference
     */
    override fun getEmail(): String = preferences.getString(PreferenceConstants.Email)!!
    override fun setEmail(email: String) = preferences.putString(PreferenceConstants.Email, email)

    /**
     * Methods for storing and getting address of user in shared preference
     */
    override fun getAddress(): String = preferences.getString(PreferenceConstants.Address)!!
    override fun setAddress(address: String) =
        preferences.putString(PreferenceConstants.Address, address)

    /**
     * Methods for storing and getting district of shared preference
     */
    override fun getDistrict(): String = preferences.getString(PreferenceConstants.District)!!
    override fun setDistrict(district: String) =
        preferences.putString(PreferenceConstants.District, district)

    /**
     * Methods for storing and getting State in shared preference
     */
    override fun getState(): String = preferences.getString(PreferenceConstants.State)!!

    override fun setState(state: String) =
        preferences.putString(PreferenceConstants.State, state)

    override fun setLastSelectedId(callId: String) =
        preferences.putString(PreferenceConstants.LastSelectedId, callId)

    override fun getLastSelectedId(): String =
        preferences.getString(PreferenceConstants.LastSelectedId)!!

    /**
     * Methods for storing and getting Role of user in shared preference
     */
    override fun setRole(role: String) = preferences.putString(PreferenceConstants.Role, role)
    override fun getRole(): String = preferences.getString(PreferenceConstants.Role)!!

    /**
     * Methods for use to clear all the data in sharedPreference.
     */
    override fun clearPreferences() {
        preferences.edit().clear().apply()
    }
}