package com.nitiaayog.apnesaathi.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nitiaayog.apnesaathi.database.constants.Tables
import com.nitiaayog.apnesaathi.model.DistrictDetails

/**
 * When Master Admin/Staff Members logs in to find out what are the working ares of them, this
 * interface is helpful
 * */
@Dao
interface DistrictDataDao {

    /**
     * Save/Insert list of Districts
     * */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(districtData: List<DistrictDetails>)

    /**
     * Retrieve the list of districts
     * */
    @Query("SELECT * FROM ${Tables.TABLE_DISTRICT}")
    fun getDistrictList(): LiveData<MutableList<DistrictDetails>>

    /**
     * Remove all the districts and clear the table
     * */
    @Query("DELETE FROM ${Tables.TABLE_DISTRICT}")
    fun clearDistricts()
}