package com.nitiaayog.apnesaathi.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nitiaayog.apnesaathi.database.constants.Columns
import com.nitiaayog.apnesaathi.database.constants.Tables
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance

@Dao
interface GrievancesDao {

    @Transaction
    @Query("SELECT * FROM ${Tables.TABLE_GRIEVANCES}")
    fun getGrievances(): LiveData<MutableList<SrCitizenGrievance>>

    @Query("SELECT * FROM ${Tables.TABLE_GRIEVANCES} LIMIT :dataCount")
    fun getFewGrievances(dataCount: Int = 3): LiveData<MutableList<SrCitizenGrievance>>

    @Query("SELECT * FROM ${Tables.TABLE_GRIEVANCES} WHERE ${Columns.CallId}=:callId ORDER BY ${Columns.CreatedDate} DESC")
    fun getGrievance(callId: Int): SrCitizenGrievance?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(grievances: List<SrCitizenGrievance>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(grievances: SrCitizenGrievance): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(grievance: SrCitizenGrievance)

    @Transaction
    fun insertOrUpdate(grievances: List<SrCitizenGrievance>) = grievances.forEach {
        if (insert(it) == -1L)
            update(it)
    }
}