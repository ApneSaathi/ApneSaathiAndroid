package com.nitiaayog.apnesaathi.database.dao

import androidx.room.*
import com.nitiaayog.apnesaathi.database.constants.Columns
import com.nitiaayog.apnesaathi.database.constants.Tables
import com.nitiaayog.apnesaathi.model.SyncSrCitizenGrievance

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

    @Transaction
    fun insertOrUpdate(grievances: SyncSrCitizenGrievance) {
        val result = insert(grievances)
        if (result == -1L) update(grievances)
    }
}