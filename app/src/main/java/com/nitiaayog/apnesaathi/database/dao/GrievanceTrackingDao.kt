package com.nitiaayog.apnesaathi.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nitiaayog.apnesaathi.database.constants.Columns
import com.nitiaayog.apnesaathi.database.constants.Tables
import com.nitiaayog.apnesaathi.model.GrievanceData

/**
 * To Store and Display issues raised by Sr. Citizens even if user opens app in offline mode
 * then this class would be used
 * */
@Dao
interface GrievanceTrackingDao {

    /**
     * Provide list of latest data with provided status only as well as update too if any and
     * reorder them ${Columns.Priority} i.e. live data
     * */
    @Query("SELECT * FROM ${Tables.TABLE_GRIEVANCE_TRACKING} WHERE ${Columns.GrievanceStatus} = :status ORDER BY ${Columns.Priority}")
    fun getGrievancesWithStatus(status: String): LiveData<MutableList<GrievanceData>>

    /**
     * Store/Save list of issues
     * */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(grievances: List<GrievanceData>)

    /**
     * Provide list of latest data with provided status only as well as update too if any and
     * reorder them ${Columns.CreatedDate} i.e. live data
     * */
    @Query("SELECT * FROM ${Tables.TABLE_GRIEVANCE_TRACKING} WHERE ${Columns.GrievanceStatus} !=:status ORDER BY ${Columns.CreatedDate} DESC")
    fun getAllGrievances(status: String): LiveData<MutableList<GrievanceData>>

    /**
     * Delete all the data and make table empty
     * */
    @Query("DELETE FROM ${Tables.TABLE_GRIEVANCE_TRACKING}")
    fun deletePreviousGrievanceData()
}