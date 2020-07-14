package com.nitiaayog.apnesaathi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.database.constants.Columns
import com.nitiaayog.apnesaathi.database.constants.Tables
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

@Entity(tableName = Tables.TABLE_GRIEVANCE_TRACKING)
class GrievanceData {

    @PrimaryKey
    @ColumnInfo(name = Columns.Id, defaultValue = "-1")
    @SerializedName(ApiConstants.GrievanceTrackingId)
    var trackingId: Int? = -1
        get() = field ?: -1
        set(value) {
            field = value ?: -1
        }
    @ColumnInfo(name = Columns.CallId, defaultValue = "-1")
    @SerializedName(ApiConstants.CallId)
    var callId: Int? = -1
        get() = field ?: -1
        set(value) {
            field = value ?: -1
        }
    @ColumnInfo(name = Columns.VolunteerId, defaultValue = "-1")
    @SerializedName(ApiConstants.VolunteerId)
    var volunteerId: Int? = -1
        get() = field ?: -1
        set(value) {
            field = value ?: -1
        }
    @ColumnInfo(name = Columns.GrievanceId, defaultValue = "-1")
    @SerializedName(ApiConstants.GrievanceId)
    var grievanceId: Int? = -1
        get() = field ?: -1
        set(value) {
            field = value ?: -1
        }

    @ColumnInfo(name = Columns.Name, defaultValue = "")
    @SerializedName(ApiConstants.SrCitizenName)
    var srCitizenName: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.Gender, defaultValue = "")
    @SerializedName(ApiConstants.SrCitizenGender)
    var gender: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.GrievanceType, defaultValue = "")
    @SerializedName(ApiConstants.GrievanceType)
    var grievanceType: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.GrievanceStatus, defaultValue = "")
    @SerializedName(ApiConstants.GrievanceStatus)
    var status: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.Description, defaultValue = "")
    @SerializedName(ApiConstants.Description)
    var description: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.CreatedDate, defaultValue = "")
    @SerializedName(ApiConstants.GrievanceCreatedDate)
    var createdDate: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.Priority, defaultValue = "")
    @SerializedName(ApiConstants.Priority)
    var priority: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.LastUpdateOn, defaultValue = "")
    @SerializedName(ApiConstants.LastUpdateOn)
    var lastUpdateOn: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }
}