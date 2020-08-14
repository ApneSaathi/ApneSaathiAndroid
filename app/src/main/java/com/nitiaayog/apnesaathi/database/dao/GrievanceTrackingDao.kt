package com.nitiaayog.apnesaathi.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nitiaayog.apnesaathi.database.constants.Columns
import com.nitiaayog.apnesaathi.database.constants.Tables
import com.nitiaayog.apnesaathi.model.GrievanceData

@Dao
interface GrievanceTrackingDao {

    @Query("SELECT * FROM ${Tables.TABLE_GRIEVANCE_TRACKING} WHERE ${Columns.GrievanceStatus} = :status ORDER BY ${Columns.Priority}")
    fun getGrievancesWithStatus(status: String): LiveData<MutableList<GrievanceData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(grievances: List<GrievanceData>)

    @Query("SELECT * FROM ${Tables.TABLE_GRIEVANCE_TRACKING} WHERE ${Columns.GrievanceStatus} !=:status ORDER BY ${Columns.CreatedDate} DESC")
    fun getAllGrievances(status:String): LiveData<MutableList<GrievanceData>>

    @Query("DELETE FROM ${Tables.TABLE_GRIEVANCE_TRACKING}")
    fun deletePreviousGrievanceData()
}