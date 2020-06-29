package com.nitiaayog.apnesaathi.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nitiaayog.apnesaathi.database.constants.DbConstants
import com.nitiaayog.apnesaathi.model.CallData

@Dao
interface CallDataDao {

    @Query("SELECT * FROM ${DbConstants.Tables.TABLE_CALL_DETAILS}")
    fun getAllCallsList(): LiveData<MutableList<CallData>>

    @Query("SELECT * FROM ${DbConstants.Tables.TABLE_CALL_DETAILS} LIMIT :dataCount")
    fun getFewCallsList(dataCount: Int = 3): LiveData<MutableList<CallData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(callData: List<CallData>)
}