package com.nitiaayog.apnesaathi.database.dao

import androidx.room.*
import com.nitiaayog.apnesaathi.database.constants.Columns
import com.nitiaayog.apnesaathi.database.constants.Tables
import com.nitiaayog.apnesaathi.model.SyncSrCitizenGrievance

/**
 * This @Dao interface is only used for syncing data when they are collected when user was offline
 * and data is updated.
 *
 * Only Sr. Citizen data will be captured offline for syncing
 **/
@Dao
interface SyncSrCitizenGrievancesDao {

    /**
     * List of the data needs to be synced.
     **/
    @Query("SELECT * FROM ${Tables.TABLE_SYNC_GRIEVANCES}")
    fun getGrievances(): List<SyncSrCitizenGrievance>

    /**
     * Save Sr. Citizen feedback form data when user is offline.
     **/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(grievances: SyncSrCitizenGrievance): Long

    /**
     * Delete the data that is synced successfully
     **/
    @Query(
        "DELETE FROM ${Tables.TABLE_SYNC_GRIEVANCES} WHERE ${Columns.Id}=:id AND ${Columns.CallId}=:callId AND ${Columns.VolunteerId}=:volunteerId"
    )
    fun delete(id: Int, callId: Int, volunteerId: String)

    /**
     * Feedback is stored in SyncSrCitizenGrievance table but again there is any update in the
     * existing data then this method is useful to update it
     **/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(grievances: SyncSrCitizenGrievance)

    /**
     * This method is mostly used when user wants to logout and still some data to be synced
     * and so to show how much data is remaining to sync and based on that user can initiate an
     * action
     **/
    @Query("SELECT COUNT(*) FROM ${Tables.TABLE_SYNC_GRIEVANCES}")
    fun getCount(): Int

    /**
     * If the data that needs to be updated that already exist in database and we will again
     * try to insert it in db then it will throw -1 in return else the value of primary key if
     * successful.
     *
     * So if return value is < 0 then data is already there and we just need to update it.
     **/
    @Transaction
    fun insertOrUpdate(grievances: SyncSrCitizenGrievance) {
        val result = insert(grievances)
        if (result == -1L) update(grievances)
    }
}