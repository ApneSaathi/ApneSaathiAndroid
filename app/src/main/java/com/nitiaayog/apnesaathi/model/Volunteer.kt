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

    /*{
            "idvolunteer": 1001,
            "phoneNo": "56895",
            "adminId": 1,
            "firstName": "RAM",
            "lastName": "P",
            "email": "rs@dfg.com",
            "gender": "M",
            "address": "ADDRESS10",
            "assignedtoFellow": "Ashish",
            "assignedtoFellowContact": "56565656",
            "pic": null,
            "role": 1,
            "block": "BLOCK10",
            "district": "DISTRICT10",
            "village": "VILLAGE2",
            "state": "ANDHRA PRADESH"
        }*/

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
    @SerializedName(ApiConstants.UserName)
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

    @ColumnInfo(name = Columns.Gender, defaultValue = "")
    @SerializedName(ApiConstants.Gender)
    var gender: String? = null
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.Address, defaultValue = "")
    @SerializedName(ApiConstants.Address)
    var address: String? = null
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    override fun equals(other: Any?): Boolean {
        if ((other != null) && (other is Volunteer))
            return ((id == other.id) && (firstName == other.firstName)
                    && (lastName == other.lastName)
                    && (phoneNumber == other.phoneNumber) && (gender == other.gender)
                    && (gender == other.gender) && (address == other.address))

        return false
    }
}