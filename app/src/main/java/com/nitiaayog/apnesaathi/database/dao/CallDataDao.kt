package com.nitiaayog.apnesaathi.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nitiaayog.apnesaathi.database.constants.Columns
import com.nitiaayog.apnesaathi.database.constants.Tables
import com.nitiaayog.apnesaathi.model.CallData

/**
 * This is an Important class for saving the data given by Sr. Citizen for the roles
 * Master Admin/Staff Members/Volunteers only
 * */
@Dao
interface CallDataDao {

    /**
     * List out all the data with notify when there is any new data or update in existing data.
     **/
    @Query("SELECT * FROM ${Tables.TABLE_CALL_DETAILS} WHERE ${Columns.CallStatus} IN (:status) ORDER BY ${Columns.LoggedDateTime} DESC")
    fun getAllCallsList(status: Array<String>): LiveData<MutableList<CallData>>

    /**
     * This method is used for pagination purpose so parameter
     * @see requestedDataCount will actually inform db how much data(Number of Rows) should be
     * return and
     * @see status parameter that let db know that what kind of status data we want.
     **/
    @Query("SELECT * FROM ${Tables.TABLE_CALL_DETAILS} WHERE ${Columns.CallStatus} IN (:status) ORDER BY ${Columns.Id} DESC LIMIT :requestedDataCount")
    fun getCalls(requestedDataCount: Int, status: Array<String>): MutableList<CallData>

    /**
     * Again this method is for paging
     *
     * @see itemKey : Inform db after which item id(Primary Key) we should start fetching the data
     *                i.e. it's last item id(Primary Key) of already returned data
     *
     * @see requestedDataCount : How many rows we need to return when there are more data
     *
     * @see status : let db know that what kind of status data we want.
     **/
    @Query("SELECT * FROM ${Tables.TABLE_CALL_DETAILS} WHERE ${Columns.CallStatus} IN (:status) AND (${Columns.Id} < :itemKey) ORDER BY ${Columns.Id} DESC LIMIT :requestedDataCount")
    fun getCallsAfter(itemKey: Int, requestedDataCount: Int, status: Array<String>):
            MutableList<CallData>

    /**
     * @see dataCount : Return at least 3 data but can be dynamic based on this variable value
     **/
    @Query("SELECT * FROM ${Tables.TABLE_CALL_DETAILS} LIMIT :dataCount")
    fun getFewCallsList(dataCount: Int = 3): LiveData<MutableList<CallData>>

    /**
     * This will return only single data that will match the id
     *
     * @see id : This is Primary Key
     **/
    @Query("SELECT * FROM ${Tables.TABLE_CALL_DETAILS} WHERE ${Columns.Id} =:id")
    fun getCallDetailFromId(id: Int): CallData

    /**
     * Insert/Save list of data in db
     **/
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(callData: List<CallData>)

    /**
     * Insert/Save single data in db
     **/
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(callData: CallData): Long

    /**
     * Update all the fields in db
     **/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(callData: CallData): Long

    /**
     * Update call status only. If call is not Connected/Out of Network or any other issue is
     * created and call did not happened in such case we just need to update call status
     **/
    @Query("UPDATE ${Tables.TABLE_CALL_DETAILS} SET ${Columns.CallStatus}=:callStatus")
    fun update(callStatus: String)

    /**
     * Insert/Update the issue. While saving grievance in db if it get failed then issue is already
     * exist in db and we just need to update it.
     **/
    @Transaction
    fun insertOrUpdate(grievances: List<CallData>) = grievances.forEach {
        if (insert(it) == -1L) update(it)
    }
}