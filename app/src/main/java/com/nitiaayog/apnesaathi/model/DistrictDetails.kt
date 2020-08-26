package com.nitiaayog.apnesaathi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.database.constants.Columns
import com.nitiaayog.apnesaathi.database.constants.Tables
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

@Entity(tableName = Tables.TABLE_DISTRICT)
class DistrictDetails {

    @PrimaryKey
    @ColumnInfo(name = Columns.Id, defaultValue = "-1")
    @SerializedName(ApiConstants.DistrictId)
    var districtId: Int? = -1
        get() = field ?: -1
        set(value) {
            field = value ?: -1
        }

    @ColumnInfo(name = Columns.State, defaultValue = "")
    @SerializedName(ApiConstants.State)
    var state: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.District, defaultValue = "")
    @SerializedName(ApiConstants.DistrictName)
    var districtName: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.AdminId, defaultValue = "")
    @SerializedName(ApiConstants.AdminId)
    var adminId: Int? = -1
        get() = field ?: -1
        set(value) {
            field = value ?: -1
        }



}