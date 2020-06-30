package com.nitiaayog.apnesaathi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.database.constants.DbConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

@Entity(tableName = DbConstants.Tables.TABLE_CALL_DETAILS)
class CallData {

    @PrimaryKey
    @ColumnInfo(name = DbConstants.Columns.Id, defaultValue = "-1")
    @SerializedName(ApiConstants.CallId)
    var callId: Int? = -1
        get() = field ?: -1
        set(value) {
            field = value ?: -1
        }

    @ColumnInfo(name = DbConstants.Columns.VolunteerId, defaultValue = "-1")
    @SerializedName(ApiConstants.VolunteerId)
    var volunteerId: Int? = -1
        get() = field ?: -1
        set(value) {
            field = value ?: -1
        }

    @ColumnInfo(name = DbConstants.Columns.Name, defaultValue = "")
    @SerializedName(ApiConstants.SrCitizenName)
    var srCitizenName: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.ContactNumber, defaultValue = "")
    @SerializedName(ApiConstants.SrCitizenContactNumber)
    var contactNumber: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.Age, defaultValue = "")
    @SerializedName(ApiConstants.SrCitizenAge)
    var age: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.Gender, defaultValue = "")
    @SerializedName(ApiConstants.SrCitizenGender)
    var gender: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.Address, defaultValue = "")
    @SerializedName(ApiConstants.SrCitizenAddress)
    var address: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.EmailId, defaultValue = "")
    @SerializedName(ApiConstants.SrCitizenEmailId)
    var emailId: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.State, defaultValue = "")
    @SerializedName(ApiConstants.SrCitizenState)
    var state: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.District, defaultValue = "")
    @SerializedName(ApiConstants.SrCitizenDistrict)
    var district: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.Block, defaultValue = "")
    @SerializedName(ApiConstants.SrCitizenBlock)
    var block: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.CallStatus, defaultValue = "")
    @SerializedName(ApiConstants.SrCitizenCallStatusCode)
    var callStatusCode: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.CallSubStatus, defaultValue = "")
    @SerializedName(ApiConstants.SrCitizenSubStatusCode)
    var callStatusSubCode: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.TalkedWith, defaultValue = "")
    @SerializedName(ApiConstants.SrCitizenTalkedWith)
    var talkedWith: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.Remark, defaultValue = "")
    @SerializedName(ApiConstants.Remark)
    var remarks: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    //@TypeConverters(MedicalGrievancesConverter::class)
    @Ignore
    @SerializedName(ApiConstants.MedicalGrievances)
    var medicalGrievance: MutableList<SrCitizenGrievance>? = mutableListOf()
        get() = field ?: mutableListOf()
        set(value) {
            field = value ?: mutableListOf()
        }
}