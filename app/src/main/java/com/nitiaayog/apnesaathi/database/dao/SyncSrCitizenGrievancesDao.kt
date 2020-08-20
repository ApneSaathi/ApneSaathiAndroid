package com.nitiaayog.apnesaathi.database.dao

import androidx.room.*
import com.nitiaayog.apnesaathi.database.constants.Columns
import com.nitiaayog.apnesaathi.database.constants.Tables
import com.nitiaayog.apnesaathi.model.SyncSrCitizenGrievance

/**
 * This interface is only used for syncing data when they are collected when user was offline
 * and data is updated.
 *
 * Only Sr. Citizen data will be captured offline for syncing
 * */
@Dao
interface SyncSrCitizenGrievancesDao {

    @Query("SELECT * FROM ${Tables.TABLE_SYNC_GRIEVANCES}")
    fun getGrievances(): List<SyncSrCitizenGrievance>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(grievances: SyncSrCitizenGrievance): Long

    @Query(
        "DELETE FROM ${Tables.TABLE_SYNC_GRIEVANCES} WHERE ${Columns.Id}=:id AND ${Columns.CallId}=:callId AND ${Columns.VolunteerId}=:volunteerId"
    )
    fun delete(id: Int, callId: Int, volunteerId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(grievances: SyncSrCitizenGrievance)

    @Query("SELECT COUNT(*) FROM ${Tables.TABLE_SYNC_GRIEVANCES}")
    fun getCount(): Int

    @Transaction
    fun insertOrUpdate(grievances: SyncSrCitizenGrievance) {
        val result = insert(grievances)
        if (result == -1L) update(grievances)
    }
}