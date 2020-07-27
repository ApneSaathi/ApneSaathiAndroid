package com.nitiaayog.apnesaathi.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nitiaayog.apnesaathi.database.constants.Columns
import com.nitiaayog.apnesaathi.database.constants.Tables
import com.nitiaayog.apnesaathi.model.CallData

@Dao
interface CallDataDao {

    @Query("SELECT * FROM ${Tables.TABLE_CALL_DETAILS} WHERE ${Columns.CallStatus} IN (:status) ORDER BY ${Columns.LoggedDateTime} DESC")
    fun getAllCallsList(status: Array<String>): LiveData<MutableList<CallData>>

    @Query("SELECT * FROM ${Tables.TABLE_CALL_DETAILS} WHERE ${Columns.CallStatus} IN (:status) ORDER BY ${Columns.LoggedDateTime} DESC LIMIT :requestedDataCount")
    fun getCalls(requestedDataCount: Int, status: Array<String>): MutableList<CallData>

    @Query("SELECT * FROM ${Tables.TABLE_CALL_DETAILS} WHERE ${Columns.CallStatus} IN (:status) AND (${Columns.LoggedDateTime} < :itemKey) ORDER BY ${Columns.Id} DESC LIMIT :requestedDataCount")
    fun getCallsAfter(itemKey: Int, requestedDataCount: Int, status: Array<String>):
            MutableList<CallData>

    @Query("SELECT * FROM ${Tables.TABLE_CALL_DETAILS} LIMIT :dataCount")
    fun getFewCallsList(dataCount: Int = 3): LiveData<MutableList<CallData>>

    @Query("SELECT * FROM ${Tables.TABLE_CALL_DETAILS} WHERE ${Columns.Id} =:id")
    fun getCallDetailFromId(id: Int): CallData

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(callData: List<CallData>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(callData: CallData): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(callData: CallData): Long

    @Query("UPDATE ${Tables.TABLE_CALL_DETAILS} SET ${Columns.CallStatus}=:callStatus")
    fun update(callStatus: String)

    @Transaction
    fun insertOrUpdate(grievances: List<CallData>) = grievances.forEach {
        if (insert(it) == -1L) update(it)
    }
}