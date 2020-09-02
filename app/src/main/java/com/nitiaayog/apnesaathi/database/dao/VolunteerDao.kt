package com.nitiaayog.apnesaathi.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nitiaayog.apnesaathi.database.constants.Columns
import com.nitiaayog.apnesaathi.database.constants.Tables
import com.nitiaayog.apnesaathi.model.Volunteer

/**
 * This dao class is used when Master Admin/Staff Member will log in.
 * */
@Dao
interface VolunteerDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(volunteers: List<Volunteer>)

    @Query("SELECT ${Columns.FirstName},${Columns.LastName},${Columns.Gender},${Columns.ContactNumber},${Columns.Block},${Columns.District},${Columns.State} FROM ${Tables.TABLE_VOLUNTEERS}")
    fun getVolunteers(): LiveData<MutableList<Volunteer>>

    /**
     * This Query will return (count) data after id afterId
     * */
    @Query("SELECT * FROM ${Tables.TABLE_VOLUNTEERS} WHERE ${Columns.Id}>:afterId ORDER BY ${Columns.Id} ASC LIMIT :count")
    fun getVolunteers(afterId: Int, count: Int = 5): MutableList<Volunteer>

    @Query("SELECT * FROM ${Tables.TABLE_VOLUNTEERS} ORDER BY ${Columns.Id} DESC")
    fun fetchVolunteers(): MutableList<Volunteer>

    @Query("SELECT * FROM ${Tables.TABLE_VOLUNTEERS}")
    fun getVolunteersList(): DataSource.Factory<Int, Volunteer>

    @Query("SELECT * FROM ${Tables.TABLE_VOLUNTEERS} WHERE ${Columns.Id}=:id")
    fun getVolunteer(id: Int): Volunteer?

    /**
     * Update volunteer ratings
     * */
    @Query("UPDATE ${Tables.TABLE_VOLUNTEERS} SET ${Columns.Ratings}=:ratings WHERE ${Columns.Id}=:id")
    fun updateRating(ratings: String, id: Int):Int

    @Query("DELETE FROM ${Tables.TABLE_VOLUNTEERS}")
    fun deleteAll()
}