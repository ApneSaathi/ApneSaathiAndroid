package com.nitiaayog.apnesaathi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.nitiaayog.apnesaathi.database.constants.Columns
import com.nitiaayog.apnesaathi.database.constants.Tables

@Entity(tableName = Tables.TABLE_SYNC_GRIEVANCES)
class SyncSrCitizenGrievance : SrCitizenGrievance() {

    @ColumnInfo(name = Columns.TalkedWith, defaultValue = "-1")
    var talkedWith: String = ""

    @ColumnInfo(name = Columns.CallSubStatus, defaultValue = "-1")
    var callStatusSubCode: String = ""

    @ColumnInfo(name = Columns.IsSynced, defaultValue = "0")
    var isSynced: Boolean = false
}