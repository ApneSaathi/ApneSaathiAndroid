package com.nitiaayog.apnesaathi.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nitiaayog.apnesaathi.database.constants.DbConstants
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance

@Dao
interface GrievancesDao {

    @Query("SELECT * FROM ${DbConstants.Tables.TABLE_GRIEVANCES}")
    fun getAllGrievances(): LiveData<MutableList<SrCitizenGrievance>>

    @Query("SELECT * FROM ${DbConstants.Tables.TABLE_GRIEVANCES} LIMIT :dataCount")
    fun getFewGrievances(dataCount: Int = 3): LiveData<MutableList<SrCitizenGrievance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(grievancesList: List<SrCitizenGrievance>)
}