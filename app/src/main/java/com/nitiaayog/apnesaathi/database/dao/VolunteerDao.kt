package com.nitiaayog.apnesaathi.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nitiaayog.apnesaathi.database.constants.Columns
import com.nitiaayog.apnesaathi.database.constants.Tables
import com.nitiaayog.apnesaathi.model.Volunteer

@Dao
interface VolunteerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(volunteers: List<Volunteer>)

    @Query("SELECT ${Columns.FirstName},${Columns.LastName},${Columns.Gender},${Columns.Gender},${Columns.ContactNumber} FROM ${Tables.TABLE_VOLUNTEERS}")
    fun getVolunteers(): LiveData<MutableList<Volunteer>>

    @Query("SELECT * FROM ${Tables.TABLE_VOLUNTEERS} WHERE ${Columns.Id}=:id")
    fun getVolunteer(id: Int): Volunteer?

    @Query("DELETE FROM ${Tables.TABLE_VOLUNTEERS}")
    fun deleteAll()
}