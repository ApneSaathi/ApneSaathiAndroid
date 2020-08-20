package com.nitiaayog.apnesaathi.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nitiaayog.apnesaathi.database.constants.Tables
import com.nitiaayog.apnesaathi.model.DistrictDetails

@Dao
interface DistrictDataDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(districtData: List<DistrictDetails>)

    @Query("SELECT * FROM ${Tables.TABLE_DISTRICT}")
    fun getDistrictList(): LiveData<MutableList<DistrictDetails>>

    @Query("DELETE FROM ${Tables.TABLE_DISTRICT}")
    fun clearDistricts()
}