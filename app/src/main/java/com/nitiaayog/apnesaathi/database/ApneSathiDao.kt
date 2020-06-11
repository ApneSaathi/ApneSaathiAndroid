package com.nitiaayog.apnesaathi.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nitiaayog.apnesaathi.model.SeniorCitizen

@Dao
interface ApneSathiDao {
    @Query("SELECT * FROM seniorCitizen")
    fun getAll(): List<SeniorCitizen>

    @Insert
    fun insertItem(vararg seniorCitizen: SeniorCitizen)

    @Query("DELETE FROM seniorCitizen")
    fun deleteAll()
}
