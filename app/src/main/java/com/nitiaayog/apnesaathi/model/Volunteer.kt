package com.nitiaayog.apnesaathi.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.database.constants.Columns
import com.nitiaayog.apnesaathi.database.constants.Tables
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

@Entity(tableName = Tables.TABLE_VOLUNTEERS)
open class Volunteer {

    @PrimaryKey
    @ColumnInfo(name = Columns.Id, defaultValue = "0")
    @SerializedName(ApiConstants.VolunteerId)
    var id: Int? = -1
        get() = field ?: -1
        set(@NonNull value) {
            field = value ?: -1
        }

    @ColumnInfo(name = Columns.FirstName, defaultValue = "")
    @SerializedName(ApiConstants.FirstName)
    var firstName: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.LastName, defaultValue = "")
    @SerializedName(ApiConstants.LastName)
    var lastName: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.ContactNumber, defaultValue = "")
    @SerializedName(ApiConstants.PhoneNumber)
    var phoneNumber: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.EmailId, defaultValue = "")
    @SerializedName(ApiConstants.ProfileEmail)
    var emailId: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.Gender, defaultValue = "")
    @SerializedName(ApiConstants.Gender)
    var gender: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.Ratings, defaultValue = "0.0")
    @SerializedName(ApiConstants.Ratings)
    var ratings: String? = "0.0"
        get() = field ?: "0.0"
        set(@NonNull value) {
            field = value ?: "0.0"
        }

    @ColumnInfo(name = Columns.Address, defaultValue = "")
    @SerializedName(ApiConstants.Address)
    var address: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.ProfilePic, defaultValue = "")
    @SerializedName(ApiConstants.ProfilePic)
    var profilePic: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.Role, defaultValue = "")
    @SerializedName(ApiConstants.Role)
    var role: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.Block, defaultValue = "")
    @SerializedName(ApiConstants.Block)
    var block: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.Village, defaultValue = "")
    @SerializedName(ApiConstants.Village)
    var village: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.District, defaultValue = "")
    @SerializedName(ApiConstants.District)
    var district: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.State, defaultValue = "")
    @SerializedName(ApiConstants.State)
    var state: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.AssessmentScore, defaultValue = "")
    @SerializedName(ApiConstants.AssessmentScore)
    var assessmentScore: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.JoiningDate, defaultValue = "")
    @SerializedName(ApiConstants.JoiningDate)
    var joiningDate: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    override fun equals(other: Any?): Boolean {
        if ((other != null) && (other is Volunteer))
            return ((id == other.id) && (firstName == other.firstName)
                    && (lastName == other.lastName) && (phoneNumber == other.phoneNumber)
                    && (gender == other.gender) && (address == other.address)
                    && (emailId == other.emailId) && (district == other.district)
                    && (state == other.state))

        return false
    }
}